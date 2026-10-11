# Architektur

## Die Schleife

```
Existenz -> Wahrnehmung -> Information -> Verarbeitung -> Entscheidung -> Aktion -> Rückmeldung -> Lernen -> Anpassung
```

Im Code: `Observation` → `Agent.decide` → `Action` → `World.step` → `Feedback` → `Agent.learn`.

## Systemformel

`System = Zustand + Regeln + Zufall + Rückkopplung + Zeit`

- Zustand: `Resource`, Energie der Agents
- Regeln: `World.step`
- Zufall: seeded `Random` (Reproduzierbarkeit!)
- Rückkopplung: `Feedback`, `lastAvgHarvest`
- Zeit: `tick`

## Reproduzierbarkeit als Grundregel

Jedes Experiment läuft über feste Seeds `baseSeed .. baseSeed+n-1`. Gleicher Seed → identische Messwerte
(wird in `SelfCheck` getestet). Ergebnisse landen als Markdown in `results/`.

## PAL (Probability Annotation Layer)

| Stufe | Bedeutung |
|---|---|
| FACT | unabhängig belegt |
| MODEL | im Modell gestützt, Übertragung auf die Realität offen |
| HYPOTHESIS | Vermutung / nicht gestützt / widerlegt |
| FICTION | bewusst erfunden (Spielwelt, Allegorie) |

Ein `Experiment` kann eine Hypothese höchstens auf `MODEL` heben.

## Was dieses Modell nicht behauptet

- Die Abstraktion „Input → Transformation → Output“ ist so allgemein, dass sie fast alles beschreibt.
  Ihren Wert hat sie erst, wenn daraus **prüfbare** Vorhersagen folgen – deshalb gibt es `Experiment` und Negativkontrollen.
- Die Gewichte des `NMaxMaxAgent` sind Annahmen, keine Naturkonstanten.
- Das Urteil `SUPPORTED` nutzt die Daumenregel |t| > 2 (Welch). Bei Simulationen mit deterministischen Teilen
  (z. B. identische Greedy-Agents) ist die Streuung 0 und t wird künstlich riesig – aussagekräftig ist dann die Effektgröße.
- Ein erfolgreicher Algorithmus in der Allmende sagt nichts über Menschen oder Gesellschaften, solange das nicht separat belegt ist.
