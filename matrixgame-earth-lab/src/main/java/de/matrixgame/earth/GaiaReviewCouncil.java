package de.matrixgame.earth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A deterministic multi-perspective review council for the Gaia water-bucket
 * prototype. These are rule-based reviewer agents, not language models.
 */
public final class GaiaReviewCouncil {
    private static final double BALANCE_TOLERANCE_MM = 1.0e-6;

    private GaiaReviewCouncil() {
    }

    public enum Status {
        PASS,
        WARNING
    }

    /**
     * Describes the declared input class. This declaration is not an automatic
     * verification of a source, licence, or measurement.
     */
    public enum InputClass {
        SYNTHETIC_EXAMPLE,
        OBSERVED_WITH_PROVENANCE_DECLARED,
        OBSERVED_WITH_INCOMPLETE_PROVENANCE
    }

    public record Finding(String agent, Status status, String summary) {
        public Finding {
            Objects.requireNonNull(agent, "agent");
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(summary, "summary");
        }
    }

    /**
     * Produces separate findings so one successful check cannot hide a warning
     * from another discipline.
     */
    public static List<Finding> review(
            GaiaWaterBucket.StepResult result,
            GaiaWaterBucket.Parameters parameters,
            InputClass inputClass) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(parameters, "parameters");
        Objects.requireNonNull(inputClass, "inputClass");

        List<Finding> findings = new ArrayList<>();

        double residual = Math.abs(result.waterBalanceResidualMm());
        if (Double.isFinite(residual) && residual <= BALANCE_TOLERANCE_MM) {
            findings.add(new Finding(
                    "HYDROLOGY",
                    Status.PASS,
                    "Water accounting closes within the configured numerical tolerance."));
        } else {
            findings.add(new Finding(
                    "HYDROLOGY",
                    Status.WARNING,
                    "Water-balance residual exceeds tolerance; inspect the step calculation."));
        }

        double storage = result.nextState().soilWaterMm();
        if (storage >= 0.0 && storage <= parameters.soilCapacityMm()) {
            findings.add(new Finding(
                    "MODEL-INVARIANTS",
                    Status.PASS,
                    "Final soil-water storage is inside the configured bucket bounds."));
        } else {
            findings.add(new Finding(
                    "MODEL-INVARIANTS",
                    Status.WARNING,
                    "Final soil-water storage is outside the configured bucket bounds."));
        }

        findings.add(new Finding(
                "ECOLOGY",
                Status.WARNING,
                "This bucket omits canopy, roots, species, habitat and biodiversity; it cannot establish forest health or justify field intervention."));

        switch (inputClass) {
            case SYNTHETIC_EXAMPLE -> findings.add(new Finding(
                    "DATA-STEWARD",
                    Status.WARNING,
                    "Inputs are synthetic examples; do not present them as real local or rainforest observations."));
            case OBSERVED_WITH_PROVENANCE_DECLARED -> findings.add(new Finding(
                    "DATA-STEWARD",
                    Status.PASS,
                    "Caller declares that source, date, unit, method, licence and uncertainty are recorded; this council does not independently verify them."));
            case OBSERVED_WITH_INCOMPLETE_PROVENANCE -> findings.add(new Finding(
                    "DATA-STEWARD",
                    Status.WARNING,
                    "Observation provenance is incomplete; do not use it as a verified evidence input."));
        }

        findings.add(new Finding(
                "RED-TEAM",
                Status.WARNING,
                "Single-bucket scope omits spatial variability, seasonal forcing, groundwater, canopy interception and calibration against measurements."));

        return List.copyOf(findings);
    }
}
