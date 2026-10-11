package matrixgame.commons;

/** Parameter der Allmende-Welt (Tragedy of the Commons mit logistischem Nachwachsen). */
public record CommonsConfig(double capacity, double initialStockFraction, double growthRate,
                            double upkeep, double maxHarvest, double startEnergy,
                            double noise, int maxTicks) {
    public static CommonsConfig defaults() {
        return new CommonsConfig(1000, 0.8, 0.25, 5, 20, 50, 0.1, 200);
    }
}
