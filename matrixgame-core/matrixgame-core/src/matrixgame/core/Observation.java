package matrixgame.core;

import java.util.List;

/** Wahrnehmung eines Agents in einem Tick (Existenz -> Wahrnehmung -> Information). */
public record Observation(int tick, double energy, double upkeep, double stock, double capacity,
                          double growthRate, int alive, double lastAvgHarvest, double maxHarvest,
                          List<Message> inbox) {}
