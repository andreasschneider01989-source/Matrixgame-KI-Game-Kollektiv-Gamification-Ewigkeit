# MACHX Second Earth — Quest 004: Photo-to-voxel reconstruction

**Date:** 2026-11-20  
**Status:** executable benchmark/evaluator scaffold; GitHub Actions PASS (30 tests total, including all 7 reconstruction tests). Image ingestion and agent perception remain future work.

## Goal
Train and evaluate agents that reconstruct real structures from photographs into Minecraft/Luanti-style voxel blueprints, estimate resources, plan build order, construct, and independently evaluate results.

## Research basis
- APT (2024) studies LLM architectural planning and text-to-blueprint construction in Minecraft: https://arxiv.org/abs/2411.17255
- Voyager (2023) demonstrates an embodied Minecraft agent with an executable, reusable skill library and feedback-based self-verification: https://voyager.minedojo.org/
- Splatter Image (CVPR 2024) predicts 3D Gaussian representations from a single image; it is evidence of learned image-to-3D representations, not a turnkey photograph-to-voxel builder: https://openaccess.thecvf.com/content/CVPR2024/papers/Szymanowicz_Splatter_Image_Ultra-Fast_Single-View_3D_Reconstruction_CVPR_2024_paper.pdf
- Odyssey (IJCAI 2025) investigates reusable open-world skills in Minecraft: https://ijcai.org/proceedings/2025/22

These works support separate components of the vision. They do not establish that a general agent can reliably reconstruct arbitrary real buildings from one photograph.

## Implemented in this increment
- `second-earth/reconstruction/blueprint.py`: typed voxel block and blueprint data model, validation, geometry scoring and material scoring.
- `second-earth/reconstruction/test_blueprint.py`: seven deterministic unit tests.
- `second-earth/reconstruction/README.md`: scope, metrics, limitations and next pipeline stages.
- A seven-block synthetic arch fixture provides a known target for testing.

## Metrics
Voxel precision, recall, F1 and IoU; missing/extra block count; material accuracy on common coordinates; bounding dimensions and absolute dimensional error; estimated occupied volume. Geometry and material scores are reported separately. Composite scores are deferred until explicit weights can be justified.

## Scientific limitations
- Single-view images do not uniquely determine hidden geometry, absolute scale or camera pose in general.
- Multi-view photographs, known dimensions, depth sensors or CAD/scan references improve identifiability.
- A voxel blueprint is a lossy discretization; retain the mesh/scene graph as a higher-resolution source representation when available.
- Color is not a reliable material identifier. Material classification requires visual/material evidence and uncertainty.
- Synthetic fixture tests establish software behavior only, not real-world reconstruction accuracy.

## Next steps
1. Verify Quest 004 tests in GitHub Actions.
2. Add a small photo benchmark with known dimensions and at least front/side/top views.
3. Compare agent blueprint to a manually verified voxel ground truth.
4. Add build-plan estimates: block inventory, sequence, support constraints and resource cost.
5. Connect only after evaluation to a Luanti adapter; keep Minecraft as a separate optional adapter.
6. Store each run with source image provenance/permission, camera metadata, agent/model version, blueprint, metrics, uncertainty and human review.
