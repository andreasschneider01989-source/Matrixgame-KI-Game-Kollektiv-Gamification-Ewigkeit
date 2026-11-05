package de.matrixgame.collective;

/**
 * Seeded demonstration for QUEST 001A.
 * Quelle: vom Projektinitiator übermittelter Qwen3.7-Plus-Entwurf, 06.11.2026.
 */
public class SimpleSimulation {
    public static void main(String[] args) {
        long seed = 42L;
        System.out.println("=== Matrixgame – QUEST 001A: Reproducible Evolution ===");
        System.out.println("Seed: " + seed + " (gleiche Startbedingungen und gleiche RNG-Reihenfolge)\n");

        GameEnvironment env = new GameEnvironment(40, 30, seed);
        for (int i = 0; i < 12; i++) {
            int x = env.getRandom().nextInt(env.width);
            int y = env.getRandom().nextInt(env.height);
            CollectiveAgent agent = new CollectiveAgent(x, y,
                    50.0 + env.getRandom().nextDouble() * 30.0, null, 0, env.getRandom());
            env.addAgent(agent);
        }

        for (int step = 1; step <= 150; step++) {
            env.step();
            if (step % 25 == 0) env.printStats();
        }

        System.out.println("\n=== Simulation beendet ===");
        System.out.println("Überlebende Agenten und ihre Genome:");
        for (CollectiveAgent a : env.getAgents()) {
            if (a.isAlive()) {
                System.out.printf("Gen %02d | %s | Energy: %.1f | %s%n",
                        a.generation, a.id, a.energy, a.genome.toString());
            }
        }
    }
}
