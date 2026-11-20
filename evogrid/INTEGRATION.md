# EvoGrid <-> Matrixgame-Kollektiv: Verdrahtung & Auto-Improve-Layer

## Wo EvoGrid im Kollektiv-Repo sitzt

- evogrid/ = eigenständiges Java-Modul (läuft für sich). Kein Eingriff in matrixgame-core, lifeos, second-earth.
- Zweig abgetrennt von feature/machx-lumini-lifeos-foundation-2026-11-19 -> additive Entwicklung, nichts Bestehendes wird verändert.

## Anschlussstellen (Stufe 1 -> wächst mit)

1. **matrixgame-core**: EvoGrid World ist eine grid-basierte Miniatur desselben Prinzips (Grid, Units, Teams, Ressourcen, Evolution). Nächster Schritt: gemeinsame Datenformate (Tile-Map + Unit-Zustände als JSON/CSV).
2. **matrixgame-protocol**: World.stats() + Fitness = Standard-Output für Agenten. Input = Config-Änderungen + Unit-Injektion. Damit kann jede LLM-Instanz (ChatGPT, Gemini, Qwen ...) Welten starten, beobachten und zielgerichtet verbessern.
3. **lifeos / Quests**: Missionen werden als Objective-Klassen realisiert (Ziel + Belohnung + Erfolgskriterium). Erste Missions-Vorschläge:
   - MissionSchwarm: 2 Teams, beide müssen > 50% Überlebensrate halten = WIN-WIN (Kollektiv-Prinzip).
   - MissionAusgang: Boulder-Dash-Runde, mindestens 5 Exit-Erreichungen in 3000 Ticks.
   - MissionHalleDerBesten: Hall-of-Fame-Genome überleben 5 Generationen ohne Verschlechterung.

## Auto-Improve-Schleife (für jede Intelligenz reproduzierbar)

    1. Headless-Lauf:      java -cp out EvoGrid --headless --ticks 20000 --snapshots 20
    2. Beobachten:         stdout (stats alle 250 Ticks) + PNG-Frames
    3. Hypothese:          z. B. metabolism senken erhoehen Ess-Verhalten
    4. Parameterversuch:   config.properties (Hot-Reload) oder Konstanten im PR
    5. Beweis:             vergleichender Lauf (gleicher seed), maxGen/avgFitness hoeher = besser
    6. Commit + PR         mit Stats-Beleg im PR-Text -> Kollektiv entscheidet per Merge

## Nächste Ausbaustufen (Reihenfolge nach Ertrag)

1. Genom-Export/Import (JSON) -> Hall-of-Fame teilen und wiedereinsetzen
2. Mehr Sensorkanäle (Nachbar-Energie, Fels-über-mir, Teamfarbe)
3. Gehirne mit Plastizität (Lernen im Leben, nicht nur über Generationen)
4. Multiplayer/Agenten-API (Sockets): Ein Agent pro Team
5. Artefakt-Welten: Spieler baut (Maus), KI lernt damit -> Weltformel-Sim
