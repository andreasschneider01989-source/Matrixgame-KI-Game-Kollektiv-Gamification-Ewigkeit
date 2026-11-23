# Matrixgame — Cross-disciplinary model architecture

**Version:** 0.1.0  
**Date:** 23 November 2026  
**Status:** Architecture plus first Python MVP

## 1. The development chain

~~~text
LANGUAGE + LOGIC + MATHEMATICS
              |
              v
           PHYSICS
              |
              v
   CHEMISTRY / BIOCHEMISTRY
              |
              v
      BIOLOGY + EVOLUTION
              |
              v
 NEUROSCIENCE + COGNITION + PSYCHOLOGY
              |
              v
 COLLECTIVE DECISIONS + GAME DESIGN
              |
              v
 MEASUREMENT -> LEARNING -> REVISION -> next model
~~~

This is a didactic dependency map: later questions can reuse concepts, constraints and methods from earlier disciplines. It is not a claim that all science is strictly one-way or reducible to one foundational equation. Feedback, emergence, environment and cross-level explanations remain possible.

## 2. Layer contracts and implementation status

| Layer | Contribution | Python MVP | Status |
|---|---|---|---|
| Language, logic, provenance | Concepts, claims, assumptions, sources and evidence labels | ModelClaim, ParameterBinding, EvidenceStatus | Data structures; no NLP compiler |
| Mathematics | Equations, probability, optimization and dimensions | Shannon entropy, softmax, SI dimension algebra | Implemented; unit tests |
| Physics | Motion, force, energy and spatial relations | Newtonian point-source acceleration, kinetic energy, kinematics | Implemented; unit tests |
| Chemistry / biochemistry | Concentration and enzyme kinetics | Michaelis–Menten rate | Implemented; unit tests |
| Biology / evolution | Population shares and differential reproduction | Discrete replicator step | Implemented; unit tests |
| Neuroscience / cognition | Simplified signal-processing abstraction | Sigmoid artificial neuron | Implemented; unit tests |
| Psychology / decisions | Utility, context and modeled choice | Softmax choice with explicit context shifts | Implemented; unit tests |
| Collective game | Multi-objective comparison | Pareto frontier | Implemented; unit tests |

## 3. Selected formula translations

- Relative position: r = position_target - position_origin.
- Newtonian point-source acceleration: a = G*M*(r_source-r_target)/|r_source-r_target|^3.
- Kinetic energy: E_k = 1/2*m*v^2.
- Constant acceleration: x_next = x + v*dt + 1/2*a*dt^2; v_next = v + a*dt.
- Shannon entropy: H = -sum(p_i*log2(p_i)).
- Michaelis–Menten: v = Vmax*[S]/(Km+[S]).
- Discrete proportional-fitness update: x_i' = x_i*f_i/sum_j(x_j*f_j).
- Artificial neuron: y = sigmoid(w dot x + b).
- Softmax decision: P(a_i) = exp(u_i/T)/sum_j exp(u_j/T).
- Pareto dominance: one option is dominated if another is at least as good on all maximized objectives and strictly better on one.

The uploaded notebook motivated several of these implementations; the larger collection contains the physics, entropy, evolution, machine-learning and vision-layer formulas. This implementation is a curated subset, not a transcription of every formula.

## 4. Corrections before reuse

1. Validate probability vectors: finite, non-negative and positive total.
2. Use numerically stable softmax by subtracting the largest score.
3. Check replicator input lengths and normalized population shares.
4. Reject coincident gravitational points instead of returning infinities.
5. Keep a consistent gravity-vector direction: source minus target.
6. Do not encode emotion-to-action effects as universal truths; require explicit scenario parameters.
7. Never map a natural-language keyword to an unexplained scalar.
8. Keep information entropy distinct from thermodynamic entropy.

## 5. Cross-layer composition

Do not pass arbitrary numbers between domains. Every interface must define variable names, units, conversion rules, assumptions, validity range, evidence status and uncertainty.

For example, an enzyme model requires substrate concentration and Michaelis constant in the same concentration unit, plus a maximum reaction rate. A gravitational acceleration in m/s² is not a concentration. A biochemical bridge needs measured variables and a scientifically specified conversion.

## 6. Personal and collective future support

The library can support optional quests based on a person's stated goals, resources, constraints and consent. It can present several reversible actions with expected benefits, costs, uncertainty and trade-offs. The person can choose, modify, pause or reject them.

No fixed universal ranking of values is imposed. Health, happiness, learning, freedom, financial security, relationships, sustainability and cooperation may conflict. Pareto analysis keeps such trade-offs visible; value weights must be explicit and user-controlled.

Public source files remain general. Do not place private health, financial, family or other personal context in repository code, public experiment logs or issue comments.

## 7. Reproducibility rules

Every new model should include:
- symbolic equation and variable meanings;
- units and dimensional checks where applicable;
- validity domain and assumptions;
- source or derivation;
- deterministic tests and boundary tests;
- fixed random seeds when stochastic behavior is added;
- run manifest with code version, inputs, seed, source data and outputs;
- distinct labels for implementation, testing and empirical validation.

Passing a unit test proves only that the implementation matches the tested specification for those cases. It does not prove that a model describes nature completely or predicts real-world outcomes.

## 8. Roadmap

**0.1 — this branch:** dependency-free package, evidence/provenance types, dimensions, selected physics/chemistry/biology/neuroscience/psychology/collective functions, tests and demo.

**0.2:** formula registry, unit-aware domain interfaces, reproducible RunManifest and stochastic experiment API.

**0.3:** restricted LUMINI-to-Model-IR translator and Python adapter that can call registered functions only.

**Later:** sourced chemistry, systems biology, ecology and neuroscience models; comparison with measured datasets; collective agent experiments; human-reviewed goal-support quests.

**Success criterion:** another contributor can understand a variable, run the tests, reproduce the demo, distinguish evidence from hypothesis, and add a formula without breaking existing behavior.

*Ideas sammeln. Modelle bauen. Experimente testen. Erkenntnisse teilen. Gemeinsam verbessern.*
