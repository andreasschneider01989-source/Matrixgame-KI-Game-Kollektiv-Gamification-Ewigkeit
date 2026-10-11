package matrixgame.commons;

import matrixgame.core.*;

/** Maximiert nur den eigenen kurzfristigen Gewinn. */
public final class GreedyAgent implements Agent {
    private final String id;
    public GreedyAgent(String id) { this.id = id; }
    @Override public String id() { return id; }
    @Override public Action decide(Observation o) { return Action.of(o.maxHarvest()); }
}
