# Evolution Forge – Python environment

This folder defines the reproducible Python environment for the Evolution Forge prototype.

## Setup

```powershell
cd python/evolution_forge
poetry install
```

The first prototype is currently distributed as the separate `Evolution_Forge_1_0.zip` artifact from the development conversation. Place its `evolution_forge.py` file in this folder before running the Java bridge, or merge the simulator source into this directory in the next implementation commit.

## Run

```powershell
poetry run python .\evolution_forge.py --scenario earth_tidal_pool --steps 360 --seed 7 --out .\runs\earth
poetry run python .\evolution_forge.py --scenario alkaline_vent --steps 360 --seed 7 --out .\runs\vent
```

## Output contract

The Java bridge expects the CLI to support:

- `--config <json-file>`
- `--out <directory>`

The prototype writes `summary.json`, `history.csv` and PNG plots to the output directory.

## Model limitations

The current model is an educational toy model. Its prebiotic readiness variables are normalized proxies, not measured chemical concentrations or validated reaction kinetics. The Titan profile is not a model of methane-based biochemistry. Outputs must not be interpreted as probabilities that life will arise on any particular world.
