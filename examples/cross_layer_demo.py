"""Illustrative Matrixgame science-layer demo; inputs are example parameters."""
from matrixgame_science.biology import replicator_step
from matrixgame_science.chemistry import michaelis_menten_rate
from matrixgame_science.collective import pareto_frontier
from matrixgame_science.language import EvidenceStatus, ModelClaim
from matrixgame_science.mathematics import shannon_entropy
from matrixgame_science.neuroscience import artificial_neuron_activation
from matrixgame_science.physics import gravitational_acceleration, kinetic_energy
from matrixgame_science.psychology import decision_probabilities


def main() -> None:
    claim = ModelClaim(
        "MG-PHYS-001",
        "A point source produces Newtonian gravitational acceleration.",
        "a = G*M*(r_source-r_target)/|r_source-r_target|^3",
        EvidenceStatus.MODEL,
        "Newtonian point-mass model; see package documentation.",
        ("classical mechanics", "point source", "SI units"),
    )
    acceleration = gravitational_acceleration((0, 0, 0), (0, 3, 4), 5.0e10)
    print("Claim status:", claim.evidence_status.value)
    print("Gravity acceleration (m/s^2):", acceleration)
    print("Kinetic energy for 2 kg at 3 m/s (J):", kinetic_energy(2, 3))
    print("Shannon entropy (bits):", round(shannon_entropy((0.7, 0.3)), 4))
    print("Illustrative enzyme rate:", round(michaelis_menten_rate(0.7, 2.0, 0.5), 4))
    print("Replicator shares:", replicator_step((0.33, 0.33, 0.34), (1.2, 0.9, 0.6)))
    print("Artificial-neuron activation:", round(artificial_neuron_activation((0.2, 0.8), (0.5, -0.1)), 4))
    print("Decision probabilities:", decision_probabilities((1.0, 0.7, 0.4), temperature=0.8))
    print("Pareto-efficient candidates:", pareto_frontier(((0.8, 0.6), (0.7, 0.9), (0.5, 0.5))))


if __name__ == "__main__":
    main()
