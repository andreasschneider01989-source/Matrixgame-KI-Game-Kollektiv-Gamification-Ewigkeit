# MATRIXGAME INFINITY PLAY ENGINE — Integration Roadmap v0.1

**Date:** 2026-11-27  
**Status:** Exploratory design; not yet implemented or merged  
**Target branch:** feature/second-earth-regeneration-mvp-2026-11-27

## Purpose

Turn the existing collection of chat-playable prototypes and scientific simulators into a modular game platform. The platform should make it possible to add new worlds, rules, agents, scenarios and visualizations without rewriting the stable simulation core.

This document is an implementation plan, not a claim that the platform already exists.

## Design principle

**One deterministic simulation core; many replaceable worlds, agents, rule sets and interfaces.**

Keep the following responsibilities separate:

1. **World model** — state variables and domain rules (for example, Second Earth ecology).
2. **Simulation runtime** — deterministic state transitions, time steps, seeds and event log.
3. **Agent layer** — human, scripted, search-based or AI-proposed actions.
4. **Validation layer** — schema checks, safety constraints, invariants and test scenarios.
5. **Presentation layer** — chat widgets, 2D/3D views, dashboards and later multiplayer clients.
6. **Knowledge/compiler layer** — LUMINI parses intent into a typed intermediate representation; validators inspect it; a compiler/runtime executes only approved models.

The language model may propose models and actions. It must not silently change authoritative physics, rules or simulation state.

## Stable core and exploratory modules

### STABLE MODE

- Preserve existing Matrixgame Java projects and tested behavior.
- Preserve the current Second Earth regeneration MVP and its regression tests.
- Do not overwrite existing modules while prototyping the integration.
- Keep simulation state changes deterministic and reproducible.

### EXPLORATORY MODE

- Build adapters and new game modes in isolated packages.
- Use synthetic data until data provenance and calibration are documented.
- Label illustrative coefficients and fictional scenarios explicitly.
- Promote a module only after tests, documentation and review.

## Proposed shared contracts

These are proposed names and responsibilities, not existing implemented APIs.

- `GameDefinition`: id, version, title, description, ruleset reference, supported actions.
- `ScenarioManifest`: scenario id, seed, initial state, external pressures, duration, parameter set and provenance.
- `WorldState`: versioned, serializable domain state.
- `AgentProfile`: id, role, policy identifier, capabilities and constraints.
- `AgentAction`: actor, action type, parameters and rationale/provenance.
- `SimulationEvent`: tick, actor, event type, before/after references and validation status.
- `RunManifest`: engine version, model version, seed, parameters, inputs and output hashes.
- `RunResult`: time series, metrics, events, warnings and validation summary.

All contracts should be versioned. Unknown fields should be handled deliberately; invalid or unsafe actions should be rejected or marked for review, not silently coerced.

## Runtime loop

1. Load and validate a GameDefinition and ScenarioManifest.
2. Initialize the domain-specific WorldState.
3. Ask each enabled agent for a proposed AgentAction.
4. Validate the action against capabilities, rules and safety constraints.
5. Apply accepted actions in a deterministic order.
6. Advance the domain model by one tick.
7. Record events, metrics and provenance.
8. Return observations to agents; learning may propose policy changes for a later run.
9. Export a RunManifest and RunResult so the run can be reproduced.

The simulation engine remains authoritative. AI-generated suggestions are proposals until validated and accepted.

## First vertical slice: Second Earth adapter

Build the smallest useful integration before attempting a universal engine:

- Wrap the existing Second Earth state and `advance()` function behind a `SimulationAdapter`.
- Define one versioned scenario manifest with a fixed seed and explicit illustrative parameters.
- Add two scripted agents: a Regeneration Planner and a Resource Steward.
- Have both agents propose actions; validate and apply actions through the existing safety gate.
- Record a compact event log and time series for biodiversity, clean energy, wellbeing, trust and safety.
- Re-run the same manifest twice and assert identical outputs.
- Add a negative test proving an action that violates the safety gate cannot be applied.

Do not yet add multiplayer, autonomous online learning, real-world intervention or GPU/3D dependencies.

## LUMINI connection

Keep the compiler pipeline explicit:

**Human intent → parsed specification → typed intermediate representation → validation → executable model → deterministic runtime → test results → feedback.**

The LUMINI IR should describe variables, units, bounds, actions, dependencies, update rules, constraints and provenance. The first adapter can use a hand-authored JSON manifest; compiler integration follows only after the manifest schema is stable.

A directed acyclic graph can represent calculation dependencies, but time-dependent simulation requires explicit state transitions and feedback loops.

## Chat-playable and commercial-grade progression

A chat widget is a presentation client, not the simulation engine. Over time, the same engine can support richer interfaces:

- Level 1: sliders, buttons, event log and simple charts.
- Level 2: selectable entities, inventories, missions and branching objectives.
- Level 3: spatial maps, camera controls, zoom/pan and 2D/3D rendering.
- Level 4: multi-agent campaigns, persistent worlds, save/load and scenario editor.
- Level 5: collaborative sessions, modding interfaces, replay, leaderboards and marketplace-ready content packaging.

Each level must preserve reproducibility and user control. A visually impressive interface does not by itself validate the underlying model.

## Acceptance criteria for the first vertical slice

1. Scenario manifest validates against a versioned schema.
2. Same seed and same inputs produce identical run results.
3. Invalid actions are rejected and logged.
4. Safety gate cannot be bypassed by an agent proposal.
5. Event log and metrics can be exported and inspected.
6. A run can be replayed from its manifest.
7. Existing Second Earth tests remain unchanged and passing.
8. Documentation clearly distinguishes measured data, calibrated parameters, illustrative model assumptions and fiction.

## Roadmap

- **Sprint A — Contracts:** schema and validation tests for GameDefinition, ScenarioManifest and RunManifest.
- **Sprint B — Adapter:** wrap Second Earth without changing its domain logic.
- **Sprint C — Agents:** two deterministic scripted agents and action arbitration.
- **Sprint D — Replay:** event log, metrics export and deterministic replay tests.
- **Sprint E — Presentation:** chat-playable control panel and scenario comparison.
- **Sprint F — LUMINI:** compile a restricted, typed model description into the adapter configuration.

## Risks and controls

- **Scope expansion:** one vertical slice only; no parallel engine rewrite.
- **False scientific confidence:** explicit provenance, calibration status and uncertainty.
- **AI unpredictability:** AI proposes; deterministic validators and runtime decide.
- **Regression:** isolated branch, unchanged stable modules and regression tests.
- **Complexity overload:** ship one meaningful playable loop before adding more systems.

## Next action

Implement Sprint A and Sprint B on the exploratory branch. First inspect the actual repository tree and current branch head; do not assume paths or overwrite files based only on this roadmap. Run focused tests and the full repository suite before opening a reviewable pull request.

**North star:** Make the game better than we found it — by making every new world easier to build, test, understand and share.
