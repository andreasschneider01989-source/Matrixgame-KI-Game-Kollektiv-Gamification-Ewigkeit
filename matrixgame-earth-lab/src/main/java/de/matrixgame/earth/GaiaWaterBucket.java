package de.matrixgame.earth;

import java.util.Objects;

/**
 * A small, deterministic soil-water bucket model.
 *
 * <p>All water quantities are millimetres of water equivalent per model step.
 * The model is intentionally lumped and does not represent a spatially
 * distributed catchment or a complete land-surface scheme.</p>
 */
public final class GaiaWaterBucket {
    private static final double BALANCE_EPSILON_MM = 1.0e-9;

    private GaiaWaterBucket() {
    }

    public record Parameters(
            double soilCapacityMm,
            double maxInfiltrationMmPerStep) {
        public Parameters {
            requireFiniteNonNegative(soilCapacityMm, "soilCapacityMm");
            requireFiniteNonNegative(maxInfiltrationMmPerStep, "maxInfiltrationMmPerStep");
            if (soilCapacityMm <= 0.0) {
                throw new IllegalArgumentException("soilCapacityMm must be greater than zero.");
            }
        }
    }

    public record State(double soilWaterMm) {
        public State {
            requireFiniteNonNegative(soilWaterMm, "soilWaterMm");
        }
    }

    public record Forcing(
            double precipitationMm,
            double potentialEvapotranspirationMm) {
        public Forcing {
            requireFiniteNonNegative(precipitationMm, "precipitationMm");
            requireFiniteNonNegative(
                    potentialEvapotranspirationMm,
                    "potentialEvapotranspirationMm");
        }
    }

    public record StepResult(
            State nextState,
            double infiltrationMm,
            double surfaceRunoffMm,
            double actualEvapotranspirationMm,
            double deepDrainageMm,
            double waterBalanceResidualMm) {
        public StepResult {
            Objects.requireNonNull(nextState, "nextState");
        }
    }

    /**
     * Advances the soil bucket by one discrete time step.
     *
     * <p>Rainfall exceeding the configured infiltration capacity becomes
     * surface runoff. Water above the soil bucket capacity becomes deep
     * drainage. Actual evapotranspiration cannot exceed water available in
     * the soil bucket.</p>
     *
     * @throws IllegalArgumentException if the initial soil water exceeds the
     *                                  configured soil capacity
     */
    public static StepResult step(
            State previous,
            Forcing forcing,
            Parameters parameters) {
        Objects.requireNonNull(previous, "previous");
        Objects.requireNonNull(forcing, "forcing");
        Objects.requireNonNull(parameters, "parameters");

        if (previous.soilWaterMm() > parameters.soilCapacityMm() + BALANCE_EPSILON_MM) {
            throw new IllegalArgumentException(
                    "Initial soil water exceeds the configured soil capacity.");
        }

        double infiltration = Math.min(
                forcing.precipitationMm(),
                parameters.maxInfiltrationMmPerStep());
        double surfaceRunoff = forcing.precipitationMm() - infiltration;

        double availableBeforeDrainage = previous.soilWaterMm() + infiltration;
        double deepDrainage = Math.max(
                0.0,
                availableBeforeDrainage - parameters.soilCapacityMm());
        double soilWaterBeforeEvapotranspiration = Math.min(
                availableBeforeDrainage,
                parameters.soilCapacityMm());

        double actualEvapotranspiration = Math.min(
                forcing.potentialEvapotranspirationMm(),
                soilWaterBeforeEvapotranspiration);
        double finalSoilWater = soilWaterBeforeEvapotranspiration
                - actualEvapotranspiration;

        double residual = forcing.precipitationMm()
                + previous.soilWaterMm()
                - surfaceRunoff
                - actualEvapotranspiration
                - deepDrainage
                - finalSoilWater;

        return new StepResult(
                new State(finalSoilWater),
                infiltration,
                surfaceRunoff,
                actualEvapotranspiration,
                deepDrainage,
                residual);
    }

    private static void requireFiniteNonNegative(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(
                    name + " must be a finite, non-negative number.");
        }
    }
}
