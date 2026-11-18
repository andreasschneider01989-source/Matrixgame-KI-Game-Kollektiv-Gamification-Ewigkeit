# Matrixgame Universal Grid Lab v0.6.1 – Frontier camera milestone

## Herkunft und Versionsstatus

- Ausgangsbasis: `Boulderdash Grid Evolution 17.11.2026 KI-Game Andreas ChatGPT` auf `main`, Klasse `rhgr.MatrixgameUniversalGridLab_v06`.
- Neue Datei: `src/rhgr/MatrixgameUniversalGridLab_v061.java`.
- Diese Version ist ein additiver Ableger von v0.6, **nicht** die fehlende lokale v0.8.5.
- Die bestehende v0.6-Datei bleibt unverändert.

## Was v0.6 bereits mitbringt

1. Einheitliches 37-Aktionen-Vokabular: WAIT, MOVE und acht gerichtete Interaktionstypen.
2. Zentrale Aktionsvalidierung und -auflösung über die Welt.
3. Getrennte Controller/Entscheidungsalgorithmen, darunter adaptive Softmax- und Zufalls-Baselines.
4. Gemeinsame Tick-Snapshots für Entscheidungen.
5. Evolution-Szenario mit Nahrung, Energie, Zellteilung, Mutation und Prädation.
6. Boulder-Dash-Szenario und bestehende Maussteuerung.
7. Headless Self-Tests für Aktionsraum, Interaktionen, Lernen, Reproduktion, Evolution und Invarianten.

## v0.6.1 – kleine, risikoarme Erweiterung

Zusätzliche Tastatursteuerung der Kamera:
- `Ctrl + Pfeile`: Kamera um jeweils drei Weltzellen verschieben.
- `Ctrl + =`: hineinzoomen.
- `Ctrl + -`: herauszoomen.
- `Ctrl + 0`: Kamera zurücksetzen.

Die bisherigen Pfeiltasten/WASD bleiben für Einheiten reserviert. Rechtsziehen und Mausrad bleiben erhalten. Kamerabewegung verändert keine Spielobjekte.

## Technische Gewichtung / Architekturbeurteilung

| Bereich | Einschätzung | Begründung |
|---|---|---|
| Aktionsmodell | stark | Einheitliches, typisiertes Vokabular |
| Resolver / Regeln | stark | Zentrale Validierung statt beliebiger Objektänderungen |
| KI-Experimente | gute Basis | Vergleichbare Controller und reproduzierbare Seeds |
| Evolution | gute Basis | Messbare Ressourcen-, Geburts- und Todesprozesse |
| Rendering / Kamera | ausbaufähig | Aktuell feste Weltgrenzen und Rendering über vorhandene Objekte |
| Unendliche Welt | noch nicht umgesetzt | Erfordert Chunk-Speicher und Weltkoordinaten-Architektur |
| Türme / Gebäude-Upgrades | noch nicht umgesetzt | Geplanter nächster Gameplay-Baustein |

## Verifikation

- Quelltext aus dem Repository abgerufen und neue Klasse mechanisch erzeugt.
- Vor dem Speichern geprüft: Klassennamen/Constructor/Main/Canvas-Callback konsistent umbenannt; Kamera-Bindings und Methoden vorhanden.
- **Noch nicht ausgeführt:** javac-Kompilierung, Swing-GUI-Test und Self-Test-Lauf. Daher keine Behauptung, dass die neue Klasse bereits kompiliert oder im Spiel getestet wurde.

## Nächster Schritt

1. v0.6.1 lokal kompilieren und `--self-test` ausführen.
2. Tastenkürzel in der GUI prüfen.
3. Danach einen isolierten Tower-Prototyp mit Reichweite, Schaden, Cooldown, Lebenspunkten und Kosten ergänzen.
4. Erst nach Messungen die Tower-Mechanik in den zentralen Resolver integrieren.

## Start / Test

Package: `rhgr`

```powershell
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v061.java
java -cp out rhgr.MatrixgameUniversalGridLab_v061
java -cp out rhgr.MatrixgameUniversalGridLab_v061 --self-test
```
