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
 * Entscheidungsbaum für den Matrixgame-Simulator
 * Implementiert spieltheoretische Entscheidungsfindung
 */
public class DecisionTree {
    
    // ========== INSTANZ-VARIABLEN ==========
    private DecisionNode root;
    private Map<String, DecisionNode> nodes;
    private Random random;
    
    // ========== KONSTRUKTOR ==========
    public DecisionTree() {
        this.root = new DecisionNode("ROOT", "Wurzelknoten", null);
        this.nodes = new HashMap<>();
        this.random = ThreadLocalRandom.current();
        
        // Standard-Entscheidungsbaum initialisieren
        buildDefaultTree();
    }
    
    // ========== STANDARD-ENTSCHEIDUNGSBAUM AUFBAUEN ==========
    private void buildDefaultTree() {
        // Wurzelknoten
        root = new DecisionNode("ROOT", "Wurzelknoten", null);
        nodes.put("ROOT", root);
        
        // 1. Ebene: Gesundheit prüfen
        DecisionNode healthCheck = new DecisionNode(
            "HEALTH_CHECK", "Gesundheit prüfen", root
        );
        healthCheck.setCondition(unit -> unit.getHealthPercentage() < 30);
        healthCheck.setAction(unit -> {
            unit.setDecisionAlgorithm("RETREAT");
            return "RETREAT";
        });
        root.addChild(healthCheck);
        nodes.put("HEALTH_CHECK", healthCheck);
        
        // 2. Ebene: Ziel prüfen
        DecisionNode targetCheck = new DecisionNode(
            "TARGET_CHECK", "Ziel prüfen", root
        );
        targetCheck.setCondition(unit -> unit.getTarget() != null && unit.getTarget().isAlive());
        root.addChild(targetCheck);
        nodes.put("TARGET_CHECK", targetCheck);
        
        // 2.1 Ebene: Ziel in Reichweite
        DecisionNode targetInRange = new DecisionNode(
            "TARGET_IN_RANGE", "Ziel in Reichweite", targetCheck
        );
        targetInRange.setCondition(unit -> 
            unit.getTarget() != null && 
            unit.getDistance(unit.getTarget()) <= unit.getRange()
        );
        targetInRange.setAction(unit -> {
            unit.setDecisionAlgorithm("ATTACK");
            return "ATTACK";
        });
        targetCheck.addChild(targetInRange);
        nodes.put("TARGET_IN_RANGE", targetInRange);
        
        // 2.2 Ebene: Ziel nicht in Reichweite
        DecisionNode targetOutOfRange = new DecisionNode(
            "TARGET_OUT_OF_RANGE", "Ziel nicht in Reichweite", targetCheck
        );
        targetOutOfRange.setCondition(unit -> 
            unit.getTarget() != null && 
            unit.getDistance(unit.getTarget()) > unit.getRange()
        );
        targetOutOfRange.setAction(unit -> {
            unit.setDecisionAlgorithm("CHASE");
            return "CHASE";
        });
        targetCheck.addChild(targetOutOfRange);
        nodes.put("TARGET_OUT_OF_RANGE", targetOutOfRange);
        
        // 3. Ebene: Gegner in der Nähe
        DecisionNode enemyNear = new DecisionNode(
            "ENEMY_NEAR", "Gegner in der Nähe", root
        );
        enemyNear.setCondition(unit -> hasEnemyNear(unit, 50.0));
        root.addChild(enemyNear);
        nodes.put("ENEMY_NEAR", enemyNear);
        
        // 3.1 Ebene: Stärkster Gegner
        DecisionNode strongestEnemy = new DecisionNode(
            "STRONGEST_ENEMY", "Stärkster Gegner", enemyNear
        );
        strongestEnemy.setCondition(unit -> true); // Immer wahr
        strongestEnemy.setAction(unit -> {
            Unit strongest = findStrongestEnemy(unit);
            if (strongest != null) {
                unit.setTarget(strongest);
                unit.setDecisionAlgorithm("TACTICAL_FOCUS");
            }
            return "TACTICAL_FOCUS";
        });
        enemyNear.addChild(strongestEnemy);
        nodes.put("STRONGEST_ENEMY", strongestEnemy);
        
        // 3.2 Ebene: Nächster Gegner
        DecisionNode nearestEnemy = new DecisionNode(
            "NEAREST_ENEMY", "Nächster Gegner", enemyNear
        );
        nearestEnemy.setCondition(unit -> true); // Immer wahr
        nearestEnemy.setAction(unit -> {
            Unit nearest = findNearestEnemy(unit);
            if (nearest != null) {
                unit.setTarget(nearest);
                unit.setDecisionAlgorithm("Nearest_ENEMI");
            }
            return "Nearest_ENEMI";
        });
        enemyNear.addChild(nearestEnemy);
        nodes.put("NEAREST_ENEMY", nearestEnemy);
        
        // 4. Ebene: Teamkollegen in der Nähe
        DecisionNode teammateNear = new DecisionNode(
            "TEAMMATE_NEAR", "Teamkollegen in der Nähe", root
        );
        teammateNear.setCondition(unit -> hasTeammateNear(unit, 50.0));
        root.addChild(teammateNear);
        nodes.put("TEAMMATE_NEAR", teammateNear);
        
        // 4.1 Ebene: Kooperieren
        DecisionNode cooperate = new DecisionNode(
            "COOPERATE", "Kooperieren", teammateNear
        );
        cooperate.setCondition(unit -> true); // Immer wahr
        cooperate.setAction(unit -> {
            Unit teammate = findNearestTeammate(unit);
            if (teammate != null && teammate.getTarget() != null) {
                unit.setTarget(teammate.getTarget());
                unit.setDecisionAlgorithm("COOPERATE");
            }
            return "COOPERATE";
        });
        teammateNear.addChild(cooperate);
        nodes.put("COOPERATE", cooperate);
        
        // 4.2 Ebene: Formation bilden
        DecisionNode formFormation = new DecisionNode(
            "FORM_FORMATION", "Formation bilden", teammateNear
        );
        formFormation.setCondition(unit -> true); // Immer wahr
        formFormation.setAction(unit -> {
            Unit teammate = findNearestTeammate(unit);
            if (teammate != null) {
                // Position relativ zum Teamkollegen
                Vector3D offset = calculateFormationOffset(unit, teammate);
                unit.setTargetPosition(
                    teammate.getX() + offset.getX(),
                    teammate.getY() + offset.getY(),
                    teammate.getZ() + offset.getZ()
                );
                unit.setDecisionAlgorithm("FORMATION");
            }
            return "FORMATION";
        });
        teammateNear.addChild(formFormation);
        nodes.put("FORM_FORMATION", formFormation);
        
        // 5. Ebene: Keine Bedrohung
        DecisionNode noThreat = new DecisionNode(
            "NO_THREAT", "Keine Bedrohung", root
        );
        noThreat.setCondition(unit -> !hasEnemyNear(unit, 100.0));
        root.addChild(noThreat);
        nodes.put("NO_THREAT", noThreat);
        
        // 5.1 Ebene: Erkunden
        DecisionNode explore = new DecisionNode(
            "EXPLORE", "Erkunden", noThreat
        );
        explore.setCondition(unit -> true); // Immer wahr
        explore.setAction(unit -> {
            unit.setDecisionAlgorithm("EXPLORE");
            // Zufällige Position in der Nähe
            unit.setTargetPosition(
                unit.getX() + (random.nextDouble() - 0.5) * 100,
                unit.getY() + (random.nextDouble() - 0.5) * 100,
                unit.getZ()
            );
            return "EXPLORE";
        });
        noThreat.addChild(explore);
        nodes.put("EXPLORE", explore);
        
        // 5.2 Ebene: Bewusstsein entwickeln
        DecisionNode evolve = new DecisionNode(
            "EVOLVE", "Bewusstsein entwickeln", noThreat
        );
        evolve.setCondition(unit -> unit.getConsciousnessLevel() < MatrixGameConfig.LOVE_THRESHOLD);
        evolve.setAction(unit -> {
            unit.setDecisionAlgorithm("EVOLVE");
            unit.evolveConsciousness(1.0);
            return "EVOLVE";
        });
        noThreat.addChild(evolve);
        nodes.put("EVOLVE", evolve);
        
        // 6. Ebene: Standard (Idle)
        DecisionNode idle = new DecisionNode(
            "IDLE", "Warten", root
        );
        idle.setCondition(unit -> true); // Immer wahr
        idle.setAction(unit -> {
            unit.setDecisionAlgorithm("IDLE");
            return "IDLE";
        });
        root.addChild(idle);
        nodes.put("IDLE", idle);
    }
    
    // ========== ENTSCHEIDUNG TREFFEN ==========
    public String makeDecision(Unit unit, MatrixGameCore core) {
        if (unit == null) return "IDLE";
        
        // Durch den Entscheidungsbaum traversieren
        return traverseTree(unit, root, core);
    }
    
    // ========== BAUM TRAVERSIEREN ==========
    private String traverseTree(Unit unit, DecisionNode node, MatrixGameCore core) {
        // Prüfen, ob die Bedingung erfüllt ist
        if (node.getCondition() != null && node.getCondition().test(unit)) {
            // Wenn es Kinder gibt, diese prüfen
            if (!node.getChildren().isEmpty()) {
                for (DecisionNode child : node.getChildren()) {
                    String result = traverseTree(unit, child, core);
                    if (result != null) {
                        return result;
                    }
                }
            }
            
            // Wenn die Bedingung erfüllt ist und es eine Aktion gibt, diese ausführen
            if (node.getAction() != null) {
                return node.getAction().apply(unit);
            }
            
            return node.getName();
        }
        
        return null;
    }
    
    // ========== HILFSMETHODEN ==========
    private boolean hasEnemyNear(Unit unit, double distance) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() != unit.getTeam() && 
                unit.getDistance(other) <= distance) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasTeammateNear(Unit unit, double distance) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && 
                unit.getDistance(other) <= distance) {
                return true;
            }
        }
        return false;
    }
    
    private Unit findNearestEnemy(Unit unit) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        Unit nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() != unit.getTeam() && other.isAlive()) {
                double distance = unit.getDistance(other);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        
        return nearest;
    }
    
    private Unit findStrongestEnemy(Unit unit) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        Unit strongest = null;
        double maxStrength = 0;
        
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() != unit.getTeam() && other.isAlive()) {
                double strength = calculateStrength(other);
                if (strength > maxStrength) {
                    maxStrength = strength;
                    strongest = other;
                }
            }
        }
        
        return strongest;
    }
    
    private double calculateStrength(Unit unit) {
        // Stärke basierend auf Gesundheit, Level und Masse
        return unit.getHealthPercentage() * 0.4 + 
               unit.getLevel() * 10 * 0.3 + 
               unit.getMass() * 0.3;
    }
    
    private Unit findNearestTeammate(Unit unit) {
        MatrixGameCore core = MatrixGameCore.getInstance();
        Unit nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && other.isAlive()) {
                double distance = unit.getDistance(other);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        
        return nearest;
    }
    
    private de.matrixgame.physics.Vector3D calculateFormationOffset(Unit unit, Unit teammate) {
        // Einfache Formation: Kreis um den Teamkollegen
        double angle = System.currentTimeMillis() % 10000 / 10000.0 * Math.PI * 2;
        double distance = 20 + Math.random() * 10;
        
        return new de.matrixgame.physics.Vector3D(
            Math.cos(angle) * distance,
            Math.sin(angle) * distance,
            0
        );
    }
    
    // ========== SPIELTHEORIE-ENTSCHEIDUNGEN ==========
    public String makeGameTheoryDecision(Unit unit, MatrixGameCore core) {
        // Prüfen, ob Kooperation oder Wettbewerb besser ist
        double cooperationValue = calculateCooperationValue(unit, core);
        double competitionValue = calculateCompetitionValue(unit, core);
        
        if (cooperationValue > competitionValue * 1.2) {
            // Kooperation ist deutlich besser
            return makeCooperativeDecision(unit, core);
        } else if (competitionValue > cooperationValue * 1.2) {
            // Wettbewerb ist deutlich besser
            return makeCompetitiveDecision(unit, core);
        } else {
            // Ausgewogen - zufällig entscheiden
            return random.nextBoolean() ? 
                makeCooperativeDecision(unit, core) : 
                makeCompetitiveDecision(unit, core);
        }
    }
    
    private double calculateCooperationValue(Unit unit, MatrixGameCore core) {
        // Wert der Kooperation basierend auf Teamstärke und Nähe
        int teammateCount = countTeammates(unit, core);
        double avgTeammateStrength = averageTeammateStrength(unit, core);
        double enemyThreat = calculateEnemyThreat(unit, core);
        
        // Je mehr Teamkollegen und je stärker sie sind, desto höher der Kooperationswert
        // Je höher die Bedrohung, desto höher der Kooperationswert
        return teammateCount * 10 + avgTeammateStrength * 0.5 + enemyThreat * 2;
    }
    
    private double calculateCompetitionValue(Unit unit, MatrixGameCore core) {
        // Wert des Wettbewerbs basierend auf individueller Stärke und Gegneranzahl
        double ownStrength = calculateStrength(unit);
        int enemyCount = countEnemies(unit, core);
        double avgEnemyStrength = averageEnemyStrength(unit, core);
        
        // Je stärker die eigene Einheit und je schwächer die Gegner, desto höher der Wettbewerbswert
        return ownStrength * 2 - avgEnemyStrength * enemyCount * 0.5;
    }
    
    private int countTeammates(Unit unit, MatrixGameCore core) {
        int count = 0;
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && other.isAlive()) {
                count++;
            }
        }
        return count;
    }
    
    private int countEnemies(Unit unit, MatrixGameCore core) {
        int count = 0;
        for (Unit other : core.getUnits()) {
            if (other.getTeam() != unit.getTeam() && other.isAlive()) {
                count++;
            }
        }
        return count;
    }
    
    private double averageTeammateStrength(Unit unit, MatrixGameCore core) {
        double sum = 0;
        int count = 0;
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && other.isAlive()) {
                sum += calculateStrength(other);
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }
    
    private double averageEnemyStrength(Unit unit, MatrixGameCore core) {
        double sum = 0;
        int count = 0;
        for (Unit other : core.getUnits()) {
            if (other.getTeam() != unit.getTeam() && other.isAlive()) {
                sum += calculateStrength(other);
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }
    
    private double calculateEnemyThreat(Unit unit, MatrixGameCore core) {
        double threat = 0;
        for (Unit other : core.getUnits()) {
            if (other.getTeam() != unit.getTeam() && other.isAlive()) {
                double distance = unit.getDistance(other);
                double strength = calculateStrength(other);
                // Bedrohung ist umgekehrt proportional zur Distanz
                threat += strength / Math.max(1, distance);
            }
        }
        return threat;
    }
    
    private String makeCooperativeDecision(Unit unit, MatrixGameCore core) {
        // Kooperative Entscheidungen
        String[] options = {"COOPERATE", "TACTICAL_FOCUS", "FORMATION", "DEFEND"};
        
        // Basierend auf Situation auswählen
        if (hasTeammateInDanger(unit, core)) {
            return "DEFEND";
        } else if (hasTeammateWithTarget(unit, core)) {
            return "COOPERATE";
        } else {
            return options[random.nextInt(options.length)];
        }
    }
    
    private String makeCompetitiveDecision(Unit unit, MatrixGameCore core) {
        // Wettbewerbsorientierte Entscheidungen
        String[] options = {"Nearest_ENEMI", "CHASE", "ATTACK", "AMBUSH"};
        
        // Basierend auf Situation auswählen
        if (hasEnemyNear(unit, 20.0)) {
            return "ATTACK";
        } else if (hasEnemyNear(unit, 50.0)) {
            return "CHASE";
        } else {
            return "Nearest_ENEMI";
        }
    }
    
    private boolean hasTeammateInDanger(Unit unit, MatrixGameCore core) {
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && 
                other.getHealthPercentage() < 30) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasTeammateWithTarget(Unit unit, MatrixGameCore core) {
        for (Unit other : core.getUnits()) {
            if (other != unit && other.getTeam() == unit.getTeam() && 
                other.getTarget() != null) {
                return true;
            }
        }
        return false;
    }
    
    // ========== INNER KLASSE: ENTSCHEIDUNGS-KNOTEN ==========
    public static class DecisionNode {
        private String id;
        private String name;
        private String description;
        private DecisionNode parent;
        private List<DecisionNode> children;
        private Predicate<Unit> condition;
        private Function<Unit, String> action;
        
        public DecisionNode(String id, String name, DecisionNode parent) {
            this.id = id;
            this.name = name;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.description = "";
        }
        
        public DecisionNode(String id, String name, String description, DecisionNode parent) {
            this(id, name, parent);
            this.description = description;
        }
        
        // Getter/Setter
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public DecisionNode getParent() {
            return parent;
        }
        
        public void setParent(DecisionNode parent) {
            this.parent = parent;
        }
        
        public List<DecisionNode> getChildren() {
            return children;
        }
        
        public void addChild(DecisionNode child) {
            children.add(child);
            child.setParent(this);
        }
        
        public void removeChild(DecisionNode child) {
            children.remove(child);
            child.setParent(null);
        }
        
        public Predicate<Unit> getCondition() {
            return condition;
        }
        
        public void setCondition(Predicate<Unit> condition) {
            this.condition = condition;
        }
        
        public Function<Unit, String> getAction() {
            return action;
        }
        
        public void setAction(Function<Unit, String> action) {
            this.action = action;
        }
        
        @Override
        public String toString() {
            return String.format("DecisionNode[%s] %s", id, name);
        }
    }
    
    // ========== FUNKTIONELLE INTERFACES ==========
    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t);
    }
    
    @FunctionalInterface
    public interface Function<T, R> {
        R apply(T t);
    }
}
