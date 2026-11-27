import unittest

from matrixgame_science.knowledge_gate import (
    Claim, EvidenceClass, validate_claim, validate_batch,
)


class KnowledgeGateTests(unittest.TestCase):
    def base(self, evidence_class):
        return Claim(
            claim_id="claim-001",
            text="A clearly scoped example claim.",
            evidence_class=evidence_class,
            scope="This test fixture only.",
            uncertainty="Illustrative; not externally validated.",
        )

    def test_fact_requires_traceable_source(self):
        result = validate_claim(self.base(EvidenceClass.FACT))
        self.assertFalse(result.reviewable)
        self.assertIn("FACT requires at least one traceable source_id", result.errors)

    def test_fact_with_source_is_structurally_reviewable(self):
        claim = self.base(EvidenceClass.FACT)
        claim = Claim(**{**claim.__dict__, "source_ids": ("source:test-1",)})
        self.assertTrue(validate_claim(claim).reviewable)

    def test_hypothesis_requires_falsification_test(self):
        result = validate_claim(self.base(EvidenceClass.HYPOTHESIS))
        self.assertFalse(result.reviewable)

    def test_hypothesis_with_test_is_reviewable_not_proven(self):
        claim = self.base(EvidenceClass.HYPOTHESIS)
        claim = Claim(**{**claim.__dict__, "falsification_test": "Specify an observation that contradicts it."})
        result = validate_claim(claim)
        self.assertTrue(result.reviewable)
        self.assertIn("not scientific validation", result.notice)

    def test_fiction_requires_notice(self):
        self.assertFalse(validate_claim(self.base(EvidenceClass.FICTION)).reviewable)

    def test_batch_returns_independent_results(self):
        good = self.base(EvidenceClass.MODEL)
        good = Claim(**{**good.__dict__, "method": "Toy deterministic model."})
        bad = self.base(EvidenceClass.FACT)
        results = validate_batch((good, bad))
        self.assertEqual(len(results), 2)
        self.assertTrue(results[0].reviewable)
        self.assertFalse(results[1].reviewable)


if __name__ == "__main__":
    unittest.main()
