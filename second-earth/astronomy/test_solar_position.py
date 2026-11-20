import math
import unittest
from datetime import date

from solar_position import (
    apparent_solar_time_hours,
    equation_of_time_minutes,
    solar_declination,
    solar_position,
)


class SolarPositionTests(unittest.TestCase):
    def test_summer_solstice_declination_near_plus_23_44(self):
        self.assertAlmostEqual(solar_declination(date(2026, 6, 21)), 23.44, delta=0.3)

    def test_winter_solstice_declination_near_minus_23_44(self):
        self.assertAlmostEqual(solar_declination(date(2026, 12, 21)), -23.44, delta=0.3)

    def test_equinox_noon_at_50_degrees_north(self):
        result = solar_position(date(2026, 3, 20), 12.0, 50.0, 1.0)
        self.assertAlmostEqual(result["solar_elevation_deg"], 40.0, delta=0.8)
        self.assertAlmostEqual(result["solar_azimuth_deg"], 180.0, delta=0.01)
        self.assertGreater(result["shadow_length_m"], 1.0)

    def test_summer_noon_shadow_is_shorter_than_winter(self):
        summer = solar_position(date(2026, 6, 21), 12.0, 50.0, 1.0)
        winter = solar_position(date(2026, 12, 21), 12.0, 50.0, 1.0)
        self.assertLess(summer["shadow_length_m"], winter["shadow_length_m"])

    def test_sun_below_horizon_has_no_finite_ground_shadow(self):
        result = solar_position(date(2026, 12, 21), 0.0, 50.0, 1.0)
        self.assertIsNone(result["shadow_length_m"])
        self.assertIsNone(result["shadow_azimuth_deg"])

    def test_solar_time_conversion_uses_longitude_and_equation_of_time(self):
        day = date(2026, 11, 20)
        result = apparent_solar_time_hours(day, 12.0, 8.5, 1.0)
        self.assertNotEqual(result, 12.0)
        self.assertTrue(math.isfinite(equation_of_time_minutes(day)))

    def test_invalid_inputs_are_rejected(self):
        with self.assertRaises(ValueError):
            solar_position(date(2026, 6, 21), 12.0, 91.0)
        with self.assertRaises(ValueError):
            solar_position(date(2026, 6, 21), 12.0, 50.0, 0.0)
        with self.assertRaises(ValueError):
            apparent_solar_time_hours(date(2026, 6, 21), 25.0, 8.0, 2.0)


if __name__ == "__main__":
    unittest.main()
