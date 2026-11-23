"""Educational computational-neuron primitives.

The sigmoid unit is an artificial neuron abstraction, not a biophysical
neuron model and not a way to infer anyone's brain state.
"""
from __future__ import annotations
import math
from typing import Sequence


def sigmoid(value: float) -> float:
    number = float(value)
    if not math.isfinite(number):
        raise ValueError("value must be finite.")
    if number >= 0:
        return 1.0 / (1.0 + math.exp(-number))
    exp_value = math.exp(number)
    return exp_value / (1.0 + exp_value)


def artificial_neuron_activation(inputs: Sequence[float], weights: Sequence[float], bias: float = 0.0) -> float:
    input_values, weight_values = tuple(map(float, inputs)), tuple(map(float, weights))
    bias_value = float(bias)
    if not input_values or len(input_values) != len(weight_values):
        raise ValueError("Inputs and weights must be non-empty and equal in length.")
    if any(not math.isfinite(value) for value in input_values + weight_values + (bias_value,)):
        raise ValueError("Inputs, weights, and bias must be finite.")
    return sigmoid(sum(x * w for x, w in zip(input_values, weight_values)) + bias_value)
