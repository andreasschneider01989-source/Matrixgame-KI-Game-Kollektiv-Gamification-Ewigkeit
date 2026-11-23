"""Ebene 1: SPRACHE -> Mathematik.

Semantik-Engine des Colab-Notebooks + Lumini/LUX-Sprachidee.
Formelsammlung: Bewusstsein als Operator B: S -> S' (Qwen-Interpretation).
"""

from __future__ import annotations

from typing import Dict, List

SEMANTIK_MAPPE: Dict[str, float] = {
    "synergie": 1.0,
    "koexistenz": 0.8,
    "harmonie": 0.9,
    "entropie": -0.5,
    "zufall": -0.2,
    "ordnung": 0.7,
    "bewusstsein": 1.0,
    "existenz": 1.0,
}


def semantik_zu_skalar(intent: str) -> float:
    """Sprache -> Zahl: v = M[clip(intent)], Default 0.0."""
    return SEMANTIK_MAPPE.get(intent.strip().lower(), 0.0)


def text_zu_vektor(text: str) -> List[float]:
    """Kombiniert mehrere Begriffe zu einem semantischen Vektor."""
    return [semantik_zu_skalar(t) for t in text.split() if t]


def bewusstseins_operator(zustand: str, mutator: float) -> str:
    """B: S -> S'. Verschiebung der semantischen Valenz -> Steuermodus."""
    v = semantik_zu_skalar(zustand) + mutator
    if v > 0.5:
        return "schöpfend"
    if v > 0.0:
        return "wachsend"
    if v > -0.5:
        return "angleichend"
    return "diffundierend"
