import unittest

from reconstruction.blueprint import Blueprint, evaluate_blueprint
from reconstruction.house import (
    compare_view_information, construction_plan, house_reference,
    resource_inventory, view_task, visible_silhouette,
)


class HouseBenchmarkTests(unittest.TestCase):
    def test_reference_house_dimensions_and_voxel_scale(self):
        house = house_reference()
        self.assertEqual(house.voxel_edge_m, 1.0)
        score = evaluate_blueprint(house, house)
        self.assertEqual(score["target_dimensions_voxels"], (6, 7, 5))
        self.assertEqual(score["voxel_iou"], 1.0)
        self.assertGreater(score["target_block_count"], 100)

    def test_single_view_exposes_only_front_camera(self):
        task = view_task("single")
        self.assertEqual(task["views"], ["front"])
        self.assertTrue(task["hidden_ground_truth"])

    def test_multi_view_has_five_directions(self):
        task = view_task("multi")
        self.assertEqual(set(task["views"]), {"front", "rear", "left", "right", "top"})
        self.assertEqual(len(task["available_evidence"]), 5)

    def test_unknown_view_mode_rejected(self):
        with self.assertRaises(ValueError):
            view_task("panorama-ish")

    def test_projection_is_deterministic(self):
        house = house_reference()
        self.assertEqual(visible_silhouette(house, "front"),
                         visible_silhouette(house, "front"))
        self.assertGreater(len(visible_silhouette(house, "front")), 0)

    def test_inventory_matches_blueprint_material_counts(self):
        house = house_reference()
        inventory = resource_inventory(house)
        self.assertEqual(sum(inventory.values()), len(house.blocks))
        self.assertEqual(set(inventory), {"brick", "stone", "wood"})

    def test_plan_stages_cover_every_block(self):
        house = house_reference()
        plan = construction_plan(house, blocks_per_minute=10)
        self.assertEqual(sum(s["block_count"] for s in plan["stages"]), len(house.blocks))
        self.assertEqual(plan["estimated_minutes_at_configured_rate"],
                         round(len(house.blocks) / 10, 3))

    def test_invalid_build_rate_rejected(self):
        with self.assertRaises(ValueError):
            construction_plan(house_reference(), blocks_per_minute=0)

    def test_single_multi_comparison_is_reproducible(self):
        self.assertEqual(compare_view_information(), compare_view_information())

    def test_exact_house_reconstruction_perfect_score(self):
        house = house_reference()
        result = evaluate_blueprint(house, house)
        self.assertEqual(result["voxel_f1"], 1.0)
        self.assertEqual(result["material_accuracy_on_common_positions"], 1.0)

    def test_removed_block_reduces_recall(self):
        house = house_reference()
        incomplete = Blueprint("incomplete-house", house.blocks[:-1], 1.0)
        result = evaluate_blueprint(house, incomplete)
        self.assertEqual(result["missing_blocks"], 1)
        self.assertLess(result["voxel_recall"], 1.0)


if __name__ == "__main__":
    unittest.main()
