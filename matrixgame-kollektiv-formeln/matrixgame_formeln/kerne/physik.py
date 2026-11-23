"""Ebene 3: PHYSIK (Formelsammlung 1.0-1.7 des Matrixgame).

Reine Übersetzungen der Originalformeln: Kinematik, Newton 1-3,
Gravitation mit normierten Richtungsvektoren und Singularitäts-Deckel,
Energie, SI-Einheiten-Registry.
"""

from __future__ import annotations

import math
from typing import Sequence, Tuple

from ..genesis_core import C_LICHT, G_GRAVITATION, H_PLANCK

Vec3 = Tuple[float, float, float]


# --- Kinematik (Formelsammlung 1.0) -----------------------------------------
def geschwindigkeit(strecke: float, zeit: float) -> float:
    """v = s / t (Durchschnittsgeschwindigkeit)."""
    if zeit == 0:
        raise ZeroDivisionError("Zeit darf nicht 0 sein.")
    return strecke / zeit


def beschleunigung(end_geschwindigkeit: float, zeit: float) -> float:
    """a = v_end / t = 2 * v_avg / t."""
    if zeit == 0:
        raise ZeroDivisionError("Zeit darf nicht 0 sein.")
    return end_geschwindigkeit / zeit


def strecke_gleichmäßig_beschleunigt(a: float, t: float) -> float:
    """s = 0.5 * a * t^2 (Start aus Ruhe)."""
    return 0.5 * a * t * t


def zeit_aus_strecke(s: float, a: float) -> float:
    """t = sqrt(2*s / a) (Umkehrung von s = 0.5*a*t^2)."""
    if a <= 0:
        raise ValueError("Beschleunigung muss positiv sein.")
    return math.sqrt(2.0 * s / a)


def kraft(masse: float, a: float) -> float:
    """Newton 2: F = m * a (äquivalent: a = F/m, m = F/a)."""
    return masse * a


def actio_reactio(f_auf_b: float) -> float:
    """Newton 3: F(A->B) = -F(B->A)."""
    return -f_auf_b


# --- Gravitation (Normierung der Richtungsvektoren) --------------------------
def abstand(p1: Vec3, p2: Vec3) -> float:
    """r = sqrt((dx)^2 + (dy)^2 + (dz)^2), Pythagoras in 3D."""
    return math.sqrt(sum((b - a) ** 2 for a, b in zip(p1, p2)))


def normiere(vektor: Vec3) -> Vec3:
    """Richtungsvektor auf Länge 1 (neutral bei Multiplikation)."""
    länge = math.sqrt(sum(c * c for c in vektor))
    if länge == 0:
        return (0.0, 0.0, 0.0)
    return tuple(c / länge for c in vektor)  # type: ignore[return-value]


def gravitationsbeschleunigungskomponenten(
    masse_1: float, pos_1: Vec3, masse_2: float, pos_2: Vec3,
    epsilon_singularity: float = 1e-4,
) -> Tuple[Vec3, Vec3]:
    """F = G*m1*m2/r^2; a1 = G*m2/r^2, a2 = G*m1/r^2, Richtung normiert.

    Epsilon-Deckel überbietet die Singularität:
    "Ein Deckel entscheidet, ob ein System Struktur zeigt oder nur Kollaps."
    """
    r = max(abstand(pos_1, pos_2), epsilon_singularity)
    a1 = G_GRAVITATION * masse_2 / (r * r)
    a2 = G_GRAVITATION * masse_1 / (r * r)
    richtung = normiere(tuple(b - a for a, b in zip(pos_1, pos_2)))  # 1 -> 2
    a1_vec = tuple(a1 * c for c in richtung)
    a2_vec = tuple(-a2 * c for c in richtung)
    return a1_vec, a2_vec  # type: ignore[return-value]


def intensität_quadrat_abfall(intensität_0: float, r: float) -> float:
    """Invers-Quadrat-Gesetz im 3D-Raum: I = I0 / r^2 (Beweis der 3 Dimensionen)."""
    if r == 0:
        raise ZeroDivisionError("Abstand 0: Intensität singulär.")
    return intensität_0 / (r * r)


# --- Energie, Impuls, Arbeit ------------------------------------------------
def kinetische_energie(masse: float, v: float) -> float:
    """E_k = 0.5 * m * v^2."""
    return 0.5 * masse * v * v


def impuls(masse: float, v: float) -> float:
    """p = m * v."""
    return masse * v


def arbeit(f: float, d: float) -> float:
    """W = F * d."""
    return f * d


def zentripetalkraft(masse: float, v: float, r: float) -> float:
    """F_z = m * v^2 / r."""
    if r == 0:
        raise ZeroDivisionError("Radius darf nicht 0 sein.")
    return masse * v * v / r


def masse_energie(masse: float) -> float:
    """E = m * c^2."""
    return masse * C_LICHT**2


def planck_einstein(frequenz: float) -> float:
    """E = h * f."""
    return H_PLANCK * frequenz


def lorentz_faktor(v: float) -> float:
    """gamma = 1 / sqrt(1 - v^2/c^2) >= 1 (Zeitdilatation)."""
    if abs(v) >= C_LICHT:
        raise ValueError("v muss kleiner als c sein.")
    return 1.0 / math.sqrt(1.0 - (v / C_LICHT) ** 2)


def entropie(wahrscheinlichkeiten: Sequence[float]) -> float:
    """S = -sum(p_i * log2(p_i)) [bit]: Zufall mit Angleichung."""
    return -sum(p * math.log2(p) for p in wahrscheinlichkeiten if p > 0)


def entropie_verknüpfungen(dimensionen: int) -> int:
    """Formelsammlung 1.1: n Raumdimensionen -> 2*n Entropie-Verknüpfungen pro Säule."""
    return 2 * dimensionen


# --- SI-Einheiten: Registry (Formelsammlung 1.2 / 1.7) -----------------------
SI_BASISEINHEITEN = {
    "Zeit": ("t", "T", "Sekunde", "s"),
    "Länge": ("l", "L", "Meter", "m"),
    "Masse": ("m", "M", "Kilogramm", "kg"),
    "Elektrische Stromstärke": ("I", "I", "Ampere", "A"),
    "Thermodynamische Temperatur": ("T", "Theta", "Kelvin", "K"),
    "Stoffmenge": ("n", "N", "Mol", "mol"),
    "Lichtstärke": ("Iv", "J", "Candela", "cd"),
}

SI_ABGELEITET = {
    "Frequenz": ("Hertz", "Hz", "s^-1"),
    "Kraft": ("Newton", "N", "kg*m*s^-2"),
    "Druck": ("Pascal", "Pa", "kg*m^-1*s^-2"),
    "Energie": ("Joule", "J", "kg*m^2*s^-2"),
    "Leistung": ("Watt", "W", "kg*m^2*s^-3"),
    "Ladung": ("Coulomb", "C", "A*s"),
    "Spannung": ("Volt", "V", "kg*m^2*s^-3*A^-1"),
    "Widerstand": ("Ohm", "Ohm", "kg*m^2*s^-3*A^-2"),
    "Radioaktivität": ("Becquerel", "Bq", "s^-1"),
}
