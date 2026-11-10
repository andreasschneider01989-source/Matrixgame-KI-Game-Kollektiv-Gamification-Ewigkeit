package de.matrixgame.language;

/** Dependency-free smoke and negative tests for the LUMINI MVP. */
public final class LuminiCompilerSelfTest {
    private static int failed;
    private LuminiCompilerSelfTest() {}

    public static void main(String[] args) {
        testDensityModel();
        testDeterministicOutput();
        testIncompatibleAdditionRejected();
        testWrongAssertionRejected();
        testUnknownNameRejected();
        testUnsupportedUnitRejected();
        if (failed > 0) {
            System.err.println(failed + " LUMINI test(s) failed.");
            System.exit(1);
        }
        System.out.println("All LUMINI self-tests passed.");
    }

    private static void testDensityModel() {
        String source = """
                project "density-demo" {
                  claim_status = MODEL;
                  parameter mass = 10 kg;
                  parameter volume = 2 m^3;
                  derive density = mass / volume;
                  assert_unit density = kg / m^3;
                }
                """;
        LuminiCompiler.CompilationResult result = LuminiCompiler.compile(source);
        check("density-demo".equals(result.modelId()), "project ID is preserved");
        check(result.declarations().size() == 3, "three declarations are emitted");
        check(result.declarations().get(2).dimension().equals(
                new LuminiCompiler.Dimension(1, -3, 0)), "density has dimension kg/m^3");
        check(result.toJson().contains("\"epistemic_status\": \"MODEL\""),
                "epistemic status is present in Model IR");
        check(result.toJson().contains("\"expression\": \"(mass / volume)\""),
                "derived expression is serialized");
    }

    private static void testDeterministicOutput() {
        String source = """
                project "repeatable" {
                  claim_status = MODEL;
                  parameter mass = 4 kg;
                  parameter volume = 2 m^3;
                  derive density = mass / volume;
                }
                """;
        String first = LuminiCompiler.compile(source).toJson();
        String second = LuminiCompiler.compile(source).toJson();
        check(first.equals(second), "same source produces identical normalized IR");
    }

    private static void testIncompatibleAdditionRejected() {
        String source = """
                project "bad-addition" {
                  claim_status = MODEL;
                  parameter mass = 5 kg;
                  parameter distance = 3 m;
                  derive invalid = mass + distance;
                }
                """;
        expectFailure(source, "incompatible dimensions", "kg and m cannot be added");
    }

    private static void testWrongAssertionRejected() {
        String source = """
                project "bad-assertion" {
                  claim_status = MODEL;
                  parameter mass = 10 kg;
                  parameter volume = 2 m^3;
                  derive density = mass / volume;
                  assert_unit density = kg / m^2;
                }
                """;
        expectFailure(source, "Unit mismatch", "incorrect derived unit is rejected");
    }

    private static void testUnknownNameRejected() {
        String source = """
                project "unknown-name" {
                  claim_status = MODEL;
                  parameter mass = 10 kg;
                  derive force = mass * acceleration;
                }
                """;
        expectFailure(source, "Unknown variable", "undeclared variables are rejected");
    }

    private static void testUnsupportedUnitRejected() {
        String source = """
                project "unsupported-unit" {
                  claim_status = MODEL;
                  parameter mass = 10 pounds;
                }
                """;
        expectFailure(source, "Unsupported unit", "unknown units are rejected explicitly");
    }

    private static void expectFailure(String source, String expectedMessage, String description) {
        try {
            LuminiCompiler.compile(source);
            check(false, description);
        } catch (LuminiCompiler.CompilerException exception) {
            check(exception.getMessage().contains(expectedMessage), description);
        }
    }

    private static void check(boolean condition, String description) {
        if (condition) System.out.println("PASS " + description);
        else {
            failed++;
            System.err.println("FAIL " + description);
        }
    }
}
