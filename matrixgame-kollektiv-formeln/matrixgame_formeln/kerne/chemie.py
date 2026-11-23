"""Ebene 4: CHEMIE (Reaktionskinetik als Symbolik für Zustandswechsel)."""
from __future__ import annotations

import math


def michaelis_menten(substrat: float, v_max: float, k_m: float) -> float:
    """Enzymkinetik (Ressourcen-Aufnahme): v = v_max * S / (K_m + S)."""
    if k_m + substrat == 0:
        raise ZeroDivisionError("K_m + S darf nicht 0 sein.")
    return (v_max * substrat) / (k_m + substrat)


def arrhenius_rate(a_faktor: float, aktivierungs_energie: float, temperatur: float,
                   r: float = 8.31446261815324) -> float:
    """Arrhenius: k = A * exp(-Ea / (R*T))."""
    if temperatur <= 0:
        raise ValueError("T > 0 erforderlich.")
    return a_faktor * math.exp(-aktivierungs_energie / (r * temperatur))


def reaktionsgleichgewicht(k_vor: float, k_rück: float) -> float:
    """Gleichgewichtskonstante K = k_vor / k_rück (Angleichung = Entropie-Symbolik)."""
    if k_rück == 0:
        raise ZeroDivisionError("k_rück darf nicht 0 sein.")
    return k_vor / k_rück
