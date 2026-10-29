package de.matrixgame.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistiken für die Matrixgame-Simulation
 */
public class SimulationStatistics {
    
    // ========== ALLGEMEINE STATISTIKEN ==========
    private long simulationTicks;
    private double universeTime;
    private int unitCount;
    private double globalEntropy;
    
    // ========== PHYSIK-STATISTIKEN ==========
    private double totalMass;
    private double totalEnergy;
    private double averageVelocity;
    private double maxVelocity;
    private int collisionCount;
    private int mergeCount;
    
    // ========== BEWUSSTSEINS-STATISTIKEN ==========
    private double averageConsciousness;
    private double minConsciousness;
    private double maxConsciousness;
    private Map<String, Integer> consciousnessModeCounts;
    
    // ========== GAMIFICATION-STATISTIKEN ==========
    private int totalQuestsCompleted;
    private int totalAchievementsUnlocked;
    private double totalRewardPoints;
    private double averageLevel;
    
    // ========== PROGNOSE-STATISTIKEN ==========
    private double predictionAccuracy;
    private int predictionsMade;
    
    // ========== KONSTRUKTOR ==========
    public SimulationStatistics() {
        this.consciousnessModeCounts = new HashMap<>();
    }
    
    // ========== GETTER/SETTER ==========
    public long getSimulationTicks() {
        return simulationTicks;
    }
    
    public void setSimulationTicks(long simulationTicks) {
        this.simulationTicks = simulationTicks;
    }
    
    public double getUniverseTime() {
        return universeTime;
    }
    
    public void setUniverseTime(double universeTime) {
        this.universeTime = universeTime;
    }
    
    public int getUnitCount() {
        return unitCount;
    }
    
    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }
    
    public double getGlobalEntropy() {
        return globalEntropy;
    }
    
    public void setGlobalEntropy(double globalEntropy) {
        this.globalEntropy = globalEntropy;
    }
    
    public double getTotalMass() {
        return totalMass;
    }
    
    public void setTotalMass(double totalMass) {
        this.totalMass = totalMass;
    }
    
    public double getTotalEnergy() {
        return totalEnergy;
    }
    
    public void setTotalEnergy(double totalEnergy) {
        this.totalEnergy = totalEnergy;
    }
    
    public double getAverageVelocity() {
        return averageVelocity;
    }
    
    public void setAverageVelocity(double averageVelocity) {
        this.averageVelocity = averageVelocity;
    }
    
    public double getMaxVelocity() {
        return maxVelocity;
    }
    
    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }
    
    public int getCollisionCount() {
        return collisionCount;
    }
    
    public void setCollisionCount(int collisionCount) {
        this.collisionCount = collisionCount;
    }
    
    public int getMergeCount() {
        return mergeCount;
    }
    
    public void setMergeCount(int mergeCount) {
        this.mergeCount = mergeCount;
    }
    
    public double getAverageConsciousness() {
        return averageConsciousness;
    }
    
    public void setAverageConsciousness(double averageConsciousness) {
        this.averageConsciousness = averageConsciousness;
    }
    
    public double getMinConsciousness() {
        return minConsciousness;
    }
    
    public void setMinConsciousness(double minConsciousness) {
        this.minConsciousness = minConsciousness;
    }
    
    public double getMaxConsciousness() {
        return maxConsciousness;
    }
    
    public void setMaxConsciousness(double maxConsciousness) {
        this.maxConsciousness = maxConsciousness;
    }
    
    public Map<String, Integer> getConsciousnessModeCounts() {
        return consciousnessModeCounts;
    }
    
    public void setConsciousnessModeCounts(Map<String, Integer> consciousnessModeCounts) {
        this.consciousnessModeCounts = consciousnessModeCounts;
    }
    
    public int getTotalQuestsCompleted() {
        return totalQuestsCompleted;
    }
    
    public void setTotalQuestsCompleted(int totalQuestsCompleted) {
        this.totalQuestsCompleted = totalQuestsCompleted;
    }
    
    public int getTotalAchievementsUnlocked() {
        return totalAchievementsUnlocked;
    }
    
    public void setTotalAchievementsUnlocked(int totalAchievementsUnlocked) {
        this.totalAchievementsUnlocked = totalAchievementsUnlocked;
    }
    
    public double getTotalRewardPoints() {
        return totalRewardPoints;
    }
    
    public void setTotalRewardPoints(double totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }
    
    public double getAverageLevel() {
        return averageLevel;
    }
    
    public void setAverageLevel(double averageLevel) {
        this.averageLevel = averageLevel;
    }
    
    public double getPredictionAccuracy() {
        return predictionAccuracy;
    }
    
    public void setPredictionAccuracy(double predictionAccuracy) {
        this.predictionAccuracy = predictionAccuracy;
    }
    
    public int getPredictionsMade() {
        return predictionsMade;
    }
    
    public void setPredictionsMade(int predictionsMade) {
        this.predictionsMade = predictionsMade;
    }
    
    // ========== METHODEN ZUM KOMBINIEREN ==========
    public void addPhysicsStats(SimulationStatistics physicsStats) {
        this.totalMass += physicsStats.totalMass;
        this.totalEnergy += physicsStats.totalEnergy;
        this.averageVelocity += physicsStats.averageVelocity;
        this.maxVelocity = Math.max(this.maxVelocity, physicsStats.maxVelocity);
        this.collisionCount += physicsStats.collisionCount;
        this.mergeCount += physicsStats.mergeCount;
    }
    
    public void addConsciousnessStats(SimulationStatistics consciousnessStats) {
        this.averageConsciousness += consciousnessStats.averageConsciousness;
        this.minConsciousness = Math.min(this.minConsciousness, consciousnessStats.minConsciousness);
        this.maxConsciousness = Math.max(this.maxConsciousness, consciousnessStats.maxConsciousness);
        
        // Mode-Counts kombinieren
        for (Map.Entry<String, Integer> entry : consciousnessStats.consciousnessModeCounts.entrySet()) {
            this.consciousnessModeCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }
    
    public void addQuestStats(SimulationStatistics questStats) {
        this.totalQuestsCompleted += questStats.totalQuestsCompleted;
        this.totalAchievementsUnlocked += questStats.totalAchievementsUnlocked;
        this.totalRewardPoints += questStats.totalRewardPoints;
        this.averageLevel += questStats.averageLevel;
    }
    
    public void addRewardStats(SimulationStatistics rewardStats) {
        this.totalRewardPoints += rewardStats.totalRewardPoints;
    }
    
    // ========== STRING-REPRÄSENTATION ==========
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SIMULATIONS-STATISTIK ===\n");
        sb.append(String.format("Ticks: %d\n", simulationTicks));
        sb.append(String.format("Universumszeit: %.2f\n", universeTime));
        sb.append(String.format("Einheiten: %d\n", unitCount));
        sb.append(String.format("Globale Entropie: %.2f\n", globalEntropy));
        sb.append("\n=== PHYSIK ===\n");
        sb.append(String.format("Gesamtmasse: %.2f\n", totalMass));
        sb.append(String.format("Gesamtenergie: %.2f\n", totalEnergy));
        sb.append(String.format("Durchschnittsgeschwindigkeit: %.2f\n", averageVelocity));
        sb.append(String.format("Maximalgeschwindigkeit: %.2f\n", maxVelocity));
        sb.append(String.format("Kollisionen: %d\n", collisionCount));
        sb.append(String.format("Zusammenführungen: %d\n", mergeCount));
        sb.append("\n=== BEWUSSTSEIN ===\n");
        sb.append(String.format("Durchschnitt: %.2f\n", averageConsciousness));
        sb.append(String.format("Minimum: %.2f\n", minConsciousness));
        sb.append(String.format("Maximum: %.2f\n", maxConsciousness));
        sb.append("\nModus-Verteilung:\n");
        for (Map.Entry<String, Integer> entry : consciousnessModeCounts.entrySet()) {
            sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\n=== GAMIFICATION ===\n");
        sb.append(String.format("Quests abgeschlossen: %d\n", totalQuestsCompleted));
        sb.append(String.format("Achievements freigeschaltet: %d\n", totalAchievementsUnlocked));
        sb.append(String.format("Belohnungspunkte: %.2f\n", totalRewardPoints));
        sb.append(String.format("Durchschnittslevel: %.2f\n", averageLevel));
        sb.append("\n=== PROGNOSE ===\n");
        sb.append(String.format("Genauigkeit: %.2f%%\n", predictionAccuracy * 100));
        sb.append(String.format("Prognosen: %d\n", predictionsMade));
        return sb.toString();
    }
    
    // ========== JSON-REPRÄSENTATION ==========
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"simulationTicks\": %d,\n", simulationTicks));
        sb.append(String.format("  \"universeTime\": %.2f,\n", universeTime));
        sb.append(String.format("  \"unitCount\": %d,\n", unitCount));
        sb.append(String.format("  \"globalEntropy\": %.2f,\n", globalEntropy));
        sb.append(String.format("  \"totalMass\": %.2f,\n", totalMass));
        sb.append(String.format("  \"totalEnergy\": %.2f,\n", totalEnergy));
        sb.append(String.format("  \"averageVelocity\": %.2f,\n", averageVelocity));
        sb.append(String.format("  \"maxVelocity\": %.2f,\n", maxVelocity));
        sb.append(String.format("  \"collisionCount\": %d,\n", collisionCount));
        sb.append(String.format("  \"mergeCount\": %d,\n", mergeCount));
        sb.append(String.format("  \"averageConsciousness\": %.2f,\n", averageConsciousness));
        sb.append(String.format("  \"minConsciousness\": %.2f,\n", minConsciousness));
        sb.append(String.format("  \"maxConsciousness\": %.2f,\n", maxConsciousness));
        sb.append("  \"consciousnessModeCounts\": {\n");
        for (Map.Entry<String, Integer> entry : consciousnessModeCounts.entrySet()) {
            sb.append(String.format("    \"%s\": %d,\n", entry.getKey(), entry.getValue()));
        }
        sb.append("  },\n");
        sb.append(String.format("  \"totalQuestsCompleted\": %d,\n", totalQuestsCompleted));
        sb.append(String.format("  \"totalAchievementsUnlocked\": %d,\n", totalAchievementsUnlocked));
        sb.append(String.format("  \"totalRewardPoints\": %.2f,\n", totalRewardPoints));
        sb.append(String.format("  \"averageLevel\": %.2f,\n", averageLevel));
        sb.append(String.format("  \"predictionAccuracy\": %.2f,\n", predictionAccuracy));
        sb.append(String.format("  \"predictionsMade\": %d\n", predictionsMade));
        sb.append("}");
        return sb.toString();
    }
}
