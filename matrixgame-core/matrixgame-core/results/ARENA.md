# Arena-Ergebnisse

Allmende-Welt, 10 Agents, 200 Seeds (1..200). Reproduzierbar mit `./run.sh 200`.

> Alle Urteile gelten nur fuer dieses Modell mit diesen Parametern und Gewichten.

### E1 Lebensdauer: N-Max-Max- vs. Greedy-Population

- Hypothese [HYPOTHESIS 80%]: Eine reine N-Max-Max-Population ueberlebt in der Allmende laenger als eine reine Greedy-Population.
- Metrik: `lifetime`, n = 200 Seeds
- N-Max-Max: 200.00 (sd 0.00)
- Greedy: 31.00 (sd 0.00)
- Welch-t = 1000000000.00
- **Urteil: SUPPORTED** -> PAL MODEL (gilt nur in diesem Modell)

### E2 Wohlfahrt: N-Max-Max- vs. Greedy-Population

- Hypothese [HYPOTHESIS 80%]: Eine reine N-Max-Max-Population erntet insgesamt mehr als eine reine Greedy-Population.
- Metrik: `welfare`, n = 200 Seeds
- N-Max-Max: 11098.38 (sd 82.17)
- Greedy: 1020.66 (sd 8.13)
- Welch-t = 1726.12
- **Urteil: SUPPORTED** -> PAL MODEL (gilt nur in diesem Modell)

### E3 Gemischte Population 5/5: wer lebt laenger?

- Hypothese [HYPOTHESIS 50%]: In gemischter Population leben N-Max-Max-Agents laenger als Greedy-Agents (Kooperation wird nicht nur ausgebeutet).
- Metrik: `lifetime (pro Agent)`, n = 200 Seeds
- N-Max-Max: 19.70 (sd 0.80)
- Greedy: 52.65 (sd 1.24)
- Welch-t = -315.62
- **Urteil: REFUTED** -> PAL HYPOTHESIS (gilt nur in diesem Modell)

### E4 Wohlfahrt: N-Max-Max- vs. Reziprozitaets-Population

- Hypothese [HYPOTHESIS 50%]: Eine N-Max-Max-Population erntet mehr als eine Reziprozitaets-Population.
- Metrik: `welfare`, n = 200 Seeds
- N-Max-Max: 11098.38 (sd 82.17)
- Reziprozitaet: 12498.05 (sd 27.55)
- Welch-t = -228.41
- **Urteil: REFUTED** -> PAL HYPOTHESIS (gilt nur in diesem Modell)

### E5 Negativkontrolle: N-Max-Max gegen sich selbst (andere Seeds)

- Hypothese [HYPOTHESIS 90%]: Messverfahren erzeugt keinen Unterschied, wo keiner existiert.
- Metrik: `welfare`, n = 200 Seeds
- N-Max-Max (Seeds A): 11098.38 (sd 82.17)
- N-Max-Max (Seeds B): 11087.53 (sd 77.59)
- Welch-t = 1.36
- **Urteil: INCONCLUSIVE** -> PAL HYPOTHESIS (gilt nur in diesem Modell)

