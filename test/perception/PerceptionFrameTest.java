package perception;

/** Dependency-free regression suite. Run with: java -ea perception.PerceptionFrameTest */
public final class PerceptionFrameTest {
    private static int passed;
    private PerceptionFrameTest() {}

    public static void main(String[] args) {
        // 1–7: all protocol modalities.
        for (String modality : new String[]{"VISUAL", "AUDITORY", "SPATIAL", "THERMAL", "CHEMICAL", "INTERNAL", "SOCIAL"}) {
            check("valid modality " + modality, () -> new PerceptionFrame("agent", modality, 0, "sample", 0.5, "sensor"));
        }
        // 8–17: invalid frame fields and valid zero timestamp.
        rejects("null agent", () -> frame(null, "VISUAL", 0, "x", 0.5, "s"));
        rejects("blank agent", () -> frame(" ", "VISUAL", 0, "x", 0.5, "s"));
        rejects("null modality", () -> frame("a", null, 0, "x", 0.5, "s"));
        rejects("blank modality", () -> frame("a", " ", 0, "x", 0.5, "s"));
        rejects("unknown modality", () -> frame("a", "MAGIC", 0, "x", 0.5, "s"));
        rejects("negative timestamp", () -> frame("a", "VISUAL", -1, "x", 0.5, "s"));
        rejects("null observation", () -> frame("a", "VISUAL", 0, null, 0.5, "s"));
        rejects("null source", () -> frame("a", "VISUAL", 0, "x", 0.5, null));
        rejects("blank source", () -> frame("a", "VISUAL", 0, "x", 0.5, " "));
        check("zero timestamp accepted", () -> frame("a", "VISUAL", 0, "x", 0.5, "s"));
        // 18–24: confidence boundaries and invalid values.
        check("confidence zero", () -> frame("a", "VISUAL", 1, "x", 0.0, "s"));
        check("confidence one", () -> frame("a", "VISUAL", 1, "x", 1.0, "s"));
        rejects("confidence below zero", () -> frame("a", "VISUAL", 1, "x", -0.01, "s"));
        rejects("confidence above one", () -> frame("a", "VISUAL", 1, "x", 1.01, "s"));
        rejects("confidence NaN", () -> frame("a", "VISUAL", 1, "x", Double.NaN, "s"));
        rejects("confidence positive infinity", () -> frame("a", "VISUAL", 1, "x", Double.POSITIVE_INFINITY, "s"));
        rejects("confidence negative infinity", () -> frame("a", "VISUAL", 1, "x", Double.NEGATIVE_INFINITY, "s"));
        // 25–32: interpretation validation.
        check("interpretation class A", () -> interpretation("A", 0.5));
        check("interpretation class B", () -> interpretation("B", 0.5));
        check("interpretation class C", () -> interpretation("C", 0.5));
        check("interpretation class D", () -> interpretation("D", 0.5));
        check("interpretation class E", () -> interpretation("E", 0.5));
        rejects("invalid evidence class", () -> interpretation("F", 0.5));
        rejects("null evidence class", () -> interpretation(null, 0.5));
        rejects("interpretation confidence NaN", () -> interpretation("C", Double.NaN));
        // 33–36: interpretation gate and immutable modality registry.
        var valid = interpretation("C", 0.55);
        check("interpretation accepted with measurement", () -> {
            if (PerceptionFrame.interpret(valid, true) != valid) throw new AssertionError("identity changed");
        });
        rejectsState("interpretation rejected without measurement", () -> PerceptionFrame.interpret(valid, false));
        rejects("null interpretation rejected", () -> PerceptionFrame.interpret(null, true));
        rejectsUnsupported("modality set immutable", () -> PerceptionFrame.supportedModalities().add("MAGIC"));
        System.out.println("PERCEPTION v0.2: " + passed + "/36 checks passed.");
        if (passed != 36) throw new AssertionError("Expected 36 checks, got " + passed);
    }

    private static PerceptionFrame frame(String a, String m, long t, Object o, double c, String s) {
        return new PerceptionFrame(a, m, t, o, c, s);
    }
    private static PerceptionFrame.Interpretation interpretation(String e, double c) {
        return new PerceptionFrame.Interpretation("research-agent", "possible living entity", c, e);
    }
    private static void check(String name, Runnable action) {
        action.run(); passed++; System.out.println("[PASS] " + name);
    }
    private static void rejects(String name, Runnable action) {
        try { action.run(); throw new AssertionError("Expected IllegalArgumentException: " + name); }
        catch (IllegalArgumentException expected) { passed++; System.out.println("[PASS] " + name); }
    }
    private static void rejectsUnsupported(String name, Runnable action) {
        try { action.run(); throw new AssertionError("Expected UnsupportedOperationException: " + name); }
        catch (UnsupportedOperationException expected) { passed++; System.out.println("[PASS] " + name); }
    }
    private static void rejectsState(String name, Runnable action) {
        try { action.run(); throw new AssertionError("Expected IllegalStateException: " + name); }
        catch (IllegalStateException expected) { passed++; System.out.println("[PASS] " + name); }
    }
}
