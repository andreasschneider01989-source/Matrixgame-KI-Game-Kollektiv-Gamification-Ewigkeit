"""A deterministic, bounded toy model for regenerative Second Earth scenarios.

This module is an educational simulation primitive, not an empirical forecast,
policy optimizer, clinical assessment, or measurement of human wellbeing.
All state values and intervention levels are dimensionless indices in [0, 1].
The illustrative coefficients must be calibrated before any real-world use.
"""
from __future__ import annotations

from dataclasses import dataclass
import math

MODEL_VERSION = "0.1.0"
MODEL_STATUS = "ILLUSTRATIVE_MODEL_NOT_EMPIRICALLY_VALIDATED"


def _check_unit_interval(name: str, value: float) -> None:
    if isinstance(value, bool) or not isinstance(value, (int, float)):
        raise TypeError(f"{name} must be a numeric value in [0, 1].")
    if not math.isfinite(float(value)) or not 0.0 <= float(value) <= 1.0:
        raise ValueError(f"{name} must be finite and in [0, 1].")


def _clamp01(value: float) -> float:
    return min(1.0, max(0.0, value))


@dataclass(frozen=True)
class SecondEarthState:
    """Independent normalized state dimensions; no aggregate paradise score."""

    biodiversity: float = 0.50
    clean_energy: float = 0.50
    wellbeing: float = 0.50
    trust: float = 0.50
    safety: float = 0.50

    def __post_init__(self) -> None:
        for name, value in vars(self).items():
            _check_unit_interval(name, value)


@dataclass(frozen=True)
class RegenerativeActions:
    """Normalized scenario levers, not literal budgets or measured interventions."""

    habitat_restoration: float = 0.0
    renewable_energy: float = 0.0
    health_access: float = 0.0
    open_education: float = 0.0
    cooperation: float = 0.0
    emergency_preparedness: float = 0.0

    def __post_init__(self) -> None:
        for name, value in vars(self).items():
            _check_unit_interval(name, value)


@dataclass(frozen=True)
class ExternalPressures:
    """Scenario pressures, also normalized; zero means no modeled pressure."""

    ecosystem_stress: float = 0.0
    transition_friction: float = 0.0
    social_stress: float = 0.0
    information_disorder: float = 0.0
    hazard_pressure: float = 0.0

    def __post_init__(self) -> None:
        for name, value in vars(self).items():
            _check_unit_interval(name, value)


def advance(
    state: SecondEarthState,
    actions: RegenerativeActions = RegenerativeActions(),
    pressures: ExternalPressures = ExternalPressures(),
    *,
    dt: float = 1.0,
) -> SecondEarthState:
    """Advance one deterministic step using transparent illustrative rates.

    The model intentionally keeps five dimensions separate. It does not infer
    causal relationships from real data and it is not calibrated to a region.
    """
    if isinstance(dt, bool) or not isinstance(dt, (int, float)):
        raise TypeError("dt must be a positive finite number.")
    if not math.isfinite(float(dt)) or float(dt) <= 0.0:
        raise ValueError("dt must be a positive finite number.")
    dt = float(dt)

    biodiversity = _clamp01(
        state.biodiversity
        + dt * (
            0.06 * actions.habitat_restoration
            + 0.02 * actions.cooperation
            - 0.04 * pressures.ecosystem_stress
        )
    )
    clean_energy = _clamp01(
        state.clean_energy
        + dt * (
            0.08 * actions.renewable_energy
            + 0.02 * actions.open_education
            - 0.03 * pressures.transition_friction
        )
    )
    wellbeing = _clamp01(
        state.wellbeing
        + dt * (
            0.04 * actions.health_access
            + 0.02 * (state.clean_energy - 0.50)
            + 0.02 * actions.cooperation
            - 0.04 * pressures.social_stress
        )
    )
    trust = _clamp01(
        state.trust
        + dt * (
            0.04 * actions.open_education
            + 0.04 * actions.cooperation
            - 0.04 * pressures.information_disorder
        )
    )
    safety = _clamp01(
        state.safety
        + dt * (
            0.05 * actions.emergency_preparedness
            + 0.03 * actions.cooperation
            - 0.05 * pressures.hazard_pressure
        )
    )
    return SecondEarthState(
        biodiversity=biodiversity,
        clean_energy=clean_energy,
        wellbeing=wellbeing,
        trust=trust,
        safety=safety,
    )


def safety_gate_open(state: SecondEarthState, *, minimum_safety: float = 0.60) -> bool:
    """Return a separate safety gate; other dimensions cannot compensate for it."""
    _check_unit_interval("minimum_safety", minimum_safety)
    return state.safety >= minimum_safety


def run_scenario(
    initial: SecondEarthState,
    actions: RegenerativeActions,
    pressures: ExternalPressures = ExternalPressures(),
    *,
    steps: int = 10,
    dt: float = 1.0,
) -> tuple[SecondEarthState, ...]:
    """Run a repeatable scenario; result includes the initial state at index 0."""
    if isinstance(steps, bool) or not isinstance(steps, int) or steps < 0:
        raise ValueError("steps must be a non-negative integer.")
    history = [initial]
    current = initial
    for _ in range(steps):
        current = advance(current, actions, pressures, dt=dt)
        history.append(current)
    return tuple(history)
