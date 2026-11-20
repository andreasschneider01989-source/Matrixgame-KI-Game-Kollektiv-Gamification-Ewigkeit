# Quest 005 — Agent roles and experiment protocol

## Objective
Reconstruct the same known synthetic house under two evidence conditions: one front view and five orthographic views. Compare geometry, materials, buildability, resource use and estimated build time.

## Roles
1. **Perception agent** — extracts facade openings, roof outline and confidence from supplied images (future model integration).
2. **Geometry agent** — proposes dimensions and voxel coordinates; records unknown/occluded regions.
3. **Material agent** — proposes material IDs separately from visual colour and reports uncertainty.
4. **Architect agent** — decomposes the house into foundation, walls/openings and roof.
5. **Resource agent** — counts blocks by material and estimates cost/time from declared rates.
6. **Builder agent** — converts accepted blueprint to Minecraft/Luanti placement commands (future adapter).
7. **Physics agent** — checks mass, support and stability using material and physics models (future increment).
8. **Critic/evaluator** — scores predictions against withheld ground truth and reports each metric separately.
9. **Teacher agent** — stores failure cases, corrections and reusable construction skills.

## Evidence split
- Single-view: front projection only. Hidden side/rear/interior geometry must remain UNKNOWN or be marked as a hypothesis.
- Multi-view: front, rear, left, right and top projections. More evidence constrains geometry but does not provide texture, exact camera calibration or hidden internal details.
- Ground truth is held out from the reconstruction agent and used only by the evaluator.

## Metrics
- Geometry: voxel precision/recall/F1/IoU, missing/extra blocks, bounding dimensions.
- Materials: material accuracy on overlapping occupied voxels.
- Planning: inventory correctness, stage coverage, estimated blocks/minute and total build-time estimate.
- Balanced comparison: report all dimensions separately. Do not collapse them into one score until weights and trade-offs are explicitly chosen.

## Reproducibility
Reference geometry is deterministic; unit tests require no network, GPU, external package or copyrighted image. Future real-photo benchmark entries must record image source/license, camera/view metadata, scale reference, ground-truth provenance, agent/model version and random seed.

## Current boundary
This is a synthetic geometry and planning benchmark. It does not yet ingest actual photographs, run a vision model, infer depth/camera calibration, build in Minecraft/Luanti, or validate real structural safety. A procedural projection is not a photograph.
