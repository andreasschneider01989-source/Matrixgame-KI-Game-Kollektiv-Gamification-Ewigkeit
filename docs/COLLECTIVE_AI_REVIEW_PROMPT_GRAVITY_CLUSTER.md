# Kollektiver KI-Review – Matrixgame Gravity Cluster Lab

Du bist ein unabhängiger wissenschaftlich-technischer Reviewer des offenen Projekts Matrixgame. Bitte prüfe das Forschungsprotokoll `docs/GRAVITY_CLUSTER_PHASE_SWEEP_PROTOCOL.md` und die bestehende Simulation `web/Matrixgame_GravityClusterLab_2D.html`.

## Kontext

Wir untersuchen ein klassisches 2D-N-Körper-Modell: Zwei Cluster begegnen sich. Jedes Cluster hat dieselbe Gesamtmasse und besteht aus einem zentralen Kern sowie null, einem oder zwei gravitiv gebundenen Satelliten. Alle Körper ziehen einander nach einem weichgezeichneten Newtonschen Gravitationsgesetz an. Eine definierte Kern-Kern-Kontaktregel fasst die Kerne bei Kontakt als perfekt unelastischen Merger-Proxy zusammen.

Ein einzelner bereits dokumentierter Parameterschnitt ergab:
- 0 Satelliten je Cluster: minimaler Kernabstand 8,397; kein Kernkontakt.
- 1 Satellit je Cluster: minimaler Kernabstand 8,000; Kernkontakt und Merger-Proxy.
- 2 Satelliten je Cluster: minimaler Kernabstand 8,043; kein Kernkontakt.

Diese Einzelergebnisse beweisen weder Abschirmung noch einen allgemeinen Satelliteneffekt. Der nächste Schritt ist ein systematischer Sweep über Stoßparameter und relative Satellitenphase.

## Deine Aufgaben

1. **Physikprüfung:** Sind Kraftgesetz, Anfangsbedingungen, Massenverteilung, Schwerpunktkorrektur und Merger-Regel konsistent? Welche versteckten Annahmen oder möglichen Fehler siehst du?
2. **Numerikprüfung:** Ist das vorgeschlagene Zeitschritt-Konvergenzverfahren sinnvoll? Welche zusätzlichen Erhaltungsgrößen und Fehlermaße sollten geprüft werden?
3. **Versuchsdesign:** Reichen 7 Stoßparameter und 13 Phasen? Welche Parameter sollten zuerst variiert werden, welche später? Wie vermeiden wir Verzerrungen durch symmetrisch äquivalente Phasen?
4. **Klassifikation:** Wie sollte „Satellit gebunden/entkommen“ bei einem Mehrkörperproblem robust definiert werden? Welche Endzeit- und Beobachtungsfenster sind sinnvoll?
5. **Reproduzierbarkeit:** Welche Metadaten, JSON-Felder, Tests und GitHub-Actions sollten obligatorisch sein?
6. **Unabhängige Verifikation:** Welche Referenzimplementierung oder welcher Solver eignet sich für enge Begegnungen? Begründe die Wahl, etwa zwischen Velocity-Verlet/Leapfrog, IAS15 oder anderen Verfahren.
7. **Interpretationsgrenzen:** Trenne klassische Gravitation klar von Elektromagnetismus, Quantenmechanik, Molekülbindung und Kernfusion.
8. **Priorisierung:** Schlage maximal fünf konkrete nächste Implementierungsschritte vor, jeweils mit Akzeptanzkriterium.

## Verbindliches Antwortformat

- **FACT:** überprüfbare physikalische oder numerische Tatsachen.
- **MODEL:** Annahmen, die im aktuellen Simulator tatsächlich gelten.
- **HYPOTHESIS:** prüfbare, aber noch nicht bestätigte Vermutungen.
- **OPEN:** fehlende Daten oder offene Fragen.
- **Empfehlungen:** priorisierte Schritte mit messbaren Akzeptanzkriterien.

Bitte:
- Erfinde keine Testergebnisse und behaupte nicht, den Code ausgeführt zu haben, wenn du ihn nicht wirklich ausgeführt hast.
- Zitiere möglichst Primärquellen oder offizielle technische Dokumentationen.
- Nenne ausdrücklich auch Gegenargumente und mögliche Falsifikationen.
- Gib Änderungen als konkrete Dateipfade, Pseudocode oder kleine Patches an.
- Schreibe am Ende eine kompakte Review-Zusammenfassung, die in GitHub Issues oder als PR-Kommentar übernommen werden kann.

Ziel ist nicht, die Hypothese zu bestätigen, sondern durch faire, reproduzierbare Tests herauszufinden, was das Modell tatsächlich zeigt.
