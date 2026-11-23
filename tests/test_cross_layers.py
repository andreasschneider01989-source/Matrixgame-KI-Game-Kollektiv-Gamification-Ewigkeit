import pytest
from matrixgame_science.biology import replicator_step
from matrixgame_science.chemistry import michaelis_menten_rate
from matrixgame_science.language import EvidenceStatus, ModelClaim, ParameterBinding
from matrixgame_science.neuroscience import artificial_neuron_activation, sigmoid
from matrixgame_science.psychology import choose_highest_probability, decision_probabilities


def test_michaelis_menten():
    assert michaelis_menten_rate(0.5, 10.0, 0.5) == pytest.approx(5.0)


def test_michaelis_menten_rejects_negative_substrate():
    with pytest.raises(ValueError):
        michaelis_menten_rate(-1.0, 10.0, 0.5)


def test_replicator_preserves_population_share_total():
    updated = replicator_step((0.5, 0.5), (2.0, 1.0))
    assert sum(updated) == pytest.approx(1.0)
    assert updated[0] > updated[1]


def test_replicator_rejects_non_normalized_shares():
    with pytest.raises(ValueError):
        replicator_step((0.4, 0.4), (1.0, 2.0))


def test_artificial_neuron_primitives_are_bounded():
    assert sigmoid(0.0) == pytest.approx(0.5)
    assert 0 < artificial_neuron_activation((1.0,), (2.0,)) < 1


def test_decision_model_uses_explicit_context_shifts():
    p = decision_probabilities((1.0, 0.5), context_shifts=(0.0, 1.0))
    assert sum(p) == pytest.approx(1.0)
    assert choose_highest_probability(p) == 1


def test_claim_and_parameter_retain_provenance():
    claim = ModelClaim("Q-1", "Model statement", "x=y", EvidenceStatus.MODEL, "test source")
    binding = ParameterBinding("rate", 0.5, "s^-1", "explicit test input", EvidenceStatus.HYPOTHESIS)
    assert claim.evidence_status is EvidenceStatus.MODEL
    assert binding.value == 0.5
