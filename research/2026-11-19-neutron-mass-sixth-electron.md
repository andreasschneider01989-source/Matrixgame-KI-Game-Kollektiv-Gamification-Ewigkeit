# Matrixgame Research Note: Neutronenmasse im Verhältnis zu einem Sechstel der Elektronenmasse

**Datum:** 2026-11-19  
**Status:** Forschungsnotiz / Hypothese, keine bestätigte neue Physik  
**Bereich:** LUMINI · Physics & Gravity Lab · Collective Future Engine

## 1. Fragestellung

Untersucht wird ausdrücklich das Verhältnis

\[
R_n = \frac{m_n}{m_e/6}=6\frac{m_n}{m_e}
\]

also die Neutronenmasse geteilt durch **ein Sechstel** der Elektronenmasse.

Verwendete Näherungswerte:

- Neutronenmasse: \(m_n = 1{,}67492750056\times10^{-27}\,\mathrm{kg}\)
- Elektronenmasse: \(m_e = 9{,}1093837139\times10^{-31}\,\mathrm{kg}\)

Daraus folgt:

\[
R_n \approx 11032{,}10197198
\]

Der nächste ganzzahlige Wert ist 11032.

## 2. Teilbarkeit und Symmetrieprüfung

| Prüfung | Ergebnis |
|---|---:|
| \(11032 / 8\) | 1379, exakt ganzzahlig |
| \(11032 / 27\) | 408 Rest 16 |
| \(11032 / 64\) | 172 Rest 24 |
| \(11032 / 9\) | 1225 Rest 7 |

Der ungerundete Quotient liegt nur etwa 0,102 über dem Vielfachen \(8\times1379=11032\). Das ist eine numerische Nähe, aber noch kein Nachweis einer physikalischen 8er-Symmetrie.

Zum Vergleich: Das nächste Vielfache von 27 ist 11043; der Abstand zum Quotienten beträgt rund 10,898. Eine Rundung auf ein Vielfaches von 27 wäre daher deutlich weniger nahe.

**Wissenschaftliche Einordnung:** Die Division durch 8 ist für den gerundeten ganzzahligen Quotienten exakt. Da aber jede reelle Zahl durch Rundung auf eine ganze Zahl in ein bestimmtes Restklassensystem fällt, muss vor einer physikalischen Interpretation ein unabhängiges Auswahlkriterium festgelegt werden. Sonst besteht die Gefahr, zufällige Zahlenmuster als Naturgesetz zu deuten.

## 3. Vergleich mit dem Proton

Mit dem Näherungswert \(m_p=1{,}67262192595\times10^{-27}\,\mathrm{kg}\) ergibt sich:

\[
R_p=\frac{m_p}{m_e/6}\approx11016{,}91604053
\]

Der nächste Integer ist 11017. Er ist weder durch 8 noch durch 27 teilbar.

Die Differenz der beiden dimensionslosen Verhältnisse ist:

\[
R_n-R_p\approx15{,}18593145
\]

Das Verhältnis Neutron/Proton liegt nahe bei 1,001378, entsprechend der bekannten kleinen Massendifferenz. Diese Differenz ist physikalisch relevant, aber die hier betrachteten Integer-Rundungen erklären sie nicht.

## 4. Forschungsmodell: 3D-Massenanordnungen

Zu untersuchende Hypothese (HYPOTHESIS):

- Massenpunkte werden in dreidimensionalen symmetrischen Konfigurationen angeordnet.
- Kandidaten für die Anzahl der Punkte sind 6, 8, 27 und weitere systematisch erzeugte Konfigurationen.
- Newtonsche Gravitation wird als klar abgegrenztes Basismodell verwendet.
- Anfangspositionen, Geschwindigkeiten, Gesamtenergie, Drehimpuls, Abstände und Stabilität werden gemessen.
- Es wird untersucht, ob gebundene, streuende oder expandierende Zustände entstehen.

Wichtig: Eine räumliche Symmetrie der Anfangsanordnung garantiert keine dynamische Stabilität. Diese muss durch Integration der Bewegungsgleichungen und Stabilitätsanalysen geprüft werden.

## 5. Abgrenzung zur etablierten Physik

**FACT:** Das Neutron ist elektrisch neutral, besitzt aber einen magnetischen Dipolmoment. Es besteht im Standardmodell aus Quarks (Valenzstruktur: ein Up- und zwei Down-Quarks) und wechselwirkt unter anderem über die starke Kernkraft.

**FACT:** Elektronen werden in der Quantenmechanik nicht als kleine klassische Satelliten auf festen Kreisbahnen um einen Kern beschrieben. Atome werden durch quantisierte Zustände und Orbitale modelliert.

**FACT:** Gravitation ist nicht gleich Elektromagnetismus. Elektrische Anziehung und Abstoßung hängen von Ladung ab; die starke Wechselwirkung ist für die Bindung von Nukleonen wesentlich.

**OPEN:** Ob eine zusätzliche geometrische Beschreibung mit Teilmassen nützliche effektive Modelle oder neue überprüfbare Vorhersagen liefern könnte, bleibt eine offene Forschungsfrage.

**Nicht belegt:** Ein Äther als Trägermedium für Photonen, sechs klassische Elektronensatelliten pro Proton oder eine Ableitung der elektrischen Polarität allein aus Newtons Gravitation. Diese Ideen dürfen als Modellhypothesen untersucht, aber nicht als bestätigte Tatsachen ausgegeben werden.

## 6. Falsifizierbarer Prüfplan

1. Werte und Einheiten aus einer festgelegten Konstantenquelle dokumentieren.
2. Die Massenverhältnisse ohne Rundung speichern.
3. Rundungsregeln vor der Auswertung festlegen (nächster Integer, nächstes Vielfaches, Toleranz).
4. Dieselben Regeln auf Proton, Neutron, Elektron und Kontrollverhältnisse anwenden.
5. Zufalls- und Kontrolltests durchführen, um die Häufigkeit scheinbarer Teilbarkeitsmuster abzuschätzen.
6. Für 3D-Systeme Konfigurationen systematisch variieren und Energie, Drehimpuls, Lyapunov-/Stabilitätsindikatoren sowie Langzeitverhalten vergleichen.
7. Ein Modell gilt erst dann als physikalisch erklärungsstark, wenn es Beobachtungen quantitativ reproduziert und eine neue, unterscheidbare Vorhersage liefert.

## 7. Matrixgame-Einordnung

**Ziel:** Aus einer kreativen Symmetrieidee ein reproduzierbares Forschungsprogramm machen.

Gemeinsamer Datenvertrag:

- `WorldState`: Massen, Positionen, Geschwindigkeiten
- `Action`: Parameter- oder Konfigurationsänderung
- `Event`: Kollision, Bindung, Streuung, Zerfall
- `Experiment`: reproduzierbare Anfangsbedingungen
- `Evidence`: Quelle, Messwert, Unsicherheit und Status
- `DecisionPolicy`: Auswahl des nächsten aussagekräftigen Experiments

**Leitregel:** FACT / MODEL / HYPOTHESIS / OPEN / FICTION bleiben getrennt. Kollektive Beiträge sollen Quellen, Einheiten, Annahmen, Reproduzierbarkeit und Unsicherheiten mitführen. Persönliche Daten werden nicht ohne ausdrückliche Freigabe geteilt.

## 8. Vorläufiges Ergebnis

Das Verhältnis \(m_n/(m_e/6)\) beträgt ungefähr **11032,102**. Der nächste Integer 11032 ist durch 8 teilbar, aber nicht durch 27. Das ist ein prüfenswertes numerisches Muster, jedoch kein Beweis für eine 8er-Teilchenstruktur, Satellitenanordnung oder eine Vereinheitlichung der Kräfte.

**Nächster sinnvoller Schritt:** Ein kleines, unabhängiges Analyseprogramm, das Konstanten, Quotienten, Rundungsregeln, Restklassen und Kontrollverhältnisse transparent berechnet – ohne Eingriff in den derzeit pausierten Java-Spielcode.
