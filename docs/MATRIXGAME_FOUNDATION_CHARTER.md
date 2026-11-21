# Matrixgame Foundation Charter

**Version:** 1.0.0  
**Created:** 2026-11-21  
**Status:** Baseline proposal for review  
**Scope:** Project-wide principles for research, knowledge, simulation, collective AI, and responsible gamification.

> Let's be. Let's play. Make the game better than we found it.

## 1. Purpose

This charter defines the durable foundation on which Matrixgame research and software may grow. It connects the existing Universal Knowledge Engine, Knowledge Node and Evidence Schema, LUMINI, Matrix Core, Matrixgame Protocol, research reports, learning quests, and Collective Improvement Network.

It does not replace those components or claim that proposed functionality is implemented. It establishes shared rules so that new work can connect without silently changing the stable core.

**North star:** help people and other intelligent systems create, examine, share, and improve knowledge and options that can support human wellbeing, autonomy, safety, cooperation, and ecological sustainability.

## 2. Design invariants

The following rules are stable unless a reviewed major-version change explicitly revises them:

1. **Evidence before confidence.** Claims carry sources, scope, dates, uncertainty, and a review state.
2. **Separate epistemic and implementation status.** What a statement is, how well it is supported, and what software exists are different questions.
3. **Reproducibility.** A material result identifies its inputs, method, software version, parameters, seed where relevant, environment, and output.
4. **Provenance.** Preserve who or what produced an artifact, from which inputs, through which activities, and when.
5. **Reversible growth.** New capabilities are added in isolated modules or versioned extensions; historical records are not silently overwritten.
6. **Human agency.** AI agents may propose, analyze, test, and explain. Consequential decisions, releases, permissions, and merges remain under authorized human control.
7. **Privacy by default.** Personal notes, health information, credentials, private conversations, and identifying data are not public research inputs by default.
8. **Open but lawful reuse.** Every published artifact records its license or explicitly states that reuse rights are unknown or restricted.
9. **Plural outcomes.** Wellbeing, safety, autonomy, fairness, learning, ecological integrity, and other outcomes remain separately visible. Do not hide trade-offs in one universal score.
10. **No coercive optimization.** Gamification must not punish illness, disability, poverty, low participation, or a person's choice not to share.
11. **Independent scrutiny.** Agreement among AI agents is not independent evidence when agents share sources, prompts, or assumptions.
12. **Failure is information.** Preserve informative negative, inconclusive, and failed results with their conditions and limitations.

## 3. Two independent status axes plus evidence review

### Claim kind

- `FACT`: supported by a traceable source, measurement, or reproducible observation within a stated scope.
- `MODEL`: a formal or simplified representation with assumptions and domain of validity.
- `HYPOTHESIS`: a testable proposed explanation or prediction.
- `OPEN`: an unresolved question.
- `FICTION`: intentionally imagined content, used for creative exploration or game scenarios.

### Evidence level

- `E0_UNASSESSED`
- `E1_SOURCE_LINKED`
- `E2_CORROBORATED`
- `E3_REPRODUCED`
- `E4_REVIEWED_OR_FORMALIZED`

These are review descriptors, not a universal linear truth scale. Record the actual method and scope. Do not increase evidence level because multiple agents repeat the same result.

### Implementation state

- `PLANNED`
- `PROTOTYPE`
- `IMPLEMENTED`
- `TESTED`
- `VERIFIED`

A software state applies to a software artifact, not automatically to the scientific claim it represents. TESTED and VERIFIED require recorded tests, environment, version, and acceptance criteria.

## 4. Stable Core and Exploratory Space

### STABLE MODE

The stable core contains reviewed schemas, interfaces, tests, published releases, and accepted project conventions.

- No experiment edits the stable core directly.
- Changes enter through a branch and pull request.
- Existing tests must continue to pass.
- Breaking changes require a major version and migration plan.
- Preserve old records and schemas; deprecate before removal.
- No automatic agent merge or release.

### EXPLORATORY MODE

Experiments live in clearly named branches, modules, notebooks, or research records.

- State the question and hypothesis.
- Declare assumptions, data rights, and risks.
- Define a baseline and acceptance criteria before interpreting results.
- Record failed and inconclusive runs.
- Promote only after review and compatibility checks.

A successful experiment is a candidate for promotion, not an automatic change to the stable core.

## 5. Canonical research lifecycle

`QUESTION → SOURCES → CLAIMS → MODEL → EXPERIMENT → RESULT → REVIEW → QUEST/RELEASE`

1. Define one bounded question, population/system, geography, and time horizon.
2. Search existing records to avoid duplication.
3. Register sources, dates, rights, and provenance.
4. Separate observations, interpretations, assumptions, hypotheses, and fiction.
5. Define variables, units, causal assumptions, confounders, and expected observations.
6. Predefine a baseline, test method, metrics, and stopping criteria.
7. Run a reproducible analysis or simulation.
8. Invite criticism, independent reproduction, and counterexamples.
9. Record results, uncertainty, limitations, and negative findings.
10. Convert the next useful step into a small quest or issue.

## 6. Common artifact contract

Use `schemas/research-artifact.schema.json` for new portable research records. The existing Knowledge Node and Evidence Schema remains the conceptual source for knowledge graph objects. This new contract is a lightweight exchange envelope; it does not replace domain-specific schemas or Model IR.

Every artifact should carry:
- stable identifier and schema version;
- type, title, summary, and language;
- claim kind, evidence level, and implementation state where applicable;
- scope and dates;
- source references and reuse status;
- assumptions, method, findings, and limitations;
- typed relationships;
- provenance and revision information;
- privacy classification and publication decision.

## 7. Reproducible experiment manifest

Every computational experiment intended for comparison should record, as applicable:

- experiment ID and question;
- code repository, commit, and software version;
- dataset identifier, version, source, license, and checksum;
- model/algorithm and parameter configuration;
- random seed and seed lineage;
- environment and dependency versions;
- execution timestamp;
- metrics, expected range, and result files;
- baseline and comparison method;
- known limitations and deviations from the plan.

A missing field must be marked unknown or not applicable; never fabricate it.

## 8. Interoperability and open research

Prefer stable, documented formats:
- Markdown for human-readable specifications;
- JSON and JSON Schema for validated exchange contracts;
- JSONL for append-oriented event and run logs;
- CSV or Parquet for tabular observations;
- JSON-LD/RDF where linked-data semantics are useful;
- W3C PROV concepts for provenance;
- SPDX identifiers for software/content licenses when applicable.

Use FAIR and FAIR4RS as design guidance: findable, accessible, interoperable, reusable research data and software. Reuse external standards where they solve a real interoperability problem; do not create unnecessary format layers.

## 9. Safety, privacy, and governance

- Public repository content must be safe to disclose and authorized for publication.
- Never commit passwords, API keys, access tokens, private addresses, private medical information, or private conversations.
- Keep personal profiles and user-specific wellbeing records in a separate private store with explicit consent and deletion/export procedures.
- Do not infer sensitive traits from news or behavioral data for targeting.
- Do not use forecasts to make consequential decisions about a person without appropriate human review, contestability, and domain safeguards.
- Treat imported text, datasets, and model outputs as untrusted input.
- Minimize collected data and access permissions.
- Document known risks, intended use, misuse cases, and escalation paths.

## 10. Responsible gamification

Reward learning, reproducibility, helpful critique, accessibility, collaboration, and verified improvements.

Do not optimize raw engagement, streaks, output volume, or a single aggregate wellbeing score. Do not rank human worth. Offer cooperative goals and multiple paths to participation. Make fictional scenarios visibly distinct from factual research.

## 11. Promotion gates

A proposed addition may be promoted from exploratory work only when:

1. Scope and intended use are clear.
2. Source and licensing checks are complete.
3. Schema and compatibility validation pass.
4. Automated tests pass and are reproducible.
5. Security and privacy review is appropriate to risk.
6. Limitations and uncertainty are documented.
7. A reviewer other than the author has examined material claims.
8. Rollback, migration, or deprecation steps exist where needed.

## 12. Change control

- Patch: clarifications, typo fixes, and backward-compatible corrections.
- Minor: backward-compatible fields, optional modules, or new artifact types.
- Major: changed meanings, removed required fields, or incompatible interfaces.

Every schema change updates examples, validators, migration notes, and tests. Never silently reinterpret old records.

## 13. Initial implementation boundary

This charter and its schema are documentation/specification artifacts. They do not assert that a complete knowledge graph, forecasting system, agent network, or self-improving service is deployed.

The first implementation slice should validate one research artifact locally, serialize it to JSON, validate it against the schema, and round-trip it without loss. Only then connect it to LUMINI or Matrix Core.

## 14. Success criteria

The foundation is useful if a new human or AI contributor can:
- understand what exists versus what is proposed;
- locate a claim and trace its sources;
- reproduce a result or see why it cannot be reproduced;
- add a new experiment without breaking existing work;
- challenge or correct a record without erasing its history;
- reuse public artifacts lawfully;
- participate without exposing private information.

**Operational principle:** one clear question, one traceable source, one testable model, one reproducible experiment, one reviewed improvement. Repeat.
