package sim;

import editor.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Die Welt v2: 2D-Grid + Units + Tick-Loop + Evolution + PHYSIK + OEKOSYSTEM.
 *
 * Erhaltungs-Axiome (simuliert, in stats() nachpruefbar):
 *  - Massen-/Energieerhaltung: Gesamtenergie = Summe(Unit-Energie) + Biomasse
 *    + Nahrungswert + Boulder potentielle/kinetische Energie + Restwaerme.
 *    Nichts entsteht, nichts verschwindet - es wechselt nur die Form.
 *  - Newton: Boulder haben Masse, fallen mit g (Impuls v steigt), Stoss uebertraegt
 *    Energie (0.5*m*v^2) auf Treffer -> Kreislauf final: PE -> KE -> Warme/Biomasse.
 *  - Oekonomie geschlossen: Stoffwechsel -> Biomasse; Tod -> Biomasse;
 *    Nahrung entsteht NUR aus Biomasse-Pool.
 *
 * Kommunikation: Pheromon-Feld (Scent) - Units hinterlassen Spuren, nehmen sie
 * wahr und Gruenden so Schwarme, Futter-Strassen und Aufgabenteilung.
 *
 * Wildfaktor: Genetik mutiert mit r/r-Multiplikatoren (Rng.wild) - medianstabil,
 * aber mit unbegrenzt wirkender Variation.
 */
public final class World {
    public final Config cfg;
    public final Rng rng;
    public Tile[] tiles;
    public final List<Unit> units = new ArrayList<>();
    public long tick = 0;
    public long nextGen = 1;

    // Pheromon-Feld (Kommunikation / Schwarm)
    public final double[] scent;

    // Oekosystem-Pools (Erhaltung!)
    public double biomass = 60;        // Rohstoff-Pool, aus dem Nahrung entsteht
    public double heat = 0;            // zerstreute Energie aus Stossen/Umwegverluste

    // Physik: Boulder-Geschindigkeiten (Index -> vy)
    private final Map<Integer, Double> boulderVy = new HashMap<>();
    private static final double BOULDER_MASS = 1.0;

    private final List<double[][]> hallOfFame = new ArrayList<>();
    public static final int HALL_MAX = 24;

    private final double[] visionBuf = new double[Brain.RAYS * Brain.SENSORS];
    private double lastAutoLog = 0;

    public World(Config cfg) {
        this.cfg = cfg;
        this.rng = new Rng(cfg.seed);
        this.scent = new double[cfg.width * cfg.height];
    }

    // ---------------- Aufbau ----------------

    public void build() {
        tiles = new Tile[cfg.width * cfg.height];
        java.util.Arrays.fill(tiles, Tile.EMPTY);
        java.util.Arrays.fill(scent, 0);
        biomass = 60; heat = 0;
        boulderVy.clear();
        for (int x = 0; x < cfg.width; x++) { set(x, 0, Tile.WALL); set(x, cfg.height - 1, Tile.WALL); }
        for (int y = 0; y < cfg.height; y++) { set(0, y, Tile.WALL); set(cfg.width - 1, y, Tile.WALL); }

        if (cfg.mode == 1) buildBoulderDash(); else buildFight();

        int teams = cfg.mode == 0 ? 4 : 2;
        for (int i = 0; i < cfg.startUnits; i++) spawnRandom(i % teams);
    }

    private void buildFight() {
        for (int i = 0; i < cfg.width * cfg.height / 8; i++) {
            int x = 2 + rng.nextInt(cfg.width - 4), y = 2 + rng.nextInt(cfg.height - 4);
            set(x, y, Tile.WALL);
        }
        scatterFood(60);
    }

    private void buildBoulderDash() {
        for (int x = 1; x < cfg.width - 1; x++)
            for (int y = 1; y < cfg.height - 1; y++) {
                double r = rng.nextDouble();
                if (r < 0.28) set(x, y, Tile.DIRT);
                else if (r < 0.40) set(x, y, Tile.BOULDER);
            }
        for (int x = 1; x < cfg.width - 1; x++) set(x, 1, Tile.EMPTY);
        set(cfg.width / 2, cfg.height - 2, Tile.EXIT);
        set(cfg.width / 2, cfg.height - 3, Tile.EMPTY);
        scatterFood(30);
    }

    private void scatterFood(int n) {
        for (int i = 0; i < n; i++) {
            int x = 1 + rng.nextInt(cfg.width - 2), y = 1 + rng.nextInt(cfg.height - 2);
            if (get(x, y) == Tile.EMPTY && biomass >= cfg.foodEnergy) {
                set(x, y, Tile.FOOD);
                biomass -= cfg.foodEnergy;    // Nahrung NUR aus Biomasse
            }
        }
    }

    // ---------------- Spawning / Evolution ----------------

    public void spawnRandom(int team) {
        if (biomass < 1.0) return;                       // keine Masse -> kein Leben (Erhaltung)
        for (int tries = 0; tries < 500; tries++) {
            int x = 1 + rng.nextInt(cfg.width - 2), y = 1 + rng.nextInt(cfg.height - 2);
            if (get(x, y) == Tile.EMPTY && unitAt(x, y) == null) {
                Unit u = makeUnit(x, y, team, new Brain(rng), 1);
                double e = Math.min(cfg.startEnergy, biomass * 0.5);
                if (e < 0.2) return;
                u.energy = e; biomass -= e;               // Geburt kostet Biomasse
                units.add(u);
                return;
            }
        }
    }

    private Unit makeUnit(int x, int y, int team, Brain b, long gen) {
        Unit u = new Unit(x, y, team, teamColor(team), b, gen);
        // Gene: Wildfaktor-Variation (r/r Prinzip) - medianstabil
        u.geneMetab = Math.max(0.4, Math.min(2.5, rng.wild(cfg.wildness, 2.0)));
        u.geneSpeed = rng.chance(0.15 + cfg.wildness * 0.1);
        return u;
    }

    public Unit spawnFrom(Unit parent) {
        Rng r = rng;
        for (int radius = 1; radius <= 4; radius++) {
            for (int attempt = 0; attempt < 40; attempt++) {
                int x = parent.x + r.nextInt(2 * radius + 1) - radius;
                int y = parent.y + r.nextInt(2 * radius + 1) - radius;
                if (inBounds(x, y) && get(x, y) == Tile.EMPTY && unitAt(x, y) == null) {
                    Brain b;
                    if (hallOfFame.size() >= 2 && r.chance(0.15)) {
                        double[][] g1 = hallOfFame.get(r.nextInt(hallOfFame.size()));
                        double[][] g2 = hallOfFame.get(r.nextInt(hallOfFame.size()));
                        b = Brain.crossover(new Brain(g1[0], g1[1]), new Brain(g2[0], g2[1]), r);
                    } else {
                        b = Brain.crossover(parent.brain, parent.brain, r);
                    }
                    b.mutate(r, cfg.mutationRate);
                    Unit u = makeUnit(x, y, parent.team, b, parent.generation + 1);
                    u.geneMetab = Math.max(0.4, Math.min(2.5,
                            parent.geneMetab * r.wild(cfg.wildness, 1.5)));
                    u.geneSpeed = r.chance(0.9) ? parent.geneSpeed : !parent.geneSpeed;
                    units.add(u);
                    return u;
                }
            }
        }
        return null;
    }

    private void rememberHall(Unit u) {
        if (u.fitness() > 25 || hallOfFame.size() < HALL_MAX / 4) hallAdd(u.brain);
    }

    private boolean hallAdd(Brain b) {
        if (hallOfFame.size() < HALL_MAX) { hallOfFame.add(new double[][]{b.w1, b.w2}); return true; }
        return false;
    }

    public static int teamColor(int team) {
        int[] c = {0xFFE74C3C, 0xFF3498DB, 0xFFF1C40F, 0xFF9B59B6, 0xFF1ABC9C, 0xFFE67E22};
        return c[Math.floorMod(team, c.length)];
    }

    // ---------------- Grid-Zugriff ----------------

    public Tile get(int x, int y) { return tiles[y * cfg.width + x]; }
    public void set(int x, int y, Tile t) {
        int idx = y * cfg.width + x;
        if (tiles[idx] == Tile.BOULDER && t != Tile.BOULDER) boulderVy.remove(idx);
        tiles[idx] = t;
    }
    public boolean inBounds(int x, int y) { return x >= 0 && y >= 0 && x < cfg.width && y < cfg.height; }

    public Unit unitAt(int x, int y) {
        for (Unit u : units) if (u.x == x && u.y == y) return u;
        return null;
    }

    // ---------------- Tick ----------------

    public void tick() {
        tick++;

        // 1) Oekosystem: Nahrung entsteht NUR aus Biomasse
        if (rng.chance(cfg.foodRegrow) && countFood() < cfg.width * cfg.height / 6
                && biomass >= cfg.foodEnergy) {
            int x = 1 + rng.nextInt(cfg.width - 2), y = 1 + rng.nextInt(cfg.height - 2);
            if (get(x, y) == Tile.EMPTY && unitAt(x, y) == null) {
                set(x, y, Tile.FOOD);
                biomass -= cfg.foodEnergy;
            }
        }

        // 2) Pheromon-Verfall (Kommunikation klingt ab)
        double decay = Math.pow(1 - cfg.scentDecay, 1);
        for (int i = 0; i < scent.length; i++) if (scent[i] > 0.001) scent[i] *= decay;

        // 3) Newton-Physik: Boulder fallen mit Gravitation, Stoss-Energie Bilanz
        if (cfg.gravity) applyPhysics();

        // 4) Auto-Balance (Auto-Einstellung): haelt das Oekosystem lebendig
        if (cfg.autoBalance && tick % 200 == 0) autoBalance();

        // 5) Units handeln (mit Genen: schnell = 2 Zuege)
        List<Unit> current = new ArrayList<>(units);
        for (Unit u : current) {
            act(u);
            if (u.geneSpeed && u.energy > 0.25) act(u);
        }

        // 6) Tod -> Biomasse (Masseerhaltung)
        for (Unit u : new ArrayList<>(units)) {
            if (u.energy <= 0 || u.age > cfg.maxAge) {
                units.remove(u);
                double body = Math.max(0, u.energy);
                biomass += body * 0.9;                      // Koerper kompostiert (90% Masse ...)
                heat += body * 0.1;                         //   ... 10% Zersetzungswaerme - exakt erhalten
                scent[u.y * cfg.width + u.x] += 0.4;         // "Gefahr/AAS"-Duft
                rememberHall(u);
            }
        }

        // 7) Respawn
        int target = Math.max(8, cfg.startUnits / 2);
        int attempts = 0;
        while (units.size() < target && attempts++ < 60) {
            Unit parent = units.isEmpty() ? null : bestAlive();
            if (parent == null) { spawnRandom(rng.nextInt(4)); continue; }
            Unit child = spawnFrom(parent);
            if (child != null) {
                double e = Math.min(cfg.startEnergy, biomass * 0.5);   // Geburt aus Biomasse
                if (e >= 0.2) { child.energy = e; biomass -= e; }
                else { units.remove(child); }
            }
            else spawnRandom(parent.team);
        }

        // Erhaltungsbuch: Abweichung seit letztem Tick (muss ~0 bleiben)
        double now = totalEnergy();
        if (lastTotal >= 0) driftAccum += now - lastTotal;
        lastTotal = now;
    }

    private double lastTotal = -1;
    private double driftAccum = 0;

    /** Auto-Einstellung: regelt Nahrung+Stoffwechsel, damit die Welt nie stirbt. */
    private void autoBalance() {
        double avgFit = 0;
        for (Unit u : units) avgFit += u.fitness();
        avgFit = units.isEmpty() ? 0 : avgFit / units.size();
        boolean changed = false;
        if (units.size() < cfg.startUnits / 4 && cfg.foodRegrow < 0.5) {
            cfg.foodRegrow = Math.min(0.5, cfg.foodRegrow * 1.2); changed = true;
        }
        if (units.size() > cfg.startUnits && cfg.foodRegrow > 0.05) {
            cfg.foodRegrow = Math.max(0.05, cfg.foodRegrow * 0.9); changed = true;
        }
        if (avgFit > 40 && cfg.mutationRate < 0.2) { cfg.mutationRate *= 1.05; changed = true; }
        if (changed)
            System.out.printf("[auto] t=%d units=%d fit=%.1f -> foodRegrow=%.3f mutation=%.3f%n",
                tick, units.size(), avgFit, cfg.foodRegrow, cfg.mutationRate);
    }

    // ---------------- Newton-Physik (exakte Energiebilanz) ----------------
    // v2-Buchung: KE = 0.5*m*v2 (v2 = v^2). Pro Zeitschritt im freien Fall:
    // v2 += 2*g  ->  dKE = 0.5*m*2g = m*g = dPE.  PE-Verlust = KE-Gewinn. Exakt.
    // Aufprall: 0.5*m*v2 -> heat.   Ruhen: normal force, nichts passiert.

    private void applyPhysics() {
        double g = cfg.gravityAccel;
        for (int y = cfg.height - 2; y >= 1; y--)
            for (int x = 1; x < cfg.width - 1; x++) {
                if (get(x, y) != Tile.BOULDER) continue;
                int idx = y * cfg.width + x;
                double v2 = boulderVy.getOrDefault(idx, 0.0);
                int below = idx + cfg.width;

                Unit hit = unitAt(x, y + 1);
                if (tiles[below] == Tile.EMPTY && hit == null) {
                    // freier Fall: Bewegung um 1 Zelle, Energieuebergang PE -> KE
                    boulderVy.remove(idx);
                    tiles[idx] = Tile.EMPTY;
                    tiles[below] = Tile.BOULDER;
                    boulderVy.put(below, v2 + 2 * g);
                } else if (hit != null && v2 >= 0.5) {
                    // Stoss auf Unit: exakte Bilanz - was der Boulder an KE verliert
                    // plus was die Unit an Energie verliert, geht zu 100% in Waerme+Biomasse
                    double impact = 0.5 * BOULDER_MASS * v2;
                    double dmg = Math.min(impact, Math.max(0, hit.energy));
                    hit.energy -= dmg;
                    double diss = 0.75 * impact + dmg;  // KE-Verlust 0.5v^2 -> 0.125v^2 = 0.75*impact, plus Unit-Schaden
                    heat += diss * 0.5;
                    biomass += diss * 0.5;
                    boulderVy.put(idx, v2 * 0.25);  // Rest-Impuls, abgedaempft
                } else if (tiles[below] != Tile.EMPTY && hit == null) {
                    // Boden/Boulder/Hindernis: KE -> Waerme, dann Ruhen
                    heat += 0.5 * BOULDER_MASS * v2;
                    boulderVy.remove(idx);
                } else {
                    // Unit unter Boulder, aber zu langsam fuer Stoss: rüttelnlassen ruhen
                    boulderVy.put(idx, v2);
                }
            }
    }

    /**
     * Rein mechanische Energie: PE + KE aller Boulder.
     * WAERME gehoert NICHT hierher (sonst doppelt in totalEnergy) - der
     * Doppeltbuchungs-Bug "drift +121" war genau das.
     */
    public double physicsEnergy() {
        double e = 0;
        for (int y = 1; y < cfg.height - 1; y++)
            for (int x = 1; x < cfg.width - 1; x++)
                if (get(x, y) == Tile.BOULDER) {
                    int idx = y * cfg.width + x;
                    double pe = BOULDER_MASS * cfg.gravityAccel * (cfg.height - 2 - y);
                    double v2 = boulderVy.getOrDefault(idx, 0.0);
                    double ke = 0.5 * BOULDER_MASS * v2;      // v2 IST bereits v^2
                    e += pe + ke;
                }
        return e;
    }

    /** Gesamt-Energiebuch: Erhaltungsnachweis in stats(). */
    public double totalEnergy() {
        double e = biomass + heat + physicsEnergy();
        for (Unit u : units) e += Math.max(0, u.energy);
        for (Tile t : tiles) if (t == Tile.FOOD) e += cfg.foodEnergy;
        return e;
    }

    private int countFood() {
        int n = 0;
        for (Tile t : tiles) if (t == Tile.FOOD) n++;
        return n;
    }

    public Unit bestAlive() {
        Unit best = null;
        for (Unit u : units) if (best == null || u.fitness() > best.fitness()) best = u;
        return best;
    }

    // ---------------- Unit-Handlung ----------------

    private static final int[] DX = {0, 0, 1, 0, -1, 0, 0, 0};
    private static final int[] DY = {0, -1, 0, 1, 0, 0, 0, 0};

    private void act(Unit u) {
        int s = 0;
        for (int r = 0; r < Brain.RAYS; r++) {
            int dx = DXRAY[r], dy = DYRAY[r];
            int x = u.x + dx, y = u.y + dy;
            Tile t = inBounds(x, y) ? get(x, y) : Tile.WALL;
            visionBuf[s++] = t.solid() ? 1 : 0;
            visionBuf[s++] = (t == Tile.FOOD || t == Tile.EXIT) ? 1 : 0;
            Unit other = unitAt(x, y);
            visionBuf[s++] = other != null && other.team != u.team ? 1 : 0;
            visionBuf[s++] = other != null && other.team == u.team ? 1 : 0;
            visionBuf[s++] = (inBounds(x, y) ? Math.min(1, scent[y * cfg.width + x] / 1.5) : 0);
        }

        int action = u.brain.decide(visionBuf, Math.max(0, Math.min(1, u.energy)));
        u.age++;
        // Stoffwechsel: Basis * Gen; alles recycelt (70% Biomasse, 30% Waerme).
        // Exakte Buchung: niemand verbrennt mehr, als er hat
        double burn = cfg.metabolism * u.geneMetab * (u.geneSpeed ? 1.5 : 1.0);
        burn = Math.min(burn, Math.max(0, u.energy));
        u.energy -= burn;
        biomass += burn * 0.7;
        heat += burn * 0.3;

        // Eigene Duftspur (Kommunikation): Spur zum Futter entsteht emergent
        scent[u.y * cfg.width + u.x] += 0.02;

        int mv = Math.min(action, 4);
        int nx = u.x + DX[mv], ny = u.y + DY[mv];

        switch (action) {
            case 0: break;
            case 1: case 2: case 3: case 4:
                if (!inBounds(nx, ny)) break;
                Tile t = get(nx, ny);
                if (t == Tile.FOOD) {
                    // Fressen mit Wildfaktor: Nahrungsenergie variiert wild, medianstabil
                    double gained = cfg.foodEnergy * rng.wild(cfg.wildness, 2.0);
                    // Wildfaktor = echtes Glueck mit Preis: das Oekosystem ("Haus")
                    // zahlt die Differenz aus Biomasse -> Gesamtenergie exakt erhalten
                    double excess = gained - cfg.foodEnergy;
                    if (excess > biomass) { gained = cfg.foodEnergy + biomass; excess = biomass; }
                    biomass -= excess;
                    set(nx, ny, Tile.EMPTY);
                    u.energy += gained;
                    u.foodEaten++;
                    scent[ny * cfg.width + nx] += 0.5;   // FUND-Alarm: Schwarm folgt
                    moveTo(u, nx, ny);
                } else if (t == Tile.EXIT) {
                    u.exitsReached++;
                    double oldE = Math.max(0, u.energy);
                    double body = oldE * 0.25;                       // Koerperprmie: 25% werden AUS dem Koerper
                    u.energy -= body;                                //   entnommen und zur Biomasse gemacht
                    biomass += body;                                 //   ( KEINE neue Energie mehr )
                    double gain = 1.5 - u.energy;                    // Quest-Belohnung aus Reserve
                    if (gain > 0 && biomass >= gain) { biomass -= gain; u.energy = 1.5; }
                    set(nx, ny, Tile.EMPTY);
                    set(u.x, u.y, Tile.EMPTY);
                    u.x = 1 + rng.nextInt(cfg.width - 2); u.y = 1;
                    rememberGood(u);
                } else if (t == Tile.EMPTY) {
                    moveTo(u, nx, ny);
                } else if (t == Tile.DIRT) {
                    set(nx, ny, Tile.EMPTY);
                    double dig = Math.min(0.05, Math.max(0, u.energy));
                    u.energy -= dig; biomass += dig;   // abgetragene Erde in den Pool
                    u.dirtDug++;
                }
                break;
            case 5:
                if (inBounds(u.x, u.y + 1) && get(u.x, u.y + 1) == Tile.DIRT) {
                    set(u.x, u.y + 1, Tile.EMPTY);
                    u.dirtDug++;
                    double dig2 = Math.min(0.05, Math.max(0, u.energy));
                    u.energy -= dig2; biomass += dig2;
                }
                break;
            case 6:
                nx = u.x + DX[lastMove()]; ny = u.y + DY[lastMove()];
                if (inBounds(nx, ny) && get(nx, ny) == Tile.BOULDER) {
                    int bx = nx + DX[lastMove()], by = ny + DY[lastMove()];
                    if (inBounds(bx, by) && get(bx, by) == Tile.EMPTY && unitAt(bx, by) == null) {
                        double cost = Math.min(0.4, Math.max(0, u.energy));
                        u.energy -= cost;
                        double oldV2 = boulderVy.getOrDefault(ny * cfg.width + nx, 0.0);
                        set(nx, ny, Tile.EMPTY); set(bx, by, Tile.BOULDER);
                        // booster KE: 0.5*(neu-alt) = cost + Schwerkraftarbeit (-g*dy):
                        // beim Runterdruecken leistet die Gravitation g zusaetzlich -> muss in KE
                        int dyPush = DY[lastMove()];
                        boulderVy.put(by * cfg.width + bx, Math.max(0, oldV2 + 2 * (cost + cfg.gravityAccel * dyPush)));
                    }
                }
                break;
            case 7: {
                Unit v = adjacentEnemy(u);
                if (v != null) {
                    double dmg = Math.min(0.7, Math.max(0, v.energy));
                    double cost = Math.min(0.1, Math.max(0, u.energy));
                    v.energy -= dmg; u.energy -= cost;
                    biomass += dmg + cost;                // Kampferlust-Emission: 100% in den Kreislauf
                    u.kills += v.energy <= 0 ? 1 : 0;
                }
                break;
            }
            case 8:
                if (u.energy > cfg.reproduceEnergy) {
                    Unit c = spawnFrom(u);
                    if (c != null) {
                        double gift = Math.min(0.8, Math.max(0, u.energy));
                        u.energy -= gift; c.energy = gift;  // Kind costet exakt das, was die Mutter abgibt
                        u.children++;
                    }
                }
                break;
        }
    }

    private void rememberGood(Unit u) {
        if (u.exitsReached == 1) hallAdd(u.brain);
        else if (rng.chance(0.3)) hallAdd(u.brain);
    }

    private int lastMove() { return 1; }

    private static final int[] DXRAY = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] DYRAY = {-1, -1, 0, 1, 1, 1, 0, -1};

    private Unit adjacentEnemy(Unit u) {
        for (int dy = -1; dy <= 1; dy++)
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                Unit v = unitAt(u.x + dx, u.y + dy);
                if (v != null && v.team != u.team) return v;
            }
        return null;
    }

    private void moveTo(Unit u, int nx, int ny) {
        Unit target = unitAt(nx, ny);
        if (target == null) { u.x = nx; u.y = ny; }
        else if (target.team != u.team) {
            double dmg = Math.min(0.15, Math.max(0, target.energy));
            double cost = Math.min(0.02, Math.max(0, u.energy));
            target.energy -= dmg; u.energy -= cost;
            biomass += dmg + cost;                // Ramm-Stoss: VERLUSTFREI in den Kreislauf
        }
    }

    public String stats() {
        int maxGen = 0; double sumF = 0;
        for (Unit u : units) { maxGen = Math.max(maxGen, (int) u.generation); sumF += u.fitness(); }
        return String.format("tick=%d units=%d maxGen=%d fit=%.1f hall=%d | E=%.2f bio=%.1f heat=%.1f drift=%.4f",
                tick, units.size(), maxGen, units.isEmpty() ? 0 : sumF / units.size(), hallOfFame.size(),
                totalEnergy(), biomass, heat, driftAccum);
    }
}
