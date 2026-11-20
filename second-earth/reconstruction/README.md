# Second Earth Reconstruction Lab — Quest 004

## Scope
This first increment defines a deterministic voxel blueprint and evaluator. It is the scoring foundation for a later image-to-blueprint agent. It does **not** yet ingest photographs, estimate camera pose/depth, run a vision model, or control Minecraft/Luanti.

## Representation
- Integer `(x, y, z)` voxel coordinates.
- Material identifier per voxel.
- Configurable voxel edge length; default 1 m.
- Duplicate coordinates are invalid.

## Evaluation
Geometry and material choice are deliberately scored separately:
- voxel precision: fraction of predicted occupied positions that are correct;
- voxel recall: fraction of target occupied positions recovered;
- voxel F1: harmonic mean of precision and recall;
- voxel IoU: intersection-over-union of occupied voxel positions;
- material accuracy: correct material labels at positions present in both models;
- dimensions and absolute dimension error;
- missing/extra block counts and estimated occupied volume.

No single score should decide whether a reconstruction is "good". A future composite score must disclose weights and report each metric separately. For an actual photograph, the reference target must be independently known (e.g. measured object, CAD model, or multi-view scan); otherwise scoring would be circular.

## Synthetic fixture
`arch_fixture()` provides a known seven-block arch. It tests exact reconstruction, missing/extra voxels, material mismatch and invalid blueprints. It is a software test fixture, not a real photograph or empirical reconstruction.

## Run tests
From repository root:

```bash
python -m unittest discover -s second-earth -p "test_*.py" -v
```

## Quest 005 — House benchmark
See [QUEST_005_AGENTS.md](QUEST_005_AGENTS.md), [house-001-reference.json](house-001-reference.json), and the executable fixture in house.py. The deterministic reference is a 6 × 7 × 5 voxel house with stone foundation, wood perimeter walls/openings, and a brick gable roof. Two evidence conditions are defined: front-only and five orthographic projections (front/rear/left/right/top). The evaluator keeps ground truth separate from the evidence supplied to a reconstruction agent. The current projections are mathematical occupancy projections, not rendered photos. The build planner reports per-material inventory and three construction stages using a declared blocks/minute rate; this is a transparent baseline estimate, not measured construction time.

## Quest 006 — Persistent hybrid world state
See [world_state.py](world_state.py), [test_world_state.py](test_world_state.py), and [world-state-001.json](world-state-001.json). This dependency-free reference model stores stable object IDs, metric transforms, material IDs, voxel cells, external mesh references, confidence, provenance and observation history. A proposer/critic/integrator protocol fuses spatially consistent observations, rejects stale/low-confidence/conflicting evidence, and serializes the world deterministically. This is a transparent synthetic fusion rule, not calibrated Bayesian inference or a learned multi-agent model.

## Planned pipeline (not yet implemented)
1. Photo(s) + camera/view metadata.
2. Vision model: segmentation, edges, object labels and confidence.
3. Geometry model: scale/depth/camera estimates; preserve ambiguity where underdetermined.
4. Intermediate mesh/point cloud/scene graph.
5. Voxelization with selectable block size and material proposals.
6. Build planner: block inventory, construction order, support/scaffolding and time estimate.
7. Independent evaluator and critic; save errors, confidence and model versions.

## Scientific and game governance
- Separate observation, inferred geometry, unknown/occluded regions and invented completion.
- Never treat a single-view depth guess as measured truth.
- Preserve the original high-resolution mesh/scene graph; voxelization is a lossy approximation.
- Physics properties remain attached to material IDs from the Materials Lab, not inferred from color alone.
- Agent proposals are not promoted to world rules until validated and human-approved.
- Optimize multiple objectives: geometric fidelity, material fidelity, buildability, cost, time and playability. Keep the Pareto trade-offs visible rather than hiding them in one opaque score.
