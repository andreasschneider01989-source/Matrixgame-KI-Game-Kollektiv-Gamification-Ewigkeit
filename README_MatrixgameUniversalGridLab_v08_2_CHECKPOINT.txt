MATRIXGAME UNIVERSAL GRID LAB v0.8.2
====================================
Basis: getestete Version v0.8.1. Die bestehende Welt, Kamera, Zoomsteuerung,
37-Aktionen-Resolver, KI-Controller und Evolutionsmechanik bleiben erhalten.

NEU IN v0.8.2
-------------
- J: Aktionsmodus direkt auf REPRODUCE setzen.
- K: Aktionsmodus direkt auf USE / allgemeine Interaktion setzen.
- WASD / Pfeile: Richtung der ausgewählten menschlich gesteuerten Unit.
- Q / E: weiterhin durch alle Aktionsmodi schalten.
- N: bisheriger Gegner-Spawn (Wanderer); J ist jetzt der Reproduktions-Kurzbefehl.
- Evolutions-Levelziel für Team 1 (blau): mindestens 12 lebende Units und
  mindestens eine lebende Unit ab Generation 8.
- Das Ziel wird als Fortschritt in der Genom-/Statuszeile angezeigt.
- Keine künstlichen Team-Boni: Nachkommen erben weiterhin die Teamidentität,
  das Genom und mutierbare biologische Eigenschaften des Elternobjekts.

STEUERUNG
---------
Linksklick       Unit auswählen
KI/MENSCH        Steuerung der ausgewählten Unit umschalten
W / Pfeil hoch   nach oben
S / Pfeil runter nach unten
A / Pfeil links  nach links
D / Pfeil rechts nach rechts
J                REPRODUCE auswählen
K                USE / Interaktion auswählen
Q / E            Aktionsmodus zurück / weiter
Space            Warten / Tick (MENSCH-Modus)
Rechtsziehen     Kamera verschieben
Mausrad          Zoom um den Mauszeiger
0                Weltübersicht
T                Evolution AUTO ein/aus
V                Evolution / Boulder Dash wechseln
G / B            Tempo schneller / langsamer
M                Mutationsdruck wechseln
F                Nahrung +10
H                intelligenten Hunter erzeugen
N                Wanderer erzeugen
I                Gegner-KI wechseln
P                Pause
R                Neustart

LEVELZIEL: BLAUE EVOLUTIONSLINIE
--------------------------------
Erreiche mit Team 1 (blau):
1. mindestens 12 gleichzeitig lebende blaue Units,
2. mindestens eine blaue Unit der Generation 8 oder höher.

Die Bedingung ist bewusst populations- UND generationsbasiert. Eine kurzzeitige
hohe Geburtenrate allein reicht nicht; die Linie muss Nachkommen über mehrere
Generationen erhalten. Rot und die anderen Teams bleiben Konkurrenten.

BUILD (Java 17+)
----------------
PowerShell im Projektordner:
  javac --release 17 -Xlint:all -Werror -d out src\matrixgame\universal\MatrixgameUniversalGridLab_v08_2.java
  java -cp out matrixgame.universal.MatrixgameUniversalGridLab_v08_2

Selbsttests:
  java -Djava.awt.headless=true -cp out matrixgame.universal.MatrixgameUniversalGridLab_v08_2 --self-test

VERIFIKATION
------------
- Java-17-Kompilierung mit -Xlint:all -Werror erfolgreich.
- 62 Headless-Selbsttests bestanden.
- Der Test prüft zusätzlich den Fortschritt und das Erreichen des blauen Levelziels.
- Ein vollständiger interaktiver Windows-Spieltest steht noch aus.

ERSTER ERFOLGREICHER SPIELDURCHLAUF (18.11.2026)
------------------------------------------------
Vom Nutzer dokumentierter Screenshot: Tick 2031, Gesamtpopulation 55,
Team 1 (blau) 15, Team 2 (rot) 34, Team 3 (gruen) 6, Team 4 0,
Geburten 339, Todesfaelle 304, globale Gen-Anzeige 11.
Das HUD meldete: ZIEL ERREICHT: Blaue Linie stabil (12+ Units, Gen 8+).
Damit ist der Sieg in v0.8.2 durch den Nutzer-Screenshot belegt.
Hinweis: Die globale Gen-Anzeige ist nicht mit der lebenden blauen Generation gleichzusetzen.
Die Levelbedingung wertet Team-1-Population und lebende Team-1-Generation aus.
