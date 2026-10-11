package matrixgame.core;

/** Informationsfluss: ein Broadcast-Token (Platzhalter fuer spaetere Lumini-Tokens). */
public record Message(int tick, String from, String payload) {}
