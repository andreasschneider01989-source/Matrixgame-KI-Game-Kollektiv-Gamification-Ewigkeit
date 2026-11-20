# Matrixgame Master Roadmap and Continuity Snapshot
**Snapshot date:** 2026-11-17  
**Branch:** matrixgame-continuity-wellbeing-2026-11-17  
**Purpose:** Preserve the shared vision, current project state, architecture decisions, milestones and next-session entry point.

## 1. North-star vision

Build an open, persistent and reproducible development environment in which people and AI systems can describe models in natural language, translate them into formal structures, compile or dispatch them to tested simulation engines, inspect results interactively, compare them with real observations, and improve both models and tools over time.

The intended loop is:

**Human language / visual authoring → typed model description → parser and validation → Model IR → registered engine → simulation / execution → measurements → comparison with evidence → learning and revision.**

The goal is not one giant engine. It is a family of domain-specific engines connected through stable, explicit contracts.

## 2. Core design principles

1. **A working artifact outranks a promising description.** A feature is not complete until its acceptance criteria and tests are recorded.
2. **One shared contract, multiple engines.** Grid games, gravity, ecology, communication and future scenarios have different rules but can share data/provenance interfaces.
3. **Input–Operation–Output–Feedback.** Agents perceive only a declared observation, select from an explicit legal action space, act through a resolver, and receive measurable feedback.
4. **Reproducibility.** Record source version, configuration, seeds, datasets, units, environment and run outputs.
5. **Separate fact, model, hypothesis and fiction.** A successful compile is not scientific validation; a simulation is not automatically a real-world forecast.
6. **Human control and privacy.** Use allow-listed engine adapters, explicit permissions and private stores for personal data.
7. **Stable mode and exploratory mode.** Protect tested behavior while new ideas are prototyped in isolated branches or modules.
8. **Constructive gamification.** Reward learning, reproducibility, cooperation, useful criticism and verified progress, not mere activity or pleasing scores.

## 3. Current module map and maturity

| Module | Current material | Status and next action |
|---|---|---|
| Universal Grid Lab | v0.9 Java Swing grid/evolution sandbox, 120×80 interactive world, genetic sensing and operation traits, 37-action vocabulary, tick metrics, CSV observation import, run export and first single-parameter calibration demo | Local Java source v0.9 compiles with Java 17 and passes 65 headless checks. Preserve and version the exact source; next implement portable schemas. |
| Matrix Core | Java 21 core with World, Agent, Resource, Message, Simulation, Experiment, Result, Claim and PAL; Commons/Allmende Arena with 200-seed results | Existing code and documented experiments. Next compare interfaces and add a reproducibility-preserving adapter. |
| Gravity / Physics | Historical Java Swing 3D gravity/entropy/infinity-canvas prototype; project journal describes N-body, softening, collision/merger and variable axes | Separate historical codebase. Locate exact GravityEngine source, compile it, and add analytical and numerical tests before integration. |
| LUMINI | Specification for source language, lexer/parser, AST, semantic/type/unit checking, Model IR and engine dispatch | Design specification, not a complete compiler. Build a tiny safe vertical slice first. |
| Universal Knowledge Engine | Knowledge nodes, typed relationships, provenance, evidence levels, version history and learning quests | Architecture/specification; implementation pending. Start with portable schemas and validation. |
| Collective AI / communication | Historic agent networks, 6-port neurons, roles, communication, strategic Unit Fight and collective coordination prototypes | Catalogue the exact source files and test status per module; do not infer implementation from historical notes alone. |
| Future / career scenario lab | LUMINI design for skills, pathways, learning effort, regional data, constraints and conservative/baseline/optimistic scenarios | Planned decision-support module, not a calibrated predictor. Keep personal profiles private and outputs uncertainty-aware. |
| Human flourishing / wellbeing | Human-centred values, meaningful goals, small actions, recovery, connection and reflective check-ins | Support framework, not a diagnosis or automated mental-state measurement. Keep individual observations private. |

## 4. The shared contracts to stabilize

The next architecture slice is a small, versioned set of portable contracts.

### Observation
- observation_id, entity_id, observed_at / valid_time
- metric_id, value, unit
- source_id, source_uri or local source reference
- uncertainty and missingness status
- spatial/temporal scope and provenance

### ModelDefinition / Model IR
- model_id and schema version
- domain and engine_id
- entity definitions and state variables
- parameters, ranges, units and defaults
- equations/rules and declared assumptions
- initial and boundary conditions
- validation plan and privacy classification

### RunManifest
- run_id, model version, source commit and engine version
- parameter set, seed(s), dataset hashes and solver
- start/end time, environment and resource limits
- test/validation protocol and output references

### RunResult / Feedback
- observations and model outputs
- residuals, uncertainty, diagnostics and metrics
- claim status (MODEL/HYPOTHESIS etc.)
- reproduction details and limitations

### Agent contract
- Observation → DecisionPolicy → Action → Resolver → Feedback
- action legality is validated by the domain engine
- decision policy is exchangeable: random, greedy, adaptive softmax, genetic policy, neural policy, later search-based policies

Grid ticks, physical time steps and career-planning periods must retain their domain semantics; they must not be silently treated as equivalent.

## 5. Roadmap

### Phase A — preserve and make the current source reproducible
- Keep v0.8 and v0.9 branches intact.
- Store the complete v0.9 source, README, test script, sample run and package hashes in the repository or an attached release.
- Record the 65-check test run and Java version.
- Maintain an explicit changelog and a short session handoff.

### Phase B — v0.10 portable data contracts
- Add JSON Schemas for Observation, ModelDefinition, RunManifest, RunResult and event JSONL.
- Add validation errors for missing units, invalid types, missing sources and incompatible metric definitions.
- Add schema-version migration tests and small non-sensitive example datasets.

### Phase C — v0.11 calibration and evaluation
- Add multiple-parameter sweeps and sensitivity analysis.
- Separate calibration, validation and final test windows.
- Compare against simple baselines.
- Report MAE/RMSE/bias/interval coverage as appropriate to the data.
- Warn about non-identifiable parameters and overfitting.

### Phase D — v0.12 Matrix Core adapter
- Map existing World, Agent, Observation, Action, Feedback, Experiment and Result types to the shared contracts.
- Reproduce the documented Commons experiments using identical seeds and parameters.
- Preserve counterexamples: the existing Arena shows that a strategy can perform well in a homogeneous population but be exploited in a mixed one.

### Phase E — v0.13 Gravity adapter
- Find the exact GravityEngine source and its build instructions.
- Verify dimensions and SI units.
- Test the two-body orbit against an analytical reference.
- Measure energy and angular-momentum drift.
- Compare integrators and test close encounters, softening and collisions.
- Keep game-specific entropy modifications labelled as model/game mechanics, not standard Newtonian physics.

### Phase F — v0.14 LUMINI vertical slice
- Define a small grammar for a simple population/regeneration model.
- Parse source into a typed AST.
- Check names, ranges and units.
- Emit versioned JSON Model IR.
- Dispatch only to a registered, permissioned Grid Lab adapter.
- Never execute arbitrary model-generated shell commands.

### Phase G — v0.15 first real-data adapter
- Select one scoped, openly accessible dataset with documented terms.
- Save an immutable source snapshot, retrieval date, license, metric definition, units, spatial/temporal scope, uncertainty and checksum.
- Compare model and observed quantities only when they are operationally comparable.
- Keep calibration data separate from held-out validation data.
- Do not publish personal data.

### Phase H — Infinity Lab / multi-domain visual workspace
- Keep the canvas a view of simulation state, not the owner of that state.
- Use variable-driven projections and labels.
- Add dynamic chunk streaming only after coordinate, persistence and performance invariants are tested.
- Provide linked code, run console, experiment table, charts and knowledge graph views.

### Phase I — future and career scenario lab
- Use user-approved skills, evidence, time constraints and preferences stored privately.
- Use public occupational and labour-market datasets with dates and provenance.
- Present several scenarios, learning paths, controllable variables and uncertainty ranges.
- Treat outcomes as conditional scenarios, not deterministic forecasts or job guarantees.

## 6. Scientific and evidence discipline

Maintain two independent labels:

**Statement status:** FACT / MODEL / HYPOTHESIS / OPEN / FICTION.  
**Software status:** PLANNED / PROTOTYPE / IMPLEMENTED / TESTED / VERIFIED.

Every result should state what was measured, under which settings, with which sources, version and tests. A result from a simulation establishes a result inside that model; it does not automatically establish a claim about nature, society or one person's future.

For real-world forecasts, use independent validation, uncertainty intervals, baseline comparisons, residual inspection and explicit limits of applicability.

## 7. Positive development and human-centred objectives

The project should support constructive outcomes while respecting that a person's wellbeing is not reducible to productivity or project progress.

Design goals:
- safety and stability before speed;
- physical and psychological wellbeing;
- agency, freedom and informed choice;
- truthfulness, traceability and correction;
- communication, cooperation and mutual benefit;
- curiosity, creativity, play and meaningful contribution;
- regeneration and sustainable effort.

A practical improvement loop:
1. notice the current state without judgement;
2. identify one controllable factor;
3. choose a small, achievable action;
4. observe what actually changes;
5. keep what helps and revise what does not;
6. include rest and social connection as valid actions.

The Hawkins Map of Consciousness may be used only as a symbolic reflection vocabulary, not an objective score, diagnosis or ranking. Its numeric calibration is not established by robust independent evidence. See the companion document on human flourishing and reflective use.

## 8. Next-session handoff

Start from branch `matrixgame-continuity-wellbeing-2026-11-17`.

1. Read this roadmap and the v0.9 Experiment Studio checkpoint.
2. Confirm exact source artifact hash and rerun tests before making code changes.
3. Implement v0.10 Observation and RunManifest schemas and validators.
4. Create one export/import round-trip test.
5. Only then connect Matrix Core.

Do not ask the user to re-explain the broad vision. Use this repository checkpoint, inspect the current code and tests, and ask for clarification only when a domain decision is genuinely ambiguous.

## 9. Key documents

- [Cross-Project Integration Map](MATRIXGAME_CROSS_PROJECT_INTEGRATION_MAP_2026-11-17.md)
- [Measurement-to-Model Pipeline](MATRIXGAME_MEASUREMENT_TO_MODEL_PIPELINE_2026-11-17.md)
- [Universal Grid Lab v0.9 Experiment Studio](MATRIXGAME_UNIVERSAL_GRID_LAB_V09_EXPERIMENT_STUDIO.md)
- [Human Flourishing and Positive Feedback Framework](MATRIXGAME_HUMAN_FLOURISHING_AND_POSITIVE_FEEDBACK_2026-11-17.md)
- [Universal Knowledge Engine](UNIVERSAL_KNOWLEDGE_ENGINE.md)
- [Knowledge Node and Evidence Schema](KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md)
- [LUMINI Compiler, Simulation and Career Engine](LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md)

**Leitgedanke:** Make the game better than the players found it. Make each claim easier to test, each module easier to reuse, and each next step easier to continue.
