# Second Earth Materials Lab — Quest 003

**Status:** executable reference model with unit tests. It is not yet connected to the 3D renderer, collision solver or an in-world scale.

## Core rule
A default voxel is a cube with edge length 1 m and volume 1 m³. Material density determines mass:

`mass_kg = density_kg_m3 * volume_m3`

Gravity determines weight force:

`weight_force_n = mass_kg * gravity_m_s2`

A force sensor measures newtons. A scale displaying kilograms divides measured force by a known local gravitational acceleration. Therefore, a scale cannot independently identify both unknown mass and unknown gravity from one force measurement.

## Included reference materials
- Gold: 19,320 kg/m³
- Silver: 10,490 kg/m³
- Copper: 8,960 kg/m³
- Iron: 7,870 kg/m³
- Water: 1,000 kg/m³ (rounded nominal value)

Reference values are representative near 20 °C, not universal constants. Purity, alloy composition, temperature, porosity, voids and manufacturing affect real density. A Minecraft-like solid block is assumed fully dense and homogeneous in this first model.

## Run tests
From repository root:

```bash
python -m unittest discover -s second-earth -p "test_*.py" -v
```

The package is intentionally standard-library-only.

## Important experimental distinction
- Known mass + measured force -> estimate local gravity.
- Known local gravity + measured force -> estimate mass.
- Unknown mass + unknown gravity + one force reading -> underdetermined; more independent information is required.

For a first in-world scale, build a force sensor model and calibrate it with a known reference mass. Later experiments can estimate local gravity using a known test mass at multiple locations.

## Next increment
Connect material IDs and block dimensions to a typed voxel/world-state schema. Keep the reference model separate from observed or agent-inferred properties. Never overwrite reference values with an unverified agent guess; store proposals, evidence and approval status separately.
