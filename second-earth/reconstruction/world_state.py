"""Quest 006: deterministic persistent hybrid 3D world state.

This is a small, dependency-free reference model, not a vision or physics model.
Meshes are referenced by asset ID; voxel occupancy is stored as integer cells.
All positions/scales are in metres. Confidence is an evidence score in [0, 1].
"""
from __future__ import annotations

from dataclasses import asdict, dataclass, field
import json
import math
from typing import Any


def _finite_vector(values: tuple[float, float, float], name: str) -> None:
    if len(values) != 3 or not all(math.isfinite(float(v)) for v in values):
        raise ValueError(f"{name} must contain three finite numbers")


@dataclass(frozen=True)
class Observation:
    observation_id: str
    object_id: str
    position_m: tuple[float, float, float]
    confidence: float
    step: int
    source_id: str
    scale_m: tuple[float, float, float] = (1.0, 1.0, 1.0)
    material_id: str | None = None
    voxel_cells: tuple[tuple[int, int, int], ...] = ()
    mesh_asset_id: str | None = None

    def __post_init__(self) -> None:
        if not self.observation_id.strip() or not self.object_id.strip() or not self.source_id.strip():
            raise ValueError("observation_id, object_id and source_id must be non-empty")
        _finite_vector(self.position_m, "position_m")
        _finite_vector(self.scale_m, "scale_m")
        if any(float(v) <= 0 for v in self.scale_m):
            raise ValueError("scale_m values must be positive")
        if not 0.0 <= self.confidence <= 1.0:
            raise ValueError("confidence must be in [0, 1]")
        if self.step < 0:
            raise ValueError("step must be non-negative")
        if any(len(cell) != 3 or any(not isinstance(v, int) for v in cell)
               for cell in self.voxel_cells):
            raise ValueError("voxel_cells must contain integer 3D coordinates")


@dataclass
class WorldObject:
    object_id: str
    label: str
    position_m: tuple[float, float, float]
    scale_m: tuple[float, float, float]
    confidence: float
    last_observed_step: int
    observation_ids: tuple[str, ...] = ()
    source_ids: tuple[str, ...] = ()
    material_id: str | None = None
    voxel_cells: tuple[tuple[int, int, int], ...] = ()
    mesh_asset_id: str | None = None


@dataclass
class WorldState:
    world_id: str
    step: int = 0
    objects: dict[str, WorldObject] = field(default_factory=dict)
    events: list[dict[str, Any]] = field(default_factory=list)

    def __post_init__(self) -> None:
        if not self.world_id.strip():
            raise ValueError("world_id must be non-empty")
        if self.step < 0:
            raise ValueError("step must be non-negative")

    def to_dict(self) -> dict[str, Any]:
        return {
            "schema_version": "1.0.0",
            "world_id": self.world_id,
            "step": self.step,
            "objects": {
                key: asdict(value) for key, value in sorted(self.objects.items())
            },
            "events": self.events,
        }

    def to_json(self) -> str:
        return json.dumps(self.to_dict(), sort_keys=True, indent=2)

    @classmethod
    def from_json(cls, payload: str) -> "WorldState":
        data = json.loads(payload)
        if data.get("schema_version") != "1.0.0":
            raise ValueError("unsupported world-state schema_version")
        objects = {}
        for key, raw in data.get("objects", {}).items():
            raw["position_m"] = tuple(raw["position_m"])
            raw["scale_m"] = tuple(raw["scale_m"])
            raw["observation_ids"] = tuple(raw.get("observation_ids", ()))
            raw["source_ids"] = tuple(raw.get("source_ids", ()))
            raw["voxel_cells"] = tuple(tuple(cell) for cell in raw.get("voxel_cells", ()))
            objects[key] = WorldObject(**raw)
        return cls(data["world_id"], data["step"], objects, data.get("events", []))


def _distance(a: tuple[float, float, float], b: tuple[float, float, float]) -> float:
    return math.sqrt(sum((float(x) - float(y)) ** 2 for x, y in zip(a, b)))


def collective_update(
    state: WorldState,
    observations: list[Observation] | tuple[Observation, ...],
    *,
    min_confidence: float = 0.5,
    max_position_delta_m: float = 2.0,
) -> dict[str, Any]:
    """Perception proposes; critic gates confidence/consistency; integrator commits.

    Proposals for one stable object ID are fused only if mutually consistent.
    Conflicting or stale proposals are logged, never silently overwritten.
    Duplicate observation IDs are idempotent. No learned agent is implied.
    """
    if not 0.0 <= min_confidence <= 1.0:
        raise ValueError("min_confidence must be in [0, 1]")
    if max_position_delta_m < 0 or not math.isfinite(max_position_delta_m):
        raise ValueError("max_position_delta_m must be finite and non-negative")

    seen = {oid for obj in state.objects.values() for oid in obj.observation_ids}
    seen.update(event.get("observation_id") for event in state.events
                if event.get("observation_id"))
    fresh = [o for o in observations if o.observation_id not in seen]
    grouped: dict[str, list[Observation]] = {}
    rejected: list[str] = []
    for obs in fresh:
        if obs.step < state.step:
            rejected.append(obs.observation_id)
            state.events.append({"type": "stale_rejected", "observation_id": obs.observation_id,
                                 "object_id": obs.object_id, "step": obs.step})
        elif obs.confidence < min_confidence:
            rejected.append(obs.observation_id)
            state.events.append({"type": "low_confidence_rejected",
                                 "observation_id": obs.observation_id,
                                 "object_id": obs.object_id, "confidence": obs.confidence})
        else:
            grouped.setdefault(obs.object_id, []).append(obs)

    accepted: list[str] = []
    conflicts: list[str] = []
    for object_id in sorted(grouped):
        proposals = sorted(grouped[object_id], key=lambda o: o.observation_id)
        mutually_consistent = all(
            _distance(a.position_m, b.position_m) <= max_position_delta_m
            for i, a in enumerate(proposals) for b in proposals[i + 1:]
        )
        if not mutually_consistent:
            conflicts.extend(o.observation_id for o in proposals)
            state.events.append({"type": "proposal_conflict", "object_id": object_id,
                                 "observation_ids": [o.observation_id for o in proposals]})
            continue

        prior = state.objects.get(object_id)
        if prior and any(_distance(prior.position_m, o.position_m) > max_position_delta_m
                         for o in proposals):
            conflicts.extend(o.observation_id for o in proposals)
            state.events.append({"type": "world_conflict", "object_id": object_id,
                                 "observation_ids": [o.observation_id for o in proposals]})
            continue

        weighted = []
        if prior:
            weighted.append((prior.position_m, prior.confidence))
        weighted.extend((o.position_m, o.confidence) for o in proposals)
        weight_sum = sum(weight for _, weight in weighted)
        position = tuple(
            sum(pos[axis] * weight for pos, weight in weighted) / weight_sum
            for axis in range(3)
        )
        confidence = 1.0 - (1.0 - (prior.confidence if prior else 0.0)) * math.prod(
            1.0 - o.confidence for o in proposals
        )
        best = max(proposals, key=lambda o: (o.confidence, o.observation_id))
        voxel_cells = set(prior.voxel_cells if prior else ())
        for obs in proposals:
            voxel_cells.update(obs.voxel_cells)
        obs_ids = set(prior.observation_ids if prior else ())
        obs_ids.update(o.observation_id for o in proposals)
        source_ids = set(prior.source_ids if prior else ())
        source_ids.update(o.source_id for o in proposals)
        state.objects[object_id] = WorldObject(
            object_id=object_id,
            label=(prior.label if prior else object_id),
            position_m=position,
            scale_m=(prior.scale_m if prior else best.scale_m),
            confidence=confidence,
            last_observed_step=max(o.step for o in proposals),
            observation_ids=tuple(sorted(obs_ids)),
            source_ids=tuple(sorted(source_ids)),
            material_id=best.material_id or (prior.material_id if prior else None),
            voxel_cells=tuple(sorted(voxel_cells)),
            mesh_asset_id=next((o.mesh_asset_id for o in reversed(proposals)
                                if o.mesh_asset_id), prior.mesh_asset_id if prior else None),
        )
        accepted.extend(o.observation_id for o in proposals)
        state.events.append({"type": "object_fused", "object_id": object_id,
                             "observation_ids": [o.observation_id for o in proposals],
                             "confidence": confidence})

    if accepted:
        state.step = max(state.step, max(
            o.step for group in grouped.values() for o in group
            if o.observation_id in accepted
        ))
    return {
        "accepted_observation_ids": sorted(accepted),
        "rejected_observation_ids": sorted(rejected),
        "conflict_observation_ids": sorted(conflicts),
        "updated_object_ids": sorted({state.objects[o.object_id].object_id
                                      for o in fresh if o.observation_id in accepted}),
        "world_step": state.step,
        "status": "SYNTHETIC_REFERENCE_MODEL",
    }
