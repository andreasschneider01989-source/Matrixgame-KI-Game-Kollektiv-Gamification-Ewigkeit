package matrixgame.core;

/** Aktiver Node: nimmt wahr, entscheidet, handelt, lernt. */
public interface Agent extends Node {
    Action decide(Observation obs);
    default void learn(Feedback fb) {}
}
