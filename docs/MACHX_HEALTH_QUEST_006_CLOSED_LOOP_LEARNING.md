# MACHX HEALTH QUEST 006 – Closed-Loop Learning

Status: experimental, deterministic software model; not a biological or clinical model.

## Objective

Implement the smallest complete feedback-learning cycle:

`observe → predict → choose action → environment transition → feedback → update`

The toy task controls one normalized scalar state in [0, 1] toward a configurable target. Actions are -1, 0, and +1. The environment clamps the state to [0, 1] and returns a reward equal to the negative distance from the target.

A tabular controller selects actions with seeded exploration and updates action values using a temporal-difference (TD) error:

`δ = r + γ max_a Q(s', a) - Q(s, a)`

`Q(s, a) ← Q(s, a) + α δ`

The full trace preserves the selected action, observed transition, predicted next state, TD error, and updated value.

## FACT – implementation behavior

- The environment and controller are deterministic for identical configuration, seed, and inputs.
- State, target, step size, learning rate, discount, and exploration are validated.
- The environment bounds state to [0, 1].
- The controller uses a discrete state table and three actions.
- Each learning step records the transition and TD update.
- The controller's table persists across episodes on the same engine instance.

## MODEL – abstraction

The scalar state can stand for a generic controllable quantity. The target and reward define the task. The TD error is a computational learning signal, not a measured brain signal.

The implementation is deliberately small: it has no body model, muscles, sensors, neurotransmitters, continuous neural network, multi-agent interaction, or biological parameter fitting.

## HYPOTHESIS – future experiments

1. Compare learning curves for different exploration rates.
2. Add delayed or noisy observations and measure robustness.
3. Compare tabular learning with a small neural function approximator.
4. Add action costs and energy budgets.
5. Let multiple agents share evidence while keeping their policies independently inspectable.

Each comparison should use fixed seeds, a baseline policy, multiple runs, and clearly defined metrics before claiming improvement.

## Reproduction

```bash
python -m pytest -q tests/test_closed_loop_learning.py
```

## Next integration seam

The engine is intentionally independent from the previous maintenance and network-learning modules. A future adapter may expose its state, action, reward, and learning trace through the shared Engine protocol. Do not couple these modules until a concrete experiment needs that connection.
