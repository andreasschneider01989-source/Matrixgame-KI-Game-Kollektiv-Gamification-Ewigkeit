# Evolution Forge – Integration in den Matrixgame-Simulator

Status: **Lauffähiger Python-Prototyp + Java-Prozessadapter auf Feature-Branch**. Die Integration ist noch nicht in den produktiven Game-Loop übernommen.

## Ziel und Architektur

- **Java / Matrixgame Core:** Spielzustand, physikalische Units, Spielzeit, Quests, Agentenkoordination und Ereignisse.
- **Python / Evolution Forge:** populationsbasierte Evolutionsläufe, Szenarioexperimente und Auswertung.
- **Poetry:** reproduzierbare Python-Abhängigkeiten.
- **Visualisierung:** liest standardisierte Zustands-Snapshots; sie besitzt nicht die biologische Simulationslogik.
- **Genetik-/Genommodell:** später eigenständiges Fachmodul mit Genotyp, Phänotyp, Mutation, Vererbung und Fitness.

Biologische Zellen erben nicht direkt von `de.matrixgame.core.Unit`: Die vorhandene Klasse enthält Spiel- und Physikeigenschaften wie Team, Kampfwerte, Gesundheit, Masse und Gravitation. Biologische Agenten benötigen dagegen Stoffwechsel, Genotyp, Zellkommunikation, Adhäsion, Reproduktion und Abstammung. Eine spätere Snapshot-/Renderer-Schicht übersetzt nur explizit freigegebene Eigenschaften.

## Prozessschnittstelle

Der Java-Adapter startet Python ohne Shell über `ProcessBuilder`. Die Parameter werden als JSON übergeben:

```json
{
  "scenario": "earth_tidal_pool",
  "steps": 360,
  "seed": 7
}
```

Aufruf:

```text
python evolution_forge.py --config request.json --out output/run-001
```

Der Adapter wartet mit Timeout, protokolliert den Prozess in `process.log` und liest `summary.json` zurück. Der JSON-Text bleibt zunächst unverändert; eine typisierte Deserialisierung folgt erst nach Stabilisierung des Schemas.

## Teststatus

- Python- und NumPy-/Matplotlib-Lauf erfolgreich in der lokalen Entwicklungsumgebung.
- Java-Adapter mit `javac --release 11` kompiliert.
- End-to-end Smoke-Test erfolgreich: Java startet Python für `icy_ocean`, Python schreibt eine Zusammenfassung, Java liest sie zurück.
- CI-Testworkflow wird im Branch eingerichtet; Maven-Gesamtregression ist davon getrennt und noch offen.

## Versions- und Reproduzierbarkeitshinweis

Der im Branch enthaltene Quellstand trägt `model_version=1.0.0`. Vergleichsläufe mit 360 Schritten und Seed 7 reproduzieren die im vorliegenden Vergleich genannten Werte für:

- `earth_tidal_pool`: erster Lebens-Agent Schritt 122, Endpopulation 520
- `alkaline_vent`: erster Lebens-Agent Schritt 107, Endpopulation 518

Die gelieferte `icy_ocean`-Tabelle nennt dagegen Schritt 217 und Endpopulation 7. Dieser Ausgang ist im Branch-Quellstand 1.0.0 unter denselben Schritten und Seed nicht reproduzierbar: Der aktuelle Lauf ergibt Schritt 160 und Endpopulation 378. Daher ist die Bezeichnung v1.1 als abweichender Modellstand zu behandeln, bis deren Quellcode, vollständige Konfiguration und Seed vorliegen.

Jeder neue Ergebnisdatensatz soll mindestens enthalten: Modellversion, Szenario-ID, vollständige Konfiguration, Seed, Anzahl der Schritte und erzeugte Kennzahlen. Für stochastische Vergleiche sind mehrere Seeds und Verteilungen aussagekräftiger als ein einzelner Lauf.

## Szenarien und Status-Tags

- `earth_tidal_pool`: wässriges Szenario mit Nass-Trocken-Zyklen
- `alkaline_vent`: wässriges Szenario mit Mineraloberflächen und Redoxgradienten
- `icy_ocean`: hypothetischer unterirdischer Ozean
- `titan_hypothetical`: ausdrücklich spekulativ; keine Methan-Chemie

`FACT` = empirisch gestützter Mechanismus; `MODEL` = vereinfachte Implementierung; `HYPOTHESIS` = offene wissenschaftliche Hypothese; `OPEN` = ungeklärte Frage; `FICTION` = erfundenes Szenario.

Die normierten präbiotischen Werte sind Modell-Proxies, keine chemischen Konzentrationen oder validierten Reaktionsraten. Die Simulationsschritte sind keine Erd- oder Planetjahre. Die Resultate sind keine Abiogenese-Wahrscheinlichkeiten.

## Weiterer Ausbau

1. Parameter- und Ergebnis-Schema stabilisieren.
2. Mehrere Seeds pro Szenario und statistische Unsicherheiten ergänzen.
3. Ressourcendiffusion, chemische Kommunikation und Zell-Agenten getrennt testen.
4. Genotyp/Phänotyp, Replikation, Variation und Selektion expliziter modellieren.
5. Biofilm, Arbeitsteilung, Endosymbiose und Ökosysteme als getrennte Module ergänzen.
6. Erst danach Zustands-Snapshots in Matrixgame und Infinity Canvas visualisieren.

Keine Änderungen am produktiven `MatrixGameCore`-Loop, bevor Schnittstellen- und Regressionstests stabil sind.
