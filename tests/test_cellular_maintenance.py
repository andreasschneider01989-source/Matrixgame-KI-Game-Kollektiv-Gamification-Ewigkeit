import pytest

from matrixgame_science.cellular_maintenance import (
    CellState,
    CellularMaintenanceEngine,
    MaintenanceConfig,
)


def test_step_is_deterministic_and_resource_limited():
    engine = CellularMaintenanceEngine()
    state = CellState(damage_load=0.4, protein_load=0.3, energy_reserve=0.5)

    first = engine.step(state)
    second = engine.step(state)

    assert first == second
    assert 0 <= first.state.energy_reserve <= state.energy_reserve
    assert first.state.damage_load == pytest.approx(0.4 + 0.005 - first.repaired_damage)
    assert first.state.protein_load == pytest.approx(0.3 + 0.005 - first.cleared_protein_load)
    assert first.energy_spent <= state.energy_reserve


def test_no_energy_means_no_maintenance_work():
    result = CellularMaintenanceEngine().step(
        CellState(damage_load=0.4, protein_load=0.3, energy_reserve=0.0)
    )
    assert result.repaired_damage == 0.0
    assert result.cleared_protein_load == 0.0
    assert result.energy_spent == 0.0


def test_state_values_must_be_normalized():
    with pytest.raises(ValueError):
        CellState(damage_load=1.1)


def test_config_rejects_invalid_repair_share():
    with pytest.raises(ValueError):
        MaintenanceConfig(repair_share=-0.1)


def test_all_state_values_remain_in_unit_interval():
    engine = CellularMaintenanceEngine(
        MaintenanceConfig(background_damage=0.8, protein_load_increment=0.8)
    )
    result = engine.step(CellState())
    for value in (
        result.state.damage_load,
        result.state.protein_load,
        result.state.energy_reserve,
        result.state.repair_capacity,
    ):
        assert 0.0 <= value <= 1.0
