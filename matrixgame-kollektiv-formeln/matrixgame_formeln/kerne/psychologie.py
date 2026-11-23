"""Ebene 6: PSYCHOLOGIE (Entscheidungsfindung, Motivation, kollektives Bewusstsein).

Softmax-Entscheidungen, Emotions-Skew, Hogen-Skala, Phi_Game
(MatrixGenesisDecisionQuest) und Motivations-Formeln (TMT, RPE).
"""
from __future__ import annotations

import math
from typing import Dict


def softmax_entscheidung(nutzen_a: float, nutzen_b: float, temperatur: float = 1.0) -> Dict[str, float]:
    """Softmax: P(A) = e^(Ua/T) / (e^(Ua/T) + e^(Ub/T))."""
    ea = math.exp(nutzen_a / temperatur)
    eb = math.exp(nutzen_b / temperatur)
    gesamt = ea + eb
    return {"aktion_A": ea / gesamt, "aktion_B": eb / gesamt}


def emotionale_entscheidung(nutzen_a: float, nutzen_b: float,
                            emotionen: Dict[str, float], temperatur: float = 1.0) -> Dict[str, float]:
    """Emotions-Skew: Begeisterung verstärkt Wachstum (A), Angst verstärkt Sicherheit (B)."""
    angst = emotionen.get("fear", 0.0)
    begeisterung = emotionen.get("excitement", 0.0)
    ua = nutzen_a * (1.0 + begeisterung) - (angst * 0.5)
    ub = nutzen_b * (1.0 + angst) - (begeisterung * 0.2)
    return softmax_entscheidung(ua, ub, temperatur)


def hogen_skala(stabilität: float, adaptabilität: float, konnektivität: float, joy: float) -> float:
    """Hogen = Mittelwert aus Stabilität, Adaptabilität, Konnektivität, Joy (je 0..100)."""
    return (stabilität + adaptabilität + konnektivität + joy) / 4.0


def phi_game(i: float, c: float, v: float, g: float, j: float, rho: float) -> float:
    """Phi_Game = I * C * V * G * J / (1 + rho): Flusswiderstand rho dämpft den Wert."""
    if rho < 0:
        raise ValueError("rho (Flusswiderstand) darf nicht negativ sein.")
    return (i * c * v * g * j) / (1.0 + rho)


def tmt_motivation(erwartung: float, wert: float, impulsivität: float, distanz: float) -> float:
    """Steel TMT: M = E * V / (1 + Gamma * D). "Die Nacht vor der Deadline ist keine Tugend." """
    if distanz < 0:
        raise ValueError("Distanz darf nicht negativ sein.")
    return (erwartung * wert) / (1.0 + impulsivität * distanz)


def rpe(belohnung: float, vorhersage: float) -> float:
    """Schultz Reward-Prediction-Error: RPE = Belohnung - Vorhersage.
    "Dopamin ist kein Lust-Stoff, es ist ein Prognosefehler."
    """
    return belohnung - vorhersage


def selbstkonzordanz(eigene: float, sollten: float) -> float:
    """Sheldon: positiv = eigenes Ziel sammelt Momentum, kontrolliertes erodiert."""
    return eigene - sollten
