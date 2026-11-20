# Matrixgame Universal Grid Lab v0.8 – Expanded Genetic Sandbox

**Datum:** 17.11.2026  
**Branch:** `expanded-world-genetic-lab-v08`  
**Basis:** v0.7 IPO-Genetik-Checkpoint und lokale v0.7-Java-Datei  
**Default world:** 120 × 80 Grid  
**Status:** Java 17 Build erfolgreich; 57 Selbsttests bestanden; GUI-Start geprüft.

## Änderungen gegenüber v0.7

- Interaktive Simulationswelt auf 120 × 80 Zellen erweitert (vierfache Kartenfläche gegenüber 60 × 40).
- Nahrungsvorrat zum Start auf 180 erhöht; Ressourcenlimit auf 360.
- Populationslimit für Kreaturen auf 200 erhöht.
- Kamera-Zoom von 0.15× bis 300× erweitert.
- Taste `0` und UI-Schaltfläche „Weltübersicht“ passen die vollständige endliche Karte in den Canvas ein.
- HUD zeigt Weltgröße, Zoom und Kamerakoordinaten.
- Snapshot erhält einen räumlichen Grid-Index: `WorldSnapshot.at(x,y)` und `isBlocked` müssen nicht mehr die vollständige Objektliste durchsuchen.
- Genetische Sensorabfragen suchen nur innerhalb der durch lokale, Reihen-, Spalten- und Manhattan-Radius-Gene definierten Zellbereiche.
- Inerte `IdleController`-Objekte werden aus dem KI-Planungsschritt ausgelassen, sofern keine manuelle Aktion für sie eingereiht ist.
- Das 37-Aktionen-Vokabular und die vererbbaren INPUT-/OPERATION-Gene aus v0.7 bleiben bestehen.

## Weltmodell / Infinity Canvas

Die Simulation ist jetzt eine größere, aber weiterhin **endliche** Welt mit 120 × 80 Zellen und Randbegrenzung. Die Kamera verändert nur die Darstellung und nicht die Weltkoordinaten.

Noch nicht implementiert:
- prozedurales Chunk-Streaming,
- negative/unbegrenzte Weltkoordinaten,
- persistentes Speichern/Entladen von Weltsektoren.

Diese Funktionen sollen als eigener Schritt entwickelt werden, statt die getesteten Welt- und Tick-Regeln unkontrolliert zu verändern.

## Verifikation

- `javac --release 17 -Xlint:all -Werror`: erfolgreich.
- Headless Self-Tests: **57 Checks bestanden**.
- ZIP-Paket separat entpackt und erneut kompiliert; Tests erneut bestanden.
- GUI-Start und Screenshot unter Xvfb: erfolgreich.
- 120 × 80 / 600 Ticks / zwei Läufe mit Seed `20261117`:
  - beide Läufe identisch,
  - Population: 35,
  - Geburten: 131,
  - Todesfälle: 116,
  - höchste lebende Generation: 5,
  - Nahrung am Ende: 79,
  - Populationsbilanz: 20 + 131 − 116 = 35,
  - Paarlauf-Laufzeit in dieser Umgebung: ca. 3.8 Sekunden.

Das ist ein reproduzierbarer Modelllauf, keine allgemeingültige biologische Prognose und kein allgemeiner Hardware-Benchmark.

## Bedienung

- Pfeile/WASD: ausgewählte Aktion in eine Richtung
- Q/E: Aktionsfamilie wechseln
- Rechtsziehen: Kamera verschieben
- Mausrad: Zoom um Cursor
- `0`: vollständige Welt einpassen
- T: Evolution Auto
- V: Evolution/Boulder-Dash-Modus wechseln
- M: Mutationsdruck
- G/B: Simulationsgeschwindigkeit
- F: Nahrung hinzufügen
- H/J: Hybrid-Gegner/Wanderer erzeugen
- I: Gegner-Entscheidungsalgorithmus wechseln
- P: Pause, R: Reset

## Nächste Entwicklungsaufgaben

1. CSV- und JSONL-Export für Population, Genome, Fitness, Mutationen und Ereignisse.
2. Gen-Loci im UI einfrieren und gezielte Mutationsraten einstellen.
3. Zustandsabhängige Aktionswerttabellen mit optionaler Vererbung.
4. Neuronales Netz als austauschbare INPUT→OPERATION→OUTPUT-Policy.
5. Kontrollierte A/B/C-Vergleiche unter identischen Seeds und Ressourcenregeln.
6. Danach echtes Chunk-Streaming für die Infinity-Welt.

## Lokale Dateien / Prüfsummen

- `MatrixgameUniversalGridLab_v08.java`
  SHA-256: `ac6341720530eab2f7088bcc7fd7d02de260998cd4ae8bb28743c4563a0d022c`
- `MatrixgameUniversalGridLab_v08.zip`
  SHA-256: `fe8a209e96ba763a5bdc6a1f3de752d28a99e4cee502106f362e9f3f52263607`

Die vollständige Java-Datei und das ZIP sind als lokale Gesprächs-Downloads bereitgestellt. Dieser GitHub-Checkpoint dokumentiert die verifizierte Architektur, Tests, Prüfsummen und Fortsetzung.
