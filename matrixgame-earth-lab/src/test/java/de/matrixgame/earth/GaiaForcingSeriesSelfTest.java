package de.matrixgame.earth;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

/** Dependency-free tests for dated Gaia forcing data and missing-value handling. */
public final class GaiaForcingSeriesSelfTest {
    private GaiaForcingSeriesSelfTest() {
    }

    public static void main(String[] args) {
        testCompleteDailySeriesConverts();
        testMissingValuesAreNotZero();
        testDatesMustBeConsecutive();
        testInvalidMeasurementsRejected();
        testRowsAreImmutable();
        System.out.println("GaiaForcingSeriesSelfTest: 5 tests passed.");
    }

    private static void testCompleteDailySeriesConverts() {
        GaiaForcingSeries series = new GaiaForcingSeries(List.of(
                row("2026-11-01", 4.0, 1.0, 2.0),
                row("2026-11-02", 0.0, 1.2, 2.5)));

        if (series.size() != 2 || !series.isComplete()) {
            throw new AssertionError("Complete two-day series was not recognised.");
        }
        List<GaiaForestWaterCycle.Forcing> forcings = series.toModelForcings();
        assertClose(4.0, forcings.get(0).precipitationMm(), "first-day precipitation");
        assertClose(0.0, forcings.get(1).precipitationMm(), "measured zero precipitation");
    }

    private static void testMissingValuesAreNotZero() {
        GaiaForcingSeries series = new GaiaForcingSeries(List.of(
                row("2026-11-01", 4.0, 1.0, 2.0),
                new GaiaForcingSeries.DailyForcing(
                        LocalDate.parse("2026-11-02"),
                        OptionalDouble.empty(),
                        OptionalDouble.of(1.0),
                        OptionalDouble.of(2.0))));

        if (series.isComplete() || series.missingValueCount() != 1) {
            throw new AssertionError("Missing precipitation was not retained.");
        }
        expectThrows(
                IllegalStateException.class,
                series::toModelForcings,
                "Missing forcing value 'precipitationMm' for 2026-11-02");
    }

    private static void testDatesMustBeConsecutive() {
        expectThrows(
                IllegalArgumentException.class,
                () -> new GaiaForcingSeries(List.of(
                        row("2026-11-01", 1.0, 1.0, 1.0),
                        row("2026-11-01", 1.0, 1.0, 1.0))),
                "expected 2026-11-02");
        expectThrows(
                IllegalArgumentException.class,
                () -> new GaiaForcingSeries(List.of(
                        row("2026-11-01", 1.0, 1.0, 1.0),
                        row("2026-11-03", 1.0, 1.0, 1.0))),
                "expected 2026-11-02");
    }

    private static void testInvalidMeasurementsRejected() {
        expectThrows(
                IllegalArgumentException.class,
                () -> row("2026-11-01", -0.1, 1.0, 1.0),
                "finite and non-negative");
        expectThrows(
                IllegalArgumentException.class,
                () -> row("2026-11-01", Double.NaN, 1.0, 1.0),
                "finite and non-negative");
        expectThrows(
                IllegalArgumentException.class,
                () -> row("2026-11-01", 1.0, Double.POSITIVE_INFINITY, 1.0),
                "finite and non-negative");
    }

    private static void testRowsAreImmutable() {
        GaiaForcingSeries series = new GaiaForcingSeries(
                List.of(row("2026-11-01", 1.0, 1.0, 1.0)));
        expectThrows(
                UnsupportedOperationException.class,
                () -> series.rows().clear(),
                null);
    }

    private static GaiaForcingSeries.DailyForcing row(
            String date,
            double precipitation,
            double canopyEvaporation,
            double rootEt) {
        return new GaiaForcingSeries.DailyForcing(
                LocalDate.parse(date),
                OptionalDouble.of(precipitation),
                OptionalDouble.of(canopyEvaporation),
                OptionalDouble.of(rootEt));
    }

    private static void assertClose(double expected, double actual, String label) {
        if (Math.abs(expected - actual) > 1.0e-9) {
            throw new AssertionError(
                    label + ": expected " + expected + " but got " + actual);
        }
    }

    private static void expectThrows(
            Class<? extends RuntimeException> type,
            Runnable action,
            String messageFragment) {
        try {
            action.run();
            throw new AssertionError("Expected " + type.getSimpleName() + ".");
        } catch (RuntimeException actual) {
            if (!type.isInstance(actual)) {
                throw actual;
            }
            if (messageFragment != null
                    && (actual.getMessage() == null
                    || !actual.getMessage().contains(messageFragment))) {
                throw new AssertionError("Unexpected exception message: "
                        + actual.getMessage(), actual);
            }
        }
    }
}
