"""Small, deterministic voxel-blueprint validation and reconstruction scoring.

Coordinates are integer voxel indices. One voxel may represent 1 m³ in the
default Second Earth scale, but the evaluator itself is scale-independent.
This module scores a proposed blueprint against a known target; it does not
perform image recognition or infer hidden geometry from photographs.
"""
from dataclasses import dataclass
from typing import Iterable


@dataclass(frozen=True, order=True)
class Block:
    x: int
    y: int
    z: int
    material: str = "stone"

    @property
    def position(self) -> tuple[int, int, int]:
        return (self.x, self.y, self.z)


@dataclass(frozen=True)
class Blueprint:
    name: str
    blocks: tuple[Block, ...]
    voxel_edge_m: float = 1.0


def validate_blueprint(blueprint: Blueprint) -> list[str]:
    errors: list[str] = []
    if not blueprint.name.strip():
        errors.append("name must not be empty")
    if blueprint.voxel_edge_m <= 0:
        errors.append("voxel_edge_m must be greater than zero")
    positions = [block.position for block in blueprint.blocks]
    if len(positions) != len(set(positions)):
        errors.append("duplicate voxel coordinates are not allowed")
    if any(not block.material.strip() for block in blueprint.blocks):
        errors.append("every block must have a material")
    if any(not isinstance(v, int) for pos in positions for v in pos):
        errors.append("voxel coordinates must be integers")
    return errors


def _positions(blocks: Iterable[Block]) -> set[tuple[int, int, int]]:
    return {block.position for block in blocks}


def _dimensions(positions: set[tuple[int, int, int]]) -> tuple[int, int, int]:
    if not positions:
        return (0, 0, 0)
    axes = list(zip(*positions))
    return tuple(max(axis) - min(axis) + 1 for axis in axes)


def evaluate_blueprint(target: Blueprint, predicted: Blueprint) -> dict:
    """Compare geometry and materials separately; return reproducible metrics."""
    target_errors = validate_blueprint(target)
    predicted_errors = validate_blueprint(predicted)
    if target_errors:
        raise ValueError("invalid target blueprint: " + "; ".join(target_errors))
    if predicted_errors:
        raise ValueError("invalid predicted blueprint: " + "; ".join(predicted_errors))

    target_pos = _positions(target.blocks)
    predicted_pos = _positions(predicted.blocks)
    intersection = target_pos & predicted_pos
    union = target_pos | predicted_pos
    precision = len(intersection) / len(predicted_pos) if predicted_pos else 0.0
    recall = len(intersection) / len(target_pos) if target_pos else 1.0
    f1 = (2 * precision * recall / (precision + recall)
          if precision + recall else 0.0)
    iou = len(intersection) / len(union) if union else 1.0

    target_materials = {b.position: b.material for b in target.blocks}
    predicted_materials = {b.position: b.material for b in predicted.blocks}
    common = target_pos & predicted_pos
    material_accuracy = (
        sum(target_materials[p] == predicted_materials[p] for p in common) / len(common)
        if common else 0.0
    )
    target_dims = _dimensions(target_pos)
    predicted_dims = _dimensions(predicted_pos)
    dimension_abs_error = tuple(abs(a - b) for a, b in zip(target_dims, predicted_dims))

    return {
        "target_block_count": len(target_pos),
        "predicted_block_count": len(predicted_pos),
        "correct_positions": len(intersection),
        "missing_blocks": len(target_pos - predicted_pos),
        "extra_blocks": len(predicted_pos - target_pos),
        "voxel_precision": precision,
        "voxel_recall": recall,
        "voxel_f1": f1,
        "voxel_iou": iou,
        "material_accuracy_on_common_positions": material_accuracy,
        "target_dimensions_voxels": target_dims,
        "predicted_dimensions_voxels": predicted_dims,
        "dimension_absolute_error_voxels": dimension_abs_error,
        "target_volume_m3": len(target_pos) * target.voxel_edge_m ** 3,
        "predicted_volume_m3": len(predicted_pos) * predicted.voxel_edge_m ** 3,
        "evaluation_status": "SYNTHETIC_BENCHMARK",
    }


def arch_fixture() -> Blueprint:
    """Seven-block, one-voxel-thick arch used as a known test target."""
    positions = (
        (0, 0, 0), (0, 1, 0), (0, 2, 0),
        (1, 2, 0),
        (2, 0, 0), (2, 1, 0), (2, 2, 0),
    )
    return Blueprint("arch-001", tuple(Block(*p, "stone") for p in positions))
