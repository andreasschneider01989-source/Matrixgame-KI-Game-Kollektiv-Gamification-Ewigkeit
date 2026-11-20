# MACHX Second Earth — Quest 001 implementation record

**Date:** 2026-11-20  
**Status:** executable pilot committed on the LifeOS feature branch; awaiting GitHub Actions and review.

## User-selected direction
- 3D basis: hybrid (Minecraft-compatible visualization plus an independent simulator).
- World vision: personal development, Second Earth and collective AI civilization connected.
- First focus: scientific simulation.
- AI cooperation: scientific review and reproducible experiments.

## Architecture decision
The simulator owns the canonical state and deterministic transition rules. A future 3D adapter visualizes approved state snapshots and submits bounded commands. LUMINI records experiment manifests, evidence status, limitations and next quests. LLMs may propose hypotheses or policy candidates; they do not directly mutate world state.

## Implemented pilot scope
\`second-earth/\` contains a dependency-free Python model for one abstract renewable biomass stock and a food store. It compares three transparent rule-based policies (GROWTH, REGENERATION, COOPERATION) over identical seed sets, with 100 steps and optional stochastic shocks. The run manifest explicitly marks the experiment as a synthetic MODEL.

The first version deliberately excludes hydrology, climate, energy infrastructure, biodiversity, trade, population dynamics, 3D rendering, Minecraft integration and trained neural networks. The existing Gaia Earth Lab water-cycle models remain the candidate hydrology module; this pilot must not replace or duplicate them.

## Scientific method
- Paired seeds: each policy is evaluated against the same random disturbances.
- Reproducibility: same config, policy and seed produce the same trajectory.
- Separate metrics: final/minimum biomass, mean food coverage and shortage steps.
- No composite civilization score.
- Synthetic parameters are teaching assumptions, not calibrated measurements.
- Model consistency and passing tests do not establish empirical validity or forecast accuracy.

## Verification
The dedicated PR workflow runs Python standard-library tests and a 30-seed experiment. Until a visible GitHub Actions result exists, CI status is PENDING; no pass is claimed.

## Acceptance criteria
1. All tests pass.
2. Same seed reproduces the same run.
3. Every policy uses the same seed set.
4. Resource stocks remain non-negative.
5. Results include configuration, assumptions, seed design and limitations.
6. No private user data or credentials are stored.
7. No existing Java prototype or stable game version is modified.

## Next quest
After review: add a typed JSON world-state snapshot and a read-only 3D adapter proof-of-concept. Choose Minecraft Java or Luanti only after the simulator contract is stable. The Gaia water model may then be connected through a versioned adapter.
