package de.matrixgame.evolution;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * End-to-end smoke test. Arguments: Python executable, simulator script, output directory.
 * This is a plain Java main class so the adapter can be tested without adding a JSON or JUnit dependency.
 */
public final class PythonEvolutionBridgeSmokeTest {
    private PythonEvolutionBridgeSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "Usage: PythonEvolutionBridgeSmokeTest <python> <script> <output-directory>");
        }

        String python = args[0];
        Path script = Paths.get(args[1]);
        Path output = Paths.get(args[2]);

        PythonEvolutionBridge bridge = new PythonEvolutionBridge(python, script);
        EvolutionRun result = bridge.run(
                new EvolutionRequest(EvolutionScenario.ICY_OCEAN, 30, 7, 0.65, null, 0.015, 0.08),
                output);

        if (!result.isSuccessful()) {
            throw new IllegalStateException(
                    "Evolution Forge failed with exit code " + result.getExitCode()
                            + "; log: " + result.getProcessLog());
        }
        String summary = result.getSummaryJson();
        if (!summary.contains("\"model_version\": \"1.0.1\"")
                || !summary.contains("\"scenario\": \"icy_ocean\"")
                || !summary.contains("\"energy_flux\": 0.65")
                || !summary.contains("\"maintenance_cost\": 0.015")
                || !summary.contains("\"cooperation_benefit\": 0.08")) {
            throw new IllegalStateException("Unexpected or incomplete summary.json");
        }
        System.out.println("PASS: Java -> Python -> JSON summary with icy_ocean overrides");
        System.out.println("Output: " + result.getOutputDirectory());
    }
}
