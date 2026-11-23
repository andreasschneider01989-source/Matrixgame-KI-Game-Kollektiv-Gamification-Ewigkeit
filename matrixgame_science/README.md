# Matrixgame Science Library

**Version:** 0.1.0  
**Status:** Experimental MVP — model primitives, not a unified theory.

A small dependency-free Python package that turns selected Matrixgame formulae into documented, testable functions.

## Learning and modeling layers

1. **Language, logic and provenance:** preserve concepts, symbolic expressions, assumptions, sources and evidence labels. Numeric parameters must be explicit; free text is never silently converted into physical values.
2. **Mathematics:** probability, Shannon information entropy, stable softmax and SI dimensional algebra.
3. **Physics:** Newtonian point-source gravity, kinetic energy, relative position and constant-acceleration kinematics.
4. **Chemistry / biochemistry:** Michaelis–Menten enzyme kinetics.
5. **Biology / evolution:** discrete proportional-fitness replicator step.
6. **Neuroscience / cognition:** simple artificial-neuron activation.
7. **Psychology / decisions:** utility-to-probability models with explicit context shifts.
8. **Collective decisions:** Pareto-frontier analysis without arbitrary universal value weights.

This is a didactic dependency map, not a claim that science is strictly one-way or that higher-level phenomena can all be reduced to lower-level equations. Model composition needs explicit interfaces, units, assumptions and provenance.

## Run

Python 3.10+; runtime package has no third-party dependencies.

~~~bash
python -m pip install -e .
python -m pytest
python examples/cross_layer_demo.py
~~~

## Boundaries

- Shannon entropy is measured in bits here, not thermodynamic entropy in J/K.
- Newtonian gravity assumes classical mechanics and point-source fields.
- Michaelis–Menten applies to a class of enzyme-kinetics settings, not every reaction.
- Replicator dynamics is an abstract model, not a real-world population forecast.
- The sigmoid unit is an artificial neuron, not a biological neuron model.
- Softmax choices are not clinical measurements or validated psychological assessments.
- Pareto analysis exposes trade-offs; it does not decide which values should dominate.
- The package does not determine a person's will. Goals, consent, constraints and final decisions remain with the person.

Every future formula should include symbolic form, variable definitions, units, assumptions, source, tests, and evidence/implementation status. See the main architecture document.


## LUMINI formula compiler

The `lumini_compiler` package uses this library's SI dimensional algebra to compile a restricted, safe formula language into versioned Model IR. See [the compiler guide](../docs/LUMINI_FORMULA_COMPILER_MVP_2026-11-23.md). The compiler verifies syntax, symbol dependencies and dimensional consistency; it does not establish empirical validity.
