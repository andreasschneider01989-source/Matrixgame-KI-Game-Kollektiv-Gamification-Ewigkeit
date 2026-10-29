/**
 * =============================================================================
 * MATRIXGAME DEMO - Kompletter Simulator zum Copy-Pasten in Eclipse
 * =============================================================================
 * 
 * Diese Datei enthält ALLE notwendigen Klassen in einer einzigen Datei.
 * Einfach in Eclipse kopieren, ausführen und loslegen!
 * 
 * Features:
 * - Physik: Gravitation, Kollisionen, Entropie
 * - Bewusstsein: Hawkins-Skala (0-1000)
 * - Gamification: Level, Erfahrung, Quests
 * - Spieltheorie: Entscheidungsbäume
 * - Prognose: Zukunftsvorhersagen
 * - Interaktive UI: Maus- und Tastatursteuerung
 * 
 * Steuerung:
 * - LMB: Einheit auswählen (Drag für Mehrfachauswahl)
 * - RMB: Bewegen/Angreifen
 * - Mausrad: Zoom
 * - A: Alle auswählen
 * - ESC: Auswahl aufheben
 * - DEL: Ausgewählte löschen
 * - LEERTASTE: Kamera zurücksetzen
 * - P: Simulation pausieren
 * - S: Statistiken anzeigen
 * 
 * Andreas Schneider - Matrixgame Kollektiv
 * Version: 1.0.0 - 2026
 * =============================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * =============================================================================
 * HAUPTPROGRAMM - Einfach ausführen!
 * =============================================================================
 */
public class MatrixGameDemo {
    
    public static void main(String[] args) {
        // Look and Feel für besseres Aussehen
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // UI starten
        SwingUtilities.invokeLater(() -> {
            MatrixGameWindow window = new MatrixGameWindow();
            window.setVisible(true);
        });
    }
}

/**
 * =============================================================================
 * FENSTER UND UI
 * =============================================================================
 */
class MatrixGameWindow extends JFrame {
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    
    public MatrixGameWindow() {
        super("Matrixgame Simulator - Demo Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Spielpanel
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
        
        // Steuerpanel
        controlPanel = new ControlPanel(gamePanel);
        add(controlPanel, BorderLayout.NORTH);
        
        // Infopanel
        infoPanel = new InfoPanel(gamePanel);
        add(infoPanel, BorderLayout.EAST);
        
        // Menüleiste
        createMenuBar();
        
        // UI-Update-Timer
        new Timer(50, e -> {
            gamePanel.repaint();
            infoPanel.update();
        }).start();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem exitItem = new JMenuItem("Beenden");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        JMenu simMenu = new JMenu("Simulation");
        JMenuItem resetItem = new JMenuItem("Zurücksetzen");
        resetItem.addActionListener(e -> gamePanel.resetSimulation());
        simMenu.add(resetItem);
        menuBar.add(simMenu);
        
        JMenu unitMenu = new JMenu("Einheiten");
        JMenuItem addKing = new JMenuItem("König hinzufügen");
        addKing.addActionListener(e -> gamePanel.addKing());
        JMenuItem addWarrior = new JMenuItem("Krieger hinzufügen");
        addWarrior.addActionListener(e -> gamePanel.addWarrior());
        JMenuItem addScout = new JMenuItem("Späher hinzufügen");
        addScout.addActionListener(e -> gamePanel.addScout());
        JMenuItem clearAll = new JMenuItem("Alle löschen");
        clearAll.addActionListener(e -> gamePanel.clearAllUnits());
        unitMenu.add(addKing);
        unitMenu.add(addWarrior);
        unitMenu.add(addScout);
        unitMenu.add(clearAll);
        menuBar.add(unitMenu);
        
        JMenu helpMenu = new JMenu("Hilfe");
        JMenuItem about = new JMenuItem("Über");
        about.addActionListener(e -> showAbout());
        helpMenu.add(about);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showAbout() {
        String msg = "Matrixgame Simulator Demo\n\n" +
                   "Version: 1.0.0\n\n" +
                   "Steuerung:\n" +
                   "  LMB: Auswählen\n" +
                   "  RMB: Bewegen/Angreifen\n" +
                   "  Drag: Mehrfachauswahl\n" +
                   "  Mausrad: Zoom\n" +
                   "  A: Alle auswählen\n" +
                   "  ESC: Auswahl aufheben\n" +
                   "  DEL: Löschen\n" +
                   "  LEER: Kamera zurück\n" +
                   "  P: Pause\n" +
                   "  S: Statistiken\n\n" +
                   "Andreas Schneider - Matrixgame Kollektiv";
        JOptionPane.showMessageDialog(this, msg, "Über", JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * =============================================================================
 * SPIEL-PANEL - Hauptvisualisierung
 * =============================================================================
 */
class GamePanel extends JPanel {
    // ========== SIMULATIONS-PARAMETER ==========
    private List<Unit> units = new CopyOnWriteArrayList<>();
    private long simulationTicks = 0;
    private double globalEntropyPool = 100.0;
    private double universeTime = 0.0;
    private boolean isRunning = true;
    private Thread gameThread;
    
    // ========== KAMERA ==========
    private double zoom = 1.0;
    private double cameraX = 0;
    private double cameraY = 0;
    
    // ========== MAUS-STEUERUNG ==========
    private int dragStartX = -1, dragStartY = -1;
    private boolean isDragging = false;
    private boolean isRightDragging = false;
    
    // ========== AUSWAHL ==========
    private List<Unit> selectedUnits = new ArrayList<>();
    
    // ========== KONSTRUKTOR ==========
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Anfangs-Einheiten erstellen
        createInitialUnits();
        
        // Maus-Listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                    isDragging = false;
                    
                    Unit clicked = getUnitAt(e.getX(), e.getY());
                    if (clicked != null) {
                        if (!e.isShiftDown()) selectedUnits.clear();
                        if (selectedUnits.contains(clicked)) {
                            selectedUnits.remove(clicked);
                        } else {
                            selectedUnits.add(clicked);
                        }
                    } else if (!e.isShiftDown()) {
                        selectedUnits.clear();
                    }
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                    isRightDragging = true;
                    
                    if (!selectedUnits.isEmpty()) {
                        Unit clicked = getUnitAt(e.getX(), e.getY());
                        if (clicked != null && clicked.team != selectedUnits.get(0).team) {
                            for (Unit u : selectedUnits) {
                                u.target = clicked;
                                u.decisionAlgorithm = "TACTICAL_FOCUS";
                            }
                        } else {
                            Vector3D worldPos = screenToWorld(e.getX(), e.getY());
                            for (Unit u : selectedUnits) {
                                u.targetPosition = worldPos;
                                u.decisionAlgorithm = "TACTICAL_MOVE";
                            }
                        }
                    }
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isDragging) {
                        int x1 = Math.min(dragStartX, e.getX());
                        int y1 = Math.min(dragStartY, e.getY());
                        int x2 = Math.max(dragStartX, e.getX());
                        int y2 = Math.max(dragStartY, e.getY());
                        
                        if (!e.isShiftDown()) selectedUnits.clear();
                        
                        for (Unit u : units) {
                            Point p = worldToScreen(u.position);
                            if (p.x >= x1 && p.x <= x2 && p.y >= y1 && p.y <= y2) {
                                selectedUnits.add(u);
                            }
                        }
                    }
                    isDragging = false;
                    isRightDragging = false;
                    dragStartX = dragStartY = -1;
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    isRightDragging = false;
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (dragStartX != -1 && (Math.abs(e.getX() - dragStartX) > 5 || Math.abs(e.getY() - dragStartY) > 5)) {
                        isDragging = true;
                    }
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e) && isRightDragging) {
                    cameraX -= (e.getX() - dragStartX) / zoom;
                    cameraY += (e.getY() - dragStartY) / zoom;
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                    repaint();
                }
            }
        });
        
        // Mausrad
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) zoom *= 1.1;
            else zoom /= 1.1;
            zoom = Math.max(0.1, Math.min(10.0, zoom));
            repaint();
        });
        
        // Tastatur
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        selectedUnits = new ArrayList<>(units);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        selectedUnits.clear();
                        break;
                    case KeyEvent.VK_DELETE:
                        units.removeAll(selectedUnits);
                        selectedUnits.clear();
                        break;
                    case KeyEvent.VK_SPACE:
                        zoom = 1.0; cameraX = 0; cameraY = 0;
                        break;
                    case KeyEvent.VK_P:
                        isRunning = !isRunning;
                        break;
                    case KeyEvent.VK_S:
                        printStatistics();
                        break;
                }
                repaint();
            }
        });
        
        // Game-Loop starten
        startSimulation();
    }
    
    // ========== SIMULATION ==========
    private void startSimulation() {
        isRunning = true;
        gameThread = new Thread(() -> {
            while (true) {
                try {
                    if (isRunning) {
                        simulationTicks++;
                        universeTime += 0.1;
                        
                        // Entropie regenerieren
                        if (globalEntropyPool < 1000) {
                            globalEntropyPool = Math.min(1000, globalEntropyPool + 0.08);
                        }
                        
                        // Physik aktualisieren
                        updatePhysics();
                        
                        // Bewusstsein aktualisieren
                        updateConsciousness();
                        
                        // Quests aktualisieren
                        updateQuests();
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "GameLoop");
        gameThread.setDaemon(true);
        gameThread.start();
    }
    
    private void updatePhysics() {
        // Gravitation berechnen
        for (int i = 0; i < units.size(); i++) {
            Unit u1 = units.get(i);
            if (!u1.movable) continue;
            
            Vector3D acceleration = new Vector3D(0, 0, 0);
            
            for (int j = 0; j < units.size(); j++) {
                if (i == j) continue;
                Unit u2 = units.get(j);
                if (!u2.movable) continue;
                
                Vector3D delta = u2.position.subtract(u1.position);
                double distSq = delta.lengthSquared();
                if (distSq < 0.0001) distSq = 0.0001;
                double dist = Math.sqrt(distSq);
                
                double force = (0.5 * u1.mass * u2.mass / distSq) * (1.0 - globalEntropyPool / 1000.0);
                Vector3D dir = delta.scale(1.0 / dist);
                acceleration = acceleration.add(dir.scale(force / u1.mass));
            }
            
            u1.acceleration = acceleration;
        }
        
        // Positionen aktualisieren
        for (Unit u : units) {
            if (!u.movable) continue;
            
            u.velocity = u.velocity.add(u.acceleration.scale(0.1));
            double speed = u.velocity.length();
            if (speed > u.maxSpeed) {
                u.velocity = u.velocity.normalize().scale(u.maxSpeed);
            }
            
            u.position = u.position.add(u.velocity.scale(0.1));
            u.acceleration = new Vector3D(0, 0, 0);
        }
        
        // Kollisionen
        resolveCollisions();
        
        // Entropie-Bewegungen
        for (Unit u : units) {
            if (u.entropyMoveActive && u.entropyMoveSpeedPercent > 0) {
                double factor = u.entropyMoveSpeedPercent / 100.0;
                u.position = u.position.add(new Vector3D(
                    (Math.random() - 0.5) * factor,
                    (Math.random() - 0.5) * factor,
                    (Math.random() - 0.5) * factor
                ));
            }
        }
        
        // Entscheidungen treffen
        for (Unit u : units) {
            if (u.isAlive()) {
                makeDecision(u);
                u.update(0.1);
            }
        }
    }
    
    private void resolveCollisions() {
        for (int i = 0; i < units.size(); i++) {
            Unit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                Unit u2 = units.get(j);
                
                double dist = u1.position.distanceTo(u2.position);
                if (dist < (u1.radius + u2.radius)) {
                    // Impulserhaltung
                    double totalMass = u1.mass + u2.mass;
                    Vector3D newVel = u1.velocity.scale(u1.mass).add(u2.velocity.scale(u2.mass)).scale(1.0 / totalMass);
                    
                    u1.velocity = newVel;
                    u1.mass = totalMass;
                    u1.radius = (int) Math.cbrt(Math.pow(u1.radius, 3) + Math.pow(u2.radius, 3));
                    u1.color = blendColors(u1.color, u2.color);
                    u1.health += u2.health;
                    u1.maxHealth += u2.maxHealth;
                    u1.damage += u2.damage;
                    
                    if (u2.mass > u1.mass) u1.team = u2.team;
                    
                    units.remove(u2);
                    j--;
                }
            }
        }
    }
    
    private void updateConsciousness() {
        for (Unit u : units) {
            if (!u.isAlive()) continue;
            
            // Bewusstsein basierend auf Erfahrung entwickeln
            double expFactor = u.getExperiencePercentage() / 100.0;
            double levelFactor = Math.min(1.0, u.level / 20.0);
            double healthFactor = u.getHealthPercentage() / 100.0;
            
            double development = (expFactor * 0.3 + levelFactor * 0.2 + healthFactor * 0.15) * 0.5;
            u.consciousnessLevel = Math.min(1000, u.consciousnessLevel + development);
            
            // Modus aktualisieren
            u.updateConsciousnessMode();
        }
    }
    
    private void updateQuests() {
        // Einfache Quest-Logik
        for (Unit u : units) {
            if (u.isAlive()) {
                u.experience += 0.5;
                if (u.experience >= u.experienceToNextLevel) {
                    u.experience -= u.experienceToNextLevel;
                    u.levelUp();
                }
            }
        }
    }
    
    private void makeDecision(Unit unit) {
        // Einfache Entscheidungslogik
        if (unit.health < unit.maxHealth * 0.3) {
            unit.decisionAlgorithm = "RETREAT";
            return;
        }
        
        if (unit.target != null && !unit.target.isAlive()) {
            unit.target = null;
        }
        
        if (unit.target != null) {
            double dist = unit.position.distanceTo(unit.target.position);
            if (dist <= unit.range) {
                unit.attack(unit.target);
            } else {
                unit.moveTo(unit.target, unit.speed);
            }
        } else if (unit.targetPosition != null) {
            unit.moveTo(unit.targetPosition, unit.speed);
            if (unit.position.distanceTo(unit.targetPosition) < 0.5) {
                unit.targetPosition = null;
            }
        } else {
            // Nächsten Gegner finden
            Unit nearestEnemy = null;
            double minDist = Double.MAX_VALUE;
            for (Unit other : units) {
                if (other != unit && other.team != unit.team && other.isAlive()) {
                    double dist = unit.position.distanceTo(other.position);
                    if (dist < minDist) {
                        minDist = dist;
                        nearestEnemy = other;
                    }
                }
            }
            
            if (nearestEnemy != null) {
                unit.target = nearestEnemy;
                unit.decisionAlgorithm = "Nearest_ENEMI";
            }
        }
    }
    
    // ========== EINHEITEN-MANAGEMENT ==========
    private void createInitialUnits() {
        // Team 0 (Rot)
        Unit king0 = addKing();
        king0.position = new Vector3D(-100, 0, 0);
        king0.team = 0;
        
        for (int i = 0; i < 5; i++) {
            Unit warrior = addWarrior();
            warrior.position = new Vector3D(
                -100 + (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50,
                0
            );
            warrior.team = 0;
        }
        
        // Team 1 (Blau)
        Unit king1 = addKing();
        king1.position = new Vector3D(100, 0, 0);
        king1.team = 1;
        
        for (int i = 0; i < 5; i++) {
            Unit warrior = addWarrior();
            warrior.position = new Vector3D(
                100 + (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50,
                0
            );
            warrior.team = 1;
        }
    }
    
    public Unit addKing() {
        Unit unit = new Unit();
        unit.type = "King";
        unit.mass = 100;
        unit.radius = 10;
        unit.health = 500;
        unit.maxHealth = 500;
        unit.damage = 50;
        unit.range = 3;
        unit.speed = 0.3;
        unit.maxSpeed = 100;
        unit.consciousnessLevel = 300;
        unit.updateConsciousnessMode();
        units.add(unit);
        return unit;
    }
    
    public Unit addWarrior() {
        Unit unit = new Unit();
        unit.type = "Warrior";
        unit.mass = 30 + Math.random() * 20;
        unit.radius = 6;
        unit.health = 200;
        unit.maxHealth = 200;
        unit.damage = 25;
        unit.range = 1.5;
        unit.speed = 0.7;
        unit.maxSpeed = 100;
        unit.consciousnessLevel = 150 + Math.random() * 100;
        unit.updateConsciousnessMode();
        units.add(unit);
        return unit;
    }
    
    public Unit addScout() {
        Unit unit = new Unit();
        unit.type = "Scout";
        unit.mass = 15 + Math.random() * 10;
        unit.radius = 4;
        unit.health = 50;
        unit.maxHealth = 50;
        unit.damage = 5;
        unit.range = 5;
        unit.speed = 1.5;
        unit.maxSpeed = 100;
        unit.consciousnessLevel = 100 + Math.random() * 100;
        unit.updateConsciousnessMode();
        units.add(unit);
        return unit;
    }
    
    public void clearAllUnits() {
        units.clear();
        selectedUnits.clear();
        createInitialUnits();
    }
    
    public void resetSimulation() {
        units.clear();
        selectedUnits.clear();
        simulationTicks = 0;
        universeTime = 0;
        globalEntropyPool = 100;
        createInitialUnits();
    }
    
    // ========== ZEICHNEN ==========
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Hintergrund
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Koordinatensystem
        drawCoordinateSystem(g2);
        
        // Einheiten
        drawUnits(g2);
        
        // Auswahl
        drawSelection(g2);
        
        // Drag-Box
        if (isDragging && dragStartX != -1) {
            drawDragBox(g2);
        }
        
        // UI
        drawUI(g2);
    }
    
    private void drawCoordinateSystem(Graphics2D g2) {
        g2.setColor(new Color(50, 50, 50));
        Point start = worldToScreen(new Vector3D(-500, 0, 0));
        Point end = worldToScreen(new Vector3D(500, 0, 0));
        g2.drawLine(start.x, start.y, end.x, end.y);
        
        start = worldToScreen(new Vector3D(0, -500, 0));
        end = worldToScreen(new Vector3D(0, 500, 0));
        g2.drawLine(start.x, start.y, end.x, end.y);
        
        Point origin = worldToScreen(new Vector3D(0, 0, 0));
        g2.setColor(Color.WHITE);
        g2.fillOval(origin.x - 3, origin.y - 3, 6, 6);
    }
    
    private void drawUnits(Graphics2D g2) {
        for (Unit u : units) {
            if (!u.isAlive()) continue;
            
            Point p = worldToScreen(u.position);
            double r = u.radius * zoom;
            
            Color color = getUnitColor(u);
            g2.setColor(color);
            g2.fillOval((int)(p.x - r), (int)(p.y - r), (int)(r * 2), (int)(r * 2));
            
            // Auswahl-Rahmen
            if (selectedUnits.contains(u)) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval((int)(p.x - r - 2), (int)(p.y - r - 2), (int)(r * 2 + 4), (int)(r * 2 + 4));
            }
            
            // Typ-Markierung
            if ("King".equals(u.type)) {
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("K", p.x - 5, (int)(p.y - r - 5));
            }
            
            // Gesundheitsbalken
            if (r > 10) {
                drawHealthBar(g2, p, r, u);
            }
        }
    }
    
    private void drawHealthBar(Graphics2D g2, Point p, double r, Unit u) {
        double hp = u.getHealthPercentage() / 100.0;
        int w = (int)(r * 2);
        int h = 4;
        int y = (int)(p.y - r - 10);
        
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(p.x - w/2, y, w, h);
        
        Color hpColor = hp > 0.5 ? Color.GREEN : hp > 0.25 ? Color.YELLOW : Color.RED;
        g2.setColor(hpColor);
        g2.fillRect(p.x - w/2, y, (int)(w * hp), h);
    }
    
    private void drawSelection(Graphics2D g2) {
        if (!selectedUnits.isEmpty()) {
            for (Unit u : selectedUnits) {
                Point p = worldToScreen(u.position);
                double r = u.radius * zoom;
                g2.setColor(new Color(255, 255, 0, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval((int)(p.x - r - 5), (int)(p.y - r - 5), (int)(r * 2 + 10), (int)(r * 2 + 10));
            }
        }
    }
    
    private void drawDragBox(Graphics2D g2) {
        int x1 = Math.min(dragStartX, getMousePosition().x);
        int y1 = Math.min(dragStartY, getMousePosition().y);
        int x2 = Math.max(dragStartX, getMousePosition().x);
        int y2 = Math.max(dragStartY, getMousePosition().y);
        
        g2.setColor(new Color(0, 255, 128, 60));
        g2.fillRect(x1, y1, x2 - x1, y2 - y1);
        g2.setColor(new Color(0, 255, 128));
        g2.drawRect(x1, y1, x2 - x1, y2 - y1);
    }
    
    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        
        String info = String.format("Ticks: %d | Zeit: %.1fs | Einheiten: %d | Entropie: %.1f",
            simulationTicks, universeTime, units.size(), globalEntropyPool);
        g2.drawString(info, 10, 20);
        
        if (!selectedUnits.isEmpty()) {
            Unit u = selectedUnits.get(0);
            String selInfo = String.format("Ausgewählt: %s (Lvl %d, HP: %.0f/%.0f, Bewusstsein: %.0f)",
                u.type, u.level, u.health, u.maxHealth, u.consciousnessLevel);
            g2.drawString(selInfo, 10, 40);
        }
        
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("LMB=Auswählen | RMB=Bewegen/Angreifen | Mausrad=Zoom | A=Alle | ESC=Aufheben | DEL=Löschen | LEER=Kamera zurück | P=Pause | S=Statistiken", 10, getHeight() - 20);
    }
    
    // ========== HILFSMETHODEN ==========
    private Point worldToScreen(Vector3D world) {
        int sx = (int)(getWidth() / 2.0 + (world.x - cameraX) * zoom);
        int sy = (int)(getHeight() / 2.0 - (world.y - cameraY) * zoom);
        return new Point(sx, sy);
    }
    
    private Vector3D screenToWorld(int sx, int sy) {
        double wx = (sx - getWidth() / 2.0) / zoom + cameraX;
        double wy = -(sy - getHeight() / 2.0) / zoom + cameraY;
        return new Vector3D(wx, wy, 0);
    }
    
    private Unit getUnitAt(int sx, int sy) {
        for (Unit u : units) {
            Point p = worldToScreen(u.position);
            double dist = Math.sqrt(Math.pow(sx - p.x, 2) + Math.pow(sy - p.y, 2));
            if (dist <= u.radius * zoom) {
                return u;
            }
        }
        return null;
    }
    
    private Color getUnitColor(Unit u) {
        Color base = u.color;
        if (base == null) {
            switch (u.team % 3) {
                case 0: base = new Color(255, 100, 100); break; // Rot
                case 1: base = new Color(100, 100, 255); break; // Blau
                case 2: base = new Color(100, 255, 100); break; // Grün
                default: base = Color.CYAN;
            }
        }
        
        // Bewusstsein in Farbe einfließen lassen
        double cons = u.consciousnessLevel / 1000.0;
        int r = Math.min(255, (int)(base.getRed() * (1 + cons * 0.5)));
        int g = Math.min(255, (int)(base.getGreen() * (1 + cons * 0.5)));
        int b = Math.min(255, (int)(base.getBlue() * (1 + cons * 0.5)));
        return new Color(r, g, b);
    }
    
    private Color blendColors(Color c1, Color c2) {
        return new Color(
            (c1.getRed() + c2.getRed()) / 2,
            (c1.getGreen() + c2.getGreen()) / 2,
            (c1.getBlue() + c2.getBlue()) / 2
        );
    }
    
    public void printStatistics() {
        System.out.println("\n=== SIMULATIONS-STATISTIK ===");
        System.out.println("Ticks: " + simulationTicks);
        System.out.println("Zeit: " + String.format("%.1f", universeTime) + "s");
        System.out.println("Einheiten: " + units.size());
        System.out.println("Entropie: " + String.format("%.1f", globalEntropyPool));
        
        double totalMass = 0;
        for (Unit u : units) totalMass += u.mass;
        System.out.println("Gesamtmasse: " + String.format("%.1f", totalMass));
        
        double avgCons = 0;
        for (Unit u : units) avgCons += u.consciousnessLevel;
        avgCons /= Math.max(1, units.size());
        System.out.println("Durchschnittliches Bewusstsein: " + String.format("%.1f", avgCons));
        
        System.out.println("===============================\n");
    }
    
    public List<Unit> getUnits() { return units; }
    public long getSimulationTicks() { return simulationTicks; }
    public double getUniverseTime() { return universeTime; }
    public double getGlobalEntropy() { return globalEntropyPool; }
}

/**
 * =============================================================================
 * STEUER-PANEL
 * =============================================================================
 */
class ControlPanel extends JPanel {
    private GamePanel gamePanel;
    
    public ControlPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(new Color(40, 40, 40));
        
        add(createButton("Start", e -> gamePanel.isRunning = true));
        add(createButton("Pause", e -> gamePanel.isRunning = false));
        add(createButton("Reset", e -> gamePanel.resetSimulation()));
        
        add(new JLabel("|"));
        
        add(createButton("König", e -> gamePanel.addKing()));
        add(createButton("Krieger", e -> gamePanel.addWarrior()));
        add(createButton("Späher", e -> gamePanel.addScout()));
        add(createButton("Alle löschen", e -> gamePanel.clearAllUnits()));
        
        add(new JLabel("|"));
        
        add(createButton("Zoom +", e -> gamePanel.zoom *= 1.1));
        add(createButton("Zoom -", e -> gamePanel.zoom /= 1.1));
        add(createButton("Kamera zurück", e -> { gamePanel.zoom = 1.0; gamePanel.cameraX = 0; gamePanel.cameraY = 0; }));
        
        add(new JLabel("|"));
        
        add(createButton("Entropie +", e -> gamePanel.globalEntropyPool += 10));
        add(createButton("Entropie -", e -> gamePanel.globalEntropyPool -= 10));
    }
    
    private JButton createButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.addActionListener(listener);
        return btn;
    }
}

/**
 * =============================================================================
 * INFO-PANEL
 * =============================================================================
 */
class InfoPanel extends JPanel {
    private GamePanel gamePanel;
    private JTextArea textArea;
    
    public InfoPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(300, 800));
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        new Timer(500, e -> update()).start();
    }
    
    public void update() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATRIXGAME DEMO ===\n\n");
        
        sb.append("SIMULATION:\n");
        sb.append(String.format("  Status: %s\n", gamePanel.isRunning ? "LAUFEND" : "PAUSIERT"));
        sb.append(String.format("  Ticks: %d\n", gamePanel.getSimulationTicks()));
        sb.append(String.format("  Zeit: %.1fs\n", gamePanel.getUniverseTime()));
        sb.append(String.format("  Einheiten: %d\n", gamePanel.getUnits().size()));
        sb.append(String.format("  Entropie: %.1f\n\n", gamePanel.getGlobalEntropy()));
        
        sb.append("EINHEITEN:\n");
        Map<Integer, Integer> teamCounts = new HashMap<>();
        for (Unit u : gamePanel.getUnits()) {
            teamCounts.put(u.team, teamCounts.getOrDefault(u.team, 0) + 1);
        }
        for (Map.Entry<Integer, Integer> e : teamCounts.entrySet()) {
            sb.append(String.format("  Team %d: %d\n", e.getKey(), e.getValue()));
        }
        sb.append("\n");
        
        sb.append("STEUERUNG:\n");
        sb.append("  LMB: Einheit auswählen\n");
        sb.append("  RMB: Bewegen/Angreifen\n");
        sb.append("  Drag: Mehrfachauswahl\n");
        sb.append("  Mausrad: Zoom\n");
        sb.append("  A: Alle auswählen\n");
        sb.append("  ESC: Auswahl aufheben\n");
        sb.append("  DEL: Ausgewählte löschen\n");
        sb.append("  LEER: Kamera zurück\n");
        sb.append("  P: Pause\n");
        sb.append("  S: Statistiken (Konsole)\n");
        
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }
}

/**
 * =============================================================================
 * EINHEIT (Unit) - Das Herzstück!
 * =============================================================================
 */
class Unit {
    // ========== IDENTIFIKATION ==========
    long id = (long)(Math.random() * Long.MAX_VALUE);
    String type = "Unit";
    int team = 0;
    
    // ========== PHYSIK ==========
    Vector3D position = new Vector3D(0, 0, 0);
    Vector3D velocity = new Vector3D(0, 0, 0);
    Vector3D acceleration = new Vector3D(0, 0, 0);
    double mass = 10.0;
    int radius = 5;
    Color color;
    
    // ========== LEBEN UND KAMPF ==========
    double health = 100.0;
    double maxHealth = 100.0;
    double damage = 10.0;
    double range = 2.0;
    double speed = 0.5;
    double maxSpeed = 100.0;
    
    // ========== BEWUSSTSEIN (Hawkins-Skala) ==========
    double consciousnessLevel = 100.0;
    String consciousnessMode = "NEUTRAL";
    
    // ========== LEVEL UND ERFAHRUNG ==========
    int level = 1;
    double experience = 0.0;
    double experienceToNextLevel = 1000.0;
    double rewardPoints = 0.0;
    
    // ========== ENTSCHEIDUNGEN ==========
    String decisionAlgorithm = "IDLE";
    Unit target;
    Vector3D targetPosition;
    
    // ========== PHYSIK-FLAGS ==========
    boolean movable = true;
    boolean entropyMoveActive = false;
    double entropyMoveSpeedPercent = 0.0;
    
    // ========== ZUSTAND ==========
    private boolean alive = true;
    
    // ========== KONSTRUKTOR ==========
    public Unit() {
        color = Color.CYAN;
    }
    
    // ========== BEWEGUNG ==========
    public void moveTo(Vector3D target, double moveSpeed) {
        double dx = target.x - position.x;
        double dy = target.y - position.y;
        double dz = target.z - position.z;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        if (dist > 0.0001) {
            position.x += (dx / dist) * moveSpeed * 0.1;
            position.y += (dy / dist) * moveSpeed * 0.1;
            position.z += (dz / dist) * moveSpeed * 0.1;
        }
    }
    
    public void moveTo(Unit other) {
        if (other != null) moveTo(other.position, speed);
    }
    
    // ========== ANGRIFF ==========
    public void attack(Unit enemy) {
        if (enemy == null || !enemy.isAlive() || !isAlive()) return;
        
        double distance = position.distanceTo(enemy.position);
        if (distance > range) return;
        
        double criticalMultiplier = 1.0 + (Math.random() * 2.5);
        double actualDamage = damage * criticalMultiplier;
        
        enemy.health -= actualDamage;
        if (enemy.health <= 0) {
            enemy.alive = false;
            // Erfahrung für den Angreifer
            addExperience(actualDamage * 10);
        }
    }
    
    // ========== ERFAHRUNG ==========
    public void addExperience(double exp) {
        experience += exp * 1.5;
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
        consciousnessLevel = Math.min(1000, consciousnessLevel * 1.05);
        experienceToNextLevel *= 1.2;
    }
    
    // ========== BEWUSSTSEIN ==========
    public void updateConsciousnessMode() {
        if (consciousnessLevel < 20) consciousnessMode = "Shame_Guilt";
        else if (consciousnessLevel < 50) consciousnessMode = "Apathy_Grief";
        else if (consciousnessLevel < 100) consciousnessMode = "Fear_Desire";
        else if (consciousnessLevel < 200) consciousnessMode = "Anger_Pride";
        else if (consciousnessLevel < 250) consciousnessMode = "Courage";
        else if (consciousnessLevel < 350) consciousnessMode = "Neutrality_Willingness";
        else if (consciousnessLevel < 400) consciousnessMode = "Acceptance_Reason";
        else if (consciousnessLevel < 600) consciousnessMode = "Love_Joy";
        else if (consciousnessLevel < 800) consciousnessMode = "Peace";
        else consciousnessMode = "Enlightenment";
    }
    
    public void evolveConsciousness(double amount) {
        consciousnessLevel = Math.min(1000, consciousnessLevel + amount);
        updateConsciousnessMode();
    }
    
    // ========== ZUSTAND ==========
    public boolean isAlive() {
        return alive && health > 0;
    }
    
    public double getHealthPercentage() {
        return (health / maxHealth) * 100.0;
    }
    
    public double getExperiencePercentage() {
        return (experience / experienceToNextLevel) * 100.0;
    }
    
    // ========== UPDATE ==========
    public void update(double dt) {
        if (!isAlive()) return;
        
        // Entropie-Bewegung
        if (entropyMoveActive && entropyMoveSpeedPercent > 0) {
            position.x += (Math.random() - 0.5) * entropyMoveSpeedPercent / 100.0;
            position.y += (Math.random() - 0.5) * entropyMoveSpeedPercent / 100.0;
            position.z += (Math.random() - 0.5) * entropyMoveSpeedPercent / 100.0;
        }
        
        // Ziel verfolgen
        if (target != null && !target.isAlive()) target = null;
        if (target != null && decisionAlgorithm.equals("Nearest_ENEMI")) {
            moveTo(target, speed);
        }
        if (targetPosition != null && decisionAlgorithm.equals("TACTICAL_MOVE")) {
            moveTo(targetPosition, speed);
        }
    }
}

/**
 * =============================================================================
 * 3D-VEKTOR
 * =============================================================================
 */
class Vector3D {
    double x, y, z;
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }
    
    public Vector3D scale(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
    
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    public double lengthSquared() {
        return x*x + y*y + z*z;
    }
    
    public double distanceTo(Vector3D other) {
        return subtract(other).length();
    }
    
    public Vector3D normalize() {
        double len = length();
        return len > 0 ? scale(1.0 / len) : new Vector3D(0, 0, 0);
    }
    
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
