# Matrixgame Web Explorer — first playable Infinite Canvas prototype

**Date:** 2026-11-25  
**Status:** PROTOTYPE — isolated browser demo on `feature/web-infinite-matrix-explorer`  
**Entry point:** [`web/index.html`](../web/index.html)

## Goal

Provide the first directly interactive browser view for Matrixgame: a navigable 2D world, a camera with pan and pointer-centred zoom, selectable world nodes and mobile Scout units, basic movement commands, and a small exploration quest.

This is deliberately a small playable slice, not the finished Matrixgame, not a 3D engine, and not a scientific ecosystem simulator.

## Run locally

No package installation or external library is required.

From the repository root:

```powershell
python -m http.server 8000
```

Open `http://localhost:8000/web/` in a browser. Alternatively, open `web/index.html` directly.

## Controls

| Action | Control |
|---|---|
| Pan the camera | Drag empty canvas space |
| Zoom | Mouse wheel, or + / − buttons |
| Select a node or Scout | Left click |
| Send selected Scout to a position | Right click on the canvas |
| Move selected Scout | W / A / S / D or arrow keys |
| Larger movement step | Shift + movement key |
| Pause / resume | Space or button |
| Focus selected object | F |
| Reset camera | Home or Center view |
| Add Scout | + Scout |

The left panel filters the visible objects into Habitat, Ecology, and Knowledge layers. The right panel shows the current selection and progress for the small exploration quest.

## Current model

The demo contains illustrative world nodes for a Genesis Hub, Mycelium Forest, Freshwater Basin, Restoration Zone, Penicillin Lab, Knowledge Archive, Orbit Observatory, and Symbiosis Garden. These are fictional game coordinates and labels. They are not real-world locations, measurements, medical recommendations, or validated ecological predictions.

Scouts move toward a commanded point at a simple constant speed. The quest registers visits when any Scout enters the configured proximity radius of the Forest and Water nodes. The simulation can be paused. The renderer draws the same local state that the UI reads.

## Architecture boundary

The prototype follows the repository's [Infinite Canvas and dual-interface design](INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md):

- The canvas is a view, not the source of simulation truth.
- World coordinates and camera coordinates are separate.
- Pan and zoom change only the camera.
- Entity selection and movement commands act on the local demo model.
- The UI labels the content as prototype data.

**Not integrated yet:** LUMINI Model IR, Java simulation engines, Python scientific models, persistent save files, networked agents, multiplayer synchronization, 3D rendering, or a real-world geographic layer. These require explicit adapters and schemas rather than ad-hoc browser state.

## Manual acceptance checklist

- [ ] Page opens without a build step or third-party dependency.
- [ ] Dragging empty space pans the camera.
- [ ] Mouse-wheel zoom keeps the world point under the cursor approximately anchored.
- [ ] Left-click selects visible nodes and Scouts.
- [ ] Right-click gives the selected Scout a destination.
- [ ] W/A/S/D and arrow keys issue movement commands.
- [ ] Pause stops Scout movement and tick advancement; resume continues.
- [ ] Adding a Scout updates the object list and unit counter.
- [ ] Layer buttons filter objects without changing the world state.
- [ ] Reaching both quest locations completes the demo quest.
- [ ] The view remains usable at desktop and narrow mobile widths.

The implementation was committed to GitHub as a prototype. The checklist above is a verification plan; it must not be treated as passed until the page is run in a browser.

## Next increments

1. Extract the inline demo model into a small versioned world-state module.
2. Define a JSON snapshot schema compatible with the existing `WorldSnapshot`, `EntitySnapshot`, `SimulationEvent`, and `RunManifest` concepts.
3. Add deterministic reset/seed and automated browser smoke tests.
4. Add a read-only adapter for one existing simulation or Boulder Dash prototype.
5. Add persistence only after schema versioning and privacy boundaries are established.
6. Configure GitHub Pages or another static host after the prototype is reviewed.

## Design principles

- Keep the tested LUMINI compiler and existing Java prototypes untouched.
- Build new presentation work in an isolated, reviewable slice.
- Keep FACT, MODEL, HYPOTHESIS, OPEN, and FICTION distinct.
- Do not present generated labels, illustrative coordinates, or simulated values as measured science.
- Gamification rewards exploration, learning, and cooperation; it must not rely on shame, coercion, or addictive pressure.
