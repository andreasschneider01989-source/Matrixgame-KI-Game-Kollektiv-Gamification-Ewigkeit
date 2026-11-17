# Matrixgame Session Handoff — Smart Events and Open-Source Quest Bridge
**Date:** 2026-11-17  
**Branch:** smart-events-open-source-quests-2026-11-17  
**Previous continuity branch:** matrixgame-continuity-wellbeing-2026-11-17

## What was reviewed
- Discord server "Matrixgame – Der göttliche Aufstieg": 2025-04-06 foundational rules and "raise happiness by an octave" quest; 2025-05-26 request to preserve the interaction loop and Colab notebook pointer; 2025-08-06 public post announcing a growing Open-Source Quest Collection for Wikidata, Wikimedia and Wikipedia.
- Google Docs "Kollektive Formelsammlung für das Matrixgame": Genesis-Core, axioms, SI units, PAL, 3D simulation framework, quest architecture, verification/falsification, dimensional checks, Bayes updates, and didactic chain.
- Google Docs "Fragment des Übergangs 6": compiler/world model, Guardian, system evaluation, didactic causal chain, code compiler for health/longevity, open Matrixgame journal / knowledge network, and self-management.
- Open AI-model tabs: supplementary reviews of current project strengths/gaps and implementation order.
- Repository historical files: July 2026 Open-Source/MMO/strategy-game protocol; August 2026 AI-Game Gemini event-driven example; existing Learning Quests and Interplanetary Knowledge Network docs.
- External Smart Events paper and current official information on Wikidata Query Service, OpenStreetMap API, Zooniverse, Foldit and EVE Project Discovery.

## Important source limitations
- The public Colab notebook named "Modellierung des kollektiven Bewusstseins" could not be fetched during this review (cache miss). The open Colab tab was a different introductory notebook; do not treat it as the 2025 archive.
- A local HTML tab was blocked by browser policy and was not examined.
- The historical code snippet using GameEvent / UPGRADE_COMPLETED is an illustrative precursor, not the current Grid Lab runtime.
- Smart Events implementation, EventBus, Quest parser and external adapters remain PLANNED.

## Key synthesis
1. Matrixgame's 2025 foundation contains two community rules: protect against unwanted physical violence and preserve communication/knowledge access.
2. The August 2025 Discord post establishes a concrete Open-Source Quest Collection idea starting with Wikidata and intended to connect Wikimedia/Wikipedia and other contributions.
3. The July 2026 repository protocol maps Albion Online, EVE Project Discovery, Plague Inc., OGame, Wikidata/DBpedia, OpenStreetMap, Zooniverse and Foldit to transferable game mechanics.
4. The historical notes already propose a strategy-game progression from Tic-Tac-Toe to Connect Four, Tak, Chess and Go, to teach legal actions, minimax, alpha-beta, MCTS and neural policies.
5. The 2021 Smart Events paper supplies a concrete event-driven NPC architecture: event state, region of influence, participants, urgency, available actions, event lifecycle and message-board notification; group behavior trees can coordinate agents. It also examines a non-emergency shop bargaining event.
6. This should extend the shared action/resolver architecture, not replace it. Smart Events provide context and coordination; policies choose among legal actions; the resolver remains authoritative.

## New GitHub document
- docs/MATRIXGAME_SMART_EVENTS_OPEN_SOURCE_QUESTS_2026-11-17.md
- docs/INDEX.md updated to include it.

## Next implementation tasks
### SE-0 / v0.10
- JSON Schemas and validators for Event, EventTransition, EventSubscription and EventResult alongside Observation and RunManifest.
- Unit tests for invalid transitions, duplicate events, expired events, untrusted sources and permission failures.
- Round-trip serialization and deterministic replay.

### SE-1
- Minimal append-only local event log and region-filtered subscriptions.

### SE-2
- One Grid Lab vertical slice: resource-regeneration Smart Event with two agent roles, legal action selection from the existing action set, shared resolver, event closure and exportable event report.

### SE-3+
- Portable Quest parser/validator.
- First Wikidata sandbox quest using a frozen, dated result snapshot and a source-backed review step.
- Only later connect OSM, Zooniverse, Foldit, EVE-inspired citizen-science tasks or other external sources.

## Acceptance rule
A Smart Event is complete only if trigger, lifecycle, source, permissions, affected agents, legal actions, resolver outcome, event log and observable outcome can be reproduced and reviewed. A quest is complete only when its deliverable passes declared acceptance criteria; clicks and self-reported XP are not proof.

## Working principles
- Preserve the existing GameObject / Observation / DecisionPolicy / legal Action / Resolver split.
- Do not silently overrule a player's command with a low-priority event.
- Pause, decline and recovery must remain valid choices.
- Keep personal wellbeing, health and career data private.
- Distinguish FACT / MODEL / HYPOTHESIS / OPEN / FICTION and PLANNED / PROTOTYPE / IMPLEMENTED / TESTED / VERIFIED.
- Review exact source and tests before making claims about implementation status.
