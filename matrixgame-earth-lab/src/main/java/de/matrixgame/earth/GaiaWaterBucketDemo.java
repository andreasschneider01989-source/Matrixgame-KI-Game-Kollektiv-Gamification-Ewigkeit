package de.matrixgame.earth;

/** Prints synthetic single-bucket and canopy/root-zone examples. */
public final class GaiaWaterBucketDemo {
    private GaiaWaterBucketDemo() {
    }

    public static void main(String[] args) {
        runSingleBucketExample();
        runForestCanopyExample();
    }

    private static void runSingleBucketExample() {
        GaiaWaterBucket.Parameters parameters =
                new GaiaWaterBucket.Parameters(120.0, 25.0);
        GaiaWaterBucket.State initial = new GaiaWaterBucket.State(60.0);

        GaiaWaterBucket.StepResult wetDay = GaiaWaterBucket.step(
                initial,
                new GaiaWaterBucket.Forcing(35.0, 3.0),
                parameters);
        print("Single-bucket synthetic wet day", wetDay);

        System.out.println("Independent reviewer findings");
        for (GaiaReviewCouncil.Finding finding : GaiaReviewCouncil.review(
                wetDay,
                parameters,
                GaiaReviewCouncil.InputClass.SYNTHETIC_EXAMPLE)) {
            System.out.printf("  [%s] %s: %s%n",
                    finding.status(), finding.agent(), finding.summary());
        }

        GaiaWaterBucket.StepResult dryDay = GaiaWaterBucket.step(
                wetDay.nextState(),
                new GaiaWaterBucket.Forcing(0.0, 5.0),
                parameters);
        print("Single-bucket synthetic dry day", dryDay);
    }

    private static void runForestCanopyExample() {
        GaiaForestWaterCycle.Parameters parameters =
                new GaiaForestWaterCycle.Parameters(2.0, 120.0, 20.0);
        GaiaForestWaterCycle.State initial =
                new GaiaForestWaterCycle.State(1.0, 60.0);
        GaiaForestWaterCycle.StepResult result = GaiaForestWaterCycle.step(
                initial,
                new GaiaForestWaterCycle.Forcing(35.0, 1.5, 4.0),
                parameters);

        System.out.println("Two-store forest synthetic wet day");
        System.out.printf("  canopy interception: %.3f mm%n", result.interceptionMm());
        System.out.printf("  throughfall: %.3f mm%n", result.throughfallMm());
        System.out.printf("  canopy evaporation: %.3f mm%n", result.canopyEvaporationMm());
        System.out.printf("  infiltration: %.3f mm%n", result.infiltrationMm());
        System.out.printf("  surface runoff: %.3f mm%n", result.surfaceRunoffMm());
        System.out.printf("  root-zone ET: %.3f mm%n", result.rootZoneEtMm());
        System.out.printf("  deep drainage: %.3f mm%n", result.deepDrainageMm());
        System.out.printf("  final canopy store: %.3f mm%n",
                result.nextState().canopyWaterMm());
        System.out.printf("  final root-zone store: %.3f mm%n",
                result.nextState().rootZoneWaterMm());
        System.out.printf("  balance residual: %.12f mm%n",
                result.waterBalanceResidualMm());
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
