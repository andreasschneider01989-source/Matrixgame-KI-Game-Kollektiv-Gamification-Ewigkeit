# Matrixgame – Human Flourishing & Research Integrity 001

**Status:** proposal / draft  
**Scope:** project governance and research workflow; not a clinical or psychological instrument.

## Purpose

Matrixgame should preserve curiosity while improving scientific reliability and real-world usefulness. A speculative idea is not a personal failure. It is an input that can be clarified, tested, revised, or kept as fiction.

**Core rule:** no idea is ridiculed for being unusual; no idea is promoted to fact because it is exciting; every idea receives a fair and proportionate path to evaluation.

## Three connected tracks

### A. LUMINI Knowledge & Validation Gate

A claim is stored as a versioned record with:
- stable identifier and exact claim text;
- evidence class: FACT, MODEL, HYPOTHESIS, OPEN, FICTION;
- source identifiers and provenance;
- method, units and scope where applicable;
- uncertainty and known limitations;
- a falsification or acceptance test;
- review status and revision history.

A passing software/schema check means only that the record is structurally reviewable. It does **not** prove the claim is true. Scientific validation requires appropriate evidence, independent scrutiny, reproducibility and domain expertise.

### B. Second Earth – empirical model validation

Continue from Quest 014, but do not confuse a conservation-law test with empirical validity. The next stage should:
1. preserve the seeded synthetic experiment as a regression fixture;
2. add a documented real-data adapter only after selecting a source and defining licensing, temporal resolution, spatial scale and units;
3. separate calibration data from held-out validation data;
4. compare baseline and intervention under paired forcing;
5. report uncertainty, missing variables and sensitivity to parameters;
6. reject causal claims when the design cannot identify causality.

The single-bucket water model remains a teaching model until independently calibrated and tested against observations.

### C. Human Flourishing – real-life outcomes

The project should support, not compete with, the user's life. Candidate domains:
- wellbeing and sustainable energy;
- meaningful social contact, communication and consent;
- learning and practical skill development;
- career steps and financial stability;
- access to qualified healthcare and recovery support;
- rest, autonomy and enjoyment.

Use small, user-chosen actions and qualitative reflection. Do not turn mood, sexuality, relationships, sobriety, medication, or health into public scores, competitive leaderboards, or automated diagnoses. Sensitive personal data must stay out of the public repository unless the person makes a deliberate, informed decision to publish it.

Medical treatment decisions—including ADHD assessment and medication—belong with qualified clinicians. The project may help prepare questions, organize records with consent, and track user-chosen practical tasks; it must not recommend self-medication, dose changes, or medication substitution.

Social-development features must respect other people's autonomy. No manipulation, coercion, deceptive persuasion, or promise of romantic/sexual outcomes. Optimize for mutuality, clarity, safety and consent.

## Research lifecycle

1. **Capture:** preserve the original wording and date; do not silently rewrite history.
2. **Clarify:** split compound statements into atomic claims.
3. **Classify:** assign FACT / MODEL / HYPOTHESIS / OPEN / FICTION.
4. **Specify:** sources, assumptions, units, scope and uncertainty.
5. **Test:** mathematical checks, code tests, counterexamples, empirical comparisons or expert review as appropriate.
6. **Decide:** ACCEPT / REJECT / UNCERTAIN / ABSTAIN / PROPOSE ALTERNATIVE, with reasons.
7. **Version:** retain superseded claims and link corrections rather than erasing the learning path.
8. **Reuse:** only claims meeting the relevant acceptance criteria may flow into models, forecasts or user-facing recommendations.

## Anti-disappointment and motivation safeguards

- Label simulations and forecasts before presenting their outputs.
- State what was actually run, what was merely drafted, and what was not checked.
- Never imply continuous background monitoring or automatic model learning from public GitHub.
- Prefer small completed experiments over grand unverified promises.
- No XP loss for errors, pauses, rest, uncertainty or changing one's mind.
- Reward correction, careful abstention, asking for help and identifying limitations.
- Keep a clear separation between a playful narrative layer and the evidence layer.

## Acceptance criteria for this module

- Every stored claim has an explicit evidence class.
- FACT records require at least one traceable source identifier.
- HYPOTHESIS records define a test that could count against them.
- FICTION records explicitly state that they make no factual claim.
- No schema pass is described as scientific proof.
- Sensitive personal information is excluded from public research artifacts by default.
- User autonomy, consent, clinical boundaries and the option to stop are explicit design constraints.

## Immediate next steps

1. Review the Knowledge Gate code and tests in this branch.
2. Connect the gate to the existing LUMINI IdeaPacket/IR pipeline only after checking current interfaces.
3. Keep Second Earth Quest 014 unchanged as the baseline; design Quest 015 as a separate real-data validation proposal.
4. Add a private, user-controlled personal action log only if a suitable private storage mechanism is chosen. Do not put personal health or relationship details in public GitHub.

**Guiding line:** Keep the wonder. Improve the method. Protect the person. Share what survives testing.
