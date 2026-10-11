# Matrix Core (Matrixgame 2.0 – Phase 1)

Ein kleiner, lauffähiger Kern für ein offenes Netzwerk aus Menschen, KIs, Simulationen und Wissensbausteinen,
das **Ideen in Modelle, Modelle in Experimente und Experimente in nachvollziehbare Erkenntnisse** verwandelt.

Dieses Repository setzt die Grundidee konkret um: Statt einer großen Vision gibt es hier
**Code, der läuft, Experimente, die widerlegt werden können, und Ergebnisse, die jeder nachrechnen kann.**

```
Hypothese -> Simulation -> Experiment -> Messung -> Ergebnis (mit PAL-Status) -> neue Hypothese
```

## Schnellstart

Voraussetzung: Java 21 (keine weiteren Abhängigkeiten).

```bash
./test.sh        # baut und führt die Selbsttests aus
./run.sh 200     # Arena mit 200 Seeds, schreibt results/ARENA.md
```

## Bausteine (`src/matrixgame/core`)

| Klasse | Rolle |
|---|---|
| `Node`, `Agent` | alles Adressierbare / aktive Nodes (`decide`, `learn`) |
| `Resource` | alles Begrenzte (hier: logistisch nachwachsender Bestand) |
| `Message` | Informationsfluss (Platzhalter für Lumini-Tokens) |
| `World`, `Simulation` | Zustand + Regeln + Zufall + Rückkopplung + Zeit |
| `Experiment`, `Result` | Hypothese → Test → Ergebnis |
| `Pal`, `Claim` | Probability Annotation Layer: FACT / MODEL / HYPOTHESIS / FICTION + Konfidenz |

Das erste Szenario (`src/matrixgame/commons`) ist eine **Allmende** (Tragedy of the Commons) mit
vier Entscheidungsalgorithmen: `GreedyAgent`, `RandomAgent`, `ReciprocatorAgent` und `NMaxMaxAgent`
(jeder maximiert seinen eigenen Nutzen aus kurzfristigem Ertrag + kollektiver Regeneration + Resilienz − Risiko).

## Erste Befunde (200 Seeds, siehe `results/ARENA.md`)

| Experiment | Urteil |
|---|---|
| E1/E2 reine N-Max-Max- vs. reine Greedy-Population | gestützt: Greedy kollabiert (Lebensdauer 31 Ticks), N-Max-Max überlebt alle 200 |
| E3 gemischte Population 5/5 | **widerlegt**: N-Max-Max-Agents sterben früher (≈20 vs ≈53 Ticks) – reine Kooperation wird ausgebeutet |
| E4 N-Max-Max vs. Reziprozität (Wohlfahrt) | **widerlegt**: die einfache Quotenregel erntet mehr (≈12 500 vs ≈11 100) |
| E5 Negativkontrolle | kein Unterschied gefunden, wie erwartet |

Die widerlegten Hypothesen sind das Wertvollste daran: Sie sagen, wo das Modell als Nächstes verbessert werden muss
(Sanktionen, Lernen, Reputation). Alle Urteile gelten **nur für dieses Modell mit diesen Parametern**
und bleiben auf PAL-Stufe `MODEL` bzw. `HYPOTHESIS` – nie automatisch `FACT`.

## Mitmachen

Siehe [CONTRIBUTING.md](CONTRIBUTING.md) und [docs/ROADMAP.md](docs/ROADMAP.md) (dort stehen konkrete Einstiegsaufgaben).
Lizenz: MIT.
