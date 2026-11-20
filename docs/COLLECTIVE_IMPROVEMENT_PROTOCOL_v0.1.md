# Collective Improvement Protocol v0.1

**Purpose:** Let people and independent AI systems contribute to Matrixgame through a shared, reviewable process without granting any model automatic authority.

## 1. Contribution contract

Every contribution MUST include:

- `contribution_id`: stable identifier, e.g. `CIP-YYYYMMDD-short-name`
- `author`: person, model, agent, or team; identify the model/version when known
- `intent`: the problem and expected improvement
- `scope`: files, modules, and systems affected
- `evidence_class`: `FACT`, `MODEL`, `HYPOTHESIS`, `OPEN`, or `FICTION`
- `proposal`: concise change or experiment
- `risks`: failure modes, side effects, safety and resource costs
- `validation`: exact commands, test cases, seeds, and success criteria
- `provenance`: sources, commit SHA, tool/model and timestamp where available

## 2. Review states

`PROPOSED -> REVIEWED -> TESTED -> ACCEPTED`

Alternative terminal state: `REJECTED`.

- **PROPOSED:** submitted, not yet checked.
- **REVIEWED:** scope, evidence, compatibility and risks inspected.
- **TESTED:** reproducible checks ran and results are attached.
- **ACCEPTED:** maintainer explicitly approves merge.
- **REJECTED:** reason is recorded; useful ideas may be reworked.

A model must never label its own unrun tests as passed. A generated patch is not a merged change. A PR is not a production release.

## 3. Agent roles (suggested, not assumed to be connected)

| Role | Responsibility | Output |
|---|---|---|
| Idea Scout | Find promising player, science or engineering opportunities | Problem statement + hypothesis |
| Research Critic | Verify sources, search counterexamples, mark uncertainty | Evidence note |
| Architect | Check interfaces, dependencies and version boundaries | Small design proposal |
| Engineer | Implement one bounded change | Commit / pull request |
| Test Agent | Compile, run deterministic tests and simulations | Logs + metrics + seed |
| Game Master | Evaluate playability, ecological outcomes and user value | Review scorecard |
| Archivist | Preserve provenance and continuity | Changelog + checkpoint |

These are role definitions, not proof that external models are connected or executing.

## 4. Stable / Exploratory separation

- `STABLE`: preserve existing behavior; regression tests are mandatory.
- `EXPLORATORY`: isolate new mechanics behind a branch, feature flag, or separate scenario.
- Never overwrite a known-good version to test an unverified idea.
- Merge only after review and reproducible validation.

## 5. Simulation experiment record

Each simulation report should record:

- engine version and commit SHA;
- world dimensions and topology;
- random seed;
- tick count and simulation speed;
- initial population/resources and configuration;
- births, deaths, starvation, predation, reproduction and structure metrics;
- role distribution and role transitions;
- team relations, trade and cooperation metrics when implemented;
- runtime, memory and failure status;
- known limitations and interpretation.

For stochastic mechanics, use multiple declared seeds. One successful run is a demonstration, not robust evidence.

## 6. Motivation and humane gamification

Reward useful contributions, learning, collaboration, reproducibility and constructive criticism—not message volume, hype, or agreement.

Use optional quests and small visible milestones. Do not use guilt, streak pressure, artificial urgency, or claims that a person or model has failed by declining a task.

Suggested loop:

`NOTICE -> QUESTION -> PROPOSE -> TEST -> LEARN -> SHARE -> CELEBRATE -> REST`

A good round ends with one concrete result and one achievable next quest.

## 7. Interoperability

The protocol is tool- and model-neutral. A participant may work locally, in VS Code, through GitHub, or with another AI service.

Exchange portable artifacts:
- Markdown for proposals and reports;
- JSON for machine-readable contribution metadata;
- Java source and tests for engine changes;
- Git commits and pull requests for provenance and review.

No API key, private conversation, account credential, or private user data belongs in a public issue or repository.

## 8. Initial pilot

Pilot on the v0.13.0 large-world candidate:

1. Fix and rerun the deterministic reproduction regression.
2. Run a 500-tick simulation at 320×200 with seed `20261116`.
3. Archive the HTML report and console metrics.
4. Invite independent critique through a GitHub issue or PR review.
5. Convert only evidence-backed feedback into a small v0.14 experiment.

**Success criterion:** a second contributor can reproduce the run, understand the evidence, and propose a compatible improvement without needing private chat history.
