# AGENTS.md — Matrixgame / LUMINI

These instructions apply to AI coding agents working in this repository.

## Project identity

- Project: Matrixgame Universal Grid Lab / Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit.
- Java package: `rhgr`.
- Minimum Java version: 17.
- Current evolutionary candidate: `src/rhgr/MatrixgameUniversalGridLab_v11_0.java`.
- Previous candidate: v0.10.0. Do not overwrite it.
- Architecture: one shared world state, legal action catalogue, immutable per-tick perception, strategy/evaluator layer, central action validation and resolver.

## Mandatory working rules

1. Read `README_MATRIXGAME_V11.md`, this file and the relevant source/test sections before editing.
2. Treat the existing source and test output as evidence. Do not claim that code compiles, a test passes, a Canvas opens or a simulation succeeds unless a real run confirms it.
3. Keep the stable baseline unchanged. Implement new behavior in a new versioned class or isolated feature branch.
4. Do not minify Java, remove useful line breaks, reformat the whole source file or replace unrelated code.
5. Prefer small, reviewable patches. Preserve public APIs, package names, keyboard controls and the existing 49-action grammar unless the task explicitly requires a change.
6. Do not add external dependencies without explaining why and updating the build/test instructions.
7. Do not put API keys, personal credentials, private tokens or local secrets in source files, commits, logs or prompts.
8. Separate **FACT**, **MODEL**, **HYPOTHESIS**, **OPEN QUESTION** and **FICTION/GAMEFICTION** in research notes. Do not present a hypothesis as a measurement.

## Required verification

Compile:
```bash
mkdir -p out
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v11_0.java
```

Run deterministic tests:
```bash
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v11_0 --self-test
```

Run a reproducible headless simulation and render the browser report:
```bash
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v11_0 --simulate 306 20261116 160 100 matrixgame-report.html
```

A successful compile is not the same as a successful self-test. A successful self-test is not proof of multi-seed or long-term ecosystem stability.

## Decision and simulation quality

When changing unit/building policies, test at least:
- survival and food collection;
- friendly-fire and team-building protection;
- resource carrying, delivery and storage conservation;
- safe founding, reproduction and production;
- population accounting (initial population + births + produced units = living population + deaths);
- deterministic regression for the recorded seed;
- multiple seeds and longer runs when making balance claims.

Use survival-first behavior as a priority, not an unconditional shortcut: consider energy, visible food, safe path, team logistics, threat, reproduction reserve, population cap and local infrastructure.

## Git and review protocol

- Work on a dedicated feature branch, not directly on `main`.
- Do not rewrite or force-push shared history.
- Keep each change scoped to one clear objective.
- Open a pull request with: motivation, changed files, test commands, actual results, known failures and remaining risks.
- Do not merge a PR or mark it ready for review without explicit maintainer approval.
- Report the exact commit SHA and link to the CI run.

## Multi-agent handoff format

When another agent picks up work, provide:
- **Goal:** the one outcome expected;
- **Baseline:** branch, commit SHA and source version;
- **Evidence:** tests already run, exact logs and known limitations;
- **Constraints:** files that must remain unchanged;
- **Task:** one bounded implementation or review;
- **Acceptance tests:** commands and expected measurable outcome;
- **Return:** branch/PR link, commit SHA, changed files, test results and unresolved questions.

A repository file or issue is the shared project memory. Private chat histories are not automatically shared between model providers.
