# Matrixgame — gemeinsamer KI-Entwicklungsvertrag

## Zweck

Dieses Dokument verbindet verschiedene Werkzeuge und Sprachmodelle über eine gemeinsame, überprüfbare Projektbasis. Es setzt nicht voraus, dass die Modelle ihre internen Chats oder Gedächtnisse miteinander teilen.

## Gemeinsame Quelle der Wahrheit

Priorität der Informationen:

1. Quellcode im jeweiligen Git-Commit;
2. reproduzierbare Tests und CI-Protokolle;
3. Versions-README und Architektur-/Entscheidungsnotizen;
4. GitHub Issues und Pull Requests als Aufgaben- und Übergabeprotokoll;
5. Chats als Ideensammlung, nicht als alleiniger Nachweis.

Bei Widersprüchen gewinnt der ausführbare Quellcode zusammen mit den aktuellen Tests – nicht eine ältere Chat-Zusammenfassung.

## Rollen als Arbeitsmuster

- **Ideen-Agent:** formuliert Hypothesen, Alternativen und überprüfbare Akzeptanzkriterien.
- **Implementierungs-Agent:** erstellt einen kleinen Patch und passende Regressionstests.
- **Review-Agent:** sucht Logikfehler, Randfälle, unbeabsichtigte Nebenwirkungen und fehlende Messungen.
- **Experiment-Agent:** führt Seeds/Tick-Läufe aus und dokumentiert Populations-, Ressourcen- und Überlebensdaten.
- **Maintainer/Mensch:** priorisiert, bewertet Risiken und entscheidet über Merge und Produktziele.

Ein einzelnes Modell kann mehrere Rollen übernehmen; getrennte Rollen sind eine Qualitätskontrolle, keine Behauptung unabhängiger Intelligenz.

## Ein Arbeitszyklus

1. Aufgabe in einem GitHub Issue oder einer klaren Übergabenotiz definieren.
2. Agent liest `AGENTS.md`, `README_MATRIXGAME_V11.md` und den angegebenen Commit.
3. Agent arbeitet auf einem eigenen Feature-Branch.
4. Änderung wird klein gehalten und mit reproduzierbaren Tests abgesichert.
5. Agent öffnet einen PR mit Commit-SHA, Diff, Testausgabe, bekannten Fehlern und offenen Fragen.
6. Ein zweiter Agent prüft die Änderung unabhängig.
7. Menschliche Freigabe vor dem Merge in `main`.

## Simulationsnachweise

Jeder Experimentbericht nennt:
- Version und Commit-SHA;
- Seed, Weltgröße und Tick-Anzahl;
- Startpopulation, Endpopulation, Geburten und Todesfälle;
- Nahrung und Ressourcentransporte;
- Gebäude pro Team, Produktion und Level-Verteilung;
- Erfolg/Niederlage und Abbruchgrund;
- exakte Test-/Workflow-URL.

Ein einzelner Seed belegt nur diesen Lauf. Für Aussagen über Robustheit braucht es mehrere Seeds und längere Zeithorizonte.

## Daten- und Sicherheitsgrenzen

- Keine Passwörter, API-Schlüssel oder privaten Daten in Git.
- Keine Agenten dürfen ungeprüft `main` überschreiben.
- Keine automatischen Merges aufgrund bloßer Modellbehauptungen.
- Externe Informationen werden mit Quelle und Datum gekennzeichnet.
- Menschen behalten die Kontrolle über kostenpflichtige Dienste, Berechtigungen, Freigaben und Veröffentlichungen.

## Aktueller technischer Einstieg

- Java 17+
- `src/rhgr/MatrixgameUniversalGridLab_v11_0.java`
- `--self-test`: deterministische Prüfungen
- `--simulate`: headless Simulation und interaktiver HTML5-Canvas-Bericht
- GitHub Actions Workflow: `matrixgame-lab-run.yml`
