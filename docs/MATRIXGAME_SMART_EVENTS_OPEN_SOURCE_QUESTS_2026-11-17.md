# Matrixgame — Smart Events, Open-Source Quests and Collective Game Integration

**Snapshot:** 2026-11-17  
**Branch:** smart-events-open-source-quests-2026-11-17  
**Status:** DESIGN / PROPOSED. Historical concepts and external platforms are catalogued; the unified Smart Event engine is not yet implemented.

## 1. Origin and purpose

This document connects four strands that had previously been developed separately:

1. The Matrixgame's foundational rules: protection from unwanted physical violence and the ability to communicate and exchange knowledge through the Internet.
2. The "raise happiness by an octave" quest: create conditions for people to feel more capable, connected, happy and productive without coercion.
3. The Open-Source Quest Collection announced in the Matrixgame Discord on 2025-08-06: connect Wikidata quests with Wikimedia, Wikipedia, other contributions and Matrixgame gamification.
4. The current Universal Grid Lab / Matrix Core / LUMINI direction: explicit observations, legal actions, deterministic resolvers, evidence, reproducible experiments and reusable engines.

The goal is not to make every external project part of one monolithic game. The goal is a **quest and event layer** that can connect independent games, datasets, human contributions and simulation engines while preserving their own rules and permissions.

## 2. Historical source-derived ideas

### Discord — Matrixgame: Der göttliche Aufstieg
- **2025-04-06:** proposes protection from unwanted physical violence and the ability to connect to the Internet and communicate with the world. A sanction must not itself violate the rules it is meant to uphold.
- **2025-04-06:** introduces the quest to raise collective happiness by an octave, linking meaningful interaction, learning and real-world contributions.
- **2025-05-26:** explicitly asks that the useful loop in the conversation be reused in future interactions and records a public Colab notebook called "Modellierung des kollektiven Bewusstseins".
- **2025-08-06:** announces a growing quest collection for Wikidata and proposes combining it with Wikimedia, Wikipedia, other contributions and Matrixgame gamification.

The supplied Colab link is preserved as a historical pointer, but the notebook contents were not verified in this review. The open Colab tab available during this review was a different notebook containing introductory Python examples.

### Matrixgame Formelsammlung
The Google Docs source already contains the outlines of:
- the Genesis-Core and a system of axioms, principles and a simulation framework;
- SI units and dimensional consistency;
- PAL (Probability Annotation Layer) for epistemic transparency;
- a 3D simulation framework;
- a formal quest architecture and player attribute space;
- Module Z for verification, corrections, falsification, a Newton-gravity simulation kernel, automatic dimensional checks, Bayesian updates and a programmable quest standard;
- extensions for information theory, complex systems, network science, dynamic systems, game theory, AI agents and evidence revision;
- a didactic chain from logic and mathematics through physics, chemistry, biology, cognition, language, communication and collective intelligence to sustainability and future-making.

These headings are important historical design intent. They are not evidence that every formula or module is correct or implemented.

### Historical Open-Source / MMO interface protocol
A Matrixgame journal from July 2026 proposes:
- **Albion Online:** flexible roles and a player-driven resource/crafting economy as a model for adaptive task allocation and resource loops.
- **EVE Online Project Discovery:** in-game analysis tasks connected to real citizen science.
- **Plague Inc.:** parameter and spread-tree mechanics, inverted into positive knowledge and solution diffusion.
- **OGame:** asynchronous production and long-horizon resource management.
- **Wikidata / DBpedia:** SPARQL puzzles and entity-relation quests that improve a structured knowledge graph.
- **OpenStreetMap:** geography and mapping quests.
- **Zooniverse / Foldit:** volunteer classification or spatial/scientific puzzles connected to research.

The same July 2026 material explicitly proposes a strategy-game learning ladder:
- Tic-Tac-Toe as a fully understandable exact-search example;
- Connect Four as a deeper look-ahead / alpha-beta example;
- Tak, Chess and Go as more complex domains for heuristic search, MCTS and neural policies.

These are source-derived design intentions. The particular old performance and game-size figures should be rechecked before reuse.

## 3. Smart Events: a reusable event layer

The term **Smart Events** has an academic precedent. Zieliński et al. (ICCS 2021) describe moving behavior-selection logic from each NPC into an event object. The event carries state, a spatial influence area, participants, urgency and available actions; relevant agents are notified through a message board and can join dynamically as they enter the affected area. The paper also discusses finite-state event evolution, coordinated group behavior trees and ordinary non-emergency events such as bargaining. DOI: https://doi.org/10.1007/978-3-030-77977-1_13

This is a useful design reference, not a claim that the paper's implementation is already present in Matrixgame. Its small test scenarios do not prove a universal performance advantage.

### Proposed Matrixgame definition

A **Smart Event** is a versioned, time-bounded or persistent world fact that:
- has an explicit trigger and lifecycle;
- affects a defined world region, entity set or information domain;
- can publish observations to relevant agents or subscribers;
- specifies a set of eligible roles and available legal actions;
- evolves through an explicit state machine or rule set;
- can coordinate multiple agents through a behavior tree or workflow;
- records its inputs, outputs, decisions and evidence.

An event does **not** create a second action system. It proposes or makes available actions from the existing engine-specific legal action space. The domain resolver remains the authority that checks and applies actions.

### Proposed event categories

| Category | Example | Trigger |
|---|---|---|
| Player-created | Player starts a build, trade or exploration task | Explicit player action |
| World/environment | Resource appears, rock falls, storm approaches | Simulation rule |
| Time/schedule | Production cycle completes, quest deadline approaches | Clock / tick |
| Agent-generated | A unit requests help or proposes an alliance | Agent policy |
| Collective milestone | Several agents complete a verified task | Aggregated events |
| External-data update | New licensed dataset snapshot is available | Approved data adapter |
| Safety / integrity | Invalid action, source conflict, permission violation | Validator / Guardian rule |

### Minimum event data contract

Illustrative schema (field names are proposals; a formal JSON Schema is a v0.10+ task):

~~~yaml
event_id: "matrixgame.event.example.001"
schema_version: "0.1.0"
event_type: "RESOURCE_REGENERATION"
source:
  kind: "simulation_rule"
  source_id: "gridlab.world.generator"
created_at: "2026-11-17T00:00:00Z"
valid_from_tick: 120
valid_until_tick: 180
scope:
  world_id: "world-001"
  region: { x_min: 10, y_min: 8, x_max: 20, y_max: 18 }
participants:
  eligible_roles: ["FORAGER", "SCOUT"]
priority: "NORMAL"
state: "ACTIVE"
state_transitions: ["CREATED", "ACTIVE", "RESOLVED", "EXPIRED"]
available_actions:
  - "MOVE"
  - "INTERACT"
  - "COLLECT"
conditions:
  - "action_is_legal"
  - "agent_has_required_capability"
evidence:
  status: "MODEL"
  references: []
privacy: "PUBLIC_GAME_STATE"
~~~

Every real event should also carry an event payload hash, engine/run reference, unit or schema version where relevant, and a record of the rule/trigger that generated it.

### Runtime cycle

1. **Detect:** a player, simulation rule, timer, agent or approved adapter emits an event.
2. **Validate:** schema, source, time range, permissions and domain constraints are checked.
3. **Publish:** the event is added to an append-only event log and broadcast only to relevant world regions/subscribers.
4. **Perceive:** agents update their Observation according to their own sensor genes and access permissions.
5. **Plan:** policies select from legal actions; an event can offer a role or task, but it cannot bypass the action resolver.
6. **Coordinate:** if a multi-agent event needs cooperation, assign roles and check that all required roles can be filled.
7. **Execute:** chosen actions pass through the existing tick resolver.
8. **Measure:** update event state, outputs, metrics, evidence and quest progress.
9. **Close:** resolve, cancel or expire the event, preserving the audit trail.

### Priority and autonomy rules

- A safety-critical event may interrupt a lower-priority autonomous activity, but the interruption policy must be explicit and inspectable.
- Player-issued commands must not be silently overwritten by low-priority background events.
- Agents may decline optional events; a quest can be paused or abandoned without punishment.
- Event urgency is not a reason to bypass consent, safety or validation.
- Multi-agent control should not remove autonomy indefinitely. The event should specify which actions are temporarily coordinated and when individual control resumes.

## 4. Shared architecture: Events + GameObjects + Policies + Quests

The current Grid Lab already supplies the key stable abstraction: each object has a legal action space and a controller chooses one action per tick; the central resolver validates and applies it.

The proposed extension is:

**WorldState → EventDetector → EventBus / EventLog → Observation → DecisionPolicy → LegalAction → Resolver → Metrics / QuestProgress**

Keep the responsibilities separate:
- **GameObject:** identity, position, properties, capabilities, team and controller.
- **Observation:** what this agent can actually perceive.
- **DecisionPolicy:** random, rule-based, genetic, adaptive softmax, neural or later search-based.
- **Smart Event:** temporary shared context, trigger, lifecycle, region and coordination plan.
- **Quest:** human-readable objective, prerequisite, steps, evidence, acceptance criteria and optional reward.
- **Resolver:** final legal-action and state-transition authority.
- **Evidence / RunManifest:** provenance and reproducibility.

This avoids a monolithic class and lets the same event system work with Grid Lab, Matrix Core, a gravity scenario, an external-data quest or a browser-based game—through domain adapters.

## 5. Open-Source Quest Bridge

### A. Wikidata / DBpedia — knowledge graph quests
Task examples:
- find entities matching a well-defined query;
- inspect missing or conflicting properties;
- add source-backed relations;
- validate entity identifiers and references;
- compare graph paths or query results.

The Wikidata Query Service exposes a SPARQL endpoint. Its documentation notes that it is suited to structured characteristic-based queries, not fuzzy text search or very large data extraction. The service is also undergoing a 2026–2027 backend migration, so adapters must version endpoints and test queries. Sources:
- https://www.wikidata.org/wiki/Wikidata:Data_access
- https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/en

### B. OpenStreetMap — spatial-data quests
Task examples:
- identify a missing or inconsistent mapping feature;
- compare map data with permitted evidence;
- create a local route/geography learning task;
- propose a correction for human review.

The OpenStreetMap editing API is intended for editing, not bulk read-only data access. Data access and editing must follow the project's API usage policy and licensing. Large reads should use suitable extracts or approved services rather than burdening the editing API:
- https://openstreetmap.github.io/owg-website/policies/api/

### C. Zooniverse — research classification quests
Task examples:
- classify images or documents through an approved research project;
- compare independent annotations;
- flag ambiguous cases for review;
- create tutorial tasks explaining the classification protocol.

Zooniverse is a people-powered research platform where volunteers assist research teams with tasks such as image classification and transcription. Participation is project-specific; Matrixgame should link to approved project tasks rather than copy research data without permission:
- https://www.zooniverse.org/projects
- https://www.zooniverse.org/get-involved/collaborate

### D. Foldit — spatial and scientific puzzle quests
Task examples:
- use a protein-structure puzzle to learn spatial geometry;
- compare candidate solutions against the puzzle's scoring and objectives;
- preserve the research context and explain what a score does and does not imply.

Foldit is a research-oriented protein-folding game developed by university scientists; its site describes published research and credited player contributions. Matrixgame should direct users to the official game/project and not pretend to reimplement or validate molecular results itself:
- https://fold.it/
- https://fold.it/about_foldit

### E. EVE Online Project Discovery — model for in-game citizen science
This is a particularly close precedent for the 2025 Discord idea. EVE's official Project Discovery page describes mini-games where player classifications contribute to research. Its 2026 decade retrospective reports nearly a billion classifications and projects spanning cellular biology, astrophysics and cancer immunology; the current page describes analysis of flow-cytometry data for cancer/immune-system research:
- https://www.eveonline.com/discovery
- https://www.eveonline.com/news/view/the-first-decade-of-project-discovery

Matrixgame lesson: a game action can create a real research contribution only when the dataset, task definition, agreement/quality control, researcher workflow and feedback path are genuine and authorized.

### F. MMO resource loops — mechanics to adapt, not to copy wholesale
The old journal's examples can be converted into domain-neutral mechanics:
- Albion Online: flexible skill/role combinations and player-driven production → capability-based task matching and transparent resource flows.
- EVE Online: long-lived institutions, economy and Project Discovery → collective task allocation and citizen science.
- Plague Inc.: propagation and mutation trees → positive diffusion of knowledge and beneficial practices, not pathogen mechanics as a real-world intervention.
- OGame: asynchronous resource production and long-term planning → background queues, scheduled events and persistent state.
The Matrixgame version should use positive-sum objectives, fair participation and transparent rules rather than competitive exploitation as the default.

## 6. Strategy-game ladder as a policy test suite

The user's older notes proposed using familiar board games to teach state/action/value search. Keep this as a separate test suite for DecisionPolicy implementations:

| Game | Suggested progression | What it teaches |
|---|---|---|
| Tic-Tac-Toe | Exact minimax | Terminal states, legal actions, complete search and optimal play |
| Connect Four | Alpha-beta / deeper look-ahead | Search depth, tactical threats and pruning |
| Tak / Chess | Search plus heuristics | Planning and evaluation under larger branching factors |
| Go | MCTS and policy/value networks | Approximate search, pattern recognition and learning |

Acceptance should include game-theoretic correctness where exact solutions exist, fixed-seed tournaments, reproducible win/draw/loss statistics, and explicit baselines. Do not use a board-game score as a proxy for real-world human value or a guaranteed life strategy.

## 7. Quest format and positive-sum governance

Reuse the existing docs/LEARNING_QUESTS_AND_GAMIFICATION.md rules:
- stable quest ID and objective;
- prerequisites and reasonable effort range;
- sources and inputs;
- small steps;
- observable deliverable;
- acceptance criteria and evidence status;
- optional collaboration roles;
- reflection and next quest.

The A-C-G-U learning loop remains useful:
- **A — Aufbau:** create a small artifact or concept.
- **C — Connection:** link it to knowledge, data or other people.
- **G — Prüfung:** test, reproduce, challenge and verify it.
- **U — Regeneration:** pause, simplify, reflect and recover.

Rewards should recognize reproducibility, helpful review, source quality, successful explanations and useful corrections. Do not reward message volume, unverified claims, compulsive activity or agreement with an AI. No penalties for uncertainty, asking for help, taking a break or leaving a quest unfinished.

## 8. Safety, rights and the positive-sum constitution

Translate the original Discord rules into explicit product requirements:
1. Protect participants from unwanted physical harm; in simulations, represent safety constraints and non-violent conflict resolution as first-class rules.
2. Preserve communication and knowledge access subject to lawful, transparent safety and privacy rules.
3. Sanctions and moderation actions must themselves follow declared rules and due process.
4. Keep a transparent event log and explain why an event was triggered or an action was blocked.
5. Use the least coercive intervention that adequately protects participants.
6. Preserve opt-out, pause and human override for non-emergency game events.
7. Do not claim that every interaction can only have positive effects; instead detect, measure, disclose and reduce harmful side effects.

The design objective is positive-sum improvement, not forced consensus or a single happiness score.

## 9. Measurement and validation

For each Smart Event and Quest, record:
- trigger count and trigger source;
- agents notified / eligible / participating;
- time to first valid response;
- completion and cancellation rates;
- action legality / resolver rejection rates;
- coordination success and role coverage;
- measurable task output (e.g. verified Wikidata edit, accepted annotation, reproducible test);
- source quality and independent agreement;
- user experience and accessibility;
- negative indicators: overload, pressure, spam, privacy exposure, harmful submissions, or poor-quality metric gaming.

A successful event dispatch is not proof of a useful outcome. A quest should be credited only when its observable deliverable meets its acceptance criteria and any external contribution is accepted by the target project's own process.

## 10. Implementation roadmap

### SE-0 — portable event schema
Define JSON Schema for Event, EventTransition, EventSubscription and EventResult. Add schema versioning, provenance, privacy and event lifecycle validation.

### SE-1 — local event bus
Implement an append-only local event log, region-filtered subscriptions and deterministic replay. Add unit tests for duplicate events, expired events, invalid transitions and unauthorized payloads.

### SE-2 — Grid Lab integration
Create one simple in-game event (e.g. resource regeneration or nearby hazard) using the existing GameAction set and central resolver. Add an optional group quest with two agent roles. Keep the existing Grid Lab action vocabulary unchanged.

### SE-3 — Quest adapter
Parse a small portable quest definition; connect acceptance criteria to test artifacts and a human-readable event timeline.

### SE-4 — Wikidata sandbox quest
Read a small, documented SPARQL result set, freeze it as a dated/hash-identified snapshot, ask a learner to identify a missing relation or inconsistent statement, and require source-backed review before any edit is submitted.

### SE-5 — multi-engine bridge
Adapt the same Event/Quest contracts to Matrix Core and later to Gravity, keeping each domain's time, units and legal actions separate.

### SE-6 — collaborative network
Add participant roles, signed contributions, delayed/offline queues and conflict review based on the existing Interplanetary Knowledge Network design.

## 11. What is implemented vs. planned

| Item | Status |
|---|---|
| Grid Lab GameObjects, legal action space and tick resolver | Existing prototype lineage; verify exact source version before changing |
| Matrix Core World/Agent/Observation/Action/Feedback/Experiment concepts | Existing separate codebase; adapter not yet proven |
| Event-driven precursor in historical Gemini snippet (GameEvent, UPGRADE_COMPLETED, journal update) | Historical illustrative snippet, not current runtime implementation |
| Smart Event engine / event bus | PLANNED |
| Portable Quest parser/validator | PLANNED |
| Wikidata / OSM / Zooniverse / Foldit adapters | PLANNED |
| Actual external scientific contribution workflow | Must be authorized and accepted by the target platform; not implemented by this document |
| Full LUMINI compiler | PLANNED / specification stage |

## 12. Recommended next concrete task

Build **SE-0: Event and Quest JSON Schemas + validators** beside the v0.10 Observation and RunManifest contracts.

Then write exactly one executable vertical slice:
- a resource-regeneration Smart Event;
- two eligible agents with different capabilities;
- event subscription based on region;
- legal action choice from the existing action set;
- resolver execution;
- event closure and a reproducible event/quest report.

This small loop connects the user's early vision (collective rules and internet exchange), the 2025 Wikidata quest collection, the old MMO/board-game ideas, the existing Grid Lab and Matrix Core, and the future LUMINI compiler without pretending they are already one implemented system.

## Sources and historical pointers

### User's own materials
- Discord server "Matrixgame – Der göttliche Aufstieg": posts on 2025-04-06, 2025-05-26 and 2025-08-06; Wikidata quest collection pointer: https://www.wikidata.org/wiki/Wikidata:Quests
- Google Docs: "Kollektive Formelsammlung für das Matrixgame" — open document examined during this review.
- Google Docs: "Fragment des Übergangs 6 bzw. Matrixgame-Journal Fortsetzung" — outline/sections examined during this review.
- Repository historical source: KIGameTrainingGeminiAndreasKopieAll23.07.2026|05:36Uhr, Open-Source / MMO / strategy-game section.
- Repository historical source: AI-Game Gemini 220816042026, including the illustrative GameEvent / UPGRADE_COMPLETED snippet and Earth Sync fictional scenario.
- Existing design docs: docs/LEARNING_QUESTS_AND_GAMIFICATION.md and docs/INTERPLANETARY_KNOWLEDGE_NETWORK.md.

### External technical references
- Zieliński et al. (2021), "Smart Events in Behavior of Non-player characters in Computer Games", DOI: https://doi.org/10.1007/978-3-030-77977-1_13
- Wikidata Query Service: https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/en
- OpenStreetMap API Usage Policy: https://openstreetmap.github.io/owg-website/policies/api/
- Zooniverse collaboration: https://www.zooniverse.org/get-involved/collaborate
- Foldit: https://fold.it/about_foldit
- EVE Online Project Discovery: https://www.eveonline.com/discovery
- EVE Online, "The First Decade of Project Discovery" (2026-03-06): https://www.eveonline.com/news/view/the-first-decade-of-project-discovery

**Core principle:** Play should create something useful beyond the score. Every event should be understandable, every action permissioned and testable, every quest produce an observable contribution, and every claimed benefit retain its evidence and limits.
