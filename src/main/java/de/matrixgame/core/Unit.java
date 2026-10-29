package de.matrixgame.core;

import de.matrixgame.physics.Vector3D;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Einheit (Unit) im Matrixgame
 * Repräsentiert ein physikalisches Objekt mit Bewusstsein und Gamification-Eigenschaften
 */
public class Unit {
    
    // ========== IDENTIFIKATION ==========
    private long id;
    private String name;
    private String type;
    private int team;
    
    // ========== PHYSIKALISCHE EIGENSCHAFTEN ==========
    private Vector3D position;
    private Vector3D velocity;
    private Vector3D acceleration;
    private double mass;
    private double radius;
    private Color color;
    
    // ========== BEWUSSTSEINS-EIGENSCHAFTEN (Hawkins-Skala) ==========
    private double consciousnessLevel; // Aktuelles Bewusstseinslevel (0-1000)
    private double consciousnessTarget; // Ziel-Bewusstseinslevel
    private String consciousnessMode; // Aktueller Modus (EGO, GUARDIAN, etc.)
    private Map<String, Double> consciousnessMetrics; // Metriken für verschiedene Bewusstseinsaspekte
    
    // ========== LEBENS- UND KAMPF-EIGENSCHAFTEN ==========
    private double health;
    private double maxHealth;
    private double damage;
    private double range;
    private double speed;
    
    // ========== ERFAHRUNG UND LEVEL ==========
    private int level;
    private double experience;
    private double experienceToNextLevel;
    
    // ========== BELOHNUNGEN UND QUESTS ==========
    private double rewardPoints;
    private Map<String, Boolean> completedQuests;
    private Map<String, Integer> achievements;
    
    // ========== ENTSCHEIDUNGSALGORITHMEN ==========
    private String decisionAlgorithm;
    private Unit target;
    private Unit manualTarget;
    private Vector3D targetPosition;
    private boolean isSelected;
    
    // ========== PHYSIK-FLAGS ==========
    private boolean movable;
    private boolean updatePositionActive;
    private boolean entropyMoveActive;
    private double entropyMoveSpeedPercent;
    private double maxSpeed;
    
    // ========== ZUSTAND ==========
    private boolean alive;
    private double creationTime;
    private double lastUpdateTime;
    
    // ========== KONSTRUKTOREN ==========
    public Unit() {
        this.id = generateId();
        this.name = "Unit-" + id;
        this.type = "Unit";
        this.team = 0;
        this.position = new Vector3D(0, 0, 0);
        this.velocity = new Vector3D(0, 0, 0);
        this.acceleration = new Vector3D(0, 0, 0);
        this.mass = 10.0;
        this.radius = 5;
        this.color = MatrixGameConfig.DEFAULT_UNIT_COLOR;
        this.consciousnessLevel = 100.0;
        this.consciousnessTarget = 200.0;
        this.consciousnessMode = "NEUTRAL";
        this.consciousnessMetrics = new HashMap<>();
        this.health = 100.0;
        this.maxHealth = 100.0;
        this.damage = 10.0;
        this.range = 2.0;
        this.speed = 0.5;
        this.level = 1;
        this.experience = 0.0;
        this.experienceToNextLevel = MatrixGameConfig.LEVEL_UP_THRESHOLD;
        this.rewardPoints = 0.0;
        this.completedQuests = new HashMap<>();
        this.achievements = new HashMap<>();
        this.decisionAlgorithm = "IDLE";
        this.target = null;
        this.manualTarget = null;
        this.targetPosition = null;
        this.isSelected = false;
        this.movable = true;
        this.updatePositionActive = true;
        this.entropyMoveActive = false;
        this.entropyMoveSpeedPercent = 0.0;
        this.maxSpeed = 100.0;
        this.alive = true;
        this.creationTime = MatrixGameCore.getInstance().getUniverseTime();
        this.lastUpdateTime = creationTime;
        
        // Standard-Bewusstseinsmetriken initialisieren
        initializeConsciousnessMetrics();
    }
    
    public Unit(String type, int team, double x, double y, double z) {
        this();
        this.type = type;
        this.team = team;
        this.position = new Vector3D(x, y, z);
        
        // Typ-spezifische Anpassungen
        if ("King".equalsIgnoreCase(type)) {
            this.radius = 8;
            this.maxHealth = 500.0;
            this.health = maxHealth;
            this.damage = 50.0;
            this.range = 3.0;
            this.speed = 0.3;
            this.color = MatrixGameConfig.KING_COLOR;
            this.consciousnessLevel = 300.0;
        } else if ("Warrior".equalsIgnoreCase(type)) {
            this.radius = 6;
            this.maxHealth = 200.0;
            this.health = maxHealth;
            this.damage = 25.0;
            this.range = 1.5;
            this.speed = 0.7;
            this.color = team == 0 ? MatrixGameConfig.TEAM_1_COLOR : 
                        team == 1 ? MatrixGameConfig.TEAM_2_COLOR : MatrixGameConfig.TEAM_3_COLOR;
        } else if ("Scout".equalsIgnoreCase(type)) {
            this.radius = 3;
            this.maxHealth = 50.0;
            this.health = maxHealth;
            this.damage = 5.0;
            this.range = 5.0;
            this.speed = 1.5;
            this.color = team == 0 ? MatrixGameConfig.TEAM_1_COLOR : 
                        team == 1 ? MatrixGameConfig.TEAM_2_COLOR : MatrixGameConfig.TEAM_3_COLOR;
        }
        
        this.name = type + "-" + team + "-" + id;
    }
    
    // ========== ID-GENERIERUNG ==========
    private static long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
    
    // ========== INITIALISIERUNG ==========
    private void initializeConsciousnessMetrics() {
        // Hawkins-Skala Metriken
        consciousnessMetrics.put("SHAME_GUILT", 0.0);
        consciousnessMetrics.put("FEAR_DESIRE", 0.0);
        consciousnessMetrics.put("ANGER_PRIDE", 0.0);
        consciousnessMetrics.put("COURAGE", 0.0);
        consciousnessMetrics.put("NEUTRALITY", 0.0);
        consciousnessMetrics.put("WILLINGNESS", 0.0);
        consciousnessMetrics.put("ACCEPTANCE", 0.0);
        consciousnessMetrics.put("REASON", 0.0);
        consciousnessMetrics.put("LOVE", 0.0);
        consciousnessMetrics.put("JOY", 0.0);
        consciousnessMetrics.put("PEACE", 0.0);
        consciousnessMetrics.put("ENLIGHTENMENT", 0.0);
    }
    
    // ========== GETTER/SETTER ==========
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getTeam() {
        return team;
    }
    
    public void setTeam(int team) {
        this.team = team;
    }
    
    // ========== POSITION ==========
    public Vector3D getPosition() {
        return position;
    }
    
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    public void setPosition(double x, double y, double z) {
        this.position = new Vector3D(x, y, z);
    }
    
    public double getX() {
        return position.getX();
    }
    
    public void setX(double x) {
        position.setX(x);
    }
    
    public double getY() {
        return position.getY();
    }
    
    public void setY(double y) {
        position.setY(y);
    }
    
    public double getZ() {
        return position.getZ();
    }
    
    public void setZ(double z) {
        position.setZ(z);
    }
    
    // ========== GESCHWINDIGKEIT ==========
    public Vector3D getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }
    
    public void setVelocity(double vx, double vy, double vz) {
        this.velocity = new Vector3D(vx, vy, vz);
    }
    
    public double getVx() {
        return velocity.getX();
    }
    
    public void setVx(double vx) {
        velocity.setX(vx);
    }
    
    public double getVy() {
        return velocity.getY();
    }
    
    public void setVy(double vy) {
        velocity.setY(vy);
    }
    
    public double getVz() {
        return velocity.getZ();
    }
    
    public void setVz(double vz) {
        velocity.setZ(vz);
    }
    
    // ========== BESCHLEUNIGUNG ==========
    public Vector3D getAcceleration() {
        return acceleration;
    }
    
    public void setAcceleration(Vector3D acceleration) {
        this.acceleration = acceleration;
    }
    
    public void setAcceleration(double ax, double ay, double az) {
        this.acceleration = new Vector3D(ax, ay, az);
    }
    
    // ========== MASSE ==========
    public double getMass() {
        return mass;
    }
    
    public void setMass(double mass) {
        this.mass = Math.max(0.1, mass);
    }
    
    // ========== RADIUS ==========
    public double getRadius() {
        return radius;
    }
    
    public void setRadius(double radius) {
        this.radius = Math.max(1, radius);
    }
    
    // ========== FARBE ==========
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color != null ? color : Color.WHITE;
    }
    
    // ========== BEWUSSTSEIN ==========
    public double getConsciousnessLevel() {
        return consciousnessLevel;
    }
    
    public void setConsciousnessLevel(double level) {
        this.consciousnessLevel = Math.max(MatrixGameConfig.MIN_CONSCIOUSNESS_LEVEL, 
                                          Math.min(MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL, level));
    }
    
    public double getConsciousnessTarget() {
        return consciousnessTarget;
    }
    
    public void setConsciousnessTarget(double target) {
        this.consciousnessTarget = target;
    }
    
    public String getConsciousnessMode() {
        return consciousnessMode;
    }
    
    public void setConsciousnessMode(String mode) {
        this.consciousnessMode = mode;
    }
    
    public Map<String, Double> getConsciousnessMetrics() {
        return consciousnessMetrics;
    }
    
    public void setConsciousnessMetric(String key, double value) {
        consciousnessMetrics.put(key, value);
    }
    
    public double getConsciousnessMetric(String key) {
        return consciousnessMetrics.getOrDefault(key, 0.0);
    }
    
    // ========== LEBEN ==========
    public double getHealth() {
        return health;
    }
    
    public void setHealth(double health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }
    
    public double getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.health = Math.min(health, maxHealth);
    }
    
    public double getHealthPercentage() {
        return (health / maxHealth) * 100.0;
    }
    
    public boolean isAlive() {
        return alive && health > 0;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    // ========== KAMPF ==========
    public double getDamage() {
        return damage;
    }
    
    public void setDamage(double damage) {
        this.damage = Math.max(0, damage);
    }
    
    public double getRange() {
        return range;
    }
    
    public void setRange(double range) {
        this.range = Math.max(0, range);
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = Math.max(0, speed);
    }
    
    // ========== ERFAHRUNG ==========
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
    
    public double getExperience() {
        return experience;
    }
    
    public void setExperience(double experience) {
        this.experience = Math.max(0, experience);
    }
    
    public double getExperienceToNextLevel() {
        return experienceToNextLevel;
    }
    
    public void setExperienceToNextLevel(double experienceToNextLevel) {
        this.experienceToNextLevel = Math.max(1, experienceToNextLevel);
    }
    
    public double getExperiencePercentage() {
        return (experience / experienceToNextLevel) * 100.0;
    }
    
    // ========== BELOHNUNGEN ==========
    public double getRewardPoints() {
        return rewardPoints;
    }
    
    public void setRewardPoints(double rewardPoints) {
        this.rewardPoints = Math.max(0, rewardPoints);
    }
    
    public void addRewardPoints(double points) {
        this.rewardPoints += points;
    }
    
    public Map<String, Boolean> getCompletedQuests() {
        return completedQuests;
    }
    
    public boolean hasCompletedQuest(String questId) {
        return completedQuests.getOrDefault(questId, false);
    }
    
    public void completeQuest(String questId) {
        completedQuests.put(questId, true);
    }
    
    public Map<String, Integer> getAchievements() {
        return achievements;
    }
    
    public int getAchievementCount(String achievementId) {
        return achievements.getOrDefault(achievementId, 0);
    }
    
    public void incrementAchievement(String achievementId) {
        achievements.put(achievementId, achievements.getOrDefault(achievementId, 0) + 1);
    }
    
    // ========== ENTSCHEIDUNGEN ==========
    public String getDecisionAlgorithm() {
        return decisionAlgorithm;
    }
    
    public void setDecisionAlgorithm(String algorithm) {
        this.decisionAlgorithm = algorithm;
    }
    
    public Unit getTarget() {
        return target;
    }
    
    public void setTarget(Unit target) {
        this.target = target;
    }
    
    public Unit getManualTarget() {
        return manualTarget;
    }
    
    public void setManualTarget(Unit manualTarget) {
        this.manualTarget = manualTarget;
    }
    
    public Vector3D getTargetPosition() {
        return targetPosition;
    }
    
    public void setTargetPosition(Vector3D targetPosition) {
        this.targetPosition = targetPosition;
    }
    
    public void setTargetPosition(double x, double y, double z) {
        this.targetPosition = new Vector3D(x, y, z);
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    // ========== PHYSIK-FLAGS ==========
    public boolean isMovable() {
        return movable;
    }
    
    public void setMovable(boolean movable) {
        this.movable = movable;
    }
    
    public boolean isUpdatePositionActive() {
        return updatePositionActive;
    }
    
    public void setUpdatePositionActive(boolean updatePositionActive) {
        this.updatePositionActive = updatePositionActive;
    }
    
    public boolean isEntropyMoveActive() {
        return entropyMoveActive;
    }
    
    public void setEntropyMoveActive(boolean entropyMoveActive) {
        this.entropyMoveActive = entropyMoveActive;
    }
    
    public double getEntropyMoveSpeedPercent() {
        return entropyMoveSpeedPercent;
    }
    
    public void setEntropyMoveSpeedPercent(double percent) {
        this.entropyMoveSpeedPercent = Math.max(0, Math.min(100, percent));
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }
    
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = Math.max(0, maxSpeed);
    }
    
    // ========== ZEIT ==========
    public double getCreationTime() {
        return creationTime;
    }
    
    public double getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(double time) {
        this.lastUpdateTime = time;
    }
    
    // ========== ENERGIE ==========
    public double getKineticEnergy() {
        return 0.5 * mass * velocity.lengthSquared();
    }
    
    public double getPotentialEnergy() {
        // Vereinfachte Berechnung - wird in der Physik-Engine genauer berechnet
        return 0.0;
    }
    
    public double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }
    
    // ========== BEWEGUNG ==========
    public void moveTo(double targetX, double targetY, double targetZ, double moveSpeed) {
        if (!movable || !updatePositionActive) return;
        
        double oldX = position.getX();
        double oldY = position.getY();
        double oldZ = position.getZ();
        
        double dx = targetX - position.getX();
        double dy = targetY - position.getY();
        double dz = targetZ - position.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (dist > 0.0001) {
            position.setX(position.getX() + (dx / dist) * moveSpeed);
            position.setY(position.getY() + (dy / dist) * moveSpeed);
            position.setZ(position.getZ() + (dz / dist) * moveSpeed);
            
            // Loggen
            if (MatrixGameConfig.ENABLE_PHYSICS_LOGGING && 
                Math.random() <= MatrixGameConfig.LOG_PROBABILITY) {
                System.out.printf("MOVE | Unit %d von (%.2f, %.2f, %.2f) -> (%.2f, %.2f, %.2f)%n",
                    id, oldX, oldY, oldZ, position.getX(), position.getY(), position.getZ());
            }
        }
    }
    
    public void moveTo(Unit other) {
        if (other != null) {
            moveTo(other.getX(), other.getY(), other.getZ(), speed);
        }
    }
    
    // ========== DISTANZ ==========
    public double getDistance(Unit other) {
        if (other == null) return Double.MAX_VALUE;
        return position.distanceTo(other.position);
    }
    
    public double getDistanceTo(double x, double y, double z) {
        double dx = x - position.getX();
        double dy = y - position.getY();
        double dz = z - position.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    // ========== ANGRIFF ==========
    public void attack(Unit enemy) {
        if (enemy == null || !enemy.isAlive() || !isAlive()) return;
        
        double distance = getDistance(enemy);
        if (distance > range) return;
        
        double criticalMultiplier = 1.0 + (Math.random() * 2.5);
        double actualDamage = damage * criticalMultiplier;
        
        enemy.setHealth(enemy.getHealth() - actualDamage);
        
        if (MatrixGameConfig.ENABLE_PHYSICS_LOGGING && 
            Math.random() <= MatrixGameConfig.LOG_PROBABILITY) {
            System.out.printf("ATTACK | Unit %d greift Unit %d an! Schaden: %.2f (Rest-HP: %.2f)%n",
                id, enemy.getId(), actualDamage, enemy.getHealth());
        }
        
        if (enemy.getHealth() <= 0) {
            enemy.setHealth(0);
            enemy.setAlive(false);
            
            if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
                System.out.printf("DESTROY | Unit %d (%s) wurde vernichtet durch Unit %d%n",
                    enemy.getId(), enemy.getType(), id);
            }
            
            // Erfahrung für den Angreifer
            addExperience(actualDamage * 10);
        }
    }
    
    // ========== ERFAHRUNG ==========
    public void addExperience(double exp) {
        experience += exp * MatrixGameConfig.EXPERIENCE_MULTIPLIER;
        
        // Level-Up prüfen
        while (experience >= experienceToNextLevel) {
            experience -= experienceToNextLevel;
            levelUp();
        }
    }
    
    private void levelUp() {
        level++;
        maxHealth *= 1.1;
        health = maxHealth;
        damage *= 1.05;
        speed *= 1.02;
        consciousnessLevel = Math.min(MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL, 
                                     consciousnessLevel * 1.05);
        experienceToNextLevel *= 1.2;
        
        if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
            System.out.printf("LEVEL UP | Unit %d ist jetzt Level %d%n", id, level);
        }
    }
    
    // ========== BEWUSSTSEINS-ENTWICKLUNG ==========
    public void evolveConsciousness(double amount) {
        setConsciousnessLevel(consciousnessLevel + amount);
        
        // Modus basierend auf Bewusstseinslevel aktualisieren
        updateConsciousnessMode();
    }
    
    private void updateConsciousnessMode() {
        if (consciousnessLevel < 20) {
            consciousnessMode = "SHame_Guilt";
        } else if (consciousnessLevel < 50) {
            consciousnessMode = "Apathy_Grief";
        } else if (consciousnessLevel < 100) {
            consciousnessMode = "Fear_Desire";
        } else if (consciousnessLevel < 200) {
            consciousnessMode = "Anger_Pride";
        } else if (consciousnessLevel < 250) {
            consciousnessMode = "Courage";
        } else if (consciousnessLevel < 350) {
            consciousnessMode = "Neutrality_Willingness";
        } else if (consciousnessLevel < 400) {
            consciousnessMode = "Acceptance_Reason";
        } else if (consciousnessLevel < 600) {
            consciousnessMode = "Love_Joy";
        } else if (consciousnessLevel < 800) {
            consciousnessMode = "Peace";
        } else {
            consciousnessMode = "Enlightenment";
        }
    }
    
    // ========== ZUSTANDS-AKTUALISIERUNG ==========
    public void update(double dt) {
        if (!alive) return;
        
        lastUpdateTime = MatrixGameCore.getInstance().getUniverseTime();
        
        // 1. Entropie-Bewegung
        if (entropyMoveActive && entropyMoveSpeedPercent > 0) {
            entropyMove();
        }
        
        // 2. Positions-Update
        if (updatePositionActive && movable) {
            position.add(velocity.clone().scale(dt));
            
            // Geschwindigkeit begrenzen
            if (velocity.length() > maxSpeed) {
                velocity = velocity.normalize().scale(maxSpeed);
            }
        }
        
        // 3. Beschleunigung anwenden
        velocity.add(acceleration.clone().scale(dt));
        acceleration.set(0, 0, 0);
        
        // 4. Bewusstsein aktualisieren
        updateConsciousnessMode();
    }
    
    // ========== ENTROPIE-BEWEGUNG ==========
    public void entropyMove() {
        if (entropyMoveSpeedPercent <= 0) return;
        
        double factor = entropyMoveSpeedPercent / 100.0;
        position.setX(position.getX() + (Math.random() - 0.5) * factor);
        position.setY(position.getY() + (Math.random() - 0.5) * factor);
        position.setZ(position.getZ() + (Math.random() - 0.5) * factor);
    }
    
    // ========== ZUSAMMENFÜHRUNG ==========
    public void merge(Unit other) {
        if (other == null) return;
        
        // Masse kombinieren
        mass += other.mass;
        
        // Radius anpassen (Volumen-Addition)
        radius = (int) Math.cbrt(Math.pow(radius, 3) + Math.pow(other.radius, 3));
        
        // Geschwindigkeit kombinieren (Impulserhaltung)
        double totalMass = mass + other.mass;
        velocity = velocity.scale(mass).add(other.velocity.scale(other.mass)).scale(1.0 / totalMass);
        
        // Farbe kombinieren (Harmonische Synthese)
        color = combineColors(color, other.color);
        
        // Bewusstsein kombinieren
        consciousnessLevel = (consciousnessLevel + other.consciousnessLevel) / 2.0;
        
        // Gesundheit kombinieren
        maxHealth += other.maxHealth;
        health += other.health;
        
        // Schaden kombinieren
        damage += other.damage;
        
        // Erfahrung kombinieren
        experience += other.experience;
        rewardPoints += other.rewardPoints;
        
        // Team behalten (oder des größeren Units)
        if (other.mass > mass) {
            team = other.team;
        }
        
        // Name aktualisieren
        name = type + "-" + team + "-" + id + "(Merged)";
        
        // Loggen
        if (MatrixGameConfig.ENABLE_GAME_LOGGING) {
            System.out.printf("MERGE | Unit %d hat Unit %d absorbiert%n", id, other.getId());
        }
    }
    
    // ========== FARB-KOMBINATION ==========
    private Color combineColors(Color c1, Color c2) {
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return new Color(
            (c1.getRed() + c2.getRed()) / 2,
            (c1.getGreen() + c2.getGreen()) / 2,
            (c1.getBlue() + c2.getBlue()) / 2
        );
    }
    
    // ========== KLONEN ==========
    @Override
    public Unit clone() {
        Unit clone = new Unit();
        clone.id = generateId();
        clone.name = name + "_Clone";
        clone.type = type;
        clone.team = team;
        clone.position = position.clone();
        clone.velocity = velocity.clone();
        clone.acceleration = acceleration.clone();
        clone.mass = mass;
        clone.radius = radius;
        clone.color = color;
        clone.consciousnessLevel = consciousnessLevel;
        clone.consciousnessTarget = consciousnessTarget;
        clone.consciousnessMode = consciousnessMode;
        clone.consciousnessMetrics = new HashMap<>(consciousnessMetrics);
        clone.health = health;
        clone.maxHealth = maxHealth;
        clone.damage = damage;
        clone.range = range;
        clone.speed = speed;
        clone.level = level;
        clone.experience = experience;
        clone.experienceToNextLevel = experienceToNextLevel;
        clone.rewardPoints = rewardPoints;
        clone.completedQuests = new HashMap<>(completedQuests);
        clone.achievements = new HashMap<>(achievements);
        clone.decisionAlgorithm = decisionAlgorithm;
        clone.movable = movable;
        clone.updatePositionActive = updatePositionActive;
        clone.entropyMoveActive = entropyMoveActive;
        clone.entropyMoveSpeedPercent = entropyMoveSpeedPercent;
        clone.maxSpeed = maxSpeed;
        clone.alive = alive;
        clone.creationTime = MatrixGameCore.getInstance().getUniverseTime();
        clone.lastUpdateTime = clone.creationTime;
        
        return clone;
    }
    
    // ========== STRING-REPRÄSENTATION ==========
    @Override
    public String toString() {
        return String.format("Unit[id=%d, name=%s, type=%s, team=%d, pos=(%.2f, %.2f, %.2f), health=%.2f/%.2f, level=%d, consciousness=%.2f]",
            id, name, type, team, position.getX(), position.getY(), position.getZ(),
            health, maxHealth, level, consciousnessLevel);
    }
    
    // ========== DYNAMISCHER ZUGRIFF ==========
    public double getVariable(String name) {
        switch (name.toLowerCase()) {
            case "x": return position.getX();
            case "y": return position.getY();
            case "z": return position.getZ();
            case "vx": return velocity.getX();
            case "vy": return velocity.getY();
            case "vz": return velocity.getZ();
            case "mass":
            case "masse": return mass;
            case "radius": return radius;
            case "health": return health;
            case "maxhealth": return maxHealth;
            case "damage": return damage;
            case "range": return range;
            case "speed": return speed;
            case "level": return level;
            case "experience": return experience;
            case "consciousness": return consciousnessLevel;
            case "rewardpoints": return rewardPoints;
            case "ekin": return getKineticEnergy();
            case "time": return lastUpdateTime;
            default: return 0.0;
        }
    }
    
    public void setVariable(String name, double value) {
        switch (name.toLowerCase()) {
            case "x": position.setX(value); break;
            case "y": position.setY(value); break;
            case "z": position.setZ(value); break;
            case "vx": velocity.setX(value); break;
            case "vy": velocity.setY(value); break;
            case "vz": velocity.setZ(value); break;
            case "mass":
            case "masse": setMass(value); break;
            case "radius": setRadius(value); break;
            case "health": setHealth(value); break;
            case "maxhealth": setMaxHealth(value); break;
            case "damage": setDamage(value); break;
            case "range": setRange(value); break;
            case "speed": setSpeed(value); break;
            case "level": setLevel((int) value); break;
            case "experience": setExperience(value); break;
            case "consciousness": setConsciousnessLevel(value); break;
            case "rewardpoints": setRewardPoints(value); break;
        }
    }
}
