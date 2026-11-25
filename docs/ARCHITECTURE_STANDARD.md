# Matrixgame Architecture Standard — integration contract

**Version:** 0.1.0  
**Date:** 2026-11-25  
**Status:** PROPOSED STANDARD / initial protocol artifact implemented  
**Scope:** Matrixgame, Collective AI Games, LUMINI, Second Earth and connected prototypes

## 1. Purpose

This document is the navigation and integration standard for the repository. It does not replace the existing research documents, schemas, compiler specification, or historical journals. It defines how those parts should connect, how their status must be described, and how new work should avoid duplicating existing structures.

The design objective is **one coherent system made from independently understandable modules**—not one monolithic application and not a collection of disconnected prototypes.

## 2. Existing canonical sources

Reuse these files before proposing a replacement:

| Concern | Canonical source | Current scope |
|---|---|---|
| Repository orientation | `README.md`, `00_START_HERE.md`, `docs/INDEX.md` | Navigation and project status |
| Historical synthesis | `docs/FRAGMENT_6_SYNTHESIS_AND_FUTURE_ARCHITECTURE_2026-11-23.md` | Curated architecture and realization roadmap |
| Knowledge records | `docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md` | Proposed knowledge/evidence model |
| Knowledge architecture | `docs/UNIVERSAL_KNOWLEDGE_ENGINE.md` | Proposed knowledge lifecycle and graph |
| LUMINI compiler | `docs/LUMINI_FORMULA_COMPILER_MVP_2026-11-23.md` | Implemented scalar compiler scope and limits |
| LUMINI IR | `matrixgame-protocol/lumini-model-ir.schema.json` | Specialized compiled-model contract |
| Shared game state | `matrixgame-protocol/game-state.schema.json` | Existing game-state contract |
| Assistance outcomes | `docs/TRANSPARENT_ASSISTANCE_PROTOCOL.md` | Proposed COMPLETE / PARTIAL / failure reporting |
| Canvas and snapshots | `docs/INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md` | Proposed WorldSnapshot / event / view boundaries |
| Responsible gamification | `docs/LEARNING_QUESTS_AND_GAMIFICATION.md` and related wellbeing research | Voluntary learning and autonomy |

The new [Artifact Envelope schema](../matrixgame-protocol/artifact-envelope.schema.json) is a shared metadata wrapper. It does not supersede any specialized schema in this table.

## 3. System flow and module boundaries

The target flow is:

~~~text
Human / AI / approved source input
                |
                v
       Ingestion and classification
                |
                v
   Knowledge records + evidence provenance
                |
                v
    LUMINI source -> typed Model IR
                |
                v
       Registered simulation engines
                |
                v
 RunManifest + events + versioned WorldSnapshot
                |
                v
       Adapters and projections
     /            |             \
 Console        JSON / JSONL    Canvas / UI
                |
                v
       Tests, review and archive
~~~

Cross-cutting requirements apply at every boundary: provenance, schema/version checks, privacy classification, least privilege, human approval for consequential external actions, and transparent reporting of incomplete work.

### Responsibility rules

1. Ingestion records material and its origin; it does not silently convert a transcript or model output into a fact.
2. Knowledge/evidence records describe claims, support, uncertainty, and typed relationships.
3. LUMINI defines model meaning and compiles only supported language constructs into a versioned intermediate representation.
4. An engine is explicitly registered and declares its input/output contract. Unknown engine identifiers must fail closed.
5. A simulation run records model version, parameters, inputs, seed when relevant, engine version, and validation results.
6. World snapshots represent simulation state. A view or camera may project a snapshot but must not become the authority for simulation truth.
7. Adapters translate between formats and user interfaces; they must not duplicate domain logic.
8. Tests and reviewers check a change before it is described as verified or promoted to a stable integration.

## 4. Shared artifact envelope

The envelope can wrap a specialized quest payload. The portable quest-definition contract lives in [`matrixgame-protocol/quest.schema.json`](../matrixgame-protocol/quest.schema.json); its example is [`quest.example.json`](../matrixgame-protocol/quest.example.json). Keep quest definition templates separate from participant-specific quest runs and private reflections. See the [Responsible Gamification Standard](RESPONSIBLE_GAMIFICATION_STANDARD.md) for autonomy, competence, relatedness, recovery, and anti-coercion requirements.



The envelope in `matrixgame-protocol/artifact-envelope.schema.json` provides common metadata for these artifact types:

- knowledge nodes and evidence records;
- equations, experiments, datasets, and simulations;
- quests and decision records;
- software modules and LUMINI Model IR;
- future WorldSnapshots, SimulationEvents, and RunManifests.

The envelope contains identity, type, title, version, timestamps, knowledge status, implementation status, evidence level, privacy class, provenance, relationships, and validation state. The optional `payload_ref` points to the domain-specific file or schema.

**Boundary:** the envelope is not the payload. A LUMINI Model IR remains validated by its own schema; a game state remains validated by `game-state.schema.json`; a future WorldSnapshot needs its own specialized schema. This avoids one enormous schema with incompatible domain rules.

### Independent status dimensions

Keep these fields separate:

- `epistemic_status`: FACT, MODEL, HYPOTHESIS, OPEN, FICTION, or null when the artifact is not a knowledge claim.
- `evidence_level`: E0_UNASSESSED through E4_REVIEWED_OR_FORMALIZED, or NOT_APPLICABLE.
- `implementation_status`: PLANNED, PROTOTYPE, IMPLEMENTED, TESTED, VERIFIED, or NOT_APPLICABLE.
- `validation.status`: NOT_RUN, PARTIAL, PASS, or FAIL for the checks recorded by this artifact.

Do not infer empirical truth from code status. Do not infer software correctness from a scientific evidence label. Agreement between multiple AI outputs is not independent corroboration.

## 5. Stable identifiers and relationships

Use durable, non-personal IDs such as `physics.density`, `software.lumini.formula.scalar`, or `protocol.lumini-model-ir`. Do not encode private names, email addresses, health details, or credentials.

Use the typed relationship vocabulary already defined in the Knowledge Node schema: REQUIRES, DERIVES_FROM, SUPPORTS, CHALLENGES, CONTRADICTS, MODELS, TESTS, IMPLEMENTS, TEACHES, RELATED_TO, and SUPERSEDES.

A relationship is a navigational assertion, not automatic proof. A later graph validator should report missing targets and cycles where those are disallowed; the 0.1 envelope schema does not yet resolve the entire repository graph.

## 6. Dependency and change rules

- Prefer existing canonical modules over new parallel implementations.
- Depend inward on stable contracts; keep UI, engines, and research sources behind adapters.
- Keep domain code independent of browser camera, rendering, and presentation state.
- Do not let an adapter silently mutate source records or simulation truth.
- Keep stable code protected; put experiments on isolated feature branches.
- Make version changes explicit when a public contract changes.
- Avoid breaking field renames; add a migration note and compatibility tests.
- Do not execute generated or imported code merely because it parses.
- Keep personal and sensitive data out of public artifacts by default.

## 7. Minimum quality gate for a new module

A module is ready for review only when it has:

1. one stated responsibility and a stable identifier;
2. defined inputs, outputs, units where applicable, and failure behavior;
3. a versioned schema or explicit interface;
4. provenance and evidence boundaries;
5. at least one reproducible example or test;
6. documented limitations and what has not been verified;
7. an owner/review path and a privacy/licensing check appropriate to its scope.

Use the repository's existing labels. A file being present means it exists; it does not automatically mean the feature works. A passing syntax check is not a runtime test. A passing unit test is not scientific validation.

## 8. Development workflow

For each change:

1. Search the repository and related project records for an existing implementation or specification.
2. Identify the canonical source of truth and the smallest gap.
3. Write a short change proposal with scope and acceptance criteria.
4. Implement on an isolated branch without altering stable functionality unnecessarily.
5. Add or update tests and examples.
6. Run the relevant checks and record exact results and environment.
7. Update the documentation index and cross-links.
8. Open a reviewable pull request; do not describe an unmerged branch as part of the stable main release.

For AI-assisted work, report outcome states in the spirit of the Transparent Assistance Protocol: COMPLETE, PARTIAL, REFUSED_SCOPE, NEEDS_INFORMATION, TOOL_UNAVAILABLE, EXECUTION_ERROR, or UNVERIFIED. State what changed and what was not checked.

## 9. Implementation roadmap

| Stage | Deliverable | Status |
|---|---|---|
| 0.1 | Shared Artifact Envelope JSON Schema, example, and repository smoke test | Implemented on this feature branch; review pending |
| 0.2 | Reusable loaders and full JSON Schema validation in supported clients | Planned |
| 0.3 | Specialized WorldSnapshot, SimulationEvent, and RunManifest schemas | Planned; design exists in Canvas document |
| 0.4 | Browser Canvas and one simulation adapter reading the same snapshot | Planned |
| 0.5 | Java/Python adapter contract and reproducible cross-engine fixtures | Planned |
| 0.6 | Graph reference checking, migration tests, and collective review tooling | Planned |

Do not skip the schema and compatibility work in order to add more visual or agent features. The interface is what lets those features compose later.

## 10. First acceptance criteria

The initial interface slice is accepted when:

- the envelope schema is valid JSON Schema Draft 2020-12;
- the example includes all required fields and uses the declared status vocabularies;
- the smoke tests verify schema/example consistency without adding runtime dependencies;
- protocol and documentation entry points link to the same canonical files;
- no existing game-state or LUMINI-specific schema is replaced;
- no claim is made that WorldSnapshot or a collective agent network is already implemented.

The smoke test is deliberately limited. It is not a substitute for a full Draft 2020-12 validator; add one when a validator dependency or shared runtime is deliberately adopted.
