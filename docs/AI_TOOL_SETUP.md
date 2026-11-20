# KI-Werkzeuge mit Matrixgame verbinden

Dieser Leitfaden beschreibt drei getrennte Ausführungsorte: GitHub Actions, lokale IDE und Mistral Vibe Code Web.

## 1. GitHub Actions — Rechnen und Testen in der Cloud

Öffne den Workflow:

https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/actions/workflows/matrixgame-lab-run.yml

Dort:
1. Workflow **Matrixgame Lab — Run simulation and Canvas report** auswählen.
2. **Run workflow** anklicken.
3. Tickzahl, Seed und Weltgröße einstellen.
4. Starten und den Run beobachten.
5. Nach erfolgreichem Lauf das Artefakt `matrixgame-browser-canvas` herunterladen.

Der Workflow kompiliert mit Java 17, führt die Selbsttests aus und erstellt ein interaktives HTML-Dashboard. Die Berechnung läuft auf dem GitHub-Runner, nicht auf dem eigenen PC. Das HTML-Dashboard ist eine browserfähige Auswertung des Endzustands und des gespeicherten Populationsverlaufs; es ist kein fernsteuerbares Live-Swing-Fenster.

## 2. Lokal in Visual Studio Code — Mistral Vibe

Mistral dokumentiert die Vibe-Erweiterung für **Visual Studio Code 1.94 oder neuer**. Das ist nicht dasselbe Produkt wie das klassische Visual Studio.

Einrichtung in VS Code:
1. Matrixgame-Repository lokal klonen und den Branch `feature/matrixgame-v0110-survival-logistics` auschecken.
2. Erweiterungsansicht mit `Ctrl+Shift+X` öffnen.
3. Nach **Mistral Vibe** suchen; Publisher ist `mistralai`.
4. Installieren und das Vibe-Panel über die Activity Bar öffnen.
5. Mit Mistral anmelden und den lokalen Projektordner als Workspace öffnen.

Die Erweiterung kann Kontext aus der aktiven Datei und markierten Zeilen verwenden. Der Agent kann den lokalen Workspace prüfen, Dateien bearbeiten und Befehle ausführen; Freigaben und Diff-Review sollten aktiviert bzw. bewusst genutzt werden.

Dokumentation:
- https://docs.mistral.ai/vibe/code/vs-code-extension/install-authenticate
- https://docs.mistral.ai/vibe/code/vs-code-extension/work-with-extension

## 3. Mistral Vibe Code Web — GitHub-Repository in einer Cloud-Sandbox

Für Arbeit direkt am Repository ohne lokale ZIP-Datei:
1. Mistral Vibe öffnen und in den Bereich **Code** wechseln.
2. Ein Projekt erstellen.
3. Das GitHub-Repository `Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit` verbinden.
4. Eine neue Session beginnen und zunächst nur eine Bestandsaufnahme anfordern.
5. Änderungen in einem eigenen Branch und Pull Request prüfen.

Vibe Code Web erstellt eine Remote-Sandbox, kann GitHub-Repositories laden, Befehle ausführen und Branches/Pull Requests erzeugen. Das läuft unabhängig von der lokalen IDE.

Dokumentation:
- https://docs.mistral.ai/vibe/code/vibe-code-web/get-started
- https://docs.mistral.ai/vibe/code/overview

## 4. GitHub Copilot und weitere Modelle

GitHub Copilot Chat hat eine Modellauswahl. Über **Manage Models** können je nach IDE, Tarif und Richtlinien weitere Modellanbieter hinzugefügt werden; meist werden dafür Provider-API-Schlüssel oder Modell-IDs benötigt.

Die offizielle Dokumentation beschreibt unter anderem Anthropic, Gemini, OpenAI und weitere Anbieter. Sie bestätigt nicht pauschal, dass Mistral Vibe selbst als Copilot-Modell auswählbar ist. Mistral Vibe ist ein separater Agent bzw. eine eigene IDE-Erweiterung.

Dokumentation:
- https://docs.github.com/en/copilot/how-tos/copilot-in-your-ide/chat-with-copilot/change-the-chat-model
- https://docs.github.com/en/copilot/how-tos/use-ai-models/change-the-chat-model

## 5. Gemeinsames Gedächtnis über mehrere Modelle

Sprachmodelle teilen ihre privaten Chatverläufe nicht automatisch. Für Zusammenarbeit wird das Repository zur gemeinsamen Quelle:
- `AGENTS.md`: allgemeine Regeln aller Coding-Agenten;
- `.github/copilot-instructions.md`: GitHub-Copilot-spezifische Regeln;
- `docs/AI_COLLABORATION_PROTOCOL.md`: Übergabe- und Reviewprozess;
- Issues: Aufgaben, Ziele und Akzeptanztests;
- Feature-Branches und Pull Requests: konkrete Änderungen und Review;
- Actions-Protokolle: reproduzierbare Tests und Simulationsergebnisse.

Empfohlen: Jeder Agent beginnt mit einer Bestandsaufnahme, arbeitet danach auf einem eigenen Branch und liefert einen PR. Kein Agent überschreibt ungeprüft `main`.

## 6. Grenzen klar unterscheiden

| Umgebung | Rechenort | Lokale GUI-Fenster |
|---|---|---|
| GitHub Actions | GitHub-Runner | Nein |
| Mistral Vibe Code Web | Mistral-Cloud-Sandbox | Nein, nicht auf dem Windows-Desktop |
| Mistral Vibe VS Code lokal | Lokaler Workspace/Shell; Modellaufrufe über den konfigurierten Anbieter | Kann lokale Programme starten, sofern freigegeben |
| Matrixgame Swing direkt | Dein Windows-PC | Ja |

Die geeignete Kombination ist: GitHub Actions für überprüfbare Regressionen und lange headless Läufe, lokale IDE/Vibe für die interaktive Swing-Anwendung und Browser-Canvas-Berichte für gemeinsame Sichtbarkeit.
