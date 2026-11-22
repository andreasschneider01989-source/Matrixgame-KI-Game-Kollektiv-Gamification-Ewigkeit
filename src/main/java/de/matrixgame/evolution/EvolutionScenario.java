package de.matrixgame.evolution;

/**
 * Supported exploratory environment profiles for Evolution Forge.
 *
 * These identifiers select model parameters; they are not claims that life
 * exists or will emerge in the corresponding environments.
 */
public enum EvolutionScenario {
    EARTH_TIDAL_POOL("earth_tidal_pool"),
    ALKALINE_VENT("alkaline_vent"),
    ICY_OCEAN("icy_ocean"),
    TITAN_HYPOTHETICAL("titan_hypothetical");

    private final String pythonId;

    EvolutionScenario(String pythonId) {
        this.pythonId = pythonId;
    }

    public String getPythonId() {
        return pythonId;
    }
}
