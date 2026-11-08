package de.matrixgame.earth;

import java.time.Instant;
import java.util.Map;

/** Dependency-free tests for Gaia run provenance manifests. */
public final class GaiaRunManifestSelfTest {
    private GaiaRunManifestSelfTest() {
    }

    public static void main(String[] args) {
        testStableJsonAndSortedMaps();
        testJsonEscaping();
        testRequiredProvenanceFields();
        System.out.println("GaiaRunManifestSelfTest: 3 tests passed.");
    }

    private static void testStableJsonAndSortedMaps() {
        GaiaRunManifest manifest = sampleManifest(
                Map.of("zeta", "last", "alpha", "first"),
                Map.of("rootWaterMm", "60 mm", "canopyWaterMm", "1 mm"));
        String json = manifest.toJson();

        if (json.indexOf("\"alpha\"") > json.indexOf("\"zeta\"")) {
            throw new AssertionError("Parameter keys are not sorted.");
        }
        if (!json.contains("\"datasetLicense\": \"not-applicable-synthetic-data\"")) {
            throw new AssertionError("Dataset licence was not serialised.");
        }
        if (!json.contains("\"missingDataPolicy\"")) {
            throw new AssertionError("Missing-data policy was not serialised.");
        }
        if (!json.contains("\"createdAt\": \"2026-11-08T20:00:00Z\"")) {
            throw new AssertionError("Timestamp was not serialised as ISO-8601.");
        }
    }

    private static void testJsonEscaping() {
        GaiaRunManifest manifest = sampleManifest(
                Map.of("note", "rainfall \"quality\"\nreview"),
                Map.of("state", "root\\zone"));
        String json = manifest.toJson();

        if (!json.contains("rainfall \\\"quality\\\"\\nreview")) {
            throw new AssertionError("Quotes and newline were not escaped.");
        }
        if (!json.contains("root\\\\zone")) {
            throw new AssertionError("Backslash was not escaped.");
        }
    }

    private static void testRequiredProvenanceFields() {
        expectThrows(
                IllegalArgumentException.class,
                () -> new GaiaRunManifest(
                        "run-1",
                        Instant.parse("2026-11-08T20:00:00Z"),
                        "GAIA_FOREST_WATER",
                        "0.3.0",
                        " ",
                        "SYNTHETIC",
                        "none",
                        "not-applicable",
                        "not-georeferenced",
                        "1 calendar day",
                        "REJECT_INCOMPLETE_ROWS",
                        Map.of(),
                        Map.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> sampleManifest(Map.of(" ", "bad"), Map.of()));
    }

    private static GaiaRunManifest sampleManifest(
            Map<String, String> parameters,
            Map<String, String> initialState) {
        return new GaiaRunManifest(
                "gaia-test-run-001",
                Instant.parse("2026-11-08T20:00:00Z"),
                "GAIA_FOREST_WATER",
                "0.3.0",
                "SYNTHETIC-UNPINNED",
                "SYNTHETIC_DEMO",
                "none",
                "not-applicable-synthetic-data",
                "not-georeferenced",
                "1 calendar day",
                "reject incomplete daily rows; never assume missing means zero",
                parameters,
                initialState);
    }

    private static void expectThrows(
            Class<? extends RuntimeException> type,
            Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected " + type.getSimpleName() + ".");
        } catch (RuntimeException actual) {
            if (!type.isInstance(actual)) {
                throw actual;
            }
        }
    }
}
