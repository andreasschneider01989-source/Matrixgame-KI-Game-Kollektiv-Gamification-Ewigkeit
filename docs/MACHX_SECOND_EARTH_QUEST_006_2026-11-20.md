# MACHX Second Earth — Quest 006: Persistent Hybrid World State

**User choices:** project=world; representation=hybrid; agents=collective; first success=balanced.  
**Status:** implementation committed on a dedicated feature branch; Second Earth pilot CI PASS (54 tests); LifeOS integrity PASS; separate legacy Python application checkout fails because historical repository filenames exceed the Linux filename-length limit.

## Objective
Build the smallest reusable foundation for a world that survives across simulation steps and can be inspected, edited, serialized and reused by later agents.

## Implemented increment
- Stable object IDs and world-step counter.
- Hybrid representation: integer voxel cells plus an external mesh asset ID.
- Metric position and scale, material ID, confidence, provenance/source IDs and observation history.
- Three explicit collective roles: proposer, independent critic, deterministic integrator.
- Confidence-weighted position fusion for consistent observations.
- Low-confidence, stale, duplicate and spatially conflicting evidence handled explicitly.
- Conflicts are recorded instead of silently replacing the world state.
- Deterministic JSON serialization and restoration.
- 12 dependency-free unit tests.

## Data contract
The world state is not a video frame and is not stored in model weights. It is an explicit, versioned data object. Mesh geometry is referenced by stable asset ID; voxel occupancy is represented by integer coordinates. Position and scale use metres. The current mesh field is a reference only: no mesh parser, renderer, physics engine or image reconstruction is included.

## Collective protocol
1. Proposer submits observations with stable observation/object IDs, source, timestamp/step, position and confidence.
2. Critic rejects low-confidence/stale evidence and checks mutual and prior-world spatial consistency.
3. Integrator fuses only accepted evidence and appends an event record.
4. Teacher/replay can serialize the exact state and reproduce the same result.

## Reproducibility
Run from repository root:

```bash
python -m unittest discover -s second-earth -p "test_*.py" -v
```

The existing Second Earth pilot workflow already runs this discovery command, so Quest 006 tests join the same CI gate.

## Important scientific limitation
The confidence combination formula and distance threshold are transparent synthetic rules, not calibrated Bayesian inference. Confidence must not be interpreted as a probability until calibrated against labeled data. The current system does not infer 3D geometry from images, render a world, or execute Minecraft/Luanti actions.

## Research connection
- PERSIST (ICML 2026) models an evolving latent 3D scene, camera and renderer to improve spatial memory and long-horizon consistency: https://proceedings.mlr.press/v306/garcin26a.html
- P3Sim (CVPR 2026) combines a learned physical world model, geometrizer and persistent scene memory: https://openaccess.thecvf.com/content/CVPR2026/papers/Lee_Perceptual_3D_Simulation_With_Physical_World_Modeling_CVPR_2026_paper.pdf
- WorldRoamBench (2026) motivates evaluating action, visual drift, physics and memory independently: https://arxiv.org/abs/2606.31672

These works motivate the separation of persistent state, geometry, observations and evaluation. They do not validate this toy implementation.

## Next gate
Render one small synthetic scene from two camera poses, produce observations with known ground truth, run collective fusion, then test whether the restored world yields the same object geometry and metrics. Only after that add an image/3D reconstruction adapter or Minecraft/Luanti bridge.
