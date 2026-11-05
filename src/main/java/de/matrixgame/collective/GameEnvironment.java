package de.matrixgame.collective;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Minimale Umgebung für die erste CollectiveAgent-Generation.
 * Später ausbauen zu Biomen, Wasserhaushalt, Ressourcenkarten und echten Interaktionen.
 *
 * Wichtige Design-Hinweise für die Weiterentwicklung:
 * - Zufallszahlengenerator sollte später konfigurierbar/reproduzierbar gemacht werden.
 * - SHARE und ATTACK enthalten bewusst einfache Logik – hier sollen Consent, Bedarf und Sicherheitsregeln ergänzt werden.
 * - Die Umwelt ist absichtlich schlank gehalten, damit sie leicht testbar und erweiterbar bleibt.
 */
public class GameEnvironment {

    public final int width;
    public final int height;
    private final List<CollectiveAgent> agents = new ArrayList<>();
    private final double[][] foodMap;
    private final Random random = new Random();

    public GameEnvironment(int width, int height) {
        this.width = width;
        this.height = height;
        this.foodMap = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (random.nextDouble() < 0.15) {
                    foodMap[i][j] = 10 + random.nextDouble() * 30;
                }
            }
        }
    }

    public void addAgent(CollectiveAgent agent) {
        agents.add(agent);
    }

    public List<CollectiveAgent> getAgents() {
        return agents;
    }

    public int clampX(int x) {
        return Math.max(0, Math.min(width - 1, x));
    }

    public int clampY(int y) {
        return Math.max(0, Math.min(height - 1, y));
    }

    public double getFoodStrengthAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return 0.0;
        return foodMap[x][y] / 40.0;
    }

    public double consumeFoodAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return 0.0;
        double amount = foodMap[x][y];
        foodMap[x][y] = 0;
        return amount;
    }

    public double getAgentDensityNear(int x, int y) {
        int count = 0;
        for (CollectiveAgent a : agents) {
            if (a.isAlive() && Math.abs(a.x - x) <= 2 && Math.abs(a.y - y) <= 2) {
                count++;
            }
        }
        return Math.min(1.0, count / 5.0);
    }

    public double getDangerAt(int x, int y) {
        return 0.1 + random.nextDouble() * 0.2;
    }

    public boolean tryShareEnergy(CollectiveAgent giver, double amount) {
        for (CollectiveAgent other : agents) {
            if (other != giver && other.isAlive()
                    && Math.abs(other.x - giver.x) <= 1
                    && Math.abs(other.y - giver.y) <= 1
                    && other.energy < other.maxEnergy * 0.8) {

                other.energy += amount;
                return true;
            }
        }
        return false;
    }

    public void broadcastWarning(int x, int y) {
        // Platzhalter – später erhalten nahe Agenten einen Warnreiz.
    }

    public boolean tryAttackNear(CollectiveAgent attacker) {
        for (CollectiveAgent other : agents) {
            if (other != attacker && other.isAlive()
                    && Math.abs(other.x - attacker.x) <= 1
                    && Math.abs(other.y - attacker.y) <= 1) {

                other.energy -= 15;
                attacker.energy += 8;
                if (other.energy <= 0) {
                    other.alive = false;
                }
                return true;
            }
        }
        return false;
    }

    public void step() {
        List<CollectiveAgent> snapshot = new ArrayList<>(agents);

        for (CollectiveAgent agent : snapshot) {
            if (!agent.isAlive()) continue;

            CollectiveAgent.SensorInput input = agent.sense(this);
            String action = agent.decide(input);
            agent.act(action, this);

            if (agent.energy > 60 && random.nextDouble() < 0.05) {
                agent.reproduce(this);
            }
        }

        agents.removeIf(a -> !a.isAlive());
    }

    public void printStats() {
        long alive = agents.stream().filter(CollectiveAgent::isAlive).count();
        double avgEnergy = agents.stream()
                .filter(CollectiveAgent::isAlive)
                .mapToDouble(a -> a.energy)
                .average()
                .orElse(0.0);

        System.out.printf("Agents alive: %d | Avg energy: %.1f%n", alive, avgEnergy);
    }
}
