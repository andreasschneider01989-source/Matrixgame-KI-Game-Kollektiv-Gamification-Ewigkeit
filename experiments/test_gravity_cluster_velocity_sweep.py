import unittest
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parent))
import gravity_cluster_phase_sweep as sim
from gravity_cluster_velocity_sweep import run_one, scale_internal_velocities

class VelocitySweepTests(unittest.TestCase):
    def test_grid_sample_matches_189_baseline_at_k1(self):
        a=run_one(1,29,46,1.0)
        b=run_one(2,29,46,1.0)
        self.assertAlmostEqual(a["core_distance_at_contact_or_closest_approach"],8.36787942,places=5)
        self.assertFalse(a["core_contact"])
        self.assertAlmostEqual(b["core_distance_at_contact_or_closest_approach"],7.99801441,places=5)
        self.assertTrue(b["core_contact"])

    def test_internal_scaling_preserves_cluster_com_velocity(self):
        bodies=sim.cluster(1,"A",-50,10,1.0,0.0,0.3,"isolated_equilibrium")
        before=(sum(x["mass"]*x["vx"] for x in bodies)/100,
                sum(x["mass"]*x["vy"] for x in bodies)/100)
        com=scale_internal_velocities(bodies,1.25)
        after=(sum(x["mass"]*x["vx"] for x in bodies)/100,
               sum(x["mass"]*x["vy"] for x in bodies)/100)
        self.assertAlmostEqual(before[0],after[0],places=12)
        self.assertAlmostEqual(before[1],after[1],places=12)
        self.assertAlmostEqual(com["A"][0],before[0],places=12)

    def test_invalid_factor(self):
        bodies=sim.cluster(1,"A",0,0,0,0,0,"isolated_equilibrium")
        with self.assertRaises(ValueError): scale_internal_velocities(bodies,0)


    def test_antipodal_phase_symmetry(self):
        a=run_one(2,29,0,1.0)
        b=run_one(2,29,180,1.0)
        c=run_one(2,29,90,1.0)
        d=run_one(2,29,270,1.0)
        self.assertAlmostEqual(a["core_distance_at_contact_or_closest_approach"],
                               b["core_distance_at_contact_or_closest_approach"],places=8)
        self.assertAlmostEqual(c["core_distance_at_contact_or_closest_approach"],
                               d["core_distance_at_contact_or_closest_approach"],places=8)
        self.assertEqual(a["core_contact"],b["core_contact"])
        self.assertEqual(c["core_contact"],d["core_contact"])

if __name__=="__main__":unittest.main()
