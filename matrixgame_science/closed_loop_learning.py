"""A tiny deterministic closed-loop control and temporal-difference learner.

This is a dimensionless software experiment, not a biological motor-control
or neuroscience simulator.
"""
from __future__ import annotations

from dataclasses import dataclass
import math
import random

ACTIONS = (-1, 0, 1)


def _finite_unit(name: str, value: float) -> float:
    value = float(value)
    if not math.isfinite(value) or not 0.0 <= value <= 1.0:
        raise ValueError(f"{name} must be finite and within [0, 1].")
    return value


@dataclass(frozen=True)
class ControlConfig:
    """Configuration for a normalized one-dimensional target task."""

    target: float = 0.8
    step_size: float = 0.1
    learning_rate: float = 0.2
    discount: float = 0.9
    exploration: float = 0.15
    bins: int = 11

    def __post_init__(self) -> None:
        object.__setattr__(self, "target", _finite_unit("target", self.target))
        object.__setattr__(self, "step_size", _finite_unit("step_size", self.step_size))
        object.__setattr__(self, "learning_rate", _finite_unit("learning_rate", self.learning_rate))
        object.__setattr__(self, "discount", _finite_unit("discount", self.discount))
        object.__setattr__(self, "exploration", _finite_unit("exploration", self.exploration))
        if self.bins < 2:
            raise ValueError("bins must be at least 2.")


@dataclass(frozen=True)
class ControlTransition:
    """One environment transition and its observable reward."""

    state: float
    action: int
    next_state: float
    reward: float


@dataclass(frozen=True)
class LearningStep:
    """Auditable decision, outcome, and temporal-difference update."""

    transition: ControlTransition
    predicted_next_state: float
    td_error: float
    updated_value: float


@dataclass(frozen=True)
class EpisodeResult:
    """Final state and the complete immutable learning trace."""

    initial_state: float
    final_state: float
    steps: tuple[LearningStep, ...]

    @property
    def total_reward(self) -> float:
        return sum(item.transition.reward for item in self.steps)


class ScalarTargetEnvironment:
    """A bounded scalar system controlled by actions -1, 0, and +1."""

    def __init__(self, config: ControlConfig | None = None) -> None:
        self.config = config or ControlConfig()

    def step(self, state: float, action: int) -> ControlTransition:
        state = _finite_unit("state", state)
        if action not in ACTIONS:
            raise ValueError("action must be -1, 0, or 1.")
        next_state = min(1.0, max(0.0, state + action * self.config.step_size))
        reward = -abs(next_state - self.config.target)
        return ControlTransition(state, action, next_state, reward)


class TemporalDifferenceController:
    """Tabular Q-learning controller with seeded, reproducible exploration."""

    def __init__(self, config: ControlConfig | None = None, seed: int = 0) -> None:
        self.config = config or ControlConfig()
        self._rng = random.Random(seed)
        self._values: dict[tuple[int, int], float] = {}

    def _state_key(self, state: float) -> int:
        state = _finite_unit("state", state)
        return round(state * (self.config.bins - 1))

    def value(self, state: float, action: int) -> float:
        if action not in ACTIONS:
            raise ValueError("action must be -1, 0, or 1.")
        return self._values.get((self._state_key(state), action), 0.0)

    def choose_action(self, state: float) -> int:
        key = self._state_key(state)
        if self._rng.random() < self.config.exploration:
            return self._rng.choice(ACTIONS)
        values = {action: self._values.get((key, action), 0.0) for action in ACTIONS}
        best = max(values.values())
        # Stable tie-breaking makes a fresh controller predictable.
        return next(action for action in (0, 1, -1) if values[action] == best)

    def learn(self, transition: ControlTransition) -> tuple[float, float]:
        state_key = self._state_key(transition.state)
        next_key = self._state_key(transition.next_state)
        key = (state_key, transition.action)
        old_value = self._values.get(key, 0.0)
        future_value = max(
            self._values.get((next_key, action), 0.0) for action in ACTIONS
        )
        td_error = transition.reward + self.config.discount * future_value - old_value
        new_value = old_value + self.config.learning_rate * td_error
        self._values[key] = new_value
        return td_error, new_value


class ClosedLoopLearningEngine:
    """Observe -> predict -> act -> receive feedback -> update policy."""

    def __init__(
        self,
        config: ControlConfig | None = None,
        seed: int = 0,
    ) -> None:
        self.config = config or ControlConfig()
        self.environment = ScalarTargetEnvironment(self.config)
        self.controller = TemporalDifferenceController(self.config, seed)

    def run_episode(self, initial_state: float, steps: int) -> EpisodeResult:
        state = _finite_unit("initial_state", initial_state)
        if steps < 1:
            raise ValueError("steps must be at least 1.")
        initial = state
        trace: list[LearningStep] = []
        for _ in range(steps):
            action = self.controller.choose_action(state)
            predicted = min(1.0, max(0.0, state + action * self.config.step_size))
            transition = self.environment.step(state, action)
            error, updated = self.controller.learn(transition)
            trace.append(LearningStep(transition, predicted, error, updated))
            state = transition.next_state
        return EpisodeResult(initial, state, tuple(trace))
