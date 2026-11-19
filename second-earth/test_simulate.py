import json
import unittest
from pathlib import Path

from simulate import POLICIES, WorldConfig, run_experiment, simulate


CONFIG_PATH = Path(__file__).parent / "config" / "valley-001.json"


class SecondEarthTests(unittest.TestCase):
    def setUp(self):
        self.config = WorldConfig.from_dict(json.loads(CONFIG_PATH.read_text(encoding="utf-8")))

    def test_same_seed_is_reproducible(self):
        self.assertEqual(
            simulate(self.config, POLICIES[0], 123),
            simulate(self.config, POLICIES[0], 123),
        )

    def test_stocks_and_coverage_stay_in_valid_ranges(self):
        result = simulate(self.config, POLICIES[0], 123)
        for row in result["trace"]:
            self.assertGreaterEqual(row["biomass"], 0.0)
            self.assertGreaterEqual(row["food_store"], 0.0)
            self.assertGreaterEqual(row["food_coverage"], 0.0)
            self.assertLessEqual(row["food_coverage"], 1.0)

    def test_paired_seed_design_and_three_policies(self):
        result = run_experiment(self.config, seed_count=5)
        self.assertTrue(result["design"]["paired_seeds"])
        self.assertEqual(len(result["policy_summaries"]), 3)
        self.assertEqual(len(result["design"]["seeds"]), 5)
        self.assertFalse(result["design"]["policies_are_llm_agents"])

    def test_metrics_remain_separate(self):
        result = run_experiment(self.config, seed_count=2)
        self.assertEqual(
            result["metrics"],
            ["mean_final_biomass", "mean_minimum_biomass",
             "mean_food_coverage", "mean_shortage_steps"],
        )
        self.assertNotIn("overall_score", result)

    def test_invalid_config_is_rejected(self):
        raw = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
        raw["food_loss_shock_probability"] = 1.5
        with self.assertRaises(ValueError):
            WorldConfig.from_dict(raw)

    def test_all_policies_use_the_same_seed_set(self):
        result = run_experiment(self.config, seed_count=3)
        expected = result["design"]["seeds"]
        for policy_runs in result["runs"].values():
            self.assertEqual([run["seed"] for run in policy_runs], expected)


if __name__ == "__main__":
    unittest.main()
