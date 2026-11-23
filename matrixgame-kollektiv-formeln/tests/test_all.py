"""Selbsttests:  python -m unittest tests.test_all -v"""
from __future__ import annotations

import math
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from matrixgame_formeln import (  # noqa: E402
    AXIOME, biologie, chemie, mathematik, physik, psychologie, sprache,
    verifiziere_system,
)


class TestGenesisCore(unittest.TestCase):
    def test_alle_30_axiome_verifiziert(self):
        self.assertTrue(verifiziere_system(ausführlich=False))
        self.assertEqual(len(AXIOME), 30)

    def test_pythagoras_axiom(self):
        self.assertAlmostEqual(3**2 + 4**2, 5**2)

    def test_gravitation_erde(self):
        g = 6.67430e-11 * 5.972e24 / (6.371e6 ** 2)
        self.assertAlmostEqual(g, 9.81, delta=0.01)


class TestSprache(unittest.TestCase):
    def test_semantik_skalar(self):
        self.assertAlmostEqual(sprache.semantik_zu_skalar("Synergie"), 1.0)
        self.assertAlmostEqual(sprache.semantik_zu_skalar("Entropie"), -0.5)
        self.assertAlmostEqual(sprache.semantik_zu_skalar("unbekannt"), 0.0)

    def test_bewusstseins_operator(self):
        self.assertEqual(sprache.bewusstseins_operator("Harmonie", 0.1), "schöpfend")


class TestMathematik(unittest.TestCase):
    def test_pythagoras(self):
        self.assertAlmostEqual(mathematik.pythagoras(3, 4), 5.0)

    def test_shannon_entropie_gleichgewicht(self):
        self.assertAlmostEqual(mathematik.shannon_entropie([0.5, 0.5]), 1.0)

    def test_logistisches_wachstum_sättigt(self):
        self.assertGreater(mathematik.logistisches_wachstum(100, 1000, 10, 0.1), 990)

    def test_fraktale_dimension(self):
        self.assertAlmostEqual(mathematik.fraktale_dimension([1, 4, 16], [1.0, 0.5, 0.25]), 2.0, places=6)


class TestPhysik(unittest.TestCase):
    def test_newton2(self):
        self.assertAlmostEqual(physik.kraft(5.0, 2.0), 10.0)

    def test_strecke_gleichmäßig(self):
        self.assertAlmostEqual(physik.strecke_gleichmäßig_beschleunigt(2.0, 3.0), 9.0)

    def test_zeit_aus_strecke_rückgängig(self):
        self.assertAlmostEqual(physik.zeit_aus_strecke(9.0, 2.0), 3.0)

    def test_gravitation_antisymmetrisch(self):
        a1, a2 = physik.gravitationsbeschleunigungskomponenten(1.0, (0, 0, 0), 1.0, (1.0, 0, 0))
        for x1, x2 in zip(a1, a2):
            self.assertAlmostEqual(x1, -x2)

    def test_actio_reactio(self):
        self.assertAlmostEqual(physik.actio_reactio(7.5), -7.5)

    def test_intensität(self):
        self.assertAlmostEqual(physik.intensität_quadrat_abfall(4.0, 2.0), 1.0)

    def test_singularity_deckel(self):
        a1, a2 = physik.gravitationsbeschleunigungskomponenten(1.0, (0, 0, 0), 1.0, (0, 0, 0))
        self.assertTrue(all(math.isfinite(c) for c in a1 + a2))

    def test_lorentz_faktor(self):
        self.assertAlmostEqual(physik.lorentz_faktor(0.6), 1.25)

    def test_entropie_verknuepfungen(self):
        self.assertEqual(physik.entropie_verknüpfungen(3), 6)


class TestChemie(unittest.TestCase):
    def test_michaelis_menten_saturation(self):
        self.assertAlmostEqual(chemie.michaelis_menten(1000, 10, 1), 10.0 * 1000 / 1001, places=6)

    def test_reaktionsgleichgewicht(self):
        self.assertAlmostEqual(chemie.reaktionsgleichgewicht(4.0, 2.0), 2.0)


class TestBiologie(unittest.TestCase):
    def test_fitness_gewichte(self):
        self.assertAlmostEqual(biologie.fitness(1.0, 1.0, 1.0), 1.0)

    def test_replikator_erhält_summe(self):
        neu = biologie.replikator_dynamik([300, 700], [2.0, 0.5])
        self.assertAlmostEqual(sum(neu), 1.0)
        self.assertGreater(neu[0], neu[1])

    def test_crispr_strikt(self):
        self.assertTrue(biologie.genetischer_abstand(1.0, 0))
        self.assertFalse(biologie.genetischer_abstand(0.98, 0))
        self.assertFalse(biologie.genetischer_abstand(1.0, 1))


class TestPsychologie(unittest.TestCase):
    def test_softmax_summiert_1(self):
        p = psychologie.softmax_entscheidung(2.0, 1.0)
        self.assertAlmostEqual(p["aktion_A"] + p["aktion_B"], 1.0)
        self.assertGreater(p["aktion_A"], p["aktion_B"])

    def test_angst_erhöht_sicherheit(self):
        neutral = psychologie.softmax_entscheidung(2.0, 2.0)
        angst = psychologie.emotionale_entscheidung(2.0, 2.0, {"fear": 1.0})
        self.assertGreater(angst["aktion_B"], neutral["aktion_B"])

    def test_hogen_skala(self):
        self.assertAlmostEqual(psychologie.hogen_skala(80, 70, 90, 60), 75.0)

    def test_phi_game_rho_faellt_negativ(self):
        with self.assertRaises(ValueError):
            psychologie.phi_game(1, 1, 1, 1, 1, -0.1)

    def test_rpe(self):
        self.assertAlmostEqual(psychologie.rpe(1.0, 0.8), 0.2)


if __name__ == "__main__":
    unittest.main(verbosity=2)
