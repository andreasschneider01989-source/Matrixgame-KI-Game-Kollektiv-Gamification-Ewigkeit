# LUMINI Formel-Compiler – ausführbarer MVP

**Version:** 0.1.0  
**Stand:** 23. November 2026  
**Status:** Implementierter, getesteter Prototyp für skalare Formeln; keine vollständige Simulationsplattform.  
**Paket:** `lumini_compiler`  
**Beziehung:** baut auf `matrixgame_science.units` und der vorhandenen LUMINI-/Model-IR-Architektur auf.

> Eine Formel wird nicht direkt zu ausführbarem Fremdcode. Sie wird gelesen, in einen AST übersetzt, auf Namen und Einheiten geprüft, als Model IR gespeichert und anschließend durch eine eng begrenzte Formel-Engine ausgewertet.

## 1. Was jetzt tatsächlich funktioniert

Der MVP implementiert eine erste schmale Teilmenge der geplanten Sprache:

1. `.lumini`-Quelldatei mit Modell-ID, Version und wissenschaftlichem Status einlesen.
2. Lexer: Zahlen, Namen, Zeichen, Strings, Zeilen und Kommentare tokenisieren.
3. Parser: Modellblock und mathematische Ausdrücke in einen AST übersetzen.
4. Namensauflösung: Parameter, Konstanten und Formeln auflösen; unbekannte Symbole und Zyklen ablehnen.
5. Dimensionsanalyse: Addition/Subtraktion nur bei gleichen Dimensionen; Multiplikation, Division und ganzzahlige Potenzen kombinieren SI-Dimensionen.
6. Einheiten: SI-Basiseinheiten sowie ein kleiner Satz abgeleiteter und skalierten Einheiten in kohärente SI-Werte umrechnen.
7. Typisierte Model-IR-Ausgabe: AST, Werte in SI, Dimensionsvektoren, Modellstatus, Quell-Hash und Validierungsstatus speichern.
8. Ausführung über die registrierte Engine `formula.scalar`.
9. Reproduzierbares Run Manifest: gleiche IR und Compiler-Version ergeben denselben Run-Identifier und dieselben numerischen Ergebnisse.
10. CLI: `check`, `compile` und `run`.

Die Implementierung ist bewusst Python 3.10+-kompatibel und benötigt zur Laufzeit keine zusätzliche Bibliothek außer dem bereits im Projekt vorhandenen `matrixgame_science`-Paket.

## 2. Architektur und Zuständigkeiten

```text
.lumini source
    |
    v
lexer.py -- Tokens mit Zeile/Spalte
    |
    v
parser.py -- Modellblock + Pratt-Parser für Ausdrücke
    |
    v
ast_nodes.py -- unveränderlicher AST
    |
    v
evaluator.py -- sichere Operatoren + Einheiten + Abhängigkeiten
    |
    v
compiler.py -- semantische Prüfung + versioniertes Model IR
    |
    v
formula.scalar -- deterministische skalare Auswertung
    |
    v
Run Manifest -- Hashes + Werte + Dimensionen + Prüfstatus
```

Verzeichnisstruktur:

```text
lumini_compiler/
  __init__.py       öffentliche Python-API
  __main__.py       python -m lumini_compiler
  ast_nodes.py      AST-Knotentypen und JSON-Konvertierung
  lexer.py          Tokenisierung und Quellpositionen
  parser.py         Modellgrammatik und Ausdrucksparser
  evaluator.py      Dimensionsrechnung und erlaubte Funktionen
  compiler.py       IR-Generator und Runner-Vertrag
  cli.py            Kommandozeile

matrixgame_science/units.py   gemeinsame SI-Dimensionsalgebra
matrixgame-protocol/
  lumini-model-ir.schema.json JSON-Schema-Vertrag für IR 0.1

examples/lumini/
  earth_surface_gravity.lumini

tests/test_lumini_compiler.py
```

### Warum zunächst ein Pratt-Parser statt ANTLR?

Die volle Spracharchitektur kann später weiterhin ANTLR 4 verwenden. Für diese kleine, bewusst begrenzte Formelsprache ist ein kompakter Pratt-Parser einfacher zu testen und benötigt keine zusätzliche Laufzeit- oder Build-Abhängigkeit. Die AST- und IR-Schnittstellen sind von dieser Parserentscheidung getrennt.

## 3. Erste LUMINI-Syntax

```lumini
model "earth-surface-gravity-demo" version "0.1" {
  status MODEL
  engine "formula.scalar"

  constant gravitational_constant = 6.67430e-11 * m^3 / (kg * s^2)
    source "Illustrative value; verify source revision" evidence MODEL
  parameter earth_mass = 5.9722e24 * kg
  parameter earth_radius = 6.371e6 * m

  formula surface_gravity = gravitational_constant * earth_mass / earth_radius^2 : m / s^2
  output surface_gravity
}
```

Die Beispieldatei im Repository enthält zusätzlich eine zweite Formel (`weight_at_surface`), die eine zuvor berechnete Formel weiterverwendet.

### Unterstützte Anweisungen

- `model "id" version "version" { ... }`
- `status FACT | MODEL | HYPOTHESIS | OPEN | FICTION`
- `engine "formula.scalar"`
- `description "... "`
- `parameter name = expression [source "..."] [evidence STATUS]`
- `constant name = expression [source "..."] [evidence STATUS]`
- `formula name = expression [: expected-unit] [source "..."] [evidence STATUS]`
- `output name`

Kommentare beginnen mit `#` oder `//`. Eine Anweisung steht jeweils in einer Zeile; Semikolons sind als Trennzeichen möglich.

### Ausdrucksoperatoren

- Klammern und unäre Vorzeichen: `(x)`, `-x`
- Addition und Subtraktion: `+`, `-` – nur bei dimensionsgleichen Größen
- Multiplikation und Division: `*`, `/`
- Ganzzahlige Potenzen: `^` – in v0.1 auf den Exponentenbereich -64 bis +64 begrenzt
- Registrierte Funktionen: `sqrt`, `abs`, `min`, `max`, `sin`, `cos`, `exp`, `ln`

Funktionen haben definierte Eingangsbedingungen. Beispielsweise benötigt `sqrt(x)` nichtnegative Werte und gerade SI-Dimensionsexponenten. `sin`, `cos`, `exp` und `ln` akzeptieren nur dimensionslose Argumente.

### Einheiten im MVP

Basiseinheiten: `kg`, `m`, `s`, `A`, `K`, `mol`, `cd`.  
Zusätzlich: `g`, `km`, `cm`, `mm`, `min`, `h`, `day`, `Hz`, `N`, `J`, `W`, `Pa`, `L`, `rad`.

Alle Werte werden intern in kohärenten SI-Basiseinheiten dargestellt. `2 * km` wird beispielsweise zu `2000 m`. Temperatur-Offsets wie Celsius und komplexe Einheiten-/Präfixregeln sind noch nicht implementiert.

## 4. Kompilieren und ausführen

Im Repository-Hauptverzeichnis:

```bash
python -m pip install -e .
python -m lumini_compiler check examples/lumini/earth_surface_gravity.lumini
python -m lumini_compiler compile examples/lumini/earth_surface_gravity.lumini
python -m lumini_compiler run examples/lumini/earth_surface_gravity.lumini
```

Alternativ stehen nach der Installation die CLI-Befehle `lumini check`, `lumini compile` und `lumini run` zur Verfügung.

Python-API:

```python
from lumini_compiler import compile_file, run_ir

model_ir = compile_file("examples/lumini/earth_surface_gravity.lumini")
run_manifest = run_ir(model_ir)
print(run_manifest["outputs"]["surface_gravity"]["value_si"])
```

`check` erzeugt noch keine Ausführung. `compile` schreibt eine JSON-Datei mit dem Model IR. `run` kompiliert und wertet das Modell über die Formel-Engine aus.

## 5. Beispielergebnis und Interpretation

Für die im Beispiel eingetragenen Näherungswerte erhält der Compiler:

- Oberflächenbeschleunigung: ungefähr **9,8203 m/s²**.
- Gewichtskraft für die Demo-Masse von 1 kg: ungefähr **9,8203 N**.
- Dimensionsvektor für Beschleunigung: `[0, 1, -2, 0, 0, 0, 0]`.
- Dimensionsvektor für Kraft: `[1, 1, -2, 0, 0, 0, 0]`.

Die Zahl ist ein Rechenergebnis aus den angegebenen Modellparametern. Sie ist kein neuer Messwert und belegt weder die Genauigkeit der Eingabekonstanten noch die empirische Gültigkeit eines größeren Erdmodells.

## 6. Sicherheit und Vertrauensgrenzen

- Es gibt kein `eval`, `exec`, Shell-Aufruf, Netzwerkzugriff oder dynamisches Laden von Python-Code.
- Quelldateien können nur die erlaubte Grammatik und registrierte Funktionen verwenden.
- Unbekannte Funktionen, Namen, Zyklen, ungültige Zahlen, inkompatible Einheiten und Division durch null führen zu Fehlern.
- SI-Einheiten sind reservierte Symbole und können nicht überschrieben werden.
- Exponenten sind begrenzt, um unverhältnismäßige Berechnungen zu verhindern.
- Der Runner führt nur die registrierte Engine `formula.scalar` aus. Ein beliebiger Engine-String startet keine externe Engine.
- `random_seed` ist `null`, weil dieser deterministische Formellauf keine Zufallszahlen verwendet.
- Der Compiler trennt `epistemic_status` und `implementation_status`. `MODEL` bedeutet nicht „wissenschaftlich bewiesen“.
- `scientific_validation` und `empirical_validation` bleiben ausdrücklich `NOT_PERFORMED`.

Ein erfolgreicher Compile-Lauf bedeutet: Der unterstützte Syntaxausschnitt ist parsebar, die referenzierten Symbole sind auflösbar und die geprüften Dimensionen sind konsistent. Er bedeutet nicht, dass eine Aussage wahr, vollständig, kalibriert oder empirisch validiert ist.

## 7. Tests und überprüfbare Kriterien

Der aktuelle Compiler-Testumfang umfasst 15 Tests, unter anderem:

- Beispielmodell und erwartete SI-Dimensionen
- reproduzierbare Runs
- Umrechnung von km in m
- `sqrt` mit dimensionsbehafteten Größen
- Ablehnung inkompatibler Einheiten
- unbekannte Symbole und zyklische Abhängigkeiten
- falsche deklarierte Formeleinheiten
- Division durch null und übergroße Potenzen
- Ablehnung nicht registrierter Funktionen
- Schutz reservierter SI-Symbole
- Prüfung manipulierter IR-Dimensionsverträge
- JSON-Serialisierung, Modellversion und Quell-Hash

Der GitHub-Actions-Lauf soll die Compilerdateien zusammen mit den vorhandenen `matrixgame_science`-Tests installieren, linten und ausführen. Das ist Software-Verifikation für diesen Prototyp, keine physikalische Validierung.

## 8. Was noch nicht implementiert ist

Die folgenden Teile bleiben ausdrücklich Roadmap und dürfen nicht als vorhanden dargestellt werden:

- Übersetzung natürlicher Sprache und Idea Inbox mit den vollständigen Filtern 0–11.
- Freie Variablen-/Vektormodelle, Zustandsräume, Zeitreihen und Differentialgleichungslöser.
- Physik-, Chemie-, Biologie-, Agenten-, Commons-, Karriere- oder Optimierungs-Engines.
- `body`, `world`, `agents`, `experiment`, `dataset`, `observe` und `report` als ausführbare DSL-Blöcke.
- Stochastische Experimente, Seeds, Monte-Carlo-Auswertung und statistische Konfidenzintervalle.
- Automatischer Import externer Daten, Quellenprüfung und Provenienzbeleg durch externe Register.
- Java-Adapter für den Matrix Core und die ältere PhysicsEngine.
- GUI, Infinite Canvas, Mehrsprach-Übersetzung oder automatisierte Freigabe.

## 9. Nächste Ausbaustufen

**0.2 – stärkere Sprache:** benannte Einheiten-/Konstantenregister, Variablenlisten, Vektoren, `let`-Ausdrücke, aussagekräftigere Diagnostik und eine dokumentierte Grammatik.

**0.3 – Engine-Vertrag:** stabiles Python-/Java-unabhängiges IR, JSON-Schema-Validierung im Test, Adapterprotokoll und erste Verbindung zum kompakten Matrix Core.

**0.4 – Experimente:** Anfangszustände, Zeitparameter, wiederholbare Läufe, Seeds, Datensatz-Hashes, RunManifests und Statistik – getrennt von der Formel-Engine.

**0.5 – Idea-to-Model:** sichtbarer FilterTrace, Mehrsprachen-Glossar, Claims mit Quellen und Annahmen, menschliche Prüfung und Freigabestatus.

Die Reihenfolge ist absichtlich inkrementell: erst verlässliche Bausteine und Verträge, dann komplexere Engines. Der vorhandene Python-MVP bleibt als stabile Referenz und Regressionstest erhalten.
