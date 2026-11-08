# Genesis Commons — Cross-Platform Research Synthesis

**Date:** 9 November 2026  
**Status:** Research and integration note; proposals remain distinct from implementation  
**Branch:** `feature/gaia-water-cycle-mvp-2026-11-08`  
**Purpose:** Convert the newly visible Matrixgame research surfaces into reusable, privacy-aware source records and small learning/game modules.

## Executive summary

The newest source review points to one practical architecture decision: **collect source metadata once, preserve provenance, then let separate engines consume the same reviewed records.** The browser tabs are not a single shared database. Some expose readable content, some only a title, and several block access. A source registry now records those differences rather than pretending that every tab has been read.

The most useful technical connection is between the legacy 2021 GravitySim project, the inspected 3D GravityLab HTML prototype, and the current 189-run 2D Gravity Cluster dataset. These should become three versioned experiments connected by a common Run Manifest—not merged into one physical model.

## 1. What was actually inspected

### GitHub — strongest reproducible surface

The main repository README and selected architecture files were read. The README explicitly describes the repository as an evolving collection of research, code fragments, prototypes and documentation, rather than one finished integrated simulator.

The current draft work includes:

- **Gaia PR #26:** Java 21 soil/forest water-cycle prototypes, forcing-series and run-manifest classes, five deterministic review roles, and 24 self-tests. Synthetic examples validate bookkeeping, not rainforest predictions.
- **Gravity PR #4:** 189 deterministic 2D Newtonian softened N-body runs, dashboard, manifest, CSV/JSON exports, and package integrity checks. The contact counts are finite-grid sample outcomes, not universal probabilities.
- **LUMINI PR #23:** a restricted Java 21 parser/dimensional-analysis prototype with deterministic Model IR output, tests and CI. It remains a draft and is not yet integrated with the Matrix Core engine.
- **Positive Change Lab PR #25:** a proposed voluntary wellbeing design. It is documentation, not a deployed service.
- **KnowledgeGap PR #2 and Qwen candidate PR #19:** isolated drafts whose own descriptions list unresolved test or accounting questions.

These PRs remain open drafts. This report does not merge them or change main.

### Original Java GravitySim — recovered as a source link

The public Java projects README identifies the original 2021 3D particle simulator, names the ZIP and demo JAR, and describes the historical entry point. Both binary files are present in the public repository and were retrieved through the GitHub contents API. Their archives have **not** yet been extracted or executed, so their internal code remains unreviewed.

### GravityLab 3D — inspected source artifact

The private library copy contains a self-contained 3D Newtonian N-body prototype. It uses Velocity-Verlet, softened pair interactions, editable body parameters, pause/step/reset controls, trajectory traces, velocity/force vectors, an energy-drift readout, and JSON experiment import/export.

The HTML itself labels its orbit geometry as a **MODEL / ANALOGY**. Its 2+6 arrangement is not a complete neon atom model: it omits the 2s subshell, quantum wavefunctions, spin, Pauli exclusion and a physical Coulomb model. The optional satellite repulsion control is a toy force modification, not electromagnetism. Keep those limits visible in every derivative.

### Browser and platform inventory

| Source surface | Observed state | Safe interpretation |
|---|---|---|
| GravitySimNeu Google Drive folder | Only a 2025 subfolder was visible | No child files or contents reviewed |
| Matrixgame Journal | Partial text and headings visible | Private workspace source; do not mirror body text publicly |
| Tagesziele 13 spreadsheet | Browser rendering error | Cell contents unknown |
| Manus | Project titles and a prompt visible | Project metadata only; no job output audited |
| Meta AI / Gemini / Qwen | Partial conversations visible | Mixture of transferred context and generated ideas; not independent peer review |
| Grok / Mistral / Discord | Browser permission denied | No content read and no actions taken |
| Colab | Notebook titles visible in previous inventory | Code, outputs, access permissions and runtime not reviewed |
| Gen Z regelt | Public page content inspected | The site states an action is planned for 20 Nov 2026 at 14:00 in 34 cities; not independently verified here |
| Morse Code World, EVE Online, SoundCloud, Google Earth, Wikipedia | Titles or landing/search pages only | Creative/learning discovery, not reviewed technical evidence |
| YouTube videos about drones, clouds, space and Lesch | Mainly titles/links; video tabs blocked | No claims extracted from these specific video pages |

The machine-readable file `research/KIM_SOURCE_REGISTRY_2026-11-09.json` records the observed source states. Private workspace URLs are intentionally omitted.

## 2. Matrixgame integration: one core, several worlds

The current Universal Knowledge Engine and LUMINI documents already propose a useful separation:

1. **Source record** — what was found, where, when, with what access and reuse status.
2. **Knowledge Node** — a concept, claim, equation, source, experiment, quest or software component.
3. **Evidence record** — what supports or challenges a claim, including scope and method.
4. **Model IR** — the typed mathematical definition of a simulation.
5. **Run Manifest / Result** — exact engine, version, parameters, units, seed, input data and measured output.
6. **Learning Quest** — a small voluntary task with observable acceptance criteria.

These objects should be linked by stable IDs, not collapsed into one giant file. The new KIM registry implements only the first, small metadata layer. It is not a live crawler or a general knowledge graph.

### World A — GRAVITY FORGE

**Quest:** recover and audit the historical Java simulator; compare its force calculation, integration scheme, units, random sources and initial conditions against the 3D HTML and 2D reference experiments.

**Acceptance criteria:** archive extracted; source inventory captured; original behavior preserved; a documented minimal test reproduces one simple two-body or symmetric configuration; energy/momentum behavior reported with solver, timestep and tolerances.

**Rule:** legacy code is preserved, never silently replaced. Any improved solver is a separately versioned implementation.

### World B — GAIA / ARES / PELAGIA

**Quest:** connect water, atmosphere, land, energy and biomass as separate conservation-aware modules. Gaia is the real-Earth research world; Ares and Pelagia are explicitly fictional scenario worlds until real measurements are attached.

A proposed seven-agent cast can provide different perspectives: water, atmosphere, soil/biology, energy, measurement/data stewardship, ethics/risk, and red-team review. The current Gaia code's five reviewers are deterministic rules, not autonomous LLMs. Future model agents must cite sources, disagree independently and record uncertainty.

**Acceptance criteria:** documented input units; explicit missing-data rules; mass/water/energy balance checks; real forcing data with licence and quality flags before predictive claims.

### World C — THEORY LAB / LUMINI

**Quest:** use the compiler draft to turn a small natural-language model into typed Model IR; reject incompatible units; bind it to a tested engine and write a Run Manifest.

Morse-code translation can become a short language quest: encode, decode, compare, and test a round trip. This is a didactic exercise, not evidence of a universal language theory.

**Acceptance criteria:** source text, grammar version, parser output, IR, unit tests and errors are all preserved; generated source code is never executed merely because an LLM proposed it.

### World D — CIVIC / COLLECTIVE COMMONS

A public civic initiative can become an optional scenario about resource allocation, climate adaptation, education, health and cost of living. The point is to model trade-offs, transparency and public deliberation—not to infer a user's political identity or to publish, join, message or donate on anyone's behalf.

EVE Online can inspire fictional logistics, markets, scarcity and diplomacy mechanics. Those mechanics are game-world references only; real economics requires separate data and assumptions.

### World E — AI GOVERNANCE ARENA

The drone and autonomous-weapons documentary titles suggest a high-level governance quest: map decision authority, human oversight, accountability, error handling, escalation prevention and auditability. Keep this as safety and policy analysis, not operational weapon design or targeting instruction.

## 3. Shared research and learning loop

**Discover → Register → Classify → Source-check → Model → Test → Critique → Teach → Version.**

A contribution earns progress when it improves reproducibility, comprehension, evidence quality, accessibility or constructive criticism—not merely because an agent agrees, a chat is long, or a player spends more time online.

Suggested portable status pairs:

- Claim kind: FACT / MODEL / HYPOTHESIS / OPEN / FICTION.
- Source access: API_READ / BROWSER_READ / TITLE_ONLY / PERMISSION_DENIED / RENDER_ERROR.
- Review: CONTENT_INSPECTED / PARTIAL_REVIEW / NOT_REVIEWED.
- Software: PLANNED / PROTOTYPE / IMPLEMENTED / TESTED / VERIFIED.

A title-only record must never silently become a verified scientific claim.

## 4. Reproducibility and publishing safeguards

- Keep original code and files intact; add versioned improvements beside them.
- Do not place private Drive, Docs, Sheets, notebook, conversation, Discord or account links in a public registry.
- Do not upload copied private conversation text to a public repository.
- A public URL does not by itself prove that the content is licensed for republication.
- AI consensus is not independent evidence. Check source lineage and shared assumptions.
- Keep synthetic examples, model outputs and measured environmental observations visibly distinct.
- The human maintainer approves merges, releases and external social publication.

The registry validator checks IDs, dates, enumerations, internal links and the rule that private/user-workspace records cannot carry a published URL. It uses only the Python standard library. The JSON Schema is provided for external tools and future user interfaces.

## 5. Next quests

1. Extract and inspect the original Java ZIP and JAR; do not execute unknown binaries before a source/security review.
2. Add a small GravityLab JSON schema and deterministic regression tests.
3. Connect LUMINI Model IR to the shared Run Manifest without mixing in the game-state schema.
4. Select one DWD station and period for the first real Gaia forcing-series example.
5. Add source-specific review queues for Colab notebooks, Drive files, playlists and AI conversations once accessible exports or suitable connectors are available.

**Current delivery state:** source registry, schema, validator and this report are a first reusable documentation/tooling slice on the existing Gaia draft branch. They do not make KIM a live cross-platform synchronization service.

*Let's play: every new portal becomes a traceable source, every model a testable hypothesis, every quest a small reproducible improvement.*
