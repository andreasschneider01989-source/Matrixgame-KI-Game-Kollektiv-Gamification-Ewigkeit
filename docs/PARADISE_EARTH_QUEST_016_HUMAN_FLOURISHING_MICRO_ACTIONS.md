# Paradise Earth Quest 016 – Human Flourishing Micro-Action Prototype

**Status:** draft experiment  
**Branch:** `feature/paradise-earth-quest-016-micro-actions-2026-11-27`  
**Evidence class:** MODEL / SOFTWARE PROTOTYPE  
**Scope:** deterministic planning aid; not a clinical, psychological, legal, or safety assessment.

## Objective

Turn a self-chosen human-development intention into one small, voluntary, reviewable action. Quest 016 tests software structure and reproducibility, not whether a person becomes happier or healthier.

## Seven-domain model

The prototype uses the seven project domains:
1. Health
2. Freedom
3. Will and meaning
4. Relationships
5. Knowledge
6. Safety
7. Joy

One primary domain is selected by the user. A distinct supporting domain is optional. The planner does not infer a person's needs or choose goals on their behalf.

## Input and output

Input:
- a short user-written goal (1–300 characters);
- one primary domain;
- optionally one different supporting domain;
- a voluntary timebox from 1 to 30 minutes.

Output:
- one general action prompt;
- a qualitative completion reflection;
- a fallback if the step is too difficult or unsuitable;
- an explicit right to pause, adapt or stop.

The planner is deterministic: identical inputs produce identical plans. It stores no data and makes no network calls.

## Safety and autonomy

- No wellbeing, consciousness, productivity or worth score.
- No streaks, leaderboards, penalties or XP loss.
- Health prompts are not medical advice and never recommend changing treatment.
- Relationship prompts require respect for consent, boundaries and non-response.
- Safety prompts do not replace emergency services or qualified human help.
- Personal goals remain user-controlled; a plan can be changed or abandoned at any time.
- Do not put private personal goals or health/relationship details into public GitHub issues or commits.

## Reproduction

From the repository root, with Python 3.10 or newer:

```bash
python -m unittest tests.test_flourishing tests.test_micro_actions -v
```

Or run the project's configured test runner if installed:

```bash
python -m pytest
```

Expected coverage:
- all seven domains have a prompt;
- same input yields the same result;
- invalid goals and timeboxes are rejected;
- optional supporting domain must be distinct;
- no score or rank is created.

## Acceptance criteria

- [x] Seven-domain vocabulary reused from the Human Flourishing module.
- [x] Deterministic action plan with no random or network dependency.
- [x] Bounded user input and timebox.
- [x] Explicit pause/adjust/stop rule.
- [x] No personal-data persistence.
- [x] Unit tests for reproducibility and validation.
- [ ] Review against the existing Knowledge Gate and project coding conventions.
- [ ] Hosted CI confirmation.
- [ ] Human review before any integration into the stable branch.

## Limitations

This is a small template-based prototype. It does not assess suitability for a specific person, predict outcomes, diagnose conditions, measure flourishing, or establish causal effects. The prompts are starting points and may not fit every culture, disability, situation or preference. User judgement takes precedence.

## Next step

If reviewed successfully, Quest 017 can add a private, local-only reflection interface with explicit consent and no automatic upload. Before implementing storage, choose and document a privacy-preserving mechanism.

**Guiding line:** A useful tool increases a person's options; it does not take ownership of their goals.
