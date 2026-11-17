# Matrixgame Universal Grid Lab v0.9

Basis: v0.8 Expanded Genetic Sandbox. This branch adds only two user-facing changes while preserving the existing world, camera, 37-action vocabulary, resolver, and evolution architecture.

## Changes

- **Per-selected-unit control:** click any controllable unit, then switch `Ausgewählte Unit: KI/MENSCH`.
- **Exclusive human mode:** a human-controlled unit receives no AI fallback when the player is idle. Other units continue using their existing AI controllers.
- **Boulder Dash default:** the player starts in human mode.
- **Evolution mode:** creatures start AI-controlled; the player can take over an individual creature.
- **Simulation speed:** default 500 ms/tick (2 ticks/s), slider 100–2000 ms/tick; G/B remain available.
- Existing keyboard and mouse navigation, zoom, 120×80 world, and all 37 actions remain intact.

## Build

```powershell
javac --release 17 -Xlint:all -Werror -d out MatrixgameUniversalGridLab_v09.java
java -cp out matrixgame.universal.MatrixgameUniversalGridLab_v09
java -Djava.awt.headless=true -cp out matrixgame.universal.MatrixgameUniversalGridLab_v09 --self-test
```

## Verification

- Java 17 strict compile: passed locally.
- Headless self-tests: 60 passed (57 in v0.8 plus 3 exclusive-control regression checks).
- GUI start smoke test under Xvfb: process remained alive until intentional timeout; this is not a full Windows interactive playtest.

The Boulder Dash scenario remains turn-based as in v0.8: each manual key action advances one world tick. The speed slider controls the automatic Evolution clock.
