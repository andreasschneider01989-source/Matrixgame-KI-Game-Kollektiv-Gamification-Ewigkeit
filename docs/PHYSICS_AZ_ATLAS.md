# Matrixgame — Physics A–Z Atlas

**Version:** 0.1.0  
**Status:** Educational index / learning map.  
**Source context:** A physics A–Z transcript shared in the Matrixgame discussion. This is a navigation aid, not a complete or canonical classification of physics.

## 1. Purpose

The A–Z list makes it easier to discover concepts and create cross-links between mathematics, experiments, physical models, simulations, and learning quests.

Alphabetical order is useful for browsing, but it is not a scientific hierarchy. The graph should also connect concepts by prerequisite, law, scale, mathematical method, experimental technique, and application.

## 2. Physics A–Z index

| Letter | Concept 1 | Concept 2 | Concept 3 |
|---|---|---|---|
| A | Atom | Acceleration | Angular momentum |
| B | Beta decay | Bose–Einstein condensate | Black hole |
| C | Capacitance | Conservation of energy | Coulomb's law |
| D | Dielectric | Diffraction | Doppler effect |
| E | Electromagnetism | Entropy | Electron |
| F | Fission | Fusion | Faraday's law |
| G | Gravity | Gauge theory | Gluon |
| H | Hydrodynamics | Heisenberg uncertainty | Higgs boson |
| I | Ion | Interference | Inductance |
| J | Joule | Josephson junction | Junction diode |
| K | Kinetic energy | Kirchhoff's laws | Kelvin |
| L | Laser | Lagrangian | Lorentz transformation |
| M | Magnetism | Mass–energy equivalence | Maxwell's equations |
| N | Nuclear energy | Neutrino | Newton's laws |
| O | Optics | Ohm's law | Orbital mechanics |
| P | Photon | Planck constant | Pauli exclusion principle |
| Q | Quantum mechanics | Quark | Quasiparticle |
| R | Relativity | Radioactivity | Resistance |
| S | Spacetime | Superconductivity | Schrödinger equation |
| T | Transistor | Thermodynamics | Tunneling |
| U | Ultrasonics | Ultraviolet radiation | Unified field theory |
| V | Vibration | Vector | Viscosity |
| W | Wave propagation | Wavelength | Work |
| X | X-ray | X-ray diffraction | Xenon |
| Y | Yoctosecond | Young's modulus | Yukawa potential |
| Z | Zero-point energy | Zeeman effect | Zener diode |

## 3. Scientific precision notes

- **Electrons and atoms:** electrons are not tiny planets moving on classical fixed tracks. Atomic structure is described using quantum states and orbitals.
- **Entropy:** "disorder" is an informal metaphor, not a definition that works in every context. For a microcanonical ensemble, the Boltzmann relation is S = k_B ln(Ω), with Ω defined for the relevant accessible microstates.
- **Entanglement:** it creates quantum correlations with no classical counterpart, but it does not provide controllable faster-than-light communication.
- **Higgs mechanism and mass:** the Higgs field contributes to the masses of elementary particles such as quarks, charged leptons, and W/Z bosons. Most of a proton's mass is associated with QCD energy and the dynamics of the strong interaction, not simply the Higgs mechanism.
- **Black-hole singularities:** singularities occur in classical general-relativistic descriptions under specified conditions. A complete quantum-gravity account remains open.
- **Uncertainty principle:** it follows from the structure of quantum states and non-commuting observables; it is not merely a limitation of imperfect measuring instruments.
- **Waves and particles:** saying "everything is just a wave" or "particles do not exist" is an oversimplification. Quantum theory uses a richer mathematical description.
- **Gravity:** Newton's law is an excellent approximation in many regimes; general relativity describes gravity geometrically through spacetime curvature.
- **Bose–Einstein condensate:** this is a collective quantum state with macroscopic coherence, not a claim that many atoms literally become one atom.
- **Sound spreading:** for an ideal point source in a free field, spherical spreading gives intensity proportional to 1/r² and amplitude proportional to 1/r. Boundaries, absorption, and near-field effects change the situation.
- **Temperature:** the connection between temperature and mean kinetic energy is simple for idealized systems such as a classical ideal gas; it is not a universal definition for every system.
- **Neutrinos:** neutrino oscillations show that neutrinos have non-zero masses. The minimal Standard Model does not fully account for this.
- **Zero-point energy:** vacuum and Casimir-effect physics are subtle. These phenomena do not establish a practical, unlimited vacuum-energy power source.

## 4. Six cross-cutting learning pathways

These pathways link A–Z entries into a progression. The order is a teaching proposal, not the only valid route.

### A — Motion, forces, and space
Start with vectors, acceleration, Newton's laws, momentum, angular momentum, energy, gravity, and orbital mechanics. Then compare Newtonian mechanics with special and general relativity.

**Small artifact:** a 2D two-body orbit simulator with unit checks, energy tracking, and a documented approximation range.

### B — Electricity, magnetism, and light
Start with charge, Coulomb's law, current, resistance, Ohm's law, capacitance, and inductance. Continue with Faraday's law, Maxwell's equations, electromagnetic waves, photons, optics, and lasers.

**Small artifact:** a circuit or electromagnetic-field learning model with clearly stated simplifications.

### C — Energy, heat, and statistical physics
Connect work, energy conservation, temperature, Kelvin, Joule, thermodynamics, viscosity, hydrodynamics, and entropy.

**Small artifact:** compare a simple ideal-gas model with a heat-transfer or diffusion example; explain what each model leaves out.

### D — Quantum foundations
Build from atoms, electrons, photons, wave behavior, interference, the Planck constant, the uncertainty principle, Schrödinger's equation, Pauli exclusion, tunneling, and Bose–Einstein condensation.

**Small artifact:** a one-dimensional potential-well visualization. Label what is probability amplitude or probability density, not a classical trajectory.

### E — Matter and the particle world
Connect ions, nuclei, radioactivity, beta decay, fission, fusion, neutrinos, quarks, gluons, gauge theory, and the Higgs boson.

**Small artifact:** a particle interaction map that distinguishes Standard Model interactions from unresolved questions.

### F — Relativity and the cosmos
Connect spacetime, Lorentz transformations, mass–energy equivalence, gravity, black holes, gravitational waves, and open questions in cosmology and quantum gravity.

**Small artifact:** compare a Newtonian approximation with a relativistic prediction in a clearly defined regime.

## 5. Knowledge graph structure

Suggested node types:

- CONCEPT — a term or physical idea.
- LAW — a law with scope, assumptions, and limitations.
- EQUATION — a mathematical expression with variables and units.
- EXPERIMENT — an observation or measurement method.
- MODEL — a representation with explicit assumptions.
- SIMULATION — an executable or reproducible model configuration.
- OPEN_QUESTION — an unresolved research question.
- LEARNING_QUEST — a practice task with acceptance criteria.

Useful links include REQUIRES, DERIVES_FROM, SUPPORTS, CHALLENGES, MODELS, TESTS, IMPLEMENTS, and TEACHES. See [Knowledge Node and Evidence Schema](KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md).

## 6. First playable atlas quest

**Quest:** Density → Mass → Volume → Units → Gravity

1. Explain density in one sentence.
2. Calculate density from one mass and volume example.
3. Verify that the units reduce to kg/m³.
4. Connect density to mass distribution in a simple gravity model.
5. Change one parameter and predict the result before running the simulation.
6. Record what the model assumes and where it is inaccurate.

**Acceptance criteria:** calculation, units, assumptions, and simulation result are recorded; another learner can reproduce the example.

## 7. Source and verification policy

The A–Z names are an educational index from material discussed in this project. Each detailed concept page should add a suitable primary, textbook, institutional, or peer-reviewed source before it is marked SOURCE_LINKED or stronger.

Use current scientific sources for changing or disputed claims. Preserve publication dates and distinguish the source's claim from the project's interpretation.

Suggested foundations:
- [BIPM — SI defining constants](https://www.bipm.org/en/measurement-units/si-defining-constants)
- [BIPM — SI base units](https://www.bipm.org/en/measurement-units/si-base-units)
- [CERN — The Higgs boson](https://home.cern/science/physics/higgs-boson)
- [OpenStax — University Physics](https://openstax.org/subjects/science)

## Further research connection — atomic interaction and shell exchange

The atlas also links to an exploratory Matrixgame research module on the proton–electron mass ratio, closed-shell analogies, Coulomb interaction, quantum exchange, and a 2D core–satellite simulation:

- [Proton–electron mass ratio and 2D shell-exchange research](RESEARCH_PROTON_ELECTRON_RATIO_AND_2D_SHELL_EXCHANGE.md)

The arithmetic 8–27–51 pattern is recorded as a checked integer identity; its physical interpretation remains OPEN. The satellite-exchange mechanism is a HYPOTHESIS / MODEL, not established chemistry.

**Next step:** turn the density pathway into the first tested learning node, then add other concepts only as their sources, units, equations, and acceptance criteria are checked.
