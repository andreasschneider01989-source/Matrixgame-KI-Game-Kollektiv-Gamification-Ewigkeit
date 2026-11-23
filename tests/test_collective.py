import pytest
from matrixgame_science.collective import pareto_frontier


def test_pareto_frontier_preserves_non_dominated_options():
    assert pareto_frontier(((0.8, 0.6), (0.7, 0.9), (0.5, 0.5))) == (0, 1)


def test_pareto_frontier_rejects_ragged_input():
    with pytest.raises(ValueError):
        pareto_frontier(((1.0, 2.0), (1.0,)))
