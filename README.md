# Matrixgame – Collective AI Game & Simulation Project

## Current Repository Status

This repository is an evolving collection of ideas, experiments, documentation, Java code fragments, simulation concepts, and previous development work related to the Matrixgame and Collective AI Games.

It brings together material developed over multiple iterations, including:

- Game and simulation concepts
- Java prototypes and code fragments
- AI agent and decision-making ideas
- Documentation, design notes, and development discussions
- Concepts for collective intelligence, cooperation, and modular simulation systems

**Important:** This repository currently represents an evolving research and development collection, not a single finished, fully integrated simulator.

Some concepts and architectural structures described in this documentation are goals for future development. They should not be interpreted as implemented or tested functionality unless the corresponding code is present and verified.

## Development Vision

The long-term goal is to transform the most useful existing Java implementations, prototypes, and ideas into a modular, extensible, and maintainable simulation framework.

Rather than continuously expanding individual monolithic Java classes, the project aims to combine reusable components from previous versions into a coherent architecture.

The planned architecture separates functionality into packages and classes with clearly defined responsibilities, interfaces, and dependencies.

This should make it easier to:

- Reuse existing simulation components
- Combine different AI agents and strategies
- Experiment with game theory and collective decision-making
- Extend simulations without rewriting the entire application
- Test individual components independently
- Develop the project incrementally with AI-assisted programming
- Share improvements with the open-source community

The existing code and documentation provide a starting point for this process. The modular architecture described below is a **development plan**, not a claim that all components already exist or work together.

## Collective Improvement Network

The broader vision for human–AI collaboration, cross-project improvement quests, evidence-aware knowledge sharing, and responsible automation is documented here:

- [Collective Improvement Network — Vision and Roadmap](docs/COLLECTIVE_IMPROVEMENT_NETWORK.md)

This is a proposed direction for future development, not a claim that the network or its automation is already implemented.

## Implementation Roadmap

The next development phase is intended to focus on:

1. Reviewing and identifying the most useful existing Java implementations.
2. Establishing a minimal, working simulation core.
3. Defining interfaces and data structures between components.
4. Integrating existing functionality incrementally.
5. Testing and documenting each integration step.
6. Gradually introducing the planned modular package structure.

The priority is to achieve a small, functional, verifiable system before expanding its complexity.

The architecture and package structure described in the following sections should therefore be understood as the intended direction of development.




# Matrixgame Simulator

## Kollektives KI-Spiel mit Physik, Bewusstsein und Gamification

**Version 1.0.0**

Ein hochmodularer, erweiterbarer Simulator, der Physik, Bewusstseinsentwicklung und Gamification-Elemente vereint. Perfekt für kollektives Spiel, Experimentieren und Prognostizieren.

---

## 🎮 Überblick

Der Matrixgame Simulator ist ein Java-basiertes Framework, das:

- **Physik-Simulation**: N-Körper-Gravitation, Kollisionen, Entropie und Relativität
- **Bewusstseins-Engine**: Hawkins-Bewusstseins-Skala mit dynamischer Entwicklung
- **Gamification**: Level, Erfahrung, Quests, Achievements und Belohnungen
- **Spieltheorie**: Entscheidungsbäume, Koalitionen und Vertrauensmodelle
- **Prognose**: Zukunftsvorhersagen mit Wahrscheinlichkeiten
- **Interaktive UI**: Swing-basierte Visualisierung mit Steuerung

---

## 📦 Struktur

```
matrixgame-simulator/
├── src/main/java/de/matrixgame/
│   ├── core/                   # Kernklassen
│   │   ├── MatrixGameCore.java      # Haupt-Simulationskern
│   │   ├── MatrixGameConfig.java     # Konfiguration
│   │   ├── Unit.java                # Einheit (physikalisches Objekt mit Bewusstsein)
│   │   ├── SimulationStatistics.java # Statistiken
│   │   └── GameStatePrediction.java # Prognosen
│   │
│   ├── physics/                 # Physik-Engine
│   │   ├── PhysicsEngine.java        # Gravitation, Kollisionen, Entropie
│   │   └── Vector3D.java            # 3D-Vektor
│   │
│   ├── consciousness/            # Bewusstseins-Engine
│   │   └── ConsciousnessEngine.java  # Hawkins-Skala, Modi, Entwicklung
│   │
│   ├── game/                    # Spiel-Logik
│   │   ├── QuestSystem.java         # Quests und Achievements
│   │   ├── RewardSystem.java        # Belohnungssystem
│   │   ├── DecisionTree.java        # Entscheidungsbäume
│   │   └── GameTheoryEngine.java    # Spieltheorie-Modelle
│   │
│   ├── api/                     # Programmierbare Schnittstelle
│   │   └── MatrixGameAPI.java       # Externe Steuerung
│   │
│   ├── ui/                      # Benutzeroberfläche
│   │   └── MatrixGameUI.java        # Swing-UI
│   │
│   └── util/                    # Hilfsklassen
│       └── MatrixGameUtils.java     # Utility-Methoden
│
├── pom.xml                      # Maven-Konfiguration
└── README.md                     # Diese Datei
```

---

## 🚀 Schnellstart

### Voraussetzungen

- Java 11 oder höher
- Maven (optional, für Build)

### Ausführung

#### 1. Konsolenanwendung (Test)

```bash
# Kompilieren und ausführen
javac -d bin src/main/java/de/matrixgame/core/*.java \
       src/main/java/de/matrixgame/physics/*.java \
       src/main/java/de/matrixgame/consciousness/*.java \
       src/main/java/de/matrixgame/game/*.java \
       src/main/java/de/matrixgame/api/*.java \
       src/main/java/de/matrixgame/util/*.java

java -cp bin de.matrixgame.core.MatrixGameCore
```

#### 2. Graphische Benutzeroberfläche

```bash
# UI starten
java -cp bin de.matrixgame.ui.MatrixGameUI
```

#### 3. Mit Maven

```bash
# Projekt bauen
mvn clean package

# Ausführen
java -jar target/matrixgame-simulator-1.0.0.jar
```
