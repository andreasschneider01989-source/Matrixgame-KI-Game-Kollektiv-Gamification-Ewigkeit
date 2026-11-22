# Icy-ocean parameter audit and reproducibility

Status: **measured in the repository's Evolution Forge 1.0.1 implementation; external v1.1 outputs not reproduced**.

## What does S = 7 mean here?

The supplied text explicitly names `seed = 7`. A seed initializes the pseudo-random generator for a reproducible stochastic run. It is not, by itself, a physical or astrobiological "S-parameter". The provided JSON has a field `seed`, not a field named `S`.

If an independent S-parameter is intended, its definition, units, range and place in the equations are still unknown.

## Parameters we can identify

The external optimized table explicitly gives:
- `energy = 0.65` (interpreted here as the model's `energy_flux` override)
- `maintenance_cost = 0.015`
- `cooperation_benefit = 0.08`
- scenario `icy_ocean`
- seed 7 and, from the earlier comparison, 360 simulation steps

The numeric resource-regeneration change mentioned in the narrative was not supplied. Therefore `resource_regen` remains at the base `icy_ocean` value 0.018. No unreported number was guessed.

The separate Gemini JSON example names `delta_t`, `max_steps`, `thermal_conductivity`, `growth_threshold`, `dissipation_rate`, `gravitational_constant` and `satellites`. These are not fields in the current Evolution Forge `Config` schema. Passing them directly to its dataclass would be rejected as unknown arguments. In particular, a gravitational constant and satellite count belong to a gravity model unless an explicit, justified cross-domain coupling is defined; they are not used as biological controls here.

## Reproduced run in GitHub Actions

Software version: `1.0.1`  
Scenario: `icy_ocean`  
Steps: 360  
Seed: 7  
Energy flux override: 0.65  
Maintenance cost: 0.015  
Cooperation benefit: 0.08  
Resource regeneration: base scenario value 0.018

| Metric | External Gemini value | Repository run |
|---|---:|---:|
| First cell / origin event | 93 | 139 |
| Final population | 446 | 517 |
| Colony fraction | 6.85% | 60.38% |
| Differentiated Soma fraction | 1.14% | 0.39% |
| Mean cooperation | 68.4% | 65.33% |

The repository result is deterministic for the recorded seed and configuration in its CI environment. It does **not** reproduce the external output.

## Metric definitions in this implementation

- `first_cell_step`: first step at which the stochastic model creates a cell agent.
- `final_population`: number of cell agents at the final recorded step.
- `final_colony_fraction`: mean of the per-cell colony condition at the final step. In code, a cell is marked as colony-associated when local density, quorum and adhesion conditions all pass. It is not automatically comparable with another model's rate of newly formed colonies.
- `final_differentiated_fraction`: differentiated Soma count divided by the number of surviving cells in the last step.
- `final_mean_cooperation`: mean value of the cell cooperation trait among recorded surviving agents.

The external table does not include enough detail to prove that its percentages use the same definitions. Thus the colony-rate discrepancy may reflect both different dynamics and different metric definitions.

## Scientific interpretation

This is an agent-based toy model with normalized readiness proxies, simple energy bookkeeping and stochastic emergence. It does not calculate chemical reaction kinetics, ocean geophysics, hydrothermal vent chemistry, or the probability of abiogenesis on Europa or Enceladus. "Step 139" is a simulation step, not a geological time estimate.

## Next reproducibility step

Obtain the exact external v1.1 source, full configuration (including any parameter transformations), random seed, code version and metric definitions. Then run a direct A/B comparison. Until that exists, keep the Gemini values labeled as external reported results, not values validated by this repository.
