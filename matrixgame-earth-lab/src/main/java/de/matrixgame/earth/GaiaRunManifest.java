package de.matrixgame.earth;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Immutable provenance record for a reproducible Gaia simulation run.
 *
 * <p>The JSON representation is deliberately dependency-free and uses sorted
 * parameter maps for stable output. A manifest documents a run; it does not
 * independently authenticate a dataset, licence, or Git revision.</p>
 */
public record GaiaRunManifest(
        String runId,
        Instant createdAt,
        String modelId,
        String modelVersion,
        String codeRevision,
        String datasetId,
        String datasetUrl,
        String datasetLicense,
        String spatialScope,
        String temporalResolution,
        String missingDataPolicy,
        Map<String, String> modelParameters,
        Map<String, String> initialState) {

    public GaiaRunManifest {
        requireText(runId, "runId");
        Objects.requireNonNull(createdAt, "createdAt");
        requireText(modelId, "modelId");
        requireText(modelVersion, "modelVersion");
        requireText(codeRevision, "codeRevision");
        if (!codeRevision.matches("(?i)[0-9a-f]{7,40}")
                && !"SYNTHETIC-UNPINNED".equals(codeRevision)) {
            throw new IllegalArgumentException(
                    "codeRevision must be a 7-40 digit Git commit SHA; "
                            + "use SYNTHETIC-UNPINNED only for synthetic test runs.");
        }
        requireText(datasetId, "datasetId");
        requireText(datasetUrl, "datasetUrl");
        requireText(datasetLicense, "datasetLicense");
        requireText(spatialScope, "spatialScope");
        requireText(temporalResolution, "temporalResolution");
        requireText(missingDataPolicy, "missingDataPolicy");
        modelParameters = immutableSortedCopy(modelParameters, "modelParameters");
        initialState = immutableSortedCopy(initialState, "initialState");
    }

    /**
     * Returns a stable JSON document. Values are strings to preserve the
     * original human-readable units and avoid implicit numeric conversions.
     */
    public String toJson() {
        StringBuilder json = new StringBuilder(512);
        json.append("{\n");
        appendField(json, "runId", runId, true);
        appendField(json, "createdAt", createdAt.toString(), true);
        appendField(json, "modelId", modelId, true);
        appendField(json, "modelVersion", modelVersion, true);
        appendField(json, "codeRevision", codeRevision, true);
        appendField(json, "datasetId", datasetId, true);
        appendField(json, "datasetUrl", datasetUrl, true);
        appendField(json, "datasetLicense", datasetLicense, true);
        appendField(json, "spatialScope", spatialScope, true);
        appendField(json, "temporalResolution", temporalResolution, true);
        appendField(json, "missingDataPolicy", missingDataPolicy, true);
        json.append("  \"modelParameters\": ");
        appendMap(json, modelParameters);
        json.append(",\n  \"initialState\": ");
        appendMap(json, initialState);
        json.append("\n}");
        return json.toString();
    }

    private static void appendField(
            StringBuilder json,
            String name,
            String value,
            boolean comma) {
        json.append("  \"").append(escape(name)).append("\": \"")
                .append(escape(value)).append("\"");
        if (comma) {
            json.append(',');
        }
        json.append('\n');
    }

    private static void appendMap(
            StringBuilder json,
            Map<String, String> values) {
        json.append('{');
        if (!values.isEmpty()) {
            json.append('\n');
            int index = 0;
            for (Map.Entry<String, String> entry : values.entrySet()) {
                json.append("    \"").append(escape(entry.getKey()))
                        .append("\": \"").append(escape(entry.getValue()))
                        .append('\"');
                if (index < values.size() - 1) {
                    json.append(',');
                }
                json.append('\n');
                index++;
            }
            json.append("  ");
        }
        json.append('}');
    }

    private static Map<String, String> immutableSortedCopy(
            Map<String, String> source,
            String name) {
        Objects.requireNonNull(source, name);
        TreeMap<String, String> copy = new TreeMap<>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String key = requireText(entry.getKey(), name + " key");
            String value = Objects.requireNonNull(
                    entry.getValue(), name + " value for " + key);
            copy.put(key, value);
        }
        return Collections.unmodifiableMap(copy);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank.");
        }
        return value;
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
