# Matrixgame repository instructions for GitHub Copilot

Follow the repository-level instructions in `/AGENTS.md`.

For Java changes:
- Preserve the package `rhgr`, Java 17 compatibility, readable formatting and current architecture.
- Keep v0.10.0 unchanged. Make evolution changes in the v0.11.0 class or a new versioned class.
- Inspect existing deterministic tests before editing.
- Add a regression test for every behavior change.
- Do not state that a simulation passed until the test or CI log proves it.
- Use the documented compile, `--self-test` and `--simulate` commands.
- Prefer a focused patch and explain any modified action legality, energy costs, population accounting or team rules.
