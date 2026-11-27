import pytest

from matrixgame_science.second_earth import (
    ExternalPressures,
    RegenerativeActions,
    SecondEarthState,
    advance,
    run_scenario,
    safety_gate_open,
)


def test_zero_actions_and_pressures_preserve_state():
    state = SecondEarthState()
    assert advance(state) == state


def test_run_is_deterministic_and_includes_initial_state():
    initial = SecondEarthState()
    actions = RegenerativeActions(habitat_restoration=0.7, renewable_energy=0.8)
    first = run_scenario(initial, actions, steps=4)
    second = run_scenario(initial, actions, steps=4)
    assert first == second
    assert len(first) == 5
    assert first[0] == initial


def test_habitat_restoration_increases_biodiversity_index():
    state = SecondEarthState()
    restored = advance(state, RegenerativeActions(habitat_restoration=1.0))
    assert restored.biodiversity > state.biodiversity


def test_health_access_increases_wellbeing_index_in_toy_model():
    state = SecondEarthState()
    improved = advance(state, RegenerativeActions(health_access=1.0))
    assert improved.wellbeing > state.wellbeing


def test_safety_gate_is_independent_from_other_high_indices():
    state = SecondEarthState(
        biodiversity=1.0, clean_energy=1.0, wellbeing=1.0, trust=1.0, safety=0.2
    )
    assert not safety_gate_open(state)


def test_hazard_pressure_reduces_safety():
    state = SecondEarthState(safety=0.8)
    stressed = advance(state, pressures=ExternalPressures(hazard_pressure=1.0))
    assert stressed.safety < state.safety


def test_all_outputs_remain_bounded():
    state = SecondEarthState()
    actions = RegenerativeActions(
        habitat_restoration=1.0, renewable_energy=1.0, health_access=1.0,
        open_education=1.0, cooperation=1.0, emergency_preparedness=1.0,
    )
    pressures = ExternalPressures(
        ecosystem_stress=1.0, transition_friction=1.0, social_stress=1.0,
        information_disorder=1.0, hazard_pressure=1.0,
    )
    result = advance(state, actions, pressures, dt=100.0)
    assert all(0.0 <= value <= 1.0 for value in vars(result).values())


def test_invalid_state_and_time_step_are_rejected():
    with pytest.raises(ValueError):
        SecondEarthState(safety=1.1)
    with pytest.raises(ValueError):
        advance(SecondEarthState(), dt=0)
    with pytest.raises(ValueError):
        run_scenario(SecondEarthState(), RegenerativeActions(), steps=-1)
