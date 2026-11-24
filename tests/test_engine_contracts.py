import pytest

from matrixgame_science.cellular_maintenance import CellState
from matrixgame_science.engine_contracts import EnginePipeline
from matrixgame_science.health_network import (
    MaintenanceToNetworkAdapter,
    RegenerationNetworkPipeline,
)
from matrixgame_science.network_learning import (
    NetworkEdge,
    NetworkLearningConfig,
    NetworkLearningEngine,
    NetworkNode,
)


class AddOne:
    def step(self, value: int) -> int:
        return value + 1


class Double:
    def step(self, value: int) -> int:
        return value * 2


def test_generic_pipeline_composes_two_independent_engines():
    pipeline = EnginePipeline(AddOne(), Double())

    result = pipeline.step(4)

    assert result.intermediate == 5
    assert result.final == 10


def test_regeneration_pipeline_connects_maintenance_and_network():
    pipeline = RegenerationNetworkPipeline()
    result = pipeline.step(
        CellState(
            damage_load=0.4,
            protein_load=0.3,
            energy_reserve=0.5,
            repair_capacity=0.8,
        )
    )

    assert result.intermediate.state.energy_reserve < 0.5
    assert {node.node_id for node in result.final.nodes} == {
        "energy_reserve", "damage_load", "protein_load", "repair_capacity"
    }
    assert len(result.final.edges) == 3


def test_pipeline_is_deterministic_and_keeps_both_stage_results():
    pipeline = RegenerationNetworkPipeline()
    state = CellState()

    assert pipeline.step(state) == pipeline.step(state)


def test_adapter_rejects_wrong_input_type():
    adapter = MaintenanceToNetworkAdapter()
    with pytest.raises(TypeError, match="MaintenanceResult"):
        adapter.step("not a maintenance result")


def test_custom_network_learning_rate_reaches_integrated_pipeline():
    pipeline = RegenerationNetworkPipeline(
        network_config=NetworkLearningConfig(learning_rate=0.5)
    )
    result = pipeline.step(CellState())

    assert all(0.0 <= edge.weight <= 1.0 for edge in result.final.edges)


def test_adapter_rejects_unknown_topology_endpoint():
    adapter = MaintenanceToNetworkAdapter(
        network_engine=NetworkLearningEngine(),
        config=type("Config", (), {"edges": (NetworkEdge("missing", "damage_load", 0.5),)})(),
    )
    from matrixgame_science.cellular_maintenance import MaintenanceResult
    with pytest.raises(ValueError, match="existing node"):
        adapter.step(MaintenanceResult(CellState(), 0.0, 0.0, 0.0))
