import math
import unittest

from materials.material_physics import (
    cube_volume, mass_from_density, weight_force, scale_mass_from_force,
    infer_gravity_from_known_mass, material_block,
)


class MaterialPhysicsTests(unittest.TestCase):
    def test_one_cubic_metre_gold_mass(self):
        self.assertAlmostEqual(material_block("gold")["mass_kg"], 19320.0)

    def test_one_cubic_metre_silver_mass(self):
        self.assertAlmostEqual(material_block("silver")["mass_kg"], 10490.0)

    def test_cube_volume_scales_cubically(self):
        self.assertAlmostEqual(cube_volume(2.0), 8.0)

    def test_density_times_volume(self):
        self.assertAlmostEqual(mass_from_density(1000.0, 0.5), 500.0)

    def test_weight_force_uses_newtons(self):
        self.assertAlmostEqual(weight_force(10.0, 9.81), 98.1)

    def test_scale_recovers_mass_when_gravity_known(self):
        force = weight_force(19320.0, 9.81)
        self.assertAlmostEqual(scale_mass_from_force(force, 9.81), 19320.0)

    def test_known_mass_recovers_local_gravity(self):
        self.assertAlmostEqual(infer_gravity_from_known_mass(196.2, 20.0), 9.81)

    def test_gold_and_silver_force_order(self):
        gold = material_block("gold")["weight_force_n"]
        silver = material_block("silver")["weight_force_n"]
        self.assertGreater(gold, silver)

    def test_invalid_zero_edge_rejected(self):
        with self.assertRaises(ValueError):
            cube_volume(0)

    def test_unknown_material_rejected(self):
        with self.assertRaises(KeyError):
            material_block("unobtainium")


if __name__ == "__main__":
    unittest.main()
