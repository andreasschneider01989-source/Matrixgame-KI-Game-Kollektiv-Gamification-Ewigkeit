# Gravity Cluster Phase Sweep – Ergebnisse vom 04.11.2026

**Status:** 189/189 deterministische Läufe abgeschlossen. Modell-Evidenz, keine Naturkonstante.

## Review-Korrekturen
- Die korrekte Laufzahl ist 189: A (0 Satelliten) benötigt nur 1 irrelevante Phase je b: 7 Läufe; B und C verwenden 13 Phasen je b: 91 + 91. 273 würde fälschlich auch A mit 13 Phasen zählen.
- Der Gesamtimpuls ist im Schwerpunktssystem null. Impulsdrift darf deshalb nicht durch den Gesamtimpuls normiert werden; verwendet wird die Summe der Beträge der Einzelimpulse.
- Energie- und Drehimpulsänderung des Merger werden unmittelbar davor/danach gemessen, getrennt von späterer Satellitendynamik.
- Ein Hill-Radius ist für allgemeine ungebundene Begegnungen keine universelle Escape-Regel; Satellitenstatus bleibt vorläufig unklar.

## Entdeckte Initialisierungsfrage
Das HTML korrigiert den Kernoffset mit sum(m_sat*r_sat)/M_total; die exakte Schwerpunktbedingung verlangt den Nenner M_core. Außerdem ist die historische Winkelgeschwindigkeit nur nominal und im Plummer-geglätteten Mehrkörperpotential keine exakt stationäre Kreisbahn.

Der Sweep kennt zwei Modi:
- legacy_html: historische Anfangsbedingungen zur Reproduktion alter Anzeigen.
- isolated_equilibrium (Standard): Schwerpunkt exakt erhalten; isolierte Kreisbahnen werden im Plummer-Potential ausbalanciert. Die äußere Begegnung stört sie anschließend.

## Ergebnisse – corrected isolated_equilibrium
Parameter: G=1, ε=0,5, Gesamtmasse je Cluster 100, Satellitenmasse 4, R=8, Kernradius=4, Kontaktdistanz=8, Velocity-Verlet, dt=0,002, 26.000 Schritte.

| Variante | Kontakte | Rasterpunkte | Rasteranteil |
|---|---:|---:|---:|
| A – 0 Satelliten | 3 | 7 | 42,86 % |
| B – 1 Satellit | 37 | 91 | 40,66 % |
| C – 2 Satelliten | 52 | 91 | 57,14 % |

| b | A | B | C |
|---:|---:|---:|---:|
| 24 | 1/1 | 13/13 | 13/13 |
| 26 | 1/1 | 13/13 | 13/13 |
| 28 | 1/1 | 10/13 | 13/13 |
| 29 | 0/1 | 1/13 | 13/13 |
| 30 | 0/1 | 0/13 | 0/13 |
| 32 | 0/1 | 0/13 | 0/13 |
| 34 | 0/1 | 0/13 | 0/13 |

Bei b=29 liefert B unter legacy_html 8/13 Kontakte; unter isolated_equilibrium nur 1/13. Das zeigt Sensitivität gegenüber Anfangsbedingungen – keinen universellen Abschirmungseffekt.

## Numerische Kontrollen
Drei grenznahe Fälle wurden mit dt=0,002 / 0,001 / 0,0005 wiederholt. Die Kontaktklassifikation blieb in allen Fällen gleich. Die minimale Kernentfernung änderte sich nur um wenige Millionstel Einheiten.

Maximale Energieabweichung bei kontaktfreien Hauptläufen: A 1,33e-7; B 1,01e-6; C 3,15e-7. Relative Impulsdrift: höchstens ca. 1,8e-14 in B/C. Dies ist ein erster QC-Befund, kein vollständiger Konvergenznachweis des gesamten Parameterraums.

## Grenzen
- Fester Kernradius 4 bei veränderter Kernmasse ist eine Modellannahme; die Dichte ändert sich.
- Kernverschmelzung ist ein perfekt unelastischer Kollisions-Proxy, keine Kernfusion.
- Keine chemischen Bindungen, Elektronenorbitale oder Quantenmechanik.
- Rasteranteile sind keine universellen Kollisionswahrscheinlichkeiten.

## Nächste Schritte
1. Legacy-/Equilibrium-Schalter im Browser sichtbar machen.
2. dt-Konvergenz auf weitere Grenzfälle ausweiten.
3. Unabhängige Vergleichsrechnung, z.B. REBOUND IAS15.
4. Kernradien und Satellitenmassen getrennt variieren.
5. Phasenraster verfeinern und danach Mehrcluster-Experimente.
