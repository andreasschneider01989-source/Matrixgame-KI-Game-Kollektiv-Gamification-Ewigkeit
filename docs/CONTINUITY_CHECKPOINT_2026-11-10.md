# Matrixgame continuity checkpoint

**Date:** 2026-11-10  
**Branch:** `agent/continuity-2026-11-10`  
**Status:** planning and integration checkpoint; no merge to `main` performed.

## Purpose

Preserve project continuity across conversations and tools. The goal is to continue from existing verified components, make small testable improvements, and avoid creating parallel architectures or claiming that planned features already exist.

## Current source-of-truth map

| Area | Existing location | Status / rule |
|---|---|---|
| Shared game state | `matrixgame-protocol/game-state.schema.json`, `game-state.example.json` | Existing protocol; reuse and version it |
| Deterministic JS prototype | `matrixgame-protocol/engine.js` | Existing local prototype, not a public multiplayer service |
| Experiment core | `matrixgame-core/matrixgame-core/` | Java 21, small dependency-free core; use for reproducible experiments |
| LUMINI compiler design | `docs/LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md` | Architecture proposal; compiler/DSL not implemented |
| Knowledge graph and evidence | `docs/UNIVERSAL_KNOWLEDGE_ENGINE.md`, `docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md` | Proposed architecture and schemas |
| Collective improvement lifecycle | `docs/COLLECTIVE_IMPROVEMENT_NETWORK.md` | Proposal: observe → describe → triage → analyze → quest → implement → verify → publish |
| Integration reference kit | Generated separately as `Matrixgame_Integration_Kit_v0.1.zip` | Local artifact; examples tested, adapters not deployed |

## Recent working evidence

1. The immune-system desktop screenshot showed a running Swing/Java visualization and an Eclipse project. The console reported 18 pathogens, 7 memory cells (2 active), 15 symbionts, inflammation 0.72, 2 memory hits and 103 entities. Those displayed values remained unchanged from tick 150 through tick 1650.
2. This is a diagnostic clue, not proof of a bug. The next run should log state hashes and event counters to distinguish a stable equilibrium from a stalled update pipeline.
3. Browser inspection found Google Colab open with a connected Python runtime and existing Matrixgame notebooks. The browser view is not a full audit of Drive contents.
4. GitHub, Gmail and Google Calendar actions are available in the working environment. Google Drive/Docs connector access was unavailable in the inspected environment; browser-visible tabs do not imply API write access.
5. A public-repository search found historical archive content matching personal banking/contact-data field patterns. This requires a separate manual review and cleanup plan. Do not copy values into issues, notebooks, public logs or this repository. Removing a current file would not remove earlier Git history.

## Continuity and safety rules

- Treat this repository's code and schemas as the canonical project record; treat conversations, browser tabs and model outputs as context or proposals.
- Keep `FACT`, `MODEL`, `HYPOTHESIS`, `OPEN` and `FICTION` distinct from implementation status `PLANNED`, `PROTOTYPE`, `IMPLEMENTED`, `TESTED`, `VERIFIED`.
- Every result should record at least model version, code commit, input parameters, random seed when applicable, tick count, units, data source and uncertainty.
- Make changes on a feature branch. Prefer small commits and pull requests. Do not merge or publish consequential changes without review.
- Never move private Drive/Docs material into public GitHub or public Colab by default. Classify as `public`, `internal` or `private` before import.
- Never store credentials, tokens, bank data or private contact records in source code, events, public notebooks or issue text.
- External writes (messages, calendar actions, publishing, repository merges) require explicit authorization at the point of action.
- A model may prepare code and checks in the current session, but it cannot continue working in the background after the session ends unless a scheduled automation is explicitly configured.

## Immediate sequence

1. **Privacy review:** identify sensitive files and decide which public files can be redacted, removed or moved; plan history cleanup separately.
2. **Tick diagnostics:** add `stateHash`, `eventsApplied`, `births`, `deaths`, `replications`, `kills` and `actionsApplied`.
3. **Entity state:** define stable typed state records and lifecycle events without replacing the current simulator all at once.
4. **Protocol adapter:** map simulator events to the existing versioned JSON protocol; keep event log and current state separate.
5. **Cross-language parity:** keep Java, JavaScript and Python implementations on the same deterministic test vectors.
6. **Colab analysis:** use Colab for experiments and plots; keep the repository/JSON version as the source of truth.
7. **LUMINI:** only after Model IR is stable, define lexer/parser/type and unit checks. No generated model code should execute without tests and human review.

## Review note

This checkpoint was created on a feature branch. It does not change `main`, edit Google Drive, deploy a web service, or modify the historical privacy findings.
