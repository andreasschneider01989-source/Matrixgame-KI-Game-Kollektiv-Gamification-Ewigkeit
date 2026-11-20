# Matrixgame Universal Grid Lab v0.9.0 — Building Action

## Base and preservation
- Base source: `src/rhgr/MatrixgameUniversalGridLab_v08_9.java` on `fix/v089-reliable-depot-actions`.
- New standalone class: `src/rhgr/MatrixgameUniversalGridLab_v09_0.java`.
- Package remains `rhgr`; Java 17+; no external dependencies.
- The original v0.8.9 source is not overwritten.
- The universal action catalogue remains 49 entries. BUILD already existed in the shared action grammar; this increment makes it directly usable and adds a resource-aware rule for evolution cells.

## Player controls
1. Select a controllable unit on the grid.
2. Choose **Gebäude bauen (Y)** or press `Y`.
3. The selected unit is switched to human control for the manual build action.
4. Choose one direction with an arrow key, WASD, or a direction button.
5. A successful build creates a stationary square `BUILDING` object on the adjacent free tile.

`Q` / `E` still cycle all action modes. `C` continues to select BUILD_DEPOT. Existing movement, dig, push, attack, collect, drop, depot, deposit, produce and reproduce interactions remain in the same 49-option action vocabulary.

## Build costs
- Evolution `CREATURE`: 6.0 energy per building. Its energy and food variable are synchronized.
- Other build-capable units: 2 carried gems.
- The target tile must be inside the map and completely empty.
- Buildings block movement and are no longer pushable.
- A building records its builder ID and team.
- Standard non-evolution AI units can autonomously build at most one simple structure after higher-priority depot logistics. Evolution cells receive a low-priority BUILD preference only with energy surplus and without urgent nearby food.

These values are game-balance parameters, not claims about real construction costs or materials.

## Validation
The self-test includes the retained original building check and new regression checks for:
- legal BUILD in all four cardinal directions for an evolution cell;
- stationary/blocking building placement and owner/team provenance;
- exact energy consumption and no gem consumption for an evolution builder;
- informative rejection when energy is insufficient.

The dedicated workflow compiles with Java 17, `-Xlint:all -Werror`, then runs the headless `--self-test`. Results are reported only after GitHub Actions completes.

## Eclipse
- Create/open package `rhgr`.
- Copy `MatrixgameUniversalGridLab_v09_0.java` into that package.
- Run `rhgr.MatrixgameUniversalGridLab_v09_0` as a Java Application.
- For headless verification use the project terminal:
  `javac -encoding UTF-8 -Xlint:all -Werror -d out src/rhgr/MatrixgameUniversalGridLab_v09_0.java`
  then
  `java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v09_0 --self-test`

## Known boundaries
This is the existing 2D grid game. Buildings occupy one grid cell; no multi-cell footprint, construction animation, structural engineering, save/load, or 3D rendering is introduced in this increment.
