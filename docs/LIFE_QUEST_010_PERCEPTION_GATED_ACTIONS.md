# LIFE Quest 010 — Perception-gated action selection

**Status:** Exploratory implementation on top of the Quest 009 draft branch.  
**Scientific class:** MODEL.  
**Scope:** Deterministic one-step decision rule; not a biological model or learned agent.

## Why this step

The 24 November 2026 Colab experiment defines a small `BioAgent` with:
- basal consumption: 8 abstract energy units per step;
- maintenance cost: 2 units per step;
- foraging cost: 12 units;
- gain at full resource availability: 20 units.

The notebook's two executed tests compare resting Alpha with foraging Bravo in an environment whose resource yield is zero. Resting costs 10 units per step. Blind foraging costs 22 units per step when yield is zero. The tests show that the repeated foraging strategy exhausts energy faster.

Important limitation: the shown `BioAgent.step(action, environment_yield)` receives the environment yield as an argument, but it does not itself perceive the environment or choose the action. The test script supplies the actions in advance. Therefore, it demonstrates energy accounting for two fixed action sequences, not autonomous perception or learning.

The next smallest useful question is: **when an agent receives an observation, can it avoid an action whose expected energy balance is worse than WAIT?**

## FACT — implemented in this branch

- `Observation` validates normalized resource yield and confidence in [0, 1].
- `ActionOption` describes an action cost and its gain at full resource yield.
- `choose_action` is deterministic and does not mutate agent or environment state.
- Missing or low-confidence observations result in WAIT.
- WAIT still pays the basal cost; it is not free survival.
- An action is considered only when the current energy can cover basal and action costs.
- An action is selected only if its expected energy delta is strictly better than WAIT.
- Equal candidate scores are resolved by action name.

## MODEL — equations

For resource yield r in [0, 1], action gain g at full yield, action cost c, and basal cost b:

`expected_delta(action) = r * g - c - b`

`expected_delta(WAIT) = -b`

The action is eligible only if `energy >= b + c`, and it must outperform WAIT. This is a one-step greedy policy. It does not prove that the selected action is globally optimal over multiple steps.

## Reproducible tests

Run:

`python -m pytest -q tests/test_agent_decision.py`

The focused tests cover:
1. no observation -> WAIT;
2. low confidence -> WAIT;
3. barren environment -> avoid wasteful foraging;
4. resource-rich observation -> forage can beat WAIT;
5. insufficient energy -> reject unaffordable action;
6. deterministic tie-breaking;
7. invalid observations and duplicate action names are rejected.

## LUMINI boundary

LUMINI Formula Compiler 0.1 supports scalar formula parsing, dimensional checks, and the registered `formula.scalar` engine. The arithmetic terms above can be represented as scalar formulas. The conditional policy (confidence gate, affordability gate, action ranking) is not claimed to be executable in Compiler 0.1. Do not add unsupported control-flow syntax to a `.lumini` file.

A future integration should compile scalar terms into a versioned Model IR and let a separate decision engine evaluate policy branches.

## Infinity Canvas boundary

The decision result can later be serialized as a small simulation event:

```json
{
  "schema": "matrixgame.agent-decision.v1",
  "tick": 0,
  "action": "WAIT",
  "reason_code": "WAIT_BETTER_THAN_ACTIONS",
  "expected_energy_delta": -10.0,
  "projected_energy": 20.0,
  "observation_confidence": 1.0
}
```

This is a proposed data contract only. No Canvas adapter or live synchronization is included in this quest.

## Limitations and next experiment

- Observation values are supplied by the caller; no sensor or perception pipeline is implemented.
- The environment is not dynamic in this module.
- WAIT still consumes basal energy. If the environment remains barren and no alternative resource or environmental change appears, waiting only delays depletion.
- The policy is not Q-learning, reinforcement learning, collective intelligence, or proof of consciousness.
- All energy values are abstract model units. The notebook's labels in joules are not validated SI measurements.

Next candidate: add a tiny deterministic environment that changes resource availability over time, then compare blind-forage, always-wait, and perception-gated policies over the same fixed scenario. Only after that should learning or multiple agents be introduced.

## Stable / exploratory separation

- STABLE: existing Quest 009 branch and its verified physics-validation workflow remain untouched.
- EXPLORATORY: this feature branch adds an isolated policy module, focused tests, and this document.
- No existing PR is merged, retargeted, or rewritten.
