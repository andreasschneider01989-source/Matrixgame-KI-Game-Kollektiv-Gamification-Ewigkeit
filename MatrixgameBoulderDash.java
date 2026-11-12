package matrixgame.boulderdash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Matrixgame Boulder Dash – 2D Grid MVP
 *
 * Controls:
 *   Arrow keys / WASD = move
 *   SPACE             = wait one turn
 *   R                 = restart
 *   P                 = pause/resume
 *
 * The playable world always uses grid coordinates.
 * The Analysis tab can project entities onto selectable data axes.
 * This keeps game topology separate from analytical visualization.
 *
 * Java: 17+
 */
public final class MatrixgameBoulderDash extends JFrame {
    private final GameModel model = new GameModel(28, 18, 42L);
    private final GridPanel gridPanel = new GridPanel(model);
    private final ProjectionPanel projectionPanel = new ProjectionPanel(model);
    private final javax.swing.Timer timer;
    private boolean paused = false;

    private final JComboBox<Axis> xAxis = new JComboBox<>(Axis.values());
    private final JComboBox<Axis> yAxis = new JComboBox<>(Axis.values());

    public MatrixgameBoulderDash() {
        super("Matrixgame – Boulder Dash Grid MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Spielwelt", gridPanel);
        tabs.addTab("Variablen-Canvas", projectionPanel);
        add(tabs, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Canvas X:"));
        xAxis.setSelectedItem(Axis.GRID_X);
        controls.add(xAxis);
        controls.add(new JLabel("Canvas Y:"));
        yAxis.setSelectedItem(Axis.GRID_Y);
        controls.add(yAxis);

        JButton applyAxes = new JButton("Achsen anwenden");
        applyAxes.addActionListener(e -> {
            Axis x = (Axis) xAxis.getSelectedItem();
            Axis y = (Axis) yAxis.getSelectedItem();
            if (x == y) {
                JOptionPane.showMessageDialog(this,
                        "Bitte für X und Y unterschiedliche Variablen wählen.",
                        "Achsenwahl", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            projectionPanel.setAxes(x, y);
            tabs.setSelectedComponent(projectionPanel);
        });
        controls.add(applyAxes);

        JButton restart = new JButton("Neustart (R)");
        restart.addActionListener(e -> restartGame());
        controls.add(restart);

        JButton pause = new JButton("Pause (P)");
        pause.addActionListener(e -> togglePause());
        controls.add(pause);
        add(controls, BorderLayout.NORTH);

        JLabel help = new JLabel(
                "  Pfeile/WASD: bewegen | Leertaste: warten | Felsen horizontal schieben | "
                + "R: Neustart | P: Pause  —  Canvas-Achsen wirken nur auf die Analyseansicht");
        help.setBorder(BorderFactory.createEmptyBorder(5, 8, 7, 8));
        add(help, BorderLayout.SOUTH);

        bindKeys();

        timer = new javax.swing.Timer(130, e -> {
            if (!paused && !model.isGameOver()) {
                model.tickEnvironment();
                refreshViews();
            }
        });
        timer.start();

        pack();
        setMinimumSize(new Dimension(850, 650));
        setLocationRelativeTo(null);
    }

    private void bindKeys() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        bind(im, am, "UP", "moveUp", () -> movePlayerInput(0, -1));
        bind(im, am, "W", "moveUpW", () -> movePlayerInput(0, -1));
        bind(im, am, "DOWN", "moveDown", () -> movePlayerInput(0, 1));
        bind(im, am, "S", "moveDownS", () -> movePlayerInput(0, 1));
        bind(im, am, "LEFT", "moveLeft", () -> movePlayerInput(-1, 0));
        bind(im, am, "A", "moveLeftA", () -> movePlayerInput(-1, 0));
        bind(im, am, "RIGHT", "moveRight", () -> movePlayerInput(1, 0));
        bind(im, am, "D", "moveRightD", () -> movePlayerInput(1, 0));
        bind(im, am, "SPACE", "waitTurn", () -> {
            if (!paused && !model.isGameOver()) {
                model.playerWait();
                model.tickEnvironment();
                refreshViews();
            }
        });
        bind(im, am, "R", "restart", this::restartGame);
        bind(im, am, "P", "pause", this::togglePause);
    }

    private void bind(InputMap im, ActionMap am, String key, String name, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), name);
        am.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    private void movePlayerInput(int dx, int dy) {
        if (paused || model.isGameOver()) return;
        model.movePlayer(dx, dy);
        model.tickEnvironment();
        refreshViews();
    }

    private void restartGame() {
        model.reset();
        paused = false;
        refreshViews();
    }

    private void togglePause() {
        paused = !paused;
        refreshViews();
    }

    private void refreshViews() {
        gridPanel.setPaused(paused);
        gridPanel.repaint();
        projectionPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatrixgameBoulderDash().setVisible(true));
    }

    enum Tile { EMPTY, DIRT, ROCK, GEM, EXIT }

    enum EntityType { PLAYER, ROCK, GEM, EXIT }

    enum Axis {
        GRID_X("Grid X"), GRID_Y("Grid Y"),
        ENERGY("Energie"), MOTIVATION("Motivation"),
        COOPERATION("Kooperation"), STRESS("Stress"),
        HEALTH("Gesundheit"), SCORE("Punktzahl"), AGE("Alter");

        final String label;
        Axis(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    static final class Entity {
        final String id;
        final EntityType type;
        double x, y;
        double energy, motivation, cooperation, stress, health, score, age;

        Entity(String id, EntityType type, double x, double y) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.energy = 0.8;
            this.motivation = 0.7;
            this.cooperation = 0.5;
            this.stress = 0.1;
            this.health = 1.0;
        }

        double value(Axis axis) {
            return switch (axis) {
                case GRID_X -> x;
                case GRID_Y -> y;
                case ENERGY -> energy;
                case MOTIVATION -> motivation;
                case COOPERATION -> cooperation;
                case STRESS -> stress;
                case HEALTH -> health;
                case SCORE -> score;
                case AGE -> age;
            };
        }
    }

    static final class GameModel {
        final int width, height;
        final long seed;
        final Random random;
        Tile[][] map;
        final List<Entity> entities = new ArrayList<>();
        Entity player;
        int gemsCollected = 0;
        int gemsRequired = 5;
        int score = 0;
        int ticks = 0;
        boolean gameOver = false;
        String message = "Sammle mindestens 5 Edelsteine und erreiche den Ausgang.";

        GameModel(int width, int height, long seed) {
            this.width = width;
            this.height = height;
            this.seed = seed;
            this.random = new Random(seed);
            reset();
        }

        void reset() {
            random.setSeed(seed);
            map = new Tile[height][width];
            entities.clear();
            gemsCollected = 0;
            score = 0;
            ticks = 0;
            gameOver = false;
            message = "Sammle mindestens 5 Edelsteine und erreiche den Ausgang.";

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        map[y][x] = Tile.ROCK;
                    } else {
                        map[y][x] = random.nextDouble() < 0.70 ? Tile.DIRT : Tile.EMPTY;
                    }
                }
            }

            player = new Entity("PLAYER", EntityType.PLAYER, 1, 1);
            player.energy = 1.0;
            player.motivation = 0.85;
            player.cooperation = 0.65;
            player.stress = 0.05;
            entities.add(player);
            map[1][1] = Tile.EMPTY;

            place(Tile.EXIT, width - 2, height - 2);
            for (int i = 0; i < 34; i++) place(Tile.ROCK, randomInteriorX(), randomInteriorY());
            for (int i = 0; i < 13; i++) place(Tile.GEM, randomInteriorX(), randomInteriorY());

            // Keep a small starting corridor open.
            for (int x = 1; x <= 4; x++) map[1][x] = Tile.EMPTY;
            for (int y = 1; y <= 3; y++) map[y][1] = Tile.EMPTY;
            rebuildEntities();
        }

        int randomInteriorX() { return 2 + random.nextInt(width - 4); }
        int randomInteriorY() { return 2 + random.nextInt(height - 4); }

        void place(Tile tile, int x, int y) {
            if (map[y][x] == Tile.EMPTY || map[y][x] == Tile.DIRT) map[y][x] = tile;
        }

        void rebuildEntities() {
            entities.removeIf(e -> e.type != EntityType.PLAYER);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] == Tile.ROCK)
                        entities.add(new Entity("ROCK-" + x + "-" + y, EntityType.ROCK, x, y));
                    else if (map[y][x] == Tile.GEM)
                        entities.add(new Entity("GEM-" + x + "-" + y, EntityType.GEM, x, y));
                    else if (map[y][x] == Tile.EXIT)
                        entities.add(new Entity("EXIT", EntityType.EXIT, x, y));
                }
            }
        }

        boolean inside(int x, int y) {
            return x >= 0 && y >= 0 && x < width && y < height;
        }

        void movePlayer(int dx, int dy) {
            if (gameOver) return;
            int nx = (int) player.x + dx;
            int ny = (int) player.y + dy;
            if (!inside(nx, ny)) return;

            Tile target = map[ny][nx];
            if (target == Tile.ROCK) {
                // Classic-style push: only horizontal, only into an empty cell.
                if (dy == 0) {
                    int beyondX = nx + dx;
                    if (inside(beyondX, ny) && map[ny][beyondX] == Tile.EMPTY) {
                        map[ny][beyondX] = Tile.ROCK;
                        map[ny][nx] = Tile.EMPTY;
                    } else return;
                } else return;
            } else if (target == Tile.DIRT) {
                map[ny][nx] = Tile.EMPTY;
                player.energy = clamp(player.energy - 0.004);
                player.score += 0.1;
            } else if (target == Tile.GEM) {
                map[ny][nx] = Tile.EMPTY;
                gemsCollected++;
                score += 10;
                player.score = score;
                player.motivation = clamp(player.motivation + 0.04);
            } else if (target == Tile.EXIT) {
                if (gemsCollected >= gemsRequired) {
                    gameOver = true;
                    message = "QUEST ERFOLGREICH! Ausgang erreicht.";
                } else {
                    message = "Ausgang verschlossen: " + gemsCollected + "/" + gemsRequired + " Edelsteine.";
                    return;
                }
            }

            player.x = nx;
            player.y = ny;
            player.age++;
            player.energy = clamp(player.energy - 0.002);
            player.stress = clamp(player.stress + 0.001);
            if (player.energy <= 0.02) {
                gameOver = true;
                message = "Energie erschöpft. Drücke R für einen Neustart.";
            }
            rebuildEntities();
        }

        void playerWait() {
            player.age++;
            player.energy = clamp(player.energy + 0.01);
            player.stress = clamp(player.stress - 0.01);
        }

        void tickEnvironment() {
            if (gameOver) return;
            ticks++;
            // Rocks fall one cell when the cell below is empty.
            for (int y = height - 2; y >= 1; y--) {
                for (int x = 1; x < width - 1; x++) {
                    if (map[y][x] != Tile.ROCK) continue;
                    if (map[y + 1][x] == Tile.EMPTY) {
                        if ((int) player.x == x && (int) player.y == y + 1) {
                            gameOver = true;
                            message = "Ein Felsen hat dich getroffen. Drücke R für Neustart.";
                        } else {
                            map[y + 1][x] = Tile.ROCK;
                            map[y][x] = Tile.EMPTY;
                        }
                    }
                }
            }
            player.age++;
            rebuildEntities();
        }

        boolean isGameOver() { return gameOver; }
        static double clamp(double v) { return Math.max(0.0, Math.min(1.0, v)); }
    }

    static final class GridPanel extends JPanel {
        final GameModel model;
        boolean paused;
        GridPanel(GameModel model) {
            this.model = model;
            setPreferredSize(new Dimension(840, 540));
            setBackground(new Color(12, 16, 22));
        }
        void setPaused(boolean paused) { this.paused = paused; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cell = Math.min((getWidth() - 30) / model.width, (getHeight() - 90) / model.height);
            int ox = (getWidth() - cell * model.width) / 2;
            int oy = 52 + (getHeight() - 70 - cell * model.height) / 2;

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.drawString("MATRIXGAME BOULDER DASH  |  Gems: " + model.gemsCollected
                    + "/" + model.gemsRequired + "  |  Score: " + model.score
                    + "  |  Tick: " + model.ticks + (paused ? "  |  PAUSE" : ""), 14, 22);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(190, 200, 215));
            g2.drawString(model.message, 14, 42);

            for (int y = 0; y < model.height; y++) {
                for (int x = 0; x < model.width; x++) {
                    int px = ox + x * cell, py = oy + y * cell;
                    Tile tile = model.map[y][x];
                    switch (tile) {
                        case EMPTY -> g2.setColor(new Color(22, 29, 39));
                        case DIRT -> g2.setColor(new Color(126, 78, 43));
                        case ROCK -> g2.setColor(new Color(115, 126, 139));
                        case GEM -> g2.setColor(new Color(30, 170, 190));
                        case EXIT -> g2.setColor(new Color(65, 145, 80));
                    }
                    g2.fillRect(px + 1, py + 1, cell - 2, cell - 2);
                    if (tile == Tile.DIRT) {
                        g2.setColor(new Color(160, 110, 68));
                        g2.drawLine(px + 4, py + cell / 2, px + cell - 4, py + cell / 2);
                    } else if (tile == Tile.ROCK) {
                        g2.setColor(new Color(190, 198, 208));
                        g2.drawOval(px + cell / 5, py + cell / 5, cell * 3 / 5, cell * 3 / 5);
                    } else if (tile == Tile.GEM) {
                        g2.setColor(new Color(180, 255, 255));
                        int[] xs = {px + cell / 2, px + cell * 3 / 4, px + cell / 2, px + cell / 4};
                        int[] ys = {py + cell / 6, py + cell / 2, py + cell * 5 / 6, py + cell / 2};
                        g2.fillPolygon(xs, ys, 4);
                    } else if (tile == Tile.EXIT) {
                        g2.setColor(Color.WHITE);
                        g2.drawRect(px + cell / 4, py + cell / 5, cell / 2, cell * 3 / 5);
                    }
                }
            }

            int px = ox + (int) model.player.x * cell;
            int py = oy + (int) model.player.y * cell;
            g2.setColor(new Color(255, 210, 65));
            g2.fillOval(px + cell / 6, py + cell / 6, cell * 2 / 3, cell * 2 / 3);
            g2.setColor(Color.BLACK);
            g2.drawOval(px + cell / 6, py + cell / 6, cell * 2 / 3, cell * 2 / 3);

            g2.dispose();
        }
    }

    static final class ProjectionPanel extends JPanel {
        final GameModel model;
        Axis xAxis = Axis.GRID_X;
        Axis yAxis = Axis.GRID_Y;

        ProjectionPanel(GameModel model) {
            this.model = model;
            setPreferredSize(new Dimension(840, 540));
            setBackground(new Color(12, 16, 22));
        }

        void setAxes(Axis x, Axis y) {
            xAxis = x;
            yAxis = y;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 75, right = getWidth() - 35, top = 65, bottom = getHeight() - 65;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.drawString("VARIABLE PROJECTION  |  X: " + xAxis + "  Y: " + yAxis, 18, 25);
            g2.setColor(new Color(100, 115, 135));
            g2.drawLine(left, bottom, right, bottom);
            g2.drawLine(left, top, left, bottom);

            List<Entity> data = new ArrayList<>(model.entities);
            data.add(model.player);
            double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
            for (Entity e : data) {
                minX = Math.min(minX, e.value(xAxis)); maxX = Math.max(maxX, e.value(xAxis));
                minY = Math.min(minY, e.value(yAxis)); maxY = Math.max(maxY, e.value(yAxis));
            }
            if (maxX - minX < 1e-9) maxX = minX + 1;
            if (maxY - minY < 1e-9) maxY = minY + 1;

            for (Entity e : data) {
                double vx = e.value(xAxis), vy = e.value(yAxis);
                int sx = (int) (left + 8 + (vx - minX) / (maxX - minX) * (right - left - 16));
                int sy = (int) (bottom - 8 - (vy - minY) / (maxY - minY) * (bottom - top - 16));
                g2.setColor(switch (e.type) {
                    case PLAYER -> new Color(255, 210, 65);
                    case ROCK -> new Color(155, 165, 180);
                    case GEM -> new Color(40, 205, 220);
                    case EXIT -> new Color(90, 190, 110);
                });
                int r = e.type == EntityType.PLAYER ? 9 : 5;
                g2.fillOval(sx - r, sy - r, r * 2, r * 2);
            }
            g2.setColor(new Color(190, 200, 215));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(xAxis.toString() + "  [" + String.format(Locale.ROOT, "%.2f", minX)
                    + " … " + String.format(Locale.ROOT, "%.2f", maxX) + "]", left, getHeight() - 25);
            g2.rotate(-Math.PI / 2);
            g2.drawString(yAxis.toString(), -getHeight() + 18, 22);
            g2.dispose();
        }
    }
}
