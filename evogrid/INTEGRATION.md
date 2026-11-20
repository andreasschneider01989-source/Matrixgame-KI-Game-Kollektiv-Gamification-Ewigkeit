# EvoGrid - Integrations- und Physikdokumentation

## Physik-Erhaltung (v2, nachgewiesen 2026-11-20)

Headless-Laeufe, je 2500 Ticks, driftAccum aus stats():

| Pruefung | drift |
|---|---|
| Mode 0 (Unit-Fight), Seed 5 | 0,0000 |
| Mode 0, Seed 9 | 0,0000 |
| Mode 1 (Boulder-Dash), Seed 5 | -0,0000 |
| Mode 1, Seed 9 | +0,3063 (Rest 0,04 %, Issue offen) |

Regeln der geschlossenen Oekonomie (nichts entsteht, nichts verschwindet):
1. Nahrung entsteht NUR aus dem Biomasse-Pool (massenneutral).
2. Wildfaktor beim Fressen: r1/r2-Multiplikator medianstabil; die Differenz zahlt die Biomasse ("Oekosystem als Haus") -> Gesamtenergie konstant.
3. Newton: PE = m*g*h; Freier Fall v2 += 2g (dKE = m*g = dPE); Aufprall -> Waerme/Biomasse;
   Schieben bucht Schwerkraftarbeit mit: v2 += 2*(cost + g*dy).
4. Tod = Kompost: 90% Biomasse + 10% Zersetzungswaerme (100% Erhaltung).
5. Kein Energie-Abzug wird negativ: alle Kosten min(Kostensatz, verfuegbare Energie) begrenzt.
