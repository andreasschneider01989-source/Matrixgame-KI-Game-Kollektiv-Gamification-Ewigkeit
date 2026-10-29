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
 * Quest-System für den Matrixgame-Simulator
 * Verwaltet Quests, Achievements und Belohnungen
 */
public class QuestSystem {
    
    // ========== QUEST-DEFINITIONEN ==========
    private List<Quest> availableQuests;
    private List<Quest> activeQuests;
    private List<Quest> completedQuests;
    
    // ========== ACHIEVEMENT-DEFINITIONEN ==========
    private List<Achievement> availableAchievements;
    private Map<Long, List<String>> unitAchievements; // Unit-ID -> Achievement-IDs
    
    // ========== STATISTIKEN ==========
    private SimulationStatistics statistics;
    private Random random;
    
    // ========== KONSTRUKTOR ==========
    public QuestSystem() {
        this.availableQuests = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.availableAchievements = new ArrayList<>();
        this.unitAchievements = new HashMap<>();
        this.statistics = new SimulationStatistics();
        this.random = ThreadLocalRandom.current();
        
        // Standard-Quests initialisieren
        initializeDefaultQuests();
        
        // Standard-Achievements initialisieren
        initializeDefaultAchievements();
    }
    
    // ========== STANDARD-QUESTS INITIALISIEREN ==========
    private void initializeDefaultQuests() {
        // 1. Überlebens-Quests
        availableQuests.add(new Quest(
            "SURVIVE_1_MINUTE", "Überleben für 1 Minute",
            "Überlebe für 60 Sekunden im Universum",
            QuestCategory.SURVIVAL, QuestDifficulty.EASY,
            100, 50, 0
        ));
        
        availableQuests.add(new Quest(
            "SURVIVE_5_MINUTES", "Überleben für 5 Minuten",
            "Überlebe für 300 Sekunden im Universum",
            QuestCategory.SURVIVAL, QuestDifficulty.MEDIUM,
            500, 250, 0
        ));
        
        availableQuests.add(new Quest(
            "SURVIVE_10_MINUTES", "Überleben für 10 Minuten",
            "Überlebe für 600 Sekunden im Universum",
            QuestCategory.SURVIVAL, QuestDifficulty.HARD,
            1000, 500, 0
        ));
        
        // 2. Entwicklungs-Quests
        availableQuests.add(new Quest(
            "REACH_LEVEL_5", "Erreiche Level 5",
            "Entwickle deine Einheit bis Level 5",
            QuestCategory.DEVELOPMENT, QuestDifficulty.EASY,
            200, 100, 0
        ));
        
        availableQuests.add(new Quest(
            "REACH_LEVEL_10", "Erreiche Level 10",
            "Entwickle deine Einheit bis Level 10",
            QuestCategory.DEVELOPMENT, QuestDifficulty.MEDIUM,
            500, 250, 0
        ));
        
        availableQuests.add(new Quest(
            "REACH_LEVEL_20", "Erreiche Level 20",
            "Entwickle deine Einheit bis Level 20",
            QuestCategory.DEVELOPMENT, QuestDifficulty.HARD,
            1500, 750, 0
        ));
        
        // 3. Bewusstseins-Quests
        availableQuests.add(new Quest(
            "REACH_COURAGE", "Erreiche Mut",
            "Entwickle dein Bewusstsein bis zum Mut-Level (200)",
            QuestCategory.CONSCIOUSNESS, QuestDifficulty.EASY,
            300, 150, 200
        ));
        
        availableQuests.add(new Quest(
            "REACH_ACCEPTANCE", "Erreiche Akzeptanz",
            "Entwickle dein Bewusstsein bis zum Akzeptanz-Level (350)",
            QuestCategory.CONSCIOUSNESS, QuestDifficulty.MEDIUM,
            800, 400, 350
        ));
        
        availableQuests.add(new Quest(
            "REACH_LOVE", "Erreiche Liebe",
            "Entwickle dein Bewusstsein bis zum Liebe-Level (500)",
            QuestCategory.CONSCIOUSNESS, QuestDifficulty.HARD,
            2000, 1000, 500
        ));
        
        // 4. Kampf-Quests
        availableQuests.add(new Quest(
            "DEFEAT_1_ENEMY", "Besiege 1 Gegner",
            "Besiege einen gegnerischen Unit",
            QuestCategory.COMBAT, QuestDifficulty.EASY,
            150, 75, 1
        ));
        
        availableQuests.add(new Quest(
            "DEFEAT_5_ENEMIES", "Besiege 5 Gegner",
            "Besiege 5 gegnerische Units",
            QuestCategory.COMBAT, QuestDifficulty.MEDIUM,
            500, 250, 5
        ));
        
        availableQuests.add(new Quest(
            "DEFEAT_10_ENEMIES", "Besiege 10 Gegner",
            "Besiege 10 gegnerische Units",
            QuestCategory.COMBAT, QuestDifficulty.HARD,
            1200, 600, 10
        ));
        
        // 5. Team-Quests
        availableQuests.add(new Quest(
            "COOPERATE_5_TIMES", "Kooperiere 5 Mal",
            "Führe 5 kooperative Aktionen durch",
            QuestCategory.TEAM, QuestDifficulty.EASY,
            200, 100, 5
        ));
        
        availableQuests.add(new Quest(
            "PROTECT_TEAMMATE", "Schütze einen Teamkollegen",
            "Verhindere, dass ein Teamkollege stirbt",
            QuestCategory.TEAM, QuestDifficulty.MEDIUM,
            400, 200, 1
        ));
        
        // 6. Physik-Quests
        availableQuests.add(new Quest(
            "REACH_SPEED_10", "Erreiche Geschwindigkeit 10",
            "Erreiche eine Geschwindigkeit von mindestens 10 Einheiten",
            QuestCategory.PHYSICS, QuestDifficulty.EASY,
            100, 50, 10
        ));
        
        availableQuests.add(new Quest(
            "TRAVEL_100_UNITS", "Reise 100 Einheiten",
            "Bewege dich insgesamt 100 Einheiten weit",
            QuestCategory.PHYSICS, QuestDifficulty.MEDIUM,
            300, 150, 100
        ));
    }
    
    // ========== STANDARD-ACHIEVEMENTS INITIALISIEREN ==========
    private void initializeDefaultAchievements() {
        // 1. Überlebens-Achievements
        availableAchievements.add(new Achievement(
            "FIRST_STEPS", "Erste Schritte",
            "Überleben für die ersten 10 Sekunden",
            AchievementCategory.SURVIVAL, AchievementRarity.COMMON,
            10
        ));
        
        availableAchievements.add(new Achievement(
            "SURVIVOR", "Überlebender",
            "Überleben für 1 Minute",
            AchievementCategory.SURVIVAL, AchievementRarity.UNCOMMON,
            50
        ));
        
        availableAchievements.add(new Achievement(
            "IMMORTAL", "Unsterblich",
            "Überleben für 10 Minuten ohne zu sterben",
            AchievementCategory.SURVIVAL, AchievementRarity.RARE,
            200
        ));
        
        // 2. Entwicklungs-Achievements
        availableAchievements.add(new Achievement(
            "LEVEL_UP", "Level Up",
            "Erreiche das erste Mal Level 2",
            AchievementCategory.DEVELOPMENT, AchievementRarity.COMMON,
            20
        ));
        
        availableAchievements.add(new Achievement(
            "MASTER", "Meister",
            "Erreiche Level 10",
            AchievementCategory.DEVELOPMENT, AchievementRarity.UNCOMMON,
            100
        ));
        
        availableAchievements.add(new Achievement(
            "GRANDMASTER", "Großmeister",
            "Erreiche Level 20",
            AchievementCategory.DEVELOPMENT, AchievementRarity.RARE,
            300
        ));
        
        // 3. Bewusstseins-Achievements
        availableAchievements.add(new Achievement(
            "AWAKENING", "Erwachen",
            "Erreiche zum ersten Mal den Mut-Level",
            AchievementCategory.CONSCIOUSNESS, AchievementRarity.COMMON,
            50
        ));
        
        availableAchievements.add(new Achievement(
            "ENLIGHTENED", "Erleuchtet",
            "Erreiche den Erleuchtungs-Level",
            AchievementCategory.CONSCIOUSNESS, AchievementRarity.RARE,
            500
        ));
        
        // 4. Kampf-Achievements
        availableAchievements.add(new Achievement(
            "FIRST_BLOOD", "Erstes Blut",
            "Besiege den ersten Gegner",
            AchievementCategory.COMBAT, AchievementRarity.COMMON,
            30
        ));
        
        availableAchievements.add(new Achievement(
            "WARRIOR", "Krieger",
            "Besiege 10 Gegner",
            AchievementCategory.COMBAT, AchievementRarity.UNCOMMON,
            100
        ));
        
        availableAchievements.add(new Achievement(
            "CONQUEROR", "Eroberer",
            "Besiege 50 Gegner",
            AchievementCategory.COMBAT, AchievementRarity.RARE,
            300
        ));
        
        // 5. Team-Achievements
        availableAchievements.add(new Achievement(
            "TEAM_PLAYER", "Teamplayer",
            "Führe die erste kooperative Aktion durch",
            AchievementCategory.TEAM, AchievementRarity.COMMON,
            25
        ));
        
        availableAchievements.add(new Achievement(
            "TEAM_LEADER", "Teamführer",
            "Führe 20 kooperative Aktionen durch",
            AchievementCategory.TEAM, AchievementRarity.UNCOMMON,
            150
        ));
        
        // 6. Physik-Achievements
        availableAchievements.add(new Achievement(
            "SPEEDSTER", "Geschwindigkeits-Dämon",
            "Erreiche eine Geschwindigkeit von 50 Einheiten",
            AchievementCategory.PHYSICS, AchievementRarity.UNCOMMON,
            75
        ));
        
        availableAchievements.add(new Achievement(
            "EXPLORER", "Erkundung",
            "Reise insgesamt 1000 Einheiten weit",
            AchievementCategory.PHYSICS, AchievementRarity.RARE,
            200
        ));
    }
    
    // ========== HAUPTPROZESS ==========
    public void update(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // 1. Neue Quests generieren
        generateNewQuests(core);
        
        // 2. Aktive Quests aktualisieren
        updateActiveQuests(core);
        
        // 3. Achievements prüfen
        checkAchievements(core);
        
        // 4. Statistiken aktualisieren
        updateStatistics(core);
    }
    
    // ========== NEUE QUESTS GENERIEREN ==========
    private void generateNewQuests(MatrixGameCore core) {
        // Alle 30 Sekunden neue Quests generieren
        long currentTime = System.currentTimeMillis();
        
        // Einfache Logik: Wenn weniger als 3 aktive Quests, neue hinzufügen
        while (activeQuests.size() < 3 && !availableQuests.isEmpty()) {
            // Zufällige Quest auswählen
            int index = random.nextInt(availableQuests.size());
            Quest quest = availableQuests.get(index);
            
            // Quest aktivieren
            Quest activeQuest = quest.clone();
            activeQuest.setStartTime(currentTime);
            activeQuest.setStatus(QuestStatus.ACTIVE);
            activeQuests.add(activeQuest);
            
            // Aus verfügbaren Quests entfernen (für diese Session)
            availableQuests.remove(index);
        }
    }
    
    // ========== AKTIVE QUESTS AKTUALISIEREN ==========
    private void updateActiveQuests(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        long currentTime = System.currentTimeMillis();
        
        for (Quest quest : new ArrayList<>(activeQuests)) {
            // Prüfen, ob Quest abgeschlossen ist
            if (isQuestComplete(quest, core, units)) {
                // Quest abschließen
                quest.setCompletionTime(currentTime);
                quest.setStatus(QuestStatus.COMPLETED);
                activeQuests.remove(quest);
                completedQuests.add(quest);
                
                // Belohnungen verteilen
                distributeQuestRewards(quest, core);
                
                if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
                    System.out.println("QUEST COMPLETE | " + quest.getName() + " abgeschlossen");
                }
            }
            
            // Prüfen, ob Quest abgelaufen ist
            if (quest.getStartTime() + quest.getTimeLimit() < currentTime) {
                quest.setStatus(QuestStatus.FAILED);
                activeQuests.remove(quest);
                
                if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
                    System.out.println("QUEST FAILED | " + quest.getName() + " abgelaufen");
                }
            }
        }
    }
    
    // ========== QUEST ABGESCHLOSSEN? ==========
    private boolean isQuestComplete(Quest quest, MatrixGameCore core, List<Unit> units) {
        switch (quest.getId()) {
            case "SURVIVE_1_MINUTE":
                return core.getUniverseTime() >= 60.0;
            case "SURVIVE_5_MINUTES":
                return core.getUniverseTime() >= 300.0;
            case "SURVIVE_10_MINUTES":
                return core.getUniverseTime() >= 600.0;
                
            case "REACH_LEVEL_5":
                return hasUnitReachedLevel(units, 5);
            case "REACH_LEVEL_10":
                return hasUnitReachedLevel(units, 10);
            case "REACH_LEVEL_20":
                return hasUnitReachedLevel(units, 20);
                
            case "REACH_COURAGE":
                return hasUnitReachedConsciousness(units, 200);
            case "REACH_ACCEPTANCE":
                return hasUnitReachedConsciousness(units, 350);
            case "REACH_LOVE":
                return hasUnitReachedConsciousness(units, 500);
                
            case "DEFEAT_1_ENEMY":
                return getTotalKills(units) >= 1;
            case "DEFEAT_5_ENEMIES":
                return getTotalKills(units) >= 5;
            case "DEFEAT_10_ENEMIES":
                return getTotalKills(units) >= 10;
                
            case "COOPERATE_5_TIMES":
                return getTotalCooperations(units) >= 5;
            case "PROTECT_TEAMMATE":
                return hasProtectedTeammate(units);
                
            case "REACH_SPEED_10":
                return hasUnitReachedSpeed(units, 10);
            case "TRAVEL_100_UNITS":
                return hasUnitTraveledDistance(units, 100);
                
            default:
                // Standard: Quest ist abgeschlossen, wenn das Ziel erreicht ist
                return quest.getCurrentProgress() >= quest.getTargetValue();
        }
    }
    
    // ========== HILFSMETHODEN ==========
    private boolean hasUnitReachedLevel(List<Unit> units, int level) {
        for (Unit unit : units) {
            if (unit.getLevel() >= level) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasUnitReachedConsciousness(List<Unit> units, double level) {
        for (Unit unit : units) {
            if (unit.getConsciousnessLevel() >= level) {
                return true;
            }
        }
        return false;
    }
    
    private int getTotalKills(List<Unit> units) {
        // Vereinfachung: Anzahl der toten Units
        int kills = 0;
        for (Unit unit : units) {
            if (!unit.isAlive()) {
                kills++;
            }
        }
        return kills;
    }
    
    private int getTotalCooperations(List<Unit> units) {
        // Vereinfachung: Anzahl der Units mit kooperativem Entscheidungsalgorithmus
        int cooperations = 0;
        for (Unit unit : units) {
            if ("COOPERATE".equals(unit.getDecisionAlgorithm()) || 
                "TACTICAL_FOCUS".equals(unit.getDecisionAlgorithm())) {
                cooperations++;
            }
        }
        return cooperations;
    }
    
    private boolean hasProtectedTeammate(List<Unit> units) {
        // Vereinfachung: Es gibt mindestens 2 lebende Units im selben Team
        Map<Integer, Integer> teamCounts = new HashMap<>();
        for (Unit unit : units) {
            if (unit.isAlive()) {
                teamCounts.put(unit.getTeam(), teamCounts.getOrDefault(unit.getTeam(), 0) + 1);
            }
        }
        for (Integer count : teamCounts.values()) {
            if (count >= 2) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasUnitReachedSpeed(List<Unit> units, double speed) {
        for (Unit unit : units) {
            if (unit.getVelocity().length() >= speed) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasUnitTraveledDistance(List<Unit> units, double distance) {
        // Vereinfachung: Prüfen, ob eine Einheit sich weit vom Ursprung entfernt hat
        for (Unit unit : units) {
            if (unit.getPosition().length() >= distance) {
                return true;
            }
        }
        return false;
    }
    
    // ========== BELOHNUNGEN VERTEILEN ==========
    private void distributeQuestRewards(Quest quest, MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Belohnungen auf alle lebenden Units verteilen
        for (Unit unit : units) {
            if (unit.isAlive()) {
                // Erfahrung
                unit.addExperience(quest.getExperienceReward());
                
                // Belohnungspunkte
                unit.addRewardPoints(quest.getRewardPoints());
                
                // Quest als abgeschlossen markieren
                unit.completeQuest(quest.getId());
            }
        }
    }
    
    // ========== ACHIEVEMENTS PRÜFEN ==========
    private void checkAchievements(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        for (Achievement achievement : availableAchievements) {
            for (Unit unit : units) {
                long unitId = unit.getId();
                
                // Prüfen, ob Achievement bereits freigeschaltet ist
                if (hasAchievement(unitId, achievement.getId())) {
                    continue;
                }
                
                // Prüfen, ob Achievement-Bedingungen erfüllt sind
                if (isAchievementUnlocked(unit, achievement)) {
                    // Achievement freischalten
                    unitAchievements.computeIfAbsent(unitId, k -> new ArrayList<>()).add(achievement.getId());
                    unit.addRewardPoints(achievement.getRewardPoints());
                    
                    if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
                        System.out.println("ACHIEVEMENT UNLOCKED | Unit " + unitId + ": " + achievement.getName());
                    }
                }
            }
        }
    }
    
    // ========== ACHIEVEMENT FREIGESCHALTET? ==========
    private boolean isAchievementUnlocked(Unit unit, Achievement achievement) {
        switch (achievement.getId()) {
            case "FIRST_STEPS":
                return core.getUniverseTime() >= 10.0;
            case "SURVIVOR":
                return core.getUniverseTime() >= 60.0;
            case "IMMORTAL":
                return core.getUniverseTime() >= 600.0 && unit.isAlive();
                
            case "LEVEL_UP":
                return unit.getLevel() >= 2;
            case "MASTER":
                return unit.getLevel() >= 10;
            case "GRANDMASTER":
                return unit.getLevel() >= 20;
                
            case "AWAKENING":
                return unit.getConsciousnessLevel() >= 200;
            case "ENLIGHTENED":
                return unit.getConsciousnessLevel() >= 700;
                
            case "FIRST_BLOOD":
                // Vereinfachung: Unit hat mindestens einen Kill
                // (In einer echten Implementierung würde man die Kills tracken)
                return unit.getExperience() >= 100;
            case "WARRIOR":
                return unit.getExperience() >= 500;
            case "CONQUEROR":
                return unit.getExperience() >= 2000;
                
            case "TEAM_PLAYER":
                return "COOPERATE".equals(unit.getDecisionAlgorithm()) || 
                       "TACTICAL_FOCUS".equals(unit.getDecisionAlgorithm());
            case "TEAM_LEADER":
                // Vereinfachung: Unit hat hohes Level und kooperativen Algorithmus
                return unit.getLevel() >= 5 && 
                       ("COOPERATE".equals(unit.getDecisionAlgorithm()) || 
                        "TACTICAL_FOCUS".equals(unit.getDecisionAlgorithm()));
                
            case "SPEEDSTER":
                return unit.getVelocity().length() >= 50;
            case "EXPLORER":
                return unit.getPosition().length() >= 1000;
                
            default:
                return false;
        }
    }
    
    // ========== ACHIEVEMENT HABEN? ==========
    public boolean hasAchievement(long unitId, String achievementId) {
        List<String> achievements = unitAchievements.get(unitId);
        return achievements != null && achievements.contains(achievementId);
    }
    
    // ========== STATISTIKEN AKTUALISIEREN ==========
    private void updateStatistics(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Gesamtzahl der abgeschlossenen Quests
        int totalQuests = completedQuests.size();
        statistics.setTotalQuestsCompleted(totalQuests);
        
        // Gesamtzahl der freigeschalteten Achievements
        int totalAchievements = 0;
        for (List<String> achievements : unitAchievements.values()) {
            totalAchievements += achievements.size();
        }
        statistics.setTotalAchievementsUnlocked(totalAchievements);
        
        // Durchschnittliches Level
        double avgLevel = 0.0;
        for (Unit unit : units) {
            avgLevel += unit.getLevel();
        }
        avgLevel /= Math.max(1, units.size());
        statistics.setAverageLevel(avgLevel);
    }
    
    // ========== STATISTIKEN ABRUFEN ==========
    public SimulationStatistics getStatistics() {
        return statistics;
    }
    
    // ========== ANALYSE ==========
    public void analyze(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // 1. Quest-Fortschritt analysieren
        for (Quest quest : activeQuests) {
            double progress = getQuestProgress(quest, core, units);
            System.out.printf("QUEST PROGRESS | %s: %.1f%%%n", quest.getName(), progress * 100);
        }
        
        // 2. Achievement-Status analysieren
        for (Achievement achievement : availableAchievements) {
            int unlockedCount = 0;
            for (Unit unit : units) {
                if (hasAchievement(unit.getId(), achievement.getId())) {
                    unlockedCount++;
                }
            }
            System.out.printf("ACHIEVEMENT STATUS | %s: %d/%d Units%n", 
                achievement.getName(), unlockedCount, units.size());
        }
        
        // 3. Warnungen ausgeben
        if (activeQuests.isEmpty()) {
            System.out.println("WARNUNG: Keine aktiven Quests - Neue Quests generieren");
        }
        
        if (completedQuests.size() > 10) {
            System.out.println("INFO: Viele Quests abgeschlossen - " + completedQuests.size() + " Quests");
        }
    }
    
    // ========== QUEST-FORTSCHRITT ABRUFEN ==========
    private double getQuestProgress(Quest quest, MatrixGameCore core, List<Unit> units) {
        switch (quest.getId()) {
            case "SURVIVE_1_MINUTE":
                return Math.min(1.0, core.getUniverseTime() / 60.0);
            case "SURVIVE_5_MINUTES":
                return Math.min(1.0, core.getUniverseTime() / 300.0);
            case "SURVIVE_10_MINUTES":
                return Math.min(1.0, core.getUniverseTime() / 600.0);
                
            case "REACH_LEVEL_5":
                return getMaxLevelProgress(units, 5);
            case "REACH_LEVEL_10":
                return getMaxLevelProgress(units, 10);
            case "REACH_LEVEL_20":
                return getMaxLevelProgress(units, 20);
                
            case "REACH_COURAGE":
                return getMaxConsciousnessProgress(units, 200);
            case "REACH_ACCEPTANCE":
                return getMaxConsciousnessProgress(units, 350);
            case "REACH_LOVE":
                return getMaxConsciousnessProgress(units, 500);
                
            case "DEFEAT_1_ENEMY":
                return Math.min(1.0, getTotalKills(units) / 1.0);
            case "DEFEAT_5_ENEMIES":
                return Math.min(1.0, getTotalKills(units) / 5.0);
            case "DEFEAT_10_ENEMIES":
                return Math.min(1.0, getTotalKills(units) / 10.0);
                
            case "COOPERATE_5_TIMES":
                return Math.min(1.0, getTotalCooperations(units) / 5.0);
            case "PROTECT_TEAMMATE":
                return hasProtectedTeammate(units) ? 1.0 : 0.0;
                
            case "REACH_SPEED_10":
                return hasUnitReachedSpeed(units, 10) ? 1.0 : 0.0;
            case "TRAVEL_100_UNITS":
                return hasUnitTraveledDistance(units, 100) ? 1.0 : 0.0;
                
            default:
                return quest.getCurrentProgress() / Math.max(1, quest.getTargetValue());
        }
    }
    
    private double getMaxLevelProgress(List<Unit> units, int targetLevel) {
        int maxLevel = 0;
        for (Unit unit : units) {
            maxLevel = Math.max(maxLevel, unit.getLevel());
        }
        return Math.min(1.0, (double) maxLevel / targetLevel);
    }
    
    private double getMaxConsciousnessProgress(List<Unit> units, double targetLevel) {
        double maxConsciousness = 0;
        for (Unit unit : units) {
            maxConsciousness = Math.max(maxConsciousness, unit.getConsciousnessLevel());
        }
        return Math.min(1.0, maxConsciousness / targetLevel);
    }
    
    // ========== GETTER/SETTER ==========
    public List<Quest> getAvailableQuests() {
        return new ArrayList<>(availableQuests);
    }
    
    public List<Quest> getActiveQuests() {
        return new ArrayList<>(activeQuests);
    }
    
    public List<Quest> getCompletedQuests() {
        return new ArrayList<>(completedQuests);
    }
    
    public List<Achievement> getAvailableAchievements() {
        return new ArrayList<>(availableAchievements);
    }
    
    public Quest getQuestById(String questId) {
        for (Quest quest : availableQuests) {
            if (quest.getId().equals(questId)) {
                return quest;
            }
        }
        for (Quest quest : activeQuests) {
            if (quest.getId().equals(questId)) {
                return quest;
            }
        }
        for (Quest quest : completedQuests) {
            if (quest.getId().equals(questId)) {
                return quest;
            }
        }
        return null;
    }
    
    public Achievement getAchievementById(String achievementId) {
        for (Achievement achievement : availableAchievements) {
            if (achievement.getId().equals(achievementId)) {
                return achievement;
            }
        }
        return null;
    }
    
    // ========== INNER KLASSEN ==========
    public static class Quest {
        private String id;
        private String name;
        private String description;
        private QuestCategory category;
        private QuestDifficulty difficulty;
        private int experienceReward;
        private int rewardPoints;
        private double targetValue;
        private QuestStatus status;
        private long startTime;
        private long completionTime;
        private double currentProgress;
        
        public Quest(String id, String name, String description, QuestCategory category, 
                    QuestDifficulty difficulty, int experienceReward, int rewardPoints, 
                    double targetValue) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.difficulty = difficulty;
            this.experienceReward = experienceReward;
            this.rewardPoints = rewardPoints;
            this.targetValue = targetValue;
            this.status = QuestStatus.AVAILABLE;
            this.currentProgress = 0;
        }
        
        // Getter/Setter
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public QuestCategory getCategory() {
            return category;
        }
        
        public QuestDifficulty getDifficulty() {
            return difficulty;
        }
        
        public int getExperienceReward() {
            return experienceReward;
        }
        
        public int getRewardPoints() {
            return rewardPoints;
        }
        
        public double getTargetValue() {
            return targetValue;
        }
        
        public QuestStatus getStatus() {
            return status;
        }
        
        public void setStatus(QuestStatus status) {
            this.status = status;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
        
        public long getCompletionTime() {
            return completionTime;
        }
        
        public void setCompletionTime(long completionTime) {
            this.completionTime = completionTime;
        }
        
        public double getCurrentProgress() {
            return currentProgress;
        }
        
        public void setCurrentProgress(double currentProgress) {
            this.currentProgress = currentProgress;
        }
        
        public long getTimeLimit() {
            // Standard: 5 Minuten für einfache Quests, 10 Minuten für mittlere, 15 Minuten für schwere
            switch (difficulty) {
                case EASY: return 300000; // 5 Minuten
                case MEDIUM: return 600000; // 10 Minuten
                case HARD: return 900000; // 15 Minuten
                default: return 300000;
            }
        }
        
        // Clone
        @Override
        public Quest clone() {
            Quest clone = new Quest(id, name, description, category, difficulty, 
                                   experienceReward, rewardPoints, targetValue);
            clone.status = status;
            clone.startTime = startTime;
            clone.completionTime = completionTime;
            clone.currentProgress = currentProgress;
            return clone;
        }
        
        @Override
        public String toString() {
            return String.format("Quest[%s] %s (%s, %s) - %s", 
                id, name, category, difficulty, description);
        }
    }
    
    public static class Achievement {
        private String id;
        private String name;
        private String description;
        private AchievementCategory category;
        private AchievementRarity rarity;
        private int rewardPoints;
        
        public Achievement(String id, String name, String description, AchievementCategory category, 
                          AchievementRarity rarity, int rewardPoints) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.rarity = rarity;
            this.rewardPoints = rewardPoints;
        }
        
        // Getter
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public AchievementCategory getCategory() {
            return category;
        }
        
        public AchievementRarity getRarity() {
            return rarity;
        }
        
        public int getRewardPoints() {
            return rewardPoints;
        }
        
        @Override
        public String toString() {
            return String.format("Achievement[%s] %s (%s, %s) - %s", 
                id, name, category, rarity, description);
        }
    }
    
    public enum QuestCategory {
        SURVIVAL, DEVELOPMENT, CONSCIOUSNESS, COMBAT, TEAM, PHYSICS
    }
    
    public enum QuestDifficulty {
        EASY, MEDIUM, HARD
    }
    
    public enum QuestStatus {
        AVAILABLE, ACTIVE, COMPLETED, FAILED
    }
    
    public enum AchievementCategory {
        SURVIVAL, DEVELOPMENT, CONSCIOUSNESS, COMBAT, TEAM, PHYSICS
    }
    
    public enum AchievementRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }
}
