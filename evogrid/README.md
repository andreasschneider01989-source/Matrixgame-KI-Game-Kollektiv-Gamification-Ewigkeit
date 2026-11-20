# EvoGrid 🧬⚡ — Evolutionäre KI-Simulation auf 2D-Grid

**Reines Java 22 + Swing, keine Abhängigkeiten, kein Buildtool.**
Unit-Fight (Team-Kampf) + Boulder-Dash (Graben, Felsen-Physik, Ausgang) im selben Kern.
Neuronale Gehirne (Input -> Output), Mutation, Crossover mit Hall-of-Fame — live sichtbar.

## Start

    cd evogrid
    javac -d out $(find src -name "*.java")
    java -cp out EvoGrid                      # Grafikfenster
    java -cp out EvoGrid --headless --ticks 5000 --snapshots 12   # Batch + PNG-Snapshots

Der Headless-Modus ist die Schnittstelle für **andere Intelligenzen**:
keine GUI nötig -> LLM-Agenten können World, Config und Ticks direkt steuern
(Input -> Simulation -> Output), Auswertung über World.stats() und Fitness.

## Steuerung (Grafikfenster)

| Taste | Wirkung |
|---|---|
| Leertaste | Pause |
| + / - | Geschwindigkeit x2 / ÷2 |
| . | Einzelschritt |
| Pfeil links/rechts | Modus (0 Unit-Fight <=> 1 Boulder-Dash) |
| F | 200 Futter regnen |
| M | alle Units stark mutieren |
| G | Gravitation an/aus |
| 1-9 | Zoom |
| S | config.properties schreiben |
| R | Welt neu aufbauen |

| Maus | Wirkung |
|---|---|
| Links klicken/ziehen | Futter setzen |
| Rechts klicken/ziehen | Zelle leeren |
| Shift+Links | Wand bauen |
| Strg+Links | Erde setzen |
| Mittelklick | Neue Unit spawnen |
| Mausrad | Zoom |

**Hot-Reload:** config.properties bearbeiten & speichern -> Regler ohne Neustart übernommen.

## Architektur

    src/sim/        Simulation (GUI-frei!): World, Unit, Brain, Tile, Rng
    src/editor/     Config: alle Regler + Hot-Reload per Datei
    src/ui/         Window (Tasten) + Canvas (Renderer + Maus-Editor)

- Brain: 8 Sichtstrahlen x 4 Sensoren + Energie -> 12 Neuronen -> 9 Outputs (idle / 4x Bewegung / graben / Felsen schieben / angreifen / fortpflanzen)
- Unit: Energie, Alter, Team, Farbe (Farbe hängt am Gehirn -> man SIEHT Evolution)
- World: Tick-Loop, Futter-Ökonomie, Gravitation, Respawn aus Elite, Hall-of-Fame
- Fitness = Alter + Essen + Kills + Graben + Ausgänge + Kinder

Praxisdaten vom ersten Live-Lauf: tick=140952, maxGen=1084, hall=24, speed=64 — stabil, lebendig, lernend.
