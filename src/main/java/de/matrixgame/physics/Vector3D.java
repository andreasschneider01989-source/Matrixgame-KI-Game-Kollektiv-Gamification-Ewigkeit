package de.matrixgame.physics;

/**
 * 3D-Vektor für physikalische Berechnungen
 */
public class Vector3D {
    
    private double x;
    private double y;
    private double z;
    
    // ========== KONSTRUKTOREN ==========
    public Vector3D() {
        this(0, 0, 0);
    }
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // ========== GETTER/SETTER ==========
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // ========== VEKTOR-OPERATIONEN ==========
    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }
    
    public Vector3D scale(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
    
    public Vector3D multiply(Vector3D other) {
        return new Vector3D(x * other.x, y * other.y, z * other.z);
    }
    
    public Vector3D divide(Vector3D other) {
        return new Vector3D(x / other.x, y / other.y, z / other.z);
    }
    
    // ========== SKALAR-OPERATIONEN ==========
    public double dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    public Vector3D cross(Vector3D other) {
        return new Vector3D(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    // ========== LÄNGE UND NORMALISIERUNG ==========
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    public double distanceTo(Vector3D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public double distanceToSquared(Vector3D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public Vector3D normalize() {
        double len = length();
        if (len > 0) {
            return new Vector3D(x / len, y / len, z / len);
        }
        return new Vector3D(0, 0, 0);
    }
    
    // ========== ZEIT-INTEGRATION ==========
    public Vector3D integrate(Vector3D velocity, double dt) {
        return new Vector3D(
            x + velocity.x * dt,
            y + velocity.y * dt,
            z + velocity.z * dt
        );
    }
    
    // ========== KLONEN ==========
    @Override
    public Vector3D clone() {
        return new Vector3D(x, y, z);
    }
    
    // ========== STRING-REPRÄSENTATION ==========
    @Override
    public String toString() {
        return String.format("(%.4f, %.4f, %.4f)", x, y, z);
    }
    
    // ========== STATISCHE METHODEN ==========
    public static Vector3D zero() {
        return new Vector3D(0, 0, 0);
    }
    
    public static Vector3D one() {
        return new Vector3D(1, 1, 1);
    }
    
    public static Vector3D unitX() {
        return new Vector3D(1, 0, 0);
    }
    
    public static Vector3D unitY() {
        return new Vector3D(0, 1, 0);
    }
    
    public static Vector3D unitZ() {
        return new Vector3D(0, 0, 1);
    }
    
    public static double distance(Vector3D a, Vector3D b) {
        return a.distanceTo(b);
    }
    
    public static double distanceSquared(Vector3D a, Vector3D b) {
        return a.distanceToSquared(b);
    }
    
    // ========== VERGLEICH ==========
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3D vector3D = (Vector3D) obj;
        return Double.compare(vector3D.x, x) == 0 &&
               Double.compare(vector3D.y, y) == 0 &&
               Double.compare(vector3D.z, z) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        return result;
    }
    
    // ========== UTILITY ==========
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }
    
    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }
    
    public void clamp(double min, double max) {
        x = Math.max(min, Math.min(max, x));
        y = Math.max(min, Math.min(max, y));
        z = Math.max(min, Math.min(max, z));
    }
    
    public void abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }
}
