import unittest
from pathlib import Path

from matrixgame_core import (
    EvidenceLabel,
    Observation,
    load_scenario,
    operate,
    status_for,
)


class KnowledgeGapTests(unittest.TestCase):
    def test_gap_is_non_negative(self):
        item = Observation(1, 10, 12, EvidenceLabel.MODEL_ASSUMPTION, "test")
        self.assertEqual(item.information_gap, 0)

    def test_gap_ratio(self):
        item = Observation(1, 100, 25, EvidenceLabel.MODEL_ASSUMPTION, "test")
        self.assertEqual(item.information_gap, 75)
        self.assertAlmostEqual(item.gap_ratio, 0.75)
        self.assertTrue(status_for(item).startswith("ROT"))

    def test_zero_estimate_has_zero_ratio(self):
        item = Observation(1, 0, 0, EvidenceLabel.UNCLEAR, "test")
        self.assertEqual(item.gap_ratio, 0.0)

    def test_demo_scenario_loads(self):
        scenario = load_scenario(Path(__file__).resolve().parents[1] / "scenarios" / "information_gap_demo.json")
        observations = operate(scenario)
        self.assertEqual(len(observations), 4)
        self.assertTrue(all(x.label is EvidenceLabel.MODEL_ASSUMPTION for x in observations))


if __name__ == "__main__":
    unittest.main()
