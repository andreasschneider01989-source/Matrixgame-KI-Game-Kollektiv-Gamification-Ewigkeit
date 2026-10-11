package matrixgame.commons;

import matrixgame.core.*;

import java.util.*;

/**
 * Gemeinsamer Bestand, n Agents. Jeder Tick: wahrnehmen -> entscheiden -> entnehmen ->
 * Energie verbuchen -> Bestand waechst nach (mit Rauschen) -> Rueckmeldung.
 * Ist die Nachfrage groesser als der Bestand, wird proportional gekuerzt.
 */
public final class CommonsWorld implements World {
    private final CommonsConfig cfg;
    private final Random rng;
    private final Resource stock;
    private final List<Agent> agents;
    private final double[] energy, welfare;
    private final boolean[] alive;
    private final int[] lifetime;
    private int tick = 0;
    private double lastAvg = 0;
    private List<Message> inbox = List.of();
    private final List<Message> log = new ArrayList<>();

    public CommonsWorld(CommonsConfig cfg, List<Agent> agents, long seed) {
        this.cfg = cfg;
        this.agents = List.copyOf(agents);
        this.rng = new Random(seed);
        this.stock = new Resource("stock", cfg.capacity() * cfg.initialStockFraction(),
                                  cfg.capacity(), cfg.growthRate());
        int n = agents.size();
        energy = new double[n]; welfare = new double[n]; alive = new boolean[n]; lifetime = new int[n];
        Arrays.fill(energy, cfg.startEnergy());
        Arrays.fill(alive, true);
    }

    private int aliveCount() { int c = 0; for (boolean a : alive) if (a) c++; return c; }

    @Override public int tick() { return tick; }
    @Override public boolean finished() { return tick >= cfg.maxTicks() || aliveCount() == 0; }
    public double stock() { return stock.amount(); }
    public List<Message> messageLog() { return Collections.unmodifiableList(log); }

    @Override
    public void step() {
        if (finished()) return;
        tick++;
        int aliveBefore = aliveCount();
        double[] want = new double[agents.size()];
        List<Message> next = new ArrayList<>();
        double demand = 0;
        for (int i = 0; i < agents.size(); i++) {
            if (!alive[i]) continue;
            Observation obs = new Observation(tick, energy[i], cfg.upkeep(), stock.amount(),
                stock.capacity(), stock.growthRate(), aliveBefore, lastAvg, cfg.maxHarvest(), inbox);
            Action a = agents.get(i).decide(obs);
            double h = Double.isNaN(a.harvest()) ? 0 : Math.max(0, Math.min(cfg.maxHarvest(), a.harvest()));
            want[i] = h;
            demand += h;
            if (a.signal() != null) next.add(new Message(tick, agents.get(i).id(), a.signal()));
        }
        double taken = stock.take(demand);
        double scale = demand > 0 ? taken / demand : 0;
        double totalGot = 0;
        for (int i = 0; i < agents.size(); i++) {
            if (!alive[i]) continue;
            double got = want[i] * scale;
            energy[i] += got - cfg.upkeep();
            welfare[i] += got;
            totalGot += got;
            lifetime[i] = tick;
            boolean dies = energy[i] <= 0;
            if (dies) alive[i] = false;
            agents.get(i).learn(new Feedback(got, energy[i], !dies));
        }
        lastAvg = totalGot / aliveBefore;
        stock.regrow(cfg.noise() * (rng.nextDouble() * 2 - 1));
        inbox = next;
        log.addAll(next);
    }

    /** Mittlere Lebensdauer der Agents, deren id mit prefix beginnt ("" = alle). */
    public double meanLifetime(String prefix) {
        double s = 0; int c = 0;
        for (int i = 0; i < agents.size(); i++)
            if (agents.get(i).id().startsWith(prefix)) { s += lifetime[i]; c++; }
        return c == 0 ? 0 : s / c;
    }
    /** Summe der Ernte der Agents mit passendem Praefix. */
    public double welfare(String prefix) {
        double s = 0;
        for (int i = 0; i < agents.size(); i++)
            if (agents.get(i).id().startsWith(prefix)) s += welfare[i];
        return s;
    }

    @Override
    public Map<String, Double> metrics() {
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("survivors", (double) aliveCount());
        m.put("welfare", welfare(""));
        m.put("lifetime", meanLifetime(""));
        m.put("finalStock", stock.amount());
        return m;
    }
}
