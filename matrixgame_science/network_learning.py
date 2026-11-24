"""A deterministic, dimensionless network-learning primitive.

This is an abstract educational model. It is not a biological neural,
immune, or stem-cell simulator.
"""
from __future__ import annotations

from dataclasses import dataclass
import math


def _unit_interval(name: str, value: float) -> float:
    value = float(value)
    if not math.isfinite(value) or not 0.0 <= value <= 1.0:
        raise ValueError(f"{name} must be finite and within [0, 1].")
    return value


@dataclass(frozen=True)
class NetworkNode:
    """A named network unit with a normalized activation."""

    node_id: str
    activation: float

    def __post_init__(self) -> None:
        if not self.node_id or not self.node_id.strip():
            raise ValueError("node_id must not be empty.")
        object.__setattr__(self, "activation", _unit_interval("activation", self.activation))


@dataclass(frozen=True)
class NetworkEdge:
    """A directed connection from source to target."""

    source: str
    target: str
    weight: float

    def __post_init__(self) -> None:
        if not self.source or not self.target:
            raise ValueError("edge endpoints must not be empty.")
        object.__setattr__(self, "weight", _unit_interval("weight", self.weight))


@dataclass(frozen=True)
class NetworkLearningConfig:
    """Dimensionless learning rate for the correlation update."""

    learning_rate: float = 0.10

    def __post_init__(self) -> None:
        object.__setattr__(
            self, "learning_rate", _unit_interval("learning_rate", self.learning_rate)
        )


@dataclass(frozen=True)
class NetworkLearningResult:
    """Synchronous node update and bounded edge-weight update."""

    nodes: tuple[NetworkNode, ...]
    edges: tuple[NetworkEdge, ...]


class NetworkLearningEngine:
    """Propagate weighted signals, then adapt weights from co-activation.

    All node activations and edge weights remain in [0, 1]. Node updates
    use the previous step's activations, so results do not depend on
    iteration order.
    """

    def __init__(self, config: NetworkLearningConfig | None = None) -> None:
        self.config = config or NetworkLearningConfig()

    def step(
        self,
        nodes: tuple[NetworkNode, ...] | list[NetworkNode],
        edges: tuple[NetworkEdge, ...] | list[NetworkEdge],
    ) -> NetworkLearningResult:
        node_list = tuple(nodes)
        edge_list = tuple(edges)
        by_id = {node.node_id: node for node in node_list}
        if len(by_id) != len(node_list):
            raise ValueError("node_id values must be unique.")
        for edge in edge_list:
            if edge.source not in by_id or edge.target not in by_id:
                raise ValueError("every edge endpoint must reference an existing node.")

        incoming: dict[str, list[NetworkEdge]] = {node_id: [] for node_id in by_id}
        for edge in edge_list:
            incoming[edge.target].append(edge)

        next_nodes: list[NetworkNode] = []
        for node in node_list:
            links = incoming[node.node_id]
            total_weight = sum(edge.weight for edge in links)
            if total_weight == 0.0:
                next_activation = node.activation
            else:
                next_activation = sum(
                    edge.weight * by_id[edge.source].activation for edge in links
                ) / total_weight
            next_nodes.append(NetworkNode(node.node_id, next_activation))

        rate = self.config.learning_rate
        next_edges = tuple(
            NetworkEdge(
                edge.source,
                edge.target,
                edge.weight
                + rate
                * (by_id[edge.source].activation * by_id[edge.target].activation - edge.weight),
            )
            for edge in edge_list
        )
        return NetworkLearningResult(tuple(next_nodes), next_edges)
