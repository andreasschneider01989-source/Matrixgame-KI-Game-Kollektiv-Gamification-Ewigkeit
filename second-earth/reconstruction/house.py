"""Quest 005 synthetic house reference, view evidence and build-plan baseline.

Coordinates use x=width, y=height, z=depth; front facade faces z=0.
This is a deterministic voxel benchmark fixture, not a photo-derived model.
"""
from collections import Counter

from reconstruction.blueprint import Block, Blueprint, evaluate_blueprint, validate_blueprint


def house_reference() -> Blueprint:
    blocks = []
    # Full 6 x 5 foundation.
    for x in range(6):
        for z in range(5):
            blocks.append(Block(x, 0, z, "stone"))
    # Perimeter walls, three blocks high; openings: front door, side windows.
    for y in range(1, 4):
        for x in range(6):
            for z in range(5):
                if x not in (0, 5) and z not in (0, 4):
                    continue
                if z == 0 and x in (2, 3) and y in (1, 2):
                    continue
                if x == 0 and z == 2 and y == 2:
                    continue
                if x == 5 and z == 2 and y == 2:
                    continue
                if z == 4 and x in (1, 4) and y == 2:
                    continue
                blocks.append(Block(x, y, z, "wood"))
    # Symmetric gable roof.
    for x in range(6):
        for z, y in ((0, 4), (4, 4), (1, 5), (3, 5), (2, 6)):
            blocks.append(Block(x, y, z, "brick"))
    return Blueprint("house-001", tuple(blocks), voxel_edge_m=1.0)


VIEWS = {
    "front": {"camera_direction": "z-", "projection_axes": ["x", "y"]},
    "rear": {"camera_direction": "z+", "projection_axes": ["x", "y"]},
    "left": {"camera_direction": "x-", "projection_axes": ["z", "y"]},
    "right": {"camera_direction": "x+", "projection_axes": ["z", "y"]},
    "top": {"camera_direction": "y+", "projection_axes": ["x", "z"]},
}


def visible_silhouette(blueprint: Blueprint, view: str) -> set[tuple[int, int]]:
    """Orthographic occupancy silhouette; geometry proxy, not a rendered photo."""
    if view not in VIEWS:
        raise ValueError(f"unknown view: {view}")
    if validate_blueprint(blueprint):
        raise ValueError("invalid blueprint")
    axes = VIEWS[view]["projection_axes"]
    return {(getattr(b, axes[0]), getattr(b, axes[1])) for b in blueprint.blocks}


def view_task(view_mode: str) -> dict:
    if view_mode == "single":
        selected = ["front"]
    elif view_mode == "multi":
        selected = ["front", "rear", "left", "right", "top"]
    else:
        raise ValueError("view_mode must be 'single' or 'multi'")
    return {
        "task_id": f"house-001-{view_mode}",
        "view_mode": view_mode,
        "views": selected,
        "available_evidence": {name: VIEWS[name] for name in selected},
        "hidden_ground_truth": True,
    }


def resource_inventory(blueprint: Blueprint) -> dict[str, int]:
    return dict(sorted(Counter(block.material for block in blueprint.blocks).items()))


def construction_plan(blueprint: Blueprint, blocks_per_minute: float = 12.0) -> dict:
    """Transparent baseline estimate; not a learned planner or real build-time claim."""
    if validate_blueprint(blueprint):
        raise ValueError("invalid blueprint")
    if blocks_per_minute <= 0:
        raise ValueError("blocks_per_minute must be positive")
    stages = [
        {"stage": "foundation", "y_min": 0, "y_max": 0},
        {"stage": "walls_and_openings", "y_min": 1, "y_max": 3},
        {"stage": "roof", "y_min": 4, "y_max": 6},
    ]
    for stage in stages:
        stage["block_count"] = sum(stage["y_min"] <= b.y <= stage["y_max"]
                                   for b in blueprint.blocks)
    total = len(blueprint.blocks)
    return {
        "inventory": resource_inventory(blueprint),
        "total_blocks": total,
        "stages": stages,
        "estimated_minutes_at_configured_rate": round(total / blocks_per_minute, 3),
        "rate_blocks_per_minute": blocks_per_minute,
        "estimate_status": "SYNTHETIC_BASELINE",
    }


def compare_view_information() -> dict:
    target = house_reference()
    multi = view_task("multi")
    return {
        "single_view_count": len(view_task("single")["views"]),
        "multi_view_count": len(multi["views"]),
        "single_front_silhouette_cells": len(visible_silhouette(target, "front")),
        "multi_unique_silhouette_cells": {
            name: len(visible_silhouette(target, name)) for name in multi["views"]
        },
    }


def evaluate_house_prediction(predicted: Blueprint) -> dict:
    return evaluate_blueprint(house_reference(), predicted)
