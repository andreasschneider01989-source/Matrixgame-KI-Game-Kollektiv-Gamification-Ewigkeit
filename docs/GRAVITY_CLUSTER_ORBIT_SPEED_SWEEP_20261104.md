# Orbitgeschwindigkeits-Sweep – Forschungsnotiz 04.11.2026

## Forschungsfrage
Wie verändert die Skalierung der anfänglichen inneren Satelliten-Umlaufgeschwindigkeit das Ergebnis einer Begegnung zweier gleich schwerer Cluster, wenn die Cluster-Schwerpunkte, Gesamtmassen, Anfangsabstände und der Gravitationskraftterm gleich bleiben?

## Definition
Der Faktor \(k\) skaliert nur die internen Geschwindigkeiten:
\[
\vec v_i'=\vec V_{\mathrm{COM}}+k(\vec v_i-\vec V_{\mathrm{COM}})
\]
Damit bleibt die Translationsgeschwindigkeit des jeweiligen Cluster-Schwerpunkts erhalten. Die Positionen, Masse und Newtonsche Kraftfunktion werden nicht verändert.

\(k=1\) ist die ausbalancierte isolierte Referenz. \(k<1\) bedeutet weniger tangentiale Geschwindigkeit; bei tangentialem Start liegt der Körper im vereinfachten Zweikörperbild nahe dem Apzentrum. \(k>1\) bedeutet mehr Drehimpuls; der Startpunkt liegt im gebundenen elliptischen Zweikörperbild am Perizentrum, bis die Fluchtgrenze erreicht wird.

Für das isolierte, weichgezeichnete Ein-Satelliten-Zweikörpermodell:
\[
v_c=\sqrt{\frac{GMR^2}{(R^2+\epsilon^2)^{3/2}}},\qquad
v_{\rm esc}=\sqrt{\frac{2GM}{\sqrt{R^2+\epsilon^2}}}
\]
Bei \(M=100, R=8, \epsilon=0.5\) ist \(v_{\rm esc}/v_c\approx1.41697\). Das ist nur ein theoretischer Richtwert des isolierten Zweikörperpotentials; im Mehrkörper-Encounter ist es keine globale Fluchtgrenze.

## Pilotmessung
40 deterministische Modellläufe, \(b=29\), fünf Phasen \(0°,46°,90°,180°,270°\), vier Faktoren \(k\in\{0.8,1.0,1.2,1.4\}\), Varianten mit einem oder zwei Satelliten.

| Satelliten je Cluster | k | Kontakte / 5 |
|---:|---:|---:|
| 1 | 0,8 | 2/5 |
| 1 | 1,0 | 0/5 |
| 1 | 1,2 | 4/5 |
| 1 | 1,4 | 3/5 |
| 2 | 0,8 | 0/5 |
| 2 | 1,0 | 5/5 |
| 2 | 1,2 | 5/5 |
| 2 | 1,4 | 0/5 |

Bei \(k=1\) stimmen die Pilotläufe mit den entsprechenden Referenzfällen aus dem 189er-Sweep überein. Die Pilotwerte zeigen Nicht-Monotonie und Phasenabhängigkeit. Sie sind nur für fünf Phasen bei einem Stoßparameter erhoben und erlauben keine allgemeine Aussage.

## Interpretation
- **FACT:** Im reinen Newton-Modell sind alle paarweisen Gravitationskräfte anziehend. Eine erhöhte Tangentialgeschwindigkeit ändert Anfangsenergie und Drehimpuls, sie erzeugt keine neue abstoßende Kraft.
- **MODEL:** Höheres \(k\) kann größere Bahnen, stärkere Exzentrizität oder Entkommen auslösen. Durch äußere Störungen kann die Reaktion nichtmonoton ausfallen.
- **HYPOTHESIS:** Für bestimmte Phasen und Stoßparameter kann erhöhte interne Rotation die Kernannäherung verringern; in anderen Fällen kann sie sie vergrößern.
- **OPEN:** Die vollständige Phasenkarte über \(k,b,\varphi\), Zeitschritt-Konvergenz in Grenzfällen und unabhängiger Solververgleich.

## Wichtige Abgrenzung zur Chemie
Zwei Satelliten pro Kern sind kein Heliumatom und ein Satellit pro Kern ist kein Wasserstoffatom. Ein Heliumatom besteht aus zwei Protonen und zwei Elektronen; seine geschlossene 1s-Schale folgt aus quantenmechanischen Zuständen und dem Pauli-Prinzip. Das Wasserstoffmolekül H₂ wird über Molekülorbitale bzw. Elektronendichte gebunden. Im aktuellen Cluster-Modell gibt es weder elektrische Ladungen noch Elektronenspin, Pauli-Prinzip oder quantenmechanische Wellenfunktionen. Die Ähnlichkeit ist daher eine visuelle/gamifizierte Analogie, keine chemische Äquivalenz.

## Nächste Experimente
1. Feinerer Pilot mit \(k=0.8\) bis 1.45 in Schritten von 0.05, mehr Phasen und \(b=28,29,30\).
2. Ein- und Zwei-Satellitenvarianten getrennt und mit identischen Phasen vergleichen.
3. Erst danach eine **separate** künstliche Satellit-Satellit-Abstoßung als zusätzlicher Kraftterm testen. Diese muss bei Null exakt auf das Newton-Modell zurückfallen und darf nicht als aus Gravitation abgeleitet bezeichnet werden.
4. Für jeden Grenzfall dt-Konvergenz und danach REBOUND IAS15 als unabhängige Referenz.
5. Später Kernradius-Kontaktkriterien und Massenanteile getrennt variieren.

## Reproduzierbarkeit
Skript: \`experiments/gravity_cluster_velocity_sweep.py\`  
Tests: \`experiments/test_gravity_cluster_velocity_sweep.py\`  
Default-Ausgabe: CSV und JSON; deterministisch, kein Zufalls-Seed nötig.
