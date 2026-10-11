package matrixgame.core;

import java.util.Locale;

/** Ergebnis eines Experiments inkl. PAL-Einordnung. */
public record Result(String name, Claim hypothesis, String metric, String labelA, String labelB,
                     int n, double meanA, double sdA, double meanB, double sdB, double t, Verdict verdict) {

    public enum Verdict { SUPPORTED, REFUTED, INCONCLUSIVE }

    /** SUPPORTED gilt nur im Modell (MODEL), nie automatisch als FACT. */
    public Pal resultingLevel() {
        return verdict == Verdict.SUPPORTED ? Pal.MODEL : Pal.HYPOTHESIS;
    }

    public String toMarkdown() {
        return String.format(Locale.ROOT,
            "### %s%n%n- Hypothese %s: %s%n- Metrik: `%s`, n = %d Seeds%n"
          + "- %s: %.2f (sd %.2f)%n- %s: %.2f (sd %.2f)%n- Welch-t = %.2f%n"
          + "- **Urteil: %s** -> PAL %s (gilt nur in diesem Modell)%n",
            name, hypothesis.tag(), hypothesis.text(), metric, n,
            labelA, meanA, sdA, labelB, meanB, sdB, t, verdict, resultingLevel());
    }
}
