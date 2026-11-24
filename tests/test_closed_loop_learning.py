import pytest

from matrixgame_science.closed_loop_learning import (
    ClosedLoopLearningEngine,
    ControlConfig,
    ScalarTargetEnvironment,
)


def test_environment_clamps_state_and_returns_reward():
    env = ScalarTargetEnvironment(ControlConfig(step_size=0.2, target=0.5))
    transition = env.step(0.95, 1)
    assert transition.next_state == 1.0
    assert transition.reward == pytest.approx(-0.5)


def test_closed_loop_records_prediction_feedback_and_update():
    engine = ClosedLoopLearningEngine(seed=7)
    result = engine.run_episode(0.2, 12)
    assert len(result.steps) == 12
    for item in result.steps:
        assert item.predicted_next_state == pytest.approx(item.transition.next_state)
        assert -1.0 <= item.transition.reward <= 0.0
        assert math_is_finite(item.prediction_error)
        assert math_is_finite(item.updated_value)


def test_same_seed_and_inputs_are_reproducible():
    first = ClosedLoopLearningEngine(seed=19).run_episode(0.1, 30)
    second = ClosedLoopLearningEngine(seed=19).run_episode(0.1, 30)
    assert first == second


def test_learning_updates_action_value():
    engine = ClosedLoopLearningEngine(seed=2)
    before = engine.controller.value(0.4, 1)
    engine.run_episode(0.4, 1)
    # The chosen action is recorded; its Q value must have been updated.
    transition = engine.run_episode(0.4, 1).steps[0].transition
    assert engine.controller.value(transition.state, transition.action) != before


def test_invalid_inputs_are_rejected():
    engine = ClosedLoopLearningEngine()
    with pytest.raises(ValueError):
        engine.run_episode(1.2, 5)
    with pytest.raises(ValueError):
        engine.run_episode(0.5, 0)
    with pytest.raises(ValueError):
        engine.environment.step(0.5, 9)


def math_is_finite(value):
    return value == value and abs(value) != float("inf")
