# Matrixgame — Integrated Development Roadmap
## Earth Simulator × Neurogame × Social & Emotional Intelligence

**Version:** 0.1  
**Status:** Architecture proposal / implementation roadmap  
**Date:** 2026-11-05  
**Purpose:** Connect the Planet Earth habitability project with collective agents, social intelligence, regenerative learning, and evidence-aware simulation.

## 1. Mission

Build a small, reproducible learning environment in which human participants and artificial agents can explore ecological systems, practice cooperation, improve their reasoning, and learn from measurable outcomes.

The project must improve learning conditions and decision quality without coercion, humiliation, unsupported scientific claims, or avoidable ecological harm.

**Guiding principle:** Learn without shaming. Cooperate without coercion. Progress with regeneration.

## 2. Existing project documents

- [Planet Earth — Habitability and Evolution](../PLANET_EARTH_HABITABILITY_AND_EVOLUTION.md)
- [Social Intelligence — Knigge, Consent and Cooperation](../social-intelligence/README.md)
- [Neurogame × Social Intelligence × Toxicity Reduction × Didactics](../neurogame/INTEGRATION_SOCIAL_INTELLIGENCE_TOXICITY_DIDACTICS.md)

These documents are specifications and research roadmaps. They do not by themselves establish that the described simulator or agent code is implemented.

## 3. Unified five-layer architecture

| Layer | Responsibility | Example evidence or behavior |
|---|---|---|
| Perception | Observe environment, other agents, resources and uncertainty | water level, soil moisture, message received |
| Intelligence | Interpret evidence, form hypotheses and select actions | compare options, estimate consequences, abstain |
| Cooperation | Communicate, share, warn, respect boundaries and resolve conflicts | SHARE, WARN, respectful refusal |
| Regeneration | Maintain energy, attention, motivation and system recovery | REST, pause, reduce workload |
| Evolution | Learn from outcomes, test variants and preserve verified knowledge | mutation in sandbox, evaluation, versioned records |

The layers are connected, not independent scoreboards. A system should not improve one layer by silently damaging another.

## 4. Shared agent decision cycle

Use the same inspectable sequence for ecological and social scenarios:

`OBSERVE → CHECK DATA → INTERPRET → GENERATE OPTIONS → APPLY SAFETY / CONSENT RULES → PREDICT → ACT OR ABSTAIN → RECORD OUTCOME → REVIEW`

Required decision labels:

- `ACCEPT`
- `REJECT`
- `UNCERTAIN`
- `ABSTAIN`
- `PROPOSE_ALTERNATIVE`

Agents must distinguish observations from interpretations and hypotheses. A persuasive explanation is not evidence by itself.

## 5. Social intelligence and toxicity substitution

Do not equate disagreement, criticism, refusal, or boundary-setting with toxicity. Preserve dissent and the right to say no.

| Harmful interaction pattern | Better available option |
|---|---|
| Insult or humiliation | Critique the claim or behavior specifically |
| Pressure after refusal | Respect the boundary; offer an optional alternative once |
| Motive assumption | Ask a neutral clarification question |
| Escalation | Pause, summarize the disagreement and lower the tempo |
| Unverified certainty | Check sources and label uncertainty |
| Retaliation | Protect affected parties and offer proportionate repair |
| Overload | REST, reduce scope and resume voluntarily |

### Three starter quests

1. **Quest 001 — Respect the NO:** an agent declines a proposed interaction. The other agent stops the request, acknowledges the boundary and does not penalize the refusal.
2. **Quest 002 — Resolve the Conflict:** agents hold different interpretations of an event. They separate observations from assumptions, ask clarifying questions and record any remaining disagreement.
3. **Quest 003 — Repair & Regenerate:** an agent identifies a harmful action, acknowledges its effect, proposes proportionate repair and allows recovery time.

Success is measured by scenario outcomes and rule compliance, never by ranking a person's worth or assigning a public empathy score.

## 6. Earth Simulator: start with a small causal model

Do not start with a planet-wide digital twin. Build and test one local process at a time.

### MVP A — Water bucket model

State variables:
- precipitation;
- infiltration;
- soil-water storage;
- evapotranspiration;
- runoff.

First invariant: water balance must close within a documented numerical tolerance.

`storage_next = storage_now + precipitation + inflow - evapotranspiration - runoff - withdrawals`

All terms must use compatible units and an explicit time step. Missing data must remain marked as missing, not silently replaced with invented measurements.

### MVP B — Soil and vegetation

Add a simple vegetation-cover state and a documented response to soil moisture. Treat parameters as assumptions until calibrated against suitable data.

### MVP C — Surface energy and temperature

Add a deliberately simplified energy balance with explicit boundary conditions. Do not infer regional climate trends from a single local reading.

### MVP D — Habitat and biodiversity

Represent habitat suitability and selected indicators. Do not claim population forecasts without appropriate ecological data and validation.

### MVP E — Collective agents

Allow agents to propose hypotheses, inspect data, challenge assumptions and suggest tests. Require human review before real-world interventions.

### MVP F — Visualization and quests

Display state changes, causal links, uncertainty and quest progress. Keep the simulation engine separate from the user interface.

## 7. Didactics, motivation and regeneration

Use a repeatable learning loop:

`NOTICE → UNDERSTAND → TRY → FEEDBACK → REFLECT → REST → REPEAT`

Design rules:
- one small objective at a time;
- explain why the task matters;
- show examples before increasing difficulty;
- provide immediate, specific, non-shaming feedback;
- make pause, opt-out and alternative paths available;
- reward reproducibility, corrections, helpful peer review and careful uncertainty handling;
- avoid streak pressure, manipulative notifications and public leaderboards for personal traits.

Gamification points are symbolic mechanics, not scientific measures of intelligence, empathy, ecological impact or human value.

## 8. Evidence and validation protocol

Every substantive claim or model component should carry:

- epistemic label: `FACT`, `MODEL`, `HYPOTHESIS`, `OPEN`, or `FICTION / GAME RULE`;
- source and provenance;
- unit and spatial/temporal scope where applicable;
- assumptions and known limitations;
- uncertainty or confidence rationale;
- test method and result;
- reviewer and version history.

Validation sequence:
1. Check definitions, units and data provenance.
2. Test invariants and boundary conditions.
3. Compare against analytical examples or trusted reference data.
4. Run sensitivity analysis.
5. Test alternative explanations and failure cases.
6. Record failed tests and corrections.
7. Only then consider a model suitable for a larger scenario.

## 9. Neuro-rights, privacy and governance

- No covert inference of a person's emotions, mental state or health.
- No diagnosis or treatment recommendation from game metrics.
- Collect only data needed for a clearly explained purpose.
- Make participation voluntary and revocable.
- Keep personal learning data private by default.
- Do not use agent state variables as claims about human psychology.
- Require human oversight for consequential decisions.
- Do not perform ecological interventions without permissions and qualified assessment.
- Do not release organisms or introduce species as part of a game quest.

## 10. Code integration status

A message supplied by the project owner reports that Grok created four Java files in a separate sandbox:

- `AgentGenome.java`
- `CollectiveAgent.java`
- `GameEnvironment.java`
- `SimpleSimulation.java`

The source files or ZIP archive have not yet been supplied to this repository in a verifiable form. Their package declarations, dependencies, behavior, and build status are therefore **unverified**.

Do not recreate or claim to integrate those classes from filenames and descriptions alone. Once the actual source files are available:
1. inspect package declarations and dependencies;
2. compare the code with the repository's existing build structure;
3. add the smallest isolated integration;
4. write deterministic tests for agent actions and environment updates;
5. test `SHARE`, `WARN`, `REST` and refusal handling;
6. run the project build and record the exact result.

The repository's current `pom.xml` targets Java 11 and declares no external dependencies. Any code integration must respect or explicitly revise that baseline.

## 11. First implementation milestone

**Milestone 1 — Verified learning loop**

Deliverables:
- a minimal water-balance model with unit tests;
- one evidence record with source, units and uncertainty;
- one playable scenario in which an agent can accept, refuse or propose an alternative;
- a visible explanation of why an action was selected;
- a short reflection and regeneration step;
- a reproducible run log.

Acceptance criteria:
- tests pass;
- water balance is within documented tolerance;
- refusal is respected and does not cause a penalty;
- assumptions are visible;
- no unverified real-world impact is claimed;
- the example can be rerun from documented instructions.

## 12. Long-term direction

The long-term vision connects ecological resilience, human wellbeing, social intelligence, scientific knowledge, collective AI and sustainable technology. These are research directions, not promises of automatic progress or guaranteed outcomes.

**Make the game better than the players found it.**  
**Let's play and let's be.**
