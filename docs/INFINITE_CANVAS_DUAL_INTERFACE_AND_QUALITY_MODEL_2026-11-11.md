# Matrixgame – Dual Interface, Infinite Canvas und Qualitätsmodell

**Version:** 0.1 · **Stichtag:** 11. November 2026  
**Status:** Architektur- und Abnahmespezifikation; keine Behauptung, dass alle beschriebenen Funktionen bereits implementiert sind.  
**Bezug:** LUMINI-Compiler- und Simulationsarchitektur vom 7. November 2026.

> Ein Simulationszustand – zwei gleichberechtigte Ausgabekanäle – ein überprüfbarer Lauf.

## 1. Ziel und Architekturentscheidung

Der Matrixgame-Simulator soll nicht zwei getrennte Systeme besitzen – eines für Text und eines für Grafik. Stattdessen berechnet eine zentrale Engine den Zustand genau einmal. Aus demselben unveränderlichen Snapshot entstehen:

1. **Menschenlesbare Konsolenausgabe** für Entwickler und Debugging.
2. **Maschinenlesbare Ausgabe** (JSON Lines / JSON) für andere Programme, KI-Systeme, Tests und spätere Netzwerkschnittstellen.
3. **Interaktive Canvas-Ansicht** für räumliche Muster, Zustandsvariablen, Beziehungen und Kausalverläufe.

Die drei Ausgabekanäle sind Ansichten derselben Simulation. Eine Visualisierung darf keine eigene, abweichende Simulationslogik enthalten.

### 1.1 Datenfluss

~~~text
LUMINI / Konfiguration / Szenario
              |
              v
       Model IR + Validierung
              |
              v
       Simulation Engine
              |
       WorldSnapshot(t)
              |
       +------+------------------+
       |             |           |
       v             v           v
 Human Console    JSONL Log   Canvas Renderer
       |             |           |
       v             v           v
 lesbarer Text   Parser/LLM    Pan / Zoom / Auswahl
       |             |           |
       +-------------+-----------+
                     |
                     v
             Feedback / Analyse
                     |
                     v
             neues Experiment
~~~

### 1.2 Trennung der Verantwortlichkeiten

- **Simulation Core:** Zustände, Regeln, Aktionen, Kausalübergänge und Zeit.
- **Observation Layer:** Was jede Unit tatsächlich sehen oder hören darf.
- **Decision Layer:** Auswahl der Handlung durch austauschbare Strategien.
- **Communication Layer:** Nachrichten, Reichweite, Kosten, Speicher und Zustellregeln.
- **Telemetry Layer:** Ereignisse, Messwerte und reproduzierbare Protokolle.
- **Canvas Layer:** Projektion und Darstellung, aber keine Änderung der Simulationsphysik.
- **Console Layer:** Menschenlesbare Statusberichte und strukturierte Daten.
- **Evaluation Layer:** Tests, Vergleichsläufe, Gütekriterien und Lernquests.

Diese Trennung ermöglicht spätere Java-, Python-, Web- oder andere Adapter, ohne das Modell neu zu erfinden.

## 2. Gemeinsamer Datenvertrag

Die zentrale Engine liefert einen unveränderlichen Snapshot pro Simulationsschritt. Renderer und Reporter lesen ihn, verändern ihn aber nicht.

### 2.1 Kernobjekte

| Objekt | Zweck |
|---|---|
| WorldSnapshot | Vollständiger, versionierter Zustand zu einem Tick |
| EntitySnapshot | Sichtbarer Zustand einer Unit oder eines Objekts |
| VariableDescriptor | Name, Typ, Einheit, Wertebereich und Beschreibung einer Variablen |
| RelationSnapshot | Beziehung zwischen zwei Entitäten |
| SimulationEvent | Ereignis mit Zeit, Quelle, Ziel und Nutzdaten |
| Observation | Wahrnehmung einer Unit gemäß ihren Sensor- und Sichtregeln |
| Message | Gesendete Nachricht mit Sender, Empfänger/Gruppe, Reichweite und Ablaufzeit |
| DecisionTrace | Kandidaten, Bewertungsfunktion, gewählter Zug und Begründung |
| RunManifest | Seed, Versionen, Parameter, Szenario und Herkunft des Laufs |

### 2.2 Beispiel für einen maschinenlesbaren Ereignisstrom

~~~json
{
  "schemaVersion": "1.0",
  "runId": "demo-seed-42",
  "tick": 12,
  "simulationTime": 12.0,
  "eventType": "MESSAGE_DELIVERED",
  "sourceEntityId": "unit-03",
  "targetEntityId": "unit-07",
  "payload": {
    "kind": "RESOURCE_LOCATION",
    "resourceId": "water-02",
    "position": {"x": 14.0, "y": 8.0, "z": 0.0},
    "confidence": 0.85
  },
  "provenance": {
    "source": "agent-observation",
    "memoryId": "memory-03-18"
  }
}
~~~

Das Beispiel zeigt ein mögliches Spielereignis, keine bereits implementierte Nachrichtenschnittstelle. Ein Wert wie confidence beschreibt zunächst eine interne Modellbewertung und ist nicht automatisch eine empirisch kalibrierte Wahrscheinlichkeit.

### 2.3 RunManifest

Jeder Lauf protokolliert mindestens:

- eindeutige Run-ID und Szenario-ID,
- Modell- und Schema-Version,
- Software-Commit oder Build-Kennung,
- Zufalls-Seed,
- Zeitschritt und Simulationsdauer,
- alle relevanten Parameter,
- aktivierte Agentenstrategien,
- Eingabedaten und deren Herkunft,
- Start- und Endstatus,
- Test- und Validierungsergebnisse.

Ein gleicher Seed soll bei gleichem Modell, gleichen Eingaben und gleicher Laufzeitumgebung reproduzierbare Ergebnisse liefern. Plattformübergreifende bitgenaue Gleichheit ist eine zusätzliche Anforderung und muss separat getestet werden.

## 3. Konsolenausgabe: für Menschen und Maschinen

Die Konsole erhält zwei Modi.

### 3.1 Human Mode

Kurze, lesbare Statusmeldungen, zum Beispiel:

~~~text
[MATRIXGAME] run=demo-seed-42 tick=12
Units: 24 | Groups: 3 | Resources: 11
Messages: sent=8 delivered=6 expired=1
Actions: move=9 gather=3 share=2 rest=4
World: energy=0.74 | resource_balance=stable
Warnings: 1 | Tests: 12/12 passed
~~~

Werte und Kennzahlen müssen aus dem tatsächlichen Snapshot stammen. Nicht verfügbare Werte werden als unknown oder n/a ausgegeben, niemals erfunden.

### 3.2 Machine Mode

- JSON Lines: ein valides JSON-Objekt pro Zeile.
- stabile Feldnamen und versioniertes Schema,
- keine dekorativen Zusatztexte im JSONL-Kanal,
- Fehler mit Fehlercode, Tick, Entität und Kontext,
- optionaler Export kompletter Snapshots und Ergebniszusammenfassungen.

So können Sprachmodelle, Skripte und spätere Rechencluster die Daten einlesen, ohne Konsolentext parsen zu müssen.

## 4. Infinite Canvas: frei zoombare Projektion

Das Canvas ist eine zweidimensionale Projektion eines Simulationszustands. Es ist nicht selbst die Simulation.

### 4.1 Welt- und Bildschirmkoordinaten

Für eine zentrierte Kamera kann die Abbildung vereinfacht so aussehen:

~~~text
screenX = viewportCenterX + (worldX - cameraX) * zoom
screenY = viewportCenterY - (worldY - cameraY) * zoom
~~~

Die Y-Richtung ist in typischen Bildschirmkoordinaten nach unten positiv. Deshalb wird die Welt-Y-Achse bei der Darstellung invertiert.

**Zoom-Verhalten:**

- Zoom erfolgt um die Position des Mauszeigers. Der Punkt unter dem Cursor bleibt beim Vergrößern möglichst an derselben Bildschirmposition.
- Pan verschiebt die Kamera, nicht die Weltobjekte.
- Die Weltkoordinaten haben keine künstliche Canvas-Grenze.
- Die numerische Zoom-Skalierung braucht trotzdem konfigurierbare Sicherheitsgrenzen gegen Gleitkomma-Unterlauf, Überlauf und unbrauchbare Darstellung. Diese Grenzen sind keine Grenzen der simulierten Welt.
- Rasterabstände passen sich dynamisch an den Zoom an.
- Objekte außerhalb des sichtbaren Bereichs werden nicht unnötig gezeichnet (Viewport Culling).
- Sehr große Szenen können Detailstufen (Level of Detail) verwenden.

### 4.2 Freie Achsenbelegung

Jede Variable, die als numerische oder kategoriale Größe definiert ist, kann – soweit sinnvoll – auf eine visuelle Dimension abgebildet werden.

| Projektion | X-Achse | Y-Achse |
|---|---|---|
| Raum | Position X | Position Y |
| Energie | Energie | Zeit |
| Lernen | Erfahrung | Erfolgsquote |
| Interaktion | soziale Distanz | Kommunikationshäufigkeit |
| Ökologie | Wasser | Vegetation |
| Prognose | Zeithorizont | Ergebnisverteilung |

Die Achsenbelegung wird nicht fest in der Unit-Klasse codiert, sondern über VariableDescriptor und AxisMapping konfiguriert.

### 4.3 Mini-Legende im Canvas

Die Legende zeigt jederzeit:

~~~text
X: energy [0.0 .. 1.0] — linear
Y: productivity [0.0 .. 1.0] — linear
Color: agentRole
Size: resourceStock
Visible domain: X 0.20–0.95 | Y 0.10–0.88
Zoom: 1.25x | Camera: (12.0, -4.0)
~~~

Zusätzlich soll eine Auswahl im Canvas den vollständigen Variablennamen, die Einheit, den aktuellen Wert, den zulässigen Bereich und die Herkunft anzeigen.

### 4.4 Weitere Darstellungsformen

- Punkte: Units, Ressourcen, Orte oder Messwerte.
- Kanten: Kommunikation, Teams, Beziehungen und Kausalverknüpfungen.
- Pfeile: gerichtete Aktionen oder Informationsflüsse.
- Zeitspuren: zurückliegende Positionen und Zustandsverläufe.
- Heatmaps: Dichte, Aktivität, Risiken oder Ressourcen.
- Verteilungen: Monte-Carlo-Ergebnisse als Histogramm oder Band.
- Inspector: ausgewähltes Objekt mit Eigenschaften, Verlauf und Entscheidungsprotokoll.

Farbe darf nie die einzige Bedeutungsinformation sein. Labels, Symbole oder Linienarten müssen die Darstellung auch für farbfehlsichtige Menschen lesbar machen.

### 4.5 Hawkins-Skala und symbolische Werte

Eine Hawkins-Skala kann als optionale, ausdrücklich symbolische Spielvariable angezeigt werden. Sie ist kein wissenschaftlich validiertes Messinstrument für Bewusstsein, psychische Gesundheit, menschlichen Wert oder Persönlichkeitsreife. Für reale Selbstberichte sind neutrale, freiwillige und klar definierte Variablen vorzuziehen.

## 5. Agenten, Kommunikation und Entscheidungsfindung

Die nächste Simulationsstufe soll mehr können als zufällige Positionsänderungen.

### 5.1 Ein Simulations-Tick

Empfohlene Reihenfolge:

1. **Snapshot:** Alle Units lesen denselben Weltzustand zu Beginn des Ticks.
2. **Perception:** Jede Unit erstellt ihre lokale Observation.
3. **Planning:** Jede Unit berechnet mögliche Handlungen.
4. **Decision:** Die Strategie wählt einen Zug und protokolliert ihre Bewertung.
5. **Resolution:** Konflikte, Ressourcen und konkurrierende Aktionen werden nach klaren Regeln aufgelöst.
6. **Commit:** Änderungen werden gemeinsam angewendet.
7. **Communication:** Nachrichten werden zugestellt, wenn Reichweite und Regeln erfüllt sind.
8. **Metrics:** Messwerte und Ereignisse werden erzeugt.
9. **Snapshot:** Neuer Zustand wird an Konsole, JSONL und Canvas ausgegeben.

Das gemeinsame Planen vor dem Commit vermeidet, dass zufällig zuerst aktualisierte Units einen unfairen Informationsvorteil erhalten.

### 5.2 Bewegungs- und Interaktionsregeln

Jede Unit besitzt mindestens:

- Position und Geschwindigkeit,
- Maximalgeschwindigkeit und Beschleunigungsgrenzen,
- Energie- oder Aktionsbudget,
- Sensorreichweite,
- Kommunikationsreichweite,
- verfügbaren Speicher,
- Rollen- oder Strategie-ID,
- lokale Wahrnehmung und Gedächtnis.

Eine Bewegung muss Geschwindigkeits- und Beschleunigungsgrenzen beachten. Energieverbrauch wird nur dann als physikalischer Energieerhaltungssatz behandelt, wenn ein entsprechendes physikalisches Modell mit definierten Einheiten und Bilanzgleichungen implementiert ist. In einem abstrahierten Spiel kann Energie stattdessen ein Ressourcenbudget sein.

### 5.3 Kommunikation ist ein eigener Mechanismus

Eine Nachricht braucht:

- Sender und Empfänger (Einzelperson, Gruppe oder Broadcast),
- Inhalt und Datentyp,
- Kommunikationsreichweite,
- Übertragungskosten,
- Zeitstempel und Ablaufzeit,
- Herkunft und Vertrauens-/Validierungsstatus,
- Regeln zu Zustimmung und Sichtbarkeit.

Ressourcenorte können geteilt und gehandelt werden. Die Empfänger kennen den Ort aber erst nach einer gültigen Wahrnehmung oder Nachricht. Es gibt kein allwissendes kollektives Gedächtnis.

### 5.4 Austauschbare Entscheidungsstrategien

Die Engine soll Strategien als Plugins behandeln:

- Random / Baseline,
- Rule-Based,
- Greedy / Utility,
- Minimax / N-Max-Max,
- Monte-Carlo Tree Search,
- Evolutionary Search,
- später lernende Policies.

Jede Strategie implementiert denselben Vertrag: Eingabe ist eine Observation plus erlaubte Aktionen; Ausgabe ist eine Aktion und ein DecisionTrace. Ein MCTS oder neuronales Netz wird nicht als trainiert bezeichnet, solange es nicht tatsächlich trainiert und evaluiert wurde.

## 6. Gütekriterien für Code, Modelle und Transkript-Transformation

Ein einzelner Gesamtwert kann schwerwiegende Fehler verdecken. Deshalb werden Qualitätsprüfungen zuerst als **Gates** geführt und danach als mehrdimensionales Qualitätsprofil dargestellt.

### 6.1 Qualitätsdimensionen

| Dimension | Prüffrage | Beispiel für Nachweis |
|---|---|---|
| Kompilierbarkeit | Baut der Code in der dokumentierten Umgebung? | sauberer Build |
| Funktionalität | Erfüllt das Modul seine spezifizierten Aufgaben? | Unit- und Integrationstests |
| Determinismus | Ist derselbe Lauf reproduzierbar? | gleicher Seed und gleicher Run-Hash |
| Kausale Nachvollziehbarkeit | Sind Eingriff, Mechanismus, Verzögerung und Ergebnis dokumentiert? | Interventionstest und DecisionTrace |
| Dimensionsrichtigkeit | Stimmen Einheiten und Größenordnungen? | Unit- und Dimensional-Checks |
| Modellvalidität | Passt das Modell zu Referenzdaten oder etablierten Grenzfällen? | Benchmark und Fehleranalyse |
| Numerische Stabilität | Bleiben Werte im gültigen Bereich und konvergiert die Methode? | Grenzfall- und Konvergenztests |
| Canvas-Kompatibilität | Können relevante Zustände mit Name, Einheit und Skala dargestellt werden? | Achsen- und Legendentest |
| Schnittstellenqualität | Sind Konsolen-, JSONL- und Canvas-Ausgaben konsistent? | Snapshot-Gleichheitstest |
| Performance | Erfüllt das System die Zielgröße? | Ticks/s, Renderzeit, Speicherbedarf |
| Didaktik | Versteht ein Nutzer das Modell und kann er es bedienen? | dokumentierte Nutzertests |
| Wartbarkeit | Sind Module klein, dokumentiert und austauschbar? | Modulgrenzen und Abhängigkeitstests |
| Sicherheit und Datenschutz | Werden Daten, Ausführung und Berechtigungen kontrolliert? | Threat-Review, Zustimmung, Stop-Funktion |
| Provenienz | Sind Quelle, Lizenz, Transformation und Änderungen nachvollziehbar? | Manifest und Herkunftsangaben |

### 6.2 Harte Qualitäts-Gates

Vor einer Freigabe müssen mindestens erfüllt sein:

- Build und verpflichtende Tests bestehen.
- Keine unkontrollierten Zufallsquellen im reproduzierbaren Modus.
- Modellvariablen und Einheiten sind definiert.
- Nicht unterstützte oder ungeprüfte Annahmen sind sichtbar markiert.
- JSON-Ausgaben sind schema-valid.
- Canvas und Konsole zeigen dieselbe Snapshot-Version.
- Keine automatische Ausführung fremder, KI-generierter Programme ohne Prüfung und isolierte Ausführungsumgebung.

### 6.3 Kausalität ist nicht dasselbe wie Invertierbarkeit

Eine frühere Formulierung, nach der Kausalität über eine allgemeine Invertierbarkeit gemessen werden könnte, wäre fachlich zu stark. Viele kausale Systeme sind nicht eindeutig invertierbar: Mehrere Ursachen können denselben Effekt erzeugen, und Beobachtungsdaten allein identifizieren nicht immer den Mechanismus.

Besser sind:

- vollständige Dokumentation der angenommenen Kausalkette,
- explizite Eingriffsvariablen,
- Vergleich mit einer Baseline,
- zeitliche Verzögerung und Randbedingungen,
- Gegenfaktual- oder Interventionsvergleiche, soweit im Modell definiert,
- Unsicherheits- und Sensitivitätsanalyse,
- externe Validierung, sofern reale Daten verfügbar sind.

### 6.4 Visualisierungsqualität messbar machen

Ein Canvas-Compatibility-Test sollte prüfen:

1. Hat jede darstellbare Variable einen eindeutigen Namen und eine Beschreibung?
2. Sind Einheit und Wertebereich vorhanden?
3. Funktionieren Achsenwechsel und automatische Skalierung?
4. Stimmen angezeigter Punkt und zugrunde liegender Snapshot überein?
5. Sind Objekt, Kante, Farbe und Größe auch ohne Farbe unterscheidbar?
6. Bleiben Pan und Zoom bei sehr großen und sehr kleinen Koordinaten stabil?
7. Kann ein ausgewähltes Element bis zu seiner Modellquelle zurückverfolgt werden?

### 6.5 Ergebnisdarstellung statt Scheinpräzision

Prognosen sollen Verteilungen, Intervalle und Annahmen ausgeben. Eine Zahl wie 0.85 ist nicht automatisch eine kalibrierte Erfolgswahrscheinlichkeit. Kalibrierung erfordert wiederholte Vorhersagen und später beobachtete Outcomes. Bei fehlenden Daten wird das Ergebnis als Modellschätzung oder Szenario gekennzeichnet.

## 7. Transkript-zu-Modul-Pipeline

Die Übersetzung von Vorträgen, Tutorials und Transkripten in Code wird als mehrstufiger, überprüfbarer Prozess behandelt.

~~~text
Transcript + timecodes + source metadata
                    |
                    v
           Segment extraction
                    |
                    v
    Claims / terms / equations / examples
                    |
                    v
  Entity-Relationship + causal hypotheses
                    |
                    v
         LUMINI model draft
                    |
                    v
       type / unit / schema checks
                    |
                    v
       generated Model IR + tests
                    |
                    v
       human review / approval
                    |
                    v
       sandboxed simulation run
                    |
                    v
       metrics + Canvas + JSONL
                    |
                    v
      documented reusable module
~~~

Jeder Baustein speichert:

- Originalquelle mit Zeitstempel oder Abschnitt,
- extrahierte Behauptung,
- Kennzeichnung FACT / MODEL / HYPOTHESIS / OPEN / FICTION,
- Transformationsannahmen,
- verwendete Formel und Einheit,
- erzeugten Code und Teststatus,
- bekannte Grenzen und offene Fragen.

Ein Transkript ist eine Quelle, nicht automatisch ein korrektes Modell. Füllwörter, vereinfachte Erklärungen, Transkriptionsfehler und unvollständige Beispiele müssen erkannt und dokumentiert werden. KI-generierter Code bleibt ein Vorschlag bis zu Build, Tests und Review.

## 8. Lern- und Quest-Mechanik

Die Didaktik folgt einem wiederverwendbaren Loop:

**Entdecken → Strukturieren → Erklären → Ausführen → Vergleichen → Verbessern → Teilen.**

Eine Quest ist abgeschlossen, wenn ein überprüfbarer Lern- oder Entwicklungsnachweis vorliegt – nicht nur, wenn ein Punktestand steigt.

### Beispielquest: Dual Interface MVP

**Ziel:** Ein Simulationslauf produziert konsistente Konsole-, JSONL- und Canvas-Ausgaben.

**Aufgaben:**
1. Gemeinsamen WorldSnapshot definieren.
2. Mindestens drei VariableDescriptor-Einträge erstellen.
3. Human- und JSONL-Reporter aus demselben Snapshot speisen.
4. Canvas mit freier X-/Y-Projektion, Legende, Pan und cursorzentriertem Zoom verbinden.
5. Snapshot-Gleichheit testen.
6. RunManifest speichern und den Run reproduzieren.

**Belohnung:** Nachweisbare Fähigkeiten in Datenmodellierung, Java, Softwaretests, Visualisierung und Systemarchitektur.

### Fairnessregeln

- Opt-out und Ablehnung sind keine negativen Leistungen.
- Es werden keine Menschen nach einem simulierten Bewusstseinswert gerankt.
- Belohnungen richten sich nach Qualität, Nachvollziehbarkeit, Kooperation und Nutzen.
- Persönliche Wohlbefindensdaten bleiben freiwillig und standardmäßig privat.

## 9. Implementierungsroadmap

### Phase A – Datenvertrag
WorldSnapshot, EntitySnapshot, VariableDescriptor, SimulationEvent und RunManifest definieren und mit Beispieldaten testen.

### Phase B – Duale Ausgabe
Human Console und JSONL aus demselben Snapshot erzeugen. Schema und Snapshot-Identität testen.

### Phase C – Canvas-Adapter
Pan, cursorzentriertes Zoom, adaptive Rasterung, dynamische Legende, frei wählbare Variablenachsen, Auswahl und Inspector.

### Phase D – Lebendige Units
Wahrnehmung, Ressourcen, Speicher, Kommunikation, Rollen und explizite Aktionskosten integrieren. Zunächst regelbasierte Agenten und Baselines.

### Phase E – Strategie- und Prognosemodule
Minimax, MCTS und evolutionäre Suche als getrennte Strategiemodule. Jede Methode erhält Vergleichsbaselines und identische Testbedingungen.

### Phase F – LUMINI-Compiler
Die bereits dokumentierte LUMINI-Kette um die Canvas- und Telemetrie-Verträge erweitern: LUMINI → AST → Model IR → Engine → RunManifest → JSONL / Canvas.

**Wichtig:** Die Phasen A–F sind ein Entwicklungsplan. Vorhandene Prototypen sind einzeln zu prüfen; dieser Text erklärt keine der geplanten Phasen automatisch für implementiert.

## 10. Definition of Done für den ersten integrierten MVP

Der erste integrierte MVP gilt erst dann als bestanden, wenn:

- eine einzelne Java-Anwendung ohne zusätzliche externe Bibliotheken startet,
- eine feste Seed-Konfiguration reproduzierbare Agentenläufe erzeugt,
- mehrere Units Ressourcen wahrnehmen, bewegen und Nachrichten austauschen,
- jede Unit nur ihre erlaubte Observation verwendet,
- alle Aktionen über einen gemeinsamen Tick-Ablauf verarbeitet werden,
- die Konsole Statusmeldungen ausgibt,
- JSONL-Events schema-valid geschrieben werden,
- das Canvas dieselben Entities und Variablen aus dem Snapshot darstellt,
- X- und Y-Achse zur Laufzeit ausgewählt und beschriftet werden können,
- die Mini-Legende Variablennamen, Einheit, Bereich und Skalierung zeigt,
- Pan und cursorzentriertes Zoom in sehr unterschiedlichen Maßstäben funktionieren,
- ein RunManifest Seed, Parameter und Modellversion enthält,
- mindestens ein Selbsttest die Identität zwischen Snapshot, JSONL und Canvas-Daten prüft.

Erst danach werden MCTS, evolutionäre Selektion, große Populationen, neuronale Netze oder zusätzliche wissenschaftliche Domänen eingebaut.

## 11. Anschluss an bestehende Repository-Dokumentation

Diese Spezifikation ergänzt die bereits vorhandene LUMINI-Compiler- und Simulationsarchitektur:

- docs/LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md
- docs/UNIVERSAL_KNOWLEDGE_ENGINE.md
- docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md
- matrixgame-protocol/game-state.schema.json
- matrixgame-core/matrixgame-core/

Die zentrale Architekturentscheidung lautet damit:

**Eine Engine, ein gemeinsamer Snapshot, mehrere konsistente Ansichten, reproduzierbare Läufe und überprüfbare Qualitätsprofile.**

So kann das Matrixgame zugleich für Menschen visuell verständlich, für Entwickler testbar, für KI-Systeme strukturiert lesbar und für die Open-Source-Community modular erweiterbar werden.
