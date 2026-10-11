package matrixgame;

import matrixgame.arena.Arena;
import matrixgame.commons.*;
import matrixgame.core.*;

import java.util.*;

/** Dependency-freie Tests: java -ea -cp out matrixgame.SelfCheck */
public final class SelfCheck {
    static int failed = 0;
    static void check(boolean ok, String what) {
        System.out.println((ok ? "PASS " : "FAIL ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        // Determinismus
        var a = Simulation.run(world(7, 10));
        var b = Simulation.run(world(7, 10));
        check(a.equals(b), "gleicher Seed -> identische Messwerte");

        // Ressource bleibt in [0, K]
        Resource r = new Resource("x", 900, 1000, 0.25);
        boolean inBounds = true;
        for (int i = 0; i < 1000; i++) {
            r.take(i % 7 * 10); r.regrow(i % 2 == 0 ? 0.1 : -0.1);
            if (r.amount() < 0 || r.amount() > 1000) inBounds = false;
        }
        check(inBounds, "Resource bleibt in [0, capacity]");
        Resource dead = new Resource("y", 100, 1000, 0.25); dead.take(1e9); dead.regrow(0);
        check(dead.amount() == 0, "Bestand 0 bleibt 0 (irreversibler Kollaps)");
        check(new Resource("z", 10, 100, 0.2).take(50) == 10, "take liefert hoechstens den Bestand");

        // Greedy kollabiert, Kooperation haelt
        var greedy = Simulation.run(world(1, 0));
        check(greedy.get("survivors") == 0, "Greedy-Population stirbt aus");
        var nmm = Simulation.run(Arena.run(1, "nmm", "10"));
        check(nmm.get("survivors") == 10, "N-Max-Max-Population ueberlebt vollstaendig (Seed 1)");

        // Claim-Validierung, Statistik
        boolean threw = false;
        try { new Claim("x", Pal.FACT, 1.5); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "Claim lehnt Konfidenz ausserhalb [0,1] ab");
        check(Experiment.welch(5, 1, 5, 1, 30) == 0, "t = 0 bei gleichen Mittelwerten");
        check(Experiment.welch(6, 1, 5, 1, 30) > Experiment.T_THRESHOLD, "t gross bei klarem Unterschied");

        // Negativkontrolle: gleiche Verteilung darf nicht 'SUPPORTED' liefern
        var spec = Arena.specs().get(4);
        check(Experiment.run(spec, 100, 1).verdict() == Result.Verdict.INCONCLUSIVE,
              "Negativkontrolle liefert INCONCLUSIVE");

        if (failed > 0) { System.out.println(failed + " Test(s) fehlgeschlagen"); System.exit(1); }
        System.out.println("Alle Tests bestanden.");
    }

    /** Hilfswelt: n Greedy-Agents (n=0 -> 10). */
    static CommonsWorld world(long seed, int n) {
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < (n == 0 ? 10 : n); i++) agents.add(new GreedyAgent("greedy-" + i));
        return new CommonsWorld(CommonsConfig.defaults(), agents, seed);
    }
}
