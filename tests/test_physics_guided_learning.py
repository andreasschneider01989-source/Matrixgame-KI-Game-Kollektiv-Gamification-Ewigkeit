import math

import pytest

from matrixgame_science.physics_guided_learning import (
    LinearAngleNeuron,
    reflect_angle_deg,
    reflect_vector,
)


def test_reflection_law_returns_equal_angles_from_normal():
    assert reflect_angle_deg(0.0) == 0.0
    assert reflect_angle_deg(42.5) == 42.5
    assert reflect_angle_deg(90.0) == 90.0


def test_reflection_angle_rejects_invalid_values():
    for value in (-1.0, 90.1, math.inf, math.nan):
        with pytest.raises(ValueError):
            reflect_angle_deg(value)


def test_vector_reflection_flips_normal_component_and_preserves_length():
    reflected = reflect_vector((1.0, -1.0), (0.0, 1.0))
    assert reflected == pytest.approx((1.0, 1.0))
    assert math.hypot(*reflected) == pytest.approx(math.sqrt(2.0))


def test_vector_reflection_rejects_zero_normal():
    with pytest.raises(ValueError):
        reflect_vector((1.0, 0.0), (0.0, 0.0))


def test_one_neuron_learns_the_reflection_mapping():
    samples = tuple((float(angle), float(angle)) for angle in range(0, 91, 10))
    neuron = LinearAngleNeuron()
    result = neuron.fit(samples, epochs=100)
    assert result.final_mae_deg < result.initial_mae_deg * 0.01
    assert result.final_mae_deg < 0.02
    assert neuron.predict(35.0) == pytest.approx(35.0, abs=0.02)


def test_neuron_is_deterministic_and_validates_training_data():
    samples = ((0.0, 0.0), (45.0, 45.0), (90.0, 90.0))
    first, second = LinearAngleNeuron(), LinearAngleNeuron()
    assert first.fit(samples) == second.fit(samples)
    with pytest.raises(ValueError):
        first.fit(())
    with pytest.raises(ValueError):
        first.fit(samples, epochs=0)
