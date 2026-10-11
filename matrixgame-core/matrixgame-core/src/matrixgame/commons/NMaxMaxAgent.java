package matrixgame.commons;

import matrixgame.core.*;
import java.util.Locale;

/**
 * N-Max-Max: Jeder Agent maximiert seinen eigenen Nutzen, aber der Nutzen enthaelt
 * kurzfristigen Ertrag + Beitrag zur kollektiven Regeneration + Resilienz - Risiko.
 * Kein Zero-Sum-Minimax; die Gewichte sind Parameter, keine Naturkonstanten.
 */
public final class NMaxMaxAgent implements Agent {
    public record Weights(double collective, double resilience, double risk, double horizon) {
        public static Weights defaults() { return new Weights(1.0, 5.0, 2.0, 10.0); }
    }

    private final String id;
    private final Weights w;
    public NMaxMaxAgent(String id) { this(id, Weights.defaults()); }
    public NMaxMaxAgent(String id, Weights w) { this.id = id; this.w = w; }
    @Override public String id() { return id; }

    @Override
    public Action decide(Observation o) {
        double quota = o.growthRate() * o.capacity() / 4.0 / o.alive();
        double perOther = o.tick() == 1 ? quota : o.lastAvgHarvest();   // keine Info in Tick 1 -> fairer Anteil
        double others = (o.alive() - 1) * perOther;
        double best = 0, bestU = Double.NEGATIVE_INFINITY;
        for (double h = 0; h <= o.maxHarvest() + 1e-9; h += 0.5) {
            double u = utility(o, h, others);
            if (u > bestU + 1e-12) { bestU = u; best = h; }
        }
        return new Action(best, String.format(Locale.ROOT, "H=%.1f", best));
    }

    double utility(Observation o, double h, double others) {
        double sAfter = Math.max(0, o.stock() - h - others);
        double growth = o.growthRate() * sAfter * (1 - sAfter / o.capacity());
        double shortTerm = h;
        double collective = w.collective() * w.horizon() * growth;                       // Regeneration fuer alle
        double resilience = w.resilience() * Math.min(sAfter, 0.5 * o.capacity()) / o.alive(); // Puffer
        double stockRisk = w.risk() * Math.max(0, 0.25 * o.capacity() - sAfter);          // Kollapszone
        double selfRisk = w.risk() * Math.max(0, 2 * o.upkeep() - (o.energy() + h - o.upkeep())); // Verhungern
        return shortTerm + collective + resilience - stockRisk - selfRisk;
    }
}
