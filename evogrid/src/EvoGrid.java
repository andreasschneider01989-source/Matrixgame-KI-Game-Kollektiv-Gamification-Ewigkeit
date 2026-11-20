import editor.Config;
import sim.Rng;
import sim.Tile;
import sim.Unit;
import sim.World;
import ui.Canvas;
import ui.Window;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * EvoGrid - Einstiegspunkt.
 *   GUI:      java -cp out EvoGrid
 *   Headless: java -cp out EvoGrid --headless --ticks 5000 --snapshots 50 --out snapshots
 */
public final class EvoGrid {
    public static void main(String[] args) throws Exception {
        Config cfg = new Config();
        boolean headless = false;
        int ticks = 3000, snapshots = 30;
        String outDir = "snapshots";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--headless" -> headless = true;
                case "--ticks" -> ticks = Integer.parseInt(args[++i]);
                case "--snapshots" -> snapshots = Integer.parseInt(args[++i]);
                case "--out" -> outDir = args[++i];
                case "--mode" -> cfg.mode = Integer.parseInt(args[++i]);
                case "--seed" -> cfg.seed = Long.parseLong(args[++i]);
            }
        }

        World world = new World(cfg);
        world.build();

        if (headless) { runHeadless(cfg, world, ticks, snapshots, outDir); return; }

        Canvas canvas = new Canvas(world, cfg);
        new Window(cfg, world, canvas).setVisible(true);
        Timer timer = new Timer(33, e -> {
            cfg.hotReload("config.properties");
            if (!cfg.paused) for (int i = 0; i < cfg.speed; i++) world.tick();
            canvas.repaint();
        });
        timer.start();
    }

    private static void runHeadless(Config cfg, World world, int ticks, int snapshots, String outDir) throws Exception {
        new File(outDir).mkdirs();
        long start = System.currentTimeMillis();
        int snapEvery = Math.max(1, ticks / snapshots);
        long maxGen = 0;
        for (int t = 1; t <= ticks; t++) {
            world.tick();
            for (Unit u : world.units) maxGen = Math.max(maxGen, u.generation);
            if (t % snapEvery == 0 || t == ticks) {
                savePng(world, cfg, outDir + "/frame_" + String.format("%05d", t) + ".png");
            }
            if (t % 250 == 0) System.out.println(world.stats());
        }
        Unit best = world.bestAlive();
        System.out.println("");
        System.out.println("=== ERGEBNIS ===");
        System.out.println(world.stats());
        if (best != null)
            System.out.printf("Beste Unit: gen=%d fitness=%.1f food=%d kills=%d kids=%d age=%d%n",
                    best.generation, best.fitness(), best.foodEaten, best.kills, best.children, best.age);
        System.out.println("Dauer: " + (System.currentTimeMillis() - start) + " ms  | PNGs in " + outDir + "/");
    }

    private static void savePng(World world, Config cfg, String path) throws Exception {
        BufferedImage img = new BufferedImage(cfg.width * 8, cfg.height * 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x0B0B10)); g.fillRect(0, 0, img.getWidth(), img.getHeight());
        int z = 8;
        for (int y = 0; y < cfg.height; y++)
            for (int x = 0; x < cfg.width; x++) {
                Tile t = world.get(x, y);
                if (t != Tile.EMPTY) { g.setColor(new Color(t.rgb)); g.fillRect(x * z, y * z, z, z); }
            }
        for (Unit u : world.units) {
            g.setColor(new Color(u.displayColor()));
            g.fillOval(u.x * z + 1, u.y * z + 1, z - 2, z - 2);
        }
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}
