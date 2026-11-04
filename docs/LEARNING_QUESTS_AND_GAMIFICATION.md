# Matrixgame — Learning Quests and Responsible Gamification

**Version:** 0.1.0  
**Status:** Proposed game design  
**Implementation status:** PLANNED

## 1. Design goal

Gamification should make learning, research, cooperation, and useful improvement more engaging without replacing evidence or pressuring people to participate.

> Make the game better than the players found it.

Rewards should favor understanding, reproducibility, constructive criticism, teaching, and verified improvements—not message volume, time spent online, agreement with an AI, or leaderboard position.

## 2. Learning progression

A learner may progress through these observable capabilities:

1. **RECOGNIZE** — identify a concept or variable.
2. **EXPLAIN** — describe it in their own words and state a limitation.
3. **CALCULATE** — use an equation, define inputs, and check units.
4. **APPLY** — solve a new example or configure a model.
5. **TRANSFER** — connect the concept to another domain.
6. **INVESTIGATE** — ask a testable question and evaluate evidence.
7. **TEACH** — create an explanation or exercise that another person can use.

This is not a ranking of human worth or intelligence. A learner can revisit any step, choose a different path, or stop without penalty.

## 3. Quest format

Each quest should have a stable ID, title, learning objective, prerequisites, effort range, inputs and sources, small steps, observable deliverable, acceptance criteria, evidence status, collaboration roles, reflection, and next quest.

Example portable quest:

~~~yaml
id: "quest.physics.density.001"
title: "Measure and calculate density"
status: "PLANNED"
objective: "Calculate density and verify units."
prerequisites:
  - "math.ratio"
  - "si.mass"
  - "si.volume"
steps:
  - "Choose one documented mass and volume example."
  - "Calculate rho = m / V."
  - "Convert to SI units."
  - "Check the dimensions."
  - "Explain one limitation of the example."
deliverable:
  - "A reproducible calculation with source and units."
acceptance:
  - "Inputs and units are stated."
  - "Result is dimensionally consistent."
  - "Another learner can reproduce the result."
reward:
  kind: "learning_points"
  amount: "configured by the game; not a measure of personal ability"
~~~

This is a draft example, not an implemented quest validator.

## 4. Quest catalogue

### Quest 1 — Physics Atlas
Turn the A–Z index into linked, sourced knowledge nodes.

Tasks: choose a concept; define it in plain language; attach a reliable source; add units and equations where relevant; link prerequisites and applications; add one check question.

Success: a learner can navigate from the concept to its assumptions, evidence, and next learning step.

### Quest 2 — Mathematics Engine
Make equations understandable and testable.

Tasks: define symbols and dimensions; record assumptions and domain of validity; add a worked numerical example; test limiting cases and units; record where the formula should not be used.

Success: the equation can be independently checked and reproduced.

### Quest 3 — Reality Simulator
Connect a documented model to a small executable simulation.

Tasks: define state variables and units; choose initial conditions; state the numerical method; run a baseline case; test a limiting case; compare a result with an analytic solution or trusted reference where possible.

Success: code, configuration, tests, and limitations are available together.

### Quest 4 — Science Archive
Preserve sources, uncertainty, failed attempts, and revisions.

Tasks: add provenance and dates; distinguish quotation, paraphrase, and original interpretation; record contradictory evidence; preserve negative results; add a version history.

Success: another researcher can reconstruct why the current record has its status.

### Quest 5 — Collective Proof Engine
Explore questions using independent research roles and verification.

Tasks: split a question into subproblems; assign diverse roles such as researcher, mathematician, critic, programmer, and verifier; record assumptions and intermediate claims; search for counterexamples; reproduce calculations or formal checks; obtain human review for consequential conclusions.

Success: output includes proof or test artifacts, limitations, and a clear review status. The number of agents is not an acceptance criterion.

### Quest 6 — Interplanetary Bridge
Model robust knowledge exchange when links are delayed or interrupted.

Tasks: define local research nodes; queue contributions while offline; add timestamps, IDs, and content hashes; simulate a delayed contact window; resolve conflicting revisions without erasing history; verify the received payload and provenance.

Success: a local node can continue working and synchronize safely after reconnection.

## 5. Small first sprint

**Theme:** Density and the physics knowledge graph.

- Create one knowledge node for density.
- Add mass and volume as prerequisites.
- Add the equation and SI units.
- Provide one worked example.
- Add a unit-validation test.
- Create a quest that asks the learner to reproduce the example.
- Store a result and a brief reflection.
- Link the quest back to the node.

Do not implement a global points economy, user ranking, or large quest dashboard before this loop works.

## 6. Responsible rewards

A possible optional learning-points scheme:

| Contribution | Example reward |
|---|---:|
| Clear, testable question | +5 |
| Traceable source and accurate summary | +10 |
| Correct units and stated assumptions | +10 |
| Reproducible calculation or test | +15 |
| Independent review or constructive counterexample | +15 |
| Useful correction or documented negative result | +10 |
| Explanation that another learner successfully uses | +15 |

These values are a GAME MODEL for experimentation, not an empirical measure of intelligence, effort, health, or social value.

Safeguards:
- No points for sheer message or token volume.
- No penalty for uncertainty, asking for help, taking breaks, or leaving a quest unfinished.
- No public display of sensitive learner profiles.
- No streak mechanics that punish missed days.
- No rewards for unsafe experiments, rule-breaking, or unsupported claims.
- Points never override source quality, test results, or human review.
- Rewards remain optional and adjustable.

## 7. Balanced outcome indicators

Evaluate whether the game improves concept understanding, reproducibility, error detection, source quality, cooperation, accessibility, and participant experience.

Track unwanted effects too: fatigue, pressure, low-quality submissions, metric gaming, privacy exposure, and maintenance costs.

## 8. Individual and collective learning

A personal learning path may adapt to what a learner chooses to practice. Keep personal progress private by default and allow export or deletion where feasible.

A collective path should expose shared resources, unresolved questions, public evidence, and reviewed contributions—not private learner histories.

Teaching should be reciprocal: explain a concept, ask another participant to challenge it, revise the explanation, and preserve the improved version.

## 9. Connection to the Matrixgame loop

**A — Aufbau:** create a small concept, model, or artifact.  
**C — Connection:** link it to other knowledge and people.  
**G — Prüfung:** test, critique, measure, and reproduce it.  
**U — Regeneration:** pause, reflect, simplify, and recover.

The cycle allows progress without demanding continuous activity.

## 10. Next implementation tasks

1. Define a quest file format.
2. Create a quest parser and validator.
3. Add one density quest.
4. Connect quest completion to test artifacts, not self-reported clicks.
5. Add private local progress storage.
6. Add a simple interface for prerequisites and deliverables.
7. Test accessibility and ensure that an unfinished quest has no penalty.

**Completion definition:** one learner can complete or pause the density quest, produce a reproducible artifact, and see the next suggested step without exposing private profile data.

## 11. Extended research quest — Two Cores, Four Satellites

**Status:** PLANNED  
**Research type:** physics model comparison and numerical simulation.

See [Proton–electron mass ratio and 2D shell-exchange research](RESEARCH_PROTON_ELECTRON_RATIO_AND_2D_SHELL_EXCHANGE.md).

Quest stages:

1. Reproduce the NIST proton–electron ratio and the exact 8 × 27 × 51 factorization after the chosen sixfold floor operation.
2. Implement two cores and four satellites with reproducible initial conditions.
3. Compare gravity-only, gravity-plus-Coulomb, phenomenological short-range repulsion, and an independently switchable exchange event.
4. Measure centre separation, relative velocity, energy, momentum, angular momentum, minimum distance, event count, and steps per second.
5. Repeat with smaller time steps and varied orbital phases.
6. If a helium-like model is selected, compare an effective interaction curve with a cited He₂ reference dataset.

**Acceptance criteria:** the model variants remain separate, the exchange effect is reproducible or falsified, numerical conservation errors are reported, and no classical toy-model result is presented as proof of quantum chemistry.
