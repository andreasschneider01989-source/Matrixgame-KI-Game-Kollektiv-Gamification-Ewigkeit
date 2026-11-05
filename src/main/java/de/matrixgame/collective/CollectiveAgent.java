package de.matrixgame.collective;

import java.util.UUID;

/**
 * CollectiveAgent – die erste Lebensform im Matrixgame.
 * Energie, Sensoren, Entscheidung, interne Zustände (attention/stress/motivation),
 * Fortpflanzung + Mutation, Aktionen inkl. SHARE / WARN / REST.
 */
public class CollectiveAgent {

    public final String id;
    public final int generation;

    public double energy;
    public double maxEnergy;
    public int x;
    public int y;
    public int age;
    public boolean alive;

    public AgentGenome genome;

    public double attention;
    public double stress;
    public double motivation;

    public int foodEaten;
    public int timesShared;
    public int timesAttacked;
    public int offspringCount;

    public CollectiveAgent(int x, int y, double startingEnergy, AgentGenome genome, int generation) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.x = x;
        this.y = y;
        this.energy = startingEnergy;
        this.maxEnergy = 100.0;
        this.genome = (genome != null) ? genome : new AgentGenome();
        this.generation = generation;
        this.age = 0;
        this.alive = true;

        this.attention = 0.7;
        this.stress = 0.2;
        this.motivation = 0.6;

        this.foodEaten = 0;
        this.timesShared = 0;
        this.timesAttacked = 0;
        this.offspringCount = 0;
    }

    public SensorInput sense(GameEnvironment env) {
        SensorInput input = new SensorInput();
        input.foodNearby = env.getFoodStrengthAt(x, y) * genome.foodSensitivity;
        input.agentNearby = env.getAgentDensityNear(x, y) * genome.agentSensitivity;
        input.dangerNearby = env.getDangerAt(x, y) * genome.dangerSensitivity;
        return input;
    }

    public String decide(SensorInput input) {
        double moveScore   = genome.moveTendency * (0.5 + attention * 0.5);
        double eatScore    = genome.eatTendency * input.foodNearby * (1.0 - stress * 0.3);
        double shareScore  = genome.shareTendency * input.agentNearby * motivation;
        double warnScore   = genome.warnTendency * input.dangerNearby * attention;
        double attackScore = genome.attackTendency * input.agentNearby * (0.5 + stress * 0.5);
        double restScore   = stress * 0.8 + (1.0 - energy / maxEnergy) * 0.5;

        double max = moveScore;
        String action = "MOVE";

        if (eatScore > max)   { max = eatScore;   action = "EAT"; }
        if (shareScore > max) { max = shareScore; action = "SHARE"; }
        if (warnScore > max)  { max = warnScore;  action = "WARN"; }
        if (attackScore > max){ max = attackScore;action = "ATTACK"; }
        if (restScore > max)  { max = restScore;  action = "REST"; }

        return action;
    }

    public void act(String action, GameEnvironment env) {
        if (!alive) return;

        double energyCost = 1.0 * (1.0 - genome.energyEfficiency * 0.5);

        switch (action) {
            case "MOVE":
                int dx = (int)(Math.random() * 3) - 1;
                int dy = (int)(Math.random() * 3) - 1;
                x = env.clampX(x + dx);
                y = env.clampY(y + dy);
                energy -= energyCost;
                break;

            case "EAT":
                double gained = env.consumeFoodAt(x, y);
                energy += gained;
                if (gained > 0) foodEaten++;
                energy -= energyCost * 0.5;
                break;

            case "SHARE":
                boolean shared = env.tryShareEnergy(this, 5.0);
                if (shared) {
                    timesShared++;
                    motivation = Math.min(1.0, motivation + 0.05);
                    energy -= 5.0 + energyCost;
                } else {
                    energy -= energyCost;
                }
                break;

            case "WARN":
                env.broadcastWarning(x, y);
                energy -= energyCost * 0.7;
                break;

            case "ATTACK":
                boolean attacked = env.tryAttackNear(this);
                if (attacked) timesAttacked++;
                energy -= energyCost * 1.5;
                stress = Math.min(1.0, stress + 0.1);
                break;

            case "REST":
                energy -= energyCost * 0.3;
                stress = Math.max(0.0, stress - 0.08);
                attention = Math.min(1.0, attention + 0.03);
                break;

            default:
                energy -= energyCost;
        }

        energy = Math.max(0.0, Math.min(maxEnergy, energy));
        age++;
        updateInternalStates();

        if (energy <= 0) {
            alive = false;
        }
    }

    private void updateInternalStates() {
        if (energy < 30) {
            stress = Math.min(1.0, stress + 0.05);
            motivation = Math.max(0.1, motivation - 0.03);
        } else {
            stress = Math.max(0.0, stress - 0.02);
        }
        attention = Math.max(0.2, Math.min(1.0, attention + (motivation - stress) * 0.01));
    }

    public CollectiveAgent reproduce(GameEnvironment env) {
        if (!alive || energy < 40) return null;

        double childEnergy = energy * 0.4;
        energy -= childEnergy;

        AgentGenome childGenome = this.genome.mutate();
        CollectiveAgent child = new CollectiveAgent(this.x, this.y, childEnergy, childGenome, this.generation + 1);

        this.offspringCount++;
        env.addAgent(child);

        return child;
    }

    public boolean isAlive() {
        return alive && energy > 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Agent[%s gen=%d energy=%.1f pos=(%d,%d) share=%.2f attack=%.2f alive=%s]",
            id, generation, energy, x, y,
            genome.shareTendency, genome.attackTendency, alive
        );
    }

    public static class SensorInput {
        public double foodNearby = 0.0;
        public double agentNearby = 0.0;
        public double dangerNearby = 0.0;
    }
}
