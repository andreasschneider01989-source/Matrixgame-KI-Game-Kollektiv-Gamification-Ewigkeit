# Matrixgame Protocol 1.0

This folder contains the first portable interface for Matrixgame and Collective AI Games.

## Files

- `UNIVERSAL_COLLECTIVE_GAME_PROMPT.md` – reusable prompt for starting a new game session.
- `game-state.schema.json` – JSON Schema contract for shared game state.
- `game-state.example.json` – example state that clients and AI agents can load.
- `engine.js` – deterministic JavaScript functions for validating state, calculating a transparent game indicator, and applying basic state changes.

## Quick local test

Requires Node.js 18 or newer for `structuredClone`.

From this folder:

```bash
node -e "const fs=require('fs'); const e=require('./engine'); const s=JSON.parse(fs.readFileSync('game-state.example.json','utf8')); console.log(e.validateState(s)); console.log(e.calculateProgress(s));"
```

## Design boundary

The prompt interprets and proposes. The JSON stores shared state. The deterministic engine validates and calculates. An HTTP service can later expose these functions to multiple clients.

The current engine is a local prototype. It is not yet a public multiplayer service. Before internet deployment, add:
- authentication and per-user authorization;
- schema validation at the API boundary;
- persistent storage and backups;
- concurrency control and event ordering;
- rate limits, abuse prevention and audit logging;
- transport security (HTTPS) and secret management;
- consent, moderation and data-retention rules.

Do not expose an unauthenticated write endpoint to the public internet.

## Scoring

The progress indicator is a transparent game heuristic. It is not a validated psychological, medical, consciousness or human-worth measure. The safety gate is shown separately so that other high scores cannot compensate for an unsafe state.

## Next milestone

Build a small HTTP API around the deterministic engine, then connect:
1. a mobile-friendly web client;
2. Java simulator agents;
3. language-model adapters;
4. a shared event log with human-confirmed consequential actions.

The protocol should remain versioned and backward-compatible where practical.
