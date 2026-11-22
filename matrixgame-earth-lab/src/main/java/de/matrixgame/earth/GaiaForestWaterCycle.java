package de.matrixgame.earth;

import java.util.Objects;

/**
 * A two-store forest water model with canopy interception and a root-zone
 * bucket. This is a lumped conceptual model, not a calibrated land-surface
 * scheme or a species-specific forest model.
 *
 * <p>Water quantities are millimetres of water equivalent per model step.</p>
 */
public final class GaiaForestWaterCycle {
    private GaiaForestWaterCycle() {
    }

    public record Parameters(
            double canopyCapacityMm,
            double rootZoneCapacityMm,
            double maxInfiltrationMmPerStep) {
        public Parameters {
            requireFiniteNonNegative(canopyCapacityMm, "canopyCapacityMm");
            requireFiniteNonNegative(rootZoneCapacityMm, "rootZoneCapacityMm");
            requireFiniteNonNegative(maxInfiltrationMmPerStep, "maxInfiltrationMmPerStep");
            if (rootZoneCapacityMm <= 0.0) {
                throw new IllegalArgumentException(
                        "rootZoneCapacityMm must be greater than zero.");
            }
        }
    }

    public record State(
            double canopyWaterMm,
            double rootZoneWaterMm) {
        public State {
            requireFiniteNonNegative(canopyWaterMm, "canopyWaterMm");
            requireFiniteNonNegative(rootZoneWaterMm, "rootZoneWaterMm");
        }
    }

    public record Forcing(
            double precipitationMm,
            double potentialCanopyEvaporationMm,
            double potentialRootZoneEtMm) {
        public Forcing {
            requireFiniteNonNegative(precipitationMm, "precipitationMm");
            requireFiniteNonNegative(
                    potentialCanopyEvaporationMm,
                    "potentialCanopyEvaporationMm");
            requireFiniteNonNegative(potentialRootZoneEtMm, "potentialRootZoneEtMm");
        }
    }

    public record StepResult(
            State nextState,
            double interceptionMm,
            double throughfallMm,
            double canopyEvaporationMm,
            double infiltrationMm,
            double surfaceRunoffMm,
            double rootZoneEtMm,
            double deepDrainageMm,
            double waterBalanceResidualMm) {
        public StepResult {
            Objects.requireNonNull(nextState, "nextState");
        }
    }

    /**
     * Advances canopy and root-zone stores by one lumped time step.
     *
     * <p>Rain first fills available canopy storage; rainfall not intercepted
     * becomes throughfall. Canopy evaporation is then limited by available
     * canopy water. Throughfall infiltrates up to the configured capacity;
     * excess becomes surface runoff. Root-zone water above its capacity
     * becomes deep drainage, and root-zone evapotranspiration cannot exceed
     * the water remaining in that store.</p>
     *
     * <p>Fluxes within a time step are ordered for bookkeeping. The model
     * does not resolve sub-daily storm dynamics, canopy drip timing,
     * hydraulic redistribution, or species-specific root uptake.</p>
     */
    public static StepResult step(
            State previous,
            Forcing forcing,
            Parameters parameters) {
        Objects.requireNonNull(previous, "previous");
        Objects.requireNonNull(forcing, "forcing");
        Objects.requireNonNull(parameters, "parameters");

        if (previous.canopyWaterMm() > parameters.canopyCapacityMm()) {
            throw new IllegalArgumentException(
                    "Initial canopy water exceeds canopy capacity.");
        }
        if (previous.rootZoneWaterMm() > parameters.rootZoneCapacityMm()) {
            throw new IllegalArgumentException(
                    "Initial root-zone water exceeds root-zone capacity.");
        }

        double availableCanopyCapacity = Math.max(
                0.0,
                parameters.canopyCapacityMm() - previous.canopyWaterMm());
        double interception = Math.min(
                forcing.precipitationMm(),
                availableCanopyCapacity);
        double throughfall = forcing.precipitationMm() - interception;

        double canopyWaterBeforeEvaporation =
                previous.canopyWaterMm() + interception;
        double canopyEvaporation = Math.min(
                forcing.potentialCanopyEvaporationMm(),
                canopyWaterBeforeEvaporation);
        double finalCanopyWater =
                canopyWaterBeforeEvaporation - canopyEvaporation;

        double infiltration = Math.min(
                throughfall,
                parameters.maxInfiltrationMmPerStep());
        double surfaceRunoff = throughfall - infiltration;

        double rootWaterBeforeDrainage =
                previous.rootZoneWaterMm() + infiltration;
        double deepDrainage = Math.max(
                0.0,
                rootWaterBeforeDrainage - parameters.rootZoneCapacityMm());
        double rootWaterAfterDrainage = Math.min(
                rootWaterBeforeDrainage,
                parameters.rootZoneCapacityMm());

        double rootZoneEt = Math.min(
                forcing.potentialRootZoneEtMm(),
                rootWaterAfterDrainage);
        double finalRootZoneWater = rootWaterAfterDrainage - rootZoneEt;

        double residual = forcing.precipitationMm()
                + previous.canopyWaterMm()
                + previous.rootZoneWaterMm()
                - surfaceRunoff
                - canopyEvaporation
                - rootZoneEt
                - deepDrainage
                - finalCanopyWater
                - finalRootZoneWater;

        return new StepResult(
                new State(finalCanopyWater, finalRootZoneWater),
                interception,
                throughfall,
                canopyEvaporation,
                infiltration,
                surfaceRunoff,
                rootZoneEt,
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
