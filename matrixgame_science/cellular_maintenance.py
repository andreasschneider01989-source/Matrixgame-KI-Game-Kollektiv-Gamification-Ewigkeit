"""A deterministic, dimensionless toy model of cellular maintenance.

This module is an educational systems model, not a biological cell simulator
or a medical model. All state variables are normalized to [0, 1].
"""
from __future__ import annotations

from dataclasses import dataclass
import math


def _unit_interval(name: str, value: float) -> float:
    value = float(value)
    if not math.isfinite(value) or not 0.0 <= value <= 1.0:
        raise ValueError(f"{name} must be finite and within [0, 1].")
    return value


@dataclass(frozen=True)
class CellState:
    """Normalized state for a didactic maintenance simulation."""

    damage_load: float = 0.20
    protein_load: float = 0.20
    energy_reserve: float = 0.80
    repair_capacity: float = 0.80

    def __post_init__(self) -> None:
        for name in ("damage_load", "protein_load", "energy_reserve", "repair_capacity"):
            object.__setattr__(self, name, _unit_interval(name, getattr(self, name)))


@dataclass(frozen=True)
class MaintenanceConfig:
    """Explicit dimensionless parameters for one simulation step."""

    max_work_per_step: float = 0.10
    repair_share: float = 0.50
    energy_cost_per_work: float = 0.50
    background_damage: float = 0.005
    protein_load_increment: float = 0.005

    def __post_init__(self) -> None:
        for name in ("max_work_per_step", "energy_cost_per_work"):
            value = float(getattr(self, name))
            if not math.isfinite(value) or value <= 0.0:
                raise ValueError(f"{name} must be finite and greater than zero.")
            object.__setattr__(self, name, value)
        object.__setattr__(self, "repair_share", _unit_interval("repair_share", self.repair_share))
        object.__setattr__(self, "background_damage", _unit_interval("background_damage", self.background_damage))
        object.__setattr__(self, "protein_load_increment", _unit_interval("protein_load_increment", self.protein_load_increment))


@dataclass(frozen=True)
class MaintenanceResult:
    """State transition plus transparent accounting for one step."""

    state: CellState
    repaired_damage: float
    cleared_protein_load: float
    energy_spent: float


class CellularMaintenanceEngine:
    """Apply one balanced, resource-limited maintenance step."""

    def __init__(self, config: MaintenanceConfig | None = None) -> None:
        self.config = config or MaintenanceConfig()

    def step(self, state: CellState) -> MaintenanceResult:
        if not isinstance(state, CellState):
            raise TypeError("state must be a CellState instance.")

        cfg = self.config
        work_budget = min(
            cfg.max_work_per_step,
            state.energy_reserve / cfg.energy_cost_per_work,
        )

        repair_work_budget = work_budget * cfg.repair_share
        cleanup_work_budget = work_budget * (1.0 - cfg.repair_share)

        repair_work = min(
            repair_work_budget,
            state.damage_load / state.repair_capacity,
        ) if state.repair_capacity > 0.0 else 0.0
        repaired = repair_work * state.repair_capacity
        cleared = min(state.protein_load, cleanup_work_budget)
        work_used = repair_work + cleared
        energy_spent = min(state.energy_reserve, work_used * cfg.energy_cost_per_work)

        next_state = CellState(
            damage_load=min(1.0, max(0.0, state.damage_load + cfg.background_damage - repaired)),
            protein_load=min(1.0, max(0.0, state.protein_load + cfg.protein_load_increment - cleared)),
            energy_reserve=max(0.0, state.energy_reserve - energy_spent),
            repair_capacity=state.repair_capacity,
        )
        return MaintenanceResult(
            state=next_state,
            repaired_damage=repaired,
            cleared_protein_load=cleared,
            energy_spent=energy_spent,
        )
