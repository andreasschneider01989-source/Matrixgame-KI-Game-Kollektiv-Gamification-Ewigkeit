import pytest

from matrixgame_science.agent_decision import (
    ActionOption,
    Observation,
    choose_action,
)


FORAGE = ActionOption(
    name="FORAGE",
    action_cost=12.0,
    gain_at_full_yield=20.0,
)


def test_missing_observation_selects_wait_without_guessing():
    result = choose_action(
        energy=30.0, basal_cost=10.0, observation=None, actions=(FORAGE,)
    )
    assert result.action == "WAIT"
    assert result.reason_code == "NO_OBSERVATION"
    assert result.expected_energy_delta == -10.0
    assert result.projected_energy == 20.0


def test_low_confidence_observation_selects_wait():
    result = choose_action(
        energy=30.0,
        basal_cost=10.0,
        observation=Observation(resource_yield=1.0, confidence=0.2),
        actions=(FORAGE,),
    )
    assert result.action == "WAIT"
    assert result.reason_code == "LOW_CONFIDENCE"


def test_perception_prevents_wasteful_foraging_in_barren_environment():
    result = choose_action(
        energy=30.0,
        basal_cost=10.0,
        observation=Observation(resource_yield=0.0, confidence=1.0),
        actions=(FORAGE,),
    )
    assert result.action == "WAIT"
    assert result.reason_code == "WAIT_BETTER_THAN_ACTIONS"
    assert result.projected_energy == 20.0


def test_perception_selects_foraging_when_expected_gain_exceeds_cost():
    result = choose_action(
        energy=30.0,
        basal_cost=10.0,
        observation=Observation(resource_yield=1.0, confidence=1.0),
        actions=(FORAGE,),
    )
    assert result.action == "FORAGE"
    assert result.reason_code == "BEST_EXPECTED_ENERGY_BALANCE"
    assert result.expected_energy_delta == -2.0
    assert result.projected_energy == 28.0


def test_action_is_rejected_when_energy_cannot_cover_its_costs():
    result = choose_action(
        energy=15.0,
        basal_cost=10.0,
        observation=Observation(resource_yield=1.0, confidence=1.0),
        actions=(FORAGE,),
    )
    assert result.action == "WAIT"
    assert result.reason_code == "NO_AFFORDABLE_ACTION"


def test_decision_is_deterministic_and_ties_use_action_name():
    options = (
        ActionOption("ZETA", action_cost=1.0, gain_at_full_yield=2.0),
        ActionOption("ALPHA", action_cost=1.0, gain_at_full_yield=2.0),
    )
    kwargs = dict(
        energy=20.0,
        basal_cost=2.0,
        observation=Observation(resource_yield=1.0, confidence=1.0),
        actions=options,
    )
    assert choose_action(**kwargs) == choose_action(**kwargs)
    assert choose_action(**kwargs).action == "ALPHA"


def test_invalid_observation_and_duplicate_actions_are_rejected():
    with pytest.raises(ValueError):
        Observation(resource_yield=1.2, confidence=1.0)
    with pytest.raises(ValueError):
        choose_action(
            energy=20.0,
            basal_cost=1.0,
            observation=Observation(resource_yield=1.0, confidence=1.0),
            actions=(FORAGE, FORAGE),
        )
