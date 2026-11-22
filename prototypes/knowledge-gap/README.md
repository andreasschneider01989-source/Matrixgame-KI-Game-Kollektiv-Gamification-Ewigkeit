# Matrixgame KnowledgeGap + I/O prototype

## Zweck

Dieser kleine Prototyp verbindet drei Ebenen:

1. **Input:** ein versioniertes JSON-Szenario mit Zahlen, Zeitpunkten, Quellen-/Statuslabels und offenen Fragen.
2. **Operation:** eine reine, nachvollziehbare Berechnung der Informationsluecke.
3. **Output:** ein lesbarer Konsolenbericht mit einer einfachen Lehr-Ampel.

Das Modell demonstriert eine Idee. Es ist noch kein validiertes epidemiologisches, soziales oder Konfliktprognosemodell.

## Wissenschaftliche Trennung

- **Daten:** Werte, die in einem konkreten Szenario stehen.
- **Modellannahmen:** bewusst gesetzte Beispielwerte.
- **Operation:** die explizite Berechnung.
- **Interpretation:** die Bedeutung, die Menschen aus dem Ergebnis ableiten.
- **Validierung:** Abgleich mit unabhaengigen Daten und dokumentierten Tests.

Die Demo verwendet ausschliesslich synthetische Werte. Die Ampel-Schwellen sind didaktische Einstellungen und keine medizinischen oder politischen Grenzwerte.

## Run

Python 3.10 oder neuer, keine Drittanbieterpakete:

```bash
python matrixgame_core.py
python -m unittest discover -s tests
```

## Anschluss an die groessere Matrixgame-Vision

Eine spaetere Architektur kann weitere Ebenen als getrennte Module abbilden:

**Sprache/Grammatik → formale Logik und Mathematik → Informatik/Information → Physik → Chemie → Biologie → Psychologie → soziale Systeme / kollektive Entscheidungsfindung.**

Das ist eine **Organisations- und Modellierungskarte**, keine Behauptung, dass jede Wissenschaft vollstaendig auf die vorherige reduzierbar sei. Jede Ebene braucht eigene Daten, Methoden und Validierung.

## Naechste Quests

- [ ] Szenarien mit JSON Schema validieren.
- [ ] Quellen und Zeitstempel je Aussage erfassen.
- [ ] Sensitivitaetsanalyse und reproduzierbare Vergleichslaeufe ergaenzen.
- [ ] Agentenbeitrag mit Hypothese, Evidenz, Unsicherheit und Gegenargument standardisieren.
- [ ] Erst nach Tests eine Browser-Oberflaeche bauen.
