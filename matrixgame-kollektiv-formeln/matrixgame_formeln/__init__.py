"""Matrixgame Kollektiv-Formeln: Sprache -> Mathematik -> Physik -> Chemie -> Biologie -> Psychologie.

Strukturierte Python-Übersetzung der Kollektiven Formelsammlung für das
Matrixgame und die kollektiven KI-Games (A. Schneider, Darmstadt-Eberstadt).

Meta-Didaktik: "Nicht verbieten, sondern überbieten."
Alle Module kombinierbar, testbar und kollektiv verbesserbar.
"""

from .genesis_core import AXIOME, verifiziere_system
from . import sprache, mathematik, physik, chemie, biologie, psychologie  # noqa: F401

__all__ = [
    "AXIOME", "verifiziere_system",
    "sprache", "mathematik", "physik", "chemie", "biologie", "psychologie",
]
__version__ = "1.0.0"
