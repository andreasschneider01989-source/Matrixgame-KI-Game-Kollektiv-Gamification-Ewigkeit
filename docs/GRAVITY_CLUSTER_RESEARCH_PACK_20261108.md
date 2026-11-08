# Matrixgame Gravity Cluster Research Pack — 8 November 2026

## Contents
- web/GravityClusterResearchDashboard_Replay_v2_1.html: interactive local dashboard, 189 embedded records and explicit replay-parameter manifest.
- results/gravity_cluster_phase_sweep_dashboard_v2_20261108.json: machine-readable manifest and all records.
- results/gravity_cluster_phase_sweep_dashboard_v2_20261108.csv: tabular export of the same records.
- results/gravity_cluster_replay_v2_1_manifest_20261108.json: provenance/model manifest.
- experiments/verify_gravity_cluster_dashboard_package.py: dependency-free integrity check.
- docs/GRAVITY_CLUSTER_REPLAY_V2_AUDIT_20261108.md: scientific boundaries and resume guide.

The dataset contains 189 deterministic runs: A (0 satellites) 3/7 contacts; B (1 satellite) 37/91; C (2 satellites) 52/91. These are descriptive fractions of the selected finite parameter grid, not universal probabilities.

The model is classical 2D Newtonian N-body gravity with Plummer softening and Velocity-Verlet. The merger is a perfectly inelastic collision proxy, not fusion or chemistry.

Run python experiments/verify_gravity_cluster_dashboard_package.py to validate package integrity. The check does not rerun the simulation or establish physical realism.
