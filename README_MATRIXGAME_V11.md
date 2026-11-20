# Matrixgame Universal Grid Lab v0.11.0

## Ziel dieser Version

v0.11.0 baut auf dem getesteten v0.10.0 auf, ohne dessen Java-Datei zu verändern.
Der Schwerpunkt liegt auf einer nachvollziehbaren, überlebensorientierten
Entscheidung jeder Evolution-Unit pro Tick.

## Neue Verhaltensregeln

- **Nahrung zuerst:** Liegt Nahrung direkt neben einer Zelle, wird sie gesammelt.
  Bei niedrigem Energievorrat plant die Zelle einen Weg zur nächsten sichtbaren Nahrung.
- **Zitadellen-Gründung:** Pro Team startet ein gezielt bevorrateter Gründer
  mit höherer Energie. Fehlt eine eigene Zitadelle in der Nähe, errichtet er
  gezielt den ersten Team-Stützpunkt – auch mit Biomasse im Inventar.
- **Teamlogistik:** Eine ausreichend versorgte Zelle mit Biomasse im Inventar
  bewegt sich zur nächsten sichtbaren, eigenen und nicht vollen Zitadelle.
  Direkt angrenzende Ressourcen werden dort eingelagert.
- **Eigene Gebäude schützen:** Eine Unit kann kein Gebäude des eigenen Teams
  mehr als legales Angriffsziel auswählen.
- **Bauen nur mit Reserve:** In der Evolution kann eine Zelle nur bauen, wenn
  nach den Baukosten noch ein definierter Energievorrat bleibt, sie noch keine
  Zitadelle gebaut hat, nicht bereits Biomasse transportiert und keine eigene
  Zitadelle in der Nähe hat.
- **Teilung nur mit Überschuss:** Für Zellteilung sind mindestens
  1,6-mal die Reproduktionsschwelle erforderlich. Das Kind erhält 55 Prozent
  der Schwelle als Startenergie; der Elternteil behält dadurch eine größere
  Reserve.
- **Fairerer Start:** Die 300 anfänglichen Nahrungseinheiten werden auf 240
  verteilte Ressourcen plus sechs lokale Startfelder mit je zehn Ressourcen
  aufgeteilt.
- **Sensorik und Auswahl:** Die zufällige Start-Reichweite erhält eine
  Mindestgrenze. Die Softmax-Auswahl bleibt adaptiv und explorativ, aber ihre
  Zufallskomponente ist reduziert.

## Was diese Version ausdrücklich nicht garantiert

Unbegrenzte Entwicklung ist ein langfristiges Ziel der Architektur, keine
Eigenschaft, die ein einzelner Lauf beweisen kann. Auch die neue Strategie ist
keine Garantie für einen Sieg. Sichtweite, Hindernisse, Konkurrenz, knappe
Ressourcen und Zufall bleiben Teil der Simulation. Deshalb enthält v0.11.0
einen Regressionstest mit demselben Seed und derselben Weltgröße wie der
beobachtete Lauf bis Tick 306. Der Test muss belegen, dass die blaue Linie
überlebt, Nahrung aufgenommen und mindestens eine Geburt erreicht wird.

## Headless-Simulation und Browser-Canvas

Die Simulation kann ohne Swing-Fenster laufen und ein eigenständiges HTML5-Dashboard erzeugen.

Kommandozeile im Projektordner:

`java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v11_0 --simulate 306 20261116 160 100 matrixgame-report.html`

Parameter: Tick-Anzahl, Seed, Weltbreite, Welthöhe und HTML-Zieldatei.

Das erzeugte `matrixgame-report.html` enthält die Weltkarte als Browser-Canvas, Teamfilter,
Zoom, Objekt-Tooltips sowie die Populationsentwicklung. Es braucht keinen lokalen Server und
keine externen JavaScript-Bibliotheken.

Unter Windows kann `RUN_MATRIXGAME_V11_SIMULATION.bat` diese Simulation starten und
das Ergebnis anschließend im Standardbrowser öffnen. Optional lässt sich die Tick-Anzahl
als erstes Argument übergeben.

## GitHub-Run-Schaltfläche

Im Repository unter **Actions → Matrixgame Lab — Run simulation and Canvas report → Run workflow**
kann der manuelle Workflow gestartet werden. Er kompiliert den ausgewählten Quellstand,
führt die Selbsttests aus, berechnet eine Simulation und lädt HTML-Bericht plus Textprotokoll
als Workflow-Artefakt hoch. Die Workflow-Datei liegt im Standard-Branch, damit GitHub den
manuellen Run-Workflow anbietet.

Direktlink: https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/actions/workflows/matrixgame-lab-run.yml

## Start unter Windows

Voraussetzung: JDK 17 oder neuer (nicht nur eine JRE).

1. ZIP-Datei vollständig entpacken.
2. Im Projektordner `RUN_MATRIXGAME_V11.bat` doppelklicken.
3. Das Skript kompiliert die formatierte Java-Datei mit UTF-8 und startet
   anschließend das Spiel.

Für den Regressionstest `TEST_MATRIXGAME_V11.bat` doppelklicken.
Er kompiliert dieselbe Datei und startet die reproduzierbaren Selbsttests ohne
grafisches Fenster.

## Start in Eclipse

1. Den entpackten Projektordner über **File → Import → Existing Projects into Workspace** importieren.
2. Als JRE ein **JDK 17 oder neuer** auswählen.
3. Projekt- und Datei-Encoding auf **UTF-8** setzen.
4. Die Klasse
   `src/rhgr/MatrixgameUniversalGridLab_v11_0.java` öffnen.
5. **Run As → Java Application** auswählen.

## Zusammenarbeit mehrerer KI-Agenten

- `AGENTS.md` enthält die gemeinsamen Regeln für alle Coding-Agenten.
- `.github/copilot-instructions.md` ergänzt die Hinweise für GitHub Copilot.
- `docs/AI_COLLABORATION_PROTOCOL.md` beschreibt Aufgabenübergabe, getrennte Branches,
  unabhängiges Review und nachvollziehbare Testergebnisse.
- `docs/AI_TOOL_SETUP.md` erklärt die Verbindung von GitHub Actions, Mistral Vibe lokal/Web
  und GitHub Copilot sowie die Grenzen der jeweiligen Laufzeitumgebung.
- Mistral Vibe, Copilot und weitere Werkzeuge sollen dieselbe Repository-Dokumentation
  verwenden und Änderungen über eigene Branches und Pull Requests einreichen.

## Technischer Aufbau

- Paket: `rhgr`
- Einstiegsklasse: `MatrixgameUniversalGridLab_v11_0`
- Keine externen Bibliotheken
- Universeller Aktionsraum bleibt erhalten
- Bestehende v0.10.0-Datei bleibt unverändert
- CI kompiliert mit Java 17 und führt die Selbsttests inklusive 306-Tick-Regression aus

## Wichtige Abgrenzung der Tests

Die automatischen Tests prüfen konkrete Regeln und einen reproduzierbaren Lauf.
Sie ersetzen noch keine systematische Balance-Auswertung über viele Seeds,
keine Messung der langfristigen Populationsstabilität und keinen vollständigen
Mehrzielvergleich aller sechs Teams. Diese Auswertungen sind die nächste
sinnvolle Evolutionsstufe.
