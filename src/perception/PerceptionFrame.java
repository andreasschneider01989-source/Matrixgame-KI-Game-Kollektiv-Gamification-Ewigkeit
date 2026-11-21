package perception;

import java.util.Set;

/**
 * MATRIXGAME – Perception core v0.2.
 *
 * A PerceptionFrame stores a measurement, not an interpretation.
 * The record is structurally immutable; observation must also be immutable
 * (or defensively copied by the producer) for deep immutability.
 */
public record PerceptionFrame(
        String agentId,
        String modality,
        long timestamp,
        Object observation,
        double confidence,
        String sourceId) {

    private static final Set<String> SUPPORTED_MODALITIES = Set.of(
            "VISUAL", "AUDITORY", "SPATIAL", "THERMAL",
            "CHEMICAL", "INTERNAL", "SOCIAL");

    public PerceptionFrame {
        requireText(agentId, "agentId");
        requireText(modality, "modality");
        if (!SUPPORTED_MODALITIES.contains(modality)) {
            throw new IllegalArgumentException("Unsupported modality: " + modality);
        }
        if (timestamp < 0) throw new IllegalArgumentException("timestamp must be >= 0");
        if (observation == null) throw new IllegalArgumentException("observation must not be null");
        requireText(sourceId, "sourceId");
        requireConfidence(confidence, "measurement confidence");
    }

    /** Interpretation is deliberately separate from the measurement record. */
    public record Interpretation(
            String interpretiertVon, String claim, double confidence, String evidenceClass) {
        public Interpretation {
            requireText(interpretiertVon, "interpretiertVon");
            requireText(claim, "claim");
            requireConfidence(confidence, "interpretation confidence");
            if (evidenceClass == null || !evidenceClass.matches("[ABCDE]")) {
                throw new IllegalArgumentException("evidenceClass must be one of A, B, C, D, E");
            }
        }
    }

    /** Accept an interpretation only when an underlying measurement exists. */
    public static Interpretation interpret(Interpretation interpretation, boolean hasUnderlyingMeasurement) {
        if (interpretation == null) throw new IllegalArgumentException("interpretation must not be null");
        if (!hasUnderlyingMeasurement) {
            throw new IllegalStateException("KONFABULATIONS-WAECHTER: interpretation without measurement");
        }
        return interpretation;
    }

    public static Set<String> supportedModalities() {
        return SUPPORTED_MODALITIES;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be null or blank");
        }
    }

    private static void requireConfidence(double value, String field) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(field + " must be finite and within [0,1]");
        }
    }
}
