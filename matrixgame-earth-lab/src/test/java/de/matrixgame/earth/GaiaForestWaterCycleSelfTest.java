package de.matrixgame.earth;

/** Dependency-free tests for the two-store canopy/root-zone model. */
public final class GaiaForestWaterCycleSelfTest {
    private GaiaForestWaterCycleSelfTest() {
    }

    public static void main(String[] args) {
        testCanopyInterceptionAndRootZoneBalance();
        testRootZoneOverflowBecomesDeepDrainage();
        testEvaporationCannotExceedStoredWater();
        testZeroCanopyCapacity();
        testDeterministicReplay();
        testNegativeForcingRejected();
        testInitialStorageAboveCapacityRejected();
        System.out.println("GaiaForestWaterCycleSelfTest: 7 tests passed.");
    }

    private static void testCanopyInterceptionAndRootZoneBalance() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(2.0, 120.0, 20.0);
        GaiaForestWaterCycle.StepResult r = GaiaForestWaterCycle.step(
                new GaiaForestWaterCycle.State(1.0, 60.0),
                new GaiaForestWaterCycle.Forcing(35.0, 1.5, 4.0),
                p);

        assertClose(1.0, r.interceptionMm(), "canopy interception");
        assertClose(34.0, r.throughfallMm(), "throughfall");
        assertClose(1.5, r.canopyEvaporationMm(), "canopy evaporation");
        assertClose(20.0, r.infiltrationMm(), "root-zone infiltration");
        assertClose(14.0, r.surfaceRunoffMm(), "surface runoff");
        assertClose(4.0, r.rootZoneEtMm(), "root-zone ET");
        assertClose(0.0, r.deepDrainageMm(), "deep drainage");
        assertClose(0.5, r.nextState().canopyWaterMm(), "final canopy store");
        assertClose(76.0, r.nextState().rootZoneWaterMm(), "final root-zone store");
        assertClose(0.0, r.waterBalanceResidualMm(), "forest water balance");
    }

    private static void testRootZoneOverflowBecomesDeepDrainage() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(1.0, 10.0, 10.0);
        GaiaForestWaterCycle.StepResult r = GaiaForestWaterCycle.step(
                new GaiaForestWaterCycle.State(0.5, 9.0),
                new GaiaForestWaterCycle.Forcing(5.0, 0.0, 0.0),
                p);

        assertClose(0.5, r.interceptionMm(), "interception at near-full canopy");
        assertClose(4.5, r.throughfallMm(), "throughfall after interception");
        assertClose(4.5, r.infiltrationMm(), "infiltration");
        assertClose(3.5, r.deepDrainageMm(), "root-zone deep drainage");
        assertClose(10.0, r.nextState().rootZoneWaterMm(), "root-zone capacity");
        assertClose(0.0, r.waterBalanceResidualMm(), "overflow balance");
    }

    private static void testEvaporationCannotExceedStoredWater() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(2.0, 50.0, 20.0);
        GaiaForestWaterCycle.StepResult r = GaiaForestWaterCycle.step(
                new GaiaForestWaterCycle.State(0.5, 3.0),
                new GaiaForestWaterCycle.Forcing(0.0, 5.0, 8.0),
                p);

        assertClose(0.5, r.canopyEvaporationMm(), "canopy evaporation limited");
        assertClose(3.0, r.rootZoneEtMm(), "root-zone ET limited");
        assertClose(0.0, r.nextState().canopyWaterMm(), "empty canopy");
        assertClose(0.0, r.nextState().rootZoneWaterMm(), "empty root zone");
        assertClose(0.0, r.waterBalanceResidualMm(), "dry-day balance");
    }

    private static void testZeroCanopyCapacity() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(0.0, 20.0, 10.0);
        GaiaForestWaterCycle.StepResult r = GaiaForestWaterCycle.step(
                new GaiaForestWaterCycle.State(0.0, 5.0),
                new GaiaForestWaterCycle.Forcing(6.0, 3.0, 2.0),
                p);

        assertClose(0.0, r.interceptionMm(), "zero canopy interception");
        assertClose(6.0, r.throughfallMm(), "all rain becomes throughfall");
        assertClose(2.0, r.rootZoneEtMm(), "root-zone ET");
        assertClose(0.0, r.waterBalanceResidualMm(), "zero-canopy balance");
    }

    private static void testDeterministicReplay() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(3.0, 100.0, 25.0);
        GaiaForestWaterCycle.State s =
                new GaiaForestWaterCycle.State(1.0, 35.0);
        GaiaForestWaterCycle.Forcing f =
                new GaiaForestWaterCycle.Forcing(18.0, 2.0, 4.0);

        GaiaForestWaterCycle.StepResult first = GaiaForestWaterCycle.step(s, f, p);
        GaiaForestWaterCycle.StepResult second = GaiaForestWaterCycle.step(s, f, p);
        assertClose(
                first.nextState().canopyWaterMm(),
                second.nextState().canopyWaterMm(),
                "deterministic canopy replay");
        assertClose(
                first.nextState().rootZoneWaterMm(),
                second.nextState().rootZoneWaterMm(),
                "deterministic root-zone replay");
        assertClose(
                first.waterBalanceResidualMm(),
                second.waterBalanceResidualMm(),
                "deterministic residual replay");
    }

    private static void testNegativeForcingRejected() {
        expectThrows(
                () -> new GaiaForestWaterCycle.Forcing(-1.0, 0.0, 0.0),
                "negative precipitation");
    }

    private static void testInitialStorageAboveCapacityRejected() {
        GaiaForestWaterCycle.Parameters p =
                new GaiaForestWaterCycle.Parameters(1.0, 10.0, 5.0);
        expectThrows(
                () -> GaiaForestWaterCycle.step(
                        new GaiaForestWaterCycle.State(2.0, 5.0),
                        new GaiaForestWaterCycle.Forcing(0.0, 0.0, 0.0),
                        p),
                "canopy storage above capacity");
        expectThrows(
                () -> GaiaForestWaterCycle.step(
                        new GaiaForestWaterCycle.State(0.5, 11.0),
                        new GaiaForestWaterCycle.Forcing(0.0, 0.0, 0.0),
                        p),
                "root-zone storage above capacity");
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
