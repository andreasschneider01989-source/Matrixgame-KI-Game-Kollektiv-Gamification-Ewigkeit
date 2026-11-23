import math
import unittest

from odc_analyzer import lorentz_lorenz_predict


class LorentzLorenzTests(unittest.TestCase):
    def test_reference_state_is_reproduced(self):
        self.assertAlmostEqual(
            lorentz_lorenz_predict(2200, 2200, 1.46), 1.46, places=12
        )

    def test_increasing_density_increases_n_for_positive_refractivity(self):
        low = lorentz_lorenz_predict(2100, 2200, 1.46)
        high = lorentz_lorenz_predict(2300, 2200, 1.46)
        self.assertLess(low, 1.46)
        self.assertGreater(high, 1.46)

    def test_invalid_density_is_rejected(self):
        with self.assertRaises(ValueError):
            lorentz_lorenz_predict(0, 2200, 1.46)

    def test_non_finite_input_is_rejected(self):
        with self.assertRaises(ValueError):
            lorentz_lorenz_predict(float("nan"), 2200, 1.46)


if __name__ == "__main__":
    unittest.main()
