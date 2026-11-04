import math
import unittest
import gravity_cluster_phase_sweep as sim

class SweepTests(unittest.TestCase):
    def test_grid_count(self):
        runs=sim.build_runs(sim.DT,sim.STEPS)
        self.assertEqual(len(runs),189)
        self.assertEqual([sum(r[0]==n for r in runs) for n in (0,1,2)],[7,91,91])

    def test_corrected_com(self):
        for n in (0,1,2):
            bs=sim.cluster(n,"A",-12,3,.7,-.2,.41,"isolated_equilibrium")
            m=sum(x["mass"] for x in bs)
            for key,target in (("x",-12),("y",3),("vx",.7),("vy",-.2)):
                self.assertAlmostEqual(sum(x["mass"]*x[key] for x in bs)/m,target,places=11)

    def test_softened_orbit_balance(self):
        for n in (1,2):
            bs=sim.cluster(n,"A",0,0,0,0,.37,"isolated_equilibrium")
            ax,ay=sim.accel(bs)
            for i,b in enumerate(bs):
                if b["kind"]=="sat":
                    r=math.hypot(b["x"],b["y"])
                    radial=(ax[i]*b["x"]+ay[i]*b["y"])/r
                    omega=math.hypot(b["vx"],b["vy"])/r
                    self.assertAlmostEqual(radial,-omega*omega*r,delta=1e-12)

    def test_legacy_parity(self):
        expected={0:(8.397,False),1:(8.000,True),2:(8.043,False)}
        for n,(d,hit) in expected.items():
            r=sim.run_one(n,29,46,.002,26000,0,"legacy_html")
            self.assertAlmostEqual(r["min_core_distance_before_contact_or_end"],d,delta=.003)
            self.assertEqual(r["core_contact"],hit)
            self.assertTrue(math.isfinite(r["linear_momentum_drift"]))

    def test_merger_immediate_metrics(self):
        r=sim.run_one(1,29,46,.002,26000,10000,"legacy_html")
        self.assertTrue(r["merger_proxy"])
        self.assertGreater(r["kinetic_energy_dissipated_by_core_velocity_merge"],0)
        self.assertIsNotNone(r["total_energy_jump_at_merger"])
        self.assertIsNotNone(r["angular_momentum_change_at_merger_z"])
        self.assertLess(r["linear_momentum_drift_after_post_observation"],1e-10)

if __name__=="__main__": unittest.main()
