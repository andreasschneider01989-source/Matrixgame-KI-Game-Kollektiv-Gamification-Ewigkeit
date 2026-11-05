package de.matrixgame.collective;

import java.util.Random;

/**
 * Einfaches vererbbares Genom für CollectiveAgent.
 * Enthält evolvierbare Parameter für Sensoren, Aktionen und Kooperation.
 * Später erweiterbar um neuronale Gewichte.
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

    private static final Random random = new Random();

    public AgentGenome() {
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

    public AgentGenome(AgentGenome other) {
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

    public AgentGenome mutate() {
        AgentGenome child = new AgentGenome(this);
        double rate = this.mutationRate;

        child.foodSensitivity   = mutateValue(child.foodSensitivity, rate);
        child.agentSensitivity  = mutateValue(child.agentSensitivity, rate);
        child.dangerSensitivity = mutateValue(child.dangerSensitivity, rate);
        child.moveTendency      = mutateValue(child.moveTendency, rate);
        child.eatTendency       = mutateValue(child.eatTendency, rate);
        child.shareTendency     = mutateValue(child.shareTendency, rate);
        child.warnTendency      = mutateValue(child.warnTendency, rate);
        child.attackTendency    = mutateValue(child.attackTendency, rate);
        child.energyEfficiency  = mutateValue(child.energyEfficiency, rate);

        child.mutationRate = mutateValue(child.mutationRate, rate * 0.5);
        child.mutationRate = Math.max(0.01, Math.min(0.3, child.mutationRate));

        return child;
    }

    private double mutateValue(double value, double rate) {
        if (random.nextDouble() < rate) {
            double change = (random.nextDouble() - 0.5) * 0.4;
            value += change;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    @Override
    public String toString() {
        return String.format(
            "Genome[food=%.2f, share=%.2f, attack=%.2f, efficiency=%.2f]",
            foodSensitivity, shareTendency, attackTendency, energyEfficiency
        );
    }
}
