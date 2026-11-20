package editor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Laufzeit-Editor: alle Schrauben zentral, per Tasten live aenderbar UND
 * per Datei (config.properties) hot-reloadbar, ohne Neustart.
 */
public final class Config {
    // Welt
    public int width = 48, height = 32;
    public long seed = 42;
    // Population
    public int startUnits = 40;
    public int maxAge = 1200;
    // Physik / Regeln
    public double metabolism = 0.0012;   // Energieverbrauch pro Tick
    public double foodEnergy = 0.5;
    public double foodRegrow = 0.18;     // Chance pro Tick, dass 1 Futter spawnt
    public double startEnergy = 1.0;
    public double reproduceEnergy = 1.5; // Mindestenergie zum Teilen
    public double mutationRate = 0.06;
    public boolean gravity = true;       // Boulder fallen (Boulder-Dash)
    public double gravityAccel = 0.35;   // Newton: Beschleunigung g pro Tick
    public double scentDecay = 0.01;     // Pheromon-Verfall
    public double wildness = 0.5;        // Staerke des r/r-Wildfaktors (0-1)
    public boolean autoBalance = true;   // Auto-Einstellung: Oekosystem regelt selbst
    public int mode = 0;                 // 0 = UnitFight, 1 = BoulderDash
    // Anzeige
    public int zoom = 14;
    public int speed = 1;                // Ticks pro Frame
    public boolean paused = false;

    private long lastLoaded = 0;

    /** Liest config.properties neu ein, wenn die Datei sich geaendert hat. */
    public void hotReload(String path) {
        try {
            long mtime = java.nio.file.Files.getLastModifiedTime(java.nio.file.Paths.get(path)).toMillis();
            if (mtime == lastLoaded) return;
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(path)) { p.load(in); }
            width    = get(p, "width", width);
            height   = get(p, "height", height);
            seed     = get(p, "seed", seed);
            startUnits = get(p, "startUnits", startUnits);
            maxAge   = get(p, "maxAge", maxAge);
            metabolism      = Double.parseDouble(p.getProperty("metabolism", String.valueOf(metabolism)));
            foodEnergy      = Double.parseDouble(p.getProperty("foodEnergy", String.valueOf(foodEnergy)));
            foodRegrow      = Double.parseDouble(p.getProperty("foodRegrow", String.valueOf(foodRegrow)));
            startEnergy     = Double.parseDouble(p.getProperty("startEnergy", String.valueOf(startEnergy)));
            reproduceEnergy = Double.parseDouble(p.getProperty("reproduceEnergy", String.valueOf(reproduceEnergy)));
            mutationRate    = Double.parseDouble(p.getProperty("mutationRate", String.valueOf(mutationRate)));
            gravity = Boolean.parseBoolean(p.getProperty("gravity", String.valueOf(gravity)));
            gravityAccel    = Double.parseDouble(p.getProperty("gravityAccel", String.valueOf(gravityAccel)));
            scentDecay      = Double.parseDouble(p.getProperty("scentDecay", String.valueOf(scentDecay)));
            wildness        = Double.parseDouble(p.getProperty("wildness", String.valueOf(wildness)));
            autoBalance = Boolean.parseBoolean(p.getProperty("autoBalance", String.valueOf(autoBalance)));
            mode    = get(p, "mode", mode);
            lastLoaded = mtime;
            System.out.println("[config] reload ok: w=" + width + " h=" + height + " mode=" + mode
                    + " mutation=" + mutationRate + " gravity=" + gravity);
        } catch (IOException e) { /* Datei noch nicht da: ignorieren */ }
    }

    private static int get(Properties p, String k, int d) {
        return Integer.parseInt(p.getProperty(k, String.valueOf(d)));
    }
    private static long get(Properties p, String k, long d) {
        return Long.parseLong(p.getProperty(k, String.valueOf(d)));
    }
}
