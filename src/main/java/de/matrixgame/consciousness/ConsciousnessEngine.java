package de.matrixgame.consciousness;

import de.matrixgame.core.MatrixGameConfig;
import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.Unit;
import de.matrixgame.core.SimulationStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bewusstseins-Engine für den Matrixgame-Simulator
 * Implementiert die Hawkins-Bewusstseins-Skala und verwandte Konzepte
 */
public class ConsciousnessEngine {
    
    // ========== HAWKINS-SKALA DEFINITIONEN ==========
    public static final Map<String, ConsciousnessLevel> HAWKINS_SCALE = new HashMap<>();
    
    static {
        // Unterer Bereich: Mangelbewusstsein (0-200)
        HAWKINS_SCALE.put("SHame", new ConsciousnessLevel(20, "Scham", "Notabschaltung, Blockade durch unverarbeitete Traumata", "REACTIVE", 0.1));
        HAWKINS_SCALE.put("Guilt", new ConsciousnessLevel(30, "Schuld", "Selbstvorwürfe und Selbstbestrafung", "REACTIVE", 0.15));
        HAWKINS_SCALE.put("Apathy", new ConsciousnessLevel(50, "Apathie", "Gleichgültigkeit, erlernte Hilflosigkeit", "REACTIVE", 0.2));
        HAWKINS_SCALE.put("Grief", new ConsciousnessLevel(75, "Trauer", "Verlust und Verzweiflung", "REACTIVE", 0.25));
        HAWKINS_SCALE.put("Fear", new ConsciousnessLevel(100, "Angst", "Überlebensmodus, hohe Reibung", "REACTIVE", 0.3));
        HAWKINS_SCALE.put("Desire", new ConsciousnessLevel(125, "Verlangen", "Gier nach äußerer Bestätigung", "REACTIVE", 0.35));
        HAWKINS_SCALE.put("Anger", new ConsciousnessLevel(150, "Zorn", "Frustration, Projektion auf äußere Faktoren", "REACTIVE", 0.4));
        HAWKINS_SCALE.put("Pride", new ConsciousnessLevel(175, "Stolz", "Ego-Zentrierung, Arroganz", "REACTIVE", 0.45));
        
        // Schwellenwert: Mut (200)
        HAWKINS_SCALE.put("Courage", new ConsciousnessLevel(200, "Mut", "System-Kompiler, Wendepunkt zum Füllebewusstsein", "TRANSITION", 0.5));
        
        // Oberer Bereich: Füllebewusstsein (200-1000)
        HAWKINS_SCALE.put("Neutrality", new ConsciousnessLevel(250, "Neutralität", "Stabiler Betriebszustand, geordnete Datenverarbeitung", "PROACTIVE", 0.6));
        HAWKINS_SCALE.put("Willingness", new ConsciousnessLevel(310, "Bereitschaft", "Offenheit für Veränderung", "PROACTIVE", 0.65));
        HAWKINS_SCALE.put("Acceptance", new ConsciousnessLevel(350, "Akzeptanz", "Erkennen der Eigenverantwortung", "PROACTIVE", 0.7));
        HAWKINS_SCALE.put("Reason", new ConsciousnessLevel(400, "Vernunft", "Logische Klarheit, Lösungsorientierung", "PROACTIVE", 0.75));
        HAWKINS_SCALE.put("Love", new ConsciousnessLevel(500, "Liebe", "Bedingungslose Kohärenz, kollektiver Mehrwert", "PROACTIVE", 0.85));
        HAWKINS_SCALE.put("Joy", new ConsciousnessLevel(540, "Freude", "Innere Freude und Zufriedenheit", "PROACTIVE", 0.9));
        HAWKINS_SCALE.put("Peace", new ConsciousnessLevel(600, "Frieden", "Innere Ruhe und Harmonie", "PROACTIVE", 0.95));
        HAWKINS_SCALE.put("Enlightenment", new ConsciousnessLevel(700, "Erleuchtung", "Höchste Bewusstseinsebene, Satori", "PROACTIVE", 1.0));
        HAWKINS_SCALE.put("Bliss", new ConsciousnessLevel(800, "Glückseligkeit", "Transzendentes Glück", "PROACTIVE", 1.0));
        HAWKINS_SCALE.put("Divine", new ConsciousnessLevel(1000, "Göttlich", "Vollständige Einheitserfahrung", "PROACTIVE", 1.0));
    }
    
    // ========== INSTANZ-VARIABLEN ==========
    private SimulationStatistics statistics;
    private Map<Long, ConsciousnessState> consciousnessStates;
    
    // ========== KONSTRUKTOR ==========
    public ConsciousnessEngine() {
        this.statistics = new SimulationStatistics();
        this.consciousnessStates = new HashMap<>();
    }
    
    // ========== HAUPTPROZESS ==========
    public void update(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        // 1. Bewusstseinslevel für jede Einheit aktualisieren
        for (Unit unit : units) {
            updateUnitConsciousness(unit, core);
        }
        
        // 2. Bewusstseinsmodi aktualisieren
        updateConsciousnessModes(units);
        
        // 3. Guardian-Modus prüfen
        checkGuardianMode(core);
        
        // 4. Kollektive Bewusstseinsentwicklung
        evolveCollectiveConsciousness(core);
        
        // 5. Statistiken aktualisieren
        updateStatistics(core);
    }
    
    // ========== EINHEIT-BEWUSSTSEIN AKTUALISIEREN ==========
    private void updateUnitConsciousness(Unit unit, MatrixGameCore core) {
        // 1. Grundlegende Entwicklung basierend auf Erfahrung
        double expFactor = unit.getExperiencePercentage() / 100.0;
        double levelFactor = Math.min(1.0, unit.getLevel() / 20.0);
        
        // 2. Entwicklung durch Interaktionen
        double interactionFactor = calculateInteractionFactor(unit, core);
        
        // 3. Entwicklung durch Gesundheit
        double healthFactor = unit.getHealthPercentage() / 100.0;
        
        // 4. Entwicklung durch Entscheidungen
        double decisionFactor = calculateDecisionFactor(unit);
        
        // Kombinierte Entwicklung
        double development = (expFactor * 0.3 + levelFactor * 0.2 + 
                              interactionFactor * 0.25 + healthFactor * 0.15 + 
                              decisionFactor * 0.1);
        
        // Bewusstseinslevel aktualisieren
        double newLevel = unit.getConsciousnessLevel() + development * 0.5;
        unit.setConsciousnessLevel(newLevel);
        
        // Ziel-Bewusstseinslevel aktualisieren
        double targetLevel = Math.min(
            MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL,
            unit.getConsciousnessLevel() + 100
        );
        unit.setConsciousnessTarget(targetLevel);
        
        // Zustand speichern
        consciousnessStates.put(unit.getId(), new ConsciousnessState(
            unit.getConsciousnessLevel(),
            unit.getConsciousnessMode(),
            System.currentTimeMillis()
        ));
    }
    
    // ========== INTERAKTIONS-FAKTOR BERECHNEN ==========
    private double calculateInteractionFactor(Unit unit, MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        if (units.size() <= 1) return 0.0;
        
        // Anzahl der nahen Einheiten
        int nearCount = 0;
        double totalDistance = 0.0;
        
        for (Unit other : units) {
            if (other == unit) continue;
            
            double distance = unit.getDistance(other);
            if (distance < 50.0) {
                nearCount++;
                totalDistance += distance;
            }
        }
        
        if (nearCount == 0) return 0.0;
        
        // Durchschnittliche Distanz zu nahen Einheiten
        double avgDistance = totalDistance / nearCount;
        
        // Interaktionsfaktor: mehr Interaktionen = höhere Entwicklung
        // aber zu nahe Interaktionen können auch negativ sein
        double density = nearCount / (Math.max(1, avgDistance) * Math.max(1, avgDistance));
        
        // Optimaler Dichtebereich
        if (density < 0.1) {
            return 0.1; // Zu wenig Interaktion
        } else if (density > 1.0) {
            return 0.3; // Zu viel Interaktion (Chaos)
        } else {
            return 0.5 + density * 0.3; // Optimaler Bereich
        }
    }
    
    // ========== ENTSCHEIDUNGS-FAKTOR BERECHNEN ==========
    private double calculateDecisionFactor(Unit unit) {
        String algorithm = unit.getDecisionAlgorithm();
        
        switch (algorithm) {
            case "COOPERATE":
            case "TACTICAL_FOCUS":
                return 0.8; // Kooperative Entscheidungen fördern Bewusstseinsentwicklung
            case "COMPETE":
            case "Nearest_ENEMI":
                return 0.3; // Wettbewerbsorientierte Entscheidungen hemmen Entwicklung
            case "IDLE":
                return 0.1; // Keine Entscheidung = keine Entwicklung
            case "EVOLVE":
            case "GUARDIAN":
                return 1.0; // Bewusste Entscheidungen maximieren Entwicklung
            default:
                return 0.5;
        }
    }
    
    // ========== BEWUSSTSEINS-MODI AKTUALISIEREN ==========
    private void updateConsciousnessModes(List<Unit> units) {
        for (Unit unit : units) {
            double level = unit.getConsciousnessLevel();
            String currentMode = unit.getConsciousnessMode();
            String newMode = getConsciousnessMode(level);
            
            // Modus aktualisieren, wenn sich das Level signifikant geändert hat
            if (!currentMode.equals(newMode)) {
                unit.setConsciousnessMode(newMode);
                
                if (MatrixGameConfig.ENABLE_CONSCIOUSNESS_LOGGING) {
                    System.out.printf("CONSCIOUSNESS | Unit %d: %s -> %s (Level: %.2f)%n",
                        unit.getId(), currentMode, newMode, level);
                }
            }
        }
    }
    
    // ========== BEWUSSTSEINS-MODUS BESTIMMEN ==========
    public static String getConsciousnessMode(double level) {
        if (level < 20) return "Shame_Guilt";
        if (level < 30) return "Guilt";
        if (level < 50) return "Apathy";
        if (level < 75) return "Grief";
        if (level < 100) return "Fear";
        if (level < 125) return "Desire";
        if (level < 150) return "Anger";
        if (level < 175) return "Pride";
        if (level < 200) return "Fear_Pride";
        if (level < 250) return "Courage";
        if (level < 310) return "Neutrality";
        if (level < 350) return "Willingness";
        if (level < 400) return "Acceptance";
        if (level < 500) return "Reason";
        if (level < 540) return "Love";
        if (level < 600) return "Joy";
        if (level < 700) return "Peace";
        if (level < 800) return "Enlightenment";
        if (level < 1000) return "Bliss";
        return "Divine";
    }
    
    // ========== GUARDIAN-MODUS PRÜFEN ==========
    private void checkGuardianMode(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        
        for (Unit unit : units) {
            // Guardian-Modus aktivieren, wenn Bewusstsein unter einen bestimmten Wert fällt
            if (unit.getConsciousnessLevel() < MatrixGameConfig.COURAGE_THRESHOLD) {
                if (!"GUARDIAN".equals(unit.getConsciousnessMode())) {
                    unit.setConsciousnessMode("GUARDIAN");
                    unit.setDecisionAlgorithm("GUARDIAN");
                    
                    if (MatrixGameConfig.ENABLE_CONSCIOUSNESS_LOGGING) {
                        System.out.printf("GUARDIAN | Unit %d aktiviert Guardian-Modus (Level: %.2f)%n",
                            unit.getId(), unit.getConsciousnessLevel());
                    }
                }
            }
        }
    }
    
    // ========== KOLLEKTIVE BEWUSSTSEINS-ENTWICKLUNG ==========
    private void evolveCollectiveConsciousness(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        if (units.isEmpty()) return;
        
        // Durchschnittliches Bewusstseinslevel
        double avgLevel = 0.0;
        for (Unit unit : units) {
            avgLevel += unit.getConsciousnessLevel();
        }
        avgLevel /= units.size();
        
        // Kollektive Bewusstseinsentwicklung
        // Wenn der Durchschnitt hoch ist, entwickeln sich alle Einheiten schneller
        double collectiveFactor = Math.min(1.0, avgLevel / 500.0);
        
        for (Unit unit : units) {
            // Bonus für Einheiten mit hohem Bewusstsein
            if (unit.getConsciousnessLevel() > avgLevel * 1.2) {
                unit.evolveConsciousness(collectiveFactor * 0.5);
            }
            // Unterstützung für Einheiten mit niedrigem Bewusstsein
            else if (unit.getConsciousnessLevel() < avgLevel * 0.8) {
                unit.evolveConsciousness(collectiveFactor * 0.2);
            }
        }
    }
    
    // ========== STATISTIKEN AKTUALISIEREN ==========
    private void updateStatistics(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        if (units.isEmpty()) return;
        
        // Durchschnittliches Bewusstseinslevel
        double avgConsciousness = 0.0;
        double minConsciousness = Double.MAX_VALUE;
        double maxConsciousness = Double.MIN_VALUE;
        
        // Modus-Zählungen
        Map<String, Integer> modeCounts = new HashMap<>();
        
        for (Unit unit : units) {
            double level = unit.getConsciousnessLevel();
            avgConsciousness += level;
            minConsciousness = Math.min(minConsciousness, level);
            maxConsciousness = Math.max(maxConsciousness, level);
            
            String mode = unit.getConsciousnessMode();
            modeCounts.put(mode, modeCounts.getOrDefault(mode, 0) + 1);
        }
        
        avgConsciousness /= units.size();
        
        statistics.setAverageConsciousness(avgConsciousness);
        statistics.setMinConsciousness(minConsciousness);
        statistics.setMaxConsciousness(maxConsciousness);
        statistics.setConsciousnessModeCounts(modeCounts);
    }
    
    // ========== STATISTIKEN ABRUFEN ==========
    public SimulationStatistics getStatistics() {
        return statistics;
    }
    
    // ========== ANALYSE ==========
    public void analyze(MatrixGameCore core) {
        List<Unit> units = core.getUnits();
        if (units.isEmpty()) return;
        
        // 1. Bewusstseinsverteilung analysieren
        Map<String, Integer> modeDistribution = new HashMap<>();
        for (Unit unit : units) {
            String mode = unit.getConsciousnessMode();
            modeDistribution.put(mode, modeDistribution.getOrDefault(mode, 0) + 1);
        }
        
        // 2. Warnungen ausgeben
        int reactiveCount = 0;
        int proactiveCount = 0;
        
        for (String mode : modeDistribution.keySet()) {
            ConsciousnessLevel level = getLevelByMode(mode);
            if ("REACTIVE".equals(level.getCategory())) {
                reactiveCount += modeDistribution.get(mode);
            } else if ("PROACTIVE".equals(level.getCategory())) {
                proactiveCount += modeDistribution.get(mode);
            }
        }
        
        double reactiveRatio = (double) reactiveCount / units.size();
        double proactiveRatio = (double) proactiveCount / units.size();
        
        if (reactiveRatio > 0.7) {
            System.out.println("WARNUNG: Hoher Anteil reaktiver Bewusstseinsmodi (" + 
                (int)(reactiveRatio * 100) + "%) - System könnte instabil werden");
        }
        
        if (proactiveRatio > 0.7) {
            System.out.println("INFO: Hoher Anteil proaktiver Bewusstseinsmodi (" + 
                (int)(proactiveRatio * 100) + "%) - System ist stabil und entwickelt sich");
        }
        
        // 3. Durchschnittliches Bewusstseinslevel
        double avgLevel = statistics.getAverageConsciousness();
        if (avgLevel > MatrixGameConfig.LOVE_THRESHOLD) {
            System.out.println("INFO: Kollektives Bewusstsein erreicht Liebe-Ebene (" + 
                avgLevel + ") - Höchste Harmonie");
        } else if (avgLevel > MatrixGameConfig.ACCEPTANCE_THRESHOLD) {
            System.out.println("INFO: Kollektives Bewusstsein auf Akzeptanz-Ebene (" + 
                avgLevel + ") - Gute Entwicklung");
        } else if (avgLevel < MatrixGameConfig.COURAGE_THRESHOLD) {
            System.out.println("WARNUNG: Kollektives Bewusstsein unter Mut-Schwelle (" + 
                avgLevel + ") - Guardian-Modus aktiv");
        }
    }
    
    // ========== BEWUSSTSEINS-LEVEL ABRUFEN ==========
    public static ConsciousnessLevel getLevelByMode(String mode) {
        return HAWKINS_SCALE.get(mode);
    }
    
    public static ConsciousnessLevel getLevelByValue(double value) {
        ConsciousnessLevel bestMatch = null;
        double minDiff = Double.MAX_VALUE;
        
        for (ConsciousnessLevel level : HAWKINS_SCALE.values()) {
            double diff = Math.abs(level.getValue() - value);
            if (diff < minDiff) {
                minDiff = diff;
                bestMatch = level;
            }
        }
        
        return bestMatch;
    }
    
    // ========== BEWUSSTSEINS-ENTWICKLUNGSPFAD ==========
    public List<ConsciousnessLevel> getDevelopmentPath(double currentLevel, double targetLevel) {
        List<ConsciousnessLevel> path = new ArrayList<>();
        
        // Aktuellen Level finden
        ConsciousnessLevel current = getLevelByValue(currentLevel);
        ConsciousnessLevel target = getLevelByValue(targetLevel);
        
        // Alle Levels zwischen current und target sammeln
        boolean ascending = targetLevel > currentLevel;
        
        for (ConsciousnessLevel level : HAWKINS_SCALE.values()) {
            double levelValue = level.getValue();
            if (ascending && levelValue >= currentLevel && levelValue <= targetLevel) {
                path.add(level);
            } else if (!ascending && levelValue <= currentLevel && levelValue >= targetLevel) {
                path.add(level);
            }
        }
        
        // Sortieren
        if (ascending) {
            path.sort((l1, l2) -> Double.compare(l1.getValue(), l2.getValue()));
        } else {
            path.sort((l1, l2) -> Double.compare(l2.getValue(), l1.getValue()));
        }
        
        return path;
    }
    
    // ========== OPTIMIERUNG ==========
    public void optimize(MatrixGameCore core) {
        // Automatische Anpassung der Bewusstseinsparameter
        List<Unit> units = core.getUnits();
        
        if (units.isEmpty()) return;
        
        // Durchschnittliches Bewusstseinslevel
        double avgLevel = statistics.getAverageConsciousness();
        
        // Wenn der Durchschnitt zu niedrig ist, die Entwicklung beschleunigen
        if (avgLevel < MatrixGameConfig.COURAGE_THRESHOLD) {
            // Guardian-Modus für alle Einheiten aktivieren
            for (Unit unit : units) {
                if (unit.getConsciousnessLevel() < MatrixGameConfig.COURAGE_THRESHOLD) {
                    unit.setConsciousnessMode("GUARDIAN");
                    unit.setDecisionAlgorithm("GUARDIAN");
                }
            }
        }
        
        // Wenn der Durchschnitt hoch ist, die Entwicklung verlangsamen
        if (avgLevel > MatrixGameConfig.LOVE_THRESHOLD * 0.8) {
            // Keine spezielle Optimierung nötig - System ist stabil
        }
    }
    
    // ========== INNER KLASSE: BEWUSSTSEINS-LEVEL ==========
    public static class ConsciousnessLevel {
        private double value;
        private String name;
        private String description;
        private String category;
        private double developmentFactor;
        
        public ConsciousnessLevel(double value, String name, String description, 
                                String category, double developmentFactor) {
            this.value = value;
            this.name = name;
            this.description = description;
            this.category = category;
            this.developmentFactor = developmentFactor;
        }
        
        // Getter
        public double getValue() {
            return value;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getDevelopmentFactor() {
            return developmentFactor;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%.0f) - %s [%s]", name, value, description, category);
        }
    }
    
    // ========== INNER KLASSE: BEWUSSTSEINS-ZUSTAND ==========
    public static class ConsciousnessState {
        private double level;
        private String mode;
        private long timestamp;
        
        public ConsciousnessState(double level, String mode, long timestamp) {
            this.level = level;
            this.mode = mode;
            this.timestamp = timestamp;
        }
        
        // Getter
        public double getLevel() {
            return level;
        }
        
        public String getMode() {
            return mode;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
