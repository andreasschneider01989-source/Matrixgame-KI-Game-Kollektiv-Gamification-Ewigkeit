# EvoGrid 🧬⚡

**Evolutionäre KI-Simulation auf 2D-Grid — reines Java 22, keine Abhängigkeiten.**
Unit-Fight (Team-Kampf) + Boulder-Dash (Graben, Felsen-Physik, Ausgang) in einem.
Neuronale Gehirne (Input → Output), Mutation, Crossover mit Hall-of-Fame — live sichtbar.

## Start

    cd EvoGrid
    find src -name "*.java" > sources.txt          # einmalig
    javac -d out @sources.txt                      # übersetzen

    java -cp out EvoGrid                           # Grafik-Fenster (Swing)
    java -cp out EvoGrid --headless --ticks 5000 --snapshots 12   # Batch + PNG-Snapshots

Der Headless-Modus ist die Schnittstelle für **andere Intelligenzen**:
keine GUI nötig → KI-Agenten können `World`, `Config` und Ticks direkt steuern
(Input → Simulation → Output), Auswertung über `World.stats()` und Fitness.

## Steuerung (Grafikfenster)

| Taste | Wirkung |
|---|---|
| Leertaste | Pause an/aus |
| `+` / `-` | Geschwindigkeit x2 / ÷2 |
| `.` (Punkt) | Einzelschritt (im Pausenmodus) |
| Pfeil links/rechts | Modus wechseln (0 Unit-Fight ⇄ 1 Boulder-Dash) |
| `F` | 200 Futter regnen |
| `M` | alle Units stark mutieren |
| `G` | Gravitation an/aus |
| `1`–`9` | Zoom |
| `S` | `config.properties` schreiben |
| `R` | Welt neu aufbauen |

**Hot-Reload:** `config.properties` bearbeiten & speichern → Regler werden
ohne Neustart übernommen (Metabolismus, Mutation, Regeln, Modus …).

## Architektur

    src/sim/        Simulation (GUI-frei!): World, Unit, Brain, Tile, Rng
    src/editor/     Config: alle Regler + Hot-Reload per Datei
    src/ui/         Window (Tastensteuerung) + Canvas (Renderer) — reiner Viewer

- **Brain**: 8 Sichtstrahlen × 4 Sensoren + Energie → 12 Neuronen → 9 Outputs
  (idle / 4× Bewegung / graben / Felsen schieben / angreifen / fortpflanzen)
- **Unit**: Energie, Alter, Team, Farbe (Farbe hängt am Gehirn → man SIEHT Evolution)
- **World**: Tick-Loop, Futter-Ökonomie, Gravitation, Respawn aus Elite,
  Hall-of-Fame (beste Genome aller Zeiten fließen in Kreuzungen zurück)
- **Fitness** = Alter + Essen + Kills + Graben + Ausgänge + Kinder

## Ausbau-Roadmap (offene Türen)

1. Beobachten, Parameter heiß justieren, Verhalten vergleichen
2. Mehr Sensorkanäle (Energie von Nachbarn, Fels-über-mir, Teamfarbe)
3. Genome speichern/laden (JSON) → Sessions teilen
4. Missionen als Klassen mit Zielen & Belohnungen
5. Sound, Sprites, Netzwerk-Multiplayer (Socket-Server in `sim/`)
6. Andere KI (z. B. LLM-Agenten) steuert Welt & Statistik Kopf-an-Kopf
