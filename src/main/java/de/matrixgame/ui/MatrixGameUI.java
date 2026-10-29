package de.matrixgame.ui;

import de.matrixgame.core.MatrixGameCore;
import de.matrixgame.core.Unit;
import de.matrixgame.core.MatrixGameConfig;
import de.matrixgame.api.MatrixGameAPI;
import de.matrixgame.physics.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.ArrayList;

/**
 * Interaktive UI für den Matrixgame-Simulator
 * Visualisiert Einheiten, Bewusstseinslevel und Physik-Simulation
 */
public class MatrixGameUI extends JFrame {
    
    // ========== KOMPONENTEN ==========
    private MatrixGamePanel gamePanel;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    private MatrixGameCore core;
    private MatrixGameAPI api;
    
    // ========== KONSTRUKTOR ==========
    public MatrixGameUI() {
        super("Matrixgame Simulator - Kollektives KI-Spiel");
        
        // Core initialisieren
        this.core = MatrixGameCore.getInstance();
        this.api = core.getAPI();
        
        // UI aufbauen
        buildUI();
        
        // Standard-Einheiten erstellen
        createInitialUnits();
        
        // Simulation starten
        core.startSimulation();
    }
    
    // ========== UI AUFBAUEN ==========
    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        
        // Spielpanel (Mitte)
        gamePanel = new MatrixGamePanel(core, api);
        add(gamePanel, BorderLayout.CENTER);
        
        // Steuerpanel (Oben)
        controlPanel = new ControlPanel(core, api, gamePanel);
        add(controlPanel, BorderLayout.NORTH);
        
        // Infopanel (Rechts)
        infoPanel = new InfoPanel(core, api);
        add(infoPanel, BorderLayout.EAST);
        
        // Menüleiste
        createMenuBar();
        
        // Fenster anzeigen
        setVisible(true);
        
        // Timer für UI-Updates
        Timer uiTimer = new Timer(100, e -> {
            gamePanel.repaint();
            infoPanel.update();
        });
        uiTimer.start();
    }
    
    // ========== MENÜLEISTE ==========
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Datei-Menü
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem exitItem = new JMenuItem("Beenden");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        // Simulation-Menü
        JMenu simMenu = new JMenu("Simulation");
        JMenuItem startItem = new JMenuItem("Start");
        startItem.addActionListener(e -> core.startSimulation());
        JMenuItem stopItem = new JMenuItem("Stopp");
        stopItem.addActionListener(e -> core.stopSimulation());
        JMenuItem pauseItem = new JMenuItem("Pause");
        pauseItem.addActionListener(e -> core.pauseSimulation());
        JMenuItem resetItem = new JMenuItem("Zurücksetzen");
        resetItem.addActionListener(e -> {
            core.resetSimulation();
            createInitialUnits();
        });
        simMenu.add(startItem);
        simMenu.add(stopItem);
        simMenu.add(pauseItem);
        simMenu.add(resetItem);
        menuBar.add(simMenu);
        
        // Einheiten-Menü
        JMenu unitMenu = new JMenu("Einheiten");
        JMenuItem addKingItem = new JMenuItem("König hinzufügen");
        addKingItem.addActionListener(e -> {
            Unit unit = api.createUnit("King", 0, 0, 0, 0);
            unit.setPosition(
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200
            );
        });
        JMenuItem addWarriorItem = new JMenuItem("Krieger hinzufügen");
        addWarriorItem.addActionListener(e -> {
            Unit unit = api.createUnit("Warrior", 0, 0, 0, 0);
            unit.setPosition(
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200
            );
        });
        JMenuItem addScoutItem = new JMenuItem("Späher hinzufügen");
        addScoutItem.addActionListener(e -> {
            Unit unit = api.createUnit("Scout", 0, 0, 0, 0);
            unit.setPosition(
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200
            );
        });
        JMenuItem clearAllItem = new JMenuItem("Alle entfernen");
        clearAllItem.addActionListener(e -> core.clearAllUnits());
        unitMenu.add(addKingItem);
        unitMenu.add(addWarriorItem);
        unitMenu.add(addScoutItem);
        unitMenu.add(clearAllItem);
        menuBar.add(unitMenu);
        
        // Ansichts-Menü
        JMenu viewMenu = new JMenu("Ansicht");
        JMenuItem zoomInItem = new JMenuItem("Hineinzoomen");
        zoomInItem.addActionListener(e -> gamePanel.zoomIn());
        JMenuItem zoomOutItem = new JMenuItem("Herauszoomen");
        zoomOutItem.addActionListener(e -> gamePanel.zoomOut());
        JMenuItem resetViewItem = new JMenuItem("Ansicht zurücksetzen");
        resetViewItem.addActionListener(e -> gamePanel.resetView());
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetViewItem);
        menuBar.add(viewMenu);
        
        // Hilfe-Menü
        JMenu helpMenu = new JMenu("Hilfe");
        JMenuItem aboutItem = new JMenuItem("Über");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    // ========== ÜBER-DIALOG ==========
    private void showAboutDialog() {
        String message = "Matrixgame Simulator\n\n" +
                       "Version: 1.0.0\n\n" +
                       "Ein kollektives KI-Spiel mit:\n" +
                       "- Physik-Simulation (Gravitation, Entropie)\n" +
                       "- Bewusstseins-Engine (Hawkins-Skala)\n" +
                       "- Gamification (Quests, Belohnungen)\n" +
                       "- Prognose und Analyse\n\n" +
                       "Andreas Schneider - Matrixgame Kollektiv";
        
        JOptionPane.showMessageDialog(this, message, "Über Matrixgame", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ========== ANFANGS-EINHEITEN ERSTELLEN ==========
    private void createInitialUnits() {
        // Team 0 (Rot)
        Unit king0 = api.createUnit("King", 0, -100, 0, 0);
        king0.setPosition(-100, 0, 0);
        king0.setVelocity(0, 0, 0);
        king0.setMass(100);
        king0.setRadius(10);
        king0.setHealth(500);
        king0.setConsciousnessLevel(300);
        
        for (int i = 0; i < 5; i++) {
            Unit warrior = api.createUnit("Warrior", 0, 0, 0, 0);
            warrior.setPosition(
                -100 + (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50
            );
            warrior.setVelocity((Math.random() - 0.5) * 5, (Math.random() - 0.5) * 5, 0);
            warrior.setMass(30 + Math.random() * 20);
            warrior.setRadius(6);
            warrior.setHealth(200);
            warrior.setDecisionAlgorithm("Nearest_ENEMI");
        }
        
        // Team 1 (Blau)
        Unit king1 = api.createUnit("King", 1, 100, 0, 0);
        king1.setPosition(100, 0, 0);
        king1.setVelocity(0, 0, 0);
        king1.setMass(100);
        king1.setRadius(10);
        king1.setHealth(500);
        king1.setConsciousnessLevel(300);
        
        for (int i = 0; i < 5; i++) {
            Unit warrior = api.createUnit("Warrior", 1, 0, 0, 0);
            warrior.setPosition(
                100 + (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 50
            );
            warrior.setVelocity((Math.random() - 0.5) * 5, (Math.random() - 0.5) * 5, 0);
            warrior.setMass(30 + Math.random() * 20);
            warrior.setRadius(6);
            warrior.setHealth(200);
            warrior.setDecisionAlgorithm("Nearest_ENEMI");
        }
        
        // Team 2 (Grün) - Neutrale Einheiten
        for (int i = 0; i < 3; i++) {
            Unit scout = api.createUnit("Scout", 2, 0, 0, 0);
            scout.setPosition(
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 200
            );
            scout.setVelocity((Math.random() - 0.5) * 10, (Math.random() - 0.5) * 10, 0);
            scout.setMass(15 + Math.random() * 10);
            scout.setRadius(4);
            scout.setHealth(50);
            scout.setSpeed(1.5);
            scout.setDecisionAlgorithm("IDLE");
        }
    }
    
    // ========== INNER KLASSE: SPIEL-PANEL ==========
    public class MatrixGamePanel extends JPanel {
        private MatrixGameCore core;
        private MatrixGameAPI api;
        
        // Kamera-Einstellungen
        private double zoom = 1.0;
        private double cameraX = 0;
        private double cameraY = 0;
        private double cameraZ = 50;
        
        // Maus-Steuerung
        private int dragStartX = -1;
        private int dragStartY = -1;
        private boolean isDragging = false;
        private boolean isRightDragging = false;
        
        // Auswahl
        private Unit selectedUnit;
        private List<Unit> selectedUnits;
        
        // Konstruktoren
        public MatrixGamePanel(MatrixGameCore core, MatrixGameAPI api) {
            this.core = core;
            this.api = api;
            this.selectedUnits = new ArrayList<>();
            
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(1200, 800));
            
            // Maus-Listener
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        dragStartX = e.getX();
                        dragStartY = e.getY();
                        isDragging = false;
                        
                        // Einheit auswählen
                        Unit clickedUnit = getUnitAt(e.getX(), e.getY());
                        if (clickedUnit != null) {
                            if (!e.isShiftDown()) {
                                selectedUnits.clear();
                            }
                            if (selectedUnits.contains(clickedUnit)) {
                                selectedUnits.remove(clickedUnit);
                            } else {
                                selectedUnits.add(clickedUnit);
                            }
                            selectedUnit = selectedUnits.isEmpty() ? null : selectedUnits.get(selectedUnits.size() - 1);
                        } else if (!e.isShiftDown()) {
                            selectedUnits.clear();
                            selectedUnit = null;
                        }
                        
                        repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        dragStartX = e.getX();
                        dragStartY = e.getY();
                        isRightDragging = true;
                        
                        // Rechte Maustaste: Bewegung oder Angriff
                        if (selectedUnits != null && !selectedUnits.isEmpty()) {
                            Unit clickedUnit = getUnitAt(e.getX(), e.getY());
                            if (clickedUnit != null && clickedUnit.getTeam() != selectedUnit.getTeam()) {
                                // Angriff
                                for (Unit unit : selectedUnits) {
                                    unit.setTarget(clickedUnit);
                                    unit.setDecisionAlgorithm("TACTICAL_FOCUS");
                                }
                            } else {
                                // Bewegung
                                Vector3D worldPos = screenToWorld(e.getX(), e.getY());
                                for (Unit unit : selectedUnits) {
                                    unit.setTargetPosition(worldPos.getX(), worldPos.getY(), worldPos.getZ());
                                    unit.setDecisionAlgorithm("TACTICAL_MOVE");
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
                            // Drag-Box Auswahl
                            int x1 = Math.min(dragStartX, e.getX());
                            int y1 = Math.min(dragStartY, e.getY());
                            int x2 = Math.max(dragStartX, e.getX());
                            int y2 = Math.max(dragStartY, e.getY());
                            
                            if (!e.isShiftDown()) {
                                selectedUnits.clear();
                            }
                            
                            for (Unit unit : core.getUnits()) {
                                Point screenPos = worldToScreen(unit.getPosition());
                                if (screenPos.x >= x1 && screenPos.x <= x2 && 
                                    screenPos.y >= y1 && screenPos.y <= y2) {
                                    selectedUnits.add(unit);
                                }
                            }
                            
                            selectedUnit = selectedUnits.isEmpty() ? null : selectedUnits.get(0);
                        }
                        
                        isDragging = false;
                        isRightDragging = false;
                        dragStartX = -1;
                        dragStartY = -1;
                        repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        isRightDragging = false;
                        repaint();
                    }
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (dragStartX != -1 && dragStartY != -1) {
                            int dx = e.getX() - dragStartX;
                            int dy = e.getY() - dragStartY;
                            if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                                isDragging = true;
                            }
                        }
                        repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        if (isRightDragging) {
                            int dx = e.getX() - dragStartX;
                            int dy = e.getY() - dragStartY;
                            cameraX -= dx / zoom;
                            cameraY += dy / zoom;
                            dragStartX = e.getX();
                            dragStartY = e.getY();
                            repaint();
                        }
                    }
                }
            });
            
            // Mausrad-Listener
            addMouseWheelListener(e -> {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
                repaint();
            });
            
            // Tastatur-Listener
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleKeyPress(e);
                }
            });
        }
        
        // Tastatur-Steuerung
        private void handleKeyPress(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    // Alle Einheiten auswählen
                    selectedUnits = new ArrayList<>(core.getUnits());
                    selectedUnit = selectedUnits.isEmpty() ? null : selectedUnits.get(0);
                    break;
                case KeyEvent.VK_ESCAPE:
                    // Auswahl aufheben
                    selectedUnits.clear();
                    selectedUnit = null;
                    break;
                case KeyEvent.VK_DELETE:
                    // Ausgewählte Einheiten entfernen
                    for (Unit unit : new ArrayList<>(selectedUnits)) {
                        core.removeUnit(unit);
                        selectedUnits.remove(unit);
                    }
                    selectedUnit = selectedUnits.isEmpty() ? null : selectedUnits.get(0);
                    break;
                case KeyEvent.VK_SPACE:
                    // Kamera zurücksetzen
                    resetView();
                    break;
                case KeyEvent.VK_P:
                    // Simulation pausieren
                    if (core.isRunning()) {
                        core.pauseSimulation();
                    } else {
                        core.resumeSimulation();
                    }
                    break;
            }
            repaint();
        }
        
        // Zoom
        public void zoomIn() {
            zoom *= 1.1;
            if (zoom > 10.0) zoom = 10.0;
        }
        
        public void zoomOut() {
            zoom /= 1.1;
            if (zoom < 0.1) zoom = 0.1;
        }
        
        public void resetView() {
            zoom = 1.0;
            cameraX = 0;
            cameraY = 0;
            cameraZ = 50;
        }
        
        // Welt-zu-Bildschirm-Koordinaten
        private Point worldToScreen(Vector3D worldPos) {
            double screenX = getWidth() / 2.0 + (worldPos.getX() - cameraX) * zoom;
            double screenY = getHeight() / 2.0 - (worldPos.getY() - cameraY) * zoom;
            return new Point((int) screenX, (int) screenY);
        }
        
        // Bildschirm-zu-Welt-Koordinaten
        private Vector3D screenToWorld(int screenX, int screenY) {
            double worldX = (screenX - getWidth() / 2.0) / zoom + cameraX;
            double worldY = -(screenY - getHeight() / 2.0) / zoom + cameraY;
            return new Vector3D(worldX, worldY, 0);
        }
        
        // Einheit an Position abrufen
        private Unit getUnitAt(int screenX, int screenY) {
            for (Unit unit : core.getUnits()) {
                Point screenPos = worldToScreen(unit.getPosition());
                double distance = Math.sqrt(
                    Math.pow(screenX - screenPos.x, 2) + 
                    Math.pow(screenY - screenPos.y, 2)
                );
                if (distance <= unit.getRadius() * zoom) {
                    return unit;
                }
            }
            return null;
        }
        
        // Zeichnen
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            // Anti-Aliasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Hintergrund
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Koordinatensystem
            drawCoordinateSystem(g2);
            
            // Einheiten zeichnen
            drawUnits(g2);
            
            // Auswahl zeichnen
            drawSelection(g2);
            
            // Drag-Box zeichnen
            if (isDragging && dragStartX != -1 && dragStartY != -1) {
                drawDragBox(g2);
            }
            
            // UI-Elemente
            drawUI(g2);
        }
        
        // Koordinatensystem zeichnen
        private void drawCoordinateSystem(Graphics2D g2) {
            g2.setColor(new Color(50, 50, 50));
            
            // X-Achse
            Point start = worldToScreen(new Vector3D(-500, 0, 0));
            Point end = worldToScreen(new Vector3D(500, 0, 0));
            g2.drawLine(start.x, start.y, end.x, end.y);
            
            // Y-Achse
            start = worldToScreen(new Vector3D(0, -500, 0));
            end = worldToScreen(new Vector3D(0, 500, 0));
            g2.drawLine(start.x, start.y, end.x, end.y);
            
            // Ursprung markieren
            Point origin = worldToScreen(new Vector3D(0, 0, 0));
            g2.setColor(Color.WHITE);
            g2.fillOval(origin.x - 3, origin.y - 3, 6, 6);
        }
        
        // Einheiten zeichnen
        private void drawUnits(Graphics2D g2) {
            List<Unit> units = core.getUnits();
            
            for (Unit unit : units) {
                if (!unit.isAlive()) continue;
                
                Point screenPos = worldToScreen(unit.getPosition());
                double radius = unit.getRadius() * zoom;
                
                // Farbe basierend auf Team und Bewusstsein
                Color color = getUnitColor(unit);
                g2.setColor(color);
                
                // Einheit zeichnen
                g2.fillOval(
                    (int) (screenPos.x - radius),
                    (int) (screenPos.y - radius),
                    (int) (radius * 2),
                    (int) (radius * 2)
                );
                
                // Rahmen für ausgewählte Einheiten
                if (selectedUnits.contains(unit)) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(
                        (int) (screenPos.x - radius - 2),
                        (int) (screenPos.y - radius - 2),
                        (int) (radius * 2 + 4),
                        (int) (radius * 2 + 4)
                    );
                }
                
                // Typ markieren
                if ("King".equals(unit.getType())) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString("K", screenPos.x - 5, screenPos.y - (int) radius - 5);
                }
                
                // Gesundheitsbalken
                if (radius > 10) {
                    drawHealthBar(g2, screenPos, radius, unit);
                }
                
                // Geschwindigkeitsanzeige
                if (radius > 15) {
                    drawVelocityIndicator(g2, screenPos, unit);
                }
            }
        }
        
        // Gesundheitsbalken zeichnen
        private void drawHealthBar(Graphics2D g2, Point screenPos, double radius, Unit unit) {
            double healthPercent = unit.getHealthPercentage() / 100.0;
            int barWidth = (int) (radius * 2);
            int barHeight = 4;
            int barY = screenPos.y - (int) radius - 10;
            
            // Hintergrund
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(screenPos.x - barWidth / 2, barY, barWidth, barHeight);
            
            // Gesundheitsbalken
            Color healthColor = healthPercent > 0.5 ? Color.GREEN : 
                              healthPercent > 0.25 ? Color.YELLOW : Color.RED;
            g2.setColor(healthColor);
            g2.fillRect(screenPos.x - barWidth / 2, barY, (int) (barWidth * healthPercent), barHeight);
        }
        
        // Geschwindigkeitsindikator zeichnen
        private void drawVelocityIndicator(Graphics2D g2, Point screenPos, Unit unit) {
            Vector3D velocity = unit.getVelocity();
            double speed = velocity.length();
            
            if (speed > 0.1) {
                // Richtungsvektor
                Vector3D dir = velocity.normalize();
                
                // Linie zeichnen
                g2.setColor(Color.CYAN);
                Point endPos = worldToScreen(
                    unit.getPosition().add(dir.scale(unit.getRadius() * 1.5))
                );
                g2.drawLine(screenPos.x, screenPos.y, endPos.x, endPos.y);
                
                // Pfeilspitze
                g2.fillOval(endPos.x - 3, endPos.y - 3, 6, 6);
            }
        }
        
        // Auswahl zeichnen
        private void drawSelection(Graphics2D g2) {
            if (selectedUnit != null) {
                Point screenPos = worldToScreen(selectedUnit.getPosition());
                double radius = selectedUnit.getRadius() * zoom;
                
                // Auswahlkreis
                g2.setColor(new Color(255, 255, 0, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(
                    (int) (screenPos.x - radius - 5),
                    (int) (screenPos.y - radius - 5),
                    (int) (radius * 2 + 10),
                    (int) (radius * 2 + 10)
                );
            }
        }
        
        // Drag-Box zeichnen
        private void drawDragBox(Graphics2D g2) {
            int x1 = Math.min(dragStartX, getMousePosition().x);
            int y1 = Math.min(dragStartY, getMousePosition().y);
            int x2 = Math.max(dragStartX, getMousePosition().x);
            int y2 = Math.max(dragStartY, getMousePosition().y);
            
            g2.setColor(new Color(0, 255, 128, 60));
            g2.fillRect(x1, y1, x2 - x1, y2 - y1);
            
            g2.setColor(new Color(0, 255, 128));
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
        
        // UI-Elemente zeichnen
        private void drawUI(Graphics2D g2) {
            // Simulations-Info
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            
            String info = String.format(
                "Ticks: %d | Zeit: %.1fs | Einheiten: %d | Entropie: %.1f",
                core.getSimulationTicks(),
                core.getUniverseTime(),
                core.getUnitCount(),
                core.getGlobalEntropyPool()
            );
            g2.drawString(info, 10, 20);
            
            // Auswahl-Info
            if (selectedUnit != null) {
                String selectionInfo = String.format(
                    "Ausgewählt: %s (Level %d, HP: %.0f/%.0f, Bewusstsein: %.0f)",
                    selectedUnit.getName(),
                    selectedUnit.getLevel(),
                    selectedUnit.getHealth(),
                    selectedUnit.getMaxHealth(),
                    selectedUnit.getConsciousnessLevel()
                );
                g2.drawString(selectionInfo, 10, 40);
            }
            
            // Steuerungs-Hinweise
            String controls = "Steuerung: LMB=Auswählen, RMB=Bewegen/Angreifen, Mausrad=Zoom, A=Alle auswählen, ESC=Auswahl aufheben, DEL=Löschen, LEERTASTE=Kamera zurücksetzen, P=Pause";
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(controls, 10, getHeight() - 20);
        }
        
        // Einheit-Farbe bestimmen
        private Color getUnitColor(Unit unit) {
            Color baseColor = unit.getColor();
            if (baseColor == null) {
                switch (unit.getTeam()) {
                    case 0: baseColor = MatrixGameConfig.TEAM_1_COLOR; break;
                    case 1: baseColor = MatrixGameConfig.TEAM_2_COLOR; break;
                    case 2: baseColor = MatrixGameConfig.TEAM_3_COLOR; break;
                    default: baseColor = MatrixGameConfig.DEFAULT_UNIT_COLOR;
                }
            }
            
            // Bewusstseinslevel in die Farbe einfließen lassen
            double consciousness = unit.getConsciousnessLevel() / MatrixGameConfig.MAX_CONSCIOUSNESS_LEVEL;
            
            // Je höher das Bewusstsein, desto heller die Farbe
            int r = Math.min(255, (int) (baseColor.getRed() * (1 + consciousness * 0.5)));
            int g = Math.min(255, (int) (baseColor.getGreen() * (1 + consciousness * 0.5)));
            int b = Math.min(255, (int) (baseColor.getBlue() * (1 + consciousness * 0.5)));
            
            return new Color(r, g, b);
        }
    }
    
    // ========== INNER KLASSE: STEUER-PANEL ==========
    public class ControlPanel extends JPanel {
        private MatrixGameCore core;
        private MatrixGameAPI api;
        private MatrixGamePanel gamePanel;
        
        public ControlPanel(MatrixGameCore core, MatrixGameAPI api, MatrixGamePanel gamePanel) {
            this.core = core;
            this.api = api;
            this.gamePanel = gamePanel;
            
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBackground(new Color(40, 40, 40));
            
            // Simulations-Steuerung
            add(createButton("Start", e -> core.startSimulation()));
            add(createButton("Pause", e -> core.pauseSimulation()));
            add(createButton("Stopp", e -> core.stopSimulation()));
            add(createButton("Reset", e -> {
                core.resetSimulation();
                // Neue Einheiten erstellen
                for (int i = 0; i < 3; i++) {
                    Unit king = api.createUnit("King", i % 3, (i - 1) * 100, 0, 0);
                    king.setMass(100);
                    king.setRadius(10);
                    king.setHealth(500);
                }
            }));
            
            add(new JLabel("|"));
            
            // Einheiten-Steuerung
            add(createButton("König", e -> {
                Unit unit = api.createUnit("King", 0, 0, 0, 0);
                unit.setPosition(
                    (Math.random() - 0.5) * 200,
                    (Math.random() - 0.5) * 200,
                    0
                );
            }));
            add(createButton("Krieger", e -> {
                Unit unit = api.createUnit("Warrior", 0, 0, 0, 0);
                unit.setPosition(
                    (Math.random() - 0.5) * 200,
                    (Math.random() - 0.5) * 200,
                    0
                );
            }));
            add(createButton("Späher", e -> {
                Unit unit = api.createUnit("Scout", 0, 0, 0, 0);
                unit.setPosition(
                    (Math.random() - 0.5) * 200,
                    (Math.random() - 0.5) * 200,
                    0
                );
            }));
            add(createButton("Alle löschen", e -> core.clearAllUnits()));
            
            add(new JLabel("|"));
            
            // Kamera-Steuerung
            add(createButton("Zoom +", e -> gamePanel.zoomIn()));
            add(createButton("Zoom -", e -> gamePanel.zoomOut()));
            add(createButton("Kamera zurück", e -> gamePanel.resetView()));
            
            add(new JLabel("|"));
            
            // Entropie-Steuerung
            add(createButton("Entropie +", e -> 
                core.setGlobalEntropyPool(core.getGlobalEntropyPool() + 10)));
            add(createButton("Entropie -", e -> 
                core.setGlobalEntropyPool(core.getGlobalEntropyPool() - 10)));
        }
        
        private JButton createButton(String text, ActionListener listener) {
            JButton button = new JButton(text);
            button.setFocusPainted(false);
            button.setBackground(new Color(60, 60, 60));
            button.setForeground(Color.WHITE);
            button.addActionListener(listener);
            return button;
        }
    }
    
    // ========== INNER KLASSE: INFO-PANEL ==========
    public class InfoPanel extends JPanel {
        private MatrixGameCore core;
        private MatrixGameAPI api;
        private JTextArea infoText;
        
        public InfoPanel(MatrixGameCore core, MatrixGameAPI api) {
            this.core = core;
            this.api = api;
            
            setLayout(new BorderLayout());
            setBackground(new Color(30, 30, 30));
            setPreferredSize(new Dimension(300, 800));
            
            infoText = new JTextArea();
            infoText.setEditable(false);
            infoText.setBackground(new Color(30, 30, 30));
            infoText.setForeground(Color.WHITE);
            infoText.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(infoText);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            add(scrollPane, BorderLayout.CENTER);
            
            // Timer für Updates
            Timer updateTimer = new Timer(500, e -> update());
            updateTimer.start();
        }
        
        public void update() {
            StringBuilder sb = new StringBuilder();
            
            // Überschrift
            sb.append("=== MATRIXGAME SIMULATOR ===\n\n");
            
            // Simulations-Statistiken
            sb.append("SIMULATION:\n");
            sb.append(String.format("  Status: %s\n", core.isRunning() ? "LAUFEND" : "ANGEHALTEN"));
            sb.append(String.format("  Ticks: %d\n", core.getSimulationTicks()));
            sb.append(String.format("  Zeit: %.1f s\n", core.getUniverseTime()));
            sb.append(String.format("  Einheiten: %d\n", core.getUnitCount()));
            sb.append(String.format("  Entropie: %.1f\n\n", core.getGlobalEntropyPool()));
            
            // Physik-Statistiken
            sb.append("PHYSIK:\n");
            sb.append(String.format("  Gesamtmasse: %.1f\n", core.getTotalMass()));
            sb.append(String.format("  Gesamtenergie: %.1f\n\n", core.getTotalEnergy()));
            
            // Bewusstseins-Statistiken
            sb.append("BEWUSSTSEIN:\n");
            sb.append(String.format("  Durchschnitt: %.1f\n", core.getAverageConsciousness()));
            sb.append("\n");
            
            // Einheiten-Statistiken
            sb.append("EINHEITEN:\n");
            Map<Integer, Integer> teamCounts = new HashMap<>();
            for (Unit unit : core.getUnits()) {
                teamCounts.put(unit.getTeam(), teamCounts.getOrDefault(unit.getTeam(), 0) + 1);
            }
            for (Map.Entry<Integer, Integer> entry : teamCounts.entrySet()) {
                sb.append(String.format("  Team %d: %d Einheiten\n", entry.getKey(), entry.getValue()));
            }
            sb.append("\n");
            
            // Quests
            sb.append("QUESTS:\n");
            for (QuestSystem.Quest quest : core.getQuestSystem().getActiveQuests()) {
                sb.append(String.format("  %s: %s\n", quest.getName(), quest.getStatus()));
            }
            sb.append("\n");
            
            // Steuerungshinweise
            sb.append("STEUERUNG:\n");
            sb.append("  LMB: Einheit auswählen\n");
            sb.append("  RMB: Bewegen/Angreifen\n");
            sb.append("  Drag: Mehrfachauswahl\n");
            sb.append("  A: Alle auswählen\n");
            sb.append("  ESC: Auswahl aufheben\n");
            sb.append("  DEL: Ausgewählte löschen\n");
            sb.append("  LEERT: Kamera zurücksetzen\n");
            sb.append("  P: Pause\n");
            
            infoText.setText(sb.toString());
            infoText.setCaretPosition(0);
        }
    }
    
    // ========== MAIN-METHODE ==========
    public static void main(String[] args) {
        // Look and Feel setzen
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // UI starten
        SwingUtilities.invokeLater(() -> {
            new MatrixGameUI();
        });
    }
}
