# Matrixgame continuity checkpoint

**Date:** 2026-11-10  
**Branch:** `agent/continuity-2026-11-10`  
**Status:** research/specification checkpoint; documentation and machine-readable profile registry added; no merge to `main`.

## Purpose

Preserve continuity across sessions and tools. Continue from verified existing work, make small testable improvements, and do not create parallel architectures or claim that planned features already exist.

## Read this checkpoint with

1. [Continuity index](CONTINUITY_INDEX.md)
2. [Browser research synthesis 2026-11-10](BROWSER_RESEARCH_SYNTHESIS_2026-11-10.md)
3. [Immune & Human Organism Entity Catalog v0.1](../biology/IMMUNE_ORGANISM_ENTITY_CATALOG_v0.1.md)
4. [Machine-readable immune/organism profiles v0.1](../biology/immune-organism-profiles.v0.1.json)
5. [Immune organism Entity-Relationship diagram](../biology/IMMUNE_ORGANISM_RELATIONS_v0.1.mmd)
6. [Implementation backlog](IMPLEMENTATION_BACKLOG_2026-11-10.md)

## Current source-of-truth map

| Area | Existing location | Status / rule |
|---|---|---|
| Shared game state | `matrixgame-protocol/game-state.schema.json`, `game-state.example.json` | Existing protocol; reuse and version it |
| Deterministic JS prototype | `matrixgame-protocol/engine.js` | Existing prototype, not a public multiplayer service |
| Experiment core | `matrixgame-core/matrixgame-core/` | Java 21, compact dependency-free core; preferred base for reproducible experiments |
| Older physics/game code | Root `src/` and legacy Java projects | Preserve and audit; do not assume all physics/game rules are validated |
| LUMINI language/compiler | [Architecture proposal](LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md), draft [PR #23](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/pull/23) | A small Java 21 compiler slice exists in an open, unmerged PR; not yet connected to Matrix Core; only kg, m, s supported |
| Gaia Earth Lab | draft [PR #26](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/pull/26) | Java 21 water-balance prototype with 24 dependency-free tests per PR and successful CI claim; synthetic demonstration, not calibrated field model |
| Positive Change Lab | draft [PR #25](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/pull/25) | Design specification only; not an implemented application |
| ODC hypothesis audit | draft [PR #29](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/pull/29) | Python analyzer and tests per PR; no empirical sample dataset yet; density-only hypothesis is not established |
| Knowledge graph / KIM | `docs/UNIVERSAL_KNOWLEDGE_ENGINE.md`, `docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md`, KIM atlas on [PR #26 branch](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/blob/feature/gaia-water-cycle-mvp-2026-11-08/docs/KIM_INTEROPERABILITY_ATLAS_2026-11-08.md), [Issue #27](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/27) | Source atlas and proposals exist; not a live cross-platform sync service |
| Immune/organism ontology | `biology/IMMUNE_ORGANISM_ENTITY_CATALOG_v0.1.md`, `biology/immune-organism-profiles.v0.1.json`, `biology/IMMUNE_ORGANISM_RELATIONS_v0.1.mmd` | New research-backed specification, JSON starter profiles and ER diagram; not yet a Java runtime or calibrated biological simulator |
| Collective improvement lifecycle | `docs/COLLECTIVE_IMPROVEMENT_NETWORK.md` | Proposal: observe → describe → triage → analyze → quest → implement → verify → publish |
| Integration reference kit | Local `Matrixgame_Integration_Kit_v0.1.zip` | Separate artifact; examples previously tested; adapters are not deployed |

## Immune model decisions

- Model the organism as typed layers: cells, microbes/virions, molecules, tissue/compartment structures, fields/resources, interactions and event records.
- Do not use one giant vector. Use a common entity envelope plus type-specific state and capabilities.
- Keep measurement values with units and context. Keep normalized 0–1 values explicitly marked MODEL_START.
- Body-wide estimates and blood concentration ranges are different data types.
- Virus, bacterium, fungus, archaea, symbiont, toxin and tumor cell are distinct entity kinds.
- Neutrophil chemotaxis 18 µm/min is tied to a specified fMLP microfluidic assay; macrophage ~1 µm/min is an in-vitro review estimate. Other speed defaults remain uncalibrated.
- Whole-body immune-cell counts are reference estimates and must not be used as literal counts in a small local agent simulation.
- Oxygen transport, immune activation and tissue damage are linked but separate processes.
- Full HRA coverage contains thousands of cell labels; current hand-maintained list is functional coverage, not exhaustive.

## Recent working evidence

1. The immune-system desktop screenshot showed a running Swing/Java visualization. The console reported 18 pathogens, 7 memory cells (2 active), 15 symbionts, inflammation 0.72, 2 memory hits and 103 entities. Displayed values stayed unchanged from tick 150 through 1650. This is a diagnostic clue, not proof of a frozen loop.
2. A separate latest `main` commit, `0dccb0d3f0e537192a6cb90902eb0881784045d7`, added the monolithic Swing Unit-Fight source file `Matrixgame KI Game Unit Fiight Sim 10.11.2026 00:04 Andreas Schneider`. It has group selection, tactical move/focus, WASD/QE, zoom/pan, team/king mechanics and an entropy pool. Source was inspected but not compiled or run in this review.
3. Code-review risks in that Unit-Fight file: unseeded `Math.random()`; ratio-based random values with very large tails; random selection with replacement plus a 90% skip for ordinary units; simulation state changed off the Swing EDT; entropy starts at 1,000,000 while regeneration is capped at 100. These are review findings to test, not confirmed runtime failures.
4. Repository search found legacy immune prototypes, including `MatrixgameImmuneSystemv50`, older `BioEntity` roles PATHOGEN/SYMBIONT/GUARDIAN and an old I/O/O agent proposal. In v50, displayed survivalProbability is simply 1 - pathogenCount/200, and Guardians automatically kill nearby pathogens; there is no biological time calibration, antigen specificity or adaptive response in that small prototype. Preserve it as historical material, not evidence or a reference physiology engine.
5. The machine-readable JSON profile file was parsed successfully during authoring. This checks JSON syntax only; no biological model execution or calibration has been performed.
6. The latest browser inventory contained around 40 open tabs. Some public profiles and selected video page metadata were inspectable; other YouTube transcripts, private/social inbox pages, Gemini, Qwen, Drive and Colab were not fully accessible. See the browser research synthesis for per-source status.
7. Google Docs journal content was visible only in part; one tab displayed a notice about unsaved changes. Verify save status before relying on that document.
8. A public-repository search previously found old archived records matching personal banking/contact-data patterns. Keep this as a separate P0 privacy review. Do not copy values into issues, notebooks, reports or public logs. Removing the current file does not remove Git history.

## Continuity and safety rules

- Treat repository files and schemas as the canonical project record; conversations, browser tabs and model outputs are context or proposals.
- Keep `FACT`, `MODEL`, `HYPOTHESIS`, `OPEN` and `FICTION` distinct from implementation state `PLANNED`, `PROTOTYPE`, `IMPLEMENTED`, `TESTED`, `VERIFIED`.
- Every result should record model version, code commit, input parameters, random seed when applicable, tick count, units, data source and uncertainty.
- Make changes on a feature branch; prefer small commits and PRs. Do not merge or publish consequential changes without review.
- Never move private Drive/Docs material into public GitHub or public Colab by default. Classify as `public`, `internal` or `private` before import.
- Never store credentials, tokens, bank data, private contact records or private messages in public source, events, notebooks or issue text.
- Public posts, comments, DMs, shares, account changes and repository merges require explicit human approval at the point of action.
- A browser tab is not proof of API access. A title/link is not proof that its contents were read. A source is not reusable just because it is accessible.
- A model may prepare code and checks in the current session, but it cannot continue working after the session ends unless a separately configured automation is active.

## Immediate sequence

1. **P0 — Privacy review:** identify candidate public files using paths/categories only; decide disposition; plan history cleanup separately.
2. **P1 — Unit-Fight baseline:** compile the exact latest source in an isolated workspace; record Java version, errors and warnings; do not refactor yet.
3. **P2 — Deterministic simulation:** introduce a seeded random generator and explicit bounded distributions; add a fair, documented per-tick action scheduler and trace counters.
4. **P3 — Immune typed state:** implement EntityEnvelope and a small set of typed immune-cell profiles from the new catalog, without refactoring unrelated modules.
5. **P4 — Immune tick diagnostic:** instrument the distinct immune-system simulator with state hashes and event/action counters. Do not conflate it with the Unit-Fight file.
6. **P5 — Biological event log:** implement one deterministic tissue-patch scenario with chemotaxis, phagocytosis, antigen presentation and tissue-damage events.
7. **P6 — Protocol adapter:** map simulator events to the existing versioned JSON protocol; keep model definition, game state, event log, run manifest and result distinct.
8. **P7 — Cross-language parity:** use identical deterministic fixtures across Java, JavaScript and Python.
9. **P8 — Source bridge:** extend KIM Issue #27 with permission-aware, source-attributed knowledge records; no automatic posting or private-content import.
10. **P9 — Research modules:** prototype SpectrumLab and CarbonLoop separately, with explicit units, uncertainty and synthetic test cases.
11. **P10 — LUMINI:** improve its draft compiler MVP and only connect it to a simulation engine after Model IR schema and engine contracts are reviewed.

## Review note

The continuity branch contains documentation and profile-specification updates only. It does not change `main`, modify Google Drive, deploy a service, publish social content, or resolve historical privacy findings.
