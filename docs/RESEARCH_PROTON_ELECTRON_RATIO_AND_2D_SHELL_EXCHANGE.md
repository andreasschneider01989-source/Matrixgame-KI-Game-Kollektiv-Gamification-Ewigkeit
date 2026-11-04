# Matrixgame Research Note — Proton–Electron Mass Ratio and 2D Core–Satellite Exchange

**Version:** 0.1.0  
**Date:** 2026-11-04  
**Research status:** HYPOTHESIS / MODEL DESIGN  
**Software status:** PLANNED — no new simulator implementation is claimed here.

## Executive summary

This note combines the current discussion with earlier Matrixgame research on the proton–electron mass ratio, sixfold scaling, the 8–27–51 decomposition, Lennard–Jones-style short-range repulsion, and fast Newtonian simulations.

The new proposal is to model two approaching systems, each consisting of one core and two orbiting satellites. Under selected conditions, the satellites may exchange partners or orbital association. The hypothesis is that this interaction could transfer momentum and produce a measurable outward motion of the two cores.

The effect is not assumed to be real. It must be compared with separate gravity, electrostatic, and phenomenological-potential baselines, while tracking energy, momentum, angular momentum, separation, numerical error, and computing speed.

**Central research principle:** a visually convincing orbit is not yet a physical result. A useful model must make predictions before a run, preserve its declared invariants, and outperform a baseline on independent measurements.

## 1. Reconstructed research question

Two similar systems approach each other in a plane. Each contains:

- one central mass or core;
- two satellites orbiting that core;
- defined masses and, in optional variants, electric charges and spin labels;
- orbital radii, phases, velocities, and a declared occupancy state.

When the centres approach sufficiently closely, their satellite systems may interact or exchange satellites. Under selected conditions—such as a filled two-satellite state and sufficient orbital speed—this interaction or redistribution may transfer momentum and generate an effective outward motion of the two centres.

The new hypothesis is:

**H-EXCHANGE:** for a defined range of separation, orbital phase, speed, and occupancy state, a satellite-exchange mechanism produces a repeatable increase in the relative separation of the two centres compared with a specified baseline.

The more ambitious extension is:

**H-VELOCITY:** a physically consistent interaction model depending on relative velocity as well as position may predict trajectories more accurately than a position-only baseline.

Neither proposal should be called a new law of gravity before it has passed tests and independent comparisons.

## 2. The proton–electron ratio and the 8–27–51 pattern

### 2.1 Measured reference value

NIST lists the 2022 CODATA recommended proton-to-electron mass ratio as:

\[
r = \frac{m_p}{m_e} = 1836.152673426(32).
\]

Source: [NIST CODATA — proton-electron mass ratio](https://physics.nist.gov/cgi-bin/cuu/Value?mpsme%7Csearch_for=mass+of+electron).

The ratio is not itself an integer. Its rounded-down integer part is:

\[
\lfloor r\rfloor=1836.
\]

### 2.2 Sixfold scaled integer

The earlier Matrixgame mass-unit idea uses one sixth of the electron mass as a selected arithmetic unit:

\[
N=\left\lfloor \frac{m_p}{m_e/6}\right\rfloor
=\lfloor 6r\rfloor.
\]

With the CODATA value:

\[
6r=11016.916040556,
\qquad N=11016.
\]

The factorization is exact:

\[
11016=8\times27\times51
\]

\[
8=2^3,\qquad27=3^3,\qquad216=8\times27=6^3
\]

\[
11016=216\times51=6^3\times51.
\]

And:

\[
51=3\times17,\qquad
11016=2^3\times3^4\times17.
\]

This confirms the arithmetic pattern from the earlier Matrixgame notes.

### 2.3 What this establishes—and what it does not

**FACT:** given the selected scaling by six and the floor operation, the integer 11016 has the exact factorization above.

**OPEN QUESTION:** does this factorization correspond to a physically meaningful structure, or is it a numerical pattern arising from the chosen scaling and rounding rule?

The exact measured value gives 6r = 11016.916..., not exactly 11016. The integer is obtained only after taking the floor. Rounding to the nearest integer would give 11017.

A physical interpretation therefore needs a prediction independent of the factorization itself. It must predict a further measurable quantity or relationship that was not used to select the scaling factor.

### 2.4 Test for numerical-pattern significance

Before exploring many possible factors, define the search procedure in advance:

1. State why the scale factor 6 was selected, using a rationale independent of the desired result.
2. State why the floor operation is appropriate.
3. Define the permitted set of alternative scale factors and rounding rules.
4. Compare the observed factorization with a declared null model.
5. Correct for the fact that many numerical patterns can be found after searching many candidate factors.
6. Identify a new prediction that can be checked against independent data.

Do not interpret the pattern as a measured substructure of the proton or electron without an independent physical observable.

## 3. What established atomic physics contributes

### 3.1 Coulomb interaction

Charged particles interact electrostatically. The Coulomb potential is proportional to q1 q2 / r, and the force magnitude is proportional to |q1 q2| / r². Opposite charges attract; equal signs repel.

Reference: [IUPAC Gold Book — Coulomb repulsion](https://goldbook.iupac.org/terms/view/CT07013).

### 3.2 Exchange and the Pauli principle

Electrons are indistinguishable fermions. Their joint quantum state obeys fermionic exchange symmetry, and the Pauli exclusion principle constrains allowed electronic states. In molecular systems, orbital overlap, spin, exchange terms, and electrostatic interactions all contribute to the energy.

IUPAC defines exchange repulsion as a correction to Coulomb repulsion between electrons in orbitals with parallel spins: [IUPAC Gold Book — exchange repulsion](https://goldbook.iupac.org/terms/view/ET07034).

This is not the same as two classical objects visibly swapping identities or orbit tracks. Exchanging labels of identical electrons does not create a distinguishable before-and-after identity for each electron.

### 3.3 Octet rule and a two-satellite model

The octet rule is a useful chemical heuristic for many main-group compounds. It concerns eight valence electrons in relevant configurations, but it is not a universal force law and has important exceptions. Hydrogen and helium have a two-electron duet configuration instead.

Reference: [OpenStax Chemistry 2e — Lewis structures and octet rule](https://openstax.org/books/chemistry-2e/pages/7-3-lewis-symbols-and-structures).

A core with two satellites is therefore not automatically an octet-rule atom. A useful first chemistry analogue is a helium-like closed-shell model: one nucleus with charge +2e and two electrons with total charge −2e, with opposite spin labels in the ground-state 1s configuration.

Even this is only an analogy for a classical orbit model. The electrons in helium are described by quantum states, not stable classical planetary tracks.

### 3.4 Physical comparison: the helium dimer

Two helium atoms form a useful closed-shell comparison. The He–He interaction has a weak attractive van der Waals region and a steep repulsive short-range region. High-level theoretical and experimental potential curves are available.

Useful references:

- Tang, Toennies, and Yiu, *Accurate Analytical He-He van der Waals Potential Based on Perturbation Theory*, Physical Review Letters 74, 1546 (1995), [DOI: 10.1103/PhysRevLett.74.1546](https://doi.org/10.1103/PhysRevLett.74.1546).
- [Ab initio values of the thermophysical properties of helium](https://pmc.ncbi.nlm.nih.gov/articles/PMC4872693/).

This provides a possible later benchmark for an effective two-centre interaction curve. It does not mean a classical satellite simulator can reproduce helium quantum mechanics without a quantum model.

### 3.5 Gravity is not the atomic repulsion mechanism

Newtonian gravity between positive masses is attractive. In the electron–proton example, the electrostatic force is about 2.27 × 10^39 times stronger than gravity at the same separation.

Reference: [OpenStax — Coulomb's law and comparison with gravity](https://openstax.org/books/college-physics-ap-courses/pages/18-4-coulombs-law).

A gravity-only model is useful for studying trajectories, but it does not explain atomic electron repulsion. If a simulation produces outward motion, the source must be visible in its declared interaction terms or event rules.

## 4. Connection to earlier Matrixgame models

Earlier Matrixgame notes already explored a Lennard–Jones-inspired pair potential and a short-range repulsive term with a high inverse power of distance. This is useful as a comparison model, but its interpretation needs precision.

The Lennard–Jones 12–6 potential is:

\[
U_{\mathrm{LJ}}(r)
=
4\epsilon
\left[
\left(\frac{\sigma}{r}\right)^{12}
-
\left(\frac{\sigma}{r}\right)^6
\right].
\]

The r^-12 and r^-6 terms are terms in the **potential energy**, not directly the force law. The force is obtained from:

\[
\mathbf F(r)=-\nabla U(r).
\]

The potential is a phenomenological model often used to approximate short-range repulsion and longer-range dispersion attraction between neutral particles. Its r^-12 term is not an exact derivation of the Pauli principle.

Retain the earlier Matrixgame concept as a comparison model:

- keep the Lennard–Jones potential as a phenomenological baseline;
- add explicit Coulomb interactions as a separate model;
- keep any new satellite-exchange rule independently switchable;
- compare each model with a relevant physical reference.

This prevents one effect from being counted twice or attributed to gravity when it was actually produced by an added repulsive potential.

## 5. Proposed 2D core–satellite experiment

### 5.1 Declare the geometry

For the first experiment, use a **planar restriction of a three-dimensional inverse-square interaction**. Positions and velocities lie in the x-y plane, but the force law retains a three-dimensional 1/r² magnitude.

This is not the same as a universe whose fundamental space is two-dimensional. A genuinely two-dimensional field theory can have a different distance dependence. The simulation must declare which interpretation it uses.

### 5.2 Initial system

Use six simulated bodies:

- core A;
- satellites A1 and A2;
- core B;
- satellites B1 and B2.

Each body has a mass. Charged variants additionally have a charge. Quantum-inspired variants may carry spin labels, but a classical spin flag is bookkeeping unless the interaction model defines what it means.

Record for every initial state:

- core position and velocity;
- satellite positions and velocities;
- orbital radius and phase;
- core and satellite masses;
- optional charges and spin labels;
- occupancy label;
- centre-to-centre distance and relative velocity.

The two-satellite occupied state is a model state. It is not assumed to be identical to the chemical octet rule.

### 5.3 Separate model variants

Run these variants independently before combining them:

| ID | Model | Purpose |
|---|---|---|
| M0 | Newtonian gravity only | Attractive baseline and orbit stability. |
| M1 | Gravity plus Coulomb interactions | Tests the role of charge and polarity. |
| M2 | M1 plus an effective short-range potential | Tests whether a repulsive energy wall produces separation. |
| M3 | M1 plus a satellite ownership/partner exchange rule | Tests the new exchange hypothesis without silently changing the force law. |
| M4 | M1 + M2 + M3 | Tests interactions among mechanisms after separate evaluation. |
| M5 | Reference quantum-chemistry interaction curve | Benchmark for a selected real system, such as He₂. |

Do not start with M4. First understand each term's contribution in isolation.

### 5.4 Define the exchange event precisely

A proposed event can be triggered by a conjunction of conditions:

- centre-to-centre distance below a declared threshold;
- a selected satellite separation or orbital-overlap condition;
- a selected occupancy state;
- an optional relative-speed range;
- a defined approach direction and orbital phase.

The event must be recorded. Particles must not be teleported or have their velocities silently reset.

For identical electrons, ownership exchange is a classical bookkeeping rule, not a literal physical identity swap. If the model adds an impulse or transfers momentum, it must record that transfer and apply equal-and-opposite momentum changes. Energy must also be accounted for; conserving momentum alone does not guarantee conservation of energy.

A smoother alternative is to let all bodies interact through declared pair potentials and use an exchange event only to record a change in closest-core or orbital association. This avoids introducing a discontinuous force merely because a label changed.

### 5.5 Newtonian gravity and velocity coordinates

The standard Newtonian gravitational force between two point masses is:

\[
\mathbf F_{ij}^{G}
=
Gm_im_j
\frac{\mathbf r_j-\mathbf r_i}
{|\mathbf r_j-\mathbf r_i|^3}.
\]

It does not depend directly on velocity. Velocity coordinates are required for predicting motion, but they do not automatically make gravity velocity-dependent.

Velocity-dependent interactions do occur in other contexts, such as magnetic forces and relativistic corrections. A Matrixgame extension that depends on relative velocity should therefore be named a **candidate velocity-coupled interaction model**, not silently called Newtonian gravity.

Any candidate term must:

1. have correct physical dimensions;
2. define its reference frame and symmetries;
3. specify how pair momentum is conserved;
4. state whether energy is conserved and under what conditions;
5. give a parameter-free or independently calibrated prediction;
6. be compared with the Newtonian baseline and real data.

A freely tuned velocity term can create interesting trajectories. That alone does not establish a better physical model.

## 6. Performance and numerical design

### 6.1 Separate simulation time from rendering time

The simulation should execute many physics steps for each rendered frame. Rendering must not determine the numerical integration step.

For six bodies, all-pairs interaction calculation is small. For larger systems, pairwise evaluation scales as O(N²); spatial partitioning or Barnes–Hut-style approximations may become useful. A complex tree structure is unlikely to be the first performance bottleneck for a six-body model.

### 6.2 Integrator and convergence

For position-dependent conservative forces, velocity-Verlet or leapfrog integration is a useful baseline. If velocity-dependent terms are introduced, select and validate an integrator appropriate to the resulting equations.

Use time-step convergence tests: rerun the same initial state with progressively smaller steps and check whether the claimed effect and measured quantities converge.

Close encounters may require adaptive time steps or documented regularization. A numerical softening parameter is a computational device, not a newly discovered physical repulsion.

### 6.3 Per-step and per-run measurements

| Metric | Purpose |
|---|---|
| Simulation time and step index | Reproducibility and temporal analysis. |
| Wall-clock time | Actual computing cost. |
| Steps per second | Throughput. |
| Centre-to-centre distance | Main separation outcome. |
| Relative centre velocity | Approach or recession. |
| Minimum particle separation | Close-encounter diagnostics. |
| Kinetic and potential energy | Energy accounting. |
| Total momentum | Momentum conservation. |
| Total angular momentum | Rotational symmetry and numerical drift. |
| Exchange-event count and timestamps | Tests the exchange mechanism. |
| Event impulse / transferred momentum | Makes the proposed mechanism measurable. |
| Energy, momentum, and angular-momentum drift | Detects numerical artifacts. |

Export time series to CSV and plot centre separation, radial velocity, energy drift, and event timing. Store initial conditions, random seed, model toggles, parameter set, code version, and time step with every run.

### 6.4 Reproducibility and speed

For every experiment:

- use fixed seeds for stochastic variants;
- separate compute, logging, and rendering timings;
- warm up the JVM before measuring steady-state throughput;
- repeat runs and report median and spread;
- profile before optimizing;
- keep a baseline with no exchange rule;
- test numerical convergence independently of raw speed.

The objective is not maximum animation speed alone. It is the fastest computation that preserves the accuracy needed for the experiment.

## 7. Falsifiable predictions and success criteria

### H-EXCHANGE

**Prediction:** for a defined and pre-registered region of parameter space, M3 produces a repeatable outward change in centre separation that is not present in M0 or adequately explained by M1 and M2.

Tests:

1. Sweep initial centre distance.
2. Sweep orbital phase and orientation.
3. Sweep relative approach speed and satellite orbital speed.
4. Compare filled, partially filled, and control occupancy states.
5. Repeat across fixed seeds and independent initial configurations.
6. Compare separation response and transferred momentum.
7. Check momentum and energy accounting.
8. Halve the time step and verify convergence.

The hypothesis is weakened or falsified if the effect disappears under time-step refinement, depends on a narrow hand-picked initial phase, arises from non-conserved event code, or is no better than established interaction models.

### H-MASS-PATTERN

**Prediction required:** the 8–27–51 factorization must imply an independently measurable property beyond the integer factorization itself.

Define the proposed physical observable before searching for further numerical patterns. If no such prediction can be derived, retain the result as an arithmetic observation and learning quest, not a physical law.

### H-VELOCITY

**Prediction:** a velocity-coupled model improves an independently measured trajectory or interaction prediction beyond M0–M2.

Success requires a declared observable, a calibration set, an independent hold-out set, a fair error metric, and conservation/symmetry checks. Improvement only on the data used to tune the model does not count.

## 8. Knowledge graph integration

Suggested nodes:

- physics.mass-ratio.proton-electron — measured constant and provenance.
- model.matrixgame.mass-ratio-sixfold-floor — selected scaling and rounding operation.
- model.matrixgame.factorization-8-27-51 — exact integer identity with an open physical interpretation.
- model.matrixgame.core-satellite-2d — abstract planar model.
- physics.atomic.coulomb-interaction — established interaction.
- physics.atomic.exchange-repulsion — quantum-mechanical concept and references.
- model.interaction.lennard-jones-12-6 — phenomenological potential.
- model.matrixgame.satellite-exchange-event — proposed game/simulation rule.
- experiment.matrixgame.exchange-ablation — comparison between model variants.
- quest.matrixgame.2d-shell-exchange — gamified research sequence.

Suggested relationships:

- the mass-ratio record SUPPORTS the numeric factorization record only as an arithmetic input;
- the factorization record MOTIVATES a testable hypothesis, but does not PROVE it;
- Coulomb and quantum-exchange nodes INFORM the physical comparison model;
- the Lennard–Jones node MODELS a phenomenological pair potential;
- the experiment TESTS the satellite-exchange hypothesis;
- results CHALLENGE, SUPPORT, or FAIL_TO_SUPPORT the candidate model, with evidence and version attached.

Connect these records to the Universal Knowledge Engine and the Physics A–Z Atlas.

## 9. Research quest — Two Cores, Four Satellites

**Quest type:** scientific model-building  
**Status:** PLANNED  
**Primary goal:** determine whether the proposed exchange rule produces a robust, measurable effect.

### Stage A — Arithmetic audit
- Reproduce the NIST ratio calculation.
- Reproduce the floor and factorization.
- Test alternative scale factors under a declared null model.
- State what observation would give the number pattern physical meaning.

### Stage B — Stable orbital baseline
- Create the two-core/four-satellite 2D model.
- Start with Newtonian gravity only.
- Validate circular-orbit initial conditions where applicable.
- Measure energy, momentum, angular momentum, and centre separation.

### Stage C — Interaction comparisons
- Add Coulomb interactions as a separate switch.
- Add the Lennard–Jones 12–6 potential as a phenomenological model.
- Compare outcomes before creating an exchange rule.

### Stage D — Exchange hypothesis
- Add a logged, deterministic ownership/partner exchange condition.
- Do not teleport particles.
- Track impulses and conservation residuals.
- Sweep orbital phases, speeds, and approach distances.

### Stage E — Scientific validation
- Compare against independent reference data, beginning with a suitable quantum-chemistry potential curve if the chosen model is helium-like.
- Use held-out conditions and report uncertainty.
- Publish failed tests and parameter ranges where the hypothesis does not work.

### Stage F — Gamification and sharing
- Turn each stage into a reproducible learning artifact.
- Reward measurements, good criticism, and documented corrections—not just dramatic visuals.
- Link Java code, CSV output, plots, and knowledge nodes.

## 10. First implementation architecture

A minimal Java package proposal:

- physics2d.Particle2D — mass, charge, position, velocity, optional spin label.
- physics2d.CoreSatelliteSystem — core and satellite references plus occupancy metadata.
- physics2d.ForceModel2D — common force-model interface.
- physics2d.NewtonianGravity2D — inverse-square gravity.
- physics2d.CoulombInteraction2D — electrostatic pair force.
- physics2d.LennardJonesPotential2D — phenomenological potential and force.
- physics2d.ExchangeRule — optional event rule and event log.
- physics2d.Integrator2D — validated integration method.
- experiment.ExperimentConfig — initial conditions, seed, model toggles, time step, run duration.
- experiment.Diagnostics — conservation and trajectory metrics.
- experiment.CsvLogger — raw time-series output.
- experiment.BenchmarkRunner — parameter sweeps and throughput measurements.

Keep force calculation, integration, event rules, metrics, and visualization separate. The first prototype should be a console simulation with CSV output; the visualizer can consume the logged trajectory later.

## 11. Source references

1. NIST CODATA 2022 — proton-electron mass ratio: https://physics.nist.gov/cgi-bin/cuu/Value?mpsme%7Csearch_for=mass+of+electron
2. IUPAC Gold Book — exchange repulsion: https://goldbook.iupac.org/terms/view/ET07034
3. IUPAC Gold Book — Coulomb repulsion: https://goldbook.iupac.org/terms/view/CT07013
4. OpenStax Chemistry 2e — Lewis structures and octet rule: https://openstax.org/books/chemistry-2e/pages/7-3-lewis-symbols-and-structures
5. OpenStax College Physics — comparison of Coulomb and gravitational forces: https://openstax.org/books/college-physics-ap-courses/pages/18-4-coulombs-law
6. Tang, Toennies, Yiu (1995) — helium dimer van der Waals potential: https://doi.org/10.1103/PhysRevLett.74.1546
7. Ab initio values of the thermophysical properties of helium: https://pmc.ncbi.nlm.nih.gov/articles/PMC4872693/

## 12. Final research rule

The project may explore freely in a sandbox, but it must label the result correctly:

- FACT — a sourced measurement or verified arithmetic identity.
- MODEL — an explicitly defined simulation rule.
- HYPOTHESIS — a proposed physical explanation or predictive extension.
- OPEN — a question still requiring evidence.
- FICTION / GAME RULE — an intentionally invented mechanic.

The ambition is to make the simulation faster and more predictive, not merely more elaborate. The strongest Matrixgame result is a simple model that predicts an independent measurement, explains its uncertainty, survives attempts to falsify it, and can be reproduced by another researcher.

**Let's play. Let's be.**
