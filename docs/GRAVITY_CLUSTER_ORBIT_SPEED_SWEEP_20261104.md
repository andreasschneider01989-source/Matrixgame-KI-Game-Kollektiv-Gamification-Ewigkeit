# Orbitgeschwindigkeits-Sweep – Forschungsnotiz 04.11.2026

## Forschungsfrage

Wie verändert die Skalierung der anfänglichen inneren Satelliten-Umlaufgeschwindigkeit das Ergebnis einer Begegnung zweier gleich schwerer Cluster, wenn Cluster-Schwerpunkte, Gesamtmassen, Anfangsabstände und die Newtonsche Kraftfunktion unverändert bleiben?

## Definition des Reglers

Der Faktor k skaliert nur die internen Geschwindigkeiten relativ zur jeweiligen Cluster-Schwerpunktgeschwindigkeit:

```
v_i_neu = V_COM + k * (v_i_alt - V_COM)
```

Damit bleiben die Cluster-Translationsgeschwindigkeiten unverändert. Positionen, Massen und Gravitationskraftterm werden nicht geändert.

- k=1: isolierte Referenzgeschwindigkeit.
- k<1: geringere tangentiale Geschwindigkeit und geringerer interner Drehimpuls.
- k>1: höhere tangentiale Geschwindigkeit und höherer interner Drehimpuls.

Eine Änderung von k erzeugt keine zusätzliche abstoßende Kraft. Sie verändert die Anfangsbedingungen und damit Energie, Drehimpuls, Bahnform und mögliche Energie-/Drehimpulsübertragungen während der Begegnung.

## Physikalischer Richtwert für die Ein-Satelliten-Referenz

Für ein isoliertes Zweikörpersystem mit Plummer-Softening epsilon:

```
v_c = sqrt(G*M*R^2 / (R^2 + epsilon^2)^(3/2))
v_esc = sqrt(2*G*M / sqrt(R^2 + epsilon^2))
```

Bei G=1, M=100, R=8 und epsilon=0.5:
- Kreisbahngeschwindigkeit v_c ≈ 3.52521 Modell-Einheiten pro Zeiteinheit
- Fluchtgeschwindigkeit v_esc ≈ 4.99513
- Verhältnis v_esc/v_c ≈ 1.41697

Das ist nur ein theoretischer Richtwert des isolierten weichgezeichneten Zweikörperpotentials. Im gestörten Mehrkörper-Encounter ist es keine globale Fluchtgrenze. Bei zwei Satelliten wird die Referenzwinkelgeschwindigkeit zusätzlich durch die gegenseitige Satellitenanziehung beeinflusst.

Die klassische Orbitalmechanik verknüpft Bahnform, Energie, Drehimpuls und Geschwindigkeit; bei gebundenen Bahnen ergeben sich Ellipsen, am Grenzfall der Gesamtenergie null eine Parabel und bei positiver Gesamtenergie eine Hyperbel (JPL, Fundamentals of Orbital Mechanics, Abschnitt 7.2.6–7.2.7).

## Pilotmessung: 80 deterministische Läufe

- Stoßparameter: b=29
- Varianten: ein oder zwei Satelliten pro Cluster
- Geschwindigkeitsfaktoren: k in {0.8, 1.0, 1.2, 1.4}
- Ein Satellit: 13 Phasen aus 0° bis 330° plus 46° wie im 13er-Raster.
- Zwei diametral gegenüberliegende Satelliten: sieben eindeutige Orientierungen modulo 180°: 0°, 30°, 46°, 60°, 90°, 120°, 150°.
- dt=0.002, bis zu 26.000 Integrationsschritte; bei erstem Kernkontakt wurde das Encounter-Kriterium ausgelöst.

### Kontaktzahlen

| Satelliten je Cluster | k=0,8 | k=1,0 | k=1,2 | k=1,4 |
|---:|---:|---:|---:|---:|
| 1 (13 Phasen) | 5/13 | 1/13 | 8/13 | 7/13 |
| 2 (7 Phasen modulo 180°) | 1/7 | 7/7 | 7/7 | 0/7 |

Bei zwei gegenüberliegenden Satelliten sind phi und phi+180° dieselbe geometrische Anordnung. Deshalb werden für die 2-Satelliten-Variante nur eindeutige Orientierungen gezählt. Die Symmetrie wird separat durch Regressionstests geprüft.

**Daten:** `results/orbit_speed_pilot_expanded_80_20261104.csv`

### Interpretation des Pilotlaufs

- **FACT:** Die paarweisen Gravitationskräfte bleiben in diesem Modell anziehend. Der Geschwindigkeitsfaktor ändert die Anfangsbewegung, nicht das Vorzeichen der Kraft.
- **MODEL:** Der Pilot ist ein dimensionsloses, klassisches N-Körper-Modell mit Plummer-Softening und Velocity-Verlet.
- **HYPOTHESIS:** Interne Rotation beeinflusst den Begegnungsverlauf phasenabhängig. Die 80 Läufe zeigen eine nicht-monotone Reaktion im getesteten Raster.
- **OPEN:** Ob die Kontaktmuster bei feinerer Phasenauflösung, weiteren b-Werten und kleineren Zeitschritten bestehen bleiben.

Die Kontaktzahlen sind Stichprobenanteile eines festgelegten endlichen Rasters bei genau einem Stoßparameter, keine universellen Kollisionswahrscheinlichkeiten. Für Kontaktfälle ist der gespeicherte Abstand der Abstand beim ersten Kontakt; für kontaktfreie Fälle ist er der minimale Abstand im Integrationsintervall.

## Wichtige Grenzen zur Chemie

Zwei Satelliten pro Kern sind kein Heliumatom; ein Satellit pro Kern ist kein Wasserstoffatom. Helium besteht aus zwei Protonen und zwei Elektronen. Seine 1s-Schale ist quantenmechanisch mit zwei Elektronen entgegengesetzten Spins gefüllt und folgt dem Pauli-Prinzip. Das Wasserstoffmolekül H2 ist eine quantenmechanische Molekülbindung mit Elektronendichte zwischen den Kernen. Im aktuellen Cluster-Modell gibt es keine elektrischen Ladungen, keinen Elektronenspin, kein Pauli-Prinzip und keine Wellenfunktionen. Die Analogie ist daher eine didaktische Bildidee und keine chemische Äquivalenz.

## Nächste Experimente

1. b in {28,29,30} und ein feineres k-Raster von 0.8 bis 1.45 in 0.05-Schritten untersuchen.
2. Ein- und Zwei-Satellitenvarianten mit eindeutig symmetrieverschiedenen Phasen vergleichen.
3. Grenzfälle mit dt=0.002, 0.001 und 0.0005 wiederholen.
4. Ausgewählte Läufe unabhängig mit REBOUND IAS15 vergleichen; IAS15 verwendet adaptive Zeitschritte und ist für enge Begegnungen geeignet.
5. Erst danach einen separaten, explizit künstlichen Satellit-Satellit-Abstoßungsterm testen. Bei Koeffizient null muss er exakt auf das reine Newton-Modell zurückfallen. Er darf nicht als aus Gravitation abgeleitet bezeichnet werden.

## Reproduzierbarkeit

- Sweep: `experiments/gravity_cluster_velocity_sweep.py`
- Regressionstests: `experiments/test_gravity_cluster_velocity_sweep.py`
- Rohdaten: `results/orbit_speed_pilot_expanded_80_20261104.csv`
- Browser-Regler: `web/GravityClusterResearchDashboard.html`
