package de.matrixgame.earth;

/** Prints a synthetic wet-day and dry-day example. */
public final class GaiaWaterBucketDemo {
    private GaiaWaterBucketDemo() {
    }

    public static void main(String[] args) {
        GaiaWaterBucket.Parameters parameters =
                new GaiaWaterBucket.Parameters(120.0, 25.0);
        GaiaWaterBucket.State initial = new GaiaWaterBucket.State(60.0);

        GaiaWaterBucket.StepResult wetDay = GaiaWaterBucket.step(
                initial,
                new GaiaWaterBucket.Forcing(35.0, 3.0),
                parameters);
        print("Synthetic wet day", wetDay);

        GaiaWaterBucket.StepResult dryDay = GaiaWaterBucket.step(
                wetDay.nextState(),
                new GaiaWaterBucket.Forcing(0.0, 5.0),
                parameters);
        print("Synthetic dry day", dryDay);
    }

    private static void print(String label, GaiaWaterBucket.StepResult result) {
        System.out.println(label);
        System.out.printf("  soil water: %.3f mm%n", result.nextState().soilWaterMm());
        System.out.printf("  infiltration: %.3f mm%n", result.infiltrationMm());
        System.out.printf("  surface runoff: %.3f mm%n", result.surfaceRunoffMm());
        System.out.printf("  actual ET: %.3f mm%n", result.actualEvapotranspirationMm());
        System.out.printf("  deep drainage: %.3f mm%n", result.deepDrainageMm());
        System.out.printf("  balance residual: %.12f mm%n", result.waterBalanceResidualMm());
    }
}
