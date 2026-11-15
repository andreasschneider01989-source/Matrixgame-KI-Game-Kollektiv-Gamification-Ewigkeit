# Gaia Commons — Data ingestion and calibration plan

**Date:** 8 November 2026  
**Status:** Research plan; no external observations have yet been imported into the Java model.

## Purpose

Move from synthetic water-cycle examples toward reproducible, regionally grounded tests without confusing a mass-balance check with validation.

## Stage 1 — local reference: South Hesse

Use daily station precipitation from the German Weather Service Climate Data Center (DWD CDC). Start by selecting a station with a sufficiently continuous, documented time series near the chosen study area. Retain station ID, coordinates, elevation, observation period, instrument/relocation metadata, quality-control status, units and retrieval date.

Official dataset description:  
https://opendata.dwd.de/climate_environment/CDC/observations_germany/climate/daily/kl/DESCRIPTION_obsgermany-climate-daily-kl_en.pdf

The DWD description separates quality-checked historical data from recent records whose quality control may not yet be complete. Do not silently combine those statuses.

## Stage 2 — global comparison: ERA5-Land

ERA5-Land provides hourly global land reanalysis from 1950 onward. Its native grid is approximately 9 km, with four soil layers listed in the catalogue: 0–7 cm, 7–28 cm, 28–100 cm and 100–289 cm. The catalogue lists a CC BY licence.

Dataset: https://cds.climate.copernicus.eu/datasets/reanalysis-era5-land  
DOI: https://doi.org/10.24381/cds.e2161bac  
Documentation: https://confluence.ecmwf.int/pages/viewpage.action?pageId=505384848

Keep the raw data and every derived value distinct. Some evaporation variables are accumulated quantities and may use negative signs for upward fluxes. Convert to millimetres per chosen time step only after checking the variable-specific official documentation. Record the formula, sign convention and aggregation window in the run manifest.

## Stage 3 — tropical rainfall: CHIRPS v3

CHIRPS v3 combines satellite thermal-infrared estimates with station observations at 0.05-degree resolution over land between 60°S and 60°N. Its daily reanalysis product uses ERA5 daily information to disaggregate pentad totals; it should not be treated as an independent daily gauge measurement.

Dataset catalogue: https://developers.google.com/earth-engine/datasets/catalog/UCSB-CHC_CHIRPS_V3_DAILY_RNL  
The catalogue describes public-domain terms and identifies the daily variable in mm/day.

Use CHIRPS as an independent rainfall product for comparison, not as a complete source for potential canopy evaporation or root-zone evapotranspiration.

## Forcing contract

The current forest model expects one row per calendar day with:

- precipitation (mm/day);
- potential canopy evaporation (mm/day);
- potential root-zone evapotranspiration (mm/day).

These are model inputs, not three interchangeable names for observed ET. A meteorological estimate of reference evapotranspiration (ET0) would require a documented method and sufficient radiation, temperature, humidity and wind inputs. Partitioning ET0 into canopy and root-zone demands is a further model assumption and must be identified as such.

Missing observations are represented as missing, not zero. No daily run should proceed while required forcing fields are missing unless a documented gap-filling method is explicitly selected and its uncertainty is retained.

## Validation ladder

1. **Schema and units:** parse a source sample; test dates, units, signs, missing markers and bounds.
2. **Bookkeeping:** test the daily water-balance residual for every time step.
3. **Temporal sanity:** compare daily/seasonal precipitation totals and store trajectories against the data's temporal coverage.
4. **Independent validation:** compare predicted storage/fluxes with independent observations at a compatible site and spatial scale.
5. **Uncertainty:** vary uncertain parameters and report sensitivity and intervals; do not present one tuned trajectory as certainty.

## Ecological and planetary bridge

The same typed data contract can later serve Ares (Mars) or Pelagia (fiction), but each world must provide its own atmospheric composition, gravity, radiation, temperature, liquid/solid water state, soil/regolith properties and energy constraints. A Gaia parameter set must never be silently reused as a Mars or ocean-world parameter set.

The planetary interior redox conditions discussed in Walton et al. (2026) are also separate from atmospheric oxygen. That research informs a long-term habitability model, not the daily terrestrial water balance:

https://www.nature.com/articles/s41550-026-02775-z

## Decisions still open

- Which DWD station and period provide the cleanest first local series?
- Which ERA5-Land variables and sign conversions will be used?
- How will reference ET be calculated and partitioned for the two-store forest model?
- Which independent forest plot or flux-tower dataset can validate storage and evapotranspiration?
