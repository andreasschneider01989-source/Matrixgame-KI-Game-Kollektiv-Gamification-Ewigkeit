"""Chemical-kinetics primitives. Michaelis-Menten is an enzyme model."""
from __future__ import annotations
import math


def michaelis_menten_rate(substrate_concentration: float, vmax: float, km: float) -> float:
    """v = Vmax*[S]/(Km+[S]); [S] and Km must share concentration units."""
    substrate, maximum_rate, michaelis_constant = map(float, (substrate_concentration, vmax, km))
    if any(not math.isfinite(value) for value in (substrate, maximum_rate, michaelis_constant)):
        raise ValueError("Kinetic parameters must be finite.")
    if substrate < 0 or maximum_rate < 0 or michaelis_constant <= 0:
        raise ValueError("Require substrate >= 0, vmax >= 0, and km > 0.")
    return maximum_rate * substrate / (michaelis_constant + substrate)
