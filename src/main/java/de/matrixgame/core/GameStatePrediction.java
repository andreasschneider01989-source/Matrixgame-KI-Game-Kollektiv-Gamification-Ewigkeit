package de.matrixgame.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prognose des Spielzustands
 * Enthält Vorhersagen über zukünftige Zustände und Wahrscheinlichkeiten
 */
public class GameStatePrediction {
    
    // ========== ZEITBEZUG ==========
    private double predictionTime;
    private int predictionSteps;
    
    // ========== PHYSIKALISCHE PROGNOSEN ==========
    private Map<Long, Vector3D> predictedPositions;
    private Map<Long, Vector3D> predictedVelocities;
    private Map<Long, Double> predictedHealths;
    
    // ========== BEWUSSTSEINS-PROGNOSEN ==========
    private Map<Long, Double> predictedConsciousnessLevels;
    private Map<Long, String> predictedConsciousnessModes;
    
    // ========== WAHRSCHEINLICHKEITEN ==========
    private Map<Long, Double> survivalProbabilities;
    private Map<Long, Double> levelUpProbabilities;
    private Map<Long, Double> questCompletionProbabilities;
    
    // ========== KOLLEKTIVE PROGNOSEN ==========
    private Map<Integer, Double> teamSurvivalProbabilities;
    private Map<Integer, Double> teamVictoryProbabilities;
    private double globalStabilityProbability;
    
    // ========== ENTSCHEIDUNGS-OPTIONEN ==========
    private List<DecisionOption> bestDecisionOptions;
    private DecisionOption recommendedDecision;
    
    // ========== METRIKEN ==========
    private double confidence;
    private double entropy;
    private double complexity;
    
    // ========== KONSTRUKTOR ==========
    public GameStatePrediction() {
        this.predictedPositions = new HashMap<>();
        this.predictedVelocities = new HashMap<>();
        this.predictedHealths = new HashMap<>();
        this.predictedConsciousnessLevels = new HashMap<>();
        this.predictedConsciousnessModes = new HashMap<>();
        this.survivalProbabilities = new HashMap<>();
        this.levelUpProbabilities = new HashMap<>();
        this.questCompletionProbabilities = new HashMap<>();
        this.teamSurvivalProbabilities = new HashMap<>();
        this.teamVictoryProbabilities = new HashMap<>();
        this.bestDecisionOptions = new ArrayList<>();
    }
    
    // ========== GETTER/SETTER ==========
    public double getPredictionTime() {
        return predictionTime;
    }
    
    public void setPredictionTime(double predictionTime) {
        this.predictionTime = predictionTime;
    }
    
    public int getPredictionSteps() {
        return predictionSteps;
    }
    
    public void setPredictionSteps(int predictionSteps) {
        this.predictionSteps = predictionSteps;
    }
    
    public Map<Long, Vector3D> getPredictedPositions() {
        return predictedPositions;
    }
    
    public void setPredictedPositions(Map<Long, Vector3D> predictedPositions) {
        this.predictedPositions = predictedPositions;
    }
    
    public Map<Long, Vector3D> getPredictedVelocities() {
        return predictedVelocities;
    }
    
    public void setPredictedVelocities(Map<Long, Vector3D> predictedVelocities) {
        this.predictedVelocities = predictedVelocities;
    }
    
    public Map<Long, Double> getPredictedHealths() {
        return predictedHealths;
    }
    
    public void setPredictedHealths(Map<Long, Double> predictedHealths) {
        this.predictedHealths = predictedHealths;
    }
    
    public Map<Long, Double> getPredictedConsciousnessLevels() {
        return predictedConsciousnessLevels;
    }
    
    public void setPredictedConsciousnessLevels(Map<Long, Double> predictedConsciousnessLevels) {
        this.predictedConsciousnessLevels = predictedConsciousnessLevels;
    }
    
    public Map<Long, String> getPredictedConsciousnessModes() {
        return predictedConsciousnessModes;
    }
    
    public void setPredictedConsciousnessModes(Map<Long, String> predictedConsciousnessModes) {
        this.predictedConsciousnessModes = predictedConsciousnessModes;
    }
    
    public Map<Long, Double> getSurvivalProbabilities() {
        return survivalProbabilities;
    }
    
    public void setSurvivalProbabilities(Map<Long, Double> survivalProbabilities) {
        this.survivalProbabilities = survivalProbabilities;
    }
    
    public Map<Long, Double> getLevelUpProbabilities() {
        return levelUpProbabilities;
    }
    
    public void setLevelUpProbabilities(Map<Long, Double> levelUpProbabilities) {
        this.levelUpProbabilities = levelUpProbabilities;
    }
    
    public Map<Long, Double> getQuestCompletionProbabilities() {
        return questCompletionProbabilities;
    }
    
    public void setQuestCompletionProbabilities(Map<Long, Double> questCompletionProbabilities) {
        this.questCompletionProbabilities = questCompletionProbabilities;
    }
    
    public Map<Integer, Double> getTeamSurvivalProbabilities() {
        return teamSurvivalProbabilities;
    }
    
    public void setTeamSurvivalProbabilities(Map<Integer, Double> teamSurvivalProbabilities) {
        this.teamSurvivalProbabilities = teamSurvivalProbabilities;
    }
    
    public Map<Integer, Double> getTeamVictoryProbabilities() {
        return teamVictoryProbabilities;
    }
    
    public void setTeamVictoryProbabilities(Map<Integer, Double> teamVictoryProbabilities) {
        this.teamVictoryProbabilities = teamVictoryProbabilities;
    }
    
    public double getGlobalStabilityProbability() {
        return globalStabilityProbability;
    }
    
    public void setGlobalStabilityProbability(double globalStabilityProbability) {
        this.globalStabilityProbability = globalStabilityProbability;
    }
    
    public List<DecisionOption> getBestDecisionOptions() {
        return bestDecisionOptions;
    }
    
    public void setBestDecisionOptions(List<DecisionOption> bestDecisionOptions) {
        this.bestDecisionOptions = bestDecisionOptions;
    }
    
    public DecisionOption getRecommendedDecision() {
        return recommendedDecision;
    }
    
    public void setRecommendedDecision(DecisionOption recommendedDecision) {
        this.recommendedDecision = recommendedDecision;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public double getEntropy() {
        return entropy;
    }
    
    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }
    
    public double getComplexity() {
        return complexity;
    }
    
    public void setComplexity(double complexity) {
        this.complexity = complexity;
    }
    
    // ========== PROGNOSE-FÜR EINZELNE UNIT ==========
    public void addUnitPrediction(long unitId, Vector3D position, Vector3D velocity, 
                                   double health, double consciousnessLevel, String consciousnessMode,
                                   double survivalProb, double levelUpProb, double questProb) {
        predictedPositions.put(unitId, position);
        predictedVelocities.put(unitId, velocity);
        predictedHealths.put(unitId, health);
        predictedConsciousnessLevels.put(unitId, consciousnessLevel);
        predictedConsciousnessModes.put(unitId, consciousnessMode);
        survivalProbabilities.put(unitId, survivalProb);
        levelUpProbabilities.put(unitId, levelUpProb);
        questCompletionProbabilities.put(unitId, questProb);
    }
    
    // ========== PROGNOSE-FÜR TEAM ==========
    public void addTeamPrediction(int teamId, double survivalProb, double victoryProb) {
        teamSurvivalProbabilities.put(teamId, survivalProb);
        teamVictoryProbabilities.put(teamId, victoryProb);
    }
    
    // ========== ENTSCHEIDUNGS-OPTIONEN ==========
    public void addDecisionOption(DecisionOption option) {
        bestDecisionOptions.add(option);
        
        // Sortieren nach Wahrscheinlichkeit
        bestDecisionOptions.sort((o1, o2) -> Double.compare(o2.getProbability(), o1.getProbability()));
        
        // Empfohlene Entscheidung aktualisieren
        if (recommendedDecision == null || option.getProbability() > recommendedDecision.getProbability()) {
            recommendedDecision = option;
        }
    }
    
    // ========== BEWERTUNG ==========
    public double getOverallStabilityScore() {
        double score = 0.0;
        
        // Überlebenswahrscheinlichkeiten
        for (Double prob : survivalProbabilities.values()) {
            score += prob;
        }
        score /= survivalProbabilities.size();
        
        // Team-Stabilität
        for (Double prob : teamSurvivalProbabilities.values()) {
            score += prob;
        }
        score /= teamSurvivalProbabilities.size();
        
        // Globale Stabilität
        score += globalStabilityProbability;
        score /= 3.0;
        
        return score;
    }
    
    public double getOverallSuccessProbability() {
        double score = 0.0;
        
        // Level-Up-Wahrscheinlichkeiten
        for (Double prob : levelUpProbabilities.values()) {
            score += prob;
        }
        score /= Math.max(1, levelUpProbabilities.size());
        
        // Quest-Abschluss-Wahrscheinlichkeiten
        for (Double prob : questCompletionProbabilities.values()) {
            score += prob;
        }
        score /= Math.max(1, questCompletionProbabilities.size());
        
        // Team-Sieg-Wahrscheinlichkeiten
        for (Double prob : teamVictoryProbabilities.values()) {
            score += prob;
        }
        score /= Math.max(1, teamVictoryProbabilities.size());
        
        return score / 3.0;
    }
    
    // ========== STRING-REPRÄSENTATION ==========
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PROGNOSE ===\n");
        sb.append(String.format("Zeit: %.2f | Schritte: %d\n", predictionTime, predictionSteps));
        sb.append(String.format("Konfidenz: %.2f%% | Entropie: %.2f | Komplexität: %.2f\n", 
            confidence * 100, entropy, complexity));
        sb.append(String.format("Globale Stabilität: %.2f%%\n", globalStabilityProbability * 100));
        sb.append("\n=== EINHEITEN-PROGNOSEN ===\n");
        for (Long unitId : predictedPositions.keySet()) {
            sb.append(String.format("Unit %d:\n", unitId));
            sb.append(String.format("  Position: (%.2f, %.2f, %.2f)\n", 
                predictedPositions.get(unitId).getX(), 
                predictedPositions.get(unitId).getY(), 
                predictedPositions.get(unitId).getZ()));
            sb.append(String.format("  Gesundheit: %.2f%%\n", predictedHealths.getOrDefault(unitId, 100.0)));
            sb.append(String.format("  Bewusstsein: %.2f (%s)\n", 
                predictedConsciousnessLevels.getOrDefault(unitId, 0.0),
                predictedConsciousnessModes.getOrDefault(unitId, "UNKNOWN")));
            sb.append(String.format("  Überleben: %.2f%% | Level-Up: %.2f%% | Quest: %.2f%%\n",
                survivalProbabilities.getOrDefault(unitId, 0.0) * 100,
                levelUpProbabilities.getOrDefault(unitId, 0.0) * 100,
                questCompletionProbabilities.getOrDefault(unitId, 0.0) * 100));
        }
        sb.append("\n=== TEAM-PROGNOSEN ===\n");
        for (Integer teamId : teamSurvivalProbabilities.keySet()) {
            sb.append(String.format("Team %d:\n", teamId));
            sb.append(String.format("  Überleben: %.2f%% | Sieg: %.2f%%\n",
                teamSurvivalProbabilities.get(teamId) * 100,
                teamVictoryProbabilities.getOrDefault(teamId, 0.0) * 100));
        }
        sb.append("\n=== ENTSCHEIDUNGS-EMPFEHLUNGEN ===\n");
        if (recommendedDecision != null) {
            sb.append("Empfohlen: " + recommendedDecision + "\n");
        }
        sb.append("Optionen:\n");
        for (DecisionOption option : bestDecisionOptions) {
            sb.append("  " + option + "\n");
        }
        return sb.toString();
    }
    
    // ========== INNER KLASSE: ENTSCHEIDUNGS-OPTION ==========
    public static class DecisionOption {
        private String action;
        private String description;
        private double probability;
        private double expectedReward;
        private double risk;
        private Map<String, Object> parameters;
        
        public DecisionOption() {
            this.parameters = new HashMap<>();
        }
        
        public DecisionOption(String action, String description, double probability, 
                             double expectedReward, double risk) {
            this();
            this.action = action;
            this.description = description;
            this.probability = probability;
            this.expectedReward = expectedReward;
            this.risk = risk;
        }
        
        // Getter/Setter
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public double getProbability() {
            return probability;
        }
        
        public void setProbability(double probability) {
            this.probability = probability;
        }
        
        public double getExpectedReward() {
            return expectedReward;
        }
        
        public void setExpectedReward(double expectedReward) {
            this.expectedReward = expectedReward;
        }
        
        public double getRisk() {
            return risk;
        }
        
        public void setRisk(double risk) {
            this.risk = risk;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameter(String key, Object value) {
            parameters.put(key, value);
        }
        
        public Object getParameter(String key) {
            return parameters.get(key);
        }
        
        public double getCostBenefitRatio() {
            return expectedReward / Math.max(0.001, risk);
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %.2f%% (Belohnung: %.2f, Risiko: %.2f, Ratio: %.2f)",
                action, probability * 100, expectedReward, risk, getCostBenefitRatio());
        }
    }
}
