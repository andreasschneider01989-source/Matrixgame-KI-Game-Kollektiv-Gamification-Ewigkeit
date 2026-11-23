package rhgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Matrixgame Universal Grid Lab v0.10.0 – keyboard camera controls over the v0.8.5 engine.
 *
 * Core contract:
 *   - Every entity is a GameObject stored in one World object list.
 *   - Every GameObject shares one fixed 49-option vocabulary per tick:
 *       WAIT + 4 MOVE options + 8 typed interactions in each direction.
 *   - Each controller chooses one legal option; the resolver validates it centrally.
 *   - Direction always means one adjacent grid cell: UP, DOWN, LEFT, RIGHT.
 *   - All strategies read the same immutable tick snapshot.
 *   - Actions are validated and resolved centrally by World.
 *   - Interactions are pluggable rules, not hard-coded into the renderer.
 *
 * Controls:
 *   Every GameObject has the same universal action vocabulary per tick:
 *       WAIT + MOVE(4 directions) + 11 typed interactions in each direction.
 *   Arrow keys / WASD : execute the selected mode in that direction
 *   Q / E             : cycle action modes; Y selects BUILD; C selects BUILD_DEPOT
 *   Left click        : select a GameObject (editor mode)
 *   Right drag        : pan camera
 *   Mouse wheel       : cursor-centred zoom
 *   Ctrl + arrows     : pan camera by three world cells
 *   Ctrl + = / -      : zoom in / out
 *   Ctrl + 0          : reset camera to fit world
 *   Space             : WAIT / advance one tick
 *   H                 : spawn intelligent hunter
 *   N                 : spawn random-moving enemy
 *   I                 : toggle enemy decision algorithm (adaptive softmax / random legal)
 *   T                 : toggle evolutionary auto-run
 *   V                 : switch Evolution / Boulder Dash scenario
 *   F                 : spawn 10 food resources
 *   G / B             : faster / slower evolution clock
 *   R                 : reset scenario
 *   P                 : pause
 *
 * Default interactive world: 120x80 cells (4x the previous 60x40 area).
 * Camera zoom: 0.15x to 300x; press 0 to fit the complete world.
 * Java 17+; no external libraries.
 */
public final class MatrixgameUniversalGridLab_v10_0 extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final long SEED = 20261116L;
    private static final int DEFAULT_WORLD_WIDTH = 160;
    private static final int DEFAULT_WORLD_HEIGHT = 100;
    private static final int TEAM_COUNT = 6;
    private static final int START_UNITS_PER_TEAM = 4;
    private static final int MAX_EVOLUTION_POPULATION = 360;
    private static final int MAX_FOOD = 600;
    private static final double BUILDING_SPLIT_THRESHOLD = 24.0;
    private static final double LEVEL2_PRODUCTION_COST = 12.0;
    private static final double BUILDING_STORAGE_CAPACITY = 48.0;
    /** Evolution cells build with energy; conventional units use carried gems. */
    private static final double CREATURE_BUILD_ENERGY_COST = 6.0;
    private static final double STANDARD_BUILD_GEM_COST = 2.0;

    private final transient GameWorld world = GameWorld.createEvolutionScenario(SEED, DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT);
    private final WorldCanvas canvas = new WorldCanvas(world);
    private final JLabel statusLabel = new JLabel();
    private final JLabel stateLabel = new JLabel();
    private final JLabel selectionLabel = new JLabel();
    private final JLabel genomeSummaryLabel = new JLabel();
    private final JLabel actionLabel = new JLabel("Modus: MOVE | Aktionsraum: 49");
    private final JToggleButton playerModeButton = new JToggleButton("Ausgewählte Unit: KI");
    private final JSlider speedSlider = new JSlider(100, 2000, 500);
    private final JLabel speedLabel = new JLabel("500 ms | 2,0 Ticks/s");
    private JButton enemyAlgorithmButton;
    private JButton evolutionAutoButton;
    private JButton mutationPressureButton;
    private boolean randomEnemyAlgorithm;
    private ActionMode actionMode = ActionMode.MOVE;
    private boolean playerMode;
    private boolean paused;
    private boolean evolutionAuto = true;
    private static final int DEFAULT_TICK_DELAY_MS = 500;
    private final javax.swing.Timer evolutionTimer;
    private String selectedObjectId = "cell-0001";

    public MatrixgameUniversalGridLab_v10_0() {
        super("Matrixgame – Universal Grid Lab v0.10.0 | Evolution Objectives + Keyboard Camera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 9, 1, 9));
        stateLabel.setBorder(BorderFactory.createEmptyBorder(1, 9, 1, 9));
        selectionLabel.setBorder(BorderFactory.createEmptyBorder(1, 9, 5, 9));
        header.add(statusLabel);
        header.add(stateLabel);
        header.add(selectionLabel);
        genomeSummaryLabel.setBorder(BorderFactory.createEmptyBorder(1, 9, 5, 9));
        header.add(genomeSummaryLabel);
        add(header, BorderLayout.NORTH);

        add(canvas, BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        installKeyBindings();
        refresh();
        setSize(1220, 820);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        selectedObjectId = world.firstControllableObjectId();
        if (world.evolutionMode) {
            genomeSummaryLabel.setText(world.genomePopulationSummary() + String.format(Locale.ROOT,
                    " | Mutationsdruck %.1fx | %s", world.mutationPressure, world.blueLineageObjective()));
        } else {
            genomeSummaryLabel.setText("INPUT → OPERATION → OUTPUT | Vererbbare Sensor- und Entscheidungsparameter im Evolutionsmodus");
        }
        playerModeButton.setEnabled(true);
        canvas.resetCamera();
        refresh();
        evolutionTimer = new javax.swing.Timer(DEFAULT_TICK_DELAY_MS, e -> {
            if (evolutionAuto && !paused && world.evolutionMode && !world.gameOver) {
                world.tick();
                refresh();
            }
        });
        evolutionTimer.start();
        canvas.requestFocusInWindow();
    }

    private JPanel buildControls() {
        JPanel controls = new JPanel(new GridLayout(4, 1, 0, 3));
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
        JPanel playRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
        JPanel adminRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));

        JButton previousAction = new JButton("Aktion ← (Q)");
        previousAction.addActionListener(e -> changeActionMode(-1));
        JButton nextAction = new JButton("Aktion → (E)");
        nextAction.addActionListener(e -> changeActionMode(1));
        playerModeButton.addActionListener(e -> {
            GameObject selected = world.getObject(selectedObjectId);
            if (selected == null || !isControllable(selected)) return;
            playerMode = playerModeButton.isSelected();
            world.setHumanControlled(selected.id, playerMode);
            world.message = playerMode
                    ? selected.name + " wird ausschließlich von dir gesteuert."
                    : selected.name + " wird wieder von der KI gesteuert.";
            refresh();
            canvas.requestFocusInWindow();
        });
        JButton buildingBuildButton = new JButton("Gebäude bauen (Y)");
        buildingBuildButton.setToolTipText("Baut ein unbewegliches Gebäude auf einem freien Nachbarfeld. Evolution: 6 Energie; sonst: 2 Edelsteine.");
        buildingBuildButton.addActionListener(e -> selectActionMode(ActionMode.BUILD));
        actionRow.add(buildingBuildButton);

        JButton depotBuildButton = new JButton("Depot bauen (C)");
        depotBuildButton.setToolTipText("Aktiviert Depotbau; danach Richtungstaste oder Richtungsbutton drücken. Kosten: 3 Edelsteine.");
        depotBuildButton.addActionListener(e -> selectActionMode(ActionMode.BUILD_DEPOT));
        actionRow.add(previousAction);
        actionRow.add(actionLabel);
        actionRow.add(nextAction);
        actionRow.add(depotBuildButton);
        actionRow.add(playerModeButton);

        playRow.add(directionButton("↑ Hoch", Direction.UP));
        playRow.add(directionButton("← Links", Direction.LEFT));
        playRow.add(directionButton("↓ Runter", Direction.DOWN));
        playRow.add(directionButton("Rechts →", Direction.RIGHT));
        JButton wait = new JButton("Warten / Tick (Space)");
        wait.addActionListener(e -> advanceWait());
        playRow.add(wait);
        JButton hunter = new JButton("Hybrid-Gegner + (H)");
        hunter.addActionListener(e -> { world.spawnHunter(); refresh(); canvas.requestFocusInWindow(); });
        adminRow.add(hunter);
        JButton wanderer = new JButton("Wanderer + (N)");
        wanderer.addActionListener(e -> { world.spawnWanderer(); refresh(); canvas.requestFocusInWindow(); });
        adminRow.add(wanderer);
        enemyAlgorithmButton = new JButton("Gegner-KI: ADAPTIVE SOFTMAX (F2)");
        enemyAlgorithmButton.addActionListener(e -> toggleEnemyAlgorithm());
        adminRow.add(enemyAlgorithmButton);
        JButton pause = new JButton("Pause (P)");
        pause.addActionListener(e -> togglePause());
        adminRow.add(pause);
        JButton reset = new JButton("NEUSTART (R)");
        reset.setFont(reset.getFont().deriveFont(Font.BOLD, 14f));
        reset.setForeground(new Color(150, 35, 35));
        reset.setToolTipText("Szenario vollständig neu starten (Taste R)");
        reset.addActionListener(e -> restart());
        adminRow.add(reset);
        JButton overview = new JButton("Weltübersicht (0)");
        overview.addActionListener(e -> { canvas.fitWorld(); canvas.requestFocusInWindow(); });
        adminRow.add(overview);

        JPanel evolutionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
        evolutionAutoButton = new JButton("Evolution AUTO: EIN (T)");
        evolutionAutoButton.addActionListener(e -> toggleEvolutionAuto(evolutionAutoButton));
        JButton scenario = new JButton("Modus wechseln: Evolution / Boulder Dash (V)");
        scenario.addActionListener(e -> switchScenario());
        JButton food = new JButton("Nahrung +10 (F)");
        food.addActionListener(e -> { world.spawnFood(10); refresh(); canvas.requestFocusInWindow(); });
        JButton faster = new JButton("Tempo + (G)");
        faster.addActionListener(e -> changeEvolutionSpeed(-20));
        JButton slower = new JButton("Tempo - (B)");
        slower.addActionListener(e -> changeEvolutionSpeed(20));
        mutationPressureButton = new JButton("Mutationsdruck: 1.0x (M)");
        mutationPressureButton.addActionListener(e -> cycleMutationPressure());
        evolutionRow.add(evolutionAutoButton);
        evolutionRow.add(scenario);
        evolutionRow.add(food);
        evolutionRow.add(faster);
        evolutionRow.add(slower);
        evolutionRow.add(new JLabel("Tempo:"));
        speedSlider.setToolTipText("Simulationsintervall: 100 bis 2000 Millisekunden pro Tick");
        speedSlider.setPreferredSize(new Dimension(150, 24));
        speedSlider.addChangeListener(e -> {
            int delay = speedSlider.getValue();
            speedLabel.setText(String.format(Locale.ROOT, "%d ms | %.1f Ticks/s", delay, 1000.0 / delay));
            if (evolutionTimer != null) evolutionTimer.setDelay(delay);
        });
        evolutionRow.add(speedSlider);
        evolutionRow.add(speedLabel);
        evolutionRow.add(mutationPressureButton);

        controls.add(actionRow);
        controls.add(playRow);
        controls.add(adminRow);
        controls.add(evolutionRow);
        return controls;
    }

    private JButton directionButton(String label, Direction direction) {
        JButton button = new JButton(label);
        button.addActionListener(e -> submitDirection(direction));
        return button;
    }

    private void selectActionMode(ActionMode mode) {
        if (mode == ActionMode.BUILD || mode == ActionMode.BUILD_DEPOT) {
            GameObject selected = world.getObject(selectedObjectId);
            if (selected != null && isControllable(selected)) {
                world.setHumanControlled(selected.id, true);
                playerMode = true;
                playerModeButton.setSelected(true);
                playerModeButton.setText("Ausgewählte Unit: MENSCH");
            }
        }
        actionMode = Objects.requireNonNull(mode);
        canvas.setActionMode(actionMode);
        actionLabel.setText("Aktion: " + actionMode.label + " | Aktionsraum: 49");
        if (mode == ActionMode.BUILD) {
            world.message = world.evolutionMode
                    ? "GEBÄUDEBAU aktiv: Richtung wählen. Kosten: 6 Energie; Zielfeld muss frei sein. (Y)"
                    : "GEBÄUDEBAU aktiv: Richtung wählen. Kosten: 2 Edelsteine; Zielfeld muss frei sein. (Y)";
        } else if (mode == ActionMode.BUILD_DEPOT) {
            world.message = "DEPOTBAU aktiv: Richtung wählen. Kosten: 3 Edelsteine; freies Feld oder lockere Erde. (C)";
        } else {
            world.message = "Aktion gewählt: " + actionMode.label + ". Q/E wechseln die Aktionsauswahl.";
        }
        refresh();
        canvas.requestFocusInWindow();
    }

    private void changeActionMode(int delta) {
        ActionMode[] modes = ActionMode.values();
        int next = Math.floorMod(actionMode.ordinal() + delta, modes.length);
        actionMode = modes[next];
        canvas.setActionMode(actionMode);
        actionLabel.setText("Aktion: " + actionMode.label + " | Aktionsraum: 49");
        canvas.requestFocusInWindow();
    }

    private void installKeyBindings() {
        InputMap input = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = getRootPane().getActionMap();
        bind(input, actions, "UP", "up", () -> submitDirection(Direction.UP));
        bind(input, actions, "W", "upW", () -> submitDirection(Direction.UP));
        bind(input, actions, "DOWN", "down", () -> submitDirection(Direction.DOWN));
        bind(input, actions, "S", "downS", () -> submitDirection(Direction.DOWN));
        bind(input, actions, "LEFT", "left", () -> submitDirection(Direction.LEFT));
        bind(input, actions, "A", "leftA", () -> submitDirection(Direction.LEFT));
        bind(input, actions, "RIGHT", "right", () -> submitDirection(Direction.RIGHT));
        bind(input, actions, "D", "rightD", () -> submitDirection(Direction.RIGHT));
        bind(input, actions, "SPACE", "wait", this::advanceWait);
        bind(input, actions, "R", "reset", this::restart);
        bind(input, actions, "P", "pause", this::togglePause);
        bind(input, actions, "H", "spawnHunter", () -> { world.spawnHunter(); refresh(); });
        bind(input, actions, "N", "spawnWanderer", () -> { world.spawnWanderer(); refresh(); });
        bind(input, actions, "F2", "toggleEnemyAlgorithm", this::toggleEnemyAlgorithm);
        bind(input, actions, "J", "actionMoveJ", () -> selectActionMode(ActionMode.MOVE));
        bind(input, actions, "I", "actionPushI", () -> selectActionMode(ActionMode.PUSH));
        bind(input, actions, "K", "actionAttackK", () -> selectActionMode(ActionMode.ATTACK));
        bind(input, actions, "L", "actionUseL", () -> selectActionMode(ActionMode.USE));
        bindCharacter(input, actions, 'ö', "actionReproduceOe", () -> selectActionMode(ActionMode.REPRODUCE));
        bind(input, actions, "Q", "previousActionQ", () -> changeActionMode(-1));
        bind(input, actions, "E", "nextActionE", () -> changeActionMode(1));
        bind(input, actions, "T", "toggleEvolution", this::toggleEvolutionAutoFromKey);
        bind(input, actions, "V", "switchScenario", this::switchScenario);
        bind(input, actions, "F", "spawnFood", () -> { world.spawnFood(10); refresh(); });
        bind(input, actions, "M", "mutationPressure", this::cycleMutationPressure);
        bind(input, actions, "G", "faster", () -> changeEvolutionSpeed(-20));
        bind(input, actions, "B", "slower", () -> changeEvolutionSpeed(20));
        bind(input, actions, "Y", "actionBuildBuilding", () -> selectActionMode(ActionMode.BUILD));
        bind(input, actions, "C", "actionBuildDepot", () -> selectActionMode(ActionMode.BUILD_DEPOT));
        bind(input, actions, "X", "actionDeposit", () -> selectActionMode(ActionMode.DEPOSIT));
        bind(input, actions, "Z", "actionProduce", () -> selectActionMode(ActionMode.PRODUCE));
        bind(input, actions, "0", "worldOverview", () -> canvas.fitWorld());
        // Camera shortcuts use CTRL, preserving the existing unit movement keys.
        bind(input, actions, "ctrl UP", "cameraPanUp", () -> canvas.panByCells(0, -3));
        bind(input, actions, "ctrl DOWN", "cameraPanDown", () -> canvas.panByCells(0, 3));
        bind(input, actions, "ctrl LEFT", "cameraPanLeft", () -> canvas.panByCells(-3, 0));
        bind(input, actions, "ctrl RIGHT", "cameraPanRight", () -> canvas.panByCells(3, 0));
        bind(input, actions, "ctrl EQUALS", "cameraZoomIn", () -> canvas.zoomBy(1.12));
        bind(input, actions, "ctrl MINUS", "cameraZoomOut", () -> canvas.zoomBy(1.0 / 1.12));
        bind(input, actions, "ctrl 0", "cameraReset", canvas::resetCamera);
    }

    private static void bind(InputMap input, ActionMap actions, String key, String name, Runnable runnable) {
        input.put(KeyStroke.getKeyStroke(key), name);
        actions.put(name, new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(ActionEvent e) { runnable.run(); }
        });
    }

    private static void bindCharacter(InputMap input, ActionMap actions, char key, String name, Runnable runnable) {
        input.put(KeyStroke.getKeyStroke(key), name);
        actions.put(name, new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(ActionEvent e) { runnable.run(); }
        });
    }

    private void submitDirection(Direction direction) {
        if (paused || world.gameOver) return;
        GameObject selected = world.getObject(selectedObjectId);
        if (selected == null || !world.isHumanControlled(selected.id)) {
            world.message = "Diese Unit ist im KI-Modus. Aktiviere zuerst 'Ausgewählte Unit: MENSCH'.";
            refresh();
            return;
        }
        GameAction action = GameAction.fromMode(actionMode, direction);
        if (actionMode == ActionMode.BUILD) {
            String reason = world.buildFailure(selected, direction);
            if (reason != null) {
                world.message = reason;
                refresh();
                return;
            }
        } else if (actionMode == ActionMode.BUILD_DEPOT) {
            String reason = world.depotBuildFailure(selected, direction);
            if (reason != null) {
                world.message = reason;
                refresh();
                return;
            }
        }
        world.submitAction(selected.id, action);
        world.tick();
        refresh();
    }

    private void advanceWait() {
        if (paused || world.gameOver) return;
        GameObject selected = world.getObject(selectedObjectId);
        if (selected == null || !world.isHumanControlled(selected.id)) {
            world.message = "Warten/Aktion ist nur für eine menschlich gesteuerte Unit verfügbar.";
            refresh();
            return;
        }
        world.submitAction(selected.id, GameAction.waitAction());
        world.tick();
        refresh();
    }

    private void toggleEvolutionAutoFromKey() {
        evolutionAuto = !evolutionAuto;
        if (evolutionAutoButton != null)
            evolutionAutoButton.setText("Evolution AUTO: " + (evolutionAuto ? "EIN (T)" : "AUS (T)"));
        world.message = "Evolution-Automatik: " + (evolutionAuto ? "EIN" : "AUS");
        refresh();
    }

    private void toggleEvolutionAuto(JButton button) {
        evolutionAuto = !evolutionAuto;
        if (evolutionAutoButton != null)
            evolutionAutoButton.setText("Evolution AUTO: " + (evolutionAuto ? "EIN (T)" : "AUS (T)"));
        world.message = "Evolution-Automatik: " + (evolutionAuto ? "EIN" : "AUS");
        refresh();
        canvas.requestFocusInWindow();
    }

    private void changeEvolutionSpeed(int deltaMillis) {
        int delay = Math.max(speedSlider.getMinimum(), Math.min(speedSlider.getMaximum(), speedSlider.getValue() + deltaMillis));
        speedSlider.setValue(delay);
        evolutionTimer.setDelay(delay);
        world.message = "Simulationsintervall: " + delay + " ms pro Tick.";
        refresh();
    }

    private void cycleMutationPressure() {
        double current = world.mutationPressure;
        world.mutationPressure = current < 0.75 ? 1.0 : current < 1.5 ? 2.0 : 0.5;
        mutationPressureButton.setText(String.format(Locale.ROOT, "Mutationsdruck: %.1fx (M)", world.mutationPressure));
        world.message = String.format(Locale.ROOT, "Globaler Mutationsdruck: %.1fx", world.mutationPressure);
        refresh();
        canvas.requestFocusInWindow();
    }

    private void switchScenario() {
        world.evolutionMode = !world.evolutionMode;
        world.reset();
        selectedObjectId = world.firstControllableObjectId();
        playerMode = world.isHumanControlled(selectedObjectId);
        playerModeButton.setSelected(playerMode);
        if (world.evolutionMode) {
            genomeSummaryLabel.setText(world.genomePopulationSummary() + String.format(Locale.ROOT,
                    " | Mutationsdruck %.1fx | %s", world.mutationPressure, world.blueLineageObjective()));
        } else {
            genomeSummaryLabel.setText("INPUT → OPERATION → OUTPUT | Vererbbare Sensor- und Entscheidungsparameter im Evolutionsmodus");
        }
        playerModeButton.setText("Ausgewählte Unit: " + (playerMode ? "MENSCH" : "KI"));
        actionMode = ActionMode.MOVE;
        canvas.setActionMode(actionMode);
        canvas.setPlayerMode(false);
        canvas.resetCamera();
        world.message = world.evolutionMode
                ? "EVOLUTION: Nahrung sammeln, Zellteilung und Mutation beobachten. T = Auto an/aus."
                : "BOULDER DASH: Sammle Edelsteine und erreiche den Ausgang.";
        refresh();
        canvas.requestFocusInWindow();
    }

    private void toggleEnemyAlgorithm() {
        randomEnemyAlgorithm = !randomEnemyAlgorithm;
        world.setEnemyDecisionAlgorithm(randomEnemyAlgorithm);
        if (enemyAlgorithmButton != null) {
            enemyAlgorithmButton.setText(randomEnemyAlgorithm
                    ? "Gegner-KI: RANDOM LEGAL (I)" : "Gegner-KI: ADAPTIVE SOFTMAX (I)");
        }
        world.message = randomEnemyAlgorithm
                ? "Gegner-KI: RANDOM_LEGAL – gleichverteilte Auswahl aus legalen Aktionen."
                : "Gegner-KI: ADAPTIVE_SOFTMAX – Controller-Präferenz und gelernte Aktionswerte beeinflussen die Wahrscheinlichkeit; Exploration bleibt aktiv.";
        refresh();
        canvas.requestFocusInWindow();
    }

    private void togglePause() {
        paused = !paused;
        refresh();
    }

    private void restart() {
        world.reset();
        randomEnemyAlgorithm = false;
        if (enemyAlgorithmButton != null) enemyAlgorithmButton.setText("Gegner-KI: ADAPTIVE SOFTMAX (I)");
        selectedObjectId = world.firstControllableObjectId();
        playerMode = world.isHumanControlled(selectedObjectId);
        playerModeButton.setSelected(playerMode);
        paused = false;
        canvas.resetCamera();
        refresh();
        canvas.requestFocusInWindow();
    }

    private void selectObject(String id) {
        GameObject selected = world.getObject(id);
        if (selected != null) {
            selectedObjectId = id;
            playerMode = world.isHumanControlled(id);
            playerModeButton.setSelected(playerMode);
            refresh();
        }
    }

    private static boolean isControllable(GameObject object) {
        return object != null && object.alive
                && Set.of("PLAYER", "CREATURE", "HUNTER", "WANDERER").contains(object.type)
                && object.capabilities.contains(Capability.MOVE);
    }

    private void refresh() {
        statusLabel.setText("<html><b>" + escapeHtml(world.message) + "</b>"
                + (paused ? " &nbsp; | &nbsp; PAUSE" : "")
                + (world.gameOver ? " &nbsp; | &nbsp; SPIEL BEENDET" : "") + "</html>");
        GameObject player = world.getObject("player");
        double hp = player == null ? 0.0 : player.value("hp");
        double energy = player == null ? 0.0 : player.value("energy");
        if (world.evolutionMode) {
            stateLabel.setText(String.format(Locale.ROOT,
                    "EVOLUTION | Tick: %d | Pop: %d | Nahrung: %d | Births: %d | Deaths: %d | Gen: %d | Kills: %d | %s",
                    world.tick, world.creatureCount(), world.countType("FOOD"), world.births,
                    world.deaths, world.maxGeneration(), world.predationKills,
                    world.teamPopulationSummary() + " | " + world.blueLineageObjective()));
        } else {
            stateLabel.setText(String.format(Locale.ROOT,
                    "BOULDER DASH | Tick: %d | GameObjects: %d | 49 Optionen/Objekt | Edelsteine: %d/%d | Inventar: %.0f | Punkte: %d | HP: %.0f | Energie: %.1f",
                    world.tick, world.objects.size(), world.gemsCollected, world.gemsRequired,
                    player == null ? 0.0 : player.value("inventoryGems"), world.score, hp, energy));
        }
        GameObject selected = world.getObject(selectedObjectId);
        if (selected == null) {
            selectedObjectId = world.firstControllableObjectId();
            selected = world.getObject(selectedObjectId);
        }
        playerModeButton.setEnabled(selected != null && isControllable(selected));
        playerModeButton.setSelected(selected != null && world.isHumanControlled(selected.id));
        playerModeButton.setText("Ausgewählte Unit: "
                + (selected != null && world.isHumanControlled(selected.id) ? "MENSCH" : "KI"));
        if (selected != null) {
            if (world.evolutionMode && selected.type.equals("CREATURE")) {
                Genome genome = selected.genome == null ? Genome.defaults() : selected.genome;
                selectionLabel.setText(String.format(Locale.ROOT,
                        "<html><b>Zelle %s</b> | Team %d | Level %.0f | Gen %d | HP %.0f | Energie %.2f | Biomasse %.0f | Fitness %.2f | Teilungsschwelle %.2f | Reproduktionskosten %.2f | Eltern %s | Mutationen %d<br>" +
                        "INPUT: lokal %.2f | Reihe %.0f | Spalte %.0f | Radius %.0f | Unit-Sampling %.2f | Rauschen %.2f | gesehen %d<br>" +
                        "OPERATION: Nahrung %.2f | Beute %.2f | Gefahr %.2f | Teilung %.2f | Exploration %.2f | Mutation %.2f<br>" +
                        "Letzte Mutation: %s | Kontrolle: %s</html>",
                        selected.id, (int) selected.value("team") + 1, selected.value("unitLevel"),
                        (int) selected.value("generation"), selected.value("hp"), selected.value("energy"),
                        selected.value("inventoryResources"), selected.value("fitness"), selected.value("reproductionThreshold"),
                        selected.value("reproductionThreshold") * 0.42,
                        selected.parentId.isBlank() ? "—" : selected.parentId,
                        selected.mutationEvents, genome.localSense(), genome.rowRange(), genome.columnRange(),
                        genome.radiusRange(), genome.unitSampling(), genome.sensorNoise(), selected.lastPerceivedCount,
                        genome.foodSeek(), genome.preySeek(), genome.threatAvoid(), genome.reproductionDrive(),
                        genome.exploration(), genome.mutationRate(), selected.lastMutationSummary,
                        world.isHumanControlled(selected.id) ? "MENSCH" : "KI"));
            } else {
                selectionLabel.setText("Ausgewählt: " + selected.id + " [" + selected.type + "]"
                        + " | KI: " + selected.decisionAlgorithm.name()
                        + " | Fähigkeiten: " + selected.capabilities
                        + " | gelernte Züge: " + selected.learnedActionValues.size()
                        + " | Position: (" + selected.x + ", " + selected.y + ")"
                        + " | Kontrolle: " + (world.isHumanControlled(selected.id) ? "MENSCH" : "KI"));
            }
        } else {
            selectionLabel.setText("Kein Objekt ausgewählt.");
        }
        if (world.evolutionMode) {
            genomeSummaryLabel.setText(world.genomePopulationSummary() + String.format(Locale.ROOT,
                    " | Mutationsdruck %.1fx | %s", world.mutationPressure, world.blueLineageObjective()));
        } else {
            genomeSummaryLabel.setText("INPUT → OPERATION → OUTPUT | Vererbbare Sensor- und Entscheidungsparameter im Evolutionsmodus");
        }
        playerMode = selected != null && world.isHumanControlled(selected.id);
        playerModeButton.setSelected(playerMode);
        playerModeButton.setText("Ausgewählte Unit: " + (playerMode ? "MENSCH" : "KI"));
        playerModeButton.setEnabled(isControllable(selected));
        canvas.setPlayerMode(false);
        actionLabel.setText("Aktion: " + actionMode.label + " | Aktionsraum: " + GameAction.ACTION_SPACE_SIZE);
        canvas.setSelectedObjectId(selectedObjectId);
        canvas.setPaused(paused);
        canvas.repaint();
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--self-test".equals(args[0])) {
            runSelfTests();
            return;
        }
        SwingUtilities.invokeLater(() -> new MatrixgameUniversalGridLab_v10_0().setVisible(true));
    }

    // ---------------------------------------------------------------------
    // 1) Universal action model
    // ---------------------------------------------------------------------

    enum Direction {
        UP(0, -1, "gridY", -1),
        DOWN(0, 1, "gridY", 1),
        LEFT(-1, 0, "gridX", -1),
        RIGHT(1, 0, "gridX", 1);

        final int dx;
        final int dy;
        final String variable;
        final int delta;

        Direction(int dx, int dy, String variable, int delta) {
            this.dx = dx;
            this.dy = dy;
            this.variable = variable;
            this.delta = delta;
        }
    }

    /** Human-selectable operation; Q/E cycles through these modes. */
    enum ActionMode {
        MOVE("MOVE", null), DIG("DIG", InteractionType.DIG), PUSH("PUSH", InteractionType.PUSH),
        ATTACK("ATTACK", InteractionType.ATTACK), COLLECT("COLLECT", InteractionType.COLLECT),
        DROP("DROP", InteractionType.DROP), BUILD("BUILD", InteractionType.BUILD),
        BUILD_DEPOT("BUILD_DEPOT", InteractionType.BUILD_DEPOT),
        DEPOSIT("DEPOSIT", InteractionType.DEPOSIT), PRODUCE("PRODUCE", InteractionType.PRODUCE),
        USE("USE", InteractionType.USE), REPRODUCE("REPRODUCE", InteractionType.REPRODUCE);
        final String label;
        final InteractionType interactionType;
        ActionMode(String label, InteractionType interactionType) {
            this.label = label;
            this.interactionType = interactionType;
        }
    }

    enum InteractionType { DIG, PUSH, ATTACK, COLLECT, DROP, BUILD, BUILD_DEPOT, DEPOSIT, PRODUCE, USE, REPRODUCE }

    enum Capability {
        MOVE, DIG, PUSH, ATTACK, COLLECT, DROP, BUILD, DEPOSIT, PRODUCE,
        DIGGABLE, PUSHABLE, ATTACKABLE, COLLECTABLE, STACKABLE, FALLS, REPRODUCE
    }

    /** Base operation; INTERACT is refined by a typed InteractionType. */
    enum ActionKind { MOVE, INTERACT, WAIT }

    record GameAction(ActionKind kind, Direction direction, InteractionType interactionType) {
        GameAction {
            Objects.requireNonNull(kind, "kind");
            if (kind == ActionKind.WAIT && (direction != null || interactionType != null))
                throw new IllegalArgumentException("WAIT has no direction or interaction type");
            if (kind == ActionKind.MOVE && (direction == null || interactionType != null))
                throw new IllegalArgumentException("MOVE requires a direction and no interaction type");
            if (kind == ActionKind.INTERACT && (direction == null || interactionType == null))
                throw new IllegalArgumentException("INTERACT requires direction and interaction type");
        }
        static GameAction move(Direction direction) {
            return new GameAction(ActionKind.MOVE, Objects.requireNonNull(direction), null);
        }
        static GameAction interact(Direction direction, InteractionType type) {
            return new GameAction(ActionKind.INTERACT, Objects.requireNonNull(direction), Objects.requireNonNull(type));
        }
        /** Compatibility convenience: generic USE, for custom registered interactions. */
        static GameAction interact(Direction direction) { return interact(direction, InteractionType.USE); }
        static GameAction waitAction() { return new GameAction(ActionKind.WAIT, null, null); }
        static GameAction fromMode(ActionMode mode, Direction direction) {
            return mode == ActionMode.MOVE ? move(direction) : interact(direction, mode.interactionType);
        }
        String key() {
            return kind == ActionKind.WAIT ? "WAIT"
                    : kind == ActionKind.MOVE ? "MOVE_" + direction
                    : interactionType + "_" + direction;
        }
        /** Fixed universal vocabulary: WAIT + four MOVE + eight directional interaction types. */
        static final int ACTION_SPACE_SIZE = 1 + 4 + 4 * InteractionType.values().length;
        static List<GameAction> allActionOptions() {
            List<GameAction> options = new ArrayList<>(ACTION_SPACE_SIZE);
            options.add(waitAction());
            for (Direction direction : Direction.values()) {
                options.add(move(direction));
                for (InteractionType type : InteractionType.values()) options.add(interact(direction, type));
            }
            return List.copyOf(options);
        }
    }

    /**
     * Heritable policy genome. Team identity and colour are intentionally not genes: they
     * remain stable across asexual cell division. Sensor genes shape INPUT; operation genes
     * shape action scores; mutationRate controls per-locus variation.
     */
    record Genome(
            double localSense,       // 0..1: cardinal adjacent-cell sensor activation
            double rowRange,         // 0 disables row scan; otherwise maximum |dx| on same y
            double columnRange,      // 0 disables column scan; otherwise maximum |dy| on same x
            double radiusRange,      // 0 disables Manhattan-radius sensor
            double unitSampling,     // 0..1: fraction of sensed units sampled
            double sensorNoise,      // 0..0.45: probability of missing a sensed object
            double foodSeek,         // operation weight: collect food / move toward food
            double preySeek,         // operation weight: approach and attack weaker opponents
            double threatAvoid,      // operation weight: avoid stronger opponents
            double reproductionDrive,// operation weight: reproduce when energy allows
            double exploration,      // operation weight: prefer open/unvisited-looking space
            double mutationRate) {   // per-locus mutation probability before global multiplier

        static Genome defaults() {
            return new Genome(0.8, 6.0, 6.0, 8.0, 0.85, 0.04,
                    1.0, 0.8, 0.9, 1.0, 0.35, 0.10);
        }

        static Genome random(Random random) {
            Objects.requireNonNull(random);
            return new Genome(
                    between(random, 0.35, 1.0),
                    between(random, 0.0, 10.0),
                    between(random, 0.0, 10.0),
                    between(random, 3.0, 12.0),
                    between(random, 0.35, 1.0),
                    between(random, 0.0, 0.12),
                    between(random, 0.55, 1.55),
                    between(random, 0.25, 1.45),
                    between(random, 0.25, 1.55),
                    between(random, 0.55, 1.55),
                    between(random, 0.05, 0.95),
                    between(random, 0.06, 0.14));
        }

        GenomeMutation mutate(Random random, double globalPressure) {
            Objects.requireNonNull(random);
            double rate = clamp(mutationRate * globalPressure, 0.0, 0.95);
            List<String> changed = new ArrayList<>();
            double local = mutateGene("localSense", localSense, 0.18, 0.0, 1.0, rate, random, changed);
            double row = mutateGene("rowRange", rowRange, 2.0, 0.0, 16.0, rate, random, changed);
            double column = mutateGene("columnRange", columnRange, 2.0, 0.0, 16.0, rate, random, changed);
            double radius = mutateGene("radiusRange", radiusRange, 2.0, 0.0, 18.0, rate, random, changed);
            double sampling = mutateGene("unitSampling", unitSampling, 0.15, 0.10, 1.0, rate, random, changed);
            double noise = mutateGene("sensorNoise", sensorNoise, 0.06, 0.0, 0.45, rate, random, changed);
            double food = mutateGene("foodSeek", foodSeek, 0.22, 0.0, 2.5, rate, random, changed);
            double prey = mutateGene("preySeek", preySeek, 0.22, 0.0, 2.5, rate, random, changed);
            double threat = mutateGene("threatAvoid", threatAvoid, 0.22, 0.0, 2.5, rate, random, changed);
            double reproduce = mutateGene("reproductionDrive", reproductionDrive, 0.22, 0.0, 2.5, rate, random, changed);
            double explore = mutateGene("exploration", exploration, 0.18, 0.0, 1.5, rate, random, changed);
            double nextMutationRate = mutateGene("mutationRate", mutationRate, 0.025, 0.01, 0.35, rate, random, changed);
            Genome child = new Genome(local, row, column, radius, sampling, noise, food, prey, threat,
                    reproduce, explore, nextMutationRate);
            return new GenomeMutation(child, List.copyOf(changed));
        }

        private static double mutateGene(String name, double value, double magnitude, double min, double max,
                                         double probability, Random random, List<String> changed) {
            if (random.nextDouble() >= probability) return value;
            double next = clamp(value + between(random, -magnitude, magnitude), min, max);
            if (Math.abs(next - value) > 1.0e-12) changed.add(name);
            return next;
        }

        private static double between(Random random, double min, double max) {
            return min + random.nextDouble() * (max - min);
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        String sensorMode() {
            List<String> modes = new ArrayList<>();
            if (localSense >= 0.35) modes.add("LOCAL");
            if (rowRange >= 1.0) modes.add("ROW");
            if (columnRange >= 1.0) modes.add("COLUMN");
            if (radiusRange >= 1.0) modes.add("RADIUS");
            return modes.isEmpty() ? "BLIND" : String.join("+", modes);
        }

        String operationMode() {
            double[] weights = {foodSeek, preySeek, threatAvoid, reproductionDrive, exploration};
            String[] labels = {"FOOD", "PREY", "AVOID", "REPRODUCE", "EXPLORE"};
            int best = 0;
            for (int i = 1; i < weights.length; i++) if (weights[i] > weights[best]) best = i;
            return labels[best];
        }
    }

    record GenomeMutation(Genome genome, List<String> changedGenes) {
        GenomeMutation {
            Objects.requireNonNull(genome);
            changedGenes = List.copyOf(changedGenes);
        }
        int mutationCount() { return changedGenes.size(); }
        String summary() { return changedGenes.isEmpty() ? "keine" : String.join(",", changedGenes); }
    }

    interface ObjectController {
        GameAction chooseAction(Perception perception);
        String name();
    }

    interface InteractionRule {
        String apply(GameWorld world, GameObject actor, GameObject target, Direction direction);
    }

    record InteractionKey(String actorType, String targetType, InteractionType interactionType) { }

    static final class GameObject {
        final String id;
        final String type;
        final String name;
        final String glyph;
        final Color color;
        final boolean blocksMovement;
        final int initiative;
        final Map<String, Double> variables = new LinkedHashMap<>();
        final EnumSet<Capability> capabilities = EnumSet.noneOf(Capability.class);
        final Map<String, Double> learnedActionValues = new LinkedHashMap<>();
        final Map<String, Integer> learnedActionVisits = new LinkedHashMap<>();
        ObjectController controller;
        DecisionAlgorithm decisionAlgorithm;
        String parentId = "";
        Genome genome;
        int mutationEvents;
        String lastMutationSummary = "—";
        int lastPerceivedCount;
        int x;
        int y;
        boolean alive = true;

        GameObject(String id, String type, String name, String glyph, Color color,
                   int x, int y, boolean blocksMovement, int initiative, ObjectController controller) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.glyph = glyph;
            this.color = color;
            this.x = x;
            this.y = y;
            this.blocksMovement = blocksMovement;
            this.initiative = initiative;
            this.controller = controller;
            this.decisionAlgorithm = new GreedyDecisionAlgorithm();
            configureDefaultCapabilities(type);
            variables.put("hp", 1.0);
            variables.put("maxHp", 1.0);
            variables.put("energy", 100.0);
            variables.put("motivation", 50.0);
            variables.put("cooperation", 50.0);
            variables.put("stress", 0.0);
            variables.put("score", 0.0);
            variables.put("inventoryGems", 0.0);
            variables.put("storedGems", 0.0);
            variables.put("storageCapacity", 0.0);
            variables.put("inventoryResources", 0.0);
            variables.put("storedResources", 0.0);
            variables.put("resourceCapacity", 0.0);
            variables.put("unitLevel", 1.0);
            variables.put("buildingLevel", 0.0);
            variables.put("buildingDivisions", 0.0);
            variables.put("resourcesDelivered", 0.0);
            variables.put("eliteUnitsProduced", 0.0);
            variables.put("attackable", 0.0);
            variables.put("attackDamage", 1.0);
            variables.put("diggable", 0.0);
            variables.put("pushable", 0.0);
            variables.put("stack", 1.0);
            variables.put("visionRange", 6.0);
            variables.put("team", 0.0);
            variables.put("generation", 0.0);
            variables.put("age", 0.0);
            variables.put("metabolism", 0.0);
            variables.put("aggression", 0.0);
            variables.put("foodEfficiency", 1.0);
            variables.put("reproductionThreshold", 0.0);
            variables.put("mutationRate", 0.08);
            variables.put("kills", 0.0);
            variables.put("fitness", 0.0);
            variables.put("mutationCount", 0.0);
            variables.put("foodValue", 3.5);
            if ("PLAYER".equals(type)) variables.put("attackable", 1.0);
            if ("DIRT".equals(type)) variables.put("diggable", 1.0);
            if ("ROCK".equals(type)) variables.put("pushable", 1.0);
            if ("GEM".equals(type)) variables.put("stack", 1.0);
            if ("HUNTER".equals(type) || "WANDERER".equals(type)) {
                variables.put("attackable", 1.0);
                variables.put("attackDamage", 20.0);
                variables.put("visionRange", 8.0);
            }
            syncPosition();
        }

        private void configureDefaultCapabilities(String objectType) {
            switch (objectType) {
                case "PLAYER" -> capabilities.addAll(EnumSet.of(Capability.MOVE, Capability.DIG,
                        Capability.PUSH, Capability.ATTACK, Capability.COLLECT, Capability.DROP, Capability.BUILD,
                        Capability.DEPOSIT, Capability.PRODUCE, Capability.ATTACKABLE));
                case "DIRT" -> capabilities.add(Capability.DIGGABLE);
                case "ROCK" -> capabilities.addAll(EnumSet.of(Capability.MOVE, Capability.PUSHABLE, Capability.FALLS));
                case "GEM" -> capabilities.addAll(EnumSet.of(Capability.COLLECTABLE, Capability.STACKABLE));
                case "HUNTER", "WANDERER" -> capabilities.addAll(EnumSet.of(Capability.MOVE,
                        Capability.ATTACK, Capability.COLLECT, Capability.BUILD, Capability.DEPOSIT,
                        Capability.PRODUCE, Capability.ATTACKABLE));
                case "CREATURE" -> capabilities.addAll(EnumSet.of(Capability.MOVE, Capability.ATTACK,
                        Capability.COLLECT, Capability.BUILD, Capability.DEPOSIT, Capability.PRODUCE,
                        Capability.ATTACKABLE, Capability.REPRODUCE));
                case "BRUISER" -> capabilities.addAll(EnumSet.of(Capability.MOVE, Capability.ATTACK,
                        Capability.COLLECT, Capability.BUILD, Capability.DEPOSIT, Capability.PRODUCE,
                        Capability.ATTACKABLE));
                case "FOOD" -> capabilities.addAll(EnumSet.of(Capability.COLLECTABLE, Capability.STACKABLE));
                case "BUILDING" -> capabilities.addAll(EnumSet.of(
                        Capability.PRODUCE, Capability.REPRODUCE, Capability.ATTACKABLE));
                default -> { }
            }
        }
        boolean has(Capability capability) { return capabilities.contains(capability); }
        void grantCapability(Capability capability) { capabilities.add(Objects.requireNonNull(capability)); }
        void revokeCapability(Capability capability) { capabilities.remove(Objects.requireNonNull(capability)); }
        double value(String key) { return variables.getOrDefault(key, 0.0); }
        void set(String key, double value) { variables.put(key, value); }
        void syncPosition() { set("gridX", x); set("gridY", y); }
        void learn(GameAction action, double reward) {
            String key = action.key();
            double oldValue = learnedActionValues.getOrDefault(key, 0.0);
            double alpha = 0.20;
            learnedActionValues.put(key, oldValue + alpha * (reward - oldValue));
            learnedActionVisits.merge(key, 1, Integer::sum);
        }
        List<GameAction> actionOptionsForTick() { return GameAction.allActionOptions(); }
    }

    record ObjectView(String id, String type, String name, int x, int y,
                      boolean blocksMovement, boolean alive, double hp,
                      Map<String, Double> variables, Set<Capability> capabilities,
                      Map<String, Double> learnedActionValues, Genome genome, int mutationEvents) {
        double value(String key) { return variables.getOrDefault(key, 0.0); }
        boolean has(Capability capability) { return capabilities.contains(capability); }
    }

    static final class WorldSnapshot {
        final int width;
        final int height;
        final int tick;
        final List<ObjectView> objects;
        final Map<String, ObjectView> byId;
        private final Map<Long, List<ObjectView>> byCell;

        WorldSnapshot(int width, int height, int tick, List<ObjectView> objects) {
            this.width = width;
            this.height = height;
            this.tick = tick;
            this.objects = List.copyOf(objects);
            Map<String, ObjectView> idIndex = new LinkedHashMap<>();
            Map<Long, List<ObjectView>> cellIndex = new HashMap<>();
            for (ObjectView object : objects) {
                idIndex.put(object.id(), object);
                if (object.alive()) {
                    cellIndex.computeIfAbsent(cellKey(object.x(), object.y()), ignored -> new ArrayList<>()).add(object);
                }
            }
            Map<Long, List<ObjectView>> frozenCells = new HashMap<>();
            cellIndex.forEach((key, value) -> frozenCells.put(key, List.copyOf(value)));
            this.byId = Collections.unmodifiableMap(idIndex);
            this.byCell = Collections.unmodifiableMap(frozenCells);
        }

        private static long cellKey(int x, int y) {
            return (((long) x) << 32) ^ (y & 0xffffffffL);
        }

        ObjectView get(String id) { return byId.get(id); }

        List<ObjectView> at(int x, int y) {
            return byCell.getOrDefault(cellKey(x, y), List.of());
        }

        void addCell(int x, int y, Map<String, ObjectView> destination) {
            for (ObjectView object : at(x, y)) destination.putIfAbsent(object.id(), object);
        }

        void addManhattan(int centerX, int centerY, int radius, Map<String, ObjectView> destination) {
            if (radius < 0) return;
            int minX = Math.max(0, centerX - radius);
            int maxX = Math.min(width - 1, centerX + radius);
            int minY = Math.max(0, centerY - radius);
            int maxY = Math.min(height - 1, centerY + radius);
            for (int y = minY; y <= maxY; y++) {
                int remaining = radius - Math.abs(y - centerY);
                for (int x = Math.max(minX, centerX - remaining);
                     x <= Math.min(maxX, centerX + remaining); x++) {
                    addCell(x, y, destination);
                }
            }
        }

        boolean isBlocked(int x, int y, String ignoreId) {
            if (x < 0 || y < 0 || x >= width || y >= height) return true;
            for (ObjectView object : at(x, y)) {
                if (object.blocksMovement() && !object.id().equals(ignoreId)) return true;
            }
            return false;
        }
    }

    /**
     * Limited observation supplied to a GameObject controller. The simulation core keeps
     * the full world; the controller receives only objects inside its vision radius.
     */
    record Perception(ObjectView self, List<ObjectView> visibleObjects,
                      int worldWidth, int worldHeight, int visionRadius) {
        Perception {
            Objects.requireNonNull(self, "self");
            visibleObjects = List.copyOf(visibleObjects);
            if (visionRadius < 1) throw new IllegalArgumentException("visionRadius must be positive");
        }
        List<ObjectView> at(int x, int y) {
            return visibleObjects.stream().filter(o -> o.alive() && o.x() == x && o.y() == y).toList();
        }
        boolean isVisible(int x, int y) {
            return x >= 0 && y >= 0 && x < worldWidth && y < worldHeight
                    && Math.abs(x - self.x()) + Math.abs(y - self.y()) <= visionRadius;
        }
        boolean isBlocked(int x, int y, String ignoreId) {
            if (!isVisible(x, y)) return true;
            for (ObjectView object : at(x, y)) {
                if (object.blocksMovement() && !object.id().equals(ignoreId)) return true;
            }
            return false;
        }
    }

    /** Input to the decision operation: local perception, fixed vocabulary and legal-action mask. */
    record DecisionContext(Perception perception, ObjectView self,
                           List<GameAction> actionSpace, List<GameAction> legalOptions) {
        DecisionContext {
            Objects.requireNonNull(perception, "perception");
            Objects.requireNonNull(self, "self");
            if (!perception.self().id().equals(self.id()))
                throw new IllegalArgumentException("Perception and self must describe the same object");
            actionSpace = List.copyOf(actionSpace);
            legalOptions = List.copyOf(legalOptions);
            if (actionSpace.size() != GameAction.ACTION_SPACE_SIZE) throw new IllegalArgumentException("Canonical action space size mismatch");
            if (!actionSpace.containsAll(legalOptions)) throw new IllegalArgumentException("Legal options must belong to canonical action space");
        }
    }

    interface ActionEvaluator {
        double score(DecisionContext context, GameAction action);
        String name();
    }

    /** Selects one action from the same finite universal action vocabulary for every GameObject. */
    interface DecisionAlgorithm {
        GameAction chooseAction(DecisionContext context, ActionEvaluator evaluator);
        String name();
    }

    // ---------------------------------------------------------------------
    // 2) Generic world, tick loop, conflict resolution and interaction rules
    // ---------------------------------------------------------------------

    static final class GameWorld {
        final int width;
        final int height;
        final long seed;
        final List<GameObject> objects = new ArrayList<>();
        final Map<String, GameAction> queuedActions = new LinkedHashMap<>();
        final Set<String> humanControlledIds = new HashSet<>();
        final Map<InteractionKey, InteractionRule> interactionRules = new LinkedHashMap<>();
        final List<int[]> populationHistory = new ArrayList<>();
        final Deque<String> eventLog = new ArrayDeque<>();
        final Random environmentRandom;
        boolean evolutionMode;
        int births;
        int deaths;
        int starvationDeaths;
        int foodCollected;
        int foodSpawned;
        int predationKills;
        int tick;
        int score;
        int gemsCollected;
        int gemsRequired = 5;
        int nextId = 1;
        boolean gameOver;
        boolean won;
        boolean randomEnemyDecision;
        double mutationPressure = 1.0;
        String message = "Wähle ein Objekt und sende pro Tick eine Aktion.";

        private GameWorld(long seed, int width, int height) {
            if (width < 8 || height < 8) throw new IllegalArgumentException("World must be at least 8x8");
            this.seed = seed;
            this.width = width;
            this.height = height;
            this.environmentRandom = new Random(seed ^ 0x51A7E5L);
            registerDefaultRules();
        }

        private void logEvent(String category, String detail) {
            String entry = String.format(Locale.ROOT, "[TICK %d][%s] %s", tick, category, detail);
            eventLog.addLast(entry);
            while (eventLog.size() > 250) eventLog.removeFirst();
            System.out.println(entry);
        }

        static GameWorld createBoulderScenario(long seed, int width, int height) {
            GameWorld world = new GameWorld(seed, width, height);
            world.evolutionMode = false;
            world.reset();
            return world;
        }

        static GameWorld createEvolutionScenario(long seed, int width, int height) {
            GameWorld world = new GameWorld(seed, width, height);
            world.evolutionMode = true;
            world.reset();
            return world;
        }

        String firstControllableObjectId() {
            if (evolutionMode) {
                return objects.stream().filter(o -> o.alive && o.type.equals("CREATURE"))
                        .map(o -> o.id).findFirst().orElse("");
            }
            return getObject("player") != null ? "player" : "";
        }

        int countType(String type) {
            int count = 0;
            for (GameObject object : objects) if (object.alive && object.type.equals(type)) count++;
            return count;
        }

        int creatureCount() { return countType("CREATURE"); }

        int maxGeneration() {
            int max = 0;
            for (GameObject object : objects) if (object.alive && object.type.equals("CREATURE"))
                max = Math.max(max, (int) object.value("generation"));
            return max;
        }

        int teamMaxGeneration(int team) {
            int max = 0;
            for (GameObject object : objects) {
                if (object.alive && object.type.equals("CREATURE") && (int) object.value("team") == team)
                    max = Math.max(max, (int) object.value("generation"));
            }
            return max;
        }

        String blueLineageObjective() {
            int population = teamPopulationCount(0);
            int generation = teamMaxGeneration(0);
            if (population >= 12 && generation >= 8) return "ZIEL ERREICHT: Blaue Linie stabil (12+ Units, Gen 8+)";
            return String.format(Locale.ROOT, "LEVELZIEL BLAU: Pop %d/12 | Generation %d/8", population, generation);
        }

        int teamPopulationCount(int team) {
            int count = 0;
            for (GameObject object : objects)
                if (object.alive && object.type.equals("CREATURE") && (int) object.value("team") == team) count++;
            return count;
        }

        String teamPopulationSummary() {
            int[] counts = new int[TEAM_COUNT];
            for (GameObject object : objects) {
                if (object.alive && object.type.equals("CREATURE")) {
                    int team = (int) object.value("team");
                    if (team >= 0 && team < counts.length) counts[team]++;
                }
            }
            StringJoiner summary = new StringJoiner(" ");
            for (int i = 0; i < counts.length; i++) summary.add("T" + (i + 1) + ":" + counts[i]);
            return summary.toString();
        }

        String genomePopulationSummary() {
            List<GameObject> cells = objects.stream()
                    .filter(o -> o.alive && o.type.equals("CREATURE") && o.genome != null).toList();
            if (cells.isEmpty()) return "Keine lebenden Genome";
            double food = cells.stream().mapToDouble(o -> o.genome.foodSeek()).average().orElse(0.0);
            double prey = cells.stream().mapToDouble(o -> o.genome.preySeek()).average().orElse(0.0);
            double avoid = cells.stream().mapToDouble(o -> o.genome.threatAvoid()).average().orElse(0.0);
            double radius = cells.stream().mapToDouble(o -> o.genome.radiusRange()).average().orElse(0.0);
            double mutate = cells.stream().mapToDouble(o -> o.genome.mutationRate()).average().orElse(0.0);
            double fitness = cells.stream().mapToDouble(o -> o.value("fitness")).average().orElse(0.0);
            long local = cells.stream().filter(o -> o.genome.localSense() >= 0.35).count();
            long row = cells.stream().filter(o -> o.genome.rowRange() >= 1.0).count();
            long column = cells.stream().filter(o -> o.genome.columnRange() >= 1.0).count();
            long radial = cells.stream().filter(o -> o.genome.radiusRange() >= 1.0).count();
            long diversity = cells.stream().map(o -> o.genome).distinct().count();
            return String.format(Locale.ROOT,
                    "GENOME n=%d unique=%d | Sensor L/R/C/D=%d/%d/%d/%d | mean R=%.1f food=%.2f prey=%.2f avoid=%.2f mut=%.1f%% fit=%.2f",
                    cells.size(), diversity, local, row, column, radial, radius, food, prey, avoid, mutate * 100.0, fitness);
        }

        void recordPopulationSample() {
            if (!evolutionMode) return;
            int[] sample = new int[TEAM_COUNT + 1];
            sample[0] = tick;
            for (GameObject object : objects) {
                if (!object.alive || !object.type.equals("CREATURE")) continue;
                int team = (int) object.value("team");
                if (team >= 0 && team < TEAM_COUNT) sample[team + 1]++;
            }
            populationHistory.add(sample);
            if (populationHistory.size() > 240) populationHistory.remove(0);
        }

        void reset() {
            if (evolutionMode) resetEvolution();
            else resetBoulder();
        }

        private void resetBoulder() {
            objects.clear();
            queuedActions.clear();
            humanControlledIds.clear();
            populationHistory.clear();
            eventLog.clear();
            environmentRandom.setSeed(seed ^ 0x51A7E5L);
            births = deaths = starvationDeaths = foodCollected = foodSpawned = predationKills = 0;
            tick = 0;
            score = 0;
            gemsCollected = 0;
            gameOver = false;
            won = false;
            randomEnemyDecision = false;
            nextId = 1;
            message = "Sammle Edelsteine, interagiere mit Objekten und erreiche den Ausgang.";
            Random random = new Random(seed);

            // Boundary walls and diggable soil are ordinary GameObjects too.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean border = x == 0 || y == 0 || x == width - 1 || y == height - 1;
                    if (border) addObject(object("wall", "WALL", "Grenze", "■", new Color(82, 100, 118), x, y, true, 0, new IdleController()));
                    else if (random.nextDouble() < 0.34) addObject(object("dirt", "DIRT", "Erde", "▪", new Color(139, 88, 53), x, y, true, 0, new IdleController()));
                }
            }

            GameObject player = objectWithStats("player", "PLAYER", "Spieler", "●", new Color(255, 213, 74),
                    1, 1, true, 100, new IdleController(), 100, 100);
            humanControlledIds.add("player");
            player.set("motivation", 85.0);
            player.set("cooperation", 65.0);
            player.set("stress", 5.0);
            player.set("inventoryGems", 0.0);
            player.set("attackDamage", 1.0);
            addObject(player);
            clearCell(1, 1);

            for (int i = 0; i < 27; i++) {
                int[] p = randomFreeCell(random, 2, width - 3, 2, height - 3);
                if (p != null) addObject(object("rock", "ROCK", "Felsen", "●", new Color(160, 174, 190), p[0], p[1], true, 70, new GravityController()));
            }
            for (int i = 0; i < 15; i++) {
                int[] p = randomFreeCell(random, 2, width - 3, 2, height - 3);
                if (p != null) addObject(object("gem", "GEM", "Edelstein", "◆", new Color(40, 205, 220), p[0], p[1], false, 5, new IdleController()));
            }
            clearCell(1, 2); clearCell(1, 3); clearCell(2, 1); clearCell(3, 1); clearCell(4, 1);
            clearCell(width - 2, height - 2);
            addObject(object("exit", "EXIT", "Ausgang", "▣", new Color(72, 176, 104),
                    width - 2, height - 2, false, 1, new IdleController()));
            clearCell(width - 3, 1);
            GameObject initialHunter = object("hunter", "HUNTER", "Hybrid-Verfolger", "◆",
                    new Color(234, 85, 93), width - 3, 1, true, 60, new HybridHunterController(6));
            initialHunter.set("team", 1.0);
            initialHunter.set("hp", 3.0);
            initialHunter.set("maxHp", 3.0);
            addObject(initialHunter);
            message = "Szenario bereit: 49 feste Aktionen je Objekt. Q/E wechseln die 12 Modi. C=BUILD_DEPOT, X=DEPOSIT, Z=PRODUCE.";
        }

        private void resetEvolution() {
            objects.clear();
            queuedActions.clear();
            humanControlledIds.clear();
            populationHistory.clear();
            eventLog.clear();
            environmentRandom.setSeed(seed ^ 0x51A7E5L);
            tick = score = gemsCollected = births = deaths = starvationDeaths = foodCollected = foodSpawned = predationKills = 0;
            gameOver = false;
            won = false;
            randomEnemyDecision = false;
            nextId = 1;
            message = "EVOLUTION LAB: Sechs Teams sammeln Nahrung, liefern Biomasse an Zitadellen und erzeugen Level-2-Units.";
            Random random = new Random(seed);

            // Larger arena: walls are ordinary objects; open cells form the ecosystem.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        addObject(object("wall", "WALL", "Grenze", "■", new Color(73, 91, 110),
                                x, y, true, 0, new IdleController()));
                    } else if (random.nextDouble() < 0.035) {
                        addObject(object("dirt", "DIRT", "Felsboden", "▪", new Color(91, 76, 63),
                                x, y, true, 0, new IdleController()));
                    }
                }
            }

            Color[] teamColors = {
                    new Color(70, 165, 245), new Color(238, 94, 102),
                    new Color(86, 202, 135), new Color(190, 125, 235),
                    new Color(245, 190, 65), new Color(65, 210, 205)
            };
            int[][] zones = {
                    {6, 6}, {width / 2, 6}, {width - 7, 6},
                    {6, height - 7}, {width / 2, height - 7}, {width - 7, height - 7}
            };
            for (int team = 0; team < teamColors.length; team++) {
                for (int i = 0; i < START_UNITS_PER_TEAM; i++) {
                    int[] pos = randomFreeCell(random,
                            Math.max(1, zones[team][0] - 3), Math.min(width - 2, zones[team][0] + 3),
                            Math.max(1, zones[team][1] - 3), Math.min(height - 2, zones[team][1] + 3));
                    if (pos == null) continue;
                    GameObject cell = createCreature("cell", "CREATURE", "Zelle Team " + (team + 1),
                            teamColors[team], pos[0], pos[1], team, 0, "", random, 8.0);
                    addObject(cell);
                }
            }
            // Distributed food field for six teams; later food regenerates stochastically.
            spawnFood(300);
            recordPopulationSample();
            message = "LEVEL 1 – BLAUE LINIE: 12 lebende blaue Units UND mindestens eine lebende blaue Gen-8-Unit. Blaue Auslöschung = Niederlage.";
        }

        private GameObject createCreature(String prefix, String type, String name, Color color,
                                          int x, int y, int team, int generation, String parentId,
                                          Random random, double energy) {
            GameObject cell = object(prefix, type, name, "●", color, x, y, true, 40, new EvolutionController());
            cell.parentId = parentId == null ? "" : parentId;
            cell.set("team", team);
            cell.set("generation", generation);
            cell.set("energy", energy);
            cell.set("hp", 3.0);
            cell.set("maxHp", 3.0);
            cell.set("metabolism", 0.035 + random.nextDouble() * 0.025);
            cell.set("aggression", 0.25 + random.nextDouble() * 0.65);
            cell.set("foodEfficiency", 0.85 + random.nextDouble() * 0.30);
            cell.set("attackDamage", 0.8 + random.nextDouble() * 0.9);
            cell.set("reproductionThreshold", 12.0 + random.nextDouble() * 2.0);
            cell.set("mutationRate", 0.10);
            cell.genome = Genome.random(random);
            cell.set("visionRange", cell.genome.radiusRange());
            cell.set("food", energy);
            cell.set("age", 0.0);
            cell.set("kills", 0.0);
            cell.decisionAlgorithm = new AdaptiveSoftmaxDecisionAlgorithm(0.16, 0.72,
                    seed ^ cell.id.hashCode() ^ (long) x * 31L ^ y);
            return cell;
        }

        void spawnFood(int amount) {
            Random random = evolutionMode ? environmentRandom : new Random(seed + tick + amount);
            int spawned = 0;
            for (int i = 0; i < amount; i++) {
                if (evolutionMode && countType("FOOD") >= MAX_FOOD) break;
                int[] pos = randomFreeCell(random, 1, width - 2, 1, height - 2);
                if (pos == null) break;
                GameObject food = object("food", "FOOD", "Nahrung", "✦", new Color(105, 230, 120),
                        pos[0], pos[1], false, 1, new IdleController());
                food.set("stack", 1.0);
                food.set("foodValue", 3.5);
                addObject(food);
                spawned++;
            }
            foodSpawned += spawned;
            if (spawned > 0) message = "Nahrung erzeugt: +" + spawned + " Ressourcen.";
        }

        private void regenerateFood() {
            if (!evolutionMode || countType("FOOD") >= MAX_FOOD) return;
            // Five independent attempts per tick maintain a broad stochastic food field.
            for (int i = 0; i < 5; i++) {
                if (environmentRandom.nextDouble() < 0.38) spawnFood(1);
                if (countType("FOOD") >= MAX_FOOD) break;
            }
        }

        private void updateEvolutionLifeCycle() {
            if (!evolutionMode) return;
            for (GameObject cell : new ArrayList<>(objects)) {
                if (!cell.alive || !cell.type.equals("CREATURE")) continue;
                cell.set("age", cell.value("age") + 1.0);
                // A small survival contribution makes sustained viability measurable.
                cell.set("fitness", cell.value("fitness") + 0.01 + Math.max(0.0, cell.value("energy")) * 0.0005);
                double upkeep = cell.value("metabolism");
                cell.set("energy", cell.value("energy") - upkeep);
                cell.set("food", cell.value("energy"));
                if (cell.value("energy") <= 0.0) {
                    logEvent("STARVATION", cell.id + " [Team " + ((int) cell.value("team") + 1)
                            + ", Gen " + (int) cell.value("generation") + "] verhungert bei ("
                            + cell.x + "," + cell.y + "); Energie=" + cell.value("energy"));
                    removeObject(cell);
                    deaths++;
                    starvationDeaths++;
                }
            }
        }

        private void evaluateBlueLevelOutcome() {
            if (!evolutionMode || gameOver) return;
            int bluePopulation = teamPopulationCount(0);
            int blueGeneration = teamMaxGeneration(0);
            if (bluePopulation >= 12 && blueGeneration >= 8) {
                gameOver = true;
                won = true;
                message = "SIEG! Blaue Linie stabilisiert: mindestens 12 lebende Units und Generation 8 erreicht.";
            } else if (bluePopulation == 0) {
                gameOver = true;
                won = false;
                message = "NIEDERLAGE: Die blaue Linie ist ausgestorben. Neustart (R) für einen neuen Versuch.";
            }
        }

        private String divide(GameObject parent, Direction direction) {
            if (!evolutionMode || !parent.type.equals("CREATURE")) return "REPRODUCE: Nur Zellen können sich teilen.";
            if (creatureCount() >= MAX_EVOLUTION_POPULATION) return "REPRODUCE: Populationslimit von " + MAX_EVOLUTION_POPULATION + " erreicht.";
            double threshold = parent.value("reproductionThreshold");
            if (parent.value("energy") < threshold) return "REPRODUCE: Energievorrat unter Teilungsschwelle.";
            int x = parent.x + direction.dx;
            int y = parent.y + direction.dy;
            if (!isCompletelyEmpty(x, y, parent.id)) return "REPRODUCE: Nachbarfeld ist nicht frei.";

            double transfer = threshold * 0.42;
            parent.set("energy", parent.value("energy") - transfer);
            parent.set("food", parent.value("energy"));
            Random mutationRandom = environmentRandom;
            GameObject child = object("cell", "CREATURE", parent.name + " – Nachkomme", "●", parent.color,
                    x, y, true, 40, new EvolutionController());
            child.parentId = parent.id;
            int team = (int) parent.value("team");
            int generation = (int) parent.value("generation") + 1;
            child.set("team", team);
            child.set("generation", generation);
            child.set("unitLevel", parent.value("unitLevel"));
            child.set("energy", transfer);
            child.set("food", transfer);
            double inheritedLevel = Math.max(1.0, parent.value("unitLevel"));
            child.set("hp", inheritedLevel >= 2.0 ? 9.0 : 3.0);
            child.set("maxHp", inheritedLevel >= 2.0 ? 9.0 : 3.0);
            child.set("age", 0.0);
            child.set("kills", 0.0);
            child.set("mutationRate", parent.value("mutationRate"));
            double mutationRate = clamp(parent.value("mutationRate"), 0.0, 1.0);
            Genome parentGenome = parent.genome == null ? Genome.defaults() : parent.genome;
            GenomeMutation genomeMutation = parentGenome.mutate(mutationRandom, mutationPressure);
            child.genome = genomeMutation.genome();
            child.mutationEvents = parent.mutationEvents + genomeMutation.mutationCount();
            child.lastMutationSummary = genomeMutation.summary();
            child.set("mutationCount", genomeMutation.mutationCount());
            child.set("fitness", 0.0);
            child.set("visionRange", child.genome.radiusRange());
            child.set("metabolism", mutate(parent.value("metabolism"), 0.008, 0.012, 0.10, mutationRate, mutationRandom));
            child.set("aggression", mutate(parent.value("aggression"), 0.08, 0.0, 1.0, mutationRate, mutationRandom));
            child.set("foodEfficiency", mutate(parent.value("foodEfficiency"), 0.05, 0.65, 1.45, mutationRate, mutationRandom));
            child.set("attackDamage", mutate(parent.value("attackDamage"), 0.18, 0.4,
                    inheritedLevel >= 2.0 ? 4.5 : 2.5, mutationRate, mutationRandom));
            child.set("reproductionThreshold", mutate(parent.value("reproductionThreshold"), 0.8,
                    inheritedLevel >= 2.0 ? 14.0 : 8.0, inheritedLevel >= 2.0 ? 24.0 : 18.0,
                    mutationRate, mutationRandom));
            child.set("visionRange", Math.rint(mutate(parent.value("visionRange"), 1.0, 3.0, 14.0, mutationRate, mutationRandom)));
            child.decisionAlgorithm = new AdaptiveSoftmaxDecisionAlgorithm(0.16, 0.72,
                    seed ^ child.id.hashCode() ^ tick);
            addObject(child);
            births++;
            parent.set("fitness", parent.value("fitness") + 1.0);
            message = String.format(Locale.ROOT,
                    "ZELLTEILUNG: %s → %s | Team %d | Gen %d | %d Gen-Mutationen [%s] | Sensor %s | Policy %s",
                    parent.id, child.id, team + 1, generation, genomeMutation.mutationCount(),
                    genomeMutation.summary(), child.genome.sensorMode(), child.genome.operationMode());
            return message;
        }

        private double mutate(double value, double magnitude, double min, double max,
                              double mutationRate, Random random) {
            if (random.nextDouble() >= mutationRate) return value;
            return clamp(value + (random.nextDouble() * 2.0 - 1.0) * magnitude, min, max);
        }

        private int[] randomFreeCell(Random random, int minX, int maxX, int minY, int maxY) {
            for (int attempt = 0; attempt < 300; attempt++) {
                int x = minX + random.nextInt(Math.max(1, maxX - minX + 1));
                int y = minY + random.nextInt(Math.max(1, maxY - minY + 1));
                if (objectsAt(x, y).isEmpty()) return new int[]{x, y};
            }
            return null;
        }

        private void clearCell(int x, int y) {
            objects.removeIf(object -> object.x == x && object.y == y
                    && ("DIRT".equals(object.type) || "ROCK".equals(object.type) || "GEM".equals(object.type)));
        }

        private GameObject object(String idPrefix, String type, String name, String glyph, Color color,
                                  int x, int y, boolean blocks, int initiative, ObjectController controller) {
            String id = idPrefix + "-" + String.format(Locale.ROOT, "%04d", nextId++);
            GameObject created = new GameObject(id, type, name, glyph, color, x, y, blocks, initiative, controller);
            if (type.equals("HUNTER") || type.equals("WANDERER")) created.decisionAlgorithm = newEnemyAlgorithm(created);
            return created;
        }

        private GameObject objectWithStats(String id, String type, String name, String glyph, Color color,
                                           int x, int y, boolean blocks, int initiative,
                                           ObjectController controller, double hp, double energy) {
            GameObject object = new GameObject(id, type, name, glyph, color, x, y, blocks, initiative, controller);
            object.set("hp", hp);
            object.set("maxHp", hp);
            object.set("energy", energy);
            return object;
        }

        private DecisionAlgorithm newEnemyAlgorithm(GameObject object) {
            long objectSeed = seed ^ object.id.hashCode() ^ tick;
            return randomEnemyDecision
                    ? new RandomLegalDecisionAlgorithm(objectSeed)
                    : new AdaptiveSoftmaxDecisionAlgorithm(0.10, 0.80, objectSeed);
        }

        void addObject(GameObject object) {
            if (getObject(object.id) != null) throw new IllegalArgumentException("Duplicate GameObject id: " + object.id);
            if (!inside(object.x, object.y)) throw new IllegalArgumentException("Object outside world: " + object.id);
            objects.add(object);
            object.syncPosition();
        }

        GameObject getObject(String id) {
            for (GameObject object : objects) if (object.alive && object.id.equals(id)) return object;
            return null;
        }

        List<GameObject> objectsAt(int x, int y) {
            List<GameObject> found = new ArrayList<>();
            for (GameObject object : objects) {
                if (object.alive && object.x == x && object.y == y) found.add(object);
            }
            return found;
        }

        boolean isBlocked(int x, int y, String ignoreId) {
            if (!inside(x, y)) return true;
            for (GameObject object : objects) {
                if (object.alive && object.blocksMovement && !object.id.equals(ignoreId)
                        && object.x == x && object.y == y) return true;
            }
            return false;
        }

        boolean isCompletelyEmpty(int x, int y, String ignoreId) {
            if (!inside(x, y)) return false;
            for (GameObject object : objects) {
                if (object.alive && !object.id.equals(ignoreId) && object.x == x && object.y == y) return false;
            }
            return true;
        }

        boolean inside(int x, int y) { return x >= 0 && y >= 0 && x < width && y < height; }

        void submitAction(String objectId, GameAction action) {
            if (getObject(objectId) == null) return;
            queuedActions.put(objectId, Objects.requireNonNull(action));
        }

        void setHumanControlled(String objectId, boolean humanControlled) {
            if (getObject(objectId) == null) return;
            if (humanControlled) humanControlledIds.add(objectId);
            else humanControlledIds.remove(objectId);
        }

        boolean isHumanControlled(String objectId) {
            return humanControlledIds.contains(objectId) && getObject(objectId) != null;
        }

        WorldSnapshot snapshot() {
            List<ObjectView> views = new ArrayList<>();
            for (GameObject object : objects) {
                if (!object.alive) continue;
                views.add(new ObjectView(object.id, object.type, object.name, object.x, object.y,
                        object.blocksMovement, object.alive, object.value("hp"),
                        Collections.unmodifiableMap(new LinkedHashMap<>(object.variables)),
                        Collections.unmodifiableSet(EnumSet.copyOf(object.capabilities)),
                        Collections.unmodifiableMap(new LinkedHashMap<>(object.learnedActionValues)),
                        object.genome, object.mutationEvents));
            }
            return new WorldSnapshot(width, height, tick, views);
        }

        DecisionContext decisionContext(GameObject object, WorldSnapshot snapshot) {
            ObjectView self = snapshot.get(object.id);
            if (self == null) throw new IllegalArgumentException("Object is absent from snapshot: " + object.id);
            List<GameAction> all = GameAction.allActionOptions();
            List<GameAction> legal = new ArrayList<>();
            for (GameAction action : all) {
                if (action.kind() == ActionKind.WAIT) {
                    legal.add(action);
                } else if (action.kind() == ActionKind.MOVE) {
                    int nx = self.x() + action.direction().dx;
                    int ny = self.y() + action.direction().dy;
                    if (self.has(Capability.MOVE) && !snapshot.isBlocked(nx, ny, self.id())) legal.add(action);
                } else {
                    int nx = self.x() + action.direction().dx;
                    int ny = self.y() + action.direction().dy;
                    List<ObjectView> nearby = snapshot.at(nx, ny);
                    ObjectView target = nearby.stream()
                            .max(Comparator.comparing(ObjectView::blocksMovement)).orElse(null);
                    String targetType = target == null ? "EMPTY" : target.type();
                    InteractionKey key = new InteractionKey(self.type(), targetType, action.interactionType());
                    boolean hasRule = interactionRules.containsKey(key)
                            || interactionRules.containsKey(new InteractionKey("*", targetType, action.interactionType()))
                            || interactionRules.containsKey(new InteractionKey("*", "*", action.interactionType()));
                    if (hasRule && isInteractionAvailable(snapshot, self, target, nx, ny,
                                    action.direction(), action.interactionType())) {
                        legal.add(action);
                    }
                }
            }
            Genome genome = object.type.equals("CREATURE") && object.genome != null ? object.genome : null;
            int visionRadius = genome == null
                    ? (int) Math.max(1.0, Math.min(64.0, self.value("visionRange")))
                    : (int) Math.max(1.0, Math.min(64.0, Math.max(Math.max(genome.radiusRange(), genome.rowRange()), genome.columnRange())));
            // Query only the cells covered by this genome's sensor geometry. This avoids
            // scanning every wall, terrain tile and resource for every living cell each tick.
            Map<String, ObjectView> candidates = new LinkedHashMap<>();
            if (genome == null) {
                snapshot.addManhattan(self.x(), self.y(), visionRadius, candidates);
            } else {
                if (genome.localSense() >= 0.35) {
                    for (Direction direction : Direction.values())
                        snapshot.addCell(self.x() + direction.dx, self.y() + direction.dy, candidates);
                    snapshot.addCell(self.x(), self.y(), candidates);
                }
                int rowRange = (int) Math.floor(genome.rowRange());
                if (rowRange >= 1) {
                    for (int x = Math.max(0, self.x() - rowRange);
                         x <= Math.min(snapshot.width - 1, self.x() + rowRange); x++)
                        snapshot.addCell(x, self.y(), candidates);
                }
                int columnRange = (int) Math.floor(genome.columnRange());
                if (columnRange >= 1) {
                    for (int y = Math.max(0, self.y() - columnRange);
                         y <= Math.min(snapshot.height - 1, self.y() + columnRange); y++)
                        snapshot.addCell(self.x(), y, candidates);
                }
                int radialRange = (int) Math.floor(genome.radiusRange());
                if (radialRange >= 1) snapshot.addManhattan(self.x(), self.y(), radialRange, candidates);
            }
            List<ObjectView> visible = new ArrayList<>();
            for (ObjectView view : candidates.values()) {
                if (view.id().equals(self.id())) continue;
                boolean sensed = true;
                if (genome != null && (view.type().equals("CREATURE") || view.type().equals("HUNTER")
                        || view.type().equals("WANDERER"))) {
                    sensed = stableSample(seed, tick, object.id, view.id(), 1) <= genome.unitSampling();
                }
                if (sensed && genome != null && genome.sensorNoise() > 0.0) {
                    sensed = stableSample(seed, tick, object.id, view.id(), 2) >= genome.sensorNoise();
                }
                if (sensed) visible.add(view);
            }
            object.lastPerceivedCount = visible.size();
            Perception perception = new Perception(self, visible, snapshot.width, snapshot.height, visionRadius);
            return new DecisionContext(perception, self, all, legal);
        }

        private static double stableSample(long seed, int tick, String actorId, String targetId, int channel) {
            long value = seed ^ ((long) tick * 0x9E3779B97F4A7C15L)
                    ^ ((long) actorId.hashCode() << 32) ^ targetId.hashCode() ^ (channel * 0xC2B2AE3D27D4EB4FL);
            value ^= value >>> 30;
            value *= 0xBF58476D1CE4E5B9L;
            value ^= value >>> 27;
            value *= 0x94D049BB133111EBL;
            value ^= value >>> 31;
            return (value >>> 11) * 0x1.0p-53;
        }

        private boolean isInteractionAvailable(WorldSnapshot snapshot, ObjectView actor, ObjectView target,
                                               int tx, int ty, Direction direction, InteractionType type) {
            if (tx < 0 || ty < 0 || tx >= snapshot.width || ty >= snapshot.height) return false;
            String targetType = target == null ? "EMPTY" : target.type();
            return switch (type) {
                case DIG -> actor.has(Capability.DIG) && target != null && target.has(Capability.DIGGABLE);
                case PUSH -> actor.has(Capability.PUSH) && target != null && target.has(Capability.PUSHABLE)
                        && snapshot.at(tx + direction.dx, ty + direction.dy).isEmpty()
                        && !snapshot.isBlocked(tx + direction.dx, ty + direction.dy, target.id());
                case ATTACK -> (actor.has(Capability.ATTACK) && target != null
                        && target.has(Capability.ATTACKABLE) && target.hp() > 0.0
                        && !(actor.type().equals("CREATURE") && target.type().equals("CREATURE")
                             && actor.value("team") == target.value("team")))
                        || (actor.type().equals("ROCK") && actor.has(Capability.FALLS)
                        && target != null && target.type().equals("PLAYER"));
                case COLLECT -> actor.has(Capability.COLLECT) && target != null
                        && target.has(Capability.COLLECTABLE)
                        && (!target.type().equals("GEM") || target.value("stack") > 0.0);
                case DROP -> actor.has(Capability.DROP) && actor.value("inventoryGems") >= 1.0
                        && (target == null || (target.type().equals("GEM") && target.value("stack") < 9.0));
                case BUILD -> actor.has(Capability.BUILD)
                        && (actor.type().equals("CREATURE")
                            ? actor.value("energy") >= CREATURE_BUILD_ENERGY_COST
                            : actor.value("inventoryGems") >= STANDARD_BUILD_GEM_COST)
                        && target == null && snapshot.at(tx, ty).isEmpty();
                case BUILD_DEPOT -> actor.has(Capability.BUILD) && actor.value("inventoryGems") >= 3.0
                        && (target == null || target.type().equals("DIRT"))
                        && snapshot.at(tx, ty).stream().allMatch(v -> v.type().equals("DIRT"));
                case DEPOSIT -> actor.has(Capability.DEPOSIT) && target != null
                        && ((target.type().equals("BUILDING")
                                && actor.value("team") == target.value("team")
                                && actor.value("inventoryResources") >= 1.0
                                && target.value("storedResources") < target.value("resourceCapacity"))
                            || (target.type().equals("DEPOT")
                                && actor.value("inventoryGems") >= 1.0
                                && target.value("storedGems") < target.value("storageCapacity")));
                case PRODUCE -> actor.has(Capability.PRODUCE)
                        && ((actor.type().equals("BUILDING") && target == null
                                && actor.value("storedResources") >= LEVEL2_PRODUCTION_COST
                                && snapshot.at(tx, ty).isEmpty())
                            || (target != null && target.type().equals("DEPOT")
                                && target.value("storedGems") >= 8.0));
                case USE -> interactionRules.containsKey(new InteractionKey(actor.type(), targetType, type));
                case REPRODUCE -> actor.has(Capability.REPRODUCE)
                        && ((actor.type().equals("CREATURE")
                                && actor.value("energy") >= actor.value("reproductionThreshold"))
                            || (actor.type().equals("BUILDING")
                                && actor.value("storedResources") >= BUILDING_SPLIT_THRESHOLD))
                        && target == null && snapshot.at(tx, ty).isEmpty();
            };
        }

        void setEnemyDecisionAlgorithm(boolean randomLegal) {
            randomEnemyDecision = randomLegal;
            for (GameObject object : objects) {
                if (object.type.equals("HUNTER") || object.type.equals("WANDERER")) {
                    object.decisionAlgorithm = newEnemyAlgorithm(object);
                }
            }
        }

        void tick() {
            if (gameOver) return;
            if (evolutionMode) regenerateFood();
            WorldSnapshot snapshot = snapshot();
            Map<String, GameAction> manual = new LinkedHashMap<>(queuedActions);
            queuedActions.clear();
            List<PlannedAction> planned = new ArrayList<>();

            // PLAN: all controllers see exactly the same immutable snapshot.
            for (GameObject object : objects) {
                if (!object.alive) continue;
                ObjectView self = snapshot.get(object.id);
                if (self == null) continue;
                GameAction action = manual.get(object.id);
                boolean humanOverride = action != null;
                // HUMAN mode is exclusive: no manual input means WAIT, never an AI fallback.
                if (action == null && humanControlledIds.contains(object.id)) continue;
                // Run infrastructure policy before idle-controller filtering.
                if (action == null) action = strategicInfrastructureAction(object, snapshot);
                if (action == null && object.controller instanceof IdleController) continue;
                if (action == null) {
                    DecisionContext context = decisionContext(object, snapshot);
                    ActionEvaluator evaluator = evolutionMode && object.type.equals("CREATURE")
                            ? new EvolutionActionEvaluator(context)
                            : new ControllerPreferenceEvaluator(object.controller, context);
                    DecisionAlgorithm algorithm = object.decisionAlgorithm == null
                            ? new GreedyDecisionAlgorithm() : object.decisionAlgorithm;
                    action = algorithm.chooseAction(context, evaluator);
                }
                if (action == null || !GameAction.allActionOptions().contains(action)) action = GameAction.waitAction();
                int priority = object.initiative + (humanOverride ? 10_000 : 0);
                planned.add(new PlannedAction(object.id, action, priority, object.x, object.y, humanOverride));
            }

            // RESOLVE: deterministic order. Every action is revalidated against the live state.
            planned.sort(Comparator.comparingInt(PlannedAction::priority).reversed()
                    .thenComparing(PlannedAction::objectId));
            int changed = 0;
            for (PlannedAction intent : planned) {
                GameObject actor = getObject(intent.objectId());
                if (actor == null || gameOver) continue;
                // A prior action may have pushed or otherwise displaced this object.
                // Its old snapshot intent is stale and must not cause a second action this tick.
                if (actor.x != intent.startX() || actor.y != intent.startY()) continue;
                double scoreBefore = actor.value("score");
                double hpBefore = actor.value("hp");
                boolean resolved = resolve(actor, intent.action(), intent.humanOverride());
                double reward = actionReward(intent.action(), resolved, actor, scoreBefore, hpBefore);
                actor.learn(intent.action(), reward);
                if (actor.type.equals("CREATURE")) actor.set("fitness", actor.value("fitness") + Math.max(-0.05, reward) * 0.08);
                if (resolved) changed++;
            }
            tick++;
            if (evolutionMode) {
                updateEvolutionLifeCycle();
                evaluateBlueLevelOutcome();
                recordPopulationSample();
            }
            if (changed == 0 && !gameOver && !evolutionMode) message = "Tick " + tick + ": keine gültige Aktion ausgeführt.";
        }

        private boolean resolve(GameObject actor, GameAction action, boolean manualOverride) {
            return switch (action.kind()) {
                case WAIT -> {
                    if (manualOverride) message = actor.name + " wartet einen Tick.";
                    yield true;
                }
                case MOVE -> move(actor, action.direction());
                case INTERACT -> interact(actor, action.direction(), action.interactionType());
            };
        }

        private boolean move(GameObject actor, Direction direction) {
            int nx = actor.x + direction.dx;
            int ny = actor.y + direction.dy;
            if (isBlocked(nx, ny, actor.id)) return false;
            int oldX = actor.x;
            int oldY = actor.y;
            actor.x = nx;
            actor.y = ny;
            actor.syncPosition();
            String enterMessage = applyEnterRules(actor);
            if (actor.type.equals("PLAYER")) {
                actor.set("energy", clamp(actor.value("energy") - 0.2, 0, 100));
                actor.set("stress", clamp(actor.value("stress") + 0.1, 0, 100));
            } else if (actor.type.equals("CREATURE")) {
                actor.set("energy", Math.max(0.0, actor.value("energy") - 0.045));
                actor.set("food", actor.value("energy"));
            }
            if (gameOver) {
                if (enterMessage != null) message = enterMessage;
                return true;
            }
            message = enterMessage != null ? enterMessage
                    : actor.name + " bewegt sich " + direction + " von (" + oldX + "," + oldY + ") nach (" + nx + "," + ny + ").";
            return true;
        }

        private String applyEnterRules(GameObject actor) {
            String feedback = null;
            for (GameObject target : new ArrayList<>(objectsAt(actor.x, actor.y))) {
                if (target == actor) continue;
                if (actor.type.equals("PLAYER") && target.type.equals("GEM")) {
                    collectGem(actor, target);
                    feedback = "Edelstein beim Betreten gesammelt: +10 Punkte.";
                } else if (actor.type.equals("CREATURE") && target.type.equals("FOOD")) {
                    collectFood(actor, target);
                    feedback = "Nahrung beim Betreten aufgenommen.";
                } else if (actor.type.equals("PLAYER") && target.type.equals("EXIT")) {
                    feedback = checkExit(actor);
                } else if (actor.type.equals("ROCK") && target.type.equals("GEM")) {
                    removeObject(target);
                }
            }
            return feedback;
        }

        private GameObject adjacentTarget(GameObject actor, Direction direction) {
            return firstInteractableAt(actor.x + direction.dx, actor.y + direction.dy, actor.id);
        }

        private boolean drop(GameObject actor, Direction direction) {
            int x = actor.x + direction.dx;
            int y = actor.y + direction.dy;
            int inventory = (int) actor.value("inventoryGems");
            if (inventory <= 0) {
                message = "ABLEGEN: Das Inventar enthält keine Edelsteine.";
                return false;
            }
            if (!inside(x, y) || isBlocked(x, y, actor.id)) {
                message = "ABLEGEN: Das Nachbarfeld ist blockiert.";
                return false;
            }
            GameObject stack = null;
            for (GameObject object : objectsAt(x, y)) {
                if (object.type.equals("GEM")) { stack = object; break; }
                if (!object.id.equals(actor.id)) {
                    message = "ABLEGEN: Das Zielfeld ist nicht frei.";
                    return false;
                }
            }
            if (stack != null) {
                if (stack.value("stack") >= 9.0) {
                    message = "ABLEGEN: Auf diesem Feld liegen bereits maximal 9 Ressourcen.";
                    return false;
                }
                stack.set("stack", stack.value("stack") + 1.0);
            } else {
                GameObject gem = object("gem", "GEM", "Edelstein", "◆", new Color(40, 205, 220),
                        x, y, false, 5, new IdleController());
                addObject(gem);
            }
            actor.set("inventoryGems", inventory - 1.0);
            message = actor.name + " legt einen Edelstein Richtung " + direction + " ab.";
            return true;
        }

        String buildFailure(GameObject actor, Direction direction) {
            if (actor == null || !actor.has(Capability.BUILD))
                return "BUILD NICHT MÖGLICH: Unit besitzt keine BUILD-Fähigkeit.";
            int x = actor.x + direction.dx;
            int y = actor.y + direction.dy;
            if (!inside(x, y)) return "BUILD NICHT MÖGLICH: Zielfeld liegt außerhalb der Karte.";
            if (actor.type.equals("CREATURE")) {
                if (actor.value("energy") < CREATURE_BUILD_ENERGY_COST)
                    return String.format(Locale.ROOT,
                            "BUILD NICHT MÖGLICH: Benötigt %.1f Energie, vorhanden %.1f.",
                            CREATURE_BUILD_ENERGY_COST, actor.value("energy"));
            } else if (actor.value("inventoryGems") < STANDARD_BUILD_GEM_COST) {
                return String.format(Locale.ROOT,
                        "BUILD NICHT MÖGLICH: Benötigt %.0f Edelsteine, vorhanden %.0f.",
                        STANDARD_BUILD_GEM_COST, actor.value("inventoryGems"));
            }
            if (!isCompletelyEmpty(x, y, actor.id))
                return "BUILD NICHT MÖGLICH: Das Nachbarfeld ist belegt.";
            return null;
        }

        private String build(GameObject actor, Direction direction) {
            String failure = buildFailure(actor, direction);
            if (failure != null) return failure;
            int x = actor.x + direction.dx;
            int y = actor.y + direction.dy;
            if (actor.type.equals("CREATURE")) {
                actor.set("energy", actor.value("energy") - CREATURE_BUILD_ENERGY_COST);
                actor.set("food", actor.value("energy"));
            } else {
                actor.set("inventoryGems", actor.value("inventoryGems") - STANDARD_BUILD_GEM_COST);
            }
            actor.set("structuresBuilt", actor.value("structuresBuilt") + 1.0);
            Color buildingColor = actor.type.equals("CREATURE")
                    ? actor.color : new Color(190, 150, 75);
            GameObject building = object("building", "BUILDING", "Team-Zitadelle", "▤",
                    buildingColor, x, y, true, 0, new BuildingController());
            building.parentId = actor.id;
            building.set("team", actor.value("team"));
            building.set("generation", actor.value("generation") + 1.0);
            building.set("buildingLevel", 1.0);
            building.set("storedResources", 0.0);
            building.set("resourceCapacity", BUILDING_STORAGE_CAPACITY);
            building.set("hp", 45.0);
            building.set("maxHp", 45.0);
            addObject(building);
            String cost = actor.type.equals("CREATURE")
                    ? String.format(Locale.ROOT, "%.1f Energie", CREATURE_BUILD_ENERGY_COST)
                    : String.format(Locale.ROOT, "%.0f Edelsteine", STANDARD_BUILD_GEM_COST);
            return "BUILD: " + actor.name + " errichtet ein unbewegliches Gebäude (Kosten: " + cost + ").";
        }

        String depotBuildFailure(GameObject actor, Direction direction) {
            int x = actor.x + direction.dx;
            int y = actor.y + direction.dy;
            if (!actor.has(Capability.BUILD)) return "DEPOTBAU NICHT MÖGLICH: Unit hat keine BUILD-Fähigkeit.";
            if (actor.value("inventoryGems") < 3.0)
                return "DEPOTBAU NICHT MÖGLICH: Benötigt 3 Edelsteine, vorhanden "
                        + (int) actor.value("inventoryGems") + ".";
            if (!inside(x, y)) return "DEPOTBAU NICHT MÖGLICH: Zielfeld liegt außerhalb der Karte.";
            List<GameObject> occupants = objectsAt(x, y);
            if (occupants.stream().anyMatch(o -> !o.id.equals(actor.id) && !o.type.equals("DIRT")))
                return "DEPOTBAU NICHT MÖGLICH: Zielfeld ist durch Fels, Einheit oder Gebäude blockiert.";
            return null;
        }

        private String buildDepot(GameObject actor, Direction direction) {
            int x = actor.x + direction.dx;
            int y = actor.y + direction.dy;
            String failure = depotBuildFailure(actor, direction);
            if (failure != null) return failure;
            List<GameObject> occupants = objectsAt(x, y);
            for (GameObject occupant : occupants) {
                if (occupant.type.equals("DIRT")) removeObject(occupant);
            }
            actor.set("inventoryGems", actor.value("inventoryGems") - 3.0);
            GameObject depot = object("depot", "DEPOT", "Lager-Kaserne", "▣",
                    new Color(205, 160, 75), x, y, true, 0, new IdleController());
            depot.set("hp", 70.0);
            depot.set("maxHp", 70.0);
            depot.set("storedGems", 0.0);
            depot.set("storageCapacity", 20.0);
            depot.set("team", actor.value("team"));
            addObject(depot);
            return "BUILD_DEPOT: Lager-Kaserne errichtet (3 Kosten, 20 Lagerplätze, 70 HP).";
        }

        private String depositResource(GameObject actor, GameObject depot) {
            if (depot == null) return "DEPOSIT: Kein Lager in Reichweite.";
            if (depot.type.equals("BUILDING")) {
                if (actor.value("team") != depot.value("team"))
                    return "DEPOSIT: Nur Einheiten des eigenen Teams dürfen hier einlagern.";
                if (actor.value("inventoryResources") < 1.0)
                    return "DEPOSIT: Keine Biomasse im Inventar.";
                if (depot.value("storedResources") >= depot.value("resourceCapacity"))
                    return "DEPOSIT: Team-Zitadelle ist voll.";
                actor.set("inventoryResources", actor.value("inventoryResources") - 1.0);
                depot.set("storedResources", depot.value("storedResources") + 1.0);
                depot.set("resourcesDelivered", depot.value("resourcesDelivered") + 1.0);
                return String.format(Locale.ROOT, "DEPOSIT: Biomasse eingelagert (%d/%.0f).",
                        (int) depot.value("storedResources"), depot.value("resourceCapacity"));
            }
            if (!depot.type.equals("DEPOT")) return "DEPOSIT: Ziel ist kein Lager.";
            if (actor.value("inventoryGems") < 1.0) return "DEPOSIT: Kein Edelstein im Inventar.";
            if (depot.value("storedGems") >= depot.value("storageCapacity"))
                return "DEPOSIT: Lager ist voll.";
            actor.set("inventoryGems", actor.value("inventoryGems") - 1.0);
            depot.set("storedGems", depot.value("storedGems") + 1.0);
            return String.format(Locale.ROOT, "DEPOSIT: 1 Edelstein eingelagert (%d/%d).",
                    (int) depot.value("storedGems"), (int) depot.value("storageCapacity"));
        }

        private String produceBruiser(GameObject actor, GameObject depot) {
            if (depot == null || !depot.type.equals("DEPOT")) return "PRODUCE: Keine Lager-Kaserne ausgewählt.";
            if (depot.value("storedGems") < 8.0) return "PRODUCE: Benötigt 8 eingelagerte Edelsteine.";
            int[] pos = findSpawnNear(depot.x, depot.y);
            if (pos == null) return "PRODUCE: Kein freies Feld in der Nähe der Kaserne.";
            depot.set("storedGems", depot.value("storedGems") - 8.0);
            GameObject unit = object("bruiser", "BRUISER", "Schwerer Wächter", "◆",
                    new Color(245, 190, 65), pos[0], pos[1], true, 75, new GuardController());
            unit.set("team", depot.value("team"));
            unit.set("hp", 60.0);
            unit.set("maxHp", 60.0);
            unit.set("attackDamage", 14.0);
            unit.set("visionRange", 10.0);
            unit.set("attackable", 1.0);
            addObject(unit);
            unit.decisionAlgorithm = new AdaptiveSoftmaxDecisionAlgorithm(0.08, 0.55, seed + nextId * 31L);
            return "PRODUCE: Schwerer Wächter ausgebildet (60 HP, 14 Schaden; Kosten: 8 Lagerressourcen).";
        }

        private String produceLevel2Unit(GameObject building, Direction direction) {
            if (building == null || !building.type.equals("BUILDING"))
                return "PRODUCE: Nur Team-Zitadellen können Level-2-Units erzeugen.";
            if (building.value("storedResources") < LEVEL2_PRODUCTION_COST)
                return "PRODUCE: 12 eingelagerte Ressourcen erforderlich.";
            int x = building.x + direction.dx, y = building.y + direction.dy;
            if (!isCompletelyEmpty(x, y, building.id))
                return "PRODUCE: Nachbarfeld ist nicht frei.";
            if (creatureCount() >= MAX_EVOLUTION_POPULATION)
                return "PRODUCE: Populationslimit erreicht.";
            building.set("storedResources", building.value("storedResources") - LEVEL2_PRODUCTION_COST);
            Random genetics = new Random(seed ^ building.id.hashCode() ^ tick ^ nextId);
            GameObject unit = createCreature("elite", "CREATURE", "Level-2-Unit",
                    building.color, x, y, (int) building.value("team"),
                    (int) building.value("generation") + 1, building.id, genetics, 18.0);
            unit.set("unitLevel", 2.0);
            unit.set("hp", 9.0);
            unit.set("maxHp", 9.0);
            unit.set("attackDamage", 3.0);
            unit.set("visionRange", 12.0);
            unit.set("metabolism", 0.05);
            unit.set("foodEfficiency", 1.20);
            unit.set("aggression", 0.70);
            unit.set("reproductionThreshold", 17.0);
            unit.genome = new Genome(0.95, 10.0, 10.0, 12.0, 0.95, 0.02,
                    1.35, 1.20, 1.35, 1.20, 0.35, 0.08);
            unit.decisionAlgorithm = new AdaptiveSoftmaxDecisionAlgorithm(0.12, 0.62,
                    seed ^ unit.id.hashCode() ^ tick);
            addObject(unit);
            building.set("eliteUnitsProduced", building.value("eliteUnitsProduced") + 1.0);
            return "LEVEL-UP: Team " + ((int) building.value("team") + 1)
                    + " erzeugt Level 2 (9 HP, 3 Schaden; 12 Ressourcen).";
        }

        private String divideBuilding(GameObject parent, Direction direction) {
            if (!evolutionMode || parent == null || !parent.type.equals("BUILDING"))
                return "REPRODUCE: Nur Team-Zitadellen können sich teilen.";
            if (countType("BUILDING") >= 240)
                return "REPRODUCE: Zitadellen-Limit erreicht.";
            double stored = parent.value("storedResources");
            if (stored < BUILDING_SPLIT_THRESHOLD)
                return "REPRODUCE: 24 Ressourcen für die Teilung erforderlich.";
            int x = parent.x + direction.dx, y = parent.y + direction.dy;
            if (!isCompletelyEmpty(x, y, parent.id))
                return "REPRODUCE: Nachbarfeld ist nicht frei.";
            double transfer = stored * 0.5;
            parent.set("storedResources", stored - transfer);
            GameObject child = object("building", "BUILDING", "Tochter-Zitadelle", "▤",
                    parent.color, x, y, true, 0, new BuildingController());
            child.parentId = parent.id;
            child.set("team", parent.value("team"));
            child.set("generation", parent.value("generation") + 1.0);
            child.set("buildingLevel", parent.value("buildingLevel") + 1.0);
            child.set("storedResources", transfer);
            child.set("resourceCapacity", parent.value("resourceCapacity"));
            child.set("buildingDivisions", parent.value("buildingDivisions"));
            child.set("eliteUnitsProduced", parent.value("eliteUnitsProduced"));
            child.set("hp", 45.0);
            child.set("maxHp", 45.0);
            addObject(child);
            parent.set("buildingDivisions", parent.value("buildingDivisions") + 1.0);
            return String.format(Locale.ROOT, "ZITADELLEN-TEILUNG: Team %d | Lager geteilt: %.0f/%.0f.",
                    (int) parent.value("team") + 1, transfer, transfer);
        }

        private GameAction strategicInfrastructureAction(GameObject actor, WorldSnapshot snapshot) {
            if (actor.type.equals("BUILDING")) return null;
            if (!actor.has(Capability.BUILD) && !actor.has(Capability.DEPOSIT)) return null;
            DecisionContext context = decisionContext(actor, snapshot);
            ObjectView self = context.self();
            // Evolution workers deliver biomass to an adjacent team citadel before optional actions.
            if (actor.type.equals("CREATURE") && actor.value("inventoryResources") >= 1.0) {
                for (Direction d : Direction.values()) {
                    ObjectView target = context.perception().at(self.x() + d.dx, self.y() + d.dy)
                            .stream().filter(v -> v.type().equals("BUILDING")
                                    && v.value("team") == self.value("team")).findFirst().orElse(null);
                    if (target != null && context.legalOptions().contains(
                            GameAction.interact(d, InteractionType.DEPOSIT)))
                        return GameAction.interact(d, InteractionType.DEPOSIT);
                }
            }
            // Existing Boulder Dash Depot logistics continue below.
            for (Direction d : Direction.values()) {
                ObjectView target = context.perception().at(self.x() + d.dx, self.y() + d.dy)
                        .stream().filter(v -> v.type().equals("DEPOT")).findFirst().orElse(null);
                if (target != null && target.value("storedGems") >= 8.0
                        && context.legalOptions().contains(GameAction.interact(d, InteractionType.PRODUCE)))
                    return GameAction.interact(d, InteractionType.PRODUCE);
            }
            for (Direction d : Direction.values()) {
                ObjectView target = context.perception().at(self.x() + d.dx, self.y() + d.dy)
                        .stream().filter(v -> v.type().equals("DEPOT")).findFirst().orElse(null);
                if (target != null && context.legalOptions().contains(GameAction.interact(d, InteractionType.DEPOSIT)))
                    return GameAction.interact(d, InteractionType.DEPOSIT);
            }
            for (Direction d : Direction.values()) {
                ObjectView target = context.perception().at(self.x() + d.dx, self.y() + d.dy)
                        .stream().filter(v -> v.type().equals("GEM")).findFirst().orElse(null);
                if (target != null && context.legalOptions().contains(GameAction.interact(d, InteractionType.COLLECT)))
                    return GameAction.interact(d, InteractionType.COLLECT);
            }
            if (actor.value("inventoryGems") >= 3.0 && actor.has(Capability.BUILD)) {
                for (Direction d : Direction.values()) {
                    int tx = self.x() + d.dx, ty = self.y() + d.dy;
                    if (context.legalOptions().contains(GameAction.interact(d, InteractionType.BUILD_DEPOT))
                            && snapshot.at(tx, ty).isEmpty())
                        return GameAction.interact(d, InteractionType.BUILD_DEPOT);
                }
                for (Direction d : Direction.values()) {
                    if (context.legalOptions().contains(GameAction.interact(d, InteractionType.BUILD_DEPOT)))
                        return GameAction.interact(d, InteractionType.BUILD_DEPOT);
                }
            }
            // Non-evolution agents may build one simple structure after preserving depot priority.
            if (!actor.type.equals("CREATURE") && actor.value("structuresBuilt") < 1.0
                    && actor.has(Capability.BUILD)
                    && actor.value("inventoryGems") >= STANDARD_BUILD_GEM_COST) {
                for (Direction d : Direction.values()) {
                    int tx = self.x() + d.dx, ty = self.y() + d.dy;
                    if (context.legalOptions().contains(GameAction.interact(d, InteractionType.BUILD))
                            && snapshot.at(tx, ty).isEmpty())
                        return GameAction.interact(d, InteractionType.BUILD);
                }
            }
            return null;
        }

        private boolean interact(GameObject actor, Direction direction, InteractionType type) {
            int tx = actor.x + direction.dx;
            int ty = actor.y + direction.dy;
            GameObject target = firstInteractableAt(tx, ty, actor.id);
            String targetType = target == null ? "EMPTY" : target.type;
            InteractionRule rule = interactionRules.get(new InteractionKey(actor.type, targetType, type));
            if (rule == null) rule = interactionRules.get(new InteractionKey("*", targetType, type));
            if (rule == null) rule = interactionRules.get(new InteractionKey("*", "*", type));
            if (rule == null) {
                message = type + ": " + actor.name + " kann diese Aktion Richtung " + direction + " hier nicht ausführen.";
                return false;
            }
            String result = rule.apply(this, actor, target, direction);
            if (result != null && !result.isBlank()) message = result;
            return true;
        }

        private GameObject firstInteractableAt(int x, int y, String ignoreId) {
            GameObject best = null;
            for (GameObject object : objects) {
                if (!object.alive || object.id.equals(ignoreId) || object.x != x || object.y != y) continue;
                if (best == null || (object.blocksMovement && !best.blocksMovement)) best = object;
            }
            return best;
        }

        void registerInteractionRule(String actorType, String targetType, InteractionType type, InteractionRule rule) {
            interactionRules.put(new InteractionKey(Objects.requireNonNull(actorType),
                    Objects.requireNonNull(targetType), Objects.requireNonNull(type)), Objects.requireNonNull(rule));
        }

        /** Generic custom-interaction convenience; specific game rules should name their type. */
        void registerInteractionRule(String actorType, String targetType, InteractionRule rule) {
            registerInteractionRule(actorType, targetType, InteractionType.USE, rule);
        }

        private void registerDefaultRules() {
            // Generic rules are keyed by target capability/type, not by one special player class.
            registerInteractionRule("*", "*", InteractionType.DIG, (w, actor, target, direction) -> {
                if (target == null || !target.has(Capability.DIGGABLE)) return "DIG: Ziel ist nicht abtragbar.";
                w.removeObject(target);
                w.score += 1;
                actor.set("score", actor.value("score") + 1);
                return "DIG: " + target.name + " wurde Richtung " + direction + " abgetragen (+1).";
            });
            registerInteractionRule("*", "*", InteractionType.PUSH, (w, actor, target, direction) -> {
                if (target == null || !target.has(Capability.PUSHABLE)) return "PUSH: Ziel ist nicht verschiebbar.";
                int beyondX = target.x + direction.dx;
                int beyondY = target.y + direction.dy;
                if (!w.isCompletelyEmpty(beyondX, beyondY, target.id)) return "PUSH: Feld hinter dem Objekt ist blockiert.";
                int oldX = target.x, oldY = target.y;
                target.x = beyondX;
                target.y = beyondY;
                target.syncPosition();
                actor.x = oldX;
                actor.y = oldY;
                actor.syncPosition();
                return "PUSH: " + target.name + " verschoben; " + actor.name + " rückt auf das alte Feld.";
            });
            registerInteractionRule("*", "*", InteractionType.ATTACK, (w, actor, target, direction) -> {
                if (target == null) return "ATTACK: Kein Ziel im Nachbarfeld.";
                if (actor.type.equals("ROCK") && actor.has(Capability.FALLS) && target.type.equals("PLAYER")) {
                    double hpBefore = target.value("hp");
                    target.set("hp", 0.0);
                    w.logEvent("COLLISION", actor.id + " bei (" + actor.x + "," + actor.y + ") trifft "
                            + target.id + " bei (" + target.x + "," + target.y + "); HP " + hpBefore + " -> 0");
                    w.gameOver = true;
                    w.won = false;
                    return "COLLISION: Ein fallender Felsen trifft den Spieler.";
                }
                if (!target.has(Capability.ATTACKABLE)) return "ATTACK: Ziel ist nicht angreifbar.";
                if (target.type.equals("PLAYER")) return w.enemyAttack(actor, target);
                return w.damageTarget(actor, target);
            });
            registerInteractionRule("*", "*", InteractionType.COLLECT, (w, actor, target, direction) -> {
                if (target == null || !target.has(Capability.COLLECTABLE)) return "COLLECT: Ziel ist nicht sammelbar.";
                if (target.type.equals("GEM")) {
                    w.collectGem(actor, target);
                    return "COLLECT: Edelstein aufgenommen (+10 Punkte).";
                }
                if (target.type.equals("FOOD")) {
                    w.collectFood(actor, target);
                    return "COLLECT: Nahrung aufgenommen.";
                }
                w.removeObject(target);
                actor.set("inventoryGems", actor.value("inventoryGems") + 1.0);
                return "COLLECT: " + target.name + " aufgenommen.";
            });
            registerInteractionRule("*", "EMPTY", InteractionType.DROP, (w, actor, target, direction) -> w.drop(actor, direction)
                    ? "DROP: Ressource auf dem Nachbarfeld abgelegt."
                    : "DROP: Ablegen nicht möglich.");
            registerInteractionRule("*", "GEM", InteractionType.DROP, (w, actor, target, direction) -> w.dropIntoStack(actor, target));
            registerInteractionRule("*", "EMPTY", InteractionType.BUILD, (w, actor, target, direction) -> w.build(actor, direction));
            registerInteractionRule("*", "EMPTY", InteractionType.BUILD_DEPOT,
                    (w, actor, target, direction) -> w.buildDepot(actor, direction));
            registerInteractionRule("*", "DIRT", InteractionType.BUILD_DEPOT,
                    (w, actor, target, direction) -> w.buildDepot(actor, direction));
            registerInteractionRule("*", "DEPOT", InteractionType.DEPOSIT,
                    (w, actor, target, direction) -> w.depositResource(actor, target));
            registerInteractionRule("*", "BUILDING", InteractionType.DEPOSIT,
                    (w, actor, target, direction) -> w.depositResource(actor, target));
            registerInteractionRule("*", "DEPOT", InteractionType.PRODUCE,
                    (w, actor, target, direction) -> w.produceBruiser(actor, target));
            registerInteractionRule("BUILDING", "EMPTY", InteractionType.PRODUCE,
                    (w, actor, target, direction) -> w.produceLevel2Unit(actor, direction));
            registerInteractionRule("BUILDING", "EMPTY", InteractionType.REPRODUCE,
                    (w, actor, target, direction) -> w.divideBuilding(actor, direction));
            registerInteractionRule("PLAYER", "EXIT", InteractionType.USE, (w, actor, target, direction) -> w.checkExit(actor));
            registerInteractionRule("CREATURE", "EMPTY", InteractionType.REPRODUCE,
                    (w, actor, target, direction) -> w.divide(actor, direction));
        }

        private double actionReward(GameAction action, boolean success, GameObject actor,
                                    double scoreBefore, double hpBefore) {
            if (!success) return -0.20;
            double scoreGain = actor.value("score") - scoreBefore;
            double hpChange = hpBefore - actor.value("hp");
            double base = switch (action.kind()) {
                case WAIT -> 0.0;
                case MOVE -> 0.05;
                case INTERACT -> switch (action.interactionType()) {
                    case DIG -> 0.20;
                    case PUSH -> 0.15;
                    case ATTACK -> 0.30;
                    case COLLECT -> 0.80;
                    case DROP -> 0.05;
                    case BUILD -> 0.25;
                    case BUILD_DEPOT -> 0.35;
                    case DEPOSIT -> 0.30;
                    case PRODUCE -> 0.65;
                    case USE -> 0.10;
                    case REPRODUCE -> 0.45;
                };
            };
            return base + Math.max(0.0, scoreGain) * 0.05 - Math.max(0.0, hpChange) * 0.02;
        }

        private String damageTarget(GameObject actor, GameObject target) {
            double damage = Math.max(0.0, actor.value("attackDamage"));
            double hpBefore = target.value("hp");
            target.set("hp", Math.max(0.0, hpBefore - damage));
            logEvent("DAMAGE", actor.id + " [" + actor.type + "] bei (" + actor.x + "," + actor.y
                    + ") attackiert " + target.id + " [" + target.type + "] bei (" + target.x + "," + target.y
                    + "); Schaden=" + damage + "; HP " + hpBefore + " -> " + target.value("hp"));
            if (target.value("hp") <= 0.0) {
                logEvent("DEATH", target.id + " [" + target.type + "] durch Angriff von " + actor.id
                        + " entfernt; Position (" + target.x + "," + target.y + ")");
                if (evolutionMode && target.type.equals("CREATURE")) {
                    deaths++;
                    if (actor.type.equals("CREATURE")) {
                        double biomass = 2.0 + Math.max(0.0, target.value("energy")) * 0.45;
                        actor.set("energy", actor.value("energy") + biomass);
                        actor.set("food", actor.value("energy"));
                        actor.set("kills", actor.value("kills") + 1.0);
                        actor.set("fitness", actor.value("fitness") + 2.0);
                        predationKills++;
                    }
                }
                removeObject(target);
                score += 25;
                return "INTERACT → ATTACK: " + target.name + " besiegt (+25 Punkte / ggf. Biomasse).";
            }
            return "INTERACT → ATTACK: " + target.name + " erhält "
                    + String.format(Locale.ROOT, "%.0f", damage) + " Schaden; HP="
                    + String.format(Locale.ROOT, "%.0f", target.value("hp")) + ".";
        }

        private String enemyAttack(GameObject actor, GameObject player) {
            double damage = Math.max(0.0, actor.value("attackDamage"));
            double hpBefore = player.value("hp");
            player.set("hp", Math.max(0.0, hpBefore - damage));
            logEvent("DAMAGE", actor.id + " [" + actor.type + "] bei (" + actor.x + "," + actor.y
                    + ") attackiert " + player.id + " [" + player.type + "] bei (" + player.x + "," + player.y
                    + "); Schaden=" + damage + "; HP " + hpBefore + " -> " + player.value("hp"));
            if (player.value("hp") <= 0.0) {
                gameOver = true;
                won = false;
                return "INTERACT → ATTACK: " + actor.name + " hat den Spieler besiegt.";
            }
            return "INTERACT → ATTACK: " + actor.name + " greift an. Spieler-HP="
                    + String.format(Locale.ROOT, "%.0f", player.value("hp")) + ".";
        }

        private String dropIntoStack(GameObject actor, GameObject stack) {
            if (stack.value("stack") >= 9.0) return "INTERACT → DROP: Ressourcenstapel ist voll (9).";
            stack.set("stack", stack.value("stack") + 1.0);
            actor.set("inventoryGems", actor.value("inventoryGems") - 1.0);
            return "INTERACT → DROP: Ressource zum vorhandenen Stapel gelegt.";
        }

        private String checkExit(GameObject actor) {
            if (gemsCollected >= gemsRequired) {
                gameOver = true;
                won = true;
                return "QUEST ERFOLGREICH! Ausgang erreicht – " + score + " Punkte.";
            }
            return "Ausgang noch verschlossen: " + gemsCollected + "/" + gemsRequired + " Edelsteine.";
        }

        private void collectGem(GameObject actor, GameObject gem) {
            double stack = Math.max(1.0, gem.value("stack"));
            if (stack > 1.0) gem.set("stack", stack - 1.0);
            else removeObject(gem);
            int inventory = (int) actor.value("inventoryGems") + 1;
            actor.set("inventoryGems", inventory);
            if ("PLAYER".equals(actor.type)) gemsCollected++;
            score += 10;
            actor.set("score", actor.value("score") + 10);
            actor.set("motivation", clamp(actor.value("motivation") + 2.0, 0, 100));
        }

        private void collectFood(GameObject actor, GameObject food) {
            double energy = Math.max(0.1, food.value("foodValue")) * Math.max(0.1, actor.value("foodEfficiency"));
            actor.set("energy", actor.value("energy") + energy);
            actor.set("food", actor.value("energy"));
            // One abstract biomass token per food item is carried to the team's citadel.
            actor.set("inventoryResources", Math.min(12.0, actor.value("inventoryResources") + 1.0));
            foodCollected++;
            removeObject(food);
        }

        private void removeObject(GameObject object) {
            object.alive = false;
            objects.remove(object);
            queuedActions.remove(object.id);
        }

        void spawnHunter() {
            int[] pos = findSpawnNear(width - 3, height - 3);
            if (pos == null) { message = "Kein freier Platz für einen neuen Gegner."; return; }
            addObject(object("hunter", "HUNTER", "Hybrid-Verfolger", "◆", new Color(234, 85, 93),
                    pos[0], pos[1], true, 60, new HybridHunterController(6)));
            GameObject spawned = objects.get(objects.size() - 1);
            spawned.decisionAlgorithm = newEnemyAlgorithm(spawned);
            spawned.set("team", 1.0);
            spawned.set("hp", 3); spawned.set("maxHp", 3);
            spawned.set("attackable", 1); spawned.set("attackDamage", 20);
            message = "Neuer Hybrid-Verfolger erzeugt: " + spawned.id;
        }

        void spawnWanderer() {
            int[] pos = findSpawnNear(width / 2, height / 2);
            if (pos == null) { message = "Kein freier Platz für einen Wanderer."; return; }
            GameObject spawned = object("wanderer", "WANDERER", "Wanderer", "○", new Color(190, 130, 240),
                    pos[0], pos[1], true, 35, new WanderController(seed + nextId));
            spawned.decisionAlgorithm = newEnemyAlgorithm(spawned);
            spawned.set("team", 1.0);
            addObject(spawned);
            message = "Neuer Wanderer erzeugt.";
        }

        private int[] findSpawnNear(int centerX, int centerY) {
            for (int radius = 0; radius < Math.max(width, height); radius++) {
                for (int y = Math.max(1, centerY - radius); y <= Math.min(height - 2, centerY + radius); y++) {
                    for (int x = Math.max(1, centerX - radius); x <= Math.min(width - 2, centerX + radius); x++) {
                        if (isCompletelyEmpty(x, y, "")) return new int[]{x, y};
                    }
                }
            }
            return null;
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        private record PlannedAction(String objectId, GameAction action, int priority,
                                     int startX, int startY, boolean humanOverride) { }
    }

    // ---------------------------------------------------------------------
    // 3) Replaceable object behaviours / strategies
    // ---------------------------------------------------------------------

    /** Team citadels alternate replication and elite production as biomass is delivered. */
    static final class BuildingController implements ObjectController {
        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            double stored = self.value("storedResources");
            boolean splitDue = self.value("buildingDivisions") <= self.value("eliteUnitsProduced");
            if (stored >= BUILDING_SPLIT_THRESHOLD && splitDue) {
                for (Direction direction : Direction.values()) {
                    int x = self.x() + direction.dx, y = self.y() + direction.dy;
                    if (perception.isVisible(x, y) && perception.at(x, y).isEmpty())
                        return GameAction.interact(direction, InteractionType.REPRODUCE);
                }
            }
            if (stored >= LEVEL2_PRODUCTION_COST) {
                for (Direction direction : Direction.values()) {
                    int x = self.x() + direction.dx, y = self.y() + direction.dy;
                    if (perception.isVisible(x, y) && perception.at(x, y).isEmpty())
                        return GameAction.interact(direction, InteractionType.PRODUCE);
                }
            }
            return GameAction.waitAction();
        }
        @Override public String name() { return "TEAM_CITADEL_CONTROLLER"; }
    }

    /** Autonomous cell policy. The evaluator adds state-dependent scoring and learned action values. */
    static final class EvolutionController implements ObjectController {
        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            for (Direction direction : Direction.values()) {
                for (ObjectView target : perception.at(self.x() + direction.dx, self.y() + direction.dy)) {
                    if (target.type().equals("FOOD"))
                        return GameAction.interact(direction, InteractionType.COLLECT);
                    if (target.type().equals("CREATURE") && target.value("team") != self.value("team")
                            && self.value("aggression") > 0.45)
                        return GameAction.interact(direction, InteractionType.ATTACK);
                }
            }
            if (self.value("energy") >= self.value("reproductionThreshold")) {
                for (Direction direction : Direction.values()) {
                    int x = self.x() + direction.dx, y = self.y() + direction.dy;
                    if (perception.at(x, y).isEmpty() && perception.isVisible(x, y))
                        return GameAction.interact(direction, InteractionType.REPRODUCE);
                }
            }
            return GameAction.waitAction();
        }
        @Override public String name() { return "EVOLUTION_CELL_POLICY"; }
    }

    /**
     * Situation-aware scorer: food seeking, predation and reproduction compete in one fixed action set.
     * All probability mass remains on legal actions; learned Q-values are added by the selector.
     */
    /**
     * Genetic INPUT-OPERATION-OUTPUT evaluator. Perception is already filtered by the
     * inherited sensor genes. Operation genes weight food-seeking, prey-seeking, threat
     * avoidance, reproduction and exploration. The resulting action is still selected only
     * from the shared, validated legal action list.
     */
    static final class EvolutionActionEvaluator implements ActionEvaluator {
        private final DecisionContext context;
        private final ObjectView self;
        private final Genome genes;
        private final ObjectView nearestFood;
        private final ObjectView nearestEnemy;
        private final ObjectView nearestBuilding;
        private final GameAction controllerRecommendation;

        EvolutionActionEvaluator(DecisionContext context) {
            this.context = context;
            this.self = context.self();
            this.genes = self.genome() == null ? Genome.defaults() : self.genome();
            this.controllerRecommendation = new EvolutionController().chooseAction(context.perception());
            this.nearestFood = context.perception().visibleObjects().stream()
                    .filter(o -> o.type().equals("FOOD"))
                    .min(Comparator.comparingInt(o -> manhattan(self.x(), self.y(), o.x(), o.y())))
                    .orElse(null);
            this.nearestEnemy = context.perception().visibleObjects().stream()
                    .filter(o -> o.type().equals("CREATURE") && o.value("team") != self.value("team"))
                    .min(Comparator.comparingInt(o -> manhattan(self.x(), self.y(), o.x(), o.y())))
                    .orElse(null);
            this.nearestBuilding = context.perception().visibleObjects().stream()
                    .filter(o -> o.type().equals("BUILDING") && o.value("team") == self.value("team"))
                    .min(Comparator.comparingInt(o -> manhattan(self.x(), self.y(), o.x(), o.y())))
                    .orElse(null);
        }

        @Override public double score(DecisionContext ignored, GameAction action) {
            double energy = self.value("energy");
            double threshold = Math.max(1.0, self.value("reproductionThreshold"));
            double hunger = 1.0 - Math.min(1.0, energy / threshold);
            double selfPower = self.hp() + self.value("attackDamage") * 1.5 + energy * 0.03;
            double score = 0.0;

            if (action.kind() == ActionKind.WAIT) {
                score = 0.02 + (controllerRecommendation.equals(action) ? 0.08 : 0.0);
                return score;
            }

            if (action.kind() == ActionKind.MOVE) {
                int nx = self.x() + action.direction().dx;
                int ny = self.y() + action.direction().dy;
                score = genes.exploration() * openNeighbourBonus(nx, ny) * 0.10;
                if (nearestFood != null) {
                    int before = manhattan(self.x(), self.y(), nearestFood.x(), nearestFood.y());
                    int after = manhattan(nx, ny, nearestFood.x(), nearestFood.y());
                    score += (before - after) * genes.foodSeek() * (0.22 + 0.72 * hunger);
                }
                if (self.value("inventoryResources") > 0.0 && nearestBuilding != null) {
                    int before = manhattan(self.x(), self.y(), nearestBuilding.x(), nearestBuilding.y());
                    int after = manhattan(nx, ny, nearestBuilding.x(), nearestBuilding.y());
                    double delivery = 0.48 + Math.min(0.55, self.value("inventoryResources") * 0.08);
                    if (hunger > 0.65) delivery *= 0.45;
                    score += (before - after) * delivery;
                }
                if (nearestEnemy != null) {
                    int before = manhattan(self.x(), self.y(), nearestEnemy.x(), nearestEnemy.y());
                    int after = manhattan(nx, ny, nearestEnemy.x(), nearestEnemy.y());
                    double enemyPower = nearestEnemy.hp() + nearestEnemy.value("attackDamage") * 1.5
                            + nearestEnemy.value("energy") * 0.03;
                    if (selfPower >= enemyPower * 1.08) {
                        score += (before - after) * genes.preySeek() * 0.16;
                    } else {
                        score += (after - before) * genes.threatAvoid() * 0.20;
                    }
                }
                if (controllerRecommendation.equals(action)) score += 0.10;
                return score;
            }

            ObjectView target = context.perception().at(self.x() + action.direction().dx,
                    self.y() + action.direction().dy).stream().findFirst().orElse(null);
            switch (action.interactionType()) {
                case COLLECT -> {
                    if (target != null && target.type().equals("FOOD"))
                        score = 1.15 + genes.foodSeek() * self.value("foodEfficiency") * (0.65 + 1.05 * hunger);
                }
                case DEPOSIT -> {
                    if (target != null && target.type().equals("BUILDING")
                            && target.value("team") == self.value("team")
                            && self.value("inventoryResources") >= 1.0)
                        score = hunger > 0.70 ? 0.40
                                : 1.20 + Math.min(0.45, self.value("inventoryResources") * 0.05);
                }
                case ATTACK -> {
                    if (target != null && target.type().equals("CREATURE")
                            && target.value("team") != self.value("team")) {
                        double targetPower = target.hp() + target.value("attackDamage") * 1.5
                                + target.value("energy") * 0.03;
                        double relative = selfPower - targetPower;
                        score = genes.preySeek() * (0.35 + self.value("aggression") * 0.55);
                        if (relative >= 0.0) score += 0.55 + Math.min(0.8, relative * 0.08);
                        else score -= genes.threatAvoid() * Math.min(1.0, Math.abs(relative) * 0.08);
                        if (target.hp() <= self.value("attackDamage")) score += 0.75;
                    }
                }
                case REPRODUCE -> {
                    if (energy >= threshold) {
                        double surplus = Math.max(0.0, energy / threshold - 1.0);
                        score = 1.45 + genes.reproductionDrive() * 0.35 + Math.min(0.45, surplus * 0.22);
                    }
                }
                case BUILD -> {
                    boolean safeReserve = energy >= threshold + CREATURE_BUILD_ENERGY_COST + 7.0;
                    boolean urgentFood = nearestFood != null
                            && manhattan(self.x(), self.y(), nearestFood.x(), nearestFood.y()) <= 3
                            && hunger > 0.10;
                    boolean alreadyServed = nearestBuilding != null
                            && manhattan(self.x(), self.y(), nearestBuilding.x(), nearestBuilding.y()) <= 12;
                    if (safeReserve && !urgentFood && !alreadyServed
                            && self.value("inventoryResources") < 2.0
                            && self.value("structuresBuilt") < 1.0)
                        score = 0.04 + genes.exploration() * 0.025;
                }
                case USE -> score = genes.exploration() * 0.02;
                default -> score = -0.12;
            }
            if (controllerRecommendation.equals(action)) score += 0.12;
            return score;
        }

        private double openNeighbourBonus(int x, int y) {
            int open = 0;
            for (Direction direction : Direction.values()) {
                int nx = x + direction.dx, ny = y + direction.dy;
                if (context.perception().isVisible(nx, ny) && context.perception().at(nx, ny).isEmpty()) open++;
            }
            return open / 4.0;
        }

        @Override public String name() { return "GENETIC_IOO_EVALUATOR"; }

        private static int manhattan(int x1, int y1, int x2, int y2) {
            return Math.abs(x1 - x2) + Math.abs(y1 - y2);
        }
    }

    /** Converts a current rule-based controller's preference into a numeric score over the canonical action options. */
    static final class ControllerPreferenceEvaluator implements ActionEvaluator {
        private final GameAction preferred;
        private final String sourceName;

        ControllerPreferenceEvaluator(ObjectController controller, DecisionContext context) {
            this.preferred = controller == null ? GameAction.waitAction()
                    : Objects.requireNonNullElse(controller.chooseAction(context.perception()), GameAction.waitAction());
            this.sourceName = controller == null ? "DEFAULT_WAIT" : controller.name();
        }

        @Override public double score(DecisionContext context, GameAction action) {
            return preferred.equals(action) ? 1.0 : 0.0;
        }
        @Override public String name() { return "PREFERENCE(" + sourceName + ")"; }
    }

    /** Chooses the highest-scoring legal option; ties keep canonical action ordering. */
    static final class GreedyDecisionAlgorithm implements DecisionAlgorithm {
        @Override public GameAction chooseAction(DecisionContext context, ActionEvaluator evaluator) {
            GameAction best = GameAction.waitAction();
            double bestScore = Double.NEGATIVE_INFINITY;
            List<GameAction> candidates = context.legalOptions().isEmpty()
                    ? List.of(GameAction.waitAction()) : context.legalOptions();
            for (GameAction action : candidates) {
                double score = evaluator.score(context, action);
                if (Double.isNaN(score)) continue;
                if (score > bestScore) {
                    bestScore = score;
                    best = action;
                }
            }
            return best;
        }
        @Override public String name() { return "GREEDY_POLICY"; }
    }

    /** Uniform random baseline over currently legal actions, useful as an AI comparison baseline. */
    static final class RandomLegalDecisionAlgorithm implements DecisionAlgorithm {
        private final Random random;
        RandomLegalDecisionAlgorithm(long seed) { random = new Random(seed); }

        @Override public GameAction chooseAction(DecisionContext context, ActionEvaluator evaluator) {
            List<GameAction> candidates = context.legalOptions().isEmpty()
                    ? List.of(GameAction.waitAction()) : context.legalOptions();
            return candidates.get(random.nextInt(candidates.size()));
        }
        @Override public String name() { return "RANDOM_LEGAL"; }
    }

    /** Epsilon-greedy baseline: exploit the evaluator most of the time, otherwise explore a legal option. */
    static final class EpsilonGreedyDecisionAlgorithm implements DecisionAlgorithm {
        private final double epsilon;
        private final Random random;
        private final GreedyDecisionAlgorithm greedy = new GreedyDecisionAlgorithm();
        private final RandomLegalDecisionAlgorithm exploratory;

        EpsilonGreedyDecisionAlgorithm(double epsilon, long seed) {
            if (!Double.isFinite(epsilon) || epsilon < 0.0 || epsilon > 1.0)
                throw new IllegalArgumentException("epsilon must be between 0 and 1");
            this.epsilon = epsilon;
            this.random = new Random(seed);
            this.exploratory = new RandomLegalDecisionAlgorithm(seed ^ 0x9E3779B97F4A7C15L);
        }

        @Override public GameAction chooseAction(DecisionContext context, ActionEvaluator evaluator) {
            return random.nextDouble() < epsilon
                    ? exploratory.chooseAction(context, evaluator)
                    : greedy.chooseAction(context, evaluator);
        }
        @Override public String name() { return String.format(Locale.ROOT, "EPSILON_GREEDY(%.2f)", epsilon); }
    }

    /**
     * Adaptive softmax selector. Controller scores and learned action values form logits.
     * epsilon reserves a non-zero probability for every currently legal action.
     */
    static final class AdaptiveSoftmaxDecisionAlgorithm implements DecisionAlgorithm {
        private final double epsilon;
        private final double temperature;
        private final Random random;

        AdaptiveSoftmaxDecisionAlgorithm(double epsilon, double temperature, long seed) {
            if (!Double.isFinite(epsilon) || epsilon <= 0.0 || epsilon > 1.0)
                throw new IllegalArgumentException("epsilon must be in (0,1]");
            if (!Double.isFinite(temperature) || temperature <= 0.0)
                throw new IllegalArgumentException("temperature must be positive");
            this.epsilon = epsilon;
            this.temperature = temperature;
            this.random = new Random(seed);
        }

        Map<GameAction, Double> probabilities(DecisionContext context, ActionEvaluator evaluator) {
            List<GameAction> candidates = context.legalOptions().isEmpty()
                    ? List.of(GameAction.waitAction()) : context.legalOptions();
            double[] logits = new double[candidates.size()];
            double max = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < candidates.size(); i++) {
                GameAction action = candidates.get(i);
                double preference = evaluator.score(context, action);
                double learned = context.self().learnedActionValues().getOrDefault(action.key(), 0.0);
                double logit = (preference + learned) / temperature;
                if (!Double.isFinite(logit)) logit = -50.0;
                logits[i] = logit;
                max = Math.max(max, logit);
            }
            double sum = 0.0;
            double[] softmax = new double[logits.length];
            for (int i = 0; i < logits.length; i++) {
                softmax[i] = Math.exp(Math.max(-60.0, logits[i] - max));
                sum += softmax[i];
            }
            Map<GameAction, Double> result = new LinkedHashMap<>();
            for (int i = 0; i < candidates.size(); i++) {
                double probability = epsilon / candidates.size() + (1.0 - epsilon) * softmax[i] / sum;
                result.put(candidates.get(i), probability);
            }
            return Collections.unmodifiableMap(result);
        }

        @Override public GameAction chooseAction(DecisionContext context, ActionEvaluator evaluator) {
            Map<GameAction, Double> p = probabilities(context, evaluator);
            double sample = random.nextDouble();
            double cumulative = 0.0;
            for (Map.Entry<GameAction, Double> entry : p.entrySet()) {
                cumulative += entry.getValue();
                if (sample <= cumulative) return entry.getKey();
            }
            return p.keySet().stream().reduce((first, second) -> second).orElse(GameAction.waitAction());
        }
        @Override public String name() {
            return String.format(Locale.ROOT, "ADAPTIVE_SOFTMAX(e=%.2f,T=%.2f)", epsilon, temperature);
        }
    }

    static final class IdleController implements ObjectController {
        @Override public GameAction chooseAction(Perception perception) {
            return GameAction.waitAction();
        }
        @Override public String name() { return "IDLE"; }
    }

    static final class GravityController implements ObjectController {
        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            int belowX = self.x();
            int belowY = self.y() + 1;
            for (ObjectView object : perception.at(belowX, belowY)) {
                if (object.type().equals("PLAYER")) return GameAction.interact(Direction.DOWN, InteractionType.ATTACK);
            }
            if (!perception.isBlocked(belowX, belowY, self.id())) return GameAction.move(Direction.DOWN);
            return GameAction.waitAction();
        }
        @Override public String name() { return "GRAVITY_DOWN"; }
    }

    static final class WanderController implements ObjectController {
        private final Random random;
        WanderController(long seed) { random = new Random(seed); }
        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            // Adjacent player: select the explicit directional ATTACK interaction.
            for (Direction direction : Direction.values()) {
                for (ObjectView target : perception.at(self.x() + direction.dx, self.y() + direction.dy)) {
                    if (target.type().equals("PLAYER")) return GameAction.interact(direction, InteractionType.ATTACK);
                }
            }
            Direction[] directions = Direction.values();
            int start = random.nextInt(directions.length);
            for (int i = 0; i < directions.length; i++) {
                Direction d = directions[(start + i) % directions.length];
                if (!perception.isBlocked(self.x() + d.dx, self.y() + d.dy, self.id())) return GameAction.move(d);
            }
            return GameAction.waitAction();
        }
        @Override public String name() { return "RANDOM_MOVE_THEN_INTERACT_ATTACK"; }
    }

    /** Team-aware controller for produced heavy units: attack hostiles, otherwise close distance. */
    static final class GuardController implements ObjectController {
        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            ObjectView nearest = null;
            int best = Integer.MAX_VALUE;
            for (Direction d : Direction.values()) {
                for (ObjectView target : perception.at(self.x() + d.dx, self.y() + d.dy)) {
                    if (hostile(self, target) && target.has(Capability.ATTACKABLE))
                        return GameAction.interact(d, InteractionType.ATTACK);
                }
            }
            for (ObjectView target : perception.visibleObjects()) {
                if (!hostile(self, target) || !target.has(Capability.ATTACKABLE)) continue;
                int distance = Math.abs(self.x() - target.x()) + Math.abs(self.y() - target.y());
                if (distance < best) { best = distance; nearest = target; }
            }
            if (nearest == null) return GameAction.waitAction();
            Direction bestDirection = null;
            int bestDistance = best;
            for (Direction d : Direction.values()) {
                int nx = self.x() + d.dx, ny = self.y() + d.dy;
                if (perception.isBlocked(nx, ny, self.id())) continue;
                int distance = Math.abs(nx - nearest.x()) + Math.abs(ny - nearest.y());
                if (distance < bestDistance) { bestDistance = distance; bestDirection = d; }
            }
            return bestDirection == null ? GameAction.waitAction() : GameAction.move(bestDirection);
        }
        private boolean hostile(ObjectView self, ObjectView other) {
            return !self.id().equals(other.id()) && other.alive()
                    && self.value("team") != other.value("team")
                    && (other.type().equals("PLAYER") || other.type().equals("HUNTER")
                        || other.type().equals("WANDERER") || other.type().equals("BRUISER")
                        || other.type().equals("CREATURE"));
        }
        @Override public String name() { return "TEAM_AWARE_GUARD"; }
    }

    /** Patrols until the player enters the detection radius, then chases and attacks. */
    static final class HybridHunterController implements ObjectController {
        private final int detectionRadius;
        private int patrolIndex;
        private static final Direction[] PATROL = {Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP};

        HybridHunterController(int detectionRadius) { this.detectionRadius = detectionRadius; }

        @Override public GameAction chooseAction(Perception perception) {
            ObjectView self = perception.self();
            ObjectView player = null;
            int bestDistance = Integer.MAX_VALUE;
            for (ObjectView object : perception.visibleObjects()) {
                if (!object.alive() || !object.type().equals("PLAYER")) continue;
                int distance = manhattan(self.x(), self.y(), object.x(), object.y());
                if (distance < bestDistance) { bestDistance = distance; player = object; }
            }
            if (player != null && bestDistance <= detectionRadius) {
                if (bestDistance == 1) return GameAction.interact(directionTo(self.x(), self.y(), player.x(), player.y()), InteractionType.ATTACK);
                Direction step = findPathStep(perception, self, player);
                if (step != null) return GameAction.move(step);
            }
            for (int i = 0; i < PATROL.length; i++) {
                Direction d = PATROL[(patrolIndex + i) % PATROL.length];
                if (!perception.isBlocked(self.x() + d.dx, self.y() + d.dy, self.id())) {
                    patrolIndex = (patrolIndex + i + 1) % PATROL.length;
                    return GameAction.move(d);
                }
            }
            return GameAction.waitAction();
        }

        private static Direction findPathStep(Perception perception, ObjectView start, ObjectView target) {
            record Node(int x, int y, Direction first) { }
            Queue<Node> queue = new ArrayDeque<>();
            Set<Long> visited = new HashSet<>();
            queue.add(new Node(start.x(), start.y(), null));
            visited.add(key(start.x(), start.y()));
            Direction[] order = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
            while (!queue.isEmpty()) {
                Node node = queue.remove();
                if (manhattan(node.x(), node.y(), target.x(), target.y()) == 1) return node.first();
                for (Direction d : order) {
                    int nx = node.x() + d.dx;
                    int ny = node.y() + d.dy;
                    long key = key(nx, ny);
                    if (visited.contains(key) || !perception.isVisible(nx, ny)
                            || perception.isBlocked(nx, ny, start.id())) continue;
                    visited.add(key);
                    Direction first = node.first() == null ? d : node.first();
                    queue.add(new Node(nx, ny, first));
                }
            }
            return null;
        }

        private static long key(int x, int y) { return (((long) x) << 32) ^ (y & 0xffffffffL); }
        private static int manhattan(int x1, int y1, int x2, int y2) { return Math.abs(x1 - x2) + Math.abs(y1 - y2); }
        private static Direction directionTo(int x1, int y1, int x2, int y2) {
            for (Direction d : Direction.values()) if (x1 + d.dx == x2 && y1 + d.dy == y2) return d;
            return Direction.UP;
        }
        @Override public String name() { return "HYBRID_PATROL_CHASE"; }
    }

    // ---------------------------------------------------------------------
    // 4) Infinity-style camera and generic object renderer
    // ---------------------------------------------------------------------

    static final class WorldCanvas extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final double MIN_ZOOM = 0.15;
        private static final double MAX_ZOOM = 300.0;
        private final transient GameWorld world;
        private double cameraX;
        private double cameraY;
        private double zoom = 28.0;
        private boolean panning;
        private boolean paused;
        private int lastMouseX;
        private int lastMouseY;
        private String selectedObjectId = "player";
        private ActionMode actionMode = ActionMode.MOVE;
        private boolean playerMode;

        WorldCanvas(GameWorld world) {
            this.world = world;
            cameraX = world.width / 2.0;
            cameraY = world.height / 2.0;
            setPreferredSize(new Dimension(1120, 700));
            setBackground(new Color(7, 12, 19));
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        panning = true;
                        lastMouseX = e.getX();
                        lastMouseY = e.getY();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        GameObject object = findObjectAtScreen(e.getX(), e.getY());
                        if (object != null) {
                            selectedObjectId = object.id;
                            thisSelectCallback(object.id);
                        }
                    }
                }
                @Override public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        panning = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    }
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    if (!panning) return;
                    cameraX -= (e.getX() - lastMouseX) / zoom;
                    cameraY -= (e.getY() - lastMouseY) / zoom;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    repaint();
                }
            });
            addMouseWheelListener(this::zoomAtCursor);
        }

        private void thisSelectCallback(String id) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MatrixgameUniversalGridLab_v10_0 lab) lab.selectObject(id);
        }

        void setSelectedObjectId(String id) { selectedObjectId = id; }
        void setPaused(boolean paused) { this.paused = paused; }
        void setActionMode(ActionMode mode) { actionMode = mode; repaint(); }
        void setPlayerMode(boolean enabled) { playerMode = enabled; repaint(); }

        void resetCamera() {
            fitWorld();
        }

        /** Moves only the camera in world-cell units; simulation entities remain untouched. */
        void panByCells(double dx, double dy) {
            if (!Double.isFinite(dx) || !Double.isFinite(dy)) return;
            cameraX += dx;
            cameraY += dy;
            repaint();
        }

        /** Zooms around the viewport centre while respecting the established zoom limits. */
        void zoomBy(double factor) {
            if (!Double.isFinite(factor) || factor <= 0.0) return;
            zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));
            repaint();
        }

        /** Fits the complete finite simulation region into the viewport. The camera never changes world coordinates. */
        void fitWorld() {
            cameraX = world.width / 2.0;
            cameraY = world.height / 2.0;
            int viewportWidth = getWidth() > 0 ? getWidth() : getPreferredSize().width;
            int viewportHeight = getHeight() > 0 ? getHeight() : getPreferredSize().height;
            double fitX = (viewportWidth - 48.0) / world.width;
            double fitY = (viewportHeight - 48.0) / world.height;
            zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, Math.min(fitX, fitY)));
            repaint();
        }

        private void zoomAtCursor(MouseWheelEvent e) {
            double beforeX = screenToWorldX(e.getX());
            double beforeY = screenToWorldY(e.getY());
            double factor = Math.pow(1.12, -e.getPreciseWheelRotation());
            zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));
            cameraX = beforeX - (e.getX() - getWidth() / 2.0) / zoom;
            cameraY = beforeY - (e.getY() - getHeight() / 2.0) / zoom;
            repaint();
        }

        private double screenToWorldX(int screenX) {
            return cameraX + (screenX - getWidth() / 2.0) / zoom;
        }
        private double screenToWorldY(int screenY) {
            return cameraY + (screenY - getHeight() / 2.0) / zoom;
        }
        private int worldToScreenX(double worldX) {
            return (int) Math.round(getWidth() / 2.0 + (worldX - cameraX) * zoom);
        }
        private int worldToScreenY(double worldY) {
            return (int) Math.round(getHeight() / 2.0 + (worldY - cameraY) * zoom);
        }

        private GameObject findObjectAtScreen(int sx, int sy) {
            int cellX = (int) Math.floor(screenToWorldX(sx));
            int cellY = (int) Math.floor(screenToWorldY(sy));
            List<GameObject> at = world.objectsAt(cellX, cellY);
            if (at.isEmpty()) return null;
            // Prefer non-terrain / interactive entities over dirt and boundary tiles.
            for (int i = at.size() - 1; i >= 0; i--) {
                GameObject object = at.get(i);
                if (!object.type.equals("DIRT") && !object.type.equals("WALL")) return object;
            }
            return at.get(at.size() - 1);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawGrid(g2);
            drawWorldObjects(g2);
            drawOverlay(g2);
            if (world.evolutionMode) drawEvolutionChart(g2);
            g2.dispose();
        }

        private void drawGrid(Graphics2D g2) {
            int left = (int) Math.floor(screenToWorldX(0)) - 1;
            int right = (int) Math.ceil(screenToWorldX(getWidth())) + 1;
            int top = (int) Math.floor(screenToWorldY(0)) - 1;
            int bottom = (int) Math.ceil(screenToWorldY(getHeight())) + 1;
            int step = zoom < 2.0 ? 10 : zoom < 7.0 ? 5 : 1;
            g2.setColor(new Color(28, 40, 53));
            g2.setStroke(new BasicStroke(1f));
            for (int x = Math.floorDiv(left, step) * step; x <= right; x += step) {
                int sx = worldToScreenX(x);
                g2.drawLine(sx, 0, sx, getHeight());
            }
            for (int y = Math.floorDiv(top, step) * step; y <= bottom; y += step) {
                int sy = worldToScreenY(y);
                g2.drawLine(0, sy, getWidth(), sy);
            }
            g2.setColor(new Color(68, 95, 118));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(worldToScreenX(0), 0, worldToScreenX(0), getHeight());
            g2.drawLine(0, worldToScreenY(0), getWidth(), worldToScreenY(0));
        }

        private void drawWorldObjects(Graphics2D g2) {
            List<GameObject> drawOrder = new ArrayList<>(world.objects);
            drawOrder.sort(Comparator.comparingInt((GameObject o) -> o.y).thenComparingInt(o -> o.x));
            for (GameObject object : drawOrder) {
                if (!object.alive) continue;
                int sx = worldToScreenX(object.x);
                int sy = worldToScreenY(object.y);
                int ex = worldToScreenX(object.x + 1);
                int ey = worldToScreenY(object.y + 1);
                int w = Math.max(1, ex - sx);
                int h = Math.max(1, ey - sy);
                if (ex < 0 || ey < 0 || sx > getWidth() || sy > getHeight()) continue;
                drawObject(g2, object, sx, sy, w, h);
            }
        }

        private void drawEvolutionChart(Graphics2D g2) {
            List<int[]> history = world.populationHistory;
            if (history.size() < 2) return;
            int panelWidth = 360;
            int panelHeight = 112;
            int x = getWidth() - panelWidth - 14;
            int y = 42;
            g2.setColor(new Color(3, 8, 13, 218));
            g2.fillRoundRect(x, y, panelWidth, panelHeight, 10, 10);
            g2.setColor(new Color(95, 125, 150, 190));
            g2.drawRoundRect(x, y, panelWidth, panelHeight, 10, 10);
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10));
            g2.setColor(new Color(210, 225, 238));
            g2.drawString("POPULATION / TEAM", x + 10, y + 15);

            int max = 1;
            for (int[] sample : history) {
                for (int team = 1; team <= TEAM_COUNT; team++) max = Math.max(max, sample[team]);
            }
            int left = x + 10, top = y + 24, graphWidth = panelWidth - 20, graphHeight = 66;
            g2.setColor(new Color(55, 70, 85));
            g2.drawLine(left, top + graphHeight, left + graphWidth, top + graphHeight);
            g2.drawLine(left, top, left, top + graphHeight);
            Color[] colors = {new Color(70, 165, 245), new Color(238, 94, 102),
                    new Color(86, 202, 135), new Color(190, 125, 235),
                    new Color(245, 190, 65), new Color(65, 210, 205)};
            for (int team = 0; team < TEAM_COUNT; team++) {
                g2.setColor(colors[team]);
                for (int i = 1; i < history.size(); i++) {
                    int x1 = left + (i - 1) * graphWidth / Math.max(1, history.size() - 1);
                    int x2 = left + i * graphWidth / Math.max(1, history.size() - 1);
                    int y1 = top + graphHeight - history.get(i - 1)[team + 1] * graphHeight / max;
                    int y2 = top + graphHeight - history.get(i)[team + 1] * graphHeight / max;
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 9));
            int legendY = y + panelHeight - 8;
            int legendX = x + 12;
            for (int team = 0; team < TEAM_COUNT; team++) {
                g2.setColor(colors[team]);
                g2.fillRect(legendX, legendY - 7, 7, 7);
                g2.drawString("T" + (team + 1) + ":" + world.teamPopulationCount(team), legendX + 10, legendY);
                legendX += 55;
            }
        }

        private void drawObject(Graphics2D g2, GameObject object, int x, int y, int w, int h) {
            int inset = Math.max(1, (int) Math.round(zoom * 0.07));
            int ix = x + inset, iy = y + inset;
            int iw = Math.max(1, w - inset * 2), ih = Math.max(1, h - inset * 2);
            if (object.type.equals("DIRT")) {
                g2.setColor(object.color);
                g2.fillRect(ix, iy, iw, ih);
                g2.setColor(new Color(176, 124, 82));
                if (iw > 9 && ih > 5) g2.drawLine(ix + 2, iy + ih / 2, ix + iw - 2, iy + ih / 2);
            } else if (object.type.equals("WALL")) {
                g2.setColor(object.color);
                g2.fillRect(ix, iy, iw, ih);
                g2.setColor(new Color(120, 143, 163));
                if (iw > 8 && ih > 8) g2.drawRect(ix + 2, iy + 2, iw - 4, ih - 4);
            } else if (object.type.equals("ROCK")) {
                g2.setColor(object.color);
                g2.fillOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                g2.setColor(new Color(220, 230, 239));
                if (iw > 9) g2.drawOval(ix + iw / 4, iy + ih / 4, Math.max(2, iw / 2), Math.max(2, ih / 2));
            } else if (object.type.equals("GEM")) {
                g2.setColor(object.color);
                int[] xs = {x + w / 2, x + w * 4 / 5, x + w / 2, x + w / 5};
                int[] ys = {y + h / 6, y + h / 2, y + h * 5 / 6, y + h / 2};
                g2.fillPolygon(xs, ys, 4);
                int stack = (int) object.value("stack");
                if (stack > 1 && w > 14 && h > 14) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, Math.max(10, Math.min(16, w / 2))));
                    g2.drawString(Integer.toString(stack), x + w - 12, y + 13);
                }
            } else if (object.type.equals("FOOD")) {
                g2.setColor(object.color);
                int cx = x + w / 2, cy = y + h / 2;
                int r = Math.max(2, Math.min(w, h) / 4);
                int[] xs = {cx, cx + r, cx, cx - r};
                int[] ys = {cy - r, cy, cy + r, cy};
                g2.fillPolygon(xs, ys, 4);
                if (w > 15 && h > 15) {
                    g2.setColor(new Color(220, 255, 220));
                    g2.drawOval(cx - 1, cy - 1, 2, 2);
                }
            } else if (object.type.equals("EXIT")) {
                g2.setColor(object.color);
                g2.fillRoundRect(ix, iy, iw, ih, Math.max(3, iw / 4), Math.max(3, ih / 4));
                g2.setColor(Color.WHITE);
                if (iw > 9 && ih > 9) g2.drawRect(ix + iw / 4, iy + ih / 6, iw / 2, ih * 2 / 3);
            } else if (object.type.equals("BUILDING")) {
                g2.setColor(object.color);
                g2.fillRect(ix, iy, iw, ih);
                g2.setColor(new Color(65, 45, 25));
                if (iw > 12 && ih > 12) {
                    g2.drawRect(ix + iw / 5, iy + ih / 5, iw * 3 / 5, ih * 3 / 5);
                    g2.drawLine(ix + iw / 2, iy + ih / 5, ix + iw / 2, iy + ih * 4 / 5);
                }
                if (iw > 22 && ih > 22) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, Math.max(9, Math.min(12, iw / 4))));
                    g2.drawString(Integer.toString((int) object.value("storedResources")), ix + 2, iy + ih - 3);
                }
            } else if (object.type.equals("DEPOT")) {
                g2.setColor(object.color);
                g2.fillRoundRect(ix, iy, iw, ih, Math.max(3, iw / 5), Math.max(3, ih / 5));
                g2.setColor(new Color(45, 35, 24));
                if (iw > 12 && ih > 12) {
                    g2.drawRect(ix + iw / 5, iy + ih / 5, iw * 3 / 5, ih * 3 / 5);
                    g2.drawString(Integer.toString((int) object.value("storedGems")),
                            ix + 2, iy + ih - 3);
                }
            } else if (object.type.equals("BRUISER")) {
                g2.setColor(object.color);
                g2.fillRect(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                g2.setColor(Color.WHITE);
                if (iw > 12 && ih > 12) g2.drawOval(ix + iw / 4, iy + ih / 4, iw / 2, ih / 2);
            } else if (object.type.equals("PLAYER")) {
                g2.setColor(object.color);
                g2.fillOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                g2.setColor(Color.BLACK);
                if (iw > 10) g2.drawOval(ix + iw / 4, iy + ih / 4, iw / 2, ih / 2);
            } else if (object.type.equals("CREATURE")) {
                g2.setColor(object.color);
                g2.fillOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                if (object.value("unitLevel") >= 2.0) {
                    g2.setColor(new Color(255, 238, 150));
                    g2.setStroke(new BasicStroke(Math.max(1.2f, (float) (zoom * 0.09))));
                    g2.drawOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                }
                g2.setColor(new Color(12, 18, 25));
                if (iw > 10) {
                    g2.fillOval(ix + iw / 4, iy + ih / 3, Math.max(2, iw / 8), Math.max(2, ih / 8));
                    g2.fillOval(ix + iw * 5 / 8, iy + ih / 3, Math.max(2, iw / 8), Math.max(2, ih / 8));
                }
                if (iw > 24) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 9));
                    String label = object.value("unitLevel") >= 2.0 ? "L2" : "G" + (int) object.value("generation");
                    g2.drawString(label, ix + 2, iy + ih - 3);
                }
            } else if (object.type.equals("HUNTER")) {
                g2.setColor(object.color);
                g2.fillOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
                g2.setColor(Color.WHITE);
                if (iw > 10) {
                    g2.fillOval(ix + iw / 4, iy + ih / 3, Math.max(2, iw / 7), Math.max(2, ih / 7));
                    g2.fillOval(ix + iw * 3 / 5, iy + ih / 3, Math.max(2, iw / 7), Math.max(2, ih / 7));
                }
            } else {
                g2.setColor(object.color);
                g2.fillOval(ix + 1, iy + 1, Math.max(1, iw - 2), Math.max(1, ih - 2));
            }

            if (object.id.equals(selectedObjectId)) {
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setStroke(new BasicStroke(Math.max(1f, (float) (zoom * 0.04))));
                g2.drawRect(x + 1, y + 1, Math.max(1, w - 2), Math.max(1, h - 2));
            }
        }

        private void drawOverlay(Graphics2D g2) {
            // Keep the canvas HUD above the playfield so that it never hides GameObjects.
            g2.setColor(new Color(3, 8, 13, 225));
            g2.fillRoundRect(10, 5, Math.min(getWidth() - 20, 1050), 58, 9, 9);
            g2.setColor(new Color(100, 235, 190));
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
            g2.drawString("MATRIXGAME UNIVERSAL GRID LAB v0.10.0", 20, 21);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            String modeStats = world.evolutionMode
                    ? String.format(Locale.ROOT, "Pop:%d Births:%d Deaths:%d", world.creatureCount(), world.births, world.deaths)
                    : String.format(Locale.ROOT, "Objects:%d", world.objects.size());
            g2.drawString(String.format(Locale.ROOT,
                    "%s | Action:%s | Tick:%d | %s | %dx%d | Zoom:%.2fx | Cam:(%.1f,%.1f)%s",
                    world.evolutionMode ? "EVOLUTION" : (playerMode ? "PLAYER" : "EDITOR"),
                    actionMode.label, world.tick, modeStats, world.width, world.height, zoom, cameraX, cameraY,
                    paused ? " | PAUSE" : ""), 20, 38);
            g2.setColor(new Color(190, 205, 218));
            g2.drawString("Y BUILD | C BUILD_DEPOT | X DEPOSIT | Z PRODUCE | Gebäude sind unbeweglich",
                    20, 53);
        }
    }

    // ---------------------------------------------------------------------
    // 5) Deterministic headless tests
    // ---------------------------------------------------------------------

    private static void runSelfTests() {
        int passed = 0;

        // Camera navigation must not mutate simulation coordinates or entity state.
        GameWorld cameraWorld = emptyWorld(10, 10);
        GameObject cameraPlayer = cameraWorld.getObject("player");
        int entityXBeforeCameraMove = cameraPlayer.x;
        int entityYBeforeCameraMove = cameraPlayer.y;
        WorldCanvas cameraProbe = new WorldCanvas(cameraWorld);
        double cameraXBefore = cameraProbe.cameraX;
        double cameraYBefore = cameraProbe.cameraY;
        cameraProbe.panByCells(3.0, -2.0);
        check(cameraProbe.cameraX == cameraXBefore + 3.0
                        && cameraProbe.cameraY == cameraYBefore - 2.0
                        && cameraPlayer.x == entityXBeforeCameraMove
                        && cameraPlayer.y == entityYBeforeCameraMove,
                "Camera panning must move only the view, never simulation entities"); passed++;

        double zoomBefore = cameraProbe.zoom;
        cameraProbe.zoomBy(1.12);
        check(Math.abs(cameraProbe.zoom - zoomBefore * 1.12) < 1.0e-9,
                "Keyboard zoom must apply its requested positive factor"); passed++;

        double zoomAfterValidChange = cameraProbe.zoom;
        double cameraXAfterValidMove = cameraProbe.cameraX;
        cameraProbe.zoomBy(Double.NaN);
        cameraProbe.panByCells(Double.POSITIVE_INFINITY, 1.0);
        check(cameraProbe.zoom == zoomAfterValidChange && cameraProbe.cameraX == cameraXAfterValidMove,
                "Non-finite camera input must be ignored safely"); passed++;

        cameraProbe.zoomBy(1.0e9);
        check(cameraProbe.zoom == WorldCanvas.MAX_ZOOM,
                "Camera zoom must respect the established maximum"); passed++;
        cameraProbe.zoomBy(1.0e-9);
        check(cameraProbe.zoom == WorldCanvas.MIN_ZOOM,
                "Camera zoom must respect the established minimum"); passed++;

        List<GameAction> catalog = GameAction.allActionOptions();
        check(catalog.size() == 49, "Action catalog must contain 1 WAIT + 4 MOVE + 44 typed interactions"); passed++;
        check(catalog.contains(GameAction.waitAction())
                        && catalog.contains(GameAction.move(Direction.UP))
                        && catalog.contains(GameAction.interact(Direction.LEFT, InteractionType.DIG))
                        && catalog.contains(GameAction.interact(Direction.DOWN, InteractionType.BUILD)),
                "Catalog must include WAIT, MOVE and every directional interaction family"); passed++;

        // HUMAN mode must suppress the unit's AI controller when no manual action is queued.
        GameWorld controlWorld = emptyWorld(10, 10);
        final int[] aiCalls = {0};
        GameObject controlled = controlWorld.object("test-unit", "TEST_UNIT", "Test Unit", "U", Color.CYAN,
                4, 4, true, 50, new ObjectController() {
                    @Override public GameAction chooseAction(Perception perception) {
                        aiCalls[0]++;
                        return GameAction.move(Direction.RIGHT);
                    }
                    @Override public String name() { return "COUNTING_TEST_CONTROLLER"; }
                });
        controlWorld.addObject(controlled);
        controlWorld.setHumanControlled(controlled.id, true);
        controlWorld.tick();
        check(aiCalls[0] == 0 && controlled.x == 4,
                "A human-controlled unit must not run its AI when no manual action is queued"); passed++;
        controlWorld.submitAction(controlled.id, GameAction.move(Direction.LEFT));
        controlWorld.tick();
        check(aiCalls[0] == 0 && controlled.x == 3,
                "A human-controlled unit must execute the human action without AI interference"); passed++;
        controlWorld.setHumanControlled(controlled.id, false);
        controlWorld.tick();
        check(aiCalls[0] > 0, "Switching back to AI must restore its controller"); passed++;

        // Direction and MOVE remain exactly one adjacent cell.
        GameWorld world = emptyWorld(10, 10);
        GameObject player = world.getObject("player");
        int startX = player.x, startY = player.y;
        world.submitAction(player.id, GameAction.move(Direction.RIGHT)); world.tick();
        check(player.x == startX + 1 && player.y == startY, "MOVE(RIGHT) must change only gridX by one"); passed++;

        // DIG requires both actor capability and a diggable target.
        world = emptyWorld(10, 10); player = world.getObject("player");
        GameObject dirt = world.object("dirt", "DIRT", "Dirt", "▪", Color.ORANGE,
                player.x + 1, player.y, true, 0, new IdleController()); world.addObject(dirt);
        DecisionContext digContext = world.decisionContext(player, world.snapshot());
        check(digContext.legalOptions().contains(GameAction.interact(Direction.RIGHT, InteractionType.DIG)),
                "PLAYER must receive DIG when adjacent to DIGGABLE dirt"); passed++;
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.DIG)); world.tick();
        check(world.getObject(dirt.id) == null, "DIG must remove the targeted dirt object"); passed++;

        // PUSH moves the rock one cell and advances the pusher into its former cell.
        world = emptyWorld(11, 9); player = world.getObject("player");
        int px = player.x, py = player.y;
        GameObject rock = world.object("rock", "ROCK", "Rock", "●", Color.GRAY,
                px + 1, py, true, 70, new GravityController()); world.addObject(rock);
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.PUSH)); world.tick();
        check(player.x == px + 1 && rock.x == px + 2 && rock.y == py,
                "PUSH must shift the rock and move the player into the old rock cell"); passed++;

        // Collect, stack, drop and build are separate actions.
        world = emptyWorld(11, 9); player = world.getObject("player");
        GameObject gem = world.object("gem", "GEM", "Gem", "◆", Color.CYAN,
                player.x + 1, player.y, false, 5, new IdleController()); world.addObject(gem);
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.COLLECT)); world.tick();
        check(player.value("inventoryGems") == 1 && world.getObject(gem.id) == null,
                "COLLECT must transfer one gem to inventory"); passed++;

        // Collect a second gem to fund BUILD.
        gem = world.object("gem", "GEM", "Gem 2", "◆", Color.CYAN,
                player.x, player.y - 1, false, 5, new IdleController()); world.addObject(gem);
        world.submitAction(player.id, GameAction.interact(Direction.UP, InteractionType.COLLECT)); world.tick();
        check(player.value("inventoryGems") == 2, "COLLECT must support repeated collection"); passed++;
        gem = world.object("gem", "GEM", "Gem 3", "◆", Color.CYAN,
                player.x - 1, player.y, false, 5, new IdleController()); world.addObject(gem);
        world.submitAction(player.id, GameAction.interact(Direction.LEFT, InteractionType.COLLECT)); world.tick();
        check(player.value("inventoryGems") == 3, "A third collection should fund both DROP and BUILD"); passed++;

        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.DROP)); world.tick();
        check(player.value("inventoryGems") == 2
                        && world.objectsAt(player.x + 1, player.y).stream().anyMatch(o -> o.type.equals("GEM")),
                "DROP must create a gem on an empty adjacent cell"); passed++;

        world.submitAction(player.id, GameAction.interact(Direction.DOWN, InteractionType.BUILD)); world.tick();
        check(player.value("inventoryGems") == 0
                        && world.objectsAt(player.x, player.y + 1).stream().anyMatch(o -> o.type.equals("BUILDING")),
                "BUILD must consume two gems and create a blocking building"); passed++;

        // Evolution cells build with energy in all four cardinal directions.
        world = emptyWorld(15, 13);
        GameObject cellBuilder = world.object("cell-builder", "CREATURE", "Baumeister-Zelle",
                "●", Color.BLUE, 5, 5, true, 40, new IdleController());
        cellBuilder.set("energy", 24.0);
        cellBuilder.set("reproductionThreshold", 12.0);
        cellBuilder.set("team", 0.0);
        world.addObject(cellBuilder);
        DecisionContext buildContext = world.decisionContext(cellBuilder, world.snapshot());
        for (Direction direction : Direction.values()) {
            check(buildContext.legalOptions().contains(GameAction.interact(direction, InteractionType.BUILD)),
                    "Evolution cell must have a legal BUILD action in direction " + direction); passed++;
        }
        for (Direction direction : Direction.values()) {
            int bx = cellBuilder.x + direction.dx, by = cellBuilder.y + direction.dy;
            world.submitAction(cellBuilder.id, GameAction.interact(direction, InteractionType.BUILD));
            world.tick();
            GameObject placed = world.objectsAt(bx, by).stream()
                    .filter(o -> o.type.equals("BUILDING")).findFirst().orElseThrow();
            check(placed.blocksMovement && !placed.has(Capability.PUSHABLE)
                            && placed.parentId.equals(cellBuilder.id)
                            && placed.value("team") == cellBuilder.value("team"),
                    "Building must be a stationary structure with owner/team provenance"); passed++;
        }
        check(Math.abs(cellBuilder.value("energy")) < 1.0e-9
                        && cellBuilder.value("inventoryGems") == 0.0
                        && cellBuilder.value("structuresBuilt") == 4.0,
                "Four evolution builds must consume exactly 24 energy and no gems"); passed++;
        world = emptyWorld(12, 10);
        GameObject poorBuilder = world.object("poor-builder", "CREATURE", "Energiearme Zelle",
                "●", Color.CYAN, 8, 5, true, 40, new IdleController());
        poorBuilder.set("energy", CREATURE_BUILD_ENERGY_COST - 0.1);
        world.addObject(poorBuilder);
        check(world.buildFailure(poorBuilder, Direction.UP).contains("Energie"),
                "BUILD must explain insufficient evolution energy before accepting an action"); passed++;

        GameObject boundaryPlayer = world.getObject("player");
        boundaryPlayer.x = 1; boundaryPlayer.y = 1; boundaryPlayer.syncPosition();
        DecisionContext boundaryContext = world.decisionContext(boundaryPlayer, world.snapshot());
        check(!boundaryContext.legalOptions().contains(GameAction.interact(Direction.LEFT, InteractionType.DROP))
                        && !boundaryContext.legalOptions().contains(GameAction.interact(Direction.UP, InteractionType.BUILD)),
                "DROP and BUILD must be filtered at world boundaries"); passed++;

        // Resource outpost: build, deposit and produce a stronger team-aware unit.
        world = emptyWorld(14, 12); player = world.getObject("player");
        player.set("inventoryGems", 3.0);
        Direction depotDirection = Direction.RIGHT;
        world.submitAction(player.id, GameAction.interact(depotDirection, InteractionType.BUILD_DEPOT));
        world.tick();
        GameObject depot = world.objectsAt(player.x + 1, player.y).stream()
                .filter(o -> o.type.equals("DEPOT")).findFirst().orElseThrow();
        check(player.value("inventoryGems") == 0.0 && depot.value("hp") == 70.0
                        && depot.value("storageCapacity") == 20.0,
                "BUILD_DEPOT must spend three gems and create a durable 20-slot resource outpost"); passed++;

        // Depot construction may replace ordinary diggable dirt, so the player need not DIG first.
        world = emptyWorld(14, 12); player = world.getObject("player");
        player.set("inventoryGems", 3.0);
        GameObject buildDirt = world.object("dirt", "DIRT", "Baugrund", "▪", Color.ORANGE,
                player.x + 1, player.y, true, 0, new IdleController());
        world.addObject(buildDirt);
        DecisionContext dirtBuildContext = world.decisionContext(player, world.snapshot());
        check(dirtBuildContext.legalOptions().contains(
                        GameAction.interact(Direction.RIGHT, InteractionType.BUILD_DEPOT)),
                "BUILD_DEPOT must be legal on an adjacent DIRT tile when the player has three gems"); passed++;
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.BUILD_DEPOT));
        world.tick();
        GameObject dirtDepot = world.objectsAt(player.x + 1, player.y).stream()
                .filter(o -> o.type.equals("DEPOT")).findFirst().orElseThrow();
        check(world.getObject(buildDirt.id) == null && dirtDepot.value("hp") == 70.0
                        && player.value("inventoryGems") == 0.0,
                "BUILD_DEPOT must replace adjacent ordinary dirt and visibly place the depot"); passed++;

        // Idle standard units must still run the shared infrastructure policy.
        world = emptyWorld(14, 12);
        GameObject idleBuilder = world.object("idle-builder", "HUNTER", "Bau-Unit", "◆",
                Color.ORANGE, 6, 6, true, 10, new IdleController());
        idleBuilder.set("inventoryGems", 3.0);
        world.addObject(idleBuilder);
        world.tick();
        check(world.objectsAt(6, 5).stream().anyMatch(o -> o.type.equals("DEPOT"))
                        || world.objectsAt(7, 6).stream().anyMatch(o -> o.type.equals("DEPOT"))
                        || world.objectsAt(6, 7).stream().anyMatch(o -> o.type.equals("DEPOT"))
                        || world.objectsAt(5, 6).stream().anyMatch(o -> o.type.equals("DEPOT")),
                "An idle standard AI unit with three gems must autonomously construct an adjacent depot"); passed++;
        player = world.getObject("player"); // Rebind to the newly created test world.
        player.set("inventoryGems", 8.0);
        for (int i = 0; i < 8; i++) {
            world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.DEPOSIT));
            world.tick();
        }
        check(player.value("inventoryGems") == 0.0 && depot.value("storedGems") == 8.0,
                "DEPOSIT must transfer carried resources into the adjacent outpost"); passed++;
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.PRODUCE));
        world.tick();
        GameObject bruiser = world.objects.stream().filter(o -> o.type.equals("BRUISER")).findFirst().orElseThrow();
        check(depot.value("storedGems") == 0.0 && bruiser.value("hp") == 60.0
                        && bruiser.value("attackDamage") == 14.0
                        && (int) bruiser.value("team") == (int) player.value("team"),
                "PRODUCE must spend eight stored gems and create a stronger allied unit"); passed++;
        check(GameAction.allActionOptions().size() == 49
                        && world.decisionContext(bruiser, world.snapshot()).actionSpace().size() == 49,
                "Every mobile unit must share the expanded 49-option action vocabulary"); passed++;

        // GEM stack can be incremented up to nine through DROP.
        world = emptyWorld(11, 9); player = world.getObject("player"); player.set("inventoryGems", 1);
        gem = world.object("gem", "GEM", "Gem stack", "◆", Color.CYAN,
                player.x + 1, player.y, false, 5, new IdleController()); gem.set("stack", 3); world.addObject(gem);
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.DROP)); world.tick();
        check(gem.value("stack") == 4 && player.value("inventoryGems") == 0,
                "DROP into a gem stack must increment stack size and consume one item"); passed++;

        // ATTACK deals damage and removes defeated enemy.
        world = emptyWorld(10, 10); player = world.getObject("player");
        GameObject enemy = world.object("hunter", "HUNTER", "Enemy", "◆", Color.RED,
                player.x + 1, player.y, true, 30, new IdleController());
        enemy.set("hp", 2); enemy.set("maxHp", 2); player.set("attackDamage", 2); world.addObject(enemy);
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.ATTACK)); world.tick();
        check(world.getObject(enemy.id) == null, "ATTACK must remove a defeated attackable target"); passed++;

        // USE remains an extension point for custom object-pair rules.
        world = emptyWorld(10, 10); player = world.getObject("player");
        GameObject switchObject = world.object("switch", "SWITCH", "Switch", "S", Color.GREEN,
                player.x + 1, player.y, false, 0, new IdleController()); world.addObject(switchObject);
        final boolean[] triggered = {false};
        world.registerInteractionRule("PLAYER", "SWITCH", InteractionType.USE,
                (w, a, t, d) -> { triggered[0] = true; return "Switch activated"; });
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.USE)); world.tick();
        check(triggered[0], "USE must support custom registered interaction rules"); passed++;

        // Capability mask filters illegal actions. A rock cannot DIG, a player can.
        world = emptyWorld(10, 10); player = world.getObject("player");
        GameObject capabilityRock = world.object("rock", "ROCK", "Rock", "●", Color.GRAY,
                2, 2, true, 70, new GravityController()); world.addObject(capabilityRock);
        DecisionContext rockContext = world.decisionContext(capabilityRock, world.snapshot());
        check(!rockContext.legalOptions().contains(GameAction.interact(Direction.RIGHT, InteractionType.DIG))
                        && capabilityRock.has(Capability.FALLS) && capabilityRock.has(Capability.PUSHABLE),
                "Object capabilities must filter action families"); passed++;
        GameObject modifiable = world.object("drone", "DRONE", "Configurable object", "D", Color.PINK,
                2, 3, true, 10, new IdleController());
        modifiable.grantCapability(Capability.MOVE);
        modifiable.grantCapability(Capability.DIG);
        world.addObject(modifiable);
        GameObject nearbyDirt = world.object("dirt", "DIRT", "Drone dirt", "▪", Color.ORANGE,
                3, 3, true, 0, new IdleController()); world.addObject(nearbyDirt);
        DecisionContext configurableContext = world.decisionContext(modifiable, world.snapshot());
        check(configurableContext.legalOptions().contains(GameAction.move(Direction.DOWN))
                        && configurableContext.legalOptions().contains(GameAction.interact(Direction.RIGHT, InteractionType.DIG)),
                "Granting capabilities must add corresponding legal actions to an arbitrary object"); passed++;
        modifiable.revokeCapability(Capability.DIG);
        check(!world.decisionContext(modifiable, world.snapshot()).legalOptions()
                        .contains(GameAction.interact(Direction.RIGHT, InteractionType.DIG)),
                "Revoking a capability must remove that action from the legal set"); passed++;

        // Gravity and hunter use their typed actions.
        world = emptyWorld(10, 10); player = world.getObject("player");
        rock = world.object("rock", "ROCK", "Falling rock", "●", Color.GRAY,
                4, 2, true, 70, new GravityController()); world.addObject(rock);
        world.tick();
        check(rock.y == 3, "Gravity controller must choose MOVE(DOWN) when free"); passed++;

        world = emptyWorld(10, 10); player = world.getObject("player");
        player.x = 4; player.y = 4; player.syncPosition();
        enemy = world.object("hunter", "HUNTER", "Hunter", "◆", Color.RED,
                5, 4, true, 60, new HybridHunterController(8));
        enemy.decisionAlgorithm = new GreedyDecisionAlgorithm();
        enemy.set("attackDamage", 7); world.addObject(enemy);
        world.tick();
        check(player.value("hp") == 93 && enemy.x == 5 && enemy.y == 4,
                "Adjacent hunter must choose typed INTERACT(ATTACK) without moving"); passed++;
        check(world.eventLog.stream().anyMatch(entry -> entry.contains("[DAMAGE]")
                        && entry.contains("hunter") && entry.contains("player") && entry.contains("HP 100.0 -> 93.0")),
                "Combat damage must record attacker, target, coordinates, amount and HP transition"); passed++;

        // Perception contains a frozen world snapshot and legal actions from the same action vocabulary.
        world = emptyWorld(10, 10); player = world.getObject("player");
        DecisionContext context = world.decisionContext(player, world.snapshot());
        check(context.actionSpace().size() == GameAction.ACTION_SPACE_SIZE && context.actionSpace().containsAll(context.legalOptions())
                        && context.legalOptions().contains(GameAction.waitAction()),
                "DecisionContext must expose canonical actions and a legal subset"); passed++;

        // Perception is local and configurable rather than an omniscient world dump.
        GameObject remoteGem = world.object("gem", "GEM", "Remote gem", "◆", Color.CYAN,
                player.x + 4, player.y, false, 5, new IdleController());
        world.addObject(remoteGem);
        player.set("visionRange", 2.0);
        DecisionContext narrowView = world.decisionContext(player, world.snapshot());
        check(narrowView.perception().visibleObjects().stream().noneMatch(o -> o.id().equals(remoteGem.id)),
                "Objects beyond visionRange must not be visible to the controller"); passed++;
        player.set("visionRange", 6.0);
        DecisionContext wideView = world.decisionContext(player, world.snapshot());
        check(wideView.perception().visibleObjects().stream().anyMatch(o -> o.id().equals(remoteGem.id)),
                "Increasing visionRange must expand the controller perception"); passed++;

        // Adaptive softmax: every legal action keeps a positive probability and total mass is one.
        AdaptiveSoftmaxDecisionAlgorithm adaptive = new AdaptiveSoftmaxDecisionAlgorithm(0.12, 0.75, 42L);
        ActionEvaluator neutral = new ActionEvaluator() {
            @Override public double score(DecisionContext c, GameAction a) { return 0.0; }
            @Override public String name() { return "NEUTRAL_TEST"; }
        };
        Map<GameAction, Double> probabilities = adaptive.probabilities(context, neutral);
        double probabilitySum = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        check(probabilities.keySet().containsAll(context.legalOptions())
                        && probabilities.values().stream().allMatch(v -> v > 0.0)
                        && Math.abs(probabilitySum - 1.0) < 1e-9,
                "Adaptive softmax must assign positive normalized probability to every legal move"); passed++;

        // Learning update changes Q-values; repeated positive outcomes increase the preference.
        GameAction learnAction = context.legalOptions().stream().filter(a -> a.kind() == ActionKind.MOVE)
                .findFirst().orElseThrow();
        double beforeQ = player.learnedActionValues.getOrDefault(learnAction.key(), 0.0);
        player.learn(learnAction, 1.0);
        double afterFirst = player.learnedActionValues.get(learnAction.key());
        player.learn(learnAction, 1.0);
        double afterSecond = player.learnedActionValues.get(learnAction.key());
        check(afterFirst > beforeQ && afterSecond > afterFirst,
                "Positive action feedback must gradually increase learned action value"); passed++;

        // A positively reinforced legal action becomes more probable than before learning.
        DecisionContext beforeLearningContext = world.decisionContext(player, world.snapshot());
        GameAction reinforced = beforeLearningContext.legalOptions().stream()
                .filter(a -> a.kind() == ActionKind.MOVE).findFirst().orElseThrow();
        double probabilityBefore = adaptive.probabilities(beforeLearningContext, neutral).get(reinforced);
        player.learn(reinforced, 2.0);
        player.learn(reinforced, 2.0);
        DecisionContext afterLearningContext = world.decisionContext(player, world.snapshot());
        double probabilityAfter = adaptive.probabilities(afterLearningContext, neutral).get(reinforced);
        check(probabilityAfter > probabilityBefore,
                "A positive reward must increase the selected action's future probability"); passed++;

        // The adaptive selector respects the legal subset across repeated choices.
        context = world.decisionContext(player, world.snapshot());
        for (int i = 0; i < 100; i++) {
            GameAction selected = adaptive.chooseAction(context, neutral);
            check(context.legalOptions().contains(selected), "Adaptive selector must never choose an illegal action");
        }
        passed++;

        // Epsilon-greedy and random baselines still operate on the legal subset.
        RandomLegalDecisionAlgorithm randomA = new RandomLegalDecisionAlgorithm(90210L);
        RandomLegalDecisionAlgorithm randomB = new RandomLegalDecisionAlgorithm(90210L);
        EpsilonGreedyDecisionAlgorithm epsilon = new EpsilonGreedyDecisionAlgorithm(0.30, 123L);
        for (int i = 0; i < 50; i++) {
            GameAction a = randomA.chooseAction(context, neutral);
            GameAction b = randomB.chooseAction(context, neutral);
            check(context.legalOptions().contains(a) && a.equals(b), "Seeded random baseline must be reproducible and legal");
            check(context.legalOptions().contains(epsilon.chooseAction(context, neutral)), "Epsilon-greedy must choose legal actions");
        }
        passed++;

        // Every object receives at most one effective action in a tick even when displaced by PUSH.
        world = emptyWorld(12, 9); player = world.getObject("player");
        int actorX = player.x, actorY = player.y;
        rock = world.object("rock", "ROCK", "Push then stale plan", "●", Color.GRAY,
                actorX + 1, actorY, true, 80, new GravityController()); world.addObject(rock);
        GameObject rockBelow = world.object("rock", "ROCK", "Lower rock", "●", Color.GRAY,
                actorX + 2, actorY + 1, true, 70, new IdleController()); world.addObject(rockBelow);
        world.submitAction(player.id, GameAction.interact(Direction.RIGHT, InteractionType.PUSH));
        int beforeTick = world.tick;
        world.tick();
        check(world.tick == beforeTick + 1 && player.x == actorX + 1 && rock.x == actorX + 2,
                "PUSH resolution must commit only one player action and invalidate stale displaced-object intents"); passed++;

        // Reproducible long run and object invariants.
        world = GameWorld.createBoulderScenario(77123L, 24, 18);
        Random commands = new Random(77123L);
        for (int i = 0; i < 180 && !world.gameOver; i++) {
            Direction direction = Direction.values()[commands.nextInt(Direction.values().length)];
            ActionMode mode = ActionMode.values()[commands.nextInt(ActionMode.values().length)];
            world.submitAction("player", GameAction.fromMode(mode, direction));
            world.tick();
            Set<Long> occupiedBlockingCells = new HashSet<>();
            for (GameObject object : world.objects) {
                check(world.inside(object.x, object.y), "Every object must remain within world bounds: " + object.id);
                check(object.value("gridX") == object.x && object.value("gridY") == object.y,
                        "Grid variables must track object position: " + object.id);
                if (object.blocksMovement) {
                    long posKey = (((long) object.x) << 32) ^ (object.y & 0xffffffffL);
                    check(occupiedBlockingCells.add(posKey), "Blocking objects may not overlap: " + object.id);
                }
            }
        }
        passed++;

        // Evolution scenario: food regeneration, division, mutation inheritance and predation biomass.
        world = GameWorld.createEvolutionScenario(334455L, 40, 28);
        check(world.creatureCount() == 24 && world.countType("FOOD") > 0
                        && world.teamPopulationSummary().contains("T6:4"),
                "Evolution scenario must start with six teams and food resources"); passed++;
        check(world.blueLineageObjective().contains("Pop 4/12")
                        && world.blueLineageObjective().contains("Generation 0/8"),
                "Blue lineage level objective must expose population and generation progress"); passed++;
        check(GameAction.allActionOptions().size() == 49
                        && GameAction.allActionOptions().contains(GameAction.interact(Direction.RIGHT, InteractionType.REPRODUCE)),
                "The universal action vocabulary must include REPRODUCE in all four directions"); passed++;

        GameObject parent = world.objects.stream().filter(o -> o.type.equals("CREATURE")).findFirst().orElseThrow();
        parent.set("energy", 30.0);
        parent.set("reproductionThreshold", 10.0);
        int initialPop = world.creatureCount();
        int parentX = parent.x, parentY = parent.y;
        Direction reproduceDirection = null;
        for (Direction candidate : Direction.values()) {
            if (world.isCompletelyEmpty(parentX + candidate.dx, parentY + candidate.dy, parent.id)) {
                reproduceDirection = candidate;
                break;
            }
        }
        if (reproduceDirection == null) throw new AssertionError("Reproduction test needs one free neighbour");
        world.submitAction(parent.id, GameAction.interact(reproduceDirection, InteractionType.REPRODUCE));
        world.tick();
        check(world.creatureCount() == initialPop + 1 && world.births == 1,
                "REPRODUCE must create exactly one offspring in an adjacent empty cell"); passed++;
        GameObject child = world.objects.stream().filter(o -> o.type.equals("CREATURE") && o.parentId.equals(parent.id))
                .findFirst().orElseThrow();
        check(child.value("team") == parent.value("team")
                        && child.value("generation") == parent.value("generation") + 1
                        && child.color.equals(parent.color) && child.value("energy") > 0.0,
                "Offspring must inherit team/color, increment generation and receive transferred energy"); passed++;
        check(child.value("metabolism") >= 0.01 && child.value("metabolism") <= 0.12
                        && child.value("aggression") >= 0.0 && child.value("aggression") <= 1.0
                        && child.value("foodEfficiency") >= 0.65 && child.value("foodEfficiency") <= 1.45,
                "Mutated traits must remain inside defined biological bounds"); passed++;
        check(child.genome != null && parent.genome != null
                        && child.genome.foodSeek() >= 0.0 && child.genome.foodSeek() <= 2.5
                        && child.genome.threatAvoid() >= 0.0 && child.genome.threatAvoid() <= 2.5
                        && child.genome.mutationRate() >= 0.01 && child.genome.mutationRate() <= 0.35,
                "Offspring must receive a bounded heritable INPUT/OPERATION genome"); passed++;
        check(child.mutationEvents == parent.mutationEvents + child.value("mutationCount")
                        && child.lastMutationSummary != null,
                "Every changed gene must be counted and recorded in the offspring lineage"); passed++;
        check(child.color.equals(parent.color) && child.value("team") == parent.value("team"),
                "Team and colour are stable inherited identity traits"); passed++;
        for (int i = 0; i < 7; i++) {
            GameObject lineage = world.object("goal", "CREATURE", "Goal lineage", "●", Color.BLUE,
                    2 + i, 2, true, 40, new EvolutionController());
            lineage.set("team", 0);
            lineage.set("generation", 8);
            world.addObject(lineage);
        }
        check(world.teamPopulationCount(0) >= 12 && world.teamMaxGeneration(0) >= 8
                        && world.blueLineageObjective().startsWith("ZIEL ERREICHT"),
                "Blue lineage objective must complete at 12 living team units and generation 8"); passed++;

        // Blue level outcome: simultaneous target completion wins; extinction loses.
        world = GameWorld.createEvolutionScenario(445566L, 40, 28);
        for (GameObject creature : new ArrayList<>(world.objects))
            if (creature.type.equals("CREATURE") && (int) creature.value("team") == 0) world.removeObject(creature);
        world.evaluateBlueLevelOutcome();
        check(world.gameOver && !world.won && world.message.contains("NIEDERLAGE"),
                "Blue-line extinction must end the level as a defeat"); passed++;

        // Genome sensor modalities: same-row and same-column targets are detected even outside a small radial field.
        world = emptyWorld(28, 20);
        GameObject sensor = world.object("sensor", "CREATURE", "Sensorzelle", "●", Color.CYAN, 8, 8, true, 40, new EvolutionController());
        sensor.genome = new Genome(0.0, 8.0, 8.0, 0.0, 1.0, 0.0,
                1.0, 1.0, 1.0, 1.0, 0.5, 0.10);
        sensor.set("team", 0);
        GameObject rowTarget = world.object("row", "FOOD", "Reihenziel", "✦", Color.GREEN, 14, 8, false, 1, new IdleController());
        GameObject columnTarget = world.object("column", "FOOD", "Spaltenziel", "✦", Color.GREEN, 8, 14, false, 1, new IdleController());
        GameObject diagonalTarget = world.object("diagonal", "FOOD", "Diagonalziel", "✦", Color.GREEN, 12, 12, false, 1, new IdleController());
        world.addObject(sensor); world.addObject(rowTarget); world.addObject(columnTarget); world.addObject(diagonalTarget);
        Perception rowColumnPerception = world.decisionContext(sensor, world.snapshot()).perception();
        check(rowColumnPerception.visibleObjects().stream().anyMatch(o -> o.id().equals(rowTarget.id))
                        && rowColumnPerception.visibleObjects().stream().anyMatch(o -> o.id().equals(columnTarget.id))
                        && rowColumnPerception.visibleObjects().stream().noneMatch(o -> o.id().equals(diagonalTarget.id)),
                "Inherited row/column sensor genes must define the observed input field"); passed++;

        // A radial sensor gene extends input coverage.
        sensor.genome = new Genome(0.0, 0.0, 0.0, 8.0, 1.0, 0.0,
                1.0, 1.0, 1.0, 1.0, 0.5, 0.10);
        Perception radialPerception = world.decisionContext(sensor, world.snapshot()).perception();
        check(radialPerception.visibleObjects().stream().anyMatch(o -> o.id().equals(diagonalTarget.id)),
                "Radial sensor gene must detect objects inside its Manhattan radius"); passed++;

        // Genetic operation weights must change scores, while the shared legal action interface remains unchanged.
        sensor.genome = new Genome(1.0, 8.0, 8.0, 8.0, 1.0, 0.0,
                2.2, 0.1, 0.1, 0.3, 0.2, 0.10);
        GameObject foodAdjacent = world.object("food-near", "FOOD", "Nahes Futter", "✦", Color.GREEN, 8, 9, false, 1, new IdleController());
        world.addObject(foodAdjacent);
        DecisionContext geneContext = world.decisionContext(sensor, world.snapshot());
        EvolutionActionEvaluator geneEvaluator = new EvolutionActionEvaluator(geneContext);
        double collectScore = geneEvaluator.score(geneContext, GameAction.interact(Direction.DOWN, InteractionType.COLLECT));
        double waitScore = geneEvaluator.score(geneContext, GameAction.waitAction());
        check(collectScore > waitScore && geneContext.actionSpace().size() == GameAction.ACTION_SPACE_SIZE,
                "Food-seeking operation gene must raise COLLECT preference without changing the action vocabulary"); passed++;
        AdaptiveSoftmaxDecisionAlgorithm geneSelector = new AdaptiveSoftmaxDecisionAlgorithm(0.16, 0.72, 556677L);
        Map<GameAction, Double> geneProbabilities = geneSelector.probabilities(geneContext, geneEvaluator);
        double geneProbabilitySum = geneProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        check(geneProbabilities.size() == geneContext.legalOptions().size()
                        && geneProbabilities.values().stream().allMatch(probability -> probability > 0.0)
                        && Math.abs(geneProbabilitySum - 1.0) < 1.0e-9
                        && geneProbabilities.get(GameAction.interact(Direction.DOWN, InteractionType.COLLECT))
                           > geneProbabilities.get(GameAction.waitAction()),
                "Genetic operation weights must shift a normalized positive distribution over legal outputs"); passed++;

        GenomeMutation forcedGenomeMutation = Genome.defaults().mutate(new Random(881122L), 100.0);
        check(forcedGenomeMutation.mutationCount() > 0
                        && forcedGenomeMutation.genome().foodSeek() >= 0.0
                        && forcedGenomeMutation.genome().foodSeek() <= 2.5
                        && forcedGenomeMutation.genome().sensorNoise() <= 0.45,
                "Genome mutation must alter and clamp inherited sensor and operation genes"); passed++;

        // A cell can collect nearby food and convert it into energy.
        world = GameWorld.createEvolutionScenario(444221L, 24, 18);
        GameObject feeder = world.objects.stream().filter(o -> o.type.equals("CREATURE")).findFirst().orElseThrow();
        Direction foodDirection = null;
        for (Direction candidate : Direction.values()) {
            if (world.isCompletelyEmpty(feeder.x + candidate.dx, feeder.y + candidate.dy, feeder.id)) {
                foodDirection = candidate;
                break;
            }
        }
        if (foodDirection == null) throw new AssertionError("Food test requires an empty neighbouring cell");
        int foodX = feeder.x + foodDirection.dx, foodY = feeder.y + foodDirection.dy;
        GameObject foodItem = world.object("food", "FOOD", "Food test", "✦", new Color(90, 220, 110),
                foodX, foodY, false, 1, new IdleController());
        foodItem.set("foodValue", 5.0);
        world.addObject(foodItem);
        double energyBeforeFood = feeder.value("energy");
        world.submitAction(feeder.id, GameAction.interact(foodDirection, InteractionType.COLLECT));
        world.tick();
        check(world.getObject(foodItem.id) == null && feeder.value("energy") > energyBeforeFood
                        && feeder.value("inventoryResources") == 1.0,
                "COLLECT must feed a cell and add one biomass token"); passed++;

        world = emptyWorld(16, 12);
        GameObject courier = world.object("courier", "CREATURE", "Kurier", "●", Color.BLUE,
                5, 5, true, 40, new EvolutionController());
        courier.set("team", 2.0);
        courier.set("inventoryResources", 3.0);
        GameObject citadel = world.object("citadel", "BUILDING", "Team-Zitadelle", "▤",
                Color.BLUE, 6, 5, true, 0, new BuildingController());
        citadel.set("team", 2.0);
        citadel.set("storedResources", 0.0);
        citadel.set("resourceCapacity", BUILDING_STORAGE_CAPACITY);
        world.addObject(courier); world.addObject(citadel);
        DecisionContext courierContext = world.decisionContext(courier, world.snapshot());
        check(courierContext.legalOptions().contains(GameAction.interact(Direction.RIGHT, InteractionType.DEPOSIT)),
                "Same-team courier can deposit biomass into its citadel"); passed++;
        world.submitAction(courier.id, GameAction.interact(Direction.RIGHT, InteractionType.DEPOSIT));
        world.tick();
        check(courier.value("inventoryResources") == 2.0 && citadel.value("storedResources") == 1.0,
                "DEPOSIT transfers exactly one biomass token from unit to building"); passed++;

        world = emptyWorld(16, 12);
        world.evolutionMode = true;
        GameObject splitBase = world.object("split-citadel", "BUILDING", "Team-Zitadelle", "▤",
                Color.GREEN, 7, 6, true, 0, new BuildingController());
        splitBase.set("team", 3.0);
        splitBase.set("storedResources", BUILDING_SPLIT_THRESHOLD);
        splitBase.set("resourceCapacity", BUILDING_STORAGE_CAPACITY);
        world.addObject(splitBase);
        DecisionContext splitContext = world.decisionContext(splitBase, world.snapshot());
        GameAction splitAction = GameAction.interact(Direction.UP, InteractionType.REPRODUCE);
        check(splitContext.legalOptions().contains(splitAction),
                "Citadel with 24 resources has a legal split action"); passed++;
        world.submitAction(splitBase.id, splitAction);
        world.tick();
        GameObject daughter = world.objects.stream()
                .filter(o -> o.type.equals("BUILDING") && o.parentId.equals(splitBase.id))
                .findFirst().orElseThrow();
        check(splitBase.value("storedResources") == 12.0
                        && daughter.value("storedResources") == 12.0
                        && daughter.value("team") == splitBase.value("team"),
                "Citadel division conserves resources and inherits team identity"); passed++;

        world = emptyWorld(16, 12);
        GameObject factory = world.object("factory", "BUILDING", "Team-Zitadelle", "▤",
                Color.ORANGE, 5, 5, true, 0, new BuildingController());
        factory.set("team", 4.0);
        factory.set("generation", 1.0);
        factory.set("storedResources", LEVEL2_PRODUCTION_COST);
        factory.set("resourceCapacity", BUILDING_STORAGE_CAPACITY);
        world.addObject(factory);
        DecisionContext factoryContext = world.decisionContext(factory, world.snapshot());
        GameAction produceAction = GameAction.interact(Direction.RIGHT, InteractionType.PRODUCE);
        check(factoryContext.legalOptions().contains(produceAction),
                "Citadel with 12 resources has a legal Level-2 production action"); passed++;
        world.submitAction(factory.id, produceAction);
        world.tick();
        GameObject level2Unit = world.objects.stream()
                .filter(o -> o.type.equals("CREATURE") && o.parentId.equals(factory.id))
                .findFirst().orElseThrow();
        check(level2Unit.value("unitLevel") == 2.0 && level2Unit.value("team") == 4.0
                        && level2Unit.value("hp") == 9.0 && level2Unit.value("attackDamage") == 3.0
                        && level2Unit.color.equals(factory.color)
                        && factory.value("storedResources") == 0.0,
                "Citadel produces a stronger Level-2 unit of the same team and colour"); passed++;

        world = emptyWorld(18, 14);
        GameObject survivor = world.object("survivor", "CREATURE", "Überlebende Zelle", "●",
                Color.CYAN, 7, 7, true, 40, new EvolutionController());
        survivor.set("team", 0.0);
        survivor.set("energy", 22.0);
        survivor.set("reproductionThreshold", 12.0);
        survivor.set("structuresBuilt", 0.0);
        survivor.genome = Genome.defaults();
        GameObject urgentFood = world.object("urgent-food", "FOOD", "Nahrung", "✦",
                Color.GREEN, 7, 6, false, 1, new IdleController());
        world.addObject(survivor); world.addObject(urgentFood);
        DecisionContext survivalContext = world.decisionContext(survivor, world.snapshot());
        EvolutionActionEvaluator survivalEvaluator = new EvolutionActionEvaluator(survivalContext);
        GameAction optionalBuild = GameAction.interact(Direction.RIGHT, InteractionType.BUILD);
        GameAction survivalReproduce = GameAction.interact(Direction.DOWN, InteractionType.REPRODUCE);
        check(survivalContext.legalOptions().contains(survivalReproduce)
                        && survivalEvaluator.score(survivalContext, survivalReproduce)
                           > survivalEvaluator.score(survivalContext, optionalBuild),
                "Survival/reproduction must outrank optional construction"); passed++;

        // Mutation probability is a trait and can be tested independently with a forced mutation rate.
        double forcedMutation = world.mutate(0.5, 0.2, 0.0, 1.0, 1.0, new Random(1122L));
        check(forcedMutation != 0.5 && forcedMutation >= 0.0 && forcedMutation <= 1.0,
                "A mutation rate of one must produce a bounded trait mutation"); passed++;

        // Starvation removes a cell whose energy reserve falls below zero after upkeep.
        GameObject starving = world.objects.stream().filter(o -> o.type.equals("CREATURE")).findFirst().orElseThrow();
        starving.set("energy", 0.001);
        starving.set("metabolism", 0.05);
        String starvingId = starving.id;
        int deathsBeforeStarvation = world.deaths;
        world.submitAction(starvingId, GameAction.waitAction());
        world.tick();
        check(world.getObject(starvingId) == null && world.deaths == deathsBeforeStarvation + 1,
                "A cell whose energy reaches zero must die and increment the death counter"); passed++;

        // Predation transfers biomass only after the target is defeated.
        world = GameWorld.createEvolutionScenario(99881L, 24, 18);
        GameObject predator = world.objects.stream().filter(o -> o.type.equals("CREATURE")).findFirst().orElseThrow();
        GameObject prey = world.objects.stream().filter(o -> o.type.equals("CREATURE")
                && o.value("team") != predator.value("team")).findFirst().orElseThrow();
        for (GameObject object : world.objects) if (object.type.equals("CREATURE") && object != predator && object != prey) object.alive = false;
        world.objects.removeIf(o -> !o.alive);
        predator.x = 8; predator.y = 8; predator.syncPosition();
        prey.x = 9; prey.y = 8; prey.syncPosition();
        predator.set("attackDamage", 5.0); prey.set("hp", 1.0); prey.set("energy", 4.0);
        double beforeBiomass = predator.value("energy");
        world.submitAction(predator.id, GameAction.interact(Direction.RIGHT, InteractionType.ATTACK));
        world.tick();
        check(world.getObject(prey.id) == null && predator.value("energy") > beforeBiomass
                        && world.predationKills == 1 && world.deaths == 1,
                "Successful predation must remove prey, count the death and transfer biomass energy"); passed++;

        world = GameWorld.createEvolutionScenario(1177L, 30, 22);
        int foodBefore = world.countType("FOOD");
        world.environmentRandom.setSeed(123456789L);
        for (int i = 0; i < 30; i++) world.tick();
        check(world.tick == 30 && world.foodSpawned > foodBefore
                        && world.populationHistory.size() == 31,
                "Evolution must regenerate resources stochastically and record each population snapshot"); passed++;
        check(world.objects.stream().filter(o -> o.type.equals("CREATURE"))
                        .allMatch(o -> o.value("energy") >= 0.0 && o.value("metabolism") > 0.0),
                "Surviving cells must retain non-negative energy and positive metabolism"); passed++;

        // The classic Boulder Dash mode remains available on the same universal engine.
        world = GameWorld.createBoulderScenario(54321L, 32, 22);
        check(world.getObject("player") != null && world.creatureCount() == 0
                        && GameAction.allActionOptions().size() == 49,
                "Switching back to Boulder Dash must preserve the player scenario and 49-action grammar"); passed++;

        // Expanded world baseline: dimensions, resource field and camera-independent world bounds.
        world = GameWorld.createEvolutionScenario(20261116L, 160, 100);
        check(world.width == 160 && world.height == 100 && world.creatureCount() == 24
                        && world.countType("FOOD") == 300,
                "Expanded 160x100 evolution world must initialize six teams and a larger food field"); passed++;
        check(world.objects.stream().anyMatch(o -> o.type.equals("WALL") && o.x == 159)
                        && world.objects.stream().anyMatch(o -> o.type.equals("WALL") && o.y == 99),
                "Expanded simulation boundary must cover the full 160x100 area"); passed++;
        WorldSnapshot indexed = world.snapshot();
        GameObject foodProbe = world.objects.stream().filter(o -> o.type.equals("FOOD")).findFirst().orElseThrow();
        check(indexed.at(foodProbe.x, foodProbe.y).stream().anyMatch(v -> v.id().equals(foodProbe.id))
                        && !indexed.isBlocked(foodProbe.x, foodProbe.y, ""),
                "Snapshot spatial index must return the exact objects at a cell and preserve non-blocking resources"); passed++;

        // Bounded ecosystem run: validate that spontaneous evolution really occurs over time.
        world = GameWorld.createEvolutionScenario(20261116L, 60, 40);
        int initialPopulation = world.creatureCount();
        for (int i = 0; i < 120; i++) world.tick();
        check(world.tick == 120 && world.births > 0 && world.creatureCount() > 0
                        && world.populationHistory.size() == 121,
                "A reproducible 120-tick ecosystem run must produce offspring and population history"); passed++;
        check(initialPopulation + world.births == world.creatureCount() + world.deaths
                        && world.deaths == world.starvationDeaths + world.predationKills,
                "Population accounting must balance births, living cells and all registered death causes"); passed++;
        check(world.objects.stream().filter(o -> o.type.equals("CREATURE"))
                        .allMatch(o -> o.genome != null && o.genome.sensorMode() != null && o.mutationEvents >= 0),
                "Every living evolved cell must carry an inspectable genome and lineage mutation count"); passed++;
        check(world.genomePopulationSummary().contains("GENOME n="),
                "Population-level genetic statistics must be available for live inspection"); passed++;

        System.out.println("Matrixgame Universal Grid Lab v0.10.0 self-test: " + passed + " checks passed.");
        System.out.println("PASS: six teams, biomass logistics, citadel reproduction, Level-2 production, survival-first policy, mutation, predation, and world invariants.");
    }

    private static GameWorld emptyWorld(int width, int height) {
        GameWorld world = new GameWorld(1L, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    world.addObject(world.object("wall", "WALL", "Boundary", "■", Color.GRAY,
                            x, y, true, 0, new IdleController()));
                }
            }
        }
        world.addObject(world.objectWithStats("player", "PLAYER", "Player", "●", Color.YELLOW,
                width / 2, height / 2, true, 100, new IdleController(), 100, 100));
        return world;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError("SELF-TEST FAILED: " + message);
    }
}
