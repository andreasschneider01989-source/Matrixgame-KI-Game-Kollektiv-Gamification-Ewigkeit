package matrixgame.core;

import java.util.Map;

/** Treibt eine World bis zum Ende und liefert die Messwerte. */
public final class Simulation {
    private Simulation() {}
    public static Map<String, Double> run(World world) {
        while (!world.finished()) world.step();
        return world.metrics();
    }
}
