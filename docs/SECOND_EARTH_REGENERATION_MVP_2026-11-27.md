# Second Earth — Regeneration Scenario MVP

**Version:** 0.1.0  
**Date:** 27 November 2026  
**Status:** Implemented educational toy model + 8 focused tests; not empirically calibrated  
**Package:** `matrixgame_science.second_earth`

## Purpose

This module adds a small, reproducible regenerative-systems experiment to Matrixgame. It connects the Second Earth vision with the repository's existing evidence labels, bounded state models, deterministic runs, multi-objective comparison, and independent safety gate.

It is deliberately a small first step—not a complete Earth simulator, ecological forecast, climate model, wellbeing measurement, public-policy optimizer, or clinical tool.

## What is implemented

- Immutable state, action, and external-pressure records.
- Five independent normalized state dimensions: biodiversity, clean energy, wellbeing, trust, and safety.
- Deterministic discrete-time updates with explicit illustrative coefficients.
- Input validation and bounds for every normalized variable.
- A separate safety gate: high biodiversity, energy, wellbeing, or trust cannot compensate for a safety value below the selected threshold.
- Repeatable scenario runs whose history includes the initial state.
- A runnable example and eight focused regression tests.

## State and scenario contracts

All state values, action levels, and external pressures are dimensionless indices in `[0, 1]`. They are not percentages, physical units, survey scores, or measured regional values.

State dimensions:

| Dimension | Interpretation inside this toy model |
|---|---|
| `biodiversity` | Abstract ecosystem-condition index |
| `clean_energy` | Abstract clean-energy transition index |
| `wellbeing` | Abstract access-and-livability index; not a person's mental-health score |
| `trust` | Abstract social-cooperation index |
| `safety` | Abstract hazard-preparedness and protection index |

Scenario levers:

- `habitat_restoration`
- `renewable_energy`
- `health_access`
- `open_education`
- `cooperation`
- `emergency_preparedness`

External pressures:

- `ecosystem_stress`
- `transition_friction`
- `social_stress`
- `information_disorder`
- `hazard_pressure`

The coefficients are illustrative placeholders chosen to make causal links explicit and testable. They are not fitted to scientific observations. The model currently has no region, calendar date, geographic grid, species interactions, real energy balance, population dynamics, or imported dataset.

## Run locally

From the repository root:

```bash
python -m pip install -e .
python -m pytest -q
python examples/second_earth_regeneration_demo.py
```

The example runs the same initial state, action set, and pressure set deterministically. Repeating the run with identical inputs produces the same trajectory.

## Evidence and safety boundaries

- Implementation status: `IMPLEMENTED` for the Python functions and example.
- Test status: `TESTED` for the eight tests in `tests/test_second_earth.py`.
- Epistemic status: `MODEL` / `HYPOTHESIS`; coefficient choices are illustrative.
- Empirical validation: `NOT_PERFORMED`.
- No combined “paradise score” is calculated. Trade-offs remain visible.
- Safety is a separate gate and cannot be averaged away by positive values elsewhere.
- The wellbeing index is not a diagnosis, clinical measure, personal rating, or proxy for an individual's worth.
- No personal health information belongs in this public module. Personal treatment plans and health records must remain private and under the person's control.

## Responsible gamification principles

When this model is later connected to quests, rewards should support freely chosen real-world steps, learning, cooperation, rest, and help-seeking. Avoid punishment for setbacks, shame-based streaks, compulsive variable rewards, leaderboards that rank people by health, or game mechanics that replace professional care.

A simulator can support reflection and habit design; it must not prescribe medication, advise dose changes, or replace addiction treatment.

## Next milestones

**0.2 — Evidence-backed parameters:** define each index operationally, choose measurable indicators, link dated public datasets and document limitations before fitting any coefficient.

**0.3 — Sensitivity and uncertainty:** sweep parameters, publish scenario ranges, test whether conclusions survive plausible coefficient changes, and show uncertainty rather than a single forecast.

**0.4 — Spatial ecology:** add a small map grid, habitat patches, corridors, water, and simple species agents only after each module has its own tests and documented assumptions.

**0.5 — Collective agents:** introduce researcher, community, land-management, and ecosystem roles with explicit objectives, constraints, event logs, and non-compensable safety rules.

The current MVP is a stable, dependency-free reference primitive. Later modules should build around its contracts rather than silently changing its semantics.
