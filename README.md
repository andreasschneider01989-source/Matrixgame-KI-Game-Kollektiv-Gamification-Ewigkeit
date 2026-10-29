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
