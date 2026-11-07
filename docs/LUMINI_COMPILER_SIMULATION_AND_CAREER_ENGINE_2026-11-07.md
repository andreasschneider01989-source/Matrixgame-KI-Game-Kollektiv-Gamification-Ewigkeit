# LUMINI – Compiler, Modellierungs- und Simulationsplattform
## Technische Architektur für Matrixgame, reale Messdaten und berufliche Zukunftsszenarien

**Version:** 0.1 · **Stichtag:** 7. November 2026  
**Status:** Architektur- und Sprachentwurf · noch kein implementierter LUMINI-Compiler  
**Zielplattform:** Java 21 als gemeinsamer Ausgangspunkt; ältere Java-11-Komponenten zunächst getrennt weiterführen  
**Prinzip:** Eine nachvollziehbare Modellbeschreibung → ein typisiertes Zwischenformat → reproduzierbare Ausführung → überprüfbares Ergebnis.

> Sprache wird zu Struktur. Struktur wird zu Modellen. Modelle werden zu Experimenten. Experimente werden zu überprüfbarem Wissen.

---

## 0. Zusammenfassung

Matrixgame soll von einer Sammlung unterschiedlicher Java-Prototypen, Forschungsnotizen, KI-Ausgaben und Spielmechaniken zu einer modularen Modellierungsplattform wachsen.

Der zentrale technische Vorschlag ist eine domänenspezifische Sprache namens **LUMINI**. Sie soll menschlich lesbare Modellbeschreibungen in ein formal geprüftes Zwischenformat übersetzen. Dieses Zwischenformat wird von vorhandenen oder zukünftigen Engines ausgeführt.

LUMINI ist keine allgemeine Ersatzsprache für Java. Sie beschreibt, **was** ein Modell, eine Simulation oder ein Experiment tun soll. Java implementiert, **wie** die jeweilige Engine es ausführt.

Die erste Zielkette lautet:

~~~text
Natürliche Sprache / Editor
          ↓
LUMINI-Quelldatei (.lumini)
          ↓
Lexer + Parser
          ↓
AST (abstrakter Syntaxbaum)
          ↓
Semantik- und Einheitenprüfung
          ↓
Model IR (versioniertes JSON)
          ↓
Engine-Adapter
          ↓
Simulation / Experiment
          ↓
Messwerte + Unsicherheit + Provenienz
          ↓
Diagramm, Bericht, Wissensknoten und neue Quest
~~~

Ein Sprachmodell darf beim Übersetzen und Erklären helfen. Es ersetzt jedoch weder den Parser noch die Typprüfung, Messung, Tests oder menschliche Freigabe. Nicht geprüftes generiertes Java darf nicht automatisch ausgeführt werden.

## 1. Bestandsaufnahme: Was bereits existiert

### 1.1 Der ältere Java-Simulator

Im Repository liegt unter `src/main/java/de/matrixgame/` ein älterer Simulator mit:

- `core/MatrixGameCore`
- `core/MatrixGameConfig`
- `core/Unit`
- `core/GameStatePrediction`
- `core/SimulationStatistics`
- `physics/PhysicsEngine`
- `physics/Vector3D`
- `api/MatrixGameAPI`
- `collective/CollectiveAgent`
- `collective/GameEnvironment`
- `game/DecisionTree`, `GameTheoryEngine`, `QuestSystem` und `RewardSystem`

Die Root-`pom.xml` ist derzeit auf Java-Quell- und Zielversion 11 gestellt und deklariert keine externen Abhängigkeiten. Diese Komponenten sind ein wertvoller Bestand, aber ihre wissenschaftliche Aussagekraft muss je Funktion gesondert geprüft werden.

**Wichtige konkrete Befunde aus dem geprüften Code:**

1. Die alte `PhysicsEngine` skaliert die Newtonsche Gravitationskraft mit einem globalen Entropiefaktor. Das ist als Spielvariante möglich, aber nicht unveränderte Newtonsche Gravitation.
2. Im Gravitations-Paarlauf werden nicht bewegliche Körper auch als Kraftquellen übersprungen. Ein festgehaltener Stern, der weiterhin Gravitation ausüben soll, braucht eine eigene Semantik: Er bewegt sich nicht, bleibt aber als Quelle wirksam.
3. Die Positions- und Geschwindigkeitsfortschreibung verwendet eine einfache semi-implizite Euler-Integration. Das ist ein brauchbarer Einstieg, aber nicht für jede Bahn- oder Langzeitsimulation ausreichend.
4. Die Geschwindigkeitsbegrenzung auf `LIGHT_SPEED` ist keine relativistische Dynamik. Eine echte relativistische Engine benötigt andere Impuls-, Energie- und Bewegungsgleichungen.
5. Die alte Prognosefunktion setzt `PREDICTION_ACCURACY` standardmäßig auf 0,95 und schreibt diesen Wert als `confidence` in die Prognose. Die Positionsvorhersage ist dabei eine lineare Extrapolation mit konstanter Geschwindigkeit. Das ist kein empirischer Genauigkeitsnachweis. Solange keine Kalibrierung gegen unabhängige Beobachtungen vorliegt, muss dieser Wert als unvalidierter Spielparameter gekennzeichnet und darf nicht als gemessene 95-%-Trefferquote dargestellt werden.
6. Die alte Engine verwendet an mehreren Stellen Zufallsquellen, die nicht über einen gemeinsamen Seed gesteuert werden. Das erschwert die exakte Reproduktion.

**Folgerung:** Den älteren Simulator nicht löschen. Seine Funktionen schrittweise hinter klaren Interfaces kapseln, physikalische und spielmechanische Modi trennen und die Tests ergänzen.

### 1.2 Der kleinere Matrix Core – bevorzugte Basis für Experimente

Unter `matrixgame-core/matrixgame-core/` befindet sich bereits ein kompakterer Kern:

- Java 21, Build über `javac --release 21`
- keine externen Abhängigkeiten im bestehenden Kern
- `World`, `Simulation`, `Agent`, `Action`, `Observation`, `Feedback`
- `Experiment`, `Result`, `Claim`, `Pal`
- Allmende-Szenario unter `commons/`
- Arena mit festen Seeds
- Selbsttests unter `test/matrixgame/SelfCheck.java`
- Ergebnisse unter `results/ARENA.md`

Die Architektur beschreibt bereits:

`Hypothese → Simulation → Experiment → Messung → Ergebnis → neue Hypothese`

Die Selbsttests prüfen unter anderem, dass ein gleicher Seed identische Messwerte erzeugt und dass Ressourcen innerhalb ihrer Kapazitätsgrenzen bleiben.

Die dokumentierte Arena umfasst 200 Seeds und enthält sowohl gestützte als auch widerlegte Hypothesen. Besonders wertvoll sind E3 und E4: In einer gemischten Population wurde die Vermutung, dass kooperative N-Max-Max-Agenten immer besser abschneiden, widerlegt. Das Ergebnis gilt nur für die gewählten Modellregeln und Parameter – nicht automatisch für reale Menschen oder Gesellschaften.

**Empfehlung:** Den kleineren Matrix Core als erste Experimentier- und Reproduzierbarkeitsbasis verwenden. Die ältere PhysicsEngine anschließend als separaten Engine-Adapter integrieren, sobald ihre Einheiten, Regeln und Tests geklärt sind.

### 1.3 Das bestehende JSON-Protokoll

`matrixgame-protocol/` enthält bereits:

- `game-state.schema.json` auf Basis von JSON Schema Draft 2020-12
- `game-state.example.json`
- einen kleinen deterministischen JavaScript-Engine-Prototyp
- eine Beschreibung für zukünftige API- und Client-Anbindung

Das ist ein guter gemeinsamer Kommunikationsvertrag. Ein Spielzustand ist aber nicht dasselbe wie eine wissenschaftliche Modellbeschreibung. Daher sollen künftig drei eigenständige Datentypen entstehen:

1. **Game State:** aktueller Spiel- oder Agentenzustand.
2. **Model IR:** mathematische und fachliche Definition einer Simulation.
3. **Run Manifest / Result:** konkrete Ausführung, Eingaben, Seed, Versionen und Messergebnisse.

Sie dürfen über stabile IDs verknüpft werden, sollen aber nicht zu einem einzigen überladenen JSON-Objekt verschmelzen.

### 1.4 Wissensengine und offene Sprachlücke

`docs/UNIVERSAL_KNOWLEDGE_ENGINE.md` und `docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md` definieren bereits Wissensobjekte, Belege, Relationen, Revisionen und Status.

Das gemeinsame Protokoll erwähnt Lumini-Tokens als geplantes Vokabular. In den untersuchten Verzeichnissen ist aber noch keine ausführbare LUMINI-Grammatik, kein Parser und kein Compiler vorhanden. Die folgende Sprache ist deshalb ein neuer Entwurf, kein bereits verfügbares Feature.

Quellen im Repository:
- `docs/UNIVERSAL_KNOWLEDGE_ENGINE.md`
- `docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md`
- `matrixgame-core/matrixgame-core/README.md`
- `matrixgame-core/matrixgame-core/docs/ARCHITEKTUR.md`
- `matrixgame-core/matrixgame-core/results/ARENA.md`

## 2. Grundentscheidung: LUMINI als Modellierungssprache

### 2.1 Ziel und Nicht-Ziel

LUMINI soll besonders gut beschreiben können:

- Größen, Einheiten und Konstanten
- physikalische und mathematische Gleichungen
- Agenten, Ressourcen, Umgebungen und Zustandsübergänge
- Simulationsdauer, Zeitschritt und Integrationsverfahren
- Datenquellen, Messreihen und Unsicherheit
- Hypothesen, Experimente, Vergleichsgruppen und Kennzahlen
- Szenarien, Bedingungen, Ziele und Randbedingungen
- berufliche Pfade, Skills, Lernaufgaben und Entscheidungsmöglichkeiten
- Visualisierungswünsche und Ergebnisberichte

LUMINI soll zunächst **keine** beliebige Java- oder Betriebssystemausführung erlauben. Es gibt keine freien Dateioperationen, Shell-Befehle, Netzwerkzugriffe oder dynamische Code-Ausführung aus einer Modell-Datei heraus.

### 2.2 Die Übersetzungskette

**A. Absichtserfassung:** Ein Mensch beschreibt sein Ziel in natürlicher Sprache oder über Formulare.

**B. Strukturierung:** Eine KI kann daraus Entitäten, Variablen, Einheiten, Bedingungen, Datenquellen und offene Fragen vorschlagen. Unsichere Details bleiben als offene Felder markiert.

**C. LUMINI-Quelle:** Die Vorschläge werden in einer lesbaren Quelldatei festgehalten. Diese Datei ist überprüfbar und versionierbar.

**D. Compiler:** Lexer und Parser prüfen die Grammatik. Die semantische Analyse prüft Namen, Typen, Einheiten, Referenzen, Randbedingungen und erlaubte Engine-Funktionen.

**E. Model IR:** Der Compiler erzeugt ein versioniertes JSON-Modell. Dieses Format ist die maschinenlesbare, sprachunabhängige Übergabe an die Engines.

**F. Ausführung:** Nur registrierte und freigegebene Engine-Adapter werden gestartet.

**G. Ergebnis:** Simulation, Seeds, Datensätze, Versionsnummern, Messwerte, Unsicherheiten und Tests werden in einem Run Manifest festgehalten.

### 2.3 Warum ein Zwischenformat wichtig ist

Das LUMINI-Modell soll nicht direkt an Java, JavaScript oder einen einzelnen KI-Anbieter gebunden sein.

Dadurch können später:
- Java den physikalischen Simulator ausführen,
- JavaScript den Zustand visualisieren,
- Python optional für Statistik oder Datenanalyse verwendet werden,
- andere KI-Modelle die LUMINI-Quelle lesen und kommentieren,
- ein Web-Client dieselbe Model-IR-Datei darstellen,
- ein zweiter Simulator denselben Modellvertrag implementieren.

JSON Schema Draft 2020-12 eignet sich als Strukturvertrag für die erste Model-IR-Version. Es definiert die Validierung von JSON-Dokumenten; physikalische Korrektheit und mathematische Gültigkeit müssen zusätzlich in der Semantik- und Modellprüfung untersucht werden.

Spezifikation: https://json-schema.org/specification

## 3. Der Compiler im Detail

### 3.1 Compiler-Stufen

| Stufe | Aufgabe | Beispiel für Fehler |
|---|---|---|
| Lexer | Zeichenfolgen zu Tokens zerlegen | ungültige Zahl oder unbekanntes Symbol |
| Parser | Syntax und Baumstruktur aufbauen | fehlende Klammer |
| AST | Absicht und Struktur repräsentieren | doppelte Definition |
| Namensauflösung | Konstanten und Referenzen verbinden | unbekannter Körper `Sun` |
| Typprüfung | Zahl, Vektor, Zeitreihe, Wahrscheinlichkeit etc. unterscheiden | Vektor statt Skalar |
| Einheitenprüfung | Dimensionen und Umrechnungen kontrollieren | kg + m |
| Modellprüfung | Randbedingungen und Engine-Vertrag prüfen | negativer Zeitschritt |
| IR-Generator | Versioniertes JSON erzeugen | nicht unterstütztes Feature |
| Engine-Dispatcher | geeignete Engine auswählen | Engine nicht registriert |
| Ergebnisvalidator | Messwerte, Status und Manifest prüfen | fehlender Seed oder Quell-Hash |

Jede Fehlermeldung soll Datei, Zeile, Spalte, betroffene Variable und eine konkrete Korrekturmöglichkeit nennen.

### 3.2 Parser-Technologie

Für eine wachsende, formale Sprache ist ANTLR 4 eine geeignete Möglichkeit. ANTLR kann aus einer Grammatik Lexer und Parser für Java generieren und stellt Visitor- beziehungsweise Listener-Strukturen bereit. Die offizielle Downloadseite führt Version 4.13.2 und Java als Zielplattform auf.

- https://www.antlr.org/
- https://www.antlr.org/download
- https://github.com/antlr/antlr4/blob/dev/doc/getting-started.md

**Architekturentscheidung für den MVP:** ANTLR 4 als Parsergenerator in einem eigenen Modul testen. Die bestehende Matrix-Core-Engine bleibt davon unabhängig. Falls das zusätzliche Build-Setup zu aufwendig ist, kann ein kleiner handgeschriebener Parser für die erste eingeschränkte Grammatik als Übergang dienen; die Sprache selbst soll trotzdem formal spezifiziert bleiben.

### 3.3 Typen und Maßeinheiten

Mindestens folgende Typen werden benötigt:

- `Scalar<T>`: eine dimensionsbehaftete Größe
- `Integer`
- `Boolean`
- `Vector2<T>`, `Vector3<T>`
- `State<T>`
- `TimeSeries<T>`
- `Probability` (Wertebereich [0,1], nicht automatisch kalibriert)
- `Distribution<T>`
- `Parameter<T>`
- `Observation<T>`
- `Metric<T>`
- `Reference<T>`

Die Einheit wird nicht als bloßer Text gespeichert, sondern als Dimensionsvektor. Beispiel:

```
mass         = kg
length       = m
time         = s
velocity     = m / s
acceleration = m / s^2
force        = kg * m / s^2
energy       = kg * m^2 / s^2
G            = m^3 / (kg * s^2)
```

Die Einheitenprüfung muss beispielsweise erkennen:

- `5 kg + 3 m` → Fehler
- `10 m / 2 s` → Geschwindigkeit
- `mass / volume` → Dichte
- `G * mass / distance^2` → Beschleunigung

Umrechnungen müssen explizit, nachvollziehbar und testbar sein. Das SI-System ist die Standardbasis. Das BIPM hat die neunte Ausgabe der SI-Broschüre im Juni 2026 aktualisiert. Für fundamentale Konstanten wird eine versionierte CODATA-Datenquelle verwendet; der NIST-Eintrag führt derzeit die 2022er Anpassung und weist auf die nächste 2026er Anpassung hin.

- BIPM SI-Broschüre: https://doi.org/10.59161/AUEZ1291
- NIST CODATA: https://www.nist.gov/programs-projects/codata-values-fundamental-physical-constants
- NIST Konstantendatenbank: https://physics.nist.gov/cuu/Constants/index.html

### 3.4 Wissensstatus und Softwarestatus getrennt halten

Jedes Modellobjekt braucht zwei unabhängige Statusfelder:

**Wissenschaftlicher Status**
- FACT
- MODEL
- HYPOTHESIS
- OPEN
- FICTION

**Softwarestatus**
- PLANNED
- PROTOTYPE
- IMPLEMENTED
- TESTED
- VERIFIED

Ein physikalisches Modell kann beispielsweise wissenschaftlich ein MODEL, softwareseitig ein TESTED-Prototyp sein. Ein lauffähiger Compiler beweist nicht, dass seine Simulation physikalisch richtig ist. Ein mathematisch korrektes Modell beweist nicht, dass der Code korrekt implementiert ist.

## 4. Beispielsyntax: LUMINI v0.1

**Achtung:** Die folgende Syntax ist ein Designentwurf und noch nicht ausführbar. Sie zeigt, wie eine gemeinsame Modellierungssprache aussehen könnte.

### 4.1 Physikalisches Modell: Kreisbahn im Zentralfeld

```lumini
// DRAFT – LUMINI v0.1, noch nicht ausführbar
project "matrixgame.kepler-baseline" version "0.1" {

  claim_status = MODEL
  engine = physics.newtonian_nbody
  unit_system = SI

  source CODATA_2022 {
    publisher = "NIST / CODATA"
    url = "https://physics.nist.gov/cuu/Constants/index.html"
    retrieved = "2026-11-07"
  }

  constant G = 6.67430e-11 m^3 / (kg * s^2)
    uncertainty = 1.5e-15 m^3 / (kg * s^2)
    from = CODATA_2022

  constant AU = 149597870700 m

  time {
    step = 1800 s
    duration = 31557600 s
  }

  body Sun {
    mass = 1.98847e30 kg
    position = vec3(0 m, 0 m, 0 m)
    mobility = fixed_source
  }

  body Earth {
    mass = 5.9722e24 kg
    position = vec3(AU, 0 m, 0 m)
    velocity = vec3(
      0 m / s,
      sqrt(G * Sun.mass / AU),
      0 m / s
    )
    mobility = dynamic
  }

  gravity = newtonian
  integrator = velocity_verlet

  observe {
    orbital_period
    total_energy
    relative_energy_drift
    angular_momentum
  }

  experiment "kepler-check" {
    repeats = 1
    compare_with = analytic.circular_orbit
    tolerance.relative_period_error = 0.01
    tolerance.relative_energy_drift = 0.001
  }

  report {
    plot = [trajectory_2d, energy_drift_over_time]
    include = [parameters, units, sources, assumptions, limitations]
  }
}
```

Das ist ein vereinfachtes Zentralfeld-Beispiel. `fixed_source` bedeutet: Die Sonne wird im gewählten Bezugssystem festgehalten, übt aber weiterhin Gravitation aus. Genau diese Unterscheidung muss der zukünftige Physik-Adapter umsetzen.

### 4.2 Agentenmodell: Allmende

```lumini
// DRAFT – die bestehende Arena wird später über diese Art Modellbeschreibung steuerbar
project "matrixgame.commons" version "0.1" {
  claim_status = MODEL
  engine = agents.commons

  world Commons {
    population = 10
    resource {
      initial = 900 resource_unit
      capacity = 1000 resource_unit
      regrowth_rate = 0.25 / tick
    }
    maximum_ticks = 200
  }

  agents {
    NMaxMax count = 5
    Greedy count = 5
  }

  experiment "mixed-population" {
    seeds = range(1, 201)
    metrics = [lifetime, welfare, survivors, resource_minimum]
    control = "pure-nmaxmax"
    statistics = [mean, standard_deviation, confidence_interval, effect_size]
  }

  output = [csv, markdown, run_manifest]
}
```

Damit würde die bisher in `Arena.java` kodierte Versuchsanordnung später als Quelldatei lesbar und änderbar. Das Ziel ist nicht, bestehende Ergebnisse nachträglich zu schönen, sondern die Regeln und Experimente explizit zu machen.

### 4.3 Berufs- und Zukunftsplanung

```lumini
// DRAFT – keine persönliche Bewertung und keine Jobzusage
project "career.scenario-lab" version "0.1" {
  claim_status = MODEL
  engine = planning.scenario

  region = "Hessen"
  horizon = 12 months

  sources {
    berufenet = "https://web.arbeitsagentur.de/berufenet/"
    esco = "https://esco.ec.europa.eu/en/use-esco/use-esco-services-api"
  }

  input {
    skill_profile = local_private("career-profile.json")
    public_market_snapshot = "ba-ikt-2025.json"
    occupation_taxonomy = "esco-versioned-snapshot.json"
  }

  compare_paths {
    path "Java application development"
    path "simulation software / scientific computing"
    path "software test automation"
    path "data engineering"
    path "AI engineering"
  }

  constraints {
    region_priority = high
    learning_budget = user_defined
    time_budget = user_defined
    internship_option = allowed
  }

  metrics {
    skill_gap
    estimated_learning_effort
    qualification_access
    portfolio_evidence
    regional_job_signal
    salary_range_if_sourced
    uncertainty
  }

  scenarios = [conservative, baseline, optimistic]

  output = [
    skill_gap_map,
    portfolio_roadmap,
    learning_quests,
    job_search_snapshot,
    scenario_comparison
  ]
}
```

Das persönliche Skillprofil bleibt lokal beziehungsweise privat. Es gehört nicht in das öffentliche GitHub-Repository. Öffentliche Berufsdaten werden mit Quelle, Region, Abrufdatum, Zeitraum und Lizenz-/Nutzungsbedingungen gespeichert.

## 5. Model IR: die gemeinsame Zwischenrepräsentation

Der Compiler soll keine beliebigen Ausführungsbefehle erzeugen, sondern eine strukturierte Modellbeschreibung.

Vorgeschlagene Kernfelder:

```json
{
  "ir_version": "0.1.0",
  "model_id": "matrixgame.kepler-baseline",
  "model_kind": "physics",
  "epistemic_status": "MODEL",
  "implementation_status": "PLANNED",
  "engine_id": "physics.newtonian_nbody",
  "units": "SI",
  "sources": [],
  "parameters": [],
  "entities": [],
  "equations": [],
  "initial_conditions": {},
  "boundary_conditions": {},
  "solver": {},
  "observations": [],
  "experiments": [],
  "validation_plan": [],
  "privacy": {},
  "provenance": {}
}
```

Die endgültige JSON-Spezifikation muss strikte Feldtypen, IDs, erlaubte Engine-Parameter, Einheiten, Referenzen und Versionsmigrationen definieren. Unbekannte oder nicht erlaubte Felder sollen nicht stillschweigend ignoriert werden.

Der vorhandene `game-state.schema.json` bleibt für Spielzustände zuständig. Die Model-IR bekommt eine eigene Datei, zum Beispiel `matrixgame-protocol/model-ir.schema.json`.

## 6. Simulationsplattform: mehrere Modellklassen, ein gemeinsamer Vertrag

Nicht jedes Problem ist ein physikalisches Problem. Die Engine-Auswahl muss zum Modell passen.

| Modellklasse | Geeignete Verfahren | Matrixgame-Beispiel |
|---|---|---|
| Kontinuierliche Physik | Differentialgleichungen, numerische Integration | Gravitation, Bahnen, Kräfte |
| Diskrete Ereignisse | Event Queue, Zustandsautomat | Prozessabläufe, Warteschlangen, Logistik |
| Agentenbasierte Modelle | autonome Agents, Umwelt, Regeln | Allmende, Kooperation, Ökologie |
| System Dynamics | Bestände, Flüsse, Rückkopplungen | Energieversorgung, Wasserhaushalt |
| Optimierung | lineare/nichtlineare Optimierung, Suche | Ressourcenverteilung, Planung |
| Stochastische Modelle | Monte Carlo, Szenariobäume | Sensitivität, Risiko, Zukunftsvarianten |
| Karriereplanung | Constraint- und Szenariomodell | Skills, Lernpfade, Berufsoptionen |

### 6.1 Gemeinsamer Engine-Vertrag

Jede Engine soll eine einheitliche Schnittstelle implementieren:

```java
public interface SimulationEngine {
    String engineId();
    ValidationReport validate(ModelDefinition model);
    RunResult execute(ModelDefinition model, RunContext context);
}
```

Der `RunContext` stellt bereit:
- Modellversion,
- reproduzierbare Zufallsquelle,
- Seed,
- Einheiten- und Konstantenregister,
- Datenquellen-Snapshots,
- Abbruch- und Ressourcenlimits,
- Logging und Messdatenerfassung.

Eine Engine darf den ursprünglichen Modellzustand nicht unkontrolliert verändern. Ein Run soll mit denselben Daten, Parametern, Softwareversionen und Seeds reproduzierbar sein – soweit die verwendeten Algorithmen und Hardwarebedingungen dies erlauben.

### 6.2 Physik-Engine

Die Physik wird in austauschbare Komponenten zerlegt:

- `ForceModel`: Newtonsche Gravitation, Federkräfte, elektrostatische Modelle usw.
- `Integrator`: semi-impliziter Euler, Velocity Verlet, Runge-Kutta.
- `CollisionModel`: elastisch, inelastisch, Zusammenführung oder reine Erkennung.
- `BoundaryCondition`: offen, periodisch, reflektierend, fest.
- `UnitSystem`: SI und später weitere eindeutig definierte Einheitensysteme.
- `InitialConditionProvider`: Startpositionen, Geschwindigkeiten, Massen.
- `Observer`: Energie, Impuls, Bahnelemente, Kollisionsstatistik.

Eine Spielregel darf eine physikalische Größe verändern, aber die Abweichung muss als eigener Modellparameter dokumentiert werden.

### 6.3 Agenten- und Kollektiv-Engine

Der bestehende kleinere Matrix Core liefert bereits gute Abstraktionen: `World`, `Agent`, `Observation`, `Action`, `Feedback`.

Diese Architektur soll erhalten und um Folgendes ergänzt werden:
- ein gemeinsamer Zufallszahlengenerator mit Seed,
- explizite Tick-/Event-Reihenfolge,
- versionierte Agentenparameter,
- reproduzierbare Interaktionsprotokolle,
- alternative Strategien als Plugins,
- Messwerte und Negativkontrollen,
- getrennte Ergebnisse für einzelne Agenten und Gesamtsystem.

Das ODD-Protokoll (Overview, Design concepts, Details) ist eine etablierte Beschreibungsmethode für agentenbasierte und individuenbasierte Modelle. Es strukturiert Zweck, Zustandsvariablen, Prozessablauf, Designkonzepte, Initialisierung, Eingaben und Submodelle. Das Matrixgame sollte für größere Agentenmodelle eine ODD-kompatible Modelldokumentation ausgeben.

Quellen:
- Grimm et al. (2006), USGS: https://pubs.usgs.gov/publication/70161470
- ODD-Update (2019): https://doi.org/10.18564/jasss.4259

## 7. Reale Messdaten: vom Internet in ein überprüfbares Modell

### 7.1 Datenquellen-Adapter

Die Plattform soll mehrere Quellentypen unterstützen:

1. **Offene wissenschaftliche Daten:** Forschungsarchive, öffentliche Messreihen, amtliche Statistiken.
2. **Offizielle APIs:** strukturierte, maschinenlesbare Daten mit dokumentierten Endpunkten.
3. **Lizenzierte beziehungsweise proprietäre Quellen:** nur mit rechtmäßigem Zugang und Einhaltung der Nutzungsbedingungen.
4. **Lokale Dateien:** CSV, JSON, später Parquet oder NetCDF, sofern das Fachgebiet es benötigt.
5. **Manuelle Messwerte:** mit Einheit, Zeitpunkt, Messmethode und Unsicherheit.
6. **KI-extrahierte Daten:** zunächst als Vorschlag, bis Quelle und Extraktion geprüft wurden.

Keine Zugangsbeschränkung, Paywall, Authentifizierung oder Nutzungsbedingung darf umgangen werden. Zugangsdaten und API-Schlüssel gehören in einen lokalen Secret Store oder eine geschützte Laufzeitkonfiguration, niemals in öffentliche Quelldateien, Git-Historie oder Modellprompts.

### 7.2 Pflichtmetadaten für jeden Datensatz

| Feld | Zweck |
|---|---|
| `dataset_id` | stabile Kennung |
| `publisher` | Herausgeber / Messstelle |
| `source_url` | Originalquelle oder API-Dokumentation |
| `retrieved_at` | Abrufdatum und Uhrzeit |
| `valid_time` | Zeitpunkt oder Zeitraum der Messung |
| `region` | räumlicher Geltungsbereich |
| `unit` | Einheit jeder Messgröße |
| `method` | Mess- oder Erhebungsmethode |
| `uncertainty` | Messunsicherheit, sofern verfügbar |
| `license` | Nutzungs- und Weitergaberechte |
| `version` | Daten- oder API-Version |
| `checksum` | Integritätsprüfung der gespeicherten Datei |
| `limitations` | bekannte Lücken und Verzerrungen |

Ein fehlender Messwert bleibt fehlend. Das System darf ihn nicht ohne Kennzeichnung durch einen erfundenen oder unpassenden Wert ersetzen.

### 7.3 Verification, Calibration und Validation unterscheiden

Das sind drei unterschiedliche Fragen:

- **Verification:** Haben wir das mathematische Modell korrekt in Software umgesetzt?
- **Calibration:** Welche Modellparameter passen innerhalb eines festgelegten Trainingsdatensatzes am besten zu den Beobachtungen?
- **Validation:** Funktioniert das kalibrierte Modell für den vorgesehenen Zweck auch auf unabhängigen Daten?

Dazu kommen:
- Sensitivitätsanalyse,
- Unsicherheitsfortpflanzung,
- Residuenprüfung,
- Randfalltests,
- Vergleich mit analytischen Lösungen,
- Vergleich gegen eine einfache Baseline.

Ein Modell, das historische Daten sehr gut nachbildet, kann zukünftige Entwicklungen trotzdem schlecht vorhersagen. Deshalb müssen Kalibrierungs- und Validierungsdaten getrennt bleiben.

### 7.4 Run Manifest

Jeder Lauf braucht ein Manifest, beispielsweise:

```json
{
  "run_id": "run-2026-000001",
  "model_id": "matrixgame.kepler-baseline",
  "model_version": "0.1",
  "source_commit": "GIT_COMMIT_SHA",
  "engine_version": "physics-engine-0.1",
  "compiler_version": "lumini-0.1",
  "seed": 42,
  "dataset_hashes": [],
  "parameter_set": {},
  "solver": {},
  "started_at": "ISO-8601",
  "finished_at": "ISO-8601",
  "validation_checks": [],
  "result_refs": []
}
```

Mit dieser Struktur können andere Personen später denselben Lauf wiederholen oder Abweichungen erklären.

## 8. Wissenschaftliche Güte: von einer schönen Simulation zu belastbarer Evidenz

### 8.1 Was wir von der bestehenden Arena lernen

Die dokumentierte Arena zeigt bereits den richtigen Grundgedanken: mehrere Seeds, Negativkontrolle, Messgrößen und die Möglichkeit, eine Hypothese zu widerlegen.

Es gibt aber einen konkreten statistischen Verbesserungsbedarf:

In `Arena.java` werden für A und B zum Teil dieselben Seeds verwendet. Dadurch sind die Ergebnisse gepaart beziehungsweise können gemeinsame Zufallseinflüsse enthalten. `Experiment.run` behandelt die beiden Stichproben derzeit mit einer Welch-t-Statistik, die unabhängige Gruppen annimmt.

Für gepaarte Läufe sollte zusätzlich die Verteilung der seedweisen Differenzen analysiert werden, beispielsweise mit gepaarten Konfidenzintervallen oder einem geeigneten Permutations-/Bootstrap-Verfahren. Bei Nullvarianz darf eine künstlich sehr große t-Zahl kein Ersatz für eine Effektgrößen- und Gleichheitsanalyse sein.

Die aktuell dokumentierte Schwelle `|t| > 2` ist ausdrücklich nur eine grobe Heuristik. Der nächste Statistikschritt sollte enthalten:
- Effektgröße,
- Konfidenzintervall,
- gepaarte oder unabhängige Testlogik gemäß Versuchsdesign,
- Negativ- und Positivkontrollen,
- Mehrfachtestkorrektur, wenn viele Hypothesen gleichzeitig untersucht werden,
- vorab festgelegte Erfolgskriterien.

### 8.2 Gütestufen

Ein mögliches internes System:

- **G0 – Idee:** noch nicht formalisiert.
- **G1 – formalisiert:** Variablen, Regeln und Annahmen beschrieben.
- **G2 – ausführbar:** reproduzierbarer Code und Beispiel.
- **G3 – verifiziert:** Invarianten, analytische Tests und numerische Eigenschaften geprüft.
- **G4 – kalibriert:** Parameter mit bezeichneten Messdaten angepasst.
- **G5 – extern validiert:** unabhängige Daten und fachliche Prüfung liegen vor.

Diese Stufen sind keine universelle wissenschaftliche Rangordnung. Sie dokumentieren, welche Prüfungen für das konkrete Modell durchgeführt wurden.

## 9. Karriere- und Berufsplanung als eigene Modellklasse

Die Plattform soll nicht nur Himmelskörper oder Agenten simulieren, sondern auch persönliche Lern- und Berufswege als transparente Szenarien modellieren.

### 9.1 Datenmodell

Die Karriere-Engine benötigt:

- Skills und Kompetenznachweise,
- Berufsprofile und Tätigkeitsbeschreibungen,
- Qualifikationen und Zugangsvoraussetzungen,
- Lernangebote und Dauer,
- regionale Stellen- und Ausbildungsdaten,
- Arbeitsbedingungen und Mobilitätsanforderungen,
- eigene Prioritäten und zeitliche Randbedingungen,
- Unsicherheiten und Datenstand.

Ein Skill darf nicht allein deshalb als vorhanden gelten, weil er in einem Gespräch erwähnt wurde. Gute Nachweise wären beispielsweise ein funktionierendes Repository, ein reproduzierbarer Test, eine konkrete Aufgabe, eine Arbeitsprobe oder eine formal bestätigte Qualifikation.

### 9.2 Offizielle Datenquellen

**Bundesagentur für Arbeit / BERUFENET:** Das Profil Fachinformatiker/in – Anwendungsentwicklung beschreibt die Entwicklung, Erweiterung, Pflege und Qualitätssicherung von Software, das Entwerfen von Algorithmen und Datenstrukturen, Schnittstellen, Tests, Dokumentation und Anwenderberatung.

**Arbeitsmarktstatistik:** Der BA-Bericht zur Informatik weist für 2025 rund 13.000 gemeldete IKT-Stellen im Jahresdurchschnitt aus, 22 % weniger als im Vorjahr. Die Neuzugänge gemeldeter IKT-Stellen lagen bei rund 39.000, 11 % weniger als 2024. Die BA sah 2025 keinen statistisch ausgewiesenen Engpass mehr in der Softwareentwicklung. Diese Zahlen betreffen gemeldete Stellen und die bundesweite Statistik; sie sind keine vollständige Zählung aller Stellenangebote und erlauben keine sichere Prognose für eine einzelne Person oder Region.

**ESCO:** Die europäische Klassifikation verknüpft Berufe, Kompetenzen und Qualifikationen. Die Europäische Kommission stellt eine API bereit, die für Job-Matching, Skill Intelligence, Karriereberatung und Bildungsanwendungen vorgesehen ist.

Quellen:
- BERUFENET – Fachinformatiker/in Anwendungsentwicklung: https://web.arbeitsagentur.de/berufenet/beruf/steckbrief/7856
- BERUFENET – Softwareentwickler/in: https://web.arbeitsagentur.de/berufenet/?dkz=15260
- BA – Informatik und Arbeitsmarkt: https://statistik.arbeitsagentur.de/DE/Statischer-Content/Statistiken/Themen-im-Fokus/Berufe/AkademikerInnen/Berufsgruppen/2-2-Informatik.html
- BA – Fachkräfteengpassanalyse 2025: https://statistik.arbeitsagentur.de/DE/Statischer-Content/Statistiken/Themen-im-Fokus/Fachkraeftebedarf/Fachkraefteengpassanalyse/Fachkraefteengpassanalyse/
- ESCO API: https://esco.ec.europa.eu/en/use-esco/use-esco-services-api

### 9.3 Berufswege vergleichbar machen

Mögliche Pfade für eine IT-orientierte Laufbahn:

1. **Java-Anwendungsentwicklung:** objektorientierte Programmierung, Datenstrukturen, Tests, Datenbanken, APIs und Wartung.
2. **Simulations- und Scientific Software:** Mathematik, numerische Verfahren, Einheiten, Validierung, Performance und reproduzierbare Experimente.
3. **Software Testing / QA Automation:** Testdesign, reproduzierbare Fehler, CI, Testdaten, Qualitätsmetriken und Analyse.
4. **Data Engineering:** Datenimport, Datenqualität, Datenbanken, Schnittstellen, Versionierung und Datenpipelines.
5. **AI Engineering:** Python, Datenverarbeitung, Modellbewertung, API-Integration, Evaluation und KI-Sicherheit.

Diese Wege sind keine Rangliste. Sie haben unterschiedliche Einstiegsvoraussetzungen und können sich überschneiden.

### 9.4 Keine falsche Präzision bei Berufsvorhersagen

Die Karriere-Engine soll keine angeblich exakte Wahrscheinlichkeit ausgeben, dass eine Person in sechs Monaten einen bestimmten Beruf erreicht oder eine Stelle erhält.

Stattdessen:
- mehrere Szenarien mit expliziten Annahmen,
- dokumentierte regionale und zeitliche Arbeitsmarktdaten,
- sichtbare Skill-Lücken,
- geschätzter Lernaufwand als Bandbreite,
- Zugangsvoraussetzungen,
- nachvollziehbare Portfolio-Belege,
- Sensitivität gegenüber Zeitbudget, Qualifikation und Marktlage.

Ein Szenario darf nur dann eine numerische Eintritts- oder Vermittlungswahrscheinlichkeit behaupten, wenn es mit geeigneten historischen Daten kalibriert und extern validiert wurde. Andernfalls zeigt es Szenariobedingungen und Unsicherheit, keine vermeintlich objektive Zukunftswahrscheinlichkeit.

### 9.5 Das Matrixgame-Portfolio als beruflicher Nachweis

Aus dem bestehenden Simulator können schrittweise nachvollziehbare Arbeitsproben entstehen:

- ein kleiner Java-Simulationskern mit sauberer Architektur,
- ein Unit-Test mit reproduzierbarem Seed,
- ein Modell mit Einheitenprüfung,
- eine Simulation mit analytischer Referenz,
- ein Experiment mit Negativkontrolle und Konfidenzintervall,
- ein Datenadapter mit Quellen- und Lizenzmetadaten,
- ein dokumentierter Bugfix oder Pull Request,
- eine verständliche Visualisierung.

Das zeigt nicht nur, dass jemand „KI interessant“ findet, sondern konkrete Anwendungsentwicklung, Qualitätssicherung, Modellierung, Dokumentation und Problemlösung.

## 10. KI-Kooperation und Kontextkontinuität

Unterschiedliche KI-Chats oder Anbieter teilen ihre Kontexte nicht automatisch. Das Repository kann aber eine gemeinsame, versionierte Übergabeschicht bilden.

### 10.1 Context Pack

Ein späterer Kontextgenerator soll einen kleinen, kuratierten Kontext zusammenstellen:

```json
{
  "context_pack_version": "0.1",
  "project": "Matrixgame",
  "created_at": "ISO-8601",
  "source_commit": "GIT_COMMIT_SHA",
  "selected_documents": [],
  "active_issues": [],
  "model_versions": [],
  "open_questions": [],
  "privacy_policy": "public-only-by-default"
}
```

Enthalten sein sollen nur die für die aktuelle Aufgabe relevanten Dokumente, Quellstände, Entscheidungen, offenen Fragen und Tests.

### 10.2 Rollen statt bloßer Mehrfachmeinungen

Vorgesehene KI- und Menschenrollen:

- Researcher: Primärquellen und Messdaten suchen.
- Modeler: Variablen, Randbedingungen und Gleichungen definieren.
- Mathematician: Formeln und Dimensionen prüfen.
- Programmer: minimalen Code und Tests erstellen.
- Critic: Gegenbeispiele und alternative Erklärungen suchen.
- Verifier: Abnahmekriterien unabhängig prüfen.
- Teacher: das Ergebnis verständlich aufbereiten.
- Maintainer: Änderungen freigeben und Versionen verwalten.

Mehrere übereinstimmende KI-Antworten sind keine unabhängigen Messungen. Ein Agent darf einen Status nicht allein durch Wiederholung oder Zustimmung anderer Agenten erhöhen.

### 10.3 Schutz privater Kontexte

- Keine privaten Chat-Exporte ungeprüft in ein öffentliches Repository kopieren.
- Private Karriereprofile, Bewerbungsdaten und persönliche Lernnotizen lokal oder privat speichern.
- Keine Gesundheitsdaten, Zugangsdaten oder vertraulichen Arbeitsdaten in öffentliche Modelldateien übernehmen.
- Vor jeder Veröffentlichung Quellenrechte, personenbezogene Daten und Lizenzen prüfen.
- Externe KI-Ausgaben als untrusted input behandeln, nicht als ausführbaren Code.

## 11. Interaktive Visualisierung

Ein späterer Client soll aus derselben Model IR mehrere Ansichten erzeugen:

1. **Model Explorer:** Entitäten, Variablen, Gleichungen und Einheiten.
2. **Causal Graph:** Ursache-Wirkungs-Beziehungen und Rückkopplungen.
3. **Parameter Panel:** Regler für Anfangswerte, Zeitschritte und Szenarien.
4. **Simulation Canvas:** 2D/3D-Darstellung von Objekten und Agenten.
5. **Measurement Dashboard:** Messwerte, Residuen, Unsicherheiten und Vergleich mit Referenzdaten.
6. **Experiment Arena:** Seeds, Gruppen, Kontrollen und Statistik.
7. **Career Map:** Berufe, Skills, Voraussetzungen, Lernquests und regionale Daten.
8. **Knowledge Graph:** Quellen, Behauptungen, Modelle, Tests, Ergebnisse und offene Fragen.

Eine UI darf die Berechnungen nicht heimlich verändern. Jede Eingabe wird zu einem versionierten Parameter-Update, das erneut validiert und protokolliert wird.

## 12. Vorgeschlagene Softwarestruktur

Die folgende Struktur ist ein Zielbild. Sie soll schrittweise entstehen und keine Behauptung über bereits vorhandene Dateien darstellen.

~~~text
Matrixgame/
├── matrixgame-core/
│   └── matrixgame-core/           # bestehende Java-21-Experimentierbasis
├── matrixgame-language/
│   ├── grammar/
│   │   └── Lumini.g4
│   ├── src/main/java/
│   │   └── de/matrixgame/language/
│   │       ├── LuminiCompiler.java
│   │       ├── AstBuilder.java
│   │       ├── SemanticAnalyzer.java
│   │       ├── UnitTypeChecker.java
│   │       └── IrSerializer.java
│   └── examples/
├── matrixgame-model-ir/
│   ├── schema/
│   │   ├── model-ir.schema.json
│   │   └── run-manifest.schema.json
│   └── examples/
├── matrixgame-engines/
│   ├── physics/
│   ├── agents/
│   ├── system-dynamics/
│   ├── discrete-events/
│   └── career-planning/
├── matrixgame-data/
│   ├── connectors/
│   ├── catalog/
│   └── snapshots/
├── matrixgame-results/
│   ├── runs/
│   ├── reports/
│   └── plots/
└── matrixgame-ui/
    ├── model-explorer/
    ├── simulation-canvas/
    └── career-map/
~~~

**Build-Strategie:** Zuerst den vorhandenen Java-21-Kern separat weiterbauen. Den Compiler als eigenständiges Modul hinzufügen. Danach ein übergeordnetes Maven-Projekt oder eine andere einheitliche Build-Strategie einführen. Die Root-Komponente mit Java 11 soll bis zur bewussten Migration isoliert bleiben; der bestehende Prototyp soll nicht unbeabsichtigt durch eine neue Sprachversion brechen.

## 13. Entwicklungsphasen und Abnahmekriterien

### Phase 0 – Bestand reproduzieren

- [ ] Im Unterprojekt `matrixgame-core/matrixgame-core` `./test.sh` ausführen.
- [ ] `./run.sh 200` reproduzieren.
- [ ] Git-Commit, Java-Version und Hash der Ergebnisse speichern.
- [ ] CI-Workflow auf dem echten Repository prüfen und grün bekommen.
- [ ] Statistikgrenzen und gepaarte Testlogik dokumentieren.

**Abnahme:** Ein anderer Rechner kann mit derselben Java-Version dieselben Seed-Ergebnisse erzeugen.

### Phase 1 – LUMINI-Grammatik und Parser

- [ ] Minimalgrammatik für `project`, `constant`, `parameter`, `entity`, `experiment`, `source` und `report`.
- [ ] Lexer und Parser erzeugen einen AST.
- [ ] Verständliche Fehler mit Zeile und Spalte.
- [ ] Unit-Test-Suite für gültige und ungültige Sprachbeispiele.
- [ ] Ein gültiges LUMINI-Beispiel wird in Model IR übersetzt.

**Abnahme:** Gleiche Quelldatei erzeugt deterministisch dieselbe normalisierte IR-Datei.

### Phase 2 – Typen, SI-Einheiten und Quellen

- [ ] Dimensionsvektoren und Umrechnungen implementieren.
- [ ] Addition inkompatibler Einheiten ablehnen.
- [ ] Quellen, Datenstand, Lizenz und Unsicherheit speichern.
- [ ] CODATA-Konstanten als versioniertes Datenpaket laden.
- [ ] Beispiel Dichte und Beispiel Kreisbahn validieren.

**Abnahme:** Der Compiler weist einen absichtlich falschen Einheiten-Ausdruck zuverlässig zurück.

### Phase 3 – Erster Engine-Adapter

- [ ] `SimulationEngine`-Interface implementieren.
- [ ] Allmende-Welt des Matrix Core als ersten Adapter registrieren.
- [ ] Arena-Experiment aus LUMINI starten.
- [ ] Run Manifest und Ergebnisdatei erzeugen.
- [ ] Seed und Parameter vollständig protokollieren.

**Abnahme:** Die LUMINI-Variante reproduziert ein bestehendes Arena-Experiment und dessen Messwerte innerhalb definierter Toleranzen.

### Phase 4 – Physikalische Referenzsimulation

- [ ] Zentralfeld-/Zweikörpermodell implementieren.
- [ ] Quellen, Konstanten und Einheiten hinterlegen.
- [ ] Integratorvergleich (z. B. Velocity Verlet vs. RK4).
- [ ] analytische Periode, Energie- und Impulserhaltung prüfen.
- [ ] Zeitschritt-Sensitivität dokumentieren.

**Abnahme:** Die Abweichung gegenüber einer analytischen Referenz bleibt innerhalb vorher festgelegter Toleranzen.

### Phase 5 – Datenadapter

- [ ] CSV- und JSON-Import mit Metadaten.
- [ ] Snapshot-Hash und Quellenversion speichern.
- [ ] fehlende Werte und Einheitenfehler sichtbar machen.
- [ ] Lizenz- und Zugriffsregeln prüfen.
- [ ] einen realen Messdatensatz als unabhängigen Validierungstest verwenden.

**Abnahme:** Ein Datenlauf lässt sich mit archiviertem Snapshot und Run Manifest nachvollziehen.

### Phase 6 – Berufliche Szenario-Engine

- [ ] ESCO-Berufe und Skills importieren.
- [ ] BA-Berufsprofile und veröffentlichte Arbeitsmarktstatistiken mit Datum und Region verknüpfen.
- [ ] privates Skillprofil lokal halten.
- [ ] Lernpfade und Kompetenzlücken berechnen.
- [ ] mindestens drei Berufswege in konservativen, mittleren und günstigen Szenarien vergleichen.
- [ ] Portfolio-Belege mit konkreten GitHub-Aufgaben verbinden.

**Abnahme:** Das System liefert nachvollziehbare Alternativen, Lernschritte und Unsicherheiten, keine erfundene Jobgarantie.

### Phase 7 – Visualisierung und Kontextgenerator

- [ ] Model Explorer.
- [ ] Ursache-Wirkungs-Graph.
- [ ] Parameter- und Experimentansicht.
- [ ] Career Map.
- [ ] Context Pack für andere KI-Systeme.
- [ ] menschliche Freigabe für öffentliche Änderungen.

**Abnahme:** Ein Projektkontext kann auf einem zweiten Gerät oder in einem anderen KI-System wiederhergestellt werden, ohne private Daten automatisch zu veröffentlichen.

## 14. Sinnvolle Arbeitssitzung: heute, klein und überprüfbar

Wir sollten heute nicht versuchen, die komplette Plattform zu programmieren. Der sinnvolle Fortschritt ist, eine erste durchgehende Strecke zu definieren und anschließend implementierbar zu machen.

### Aufgabe A – vorhandenen Kern sichern
1. `matrixgame-core/matrixgame-core` öffnen.
2. `./test.sh` ausführen.
3. Ergebnis und Java-Version notieren.
4. Falls Tests scheitern, erst Fehler reproduzieren, dann ändern.

### Aufgabe B – erstes Modell auswählen
Empfohlen: eine einfache Dichte-Definition oder eine Kreisbahn. Dichte ist ideal für die erste Einheitenprüfung; die Kreisbahn ist der erste physikalische Engine-Test.

### Aufgabe C – minimale Sprache
Zunächst nur:
- `project`
- `constant`
- `parameter`
- `source`
- `experiment`
- `report`

Keine freien Schleifen, keine freie Netzwerkkommunikation, keine arbitrary Java-Ausführung.

### Aufgabe D – erstes Erfolgskriterium
Eine kleine LUMINI-Quelldatei wird gelesen, syntaktisch geprüft, auf Einheiten kontrolliert und in eine normalisierte JSON-IR übersetzt. Ein Test prüft, dass eine Dimensionsverletzung zurückgewiesen wird.

Das wäre ein echter erster Compiler-Fortschritt – klein genug, um ihn zu überprüfen, aber groß genug, um die zukünftige Architektur zu tragen.

---

## 15. Projektregeln

1. **Die Quelldatei ist die Modellbeschreibung; die IR ist die maschinenlesbare Übergabe.**
2. **Die Engine ist für die Berechnung zuständig; die UI visualisiert.**
3. **Messdaten werden nicht mit Modellannahmen vermischt.**
4. **Jede Simulation speichert Seed, Version, Datenstand und Parameter.**
5. **Ein Ergebnis darf eine Hypothese widerlegen.**
6. **Ein Modellresultat wird nicht ohne unabhängige Prüfung zum FACT.**
7. **KI-generierter Code wird nicht ungeprüft ausgeführt.**
8. **Private Karriere- und Lernprofile bleiben privat.**
9. **Proprietäre Daten werden nur innerhalb der erlaubten Nutzungsbedingungen verwendet.**
10. **Jede Funktion erhält Tests und überprüfbare Abnahmekriterien, bevor sie als VERIFIED gilt.**

## 16. Quellen und Standards

- ANTLR 4 – Parsergenerator und Java-Codegenerierung: https://www.antlr.org/ und https://github.com/antlr/antlr4/blob/dev/doc/getting-started.md
- JSON Schema – Spezifikation, aktuell Draft 2020-12: https://json-schema.org/specification
- BIPM – SI-Broschüre, neunte Ausgabe, 2026 aktualisiert: https://doi.org/10.59161/AUEZ1291
- NIST / CODATA – fundamentale Konstanten: https://www.nist.gov/programs-projects/codata-values-fundamental-physical-constants
- ODD-Protokoll für agentenbasierte Modelle: https://pubs.usgs.gov/publication/70161470
- ODD-Update zur Reproduzierbarkeit und Modellbeschreibung: https://doi.org/10.18564/jasss.4259
- Bundesagentur für Arbeit – BERUFENET Anwendungsentwicklung: https://web.arbeitsagentur.de/berufenet/beruf/steckbrief/7856
- Bundesagentur für Arbeit – IKT-Arbeitsmarkt: https://statistik.arbeitsagentur.de/DE/Statischer-Content/Statistiken/Themen-im-Fokus/Berufe/AkademikerInnen/Berufsgruppen/2-2-Informatik.html
- Europäische Kommission – ESCO API: https://esco.ec.europa.eu/en/use-esco/use-esco-services-api

---

## 17. Leitbild

**Ein Modell soll verständlich sein, bevor es ausgeführt wird.**  
**Eine Simulation soll reproduzierbar sein, bevor sie prognostisch ernst genommen wird.**  
**Eine Prognose soll Unsicherheit zeigen, bevor sie Entscheidungen beeinflusst.**  
**Eine KI soll übersetzen und unterstützen, nicht Evidenz erfinden.**

Die gemeinsame technische Vision lautet:

**Sprache → Modell → Compiler → Simulation → Messung → Wissen → Lernen → bessere Entscheidungen.**

*Make the game better than the players found it.*
