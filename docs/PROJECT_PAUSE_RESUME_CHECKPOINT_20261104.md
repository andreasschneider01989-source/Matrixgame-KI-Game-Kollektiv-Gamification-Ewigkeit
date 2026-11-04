# MATRIXGAME – Pausen- und Wiederaufnahmeprotokoll
**Stand:** 04.11.2026  
**Arbeitszweig:** `feature/evolution-forge-bridge`  
**Pull Request:** [#4 – Evolution Forge / Gravity Cluster research](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/pull/4)  
**Status:** offen, Draft, nicht in `main` gemergt.

> **Zweck dieses Dokuments:** Nach einer bewussten Pause ohne Kontextverlust weiterarbeiten. Dies ist der zentrale Einstiegspunkt; die verlinkten Quelldateien und Messdaten sind maßgeblich.

## 1. Projektziel und Leitlinien

Matrixgame / Kollektive KI-Games verbindet interaktive Simulation, offene Softwareentwicklung, wissenschaftliches Lernen, Evolution Forge, Spieltheorie und die Zusammenarbeit menschlicher und maschineller Intelligenz.

Leitprinzipien:
- Wissen und Code nachvollziehbar teilen.
- Erst rechnen und messen, danach interpretieren.
- FACT / MODEL / HYPOTHESIS / OPEN / FICTION ausdrücklich unterscheiden.
- Modelle nicht vermischen: klassische Gravitation, Elektromagnetismus, Quantenmechanik, Chemie und Kernphysik bleiben zunächst getrennte Modellschichten.
- Das Projekt soll Bildung, Kreativität, Zusammenarbeit und praktische Fähigkeiten fördern. Ein noch unbestätigter Forschungsdurchbruch wird nicht versprochen.

## 2. Was zum Pausenzeitpunkt vorhanden ist

### A. Evolution Forge und Java/Python
- Python-Simulationszweig mit mehreren astrobiologischen Szenarien.
- Java-Bridge, die einen Python-Prozess startet und JSON-Ergebnisse übernimmt.
- Dokumentation: [Evolution Forge Integration](EVOLUTION_FORGE_INTEGRATION.md).
- Die Abweichung des extern berichteten `icy_ocean v1.1`-Ergebnisses gegenüber v1.0 ist als offene Reproduzierbarkeitsfrage dokumentiert.

### B. Gravity Labs
- 2D Newtonian Infinity Canvas.
- 3D Gravity Lab mit x/y/z und vx/vy/vz, drei senkrechten Orbitalebenen, Editor und JSON-Import/Export.
- 2D Gravity Cluster Lab mit 0, 1 oder 2 Satelliten je Cluster, Begegnungs- und Doppel-Kernbahn-Modus.
- Die Kern-Kern-Zusammenfassung ist ein perfekt unelastischer Kollisions-/Merger-Proxy. Sie ist keine Kernfusion.

### C. Gravity Cluster Phase Sweep – 189 Läufe
Protokoll: [Phase Sweep Protocol](GRAVITY_CLUSTER_PHASE_SWEEP_PROTOCOL.md)  
Ergebnisse: [Phase Sweep Results](GRAVITY_CLUSTER_PHASE_SWEEP_RESULTS_20261104.md)  
Code: [gravity_cluster_phase_sweep.py](../experiments/gravity_cluster_phase_sweep.py)  
Tests: [test_gravity_cluster_phase_sweep.py](../experiments/test_gravity_cluster_phase_sweep.py)

Parameter des korrigierten Standardmodus `isolated_equilibrium`:
- 7 Stoßparameter: b = 24, 26, 28, 29, 30, 32, 34.
- 13 Phasen für 1- und 2-Satelliten-Varianten; bei 0 Satelliten ist die Phase irrelevant.
- 7 + 91 + 91 = 189 deterministische Läufe.
- G=1, Plummer-Softening epsilon=0.5, Gesamtmasse je Cluster=100, Satellitenmasse=4, R=8, Kernradius=4, Kontaktgrenze=8, Velocity-Verlet, dt=0.002, 26.000 Schritte.

Ergebnis im endlichen getesteten Raster:
| Variante | Kernkontakte | Rasterpunkte | Rasteranteil |
|---|---:|---:|---:|
| 0 Satelliten | 3 | 7 | 42,86 % |
| 1 Satellit | 37 | 91 | 40,66 % |
| 2 Satelliten | 52 | 91 | 57,14 % |

Diese Rasteranteile sind keine universellen Kollisionswahrscheinlichkeiten.

Wichtige Modellkorrektur:
- `legacy_html` erhält historische Startbedingungen zur Reproduktion.
- `isolated_equilibrium` ist Standard: Schwerpunkt exakt erhalten und isolierte Kreisbewegung im weichgezeichneten Potential ausbalanciert.
- Der historische HTML-Kernoffset verwendete die Gesamtmasse statt der Kernmasse; daher war der Schwerpunkt für Satellitenvarianten nicht exakt.
- Bei b=29, einem Satelliten und 13 Phasen ergab `legacy_html` 8/13 Kontakte, der korrigierte Modus 1/13. Die starke Sensitivität gegenüber Startbedingungen ist dokumentiert.

Erste numerische QC:
- Drei grenznahe Fälle wurden mit dt=0.002, 0.001 und 0.0005 wiederholt.
- In diesen drei Fällen wechselte die Kontaktklassifikation nicht.
- Das ist ein erster Spot-Check, keine vollständige Konvergenzprüfung aller 189 Läufe.

### D. Orbitgeschwindigkeits-Pilot – 80 Läufe
Notiz: [Orbit Speed Sweep](GRAVITY_CLUSTER_ORBIT_SPEED_SWEEP_20261104.md)  
Code: [gravity_cluster_velocity_sweep.py](../experiments/gravity_cluster_velocity_sweep.py)  
Tests: [test_gravity_cluster_velocity_sweep.py](../experiments/test_gravity_cluster_velocity_sweep.py)  
Rohdaten: [orbit_speed_pilot_expanded_80_20261104.csv](../results/orbit_speed_pilot_expanded_80_20261104.csv)

Der Faktor k verändert nur die internen Satellitengeschwindigkeiten relativ zur Cluster-Schwerpunktgeschwindigkeit:
`v_i_neu = V_COM + k * (v_i_alt - V_COM)`.
Die Schwerpunkt-Translation, Positionen, Massen und Newtonsche Kraftfunktion bleiben gleich. Es wird dadurch keine zusätzliche Abstoßungskraft eingebaut.

Pilot bei b=29, dt=0.002:
| Satelliten je Cluster | k=0,8 | k=1,0 | k=1,2 | k=1,4 |
|---|---:|---:|---:|---:|
| 1 (13 Phasen) | 5/13 | 1/13 | 8/13 | 7/13 |
| 2 (7 eindeutige Orientierungen modulo 180°) | 1/7 | 7/7 | 7/7 | 0/7 |

Das Verhalten ist im untersuchten Raster nicht monoton und phasenabhängig. Es ist noch kein allgemeines Gesetz. Bei zwei gegenüberliegenden Satelliten sind phi und phi+180° geometrisch äquivalent; der Pilot berücksichtigt diese Symmetrie.

## 3. Browser-Dashboard

Quellcode: [GravityClusterResearchDashboard.html](../web/GravityClusterResearchDashboard.html)

Vorhandene Funktionen:
- Import von CSV und JSON.
- Phasenkarte, Varianten-/b-Filter, Messwerttabelle.
- Auswahl eines einzelnen Messlaufs.
- Live-Replay mit Newtonscher N-Körper-Dynamik und Velocity-Verlet.
- Start/Pause, Einzelschritt, Tempoauswahl, Bahnspuren und optionale Geschwindigkeitsvektoren.
- Orbittempo-Regler k, der interne Geschwindigkeiten relativ zum Schwerpunkt skaliert.

Der Nutzer hat die Offline-Dashboard-Version geöffnet und die Phasenkarte erfolgreich geprüft. Das Replay wurde numerisch mit Referenzläufen verglichen. Der neue k-Regler benötigt noch einen gezielten manuellen UI-Test durch den Nutzer.

## 4. Wissenschaftliche Grenzen – unbedingt beibehalten

1. Alle Kräfte im aktuellen Cluster-Grundmodell sind gravitativ anziehend. Mehr interne Rotation bedeutet nicht Gravitationsabschirmung.
2. Ein Kern-Merger ist eine vereinfachte perfekt unelastische Kollision mit Dissipation; keine Kernfusion.
3. 1 oder 2 klassische Satelliten entsprechen nicht Wasserstoff- oder Heliumatomen.
4. Echte chemische Bindung erfordert elektrische Wechselwirkung und Quantenmechanik; Elektronenspin, Pauli-Prinzip und Orbitale sind im aktuellen Modell nicht enthalten.
5. Ein späterer künstlicher Satellit-Satellit-Abstoßungsterm wäre ein separates hypothetisches Modell. Bei Kopplung null muss er exakt das reine Newton-Modell ergeben.
6. Der Kernradius bleibt im vorhandenen Sweep bei 4, während die Kernmasse je nach Satellitenzahl variiert. Das ist eine bewusst beibehaltene Modellannahme und später separat zu testen.
7. Satelliten-Fluchtstatus ist noch nicht zuverlässig klassifiziert; entsprechende Hinweise sind keine bestätigten Escape-Ergebnisse.

## 5. Nächste Schritte nach der Pause

### Priorität 1 – bestehende Daten sichern und UI prüfen
- Den k-Regler im Browser testen: k=0.8, 1.0, 1.2, 1.4.
- Prüfen, dass der Regler nur interne Geschwindigkeiten ändert und die Schwerpunktbewegung konstant bleibt.
- Exportierte Versuchszustände und Screenshots bei Bedarf in GitHub ablegen.

### Priorität 2 – Geschwindigkeits-Sweep erweitern
- b = 28, 29, 30.
- k fein von 0.80 bis 1.45 in 0.05-Schritten.
- Ein- und Zwei-Satellitenfälle mit korrekter Phasensymmetrie getrennt auswerten.
- Grenzfälle mit dt = 0.002 / 0.001 / 0.0005 wiederholen.
- Nicht nur Kernkontakt, sondern minimalen Kernabstand, Kontaktzeit, Energie und Drehimpuls dokumentieren.

### Priorität 3 – unabhängiger Solver
- Ausgewählte Fälle gegen REBOUND IAS15 vergleichen.
- Erst nach Solververgleich robuste Aussagen über numerisch stabile Effekte formulieren.

### Priorität 4 – getrennte Kraftmodelle
- Nur wenn der reine Newton-Baselinefall abgesichert ist: optional einen explizit künstlichen Abstoßungsterm hinzufügen.
- Kopplungsparameter lambda=0 muss exakt das bisherige Modell reproduzieren.
- Kraftgesetz, Vorzeichen, Einheiten und Potential sauber dokumentieren; nicht als aus Gravitation abgeleitet bezeichnen.

### Priorität 5 – langfristige Verbindungen
- Mehrere Cluster und größere N-Körper-Ensembles.
- Reale astronomische Zustandsvektoren erst nach Validierung mit einem Referenzsolver.
- Eigenständiges Quanten-Wahrscheinlichkeitsmodell (Wellenfunktionen/Orbitale), nicht als klassische Gravitation tarnen.
- Evolution Forge und kollektive KI-Reviews als getrennte, aber verknüpfbare Forschungszweige weiterführen.

## 6. Kollektiver KI-Review

Review-Auftrag: [COLLECTIVE_AI_REVIEW_PROMPT_GRAVITY_CLUSTER.md](COLLECTIVE_AI_REVIEW_PROMPT_GRAVITY_CLUSTER.md)

Gemini- und Grok-Feedback wurde in die Protokolle eingearbeitet (Integrator-/Erhaltungsprüfungen, Escape-Kriterien, Phasensymmetrie, Reproduzierbarkeit). Weitere Modellantworten können später mit diesem Review-Auftrag eingeholt werden. Geteilte Chats übertragen nicht automatisch GitHub-Schreibrechte; jedes Modell braucht eine eigene autorisierte Verbindung für Änderungen.

## 7. Wiederaufnahme in einem neuen Chat

Diesen Abschnitt oder den Link zu diesem Dokument in den neuen Chat kopieren und sagen:

> „Bitte lies zuerst docs/PROJECT_PAUSE_RESUME_CHECKPOINT_20261104.md auf meinem Matrixgame-GitHub-Branch feature/evolution-forge-bridge. Wir setzen die Arbeit am Orbitgeschwindigkeits-Sweep und am Live-Dashboard fort. Prüfe zuerst den aktuellen GitHub-Stand, verändere keine bestehenden Messdaten und beginne mit dem nächsten offenen Schritt.“

**Letzter bekannter Projektstatus:** Pull Request #4 ist offen, Draft und nicht gemergt. Vor jeder Wiederaufnahme den aktuellen Branch-/PR-Stand erneut prüfen.

**Pausenregel:** Die Pause ist ausdrücklich Teil des Arbeitsprozesses. Messdaten und Ideen bleiben versioniert verfügbar; es ist nicht nötig, sofort weitere Experimente auszuführen.
