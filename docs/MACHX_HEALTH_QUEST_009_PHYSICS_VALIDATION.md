# MACHX Quest 009 — Physics validation lab

## Purpose
Check numerical mechanics against an analytic reference before adding more
complex forces or organism dynamics.

## FACT — implemented
- Integrates constant-acceleration motion using exact kinematics,
  explicit Euler, semi-implicit Euler, and velocity-Verlet updates.
- Compares sampled trajectories using position and velocity RMSE and maximum
  component errors.
- Checks SI dimensions for force/mass -> acceleration, mass/velocity ->
  momentum, and force/displacement -> work.
- Velocity-Verlet's constant-acceleration update matches the analytic
  kinematic solution (up to floating-point roundoff).

## MODEL / assumptions
- Classical, non-relativistic mechanics.
- Constant acceleration within each integration step.
- Values passed to the trajectory integrator are coherent SI values.
- This validation lab does not yet test orbital energy conservation,
  molecular potentials, collisions, or chaotic systems.

## HYPOTHESIS / next experiments
- Add harmonic oscillator and central-force test problems.
- Measure energy drift for symplectic versus non-symplectic integrators.
- Use validated force/energy contracts as prerequisites for Quest 010
  pair-interaction potentials.

## Reproduction
python -m pytest -q tests/test_physics_validation.py
