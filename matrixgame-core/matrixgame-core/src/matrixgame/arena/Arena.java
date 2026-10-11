package matrixgame.arena;

import matrixgame.commons.*;
import matrixgame.core.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Algorithmus-Arena: laeuft reproduzierbar (feste Seeds) und schreibt results/ARENA.md. */
public final class Arena {
    static final int POP = 10;

    static Agent make(String type, String id, long seed) {
        return switch (type) {
            case "nmm" -> new NMaxMaxAgent(id);
            case "greedy" -> new GreedyAgent(id);
            case "recip" -> new ReciprocatorAgent(id);
            case "random" -> new RandomAgent(id, seed * 31 + id.hashCode());
            default -> throw new IllegalArgumentException(type);
        };
    }

    /** Baut eine Population aus (Typ, Anzahl)-Paaren, id = Typ-Index. */
    public static CommonsWorld run(long seed, String... typeCount) {
        List<Agent> agents = new ArrayList<>();
        for (int k = 0; k < typeCount.length; k += 2) {
            String type = typeCount[k];
            int count = Integer.parseInt(typeCount[k + 1]);
            for (int i = 0; i < count; i++) agents.add(make(type, type + "-" + i, seed));
        }
        CommonsWorld w = new CommonsWorld(CommonsConfig.defaults(), agents, seed);
        Simulation.run(w);
        return w;
    }

    public static List<Experiment.Spec> specs() {
        String p = String.valueOf(POP), h = String.valueOf(POP / 2);
        List<Experiment.Spec> s = new ArrayList<>();
        s.add(new Experiment.Spec("E1 Lebensdauer: N-Max-Max- vs. Greedy-Population",
            new Claim("Eine reine N-Max-Max-Population ueberlebt in der Allmende laenger als eine reine Greedy-Population.", Pal.HYPOTHESIS, 0.8),
            "lifetime", "N-Max-Max", "Greedy",
            seed -> run(seed, "nmm", p).meanLifetime(""), seed -> run(seed, "greedy", p).meanLifetime("")));
        s.add(new Experiment.Spec("E2 Wohlfahrt: N-Max-Max- vs. Greedy-Population",
            new Claim("Eine reine N-Max-Max-Population erntet insgesamt mehr als eine reine Greedy-Population.", Pal.HYPOTHESIS, 0.8),
            "welfare", "N-Max-Max", "Greedy",
            seed -> run(seed, "nmm", p).welfare(""), seed -> run(seed, "greedy", p).welfare("")));
        s.add(new Experiment.Spec("E3 Gemischte Population 5/5: wer lebt laenger?",
            new Claim("In gemischter Population leben N-Max-Max-Agents laenger als Greedy-Agents (Kooperation wird nicht nur ausgebeutet).", Pal.HYPOTHESIS, 0.5),
            "lifetime (pro Agent)", "N-Max-Max", "Greedy",
            seed -> run(seed, "nmm", h, "greedy", h).meanLifetime("nmm"),
            seed -> run(seed, "nmm", h, "greedy", h).meanLifetime("greedy")));
        s.add(new Experiment.Spec("E4 Wohlfahrt: N-Max-Max- vs. Reziprozitaets-Population",
            new Claim("Eine N-Max-Max-Population erntet mehr als eine Reziprozitaets-Population.", Pal.HYPOTHESIS, 0.5),
            "welfare", "N-Max-Max", "Reziprozitaet",
            seed -> run(seed, "nmm", p).welfare(""), seed -> run(seed, "recip", p).welfare("")));
        s.add(new Experiment.Spec("E5 Negativkontrolle: N-Max-Max gegen sich selbst (andere Seeds)",
            new Claim("Messverfahren erzeugt keinen Unterschied, wo keiner existiert.", Pal.HYPOTHESIS, 0.9),
            "welfare", "N-Max-Max (Seeds A)", "N-Max-Max (Seeds B)",
            seed -> run(seed, "nmm", p).welfare(""), seed -> run(seed + 1_000_000L, "nmm", p).welfare("")));
        return s;
    }

    public static void main(String[] args) throws IOException {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 200;
        long baseSeed = 1;
        StringBuilder md = new StringBuilder();
        md.append("# Arena-Ergebnisse\n\n")
          .append("Allmende-Welt, ").append(POP).append(" Agents, ").append(n).append(" Seeds (")
          .append(baseSeed).append("..").append(baseSeed + n - 1).append("). Reproduzierbar mit `./run.sh ").append(n).append("`.\n\n")
          .append("> Alle Urteile gelten nur fuer dieses Modell mit diesen Parametern und Gewichten.\n\n");
        for (Experiment.Spec s : specs()) {
            Result r = Experiment.run(s, n, baseSeed);
            md.append(r.toMarkdown()).append('\n');
            System.out.printf(Locale.ROOT, "%-70s %s (t=%.1f)%n", s.name(), r.verdict(), r.t());
        }
        Path out = Path.of("results", "ARENA.md");
        Files.createDirectories(out.getParent());
        Files.writeString(out, md.toString());
        System.out.println("Geschrieben: " + out);
    }
}
