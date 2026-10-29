package de.matrixgame.api;

import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.Unit;
import de.matrixgame.core.GameStatePrediction;
import de.matrixgame.core.SimulationStatistics;
import de.matrixgame.physics.Vector3D;
import de.matrixgame.game.QuestSystem;
import de.matrixgame.game.RewardSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Programmierbare Schnittstelle (API) für den Matrixgame-Simulator
 * Ermöglicht externe Steuerung, Beobachtung und Erweiterung
 */
public class MatrixGameAPI {
    
    // ========== INSTANZ ==========
    private MatrixGameCore core;
    
    // ========== EVENT-HANDLER ==========
    private Map<String, List<Consumer<Object>>> eventHandlers;
    private Map<String, List<Function<Object, Object>>> queryHandlers;
    
    // ========== KONSTRUKTOR ==========
    public MatrixGameAPI(MatrixGameCore core) {
        this.core = core;
        this.eventHandlers = new HashMap<>();
        this.queryHandlers = new HashMap<>();
        
        // Standard-Event-Handler registrieren
        registerDefaultHandlers();
    }
    
    // ========== STANDARD-HANDLER REGISTRIEREN ==========
    private void registerDefaultHandlers() {
        // Simulation-Events
        registerEventHandler("SIMULATION_START", this::handleSimulationStart);
        registerEventHandler("SIMULATION_STOP", this::handleSimulationStop);
        registerEventHandler("SIMULATION_PAUSE", this::handleSimulationPause);
        registerEventHandler("SIMULATION_RESUME", this::handleSimulationResume);
        registerEventHandler("SIMULATION_RESET", this::handleSimulationReset);
        
        // Unit-Events
        registerEventHandler("UNIT_CREATED", this::handleUnitCreated);
        registerEventHandler("UNIT_DESTROYED", this::handleUnitDestroyed);
        registerEventHandler("UNIT_LEVEL_UP", this::handleUnitLevelUp);
        registerEventHandler("UNIT_CONSCIOUSNESS_EVOLVED", this::handleConsciousnessEvolved);
        
        // Quest-Events
        registerEventHandler("QUEST_COMPLETED", this::handleQuestCompleted);
        registerEventHandler("ACHIEVEMENT_UNLOCKED", this::handleAchievementUnlocked);
        
        // Physik-Events
        registerEventHandler("COLLISION", this::handleCollision);
        registerEventHandler("MERGE", this::handleMerge);
        
        // Query-Handler
        registerQueryHandler("GET_UNITS", this::handleGetUnits);
        registerQueryHandler("GET_UNIT", this::handleGetUnit);
        registerQueryHandler("GET_STATISTICS", this::handleGetStatistics);
        registerQueryHandler("GET_PREDICTION", this::handleGetPrediction);
        registerQueryHandler("GET_QUESTS", this::handleGetQuests);
        registerQueryHandler("GET_ACHIEVEMENTS", this::handleGetAchievements);
    }
    
    // ========== EVENT-HANDLING ==========
    public void registerEventHandler(String eventType, Consumer<Object> handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    public void unregisterEventHandler(String eventType, Consumer<Object> handler) {
        List<Consumer<Object>> handlers = eventHandlers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }
    
    public void triggerEvent(String eventType, Object data) {
        List<Consumer<Object>> handlers = eventHandlers.get(eventType);
        if (handlers != null) {
            for (Consumer<Object> handler : handlers) {
                try {
                    handler.accept(data);
                } catch (Exception e) {
                    System.err.println("Fehler im Event-Handler für " + eventType + ": " + e.getMessage());
                }
            }
        }
    }
    
    // ========== QUERY-HANDLING ==========
    public void registerQueryHandler(String queryType, Function<Object, Object> handler) {
        queryHandlers.computeIfAbsent(queryType, k -> new ArrayList<>()).add(handler);
    }
    
    public void unregisterQueryHandler(String queryType, Function<Object, Object> handler) {
        List<Function<Object, Object>> handlers = queryHandlers.get(queryType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T query(String queryType, Object parameter) {
        List<Function<Object, Object>> handlers = queryHandlers.get(queryType);
        if (handlers != null && !handlers.isEmpty()) {
            return (T) handlers.get(0).apply(parameter);
        }
        return null;
    }
    
    // ========== STANDARD-EVENT-HANDLER ==========
    private void handleSimulationStart(Object data) {
        System.out.println("API: Simulation gestartet");
    }
    
    private void handleSimulationStop(Object data) {
        System.out.println("API: Simulation angehalten");
    }
    
    private void handleSimulationPause(Object data) {
        System.out.println("API: Simulation pausiert");
    }
    
    private void handleSimulationResume(Object data) {
        System.out.println("API: Simulation fortgesetzt");
    }
    
    private void handleSimulationReset(Object data) {
        System.out.println("API: Simulation zurueckgesetzt");
    }
    
    private void handleUnitCreated(Object data) {
        if (data instanceof Unit) {
            Unit unit = (Unit) data;
            System.out.println("API: Einheit erstellt - " + unit.getName());
        }
    }
    
    private void handleUnitDestroyed(Object data) {
        if (data instanceof Unit) {
            Unit unit = (Unit) data;
            System.out.println("API: Einheit vernichtet - " + unit.getName());
        }
    }
    
    private void handleUnitLevelUp(Object data) {
        if (data instanceof Unit) {
            Unit unit = (Unit) data;
            System.out.println("API: Einheit Level Up - " + unit.getName() + " (Level " + unit.getLevel() + ")");
        }
    }
    
    private void handleConsciousnessEvolved(Object data) {
        if (data instanceof Unit) {
            Unit unit = (Unit) data;
            System.out.println("API: Bewusstsein entwickelt - " + unit.getName() + 
                             " (Level: " + unit.getConsciousnessLevel() + ")");
        }
    }
    
    private void handleQuestCompleted(Object data) {
        if (data instanceof QuestSystem.Quest) {
            QuestSystem.Quest quest = (QuestSystem.Quest) data;
            System.out.println("API: Quest abgeschlossen - " + quest.getName());
        }
    }
    
    private void handleAchievementUnlocked(Object data) {
        if (data instanceof QuestSystem.Achievement) {
            QuestSystem.Achievement achievement = (QuestSystem.Achievement) data;
            System.out.println("API: Achievement freigeschaltet - " + achievement.getName());
        }
    }
    
    private void handleCollision(Object data) {
        System.out.println("API: Kollision erkannt");
    }
    
    private void handleMerge(Object data) {
        System.out.println("API: Verschmelzung erkannt");
    }
    
    // ========== STANDARD-QUERY-HANDLER ==========
    private Object handleGetUnits(Object parameter) {
        return core.getUnits();
    }
    
    private Object handleGetUnit(Object parameter) {
        if (parameter instanceof Long) {
            return core.getUnitById((Long) parameter);
        } else if (parameter instanceof String) {
            try {
                long id = Long.parseLong((String) parameter);
                return core.getUnitById(id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Object handleGetStatistics(Object parameter) {
        return core.getStatistics();
    }
    
    private Object handleGetPrediction(Object parameter) {
        if (parameter instanceof Integer) {
            return core.predictNextState((Integer) parameter);
        }
        return core.predictNextState(5); // Standard: 5 Schritte
    }
    
    private Object handleGetQuests(Object parameter) {
        return core.getQuestSystem().getActiveQuests();
    }
    
    private Object handleGetAchievements(Object parameter) {
        return core.getQuestSystem().getAvailableAchievements();
    }
    
    // ========== SIMULATIONS-KONTROLLE ==========
    public void startSimulation() {
        core.startSimulation();
        triggerEvent("SIMULATION_START", null);
    }
    
    public void stopSimulation() {
        core.stopSimulation();
        triggerEvent("SIMULATION_STOP", null);
    }
    
    public void pauseSimulation() {
        core.pauseSimulation();
        triggerEvent("SIMULATION_PAUSE", null);
    }
    
    public void resumeSimulation() {
        core.resumeSimulation();
        triggerEvent("SIMULATION_RESUME", null);
    }
    
    public void resetSimulation() {
        core.resetSimulation();
        triggerEvent("SIMULATION_RESET", null);
    }
    
    // ========== EINHEITEN-MANAGEMENT ==========
    public Unit createUnit() {
        Unit unit = core.createUnit();
        triggerEvent("UNIT_CREATED", unit);
        return unit;
    }
    
    public Unit createUnit(String type, int team, double x, double y, double z) {
        Unit unit = core.createUnit(type, team, x, y, z);
        triggerEvent("UNIT_CREATED", unit);
        return unit;
    }
    
    public void addUnit(Unit unit) {
        core.addUnit(unit);
        triggerEvent("UNIT_CREATED", unit);
    }
    
    public boolean removeUnit(Unit unit) {
        boolean removed = core.removeUnit(unit);
        if (removed) {
            triggerEvent("UNIT_DESTROYED", unit);
        }
        return removed;
    }
    
    public boolean removeUnit(long id) {
        Unit unit = core.getUnitById(id);
        boolean removed = core.removeUnit(id);
        if (removed && unit != null) {
            triggerEvent("UNIT_DESTROYED", unit);
        }
        return removed;
    }
    
    public Unit getUnit(long id) {
        return core.getUnitById(id);
    }
    
    public List<Unit> getUnits() {
        return core.getUnits();
    }
    
    public List<Unit> getUnitsByTeam(int team) {
        return core.getUnitsByTeam(team);
    }
    
    public List<Unit> getUnitsByType(String type) {
        return core.getUnitsByType(type);
    }
    
    // ========== EINHEITEN-STEUERUNG ==========
    public void moveUnit(long unitId, double x, double y, double z, double speed) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.moveTo(x, y, z, speed);
        }
    }
    
    public void moveUnit(long unitId, long targetUnitId) {
        Unit unit = core.getUnitById(unitId);
        Unit target = core.getUnitById(targetUnitId);
        if (unit != null && target != null) {
            unit.moveTo(target);
        }
    }
    
    public void attackUnit(long unitId, long targetUnitId) {
        Unit unit = core.getUnitById(unitId);
        Unit target = core.getUnitById(targetUnitId);
        if (unit != null && target != null) {
            unit.attack(target);
        }
    }
    
    public void setUnitDecisionAlgorithm(long unitId, String algorithm) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setDecisionAlgorithm(algorithm);
        }
    }
    
    public void setUnitTarget(long unitId, long targetUnitId) {
        Unit unit = core.getUnitById(unitId);
        Unit target = core.getUnitById(targetUnitId);
        if (unit != null) {
            unit.setTarget(target);
        }
    }
    
    public void setUnitTargetPosition(long unitId, double x, double y, double z) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setTargetPosition(x, y, z);
        }
    }
    
    // ========== BEWUSSTSEINS-STEUERUNG ==========
    public void evolveUnitConsciousness(long unitId, double amount) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            double oldLevel = unit.getConsciousnessLevel();
            unit.evolveConsciousness(amount);
            double newLevel = unit.getConsciousnessLevel();
            
            if (newLevel - oldLevel >= 50) {
                triggerEvent("UNIT_CONSCIOUSNESS_EVOLVED", unit);
            }
        }
    }
    
    public void setUnitConsciousnessLevel(long unitId, double level) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setConsciousnessLevel(level);
        }
    }
    
    public void setUnitConsciousnessMode(long unitId, String mode) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setConsciousnessMode(mode);
        }
    }
    
    // ========== PHYSIK-STEUERUNG ==========
    public void setUnitPosition(long unitId, double x, double y, double z) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setPosition(x, y, z);
        }
    }
    
    public void setUnitVelocity(long unitId, double vx, double vy, double vz) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setVelocity(vx, vy, vz);
        }
    }
    
    public void setUnitMass(long unitId, double mass) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setMass(mass);
        }
    }
    
    public void setUnitRadius(long unitId, double radius) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            unit.setRadius(radius);
        }
    }
    
    // ========== PROGNOSE ==========
    public GameStatePrediction predict(int steps) {
        return core.predictNextState(steps);
    }
    
    public List<GameStatePrediction> predictMultiple(int steps, int simulations) {
        return core.predictMultipleOutcomes(steps, simulations);
    }
    
    // ========== STATISTIKEN ==========
    public SimulationStatistics getStatistics() {
        return core.getStatistics();
    }
    
    public double getSimulationTime() {
        return core.getUniverseTime();
    }
    
    public long getSimulationTicks() {
        return core.getSimulationTicks();
    }
    
    public double getGlobalEntropy() {
        return core.getGlobalEntropyPool();
    }
    
    public void setGlobalEntropy(double entropy) {
        core.setGlobalEntropyPool(entropy);
    }
    
    // ========== QUESTS UND ACHIEVEMENTS ==========
    public QuestSystem getQuestSystem() {
        return core.getQuestSystem();
    }
    
    public RewardSystem getRewardSystem() {
        return core.getRewardSystem();
    }
    
    // ========== KONFIGURATION ==========
    public void setConfiguration(String key, Object value) {
        // Hier könnte man eine Konfigurations-API implementieren
        // Für jetzt nur einfache Parameter
        if ("GRAVITATIONAL_CONSTANT".equals(key) && value instanceof Double) {
            de.matrixgame.core.MatrixGameConfig.GRAVITATIONAL_CONSTANT = (Double) value;
        } else if ("ENTROPY_REGENERATION_RATE".equals(key) && value instanceof Double) {
            de.matrixgame.core.MatrixGameConfig.ENTROPY_REGENERATION_RATE = (Double) value;
        } else if ("LOG_PROBABILITY".equals(key) && value instanceof Double) {
            de.matrixgame.core.MatrixGameConfig.LOG_PROBABILITY = (Double) value;
        }
    }
    
    public Object getConfiguration(String key) {
        switch (key) {
            case "GRAVITATIONAL_CONSTANT":
                return de.matrixgame.core.MatrixGameConfig.GRAVITATIONAL_CONSTANT;
            case "ENTROPY_REGENERATION_RATE":
                return de.matrixgame.core.MatrixGameConfig.ENTROPY_REGENERATION_RATE;
            case "LOG_PROBABILITY":
                return de.matrixgame.core.MatrixGameConfig.LOG_PROBABILITY;
            case "MAX_UNITS":
                return de.matrixgame.core.MatrixGameConfig.MAX_UNITS;
            case "SIMULATION_TICKS":
                return core.getSimulationTicks();
            case "UNIVERSE_TIME":
                return core.getUniverseTime();
            default:
                return null;
        }
    }
    
    // ========== SKRIPT-AUSFÜHRUNG ==========
    public void executeScript(String script) {
        // Einfache Skript-Ausführung
        // In einer echten Implementierung würde man hier einen Parser verwenden
        System.out.println("Skript-Ausführung: " + script);
        
        // Beispiel: "create_unit King 0 0 0 0"
        String[] parts = script.split("\\s+");
        if (parts.length > 0) {
            switch (parts[0].toLowerCase()) {
                case "create_unit":
                    if (parts.length >= 5) {
                        String type = parts[1];
                        int team = Integer.parseInt(parts[2]);
                        double x = Double.parseDouble(parts[3]);
                        double y = Double.parseDouble(parts[4]);
                        double z = parts.length > 5 ? Double.parseDouble(parts[5]) : 0;
                        createUnit(type, team, x, y, z);
                    }
                    break;
                case "move_unit":
                    if (parts.length >= 6) {
                        long unitId = Long.parseLong(parts[1]);
                        double x = Double.parseDouble(parts[2]);
                        double y = Double.parseDouble(parts[3]);
                        double z = Double.parseDouble(parts[4]);
                        double speed = parts.length > 5 ? Double.parseDouble(parts[5]) : 1.0;
                        moveUnit(unitId, x, y, z, speed);
                    }
                    break;
                case "attack":
                    if (parts.length >= 3) {
                        long unitId = Long.parseLong(parts[1]);
                        long targetId = Long.parseLong(parts[2]);
                        attackUnit(unitId, targetId);
                    }
                    break;
                case "set_entropy":
                    if (parts.length >= 2) {
                        double entropy = Double.parseDouble(parts[1]);
                        setGlobalEntropy(entropy);
                    }
                    break;
                default:
                    System.out.println("Unbekanntes Kommando: " + parts[0]);
            }
        }
    }
    
    // ========== BATCH-OPERATIONEN ==========
    public void executeBatch(List<String> scripts) {
        for (String script : scripts) {
            executeScript(script);
        }
    }
    
    // ========== UTILITY ==========
    public int getUnitCount() {
        return core.getUnitCount();
    }
    
    public double getAverageConsciousness() {
        return core.getAverageConsciousness();
    }
    
    public double getTotalMass() {
        return core.getTotalMass();
    }
    
    public double getTotalEnergy() {
        return core.getTotalEnergy();
    }
    
    // ========== ZUSTANDS-ABFRAGE ==========
    public boolean isSimulationRunning() {
        return core.isRunning();
    }
    
    public String getSimulationStatus() {
        if (core.isRunning()) {
            return "RUNNING";
        } else if (core.getSimulationTicks() > 0) {
            return "PAUSED";
        } else {
            return "STOPPED";
        }
    }
    
    // ========== KOMFORTABLE METHODEN ==========
    public void printSimulationStatus() {
        System.out.println("=== SIMULATIONS-STATUS ===");
        System.out.println("Status: " + getSimulationStatus());
        System.out.println("Ticks: " + core.getSimulationTicks());
        System.out.println("Zeit: " + core.getUniverseTime());
        System.out.println("Einheiten: " + core.getUnitCount());
        System.out.println("Entropie: " + core.getGlobalEntropyPool());
        System.out.println("Durchschnittliches Bewusstsein: " + core.getAverageConsciousness());
        System.out.println("Gesamtmasse: " + core.getTotalMass());
        System.out.println("Gesamtenergie: " + core.getTotalEnergy());
    }
    
    public void printUnitStatus(long unitId) {
        Unit unit = core.getUnitById(unitId);
        if (unit != null) {
            System.out.println("=== EINHEITEN-STATUS ===");
            System.out.println("ID: " + unit.getId());
            System.out.println("Name: " + unit.getName());
            System.out.println("Typ: " + unit.getType());
            System.out.println("Team: " + unit.getTeam());
            System.out.println("Position: (" + unit.getX() + ", " + unit.getY() + ", " + unit.getZ() + ")");
            System.out.println("Geschwindigkeit: (" + unit.getVx() + ", " + unit.getVy() + ", " + unit.getVz() + ")");
            System.out.println("Masse: " + unit.getMass());
            System.out.println("Radius: " + unit.getRadius());
            System.out.println("Gesundheit: " + unit.getHealth() + "/" + unit.getMaxHealth());
            System.out.println("Level: " + unit.getLevel());
            System.out.println("Erfahrung: " + unit.getExperience() + "/" + unit.getExperienceToNextLevel());
            System.out.println("Bewusstsein: " + unit.getConsciousnessLevel() + " (" + unit.getConsciousnessMode() + ")");
            System.out.println("Belohnungspunkte: " + unit.getRewardPoints());
            System.out.println("Algorithmus: " + unit.getDecisionAlgorithm());
            System.out.println("Ziel: " + (unit.getTarget() != null ? unit.getTarget().getId() : "kein Ziel"));
        }
    }
}
