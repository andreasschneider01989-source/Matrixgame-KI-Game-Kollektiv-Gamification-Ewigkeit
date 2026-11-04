package de.matrixgame.evolution;

import java.nio.file.Path;

/** Result metadata for a completed or failed external Python simulation run. */
public final class EvolutionRun {
    private final int exitCode;
    private final Path outputDirectory;
    private final Path processLog;
    private final String summaryJson;

    public EvolutionRun(int exitCode, Path outputDirectory, Path processLog, String summaryJson) {
        this.exitCode = exitCode;
        this.outputDirectory = outputDirectory;
        this.processLog = processLog;
        this.summaryJson = summaryJson;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isSuccessful() {
        return exitCode == 0 && summaryJson != null && !summaryJson.isEmpty();
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public Path getProcessLog() {
        return processLog;
    }

    /** Raw JSON is retained until a versioned result schema is introduced. */
    public String getSummaryJson() {
        return summaryJson;
    }
}
