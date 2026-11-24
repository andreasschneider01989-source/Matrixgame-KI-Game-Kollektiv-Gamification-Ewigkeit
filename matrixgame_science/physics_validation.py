"""Numerical mechanics validation helpers with explicit SI dimensions."""
from __future__ import annotations
from dataclasses import dataclass
import math
from typing import Sequence

from .units import ACCELERATION, ENERGY, FORCE, LENGTH, MASS, TIME, VELOCITY, Quantity, require_dimension


def _vector(values: Sequence[float], name: str) -> tuple[float, ...]:
    result = tuple(float(value) for value in values)
    if not result or any(not math.isfinite(v) for v in result):
        raise ValueError(f"{name} must contain finite values and not be empty.")
    return result


@dataclass(frozen=True)
class Trajectory:
    positions: tuple[tuple[float, ...], ...]
    velocities: tuple[tuple[float, ...], ...]
    method: str


@dataclass(frozen=True)
class TrajectoryError:
    position_rmse: float
    velocity_rmse: float
    max_position_error: float
    max_velocity_error: float


def acceleration_from_force(force: Quantity, mass: Quantity) -> Quantity:
    """Return a = F/m after checking coherent SI dimensions and positive mass."""
    require_dimension(force, FORCE, "force")
    require_dimension(mass, MASS, "mass")
    if mass.value <= 0:
        raise ValueError("mass must be positive.")
    return Quantity(force.value / mass.value, ACCELERATION)


def momentum(mass: Quantity, velocity: Quantity) -> Quantity:
    require_dimension(mass, MASS, "mass")
    require_dimension(velocity, VELOCITY, "velocity")
    if mass.value < 0:
        raise ValueError("mass must be non-negative.")
    return Quantity(mass.value * velocity.value, MASS * VELOCITY)


def work(force: Quantity, displacement: Quantity) -> Quantity:
    require_dimension(force, FORCE, "force")
    require_dimension(displacement, LENGTH, "displacement")
    return Quantity(force.value * displacement.value, ENERGY)


def integrate_constant_acceleration(
    initial_position: Sequence[float],
    initial_velocity: Sequence[float],
    acceleration: Sequence[float],
    dt_seconds: float,
    steps: int,
    method: str = "velocity_verlet",
) -> Trajectory:
    """Integrate a constant-acceleration trajectory in coherent SI values.

    Methods: exact, explicit_euler, semi_implicit_euler, velocity_verlet.
    The velocity-Verlet position and velocity updates are exact for constant a.
    """
    position = _vector(initial_position, "initial_position")
    velocity = _vector(initial_velocity, "initial_velocity")
    accel = _vector(acceleration, "acceleration")
    if not (len(position) == len(velocity) == len(accel)):
        raise ValueError("All vectors must have the same dimension.")
    dt = float(dt_seconds)
    if not math.isfinite(dt) or dt <= 0:
        raise ValueError("dt_seconds must be finite and positive.")
    if not isinstance(steps, int) or steps < 0:
        raise ValueError("steps must be a non-negative integer.")
    if method not in {"exact", "explicit_euler", "semi_implicit_euler", "velocity_verlet"}:
        raise ValueError("Unknown integration method.")

    positions = [position]
    velocities = [velocity]
    x, v = position, velocity
    for _ in range(steps):
        if method == "exact" or method == "velocity_verlet":
            next_x = tuple(px + pv * dt + 0.5 * a * dt * dt for px, pv, a in zip(x, v, accel))
            next_v = tuple(pv + a * dt for pv, a in zip(v, accel))
        elif method == "explicit_euler":
            next_x = tuple(px + pv * dt for px, pv in zip(x, v))
            next_v = tuple(pv + a * dt for pv, a in zip(v, accel))
        else:
            next_v = tuple(pv + a * dt for pv, a in zip(v, accel))
            next_x = tuple(px + nv * dt for px, nv in zip(x, next_v))
        x, v = next_x, next_v
        positions.append(x)
        velocities.append(v)
    return Trajectory(tuple(positions), tuple(velocities), method)


def compare_trajectory(candidate: Trajectory, reference: Trajectory) -> TrajectoryError:
    if len(candidate.positions) != len(reference.positions):
        raise ValueError("Trajectories must have the same number of samples.")
    if not candidate.positions:
        raise ValueError("Trajectories must not be empty.")
    p_errors: list[float] = []
    v_errors: list[float] = []
    for cp, rp, cv, rv in zip(candidate.positions, reference.positions,
                               candidate.velocities, reference.velocities):
        if not (len(cp) == len(rp) == len(cv) == len(rv)):
            raise ValueError("Trajectory dimensions must match.")
        p_errors.extend((a - b) ** 2 for a, b in zip(cp, rp))
        v_errors.extend((a - b) ** 2 for a, b in zip(cv, rv))
    if not p_errors or not v_errors:
        raise ValueError("Trajectories must contain vector components.")
    return TrajectoryError(
        math.sqrt(sum(p_errors) / len(p_errors)),
        math.sqrt(sum(v_errors) / len(v_errors)),
        math.sqrt(max(p_errors)),
        math.sqrt(max(v_errors)),
    )
