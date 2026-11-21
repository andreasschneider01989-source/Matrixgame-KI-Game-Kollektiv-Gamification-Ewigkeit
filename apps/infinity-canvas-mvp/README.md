# Matrixgame Infinity Canvas — MVP 0

A dependency-free, local-first browser prototype for the existing Matrixgame research document model.

## Run

From this directory, start any static HTTP server (the browser fetches the JSON fixture):

```bash
python -m http.server 8000
```

Open http://localhost:8000/apps/infinity-canvas-mvp/ when serving from the repository root, or http://localhost:8000/ when serving this directory.

## Test

Requires Node.js 20 or newer:

```bash
node --test apps/infinity-canvas-mvp/tests/model.test.js
```

## MVP 0 features

- Loads a versioned JSON fixture with Mission, Quest, KnowledgeNode, Evidence, SimulationRun, and Agent objects.
- Draws relations, supports pan, object dragging, cursor-centred zoom, selection, inspector, and search.
- Imports and exports JSON while preserving unknown document/object properties.
- Includes a keyboard-accessible object list and non-colour type labels.
- Uses no runtime dependency and makes no network requests except loading its local fixture.

## Boundaries

This is a canvas-document editor, not the simulation engine. Moving an object changes only its local canvas coordinates. It cannot alter canonical simulation state. No accounts, multiplayer, CRDT, external LLM, live WorldSnapshot adapter, or arbitrary code execution are included. Those require separate reviewed stages.

Canvas coordinates are layout coordinates, not physical units. The fixture's SimulationRun is explicitly illustrative, not an empirical result.
