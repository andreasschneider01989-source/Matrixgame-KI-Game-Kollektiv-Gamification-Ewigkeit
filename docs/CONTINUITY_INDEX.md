# Matrixgame continuity index

**Last checkpoint:** 2026-11-10  
**Checkpoint branch:** `agent/continuity-2026-11-10`

Read this index first when continuing work. Then read the dated checkpoint and the implementation backlog before changing code.

## Canonical references

1. [Continuity checkpoint 2026-11-10](CONTINUITY_CHECKPOINT_2026-11-10.md)
2. [Implementation backlog 2026-11-10](IMPLEMENTATION_BACKLOG_2026-11-10.md)
3. [Root README](../README.md)
4. [Existing Matrixgame Protocol](../matrixgame-protocol/README.md)
5. [Java 21 Matrix Core](../matrixgame-core/matrixgame-core/README.md)
6. [LUMINI compiler architecture (proposal)](LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md)
7. [Universal Knowledge Engine (proposal)](UNIVERSAL_KNOWLEDGE_ENGINE.md)
8. [Collective Improvement Network (proposal)](COLLECTIVE_IMPROVEMENT_NETWORK.md)

## Continuation prompt

Copy this into a new development session:

> Continue the existing Matrixgame project; do not start from zero. Read `docs/CONTINUITY_INDEX.md`, the linked latest checkpoint and backlog, then inspect the actual current branch, source files and tests. Report what is implemented, tested, merely proposed, or blocked. Choose one smallest high-value backlog item, make the change on a feature branch, run the relevant tests, record exact commands and results, and update the checkpoint. Do not merge to main or publish private material without explicit approval. Keep FACT/MODEL/HYPOTHESIS/OPEN/FICTION separate from implementation status. Use existing schemas and modules rather than inventing parallel systems.

## Storage boundary

GitHub is the canonical public source for reviewed code, public schemas and documentation. Private documents and personal data remain in private storage. A browser tab is not proof of API access; a connected service is not permission to publish its contents. The current integration reference kit is a separate local artifact until its files are intentionally reviewed and added to the repository.

## Session discipline

- One primary task per work session.
- Start by loading this index and checking real repository state.
- Make small branch-based changes with tests.
- Record failed tests and unresolved questions rather than smoothing them over.
- End with a checkpoint: changed paths, commit(s), tests, known limits, next smallest action.
- No background execution is implied; ongoing checks require a separately configured automation.
