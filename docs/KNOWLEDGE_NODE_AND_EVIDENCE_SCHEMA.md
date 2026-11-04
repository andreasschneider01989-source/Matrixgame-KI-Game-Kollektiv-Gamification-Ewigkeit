# Matrixgame — Knowledge Node and Evidence Schema

**Version:** 0.1.0  
**Status:** Proposed; implementation pending.

## 1. Design requirements

A knowledge record should be traceable, scoped, testable, composable, revisable, portable, and privacy-aware. It should retain source, attribution, dates, version, assumptions, test methods, and permission metadata. Personal notes and private learning data belong outside the public knowledge layer by default.

The following example is illustrative YAML, not a deployed database schema or validated JSON Schema.

## 2. Example record

~~~yaml
id: "physics.density"
type: "concept"
title: "Density"
language: "en"
summary: "Mass per unit volume."
definition:
  text: "Density is the ratio of mass to volume for a defined sample."
  scope: "Macroscopic sample; material may be non-uniform."
claim_status: "FACT"
implementation_status: "PLANNED"
evidence_level: "SOURCE_LINKED"
equation:
  expression: "rho = m / V"
  variables:
    - symbol: "rho"
      meaning: "density"
      unit: "kg/m^3"
    - symbol: "m"
      meaning: "mass"
      unit: "kg"
    - symbol: "V"
      meaning: "volume"
      unit: "m^3"
  assumptions:
    - "Mass and volume refer to the same defined sample."
    - "The chosen volume convention is stated."
sources:
  - id: "source.example"
    kind: "textbook"
    title: "Source title"
    url: "https://example.org/source"
    accessed: "YYYY-MM-DD"
    supports: ["definition", "equation"]
    quotation: false
    license: "Check before redistribution"
tests:
  - id: "density.unit-check"
    type: "dimensional"
    expected: "kg / m^3"
    status: "PLANNED"
relationships:
  - type: "REQUIRES"
    target: "math.ratio"
  - type: "RELATED_TO"
    target: "physics.gravitation"
  - type: "TEACHES"
    target: "quest.physics-atlas-density"
learning:
  level: "introductory"
  outcomes:
    - "Explain density in plain language."
    - "Calculate density from mass and volume."
    - "Convert compatible units."
uncertainty:
  note: "Density can depend on temperature, pressure, composition, and measurement method."
  open_questions: []
provenance:
  created: "YYYY-MM-DD"
  updated: "YYYY-MM-DD"
  contributor: "project contributor or pseudonym"
  license: "Repository-level license; verify compatibility"
history:
  - version: "0.1.0"
    change: "Initial example record"
    date: "YYYY-MM-DD"
~~~

Replace placeholders before treating an example as a real scientific record. Never present a placeholder URL as a real source.

## 3. Required conceptual fields

| Field | Requirement |
|---|---|
| id | Stable, unique identifier within the project namespace. |
| type | Controlled object type: concept, claim, equation, experiment, dataset, simulation, source, quest, decision, or software component. |
| title | Human-readable name. |
| summary | Short description without unsupported certainty. |
| claim_status | FACT, MODEL, HYPOTHESIS, OPEN, or FICTION. |
| evidence_level | State of support or review, with method and scope. |
| implementation_status | PLANNED, PROTOTYPE, IMPLEMENTED, TESTED, or VERIFIED where software implementation applies. |
| sources | Traceable references, dates, source types, and reuse/license notes. |
| provenance | Origin, contributor attribution, timestamps, and record version. |
| history | Auditable record of meaningful changes. |

For a quest or planned software component, state its actual type. Do not force every object into the FACT label.

## 4. Evidence levels

Evidence level is a review aid, not a universal numerical measure of truth.

| Level | Meaning | Example |
|---|---|---|
| E0 — UNASSESSED | No adequate source or test is attached. | Initial idea. |
| E1 — SOURCE_LINKED | At least one identifiable source or observation is linked. | Official standard or primary paper. |
| E2 — CORROBORATED | Independent sources or observations support the same scoped claim. | Separate measurements agree within stated limits. |
| E3 — REPRODUCED | A method, experiment, or computation has been independently reproduced. | Test script and environment reproduce the result. |
| E4 — REVIEWED_OR_FORMALIZED | Qualified review or a formal verification method is completed and scoped. | Peer review, Lean proof checking, or independent audit. |

The levels are not always a simple ladder. A formal proof, empirical replication, and peer-reviewed synthesis are different forms of support. Record the method and scope, not merely the highest-looking label.

Do not increase an evidence level because many AI agents repeat the same output. Agents may share sources, assumptions, or errors.

## 5. Keep the statuses separate

- **claim_status:** What kind of statement is this?
- **evidence_level:** What support or review does it currently have?
- **implementation_status:** What has actually been built and verified?

Example: a proposed orbital simulation may be a MODEL, have evidence level E1 because it cites established equations, and implementation status PROTOTYPE because its code has not passed validation tests.

## 6. Typed graph edges

| Edge | Interpretation |
|---|---|
| REQUIRES | A prerequisite is needed. |
| DERIVES_FROM | The item follows from or is derived from another. |
| SUPPORTS | Evidence supports a claim. |
| CHALLENGES | Evidence challenges a claim. |
| CONTRADICTS | Two scoped claims or results are incompatible. |
| MODELS | An equation or simulation represents a phenomenon. |
| TESTS | A test or experiment checks an item. |
| IMPLEMENTS | A software component implements a model or method. |
| TEACHES | A resource teaches a concept. |
| RELATED_TO | A useful association without a stronger inference. |
| SUPERSEDES | A later version replaces an earlier one for a defined purpose. |

A relationship itself should be attributable and reviewable when it makes a substantive claim.

## 7. Validation rules

A future validator should check:

1. IDs are unique and stable.
2. Every relationship target exists or is explicitly external.
3. Dates use ISO 8601.
4. URLs are syntactically valid and identify the cited source.
5. Quoted material is distinguished from paraphrase and original work.
6. License and redistribution conditions are recorded where content is imported.
7. Equations define variables and include compatible units.
8. Numerical examples include inputs, expected outputs, and tolerances.
9. Simulation records include algorithm, parameters, initial conditions, and version.
10. Test results include environment and execution date.
11. Corrections preserve the prior version and explain what changed.
12. Privacy and security review occurs before publication.

For physical equations, dimensional consistency is necessary but not sufficient for correctness. A dimensionally valid equation can still be physically wrong or outside its domain of validity.

## 8. Negative results and uncertainty

Store informative unsuccessful approaches:

- What was the question?
- Which method and parameter set were used?
- What was expected?
- What happened?
- Which limits or possible confounders remain?
- What should a later researcher avoid repeating?

Evidence records may use states such as NOT_REPRODUCED, INCONCLUSIVE, FAILED_TEST, and RETRACTED. Never transform missing evidence into evidence of absence.

## 9. Privacy and publication boundary

Keep private learner notes, private messages, personal profiles, and sensitive data outside public knowledge nodes by default. Publish only information that the contributor is authorized to share.

Do not include passwords, access tokens, personal medical information, private addresses, confidential workplace material, or third-party conversations without permission. The knowledge engine must not require a person to expose their private learning history to participate.

## 10. Proposed implementation tasks

1. Create Java records or POJOs for KnowledgeNode, EvidenceRecord, and KnowledgeRelation.
2. Add parsing and serialization for a minimal YAML or JSON format.
3. Validate IDs, statuses, references, dates, and required fields.
4. Add unit-dimension metadata and one dimensional-analysis test.
5. Add version history and a small sample dataset.
6. Add search by ID, title, tag, source, and relationship.
7. Add an export/import test to verify portability.

### Acceptance criteria for the first slice

- One density node loads from a file.
- Its source and equation are displayed.
- A dimensional test confirms that kg/m³ is consistent with mass divided by volume.
- An invalid relationship target is rejected or reported.
- A revision can be added without losing the earlier record.
- No personal or private information is required for the example.

**Status:** Specification only. Implement each task in small, testable pull requests.
