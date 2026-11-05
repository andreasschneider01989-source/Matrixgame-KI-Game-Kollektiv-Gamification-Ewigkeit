# QUEST 001A — Qwen reproducible-evolution candidate

**Status:** preserved as an experimental source snapshot; not merged into the main simulation and not yet validated by compilation or runtime tests.

## Provenance

- Project: Matrixgame / Collective AI Games
- Source: code supplied by the project maintainer from a Qwen3.7-Plus conversation
- Captured: 2026-11-06
- Package: `de.matrixgame.collective`
- Intent: seeded simulation, agent genomes, resource regeneration, energy sharing/attack, basic population statistics.

This is a preservation and review snapshot. It does not claim that the implementation is scientifically validated, fully deterministic, or ready for production.

## Candidate files

The source files are under `src/main/java/de/matrixgame/collective/` in this experiment directory.

## Review notes before adoption

1. **SHARE double debit:** `tryShareEnergy()` already deducts the transferred amount from the donor. `CollectiveAgent.act("SHARE")` then subtracts another 5 energy. Decide whether the action has a separate overhead; do not unintentionally charge the transfer twice.
2. **Seeded RNG is not full determinism:** agent IDs use `UUID.randomUUID()`. The simulation's random behavior is seeded, but displayed IDs are not. Use deterministic IDs or omit IDs from reproducibility comparisons.
3. **Energy accounting needs explicit semantics:** attack cost (5), victim damage (15), and nutrition gain (8) are model parameters, not automatically a physical/thermodynamic law. Define energy units and distinguish internal energy transfer from external resource input and dissipated energy.
4. **Attack energy bookkeeping:** attacker pays 5 and gains 8; this is a net +3 before the action's extra cost. Victim loses 15. This may be intended as prey-energy conversion, but it needs a consistent ledger and clear accounting for the remaining energy.
5. **Dead-agent handling:** attack can kill an agent before its turn. Ensure death counters cannot be incremented twice and that dead agents are not counted in future interactions.
6. **Mutable collection exposure:** `getAgents()` returns the internal mutable list. Prefer a read-only view or snapshot.
7. **Density sensor includes self:** `getAgentDensityNear()` counts the sensing agent. Consider excluding it if density is intended to measure neighbors.
8. **Regeneration model:** 5% chance per cell per tick is a toy rule. Document the implied resource influx; it is not a conservation-based ecological model yet.
9. **Reproduction:** no population cap or explicit placement/overlap rule; reproduction is stochastic and only triggered after an action.
10. **Statistics:** action counters currently count attempted actions in some cases (e.g. SHARE/ATTACK), not necessarily successful outcomes. Consider separate attempt/success counters.
11. **WARN is a placeholder:** no effect on the environment or agents yet.
12. **No validation yet:** compile and test with Java 11+ (or the project's actual configured version), then add deterministic regression tests and energy-ledger tests.

## Suggested acceptance tests

- Same seed and same configuration produce identical state/statistics traces.
- SHARE: donor loss equals receiver gain plus any explicitly modeled transfer cost.
- EAT: resource reduction equals energy gain (unless a declared conversion loss is included).
- ATTACK: ledger accounts for donor/attacker/victim energy and any externalized dissipation.
- Energy never falls below 0 or exceeds maxEnergy after each action.
- Birth/death counters match population transitions.
- No action is performed by dead agents.
- Resource regeneration is reported as an external energy/resource input.

## Next step

Review and fix the candidate in a separate pass; only then merge or port it into the main `src/main/java/de/matrixgame/collective/` files.
