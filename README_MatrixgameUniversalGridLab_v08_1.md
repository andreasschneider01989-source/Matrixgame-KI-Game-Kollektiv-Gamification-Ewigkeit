# Matrixgame Universal Grid Lab v0.8.1

Basis: v0.8 Expanded Genetic Sandbox. This is a deliberately small compatibility update.

## Changes
- Click any controllable unit to select it.
- The **Ausgewählte Unit: KI / MENSCH** switch affects only that selected unit.
- In **MENSCH** mode, the unit is exclusively player-controlled. With no manual input it stays idle; its AI is never used as a fallback.
- All other units retain their existing AI controllers.
- Boulder Dash starts with the player in MENSCH mode.
- Evolution starts with all creatures AI-controlled; individual creatures can be taken over.
- Evolution clock defaults to 500 ms/tick (2 ticks/s), adjustable from 100 to 2000 ms/tick. G/B remain supported.

## Preserved
120×80 world, camera and Infinity zoom, 37 actions, central resolver, interactions, genetic/evolution mechanics and resource rules.

## Build (Java 17+)
```powershell
javac --release 17 -Xlint:all -Werror -d out src\matrixgame\universal\MatrixgameUniversalGridLab_v08_1.java
java -cp out matrixgame.universal.MatrixgameUniversalGridLab_v08_1
java -Djava.awt.headless=true -cp out matrixgame.universal.MatrixgameUniversalGridLab_v08_1 --self-test
```

## Verification
Strict Java 17 compile passed. 60 headless self-tests passed (57 in v0.8, plus 3 exclusive-control regression checks). GUI startup smoke test under Xvfb passed until intentional timeout; no claim of a full interactive Windows playtest.

The speed slider controls automatic Evolution. Boulder Dash remains turn-based as in v0.8: each manual action advances one world tick.
