# Matrixgame Project Checkpoint — 2026-11-06

> Repository inspection snapshot. This is a source-level review of the default branch `main`, not a claim that the project was compiled or executed in this environment.

## Status labels

- **VERIFIED IN REPOSITORY**: observed in source or configuration on `main`.
- **REPORTED**: described in project/chat notes but not confirmed by this source inspection.
- **NOT YET VERIFIED**: requires local compilation or execution.
- **PLANNED**: design proposal, not implemented.

## Repository snapshot

- Repository: [Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit)
- Default branch: `main`
- Inspected commit: `609b0b2020ce0bd9888918f3ce12ac5eb9b3062e`
- Java/Maven project exists: `pom.xml`
- Main Java source root: `src/main/java/de/matrixgame/`
- Collective-agent demo source package: `de.matrixgame.collective`
- Existing protocol directory: `matrixgame-protocol/`
- Existing Java core package: `de.matrixgame.core`
- Existing physics package: `de.matrixgame.physics`

## Collective-agent files verified

| File | Status | Observation |
|---|---|---|
| `AgentGenome.java` | VERIFIED | Evolvable traits; random initialization and mutation |
| `CollectiveAgent.java` | VERIFIED | Sensing, action scoring, MOVE/EAT/SHARE/WARN/ATTACK/REST, reproduction |
| `GameEnvironment.java` | VERIFIED | Food grid, agents, share/attack, sequential simulation step |
| `SimpleSimulation.java` | VERIFIED | Demo population and 100-step loop |

**Important:** Source files are present. Compilation and successful execution are **NOT YET VERIFIED** by this checkpoint.

## Maven configuration

- `pom.xml` sets `maven.compiler.source=11` and `target=11`.
- No external dependencies are declared.
- Jar main class is configured as `de.matrixgame.core.MatrixGameCore`.
- A `run-ui` profile is configured for `de.matrixgame.ui.MatrixGameUI`.

## Reproducibility audit — observed issues

The current demo is **not reproducible from a single seed** yet:

1. `AgentGenome` uses a private static `new Random()`.
2. `GameEnvironment` uses a separate `new Random()`.
3. `CollectiveAgent.act(MOVE)` uses `Math.random()`.
4. `SimpleSimulation` uses `Math.random()` for spawn locations and starting energy.
5. `CollectiveAgent.id` uses random UUID values. IDs do not necessarily affect dynamics, but make output differ.
6. `getDangerAt()` consumes random values during sensing. Results depend on sensing order and number of calls.
7. The environment processes agents sequentially and actions have immediate effects. Agent iteration order can affect outcomes.

**Conclusion:** Seed 42 is not currently a sufficient guarantee. A single simulation-wide random source (or explicitly derived streams) must be injected into all stochastic components. For exact replay, stable agent IDs and deterministic iteration order are also needed.

## Other source-level risks / design questions

- `getAgents()` returns the mutable internal list.
- SHARE adds energy to the recipient without clamping to its maximum; giver pays a fixed 5 plus action cost.
- ATTACK subtracts 15 from a target and adds 8 to attacker, with immediate state changes.
- Sequential actions may create order-dependent outcomes; decide whether this is intentional or whether actions should be collected and resolved in phases.
- Food cells are consumed completely by one EAT action.
- Reproduction is probabilistic and occurs after an agent acts; it currently depends on the environment RNG.
- The current danger field is random per query rather than a stable environment property.
- No EarthCell class was found in the inspected Java source paths.
- No matching Java test source files were found in the inspected path list. The project does have a separate `matrixgame-core/.../test/.../SelfCheck.java`; its relationship to Maven test execution should be checked.

## Recommended immediate implementation sequence

1. Compile the existing Maven project locally and record the exact result.
2. Add deterministic random injection without changing game mechanics.
3. Add a small regression test: same seed + same config => same event log / statistics.
4. Record baseline statistics.
5. Implement EarthCell v0.1 as a separate package/module, then test its conservation rules.
6. Integrate agent behavior with EarthCell only after the independent cell model is stable.

## Next action for local verification

From the directory containing this `pom.xml`:

```powershell
mvn clean test
mvn package
```

If the current environment uses Java 22, note the installed version as well:

```powershell
java -version
mvn -version
```

Do not interpret successful compilation as scientific validation. It verifies build integrity, not the correctness or realism of the model.
