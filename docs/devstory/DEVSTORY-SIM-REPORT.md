# DEVSTORY SIM v1.1 - Entwicklungsrekonstruktion & Prognose
Evolution Update 004. Kombination: KI-Vorlesungen (Q83: Q-Learning, VPI, Admissibility), SE-Vorlesungen (Q84: Design-by-Contract, SRP, Coverage, Least Commitment) + das reale Matrixgame-Archiv (Q18-Q84).

## Was das Modul macht

**Modus REKO** - Rekonstruiert die ECHTE Quest-Historie (12 Archiv-Eintraege) ueber eine admissible Audit-Heuristik (compiled 0.9 + Tests 0.25 + Reviews 0.15) und prueft die Vorhersage gegen die dokumentierten Evidenzklassen. Ergebnis: **11/12 korrekt**. Die eine Ausnahme ist ehrlich dokumentiert: Q32 (Ratio-Wuerfel) ist Klasse A wegen Quellcode-Evidenz, nicht wegen Coverage - ein Evidenztyp, zu dem die Heuristik bewusst keinen Zugang hat.

**Modus PROGNO** - Ein Q-Learning-Agent (MENACE-Prinzip, KI-Kap.15) lernt ueber 20000 Episoden die optimale Entwicklungsreihenfolge aus 3 Aktionen: TEST (Kosten 2), REVIEW (Kosten 5), COMPILE (Terminal). Erfolgswahrscheinlichkeiten sind aus der realen Compile-Queue geschaetzt (10 % ohne Vorbereitung, 92 % mit Tests+Reviews).

## Verifizierte Zahlen (Sandbox-Spiegelung, Park-Miller Seed 42, frischer Stream pro Test)
| Test | Ergebnis |
|---|---|
| T1 LCG-Determinismus | first3 = 0.0009440733..., 0.5713628640..., 0.2568094233... |
| T2 Gelernte Policy | S0 -> REVIEW, S1(R fertig) -> TEST, S2(T fertig) -> REVIEW, S3(TR) -> COMPILE |
| T3 Rekonstruktion | 11/12 Evidenzklassen korrekt rekonstruiert |
| T4 VPI-Gate | VPI(perfekte Info, S3) = 0.8 exakt; TEST-then-COMPILE (89.2) dominiert COMPILE-now (28.5) |
| T5 Monte-Carlo S3 | Erfolgsrate 0.9198 exakt (N=10000, Seed 42) |
| T6 Least Commitment | 2 gueltige Linearisierungen (TEST/REVIEW frei, beide vor COMPILE) |

## Die drei Lernsaetze
1. **"Tue immer den fehlenden Schritt" ist GELERNT, nicht deklariert.** Der Agent lernt aus Belohnungen allein: fehlende Tests -> TEST, fehlendes Review -> REVIEW, beides vorhanden -> COMPILE. Das ist die gelernte Version von Least Commitment.
2. **VPI = 0.8: Information ueber einen drohenden Compile-Fehler ist exakt so viel wert, wie du durch Skippen sparst** (Info kann nie schaden, VPI >= 0 - aber ohne Handlungswechsel ist sie null wert).
3. **Der Stream entscheidet mit**: dieselbe Logik, geringfuegig anderer Seed-Stream ergab die Policy-Marke [[REVIEW zuerst]] statt [[TEST zuerst]] - sowohl deterministisch reproduzierbar als auch ein Warnschild: Prognosen brauchen deklarierten Seed und Stream-Anordnung (Z9-Lektion der eigenen Runde).

## Compile (Evidenzklasse-A-Upgrade)
    javac src/devstory/MatrixgameDevStorySim.java
    java devstory.MatrixgameDevStorySim TEST   # 6/6 Tests gruen
    java devstory.MatrixgameDevStorySim DEMO   # Reko-Log + Prognose

**Q-K9 "Der DevStory-Compile"** = 18. Kandidat der Compile-Queue. Nach erfolgreichem Compile gilt der Baustein als Klasse A (Vorlage: Q18, das als einziges echtes Guardian-Kompilat den Status A traegt).

## Architektur-Anschluss
Das Modul nutzt die SE-Vorlesung direkt: SRP (eine Klasse, eine Aufgabe), Design-by-Contract (require-Invariante: 0 < state < 2^31-1), Dijkstra-Testregel (Tests zeigen Anwesenheit von Fehlern, niemals Abwesenheit - deshalb bleibt bis zum Compile alles Klasse C) und Least Commitment (T6).
