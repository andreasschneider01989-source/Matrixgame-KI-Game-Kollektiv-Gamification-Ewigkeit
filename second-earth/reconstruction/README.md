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
