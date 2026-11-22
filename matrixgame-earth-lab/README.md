# Matrixgame Earth Lab — Gaia Water-Cycle MVP

Version 0.2.0 · PROTOTYPE · Java 21 · no external dependencies

This module contains two small deterministic water models. The first is a single soil bucket; the second adds a canopy store and a root-zone store. They are testable learning models, not complete rainforest, hydrology, weather, or climate models.

## Model A — single soil bucket

Tracks precipitation, infiltration limited by a configured maximum rate, surface runoff, soil-water storage, actual evapotranspiration, deep drainage, and water-balance residual.

## Model B — canopy and root-zone stores

Tracks:
- rainfall interception into a finite canopy-water store;
- throughfall reaching the ground;
- canopy evaporation limited by water in the canopy store;
- infiltration from throughfall;
- surface runoff when infiltration capacity is exceeded;
- root-zone storage and its capacity;
- root-zone evapotranspiration limited by available water;
- deep drainage when root-zone input exceeds storage capacity.

For both models, the accounting form is:

    precipitation + initial stored water
      = runoff + deep drainage + evaporation / ET + final stored water

For the two-store model the stored water includes both canopy and root-zone stores. Canopy interception and throughfall are internal transfers, not losses.

All water quantities are millimetres of water equivalent per model step. The current example values are synthetic. A near-zero balance residual is a necessary bookkeeping check, not proof of hydrological realism.

## Deterministic multi-perspective reviewer council

The single-bucket demo runs five separate rule-based reviewer roles:

- HYDROLOGY checks the water-balance residual.
- MODEL-INVARIANTS checks storage bounds.
- ECOLOGY warns that the model does not represent species, habitats, or biodiversity.
- DATA-STEWARD checks the declared input class and provenance status.
- RED-TEAM highlights omitted processes and calibration gaps.

These are deterministic Java reviewers, not language models or a live multi-AI service. A PASS means only that the specific software check passed. A WARNING must remain visible; one passing check cannot cancel another discipline's warning. The provenance category is a caller declaration and is not independently authenticated.

## Quick start

Requires JDK 21 or newer.

    sh test.sh
    sh run.sh

The test script runs nine single-bucket and seven canopy/root-zone self-tests. The demo prints synthetic wet/dry cases and independent reviewer findings.

## Epistemic boundaries

- MODEL: equations and deterministic updates implemented here.
- HYPOTHESIS: claims that the models predict a particular real forest, soil, or catchment.
- FACT: only sourced measurements supplied with a future dataset and provenance record.
- OPEN: sub-daily rainfall, multiple soil horizons, groundwater, root-depth differences, species-specific uptake, soil texture, and spatial heterogeneity are not represented.
- FICTION / GAME: XP, quests, world states, and speculative planetary scenarios.

Synthetic demo values are illustrative and are not measurements from Darmstadt, the Amazon, or any other real location.

## Known limitations

The two-store model is still a lumped, one-time-step model. Canopy water is filled by the current time step's rainfall up to remaining storage capacity; remaining rain is throughfall. Canopy evaporation is then withdrawn from the stored canopy water. Throughfall infiltrates up to the configured infiltration capacity; excess becomes surface runoff. Root-zone water above capacity becomes deep drainage, and root-zone ET is limited by the remaining storage.

This ordering is designed for explicit bookkeeping. It does not resolve within-day rainfall intensity, canopy drip timing, aerodynamic evaporation, interception by different canopy layers, leaf area, stomata, root distributions, preferential flow, lateral runoff, water tables, freeze/thaw, vegetation succession, or energy balance.

The model must not be used to prescribe ecological interventions. Field actions require appropriate permissions, local knowledge, ecological assessment, and expert review.

## Next increments

1. Add dated forcing series and a reproducible run manifest.
2. Add multiple soil horizons, root-depth profiles and independently tested drainage.
3. Add canopy layers and leaf-area or vegetation-class parameters backed by data.
4. Compare model output with an openly licensed local dataset.
5. Add surface-energy, carbon-cycle and biodiversity modules only with explicit units, boundary conditions, and validation.
6. Connect the model to LUMINI Model IR and Matrix Core only through a versioned, reviewed adapter.

See ../docs/GAIA_ASTROBIOLOGY_AI_AND_EARTH_SIMULATION_2026-11-08.md and ../docs/PLANET_EARTH_HABITABILITY_AND_EVOLUTION.md.

## Dated forcing series and reproducible run manifests

The 0.3 foundation adds:

- `GaiaForcingSeries`: immutable daily rows with a calendar date and three forcing fields, all expressed in mm/day.
- Missing observations remain explicit through `OptionalDouble.empty()`. They are not silently interpreted as zero.
- Dates must be contiguous, and present values must be finite and non-negative. A simulation conversion fails on the first incomplete day.
- `GaiaRunManifest`: a dependency-free provenance record containing run time, model/version, code revision, dataset identifier and URL, licence, spatial scope, temporal resolution, missing-data policy, parameters and initial state.
- Stable JSON output with sorted parameter keys. The manifest records caller-supplied provenance; it does not authenticate the source or revision.

These classes do not yet download, parse, or calibrate external datasets. That is a deliberate separation: data ingestion and scientific transformations must be reviewed and tested before being coupled to the hydrology model.

### Candidate evidence sources for the next adapter

- **DWD Climate Data Center:** station observations for Germany, including daily precipitation and meteorological variables. Historical and recent datasets have different quality-control states; retain station metadata and flag provisional recent records. [Dataset description](https://opendata.dwd.de/climate_environment/CDC/observations_germany/climate/daily/kl/DESCRIPTION_obsgermany-climate-daily-kl_en.pdf).
- **Copernicus ERA5-Land:** global hourly land reanalysis from 1950 onward, including precipitation, evaporation, soil-water and energy variables; the catalogue lists a CC BY licence. Preserve product attribution and carefully handle accumulated-variable units and sign conventions. [Dataset catalogue](https://cds.climate.copernicus.eu/datasets/reanalysis-era5-land).
- **CHIRPS v3:** near-global gridded precipitation with a daily product and public-domain terms. It is useful for tropical rainfall comparisons, but it supplies precipitation rather than a complete canopy/root-zone evapotranspiration forcing. [Earth Engine catalogue](https://developers.google.com/earth-engine/datasets/catalog/UCSB-CHC_CHIRPS_V3_DAILY_RNL).

### Scientific safeguards before calibration

1. A station point, a roughly 9 km reanalysis grid cell and a forest plot do not represent the same spatial footprint. Record the scale and do not treat them as interchangeable.
2. Precipitation, potential evapotranspiration, actual evapotranspiration, canopy evaporation and root-zone uptake are distinct quantities. Do not copy an observed actual-ET value into a potential-ET input field.
3. Evaporation variables in reanalysis products may be accumulated and use product-specific signs. Convert only from the official product documentation and record the transformation.
4. No calibration or predictive claim is valid until the dataset, period, spatial support, missing-data treatment, parameters and independent validation metrics are documented.

