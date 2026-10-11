# Roadmap

## Phase 1 – Matrix Core (hier, Prototyp steht)
- [x] Kernklassen, Allmende-Welt, 4 Entscheidungsalgorithmen, Arena, Selbsttests
- [ ] CI grün auf GitHub (Workflow liegt in `.github/workflows/ci.yml`)

## Phase 2 – Modell verbessern (Befunde aus E3/E4)
- [ ] `NMaxMax-T`: Zeitkomponente / Lernen über `Agent.learn` (z. B. Gewichte anpassen)
- [ ] Sanktionen oder Reputation, damit Kooperation in gemischten Populationen nicht ausgebeutet wird
- [ ] Evolutionäres Turnier (Replikatordynamik): Welche Strategien setzen sich langfristig durch?
- [ ] Parameter-Sweeps (Wachstumsrate, Rauschen, Populationsgröße) und Sensitivitätsanalyse
- [ ] Bessere Statistik als |t| > 2 (Konfidenzintervalle, Effektgrößen)

## Phase 3 – Wissensschicht
- [ ] Matrixgraph: `KnowledgeObject` mit Relationen `CAUSES`, `SUPPORTS`, `CONTRADICTS` und PAL-Status
- [ ] Ergebnisse (`Result`) automatisch als KnowledgeObjects ablegen
- [ ] Lumini-Tokens als Vokabular für `Message`
- [ ] Szenario-Editor (JSON/YAML statt Code)

## Phase 4 – Offenheit
- [ ] Bestehende Java-Prototypen (Unit Fight Simulator, Genesis Core, Infinity Canvas) auf `World`/`Agent` portieren
- [ ] Cross-Review: andere KIs und Menschen prüfen `results/ARENA.md` und versuchen, Befunde zu widerlegen
- [ ] Reale Szenarien (z. B. ökologische Quests) erst, wenn Modelle an echten Daten geprüft sind

## Gute Einstiegsaufgaben
1. Einen fünften Agent-Typ schreiben und in `Arena.make` registrieren.
2. Ein Experiment E6 mit einer neuen Hypothese ergänzen (Vorbild: `Arena.specs`).
3. Einen Test in `SelfCheck` ergänzen.
4. Ein Diagramm aus `results/ARENA.md` erzeugen.
