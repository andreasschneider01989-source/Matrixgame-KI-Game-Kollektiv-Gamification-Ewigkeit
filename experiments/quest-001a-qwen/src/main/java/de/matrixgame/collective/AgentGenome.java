package de.matrixgame.collective;

import java.util.Random;

/**
 * Vererbbares Genom für CollectiveAgent.
 * Nutzt nun eine übergebene Random-Instanz für reproduzierbare Zufallsfolgen.
 * Quelle: vom Projektinitiator übermittelter Qwen3.7-Plus-Entwurf, 06.11.2026.
 */
public class AgentGenome {
    public double foodSensitivity;
    public double agentSensitivity;
    public double dangerSensitivity;
    public double moveTendency;
    public double eatTendency;
    public double shareTendency;
    public double warnTendency;
    public double attackTendency;
    public double energyEfficiency;
    public double mutationRate;

    public AgentGenome(Random random) {
        this.foodSensitivity   = random.nextDouble();
        this.agentSensitivity  = random.nextDouble();
        this.dangerSensitivity = random.nextDouble();
        this.moveTendency      = random.nextDouble();
        this.eatTendency       = random.nextDouble();
        this.shareTendency     = random.nextDouble() * 0.3;
        this.warnTendency      = random.nextDouble() * 0.3;
        this.attackTendency    = random.nextDouble();
        this.energyEfficiency  = 0.5 + random.nextDouble() * 0.5;
        this.mutationRate      = 0.05 + random.nextDouble() * 0.1;
    }

    public AgentGenome(AgentGenome other, Random random) {
        this.foodSensitivity   = other.foodSensitivity;
        this.agentSensitivity  = other.agentSensitivity;
        this.dangerSensitivity = other.dangerSensitivity;
        this.moveTendency      = other.moveTendency;
        this.eatTendency       = other.eatTendency;
        this.shareTendency     = other.shareTendency;
        this.warnTendency      = other.warnTendency;
        this.attackTendency    = other.attackTendency;
        this.energyEfficiency  = other.energyEfficiency;
        this.mutationRate      = other.mutationRate;
    }

    public AgentGenome mutate(Random random) {
        AgentGenome child = new AgentGenome(this, random);
        double rate = this.mutationRate;

        child.foodSensitivity   = mutateValue(child.foodSensitivity, rate, random);
        child.agentSensitivity  = mutateValue(child.agentSensitivity, rate, random);
        child.dangerSensitivity = mutateValue(child.dangerSensitivity, rate, random);
        child.moveTendency      = mutateValue(child.moveTendency, rate, random);
        child.eatTendency       = mutateValue(child.eatTendency, rate, random);
        child.shareTendency     = mutateValue(child.shareTendency, rate, random);
        child.warnTendency      = mutateValue(child.warnTendency, rate, random);
        child.attackTendency    = mutateValue(child.attackTendency, rate, random);
        child.energyEfficiency  = mutateValue(child.energyEfficiency, rate, random);
        child.mutationRate = Math.max(0.01, Math.min(0.3,
                mutateValue(child.mutationRate, rate * 0.5, random)));
        return child;
    }

    private double mutateValue(double value, double rate, Random random) {
        if (random.nextDouble() < rate) {
            value += (random.nextDouble() - 0.5) * 0.4;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    @Override
    public String toString() {
        return String.format("Genome[share=%.2f, attack=%.2f, eff=%.2f]",
                shareTendency, attackTendency, energyEfficiency);
    }
}
