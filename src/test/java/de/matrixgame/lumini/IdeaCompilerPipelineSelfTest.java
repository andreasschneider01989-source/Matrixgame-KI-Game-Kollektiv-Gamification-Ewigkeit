package de.matrixgame.lumini;
import java.time.Instant;
import java.util.Arrays;
/** Dependency-free smoke/regression tests; run with -ea or via the provided CI workflow. */
public final class IdeaCompilerPipelineSelfTest {
    private IdeaCompilerPipelineSelfTest() { }
    public static void main(String[] args) {
        testOriginalTextIsPreserved();
        testRoutesAreHintsNotDecisions();
        testClaimsAreNotPromotedToFacts();
        testJsonEscaping();
        testBlankInputIsRejected();
        System.out.println("LUMINI MVP self-tests: 5/5 PASS");
    }
    private static void testOriginalTextIsPreserved() {
        String source = "  Matrixgame lernen\nund Java testen.  ";
        PipelineResult result = new IdeaCompilerPipeline().process(source, "self-test");
        check(source.equals(result.getPacket().getOriginalText()), "original text changed");
        check(result.getTrace().get(0).getStatus() == FilterStatus.CAPTURED, "intake status wrong");
    }
    private static void testRoutesAreHintsNotDecisions() {
        PipelineResult result = new IdeaCompilerPipeline().process(
                "Java programmieren und Lernen im Matrixgame", "self-test");
        check(result.getSuggestedRoutes().containsAll(Arrays.asList(
                PipelineRoute.CODE_MODULE, PipelineRoute.MICRO_QUEST, PipelineRoute.SIMULATION)),
                "expected route hints missing");
        check(result.getTrace().get(2).getStatus() == FilterStatus.REVIEW_REQUIRED,
                "heuristic intent must require review");
    }
    private static void testClaimsAreNotPromotedToFacts() {
        PipelineResult result = new IdeaCompilerPipeline().process(
                "Die Idee ist garantiert richtig.", "self-test");
        check(result.getPacket().getEpistemicStatus() == EpistemicStatus.OPEN,
                "unreviewed input must remain OPEN");
        check(result.getTrace().get(3).getStatus() == FilterStatus.NOT_RUN,
                "epistemic classifier must not pretend to have run");
        check(result.getTrace().get(4).getStatus() == FilterStatus.NOT_RUN,
                "causal engine must not pretend to have run");
    }
    private static void testJsonEscaping() {
        String source = "quote=\" slash=\\ newline=\n tab=\t";
        IdeaPacket packet = new IdeaPacket("id-1", source, "self-test", Instant.EPOCH,
                EpistemicStatus.OPEN, "PRIVATE");
        PipelineResult result = new IdeaCompilerPipeline().process(source, "self-test");
        String json = result.toJsonLine();
        check(json.contains("\\\""), "quote was not escaped");
        check(json.contains("\\\\"), "backslash was not escaped");
        check(json.contains("\\n"), "newline was not escaped");
        check(!json.contains("\n"), "JSONL must be exactly one physical line");
        check(packet.getCreatedAt().equals(Instant.EPOCH), "explicit timestamp not preserved");
    }
    private static void testBlankInputIsRejected() {
        boolean rejected = false;
        try {
            new IdeaCompilerPipeline().process("  \n  ", "self-test");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "blank input must be rejected");
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
