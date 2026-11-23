"""Small, auditable material-mass and scale model for Second Earth.

All lengths are metres, volumes m^3, densities kg/m^3, masses kg,
accelerations m/s^2 and forces newtons. Reference densities are nominal
values near 20 °C; real samples vary with purity, alloy, porosity and state.
"""
from dataclasses import dataclass
from typing import Mapping

STANDARD_EARTH_GRAVITY_M_S2 = 9.81
DEFAULT_CUBE_EDGE_M = 1.0


@dataclass(frozen=True)
class Material:
    name: str
    density_kg_m3: float
    temperature_c: float = 20.0
    note: str = "Nominal reference value; sample composition and condition matter."


# Educational reference values near 20 °C, not material certificates.
MATERIALS: Mapping[str, Material] = {
    "gold": Material("Gold", 19320.0),
    "silver": Material("Silver", 10490.0),
    "copper": Material("Copper", 8960.0),
    "iron": Material("Iron", 7870.0),
    "water": Material("Water", 1000.0, note="Rounded nominal value; actual water density depends on temperature."),
}


def cube_volume(edge_m: float = DEFAULT_CUBE_EDGE_M) -> float:
    """Return volume of a cube with the given edge length in metres."""
    if edge_m <= 0:
        raise ValueError("edge_m must be greater than zero")
    return edge_m ** 3


def mass_from_density(density_kg_m3: float, volume_m3: float) -> float:
    """Calculate mass m = rho * V in kilograms."""
    if density_kg_m3 <= 0 or volume_m3 <= 0:
        raise ValueError("density and volume must be greater than zero")
    return density_kg_m3 * volume_m3


def weight_force(mass_kg: float, gravity_m_s2: float = STANDARD_EARTH_GRAVITY_M_S2) -> float:
    """Calculate gravitational force F = m * g in newtons."""
    if mass_kg < 0 or gravity_m_s2 < 0:
        raise ValueError("mass and gravity must not be negative")
    return mass_kg * gravity_m_s2


def scale_mass_from_force(force_n: float, local_gravity_m_s2: float) -> float:
    """Infer mass from a force sensor only when local gravity is known."""
    if force_n < 0 or local_gravity_m_s2 <= 0:
        raise ValueError("force must be non-negative and gravity must be positive")
    return force_n / local_gravity_m_s2


def infer_gravity_from_known_mass(force_n: float, known_mass_kg: float) -> float:
    """Infer local gravitational acceleration from a known test mass."""
    if force_n < 0 or known_mass_kg <= 0:
        raise ValueError("force must be non-negative and known mass must be positive")
    return force_n / known_mass_kg


def material_block(material_key: str, edge_m: float = DEFAULT_CUBE_EDGE_M,
                   gravity_m_s2: float = STANDARD_EARTH_GRAVITY_M_S2) -> dict:
    """Return a serializable physical summary for a solid cubic block."""
    try:
        material = MATERIALS[material_key.lower()]
    except KeyError as exc:
        raise KeyError(f"Unknown material: {material_key}") from exc
    volume = cube_volume(edge_m)
    mass = mass_from_density(material.density_kg_m3, volume)
    return {
        "material": material.name,
        "edge_m": edge_m,
        "volume_m3": volume,
        "density_kg_m3": material.density_kg_m3,
        "mass_kg": mass,
        "weight_force_n": weight_force(mass, gravity_m_s2),
        "gravity_m_s2": gravity_m_s2,
        "reference_temperature_c": material.temperature_c,
        "model_status": "REFERENCE_MODEL",
    }
