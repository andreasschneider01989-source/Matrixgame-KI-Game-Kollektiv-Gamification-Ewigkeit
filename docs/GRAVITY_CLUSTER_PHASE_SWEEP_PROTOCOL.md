# Forschungsphase 2 – Phasen- und Stoßparameter-Sweep im Gravity Cluster Lab 2D

**Status:** Versuchsprotokoll / noch nicht als Sweep-Ergebnis ausgeführt  
**Version:** 0.1  
**Anknüpfung:** `web/Matrixgame_GravityClusterLab_2D.html` und `docs/GRAVITY_CLUSTER_LAB_2D.md`

## 1. Forschungsfrage

Verändert die innere Massenverteilung eines gravitativ wechselwirkenden Clusters – null, ein oder zwei Satelliten bei gleicher Gesamtmasse – den minimalen Kernabstand und die Häufigkeit eines definierten Kern-Kontakts während einer Begegnung zweier Cluster?

**Wichtig:** Wir testen keinen vorausgesetzten „Schutzschirm“. Alle Körper ziehen sich weiterhin nach dem verwendeten weichgezeichneten Newton-Modell an. Satelliten können Energie und Drehimpuls innerhalb eines Clusters umverteilen und damit die äußere Begegnung beeinflussen. Ob das in den gewählten Bedingungen systematisch geschieht, ist die offene Frage.

## 2. Bereits dokumentierte Ausgangsmessung (MODEL)

Der bisherige Einzelversuch verwendet:

- Gravitationskonstante (G=1), Softening (ε=0{,}5)
- Gesamtmasse je Cluster: 100 Simulationseinheiten
- Satellitenmasse: 4 je Satellit
- anfänglicher Satellitenradius: 8
- Kern-Kontaktgrenze: 8 (zwei Kernradien zu je 4)
- Zeitschritt: (Δt=0{,}002)
- Stoßparameter: (b=29)
- Phase des zweiten Ein-Satelliten-Clusters: 46°
- gleiche Cluster-Schwerpunkte und Schwerpunktgeschwindigkeiten nach der Initialisierung

| Satelliten je Cluster | Kernmasse | Satellitenmassen | minimaler Kernabstand | Kernkontakt |
|---:|---:|---:|---:|---|
| 0 | 100 | – | 8,397 | nein |
| 1 | 96 | 4 | 8,000 | ja |
| 2 | 92 | 4 + 4 | 8,043 | nein |

Diese drei Resultate sind ein einzelner Parameterschnitt, kein Nachweis eines allgemeinen Effekts. Insbesondere folgt daraus nicht, dass zwei Satelliten Kerne grundsätzlich abschirmen oder ein Satellit grundsätzlich Verschmelzung auslöst.

## 3. Nullhypothese und Alternativhypothese

- **H0:** Bei kontrollierter Gesamtmasse und Schwerpunktbewegung zeigt die Satellitenkonfiguration über den untersuchten Phasen- und Stoßparameterbereich keinen robusten systematischen Einfluss auf den Kernkontakt.
- **H1:** Die Satellitenkonfiguration verändert die Begegnungsdynamik messbar; der Effekt hängt von Phase und Stoßparameter ab.

Die Auswertung soll sowohl Fälle nennen, die H1 stützen, als auch Gegenbeispiele. Ein Ergebnis darf nicht nachträglich nur anhand passender Einzelfälle ausgewählt werden.

## 4. Primärer Sweep – klein, reproduzierbar, überschaubar

### Konfigurationen

A. 0 Satelliten je Cluster  
B. 1 Satellit je Cluster  
C. 2 Satelliten je Cluster, diametral gegenüberliegend

Gesamtmasse, Schwerpunktposition und Schwerpunktgeschwindigkeit bleiben zwischen A/B/C gleich. Die vorhandene Initialisierungslogik für Satellitenkorrekturen ist beizubehalten und mit Tests abzusichern.

### Parameterwerte

- Stoßparameter (b): 24, 26, 28, 29, 30, 32, 34
- relative Phase des zweiten Clusters: 0°, 30°, 60°, 90°, 120°, 150°, 180°, 210°, 240°, 270°, 300°, 330° sowie zusätzlich 46° als bestehender Referenzfall
- Beim 0-Satelliten-Fall ist die Phase irrelevant; daher nur ein Lauf pro Stoßparameter.
- Beim 1- und 2-Satelliten-Fall wird die Phase des ersten Clusters auf 0° fixiert und nur die relative Orientierung des zweiten Clusters verändert.

Damit entstehen im ersten Sweep 7 Läufe für 0 Satelliten sowie je 91 Läufe für 1 und 2 Satelliten, insgesamt **189 deterministische Läufe**. Äquivalente Orientierungen bei antipodalen Paaren dürfen später erkannt und als Symmetrieprüfung ausgewiesen werden; die Rohdaten sollen dennoch vollständig erhalten bleiben.

Alle übrigen Startwerte werden aus dem aktuellen Lab übernommen und pro Lauf vollständig im Manifest gespeichert. Werte, die im Code nicht eindeutig dokumentiert sind, dürfen nicht stillschweigend geraten werden.

## 5. Messgrößen und Klassifikation

### Primäre Messgrößen

1. Minimaler Abstand Kern A – Kern B.
2. Kernkontakt ja/nein nach bestehender Grenze (d_mathrm{core}leq8).
3. Simulationszeit bis zum ersten Kernkontakt.
4. Minimaler Abstand aller Körper zwischen den Clustern.
5. Endzustand und Zahl gebundener bzw. entkommener Satelliten nach einem möglichen Kern-Merger.

### Sekundäre Messgrößen

- Gesamtenergie und relative Energieabweichung in kollisionsfreien Läufen.
- Gesamtimpuls und Schwerpunktdrift.
- Gesamtdrehimpuls; bei Kern-Merger zusätzlich den durch die vereinfachte Zusammenfassung nicht mehr als Spin repräsentierten Drehimpulsverlust dokumentieren.
- Zeitpunkt und Energieänderung beim Merger.
- Satellitenstatus: gebunden, entkommen oder unklar.

Ein Satellit gilt nicht allein wegen eines großen Abstands als entkommen. Eine robuste spätere Klassifikation soll seine spezifische Energie relativ zum Remnant, seine Radialbewegung und einen dokumentierten Mindestabstand gemeinsam prüfen. Bis diese Klassifikation implementiert ist, den Status als „unklar“ kennzeichnen.

### Merger-Regel

Die bisherige Kern-Kern-Kontaktregel bleibt ein **perfekt unelastischer Kollisions-/Merger-Proxy**: Masse, Schwerpunktposition und linearer Impuls werden zusammengeführt; relative kinetische Energie wird dissipiert. Das Modell bildet weder Kernfusion noch starke Kernkraft oder chemische Bindung ab. Energie- und Drehimpulsbilanzen sind deshalb vor und nach einem Merger getrennt auszuweisen.

## 6. Numerische Qualitätskontrolle

1. Den vollständigen Sweep zunächst mit (Δt=0{,}002) ausführen.
2. Eine repräsentative Teilmenge – insbesondere Grenzfälle knapp an der Kontaktgrenze – mit (Δt=0{,}001) und (0{,}0005) wiederholen.
3. Minimalabstände, Kontaktklassifikation und Ereigniszeiten vergleichen.
4. Schwellennahe Ergebnisse als numerisch sensitiv markieren, falls die Klassifikation bei kleinerem Zeitschritt wechselt.
5. Kollisionfreie Läufe getrennt auf Energie-, Impuls- und Drehimpulsdrift prüfen.
6. Merger-Läufe mit expliziter erwarteter dissipativer Änderung ausweisen, statt Energieerhaltung nach dem Merger zu behaupten.

Für eine spätere unabhängige Gegenprüfung kann ein zweiter N-Körper-Solver eingesetzt werden. REBOUND dokumentiert beispielsweise IAS15 als adaptiven hochgenauen Integrator und WHFast als symplektischen Integrator für Systeme mit dominantem Zentralkörper. WHFast ist nicht die passende pauschale Wahl für beliebige enge Begegnungen; Integrator und Annahmen müssen zur jeweiligen Dynamik passen.

## 7. Ergebnisformat

Jeder Lauf soll mindestens diese Felder enthalten:

```json
{
  "schema_version": "1.0",
  "experiment": "gravity_cluster_phase_sweep",
  "model": "classical_newtonian_n_body_softened",
  "run_id": "C_b29_phase046_dt0002",
  "satellites_per_cluster": 1,
  "impact_parameter": 29,
  "relative_phase_deg": 46,
  "G": 1.0,
  "softening": 0.5,
  "dt": 0.002,
  "total_cluster_mass": 100,
  "satellite_mass": 4,
  "orbital_radius": 8,
  "core_contact_distance": 8,
  "seed": null,
  "min_core_distance": null,
  "core_contact": null,
  "first_contact_time": null,
  "energy_drift_pre_merger": null,
  "linear_momentum_drift": null,
  "angular_momentum_change_at_merger": null,
  "satellite_outcomes": [],
  "notes": []
}
```

`null` bedeutet: nicht gemessen oder nicht anwendbar – nicht null als Messwert. Zusätzlich werden Modellversion, Commit-SHA, exakte Anfangszustände aller Körper und Laufzeit gespeichert. Ein Zufalls-Seed ist bei vollständig deterministischen Läufen nicht erforderlich; falls Zufall eingeführt wird, ist er Pflicht.

Aus dem Sweep entstehen:
- CSV mit einer Zeile je Lauf;
- JSON-Manifeste und bei Bedarf Zustands-Snapshots;
- Karte ((b,φ)\rightarrow) minimaler Kernabstand und Kontaktstatus;
- Zusammenfassung mit Anzahl der getesteten Parameterkombinationen, nicht als universelle Kollisionswahrscheinlichkeit interpretiert.

## 8. Interpretation – was das Ergebnis aussagen darf

**Zulässig:** „Im untersuchten Parameterbereich änderte die Satellitenkonfiguration den minimalen Kernabstand / den Anteil der getesteten Phasen mit Kernkontakt.“

**Nicht zulässig:** „Zwei Satelliten schirmen Gravitation ab“, „die Struktur verhält sich wie Helium“, „ein Satellit verursacht Wasserstoffbindung“ oder „die Simulation zeigt Kernfusion“.

Die bisherige Analogie zur Chemie dient als motivierende Forschungsfrage. Echte H₂-Bindung erfordert Elektronen- und Protonenphysik inklusive Quantenmechanik; klassische Gravitation allein reicht dafür nicht.

## 9. Nächste Ausbaustufen nach dem Basissweep

1. Satellitenmassenanteil und Bahnradius variieren, weiterhin Gesamtmasse konstant halten.
2. Größere Phasenauflösung um Übergangsbereiche verwenden.
3. Mehr als zwei Cluster simulieren und Wechselwirkungen in einem Ensemble untersuchen.
4. Einen unabhängigen Integratorvergleich durchführen.
5. Erst danach optional zusätzliche Modellkräfte ergänzen – jede Kraft separat schaltbar, dokumentiert und mit eigenem Test.

Für reale Himmelskörper ist ein separater Validierungspfad vorgesehen: JPL Horizons liefert unter anderem kartesische Zustandsvektoren über seine API. Reale Einheiten, Koordinatensystem, Zeitskala und Referenzzentrum müssen dann ausdrücklich festgelegt werden. Diese Daten gehören nicht in den dimensionslosen Cluster-Test, sondern in einen eigenen Validierungsdatensatz.

## 10. Referenzen

- REBOUND IAS15: https://rebound.hanno-rein.de/integrators/ias15/
- REBOUND WHFast: https://rebound.hanno-rein.de/integrators/whfast/
- NASA/JPL Horizons API: https://ssd-api.jpl.nasa.gov/doc/horizons.html
- Bestehendes Experiment: `docs/GRAVITY_CLUSTER_LAB_2D.md`

---
**Leitprinzip:** FACT / MODEL / HYPOTHESIS / OPEN / FICTION unterscheiden. Rohdaten, Codeversion und Parameter bleiben nachvollziehbar. Erst messen, dann interpretieren.


---

## 11. Präzisierung nach unabhängigen Reviews und Implementierung (04.11.2026)

### Laufzahl
Der 189er-Rasterumfang ist: Variante A (0 Satelliten) hat keine relevante Satellitenphase und erhält daher 7 Läufe (ein Lauf je Stoßparameter). Varianten B und C erhalten jeweils 7 × 13 = 91 Läufe. Gesamt: 7 + 91 + 91 = 189. Die Rechnung 7 × 13 × 3 = 273 zählt für A redundante Phasenvarianten.

### Zwei Initialisierungsmodi
Der HTML-Prototyp bleibt als historische Referenz unter `legacy_html` verfügbar. Seine Kernoffset-Korrektur verwendet den Nenner Gesamtmasse. Für exakten Schwerpunkt ist jedoch der Nenner Kernmasse erforderlich. Der neue Standard `isolated_equilibrium` erhält den Schwerpunkt exakt und setzt isolierte Kreisbahnen passend zum Plummer-Softening auf. Die Begegnung zweier Cluster stört diese isolierten Gleichgewichte nach dem Start.

Die alten HTML-Bedingungen und die korrigierten isolierten Gleichgewichte sind unterschiedliche Modelleinstellungen; Ergebnisse dürfen nicht ohne Angabe des Modus vermischt werden.

### Zusätzliche Modellannahmen
- Der Kernradius bleibt bei 4 Einheiten, auch wenn die Kernmasse durch Satelliten abnimmt.
- Satelliten-„Flucht“ bleibt eine vorläufige Momentaufnahme-Klassifikation; endgültiger Status erfordert längere Mehrkörper-Nachbeobachtung.
- Ein Kern-Merger hat keinen eigenen Spin-Freiheitsgrad. Daher kann orbitaler Drehimpuls bei der vereinfachten Zusammenfassung verloren gehen und ist unmittelbar vor/nach dem Ereignis gesondert auszuweisen.

### Implementierung und Ergebnisse
Das ausführbare Skript liegt unter `experiments/gravity_cluster_phase_sweep.py`; Tests liegen daneben. Die Messzusammenfassung und Interpretationsgrenzen stehen in `docs/GRAVITY_CLUSTER_PHASE_SWEEP_RESULTS_20261104.md`. Der Hauptlauf umfasst 189/189 deterministische Läufe im Modus `isolated_equilibrium`. Drei grenznahe Fälle wurden mit dt=0,002 / 0,001 / 0,0005 geprüft; dort wechselte die Kontaktklassifikation nicht. Dies ist ein erster QC-Schritt, kein vollständiger Konvergenznachweis des gesamten Rasters.
