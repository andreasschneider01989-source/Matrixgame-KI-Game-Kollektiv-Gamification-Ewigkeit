"""Explicit adapter connecting cellular maintenance to network learning.

The mapping is an educational software interface, not a biological mechanism.
"""
from __future__ import annotations

from dataclasses import dataclass

from .cellular_maintenance import (
    CellState,
    CellularMaintenanceEngine,
    MaintenanceConfig,
    MaintenanceResult,
)
from .engine_contracts import EnginePipeline, PipelineResult
from .network_learning import (
    NetworkEdge,
    NetworkLearningConfig,
    NetworkLearningEngine,
    NetworkLearningResult,
    NetworkNode,
)


@dataclass(frozen=True)
class MaintenanceNetworkConfig:
    """Static network topology used to observe a maintenance result."""

    edges: tuple[NetworkEdge, ...] = (
        NetworkEdge("energy_reserve", "damage_load", 0.5),
        NetworkEdge("energy_reserve", "protein_load", 0.5),
        NetworkEdge("damage_load", "protein_load", 0.5),
    )


class MaintenanceToNetworkAdapter:
    """Convert a maintenance result into a network-learning input graph."""

    def __init__(
        self,
        network_engine: NetworkLearningEngine | None = None,
        config: MaintenanceNetworkConfig | None = None,
    ) -> None:
        self.network_engine = network_engine or NetworkLearningEngine()
        self.config = config or MaintenanceNetworkConfig()

    def step(self, result: MaintenanceResult) -> NetworkLearningResult:
        if not isinstance(result, MaintenanceResult):
            raise TypeError("result must be a MaintenanceResult instance.")
        state = result.state
        nodes = (
            NetworkNode("energy_reserve", state.energy_reserve),
            NetworkNode("damage_load", state.damage_load),
            NetworkNode("protein_load", state.protein_load),
            NetworkNode("repair_capacity", state.repair_capacity),
        )
        return self.network_engine.step(nodes, self.config.edges)


class RegenerationNetworkPipeline:
    """Run cellular maintenance, then expose its result to a learning network."""

    def __init__(
        self,
        maintenance_config: MaintenanceConfig | None = None,
        network_config: NetworkLearningConfig | None = None,
        bridge_config: MaintenanceNetworkConfig | None = None,
    ) -> None:
        maintenance = CellularMaintenanceEngine(maintenance_config)
        network = NetworkLearningEngine(network_config)
        adapter = MaintenanceToNetworkAdapter(network, bridge_config)
        self.pipeline = EnginePipeline(maintenance, adapter)

    def step(
        self, state: CellState
    ) -> PipelineResult[MaintenanceResult, NetworkLearningResult]:
        return self.pipeline.step(state)
