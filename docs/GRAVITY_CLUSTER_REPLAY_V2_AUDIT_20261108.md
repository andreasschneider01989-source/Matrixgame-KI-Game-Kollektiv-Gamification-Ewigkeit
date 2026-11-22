# Gravity Cluster Replay v2.1 — Audit and resume guide

**Date:** 8 November 2026  
**Status:** Reproducibility/export layer for an existing deterministic experiment  
**Existing development branch:** feature/evolution-forge-bridge  
**Existing draft PR:** #4

## What has been recovered

The uploaded GravityClusterResearchDashboard_Replay_v2(1).html contains 189 deterministic run summaries, a phase heatmap, filters, CSV/JSON export, a measurement table and a second numerical Newtonian replay.

The package preserves the uploaded source as a source artifact and adds a separate v2.1 dashboard. The new copy makes replay parameters and initial-condition geometry explicit in META.model_parameters; replay initialization and the contact check read the manifest instead of relying solely on hard-coded fallbacks.

## Dataset audit

| Variant | Satellites per cluster | Runs | Core contacts | Fraction of sampled grid |
|---|---:|---:|---:|---:|
| A | 0 | 7 | 3 | 42.86% |
| B | 1 | 91 | 37 | 40.66% |
| C | 2 | 91 | 52 | 57.14% |
| Total | — | 189 | 92 | 48.68% |

All 189 run IDs and parameter tuples are unique. All rows declare dt=0.002 and steps=26000. These fractions describe only this finite selected parameter grid; they are not universal encounter probabilities.

## Declared model parameters

- Model: classical planar N-body gravity with Plummer softening.
- Integrator: fixed-step Velocity-Verlet.
- G=1, softening epsilon=0.5.
- Total mass per cluster: 100; satellite mass: 4.
- Satellite orbital radius: 8; fixed core radius: 4.
- Core-contact threshold: 8.
- Initial centres: A at (-50,+b/2), velocity (+1,0); B at (+50,-b/2), velocity (-1,0).
- Main encounter: 26,000 steps at dt=0.002.

The core radius remains fixed while the core mass changes by variant. This is a model assumption, not a physical constant.

## Numerical quality and limitations

The source META contains three threshold-near step-size spot checks (dt=0.002, 0.001, 0.0005). The contact classification did not switch in those three checked cases. This is a local numerical check, not a complete convergence study over all 189 runs.

The collision/merger rule is a perfectly inelastic classical proxy. It is not nuclear fusion, chemical bonding or an atomic interaction. No gravitational screening or repulsive force is established. Classical satellites are not electrons, protons or helium atoms.

The source dashboard reports a generator-script SHA-256. This package records the reported value but does not claim it was independently recalculated during packaging.

## Recovered legacy Java work

Archived Java notes contain older MasseKugel, GravityGemini, Gravity, GravityClass and entropy-oriented variants. They are useful historical prototypes, but they should not be copied directly into the current reference engine without a separate refactor and tests: some methods mix pairwise force calculation with immediate state updates, random perturbations or implicit time-step handling.

Recommended separation:
1. Preserve the older GUI/game mechanics as a legacy front end.
2. Use an immutable body-state model and explicit force accumulator for the physics core.
3. Add a deterministic, explicitly timed Velocity-Verlet integrator.
4. Keep entropy/random gameplay forces as separately named modules.
5. Compare selected trajectories against an independent solver such as REBOUND IAS15 before increasing scientific claims.

## File map

- Dashboard: web/GravityClusterResearchDashboard_Replay_v2_1.html
- JSON records + manifest: results/gravity_cluster_phase_sweep_dashboard_v2_20261108.json
- CSV records: results/gravity_cluster_phase_sweep_dashboard_v2_20261108.csv
- Export manifest: results/gravity_cluster_replay_v2_1_manifest_20261108.json
- Verifier: experiments/verify_gravity_cluster_dashboard_package.py
- Canonical generator: experiments/gravity_cluster_phase_sweep.py
- Generator tests: experiments/test_gravity_cluster_phase_sweep.py
- Protocol: docs/GRAVITY_CLUSTER_PHASE_SWEEP_PROTOCOL.md
- Scientific results: docs/GRAVITY_CLUSTER_PHASE_SWEEP_RESULTS_20261104.md
- Pause/resume checkpoint: docs/PROJECT_PAUSE_RESUME_CHECKPOINT_20261104.md

## Reproduce and verify

Open the dashboard locally in a browser. The integrity check requires only the Python standard library:

    python experiments/verify_gravity_cluster_dashboard_package.py

The checker verifies record count, unique keys, group totals, declared time step, step count and consistency between JSON, CSV and embedded HTML. It does not rerun the integrations and does not validate physical realism.

## Next quests

1. Manually QA the dashboard in a browser and compare selected replays with their stored reference minima.
2. Broaden dt-convergence beyond three threshold-near cases.
3. Run selected cases with an independent integrator (REBOUND IAS15).
4. Vary core radii and satellite masses separately.
5. Then expand to a finer phase grid and larger ensembles.
6. Keep any Coulomb/short-range/quantum model as a separate switchable physics layer, with pure Newtonian gravity as the baseline.
