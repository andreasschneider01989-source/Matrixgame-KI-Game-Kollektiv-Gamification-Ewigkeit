package ui;

import editor.Config;
import sim.World;

import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Fenster + Laufzeit-Editor-Steuerung.
 * Tasten:
 *  Leertaste = Pause | +/- = speed | re/links Pfeil = Mode wechseln | r = Reset
 *  f = Futter regnen | m = alle mutieren staerker | b = Boulder setzen | s = config speichern
 */
public final class Window extends JFrame {
    public Window(Config cfg, World world, Canvas canvas) {
        super("EvoGrid - evolutionaere KI-Simulation (Swing, rein java)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(canvas);
        pack();
        setLocationRelativeTo(null);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> { cfg.paused = !cfg.paused; System.out.println("paused=" + cfg.paused); }
                    case KeyEvent.VK_PLUS, KeyEvent.VK_ADD -> cfg.speed = Math.min(64, cfg.speed * 2);
                    case KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT -> cfg.speed = Math.max(1, cfg.speed / 2);
                    case KeyEvent.VK_RIGHT -> { cfg.mode = (cfg.mode + 1) % 2; reset(cfg, world); }
                    case KeyEvent.VK_LEFT -> { cfg.mode = (cfg.mode + 1) % 2; reset(cfg, world); }
                    case KeyEvent.VK_R -> reset(cfg, world);
                    case KeyEvent.VK_F -> rainFood(world);
                    case KeyEvent.VK_M -> world.units.forEach(u ->
                            u.brain.mutate(world.rng, cfg.mutationRate * 5));
                    case KeyEvent.VK_G -> { cfg.gravity = !cfg.gravity; System.out.println("gravity=" + cfg.gravity); }
                    case KeyEvent.VK_PERIOD -> world.tick();             // Einzelschritt im Pausenmodus
                    case KeyEvent.VK_S -> saveConfig(cfg);
                }
                if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') cfg.zoom = (e.getKeyChar() - '0') * 6 + 6;
                canvas.setPreferredSize(new java.awt.Dimension(cfg.width * cfg.zoom, cfg.height * cfg.zoom));
                Window.this.pack();
            }
        });
        setFocusable(true);
    }

    private void reset(Config cfg, World world) {
        world.tick = 0;
        world.units.clear();
        world.build();
        System.out.println("world reset, mode=" + cfg.mode);
    }

    private void rainFood(World world) {
        for (int i = 0; i < 200; i++) {
            int x = 1 + world.rng.nextInt(world.cfg.width - 2);
            int y = 1 + world.rng.nextInt(world.cfg.height - 2);
            if (world.get(x, y) == sim.Tile.EMPTY) world.set(x, y, sim.Tile.FOOD);
        }
    }

    private void saveConfig(Config cfg) {
        try (java.io.PrintWriter w = new java.io.PrintWriter("config.properties")) {
            w.println("width=" + cfg.width);        w.println("height=" + cfg.height);
            w.println("seed=" + cfg.seed);          w.println("startUnits=" + cfg.startUnits);
            w.println("maxAge=" + cfg.maxAge);      w.println("metabolism=" + cfg.metabolism);
            w.println("foodEnergy=" + cfg.foodEnergy); w.println("foodRegrow=" + cfg.foodRegrow);
            w.println("startEnergy=" + cfg.startEnergy);
            w.println("reproduceEnergy=" + cfg.reproduceEnergy);
            w.println("mutationRate=" + cfg.mutationRate);
            w.println("gravity=" + cfg.gravity);    w.println("mode=" + cfg.mode);
            System.out.println("config.properties gespeichert (aendern + speichern = Hot-Reload!)");
        } catch (IOException ex) { System.out.println("Fehler beim Speichern: " + ex); }
    }
}
