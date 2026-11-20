# Matrixgame Session Handoff – 2026-11-17

## Current continuation point
- Branch: `matrixgame-continuity-wellbeing-2026-11-17`
- Base branch: `measurement-experiment-studio-v09`
- Current implementation milestone: Universal Grid Lab v0.9 Experiment Studio.
- Java source version: 0.9; Java 17 build; 65 headless self-tests passed.
- Java source SHA-256: `ae4ef5207bad62cd7fe82681cabc8e37c9a882f91557ff5b3a1bb1258f1e727c`
- ZIP SHA-256: `769ff16eaba59eacb0cfbb530b95e1774f1f779c7878a532bb69b76fa63a2df7`
- The full Java and ZIP files are conversation download artifacts, not yet committed as source files in this GitHub branch. Do not claim otherwise.

## Documents saved on this branch
1. `docs/MATRIXGAME_MASTER_ROADMAP_AND_CONTINUITY_2026-11-17.md`
2. `docs/MATRIXGAME_HUMAN_FLOURISHING_AND_POSITIVE_FEEDBACK_2026-11-17.md`
3. `docs/MATRIXGAME_CROSS_PROJECT_INTEGRATION_MAP_2026-11-17.md` (inherited from v0.9 branch)
4. `docs/MATRIXGAME_MEASUREMENT_TO_MODEL_PIPELINE_2026-11-17.md` (inherited)
5. `docs/MATRIXGAME_UNIVERSAL_GRID_LAB_V09_EXPERIMENT_STUDIO.md` (inherited)
6. `docs/INDEX.md` updated to include the roadmap and human-flourishing framework.

## Current v0.9 capabilities
- Deterministic, seeded Grid Lab experiments.
- Tick-level aggregate metrics and run manifests.
- CSV observation import with metric, unit, source and optional uncertainty.
- CSV/JSONL/manifest exports.
- Initial one-parameter grid search for food regeneration across five candidate settings.
- Calibration results and model projections are clearly labelled MODEL.
- Food regeneration and mutation use separate random streams.

## Explicit limits
- No live real-world API connector.
- No verified Matrix Core or Gravity adapter.
- No complete LUMINI parser/compiler.
- No general-purpose multi-parameter optimiser.
- No independently validated real-world forecast.
- No unbounded chunk-streaming world.
- Existing projections are game-model outputs, not claims about actual ecology or human futures.

## Next concrete task: v0.10
Implement versioned schemas and validators for:
- Observation
- ModelDefinition / Model IR
- RunManifest
- RunResult
- event JSONL

Acceptance:
- valid sample data round-trips through parse/serialize;
- invalid units, missing source IDs, invalid types and unknown schema versions fail clearly;
- export/import preserves provenance and values;
- at least one regression test reproduces a known sample.

Then connect Matrix Core through an adapter and reproduce its existing Arena results before claiming compatibility.

## Cross-project continuity
Keep these separate domain modules linked through shared contracts:
- Universal Grid Lab: discrete grid, GameObjects, 37 actions, genetic perceptions/policies and evolution.
- Matrix Core: general agents and Commons experiments.
- Gravity: continuous physics with explicit units, integrators and conservation checks.
- LUMINI: future typed model description and safe engine dispatch.
- Universal Knowledge Engine: provenance and evidence graph.
- Career/future scenarios: private, uncertainty-aware decision support.

## Personal snapshot boundary
The user's personal wellbeing reflection is stored separately as a local conversation artifact, not in this public repository. Do not add private emotional, health, career or personal-chat information to public GitHub. The public Human Flourishing document is generic and non-clinical.

## Working rule
At the start of the next session, inspect this handoff, verify the exact source/package hashes and rerun the tests before changing code. Continue from the current branch; do not restart from an unrelated prototype.
