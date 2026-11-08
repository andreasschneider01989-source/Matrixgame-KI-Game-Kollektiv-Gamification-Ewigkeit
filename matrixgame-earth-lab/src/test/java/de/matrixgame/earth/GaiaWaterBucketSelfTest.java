package de.matrixgame.earth;

/** Dependency-free checks for the Gaia water bucket MVP. */
public final class GaiaWaterBucketSelfTest {
    private GaiaWaterBucketSelfTest() {
    }

    public static void main(String[] args) {
        testHeavyRainBalance();
        testDryDayEvapotranspiration();
        testEvapotranspirationCannotExceedAvailableWater();
        testDeterministicReplay();
        testNegativeForcingRejected();
        testInitialStorageAboveCapacityRejected();
        System.out.println("GaiaWaterBucketSelfTest: 6 tests passed.");
    }

    private static void testHeavyRainBalance() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult r = GaiaWaterBucket.step(
                new GaiaWaterBucket.State(25.0),
                new GaiaWaterBucket.Forcing(20.0, 3.0),
                p);

        assertClose(10.0, r.infiltrationMm(), "infiltration");
        assertClose(10.0, r.surfaceRunoffMm(), "surface runoff");
        assertClose(5.0, r.deepDrainageMm(), "deep drainage");
        assertClose(3.0, r.actualEvapotranspirationMm(), "actual ET");
        assertClose(27.0, r.nextState().soilWaterMm(), "final storage");
        assertClose(0.0, r.waterBalanceResidualMm(), "water balance");
    }

    private static void testDryDayEvapotranspiration() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult r = GaiaWaterBucket.step(
                new GaiaWaterBucket.State(8.0),
                new GaiaWaterBucket.Forcing(0.0, 3.0),
                p);

        assertClose(5.0, r.nextState().soilWaterMm(), "dry-day storage");
        assertClose(3.0, r.actualEvapotranspirationMm(), "dry-day actual ET");
        assertClose(0.0, r.waterBalanceResidualMm(), "dry-day water balance");
    }

    private static void testEvapotranspirationCannotExceedAvailableWater() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult r = GaiaWaterBucket.step(
                new GaiaWaterBucket.State(0.0),
                new GaiaWaterBucket.Forcing(5.0, 10.0),
                p);

        assertClose(5.0, r.actualEvapotranspirationMm(), "ET limited by water");
        assertClose(0.0, r.nextState().soilWaterMm(), "empty soil bucket");
        assertClose(0.0, r.waterBalanceResidualMm(), "limited-ET water balance");
    }

    private static void testDeterministicReplay() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(100.0, 20.0);
        GaiaWaterBucket.State s = new GaiaWaterBucket.State(40.0);
        GaiaWaterBucket.Forcing f = new GaiaWaterBucket.Forcing(12.0, 4.0);

        GaiaWaterBucket.StepResult first = GaiaWaterBucket.step(s, f, p);
        GaiaWaterBucket.StepResult second = GaiaWaterBucket.step(s, f, p);

        assertClose(
                first.nextState().soilWaterMm(),
                second.nextState().soilWaterMm(),
                "deterministic replay");
        assertClose(
                first.waterBalanceResidualMm(),
                second.waterBalanceResidualMm(),
                "deterministic residual");
    }

    private static void testNegativeForcingRejected() {
        expectThrows(
                () -> new GaiaWaterBucket.Forcing(-1.0, 2.0),
                "negative precipitation");
    }

    private static void testInitialStorageAboveCapacityRejected() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(10.0, 5.0);
        expectThrows(
                () -> GaiaWaterBucket.step(
                        new GaiaWaterBucket.State(11.0),
                        new GaiaWaterBucket.Forcing(0.0, 0.0),
                        p),
                "storage above capacity");
    }

    private static void assertClose(double expected, double actual, String label) {
        if (Math.abs(expected - actual) > 1.0e-9) {
            throw new AssertionError(
                    label + ": expected " + expected + " but got " + actual);
        }
    }

    private static void expectThrows(Runnable action, String label) {
        try {
            action.run();
            throw new AssertionError(label + ": expected IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
            // Expected validation failure.
        }
    }
}
