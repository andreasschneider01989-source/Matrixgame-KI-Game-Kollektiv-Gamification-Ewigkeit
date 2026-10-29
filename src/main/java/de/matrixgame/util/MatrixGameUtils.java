package de.matrixgame.util;

import de.matrixgame.core.Unit;
import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.MatrixGameConfig;
import de.matrixgame.physics.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hilfsklasse für den Matrixgame-Simulator
 * Enthält nützliche Utility-Methoden
 */
public class MatrixGameUtils {
    
    // ========== ZUFALLS-GENERATOR ==========
    private static Random random = ThreadLocalRandom.current();
    
    // ========== ZUFALLS-METHODEN ==========
    public static double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
    
    public static double randomDouble(double factor) {
        return factor * (random.nextDouble() - random.nextDouble());
    }
    
    public static double randomDoublePos(double factor) {
        return factor * random.nextDouble();
    }
    
    public static int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
    
    public static boolean randomBoolean(double probability) {
        return random.nextDouble() < probability;
    }
    
    // ========== EINHEITEN-GENERATOR ==========
    public static Unit createRandomUnit() {
        String[] types = {"Unit", "Warrior", "Scout", "King"};
        String type = types[random.nextInt(types.length)];
        int team = random.nextInt(3);
        
        Unit unit = new Unit(type, team, 
            randomDouble(-100, 100),
            randomDouble(-100, 100),
            randomDouble(-100, 100)
        );
        
        unit.setVelocity(
            randomDouble(-5, 5),
            randomDouble(-5, 5),
            randomDouble(-5, 5)
        );
        
        unit.setMass(randomDouble(10, 50));
        unit.setHealth(randomDouble(50, 200));
        unit.setMaxHealth(unit.getHealth());
        unit.setDamage(randomDouble(5, 20));
        unit.setRange(randomDouble(1, 5));
        unit.setSpeed(randomDouble(0.1, 1.0));
        unit.setConsciousnessLevel(randomDouble(0, MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL));
        
        return unit;
    }
    
    public static Unit createKingUnit(int team) {
        Unit king = new Unit("King", team, 0, 0, 0);
        king.setMass(100);
        king.setRadius(10);
        king.setHealth(500);
        king.setMaxHealth(500);
        king.setDamage(50);
        king.setRange(3);
        king.setSpeed(0.3);
        king.setConsciousnessLevel(300);
        return king;
    }
    
    public static Unit createWarriorUnit(int team) {
        Unit warrior = new Unit("Warrior", team, 0, 0, 0);
        warrior.setMass(30 + random.nextDouble() * 20);
        warrior.setRadius(6);
        warrior.setHealth(200);
        warrior.setMaxHealth(200);
        warrior.setDamage(25);
        warrior.setRange(1.5);
        warrior.setSpeed(0.7);
        warrior.setConsciousnessLevel(150 + random.nextDouble() * 100);
        return warrior;
    }
    
    public static Unit createScoutUnit(int team) {
        Unit scout = new Unit("Scout", team, 0, 0, 0);
        scout.setMass(15 + random.nextDouble() * 10);
        scout.setRadius(4);
        scout.setHealth(50);
        scout.setMaxHealth(50);
        scout.setDamage(5);
        scout.setRange(5);
        scout.setSpeed(1.5);
        scout.setConsciousnessLevel(100 + random.nextDouble() * 100);
        return scout;
    }
    
    // ========== ARRAY- UND LISTEN-OPERATIONEN ==========
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(function.apply(item));
        }
        return result;
    }
    
    public static <T> T findFirst(List<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }
    
    public static <T> boolean any(List<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (predicate.test(item)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> boolean all(List<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (!predicate.test(item)) {
                return false;
            }
        }
        return true;
    }
    
    public static <T> int count(List<T> list, Predicate<T> predicate) {
        int count = 0;
        for (T item : list) {
            if (predicate.test(item)) {
                count++;
            }
        }
        return count;
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
    
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }
    
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
    
    // ========== MATHEMATISCHE METHODEN ==========
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
    
    public static double inverseLerp(double a, double b, double value) {
        return (value - a) / (b - a);
    }
    
    public static double smoothstep(double t) {
        return t * t * (3 - 2 * t);
    }
    
    public static double remap(double value, double inMin, double inMax, double outMin, double outMax) {
        double t = inverseLerp(inMin, inMax, value);
        return lerp(outMin, outMax, t);
    }
    
    // ========== VEKTOR-METHODEN ==========
    public static Vector3D randomVector3D(double min, double max) {
        return new Vector3D(
            randomDouble(min, max),
            randomDouble(min, max),
            randomDouble(min, max)
        );
    }
    
    public static Vector3D randomUnitVector3D() {
        Vector3D v;
        do {
            v = randomVector3D(-1, 1);
        } while (v.lengthSquared() > 1);
        return v.normalize();
    }
    
    public static Vector3D randomVector3DOnSphere(double radius) {
        return randomUnitVector3D().scale(radius);
    }
    
    // ========== FARB-METHODEN ==========
    public static java.awt.Color randomColor() {
        return new java.awt.Color(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        );
    }
    
    public static java.awt.Color randomTeamColor(int team) {
        switch (team % 3) {
            case 0: return new java.awt.Color(255, 100, 100); // Rot
            case 1: return new java.awt.Color(100, 100, 255); // Blau
            case 2: return new java.awt.Color(100, 255, 100); // Grün
            default: return randomColor();
        }
    }
    
    public static java.awt.Color blendColors(java.awt.Color c1, java.awt.Color c2, double t) {
        int r = (int) lerp(c1.getRed(), c2.getRed(), t);
        int g = (int) lerp(c1.getGreen(), c2.getGreen(), t);
        int b = (int) lerp(c1.getBlue(), c2.getBlue(), t);
        return new java.awt.Color(r, g, b);
    }
    
    // ========== STRING-METHODEN ==========
    public static String formatNumber(double value) {
        if (value >= 1000000 || value <= -1000000) {
            return String.format("%.2fM", value / 1000000);
        } else if (value >= 1000 || value <= -1000) {
            return String.format("%.2fK", value / 1000);
        } else if (value >= 1 || value <= -1) {
            return String.format("%.2f", value);
        } else {
            return String.format("%.4f", value);
        }
    }
    
    public static String formatPercentage(double value) {
        return String.format("%.1f%%", value * 100);
    }
    
    public static String formatTime(double seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    // ========== EINHEITEN-STATISTIKEN ==========
    public static double calculateAverageHealth(List<Unit> units) {
        if (units.isEmpty()) return 0;
        double sum = 0;
        for (Unit unit : units) {
            sum += unit.getHealthPercentage();
        }
        return sum / units.size();
    }
    
    public static double calculateAverageLevel(List<Unit> units) {
        if (units.isEmpty()) return 0;
        double sum = 0;
        for (Unit unit : units) {
            sum += unit.getLevel();
        }
        return sum / units.size();
    }
    
    public static double calculateTotalMass(List<Unit> units) {
        double total = 0;
        for (Unit unit : units) {
            total += unit.getMass();
        }
        return total;
    }
    
    public static double calculateTotalEnergy(List<Unit> units) {
        double total = 0;
        for (Unit unit : units) {
            total += unit.getKineticEnergy();
        }
        return total;
    }
    
    // ========== KOLLISIONS-ERKENNUNG ==========
    public static boolean checkCollision(Unit u1, Unit u2) {
        double distance = u1.getDistance(u2);
        return distance < (u1.getRadius() + u2.getRadius());
    }
    
    public static boolean checkCollision(Unit u1, Unit u2, double threshold) {
        double distance = u1.getDistance(u2);
        return distance < (u1.getRadius() + u2.getRadius() + threshold);
    }
    
    // ========== DISTANZ-METHODEN ==========
    public static double distance(Vector3D v1, Vector3D v2) {
        return v1.distanceTo(v2);
    }
    
    public static double distanceSquared(Vector3D v1, Vector3D v2) {
        return v1.distanceToSquared(v2);
    }
    
    // ========== WINKEL-METHODEN ==========
    public static double angleBetween(Vector3D v1, Vector3D v2) {
        double dot = v1.dot(v2);
        double len1 = v1.length();
        double len2 = v2.length();
        return Math.acos(dot / (len1 * len2));
    }
    
    public static double angleBetweenDegrees(Vector3D v1, Vector3D v2) {
        return Math.toDegrees(angleBetween(v1, v2));
    }
}
