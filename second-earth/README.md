# MACHX Second Earth — Valley 001

**Status:** executable teaching/research prototype, synthetic MODEL; not a calibrated Earth forecast.

## Purpose
A small reproducible comparison of three transparent resource policies in the same toy ecosystem. It is the first simulation slice for the hybrid Second Earth vision. It deliberately runs without Minecraft, external services, LLMs, or third-party Python packages.

## Model
- One renewable biomass stock with logistic regrowth.
- A food store, food production from harvested biomass, food loss, and fixed demand.
- Optional seeded disturbance events.
- Three transparent policies: GROWTH, REGENERATION, COOPERATION.
- Paired seed comparisons: each policy sees the same set of random seeds.

The policies are rule-based baselines, **not autonomous or neural-network agents**. The numbers are synthetic assumptions for learning and software verification. The model does not represent a real valley, climate, population, economy, hydrology, or biodiversity.

## Run (Python 3.10+; standard library only)
\`\`\`bash
python second-earth/simulate.py --config second-earth/config/valley-001.json --seeds 30 --output /tmp/valley-001-summary.json
python -m unittest discover -s second-earth -p "test_*.py" -v
\`\`\`

## What to inspect
The JSON output records the config, policy parameters, seed range, model version, and per-policy mean outcomes. Metrics stay separate: final biomass, mean food coverage, shortage steps, and minimum biomass. There is intentionally no single "best civilization" score.

## Architecture boundaries
- **MACHX / Matrixgame:** simulation state and deterministic transition rules.
- **Scientific council:** currently represented by three transparent rule policies; future LLMs may propose hypotheses or policies but must not directly mutate world state.
- **LUMINI:** records the experiment manifest, evidence status, limitations, and next quest.
- **3D adapter:** not implemented in this slice. A later adapter can map approved world-state snapshots to Minecraft Java or Luanti.
- **Gaia Earth Lab:** existing water-cycle models remain the candidate hydrology module; this toy biomass/food model does not duplicate or replace them.

## Next acceptance gate
1. Python tests pass.
2. Identical config and seeds produce identical results.
3. A reviewer checks the equations and limitations.
4. Only then design the first 3D adapter.
