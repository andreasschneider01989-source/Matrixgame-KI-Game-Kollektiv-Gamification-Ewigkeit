package sim;

/** Ein Lebewesen: Position, Team, Farbe, Energie, Gehirn, Gene, Fitness. */
public final class Unit {
    public int x, y;
    public int team;
    public int color;
    public double energy = 1.0;
    public int age = 0;
    public long generation;
    public Brain brain;

    // Gene (vererbbar, mutierbar, wild-varierbar)
    public double geneMetab = 1.0;   // Stoffwechsel-Multiplikator (sparsam vs. verschwenderisch)
    public boolean geneSpeed = false; // Schnell-Gen: 2 Zuege/Tick, dafuer doppelter Verbrauch

    // Fitness / Statistik
    public long foodEaten = 0, kills = 0, exitsReached = 0, children = 0;
    public double dirtDug = 0;

    public Unit(int x, int y, int team, int color, Brain brain, long gen) {
        this.x = x; this.y = y; this.team = team; this.color = color;
        this.brain = brain; this.generation = gen;
    }

    public double fitness() {
        return age * 0.02 + foodEaten * 5 + kills * 8 + dirtDug * 3 + exitsReached * 50 + children * 4;
    }

    /** Farbe aus Gehirn + Genen -> man SIEHT Evolution und Stoffwechsel-Typen. */
    public int displayColor() {
        double h = 0;
        for (double w : brain.w2) h += w;
        h = (h * 37.0 + team * 137.508) % 360.0;
        h = (h + (geneMetab - 1) * 80.0) % 360.0;
        if (h < 0) h += 360;
        float sat = geneSpeed ? 1.0f : 0.7f;
        int brightness = 140 + (int) (Math.min(1, energy) * 100);
        return java.awt.Color.HSBtoRGB((float) (h / 360.0), sat, brightness / 255f);
    }
}
