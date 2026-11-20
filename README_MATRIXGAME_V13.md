# Matrixgame Universal Grid Lab v0.13.0 – Large World

## Was sich ändert

v0.13.0 ist ein separater Entwicklungskandidat auf Basis von v0.12.0. Die vorherigen Versionen bleiben unverändert.

- Standardwelt: **320 × 200** statt 160 × 100 (vierfache Fläche).
- Mehr Nahrung: bis zu 1.600 Ressourcen; 960 werden zu Beginn verteilt, dazu sechs lokale Starterfelder.
- Starterfelder sind größer und liegen bei den sechs Team-Startzonen.
- Batch-Simulationen erlauben jetzt bis zu 800 × 800 Felder.
- Der bestehende Seed, die sechs Teams, die Teamfarben, Ökologie, Diplomatie, Zitadellen, Level-2-Produktion und Infinity-Frontier bleiben erhalten.

## Start unter Windows

Voraussetzung: JDK 17 oder neuer.

1. Repository-Branch herunterladen oder auschecken.
2. `RUN_MATRIXGAME_V13_SIMULATION.bat` doppelklicken.
3. Die Datei `matrixgame-v13-report.html` öffnet sich im Browser.

Mit einer Tickzahl als Argument lässt sich die Laufzeit ändern, z. B.:
`RUN_MATRIXGAME_V13_SIMULATION.bat 1000`

Für die interaktive Java-Swing-Oberfläche:
```powershell
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v13_0.java
java -cp out rhgr.MatrixgameUniversalGridLab_v13_0
```

## Selbsttests

`TEST_MATRIXGAME_V13.bat` oder:

```powershell
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v13_0.java
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v13_0 --self-test
```

## Größere Batch-Welten

```powershell
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v13_0 --simulate 1000 20261116 480 300 matrixgame-large.html
```

Maximal zulässig: 800 × 800; die tatsächliche Laufzeit hängt von Objektdichte, Bevölkerung und Hardware ab.

## Technische Grenze

Diese Version vergrößert die **aktive endliche Simulationsregion**. Die 64×64-Frontier außerhalb der Region bleibt eine prozedurale visuelle Darstellung. Es ist noch kein echtes unendliches Chunk-Streaming mit persistenten, aufweckbaren Agenten. Auch Handel, Bündnisnachrichten und explizite Unit-zu-Unit-Kommandos sind noch nicht implementiert. Sie werden als eigene, testbare Systeme in späteren Stufen ergänzt, statt hier nur vorgetäuscht zu werden.

## Wissenschaftliche Einordnung

Die Rollen sind adaptive Spielmodelle, keine biologisch kalibrierten Artenmodelle. Die 320×200-Vergrößerung ist ein Skalierbarkeitstest, kein Beleg für unbegrenzte Rechenleistung.
