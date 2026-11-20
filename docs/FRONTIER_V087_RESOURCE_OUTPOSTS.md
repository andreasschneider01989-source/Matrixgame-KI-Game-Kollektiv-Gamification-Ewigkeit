# Matrixgame Universal Grid Lab v0.8.7 – Resource Outposts

## Herkunft und Schutz der Basis
- Additive Weiterentwicklung der v0.8.6-Datei aus `feature/v086-keyboard-camera-controls`.
- v0.8.6 bleibt unverändert.
- Neue Startklasse: `rhgr.MatrixgameUniversalGridLab_v08_7`.
- Java 17+, keine externen Bibliotheken.

## Neu in v0.8.7
- Universal action vocabulary erweitert von 37 auf 49 Optionen: WAIT + 4 MOVE + 11 Interaktionstypen in vier Richtungen.
- `BUILD_DEPOT`: errichtet neben der handelnden Unit eine blockierende Lager-Kaserne.
  - Kosten: 3 Edelsteine aus dem Inventar.
  - 70 HP.
  - Lagerkapazität: 20 Ressourcen.
- `DEPOSIT`: überträgt einen Edelstein pro Aktion in das benachbarte Depot.
- `PRODUCE`: verbraucht 8 eingelagerte Ressourcen und erzeugt einen verbündeten schweren Wächter.
  - 60 HP, 14 Angriffsschaden, Sichtweite 10.
  - Teamzugehörigkeit wird vom Depot übernommen.
  - Team-aware GuardController greift sichtbare feindliche Einheiten an und nähert sich ihnen, wenn ein freier Schritt möglich ist.
- Mobile AI-Einheiten mit den entsprechenden Fähigkeiten können Ressourcen sammeln, Depots errichten, Ressourcen einlagern und Wächter produzieren. Die Strategie ist regelbasiert; die bestehenden Action-Rewards protokollieren erfolgreiche und erfolglose Aktionen weiter.
- Neue Tastenkürzel: C = BUILD_DEPOT, X = DEPOSIT, Z = PRODUCE. Alternativ Q/E zum Durchschalten der Aktionsmodi.
- HUD erklärt die neuen Tasten.

## Bewusste Grenzen dieses Inkrements
- Noch keine Produktionswarteschlange, mehrere Einheitentypen oder Upgrade-Bäume.
- Noch keine automatischen Verteidigungstürme; das ist der nächste getrennte Baustein.
- Die Welt ist weiterhin 120x80 bzw. szenarioabhängig begrenzt; kein Chunk-/Infinity-World-Backend.
- Die KI-Infrastrukturstrategie ist derzeit eine nachvollziehbare Regelpolitik, kein Nachweis selbstständigen strategischen Lernens. Die Action-Rewards bleiben als Lernspur erhalten.

## Verifikation
- Vier zusätzliche Selbsttest-Assertions für Errichtung, Lagerung, Produktion und gemeinsame Aktionsraumgröße.
- Quelltext-/Strukturprüfung durchgeführt.
- Java-Compiler, GUI-Interaktion und `--self-test` wurden in dieser Umgebung nicht ausgeführt. Bitte lokal mit Java 17+ prüfen.

## Eclipse
Package: `rhgr`
Datei: `MatrixgameUniversalGridLab_v08_7.java`
Main class: `rhgr.MatrixgameUniversalGridLab_v08_7`

Optionaler Konsolentest:
```powershell
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v08_7.java
java -cp out rhgr.MatrixgameUniversalGridLab_v08_7 --self-test
```
