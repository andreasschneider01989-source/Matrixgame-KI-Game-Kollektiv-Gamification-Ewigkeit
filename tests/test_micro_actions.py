import unittest

from matrixgame_science.flourishing import FlourishingDomain
from matrixgame_science.micro_actions import build_micro_action


class MicroActionTests(unittest.TestCase):
    def test_same_input_is_reproducible(self):
        args = ("Mehr lernen", FlourishingDomain.KNOWLEDGE, None, 8)
        self.assertEqual(build_micro_action(*args), build_micro_action(*args))

    def test_all_seven_domains_have_action_templates(self):
        for domain in FlourishingDomain:
            with self.subTest(domain=domain):
                plan = build_micro_action("Mein eigenes Ziel", domain)
                self.assertTrue(plan.action)
                self.assertTrue(plan.completion_check)
                self.assertTrue(plan.fallback)

    def test_supporting_domain_is_optional_and_distinct(self):
        plan = build_micro_action(
            "Kontakt aufnehmen",
            FlourishingDomain.RELATIONSHIPS,
            FlourishingDomain.JOY,
        )
        self.assertEqual(plan.supporting_domain, FlourishingDomain.JOY)
        with self.assertRaises(ValueError):
            build_micro_action("Ziel", FlourishingDomain.JOY, FlourishingDomain.JOY)

    def test_blank_goal_is_rejected(self):
        with self.assertRaises(ValueError):
            build_micro_action("  ", FlourishingDomain.KNOWLEDGE)

    def test_goal_length_is_bounded(self):
        with self.assertRaises(ValueError):
            build_micro_action("x" * 301, FlourishingDomain.KNOWLEDGE)

    def test_timebox_must_be_between_one_and_thirty(self):
        for value in (0, 31, True, 2.5):
            with self.subTest(value=value):
                with self.assertRaises(ValueError):
                    build_micro_action("Ziel", FlourishingDomain.JOY, timebox_minutes=value)

    def test_plan_has_no_score_or_rank(self):
        plan = build_micro_action("Mehr Freude", FlourishingDomain.JOY)
        self.assertFalse(hasattr(plan, "score"))
        self.assertFalse(hasattr(plan, "rank"))
        self.assertIn("jederzeit", plan.stop_rule)


if __name__ == "__main__":
    unittest.main()
