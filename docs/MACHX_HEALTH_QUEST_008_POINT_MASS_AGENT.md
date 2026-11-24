# MACHX Quest 008 — Physics-grounded point-mass agent

## Goal
Build the smallest useful bridge from physical motion to an agent that learns a
movement policy. This is a 2D point mass, not yet a body with joints, muscles,
proteins, cells, or a quadruped.

## FACT — implemented mechanics
- Force becomes acceleration through Newton's second law, a = F / m.
- Velocity is advanced with a configurable time step and environmental
  acceleration; damping and a maximum-speed constraint are applied.
- Position is advanced from velocity.
- The world is bounded. Boundary contact clamps position and reverses the
  corresponding velocity component with a 0.5 restitution-like factor.
- Reward measures progress toward a target, with a small step cost and a
  terminal bonus.
- Energy spent is an illustrative control-effort proxy, not calibrated
  thermodynamic energy.

## MODEL — learning agent
A tabular Q-learning agent observes coarse bins of target-relative position
and velocity, chooses one of nine discrete force directions, receives the
physics environment's reward, and updates action values:

Q(s,a) <- Q(s,a) + alpha [r + gamma max Q(s',a') - Q(s,a)]

A fixed seed makes exploration reproducible. The state abstraction and table
are deliberately small and interpretable.

## HYPOTHESIS / next experiments
- Compare learned control with random and hand-coded controllers on held-out
  starting positions and target placements.
- Add obstacles and explicit collision geometry.
- Introduce rotational dynamics and a pendulum before adding joints.
- Add spring-like forces, then compare simple pair potentials with
  Van-der-Waals-inspired interaction curves.
- Only after these stages attempt multi-joint locomotion.

## Scientific boundary
This model demonstrates software architecture and basic mechanics. It is not
a biological motor-control model, molecular simulation, protein model, or
organism. Arbitrary world units and the energy proxy are not empirical
physical or biological measurements.

## Reproduction
python -m pytest -q tests/test_point_mass_agent.py
