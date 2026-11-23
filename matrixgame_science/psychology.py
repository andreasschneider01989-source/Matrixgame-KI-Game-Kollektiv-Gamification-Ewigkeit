"""Decision models for games; not clinical or validated human assessments."""
from __future__ import annotations
import math
from typing import Sequence
from .mathematics import softmax


def decision_probabilities(
    utilities: Sequence[float], temperature: float = 1.0,
    context_shifts: Sequence[float] | None = None,
) -> tuple[float, ...]:
    """Softmax supplied utilities; context shifts are explicit model parameters.

    The package does not assume that an emotion universally raises or lowers
    the utility of a given action. That mapping must be specified and tested.
    """
    scores = tuple(map(float, utilities))
    if not scores:
        raise ValueError("utilities must not be empty.")
    if context_shifts is not None:
        shifts = tuple(map(float, context_shifts))
        if len(shifts) != len(scores):
            raise ValueError("context_shifts must match utilities in length.")
        scores = tuple(a + b for a, b in zip(scores, shifts))
    return softmax(scores, temperature)


def choose_highest_probability(probabilities: Sequence[float]) -> int:
    values = tuple(map(float, probabilities))
    if not values or any(not math.isfinite(value) or value < 0 for value in values):
        raise ValueError("probabilities must be non-empty and non-negative.")
    if abs(sum(values) - 1.0) > 1e-9:
        raise ValueError("probabilities must sum to one.")
    return max(range(len(values)), key=values.__getitem__)
