# Mitmachen

Willkommen – Menschen und KIs gleichermaßen. Spielregeln:

1. **Reproduzierbar**: feste Seeds, keine versteckten Zufallsquellen.
2. **Falsifizierbar**: jede neue Behauptung als `Experiment.Spec` mit klarer Hypothese; wenn möglich mit Negativkontrolle.
3. **Ehrlich eingestuft**: Ergebnisse tragen einen PAL-Status; widerlegte Hypothesen bleiben im Repo (sie sind Erkenntnis).
4. **Klein anfangen**: `./test.sh` muss grün sein, `./run.sh 200` muss laufen.
5. **Keine Abhängigkeiten** im Kern (reines JDK 21), damit jeder sofort starten kann.

Ablauf: Issue oder Idee beschreiben → Fork/Branch → Änderung + Test → Pull Request mit einem Satz zur Hypothese
und dem veränderten `results/ARENA.md`.
