package de.matrixgame.game;

import de.matrixgame.core.MatrixGameConfig;
import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.Unit;
import de.matrixgame.core.SimulationStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Belohnungssystem für den Matrixgame-Simulator
 * Verwaltet Belohnungen, Multiplikatoren und Spieltheorie-Elemente
 */
public class RewardSystem {
    
    // ========== BELOHNUNGS-TYPEN ==========
    public enum RewardType {
        EXPERIENCE, REWARD_POINTS, HEALTH, DAMAGE, SPEED, MASS, CONSCIOUSNESS
    }
    
    // ========== BELOHNUNGS-DEFINITIONEN ==========
    private Map<String, Reward> availableRewards;
    private Map<Long, List<Reward>> unitRewards; // Unit-ID -> Liste der erhaltenen Belohnungen
    
    // ========== SPIELTHEORIE-PARAMETER ==========
    private double cooperationReward;
    private double competitionReward;
    private double betrayalPenalty;
    private double trustBonus;
    
    // ========== STATISTIKEN ==========
    private SimulationStatistics statistics;
    private Random random;
    
    // ========== KONSTRUKTOR ==========
    public RewardSystem() {
        this.availableRewards = new HashMap<>();
        this.unitRewards = new HashMap<>();
        this.statistics = new SimulationStatistics();
        this.random = ThreadLocalRandom.current();
        
        // Standard-Belohnungen initialisieren
        initializeDefaultRewards();
        
        // Spieltheorie-Parameter setzen
        this.cooperationReward = MatrixGameConfig.COOPERATION_REWARD;
        this.competitionReward = MatrixGameConfig.COMPETITION_REWARD;
        this.betrayalPenalty = MatrixGameConfig.BETRAYAL_PENALTY;
        this.trustBonus = MatrixGameConfig.TRUST_BONUS;
    }
    
    // ========== STANDARD-BELOHNUNGEN INITIALISIEREN ==========
    private void initializeDefaultRewards() {
        // Erfahrung
        availableRewards.put("EXPERIENCE_10", new Reward(
            "EXPERIENCE_10", "10 Erfahrungspunkte",
            RewardType.EXPERIENCE, 10, 0, 0
        ));
        availableRewards.put("EXPERIENCE_50", new Reward(
            "EXPERIENCE_50", "50 Erfahrungspunkte",
            RewardType.EXPERIENCE, 50, 0, 0
        ));
        availableRewards.put("EXPERIENCE_100", new Reward(
            "EXPERIENCE_100", "100 Erfahrungspunkte",
            RewardType.EXPERIENCE, 100, 0, 0
        ));
        
        // Belohnungspunkte
        availableRewards.put("POINTS_10", new Reward(
            "POINTS_10", "10 Belohnungspunkte",
            RewardType.REWARD_POINTS, 0, 10, 0
        ));
        availableRewards.put("POINTS_50", new Reward(
            "POINTS_50", "50 Belohnungspunkte",
            RewardType.REWARD_POINTS, 0, 50, 0
        ));
        availableRewards.put("POINTS_100", new Reward(
            "POINTS_100", "100 Belohnungspunkte",
            RewardType.REWARD_POINTS, 0, 100, 0
        ));
        
        // Gesundheit
        availableRewards.put("HEALTH_10", new Reward(
            "HEALTH_10", "10 Gesundheitspunkte",
            RewardType.HEALTH, 0, 0, 10
        ));
        availableRewards.put("HEALTH_50", new Reward(
            "HEALTH_50", "50 Gesundheitspunkte",
            RewardType.HEALTH, 0, 0, 50
        ));
        
        // Schaden
        availableRewards.put("DAMAGE_5", new Reward(
            "DAMAGE_5", "+5 Schaden",
            RewardType.DAMAGE, 0, 0, 5
        ));
        availableRewards.put("DAMAGE_10", new Reward(
            "DAMAGE_10", "+10 Schaden",
            RewardType.DAMAGE, 0, 0, 10
        ));
        
        // Geschwindigkeit
        availableRewards.put("SPEED_0.1", new Reward(
            "SPEED_0.1", "+0.1 Geschwindigkeit",
            RewardType.SPEED, 0, 0, 0.1
        ));
        availableRewards.put("SPEED_0.5", new Reward(
            "SPEED_0.5", "+0.5 Geschwindigkeit",
            RewardType.SPEED, 0, 0, 0.5
        ));
        
        // Masse
        availableRewards.put("MASS_5", new Reward(
            "MASS_5", "+5 Masse",
            RewardType.MASS, 0, 0, 5
        ));
        availableRewards.put("MASS_10", new Reward(
            "MASS_10", "+10 Masse",
            RewardType.MASS, 0, 0, 10
        ));
        
        // Bewusstsein
        availableRewards.put("CONSCIOUSNESS_10", new Reward(
            "CONSCIOUSNESS_10", "+10 Bewusstseinslevel",
            RewardType.CONSCIOUSNESS, 0, 0, 10
        ));
        availableRewards.put("CONSCIOUSNESS_50", new Reward(
            "CONSCIOUSNESS_50", "+50 Bewusstseinslevel",
            RewardType.CONSCIOUSNESS, 0, 0, 50
        ));
    }
    
    // ========== HAUPTPROZESS ==========
    public void update(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // 1. Spieltheorie-Belohnungen berechnen
        calculateGameTheoryRewards(core);
        
        // 2. Zufällige Belohnungen verteilen
        distributeRandomRewards(core);
        
        // 3. Statistiken aktualisieren
        updateStatistics(core);
    }
    
    // ========== SPIELTHEORIE-BELOHNUNGEN ==========
    private void calculateGameTheoryRewards(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Kooperationen identifizieren
        for (int i = 0; i < units.size(); i++) {
            Unit u1 = units.get(i);
            
            for (int j = i + 1; j < units.size(); j++) {
                Unit u2 = units.get(j);
                
                // Prüfen, ob die Einheiten kooperieren
                if (areCooperating(u1, u2)) {
                    // Kooperationsbelohnung
                    applyReward(u1, RewardType.REWARD_POINTS, cooperationReward);
                    applyReward(u2, RewardType.REWARD_POINTS, cooperationReward);
                    
                    // Vertrauensbonus
                    applyReward(u1, RewardType.REWARD_POINTS, trustBonus);
                    applyReward(u2, RewardType.REWARD_POINTS, trustBonus);
                }
                
                // Prüfen, ob die Einheiten konkurrieren
                if (areCompeting(u1, u2)) {
                    // Wettbewerbsbelohnung
                    applyReward(u1, RewardType.REWARD_POINTS, competitionReward);
                    applyReward(u2, RewardType.REWARD_POINTS, competitionReward);
                }
                
                // Prüfen, ob eine Einheit die andere verrät
                if (hasBetrayed(u1, u2)) {
                    // Verratsstrafe
                    applyReward(u1, RewardType.REWARD_POINTS, betrayalPenalty);
                    applyReward(u2, RewardType.REWARD_POINTS, -betrayalPenalty);
                }
                
                if (hasBetrayed(u2, u1)) {
                    // Verratsstrafe
                    applyReward(u2, RewardType.REWARD_POINTS, betrayalPenalty);
                    applyReward(u1, RewardType.REWARD_POINTS, -betrayalPenalty);
                }
            }
        }
    }
    
    // ========== KOOPERATION PRÜFEN ==========
    private boolean areCooperating(Unit u1, Unit u2) {
        // Einheiten kooperieren, wenn sie:
        // 1. Im selben Team sind
        // 2. Einen kooperativen Entscheidungsalgorithmus haben
        // 3. Sich in der Nähe befinden
        
        if (u1.getTeam() != u2.getTeam()) return false;
        
        String algo1 = u1.getDecisionAlgorithm();
        String algo2 = u2.getDecisionAlgorithm();
        
        boolean isCooperative1 = "COOPERATE".equals(algo1) || "TACTICAL_FOCUS".equals(algo1);
        boolean isCooperative2 = "COOPERATE".equals(algo2) || "TACTICAL_FOCUS".equals(algo2);
        
        if (!isCooperative1 && !isCooperative2) return false;
        
        double distance = u1.getDistance(u2);
        return distance < 50.0;
    }
    
    // ========== WETTBEWERB PRÜFEN ==========
    private boolean areCompeting(Unit u1, Unit u2) {
        // Einheiten konkurrieren, wenn sie:
        // 1. In verschiedenen Teams sind
        // 2. Einen kompetitiven Entscheidungsalgorithmus haben
        // 3. Sich in der Nähe befinden
        
        if (u1.getTeam() == u2.getTeam()) return false;
        
        String algo1 = u1.getDecisionAlgorithm();
        String algo2 = u2.getDecisionAlgorithm();
        
        boolean isCompetitive1 = "COMPETE".equals(algo1) || "Nearest_ENEMI".equals(algo1);
        boolean isCompetitive2 = "COMPETE".equals(algo2) || "Nearest_ENEMI".equals(algo2);
        
        if (!isCompetitive1 && !isCompetitive2) return false;
        
        double distance = u1.getDistance(u2);
        return distance < 50.0;
    }
    
    // ========== VERRAT PRÜFEN ==========
    private boolean hasBetrayed(Unit betrayer, Unit victim) {
        // Verrat liegt vor, wenn:
        // 1. Die Einheiten im selben Team sind
        // 2. Der "Verräter" den "Opfer" angreift
        
        if (betrayer.getTeam() != victim.getTeam()) return false;
        
        // Prüfen, ob der Verräter das Opfer als Ziel hat
        Unit target = betrayer.getTarget();
        return target != null && target.getId() == victim.getId();
    }
    
    // ========== ZUFÄLLIGE BELOHNUNGEN ==========
    private void distributeRandomRewards(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Alle 10 Sekunden zufällige Belohnungen verteilen
        long currentTime = System.currentTimeMillis();
        long lastRewardTime = core.getSimulationTicks() * 100; // Vereinfachung
        
        if (currentTime - lastRewardTime > 10000) {
            // Für jede Einheit eine zufällige Belohnung
            for (Unit unit : units) {
                if (unit.isAlive() && random.nextDouble() < 0.1) {
                    // Zufällige Belohnung auswählen
                    Reward reward = getRandomReward();
                    applyReward(unit, reward);
                }
            }
        }
    }
    
    // ========== ZUFÄLLIGE BELOHNUNG ABRUFEN ==========
    private Reward getRandomReward() {
        List<Reward> rewards = new ArrayList<>(availableRewards.values());
        return rewards.get(random.nextInt(rewards.size()));
    }
    
    // ========== BELOHNUNG ANWENDEN ==========
    public void applyReward(Unit unit, Reward reward) {
        applyReward(unit, reward.getType(), reward.getValue());
    }
    
    public void applyReward(Unit unit, RewardType type, double value) {
        if (!unit.isAlive()) return;
        
        switch (type) {
            case EXPERIENCE:
                unit.addExperience(value);
                break;
            case REWARD_POINTS:
                unit.addRewardPoints(value);
                break;
            case HEALTH:
                unit.setHealth(unit.getHealth() + value);
                break;
            case DAMAGE:
                unit.setDamage(unit.getDamage() + value);
                break;
            case SPEED:
                unit.setSpeed(unit.getSpeed() + value);
                break;
            case MASS:
                unit.setMass(unit.getMass() + value);
                break;
            case CONSCIOUSNESS:
                unit.evolveConsciousness(value);
                break;
        }
        
        // Belohnung in der Historie speichern
        unitRewards.computeIfAbsent(unit.getId(), k -> new ArrayList<>())
            .add(new Reward("RANDOM_", "Zufällige Belohnung", type, value, 0, 0));
    }
    
    // ========== BELOHNUNG MIT MULTIPLIKATOR ==========
    public void applyRewardWithMultiplier(Unit unit, RewardType type, double value, double multiplier) {
        applyReward(unit, type, value * multiplier);
    }
    
    // ========== BELOHNUNGEN FÜR ALLE EINHEITEN ==========
    public void applyRewardToAll(MatrixGameCore core, RewardType type, double value) {
        for (Unit unit : core.getUnits()) {
            if (unit.isAlive()) {
                applyReward(unit, type, value);
            }
        }
    }
    
    // ========== BELOHNUNGEN FÜR TEAM ==========
    public void applyRewardToTeam(MatrixGameCore core, int team, RewardType type, double value) {
        for (Unit unit : core.getUnits()) {
            if (unit.isAlive() && unit.getTeam() == team) {
                applyReward(unit, type, value);
            }
        }
    }
    
    // ========== STATISTIKEN AKTUALISIEREN ==========
    private void updateStatistics(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Gesamtzahl der Belohnungspunkte
        double totalRewardPoints = 0.0;
        for (Unit unit : units) {
            totalRewardPoints += unit.getRewardPoints();
        }
        statistics.setTotalRewardPoints(totalRewardPoints);
    }
    
    // ========== STATISTIKEN ABRUFEN ==========
    public SimulationStatistics getStatistics() {
        return statistics;
    }
    
    // ========== ANALYSE ==========
    public void analyze(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // 1. Belohnungsverteilung analysieren
        Map<RewardType, Integer> rewardDistribution = new HashMap<>();
        for (List<Reward> rewards : unitRewards.values()) {
            for (Reward reward : rewards) {
                rewardDistribution.put(reward.getType(), 
                    rewardDistribution.getOrDefault(reward.getType(), 0) + 1);
            }
        }
        
        System.out.println("=== BELOHNUNGS-ANALYSE ===");
        for (Map.Entry<RewardType, Integer> entry : rewardDistribution.entrySet()) {
            System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
        }
        
        // 2. Spieltheorie-Statistiken
        int cooperationCount = 0;
        int competitionCount = 0;
        int betrayalCount = 0;
        
        for (int i = 0; i < units.size(); i++) {
            Unit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                Unit u2 = units.get(j);
                if (areCooperating(u1, u2)) cooperationCount++;
                if (areCompeting(u1, u2)) competitionCount++;
                if (hasBetrayed(u1, u2) || hasBetrayed(u2, u1)) betrayalCount++;
            }
        }
        
        System.out.println("\n=== SPIELTHEORIE-STATISTIKEN ===");
        System.out.printf("  Kooperationen: %d%n", cooperationCount);
        System.out.printf("  Wettbewerbe: %d%n", competitionCount);
        System.out.printf("  Verrate: %d%n", betrayalCount);
        
        // 3. Warnungen
        if (betrayalCount > cooperationCount * 0.5) {
            System.out.println("WARNUNG: Hohe Verratsrate - System könnte instabil werden");
        }
        
        if (cooperationCount > competitionCount * 2) {
            System.out.println("INFO: Hohe Kooperationsrate - System ist stabil");
        }
    }
    
    // ========== GETTER/SETTER ==========
    public Map<String, Reward> getAvailableRewards() {
        return new HashMap<>(availableRewards);
    }
    
    public Reward getRewardById(String rewardId) {
        return availableRewards.get(rewardId);
    }
    
    public double getCooperationReward() {
        return cooperationReward;
    }
    
    public void setCooperationReward(double cooperationReward) {
        this.cooperationReward = cooperationReward;
    }
    
    public double getCompetitionReward() {
        return competitionReward;
    }
    
    public void setCompetitionReward(double competitionReward) {
        this.competitionReward = competitionReward;
    }
    
    public double getBetrayalPenalty() {
        return betrayalPenalty;
    }
    
    public void setBetrayalPenalty(double betrayalPenalty) {
        this.betrayalPenalty = betrayalPenalty;
    }
    
    public double getTrustBonus() {
        return trustBonus;
    }
    
    public void setTrustBonus(double trustBonus) {
        this.trustBonus = trustBonus;
    }
    
    // ========== INNER KLASSE: BELOHNUNG ==========
    public static class Reward {
        private String id;
        private String name;
        private RewardType type;
        private double experienceValue;
        private double rewardPointsValue;
        private double numericValue;
        
        public Reward(String id, String name, RewardType type, double experienceValue, 
                     double rewardPointsValue, double numericValue) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.experienceValue = experienceValue;
            this.rewardPointsValue = rewardPointsValue;
            this.numericValue = numericValue;
        }
        
        public Reward(String id, String name, RewardType type, double value1, double value2, double value3) {
            this(id, name, type, value1, value2, value3);
        }
        
        // Getter
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public RewardType getType() {
            return type;
        }
        
        public double getValue() {
            switch (type) {
                case EXPERIENCE: return experienceValue;
                case REWARD_POINTS: return rewardPointsValue;
                default: return numericValue;
            }
        }
        
        public double getExperienceValue() {
            return experienceValue;
        }
        
        public double getRewardPointsValue() {
            return rewardPointsValue;
        }
        
        public double getNumericValue() {
            return numericValue;
        }
        
        @Override
        public String toString() {
            return String.format("Reward[%s] %s (%s: %.2f)", id, name, type, getValue());
        }
    }
}
