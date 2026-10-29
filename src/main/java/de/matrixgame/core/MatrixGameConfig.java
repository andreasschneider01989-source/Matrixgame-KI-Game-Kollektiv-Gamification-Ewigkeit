package de.matrixgame.core;

import java.awt.Color;

/**
 * Konfiguration für den Matrixgame-Simulator
 * Enthält alle einstellbaren Parameter für Physik, Bewusstsein und Gamification
 */
public class MatrixGameConfig {
    
    // ========== PHYSIK-KONSTANTEN ==========
    public static double GRAVITATIONAL_CONSTANT = 6.67430e-11; // Gravitationskonstante
    public static double LIGHT_SPEED = 299792458.0; // Lichtgeschwindigkeit in m/s
    public static double PLANCK_CONSTANT = 6.62607015e-34; // Planck-Konstante
    public static double EPSILON_SINGULARITY = 1e-4; // Schutz vor Division durch Null
    
    // ========== SIMULATIONS-PARAMETER ==========
    public static double DEFAULT_TIME_STEP = 0.1; // Standard-Zeitschritt
    public static int MAX_UNITS = 10000; // Maximale Anzahl an Einheiten
    public static double UNIVERSE_SIZE = 1000.0; // Größe des Universums
    
    // ========== ENTROPIE-PARAMETER ==========
    public static double GLOBAL_ENTROPY_POOL = 100.0; // Globaler Entropie-Pool
    public static double ENTROPY_REGENERATION_RATE = 0.08; // Regenerationsrate pro Tick
    public static double ENTROPY_MOVE_PROBABILITY = 0.01; // Wahrscheinlichkeit für Entropie-Bewegung
    
    // ========== BEWUSSTSEINS-PARAMETER (Hawkins-Skala) ==========
    public static double MIN_CONSCIOUSNESS_LEVEL = 0.0; // Minimum (Scham/Schuld)
    public static double MAX_CONSCIOUSNESS_LEVEL = 1000.0; // Maximum (Erleuchtung)
    public static double COURAGE_THRESHOLD = 200.0; // Schwellenwert für Mut
    public static double ACCEPTANCE_THRESHOLD = 250.0; // Schwellenwert für Akzeptanz
    public static double LOVE_THRESHOLD = 500.0; // Schwellenwert für Liebe
    
    // ========== GAMIFICATION-PARAMETER ==========
    public static int BASE_EXPERIENCE = 10; // Basis-Erfahrungspunkte
    public static double EXPERIENCE_MULTIPLIER = 1.5; // Multiplikator für Erfahrung
    public static int LEVEL_UP_THRESHOLD = 1000; // Schwellenwert für Level-Up
    public static double REWARD_MULTIPLIER = 2.0; // Belohnungsmultiplikator
    
    // ========== SPIELTHEORIE-PARAMETER ==========
    public static double COOPERATION_REWARD = 3.0; // Belohnung für Kooperation
    public static double COMPETITION_REWARD = 2.0; // Belohnung für Wettbewerb
    public static double BETRAYAL_PENALTY = -5.0; // Strafe für Verrat
    public static double TRUST_BONUS = 1.5; // Bonus für Vertrauen
    
    // ========== DARSTELLUNG ==========
    public static Color DEFAULT_UNIT_COLOR = Color.CYAN;
    public static Color TEAM_1_COLOR = new Color(255, 100, 100); // Rot
    public static Color TEAM_2_COLOR = new Color(100, 100, 255); // Blau
    public static Color TEAM_3_COLOR = new Color(100, 255, 100); // Grün
    public static Color KING_COLOR = new Color(255, 215, 0); // Gold
    
    // ========== LOGGING ==========
    public static boolean ENABLE_PHYSICS_LOGGING = false;
    public static boolean ENABLE_CONSCIOUSNESS_LOGGING = false;
    public static boolean ENABLE_GAME_LOGGING = true;
    public static double LOG_PROBABILITY = 0.005; // 0.5% aller Bewegungen loggen
    
    // ========== OPTIMIERUNG ==========
    public static boolean USE_OPTIMIZED_PHYSICS = true;
    public static boolean USE_MULTITHREADING = false;
    public static int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    
    // ========== PROGNOSE ==========
    public static int PREDICTION_DEPTH = 5; // Tiefe der Prognose
    public static double PREDICTION_ACCURACY = 0.95; // Genauigkeit der Prognose
    public static int MONTE_CARLO_ITERATIONS = 100; // Iterationen für Monte-Carlo-Simulation
    
    // ========== SINGLETON ==========
    private static MatrixGameConfig instance;
    
    private MatrixGameConfig() {}
    
    public static synchronized MatrixGameConfig getInstance() {
        if (instance == null) {
            instance = new MatrixGameConfig();
        }
        return instance;
    }
    
    /**
     * Setzt die Konfiguration zurück auf Standardwerte
     */
    public static void resetToDefaults() {
        GRAVITATIONAL_CONSTANT = 6.67430e-11;
        LIGHT_SPEED = 299792458.0;
        PLANCK_CONSTANT = 6.62607015e-34;
        EPSILON_SINGULARITY = 1e-4;
        DEFAULT_TIME_STEP = 0.1;
        MAX_UNITS = 10000;
        UNIVERSE_SIZE = 1000.0;
        GLOBAL_ENTROPY_POOL = 100.0;
        ENTROPY_REGENERATION_RATE = 0.08;
        ENTROPY_MOVE_PROBABILITY = 0.01;
        MIN_CONSCIOUSNESS_LEVEL = 0.0;
        MAX_CONSCIOUSNESS_LEVEL = 1000.0;
        COURAGE_THRESHOLD = 200.0;
        ACCEPTANCE_THRESHOLD = 250.0;
        LOVE_THRESHOLD = 500.0;
        BASE_EXPERIENCE = 10;
        EXPERIENCE_MULTIPLIER = 1.5;
        LEVEL_UP_THRESHOLD = 1000;
        REWARD_MULTIPLIER = 2.0;
        COOPERATION_REWARD = 3.0;
        COMPETITION_REWARD = 2.0;
        BETRAYAL_PENALTY = -5.0;
        TRUST_BONUS = 1.5;
        ENABLE_PHYSICS_LOGGING = false;
        ENABLE_CONSCIOUSNESS_LOGGING = false;
        ENABLE_GAME_LOGGING = true;
        LOG_PROBABILITY = 0.005;
        USE_OPTIMIZED_PHYSICS = true;
        USE_MULTITHREADING = false;
        PREDICTION_DEPTH = 5;
        PREDICTION_ACCURACY = 0.95;
        MONTE_CARLO_ITERATIONS = 100;
    }
}
