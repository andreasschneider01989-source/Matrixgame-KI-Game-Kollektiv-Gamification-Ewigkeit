package matrixgame.commons;

import matrixgame.core.*;
import java.util.Random;

/** Nullmodell: zufaellige Entnahme. */
public final class RandomAgent implements Agent {
    private final String id;
    private final Random rng;
    public RandomAgent(String id, long seed) { this.id = id; this.rng = new Random(seed); }
    @Override public String id() { return id; }
    @Override public Action decide(Observation o) { return Action.of(rng.nextDouble() * o.maxHarvest()); }
}
