# Matrixgame Universal Grid Lab v0.7 – Entwicklungscheckpoint

**Datum:** 17.11.2026  
**Branch:** `evolution-ipo-genetics-v07`  
**Basierend auf:** Matrixgame Universal Grid Lab v0.6  
**Status:** lokaler Java-Prototyp kompiliert und getestet; dieser Checkpoint dokumentiert Architektur, Tests und Fortsetzung.

## Ziel

Die Evolution umfasst nun nicht nur körperliche Eigenschaften, sondern auch vererbbare Teile der INPUT-OPERATION-OUTPUT-Entscheidungsarchitektur.

## Architektur

`GameObject -> Perception/Input -> DecisionContext -> DecisionAlgorithm -> GameAction -> zentraler World Resolver -> Feedback/Fitness`

Der feste gemeinsame Aktionsraum aus v0.6 bleibt unverändert: 37 Optionen (1 WAIT + 4 MOVE + 8 Interaktionstypen × 4 Richtungen). Pro Objekt und Tick wird höchstens eine Aktion über den gemeinsamen Resolver verarbeitet.

## Vererbbares Genom

### INPUT-Gene
- `localSense`: unmittelbare vier Nachbarfelder
- `rowRange`: gleiche Grid-Reihe (gleiches Y)
- `columnRange`: gleiche Grid-Spalte (gleiches X)
- `radiusRange`: Manhattan-Radius
- `unitSampling`: Anteil der wahrgenommenen Units
- `sensorNoise`: Wahrscheinlichkeit, ein eigentlich wahrnehmbares Objekt zu übersehen

Sensor-Stichprobe und Sensorrauschen werden deterministisch aus Seed, Tick, Beobachter-ID, Ziel-ID und Sensor-Kanal abgeleitet. Die Wahrnehmung verbraucht dadurch nicht den Zufallsstrom für Ressourcenregeneration und Mutation.

### OPERATION-Gene
- `foodSeek`: Nahrungssuche
- `preySeek`: Beute-/Angriffsneigung
- `threatAvoid`: Vermeidung stärkerer Gegner
- `reproductionDrive`: Gewicht für Zellteilung
- `exploration`: Erkundung freier Nachbarfelder
- `mutationRate`: Mutation pro Gen-Locus vor globalem Mutationsdruck

### OUTPUT
Der Evaluator bewertet die aktuell legalen GameActions. Adaptive Softmax erzeugt eine Auswahlverteilung; Exploration hält jede legale Aktion mit positiver Wahrscheinlichkeit erreichbar. Der Resolver prüft und führt die ausgewählte Aktion aus.

## Vererbung, Mutation und Identität
- Nachkommen übernehmen Team und Farbe des Elternobjekts unverändert.
- Sensor- und Operationsgene werden vererbt und je Locus begrenzt mutiert.
- Mutationsdruck kann im UI mit `M` zwischen 1.0x, 2.0x und 0.5x umgeschaltet werden.
- Generation, Parent-ID, kumulierte Mutationszahl und zuletzt veränderte Gen-Namen werden protokolliert.
- Körperliche Merkmale wie Metabolismus, Aggression, Nahrungseffizienz, Angriffsschaden, Teilungsschwelle und Sichtreichweite werden ebenfalls mutiert.

## Fitness und wissenschaftliche Einordnung
Fitness wird als Messgröße aus Überleben, Energie, erfolgreichen Aktionen, Fortpflanzung und Prädation geführt. Es gibt **noch keinen globalen Fitness-Ranking-Zuchtoperator**; Selektion entsteht derzeit vor allem über unterschiedliche Überlebens- und Fortpflanzungsergebnisse. Die Entscheidungsgewichte sind explizite Parameter, keine trainierten Neuronen. Q-/Aktionswerte werden derzeit nicht an Nachkommen vererbt. Minimax, Monte-Carlo und hypothetische Suchbäume sind noch nicht implementiert.

## Bedienung
- `T`: Evolution starten/stoppen
- `P`: Pause
- `G/B`: Geschwindigkeit
- `M`: Mutationsdruck zyklisch wechseln
- `F`: Nahrung hinzufügen
- `V`: Evolution/Boulder-Dash-Modus wechseln
- `R`: Reset
- Linksklick: Zelle auswählen
- `Q/E`: Aktionsfamilie
- Pfeile/WASD: manuelle Aktion
- Mausrad: Zoom; Rechtsziehen: Kamera

## Verifikation
- Build: `javac --release 17 -Xlint:all -Werror`
- Headless-Selbsttests: **54 Checks bestanden**
- GUI-Starttest: Swing-Fenster unter Xvfb gestartet und Screenshot erstellt
- Reproduzierbarer Referenzlauf: 600 Ticks, Seed `20261117`
  - lebende Zellen: 47
  - Geburten: 129
  - Todesfälle: 102 (65 Verhungern, 37 Prädation)
  - höchste lebende Generation: 5
  - lebende Zellen mit mindestens einer Mutation: 29
  - verschiedene Genome unter lebenden Zellen: 39
  - mittleres `foodSeek`: 1.194
  - mittleres `preySeek`: 0.911
  - mittleres `threatAvoid`: 0.918
  - mittleres `radiusRange`: 7.150
- Zwei unabhängige Läufe mit identischem Seed lieferten identische Kennzahlen.
- Populationsbilanz: 20 Startzellen + 129 Geburten - 102 Todesfälle = 47 lebende Zellen.

Diese Werte beschreiben einen einzelnen reproduzierbaren Modelllauf, keine biologische Prognose.

## Nächste Aufgaben
1. CSV/JSONL-Export für Genom-, Fitness- und Populationsverläufe.
2. Einzelne Gene im UI einfrieren und gezielt Mutationsraten steuern.
3. Zustandsabhängige Aktionswerte und Gedächtnis untersuchen; Vererbung optional zuschaltbar machen.
4. Kontrollierte Vergleichsexperimente für Mutationsdruck, Sensorarchitekturen und Selektionsregime durchführen.
5. Ein kleines neuronales Netz als austauschbare I/O/O-Policy ergänzen und gegen Adaptive Softmax vergleichen.
6. Erst danach Fitness-Ranking-Selektion, Minimax oder Monte-Carlo untersuchen.

## Lokale vollständige Quelldatei
Die vollständige Einzeldatei `MatrixgameUniversalGridLab_v07.java` ist als Gesprächsartefakt bereitgestellt. SHA-256:

`5a7e427a3478734174833078d2c901bfb72c0afa887e5f02625c8909c4f3f673`

Begleitende lokale Dateien: README, `START_WINDOWS_v07.bat`, `RUN_SELF_TESTS_v07.bat` und ZIP-Paket.
