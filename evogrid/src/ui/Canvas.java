package ui;

import sim.Tile;
import sim.Unit;
import sim.World;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Zeichnet die Welt auf ein JPanel - reiner Viewer + Maus-Editor auf denselben Daten.
 *
 * Maus:
 *   Linke Taste / ziehen        -> Futter setzen
 *   Rechte Taste / ziehen       -> Zelle leeren (loeschen)
 *   Umschalt + Linke Taste      -> Wand setzen
 *   Strg + Linke Taste          -> Erde/Dirt setzen (grabbbar, Boulder-Dash)
 *   Mittelklick                 -> Unit mit zufaelligem Gehirn spawen
 *   Mausrad                     -> Zoom (6..60)
 */
public final class Canvas extends JPanel {
    private final World world;
    private final editor.Config cfg;
    private boolean painting = false;

    public Canvas(World world, editor.Config cfg) {
        this.world = world;
        this.cfg = cfg;
        setPreferredSize(new Dimension(cfg.width * cfg.zoom, cfg.height * cfg.zoom));
        setBackground(new Color(0x0B0B10));

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { painting = true; paintAt(e); }
            @Override public void mouseReleased(MouseEvent e) { painting = false; }
            @Override public void mouseDragged(MouseEvent e) { if (painting) paintAt(e); }

            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                cfg.zoom = Math.max(6, Math.min(60, cfg.zoom - e.getWheelRotation() * 2));
                applyZoom();
            }

            @Override public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {           // Mittelklick: neue Unit
                    int x = cellX(e), y = cellY(e);
                    if (world.inBounds(x, y) && world.get(x, y) == Tile.EMPTY && world.unitAt(x, y) == null) {
                        world.units.add(new sim.Unit(x, y, 0, sim.World.teamColor(0),
                                new sim.Brain(world.rng), world.nextGen++));
                    }
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private int cellX(MouseEvent e) { return e.getX() / cfg.zoom; }
    private int cellY(MouseEvent e) { return e.getY() / cfg.zoom; }

    private void paintAt(MouseEvent e) {
        int x = cellX(e), y = cellY(e);
        if (!world.inBounds(x, y)) return;
        if (e.isShiftDown()) world.set(x, y, Tile.WALL);            // Wand
        else if (e.isControlDown()) world.set(x, y, Tile.DIRT);     // Erde
        boolean left  = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
        boolean right = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;
        if (left && !e.isShiftDown() && !e.isControlDown()) {
            if (world.get(x, y) == Tile.EMPTY) world.set(x, y, Tile.FOOD);
        } else if (right) {
            world.set(x, y, Tile.EMPTY);
        }
        repaint();
    }

    private void applyZoom() {
        setPreferredSize(new Dimension(cfg.width * cfg.zoom, cfg.height * cfg.zoom));
        revalidate();
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (w != null) w.pack();
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int z = cfg.zoom;
        for (int y = 0; y < cfg.height; y++)
            for (int x = 0; x < cfg.width; x++) {
                Tile t = world.get(x, y);
                if (t != Tile.EMPTY) {
                    g2.setColor(new Color(t.rgb));
                    g2.fillRect(x * z, y * z, z, z);
                }
            }
        for (Unit u : world.units) {
            // Pheromon-Schleier hinter der Unit zeichnen (Kommunikation sichtbar)
            double sc = world.scent[u.y * world.cfg.width + u.x];
            if (sc > 0.05) {
                g2.setColor(new Color(0, 1f, 1f, Math.min(0.35f, (float) (sc / 3.0))));
                g2.fillRect(u.x * z, u.y * z, z, z);
            }
            g2.setColor(new Color(u.displayColor()));
            int pad = Math.max(1, z / 6);
            g2.fillOval(u.x * z + pad, u.y * z + pad, z - 2 * pad, z - 2 * pad);
        }
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRect(0, 0, getWidth(), 22);
        g2.setColor(Color.GREEN);
        g2.drawString(world.stats() + (cfg.paused ? "   [PAUSE]" : "   speed=" + cfg.speed), 8, 15);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Maus: links=Futter  rechts=loeschen  Shift+links=Wand  Strg+links=Erde  Mitte=Unit  Rad=Zoom", 8, getHeight() - 6);
    }
}
