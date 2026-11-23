"""Pipeline: Sprache -> Mathematik -> Physik -> Chemie -> Biologie -> Psychologie."""
from __future__ import annotations

import sys

from matrixgame_formeln import (chemie, biologie, mathematik, physik, psychologie,
                                sprache, verifiziere_system)


def main() -> int:
    print("=" * 60)
    print("Matrixgame Pipeline Demonstration")
    print("=" * 60)

    ok = verifiziere_system(ausführlich=False)
    print(f"Genesis-Core: {'SYSTEM STABIL (30/30 Axiome verifiziert)' if ok else 'FEHLER'}")

    intent = "Synergie"
    print(f"[1 Sprache] Konzept '{intent}' -> Skalar: {sprache.semantik_zu_skalar(intent)}")
    print(f"[1 Sprache] Bewusstseinsoperator: '{sprache.bewusstseins_operator(intent, 0.1)}'")

    print(f"[2 Mathematik] Pythagoras 3,4 -> c = {mathematik.pythagoras(3.0, 4.0)}")
    print(f"[2 Mathematik] Entropie [0.7,0.3]: {mathematik.shannon_entropie([0.7, 0.3]):.4f} bit")

    a_vec, b_vec = physik.gravitationsbeschleunigungskomponenten(
        masse_1=1.0, pos_1=(0.0, 0.0, 0.0), masse_2=2.0, pos_2=(3.0, 4.0, 0.0))
    print(f"[3 Physik] Gravitation: a1={a_vec}, a2={b_vec}")
    print(f"[3 Physik] Kinetische Energie m=2, v=3: {physik.kinetische_energie(2.0, 3.0)} J")
    print(f"[3 Physik] Entropie-Verknüpfungen bei 3 Dimensionen: {physik.entropie_verknüpfungen(3)}")

    print(f"[4 Chemie] Michaelis-Menten S=4: {chemie.michaelis_menten(4.0, 10.0, 1.0):.4f}")

    f = biologie.fitness(0.8, 0.6, 0.9)
    neu = biologie.replikator_dynamik([0.6, 0.4], [f, 0.5])
    print(f"[5 Biologie] Fitness: {f:.4f}; Replicator-Schritt: {[round(x, 4) for x in neu]}")

    p = psychologie.emotionale_entscheidung(2.0, 1.5, {"excitement": 0.8, "fear": 0.2})
    print(f"[6 Psychologie] Emotionale Entscheidung: {p}")
    print(f"[6 Psychologie] Phi_Game: {psychologie.phi_game(1.0, 0.8, 0.9, 0.7, 0.6, 0.1):.4f}")
    print(f"[6 Psychologie] TMT-Motivation: {psychologie.tmt_motivation(0.8, 10.0, 0.35, 1.0):.2f}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
