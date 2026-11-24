"""Deterministic, perception-gated action selection for a toy life agent.

This module chooses an action from an explicit observation. It does not sense
the environment, learn a policy, or simulate a complete organism.
All energy values are abstract, internally consistent model units.
"""
from __future__ import annotations

from dataclasses import dataclass
import math
from typing import Sequence


def _finite_nonnegative(name: str, value: float) -> float:
    result = float(value)
    if not math.isfinite(result) or result < 0.0:
        raise ValueError(f"{name} must be finite and non-negative.")
    return result


def _unit_interval(name: str, value: float) -> float:
    result = float(value)
    if not math.isfinite(result) or not 0.0 <= result <= 1.0:
        raise ValueError(f"{name} must be finite and within [0, 1].")
    return result


@dataclass(frozen=True)
class Observation:
    """A supplied sensor estimate; this class does not implement sensing."""

    resource_yield: float
    confidence: float

    def __post_init__(self) -> None:
        object.__setattr__(self, "resource_yield", _unit_interval("resource_yield", self.resource_yield))
        object.__setattr__(self, "confidence", _unit_interval("confidence", self.confidence))


@dataclass(frozen=True)
class ActionOption:
    """One action's cost and its gain at full resource availability."""

    name: str
    action_cost: float
    gain_at_full_yield: float

    def __post_init__(self) -> None:
        if not self.name or not self.name.strip() or self.name.upper() == "WAIT":
            raise ValueError("Action name must be non-empty and must not be WAIT.")
        object.__setattr__(self, "action_cost", _finite_nonnegative("action_cost", self.action_cost))
        object.__setattr__(
            self, "gain_at_full_yield",
            _finite_nonnegative("gain_at_full_yield", self.gain_at_full_yield),
        )


@dataclass(frozen=True)
class DecisionResult:
    """Auditable one-step recommendation; no state is mutated."""

    action: str
    reason_code: str
    expected_energy_delta: float
    projected_energy: float
    observation_confidence: float


def choose_action(
    *,
    energy: float,
    basal_cost: float,
    observation: Observation | None,
    actions: Sequence[ActionOption],
    minimum_confidence: float = 0.60,
) -> DecisionResult:
    """Choose the best affordable action, but only with adequate perception.

    WAIT is the baseline and still pays basal_cost. An action is selected only
    if its expected energy delta is strictly better than WAIT. Ties resolve
    by action name, making the result deterministic.
    """
    energy = _finite_nonnegative("energy", energy)
    basal_cost = _finite_nonnegative("basal_cost", basal_cost)
    minimum_confidence = _unit_interval("minimum_confidence", minimum_confidence)

    names = [action.name for action in actions]
    if len(names) != len(set(names)):
        raise ValueError("Action names must be unique.")

    wait_delta = -basal_cost
    confidence = 0.0 if observation is None else observation.confidence

    def result(action: str, reason: str, delta: float) -> DecisionResult:
        return DecisionResult(
            action=action,
            reason_code=reason,
            expected_energy_delta=delta,
            projected_energy=max(0.0, energy + delta),
            observation_confidence=confidence,
        )

    if observation is None:
        return result("WAIT", "NO_OBSERVATION", wait_delta)
    if confidence < minimum_confidence:
        return result("WAIT", "LOW_CONFIDENCE", wait_delta)

    candidates: list[tuple[float, str]] = []
    for option in actions:
        total_required = basal_cost + option.action_cost
        if energy < total_required:
            continue
        expected_delta = (
            observation.resource_yield * option.gain_at_full_yield
            - option.action_cost
            - basal_cost
        )
        if expected_delta > wait_delta:
            candidates.append((expected_delta, option.name))

    if not candidates:
        reason = "NO_AFFORDABLE_ACTION" if actions and all(
            energy < basal_cost + option.action_cost for option in actions
        ) else "WAIT_BETTER_THAN_ACTIONS"
        return result("WAIT", reason, wait_delta)

    best_delta, best_name = sorted(candidates, key=lambda item: (-item[0], item[1]))[0]
    return result(best_name, "BEST_EXPECTED_ENERGY_BALANCE", best_delta)
