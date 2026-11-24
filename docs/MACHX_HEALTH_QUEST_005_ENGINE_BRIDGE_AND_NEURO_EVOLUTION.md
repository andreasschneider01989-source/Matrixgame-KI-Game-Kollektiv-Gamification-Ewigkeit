# MACHX HEALTH QUEST 005 – Engine Bridge and Neuro-Evolution Research Map

Status: experimental software architecture; not a validated biological model.

## Objective

Connect two independent engines through one typed interface:
1. CellularMaintenanceEngine: produces a maintenance result from CellState.
2. MaintenanceToNetworkAdapter: maps that result into a NetworkLearningEngine graph.

The generic Engine protocol and EnginePipeline preserve each stage's input/output
types and return both the intermediate and final result. The bridge is explicit:
it maps energy reserve, damage load, protein load, and repair capacity into
normalized network-node activations.

## FACT – implementation behavior

- Each stage implements a one-step `step(value)` contract.
- The pipeline runs stage one, then passes its output to stage two.
- Both stage outputs are retained in PipelineResult.
- The network topology is configurable.
- The pipeline is deterministic when its stages and inputs are deterministic.

These are facts about the code, not claims about living cells or brains.

## MODEL – abstraction

The bridge treats selected maintenance-state variables as network activations.
The network then computes weighted signal propagation and connection updates.
This is a software composition experiment: it demonstrates interoperability
between modules with different internal data structures.

## NEUROSCIENCE RESEARCH MAP

### FACT – evidence from published research

- Neuromodulators such as dopamine, acetylcholine, noradrenaline and serotonin
  influence neural activity and plasticity in context-dependent ways. They do
  not act as one universal scalar reward or a simple direct equivalent of an
  artificial-network weight.
- Motor learning and control involve distributed networks, including motor
  cortex, basal ganglia, cerebellum, thalamus and brainstem.
- Computational accounts of motor control discuss prediction, state
  estimation, feedback, costs and reward as useful abstractions.
- Biological synapses are diverse and dynamic; recent work has explored
  transferring selected forms of synaptic diversity into artificial networks.
  Such transfers are research hypotheses and do not guarantee better AI.

### MODEL – candidate computational analogies

| Biological concept | Possible software abstraction | Boundary |
|---|---|---|
| Synaptic efficacy | Edge weight | Not a one-to-one equivalence |
| Neuromodulation | Context-dependent learning/gain signal | Not a neurotransmitter simulation |
| Dopamine-related prediction error | Outcome prediction-error signal | Dopamine has multiple context-dependent roles |
| Acetylcholine-related attention | Precision or input-selection gate | Simplified analogy |
| Noradrenaline-related arousal | Network gain / exploration parameter | Not a direct physiological mapping |
| Serotonergic modulation | State-dependent policy or plasticity control | Highly context-dependent |
| Cerebellar predictive control | Forward model and error correction | Functional abstraction |
| Basal ganglia action selection | Candidate-action selection / value learning | Distributed circuit, not a single selector |
| Motor cortex and spinal pathways | Command generation and actuator interface | Requires dynamics and sensory feedback |

### HYPOTHESIS – useful cross-domain research questions

1. Can heterogeneous learning rules outperform a network where every connection
   is updated identically?
2. Can separate prediction, selection and feedback modules improve stability?
3. Can context-dependent learning rates reduce interference between tasks?
4. Can energy and resource constraints improve robustness and efficiency?
5. Can topology adaptation be made interpretable and reproducible?

These are experiments to design, not established outcomes.

## OPEN QUESTIONS

- Which biological process is the target of each future model, and at what scale?
- Which signals are measured, inferred, or merely illustrative?
- How do we prevent a high-level analogy from being mistaken for a mechanism?
- What benchmark would demonstrate improvement over a simpler baseline?
- How should uncertainty, energy cost, safety constraints and provenance travel
  through the common engine interface?

## Reproduction

```bash
python -m pytest -q tests/test_engine_contracts.py
```

The tests cover generic composition, the maintenance-to-network bridge,
determinism, explicit type checks, configurable learning, and invalid topology.

## Suggested next step

Add a small closed-loop control experiment: a controller proposes an action,
a toy environment returns an outcome, and a prediction-error module updates
the next action. Keep the neuroscience analogy documented separately from
the tested software behavior.
