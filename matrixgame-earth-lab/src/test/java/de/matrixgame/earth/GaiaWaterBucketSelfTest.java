package de.matrixgame.earth;

import java.util.List;

/** Dependency-free checks for the Gaia water bucket and reviewer council. */
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
        testReviewerCouncilKeepsPerspectivesSeparate();
        testReviewerCouncilFlagsBadWaterBalance();
        testSyntheticInputsRemainClearlyLabelled();
        System.out.println("GaiaWaterBucketSelfTest: 9 tests passed.");
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

    private static void testReviewerCouncilKeepsPerspectivesSeparate() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult result = GaiaWaterBucket.step(
                new GaiaWaterBucket.State(20.0),
                new GaiaWaterBucket.Forcing(5.0, 2.0),
                p);

        List<GaiaReviewCouncil.Finding> findings = GaiaReviewCouncil.review(
                result,
                p,
                GaiaReviewCouncil.InputClass.SYNTHETIC_EXAMPLE);

        if (findings.size() != 5) {
            throw new AssertionError("Expected five independent reviewer findings.");
        }
        assertFinding(findings, "HYDROLOGY", GaiaReviewCouncil.Status.PASS);
        assertFinding(findings, "MODEL-INVARIANTS", GaiaReviewCouncil.Status.PASS);
        assertFinding(findings, "ECOLOGY", GaiaReviewCouncil.Status.WARNING);
        assertFinding(findings, "DATA-STEWARD", GaiaReviewCouncil.Status.WARNING);
        assertFinding(findings, "RED-TEAM", GaiaReviewCouncil.Status.WARNING);
    }

    private static void testReviewerCouncilFlagsBadWaterBalance() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult inconsistent = new GaiaWaterBucket.StepResult(
                new GaiaWaterBucket.State(10.0),
                5.0,
                0.0,
                2.0,
                0.0,
                1.0);

        List<GaiaReviewCouncil.Finding> findings = GaiaReviewCouncil.review(
                inconsistent,
                p,
                GaiaReviewCouncil.InputClass.SYNTHETIC_EXAMPLE);
        assertFinding(findings, "HYDROLOGY", GaiaReviewCouncil.Status.WARNING);
    }

    private static void testSyntheticInputsRemainClearlyLabelled() {
        GaiaWaterBucket.Parameters p = new GaiaWaterBucket.Parameters(30.0, 10.0);
        GaiaWaterBucket.StepResult result = GaiaWaterBucket.step(
                new GaiaWaterBucket.State(20.0),
                new GaiaWaterBucket.Forcing(5.0, 2.0),
                p);

        List<GaiaReviewCouncil.Finding> findings = GaiaReviewCouncil.review(
                result,
                p,
                GaiaReviewCouncil.InputClass.SYNTHETIC_EXAMPLE);
        assertFinding(findings, "DATA-STEWARD", GaiaReviewCouncil.Status.WARNING);
    }

    private static void assertFinding(
            List<GaiaReviewCouncil.Finding> findings,
            String agent,
            GaiaReviewCouncil.Status expectedStatus) {
        GaiaReviewCouncil.Finding finding = findings.stream()
                .filter(item -> item.agent().equals(agent))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing reviewer: " + agent));
        if (finding.status() != expectedStatus) {
            throw new AssertionError(
                    agent + ": expected " + expectedStatus + " but got " + finding.status());
        }
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
