# Matrixgame Universal Grid Lab v0.9 – Experiment Studio Checkpoint

**Date:** 2026-11-17  
**Branch:** measurement-experiment-studio-v09  
**Base:** expanded-world-genetic-lab-v08  
**Software status:** PROTOTYPE / TESTED  
**Claim status:** MODEL

## 1. Purpose

v0.9 adds the first measurement-to-model bridge to the Universal Grid Lab. It is the next practical step toward a shared contract for:
- discrete Grid Lab and agent-based simulations,
- Matrix Core experiments,
- continuous Gravity / N-body engines,
- external observations and data snapshots,
- future LUMINI Model IR and compiler dispatch,
- later career and future-scenario models.

The long-term pipeline is:

External measurement / dataset snapshot
→ provenance and unit validation
→ normalized Observation records
→ versioned Model IR
→ registered SimulationEngine
→ calibration
→ prediction / scenario distribution
→ independent validation
→ report, visualization and reusable knowledge node.

## 2. Implemented in v0.9

### Tick-level metrics
Every Grid Lab simulation tick records an aggregate metric sample, starting at tick zero. Recorded metrics include:
- total and team populations,
- food count,
- births, deaths, starvation deaths and predation kills,
- collected and spawned food,
- maximum generation and unique live genomes,
- mean energy, fitness and genetic traits,
- food-regeneration probability and mutation pressure.

### Reproducible run export
The GUI includes a measurement-export action. A run package contains:
- `run_manifest.json`
- `timeseries.csv`
- `events.jsonl`
- a README with claim status and limitations.

The manifest records engine/version, seed, world dimensions, parameters, tick range, sample count, output list and limitations.

### Observation CSV import
Schema:

`tick,metric,value,unit,source_id,uncertainty`

Required fields: tick, metric, value, unit, source_id.  
Optional field: uncertainty, in the same unit as value.  
Comment lines beginning with # are ignored. One metric must use one consistent unit in a dataset.

### Parameter calibration demo
The current adapter searches five candidates for one game parameter, `food_regeneration_probability`: 0.15, 0.25, 0.38, 0.50 and 0.65.

For each candidate it repeats runs with the same replicate seed set, averages outputs at observed ticks, calculates normalized RMSE, ranks candidates and exports:
- `calibration_candidates.csv`
- `calibration_residuals.csv`
- `calibration_report.md`
- `model_projection.csv`

If observation uncertainty is supplied, it scales residuals. Otherwise, the fallback is `max(1, abs(observed value))`; this is a numerical normalization convention, not measured uncertainty.

The selected parameter is only the best among five tested candidates, for the chosen model, seed ensemble and input dataset. `model_projection.csv` is explicitly labelled MODEL and is not a validated forecast.

### Independent deterministic random streams
Food regeneration and genetic mutation use separate seeded random streams. Changing the food-regeneration parameter therefore does not consume random draws from the mutation stream.

## 3. Verification performed

- Build: `javac --release 17 -Xlint:all -Werror` — successful.
- Headless self-tests: **65 checks passed**.
- GUI startup: checked under Xvfb; export control visible.
- Example reference run: 600 ticks, seed `20261117`, 120 × 80 world, food-regeneration probability 0.38.
  - population: 34
  - births: 119
  - deaths: 105
  - food remaining: 82
  - population balance: 20 + 119 − 105 = 34
  - time-series samples: 601 (tick 0 through 600)
- Calibration smoke test used the included synthetic dataset `synthetic-v09-self-test-only`, not real-world measurements. It selected 0.38 from the five candidates.

These are software/model verification results. They do not demonstrate that the game ecology predicts real ecosystems or human behaviour.

## 4. Explicitly not implemented yet

- No automatic live real-world API connector.
- No direct runtime adapter for Matrix Core.
- No Gravity / N-body adapter.
- No LUMINI lexer, parser, AST or compiler execution.
- No generic multi-parameter optimizer.
- No automated calibration/validation hold-out split yet.
- No empirically validated real-world forecast.
- No dynamically infinite chunk-streamed world; the current world remains finite.
- The Java source is distributed as a conversation download artifact. This checkpoint does not claim that the full 199 KB .java source has been committed to this repository branch.

## 5. Next steps

### v0.10 – portable data contracts
Create versioned JSON Schemas for Observation, ModelDefinition, RunManifest, RunResult and JSONL events. Add validation and a metric dictionary.

### v0.11 – stronger calibration and evaluation
Add multi-parameter sweeps, sensitivity analysis, baseline comparisons, explicit calibration/hold-out partitions and residual diagnostics. Avoid parameter fitting on the same data used for final evaluation.

### v0.12 – Matrix Core adapter
Map Matrix Core's existing World, Agent, Observation, Action, Feedback, Experiment and Result abstractions to the shared contracts. Reproduce documented Commons/Arena results before claiming compatibility.

### v0.13 – Gravity adapter
Locate and inspect the exact GravityEngine source. Add SI-unit checks, two-body analytical comparisons, energy/momentum drift checks, integrator comparisons and collision tests before integration.

### v0.14 – first LUMINI compiler vertical slice
Parse a very small declarative model into a typed AST and versioned Model IR. Validate units and ranges. Dispatch only to an allow-listed engine adapter. Never execute arbitrary model-generated shell commands.

### v0.15 – first real public-data connector
Choose one well-defined public dataset/API; store immutable source snapshot, date, license/terms, metric definition, spatial and temporal scope, units, missing-data policy, uncertainty and checksum. Keep calibration and independent validation data separate.

## 6. Future compiler architecture

Natural language / visual editor
→ LUMINI source
→ lexer/parser
→ AST
→ semantic/type/unit validation
→ versioned Model IR
→ allow-listed engine adapter
→ run manifest and outputs
→ comparison with observations
→ calibrated model and validation report
→ interactive canvas, visualization and knowledge graph.

A language model may draft or explain a model description, but it must not bypass parser, semantic checks, unit validation, test execution, provenance or human approval.

Potential compilation optimizations:
- cache parsed ASTs by source hash,
- cache validated IR by source/schema version,
- rebuild only changed model sections,
- cache engine configuration where the engine and parameters are unchanged,
- separate data ingestion from simulation execution,
- preserve complete run provenance.

## 7. Gamified research quests

- **REPRODUCE:** rerun same configuration and seed; verify same outputs.
- **SOURCE:** add provenance and metric definitions to a dataset.
- **CALIBRATE:** compare candidate parameters on declared observations.
- **CHALLENGE:** test on hold-out data or an explicit counterexample.
- **BRIDGE:** connect another engine through the common contract.
- **COMPILE:** translate a safe LUMINI source into validated Model IR and execute an allow-listed engine.

XP should reward reproducibility, provenance, careful testing, useful negative results and independent review—not merely high scores or frequent activity.

## 8. Source artifact integrity

Local Java source: `MatrixgameUniversalGridLab_v09.java`  
SHA-256: `ae4ef5207bad62cd7fe82681cabc8e37c9a882f91557ff5b3a1bb1258f1e727c`

Local ZIP package: `MatrixgameUniversalGridLab_v09.zip`  
SHA-256: `769ff16eaba59eacb0cfbb530b95e1774f1f779c7878a532bb69b76fa63a2df7`

The package includes the source, Windows launch/test/batch scripts, README, observation template, GUI preview, a reproducible MODEL run, and an explicitly synthetic calibration example.

**Principle:** A model projection is not a real-world forecast until the metrics are comparable, the data provenance is sound, the model is calibrated without leakage, and predictions are evaluated against independent measurements.
