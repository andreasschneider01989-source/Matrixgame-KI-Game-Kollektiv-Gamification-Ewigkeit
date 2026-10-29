package de.matrixgame.core;

import de.matrixgame.consciousness.ConsciousnessEngine;
import de.matrixgame.physics.PhysicsEngine;
import de.matrixgame.game.QuestSystem;
import de.matrixgame.game.RewardSystem;
import de.matrixgame.api.MatrixGameAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Kernklasse des Matrixgame-Simulators
 * Integriert Physik, Bewusstsein, Gamification und Prognose
 */
public class MatrixGameCore {
    
    // ========== INSTANZ-VARIABLEN ==========
    private static MatrixGameCore instance;
    
    private List<Unit> units;
    private PhysicsEngine physicsEngine;
    private ConsciousnessEngine consciousnessEngine;
    private QuestSystem questSystem;
    private RewardSystem rewardSystem;
    private MatrixGameAPI api;
    
    private long simulationTicks = 0;
    private double globalEntropyPool;
    private double universeTime = 0.0;
    
    private boolean isRunning = false;
    private Thread gameThread;
    
    // ========== KONSTRUKTOR ==========
    private MatrixGameCore() {
        this.units = new CopyOnWriteArrayList<>();
        this.physicsEngine = new PhysicsEngine();
        this.consciousnessEngine = new ConsciousnessEngine();
        this.questSystem = new QuestSystem();
        this.rewardSystem = new RewardSystem();
        this.api = new MatrixGameAPI(this);
        this.globalEntropyPool = MatrixGameConfig.GLOBAL_ENTROPY_POOL;
    }
    
    // ========== SINGLETON ==========
    public static synchronized MatrixGameCore getInstance() {
        if (instance == null) {
            instance = new MatrixGameCore();
        }
        return instance;
    }
    
    // ========== GETTER/SETTER ==========
    public List<Unit> getUnits() {
        return units;
    }
    
    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }
    
    public ConsciousnessEngine getConsciousnessEngine() {
        return consciousnessEngine;
    }
    
    public QuestSystem getQuestSystem() {
        return questSystem;
    }
    
    public RewardSystem getRewardSystem() {
        return rewardSystem;
    }
    
    public MatrixGameAPI getAPI() {
        return api;
    }
    
    public long getSimulationTicks() {
        return simulationTicks;
    }
    
    public double getGlobalEntropyPool() {
        return globalEntropyPool;
    }
    
    public void setGlobalEntropyPool(double entropy) {
        this.globalEntropyPool = Math.max(0, Math.min(MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL, entropy));
    }
    
    public double getUniverseTime() {
        return universeTime;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    // ========== UNIT-MANAGEMENT ==========
    public Unit createUnit() {
        Unit unit = new Unit();
        units.add(unit);
        return unit;
    }
    
    public Unit createUnit(String type, int team, double x, double y, double z) {
        Unit unit = new Unit(type, team, x, y, z);
        units.add(unit);
        return unit;
    }
    
    public void addUnit(Unit unit) {
        if (units.size() < MatrixGameConfig.MAX_UNITS) {
            units.add(unit);
        }
    }
    
    public boolean removeUnit(Unit unit) {
        return units.remove(unit);
    }
    
    public void removeUnit(long id) {
        units.removeIf(u -> u.getId() == id);
    }
    
    public Unit getUnitById(long id) {
        for (Unit unit : units) {
            if (unit.getId() == id) {
                return unit;
            }
        }
        return null;
    }
    
    public List<Unit> getUnitsByTeam(int team) {
        List<Unit> teamUnits = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.getTeam() == team) {
                teamUnits.add(unit);
            }
        }
        return teamUnits;
    }
    
    public List<Unit> getUnitsByType(String type) {
        List<Unit> typeUnits = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.getType().equalsIgnoreCase(type)) {
                typeUnits.add(unit);
            }
        }
        return typeUnits;
    }
    
    // ========== SIMULATION ==========
    public void startSimulation() {
        if (isRunning) return;
        
        isRunning = true;
        gameThread = new Thread(this::gameLoop, "MatrixGame-Simulation");
        gameThread.setDaemon(true);
        gameThread.start();
        
        if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
            System.out.println("Matrixgame-Simulation gestartet");
        }
    }
    
    public void stopSimulation() {
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
            System.out.println("Matrixgame-Simulation angehalten");
        }
    }
    
    public void pauseSimulation() {
        isRunning = false;
    }
    
    public void resumeSimulation() {
        if (!isRunning) {
            startSimulation();
        }
    }
    
    public void resetSimulation() {
        stopSimulation();
        units.clear();
        simulationTicks = 0;
        universeTime = 0.0;
        globalEntropyPool = MatrixGameConfig.GLOBAL_ENTROPY_POOL;
        
        if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
            System.out.println("Matrixgame-Simulation zurueckgesetzt");
        }
    }
    
    // ========== GAME LOOP ==========
    private void gameLoop() {
        while (isRunning) {
            try {
                // 1. Tick aktualisieren
                simulationTicks++;
                universeTime += MatrixGameConfig.DEFAULT_TIME_STEP;
                
                // 2. Entropie-Pool regenerieren
                if (globalEntropyPool < MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL) {
                    globalEntropyPool = Math.min(
                        MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL,
                        globalEntropyPool + MatrixGameConfig.ENTROPY_REGENERATION_RATE
                    );
                }
                
                // 3. Physik-Engine aktualisieren
                physicsEngine.update(this, MatrixGameConfig.DEFAULT_TIME_STEP);
                
                // 4. Bewusstseins-Engine aktualisieren
                consciousnessEngine.update(this);
                
                // 5. Gamification-Systeme aktualisieren
                questSystem.update(this);
                rewardSystem.update(this);
                
                // 6. Prognose und Analyse durchführen
                analyzeGameState();
                
                // 7. Loggen (optional)
                if (MatrixGameConfig.ENABLE_GAME_LOGGING && 
                    Math.random() <= MatrixGameConfig.LOG_PROBABILITY) {
                    System.out.printf("TICK %d | Zeit: %.2f | Einheiten: %d | Entropie: %.2f%n",
                        simulationTicks, universeTime, units.size(), globalEntropyPool);
                }
                
                // 8. Warten auf nächsten Tick
                Thread.sleep((long) (MatrixGameConfig.DEFAULT_TIME_STEP * 1000));
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;
            } catch (Exception e) {
                if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
                    System.err.println("Fehler im Game-Loop: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    // ========== ANALYSE ==========
    public void analyzeGameState() {
        // 1. Physikalische Analyse
        physicsEngine.analyze(this);
        
        // 2. Bewusstseins-Analyse
        consciousnessEngine.analyze(this);
        
        // 3. Gamification-Analyse
        questSystem.analyze(this);
        rewardSystem.analyze(this);
    }
    
    // ========== PROGNOSE ==========
    public GameStatePrediction predictNextState(int steps) {
        return physicsEngine.predict(this, steps);
    }
    
    public List<GameStatePrediction> predictMultipleOutcomes(int steps, int simulations) {
        List<GameStatePrediction> predictions = new ArrayList<>();
        for (int i = 0; i < simulations; i++) {
            predictions.add(physicsEngine.predict(this, steps));
        }
        return predictions;
    }
    
    // ========== OPTIMIERUNG ==========
    public void optimizeGameParameters() {
        // Automatische Optimierung basierend auf aktuellem Zustand
        physicsEngine.optimize(this);
        consciousnessEngine.optimize(this);
    }
    
    // ========== STATISTIK ==========
    public SimulationStatistics getStatistics() {
        SimulationStatistics stats = new SimulationStatistics();
        stats.setSimulationTicks(simulationTicks);
        stats.setUniverseTime(universeTime);
        stats.setUnitCount(units.size());
        stats.setGlobalEntropy(globalEntropyPool);
        stats.setPhysicsStats(physicsEngine.getStatistics());
        stats.setConsciousnessStats(consciousnessEngine.getStatistics());
        stats.setQuestStats(questSystem.getStatistics());
        stats.setRewardStats(rewardSystem.getStatistics());
        return stats;
    }
    
    // ========== UTILITY-METHODEN ==========
    public void clearAllUnits() {
        units.clear();
    }
    
    public int getUnitCount() {
        return units.size();
    }
    
    public double getAverageConsciousness() {
        if (units.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Unit unit : units) {
            sum += unit.getConsciousnessLevel();
        }
        return sum / units.size();
    }
    
    public double getTotalMass() {
        double totalMass = 0.0;
        for (Unit unit : units) {
            totalMass += unit.getMass();
        }
        return totalMass;
    }
    
    public double getTotalEnergy() {
        double totalEnergy = 0.0;
        for (Unit unit : units) {
            totalEnergy += unit.getKineticEnergy();
            totalEnergy += unit.getPotentialEnergy();
        }
        return totalEnergy;
    }
    
    // ========== MAIN-METHODE (FÜR TESTS) ==========
    public static void main(String[] args) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        
        // Test: Einheiten erstellen
        for (int i = 0; i < 10; i++) {
            Unit unit = core.createUnit();
            unit.setPosition(
                (Math.random() - 0.5) * 100,
                (Math.random() - 0.5) * 100,
                (Math.random() - 0.5) * 100
            );
            unit.setVelocity(
                (Math.random() - 0.5) * 10,
                (Math.random() - 0.5) * 10,
                (Math.random() - 0.5) * 10
            );
            unit.setMass(10.0 + Math.random() * 90.0);
            unit.setTeam(i % 3);
            unit.setType(i == 0 ? "King" : "Unit");
        }
        
        // Simulation starten
        core.startSimulation();
        
        // 10 Sekunden laufen lassen
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Statistik ausgeben
        SimulationStatistics stats = core.getStatistics();
        System.out.println("=== SIMULATIONS-STATISTIK ===");
        System.out.println("Ticks: " + stats.getSimulationTicks());
        System.out.println("Zeit: " + stats.getUniverseTime());
        System.out.println("Einheiten: " + stats.getUnitCount());
        System.out.println("Entropie: " + stats.getGlobalEntropy());
        
        // Simulation beenden
        core.stopSimulation();
    }
}
