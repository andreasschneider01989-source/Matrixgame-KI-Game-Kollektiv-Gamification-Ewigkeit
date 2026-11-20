# MACHX Second Earth — Quest 003: Material physics and experimental scales

**Date:** 2026-11-20  
**Status:** first executable material-property model and tests added to the existing Quest 001/002 feature branch.

## User direction
Extend 1 m³ voxel blocks with material density and mass; let agents construct a scale, measure force, infer mass or gravity when the other is independently known, and propose evidence-backed improvements to simulation physics without degrading useful gameplay or research capabilities.

## Scientific clarification
Mass and weight are not interchangeable:
- mass is measured in kg and describes inertia;
- weight is gravitational force, measured in N;
- density is mass per volume, kg/m³;
- a force sensor measures force; a conventional scale converts force to a mass display using an assumed local gravity.

A single force reading cannot determine both unknown mass and unknown gravitational acceleration. With a known calibration mass, the agent can estimate local gravity; with known local gravity, it can infer an unknown mass.

## Reference model
A default block is a 1 m edge cube (1 m³). Nominal densities near 20 °C:
- Gold 19,320 kg/m³ -> 19,320 kg per block
- Silver 10,490 kg/m³ -> 10,490 kg per block
- Copper 8,960 kg/m³ -> 8,960 kg per block
- Iron 7,870 kg/m³ -> 7,870 kg per block
- Water 1,000 kg/m³ -> 1,000 kg per m³ (rounded)

At g = 9.81 m/s², gold's weight force is 189,529.2 N and silver's is 102,906.9 N. These are calculated model values, not field measurements.

The values are representative rather than exact universal constants. Purity, alloy, temperature, porosity, voids and manufacturing can change effective density. A fully solid homogeneous voxel is assumed for this first increment.

## Implementation
- `second-earth/materials/material_physics.py`: standard-library model for cube volume, mass, weight force, force-to-mass and force-to-gravity inference.
- `second-earth/materials/test_material_physics.py`: 10 unit tests including invalid inputs and identifiability distinction.
- `second-earth/materials/README.md`: units, assumptions, test command and next steps.
- No existing simulation, Java game, Gaia hydrology, astronomy or renderer code is modified.

## Evidence and governance
- Reference density table at 20 °C: https://uchi.vscht.cz/e-tables/density-solids?jazyk=en&lang=en
- Additional room-temperature metal table: https://www.guilinmetals.com/en/tools/density
- This increment is a REFERENCE_MODEL, not observed evidence. Agent-generated changes must be proposed separately with provenance, uncertainty, experiment design, test results and human approval.
- A successful unit test demonstrates internal consistency of the code, not that a physical block or sensor has been measured in the real world.

## Run tests
`python -m unittest discover -s second-earth -p "test_*.py" -v`

## Next quest
Implement a small calibration experiment: known reference mass, local force reading, sensor noise/uncertainty, repeated trials, and a decision record comparing measured gravity against the configured model. Only then consider connecting physical block values to a renderer or physics engine.
