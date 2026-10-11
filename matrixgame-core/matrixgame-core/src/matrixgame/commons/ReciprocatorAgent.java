package matrixgame.commons;

import matrixgame.core.*;

/** Nimmt seinen fairen nachhaltigen Anteil; vergilt Uebernutzung der anderen mit Maximalentnahme. */
public final class ReciprocatorAgent implements Agent {
    private final String id;
    public ReciprocatorAgent(String id) { this.id = id; }
    @Override public String id() { return id; }
    @Override public Action decide(Observation o) {
        double quota = o.growthRate() * o.capacity() / 4.0 / o.alive(); // Anteil am maximalen Dauerertrag
        boolean defected = o.tick() > 1 && o.lastAvgHarvest() > 1.5 * quota;
        return Action.of(defected ? o.maxHarvest() : Math.min(quota, o.maxHarvest()));
    }
}
