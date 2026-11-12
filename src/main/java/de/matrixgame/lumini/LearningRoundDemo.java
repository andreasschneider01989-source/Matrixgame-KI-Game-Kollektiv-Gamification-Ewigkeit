package de.matrixgame.lumini;
import java.util.Arrays;
/** Console entry point for a small, explainable Matrixgame learning round. */
public final class LearningRoundDemo {
    private LearningRoundDemo() { }
    public static void main(String[] args) {
        boolean jsonOnly = args.length > 0 && "--jsonl-only".equals(args[0]);
        String input = args.length > (jsonOnly ? 1 : 0)
                ? String.join(" ", Arrays.copyOfRange(args, jsonOnly ? 1 : 0, args.length))
                : "Matrixgame: Lernen, Java programmieren, Compiler-Filter testen und gemeinsam simulieren.";
        PipelineResult result = new IdeaCompilerPipeline().process(input, "interactive-cli");
        if (jsonOnly) {
            System.out.println(result.toJsonLine());
            return;
        }
        System.out.println("=== MATRIXGAME / LUMINI — LEARNING ROUND 001 ===");
        System.out.println(result.toHumanReport());
        System.out.println("\n[QUEST 1] Prüfe den Originaltext und bestätige oder ändere die vorgeschlagene Route.");
        System.out.println("[QUEST 2] Ordne mindestens eine Aussage bewusst als FACT, MODEL, HYPOTHESIS, OPEN oder FICTION ein.");
        System.out.println("[SUCCESS] Intake und Trace erzeugt; automatische Wahrheits- und Kausalbehauptungen vermieden.");
        System.out.println("\n--- JSONL (one record) ---");
        System.out.println(result.toJsonLine());
    }
}
