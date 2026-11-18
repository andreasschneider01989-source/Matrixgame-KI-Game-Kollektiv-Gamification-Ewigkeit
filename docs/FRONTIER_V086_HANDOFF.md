# Matrixgame Universal Grid Lab v0.8.6 – camera controls milestone

## Source of truth

- User-provided v0.8.5 source branch: `fix-v08-4-format-exception`
- Source file: `2D Matrixgame Kollektiv KI Game Gridgame Basis Code for ChatGPT 18.11.2026 Java Code copie paste as text so it is not to long and visible for ChatGPT`
- Source blob SHA: `a15bf7010e588828d2196aadfba2bf7de6d58597`
- New class: `src/rhgr/MatrixgameUniversalGridLab_v08_6.java`
- New branch: `feature/v086-keyboard-camera-controls`
- v0.8.5 remains unchanged.

## What was already present in v0.8.5

- Universal 37-action vocabulary: WAIT, MOVE and eight typed interactions in four directions.
- Human-controlled units and AI-controlled units, with self-tests that verify the AI is suppressed while a unit is human-controlled.
- Pluggable decision algorithms and central action validation/resolution.
- Evolution scenario with genomes, mutation, food regeneration, reproduction, predation, population accounting and a fixed random seed.
- 120x80 finite grid, spatial snapshot index, mouse selection, right/middle-drag panning and cursor-centred mouse-wheel zoom.
- Existing BUILD action creates a generic 30-HP blocking building for two gems.
- Integrated `--self-test` suite (count in source: 67 calls to check()).

## v0.8.6 additive change

Adds keyboard camera controls without taking over the existing unit controls:

- Ctrl + Arrow keys: pan camera by 3 world cells.
- Ctrl + Equals: zoom in by factor 1.12.
- Ctrl + Minus: zoom out by factor 1/1.12.
- Ctrl + 0: reset camera to fit the world.
- Existing unmodified 0 key still fits the world.
- Existing mouse panning and wheel zoom remain available.
- Camera changes only `cameraX`, `cameraY`, and `zoom`; it does not alter world entity positions or simulation state.

The new class is named `MatrixgameUniversalGridLab_v08_6`; its title and self-test label are updated accordingly. The original v0.8.5 source remains untouched.

## Architecture assessment (qualitative)

Strong foundations:
1. The central resolver and typed action vocabulary make new mechanics easier to validate.
2. Controller/evaluator/decision-algorithm interfaces support AI comparisons.
3. Seeded simulation and existing self-tests support reproducible experiments.
4. Genome and lifecycle tracking already provide a foundation for evolution experiments.

Known next-step gaps:
- World dimensions and boundary walls are fixed; the current world is not infinite.
- BUILD is generic; there are no specialized towers, upgrades, production queues or placement UI yet.
- Teams exist as a numeric variable in the ecosystem, but there is no general faction-management or diplomacy layer.
- Unit progression by XP/skill trees is not yet a distinct mechanic.

## Verification status

Performed before GitHub write:
- Fetched the full source from the exact user-provided branch.
- Created a separate class and checked that no v0.8.5 class-name references remain.
- Checked that the new camera bindings and camera methods exist.
- Checked that old mouse drag panning and mouse-wheel zoom code remain in the copied source.
- Checked that the self-test label was updated.

Not yet performed:
- Java compilation.
- Running the GUI and testing keyboard shortcuts.
- Running `--self-test`.

Do not treat this as compiled or runtime-verified until those steps pass.

## Build and run

From a project root where the file is located at `src/rhgr/MatrixgameUniversalGridLab_v08_6.java`:

```powershell
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v08_6.java
java -cp out rhgr.MatrixgameUniversalGridLab_v08_6
java -cp out rhgr.MatrixgameUniversalGridLab_v08_6 --self-test
```

## Next small gameplay increment

Add one specialized defensive tower through the existing action/resolver architecture:
- tower HP and max HP;
- attack range, damage and cooldown;
- build cost and placement validation;
- deterministic targeting and event-log entries;
- tests for valid/invalid placement, range, cooldown, damage and destruction.

Do not begin the infinite/chunked world rewrite until this tower increment is independently tested.
