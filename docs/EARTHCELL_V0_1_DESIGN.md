# EarthCell v0.1 — Design Specification

**Status:** PLANNED design; not implemented.  
**Purpose:** Create a small, auditable ecological cell model before integrating it with CollectiveAgent.

## Design principles

1. Small enough to understand and test.
2. Deterministic when supplied a fixed configuration and random seed.
3. Explicit resource accounting.
4. Separate state, transition rules, simulation orchestration, and visualization.
5. Use physical units where meaningful; label arbitrary game units clearly.
6. Do not claim ecological realism beyond the tested assumptions.

## State

Proposed initial state:

| Field | Meaning | Unit / interpretation |
|---|---|---|
| `solarEnergy` | sunlight available during a tick | energy units / tick |
| `water` | water stored in the cell | liters or normalized water units |
| `soilNutrients` | available nutrient pool | normalized nutrient units |
| `biomass` | plant biomass | normalized biomass units |
| `temperature` | cell temperature | °C if calibrated; otherwise explicitly arbitrary |

Additional configuration (not necessarily mutable cell state):

- `maxWater`
- `maxNutrients`
- `maxBiomass`
- photosynthesis efficiency
- water-use coefficient
- nutrient-use coefficient
- maintenance cost
- temperature growth factor / bounds

## First-pass transition model (MODEL, deliberately simplified)

For each tick:

1. Compute a growth opportunity from sunlight, water, nutrients, temperature, and available biomass capacity.
2. Limit growth by the scarcest required resource.
3. Convert solar energy into biomass with an explicit efficiency.
4. Deduct water and nutrients proportional to biomass growth.
5. Apply maintenance cost / biomass loss.
6. Clamp state to valid bounds and record any overflow or loss.

Illustrative structure (not a validated ecological equation):

`growth = min(energyLimitedGrowth, waterLimitedGrowth, nutrientLimitedGrowth, capacityLimitedGrowth)`

Then:

- `biomass += growth - maintenanceLoss`
- `water -= waterUsePerBiomass * growth`
- `soilNutrients -= nutrientUsePerBiomass * growth`
- `solarEnergy -= energyUsed`

Do not use this as a real photosynthesis or climate model until calibrated against data.

## Conservation / accounting requirements

- Water cannot increase except through a defined input (rain, transfer, irrigation).
- Nutrients cannot increase except through a defined input (deposition, decomposition, transfer).
- Biomass cannot increase without an energy and matter pathway.
- Solar energy is an external input; it may be transformed, stored, dissipated, or exported.
- If the model uses normalized units, the accounting is still explicit.
- Every state update should be logged or testable.

## Proposed Java shape

Package: `de.matrixgame.earth`

Initial files:

- `EarthCell.java` — state + validation; no simulation-wide random logic.
- `EarthCellConfig.java` — immutable parameters.
- `EarthCellModel.java` — deterministic state transition.
- `EarthCellSnapshot.java` — read-only state for logging / UI.
- `EarthCellTest.java` — conservation and boundary tests.

Keep the first version independent of Swing, UI, CollectiveAgent, and external AI APIs.

## Acceptance tests for v0.1

1. No growth when sunlight is zero.
2. No growth when water is zero.
3. No growth when nutrients are zero.
4. Biomass never exceeds configured capacity.
5. Water and nutrients never become negative.
6. Without external water input, water does not increase.
7. Without nutrient input, nutrient pool does not increase.
8. Same initial state + same config => same result.
9. Invalid configuration (negative capacity, efficiency outside [0,1], etc.) is rejected.
10. Growth and resource use are quantitatively logged.

## Later extensions (not v0.1)

- rain / evaporation
- decomposition and nutrient recycling
- neighboring cell transfer
- plant species
- temperature dynamics
- agent actions (restore, irrigate, harvest, protect)
- external weather / ecological datasets

## FACT / MODEL / HYPOTHESIS / VISION

- **FACT:** Plant growth depends on energy, water, nutrients, and environmental conditions; these relationships are more complex than this toy model.
- **MODEL:** The proposed cell uses simplified limiting-resource rules.
- **HYPOTHESIS:** Even this small model may reveal useful trade-offs between exploitation and regeneration.
- **VISION:** EarthCell becomes a modular learning environment for ecological restoration strategies.
- **LIMITATION:** Simulation outcomes are not evidence that a real-world restoration intervention will work without empirical validation.
