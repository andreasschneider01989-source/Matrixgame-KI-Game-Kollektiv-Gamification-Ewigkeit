"""Population-level evolutionary toy models."""
from __future__ import annotations
import math
from typing import Sequence


def replicator_step(population_shares: Sequence[float], fitness_values: Sequence[float]) -> tuple[float, ...]:
    """Discrete proportional-fitness update x_i' = x_i*f_i / sum_j(x_j*f_j).

    Shares must sum to one; this simplified form requires non-negative fitness.
    It is an abstract model, not a prediction about real populations.
    """
    shares = tuple(float(value) for value in population_shares)
    fitness = tuple(float(value) for value in fitness_values)
    if not shares or len(shares) != len(fitness):
        raise ValueError("Shares and fitness must be non-empty and equal in length.")
    if any(not math.isfinite(value) for value in shares + fitness):
        raise ValueError("Shares and fitness must be finite.")
    if any(value < 0 for value in shares) or any(value < 0 for value in fitness):
        raise ValueError("Shares and fitness must be non-negative.")
    if not math.isclose(sum(shares), 1.0, rel_tol=1e-9, abs_tol=1e-9):
        raise ValueError("Population shares must sum to one.")
    mean_fitness = sum(x * f for x, f in zip(shares, fitness))
    if mean_fitness == 0:
        return shares
    updated = tuple(x * f / mean_fitness for x, f in zip(shares, fitness))
    total = sum(updated)
    return tuple(value / total for value in updated)
