# Matrixgame Universal Grid Lab v0.10.0

Eigenständiges Java-Swing-Projekt auf Basis des vorhandenen v0.9.0-Codes.

## Enthalten

- src/rhgr/MatrixgameUniversalGridLab_v10_0.java — vollständiger, formatierter Java-Quelltext
- RUN_MATRIXGAME_V10.bat — kompilieren und Simulation starten
- TEST_MATRIXGAME_V10.bat — kompilieren und 93 Selbsttests ausführen
- .project / .classpath / .settings — Eclipse-Projektmetadaten

## Voraussetzungen

- JDK 17 oder höher (nicht nur eine JRE)
- Windows 10/11 oder Eclipse IDE
- Keine externen Java-Bibliotheken erforderlich

## Schnellstart unter Windows

1. Den Projektordner vollständig entpacken.
2. RUN_MATRIXGAME_V10.bat doppelklicken.
3. Das Skript prüft `javac`, kompiliert UTF-8 und startet die GUI.

Für den Testlauf TEST_MATRIXGAME_V10.bat doppelklicken. Erfolgreich ist der Lauf, wenn am Ende
`Matrixgame Universal Grid Lab v0.10.0 self-test: 93 checks passed.`
erscheint.

## Eclipse

1. File → Import → Existing Projects into Workspace.
2. Den entpackten Projektordner auswählen.
3. Sicherstellen, dass ein JDK 17+ zugewiesen ist.
4. Project → Properties → Resource → Text file encoding: UTF-8.
5. `src/rhgr/MatrixgameUniversalGridLab_v10_0.java` als Java Application starten.

**Wichtig zur Zeichencodierung:** Der Java-Quelltext ist UTF-8 ohne BOM. Bei falscher Zeichencodierung können Umlaute und Spielfeldsymbole als Zeichensalat erscheinen. In Eclipse daher UTF-8 einstellen. Das Kompilierungsskript verwendet bereits `javac -encoding UTF-8`.

## Kernmechanik

- Sechs Teams, je vier Start-Units, Weltgröße 160×100.
- Nahrung erhöht Energie und gibt einen transportierbaren Biomasse-Marker.
- Einheiten können Ressourcen in ein benachbartes Gebäude des eigenen Teams einlagern.
- Gebäude lagern bis zu 48 Ressourcen.
- Ab 24 Ressourcen kann das Gebäude sein Lager teilen und eine Tochterzitadelle errichten.
- Ab 12 Ressourcen kann es eine Level-2-Unit produzieren.
- Überleben und Fortpflanzung haben Vorrang vor optionalem Bauen.

Die Ressource ist eine abstrakte Spiel-Biomasse, keine physikalisch kalibrierte Masse.
