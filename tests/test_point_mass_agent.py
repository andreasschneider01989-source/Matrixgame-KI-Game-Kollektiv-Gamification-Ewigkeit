import math
import pytest

from matrixgame_science.point_mass_agent import (
    PointMassConfig, PointMassEnvironment, PointMassLearningEngine, PointMassState,
)


def test_newtonian_step_changes_velocity_and_position():
    env = PointMassEnvironment(PointMassConfig(dt=0.2, damping=1.0))
    start = PointMassState(1.0, 1.0)
    result = env.step(start, 1)
    assert result.next_state.vx > 0
    assert result.next_state.x > start.x
    assert result.energy_spent > 0


def test_diagonal_force_is_normalized_and_speed_is_bounded():
    env = PointMassEnvironment(PointMassConfig(dt=1.0, damping=1.0, max_speed=0.2))
    result = env.step(PointMassState(5.0, 5.0), 5)
    assert math.hypot(result.next_state.vx, result.next_state.vy) == pytest.approx(0.2)


def test_world_boundary_clamps_and_reverses_velocity():
    env = PointMassEnvironment(PointMassConfig(dt=1.0, damping=1.0, max_speed=2.0))
    result = env.step(PointMassState(9.9, 5.0, 1.0, 0.0), 0)
    assert result.next_state.x == 10.0
    assert result.next_state.vx < 0


def test_learning_episode_is_reproducible_for_same_seed():
    config = PointMassConfig(exploration=0.25)
    first = PointMassLearningEngine(config, seed=42).run_episode(max_steps=30)
    second = PointMassLearningEngine(config, seed=42).run_episode(max_steps=30)
    assert first == second


def test_invalid_parameters_and_actions_are_rejected():
    with pytest.raises(ValueError):
        PointMassConfig(mass=0.0)
    with pytest.raises(ValueError):
        PointMassEnvironment().step(PointMassState(1.0, 1.0), 99)
