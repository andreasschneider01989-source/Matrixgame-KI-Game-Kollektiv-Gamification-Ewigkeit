# Matrixgame implementation backlog — 10 November 2026

This backlog prioritizes continuity, reproducibility and evidence over feature count. Each item is deliberately small enough to review independently.

## P0 — Public-data privacy review

**Goal:** prevent personal or confidential information from spreading further.

Acceptance criteria:
- Search current public files and inspect candidate matches locally.
- Record findings using paths and categories only; do not copy values into issues or public reports.
- Decide which files have a legitimate public purpose.
- Redact or remove current files only after human review.
- If sensitive values exist in Git history, plan history rewriting separately and coordinate forks/clones.
- Add a release check; document scanner limitations and false positives.

**Definition of done:** reviewed file list, explicit disposition, no raw values in public issue text, and a documented history-cleanup decision.

## P1 — Immune simulator tick diagnostics

**Goal:** distinguish a genuine equilibrium from a frozen state.

Instrumentation:
- tick, modelVersion, seed, stateHash;
- entitiesBefore/entitiesAfter;
- eventsQueued/eventsApplied;
- births, deaths, replications, kills;
- actionsProposed/actionsApplied/actionsRejected;
- changedStateFields.

Tests:
- same seed + same config → same state hashes;
- a no-op tick is distinguishable from a tick with no events;
- rejected action leaves state unchanged;
- counters reconcile with entity deltas.

**Definition of done:** one run with a trace that explains why the state changed or remained unchanged.

## P2 — Typed entity state and event log

Initial types:
- `Cell`, `ImmuneCell`, `Pathogen`, `Bacterium`, `Fungus`, `Symbiont`, `Tissue`, `Signal`, `Receptor`, `Antigen`, `Resource`.

Minimum shared fields:
- `id`, `type`, `createdAt`, `updatedAt`, `version`, `lifecycleState`, `compartmentId`, `position`, `resources`, `attributes`.

Events are append-only records; current state is derived from the initial state plus accepted events. Do not force every biological property into one giant untyped map.

**Definition of done:** one deterministic scenario can replay its event log and reproduce the final state.

## P3 — Protocol and adapter parity

- Preserve `matrixgame-protocol/game-state.schema.json` as the existing game-state contract.
- Add a separately versioned event schema; do not merge model definition, game state, run manifest and result into one object.
- Keep JSON canonical and language-neutral.
- Add the same input vectors to Java, JavaScript and Python.
- Compare outputs with declared numeric tolerances and store toolchain versions.

**Definition of done:** all three adapters pass the same parity fixtures.

## P4 — Model and experiment quality

First immune toy model:
- pathogen growth;
- innate response;
- delayed adaptive response;
- memory formation;
- inflammation;
- tissue damage and repair.

This is a pedagogical model, not medical prediction. A biological interpretation requires explicit equations, units, parameter sources and domain review.

Experiments:
- baseline / no infection;
- single infection;
- repeat exposure;
- delayed immune activation;
- excessive inflammation;
- evasion variant.

**Definition of done:** reproducible runs, graphs, parameter table, model limitations, and falsifiable expected outcomes.

## P5 — Colab and Google interoperability

- Colab: run parameter sweeps and create plots from versioned JSON/CSV.
- Drive: keep private working material private; only reviewed public artefacts can be exported.
- Apps Script: optional demonstration endpoint only, with narrow access, authentication and idempotency.
- HTTP API: implement only after event schema and validation rules are stable.
- Avoid making a browser tab or an LLM session the canonical storage layer.

**Definition of done:** a clean public sample can move between Java, JSON and Colab without manual field rewriting.

## P6 — LUMINI compiler foundations

Order:
1. glossary with stable concept IDs and definitions;
2. units and type system;
3. grammar and parser;
4. AST;
5. semantic checks and reference validation;
6. Model IR schema;
7. engine adapter;
8. run manifest and result;
9. tests, provenance and human approval.

Translation is not generally bijective. Preserve source language, canonical concept ID, context, alternatives and uncertainty instead of pretending that every natural-language phrase has one exact inverse translation.

**Definition of done:** a small reviewed LUMINI example parses into valid Model IR and runs only through an allow-listed deterministic engine.

## Suggested session-size prompts

- **Tiny (about 1% scope):** inspect one tick log and explain which counters or state hashes are missing.
- **Small (about 5% scope):** implement only the diagnostic counters in the existing simulator class; do not refactor other modules.
- **Medium:** introduce one typed `EntityState` record and two replay tests.
- **Large:** implement the event adapter and cross-language parity fixtures after the schema is reviewed.

Effort percentages are labels for task size, not promises of completion time or measured productivity.
