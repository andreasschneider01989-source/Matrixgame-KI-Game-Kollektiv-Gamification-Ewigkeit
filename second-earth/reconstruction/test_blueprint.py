import unittest

from reconstruction.blueprint import (
    Block, Blueprint, arch_fixture, evaluate_blueprint, validate_blueprint,
)


class BlueprintTests(unittest.TestCase):
    def test_exact_reconstruction_scores_one(self):
        target = arch_fixture()
        score = evaluate_blueprint(target, target)
        self.assertEqual(score["voxel_iou"], 1.0)
        self.assertEqual(score["voxel_f1"], 1.0)
        self.assertEqual(score["material_accuracy_on_common_positions"], 1.0)
        self.assertEqual(score["missing_blocks"], 0)
        self.assertEqual(score["extra_blocks"], 0)

    def test_missing_voxel_is_detected(self):
        target = arch_fixture()
        predicted = Blueprint("partial", target.blocks[:-1])
        score = evaluate_blueprint(target, predicted)
        self.assertEqual(score["missing_blocks"], 1)
        self.assertEqual(score["extra_blocks"], 0)
        self.assertLess(score["voxel_recall"], 1.0)

    def test_extra_voxel_is_detected(self):
        target = arch_fixture()
        predicted = Blueprint("extra", target.blocks + (Block(9, 9, 9),))
        score = evaluate_blueprint(target, predicted)
        self.assertEqual(score["extra_blocks"], 1)
        self.assertEqual(score["missing_blocks"], 0)
        self.assertLess(score["voxel_precision"], 1.0)

    def test_material_score_is_separate_from_geometry(self):
        target = arch_fixture()
        changed = list(target.blocks)
        changed[0] = Block(*changed[0].position, "gold")
        predicted = Blueprint("wrong-material", tuple(changed))
        score = evaluate_blueprint(target, predicted)
        self.assertEqual(score["voxel_iou"], 1.0)
        self.assertLess(score["material_accuracy_on_common_positions"], 1.0)

    def test_duplicate_positions_rejected(self):
        bp = Blueprint("duplicate", (Block(0, 0, 0), Block(0, 0, 0)))
        self.assertIn("duplicate voxel coordinates are not allowed",
                      validate_blueprint(bp))

    def test_nonpositive_voxel_size_rejected(self):
        bp = Blueprint("bad-scale", (Block(0, 0, 0),), voxel_edge_m=0)
        self.assertIn("voxel_edge_m must be greater than zero",
                      validate_blueprint(bp))

    def test_empty_prediction_has_zero_precision_and_recall(self):
        score = evaluate_blueprint(arch_fixture(), Blueprint("empty", ()))
        self.assertEqual(score["voxel_precision"], 0.0)
        self.assertEqual(score["voxel_recall"], 0.0)


if __name__ == "__main__":
    unittest.main()
