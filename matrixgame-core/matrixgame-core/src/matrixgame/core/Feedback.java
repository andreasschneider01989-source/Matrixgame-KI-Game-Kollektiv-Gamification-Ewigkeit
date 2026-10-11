package matrixgame.core;

/** Rueckmeldung nach der Aktion (Rueckmeldung -> Lernen -> Anpassung). */
public record Feedback(double gained, double energyAfter, boolean stillAlive) {}
