import pytest
from matrixgame_science.mathematics import normalize_probabilities, shannon_entropy, softmax


def test_shannon_entropy_fair_binary_distribution_is_one_bit():
    assert shannon_entropy((0.5, 0.5)) == pytest.approx(1.0)


def test_entropy_normalizes_weights():
    assert shannon_entropy((7, 3)) == pytest.approx(shannon_entropy((0.7, 0.3)))


def test_probability_input_validation():
    with pytest.raises(ValueError):
        normalize_probabilities((1.0, -1.0))
    with pytest.raises(ValueError):
        normalize_probabilities((0.0, 0.0))


def test_softmax_is_stable_and_sums_to_one():
    result = softmax((10000.0, 10001.0, 9999.0))
    assert sum(result) == pytest.approx(1.0)
    assert result[1] > result[0] > result[2]


def test_softmax_rejects_non_positive_temperature():
    with pytest.raises(ValueError):
        softmax((1.0, 2.0), temperature=0.0)
