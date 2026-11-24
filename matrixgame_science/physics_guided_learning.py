"""A tiny learner trained against an explicit law of optical reflection.

Angles are measured from the surface normal. The physical oracle remains
separate from the trainable one-neuron approximation.
"""
from __future__ import annotations

from dataclasses import dataclass
import math
from typing import Iterable


def _angle(value: float, name: str) -> float:
    value = float(value)
    if not math.isfinite(value) or not 0.0 <= value <= 90.0:
        raise ValueError(f"{name} must be finite and within [0, 90] degrees.")
    return value


def reflect_angle_deg(incident_angle_deg: float) -> float:
    """Ideal mirror reflection: equal angles from the surface normal."""
    return _angle(incident_angle_deg, "incident_angle_deg")


def reflect_vector(
    direction: tuple[float, float],
    normal: tuple[float, float],
) -> tuple[float, float]:
    """Reflect a 2D direction about a unit surface normal.

    The returned vector has the same magnitude as the incoming direction.
    The normal is normalized internally and must be non-zero.
    """
    dx, dy = map(float, direction)
    nx, ny = map(float, normal)
    values = (dx, dy, nx, ny)
    if not all(math.isfinite(value) for value in values):
        raise ValueError("direction and normal must contain finite values.")
    normal_length = math.hypot(nx, ny)
    if normal_length == 0.0:
        raise ValueError("normal must be non-zero.")
    nx, ny = nx / normal_length, ny / normal_length
    projection = dx * nx + dy * ny
    return (dx - 2.0 * projection * nx, dy - 2.0 * projection * ny)


@dataclass(frozen=True)
class NeuronTrainingResult:
    """Auditable summary of one deterministic training run."""

    initial_mae_deg: float
    final_mae_deg: float
    epochs: int
    weight: float
    bias: float


class LinearAngleNeuron:
    """Single linear neuron approximating the reflection law.

    This is intentionally a minimal trainable model, not a deep neural net.
    Inputs and outputs are normalized by 90 degrees during optimization.
    """

    def __init__(self, learning_rate: float = 0.05) -> None:
        learning_rate = float(learning_rate)
        if not math.isfinite(learning_rate) or not 0.0 < learning_rate <= 1.0:
            raise ValueError("learning_rate must be finite and in (0, 1].")
        self.learning_rate = learning_rate
        self.weight = 0.0
        self.bias = 0.0

    def predict(self, incident_angle_deg: float) -> float:
        angle = _angle(incident_angle_deg, "incident_angle_deg")
        normalized = angle / 90.0
        prediction = (self.weight * normalized + self.bias) * 90.0
        return min(90.0, max(0.0, prediction))

    @staticmethod
    def _mae(weight: float, bias: float, samples: tuple[tuple[float, float], ...]) -> float:
        errors = []
        for incident, expected in samples:
            x, y = incident / 90.0, expected / 90.0
            errors.append(abs((weight * x + bias) - y) * 90.0)
        return sum(errors) / len(errors)

    def fit(
        self,
        samples: Iterable[tuple[float, float]],
        epochs: int = 100,
    ) -> NeuronTrainingResult:
        data = tuple(
            (_angle(incident, "incident angle"), _angle(expected, "reflected angle"))
            for incident, expected in samples
        )
        if not data:
            raise ValueError("at least one training sample is required.")
        if not isinstance(epochs, int) or epochs < 1:
            raise ValueError("epochs must be a positive integer.")

        initial_mae = self._mae(self.weight, self.bias, data)
        for _ in range(epochs):
            for incident, expected in data:
                x, y = incident / 90.0, expected / 90.0
                error = self.weight * x + self.bias - y
                self.weight -= self.learning_rate * 2.0 * error * x
                self.bias -= self.learning_rate * 2.0 * error

        return NeuronTrainingResult(
            initial_mae_deg=initial_mae,
            final_mae_deg=self._mae(self.weight, self.bias, data),
            epochs=epochs,
            weight=self.weight,
            bias=self.bias,
        )
