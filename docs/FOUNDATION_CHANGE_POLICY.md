# Matrixgame Foundation Change and Release Policy

**Version:** 1.0.0  
**Status:** Proposed governance policy  
**Applies to:** Documentation, schemas, research data, simulation code, agent workflows, and releases.

## 1. Repository protection model

The default branch is the protected reference line for reviewed work. All substantive changes should be made on a named branch and submitted as a pull request. Do not use an AI agent's ability to write files as authorization to merge, publish a release, or change consequential settings.

Recommended branch prefixes:
- `docs/` — documentation and specifications
- `schema/` — schema changes
- `research/` — research and data experiments
- `feature/` — implementation
- `fix/` — corrections
- `experiment/` — temporary or exploratory work

## 2. Stable and exploratory lanes

| Stable lane | Exploratory lane |
|---|---|
| Reviewed contracts and interfaces | New hypotheses and prototypes |
| Passing regression tests | Experimental tests and baselines |
| Versioned releases | Feature branches and notebooks |
| Documented migrations | Rapid iteration with explicit limitations |
| Human-reviewed merges | No automatic promotion |

Exploratory work must not overwrite stable results. Use separate output directories and unique run IDs.

## 3. Change classes

### Patch
Typographical corrections, clarifications, metadata corrections, and backward-compatible documentation improvements.

### Minor
Optional schema fields, new non-breaking artifact types, adapters, or modules.

### Major
Changed field meaning, removed required fields, changed status semantics, or incompatible protocol behavior.

Each change must state its class and explain compatibility.

## 4. Pull request checklist

- [ ] Scope and user value stated.
- [ ] Existing architecture and files inspected before adding duplicates.
- [ ] Facts, models, hypotheses, open questions, and fiction separated.
- [ ] Sources, timestamps, and reuse rights recorded.
- [ ] Privacy and security review completed.
- [ ] Tests and validation results included.
- [ ] Reproducibility manifest supplied where relevant.
- [ ] Backward compatibility and migration assessed.
- [ ] Documentation and examples updated.
- [ ] Human reviewer approved.

## 5. Reproducibility and immutable history

- Never silently edit a published result in place.
- Create a new revision and link it with `SUPERSEDES` or `CORRECTS`.
- Preserve original inputs and run configuration where redistribution is permitted.
- Record cryptographic checksums for large external datasets and artifacts.
- A random seed alone is not sufficient if software, dependencies, or hardware behavior can change.
- Record nondeterminism and expected tolerance.

## 6. Data and licensing

- Store source URL, publisher, access date, version/date, and license or rights status.
- A public URL does not imply permission to redistribute its content or train models on it.
- Prefer official open datasets and clearly licensed materials.
- Do not copy full copyrighted news articles into the repository without permission.
- Keep secrets and personal/sensitive data out of public branches, issues, logs, and generated reports.

## 7. AI agent permissions

Agents may, within their authorized scope:
- inspect repository files;
- propose edits on isolated branches;
- run approved tests;
- create draft pull requests;
- explain diffs and uncertainty.

Agents must not autonomously:
- merge their own changes;
- change branch protection or repository permissions;
- publish a release;
- expose credentials or private user data;
- deploy consequential decision systems;
- convert a hypothesis into a fact label without review.

## 8. Promotion and rollback

Promotion requires the Foundation Charter gates. If a new version causes regressions:
1. stop promotion;
2. identify the last known good revision;
3. revert or disable the new adapter/module;
4. preserve logs and failure details;
5. create a correction issue;
6. rerun regression tests before retrying.

## 9. Minimal release record

Each release should include:
- version and date;
- changes and compatibility notes;
- known limitations;
- test summary;
- source/data changes;
- license and provenance notes;
- rollback instructions.

## 10. Review cadence

Review this policy when a major architecture change is proposed, a security/privacy incident occurs, or at least once per major project phase. Changes to this policy require a pull request and a recorded rationale.
