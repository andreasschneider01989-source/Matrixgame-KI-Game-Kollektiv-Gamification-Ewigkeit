package matrixgame.core;

import java.util.function.Function;

/** Hypothese -> Test -> Ergebnis. Hypothese lautet immer: Mittelwert A > Mittelwert B. */
public final class Experiment {
    private Experiment() {}

    /** Grobe Daumenregel |t| > 2, kein Ersatz fuer saubere Statistik. */
    public static final double T_THRESHOLD = 2.0;

    public record Spec(String name, Claim hypothesis, String metric, String labelA, String labelB,
                       Function<Long, Double> sampleA, Function<Long, Double> sampleB) {}

    public static Result run(Spec s, int n, long baseSeed) {
        if (n < 2) throw new IllegalArgumentException("n >= 2");
        double[] a = new double[n], b = new double[n];
        for (int i = 0; i < n; i++) {
            long seed = baseSeed + i;
            a[i] = s.sampleA().apply(seed);
            b[i] = s.sampleB().apply(seed);
        }
        double mA = mean(a), mB = mean(b), sA = sd(a, mA), sB = sd(b, mB);
        double t = welch(mA, sA, mB, sB, n);
        Result.Verdict v = (mA > mB && t > T_THRESHOLD) ? Result.Verdict.SUPPORTED
                         : (mA < mB && t < -T_THRESHOLD) ? Result.Verdict.REFUTED
                         : Result.Verdict.INCONCLUSIVE;
        return new Result(s.name(), s.hypothesis(), s.metric(), s.labelA(), s.labelB(), n, mA, sA, mB, sB, t, v);
    }

    public static double mean(double[] x) {
        double s = 0; for (double v : x) s += v; return s / x.length;
    }
    public static double sd(double[] x, double m) {
        double s = 0; for (double v : x) s += (v - m) * (v - m); return Math.sqrt(s / (x.length - 1));
    }
    public static double welch(double mA, double sA, double mB, double sB, int n) {
        double se = Math.sqrt(sA * sA / n + sB * sB / n);
        if (se == 0) return mA == mB ? 0 : Math.copySign(1e9, mA - mB);
        return (mA - mB) / se;
    }
}
