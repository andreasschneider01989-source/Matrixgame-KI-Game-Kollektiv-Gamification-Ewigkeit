# Gravity Cluster Lab 2D – collision / close-passage experiment

## Research question (MODEL)

Does redistributing a fixed total cluster mass from a central core into zero, one or two orbiting satellites measurably alter a close gravitational encounter between two clusters?

This is a controlled classical N-body comparison. It does not assume that satellites shield a core. All masses attract all other masses under the same softened Newtonian gravity law.

## Setup

Three independent lanes run side-by-side:
- A: zero satellites per core;
- B: one satellite per core;
- C: two satellites per core, placed at opposite points on a circular initial orbit.

Every cluster has total mass 100 simulation units. Satellite mass is 4, and central core mass is reduced accordingly (100, 96 or 92). The encounter uses G=1, softened gravity epsilon=0.5, orbital radius 8, core contact radius 4, time step 0.002, initial core-cluster positions near x=±50 and impact parameter b=29. Cluster B's satellite phase is 46 degrees.

The cluster centre of mass and translational velocity are preserved when satellites are added by applying corresponding corrections to the core's initial position and velocity.

## Baseline measured result

After 26,000 integration steps per lane:

| Satellites per core | Minimum core separation | Contact / merger proxy |
|---:|---:|---|
| 0 | 8.397 | No |
| 1 | 8.000 | Yes, model time ≈ 38.898 |
| 2 | 8.043 | No; close passage |

The one-satellite lane merges under the chosen phase and impact parameter, while the two-satellite lane narrowly avoids core contact. This is not a general shielding effect. The result depends on orbital phase, impact parameter, satellite mass and other initial conditions. Changing these can alter the outcome. There is no repulsive shell in this model.

After a further 10,000 steps, the merged lane continues evolving: the combined core has mass 192 and both satellites remain as bodies. In this particular run their distances from the merged core are approximately 11.84 and 143.31 units, illustrating that one remains relatively close while the other is flung outward.

## Core merger rule

When two core centres approach within 8 distance units, they are replaced by one body at their centre of mass, with the mass-weighted centre-of-mass velocity. This is a perfectly inelastic collision rule:
- total mass is conserved;
- linear momentum is conserved;
- relative kinetic energy is dissipated by the collision rule.

This is a numerical core-merger proxy, not nuclear fusion. Newtonian gravity alone has no model of the strong nuclear interaction, nuclear binding energy, quantum mechanics, electron orbitals or chemical bonds.

## Gravitational binary-core mode

A second mode initializes two cores in a close gravitationally bound circular orbit, with satellite pairs orbiting each core. Over 20,000 integration steps (40 model time units), the separation ranges were:

| Satellites per core | Minimum separation | Maximum separation |
|---:|---:|---:|
| 0 | 40.000 | 40.015 |
| 1 | 39.793 | 40.275 |
| 2 | 39.062 | 40.888 |

This is a classical binary system, not a molecular orbital. The inner satellites and core pair can perturb one another, so the small changes in separation are part of the N-body dynamics.

## Reproducibility

Run the local smoke test:
```sh
node web/tests/gravitycluster2d_smoke.cjs web/Matrixgame_GravityClusterLab_2D.html
```

The test covers the three baseline encounter conditions, continued post-merger motion, mass and momentum conservation in the collision rule, and the 20,000-step binary-orbit test.

## Next experiments

1. Sweep impact parameter and satellite phase with fixed random-seed/parameter sets.
2. Run batches across satellite mass and orbital radius; record closest approach, merger outcome, satellite ejection and centre-of-mass drift.
3. Increase from a pair of clusters to multiple clusters.
4. Keep any later quantum-probability visualization as a separate model, not a force term inferred from classical gravity.
