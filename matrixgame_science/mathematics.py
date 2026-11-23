"""Mathematical primitives: probability validation, entropy and softmax."""
from __future__ import annotations
import math
from typing import Iterable


def _finite_values(values: Iterable[float], name: str) -> list[float]:
    result = [float(value) for value in values]
    if not result or any(not math.isfinite(value) for value in result):
        raise ValueError(f"{name} must contain at least one finite number.")
    return result


def normalize_probabilities(probabilities: Iterable[float]) -> tuple[float, ...]:
    values = _finite_values(probabilities, "probabilities")
    if any(value < 0 for value in values):
        raise ValueError("Probabilities must be non-negative.")
    total = sum(values)
    if total <= 0:
        raise ValueError("At least one probability must be positive.")
    return tuple(value / total for value in values)


def shannon_entropy(probabilities: Iterable[float], base: float = 2.0) -> float:
    """Shannon information entropy. Default output is bits, not J/K."""
    if not math.isfinite(base) or base <= 1:
        raise ValueError("Logarithm base must be finite and greater than one.")
    distribution = normalize_probabilities(probabilities)
    log_base = math.log(base)
    return -sum(p * math.log(p) / log_base for p in distribution if p > 0)


def softmax(values: Iterable[float], temperature: float = 1.0) -> tuple[float, ...]:
    scores = _finite_values(values, "values")
    if not math.isfinite(temperature) or temperature <= 0:
        raise ValueError("temperature must be finite and greater than zero.")
    scaled = [score / temperature for score in scores]
    maximum = max(scaled)
    exponentials = [math.exp(value - maximum) for value in scaled]
    total = sum(exponentials)
    return tuple(value / total for value in exponentials)
