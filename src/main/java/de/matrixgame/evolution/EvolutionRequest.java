package de.matrixgame.evolution;

/** Immutable request parameters for one Evolution Forge experiment. */
public final class EvolutionRequest {
    private final EvolutionScenario scenario;
    private final int steps;
    private final long seed;
    private final Double energyFlux;
    private final Double resourceRegen;
    private final Double maintenanceCost;
    private final Double cooperationBenefit;

    /** Backwards-compatible constructor using the scenario's default parameters. */
    public EvolutionRequest(EvolutionScenario scenario, int steps, long seed) {
        this(scenario, steps, seed, null, null, null, null);
    }

    /**
     * Creates a bounded experiment request. Null optional values keep the scenario defaults.
     */
    public EvolutionRequest(EvolutionScenario scenario, int steps, long seed,
            Double energyFlux, Double resourceRegen,
            Double maintenanceCost, Double cooperationBenefit) {
        if (scenario == null) {
            throw new IllegalArgumentException("scenario must not be null");
        }
        if (steps < 20 || steps > 2000) {
            throw new IllegalArgumentException("steps must be between 20 and 2000");
        }
        if (seed < 0 || seed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("seed must be between 0 and Integer.MAX_VALUE");
        }
        validateRange("energyFlux", energyFlux, 0.0, 1.0);
        validateRange("resourceRegen", resourceRegen, 0.0, 0.2);
        validateRange("maintenanceCost", maintenanceCost, 0.001, 0.15);
        validateRange("cooperationBenefit", cooperationBenefit, 0.0, 0.3);

        this.scenario = scenario;
        this.steps = steps;
        this.seed = seed;
        this.energyFlux = energyFlux;
        this.resourceRegen = resourceRegen;
        this.maintenanceCost = maintenanceCost;
        this.cooperationBenefit = cooperationBenefit;
    }

    private static void validateRange(String name, Double value, double min, double max) {
        if (value != null && (value.isNaN() || value.isInfinite() || value < min || value > max)) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
    }

    public EvolutionScenario getScenario() { return scenario; }
    public int getSteps() { return steps; }
    public long getSeed() { return seed; }
    public Double getEnergyFlux() { return energyFlux; }
    public Double getResourceRegen() { return resourceRegen; }
    public Double getMaintenanceCost() { return maintenanceCost; }
    public Double getCooperationBenefit() { return cooperationBenefit; }

    /** JSON fields match the Python Config dataclass; null overrides are omitted. */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n")
            .append("  \"scenario\": \"").append(scenario.getPythonId()).append("\",\n")
            .append("  \"steps\": ").append(steps).append(",\n")
            .append("  \"seed\": ").append(seed);
        appendNumber(json, "energy_flux", energyFlux);
        appendNumber(json, "resource_regen", resourceRegen);
        appendNumber(json, "maintenance_cost", maintenanceCost);
        appendNumber(json, "cooperation_benefit", cooperationBenefit);
        json.append("\n}\n");
        return json.toString();
    }

    private static void appendNumber(StringBuilder json, String key, Double value) {
        if (value != null) {
            json.append(",\n  \"").append(key).append("\": ").append(Double.toString(value));
        }
    }
}
