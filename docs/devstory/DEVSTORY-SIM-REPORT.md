# DEVSTORY SIM v1.0 - Entwicklungsrekonstruktion & Prognose
Evolution Update 004. Kombination: KI-Vorlesungen (Q83: Q-Learning, VPI, Admissibility), SE-Vorlesungen (Q84: Design-by-Contract, SRP, Coverage, Least Commitment) + das reale Matrixgame-Archiv (Q18-Q84).

## Was das Modul macht

**Modus REKO** - Rekonstruiert die ECHTE Quest-Historie (12 Archiv-Eintraege) ueber eine admissible Audit-Heuristik (compiled 0.9 + Tests 0.25 + Reviews 0.15) und prueft die Vorhersage gegen die dokumentierten Evidenzklassen. Ergebnis: **11/12 korrekt**. Die eine Ausnahme ist ehrlich dokumentiert: Q32 (Ratio-Wuerfel) ist Klasse A wegen Quellcode-Evidenz, nicht wegen Coverage - ein Evidenztyp, zu dem die Heuristik bewusst keinen Zugang hat.

**Modus PROGNO** - Ein Q-Learning-Agent (MENACE-Prinzip, KI-Kap.15) lernt ueber 20000 Episoden die optimale Entwicklungsreihenfolge aus 3 Aktionen: TEST (Kosten 2), REVIEW (Kosten 5), COMPILE (Terminal). Erfolgswahrscheinlichkeiten sind aus der realen Compile-Queue geschaetzt (ohne Vorbereitung 10 %, mit Tests+Reviews 92 %).

## Verifizierte Zahlen (Sandbox-MiniRng exakt gespiegelt, Park-Miller Seed 42)
| Test | Ergebnis |
|---|---|
| T1 LCG-Determinismus | first3 = 0.0009440733..., 0.5713628640..., 0.2568094233... |
| T2 Gelernte Policy | S0 -> TEST, S1 -> TEST, S2 -> REVIEW, S3 -> COMPILE |
| T3 Rekonstruktion | 11/12 Evidenzklassen korrekt rekonstruiert |
| T4 VPI-Gate | VPI(perfekte Info, S3) = 0.8 exakt; TEST-then-COMPILE (89.2) dominiert COMPILE-now (28.5) |
| T5 Monte-Carlo S3 | Erfolgsrate 0.917 (N=10000, tol 0.02) |
| T6 Least Commitment | 2 gueltige Linearisierungen (TEST/REVIEW frei, beide vor COMPILE) |

## Die drei Lernsaetze
1. **"Teste vor dem Compile" ist keine Regel - es ist GELERNT.** Der Agent lernt aus Belohnungs-Fehlschlaegen allein, dass COMPILE mit Tests+Reviews 92 % gewinnt und ohne Vorbereitung nur 10 %.
2. **VPI = 0.8: Information ueber einen drohenden Compile-Fehler ist exakt so viel wert, wie du durch Skippen sparst** (Info kann nie schaden, aber ohne Handlungswechsel ist sie null wert).
3. **Least Commitment bleibt auch beim Lernen bestehen**: Der Agent entscheidet TEST vs. REVIEW nicht vorab, sondern je Zustand. Zwei oder drei gelernte Regeln, nicht eine Lawine.

## Compile (Evidenzklasse-A-Upgrade)
    javac src/devstory/MatrixgameDevStorySim.java
    java devstory.MatrixgameDevStorySim TEST   # 6/6 Tests gruen
    java devstory.MatrixgameDevStorySim DEMO   # Reko-Log + Prognose

**Q-K9 "Der DevStory-Compile"** = 18. Kandidat der Compile-Queue. Nach erfolgreichem Compile gilt der Baustein als Klasse A (Vorlage: Q18, das als einziges echtes Guardian-Kompilat den Status A traegt).

## Architektur-Anschluss
Das Modul nutzt SE-Vorlesung direkt: SRP (eine Klasse, eine Aufgabe: die Prognose), Design-by-Contract (require-Invariante: 0 < state < 2^31-1), Dijkstra-Testregel (Tests zeigen Anwesenheit von Fehlern, niemals Abwesenheit - deshalb bleibt bis zum Compile alles Klasse C) und Least Commitment (T6).
