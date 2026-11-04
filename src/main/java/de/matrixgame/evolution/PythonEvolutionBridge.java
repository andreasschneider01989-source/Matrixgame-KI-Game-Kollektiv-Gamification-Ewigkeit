package de.matrixgame.evolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Starts Evolution Forge as an isolated Python process.
 *
 * No shell is used: arguments are passed directly to ProcessBuilder.
 * The Python script path and Python executable are supplied by the caller.
 */
public final class PythonEvolutionBridge {
    private final String pythonExecutable;
    private final Path simulatorScript;
    private final Duration timeout;

    public PythonEvolutionBridge(String pythonExecutable, Path simulatorScript) {
        this(pythonExecutable, simulatorScript, Duration.ofMinutes(5));
    }

    public PythonEvolutionBridge(String pythonExecutable, Path simulatorScript, Duration timeout) {
        if (pythonExecutable == null || pythonExecutable.trim().isEmpty()) {
            throw new IllegalArgumentException("pythonExecutable must not be blank");
        }
        if (simulatorScript == null) {
            throw new IllegalArgumentException("simulatorScript must not be null");
        }
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        this.pythonExecutable = pythonExecutable;
        this.simulatorScript = simulatorScript.toAbsolutePath().normalize();
        this.timeout = timeout;
    }

    public EvolutionRun run(EvolutionRequest request, Path outputRoot)
            throws IOException, InterruptedException {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (!Files.isRegularFile(simulatorScript)) {
            throw new IOException("Evolution Forge script not found: " + simulatorScript);
        }

        Path root = outputRoot.toAbsolutePath().normalize();
        Files.createDirectories(root);

        String runName = "run-" + Instant.now().toString().replace(':', '-')
                + "-seed-" + request.getSeed();
        Path runDirectory = root.resolve(runName);
        Files.createDirectories(runDirectory);

        Path requestFile = runDirectory.resolve("request.json");
        Path logFile = runDirectory.resolve("process.log");
        Path summaryFile = runDirectory.resolve("summary.json");

        Files.write(requestFile, request.toJson().getBytes(StandardCharsets.UTF_8));

        ProcessBuilder builder = new ProcessBuilder(
                pythonExecutable,
                simulatorScript.toString(),
                "--config", requestFile.toString(),
                "--out", runDirectory.toString()
        );
        builder.directory(simulatorScript.getParent().toFile());
        builder.redirectErrorStream(true);
        builder.redirectOutput(logFile.toFile());

        Process process = builder.start();
        boolean completed = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (!completed) {
            process.destroyForcibly();
            process.waitFor();
            throw new IOException("Evolution Forge timed out after " + timeout);
        }

        int exitCode = process.exitValue();
        String summaryJson = Files.isRegularFile(summaryFile)
                ? new String(Files.readAllBytes(summaryFile), StandardCharsets.UTF_8)
                : "";

        return new EvolutionRun(exitCode, runDirectory, logFile, summaryJson);
    }
}
