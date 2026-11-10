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

## P1 — Unit-Fight reproducible baseline

**Goal:** get the latest 10 November Swing Unit-Fight source to compile and replay deterministically before refactoring.

Tasks:
- Compile in an isolated workspace and record JDK version, exact command, warnings and result.
- Add seed input and a single injected PRNG; remove unseeded Math.random calls only after baseline is preserved.
- Replace ratio-based random attributes with named, bounded distributions.
- Define a fair per-tick scheduling policy; current random selection-with-replacement plus skip can starve or repeat units.
- Keep authoritative simulation state updates away from Swing rendering; publish immutable snapshots to the UI.

**Definition of done:** same seed/config yields identical event trace and final state; existing interactions are documented before behavior changes.

## P2 — Immune and organism typed ontology

**Source specification:** `biology/IMMUNE_ORGANISM_ENTITY_CATALOG_v0.1.md` and `biology/immune-organism-profiles.v0.1.json`.

Goal: replace the old all-purpose PATHOGEN/SYMBIONT/GUARDIAN simplification with typed entity kinds and capabilities, incrementally.

Initial types:
- ImmuneCell: Neutrophil, Macrophage, DendriticCell, CD4T, CD8T, BCell, PlasmaCell, NKCell.
- HostCell: EpithelialCell, EndothelialCell, RBC.
- Microbe: VirusParticle, Bacterium, Fungus, Symbiont.
- Tissue, Vessel, Molecule, Field, Resource.

Minimum common identity/location fields:
- entityId, entityKind, typeId, speciesId, compartmentId, positionUm, velocityUmPerMin, lifecycleState, ageMin, viability01, modelProfileId.

Use typed payloads such as CellState, ImmuneCellProfile, PathogenState, TissueState and MetabolicState. Do not force every property into one giant map.

**Definition of done:** JSON profile registry loads; unknown fields/types reject cleanly; one deterministic scenario serializes and deserializes without loss.

## P3 — Immune simulator tick and event diagnostics

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

## P4 — Typed event log and biological interaction slice

Start with one tissue patch and a limited cast: endothelial/barrier layer, resident macrophage, neutrophil, dendritic cell, one pathogen, chemokine field and tissue integrity.

First event types:
- CHEMOTACTIC_SIGNAL;
- ROLLING_AND_ADHESION / TRANSMIGRATION;
- PATTERN_RECOGNITION;
- PHAGOCYTOSIS;
- ANTIGEN_PRESENTATION and COSTIMULATION;
- CYTOKINE_FEEDBACK;
- APOPTOSIS_AND_EFFEROCYTOSIS;
- TISSUE_REPAIR.

Each event records source/target IDs, time, compartment, magnitude/unit, outcome, resource cost, host damage, provenance and epistemic status.

**Definition of done:** one deterministic event log replays to the same final state; failed events do not mutate state.

## P5 — Model and experiment quality

First immune toy model:
- pathogen growth;
- innate response;
- delayed adaptive response;
- memory formation;
- inflammation;
- tissue damage and repair;
- oxygen delivery as a separate coupled subsystem only after the local tissue model works.

This is a pedagogical model, not medical prediction. Biological interpretation requires explicit equations, units, parameter sources and domain review.

Experiments:
- baseline / no infection;
- single infection;
- repeat exposure;
- delayed immune activation;
- excessive inflammation;
- immune evasion;
- symbiont competition.

**Definition of done:** reproducible runs, graphs, parameter table, model limitations, and falsifiable expected outcomes.

## P6 — Protocol and cross-language parity

- Preserve `matrixgame-protocol/game-state.schema.json` as the existing game-state contract.
- Add a separately versioned event schema; do not merge model definition, game state, run manifest and result into one object.
- Keep JSON canonical and language-neutral.
- Add the same input vectors to Java, JavaScript and Python.
- Compare outputs with declared numeric tolerances and store toolchain versions.

**Definition of done:** all adapters pass the same parity fixtures.

## P7 — Colab and Google interoperability

- Colab: run parameter sweeps and create plots from versioned JSON/CSV.
- Drive: keep private working material private; only reviewed public artifacts can be exported.
- Apps Script: optional demonstration endpoint only, with narrow access, authentication and idempotency.
- HTTP API: implement only after event schema and validation rules are stable.
- Avoid making a browser tab or an LLM session the canonical storage layer.

**Definition of done:** a clean public sample can move between Java, JSON and Colab without manual field rewriting.

## P8 — LUMINI compiler foundations

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

## P9 — Research modules: SpectrumLab and CarbonLoop

Keep each module distinct:
- SpectrumLab: reference molecular transitions, synthetic spectra, noise/Doppler effects, multi-line matching and uncertainty.
- CarbonLoop: CO₂/H₂/energy/carbon/water mass and energy balance; explicit process boundary, product fate and lifecycle emissions.

Do not mix these research modules into the immune engine. Reuse the common entity/measurement/provenance layer only where appropriate.

**Definition of done:** a small deterministic model, units, reference source list, synthetic tests and explicit scope limits.

## Suggested session-size prompts

- **Tiny (about 1% scope):** inspect one tick log and identify missing counters or state hashes.
- **Small (about 5% scope):** implement only reproducible scheduling and diagnostic counters in one existing simulator class; do not refactor other modules.
- **Medium:** add the common entity envelope and two replay tests.
- **Large:** implement an event schema adapter and cross-language parity fixtures after the schema is reviewed.

Effort percentages are task labels, not promises of completion time or measured productivity.
