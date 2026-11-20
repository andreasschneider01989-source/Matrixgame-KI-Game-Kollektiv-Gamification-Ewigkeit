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
 * Zeichnet die Welt auf ein JPanel - Viewer + Maus-Editor auf denselben Daten.
 * Links=Futter, Rechts=loeschen, Shift+Links=Wand, Strg+Links=Erde,
 * Mitte=Unit spawnen, Rad=Zoom.
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
                if (e.getButton() == MouseEvent.BUTTON2) {
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
        boolean left  = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
        boolean right = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;
        if (e.isShiftDown()) world.set(x, y, Tile.WALL);
        else if (e.isControlDown()) world.set(x, y, Tile.DIRT);
        else if (left) {
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
