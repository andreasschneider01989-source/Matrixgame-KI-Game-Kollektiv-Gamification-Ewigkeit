# MACHX HEALTH QUEST 007 — Physics-Guided Learning

## Goal

Connect an explicit mathematical law of nature to a trainable artificial neuron.
The first experiment uses ideal mirror reflection because its physical rule is
simple, exact, and independently testable.

## FACT — physical rule

Angles of incidence and reflection are measured from the surface normal.
For ideal specular reflection:

    theta_reflected = theta_incident

For a direction vector d and unit surface normal n, vector reflection is:

    r = d - 2 (d · n) n

The implementation validates finite inputs and preserves vector magnitude.

## MODEL — implemented

- reflect_angle_deg is the deterministic physics oracle.
- reflect_vector implements the vector reflection equation in 2D.
- LinearAngleNeuron is a single trainable linear neuron:
  prediction = weight * normalized_input + bias.
- Training uses deterministic online gradient descent against example pairs
  generated from the reflection law.
- NeuronTrainingResult records initial/final mean absolute error, epochs,
  weight, and bias.

This is a minimal neuron demonstration, not a deep neural network, optical
ray-tracing engine, or biological neuron model. The exact physical oracle and
the learned approximation remain separate so their errors can be measured.

## Reproduction

    python -m pytest -q tests/test_physics_guided_learning.py

## Evaluation

The focused tests verify:
- equality of incident/reflected angles;
- vector reflection and magnitude preservation;
- invalid-input rejection;
- deterministic training;
- error reduction against the exact law.

A lower training error demonstrates approximation of this deliberately simple
mapping only. It does not establish generalization to arbitrary physics.

## Next experiments

1. Hold out unseen angles and report generalization error.
2. Add noisy measurements and uncertainty estimates.
3. Compare a single neuron, a small multilayer network, and the analytic law.
4. Add a 2D point-mass task with gravity, contact, and energy accounting.
5. Only after that, build a multi-joint quadruped with rigid-body dynamics,
   actuators, sensors, and a reinforcement-learning controller.

## Scientific boundaries

The physics oracle is an idealized model. Real surfaces may involve diffuse
reflection, absorption, refraction, roughness, and measurement uncertainty.
The trainable neuron is an educational computational model, not a model of
brain tissue, vision, or biological motor control.
