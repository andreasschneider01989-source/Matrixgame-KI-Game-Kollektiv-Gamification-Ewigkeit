package de.matrixgame.collective;

/**
 * Minimale Demo-Simulation, um CollectiveAgent zu testen.
 * Kann später durch eine vollständige EvolutionEngine und reproduzierbare Seeds ersetzt werden.
 */
public class SimpleSimulation {

    public static void main(String[] args) {
        System.out.println("=== Matrixgame – CollectiveAgent Demo ===");
        System.out.println("Starte kleine Population...\n");

        GameEnvironment env = new GameEnvironment(40, 30);

        for (int i = 0; i < 12; i++) {
            int x = (int) (Math.random() * env.width);
            int y = (int) (Math.random() * env.height);
            CollectiveAgent agent = new CollectiveAgent(x, y, 50 + Math.random() * 30, null, 0);
            env.addAgent(agent);
        }

        for (int step = 1; step <= 100; step++) {
            env.step();

            if (step % 20 == 0) {
                System.out.print("Schritt " + step + " → ");
                env.printStats();
            }
        }

        System.out.println("\n=== Simulation beendet ===");
        System.out.println("Lebende Agenten und ihre Genome:");

        for (CollectiveAgent a : env.getAgents()) {
            if (a.isAlive()) {
                System.out.println(a + " | " + a.genome);
            }
        }
    }
}
