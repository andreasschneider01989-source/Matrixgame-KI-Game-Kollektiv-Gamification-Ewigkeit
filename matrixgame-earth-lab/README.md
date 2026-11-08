# Matrixgame Earth Lab — Gaia Water-Cycle MVP

Version 0.1.0 · PROTOTYPE · Java 21 · no external dependencies

The first executable Gaia component is a deliberately small soil-water bucket model. It is a testable starting point for the Planet Earth project, not a complete rainforest, hydrology, weather, or climate model.

## What it represents

For one discrete model step (the demo uses a daily step), the model tracks precipitation, infiltration limited by a configured maximum rate, surface runoff, soil-water storage, actual evapotranspiration, deep drainage, and the water-balance residual.

Governing balance:

    precipitation + initial soil water
      = surface runoff + actual ET + deep drainage + final soil water

All water quantities are millimetres of water equivalent per model step. A residual close to zero is a necessary bookkeeping check, not proof that the model represents a real catchment accurately.

## Quick start

Requires JDK 21 or newer.

    sh test.sh
    sh run.sh

The self-test is dependency-free. The demo prints a synthetic wet-day step followed by a synthetic dry-day step.

## Epistemic boundaries

- MODEL: equations and deterministic update implemented here.
- HYPOTHESIS: claims that the model predicts a particular real forest, soil, or catchment.
- FACT: only sourced measurements supplied with a future dataset and provenance record.
- OPEN: local infiltration, soil capacity, roots, canopy interception, groundwater exchange, and spatial heterogeneity are not represented yet.
- FICTION / GAME: XP, quests, world states, and speculative planetary scenarios.

Synthetic demo values are illustrative and are not measurements from Darmstadt, the Amazon, or any other real location.

## Known limitations

This is a single-bucket, lumped model. It does not represent rainfall intensity within a day, canopy interception, multiple soil layers, groundwater, root-depth differences, snow, frozen soil, slope, lateral flow, soil texture, compaction, or feedbacks among vegetation, albedo, surface temperature, and atmospheric moisture.

The model must not be used to prescribe ecological interventions. Field actions require appropriate permissions, local knowledge, ecological assessment, and expert review.

## Next increments

1. Add explicit time-step metadata and dated forcing series.
2. Add a layered soil profile and independently tested drainage.
3. Add canopy interception and throughfall with a second water-balance check.
4. Compare model output with an openly licensed local dataset.
5. Add heat and energy balance only after water balance and provenance are stable.
6. Connect the model to LUMINI Model IR and Matrix Core only through a versioned, reviewed adapter.

See the research review at ../docs/GAIA_ASTROBIOLOGY_AI_AND_EARTH_SIMULATION_2026-11-08.md and the broader Planet Earth chapter at ../docs/PLANET_EARTH_HABITABILITY_AND_EVOLUTION.md.
