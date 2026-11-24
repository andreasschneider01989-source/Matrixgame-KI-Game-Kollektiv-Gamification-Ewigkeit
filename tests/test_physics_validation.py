import pytest

from matrixgame_science.physics_validation import (
    acceleration_from_force, compare_trajectory, integrate_constant_acceleration,
    momentum, work,
)
from matrixgame_science.units import FORCE, LENGTH, MASS, TIME, VELOCITY, Quantity


def test_velocity_verlet_matches_analytic_constant_acceleration():
    args = ((1.0, -2.0), (3.0, 0.5), (0.0, -9.81), 0.01, 100)
    exact = integrate_constant_acceleration(*args, method="exact")
    verlet = integrate_constant_acceleration(*args, method="velocity_verlet")
    error = compare_trajectory(verlet, exact)
    assert error.position_rmse == pytest.approx(0.0, abs=1e-12)
    assert error.velocity_rmse == pytest.approx(0.0, abs=1e-12)


def test_euler_error_decreases_when_time_step_is_halved():
    coarse = integrate_constant_acceleration((0.0,), (0.0,), (2.0,), 0.1, 10, "explicit_euler")
    fine = integrate_constant_acceleration((0.0,), (0.0,), (2.0,), 0.05, 20, "explicit_euler")
    exact = integrate_constant_acceleration((0.0,), (0.0,), (2.0,), 0.1, 10, "exact")
    coarse_error = compare_trajectory(coarse, exact).position_rmse
    fine_reference = integrate_constant_acceleration((0.0,), (0.0,), (2.0,), 0.05, 20, "exact")
    fine_error = compare_trajectory(fine, fine_reference).position_rmse
    assert fine_error < coarse_error


def test_newton_second_law_checks_dimensions():
    acceleration = acceleration_from_force(Quantity(10.0, FORCE), Quantity(2.0, MASS))
    assert acceleration.value == pytest.approx(5.0)
    assert acceleration.dimension == LENGTH / (TIME ** 2)


def test_momentum_and_work_have_expected_dimensions():
    p = momentum(Quantity(2.0, MASS), Quantity(3.0, VELOCITY))
    w = work(Quantity(4.0, FORCE), Quantity(2.0, LENGTH))
    assert p.value == pytest.approx(6.0)
    assert p.dimension == MASS * VELOCITY
    assert w.value == pytest.approx(8.0)
    assert w.dimension == FORCE * LENGTH


def test_dimension_mismatch_and_invalid_inputs_are_rejected():
    with pytest.raises(ValueError):
        acceleration_from_force(Quantity(1.0, LENGTH), Quantity(1.0, MASS))
    with pytest.raises(ValueError):
        acceleration_from_force(Quantity(1.0, FORCE), Quantity(0.0, MASS))
    with pytest.raises(ValueError):
        integrate_constant_acceleration((0.0,), (0.0,), (1.0,), 0.0, 10)
