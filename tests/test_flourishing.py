import unittest

from matrixgame_science.flourishing import (
    DOMAINS,
    FlourishingDomain,
    validate_domain_map,
)


class HumanFlourishingDomainTests(unittest.TestCase):
    def test_all_seven_domains_are_present(self):
        self.assertEqual({d.domain for d in DOMAINS}, set(FlourishingDomain))

    def test_domain_map_is_structurally_complete(self):
        self.assertEqual(validate_domain_map(), ())

    def test_duplicate_domain_is_reported(self):
        self.assertTrue(validate_domain_map(DOMAINS + (DOMAINS[0],)))

    def test_missing_domain_is_reported(self):
        self.assertTrue(validate_domain_map(DOMAINS[:-1]))


if __name__ == "__main__":
    unittest.main()
