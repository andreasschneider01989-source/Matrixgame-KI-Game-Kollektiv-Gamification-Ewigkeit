# Matrixgame — Flexible Game Modes and Learning Telemetry

**Artifact ID:** `mg.design.flexible-game-modes-001`  
**Version:** 0.1.0  
**Status:** PROPOSED DESIGN  
**Claim status:** MODEL  
**Implementation status:** PLANNED  
**Compatibility:** Additive; preserve existing game rules and prototypes.

## 1. Purpose

Make game format, mission, quest, social interaction, world rules, difficulty, and evaluation independently configurable. The same simulation can support personal exploration, cooperation, competition, survival, creative sandbox play, or combinations—without rewriting its core mechanics.

The game is a learning and experimentation environment, not a single prescribed social model. Different modes are hypotheses to test against specific goals and participant preferences.

## 2. Configuration layers

Keep these dimensions separate:

1. **World preset:** physics, map, resources, time scale, environment, event model.
2. **Mission:** what outcome the session is trying to achieve.
3. **Quest graph:** optional and mandatory objectives, prerequisites, branches, and completion conditions.
4. **Social mode:** solo, cooperative, team-vs-team, free-for-all, asymmetric roles, observer, or mixed.
5. **Conflict rules:** none, bounded competition, simulated adversary, survival/elimination, negotiation, or resource contest.
6. **Progression:** sandbox, scenario, campaign, generational/evolutionary, or persistent world.
7. **Difficulty and accessibility:** adjustable challenge, pace, input, visual/audio support, hints, and cognitive load.
8. **Telemetry and evaluation:** what is measured, at what granularity, for what research question, and with what consent.
9. **Reward policy:** optional feedback, points, unlocks, recognition, or no reward.
10. **Safety profile:** permitted actions, content boundaries, stop conditions, privacy, and review requirements.

Do not conflate the selected game mode with the mission. For example, "cooperative" describes the social structure; "restore a watershed" describes the mission.

## 3. Initial mode catalogue

| Mode ID | Interaction | Main question |
|---|---|---|
| SOLO_SANDBOX | One participant, open goals | What can I discover or create? |
| COOPERATIVE | Shared goals and resources | How can we achieve more together? |
| TEAM_VS_TEAM | Teams cooperate internally and compete externally | Which team strategy works under these rules? |
| FREE_FOR_ALL | Individual competitors | How do strategies adapt under rivalry? |
| BATTLE_ROYALE_SIM | Survival/elimination in a fictional bounded simulation | Which choices improve survival from different starting conditions? |
| ASYMMETRIC_ROLES | Different abilities, information, or objectives | How do complementary roles coordinate? |
| EVOLUTION_LAB | Strategies/agents vary across generations | Which traits persist under defined selection pressures? |
| CIVILIZATION_SANDBOX | Long-horizon societies and institutions | Which systems remain resilient and improve wellbeing? |
| OBSERVER_ANALYST | Observe, annotate, forecast, or challenge | Can a participant learn by analyzing others' decisions? |
| MIXED / CUSTOM | Explicitly composed rule sets | Which combination best fits this question? |

Battle Royale is a fictional simulation preset, not a claim that real-world harm is acceptable. Keep simulated conflict bounded, reversible, and separate from real-world targeting or instructions for harm.

## 4. Mission and quest composition

A mission should define:
- stable ID and version;
- intended learning or research outcome;
- world preset and scenario;
- participant roles and team topology;
- initial conditions and information available;
- objective function(s), constraints, and protected outcomes;
- quest graph and optional branches;
- termination and pause conditions;
- expected artifacts and evidence requirements;
- evaluation plan and uncertainty.

A quest is a bounded subproblem with prerequisites, steps, optional routes, deliverable, acceptance criteria, and reflection. Quests may be personal, cooperative, competitive, or hidden-role, but the learning outcome and assessment criteria must remain explicit.

Allow multiple valid strategies. Do not force every participant toward the same play style or public leaderboard.

## 5. Configuration example (illustrative YAML)

```yaml
game_config_version: "0.1.0"
session:
  id: "example.watershed.001"
  mode: "COOPERATIVE"
  progression: "SCENARIO"
world:
  preset: "EARTH_LOCAL_WATERSHED"
  ruleset_version: "0.1.0"
mission:
  id: "mission.restore-watershed"
  objective: "Improve water quality while preserving fair access"
  horizon_turns: 40
  protected_outcomes: ["health", "equity", "ecosystem_integrity"]
players:
  topology: "TEAMS"
  team_count: 2
  roles: ["RESEARCHER", "PLANNER", "ECOLOGIST", "COMMUNITY_VOICE"]
challenge:
  difficulty: "ADAPTIVE"
  pace: "USER_CONTROLLED"
  hints: "OPTIONAL"
telemetry:
  profile: "RESEARCH_MINIMAL"
  consent_required: true
  collect_personal_identifiers: false
  event_schema_version: "0.1.0"
rewards:
  profile: "CONTRIBUTION_QUALITY"
  public_leaderboard: false
safety:
  pause_anytime: true
  reset_available: true
  real_world_actions: "OUT_OF_SCOPE"
```

This is a design example, not an implemented or validated configuration schema. A future validator should reject unknown or contradictory settings and preserve backward compatibility.

## 6. Learning telemetry: measure decisions, not just scores

A high score is not proof of learning. A win can result from luck, hidden information, opponent weakness, or an exploit. A loss can still demonstrate excellent reasoning, collaboration, or learning.

For each opt-in session, consider recording a minimal event stream:
- session and configuration version;
- pseudonymous participant/agent role ID (not account identity);
- turn/time index;
- action type and relevant public state;
- decision rationale only when voluntarily supplied;
- predicted outcome/probability where the game asks for a forecast;
- observed outcome;
- feedback, correction, or reflection;
- resource and state changes;
- provenance for generated scenarios and AI-agent versions.

Avoid collecting raw chat, biometric/emotion inference, precise location, or personal profiles by default. Separate private personal progress from public research artifacts. Provide clear consent, export, deletion/retention rules, and a non-telemetry play mode.

### Core evaluation layers

1. **Gameplay:** objective progress, survival, resources, strategy diversity, fairness of starting conditions.
2. **Learning:** pre/post task performance, explanation quality, transfer to a new scenario, error detection, delayed retention where feasible.
3. **Collective intelligence:** coordination quality, useful information sharing, robustness to disagreement, independent verification.
4. **Experience:** enjoyment, autonomy, perceived challenge, fatigue, accessibility, and willingness to return (never optimize return frequency alone).
5. **External impact:** only when a real-world intervention is separately approved and measured; game outcomes are not proof of real-world benefit.

Use both quantitative metrics and participant explanations. Keep missing data and non-participation visible; do not infer motivation or mental state from clicks.

## 7. Experimental comparison

To compare modes fairly:
- define one bounded research question and primary outcome before running;
- keep mission, scenario difficulty, available information, and assessment comparable;
- record mode-specific rule changes;
- use random assignment or within-person crossover where ethical and feasible;
- counterbalance order to reduce learning/order effects;
- account for prior skill, familiarity, and team composition;
- report sample size, uncertainty, missingness, and negative results;
- avoid claiming that a mode caused improvement from uncontrolled comparisons.

A single participant can run exploratory self-comparisons, but label them as N-of-1 observations, not general population evidence.

## 8. Reward and scoring principles

Use separate scores rather than one opaque total:

- **Mission score:** performance under the selected rules.
- **Learning score:** demonstrated understanding, transfer, and correction.
- **Evidence score:** reproducibility, source quality, and uncertainty handling.
- **Team score:** coordination and shared contribution.
- **Wellbeing/impact score:** only for separately measured outcomes with defined population, timeframe, and safeguards.

A configurable weighted score may be useful for a specific game, but weights must be visible, versioned, and adjustable. Show trade-offs and Pareto fronts where multiple objectives conflict. Never use a game score as a measure of human worth.

Reward useful corrections, replication, clear explanations, accessibility improvements, and informative negative results. Do not reward outrage, harmful play, compulsive engagement, or volume of prompts. Breaks, opting out, and unfinished quests carry no penalty.

## 9. Research outputs and knowledge graph

A completed session may produce:
- configuration and ruleset hash;
- scenario/seed and environment version;
- anonymized event log, if consented;
- forecast records and outcome comparisons;
- quest completion artifacts;
- test results and limitations;
- participant reflection, only if voluntarily shared;
- research summary with FACT / MODEL / HYPOTHESIS / OPEN / FICTION labels.

Link records to existing Knowledge Nodes using typed relations such as `TESTS`, `MODELS`, `SUPPORTS`, `CHALLENGES`, and `TEACHES`. A session is not itself evidence that a general claim is true; replication and independent review remain necessary.

## 10. Small implementation sequence

**MVP 0 — Configuration only**
- define a typed Java `GameConfig` / `MissionConfig` / `QuestDefinition`;
- add schema validation and versioning;
- create SOLO_SANDBOX and COOPERATIVE presets;
- run the same deterministic scenario under both presets;
- verify that core simulation rules are unchanged when only social mode changes.

**MVP 1 — Session events**
- add opt-in local event logging with pseudonymous IDs;
- record config version, seed, turn, action, state delta, and outcome;
- replay a session deterministically where the engine supports it;
- provide a no-logging mode.

**MVP 2 — Learning evaluation**
- add one pre-task and one post-task using a new but equivalent scenario;
- measure explanation, transfer, and error correction;
- produce a compact report, including uncertainty and limitations.

**MVP 3 — Additional modes**
- add TEAM_VS_TEAM, FREE_FOR_ALL, BATTLE_ROYALE_SIM, ASYMMETRIC_ROLES, and EVOLUTION_LAB as independent presets;
- test balancing and fairness separately for each mode.

Do not implement global matchmaking, public rankings, persistent identity, or a global points economy before the local reproducible loop works.

## 11. Research basis and limitations

- Player agency: a 2025 study of an educational programming game found that an unrestricted mode increased enjoyment and intrinsic motivation without compromising measured learning outcomes. This supports testing agency, not assuming identical effects for every game or population.
- Social mode choice: a 2026 randomized trial (n=42 nursing students) comparing individual, competitive, and cooperative digital learning found higher visual attention in the individual condition; other outcome differences were not established. This cautions against assuming multiplayer is always better.
- Serious-game assessment: a 2024 study (n=84) found reflective dashboard feedback and analytical rubrics associated with greater learning gains and more efficient gameplay in its study context.
- Telemetry: research on serious-game analytics demonstrates how event logs and game metrics can support testable learning hypotheses, but feature associations need validation and should not be treated as causal by default.
- Collaborative vs competitive modes: studies in specific contexts show different outcomes for social involvement, flow, and challenge; context and participant abilities matter.

Sources:
- Exploring player agency in educational video games (2025): https://doi.org/10.1016/j.entcom.2024.100754
- Individual, competitive, and cooperative dynamics in gamified digital education (2026): https://doi.org/10.1016/j.entcom.2025.101065
- Gamebrics: analytical rubrics in serious game play (2024): https://doi.org/10.1111/jcal.12998
- Serious Game Analytics by Design (2023): https://doi.org/10.18608/jla.2023.7681
- Learning Analytics in Supporting Student Agency (2023): https://doi.org/10.3390/su151813662
- Collaborative-competitive serious game design (2018): https://doi.org/10.1016/j.compedu.2017.11.007

These studies do not validate this Matrixgame design. The configuration model, metrics, and presets remain proposed and must be prototyped and evaluated.
