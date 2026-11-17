# Matrixgame v0.9 – Measurement-to-Model Pipeline and Future Compiler Layer

**Date:** 2026-11-17  
**Current implementation:** Universal Grid Lab v0.9 Experiment Studio  
**Epistemic status:** MODEL / architecture proposal  
**Software status:** v0.9 PROTOTYPE + TESTED; future real-data connectors and LUMINI execution remain PLANNED.

## 1. Why this is the next layer

The Grid Lab now generates repeatable model data, but future research needs a bridge from external observations to simulation parameters and back to testable projections.

The intended long-term chain is:

External measurement / dataset snapshot
→ provenance and unit validation
→ normalized Observation records
→ versioned Model IR
→ registered SimulationEngine
→ calibrated parameter set
→ prediction / scenario distribution
→ independent validation
→ report, visualization, knowledge node and next quest.

This pipeline is deliberately separate from the specific game rules. Grid evolution, Matrix Core agent models, continuous gravity, system dynamics and career scenarios should implement their own engine adapters while sharing data and experiment contracts.

## 2. What v0.9 actually implements

### 2.1 Run recording

Every completed Grid Lab tick records an aggregate MetricSample, beginning at tick zero. The sample includes:
- total population and four team populations,
- food count,
- births, deaths, starvation deaths and predation kills,
- food collected and spawned,
- maximum generation and unique live genomes,
- mean energy, fitness, metabolism, aggression and food efficiency,
- mean selected genetic operation weights and sensor radius,
- food-regeneration probability and mutation pressure.

The GUI can export the current session. A batch mode can run a deterministic experiment from the command line.

### 2.2 Run package

Each export contains:
- run_manifest.json — version, run id, seed, world dimensions, parameters, tick range, sample count, status and limitations;
- timeseries.csv — per-tick aggregate metrics;
- events.jsonl — non-zero tick deltas for births, deaths, predation, collected and spawned food;
- README.txt — data and epistemic-status notes.

### 2.3 External observation CSV

Current import schema:

`tick,metric,value,unit,source_id,uncertainty`

Required: tick, metric, value, unit, source_id.  
Optional: uncertainty, expressed in the same unit as value.  
Rows beginning with # are ignored. One metric must use one consistent unit within a dataset.

The current adapter reads local CSV files. There is no automatic real-world API connector in v0.9.

Supported metrics are the aggregate metrics exported by the Grid Lab, including creature/team population, food count, births/deaths, kills, generation, genome diversity and mean trait values.

### 2.4 First calibration method

The current calibration demo varies one model parameter: `food_regeneration_probability`, over five predeclared candidate values: 0.15, 0.25, 0.38, 0.50 and 0.65.

For every candidate, the tool:
1. repeats the simulation with the same replicate seed set;
2. averages model values at the observed ticks;
3. calculates residuals against supplied observations;
4. ranks candidates by normalized RMSE;
5. exports candidate scores and residuals;
6. generates a forward `model_projection.csv` labelled MODEL.

If measurement uncertainty is supplied, it is used as a residual scale. If it is absent, the software uses max(1, abs(observed value)) as a fallback scale. That fallback is a numeric normalization convention, not a measured uncertainty.

This is a coarse grid search over one game parameter. It is not yet a general optimizer, an empirical model calibration pipeline or proof of prediction accuracy.

## 3. Provenance required for real measurements

Before a real dataset can be used responsibly, its data card should include:

| Field | Purpose |
|---|---|
| dataset_id / source_id | Stable source identifier |
| publisher / instrument | Who published or measured it |
| source_uri | Original URL, DOI, API endpoint or local source description |
| retrieved_at | When the local snapshot was obtained |
| valid_time | When the underlying measurement applies |
| region / spatial_extent | Geographic or physical scope |
| metric_definition | Operational meaning of each value |
| unit | SI or a clearly defined domain unit |
| sampling_interval | Temporal resolution |
| uncertainty | Instrument, sampling or estimation uncertainty |
| missing_data_policy | How missing observations are represented |
| license / terms | Reuse and redistribution permission |
| checksum / version | Snapshot identity and integrity |
| limitations | Known gaps, bias and measurement constraints |

Do not silently interpolate missing observations or mix incompatible units. Keep raw observations immutable; cleaned or transformed observations should be derived datasets with their own provenance.

Personal career or learning data must remain in a private/local store unless explicitly selected for sharing. Public GitHub should contain generic schemas and non-sensitive example datasets only.

## 4. Calibration and validation must remain separate

The future pipeline should partition observations into:
- calibration/training window — used to estimate parameters;
- validation/hold-out window — not used during parameter selection;
- optional test window — final comparison after model decisions are frozen.

A low residual on the calibration data does not establish reliable future predictions. Compare against simple baselines, report error by metric and time horizon, examine residual structure, quantify uncertainty, and repeat across seeds or independent datasets.

For a real forecast, useful metrics may include MAE, RMSE, bias, interval coverage and calibration curves. Select metrics according to the data and decision purpose; do not automatically use a single accuracy score for every domain.

## 5. Planned common software contracts

The planned common contract is:

- `Observation`: timestamp/valid-time, entity, metric, value, unit, source and uncertainty.
- `ModelDefinition`: state variables, equations/rules, entities, parameters, initial/boundary conditions and assumptions.
- `RunManifest`: model/version, engine/version, source commit, seeds, dataset hashes, parameter set, solver, timestamps and tests.
- `RunResult`: observations, predictions, residuals, uncertainty and provenance.
- `Experiment`: hypothesis, control, intervention, metrics, repeats and acceptance criteria.

The existing Matrix Core already has the concepts `World`, `Agent`, `Action`, `Observation`, `Feedback`, `Experiment`, `Result` and `Claim`. The next integration task is to map its existing types to these contracts without duplicating the core or silently changing semantics.

Gravity remains a separate continuous-physics engine: it needs SI units, force models, integrators, collision models and conservation/error checks. A grid tick is not a physical time step and should not be treated as one.

## 6. LUMINI: future compiler architecture

The long-term compiler vision is recorded as:

Natural language / visual editor
→ LUMINI source (.lumini)
→ lexer and parser
→ AST
→ semantic, type and unit checks
→ versioned Model IR (JSON)
→ registered engine adapter
→ compile/prepare and execute
→ RunManifest + measured outputs
→ charts, canvas, report and reusable knowledge nodes.

A language model may help translate a human description into a draft LUMINI file. The parser and semantic checker must independently validate the draft. Unknown functions, incompatible units, missing assumptions or unsupported engine features should fail with actionable errors.

The first compiler must not execute arbitrary generated shell commands or unrestricted source code. It should dispatch only to registered, permissioned engine adapters with resource limits and explicit user approval where required.

### Compiler optimization roadmap
1. Cache parsed AST and validated Model IR using source and schema hashes.
2. Rebuild only affected model sections after small edits.
3. Cache compiled engine configuration when parameters and engine versions are unchanged.
4. Keep data ingestion separate from simulation execution.
5. Store every run's inputs and result references in its manifest.
6. Allow alternate implementations (Java, JavaScript, Python) to consume the same IR contract.

This is a roadmap, not an implemented LUMINI compiler.

## 7. Gamification and collaborative learning

The experiment pipeline itself can become a quest system:

- **Quest: Reproduce** — repeat a run with identical seed/configuration and match the checksum or expected metrics.
- **Quest: Source** — attach valid provenance and unit definitions to an external observation dataset.
- **Quest: Calibrate** — compare a baseline with a declared intervention and report residuals.
- **Quest: Challenge** — try a hold-out period or a counterexample that can disconfirm the model.
- **Quest: Bridge** — connect a second engine through the common contracts.
- **Quest: Compile** — translate one safe LUMINI model into a validated Model IR and execute an allow-listed engine.

XP should reward reproducibility, transparent provenance, good tests, useful negative results and independent review—not merely high model scores or frequent activity.

## 8. Next implementation sequence

### v0.10 — portable schemas
- Versioned JSON Schema for Observation, Model IR, RunManifest and RunResult.
- JSONL event schema and import/export validation.
- A data dictionary for every supported metric.

### v0.11 — multiple model parameters
- Parameter sweeps for more than one parameter.
- Sensitivity analysis and parameter identifiability warnings.
- Baseline model and hold-out split.

### v0.12 — Matrix Core adapter
- Run the existing Commons experiments through the shared RunManifest and Observation contract.
- Preserve the Arena's original metrics and test results.
- Add comparison tests showing the adapter reproduces the documented run.

### v0.13 — gravity adapter
- Locate and inspect the exact GravityEngine source.
- Add two-body and N-body verification tests before integrating.
- Compare integrators and monitor energy/momentum drift.

### v0.14 — first LUMINI vertical slice
- Parse a tiny declarative model (initially a simple population/regeneration model).
- Validate types, units and parameter ranges.
- Generate Model IR and dispatch only to the Grid Lab adapter.

### v0.15 — real public-data connector
- One carefully scoped dataset/API with documented terms.
- Immutable source snapshot, retrieval date, checksum and unit metadata.
- Calibration/validation split and a baseline comparison.

## 9. Acceptance principles

- Same model version, parameters, dataset snapshot and seed must reproduce the same run where deterministic behavior is intended.
- Every exported result is traceable to its source inputs and version.
- Missing data and unit mismatches are visible errors, not guessed values.
- Calibration results are distinct from validation results.
- Model projections remain labelled MODEL until independently validated.
- Real-world conclusions are limited to the dataset scope and model's demonstrated validity.
- Simulation engines can evolve independently without changing one another's domain semantics.

**Vision:** Human language should eventually become a clear model description that can be parsed, checked, compiled to a safe engine configuration, run interactively, compared with evidence and improved through reproducible experiments. v0.9 implements the first small measurement/export/calibration bridge; the full compiler and real-world forecasting system remain future work.
