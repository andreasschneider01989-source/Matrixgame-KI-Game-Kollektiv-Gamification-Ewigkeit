package sim;

/**
 * Deterministischer Zufall (xorshift) - Reproduzierbarkeit fuer Evolution/Debug.
 *
 * WILDFAKTOR-Gamifizierung: wild() erzeugt Multiplikatoren der Form r1/r2
 * (uniform/uniform). Median = 1 -> oekonomische Mittelwerte bleiben erhalten,
 * aber einzelne Ereignisse koennen EXTREM ausfallen (unbegrenzte Variation).
 * Der Grundwert (z.B. Energiemenge) bleibt im Medium derselbe - die Verteilung
 * wird nur "wild". Genau das r1/r2-Prinzip aus der Vision.
 */
public final class Rng {
    private long s;
    public Rng(long seed) { this.s = seed == 0 ? 0x9E3779B97F4A7C15L : seed; }

    public long nextLong() {
        long x = s; s ^= s << 13; s ^= s >>> 7; s ^= s << 17; return x;
    }
    public int nextInt(int bound) {
        return (int) Long.remainderUnsigned(nextLong() >>> 1, bound);
    }
    public double nextDouble() { return (nextLong() >>> 11) * 0x1.0p-53; }
    public boolean chance(double p) { return nextDouble() < p; }
    public double nextGaussianLike() { return (nextDouble() + nextDouble() + nextDouble()) * 2 - 3; }

    /**
     * r1/r2-Multiplikator, logarithmisch symmetrisch um 1, auf maxFaktor begrenzt.
     * wild(4) liefert Werte in [0.25, 4], Median 1 -> Erwartung der Basis bleibt.
     */
    public double wild(double maxFaktor) {
        double r1 = nextDouble(), r2 = nextDouble();
        double raw = r1 / r2;
        return Math.max(1.0 / maxFaktor, Math.min(maxFaktor, raw));
    }

    /** Wilder Faktor mit einstellbarer Staerke (0 = aus, 1 = voll Flamboyant). */
    public double wild(double strength, double maxFaktor) {
        if (strength <= 0) return 1.0;
        double f = wild(maxFaktor);
        return Math.pow(f, strength); // Zwischen 1 und maxFaktor interpolieren (log-Skala)
    }
}
