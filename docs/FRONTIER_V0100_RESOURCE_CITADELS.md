# Matrixgame Universal Grid Lab v0.10.0 — Resource Citadels & Level-2 Evolution

## Based on
- Full v0.9.0 Java source: `src/rhgr/MatrixgameUniversalGridLab_v09_0.java`
- New standalone file: `src/rhgr/MatrixgameUniversalGridLab_v10_0.java`
- Package `rhgr`; Java 17+; Swing; no third-party dependencies.
- v0.9.0 remains unchanged.

## New gameplay loop
1. A CREATURE collects FOOD. Food still increases energy as before.
2. Each collected food item additionally yields one abstract biomass token in `inventoryResources`.
3. A unit carries up to 12 tokens.
4. A unit can deposit one token into a neighbouring BUILDING of the same team using the existing DEPOSIT action.
5. The building stores biomass in `storedResources` (capacity 48).
6. At 12 stored tokens, a citadel can produce one Level-2 CREATURE.
7. At 24 stored tokens, a citadel can split its stock 50/50 and create a same-team daughter citadel.
8. The building controller alternates replication and elite production so a stocked citadel does not only split forever.

The biomass token is a game resource abstracted from food collection; it does not represent a calibrated physical mass.

## Level-2 unit
- Type remains `CREATURE` so the existing evolution, population and lineage systems continue to work.
- `unitLevel=2`, 9 HP, 3 attack damage, 18 starting energy, improved food efficiency and wider vision.
- Team, colour, parent building and next generation are inherited.
- When a Level-2 unit divides, its children inherit Level 2 and stronger bounded stats.
- Ordinary cell division remains available and unchanged for Level-1 units.

## Survival-first policy
The genetic action evaluator now follows these priorities:
- urgent food collection and threat avoidance;
- reproduction when energy reaches the division threshold;
- deposit biomass when carrying resources and not urgently hungry;
- optional construction only when a survival reserve remains, food is not urgent, and no nearby team citadel already serves the unit.

The old BUILD action and the 49-action catalogue remain intact. The change reduces the score of optional BUILD rather than removing the ability to build.

## World scale
- Default evolution grid: 160 × 100.
- Six teams, four starting cells per team, six distinct colours.
- 300 starting food items, food cap 600, 5 stochastic regeneration attempts per tick.
- Population history and graph expanded to six teams.

## Regression tests added
- Food collection yields energy and one biomass token.
- Same-team adjacent deposit transfers exactly one token.
- Citadel split at 24 conserves resources and inherits team.
- Citadel produces a stronger Level-2 unit at 12 resources.
- A feasible reproduction action outranks optional BUILD in a survival-sensitive context.
- Existing tests remain in the same self-test suite.

## Build and run
In Eclipse, save the full class under `src/rhgr/MatrixgameUniversalGridLab_v10_0.java` and run `rhgr.MatrixgameUniversalGridLab_v10_0`.

Headless verification:
```bash
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v10_0.java
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v10_0 --self-test
```

GitHub Actions workflow: `.github/workflows/matrixgame-v100-java.yml`. The status is only considered verified after the job completes successfully.
