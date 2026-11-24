# MACHX Scientific Layer Roadmap

Status: architecture proposal and staged implementation plan (2026-11-24).

## Design principle

Build from lower-level constraints toward higher-level behavior. Each layer
must have an independently testable contract before the next layer depends on
it. Keep stable scientific primitives separate from exploratory game logic.

## Layer map

| Layer | Scope | First useful computational artifact |
|---|---|---|
| 0. Mathematics and units | vectors, dimensions, numerical integration, probability, optimization | typed quantities and tested vector operations |
| 1. Classical physics | mass, force, acceleration, momentum, energy, gravity, collisions | point-mass and multi-body environments |
| 2. Intermolecular physics | electrostatics, dispersion, repulsion, Van der Waals potentials | pair-potential module with unit tests and energy/force consistency |
| 3. Chemistry | molecular states, bonds, reaction coordinates, activation barriers, reaction rates | small reaction-network model |
| 4. Molecular biology | amino acids, peptide chains, protein structure and folding abstractions, enzymes | coarse-grained chain and energy landscape toy model |
| 5. Cell biology | membranes, transport, metabolism, maintenance, signaling | compartment and resource-flow model |
| 6. Organism mechanics | segments, joints, springs, actuators, contact and locomotion | pendulum, then articulated planar body |
| 7. Neural and agentic control | sensing, state estimation, learning, action selection, feedback | controller trained in a known physical environment |
| 8. Integrated Grayhawk | body + environment + energy + sensing + learned control | small modular virtual organism benchmark |

## Current verified implementation path

- Quest 006: scalar closed-loop temporal-difference control.
- Quest 007: ideal optical reflection oracle and a one-neuron approximation.
- Quest 008: 2D point-mass mechanics and tabular Q-learning over nine force
  directions.
- Quest 009: constant-acceleration integration validation, trajectory error
  metrics, and SI dimensional checks for acceleration, momentum, and work
  (implementation added; focused CI validation pending).

These are small software experiments. They do not yet implement molecular
forces, chemical reactions, proteins, cells, muscles, or a biological animal.

## Next implementation sequence

1. **Quest 009 — physics validation lab:** compare numerical integration against
   analytic constant-acceleration trajectories; add explicit dimensional
   checks and trajectory-error metrics. (In progress.)
2. **Quest 010 — pair interactions:** implement a carefully bounded,
   parameterized pair potential. Start with generic attractive/repulsive
   potentials; only then add a Lennard-Jones-style model as a simplified
   dispersion/repulsion approximation. Do not claim it reproduces all
   Van-der-Waals chemistry.
3. **Quest 011 — reaction coordinate:** model a one-dimensional energy
   landscape with reactant/product states and an activation barrier. Distinguish
   barrier height, temperature-dependent rate models, and arbitrary game
   parameters.
4. **Quest 012 — molecular building blocks:** introduce coarse-grained beads
   and chain connectivity before attempting amino-acid-specific protein
   folding.
5. **Quest 013 — articulated mechanics:** pendulum, joint, actuator, contact,
   then a simple multi-segment body.
6. **Quest 014 — learned locomotion:** compare random, hand-coded, and learned
   controllers on fixed and held-out scenarios.
7. **Grayhawk integration:** connect only validated modules through explicit
   state, unit, energy, and time-step contracts.

## Scientific guardrails

- **FACT:** equations and algorithms must be traceable to their stated
  assumptions and tested numerical behavior.
- **MODEL:** every simulation omits detail; document its state variables,
  units, approximations, and validity range.
- **HYPOTHESIS:** proposed links between molecular, cellular, neural, and
  organism-level behavior are experiments until independently validated.
- **OPEN:** unknown parameters and missing mechanisms stay visible.
- **FICTION/GAME:** narrative abilities and symbolic game mechanics are clearly
  labeled and never presented as scientific findings.

## Architecture rule

Physics computes the consequences of forces. Chemistry computes modeled
reaction transitions. Biology composes molecular and cellular processes.
Agent controllers choose actions using observations and learned policies.
The controller must not silently rewrite the physical laws; the environment
provides the consequences and feedback.

## Near-term acceptance criteria

- reproducible seeds and focused tests;
- explicit units or explicit dimensionless normalization;
- bounded numerical behavior and invalid-input tests;
- baseline comparisons before claims of learning improvement;
- no biological or medical claims from toy-model behavior;
- each quest remains isolated in a draft PR until reviewed and approved.
