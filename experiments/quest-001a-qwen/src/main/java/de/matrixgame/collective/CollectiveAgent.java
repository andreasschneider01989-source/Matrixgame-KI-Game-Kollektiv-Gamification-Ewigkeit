package de.matrixgame.collective;

import java.util.UUID;
import java.util.Random;

/**
 * Candidate implementation for QUEST 001A.
 * Quelle: vom Projektinitiator übermittelter Qwen3.7-Plus-Entwurf, 06.11.2026.
 */
public class CollectiveAgent {
    public final String id;
    public final int generation;
    public double energy;
    public final double maxEnergy = 100.0;
    public int x, y, age;
    public boolean alive;
    public AgentGenome genome;
    public double attention, stress, motivation;
    public int foodEaten, timesShared, timesAttacked, offspringCount;

    public CollectiveAgent(int x, int y, double startingEnergy, AgentGenome genome,
                           int generation, Random random) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.x = x; this.y = y;
        this.energy = startingEnergy;
        this.genome = (genome != null) ? genome : new AgentGenome(random);
        this.generation = generation;
        this.age = 0; this.alive = true;
        this.attention = 0.7; this.stress = 0.2; this.motivation = 0.6;
    }

    public SensorInput sense(GameEnvironment env) {
        SensorInput input = new SensorInput();
        input.foodNearby = env.getFoodStrengthAt(x, y) * genome.foodSensitivity;
        input.agentNearby = env.getAgentDensityNear(x, y) * genome.agentSensitivity;
        input.dangerNearby = env.getDangerAt(x, y) * genome.dangerSensitivity;
        return input;
    }

    public String decide(SensorInput input) {
        double move = genome.moveTendency * (0.5 + attention * 0.5);
        double eat = genome.eatTendency * input.foodNearby * (1.0 - stress * 0.3);
        double share = genome.shareTendency * input.agentNearby * motivation;
        double warn = genome.warnTendency * input.dangerNearby * attention;
        double attack = genome.attackTendency * input.agentNearby * (0.5 + stress * 0.5);
        double rest = stress * 0.8 + (1.0 - energy / maxEnergy) * 0.5;

        double max = move; String action = "MOVE";
        if (eat > max) { max = eat; action = "EAT"; }
        if (share > max) { max = share; action = "SHARE"; }
        if (warn > max) { max = warn; action = "WARN"; }
        if (attack > max) { max = attack; action = "ATTACK"; }
        if (rest > max) { max = rest; action = "REST"; }
        return action;
    }

    public void act(String action, GameEnvironment env) {
        if (!alive) return;
        double baseCost = 1.0 * (1.0 - genome.energyEfficiency * 0.5);

        switch (action) {
            case "MOVE":
                x = env.clampX(x + env.getRandom().nextInt(3) - 1);
                y = env.clampY(y + env.getRandom().nextInt(3) - 1);
                energy -= baseCost; env.actionMove++;
                break;
            case "EAT":
                double gained = env.consumeFoodAt(x, y);
                energy += gained; if (gained > 0) foodEaten++;
                energy -= baseCost * 0.5; env.actionEat++;
                break;
            case "SHARE":
                if (env.tryShareEnergy(this, 5.0)) {
                    timesShared++; motivation = Math.min(1.0, motivation + 0.05);
                    energy -= 5.0 + baseCost;
                } else { energy -= baseCost; }
                env.actionShare++;
                break;
            case "WARN":
                env.broadcastWarning(x, y); energy -= baseCost * 0.7; env.actionWarn++;
                break;
            case "ATTACK":
                if (env.tryAttackNear(this)) { timesAttacked++; stress = Math.min(1.0, stress + 0.1); }
                energy -= baseCost * 1.5; env.actionAttack++;
                break;
            case "REST":
                energy -= baseCost * 0.3; stress = Math.max(0.0, stress - 0.08);
                attention = Math.min(1.0, attention + 0.03); env.actionRest++;
                break;
            default:
                energy -= baseCost;
        }
        energy = Math.max(0.0, Math.min(maxEnergy, energy));
        age++;
        updateInternalStates();
        if (energy <= 0) { alive = false; env.deaths++; }
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
        AgentGenome childGenome = this.genome.mutate(env.getRandom());
        CollectiveAgent child = new CollectiveAgent(this.x, this.y, childEnergy,
                childGenome, this.generation + 1, env.getRandom());
        this.offspringCount++; env.births++;
        env.addAgent(child);
        return child;
    }

    public boolean isAlive() { return alive; }

    public static class SensorInput {
        public double foodNearby, agentNearby, dangerNearby;
    }
}
