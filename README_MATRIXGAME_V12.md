# Matrixgame Universal Grid Lab v0.12.0

## Ziel

v0.12.0 erweitert den getesteten v0.11-Kandidaten um erkennbare Teamidentität,
dynamische Beziehungen, ökologische Rollen und eine prozedurale Infinity-Frontier.
Die vorherige Version wird nicht überschrieben.

## Teamidentität im INPUT

Jede wahrgenommene Team-Unit bzw. jedes Team-Gebäude liefert:
- numerischen Teamcode und Teamnamen-Code;
- RGB-Farbkanäle passend zur stabilen Teamfarbe;
- den Beziehungswert aus Sicht der beobachtenden Unit;
- eine Beziehungsklasse: ALLY, NEUTRAL oder RIVAL.

Die sechs Profile heißen AZUR, KORALLE, JADE, VIOLETT, AMBER und CYAN.
Farbidentität bleibt innerhalb des Teams erhalten. Beziehungen sind keine Gene.

## Dynamische Diplomatie

- Die sechs Teams beginnen mit einer deterministischen, symmetrischen Beziehungsmatrix.
- Angriffe und Kills verschlechtern die Beziehung zwischen den beteiligten Teams.
- Positive Beziehungen ab dem Allianzwert sperren Angriffe auf verbündete Kreaturen.
- Die KI nutzt den Beziehungswert bei der Auswahl von Gegnern und Angriffsentscheidungen.
- Beziehungen werden in der Konsolenausgabe als Team-Matrix zusammengefasst.

Das ist eine einfache, messbare Diplomatie-Grundlage, noch keine vollständige Verhandlungs- oder BündniskI.

## Ökologische Rollen

Rollen werden nicht nur als feste Klasse vergeben. Jede Unit besitzt eine vererbbare Ernährungspräferenz
und gewichtete Spuren von Pflanzen- und Beuteaufnahme. Diese gleiten mit einem exponentiellen
Vergessen und passen die Ernährungspräferenz bei jedem Simulationsschritt an.

Rollen:
- GATHERER: Schwerpunkt Sammeln und Nahrung;
- HERBIVORE: stärker pflanzen-/nahrungsorientiert;
- OMNIVORE: gemischte Strategie;
- CARNIVORE: stärkere Beuteorientierung und Jagd;
- SCOUT: stärkere Exploration.

Die Rollen beeinflussen Bewegung, Sammel- und Angriffs-Scores. Teamfarbe bleibt dabei
der Primärfarbcode; Form und Buchstabe kennzeichnen die ökologische Rolle.

## Infinity Canvas

Die Kamera kann weiterhin frei in positive und negative Weltkoordinaten geschwenkt werden.
Außerhalb der endlichen aktiven Simulationsregion werden 64×64-Frontier-Chunks bei Bedarf
deterministisch aus Seed und Chunk-Koordinaten dargestellt. Die aktive Region ist gestrichelt
markiert. Der Browser-Canvas-Bericht unterstützt ebenfalls Ziehen, Zoom und Teamfilter.

**Wichtige technische Grenze:** Die Frontier ist in v0.12.0 eine prozedural dargestellte,
unendlich adressierbare visuelle Fläche. Lebende Units, Ressourcen, Gebäude und Tick-Updates
werden weiterhin in der begrenzten aktiven Simulationregion berechnet. Unendliche visuelle
Koordinaten bedeuten nicht unendliche simultane Agenten oder unbegrenzte CPU-/Speichernutzung.
Die nächste Stufe ist kontrolliertes Chunk-Streaming mit begrenztem Aktivierungsradius,
persistenter Chunk-Seed-State und messbarer Simulationslast.

## Start unter Windows

Voraussetzung: JDK 17 oder neuer.

- `RUN_MATRIXGAME_V12.bat`: startet das Swing-Canvas.
- `RUN_MATRIXGAME_V12_SIMULATION.bat`: führt eine headless Simulation aus und öffnet den HTML-Bericht.
- `TEST_MATRIXGAME_V12.bat`: kompiliert und führt die deterministischen Selbsttests aus.

## Kommandozeile

Kompilieren:
```bash
mkdir -p out
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v12_0.java
```

Tests:
```bash
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v12_0 --self-test
```

Simulation:
```bash
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v12_0 --simulate 306 20261116 160 100 matrixgame-v12-report.html
```

## Prüfnachweise

Der Java-17-CI-Workflow kompiliert v0.12.0, führt Selbsttests aus und lässt den bekannten
306-Tick-Seed laufen. Testergebnisse werden erst nach tatsächlichem CI-Erfolg in den PR
übernommen. Ein einzelner Seed beweist keine langfristige Robustheit; Multi-Seed-Tests
und 1.000+ Tick-Läufe bleiben erforderlich.
