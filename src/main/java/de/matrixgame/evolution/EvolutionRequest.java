package de.matrixgame.evolution;

/** Immutable request parameters for one Evolution Forge experiment. */
public final class EvolutionRequest {
    private final EvolutionScenario scenario;
    private final int steps;
    private final long seed;

    public EvolutionRequest(EvolutionScenario scenario, int steps, long seed) {
        if (scenario == null) {
            throw new IllegalArgumentException("scenario must not be null");
        }
        if (steps < 20 || steps > 2000) {
            throw new IllegalArgumentException("steps must be between 20 and 2000");
        }
        if (seed < 0 || seed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("seed must be between 0 and Integer.MAX_VALUE");
        }
        this.scenario = scenario;
        this.steps = steps;
        this.seed = seed;
    }

    public EvolutionScenario getScenario() {
        return scenario;
    }

    public int getSteps() {
        return steps;
    }

    public long getSeed() {
        return seed;
    }

    /** Minimal JSON payload understood by the current Python Config dataclass. */
    public String toJson() {
        return "{\n"
            + "  \"scenario\": \"" + scenario.getPythonId() + "\",\n"
            + "  \"steps\": " + steps + ",\n"
            + "  \"seed\": " + seed + "\n"
            + "}\n";
    }
}
