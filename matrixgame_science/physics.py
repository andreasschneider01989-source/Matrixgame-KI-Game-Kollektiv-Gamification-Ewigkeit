"""Classical mechanics primitives in SI units; not relativistic or quantum."""
from __future__ import annotations
import math
from typing import Sequence

G_NEWTON = 6.67430e-11  # m^3 kg^-1 s^-2


def _vector(values: Sequence[float], name: str) -> tuple[float, ...]:
    result = tuple(float(value) for value in values)
    if not result or any(not math.isfinite(value) for value in result):
        raise ValueError(f"{name} must contain finite values and not be empty.")
    return result


def relative_position(origin: Sequence[float], target: Sequence[float]) -> tuple[float, ...]:
    first, second = _vector(origin, "origin"), _vector(target, "target")
    if len(first) != len(second):
        raise ValueError("Position vectors must have equal dimensions.")
    return tuple(b - a for a, b in zip(first, second))


def kinetic_energy(mass_kg: float, speed_m_s: float) -> float:
    mass, speed = float(mass_kg), float(speed_m_s)
    if not math.isfinite(mass) or mass < 0 or not math.isfinite(speed):
        raise ValueError("Mass must be non-negative and both inputs finite.")
    return 0.5 * mass * speed * speed


def gravitational_acceleration(
    position: Sequence[float],
    source_position: Sequence[float],
    source_mass_kg: float,
    gravitational_constant: float = G_NEWTON,
) -> tuple[float, ...]:
    """Newtonian acceleration toward one point source: G*M*(source-target)/r^3."""
    delta = relative_position(position, source_position)
    mass, constant = float(source_mass_kg), float(gravitational_constant)
    if not math.isfinite(mass) or mass < 0:
        raise ValueError("source_mass_kg must be finite and non-negative.")
    if not math.isfinite(constant) or constant <= 0:
        raise ValueError("gravitational_constant must be finite and positive.")
    r2 = sum(value * value for value in delta)
    if r2 == 0:
        raise ValueError("Target and source positions must not coincide.")
    distance = math.sqrt(r2)
    factor = constant * mass / (r2 * distance)
    return tuple(factor * value for value in delta)


def constant_acceleration_step(
    position: Sequence[float], velocity: Sequence[float],
    acceleration: Sequence[float], dt_seconds: float,
) -> tuple[tuple[float, ...], tuple[float, ...]]:
    pos, vel, acc = _vector(position, "position"), _vector(velocity, "velocity"), _vector(acceleration, "acceleration")
    if not (len(pos) == len(vel) == len(acc)):
        raise ValueError("Position, velocity, and acceleration dimensions must match.")
    dt = float(dt_seconds)
    if not math.isfinite(dt) or dt < 0:
        raise ValueError("dt_seconds must be finite and non-negative.")
    next_pos = tuple(x + v * dt + 0.5 * a * dt * dt for x, v, a in zip(pos, vel, acc))
    next_vel = tuple(v + a * dt for v, a in zip(vel, acc))
    return next_pos, next_vel
