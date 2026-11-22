# v0.8.9 – Reliable depot actions

## Purpose
Fix depot construction as a playable action for both human-controlled units and autonomous standard units.

## Changes
- Selecting BUILD_DEPOT (C) automatically places the selected controllable unit in human-control mode.
- Direction keys and direction buttons execute the selected BUILD_DEPOT action.
- Before the action is submitted, the UI reports the exact reason when construction is impossible: missing BUILD capability, fewer than 3 carried gems, map boundary, or occupied target.
- Depot placement remains one shared typed action: BUILD_DEPOT(direction).
- The infrastructure policy now runs before IdleController filtering. Standard AI units with BUILD capability can therefore build, deposit, and produce even when their ordinary movement controller is idle.
- AI prefers an adjacent empty tile, then eligible ordinary dirt.
- Added a regression test for autonomous depot construction by an IdleController-backed HUNTER with 3 gems.

## Human controls
1. Select a controllable unit.
2. Press C (or choose BUILD_DEPOT with Q/E).
3. The unit is automatically switched to human control.
4. Press an arrow key or click a direction button.
5. The status line explains any unmet condition.

Cost: 3 gems in inventory. Energy is not currently a construction requirement. Target: adjacent empty tile or ordinary DIRT only.

## AI behavior
A standard AI unit with BUILD capability and at least 3 carried gems attempts an adjacent depot construction through the same legal-action catalog and action resolver.

## Verification status
Regression tests are included in the source but have NOT been compiled or executed in this environment. GUI behavior is therefore not yet independently verified. Treat this as a candidate build until local javac and self-test pass.
