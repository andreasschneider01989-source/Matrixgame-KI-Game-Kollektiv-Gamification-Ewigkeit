"""Ebene 2: MATHEMATIK (Kern-Transformationen aus der Formelsammlung)."""

from __future__ import annotations

import math
from typing import Iterable, List


def pythagoras(a: float, b: float) -> float:
    """Satz des Pythagoras: a^2 + b^2 = c^2 -> c = sqrt(a^2 + b^2)."""
    return math.sqrt(a * a + b * b)


def shannon_entropie(wahrscheinlichkeiten: Iterable[float]) -> float:
    """Zufall mit Angleichung -> Entropie: S = -sum(p_i * log2(p_i)) [bit]."""
    return -sum(p * math.log2(p) for p in wahrscheinlichkeiten if p > 0)


def logistisches_wachstum(t: float, k: float, n0: float, r: float) -> float:
    """Logistisches Wachstum (x_i): N(t) = K / (1 + ((K-N0)/N0)*e^(-r*t))."""
    if n0 <= 0 or k <= 0:
        raise ValueError("n0 und k müssen positiv sein.")
    return k / (1.0 + ((k - n0) / n0) * math.exp(-r * t))


def fraktale_dimension(box_zaehler: List[int], epsilons: List[float]) -> float:
    """Box-Counting: N(eps) ~ eps^(-D) -> D = Steigung von log N gegen log(1/eps)."""
    xs = [math.log(1.0 / e) for e in epsilons]
    ys = [math.log(float(n)) for n in box_zaehler]
    n = len(xs)
    if n < 2:
        raise ValueError("Mindestens zwei Skalen Needed.")
    mx, my = sum(xs) / n, sum(ys) / n
    nenner = sum((x - mx) * (y - my) for x, y in zip(xs, ys))
    teiler = sum((x - mx) ** 2 for x in xs)
    return nenner / teiler if teiler else 0.0


def fraktale_skalierung(x: float, a: float, b: float) -> float:
    """Skaleninvarianz: f(x) = a * f(b * x); ein Schritt der Rekursion."""
    return a * (b * x)
