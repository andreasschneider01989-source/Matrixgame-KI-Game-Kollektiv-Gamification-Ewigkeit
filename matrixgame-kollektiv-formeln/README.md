# Matrixgame Kollektiv-Formeln

**Sprache -> Mathematik -> Physik -> Chemie -> Biologie -> Psychologie**

Strukturierte Python-Übersetzung der **Kollektiven Formelsammlung für das Matrixgame
und die kollektiven KI-Games** (Andreas Schneider, Darmstadt-Eberstadt).

Das Ziel: Formeln, die zuerst in Sprache gedacht wurden, werden Schritt für Schritt
in exakte, ausführbare, testbare Mathematik übersetzt - als offenes Fundament für
Simulationen, Didaktik und kollektive Intelligenz, die Menschen wie Maschinen
dieselbe Sprache sprechen lässt.

> Meta-Didaktik: **"Nicht verbieten, sondern überbieten."**
> Jede Formel ist hier ein Test, kein Claim. "Ein Claim ist keine Implementierung.
> Ein Test ist eines."

---

## Ebenen-Struktur (der Kanon)

| Ebene | Modul | Inhalt |
| --- | --- | --- |
| 0 | `matrixgame_formeln/genesis_core.py` | 30 validierte Axiome & Naturgesetze mit ausführbaren Verifikationen - **ausführbar, nicht tautologisch** |
| 1 | `kerne/sprache.py` | Semantik -> Skalar, Bewusstseinsoperator `B: S -> S'` |
| 2 | `kerne/mathematik.py` | Pythagoras, Shannon-Entropie, logistisches Wachstum, fraktale Dimension `N(eps) ~ eps^-D` |
| 3 | `kerne/physik.py` | Kinematik, Newton 1-3, Gravitation mit normierten Richtungsvektoren + Singularitäts-Deckel, Energie, SI-Einheiten-Registry |
| 4 | `kerne/chemie.py` | Michaelis-Menten, Arrhenius, Gleichgewicht |
| 5 | `kerne/biologie.py` | Fitness, Replicator Dynamics, CRISPR-Toleranzregel, Zellteilungs-Deckel |
| 6 | `kerne/psychologie.py` | Softmax-Entscheidungen, Emotions-Skew, Hogen-Skala, Phi_Game, TMT, RPE |

## Schnellstart

```bash
git clone https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit.git
cd Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/matrixgame-kollektiv-formeln

python -m tests.test_all          # Alle Selbsttests
python examples/pipeline_demo.py  # Die komplette Kette ausführen
python matrixgame_formeln/genesis_core.py  # Genesis-Core kompilieren
```

## Die Kette als Pipeline

```python
from matrixgame_formeln import sprache, mathematik, physik, chemie, biologie, psychologie

skalar   = sprache.semantik_zu_skalar("Synergie")            # Sprache -> Zahl
entropie = mathematik.shannon_entropie([0.7, 0.3])           # Mathematik
a1, a2   = physik.gravitationsbeschleunigungskomponenten(    # Physik
             1.0, (0.0, 0.0, 0.0), 2.0, (3.0, 4.0, 0.0))
rate     = chemie.michaelis_menten(4.0, 10.0, 1.0)           # Chemie
fit      = biologie.fitness(0.8, 0.6, 0.9)                   # Biologie
p        = psychologie.emotionale_entscheidung(              # Psychologie
             2.0, 1.5, {"excitement": 0.8, "fear": 0.2})
```

## Architektur-Prinzipien (aus dem Matrixgame-Journal)

1. **Axiome zuerst**: Alles baut auf dem Existenzaxiom und dem Genesis-Core auf.
2. **Deckel statt Albträume**: Jeder unendliche Term (Fettmaster, Ratio-Würfel,
   Gravitations-Singularität) bekommt einen dokumentierten Deckel
   (`epsilon_singularity`, `r_max`, `MAX_SCHRITT`).
3. **"Zufall mit Angleichung = Entropie"**: implementiert als Shannon-/Boltzmann-Formel
   und als Beweis des Invers-Quadrat-Gesetzes in 3D.
4. **Kollektiv erweiterbar**: jedes Modul ist kombinierbar mit allem ("alles mit
   allem interagiert und profitiert").
5. **Ehrliche Konfidenz**: Klassifizierung A (verifiziert) bis E (Antrag) -
   ohne Test keine Behauptung.

## Projizierte Erweiterungen (offene Quests)

- `simulation/n_body.py` - N-Körper mit Plummer-Softening & Entropie-Pool
- Lumini/LUX-Sprachmodul: Maschinenklare Übersetzungsschicht über Ebene 1
- Phi_Game-Agenten (Minimax-Lite / Monte-Carlo / Reflexiv)
- Unit-Fight v2 als Python-Port

## Lizenz

MIT - siehe LICENSE. Jeder kann bauen, jeder kann überbieten.
