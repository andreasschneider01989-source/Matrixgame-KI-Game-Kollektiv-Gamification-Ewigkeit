"""Ebene 5: BIOLOGIE (Evolution, Fitness, Selektion, CRISPR-Symbolik)."""
from __future__ import annotations

from typing import List, Sequence


def fitness(energie: float, information: float, kooperation: float,
            gewichte: Sequence[float] = (0.3, 0.4, 0.3)) -> float:
    """Fitness im Matrixgame: gewichtete Summe Energie/Information/Kooperation.

    Kooperation ist der kollektive Term:
    "Gemeinsam sind wir mehr als die Summe unserer Teile."
    """
    return sum(g * x for g, x in zip(gewichte, (energie, information, kooperation)))


def replikator_dynamik(anteile: List[float], fitness_werte: List[float]) -> List[float]:
    """Natürliche Selektion (Replicator Dynamics): x_i' = x_i * (f_i / f_avg).

    Re-Normierung gegen numerisches Drift: die Normierung ist der Deckel.
    """
    gesamt = sum(anteile)
    normiert = [x / gesamt for x in anteile]
    f_avg = sum(x * f for x, f in zip(normiert, fitness_werte))
    if f_avg == 0:
        return normiert
    neu = [x * (f / f_avg) for x, f in zip(normiert, fitness_werte)]
    s = sum(neu)
    return [n / s for n in neu]


def genetischer_abstand(match: float, toleranz: int) -> bool:
    """CRISPR/Cas9-Schnittregel: Toleranz=0 schneidet nur 100%-Matches.
    "Effizienz schlägt Kaliber."
    """
    return match == 1.0 and toleranz == 0


def zellteilungs_deckel(r: float, r_max: float = 22.0, alpha: float = 0.05) -> float:
    """Wachstum mit Fettmaster-Deckel (Matrixgame v5): R' = min(R*(1+alpha), R_max)."""
    return min(r * (1.0 + alpha), r_max)
