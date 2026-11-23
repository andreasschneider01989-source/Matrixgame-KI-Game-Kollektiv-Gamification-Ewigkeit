# Q83 — Der Fabrik-Kern
## KI-Infrastruktur, technologische Souveränität, Machtanalyse und HTTP-Schnittstelle

**Version:** 0.1.0  
**Stichtag:** 13. November 2026  
**Status:** Research + architecture specification  
**Implementation status:** DOCUMENTATION ONLY — die hier beschriebene API und die Kennzahlen-Engine sind noch nicht implementiert.  
**Stabilitätsregel:** Dieses Modul ergänzt Universal Knowledge Engine, Knowledge Node Schema, Collective Improvement Network und LUMINI. Es ersetzt keinen bestehenden Simulator und verändert keine stabilen Kernklassen.

> Macht sichtbar machen. Abhängigkeiten prüfen. Optionen erweitern. Entscheidungen überprüfbar halten.

---

## 1. Ziel und Einordnung

Q83 untersucht, wie KI-Infrastruktur, Rechenkapazität, Daten, Energie, Lieferketten, Software, Recht und Governance die tatsächliche Handlungsfähigkeit einer Gesellschaft beeinflussen.

Die KI-Fabrik in München dient als konkreter Fall. Sie ist weder automatisch ein Beweis vollständiger digitaler Souveränität noch bloß ein Symbol fremder Abhängigkeit. Sie ist ein mehrschichtiges System: Ein Teil der Kontrolle liegt bei deutschen und europäischen Akteuren, andere Teile hängen von globalen Hardware-, Software- und Lieferketten ab.

Das Modul soll drei Fragen beantworten:

1. **Beschreiben:** Wer kontrolliert welche Ressourcen, Regeln, Schnittstellen und Entscheidungen?
2. **Analysieren:** Welche Abhängigkeiten, Engpässe, Risiken und positiven Fähigkeiten entstehen daraus?
3. **Gestalten:** Welche Alternativen erhöhen Resilienz, Wahlfreiheit, Transparenz und demokratische Kontrolle?

Das Ziel ist nicht, eine einzige Machtinstanz durch eine andere zu ersetzen. Kein einzelner Staat, Anbieter, Mensch oder KI-Agent erhält automatisch Deutungshoheit über das Gemeinwohl.

## 2. Evidenz-Audit der Q83-Kernaussagen

### 2.1 KI-Fabrik München

**FACT — mit Quellen- und Unternehmenskontext**

- Die Deutsche Telekom nahm ihre Industrial AI Cloud in München am 4. Februar 2026 offiziell in Betrieb.
- Die Telekom nennt rund 10.000 NVIDIA-Blackwell-GPUs und eine Rechenleistung von bis zu 0,5 EFLOPS.
- Die Telekom beziffert den zusätzlichen Zuwachs auf rund 50 Prozent der deutschen KI-Rechenleistung. Diese Zahl ist als Unternehmensangabe mit deren Bezugsgröße zu zitieren, nicht als unabhängig geprüfte Vollerhebung des deutschen Marktes.
- Am 9. November 2026 kündigte die Telekom weitere NVIDIA-Blackwell-Ultra-Systeme (B300) und einen Ausbau auf 20 MW an. Sie erklärte, die erste Ausbaustufe sei ausverkauft.
- Die Telekom nennt für die Anlage einen PUE-Wert von 1,2. Das ist eine Kennzahl für den Energie-Overhead des Rechenzentrums, nicht für die gesamte ökologische Wirkung des KI-Trainings.

**OPEN — nicht als bereits abgeschlossene Leistung darstellen**

- Die Nutzung der Abwärme für das gesamte Tucherpark-Quartier wurde als zukünftige Nutzung beschrieben. Bis ein nachweisbarer Betriebsstatus vorliegt, bleibt dies ein angekündigtes Infrastrukturvorhaben.
- Kühlwasserherkunft, Kreislaufführung, tatsächliche Entnahme, Rückgabe-Temperatur und ökologische Auswirkungen benötigen getrennte Messdaten. Aus der bloßen Angabe „Kühlung mit Eisbachwasser“ folgt nicht automatisch ein geschlossener Kreislauf oder eine nachweislich vernachlässigbare Erwärmung.
- „Ausverkauft“ ist ein wichtiges Kapazitätssignal, aber keine vollständige Auslastungszeitreihe und keine Aussage über die tatsächlich gelieferte Rechenleistung.

### 2.2 Soofi

**FACT — Projektbeschreibung, kein vollständiges unabhängiges Audit**

Die offizielle Soofi-FAQ beschreibt Soofi S als 30B-MoE-Modell mit ungefähr 3B aktiven Parametern und 27 Billionen Trainings-Tokens. Für den Trainingslauf nennt sie bis zu 512 NVIDIA-B200-GPUs und rund 253.000 GPU-Stunden.

Die FAQ kündigt Modellgewichte, ausgewählte Checkpoints, Trainings- und Evaluationscode sowie ein Dateninventar an. Die Veröffentlichung steht unter Release Gates und soll nach Tests erfolgen. Daher muss der Status bei jeder späteren Runde erneut geprüft werden.

**Wichtige Unterscheidung**

- Offene Gewichte ermöglichen Betrieb und Anpassung, soweit Lizenz und technische Voraussetzungen dies erlauben.
- Reproduzierbarkeit erfordert zusätzlich Trainingscode, Datenherkunft, Vorverarbeitung, Evaluationsmethoden, Hardware-/Softwareumgebung und hinreichende Checkpoints.
- Offenheit allein garantiert weder Sicherheit noch Qualität, Unabhängigkeit von Hardwarelieferanten oder langfristige Wartbarkeit.

### 2.3 CLOUD Act

**FACT — eng und juristisch präzise formulieren**

18 U.S.C. § 2713 verpflichtet erfasste Anbieter elektronischer Kommunikations- oder Remote-Computing-Dienste zur Erfüllung gesetzlicher Pflichten bezüglich Daten, die sich in ihrem Besitz, ihrer Verwahrung oder Kontrolle befinden, unabhängig davon, ob diese Daten innerhalb oder außerhalb der USA gespeichert sind.

Daraus folgt nicht, dass jeder US-Anbieter jederzeit auf alle Daten zugreifen kann. Zuständigkeit, tatsächliche Kontrolle, rechtliche Anordnung, Verfahrensschutz und mögliche Konflikte mit anderem Recht müssen im Einzelfall bewertet werden. Diese Dokumentation ist keine Rechtsberatung.

### 2.4 Rechenkalender und FLOP-Rechnung

Die Telekom-Angabe von 0,5 EFLOPS entspricht 5 × 10^17 FLOP/s. Als rein idealisierte Rechenübung gilt:

- Rechenzeit = FLOP-Budget / FLOP pro Sekunde.
- Für 10^30 FLOP ergibt sich bei ununterbrochener idealer Volllast ungefähr 2 × 10^12 Sekunden beziehungsweise 63.400 Jahre.
- Diese Rechnung ignoriert Auslastung, Kommunikation, Speicherbandbreite, Parallelisierungseffizienz, Präzisionsformat, Kühlung, Verfügbarkeit und die Frage, ob beide FLOP-Angaben überhaupt dieselbe Rechenoperation und Messkonvention verwenden.

**OPEN:** Die konkrete Zuordnung von 10^30 FLOP zu einem einzelnen AI-2027-Trainingslauf sowie die Angabe von 2,5 Millionen Chips sind mit den hier verlinkten Primärquellen nicht belegt. Diese Zahlen dürfen nicht als FACT in Prognosen oder Grafiken übernommen werden.

Die offizielle AI-2027-Compute-Seite enthält eigene, ausdrücklich unsichere Prognosen zu Rechenbudgets. Diese sind als Szenarioannahmen und nicht als gemessene Zukunftsdaten zu behandeln.

## 3. Was bedeutet Macht im Matrixgame?

Macht ist die Fähigkeit, Handlungsmöglichkeiten, Ressourcen, Regeln, Informationsflüsse oder Entscheidungen anderer Akteure zu beeinflussen. Sie kann produktiv und ermöglichend sein – etwa wenn Infrastruktur Menschen Zugang zu Bildung und Forschung verschafft. Sie kann aber auch konzentriert, intransparent, missbrauchbar oder schwer anfechtbar werden.

Q83 zerlegt Macht deshalb in unterscheidbare Dimensionen:

| Dimension | Prüffrage | Beispiel |
|---|---|---|
| Ressourcenmacht | Wer besitzt oder finanziert knappe Mittel? | GPUs, Energie, Kapital, Daten |
| Infrastrukturmacht | Wer betreibt die Systeme und kontrolliert den Zugang? | Rechenzentrum, Cloud, Netze |
| Lieferkettenmacht | Wer kann Ersatz, Wartung oder Upgrades liefern? | Chipfertigung, Ersatzteile |
| Softwaremacht | Wer kontrolliert Laufzeit, Treiber, APIs und Formate? | proprietäre Toolchains |
| Datenmacht | Wer darf Daten sehen, speichern, kombinieren und löschen? | Trainingsdaten, Kundendaten |
| Rechtsmacht | Welche Gerichte und Gesetze können Pflichten begründen? | Datenschutz, CLOUD Act |
| Wissensmacht | Wer kann Modelle, Messungen und Behauptungen prüfen? | offene Gewichte, Dokumentation |
| Entscheidungsmacht | Wer darf freigeben, sperren, priorisieren oder sanktionieren? | Betreiber, Regulierer, Maintainer |
| Gegenmacht | Wer kann widersprechen, wechseln, auditieren oder aussteigen? | Wettbewerb, Gerichte, Open Source |

### 3.1 Machtanalyse als Akteurs- und Kontrollgraph

Ein Machtgraph besteht aus Akteuren, Ressourcen, Entscheidungen und gerichteten Beziehungen.

Beispielhafte Beziehungstypen:

- OWNS — besitzt eine Ressource.
- OPERATES — betreibt eine Infrastruktur.
- DEPENDS_ON — ist auf einen Anbieter oder Prozess angewiesen.
- CONTROLS_ACCESS — kontrolliert den Zugang.
- SETS_RULES — setzt verbindliche Regeln.
- CAN_AUDIT — kann unabhängig prüfen.
- CAN_APPEAL — kann Entscheidungen anfechten.
- CAN_EXIT — kann mit vertretbaren Kosten wechseln.
- SUPPLIES — liefert eine kritische Komponente.
- FUNDS — finanziert eine Fähigkeit.

Eine Beziehung muss Quelle, Zeitraum, Geltungsbereich und Evidenzstatus besitzen. Ein Graph darf keine bloß vermutete Kontrolle als gesicherte Tatsache darstellen.

### 3.2 Gute Macht und problematische Macht

**Ermöglichende Macht** erweitert Fähigkeiten: Bildung, Zugang, Sicherheit, Kooperation, Wahlfreiheit und Innovation.

**Dominierende Macht** reduziert Alternativen, erzwingt Abhängigkeit, verschleiert Entscheidungen oder macht Widerspruch und Ausstieg praktisch unmöglich.

Die Bewertung fragt daher nicht nur „Wer ist mächtig?“, sondern:
- Wer profitiert?
- Wer trägt Kosten und Risiken?
- Wer kann Entscheidungen nachvollziehen?
- Wer kann widersprechen oder Beschwerde einlegen?
- Wer kann Anbieter, Modell oder Infrastruktur wechseln?
- Welche Gruppe wird durch die Entscheidung ausgeschlossen?
- Was geschieht, wenn ein einzelner Akteur ausfällt oder seine Regeln ändert?

## 4. Souveränität: erst ein Profil, dann eventuell ein Index

Die ursprüngliche Q83-Formel

S = J × H × E × K / (1 + ρ)

ist ein **illustratives Modell**, keine etablierte wissenschaftliche Kennzahl. Die Beispielwerte J=1, H=0,1, E=0,9 und K=0,5 ergeben nur bei ρ=0 den Wert 0,045. Die Zahlen sind nicht empirisch kalibriert. Das Ergebnis darf deshalb nicht als gemessene Souveränität der Münchner KI-Fabrik ausgegeben werden.

### 4.1 Vorgeschlagenes Komponentenprofil

Bewerte zunächst getrennt und mit Belegen:

1. Rechtliche und organisatorische Kontrolle
2. Hardwareverfügbarkeit und Lieferantenkonzentration
3. Software-Portabilität und offene Standards
4. Datenkontrolle, Verschlüsselung und Schlüsselverwaltung
5. Energie-, Wasser- und Kühlungsresilienz
6. Kapazität, Auslastung und vertragliche Verfügbarkeit
7. Auditierbarkeit, Transparenz und unabhängige Prüfung
8. Wechselmöglichkeit und realistische Exit-Kosten

Jede Dimension erhält:
- eine qualitative Einstufung (LOW / MEDIUM / HIGH / UNKNOWN),
- einen Messwert nur dann, wenn eine definierte Messmethode existiert,
- Quelle und Gültigkeitszeitraum,
- Unsicherheit und fehlende Daten,
- verantwortlichen Prüfer oder Review-Status.

UNKNOWN ist ein gültiges Ergebnis. Fehlende Daten dürfen nicht stillschweigend als null, mittel oder gut interpretiert werden.

### 4.2 Wann ein Gesamtindex zulässig wäre

Ein zusammengesetzter Index darf erst später entstehen, wenn:
- jede Komponente operational definiert ist,
- die Skalen und Gewichte begründet und öffentlich dokumentiert sind,
- die Gewichtung mit alternativen Annahmen auf Sensitivität geprüft wurde,
- fehlende Werte sichtbar bleiben,
- externe Reviewer die Methode prüfen,
- der Index niemals das Komponentenprofil ersetzt.

Bis dahin zeigt das Dashboard ein Profil und einen Engpassbericht statt einer scheinbar objektiven Souveränitätszahl.

## 5. Geometrie und Lernen: die richtige Verbindung

Die Riemannsche Geometrie ist ein nützliches mathematisches Werkzeug, aber keine physikalische Identität zwischen KI-Training und Raumzeit.

- In der Informationsgeometrie definiert die Fisher-Informationsmatrix unter geeigneten Regularitätsbedingungen eine lokale Metrik auf einem statistischen Modellraum.
- Der natürliche Gradient berücksichtigt diese Metrik. Für eine Zielfunktion g gilt in Koordinaten häufig: natürlicher Gradient = F^{-1} ∇g, sofern die Fisher-Matrix F invertierbar beziehungsweise geeignet regularisiert ist.
- Das ist ein geometrisch angepasstes lokales Optimierungsupdate, nicht automatisch eine exakte Geodäte.
- Hyperbolische Einbettungen können hierarchische Strukturen effizient repräsentieren; sie sind nicht für jeden Datentyp überlegen.
- Riemannsche Optimierung auf Matrixmannigfaltigkeiten ist relevant, wenn Nebenbedingungen wie Orthogonalität oder positive Definitheit erhalten werden müssen.

**Matrixgame-Brücke:** Der Wissensgraph kann verschiedene Geometrien als alternative Repräsentationen testen. Jede Wahl benötigt einen Vergleich anhand von Verzerrung, Generalisierung, Rechenkosten und Interpretierbarkeit. Eine schöne Visualisierung ist kein Validierungsnachweis.

## 6. HTTP-API: ein sicherer, kleiner Start

Die API soll die vorhandene Architektur verbinden, nicht ersetzen:

LUMINI / Wissensobjekte → Validierung → persistierter Datensatz → unveränderlicher RunSnapshot → Konsole / JSONL / Canvas / KI-Adapter.

### 6.1 Grundprinzipien

- REST über HTTPS für klar definierte Ressourcen und Aktionen.
- JSON als Standardformat; JSONL für Streaming, Batch-Import und reproduzierbare Exporte.
- Versionierung unter /api/v1.
- Jede schreibende Anfrage benötigt Authentifizierung und passende Berechtigung.
- Lesende Endpunkte sind standardmäßig read-only.
- Batch-Aufträge laufen asynchron und liefern eine Job-ID zurück.
- Schreibende Operationen unterstützen Idempotency-Key, damit Wiederholungen keine Duplikate erzeugen.
- Alle Änderungen erzeugen Audit-Ereignisse.
- Kein Endpunkt führt beliebigen Java-, Shell- oder Browser-Code aus.
- Externe KI-Agenten dürfen Vorschläge erzeugen; sie dürfen keine geschützten Aktionen selbst freigeben.

### 6.2 Erste Ressourcen

| Methode | Pfad | Zweck |
|---|---|---|
| GET | /api/v1/health | Prozess- und Dienststatus |
| GET | /api/v1/version | API-, Schema- und Build-Version |
| POST | /api/v1/knowledge/nodes | Wissensknoten als Entwurf anlegen |
| GET | /api/v1/knowledge/nodes/{id} | Wissensknoten lesen |
| POST | /api/v1/knowledge/batches | Batch-Import als asynchronen Auftrag starten |
| GET | /api/v1/jobs/{jobId} | Status und Fortschritt eines Auftrags |
| GET | /api/v1/jobs/{jobId}/results | geprüfte Importergebnisse abrufen |
| POST | /api/v1/reviews/{id}/decision | Menschliche Review-Entscheidung protokollieren |
| GET | /api/v1/runs/{runId} | unveränderlichen Simulationslauf lesen |
| GET | /api/v1/runs/{runId}/events | Ereignisse als JSONL exportieren |

### 6.3 Batch-Verarbeitung

Ein Batch enthält eine endliche Liste von Datensätzen. Jeder Datensatz behält eine stabile Quell-ID, einen Quelltyp, einen Zeitstempel, eine Provenienzangabe und eine Datenschutzklasse.

Ablauf:

1. Client sendet einen Batch mit Idempotency-Key.
2. Server prüft Authentifizierung, Dateigröße, Schema und Berechtigungen.
3. Server legt einen Job mit Status ACCEPTED an.
4. Worker validiert jeden Datensatz einzeln.
5. Ergebnisse erhalten PASS, REVIEW_REQUIRED oder REJECTED.
6. Der Job wird COMPLETED, COMPLETED_WITH_ERRORS oder FAILED.
7. Nur freigegebene Datensätze gelangen in den öffentlichen Wissensbestand.

Ein fehlerhafter Eintrag darf nicht unbemerkt den gesamten Batch verfälschen. Der Jobbericht zählt akzeptierte, zurückgestellte und abgelehnte Datensätze getrennt.

### 6.4 Browser-Batch: ausdrücklich begrenzt

Der Browser kann als optionaler, vom Nutzer gestarteter Importadapter dienen. Er darf nur die vom Nutzer ausgewählten und tatsächlich zugänglichen Tabs oder Dokumente verarbeiten.

Nicht erlaubt als Standard:
- stilles Auslesen sämtlicher Tabs,
- Umgehen von Login- oder Berechtigungsgrenzen,
- Übernahme von Cookies, Passwörtern oder Sitzungstokens,
- automatisches Veröffentlichen privater Chats,
- automatisches Kommentieren, Liken, Teilen oder Ändern fremder Konten.

Der Import zeigt vor dem Absenden eine Vorschau mit Quelle, Titel, Datum, Datenklasse und geschätztem Umfang. Private oder sensible Inhalte bleiben standardmäßig lokal beziehungsweise PRIVATE. Veröffentlichung benötigt eine separate menschliche Freigabe.

### 6.5 Beispiel eines Batch-Auftrags

~~~json
{
  "schemaVersion": "1.0",
  "batchId": "q83-research-2026-11-13",
  "items": [
    {
      "sourceId": "telekom-ai-factory-2026-11-09",
      "sourceType": "web_reference",
      "claimStatus": "FACT",
      "privacyClass": "PUBLIC",
      "title": "Telekom AI factory expansion announcement",
      "url": "https://www.telekom.com/de/newsroom/aktuelles/medieninformationen/2026/11/ki-nachfrage-steigt-telekom-erweitert-ki-fabrik-in-muenchen",
      "observedAt": "2026-11-13"
    }
  ]
}
~~~

Das Beispiel ist ein API-Entwurf. Es zeigt keine bereits laufende Serverfunktion.

### 6.6 Fehlerformat

Jede API-Fehlermeldung sollte mindestens enthalten:
- code: maschinenlesbarer Fehlercode,
- message: kurze Erklärung,
- requestId: Korrelations-ID,
- retryable: ob ein erneuter Versuch sinnvoll ist,
- details: optionale Feld- oder Validierungsfehler.

Keine Zugangsdaten, internen Pfade, privaten Inhalte oder Stacktraces an Clients ausgeben.

## 7. Sicherheits- und Governance-Modell

### Rollen

- READER — darf freigegebene Wissensobjekte lesen.
- CONTRIBUTOR — darf private oder öffentliche Entwürfe gemäß Berechtigung einreichen.
- REVIEWER — darf Quellen und Aussagen prüfen und Entscheidungen empfehlen.
- MAINTAINER — darf geprüfte Änderungen in den Projektbestand übernehmen.
- ADMIN — verwaltet Rollen und technische Konfiguration; darf fachliche Evidenz nicht allein durch seine Rolle in FACT umwandeln.

### Schutzregeln

- Least privilege: jeder Akteur erhält nur die minimal nötigen Rechte.
- Zero Trust: jede Anfrage wird authentifiziert und autorisiert.
- Verschlüsselung während der Übertragung und für gespeicherte sensible Daten.
- Keine Secrets im Repository, in Logs oder in Batch-Dateien.
- Audit-Log für Änderungen, Freigaben und Exporte.
- Rate Limits und Größenlimits für Batch-Endpunkte.
- Schutz vor Replay, CSRF und unzulässigen Cross-Origin-Aufrufen.
- Getrennte Speicherbereiche für private Lernnotizen und öffentliche Wissensknoten.
- Datenlöschung und Aufbewahrungsfristen müssen nachvollziehbar geregelt sein.
- Hochriskante Aktionen benötigen menschliche Bestätigung und eine überprüfbare Begründung.

## 8. Automatisierung und Rollen im kollektiven KI-Game

Ein sinnvoller Workflow verteilt Arbeit, aber nicht unkontrolliert Autorität:

1. SCOUT findet Quellen und neue Fragen.
2. EXTRACTOR erstellt strukturierte, zitierfähige Aussagen.
3. SKEPTIC sucht Gegenbelege und alternative Erklärungen.
4. DOMAIN_REVIEWER prüft fachliche Annahmen.
5. IMPLEMENTER erstellt isolierte Codeänderungen.
6. TESTER führt reproduzierbare Tests aus.
7. HUMAN_MAINTAINER entscheidet über Veröffentlichung und Merge.
8. TEACHER erzeugt eine kurze Übung und dokumentiert, was gelernt wurde.

Mehrere Agenten dürfen unabhängig prüfen. Ihre Übereinstimmung erhöht nicht automatisch die Evidenzstufe. Gemeinsame Quellen oder identische Fehlannahmen können zu korrelierten Fehlern führen.

## 9. Abnahmekriterien für die erste Implementierung

### API-Vertrag

- [ ] Unbekannte API-Version wird klar abgewiesen.
- [ ] Nicht authentifizierte Schreibzugriffe werden abgewiesen.
- [ ] Fehlende Berechtigungen führen zu 403, nicht zu stiller Teilausführung.
- [ ] Wiederholte Batch-Anfragen mit gleichem Idempotency-Key erzeugen keinen zweiten Auftrag.
- [ ] Jeder Job ist über seine Job-ID abrufbar.
- [ ] Jeder Datensatz erhält ein eigenes Validierungsergebnis.
- [ ] Private Inhalte werden nicht automatisch veröffentlicht.
- [ ] Fehlerantworten enthalten requestId und einen stabilen Fehlercode.

### Wissens- und Evidenzvertrag

- [ ] FACT benötigt eine konkrete Quelle oder reproduzierbare Beobachtung.
- [ ] MODEL benötigt Annahmen, Geltungsbereich und Grenzen.
- [ ] HYPOTHESIS benötigt eine prüfbare Vorhersage oder Widerlegungsbedingung.
- [ ] OPEN bleibt sichtbar, bis belastbare Evidenz vorliegt.
- [ ] FICTION wird nie als empirische Aussage exportiert.
- [ ] EVIDENCE_STATUS und IMPLEMENTATION_STATUS bleiben getrennt.
- [ ] Jede Änderung ist versioniert und nachvollziehbar.

### Q83-Souveränitätsprofil

- [ ] UNKNOWN wird nicht stillschweigend zu null.
- [ ] Jede Einstufung hat Quelle, Zeitraum und Begründung.
- [ ] Keine Gesamtzahl wird ohne veröffentlichte Kalibrierung angezeigt.
- [ ] Sensitivitätsanalyse zeigt, wie Bewertungen von Gewichtungen abhängen.
- [ ] Der Bericht nennt mindestens einen Gegenbeleg oder eine Datenlücke.

## 10. Lernquests

### Quest A — Macht lesen (5 Minuten)
Nenne drei Akteure, die an einer KI-Fabrik beteiligt sind. Ordne für jeden eine Ressource, eine Abhängigkeit und eine Kontrollmöglichkeit zu.

**Erfolg:** Du kannst mindestens eine Abhängigkeit und eine Gegenmacht erklären, ohne daraus eine unbelegte Absicht abzuleiten.

### Quest B — Rechenleistung verstehen (5 Minuten)
Berechne die ideale Laufzeit für 10^24, 10^27 und 10^30 FLOP bei 0,5 EFLOPS. Wiederhole die Rechnung mit 30 Prozent angenommener Auslastung.

**Erfolg:** Du unterscheidest FLOP, FLOP/s, theoretische Spitzenleistung und tatsächlich erreichte Nutzleistung.

### Quest C — API denken (5 Minuten)
Erkläre den Unterschied zwischen POST /knowledge/batches und GET /jobs/{jobId}. Warum sollte ein großer Batch nicht zwingend synchron in einer HTTP-Anfrage fertig werden?

**Erfolg:** Du kannst Request, Response, Jobstatus, Idempotenz und Fehlerbehandlung erklären.

### Quest D — Macht fair gestalten (5 Minuten)
Nenne zwei Regeln, die eine KI daran hindern, selbst zur nicht kontrollierbaren Machtinstanz zu werden.

**Erfolg:** Du benennst mindestens zwei überprüfbare Schutzmechanismen, etwa Audit, Widerspruch, Exit, Least Privilege oder menschliche Freigabe.

## 11. Priorisierte Umsetzung

**STABLE MODE**
- Bestehende LUMINI-Pipeline, Wissensschema und Simulatoren unverändert lassen.
- Q83 zunächst als dokumentiertes, versioniertes Forschungsmodul hinzufügen.
- Bestehende Testläufe und PRs nicht überschreiben.

**EXPLORATORY MODE**
1. JSON-Schema für Q83-Datensätze und Batch-Requests definieren.
2. Reine Java-Validierung ohne Netzwerkzugriff implementieren.
3. Deterministische Tests für Evidenzstatus, Datenschutzklasse und Batch-Fehler ergänzen.
4. Erst danach einen lokalen HTTP-Serveradapter hinzufügen.
5. Browserimport erst nach expliziter Auswahl und Vorschau anbinden.
6. Später OpenAPI 3.1, Authentifizierung, Job-Worker und Event-Streaming ergänzen.

Die erste Codeversion sollte ohne Datenbank, Cloudkonto, API-Schlüssel oder externe Dienste testbar sein. So bleibt der Kern lokal, verständlich und reproduzierbar.

## 12. Quellen

- Deutsche Telekom, 4. Februar 2026: [Deutschlands erste KI-Fabrik für die Industrie geht in München offiziell in Betrieb](https://www.telekom.com/de/newsroom/aktuelles/medieninformationen/2026/02/deutschlands-erste-ki-fabrik-fuer-die-industrie)
- Deutsche Telekom, 9. November 2026: [KI-Nachfrage steigt: Telekom erweitert KI-Fabrik in München](https://www.telekom.com/de/newsroom/aktuelles/medieninformationen/2026/11/ki-nachfrage-steigt-telekom-erweitert-ki-fabrik-in-muenchen)
- Soofi: [FAQ — Soofi S, Training, Veröffentlichung und Souveränität](https://www.soofi.info/faq/)
- Soofi: [Soofi S — Modellbeschreibung](https://www.soofi.info/soofi-s/)
- United States Code: [18 U.S.C. § 2713](https://uscode.house.gov/view.xhtml?req=granuleid:USC-prelim-title18-section2713&num=0&edition=prelim)
- Eurojust: [The CLOUD Act](https://www.eurojust.europa.eu/publication/cloud-act)
- AI 2027: [Compute Forecast](https://ai-2027.com/research/compute-forecast)
- JMLR: [Information-Geometric Optimization](https://jmlr.csail.mit.edu/papers/volume18/14-467/14-467.pdf)

## 13. Verknüpfung mit dem Matrixgame-Projekt

Dieses Dokument ergänzt:

- docs/UNIVERSAL_KNOWLEDGE_ENGINE.md
- docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md
- docs/COLLECTIVE_IMPROVEMENT_NETWORK.md
- docs/INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md
- docs/LUMINI_IDEA_PIPELINE_MVP_2026-11-12.md (auf dem zugehörigen Feature-Branch)

Es ist eine Spezifikation und ein Lernbaustein. Es behauptet weder eine implementierte HTTP-API noch eine fertige Machtanalyse-Engine oder einen wissenschaftlich validierten Souveränitätsindex.

**Leitsatz:** Macht wird verantwortbar, wenn sie sichtbar, begrenzt, anfechtbar und ersetzbar bleibt.
