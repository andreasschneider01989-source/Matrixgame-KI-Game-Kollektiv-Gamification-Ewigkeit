package matrixgame.core;

import java.util.Locale;
import java.util.Objects;

/** Eine Aussage mit PAL-Status und Konfidenz in [0,1]. */
public record Claim(String text, Pal level, double confidence) {
    public Claim {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(level, "level");
        if (!(confidence >= 0.0 && confidence <= 1.0))
            throw new IllegalArgumentException("confidence muss in [0,1] liegen: " + confidence);
    }
    public String tag() {
        return String.format(Locale.ROOT, "[%s %.0f%%]", level, confidence * 100);
    }
}
