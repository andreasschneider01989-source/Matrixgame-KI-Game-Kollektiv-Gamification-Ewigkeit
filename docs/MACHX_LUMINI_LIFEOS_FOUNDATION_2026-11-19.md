# MACHX × MATRIXGAME × LUMINI LifeOS
## Durable, evidence-aware, human-governed development foundation
**Version:** 1.0.0-draft  
**Date:** 2026-11-19  
**Status:** Architecture and documentation foundation; automation is opt-in and not yet deployed to the default branch.

## 1. Mission
Create a maintainable system that preserves project continuity, turns useful knowledge into tested actions, supports personal and collective development, and reduces the user's need to repeatedly reconstruct context.

The system must support the human rather than maximize engagement, screen time, task count, or productivity at the expense of wellbeing.

## 2. System boundaries
- **MACHX:** simulation/game environment for agents, resources, evolution, construction, cooperation and competition.
- **MATRIXGAME:** shared rules, causal models, experiments, evidence, decision policies and evaluation.
- **LUMINI LifeOS:** human-governed planning, learning, recovery-aware task selection, project continuity and optional reminders.
- **Collective Intelligence:** independent agents/reviewers that propose, critique and test; they do not silently overwrite approved state.
- **GitHub:** versioned public engineering memory, schemas, tests, issues and reviewed changes.

This repository is public. Never put private journal entries, health/diagnostic details, precise location, private correspondence, credentials, personal schedules or sensitive profile data here. Keep the public state generic and store personal check-ins only in a private, user-controlled location.

## 3. Core principles
1. **Human agency:** suggestions are proposals; the user chooses.
2. **Stable before exploratory:** protect known-good behavior; isolate experiments on branches.
3. **Evidence labels:** FACT, MODEL, ASSUMPTION, INFERENCE, HYPOTHESIS, OPEN, FICTION.
4. **Reproducibility:** record model/version, seed, configuration, data provenance and test results.
5. **No false completion:** NOT_RUN is a valid status; never describe a design as implemented.
6. **Low-friction learning:** one useful next action, with a small fallback.
7. **Recovery-aware pacing:** work/rest intensity is adapted to voluntary self-report and context; no clinical inference.
8. **No punitive gamification:** no streak loss, shame, FOMO, coercive reminders or productivity score as a proxy for human worth.
9. **Privacy by default:** public artifacts contain generic examples only.
10. **Open interoperability:** JSON/JSONL contracts; Java, Python and browser clients may implement adapters independently.

## 4. Data flow
`Capture → classify → preserve source → review → model → simulate → test → learn → schedule optional next action → checkpoint`

Each transition must have a timestamp, provenance, status and responsible actor. An LLM proposal is not an accepted fact until reviewed.

## 5. Canonical records
- **Goal:** desired outcome, rationale, horizon, constraints, status and evidence of progress.
- **Quest/Task:** one executable action, estimated effort, prerequisites, acceptance test, fallback and completion evidence.
- **KnowledgeNode:** claim, epistemic status, sources, uncertainty, counterevidence and linked tests.
- **Experiment:** question, hypothesis, controls, seed, parameters, repetitions, metrics and limitations.
- **Event:** typed occurrence, source, time, scope, severity, deduplication key and proposed response.
- **Checkpoint:** current branch/commit, implemented vs planned status, tests, blockers and next smallest step.
- **Review:** independent critique, conflicts, confidence rationale and human disposition.

The JSON schema in `lifeos/schema/lifeos-state.schema.json` defines a deliberately small initial state contract. Extend it only with versioned migrations and tests.

## 6. Task selection and wellbeing
The first selector is transparent and rule-based, not an opaque AI optimizer. It may consider:
- user-selected current focus;
- task value and dependency;
- estimated duration;
- user-selected available time/energy band;
- urgency and external deadline;
- recovery need and recent workload;
- whether a small fallback exists.

It must return at most one primary suggestion and one optional fallback. If the user reports low capacity, offer a 2–10 minute maintenance step or rest; do not pressure them to continue. Never infer medical status from task completion, language, sleep data or mood.

Suggested loop: choose one action → work in a bounded block → pause → record a tiny outcome → decide whether to continue, switch or recover. Default work blocks are suggestions, not prescriptions; user preference overrides them.

## 7. Automation model
Automation is split into safe levels:
- **L0 — Manual:** user invokes an action.
- **L1 — Reminder:** scheduled prompt or digest; no external mutation.
- **L2 — Read-only monitoring:** validate schemas, tests, deadlines and repository state.
- **L3 — Proposed change:** bot opens an issue/PR with evidence and tests.
- **L4 — Approved execution:** only after explicit human approval; never autonomous merge, publishing, spending, contacting people or changing personal data.

GitHub Actions should begin with read-only validation. Scheduled jobs run only after the workflow is merged to the default branch. GitHub schedule events can be delayed and public-repository schedules may be disabled after 60 days without repository activity; they are not a guaranteed personal notification channel. Use an explicit reminder service for personal prompts, with a visible pause/disable path.

## 8. Repository workflow
- `main` = stable reviewed baseline.
- `feature/*` = isolated experiments and documentation increments.
- Draft PRs are review checkpoints, not evidence of runtime success.
- Every change states: scope, evidence, tests run, tests not run, risks, rollback and next step.
- Never overwrite a working game version to add an experiment; add a versioned candidate.
- Before a new session, read the latest checkpoint and open PRs; verify current source rather than relying on chat memory.

## 9. MACHX integration roadmap
1. Preserve v0.8.x game versions and inspect current PRs (#41–#43) before choosing a merge path.
2. Verify Java compilation, self-tests and manual GUI behavior for the depot/production increments.
3. Add typed event records and deterministic replay.
4. Add agent observation/action contracts and capability checks.
5. Add controlled cooperation-vs-competition experiments with fixed seeds and repeated trials.
6. Add learning only after baseline policies, metrics and reproducible evaluation exist.
7. Consider chunked world/infinity-grid work only after finite-world stability and profiling.

## 10. Collective review protocol
Each proposed change may receive independent roles:
- IMPLEMENTER: smallest working change.
- TESTER: acceptance tests and edge cases.
- SCIENTIFIC REVIEWER: assumptions, units, causal validity and evidence.
- RED TEAM: failure modes, misuse, privacy and security.
- DOCUMENTARIAN: update status, provenance and handoff.
Conflicts are retained; do not average incompatible claims into false consensus. Human owner resolves material decisions.

## 11. Definition of done
A feature is done only when:
- code/documentation exists at the stated path;
- acceptance criteria are explicit;
- relevant tests were executed and results recorded;
- limitations and non-tested areas are visible;
- a reproducible handoff exists;
- no private data or secrets were committed.

## 12. First implementation slice
This branch adds:
- a versioned generic state schema and example;
- a dependency-free validator;
- a read-only GitHub Actions workflow for pull requests, main pushes, manual dispatch and a weekly integrity check.

It does **not** implement an LLM agent network, personal data store, calendar integration, autonomous task scheduling, game AI learning or guaranteed notifications. Those require separate reviewed increments.


## 14. Second Earth pilot integration
The first simulation increment is documented in [MACHX Second Earth Quest 001](MACHX_SECOND_EARTH_QUEST_001_2026-11-20.md) and implemented as the isolated `second-earth/` Python reference pilot. It is intentionally a small synthetic biomass/food model with paired random seeds and transparent policy baselines. It does not replace the existing Gaia Earth Lab water-cycle models, the Java Matrixgame prototypes, or the LUMINI compiler.

The simulator owns canonical state transitions. Future Minecraft Java or Luanti adapters are presentation/control clients only. Scientific reviewers and LLMs may propose hypotheses or policy candidates; deterministic validation and human review remain required before state-changing actions. The dedicated `.github/workflows/second-earth-pilot.yml` workflow tests this module independently.

## 13. Next human quest
1. Review this architecture and the public/private boundary.
2. Run the validator locally.
3. Review the generated PR; do not merge until checks pass.
4. Choose whether personal reminders should be daily, weekly or only on request.
5. Resume MACHX from the latest verified v0.8.x source, not from an untested draft.
