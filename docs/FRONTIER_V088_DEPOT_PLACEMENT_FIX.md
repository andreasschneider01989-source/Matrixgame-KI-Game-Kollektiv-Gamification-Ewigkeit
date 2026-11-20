# Matrixgame v0.8.8 – Depot placement fix

## Problem
In v0.8.7, BUILD_DEPOT was only considered legal when the adjacent cell was completely empty. In Boulder Dash terrain, ordinary DIRT occupies cells, so the action was filtered out before the build rule ran. This made the advertised build action appear broken even when the player had three gems.

## Change
- BUILD_DEPOT is now legal on an empty adjacent cell or a cell containing only ordinary DIRT.
- Building a depot automatically removes ordinary DIRT from that one adjacent tile.
- Rocks, gems, units, walls, buildings and other objects still block construction.
- Added regression checks for legal-action availability and actual depot placement on a DIRT tile.
- Existing v0.8.7 source and branch remain unchanged.

## Controls
1. Select a human-controlled PLAYER.
2. Collect at least 3 gems.
3. Press C (or choose BUILD_DEPOT with Q/E).
4. Press the arrow key toward an adjacent empty tile or ordinary dirt tile.
5. The depot should appear as a gold-coloured `▣` and the inventory should decrease by 3.

## Verification
The regression test is included in the source, but this update has not yet been compiled or executed in a Java runtime. Please run:

```powershell
javac -encoding UTF-8 -d out src/rhgr/MatrixgameUniversalGridLab_v08_8.java
java -cp out rhgr.MatrixgameUniversalGridLab_v08_8 --self-test
```
