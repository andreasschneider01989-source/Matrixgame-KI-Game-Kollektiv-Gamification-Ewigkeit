package de.matrixgame.physics;

import de.matrixgame.core.MatrixGameConfig;
import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.Unit;
import de.matrixgame.core.GameStatePrediction;
import de.matrixgame.core.SimulationStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Physik-Engine für den Matrixgame-Simulator
 * Implementiert Gravitation, Kollisionen, Entropie und Relativität
 */
public class PhysicsEngine {
    
    // ========== INSTANZ-VARIABLEN ==========
    private Random random;
    private SimulationStatistics statistics;
    
    // ========== KONSTRUKTOR ==========
    public PhysicsEngine() {
        this.random = ThreadLocalRandom.current();
        this.statistics = new SimulationStatistics();
    }
    
    // ========== HAUPTPHYSIK-BERECHNUNG ==========
    public void update(MatrixGameCore core, double dt) {
        List<Unit> units = core.getUnits();
        if (units.isEmpty()) return;
        
        // 1. Beschleunigung berechnen (Gravitation)
        computeGravity(units, dt, core.getGlobalEntropyPool());
        
        // 2. Position und Geschwindigkeit aktualisieren
        updatePositionsAndVelocities(units, dt);
        
        // 3. Kollisionen auflösen
        resolveCollisions(units);
        
        // 4. Entropie-Bewegungen anwenden
        applyEntropyMovements(units);
        
        // 5. Statistiken aktualisieren
        updateStatistics(core);
    }
    
    // ========== GRAVITATIONS-BERECHNUNG ==========
    private void computeGravity(List<Unit> units, double dt, double globalEntropyPool) {
        int n = units.size();
        
        // Beschleunigungsvektoren für alle Einheiten
        Vector3D[] accelerations = new Vector3D[n];
        for (int i = 0; i < n; i++) {
            accelerations[i] = Vector3D.zero();
        }
        
        // N-Körper-Gravitation (O(n^2) aber optimiert)
        for (int i = 0; i < n; i++) {
            Unit u1 = units.get(i);
            if (!u1.isMovable() || !u1.isUpdatePositionActive()) continue;
            
            for (int j = i + 1; j < n; j++) {
                Unit u2 = units.get(j);
                if (!u2.isMovable() || !u2.isUpdatePositionActive()) continue;
                
                // Distanzvektor
                Vector3D delta = u2.getPosition().subtract(u1.getPosition());
                double distSq = delta.lengthSquared();
                
                // Schutz vor Division durch Null
                if (distSq < MatrixGameConfig.EPSILON_SINGULARITY) {
                    distSq = MatrixGameConfig.EPSILON_SINGULARITY;
                }
                
                double dist = Math.sqrt(distSq);
                
                // Newton'sches Gravitationsgesetz: F = G * (m1 * m2) / r^2
                double force = (MatrixGameConfig.GRAVITATIONAL_CONSTANT * 
                               u1.getMass() * u2.getMass() / distSq);
                
                // Entropie-Modulation: Niedrige Entropie = stabilere Gravitation
                double entropyFactor = Math.max(0.1, 1.0 - (globalEntropyPool / 1000.0));
                force *= entropyFactor;
                
                // Normierter Richtungsvektor
                Vector3D direction = delta.scale(1.0 / dist);
                
                // Beschleunigung: a = F / m
                Vector3D forceVector = direction.scale(force);
                Vector3D acc1 = forceVector.scale(1.0 / u1.getMass());
                Vector3D acc2 = forceVector.scale(-1.0 / u2.getMass());
                
                // Beschleunigung akkumulieren
                accelerations[i] = accelerations[i].add(acc1);
                accelerations[j] = accelerations[j].add(acc2);
            }
        }
        
        // Beschleunigung auf Einheiten anwenden
        for (int i = 0; i < n; i++) {
            Unit unit = units.get(i);
            unit.setAcceleration(accelerations[i]);
        }
    }
    
    // ========== POSITIONEN UND GESCHWINDIGKEITEN AKTUALISIEREN ==========
    private void updatePositionsAndVelocities(List<Unit> units, double dt) {
        for (Unit unit : units) {
            if (!unit.isMovable() || !unit.isUpdatePositionActive()) continue;
            
            // Geschwindigkeit aktualisieren: v(t+dt) = v(t) + a * dt
            Vector3D newVelocity = unit.getVelocity().add(
                unit.getAcceleration().scale(dt)
            );
            
            // Relativistische Geschwindigkeitsbegrenzung
            double currentSpeed = newVelocity.length();
            if (currentSpeed > MatrixGameConfig.LIGHT_SPEED) {
                double scale = MatrixGameConfig.LIGHT_SPEED / currentSpeed;
                newVelocity = newVelocity.scale(scale);
            }
            
            // Maximale Geschwindigkeit der Einheit
            if (currentSpeed > unit.getMaxSpeed()) {
                double scale = unit.getMaxSpeed() / currentSpeed;
                newVelocity = newVelocity.scale(scale);
            }
            
            unit.setVelocity(newVelocity);
            
            // Position aktualisieren: r(t+dt) = r(t) + v * dt
            Vector3D newPosition = unit.getPosition().add(
                newVelocity.scale(dt)
            );
            unit.setPosition(newPosition);
            
            // Kinetische Energie berechnen
            double eKin = 0.5 * unit.getMass() * newVelocity.lengthSquared();
            unit.setVariable("ekin", eKin);
        }
    }
    
    // ========== KOLLISIONEN AUFLÖSEN ==========
    private void resolveCollisions(List<Unit> units) {
        int n = units.size();
        boolean[] merged = new boolean[n];
        
        for (int i = 0; i < n; i++) {
            Unit u1 = units.get(i);
            if (merged[i] || !u1.isMovable()) continue;
            
            for (int j = i + 1; j < n; j++) {
                Unit u2 = units.get(j);
                if (merged[j] || !u2.isMovable()) continue;
                
                // Abstand berechnen
                double distance = u1.getDistance(u2);
                double collisionDistance = u1.getRadius() + u2.getRadius();
                
                // Kollision prüfen
                if (distance < collisionDistance) {
                    // Impulserhaltung: v_neu = (m1*v1 + m2*v2) / (m1 + m2)
                    double totalMass = u1.getMass() + u2.getMass();
                    Vector3D newVelocity = u1.getVelocity().scale(u1.getMass())
                        .add(u2.getVelocity().scale(u2.getMass()))
                        .scale(1.0 / totalMass);
                    
                    // Massen- und Volumen-Akkretion
                    u1.setMass(totalMass);
                    u1.setRadius((int) Math.cbrt(
                        Math.pow(u1.getRadius(), 3) + Math.pow(u2.getRadius(), 3)
                    ));
                    u1.setVelocity(newVelocity);
                    
                    // Farbe kombinieren (Harmonische Synthese)
                    u1.setColor(combineColors(u1.getColor(), u2.getColor()));
                    
                    // Bewusstsein kombinieren
                    double newConsciousness = (u1.getConsciousnessLevel() + 
                                              u2.getConsciousnessLevel()) / 2.0;
                    u1.setConsciousnessLevel(newConsciousness);
                    
                    // Gesundheit kombinieren
                    u1.setMaxHealth(u1.getMaxHealth() + u2.getMaxHealth());
                    u1.setHealth(u1.getHealth() + u2.getHealth());
                    
                    // Schaden kombinieren
                    u1.setDamage(u1.getDamage() + u2.getDamage());
                    
                    // Erfahrung kombinieren
                    u1.addExperience(u2.getExperience());
                    u1.addRewardPoints(u2.getRewardPoints());
                    
                    // Team behalten (oder des größeren Units)
                    if (u2.getMass() > u1.getMass()) {
                        u1.setTeam(u2.getTeam());
                    }
                    
                    // Einheit entfernen
                    units.remove(u2);
                    merged[j] = true;
                    statistics.setMergeCount(statistics.getMergeCount() + 1);
                    
                    if (MatrixGameConfig.ENABLE_PHYSICS_LOGGING) {
                        System.out.printf("COLLISION | Unit %d absorbiert Unit %d | Neue Masse: %.2f%n",
                            u1.getId(), u2.getId(), u1.getMass());
                    }
                    
                    // Index anpassen
                    j--;
                    n--;
                }
            }
        }
    }
    
    // ========== ENTROPIE-BEWEGUNGEN ==========
    private void applyEntropyMovements(List<Unit> units) {
        for (Unit unit : units) {
            if (unit.isEntropyMoveActive() && unit.getEntropyMoveSpeedPercent() > 0) {
                double factor = unit.getEntropyMoveSpeedPercent() / 100.0;
                double dx = (random.nextDouble() - 0.5) * factor;
                double dy = (random.nextDouble() - 0.5) * factor;
                double dz = (random.nextDouble() - 0.5) * factor;
                
                Vector3D currentPos = unit.getPosition();
                unit.setPosition(
                    currentPos.getX() + dx,
                    currentPos.getY() + dy,
                    currentPos.getZ() + dz
                );
            }
        }
    }
    
    // ========== FARBEN KOMBINIEREN ==========
    private java.awt.Color combineColors(java.awt.Color c1, java.awt.Color c2) {
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return new java.awt.Color(
            (c1.getRed() + c2.getRed()) / 2,
            (c1.getGreen() + c2.getGreen()) / 2,
            (c1.getBlue() + c2.getBlue()) / 2
        );
    }
    
    // ========== STATISTIKEN AKTUALISIEREN ==========
    private void updateStatistics(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // Gesamtmasse berechnen
        double totalMass = 0.0;
        for (Unit unit : units) {
            totalMass += unit.getMass();
        }
        statistics.setTotalMass(totalMass);
        
        // Gesamtenergie berechnen
        double totalEnergy = 0.0;
        double maxVelocity = 0.0;
        double sumVelocity = 0.0;
        
        for (Unit unit : units) {
            totalEnergy += unit.getKineticEnergy();
            double vel = unit.getVelocity().length();
            sumVelocity += vel;
            maxVelocity = Math.max(maxVelocity, vel);
        }
        
        statistics.setTotalEnergy(totalEnergy);
        statistics.setAverageVelocity(units.isEmpty() ? 0 : sumVelocity / units.size());
        statistics.setMaxVelocity(maxVelocity);
    }
    
    // ========== STATISTIKEN ABRUFEN ==========
    public SimulationStatistics getStatistics() {
        return statistics;
    }
    
    // ========== PROGNOSE ==========
    public GameStatePrediction predict(MatrixGameCore core, int steps) {
        GameStatePrediction prediction = new GameStatePrediction();
        prediction.setPredictionSteps(steps);
        prediction.setPredictionTime(core.getUniverseTime() + steps * MatrixGameConfig.DEFAULT_TIME_STEP);
        
        List<Unit> units = core.getUnits();
        
        // Für jede Einheit eine Prognose erstellen
        for (Unit unit : units) {
            predictUnit(unit, steps, prediction);
        }
        
        // Team-Prognosen
        predictTeams(core, steps, prediction);
        
        // Entscheidungsoptionen generieren
        generateDecisionOptions(core, prediction);
        
        // Metriken setzen
        prediction.setConfidence(MatrixGameConfig.PREDICTION_ACCURACY);
        prediction.setEntropy(core.getGlobalEntropyPool() / MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL);
        prediction.setComplexity(calculateComplexity(core));
        
        return prediction;
    }
    
    // ========== EINHEIT PROGNOSTIZIEREN ==========
    private void predictUnit(Unit unit, int steps, GameStatePrediction prediction) {
        Vector3D currentPos = unit.getPosition();
        Vector3D currentVel = unit.getVelocity();
        double currentHealth = unit.getHealth();
        double currentConsciousness = unit.getConsciousnessLevel();
        
        // Position prognostizieren (lineare Extrapolation)
        Vector3D predictedPos = currentPos.add(currentVel.scale(steps * MatrixGameConfig.DEFAULT_TIME_STEP));
        
        // Geschwindigkeit prognostizieren (konstant annehmen)
        Vector3D predictedVel = currentVel;
        
        // Gesundheit prognostizieren (leicht abnehmend)
        double predictedHealth = Math.max(0, currentHealth - steps * 0.1);
        
        // Bewusstsein prognostizieren (leicht steigend)
        double predictedConsciousness = Math.min(
            MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL,
            currentConsciousness + steps * 0.5
        );
        
        // Bewusstseinsmodus prognostizieren
        String predictedMode = predictConsciousnessMode(predictedConsciousness);
        
        // Wahrscheinlichkeiten berechnen
        double survivalProb = calculateSurvivalProbability(unit, steps);
        double levelUpProb = calculateLevelUpProbability(unit, steps);
        double questProb = calculateQuestCompletionProbability(unit, steps);
        
        // Prognose hinzufügen
        prediction.addUnitPrediction(
            unit.getId(), predictedPos, predictedVel, predictedHealth,
            predictedConsciousness, predictedMode, survivalProb, levelUpProb, questProb
        );
    }
    
    // ========== BEWUSSTSEINS-MODUS PROGNOSTIZIEREN ==========
    private String predictConsciousnessMode(double consciousnessLevel) {
        if (consciousnessLevel < 20) return "SHame_Guilt";
        if (consciousnessLevel < 50) return "Apathy_Grief";
        if (consciousnessLevel < 100) return "Fear_Desire";
        if (consciousnessLevel < 200) return "Anger_Pride";
        if (consciousnessLevel < 250) return "Courage";
        if (consciousnessLevel < 350) return "Neutrality_Willingness";
        if (consciousnessLevel < 400) return "Acceptance_Reason";
        if (consciousnessLevel < 600) return "Love_Joy";
        if (consciousnessLevel < 800) return "Peace";
        return "Enlightenment";
    }
    
    // ========== WAHRSCHEINLICHKEITEN BERECHNEN ==========
    private double calculateSurvivalProbability(Unit unit, int steps) {
        // Basierend auf Gesundheit, Level und Bewusstsein
        double healthFactor = unit.getHealthPercentage() / 100.0;
        double levelFactor = Math.min(1.0, unit.getLevel() / 10.0);
        double consciousnessFactor = unit.getConsciousnessLevel() / MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL;
        
        // Zeitfaktor (je mehr Schritte, desto unsicherer)
        double timeFactor = Math.exp(-steps * 0.01);
        
        return Math.min(1.0, (healthFactor * 0.4 + levelFactor * 0.3 + consciousnessFactor * 0.3) * timeFactor);
    }
    
    private double calculateLevelUpProbability(Unit unit, int steps) {
        // Basierend auf Erfahrung und Bewusstsein
        double expFactor = unit.getExperiencePercentage() / 100.0;
        double consciousnessFactor = unit.getConsciousnessLevel() / MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL;
        
        // Zeitfaktor
        double timeFactor = Math.min(1.0, steps * 0.1);
        
        return Math.min(1.0, (expFactor * 0.6 + consciousnessFactor * 0.4) * timeFactor);
    }
    
    private double calculateQuestCompletionProbability(Unit unit, int steps) {
        // Basierend auf Bewusstsein und Level
        double consciousnessFactor = unit.getConsciousnessLevel() / MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL;
        double levelFactor = Math.min(1.0, unit.getLevel() / 10.0);
        
        // Zeitfaktor
        double timeFactor = Math.min(1.0, steps * 0.05);
        
        return Math.min(1.0, (consciousnessFactor * 0.7 + levelFactor * 0.3) * timeFactor);
    }
    
    // ========== TEAM-PROGNOSEN ==========
    private void predictTeams(MatrixGameCore core, int steps, GameStatePrediction prediction) {
        List<Unit> units = core.getUnits();
        Map<Integer, Integer> teamSizes = new HashMap<>();
        Map<Integer, Double> teamMasses = new HashMap<>();
        Map<Integer, Double> teamConsciousness = new HashMap<>();
        
        // Team-Statistiken sammeln
        for (Unit unit : units) {
            int team = unit.getTeam();
            teamSizes.put(team, teamSizes.getOrDefault(team, 0) + 1);
            teamMasses.put(team, teamMasses.getOrDefault(team, 0.0) + unit.getMass());
            teamConsciousness.put(team, teamConsciousness.getOrDefault(team, 0.0) + unit.getConsciousnessLevel());
        }
        
        // Für jedes Team eine Prognose erstellen
        for (Integer team : teamSizes.keySet()) {
            int size = teamSizes.get(team);
            double mass = teamMasses.get(team);
            double avgConsciousness = teamConsciousness.get(team) / size;
            
            // Überlebenswahrscheinlichkeit
            double survivalProb = Math.min(1.0, size * 0.1 + mass * 0.01 + avgConsciousness * 0.001);
            
            // Siegwahrscheinlichkeit
            double victoryProb = calculateVictoryProbability(team, teamSizes, teamMasses, teamConsciousness);
            
            prediction.addTeamPrediction(team, survivalProb, victoryProb);
        }
        
        // Globale Stabilität
        double stability = calculateGlobalStability(teamSizes, teamMasses);
        prediction.setGlobalStabilityProbability(stability);
    }
    
    // ========== SIEG-WAHRSCHEINLICHKEIT BERECHNEN ==========
    private double calculateVictoryProbability(int team, Map<Integer, Integer> teamSizes, 
                                               Map<Integer, Double> teamMasses, 
                                               Map<Integer, Double> teamConsciousness) {
        int size = teamSizes.get(team);
        double mass = teamMasses.get(team);
        double consciousness = teamConsciousness.get(team);
        
        // Vergleich mit anderen Teams
        double totalSize = teamSizes.values().stream().mapToInt(Integer::intValue).sum();
        double totalMass = teamMasses.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalConsciousness = teamConsciousness.values().stream().mapToDouble(Double::doubleValue).sum();
        
        double sizeRatio = size / Math.max(1, totalSize);
        double massRatio = mass / Math.max(1, totalMass);
        double consciousnessRatio = consciousness / Math.max(1, totalConsciousness);
        
        return Math.min(1.0, (sizeRatio * 0.3 + massRatio * 0.3 + consciousnessRatio * 0.4));
    }
    
    // ========== GLOBALE STABILITÄT BERECHNEN ==========
    private double calculateGlobalStability(Map<Integer, Integer> teamSizes, Map<Integer, Double> teamMasses) {
        // Gleichgewicht zwischen Teams
        double maxSize = teamSizes.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        double minSize = teamSizes.values().stream().mapToInt(Integer::intValue).min().orElse(1);
        double sizeBalance = minSize / Math.max(1, maxSize);
        
        double maxMass = teamMasses.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double minMass = teamMasses.values().stream().mapToDouble(Double::doubleValue).min().orElse(1);
        double massBalance = minMass / Math.max(1, maxMass);
        
        return Math.min(1.0, (sizeBalance * 0.5 + massBalance * 0.5));
    }
    
    // ========== KOMPLEXITÄT BERECHNEN ==========
    private double calculateComplexity(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        if (units.size() <= 1) return 0.0;
        
        // Anzahl der Interaktionen
        int interactions = units.size() * (units.size() - 1) / 2;
        
        // Durchschnittliche Distanz
        double totalDistance = 0.0;
        int distanceCount = 0;
        for (int i = 0; i < units.size(); i++) {
            for (int j = i + 1; j < units.size(); j++) {
                totalDistance += units.get(i).getDistance(units.get(j));
                distanceCount++;
            }
        }
        double avgDistance = distanceCount > 0 ? totalDistance / distanceCount : 1.0;
        
        // Komplexität = Interaktionen / Durchschnittliche Distanz
        return Math.min(1.0, interactions / Math.max(1, avgDistance));
    }
    
    // ========== ENTSCHEIDUNGS-OPTIONEN GENERIEREN ==========
    private void generateDecisionOptions(MatrixGameCore core, GameStatePrediction prediction) {
        List<Unit> units = core.getUnits();
        
        // 1. Kooperation
        GameStatePrediction.DecisionOption cooperate = new GameStatePrediction.DecisionOption(
            "COOPERATE", "Mit anderen Einheiten kooperieren",
            MatrixGameConfig.COOPERATION_REWARD / 10.0,
            MatrixGameConfig.COOPERATION_REWARD * units.size(),
            0.1
        );
        prediction.addDecisionOption(cooperate);
        
        // 2. Wettbewerb
        GameStatePrediction.DecisionOption compete = new GameStatePrediction.DecisionOption(
            "COMPETE", "Gegen andere Einheiten konkurrieren",
            MatrixGameConfig.COMPETITION_REWARD / 10.0,
            MatrixGameConfig.COMPETITION_REWARD * units.size(),
            0.3
        );
        prediction.addDecisionOption(compete);
        
        // 3. Entropie erhöhen (Chaos)
        GameStatePrediction.DecisionOption increaseEntropy = new GameStatePrediction.DecisionOption(
            "INCREASE_ENTROPY", "Entropie erhöhen (Chaos erzeugen)",
            0.7, -10.0, 0.5
        );
        prediction.addDecisionOption(increaseEntropy);
        
        // 4. Entropie verringern (Ordnung)
        GameStatePrediction.DecisionOption decreaseEntropy = new GameStatePrediction.DecisionOption(
            "DECREASE_ENTROPY", "Entropie verringern (Ordnung schaffen)",
            0.8, 20.0, 0.2
        );
        prediction.addDecisionOption(decreaseEntropy);
        
        // 5. Bewusstsein entwickeln
        GameStatePrediction.DecisionOption evolve = new GameStatePrediction.DecisionOption(
            "EVOLVE_CONSCIOUSNESS", "Bewusstsein entwickeln",
            0.9, 50.0, 0.1
        );
        prediction.addDecisionOption(evolve);
    }
    
    // ========== OPTIMIERUNG ==========
    public void optimize(MatrixGameCore core) {
        // Automatische Anpassung der Gravitationskonstante basierend auf Systemzustand
        List<Unit> units = core.getUnits();
        
        if (units.size() > 100) {
            // Bei vielen Einheiten: Gravitation reduzieren für bessere Performance
            MatrixGameConfig.GRAVITATIONAL_CONSTANT = Math.min(
                MatrixGameConfig.GRAVITATIONAL_CONSTANT * 1.1,
                1.0
            );
        } else if (units.size() < 10) {
            // Bei wenigen Einheiten: Gravitation erhöhen für mehr Interaktion
            MatrixGameConfig.GRAVITATIONAL_CONSTANT = Math.max(
                MatrixGameConfig.GRAVITATIONAL_CONSTANT * 0.9,
                1e-12
            );
        }
        
        // Entropie-Regenerationsrate anpassen
        if (core.getGlobalEntropyPool() < 50) {
            MatrixGameConfig.ENTROPY_REGENERATION_RATE = Math.min(
                MatrixGameConfig.ENTROPY_REGENERATION_RATE * 1.1,
                1.0
            );
        } else if (core.getGlobalEntropyPool() > 90) {
            MatrixGameConfig.ENTROPY_REGENERATION_RATE = Math.max(
                MatrixGameConfig.ENTROPY_REGENERATION_RATE * 0.9,
                0.01
            );
        }
    }
    
    // ========== ANALYSE ==========
    public void analyze(MatrixGameCore core) {
        // Aktuelle Statistiken aktualisieren
        updateStatistics(core);
        
        // Warnungen ausgeben
        if (statistics.getCollisionCount() > 10) {
            System.out.println("WARNUNG: Hohe Kollisionsrate - " + statistics.getCollisionCount() + " Kollisionen");
        }
        
        if (statistics.getMergeCount() > 5) {
            System.out.println("INFO: Hohe Verschmelzungsrate - " + statistics.getMergeCount() + " Verschmelzungen");
        }
        
        if (statistics.getMaxVelocity() > MatrixGameConfig.LIGHT_SPEED * 0.1) {
            System.out.println("INFO: Hohe Geschwindigkeiten - Max: " + statistics.getMaxVelocity());
        }
    }
}
