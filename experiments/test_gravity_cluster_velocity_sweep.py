import unittest
import gravity_cluster_phase_sweep as sim
from gravity_cluster_velocity_sweep import run_one, scale_internal_velocities

class VelocitySweepTests(unittest.TestCase):
    def test_grid_sample_matches_189_baseline_at_k1(self):
        a=run_one(1,29,46,1.0)
        b=run_one(2,29,46,1.0)
        self.assertAlmostEqual(a["min_core_distance"],8.36787942,places=5)
        self.assertFalse(a["core_contact"])
        self.assertAlmostEqual(b["min_core_distance"],7.99801441,places=5)
        self.assertTrue(b["core_contact"])

    def test_internal_scaling_preserves_cluster_com_velocity(self):
        bodies=sim.create_cluster(1,"A",-50,10,1.0,0.0,0.3,"isolated_equilibrium")
        before=(sum(x["mass"]*x["vx"] for x in bodies)/100,
                sum(x["mass"]*x["vy"] for x in bodies)/100)
        com=scale_internal_velocities(bodies,1.25)
        after=(sum(x["mass"]*x["vx"] for x in bodies)/100,
               sum(x["mass"]*x["vy"] for x in bodies)/100)
        self.assertAlmostEqual(before[0],after[0],places=12)
        self.assertAlmostEqual(before[1],after[1],places=12)
        self.assertAlmostEqual(com["A"][0],before[0],places=12)

    def test_invalid_factor(self):
        bodies=sim.create_cluster(1,"A",0,0,0,0,0,"isolated_equilibrium")
        with self.assertRaises(ValueError): scale_internal_velocities(bodies,0)

if __name__=="__main__":unittest.main()
