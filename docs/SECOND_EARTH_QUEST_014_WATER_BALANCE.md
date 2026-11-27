# SECOND EARTH QUEST 014 — Reproducible Water-Balance Experiment

**Status:** MODEL / illustrative software experiment  
**Strategy:** combined  
**Priority:** balanced  
**Seed:** 20261127  
**Duration:** 120 synthetic daily steps  
**Units:** water in mm; temperature in °C; vegetation and soil-quality indices are dimensionless proxies.

## Purpose

Compare a reference climate sequence, a drought/heat control, and a combined restoration hypothesis under paired weather forcing. The experiment is intentionally small so that its accounting and acceptance tests can be inspected.

## Scenarios

1. **Reference:** generated synthetic weather without the drought perturbation.
2. **Drought control:** on days 21–80, rainfall is multiplied by 0.62 and temperature receives +4 °C.
3. **Combined restoration:** exactly the same drought rainfall and temperature as the control; infiltration fraction is increased by 0.08 during the drought window, potential evapotranspiration demand is multiplied by 0.88, and the soil-quality proxy increases slowly.

The restoration parameters are **assumptions for a controlled software experiment**, not measured intervention effects or recommendations for real land management.

## Water accounting

For each daily step:

`old_storage + rainfall = new_storage + runoff + actual_evapotranspiration`

Runoff includes rainfall that does not infiltrate and any soil-bucket overflow. The model records the daily residual and rejects runs where its absolute value is at least 1e-8 mm.

## Reproduction

Run:

```bash
python experiments/second_earth_quest_014_water_balance.py
```

The script uses only the Python standard library. Seed, duration, capacity, initial storage, forcing generator, scenario parameters, and tests are in the source file.

## Acceptance tests

- Same seed produces identical synthetic weather.
- Each scenario has exactly 120 steps.
- Soil water remains between 0 and 150 mm.
- Vegetation index remains between 0 and 100.
- Daily water-balance residual remains below 1e-8 mm.
- Drought control and restoration receive exactly the same rainfall and temperature sequence.

## Run result (2026-11-27)

All acceptance assertions passed.

| Scenario | Rain total (mm) | Runoff total (mm) | Actual ET total (mm) | Final soil water (mm) | Final vegetation index |
|---|---:|---:|---:|---:|---:|
| Reference | 289.225 | 63.630 | 200.776 | 94.820 | 39.637 |
| Drought control | 235.024 | 51.705 | 214.635 | 38.684 | 34.704 |
| Combined restoration | 235.024 | 44.631 | 194.845 | 65.549 | 37.017 |

Maximum absolute daily balance residual: approximately 1.42e-14 mm in each scenario.

## Interpretation

In this toy model, the combined intervention scenario ends with more soil water and a higher vegetation proxy than the drought control, while receiving identical rainfall and temperature forcing. This is a result of the chosen assumptions and equations; it does **not** establish that a real restoration intervention will produce these magnitudes.

## Explicit limitations

Single soil bucket; synthetic forcing; no calibration against observations; no groundwater, lateral flow, runoff routing, plant species, roots, nutrient cycling, soil texture, radiation balance, canopy dynamics, or regional climate feedback. The vegetation and soil-quality indices are teaching proxies, not physical or biological measurements. This experiment is not a forecast for Darmstadt, Germany, or Earth.

## Next validation gate

Before any real-world interpretation: compare the water-balance structure with hydrology references, select an observed site and dataset, document units and missing data, calibrate on one period, validate on a separate period, and report uncertainty and baseline skill. Keep the current toy model as a regression fixture.
