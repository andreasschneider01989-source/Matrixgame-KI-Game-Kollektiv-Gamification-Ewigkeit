package matrixgame.core;

import java.util.Map;

/** Zustand + Regeln + Zufall + Rueckkopplung + Zeit. */
public interface World {
    void step();
    boolean finished();
    int tick();
    Map<String, Double> metrics();
}
