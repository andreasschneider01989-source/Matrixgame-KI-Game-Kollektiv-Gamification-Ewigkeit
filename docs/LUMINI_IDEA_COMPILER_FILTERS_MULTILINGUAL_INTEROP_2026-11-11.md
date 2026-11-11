# LUMINI – Idea Inbox, Compiler-Filter und Mehrsprachen-Interoperabilität

**Version:** 0.1 · **Stichtag:** 11. November 2026  
**Status:** Architekturentwurf und Implementierungsplan  
**Bezug:** LUMINI-Compilerarchitektur sowie Infinite-Canvas-/Dual-Interface-Spezifikation

> Nichts Wertvolles muss sofort ausgearbeitet werden. Ideen werden zuerst sicher erfasst, dann nachvollziehbar gefiltert, strukturiert und bei Eignung in überprüfbare Module transformiert.

## 1. Zweck

Matrixgame erhält eine gemeinsame Eingangsstufe für spontane Gedanken, Tagträume, Aufgaben, Transkripte, wissenschaftliche Quellen, Programmierideen und Beiträge anderer KI-Systeme.

Diese Eingangsstufe soll nicht jede Idee sofort in Code verwandeln. Sie soll:
- den ursprünglichen Gedanken unverändert erhalten,
- wichtige Begriffe und Absichten erkennen,
- Fakten, Modelle, Hypothesen, offene Fragen und Fiktion trennen,
- Beziehungen und mögliche Kausalitäten vorschlagen,
- Nutzen, Aufwand, Risiken und Prüfbarkeit sichtbar machen,
- geeignete nächste Schritte auswählen,
- Ideen bei Eignung in Module, Quests, Experimente oder Codeprojekte überführen,
- fachliche Bedeutung in verständlicher Sprache, mehreren natürlichen Sprachen, Model IR und verschiedenen Programmiersprachen darstellen.

Ein Filter darf eine Idee priorisieren oder zurückstellen, aber nicht unbemerkt löschen.

## 2. Grundmodell: IdeaPacket

Jeder Eingang wird als eigenständiger, versionierbarer Datensatz gespeichert.

| Feld | Inhalt |
|---|---|
| ideaId | stabile eindeutige Kennung |
| originalText | unveränderter Originaltext |
| source | Mensch, Transkript, URL, KI-Agent, Datei oder Experiment |
| createdAt | Erfassungszeitpunkt |
| language | erkannte Originalsprache |
| intent | vermutetes Ziel der Idee |
| entities | erkannte Akteure, Objekte, Gruppen und Systeme |
| relations | Beziehungen zwischen Entitäten |
| claims | einzelne behauptete Aussagen |
| epistemicStatus | FACT / MODEL / HYPOTHESIS / OPEN / FICTION |
| causalHypotheses | vermutete Ursachen, Mechanismen, Bedingungen und Effekte |
| outputTargets | Bericht, Diagramm, LUMINI, Java, Python, SQL, JSON etc. |
| privacyClass | privat, intern, freigabepflichtig oder öffentlich |
| licenseStatus | Lizenz bzw. noch ungeklärt |
| reviewState | aktueller Bearbeitungs- und Freigabestatus |
| provenance | Quellen, Zeitmarken, Autorenschaft und Transformationshistorie |

Der Originaltext bleibt unverändert. Normalisierte Texte, Übersetzungen, extrahierte Claims, Graphen, LUMINI-Dateien und Code sind abgeleitete Artefakte, die auf ihre Quelle zurückverweisen.

## 3. Die Compiler-Filterkette

Jeder Filter gibt ein strukturiertes Resultat und einen sichtbaren FilterTrace-Eintrag aus.

### Filter 0 – INTAKE: Erfassung
Prüft Lesbarkeit und Herkunft. Erzeugt ein IdeaPacket mit unverändertem Originaltext.  
Status: CAPTURED oder INPUT_ERROR.

### Filter 1 – NORMALIZE: Sprach- und Textaufbereitung
Prüft Sprache, Zeichencodierung, Segmentierung, Zeitmarken, offensichtliche Transkriptionsfehler und Mehrdeutigkeiten. Erzeugt eine normalisierte Lesefassung mit Verweis auf den Originaltext. Unsichere Korrekturen werden als Vorschläge markiert; die Bedeutung wird nicht stillschweigend geändert.

### Filter 2 – INTENT: Ziel- und Aufgabenanalyse
Ermittelt, ob es um eine Frage, Erklärung, persönliches Ziel, technische Funktion, Forschungsfrage, kreative Idee oder einen konkreten Auftrag geht. Erzeugt mögliche Intentionen, Zielgruppe, erwarteten Nutzen und offene Rückfragen. Bei Mehrdeutigkeit bleiben mehrere Interpretationen erhalten.

### Filter 3 – ENTITY: Entitäten und Beziehungen
Erkennt Akteure, Objekte, Gruppen, Ressourcen, Umgebungen, Ereignisse und Variablen. Erzeugt einen ER-Graph mit stabilen IDs und typisierten Kanten, beispielsweise:
- MEMBER_OF
- INTERACTS_WITH
- OBSERVES
- COMMUNICATES_WITH
- AFFECTS
- DERIVES_FROM
- SUPPORTS
- CHALLENGES
- IMPLEMENTS
- TESTS

### Filter 4 – EPISTEMIC: Erkenntnisstatus und Evidenz
Ordnet einzelne Aussagen FACT, MODEL, HYPOTHESIS, OPEN oder FICTION zu und verknüpft Quellen, Geltungsbereich, Datum, Unsicherheit und Evidenzstatus. Die Zustimmung mehrerer KI-Systeme macht eine Aussage nicht automatisch zu FACT. Ein Transkript ist eine Quelle, aber kein automatischer Wahrheitsbeweis.

### Filter 5 – CAUSAL: Kausalitätsmodell
Prüft, ob Ursache, Mechanismus, Bedingung, zeitliche Verzögerung, Störfaktoren und beobachtbarer Effekt benannt sind. Erzeugt gerichtete Kausalhypothesen, Gleichungsentwürfe oder einen offenen Kausalgraphen. Eine Korrelation oder ER-Beziehung allein ist kein Kausalnachweis. Fehlende Mechanismen bleiben offen.

### Filter 6 – FORMALIZE: Modell und Typen
Prüft, ob Variablen, Typen, Einheiten, Zustände, Aktionen, Übergänge und Randbedingungen formal beschrieben werden können. Erzeugt einen LUMINI-Modellentwurf und später versioniertes Model IR. Unklare Einheiten, Bereiche oder Definitionen erzeugen REVIEW_REQUIRED statt erfundener Werte.

### Filter 7 – QUALITY: Nutzen und Umsetzbarkeit
Bewertet getrennt:
- Relevanz für ein aktuelles Ziel,
- erwarteten Nutzen,
- Aufwand und Abhängigkeiten,
- wissenschaftliche bzw. technische Prüfbarkeit,
- Risiken und mögliche Nebenwirkungen,
- Wiederverwendbarkeit,
- didaktischen Wert,
- Datenschutz und Lizenzlage.

Es entsteht ein Qualitätsprofil mit Begründungen und offenen Fragen. Kein intransparenter Gesamtscore entscheidet allein. Begeisterung, Nutzen und wissenschaftliche Sicherheit sind unterschiedliche Eigenschaften.

### Filter 8 – ROUTE: nächste sinnvolle Verwendung
Mögliche Ziele:
- QUICK_ANSWER – direkt beantworten,
- MICRO_QUEST – in einen kleinen nächsten Schritt umwandeln,
- RESEARCH_TASK – Quellen und offene Fragen untersuchen,
- MODEL_DRAFT – ER- und Kausalmodell erstellen,
- CODE_MODULE – isoliertes Programmiermodul vorbereiten,
- SIMULATION – Experiment oder Simulation definieren,
- FICTION_WORLD – ausdrücklich fiktionales Spielsystem ausbauen,
- PRIVATE_ARCHIVE – sicher aufbewahren und später erneut prüfen,
- PARKED – derzeit nicht priorisiert, aber nicht gelöscht.

### Filter 9 – GENERATE: Artefakte erzeugen
Je nach Route entstehen menschenlesbare Erklärungen, mehrsprachige Textfassungen, ER-Diagramme, Kausalgraphen, LUMINI-Quelldateien, JSON Model IR, Java-/Python-/SQL-Codeentwürfe, Tests, Beispieldaten, Canvas-Projektionen oder Quests.

### Filter 10 – VERIFY: Tests und Freigabe
Prüft Syntax, Schema, Typen, Einheiten, Build, Tests, Reproduzierbarkeit, Schnittstellen und Sicherheitsbedingungen. Erzeugt PASS, FAIL, REVIEW_REQUIRED oder NOT_RUN. Generierter Code wird nicht automatisch als korrekt oder sicher ausgeführt: Build, Tests, Review und gegebenenfalls isolierte Ausführung sind erforderlich.

### Filter 11 – LEARN: Feedback und Revision
Vergleicht Erwartungen mit Ergebnissen und Nutzerfeedback. Erzeugt FeedbackRecord, neue Modellversion, verbesserte Tests oder eine Folgequest. Feedback verändert nicht rückwirkend Originaltext oder historische Ergebnisse.

## 4. Sichtbarer CompilerTrace in der Konsole

Ein beispielhafter Filterlauf:

~~~text
[LUMINI] idea=idea-2026-11-11-0042
[00 INTAKE]          CAPTURED
[01 NORMALIZE]       PASS | language=de | ambiguities=3
[02 INTENT]          REVIEW_REQUIRED | intent=simulation+learning
[03 ENTITY]          PASS | entities=7 | relations=9
[04 EPISTEMIC]       PASS | FACT=1 MODEL=2 HYPOTHESIS=3 FICTION=1
[05 CAUSAL]          REVIEW_REQUIRED | mechanism_missing=2
[06 FORMALIZE]       BLOCKED_PENDING_REVIEW | variables=5
[07 QUALITY]         PROFILE_READY | reuse=high | effort=medium
[08 ROUTE]           MICRO_QUEST + MODEL_DRAFT
[09 GENERATE]        NOT_RUN
[10 VERIFY]          NOT_RUN
[11 LEARN]           WAITING_FOR_FEEDBACK

NEXT: clarify two causal mechanisms, then generate Model IR.
~~~

Die Ausgabe muss aus real ausgeführten Filtern stammen. Ein nicht ausgeführter Filter darf niemals als PASS erscheinen.

## 5. Interaktive Filtersteuerung

Die Konsole soll nicht nur protokollieren. Nutzer und berechtigte Agenten können Vorschläge annehmen, verändern, vertagen oder zurückweisen.

Beispielbefehle als Entwurf:

~~~text
show idea-0042
explain filter 05
accept proposal causal-link-03
edit variable energy
translate idea-0042 --to en
generate idea-0042 --target lumini
validate model-0042
simulate model-0042 --seed 42
export idea-0042 --format json
park idea-0042
~~~

Jede Änderung bekommt Autor, Zeitstempel, Begründung und Versionsbezug. Öffentliche Veröffentlichung oder externe Aktionen erfordern eine gesonderte menschliche Freigabe.

## 6. Mehrsprachen und sprachunabhängige Modellierung

Das fachliche Modell darf nicht an eine natürliche Sprache gebunden sein.

~~~text
Originalsprache
      |
      v
unveränderter Originaltext
      |
      v
sprachneutrale Struktur
(Entities, Claims, Relations, Model IR)
      |
  +---+-----------+----------------+
  |               |                |
  v               v                v
Plain German  Plain English   weitere Sprachen
  |               |                |
  +---------------+----------------+
                  |
                  v
              Fachnotation
                  |
                 LUMINI
                  |
                  v
            versioniertes Model IR
                  |
             +----+----+
             |         |
             v         v
           Java     Python
~~~

Übersetzungsregeln:
- Originalsprache immer erhalten.
- Jede Übersetzung speichert Zielsprache, Version und Herkunft.
- Fachbegriffe bekommen stabile IDs und optionale Glossare.
- Variablen- und Feldnamen bleiben stabil, während Beschreibungen übersetzt werden.
- Zahlen, Einheiten, Formeln und logische Operatoren werden nicht durch freie Textübersetzung verändert.
- Bei mehrdeutigen Begriffen werden Alternativen ausgewiesen.
- Natürliche Sprache wird nicht direkt als unkontrollierter ausführbarer Code behandelt. Die Übersetzung führt über ein formalisiertes und validiertes Model IR.

So kann ein Mensch auf Deutsch lernen, ein anderer auf Englisch diskutieren und eine Engine dieselbe Modellbeschreibung in Java ausführen.

## 7. Zusammenarbeit unterschiedlicher Sprachmodelle

Jedes KI-System erhält denselben freigegebenen Aufgaben- und Datenvertrag. Seine Beiträge werden als Vorschläge mit Rollen und Herkunft gespeichert.

Mögliche Rollen:
- EXTRACTOR – extrahiert Begriffe und Behauptungen,
- MODELER – schlägt ER- und Kausalmodelle vor,
- MATHEMATICIAN – prüft Gleichungen und Einheiten,
- ENGINEER – entwirft Code und Schnittstellen,
- TESTER – sucht Fehler und Gegenbeispiele,
- TEACHER – erklärt Ergebnisse in verständlichen Lernstufen,
- REVIEWER – prüft Quellen, Risiken und Widersprüche.

Ein Agent darf Änderungen als Patch vorschlagen, aber keine stillen Änderungen am freigegebenen Modell vornehmen.

Übereinstimmung mehrerer Modelle wird als Übereinstimmung dokumentiert, nicht als Wahrheitsbeweis. Widersprüche bleiben sichtbar und können eigene Experimente auslösen. Private Daten werden nicht an andere Modelle oder öffentliche Projekte weitergegeben, sofern dies nicht ausdrücklich freigegeben wurde. Diese Zusammenarbeit erzeugt Daten, Tests und Softwareartefakte; sie trainiert nicht automatisch die zugrunde liegenden Sprachmodelle.

## 8. Knowledge Commons und Beitrags-/Marktsystem

Ein freiwilliges Beitragsmodell kann Angebot und Bedarf verbinden:
- Menschen und Teams veröffentlichen klar beschriebene Probleme oder Lernwünsche.
- Entwickler und KI-Systeme schlagen Module, Tests, Erklärungen oder Lösungen vor.
- Beiträge behalten Urheberschaft, Lizenz und Herkunft.
- Qualität wird anhand von Tests, Evidenz, Wartbarkeit und tatsächlicher Nutzung dokumentiert.
- Anerkennung und mögliche Vergütung werden transparent und freiwillig geregelt.
- Persönliche Daten, Gesundheit, Beziehungen und private Kommunikation sind keine Handelsware.
- Ein Ranglistenwert darf menschlichen Wert oder gesellschaftliche Berechtigung nicht abbilden.

Das Ziel ist ein offenes Wissens- und Kooperationssystem, kein manipulativer Aufmerksamkeitsmarkt.

## 9. Filterqualität und messbare Gütekriterien

Die Filter selbst müssen getestet werden.

| Filter | Gütekriterium | Testidee |
|---|---|---|
| Intake | Informations- und Quellenverlust | Originalhash und Vollständigkeitsprüfung |
| Normalize | Bedeutungserhalt | menschlicher Vergleich und Ambiguitätsreport |
| Intent | korrekte Aufgabenroute | gelabelte Beispielsammlung |
| Entity | Extraktionspräzision und -vollständigkeit | Goldstandard-Graph |
| Epistemic | korrekte Statuszuordnung | Review durch Fach- oder Quellenprüfer |
| Causal | explizite Mechanismen und Bedingungen | Gegenbeispiel- und Interventionsprüfung |
| Formalize | Typ- und Einheitenkorrektheit | Schema- und Dimensionaltests |
| Quality | begründete statt versteckte Priorisierung | Kriterien einzeln ausgeben |
| Route | passende nächste Aktion | Nutzerfeedback und Aufgabenabschluss |
| Generate | gültige Artefakte | Parser, Build, Schema, Tests |
| Verify | Fehlererkennung | bekannte fehlerhafte Beispiele einspeisen |
| Learn | nachvollziehbare Verbesserung | Versionsvergleich und Regressionstests |

Metriken wie Precision, Recall, Build-Success-Rate, Reproduzierbarkeit und Aufgabenabschluss messen unterschiedliche Eigenschaften. Sie dürfen nicht unkritisch zu einem einzigen Wahrheits- oder Intelligenzwert vermischt werden.

## 10. Kleiner Implementierungspfad

### MVP 1 – Java IdeaPacket und FilterTrace
Eine einzelne Java-Klasse oder ein kleines Paket nimmt Text entgegen, vergibt eine ID und erzeugt einen nachvollziehbaren FilterTrace. Zunächst laufen Filter als explizite Regeln und markierte Platzhalter; es wird keine KI-Funktion vorgetäuscht.

### MVP 2 – Konsoleninteraktion
Filterstatus, Erklärungen, Annahme, Änderung, Zurückstellen und Export werden über einfache Befehle möglich.

### MVP 3 – JSON Schema
IdeaPacket und FilterTrace werden als versionierte JSON-Schemas festgelegt.

### MVP 4 – KI-Adapter
Ein Adapter kann Vorschläge verschiedener LLMs einlesen. Agenten-Ausgaben werden validiert und als Vorschläge statt als autoritative Fakten behandelt.

### MVP 5 – LUMINI und Model IR
Freigegebene strukturierte Ideen werden in LUMINI-Modellbeschreibungen und Model IR transformiert.

### MVP 6 – Zielsprachen und Canvas
Dieselben Modelle erzeugen Java-/Python-Artefakte, mehrsprachige Erklärungen, ER-/Kausalgraphen und Canvas-Ansichten.

### MVP 7 – Lern- und Beitragsnetz
Quests, Feedback, Attribution, offene Aufgaben und freiwillige Beiträge werden ergänzt.

## 11. Definition of Done für den ersten Filter-Prototyp

Der erste Prototyp gilt als bestanden, wenn:
- ein Text unverändert als IdeaPacket gespeichert wird,
- jeder Filter einen echten Status und eine nachvollziehbare Meldung ausgibt,
- NOT_RUN klar von PASS unterschieden wird,
- unklare Inhalte auf REVIEW_REQUIRED oder PARKED gesetzt werden können,
- FACT, MODEL, HYPOTHESIS, OPEN und FICTION getrennt darstellbar sind,
- ein ER-Graph mit stabilen IDs exportiert werden kann,
- keine automatische Kausalität aus bloßer Korrelation erzeugt wird,
- ein JSON-Export schema-valid ist,
- ein Nutzer Route und nächste Aktion interaktiv auswählen kann,
- private Einträge privat bleiben und nur ausdrücklich freigegebene Inhalte exportiert werden.

**Ergebnis:** Aus spontanen Gedanken wird kein unkontrollierter Codegenerator, sondern eine lernbare, erklärbare und überprüfbare Pipeline. Sie bewahrt kreative Freiheit und macht sinnvolle Ideen schrittweise ausführbar.
