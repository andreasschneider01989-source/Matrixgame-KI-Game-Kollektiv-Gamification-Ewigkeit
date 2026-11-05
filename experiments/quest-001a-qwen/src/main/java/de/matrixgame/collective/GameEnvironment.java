package de.matrixgame.collective;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Candidate environment for QUEST 001A.
 * Quelle: vom Projektinitiator übermittelter Qwen3.7-Plus-Entwurf, 06.11.2026.
 */
public class GameEnvironment {
    public final int width, height;
    private final List<CollectiveAgent> agents = new ArrayList<>();
    private final double[][] foodMap;
    private final Random random;

    public int stepCount = 0;
    public int actionMove, actionEat, actionShare, actionWarn, actionAttack, actionRest;
    public int births, deaths;

    public GameEnvironment(int width, int height, long seed) {
        this.width = width; this.height = height;
        this.random = new Random(seed);
        this.foodMap = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (random.nextDouble() < 0.15) foodMap[i][j] = 10.0 + random.nextDouble() * 30.0;
            }
        }
    }

    public Random getRandom() { return random; }
    public void addAgent(CollectiveAgent agent) { agents.add(agent); }
    public List<CollectiveAgent> getAgents() { return agents; }
    public int clampX(int x) { return Math.max(0, Math.min(width - 1, x)); }
    public int clampY(int y) { return Math.max(0, Math.min(height - 1, y)); }

    public double getFoodStrengthAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return 0.0;
        return foodMap[x][y] / 40.0;
    }

    public double consumeFoodAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return 0.0;
        double amount = foodMap[x][y]; foodMap[x][y] = 0;
        return amount;
    }

    public double getAgentDensityNear(int x, int y) {
        int count = 0;
        for (CollectiveAgent a : agents) {
            if (a.isAlive() && Math.abs(a.x - x) <= 2 && Math.abs(a.y - y) <= 2) count++;
        }
        return Math.min(1.0, count / 5.0);
    }

    public double getDangerAt(int x, int y) {
        return 0.1 + random.nextDouble() * 0.2;
    }

    /** Transfer is bounded by donor energy and receiver capacity. */
    public boolean tryShareEnergy(CollectiveAgent giver, double amount) {
        for (CollectiveAgent other : agents) {
            if (other != giver && other.isAlive()
                    && Math.abs(other.x - giver.x) <= 1 && Math.abs(other.y - giver.y) <= 1
                    && other.energy < other.maxEnergy) {
                double actualTransfer = Math.min(amount, other.maxEnergy - other.energy);
                if (giver.energy >= actualTransfer && actualTransfer > 0) {
                    giver.energy -= actualTransfer;
                    other.energy += actualTransfer;
                    return true;
                }
            }
        }
        return false;
    }

    public void broadcastWarning(int x, int y) {
        // TODO: implement warning effect on nearby agents.
    }

    /** Attack cost, victim damage and attacker nutrition are separate model parameters. */
    public boolean tryAttackNear(CollectiveAgent attacker) {
        for (CollectiveAgent other : agents) {
            if (other != attacker && other.isAlive()
                    && Math.abs(other.x - attacker.x) <= 1 && Math.abs(other.y - attacker.y) <= 1) {
                double attackCost = 5.0;
                if (attacker.energy < attackCost) return false;
                attacker.energy -= attackCost;
                double damage = 15.0;
                other.energy -= damage;
                double nutritionGain = 8.0;
                attacker.energy = Math.min(attacker.maxEnergy, attacker.energy + nutritionGain);
                if (other.energy <= 0) {
                    other.alive = false; deaths++;
                }
                return true;
            }
        }
        return false;
    }

    private void regenerateResources() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (foodMap[i][j] < 40.0 && random.nextDouble() < 0.05) {
                    foodMap[i][j] = Math.min(40.0, foodMap[i][j] + 5.0);
                }
            }
        }
    }

    public void step() {
        stepCount++;
        List<CollectiveAgent> snapshot = new ArrayList<>(agents);
        for (CollectiveAgent agent : snapshot) {
            if (!agent.isAlive()) continue;
            CollectiveAgent.SensorInput input = agent.sense(this);
            String action = agent.decide(input);
            agent.act(action, this);
            if (agent.energy > 60 && random.nextDouble() < 0.05) agent.reproduce(this);
        }
        agents.removeIf(a -> !a.isAlive());
        regenerateResources();
    }

    public void printStats() {
        long alive = agents.stream().filter(CollectiveAgent::isAlive).count();
        double avgEnergy = agents.stream().filter(CollectiveAgent::isAlive)
                .mapToDouble(a -> a.energy).average().orElse(0.0);
        System.out.printf("Step %03d | Alive: %2d | Avg Energy: %5.1f | Shares: %2d | Attacks: %2d | Births: %2d | Deaths: %2d%n",
                stepCount, alive, avgEnergy, actionShare, actionAttack, births, deaths);
    }
}
