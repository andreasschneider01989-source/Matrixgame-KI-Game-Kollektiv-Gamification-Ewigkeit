# MACHX HEALTH QUEST 004 – Network Learning Primitive

Status: experimental educational model; not a biological simulator.

## Goal

Provide the smallest reproducible network primitive for later composition
with the cellular-maintenance, immune-education, and stem-cell-niche models.

## FACT

- The implementation is deterministic for identical inputs and configuration.
- Node activations and connection weights are normalized to [0, 1].
- Connections are directed.
- A node with incoming connections receives the weighted mean of source
  activations. A node without positive incoming weight retains its activation.
- Connection weights move toward the product of the source and target
  activations using a configurable learning rate.
- All updates use the previous step's node activations (synchronous update).

These are facts about this implementation, not claims about biological tissue.

## MODEL

For node j, with incoming edges i -> j:

`a_j(next) = sum(w_ij * a_i) / sum(w_ij)`

If there is no positive incoming weight, `a_j(next) = a_j`.

For each edge i -> j:

`w_ij(next) = w_ij + learning_rate * (a_i * a_j - w_ij)`

All values are dimensionless and constrained to [0, 1].

## MODEL ASSUMPTIONS

- A single scalar activation is enough to demonstrate signal propagation.
- A single scalar weight is enough to demonstrate adaptive connectivity.
- Co-activation is used as a simple learning signal.
- The model is synchronous and has no time delay, noise, topology growth,
  resource budget, or biological calibration.

## HYPOTHESIS / INTERPRETATION

A modular regenerative system may eventually be represented as interacting
subsystems whose signals and capacities influence one another. This primitive
can help test software architecture and network reasoning before biological
mechanisms are introduced.

It does not establish that thymic education, stem-cell niches, or tissue repair
follow these equations.

## OPEN QUESTIONS

1. Should later networks use multiple signal channels rather than one scalar?
2. Should edge updates include costs, decay, or evidence confidence?
3. How should modules exchange typed state without creating circular imports?
4. What validation data would be needed before making any biological claim?
5. Should network topology itself be allowed to change?

## Reproduce

From the repository root:

```bash
python -m pytest -q tests/test_network_learning.py
```

The tests cover weighted propagation, deterministic execution, bounded values,
co-activation updates, isolated nodes, and invalid topology references.

## Next small step

Compose two independent modules through an explicit adapter interface. Keep
biological labels and parameter calibration outside this generic primitive.
