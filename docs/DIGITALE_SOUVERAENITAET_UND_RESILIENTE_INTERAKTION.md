# Digitale Souveränität und resiliente Interaktion
## Entwicklungsmodul für Matrixgame und kollektive KI-Games

**Status:** Entwurf 1.0 – Konzept und umsetzbare Roadmap  
**Ziel:** Digitale Handlungsmöglichkeiten erweitern, ohne Sicherheit, Privatsphäre, Selbstbestimmung oder ökologische Verantwortung zu opfern.

## 1. Leitidee

Menschen und berechtigte digitale Systeme sollen Wissen möglichst barrierearm finden, verstehen, austauschen und weiterentwickeln können. Der Zugang soll nicht unnötig von einem einzelnen Gerät, Konto, Anbieter oder Kommunikationskanal abhängen.

Das bedeutet nicht, Sicherheitsprüfungen abzuschaffen. Es bedeutet, Sicherheit so zu gestalten, dass legitime Nutzerinnen und Nutzer verlässlich arbeiten können und bei Geräteverlust, Ausfall oder Kontoproblemen sichere Alternativen haben.

**Leitsatz:** Keine einzelne technische Plattform sollte ein unnötiger Single Point of Failure für Lernen, Kommunikation und Wissensaustausch sein.

## 2. Problemdefinition aus einem realen Nutzungsszenario

Ein Arbeitsablauf kann durch mehrere voneinander unabhängige Störungen unterbrochen werden:

- Eine Plattform verlangt eine erneute Anmeldung oder einen zweiten Faktor.
- Der erwartete Authentifizierungskanal ist nicht verfügbar oder der Code wird nicht gefunden.
- Ein Rechner stürzt ab oder wird vorübergehend unbenutzbar.
- Nicht gespeicherte Arbeit oder lokale Entwicklungsumgebungen sind dadurch schwer erreichbar.
- Die Person muss Zeit und Aufmerksamkeit in Wiederherstellung statt in Erkenntnisgewinn investieren.

Das ist ein konkreter Testfall für digitale Resilienz. Er beweist nicht, dass jede Sicherheitsabfrage unnötig ist. Er zeigt, dass Wiederherstellbarkeit, verständliche Fehlermeldungen und alternative sichere Zugangswege Teil guter Sicherheit sein müssen.

## 3. Gestaltungsprinzipien

1. **Offene Standards:** Bevorzuge öffentlich dokumentierte, interoperable Standards gegenüber unnötigen proprietären Abhängigkeiten.
2. **Mehrere Zugangswege:** Unterstütze Smartphone, Desktop, Browser und – wo sinnvoll – lokale/offline Arbeitsmöglichkeiten.
3. **Sichere Wiederherstellung:** Biete mehrere voneinander unabhängige, sicher verwaltete Authentifizierungs- und Recovery-Optionen.
4. **Datenportabilität:** Inhalte und Projektstände müssen exportierbar und auf anderen Systemen nutzbar sein.
5. **Automatische Speicherung:** Häufige Zwischenspeicherung, Versionshistorie und Wiederaufnahme nach Abstürzen.
6. **Interoperabilität:** APIs und offene Datenformate ermöglichen Zusammenarbeit zwischen unterschiedlichen Anwendungen und KI-Systemen.
7. **Datenschutz und Einwilligung:** Öffentliche Auffindbarkeit darf nicht mit uneingeschränkter Datennutzung verwechselt werden. Private Daten, Identitäten und Zugangsdaten bleiben geschützt.
8. **Barrierefreiheit:** Bedienung muss auch mit mobilen Geräten, Vorlesefunktionen, assistiven Technologien und unterschiedlichen Fähigkeiten funktionieren.
9. **Ökologische Verantwortung:** Infrastrukturkosten wie Energie, Hardware, Kühlung und Datenübertragung berücksichtigen.
10. **Pluralität statt Monokultur:** Föderation und dezentrale Optionen schaffen Wahlmöglichkeiten; zentrale Dienste können weiterhin nützlich sein, dürfen aber nicht alternativlos sein.

## 4. Technische Bausteine – Kandidaten, keine Einheitslösung

| Bereich | Mögliche Bausteine | Beitrag |
|---|---|---|
| Internettransport | IP, DNS, HTTPS, TLS | Geräte und Netze interoperabel verbinden und Kommunikation schützen |
| Identität | Passkeys/WebAuthn, mehrere Authenticatoren, Recovery-Codes | Phishing-resistentere Anmeldung und sichere Wiederherstellung |
| Wissensveröffentlichung | Git, Markdown, offene Lizenzen, statische Webseiten | Nachvollziehbare, versionierte und maschinenlesbare Inhalte |
| Föderierte Kommunikation | Matrix-Protokoll, ActivityPub | Kommunikation über unterschiedliche Server und Dienste |
| KI-Integration | dokumentierte APIs, austauschbare Adapter, offene Datenformate | Modelle und Werkzeuge verbinden, ohne ein Modell zum Pflichtanbieter zu machen |
| Resilienz | lokale Kopien, Backups, Export, Offline-Modus | Weiterarbeit bei Geräte-, Konto- oder Netzausfällen |

Die Auswahl muss je nach Bedrohungsmodell, Nutzergruppe, Kosten und Wartbarkeit geprüft werden. Dezentralisierung beseitigt nicht automatisch Risiken: Sie kann zusätzliche Anforderungen an Updates, Moderation, Spam-Schutz, Identitätsprüfung und Datensicherung erzeugen.

## 5. Architekturvorschlag: Resilient Interaction Layer

**Schicht 1 – Zugang:** Browser und mobile/desktop Clients; barrierearme Bedienung; Passkeys und sichere Alternativen.

**Schicht 2 – Identität und Rechte:** Nutzerkontrollierte Identität, minimale Berechtigungen, widerrufbare Zugriffe und nachvollziehbare Freigaben.

**Schicht 3 – Austausch:** Offene APIs und Formate; Adapter für unterschiedliche KI-Modelle, Wissensspeicher und Kommunikationsnetze.

**Schicht 4 – Wissen:** Git-Repositories, Dokumentation, Metadaten, Quellenangaben, Versionierung und klare Lizenzierung.

**Schicht 5 – Resilienz:** Lokale Zwischenspeicherung, Backups, Wiederaufnahme, alternative Geräte und dokumentierte Wiederherstellungsverfahren.

**Schicht 6 – Governance:** Transparente Regeln, menschliche Aufsicht bei folgenreichen Aktionen, Schutz vor Missbrauch und faire Beteiligungsmöglichkeiten.

## 6. Integration in Matrixgame

Neue Spielkomponente: **Digital Sovereignty & Resilience Questline**.

Spielziele:
- Zugangshürden erkennen und mit Belegen dokumentieren.
- Einen Arbeitsablauf auf mindestens zwei Geräten testen.
- Einen sicheren Wiederherstellungsweg einrichten.
- Einen Datenexport und eine Wiederherstellung testen.
- Ein offenes Protokoll oder Datenformat integrieren.
- Eine KI-übergreifende Aufgabe mit klaren Rollen und überprüfbaren Ergebnissen lösen.
- Zugänglichkeit, Datenschutz und Ressourcenverbrauch mitbewerten.

### Spielvariablen

Alle Werte sind zunächst Modellwerte von 0 bis 100:

- **A – Accessibility:** Zugänglichkeit
- **S – Security:** Sicherheit
- **I – Interoperability:** Interoperabilität
- **R – Resilience:** Ausfallsicherheit
- **K – Knowledge Sharing:** Wissensaustausch

Vorgeschlagener Gesamtindikator:

\[
DSI = 100 \cdot \left(\frac{A}{100}\cdot\frac{S}{100}\cdot\frac{I}{100}\cdot\frac{R}{100}\cdot\frac{K}{100}\right)^{1/5}
\]

Der Digital Sovereignty Index (DSI) ist eine Spiel- und Diskussionsmetrik, kein validierter wissenschaftlicher Index. Einzelwerte müssen zusätzlich angezeigt werden, damit ein hoher Gesamtwert keine gravierende Schwäche verdeckt. Ein niedriger Wert in Sicherheit darf nicht durch hohe Zugänglichkeit „ausgeglichen“ werden.

### Belohnungslogik

Belohnt werden nachweisbare Verbesserungen, nicht bloße Aktivität:
- reproduzierbare Tests,
- dokumentierte Fehlerbehebungen,
- verständliche Anleitungen,
- barrierearme Lösungen,
- sichere Wiederherstellung,
- kompatible Schnittstellen,
- messbare Reduktion unnötiger Arbeitsschritte.

Keine Belohnung für das Umgehen von Sicherheitskontrollen, das Sammeln persönlicher Daten oder die Maximierung von Bildschirmzeit.

## 7. Erste Roadmap

### Phase 1 – Sofort: Arbeitsfähigkeit sichern
- Wichtige Dokumente zusätzlich lokal und in einem versionierten Repository sichern.
- GitHub-Recovery-Optionen prüfen und mehrere sichere Authentifizierungsmethoden einrichten.
- Arbeit in kleine, speicherbare Dateien aufteilen.
- Smartphone als funktionsfähigen Ersatzarbeitsplatz dokumentieren.

### Phase 2 – Prototyp
- Eine kleine Markdown-Wissensbasis im Matrixgame-Repository anlegen.
- Ein minimales Austauschformat für Spielereignisse definieren (z. B. JSON mit Schema-Version).
- Ein einfaches Import-/Export-Beispiel implementieren.
- Testen, ob dieselben Daten auf Smartphone und Desktop lesbar und bearbeitbar sind.

### Phase 3 – Interoperabilität
- Adapter-Schnittstelle für unterschiedliche KI-Modelle definieren.
- Quellen, Unsicherheiten und Zuständigkeiten maschinenlesbar kennzeichnen.
- Einen föderierten Kommunikationsweg als optionalen Prototyp untersuchen.
- Datenschutz, Missbrauchsschutz und Moderation testen.

### Phase 4 – Evaluation und Gemeinschaft
- Testfälle und Ergebnisse veröffentlichen.
- Beiträge anderer prüfen und integrieren.
- Offene Fragen und bekannte Grenzen transparent dokumentieren.
- Nur Funktionen als stabil bezeichnen, die reproduzierbar getestet wurden.

## 8. Messbare Erfolgsindikatoren

- **Wiederanlaufzeit:** Wie schnell kann nach Geräte- oder Kontenausfall weitergearbeitet werden?
- **Portabilität:** Lassen sich Daten ohne proprietäre Spezialsoftware exportieren und wieder importieren?
- **Geräteunabhängigkeit:** Funktioniert der Kernworkflow auf mindestens zwei Gerätetypen?
- **Recovery-Test:** Sind alternative Authentifizierungswege eingerichtet und praktisch getestet?
- **Interoperabilität:** Können mindestens zwei unabhängige Clients dasselbe Austauschformat verarbeiten?
- **Barrierefreiheit:** Funktioniert der zentrale Ablauf mit Tastatur, Screenreader/Vorlesefunktion und mobilem Display?
- **Sicherheit:** Sind Berechtigungen minimal, Geheimnisse geschützt und Wiederherstellungsmethoden dokumentiert?
- **Ökologie:** Werden unnötige Datenübertragung und Rechenlast vermieden?

## 9. Fakten, Modellannahmen und offene Fragen

**FAKT:** Offene Internetstandards ermöglichen Interoperabilität zwischen unabhängig entwickelten Netzen und Anwendungen. Die Internet Society beschreibt offene Standards als wichtige Grundlage für globale Kompatibilität und Innovation.

**FAKT:** ActivityPub definiert Client-zu-Server- und Server-zu-Server-Schnittstellen für föderierte soziale Kommunikation.

**FAKT:** Das Matrix-Protokoll spezifiziert offene APIs für föderierte Kommunikation ohne einzelnen zentralen Kontrollpunkt.

**MODELLANNAHME:** Mehrere sichere Zugangswege, portable Daten und getestete Wiederherstellung reduzieren die Abhängigkeit von einzelnen Geräten und Diensten.

**OFFENE FRAGE:** Welche Kombination aus zentralen und föderierten Diensten bietet für die jeweilige Nutzergruppe die beste Balance aus Sicherheit, Bedienbarkeit, Kosten und Wartbarkeit?

**GRENZE:** Ein offenes Protokoll garantiert weder universellen Internetzugang noch faire Governance, Sicherheit oder dauerhafte Verfügbarkeit. Diese Eigenschaften benötigen Finanzierung, Wartung, Zugangsinfrastruktur und gesellschaftliche Regeln.

## 10. Quellen und weiterführende Standards

- Internet Society, *Open Internet Standards* (29. Oktober 2025): https://www.internetsociety.org/resources/policybriefs/2025/open-internet-standards/
- W3C, *ActivityPub*: https://www.w3.org/TR/activitypub/
- W3C, *ActivityPub Editor's Draft*: https://w3c.github.io/activitypub/
- Matrix, *Protocol Specification*: https://spec.matrix.org/
- Matrix.org, *About Matrix*: https://matrix.org/about/
- OWASP, *Passkey Security Cheat Sheet*: https://cheatsheetseries.owasp.org/cheatsheets/Passkey_Security_Cheat_Sheet.html

---

**Leitmotiv:** Nicht verbieten, sondern überbieten. Nicht abhängig machen, sondern befähigen. Wissen teilen, Systeme verbinden und gemeinsam bessere digitale Möglichkeiten schaffen.

**Let's play. Let's be. Let's beam.**
