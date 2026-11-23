"""Minimal SI dimensional algebra. Values use coherent SI units."""
from __future__ import annotations
from dataclasses import dataclass
import math
from typing import Union

Number = Union[int, float]

# Exponent order: mass, length, time, current, temperature, amount, luminous intensity.
@dataclass(frozen=True)
class Dimension:
    exponents: tuple[int, int, int, int, int, int, int]

    def __mul__(self, other: "Dimension") -> "Dimension":
        return Dimension(tuple(a + b for a, b in zip(self.exponents, other.exponents)))

    def __truediv__(self, other: "Dimension") -> "Dimension":
        return Dimension(tuple(a - b for a, b in zip(self.exponents, other.exponents)))

    def __pow__(self, power: int) -> "Dimension":
        if not isinstance(power, int):
            raise TypeError("Dimension powers must be integers.")
        return Dimension(tuple(power * value for value in self.exponents))


DIMENSIONLESS = Dimension((0, 0, 0, 0, 0, 0, 0))
MASS = Dimension((1, 0, 0, 0, 0, 0, 0))
LENGTH = Dimension((0, 1, 0, 0, 0, 0, 0))
TIME = Dimension((0, 0, 1, 0, 0, 0, 0))
CURRENT = Dimension((0, 0, 0, 1, 0, 0, 0))
TEMPERATURE = Dimension((0, 0, 0, 0, 1, 0, 0))
AMOUNT = Dimension((0, 0, 0, 0, 0, 1, 0))
LUMINOUS_INTENSITY = Dimension((0, 0, 0, 0, 0, 0, 1))
VELOCITY = LENGTH / TIME
ACCELERATION = LENGTH / (TIME ** 2)
FORCE = MASS * ACCELERATION
ENERGY = FORCE * LENGTH
VOLUME = LENGTH ** 3
CONCENTRATION = AMOUNT / VOLUME


@dataclass(frozen=True)
class Quantity:
    value: float
    dimension: Dimension

    def __post_init__(self) -> None:
        if not math.isfinite(float(self.value)):
            raise ValueError("Quantity value must be finite.")

    def _check(self, other: "Quantity") -> None:
        if self.dimension != other.dimension:
            raise ValueError(f"Dimension mismatch: {self.dimension} != {other.dimension}")

    def __add__(self, other: "Quantity") -> "Quantity":
        self._check(other)
        return Quantity(self.value + other.value, self.dimension)

    def __sub__(self, other: "Quantity") -> "Quantity":
        self._check(other)
        return Quantity(self.value - other.value, self.dimension)

    def __mul__(self, other: Union["Quantity", Number]) -> "Quantity":
        if isinstance(other, Quantity):
            return Quantity(self.value * other.value, self.dimension * other.dimension)
        if isinstance(other, (int, float)):
            return Quantity(self.value * float(other), self.dimension)
        return NotImplemented

    def __rmul__(self, other: Number) -> "Quantity":
        return self.__mul__(other)

    def __truediv__(self, other: Union["Quantity", Number]) -> "Quantity":
        if isinstance(other, Quantity):
            if other.value == 0:
                raise ZeroDivisionError("Cannot divide by a zero-valued quantity.")
            return Quantity(self.value / other.value, self.dimension / other.dimension)
        if isinstance(other, (int, float)):
            if float(other) == 0:
                raise ZeroDivisionError("Cannot divide by zero.")
            return Quantity(self.value / float(other), self.dimension)
        return NotImplemented


def require_dimension(quantity: Quantity, expected: Dimension, name: str) -> None:
    if quantity.dimension != expected:
        raise ValueError(f"{name} must have dimension {expected}, got {quantity.dimension}.")
