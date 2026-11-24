import pytest

from matrixgame_science.network_learning import (
    NetworkEdge,
    NetworkLearningConfig,
    NetworkLearningEngine,
    NetworkNode,
)


def test_signal_propagation_is_weighted_and_synchronous():
    nodes = (
        NetworkNode("source-a", 1.0),
        NetworkNode("source-b", 0.0),
        NetworkNode("target", 0.2),
    )
    edges = (
        NetworkEdge("source-a", "target", 3.0 / 4.0),
        NetworkEdge("source-b", "target", 1.0 / 4.0),
    )

    result = NetworkLearningEngine().step(nodes, edges)

    assert result.nodes[2].activation == pytest.approx(0.75)


def test_coactivation_increases_connection_weight():
    nodes = (NetworkNode("a", 1.0), NetworkNode("b", 1.0))
    edges = (NetworkEdge("a", "b", 0.2),)

    result = NetworkLearningEngine(NetworkLearningConfig(0.5)).step(nodes, edges)

    assert result.edges[0].weight == pytest.approx(0.6)


def test_no_coactivation_reduces_connection_weight():
    nodes = (NetworkNode("a", 0.0), NetworkNode("b", 1.0))
    edges = (NetworkEdge("a", "b", 0.8),)

    result = NetworkLearningEngine(NetworkLearningConfig(0.5)).step(nodes, edges)

    assert result.edges[0].weight == pytest.approx(0.4)


def test_step_is_deterministic_and_keeps_values_bounded():
    nodes = (NetworkNode("a", 0.7), NetworkNode("b", 0.3))
    edges = (NetworkEdge("a", "b", 0.9),)

    engine = NetworkLearningEngine()
    first = engine.step(nodes, edges)
    second = engine.step(nodes, edges)

    assert first == second
    assert all(0.0 <= node.activation <= 1.0 for node in first.nodes)
    assert all(0.0 <= edge.weight <= 1.0 for edge in first.edges)


def test_unknown_edge_endpoint_is_rejected():
    with pytest.raises(ValueError, match="existing node"):
        NetworkLearningEngine().step(
            (NetworkNode("a", 0.5),),
            (NetworkEdge("a", "missing", 0.5),),
        )


def test_duplicate_node_ids_are_rejected():
    with pytest.raises(ValueError, match="unique"):
        NetworkLearningEngine().step(
            (NetworkNode("a", 0.2), NetworkNode("a", 0.8)),
            (),
        )


def test_isolated_node_keeps_its_activation():
    result = NetworkLearningEngine().step((NetworkNode("alone", 0.6),), ())

    assert result.nodes[0].activation == pytest.approx(0.6)
