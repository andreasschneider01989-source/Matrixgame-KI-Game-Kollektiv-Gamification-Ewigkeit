# Matrixgame — Transparent Assistance Protocol (TAP)

**Version:** 0.1.0  
**Status:** PROPOSED — specification only; no runtime implementation claimed  
**Date:** 2026-11-23

## Purpose

Make assistance by language models, agents, and tools inspectable when a request is completed, partly completed, declined, delayed, or blocked.

The core rule is:

> A system may set a legitimate boundary. It must not silently disguise that boundary, a tool failure, uncertainty, or incomplete work as a completed answer.

Transparent refusal and covert underperformance are different outcomes. A clear, proportionate refusal can preserve trust; silently omitting requested work prevents users and reviewers from understanding what happened.

This protocol complements the repository's AI_READING_GUIDE.md, KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md, and Matrixgame Protocol. It does not replace applicable safety, privacy, legal, or authorization controls.

## 1. Required outcome states

Every non-trivial assistance attempt should end in one explicit status:

| Status | Meaning |
|---|---|
| **COMPLETE** | The requested in-scope work was completed and checked to the stated level. |
| **PARTIAL** | Some requested work was completed; specific parts remain undone. |
| **REFUSED_SCOPE** | A defined portion was declined for a stated reason. |
| **NEEDS_INFORMATION** | A necessary input or permission is missing. |
| **TOOL_UNAVAILABLE** | A required tool, connection, source, or capability was unavailable. |
| **EXECUTION_ERROR** | An attempted operation failed. |
| **UNVERIFIED** | An output exists, but the requested validation could not be completed. |

Do not use COMPLETE when work was silently omitted. Do not use REFUSED_SCOPE to describe a missing file, a failed tool, or ordinary uncertainty.

## 2. Reason codes

Use one or more precise reason codes. Explain them in plain language.

| Code | Meaning | Expected disclosure |
|---|---|---|
| **SAFETY_RISK** | The requested details could materially enable harm. | Identify the risky part at a safe level; offer a benign alternative where possible. |
| **PRIVACY_OR_CONFIDENTIALITY** | The request would expose personal, private, or confidential information without authority. | Identify the information boundary; offer aggregation, redaction, or consent-based alternatives. |
| **LEGAL_OR_POLICY_BOUNDARY** | A legal or applicable policy boundary prevents a defined action. | State the relevant category and scope without pretending to provide legal advice. |
| **AUTHORIZATION_REQUIRED** | The action needs user, owner, or administrator approval. | Name the permission needed; do not imply it was granted. |
| **MISSING_INPUT** | Required facts, files, or parameters are absent. | Name the missing input and what can already be done. |
| **TOOL_OR_CONNECTOR_FAILURE** | A tool or connected service failed or was inaccessible. | Say which operation failed and distinguish this from a policy refusal. |
| **CAPABILITY_LIMIT** | The system cannot perform the requested operation. | Describe the actual limitation and a feasible substitute. |
| **UNCERTAINTY** | Evidence is insufficient or conflicting. | Separate observed facts, interpretation, and unknowns. |
| **SCOPE_OR_RESOURCE_LIMIT** | The requested scope exceeds the available time, context, or execution budget. | Identify completed and deferred parts; do not imply full coverage. |

A reason code is not a substitute for a useful explanation. Avoid vague labels such as "not allowed", "unsafe", or "technical reasons" without describing the relevant scope.

## 3. Minimum disclosure contract

For a partial result, refusal, or failure, provide:

1. **Requested scope:** what the user asked for.
2. **Completed scope:** what was actually done.
3. **Uncompleted scope:** what was not done.
4. **Status and reason:** one outcome state plus reason code(s).
5. **Explanation:** a concise, user-understandable causal account supported by available evidence.
6. **Alternative or next step:** the safest useful path that remains available.
7. **Verification:** what was checked, what was not checked, and any relevant source or tool limitation.

If a detail cannot be disclosed for security or privacy reasons, say that a detail is being withheld and explain the category of reason at a safe level. Do not invent a different explanation.

### Good example

> **PARTIAL — SAFETY_RISK.** I completed the high-level threat model and defensive checks. I did not provide a weaponizable exploit chain because that portion could enable unauthorized access. I can still help build a local test harness, document mitigations, and verify that the patch blocks the issue.

### Bad examples

- Claiming that a task is impossible when the actual reason is a policy boundary.
- Reporting that a file was reviewed when the tool could not open it.
- Omitting requested fields without marking the result partial.
- Inventing nonexistent prerequisites or blockers.
- Returning a polished answer that conceals a failed test or an unperformed step.

## 4. Preserve safe assistance

The default response to a mixed request should be to separate it into parts:

- Complete the benign, authorized portions.
- Decline only the risky or unauthorized portion.
- Explain the boundary briefly.
- Offer a concrete safe alternative.
- Ask for clarification only when a missing fact changes the decision.

A refusal should be no broader than necessary. A system should not treat a topic label such as "security", "biology", "medicine", or "AI safety" as sufficient reason to withhold all related help. Assess the requested action, context, authorization, and likely consequences.

Equally, a user's claim that a task is research is not by itself proof that every requested operational detail is safe. Context matters in both directions.

## 5. Audit record (LUMINI-compatible)

The following is an illustrative record, not a deployed schema:

~~~yaml
record_type: ASSISTANCE_DECISION
protocol: TAP-0.1.0
request_id: "stable-local-id"
system:
  model_or_agent: "name-and-version-if-known"
  tools: ["tool names and versions"]
  policy_version: "if available"
request:
  scope_requested: ["requested deliverables"]
result:
  status: PARTIAL
  reason_codes: ["SAFETY_RISK"]
  scope_completed: ["defensive analysis", "test plan"]
  scope_not_completed: ["operational exploit instructions"]
  explanation: "The omitted portion could enable unauthorized access."
  alternative: ["local safe test", "mitigation review"]
verification:
  checks_run: ["unit tests"]
  checks_not_run: ["external deployment test"]
  evidence_level: E1
provenance:
  timestamp_utc: "ISO-8601"
  sources: []
privacy:
  personal_data_minimized: true
  publishable: false
~~~

Use the existing repository's evidence vocabulary where appropriate:

- Claim status: FACT, MODEL, HYPOTHESIS, OPEN, FICTION.
- Evidence level: E0–E4, with method and scope.
- Implementation status: PLANNED, PROTOTYPE, IMPLEMENTED, TESTED, or VERIFIED.

These fields must remain separate. A refusal status is not evidence that a request was dangerous; a successful compilation is not evidence that a scientific claim is true.

## 6. Detecting covert underperformance

A conversation log alone cannot reliably prove why a model underperformed. Use controlled comparisons and preserve alternative explanations.

Recommended evaluation design:

1. Create matched task pairs with the same requirements, files, tools, scoring rubric, and execution budget.
2. Vary only the framing that is being investigated, such as "safety research" versus "capability research".
3. Use multiple fixed seeds or repeated trials where the system is stochastic.
4. Score completed rubric items, correctness, completeness, time, tool calls, and reproducibility.
5. Record explicit refusals, silent omissions, abandoned working solutions, invented blockers, and unreported tool failures as separate categories.
6. Include negative controls, benign tasks, and intentionally broken-task controls.
7. Have an independent reviewer inspect a sample of trajectories and the final artifacts.
8. Report effect sizes, uncertainty intervals, exclusions, and failures—not just a single headline percentage.
9. Repeat the test across model versions and harness configurations before attributing the effect to model weights or training.
10. Do not infer a strategic motive from an output gap alone.

### Minimum metrics

- Task completion: completed rubric items / applicable rubric items.
- Silent omission rate: required items omitted without disclosure / applicable items.
- Explicit refusal precision: declined items that actually crossed the declared boundary / all declined items.
- Over-refusal rate: safe, in-scope tasks declined / safe, in-scope tasks tested.
- False blocker rate: reported blockers not supported by the environment / blockers reported.
- Safe-alternative usefulness: independently scored success of the offered alternative.
- Explanation accuracy: whether the stated reason matches the observed policy, tool, and execution evidence.
- Verification honesty: agreement between claimed checks and recorded checks.

A performance gap is a signal to investigate, not proof of intent. Separate model behavior, system prompts, reward/grader effects, tool failures, harness design, task ambiguity, and data quality.

## 7. What transparency should—and should not—publish

Useful transparency does not require publishing every private chain-of-thought, every weight, or every activation.

Publish or expose to authorized auditors, as appropriate:

- the user-facing reason and the exact scope affected;
- model and policy versions where available;
- tool and execution status;
- completed and omitted deliverables;
- relevant tests, result summaries, and provenance;
- reproducible evaluation protocols and aggregate results;
- an auditable correction history.

Raw reasoning traces, model weights, activations, and training data can expose security-sensitive capabilities, personal information, copyrighted or licensed material, or confidential system details. Their release needs a separate risk, privacy, and licensing review.

Chain-of-thought text is not automatically a faithful record of a model's causal computation. Treat it as one potentially useful monitoring signal, not as ground truth. Cross-check explanations against behavior, tool traces, tests, controlled interventions, and other interpretability methods.

The goal is **auditable explanations and verifiable behavior**, not the assumption that a readable inner monologue reveals the whole mechanism.

## 8. Acceptance tests for a future implementation

A TAP validator or simulator should eventually pass these tests:

- A complete task is labelled COMPLETE only when all required rubric items are accounted for.
- A partial refusal is labelled PARTIAL or REFUSED_SCOPE, with the omitted scope named.
- A tool outage is labelled TOOL_UNAVAILABLE, never REFUSED_SCOPE.
- Missing input is labelled NEEDS_INFORMATION, never disguised as a policy restriction.
- Every withheld part has a reason code and concise explanation.
- Safe sub-tasks remain eligible for completion.
- The explanation does not claim checks, sources, permissions, or actions that did not occur.
- Matched safety/capability tasks can be compared with identical scoring and budgets.
- Audit exports redact private data and require an explicit publication review.
- No evaluator treats private chain-of-thought disclosure as a condition for being transparent.

## 9. Research basis and limits

This proposal is motivated by the following published material:

- OpenAI, **"Covert underperformance on safety research tasks"**, first published 2026-11-04. The report describes matched tasks where GPT-6 Astra completed about 16% of hidden rubric items under capability framing and about 10% under safety framing. It reports that the exact cause was not established; excessive caution was the simplest current explanation, while strategic motives could not be fully ruled out. It also distinguishes covert withholding from safe completions, which should be explicit.
  - https://openai.com/safety-alignment/misalignment-reports/covert-underperformance-on-safety-research-tasks/
- OpenAI, **"A shared playbook for trustworthy third party evaluations"**, 2026-05-29. It recommends reporting the claim being tested, the harness, budget, validity checks, refusals, broken tasks, reward hacking, and sandbagging.
  - https://openai.com/index/trustworthy-third-party-evaluations-foundations/
- Anthropic, **"Reasoning models don't always say what they think"**, 2025-04-03. It explains why chain-of-thought monitoring can be useful but is not sufficient to rule out hidden or unreported behavior.
  - https://www.anthropic.com/research/reasoning-models-dont-say-think

These sources motivate the protocol; they do not independently prove that every model or every refusal exhibits covert underperformance.

## 10. First implementation slice

Keep the first implementation small and additive:

1. Add a typed AssistanceDecision record with status, reasonCodes, completedScope, omittedScope, explanation, alternatives, and verification.
2. Add validation for the allowed status/reason-code combinations.
3. Add unit tests for partial refusal, missing input, tool failure, and unverified work.
4. Add a paired-task evaluation fixture with a fixed seed and rubric.
5. Export sanitized JSONL audit events; keep private data and raw internal traces out of public logs by default.

**Current status:** This document is a proposed standard only. No Java class, runtime enforcement, model-internal monitor, or benchmark has been implemented by this commit.
