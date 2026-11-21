# Matrixgame Infinity Canvas — Open Collaborative Interface Architecture

**Artifact ID:** mg.architecture.infinity-canvas-platform-001  
**Version:** 0.1.0  
**Status:** PROPOSED ARCHITECTURE / IMPLEMENTATION BACKLOG  
**Claim status:** MODEL  
**Implementation status:** PLANNED  
**Compatibility:** Additive to the 11 November 2026 Dual Interface specification.

## 1. Purpose

Turn the existing Infinite Canvas concept into a modular, open, human-and-AI interface for exploring knowledge, missions, simulations, evidence, and collective experiments.

The Canvas is not the simulation engine, database, or authority for scientific truth. It is a navigable projection and interaction surface over versioned project objects and simulation snapshots.

The existing architecture's governing invariant remains:

**One simulation state → one versioned snapshot → multiple consistent views.**

The console, JSONL stream, Canvas, exports, and agents must consume the same canonical data contract. The Canvas may submit a typed command or proposal, but only the authoritative domain service validates and commits state-changing actions.

## 2. What the Canvas should make possible

A user can zoom from a global overview into:
- a world, region, ecosystem, or simulation;
- a mission and its quest graph;
- an individual entity, Unit, agent, resource, or event;
- a variable, measurement, forecast, evidence record, or source;
- a decision trace, experiment run, or code module.

Canvas objects should be typed, linkable, searchable, selectable, inspectable, versioned, and exportable. A board may contain freeform notes and drawings, but those must remain distinct from evidence-backed knowledge nodes and executable simulation objects.

## 3. Layered architecture

| Layer | Responsibility | Authority |
|---|---|---|
| Domain / Simulation Core | Rules, world state, deterministic transitions | Canonical for simulation |
| Shared Data Contract | WorldSnapshot, EntitySnapshot, VariableDescriptor, RelationSnapshot, SimulationEvent, RunManifest | Canonical exchange format |
| Knowledge / Evidence | Claims, sources, provenance, review state, quests | Canonical for knowledge records |
| Canvas Projection | Spatial layout, camera, rendering, selection, inspectors | Read/propose; not simulation authority |
| Interaction Gateway | Validates user/agent commands and permissions | Only path for committed actions |
| Collaboration | Presence, cursors, annotations, concurrent board editing | Shared workspace state |
| Persistence / Sync | Version history, snapshots, offline queue, export/import | Durable storage and synchronization |
| Agent Gateway | Scoped read, search, propose, explain, run approved tools | Capability-limited access |

Never allow a visual drag to silently change physical simulation coordinates unless the active tool explicitly means MOVE_ENTITY and the command passes domain validation. Moving a note on the board is not the same as moving a Unit in the simulated world.

## 4. Three complementary visual modes

1. **Freeform Canvas:** sketches, notes, frames, images, annotations, storyboards, and human planning.
2. **Semantic Graph:** typed nodes and edges for causal hypotheses, knowledge relationships, quests, dependencies, and workflows.
3. **Simulation View:** live or replayed entities, teams, resources, trails, heatmaps, vectors, timelines, and metric projections.

These can share one camera and navigation shell while retaining distinct interaction semantics and data contracts. Do not force every visualization into a sticky-note model.

## 5. Open-source technology strategy

Prefer permissively licensed components and preserve replaceable adapters.

### Candidate roles

- **Excalidraw (MIT):** strongest initial candidate for an embeddable, human-friendly freeform board, sketching, annotations, and portable scene export. Its core npm package is open source; hosted collaboration features should not be assumed to be turnkey for a self-hosted embedded app.
- **Quickdraw (MIT):** lightweight alternative worth a small technical spike for a simpler custom canvas and JSON snapshot/diff integration. Its current ecosystem and scale suitability require independent evaluation.
- **React Flow (MIT):** use for semantic node/edge editors, quest graphs, causal diagrams, and workflow authoring; it is not a complete freehand whiteboard.
- **PixiJS (MIT):** candidate rendering layer for high-density simulation entities, effects, trails, and GPU-accelerated 2D scenes. Benchmark before committing.
- **Yjs (MIT) or Automerge (MIT):** evaluate for concurrent edits to board documents, annotations, and presence-related shared state. Do not use a CRDT merge as the authority for physical simulation transitions.
- **JSON Canvas:** support import/export as an interoperability format for compatible node/edge board content; define Matrixgame-specific typed metadata in a namespaced extension and never discard unknown extension fields.

### License decision

Do not make tldraw SDK a required dependency for the open-source core: its current SDK is source-available but commercially licensed, not OSI open source. It may be evaluated separately only if its licensing terms are compatible with the intended distribution and business model.

This is a preliminary technology selection, not a completed legal review. Pin versions, record exact license files, audit transitive dependencies, and review asset/font licenses before release.

## 6. Collaboration and distributed state

Separate two classes of data:

**Collaborative document state**
- board positions and layout;
- freeform annotations and drawings;
- comments, cursors, viewport presence;
- shared quest planning and review notes.

This may use CRDT-based synchronization to merge concurrent edits and support offline-first work.

**Authoritative simulation state**
- entity positions, health, resources, physics, actions, tick order, and event resolution.

This remains owned by the simulation service. Clients submit commands with actor, expected state/version, command ID, permission, and preconditions. The server validates and emits the next versioned snapshot and event log. Conflicting commands are resolved by deterministic domain rules, not by last-write-wins document merging.

Offline users may prepare proposals and annotations. They must not silently rewrite a canonical world history; synchronization revalidates proposals against the current world version.

## 7. Open interface contract

Minimum conceptual records:

- CanvasDocument: document ID, schema version, title, owner/visibility policy, version, object references.
- CanvasObject: stable ID, type, geometry/layout, z-order, metadata, provenance.
- DomainReference: object type, stable domain ID, source version, deep link.
- CanvasViewState: camera center, zoom, active layers, axis mapping, filters.
- CommandEnvelope: command ID, actor/agent ID, action, target, payload, expected version, timestamp, permission scope.
- WorldSnapshotRef: run ID, tick, model version, snapshot hash, schema version.
- PresenceEvent: pseudonymous session ID, cursor, viewport, expiry.
- AuditEvent: actor, operation, target, before/after version, result, provenance.

Every object needs a stable ID. Coordinates and view state must not be mistaken for physical units. Use explicit coordinate spaces: canvas, world, screen, and where applicable SI-valued domain coordinates.

## 8. AI and human interaction

Expose scoped, inspectable operations:
- read a selected object or visible region;
- search knowledge nodes and evidence;
- explain a graph or simulation state;
- propose a new node, edge, quest, annotation, or scenario;
- request a forecast or experiment plan;
- submit a typed simulation command;
- run an approved deterministic experiment;
- attach a result and provenance record.

Default agent permission is read-only. Proposals are visibly distinguished from accepted state. Any action that changes shared simulation state requires role authorization, validation, and an audit event. External real-world actions remain outside this Canvas MVP.

The same APIs should support keyboard navigation, screen readers, touch, and non-visual summaries. Color must never be the only channel for team or status identity.

## 9. MVP sequence

### Canvas MVP 0 — Local interactive board
- Create a small web app shell and one canvas adapter.
- Load a versioned sample CanvasDocument from a local JSON fixture.
- Render typed cards for Mission, Quest, KnowledgeNode, Evidence, SimulationRun, and Agent.
- Support pan, zoom, select, inspect, search, and save/load JSON.
- Add no multiplayer, account system, external LLM execution, or live simulation yet.

### MVP 1 — Shared snapshot projection
- Read an existing WorldSnapshot fixture and render Units/relations.
- Add variable-driven X/Y axes, color/shape/size encodings, legend, filters, and inspector.
- Prove that Canvas and JSONL refer to the same snapshot hash and tick.
- Keep simulation rules outside the renderer.

### MVP 2 — Safe interaction gateway
- Add typed command proposals and permission checks.
- Show preview, validation result, and explicit confirmation.
- Record accepted/rejected commands in an audit log.
- Test stale versions, invalid actions, duplicate commands, and unauthorized attempts.

### MVP 3 — Local-first collaboration
- Add presence and shared annotations.
- Evaluate Yjs versus Automerge on conflict tests, offline reconnect, document size, and multi-tab behavior.
- Keep simulation state server-authoritative and versioned.

### MVP 4 — Multi-agent and public rooms
- Add read-only AI observers and proposal agents.
- Add human review, role permissions, moderation, abuse reporting, quotas, and export.
- Run a small opt-in pilot before any open public service.

## 10. Required acceptance tests

- Round-trip JSON save/load preserves stable IDs, metadata, and unknown extensions.
- Canvas view changes do not mutate canonical simulation state.
- Canvas and JSONL show the same run ID, tick, and snapshot hash.
- A typed command with stale expected version is rejected or revalidated.
- Unauthorized agent commands cannot commit.
- Offline board edits merge without overwriting unrelated edits.
- Conflicting simulation commands are resolved by domain rules and audited.
- No private personal data is included in public export by default.
- Keyboard-only navigation and non-color status cues are tested.
- Renderer remains responsive at a documented target entity count; benchmark hardware and frame-time distribution are recorded.

## 11. Risks and open questions

- Canvas performance and data density vary substantially by renderer and hardware.
- Collaboration adds authentication, authorization, moderation, storage, and operational cost; it is not just a WebSocket.
- CRDTs solve concurrent document edits, not the semantics of simulation physics or scientific truth.
- Freeform boards can become visually chaotic; use layers, filters, semantic zoom, search, and curated entry points.
- A global shared canvas should not imply one global writable database. Support independent workspaces, forks, local copies, and explicit publishing.
- Public participation needs accessible defaults, low bandwidth modes, localization, and safe anonymous/pseudonymous options.
- Plugin and model interfaces must be versioned and sandboxed; untrusted code is never executed directly from a canvas object.

## 12. Research and technology references

- Existing Matrixgame specification: docs/INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md.
- Excalidraw repository and MIT license: https://github.com/excalidraw/excalidraw
- Quickdraw project and MIT licensing: https://tryquickdraw.com/
- React Flow and MIT license: https://reactflow.dev/
- tldraw licensing FAQ: https://tldraw.dev/faq
- tldraw collaboration architecture (useful technical reference, not selected as an open-source dependency): https://tldraw.dev/features/composable-primitives/multiplayer-collaboration
- Curated whiteboard technology and licensing comparison: https://github.com/nmndwivedi/awesome-whiteboard

## 13. Current status

This document is a proposed architecture and staged implementation plan. No new web Canvas app, multiplayer server, agent gateway, or global platform is claimed to have been implemented by this specification. The first concrete deliverable is a local, fixture-driven, testable Canvas MVP that reuses the existing shared-data principles.
