# LUMINI Idea Pipeline — MVP 0.1
**Status:** executable, dependency-free Java prototype. The scope is deliberately limited to intake, traceability, heuristic routing hints and JSONL export.

## What is implemented
- Immutable IdeaPacket; original text is retained unchanged.
- Default epistemic status OPEN and privacy class PRIVATE.
- Explicit filter outcomes. NOT_RUN is never printed as PASS.
- Deterministic keyword hints for CODE_MODULE, MICRO_QUEST, RESEARCH_TASK, MODEL_DRAFT and SIMULATION.
- Human-readable console report and one-record JSON Lines export.
- Dependency-free regression tests and a GitHub Actions workflow compiling against the Java 11 API.

## What is intentionally not implemented
This MVP is not an LLM, semantic parser, fact checker, causal discovery engine, LUMINI parser, Model IR compiler, simulation engine or automatic code generator. Keyword matches are suggestions only. Epistemic and causal analysis remain NOT_RUN; unreviewed claims remain OPEN. Public export requires a separate human decision; new packets are marked PRIVATE.

## Build and run
Run ./scripts/run-lumini-mvp.sh
For JSONL-only output, run ./scripts/run-lumini-mvp.sh --jsonl-only "Matrixgame Java lernen"
Under Windows, double-click scripts/Start-LUMINI-MVP.bat. The script compiles with UTF-8, runs regression tests and starts the demo.

## Acceptance criteria
1. Blank input is rejected.
2. Original text survives unchanged, including whitespace and line breaks.
3. Route suggestions are labelled as heuristics and require human review.
4. OPEN is the default epistemic status; no unreviewed claim becomes FACT.
5. Unimplemented epistemic and causal filters report NOT_RUN.
6. JSONL output is one validly escaped physical line.
7. The regression test suite reports 5/5 PASS.

## Next extension
Add a user-reviewed Claim model with source references and explicit epistemic labels; then add typed entities/relations and a JSON Schema. Only after those contracts are tested should the project compile reviewed models to Model IR or execute them in simulation.
