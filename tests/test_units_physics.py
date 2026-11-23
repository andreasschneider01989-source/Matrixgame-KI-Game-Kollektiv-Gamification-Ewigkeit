import pytest
from matrixgame_science.physics import constant_acceleration_step, gravitational_acceleration, kinetic_energy, relative_position
from matrixgame_science.units import ACCELERATION, ENERGY, FORCE, LENGTH, MASS, TIME, VELOCITY, Quantity


def test_si_dimensions_derive_velocity_force_and_energy():
    assert VELOCITY == LENGTH / TIME
    assert ACCELERATION == LENGTH / (TIME ** 2)
    assert FORCE == MASS * ACCELERATION
    assert ENERGY == FORCE * LENGTH
    assert Quantity(3.0, MASS) * Quantity(2.0, ACCELERATION) == Quantity(6.0, FORCE)


def test_quantity_addition_rejects_mismatched_dimensions():
    with pytest.raises(ValueError):
        _ = Quantity(1, LENGTH) + Quantity(1, TIME)


def test_relative_position_is_target_minus_origin():
    assert relative_position((1, 2, 3), (4, 6, 3)) == (3.0, 4.0, 0.0)


def test_kinetic_energy():
    assert kinetic_energy(2.0, 3.0) == pytest.approx(9.0)


def test_gravity_points_toward_source():
    result = gravitational_acceleration((0, 0, 0), (0, 3, 4), 5.0e10)
    assert result[0] == pytest.approx(0.0)
    assert result[1] > 0 and result[2] > 0


def test_gravity_rejects_coincident_points():
    with pytest.raises(ValueError):
        gravitational_acceleration((1, 1, 1), (1, 1, 1), 1.0)


def test_constant_acceleration_kinematics():
    pos, vel = constant_acceleration_step((0,), (2,), (3,), 2)
    assert pos == pytest.approx((10.0,))
    assert vel == pytest.approx((8.0,))
