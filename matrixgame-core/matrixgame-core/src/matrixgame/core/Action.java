package matrixgame.core;

/** Entscheidung eines Agents: Entnahmemenge plus optionales Signal (Message). */
public record Action(double harvest, String signal) {
    public static Action of(double harvest) { return new Action(harvest, null); }
}
