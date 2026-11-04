# Evolution Forge 1.0.1

Eigenständiger, reproduzierbarer Python-Agentensimulator im Matrixgame-Repository.

## Installation

```powershell
cd python/evolution_forge
poetry install
```

Alternativ ohne Poetry:

```powershell
python -m pip install "numpy>=2.1,<3" "matplotlib>=3.9,<4"
```

Unterstützt Python 3.10 bis 3.13. Die Modellsoftware im Branch ist Version 1.0.1; diese Versionsnummer bezeichnet die Software mit expliziten Umweltparameter-Overrides und ist nicht mit Geminis extern genannter Version 1.1 gleichzusetzen.

## Start

```powershell
poetry run python .\evolution_forge.py --scenario earth_tidal_pool --steps 360 --seed 7 --out .\runs\earth
poetry run python .\evolution_forge.py --scenario alkaline_vent --steps 360 --seed 7 --out .\runs\vent
poetry run python .\evolution_forge.py --scenario icy_ocean --steps 360 --seed 7 --out .\runs\icy
```

Szenarien: `earth_tidal_pool`, `alkaline_vent`, `icy_ocean`, `titan_hypothetical`.

## JSON-Aufruf durch Java

Die Java-Brücke schreibt eine Konfigurationsdatei und startet:

```text
python evolution_forge.py --config request.json --out output/run-001
```

Beispiel für `request.json`:

```json
{
  "scenario": "icy_ocean",
  "steps": 360,
  "seed": 7
}
```

Nicht angegebene Felder erhalten die dokumentierten `Config`-Standardwerte. Das Ergebnis `summary.json` enthält Modellversion, vollständige wirksame Konfiguration, Szenarioparameter und Endmetriken. Damit werden unterschiedliche Modellversionen und Parametersätze vergleichbar.

## Optimiertes Eismeer-Experiment

Die Datei `experiments/icy_ocean_energy_cooperation.json` enthält ausschließlich die aus der externen Tabelle explizit genannten Optimierungsparameter: Energiefluss 0,65, Erhaltungskosten 0,015 und Kooperationsvorteil 0,08. Nicht angegebene Werte bleiben auf dem dokumentierten Basisszenario. Das Experiment ist eine reproduzierbare Sensitivitätsprüfung, keine Bestätigung der externen Zielwerte.

Start:

```powershell
poetry run python .\evolution_forge.py --config .\experiments\icy_ocean_energy_cooperation.json --out .\runs\icy-optimized
```

## Ausgaben je Lauf

- `summary.json`: kompakte Kennzahlen und wirksame Konfiguration
- `history.csv`: Zeitreihe jeder Simulationsstufe
- vier PNG-Diagramme: Populationsdynamik, präbiotische Proxies, kollektive Dynamik und räumlicher Endzustand

Ein typischer 360-Schritte-Lauf benötigt in dieser Entwicklungsumgebung ungefähr 0,3–0,4 MB einschließlich Diagrammen. Die Größe hängt von Population und Plotinhalt ab.

## Reproduzierbarkeit und Versionshinweis

Die Vergleichsläufe mit Seed 7 reproduzieren im Modellstand 1.0.1 die Earth-like-Kennzahlen (Entstehungsschritt 122, Endpopulation 520) und das Alkaline-Vent-Ergebnis (Schritt 107, Endpopulation 518) aus der vorliegenden Vergleichstabelle.

Das in der Tabelle angegebene `icy_ocean`-Ergebnis (Schritt 217, Endpopulation 7) wird von diesem Stand mit `steps=360` und `seed=7` **nicht** reproduziert. Der hier ausgeführte Lauf ergibt Schritt 160 und Endpopulation 378. Für einen fairen Vergleich wird deshalb die vollständige v1.1-Konfiguration samt Zufalls-Seed und Quellcode benötigt. Die v1.1-Zahlen werden nicht stillschweigend dem Modell 1.0.1 zugeschrieben.

## Modellgrenzen

Dieses Modell ist ein pädagogisches Agentenmodell, keine realistische Molekulardynamik und keine validierte Simulation der Abiogenese. Präbiotische Variablen sind normierte Modell-Proxies, keine gemessenen Konzentrationen oder Reaktionskinetiken. Die Zeitachse besteht aus Simulationsschritten, nicht aus Jahren. Ausgaben sind keine Wahrscheinlichkeiten für Leben auf Europa, Enceladus, Titan oder Exoplaneten.

Das Titan-Profil bildet keine Methan-Biochemie ab. Auch das Eismeer-Profil ist ein Gedankenexperiment mit vereinfachten Umweltparametern, kein geophysikalisches Modell von Europa oder Enceladus.

## Tests

```powershell
poetry run pytest -q
```
