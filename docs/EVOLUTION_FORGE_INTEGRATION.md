# Evolution Forge – Integration in den Matrixgame-Simulator

Status: **Integrationsentwurf + Java-Prozessadapter auf Feature-Branch**. Noch keine Verschmelzung mit dem produktiven Game-Loop.

## Ziel

Die Evolutionssimulation wird als eigenständige wissenschaftliche Domäne an das Matrixgame angebunden. Die Systeme teilen ein Datenprotokoll, nicht zwangsläufig dieselben internen Objektklassen.

## Architekturentscheidung

- **Java / Matrixgame Core:** Spielzustand, physikalische Units, Spielzeit, Quests, Agentenkoordination und Ereignisse.
- **Python / Evolution Forge:** populationsbasierte Evolutionsläufe, Szenarioexperimente, statistische Auswertung und wissenschaftliche Modellparameter.
- **Poetry:** reproduzierbare Python-Abhängigkeiten und Entwicklungsumgebung.
- **Unity oder andere Visualisierung:** optionale 2D-/3D-Ansicht; Visualisierung liest standardisierte Zustands-Snapshots und wird nicht zum Besitzer der Simulationslogik.
- **Genetik-/Genommodell:** eigenständiges Fachmodul mit Genotyp, Phänotyp, Mutation, Vererbung und Fitness. Noch nicht mit der vereinfachten Trait-Vererbung des Prototyps gleichzusetzen.

## Warum biologische Zellen nicht direkt von `de.matrixgame.core.Unit` erben

Die vorhandene `Unit`-Klasse enthält Spiel- und Physikeigenschaften wie Team, Kampfwerte, Gesundheit, Masse, Gravitation und Gamification. Eine biologische Zelle benötigt andere Zustände: Energiehaushalt, Genotyp, Phänotyp, Stoffwechsel, Signalrezeptoren, Kooperation, Adhäsion, Alter, Reproduktionsstatus und Abstammung.

Eine direkte Vererbung würde biologische und spielmechanische Zuständigkeiten vermischen. Daher gilt:

1. Biologische Agenten bleiben eigenständige Domain-Objekte.
2. Ein späterer Renderer-/Snapshot-Adapter übersetzt ausgewählte Eigenschaften in darstellbare Objekte.
3. Nur explizit definierte Interaktionen werden zwischen Game-Units und Evolutionsagenten zugelassen.

## Erste Prozessschnittstelle

Der Java-Adapter startet den bestehenden Python-Prototyp als separaten Prozess. Die Übergabe erfolgt über eine JSON-Konfigurationsdatei:

```json
{
  "scenario": "earth_tidal_pool",
  "steps": 360,
  "seed": 7
}
```

Der Aufruf entspricht dem CLI-Vertrag des Prototyps:

```text
python evolution_forge.py --config request.json --out output/run-001
```

Erwartete Dateien im Ausgabeverzeichnis sind `summary.json`, `history.csv` und die PNG-Diagramme. Der Java-Adapter liefert zunächst den JSON-Text unverändert zurück. Eine typisierte JSON-Deserialisierung wird erst ergänzt, wenn ein verbindliches Schema und eine JSON-Bibliothek ausgewählt wurden.

## Szenarien

- `earth_tidal_pool`: wässriges Szenario mit Nass-Trocken-Zyklen
- `alkaline_vent`: wässriges Szenario mit Mineraloberflächen und Redoxgradienten
- `icy_ocean`: hypothetischer unterirdischer Ozean
- `titan_hypothetical`: ausdrücklich spekulatives Gedankenexperiment; der vorhandene Prototyp modelliert keine Methan-Chemie

## Wissenschaftliche Status-Tags

- `FACT`: empirisch gut gestützter Mechanismus
- `MODEL`: vereinfachte Implementierung
- `HYPOTHESIS`: offene Ursprung-des-Lebens- oder Astrobiologiehypothese
- `OPEN`: ungeklärte Frage oder fehlende Daten
- `FICTION`: bewusst erfundenes Zukunftsszenario

Der gegenwärtige Evolution-Forge-Prototyp ist ein exploratives Agentenmodell, keine validierte Abiogenese- oder Astrobiologiesimulation. Seine normierten präbiotischen Werte sind Modell-Proxies, keine realen Konzentrationen oder chemischen Reaktionsraten.

## Evolutionsmodule – vorgesehene Reihenfolge

1. Umwelt und Ressourcenfelder
2. Präbiotische Chemie als getrennte Modellhypothesen
3. Kompartimente / Protokell-Agenten
4. Replikation, Variation und Selektion
5. Quorum Sensing und chemische Kommunikation
6. Kooperation, Public Goods und Cheater-Dynamik
7. Biofilm- und Kolonieorganisation
8. Zelladhäsion und Differenzierung
9. Endosymbiose und mehrstufige Organisation
10. Ökosysteme und planetare Langzeitdynamik

## Integrationsprinzip

Zuerst einen reproduzierbaren Python-Lauf aus Java starten, danach Ergebnisschema und Tests stabilisieren, erst dann Agenten-Snapshots in Matrixgame und Visualisierung übertragen. Keine Änderungen am bestehenden `MatrixGameCore`-Loop, bevor Schnittstellen- und Regressionstests vorhanden sind.
