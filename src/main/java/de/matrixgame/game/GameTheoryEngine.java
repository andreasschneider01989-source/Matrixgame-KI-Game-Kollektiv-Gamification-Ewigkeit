package de.matrixgame.game;

import de.matrixgame.core.Unit;
import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.MatrixGameConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Spieltheorie-Engine für den Matrixgame-Simulator
 * Implementiert verschiedene spieltheoretische Modelle und Strategien
 */
public class GameTheoryEngine {
    
    // ========== SPIELTYPEN ==========
    public enum GameType {
        PRISONERS_DILEMMA,
        COOPERATION_GAME,
        COMPETITION_GAME,
        COORDINATION_GAME,
        MIXED_GAME
    }
    
    // ========== STRATEGIEN ==========
    public enum Strategy {
        COOPERATE,
        DEFECT,
        TIT_FOR_TAT,
        GRUDGER,
        RANDOM,
        ALWAYS_COOPERATE,
        ALWAYS_DEFECT
    }
    
    // ========== INSTANZ-VARIABLEN ==========
    private Map<Long, Strategy> unitStrategies;
    private Map<GameType, PayoffMatrix> payoffMatrices;
    private Random random;
    
    // ========== KONSTRUKTOR ==========
    public GameTheoryEngine() {
        this.unitStrategies = new HashMap<>();
        this.payoffMatrices = new HashMap<>();
        this.random = ThreadLocalRandom.current();
        
        // Standard-Auszahlungsmatrizen initialisieren
        initializePayoffMatrices();
    }
    
    // ========== AUSZAHLUNGSMATRIZEN INITIALISIEREN ==========
    private void initializePayoffMatrices() {
        // Prisoner's Dilemma
        PayoffMatrix pdMatrix = new PayoffMatrix(2, 2);
        pdMatrix.setPayoff(0, 0, -1, -1); // Beide kooperieren
        pdMatrix.setPayoff(0, 1, -3, 0); // Spieler 1 kooperiert, Spieler 2 verrät
        pdMatrix.setPayoff(1, 0, 0, -3); // Spieler 1 verrät, Spieler 2 kooperiert
        pdMatrix.setPayoff(1, 1, -2, -2); // Beide verraten
        payoffMatrices.put(GameType.PRISONERS_DILEMMA, pdMatrix);
        
        // Kooperationsspiel
        PayoffMatrix coopMatrix = new PayoffMatrix(2, 2);
        coopMatrix.setPayoff(0, 0, MatrixGameConfig.COOPERATION_REWARD, MatrixGameConfig.COOPERATION_REWARD);
        coopMatrix.setPayoff(0, 1, 0, MatrixGameConfig.COOPERATION_REWARD + MatrixGameConfig.BETRAYAL_PENALTY);
        coopMatrix.setPayoff(1, 0, MatrixGameConfig.COOPERATION_REWARD + MatrixGameConfig.BETRAYAL_PENALTY, 0);
        coopMatrix.setPayoff(1, 1, MatrixGameConfig.COMPETITION_REWARD, MatrixGameConfig.COMPETITION_REWARD);
        payoffMatrices.put(GameType.COOPERATION_GAME, coopMatrix);
        
        // Wettbewerbs-Spiel
        PayoffMatrix compMatrix = new PayoffMatrix(2, 2);
        compMatrix.setPayoff(0, 0, MatrixGameConfig.COMPETITION_REWARD, MatrixGameConfig.COMPETITION_REWARD);
        compMatrix.setPayoff(0, 1, MatrixGameConfig.COMPETITION_REWARD + MatrixGameConfig.TRUST_BONUS, MatrixGameConfig.COMPETITION_REWARD);
        compMatrix.setPayoff(1, 0, MatrixGameConfig.COMPETITION_REWARD, MatrixGameConfig.COMPETITION_REWARD + MatrixGameConfig.TRUST_BONUS);
        compMatrix.setPayoff(1, 1, MatrixGameConfig.COOPERATION_REWARD, MatrixGameConfig.COOPERATION_REWARD);
        payoffMatrices.put(GameType.COMPETITION_GAME, compMatrix);
    }
    
    // ========== STRATEGIE FÜR EINHEIT FESTLEGEN ==========
    public void setUnitStrategy(long unitId, Strategy strategy) {
        unitStrategies.put(unitId, strategy);
    }
    
    public Strategy getUnitStrategy(long unitId) {
        return unitStrategies.getOrDefault(unitId, Strategy.TIT_FOR_TAT);
    }
    
    // ========== ENTSCHEIDUNG FÜR EINHEIT TREFFEN ==========
    public boolean makeDecision(Unit unit, Unit other) {
        Strategy strategy = getUnitStrategy(unit.getId());
        Strategy otherStrategy = getUnitStrategy(other.getId());
        
        switch (strategy) {
            case COOPERATE:
                return true;
            case DEFECT:
                return false;
            case TIT_FOR_TAT:
                // Wie der andere in der letzten Runde gespielt hat
                return getLastAction(other.getId(), unit.getId());
            case GRUDGER:
                // Kooperieren, bis der andere verrät, dann immer verraten
                return !hasDefectedBefore(other.getId(), unit.getId());
            case RANDOM:
                return random.nextBoolean();
            case ALWAYS_COOPERATE:
                return true;
            case ALWAYS_DEFECT:
                return false;
            default:
                return true;
        }
    }
    
    // ========== LETZTE AKTION ABRUFEN ==========
    private boolean getLastAction(long unitId, long opponentId) {
        // In einer echten Implementierung würde man eine Historie speichern
        // Für jetzt: Zufällige Antwort
        return random.nextBoolean();
    }
    
    private boolean hasDefectedBefore(long unitId, long opponentId) {
        // In einer echten Implementierung würde man prüfen, ob der Gegner schon mal verraten hat
        // Für jetzt: 20% Chance, dass er schon mal verraten hat
        return random.nextDouble() < 0.2;
    }
    
    // ========== AUSZAHLUNG BERECHNEN ==========
    public double calculatePayoff(Unit unit, Unit other, boolean unitAction, boolean otherAction, GameType gameType) {
        PayoffMatrix matrix = payoffMatrices.get(gameType);
        if (matrix == null) {
            matrix = payoffMatrices.get(GameType.PRISONERS_DILEMMA);
        }
        
        int unitStrategy = unitAction ? 0 : 1;
        int otherStrategy = otherAction ? 0 : 1;
        
        return matrix.getPayoff(unitStrategy, otherStrategy, 0); // Spieler 0 ist die Einheit
    }
    
    // ========== SPIEL SIMULIEREN ==========
    public void simulateGame(Unit player1, Unit player2, GameType gameType, int rounds) {
        double player1Total = 0;
        double player2Total = 0;
        
        for (int i = 0; i < rounds; i++) {
            boolean p1Action = makeDecision(player1, player2);
            boolean p2Action = makeDecision(player2, player1);
            
            double p1Payoff = calculatePayoff(player1, player2, p1Action, p2Action, gameType);
            double p2Payoff = calculatePayoff(player2, player1, p2Action, p1Action, gameType);
            
            player1Total += p1Payoff;
            player2Total += p2Payoff;
            
            // Aktionen in der Historie speichern
            // In einer echten Implementierung
        }
        
        // Belohnungen verteilen
        player1.addRewardPoints(player1Total);
        player2.addRewardPoints(player2Total);
    }
    
    // ========== EVOLUTIONÄRE SPIELTHEORIE ==========
    public void evolveStrategies(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        Map<Strategy, Double> strategyFitness = new HashMap<>();
        
        // Fitness für jede Strategie berechnen
        for (Strategy strategy : Strategy.values()) {
            strategyFitness.put(strategy, 0.0);
        }
        
        // Für jede Einheit die Fitness ihrer Strategie berechnen
        for (Unit unit : units) {
            Strategy strategy = getUnitStrategy(unit.getId());
            double fitness = calculateFitness(unit, strategy, core);
            strategyFitness.put(strategy, strategyFitness.get(strategy) + fitness);
        }
        
        // Strategien basierend auf Fitness anpassen
        for (Unit unit : units) {
            Strategy currentStrategy = getUnitStrategy(unit.getId());
            Strategy bestStrategy = getBestStrategy(strategyFitness);
            
            // Mit einer gewissen Wahrscheinlichkeit die beste Strategie übernehmen
            if (random.nextDouble() < 0.1) {
                setUnitStrategy(unit.getId(), bestStrategy);
            }
        }
    }
    
    private double calculateFitness(Unit unit, Strategy strategy, MatrixGameCore core) {
        // Fitness basierend auf Gesundheit, Level und Belohnungspunkten
        return unit.getHealthPercentage() * 0.4 + 
               unit.getLevel() * 10 * 0.3 + 
               unit.getRewardPoints() * 0.3;
    }
    
    private Strategy getBestStrategy(Map<Strategy, Double> strategyFitness) {
        Strategy bestStrategy = null;
        double maxFitness = Double.MIN_VALUE;
        
        for (Map.Entry<Strategy, Double> entry : strategyFitness.entrySet()) {
            if (entry.getValue() > maxFitness) {
                maxFitness = entry.getValue();
                bestStrategy = entry.getKey();
            }
        }
        
        return bestStrategy;
    }
    
    // ========== KOALITIONEN BILDEN ==========
    public void formCoalitions(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Einheiten nach Team gruppieren
        Map<Integer, List<Unit>> teams = new HashMap<>();
        for (Unit unit : units) {
            teams.computeIfAbsent(unit.getTeam(), k -> new ArrayList<>()).add(unit);
        }
        
        // Für jedes Team Koalitionen bilden
        for (List<Unit> teamUnits : teams.values()) {
            if (teamUnits.size() > 1) {
                // Einheiten im Team können Koalitionen bilden
                for (int i = 0; i < teamUnits.size(); i++) {
                    Unit u1 = teamUnits.get(i);
                    for (int j = i + 1; j < teamUnits.size(); j++) {
                        Unit u2 = teamUnits.get(j);
                        
                        // Prüfen, ob eine Koalition sinnvoll ist
                        if (shouldFormCoalition(u1, u2, core)) {
                            // Koalition bilden
                            u1.setDecisionAlgorithm("COALITION");
                            u2.setDecisionAlgorithm("COALITION");
                            
                            // Gemeinsames Ziel setzen
                            Unit commonTarget = findCommonTarget(u1, u2, core);
                            if (commonTarget != null) {
                                u1.setTarget(commonTarget);
                                u2.setTarget(commonTarget);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean shouldFormCoalition(Unit u1, Unit u2, MatrixGameCore core) {
        // Koalition bilden, wenn:
        // 1. Beide Einheiten im selben Team sind
        // 2. Es einen gemeinsamen Gegner in der Nähe gibt
        // 3. Die kombinierte Stärke höher ist als die des Gegners
        
        if (u1.getTeam() != u2.getTeam()) return false;
        
        // Gemeinsamen Gegner finden
        Unit commonEnemy = findCommonEnemy(u1, u2, core);
        if (commonEnemy == null) return false;
        
        // Kombinierte Stärke
        double combinedStrength = calculateStrength(u1) + calculateStrength(u2);
        double enemyStrength = calculateStrength(commonEnemy);
        
        return combinedStrength > enemyStrength * 1.2;
    }
    
    private Unit findCommonEnemy(Unit u1, Unit u2, MatrixGameCore core) {
        // Gegner finden, der für beide Einheiten ein Ziel sein könnte
        for (Unit other : core.getUnits()) {
            if (other.getTeam() != u1.getTeam() && other.isAlive()) {
                double dist1 = u1.getDistance(other);
                double dist2 = u2.getDistance(other);
                if (dist1 < 100 && dist2 < 100) {
                    return other;
                }
            }
        }
        return null;
    }
    
    private Unit findCommonTarget(Unit u1, Unit u2, MatrixGameCore core) {
        // Gemeinsames Ziel finden
        Unit target1 = u1.getTarget();
        Unit target2 = u2.getTarget();
        
        // Wenn beide das gleiche Ziel haben
        if (target1 != null && target2 != null && target1.getId() == target2.getId()) {
            return target1;
        }
        
        // Sonst einen gemeinsamen Gegner finden
        return findCommonEnemy(u1, u2, core);
    }
    
    private double calculateStrength(Unit unit) {
        return unit.getHealthPercentage() * 0.4 + 
               unit.getLevel() * 10 * 0.3 + 
               unit.getMass() * 0.3;
    }
    
    // ========== VERTRAUENSMODELL ==========
    public void updateTrust(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Vertrauenswerte für alle Einheitenpaare aktualisieren
        for (int i = 0; i < units.size(); i++) {
            Unit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                Unit u2 = units.get(j);
                
                // Vertrauen aktualisieren
                if (u1.getTeam() == u2.getTeam()) {
                    updateTrust(u1, u2, core);
                }
            }
        }
    }
    
    private void updateTrust(Unit u1, Unit u2, MatrixGameCore core) {
        // Vertrauen basierend auf vergangenen Interaktionen
        // In einer echten Implementierung würde man eine Historie speichern
        
        // Vereinfachung: Vertrauen basierend auf Nähe und gemeinsamer Ausrichtung
        double distance = u1.getDistance(u2);
        boolean sameTarget = u1.getTarget() != null && u2.getTarget() != null && 
                           u1.getTarget().getId() == u2.getTarget().getId();
        
        double trust = 0.5; // Basisvertrauen
        
        if (distance < 30) {
            trust += 0.2; // Nähe erhöht Vertrauen
        }
        
        if (sameTarget) {
            trust += 0.3; // Gemeinsames Ziel erhöht Vertrauen
        }
        
        // Vertrauen begrenzen
        trust = Math.max(0, Math.min(1, trust));
        
        // Vertrauensbonus auf Entscheidungsalgorithmus anwenden
        if (trust > 0.7) {
            u1.setDecisionAlgorithm("COOPERATE");
            u2.setDecisionAlgorithm("COOPERATE");
        }
    }
    
    // ========== INNER KLASSE: AUSZAHLUNGSMATRIX ==========
    public static class PayoffMatrix {
        private int rows;
        private int cols;
        private double[][] payoffs;
        
        public PayoffMatrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            this.payoffs = new double[rows * 2][cols * 2]; // 2 Spieler
        }
        
        public void setPayoff(int strategy1, int strategy2, double payoff1, double payoff2) {
            payoffs[strategy1][strategy2] = payoff1;
            payoffs[strategy1 + rows][strategy2] = payoff2;
        }
        
        public double getPayoff(int strategy1, int strategy2, int player) {
            if (player == 0) {
                return payoffs[strategy1][strategy2];
            } else {
                return payoffs[strategy1 + rows][strategy2];
            }
        }
        
        public int getRows() {
            return rows;
        }
        
        public int getCols() {
            return cols;
        }
    }
    
    // ========== STATISTIKEN ==========
    public Map<Strategy, Integer> getStrategyDistribution(MatrixGameCore core) {
        Map<Strategy, Integer> distribution = new HashMap<>();
        
        for (Strategy strategy : Strategy.values()) {
            distribution.put(strategy, 0);
        }
        
        for (Unit unit : core.getUnits()) {
            Strategy strategy = getUnitStrategy(unit.getId());
            distribution.put(strategy, distribution.get(strategy) + 1);
        }
        
        return distribution;
    }
    
    public double getCooperationRate(MatrixGameCore core) {
        int cooperateCount = 0;
        int totalCount = 0;
        
        for (Unit unit : core.getUnits()) {
            Strategy strategy = getUnitStrategy(unit.getId());
            if (strategy == Strategy.COOPERATE || strategy == Strategy.TIT_FOR_TAT || 
                strategy == Strategy.ALWAYS_COOPERATE) {
                cooperateCount++;
            }
            totalCount++;
        }
        
        return totalCount > 0 ? (double) cooperateCount / totalCount : 0;
    }
    
    public double getDefectionRate(MatrixGameCore core) {
        int defectCount = 0;
        int totalCount = 0;
        
        for (Unit unit : core.getUnits()) {
            Strategy strategy = getUnitStrategy(unit.getId());
            if (strategy == Strategy.DEFECT || strategy == Strategy.ALWAYS_DEFECT) {
                defectCount++;
            }
            totalCount++;
        }
        
        return totalCount > 0 ? (double) defectCount / totalCount : 0;
    }
}
