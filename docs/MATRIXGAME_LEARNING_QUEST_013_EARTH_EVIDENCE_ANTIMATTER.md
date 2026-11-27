# MATRIXGAME LEARNING QUEST 013 – Earth, Evidence & Antimatter
Date: 2026-11-27
Branch: `feature/wellbeing-learning-loop-2026-11-27`
Mode: EXPLORATORY / documentation-first

## Mission purpose

Connect curiosity about antimatter with a grounded Earth-simulation development workflow. Strengthen evidence literacy, reproducibility, individual and collective decision quality, and open-source collaboration.

This document is a learning and engineering plan, not a claim that the proposed simulator is a validated Earth-system forecast.

## 1. Antimatter: distinguish two different claims

### FACT – laboratory antihydrogen is established

Antihydrogen is the antimatter counterpart of hydrogen: an antiproton bound with a positron. CERN experiments have produced antihydrogen, trapped it, released it under controlled conditions, and measured its spectrum.

Milestones:
- 2002: CERN reports detection and measurement of cold antihydrogen.
- 2010: ALPHA reports successful magnetic trapping of antihydrogen.
- 2014: ASACUSA reports unambiguous detection of 80 antihydrogen atoms in a beam 2.7 metres downstream from production.
- 2017: ALPHA reports observation of the 1S–2S transition in trapped antihydrogen; the measured frequency is consistent with hydrogen.
- 2025: CERN reports a new cooling method enabling production of over 15,000 antihydrogen atoms in hours.

These are not merely speculative interpretations of an unexplained detector signal. The collaborations use controlled production of antiprotons and positrons, known apparatus, magnetic trapping or beam transport, annihilation signatures, background estimates and spectroscopy.

### OPEN – cosmic-ray antihelium candidates are a different claim

AMS-02 antihelium-3 / antihelium-4 candidate events have been discussed in conference presentations and theoretical papers. A 2024 Physical Review D paper explicitly notes that the candidate events were not officially claimed as a formal antihelium detection by AMS-02, were not published as a formal collaboration result at that time, and had limited publicly available data with substantial uncertainties.

A candidate signal can be challenged by alternative explanations such as:
- misidentification of ordinary nuclei due to finite detector resolution;
- charge-sign confusion from track reconstruction;
- background events or interactions in detector material;
- uncertainties in mass determination and event selection.

The relevant question is not whether anti-atoms can exist (they are experimentally established), but whether a particular astrophysical antihelium candidate has been identified reliably and what its origin would be.

### MODEL – evidence ladder for unusual discoveries

1. Signal observed.
2. Instrument response and calibration checked.
3. Background hypotheses estimated.
4. Independent reconstruction / alternative explanations tested.
5. Statistical significance and systematic uncertainty reported.
6. Independent confirmation or reproducible follow-up.
7. Physical interpretation constrained by the evidence.

Do not collapse “candidate”, “evidence”, “observation” and “confirmed discovery” into one label.

## 2. Earth Simulator: next reproducible milestone

### QUEST 013 – Paired Scenario Experiment

Compare two scenarios from the same initial state:
- BASELINE: reference conditions.
- HEATWAVE: altered heat forcing, with all other parameters explicitly declared.

Use the same seed and model version for both. Record every parameter and the source or rationale for it.

Initial metrics:
- soil water / available water;
- vegetation biomass or explicitly named vegetation proxy;
- soil nutrient pool;
- temperature or heat-stress proxy;
- species richness / habitat occupancy proxy;
- water and nutrient budget residuals.

For each metric, declare unit, allowed range, update equation and whether it is a measurement, estimate, model state or game-only proxy.

### Required reproducibility checks

- Same version + seed + parameters + initial state gives identical output (or a documented numerical tolerance).
- Inputs outside valid ranges fail clearly.
- Water balance: initial storage + inflows - outflows = final storage, within declared tolerance.
- Nutrient balance: initial pool + inputs - uptake/losses = final pool, within declared tolerance.
- Scenario comparison changes only the intended forcing.
- JSON run manifest records model commit, version, seed, scenario, parameters, environment and outputs.
- A separate reviewer can reproduce the run from the manifest.

Do not tune the model to make the heatwave look dramatic. A scientifically useful result can be surprising, weak, null or contrary to the initial expectation.

## 3. Collective intelligence protocol

Each agent first submits an independent proposal before seeing other agents' conclusions.

Roles:
- Earth Modeler: describes mechanisms and equations.
- Data/Evidence Agent: checks sources, units, provenance and uncertainty.
- Skeptic: proposes alternative explanations and falsification tests.
- Simulation Agent: executes only reproducible runs and records artifacts.
- Synthesis Agent: compares evidence and reports disagreements.
- Human Controller: chooses goals, approves changes and interprets practical relevance.

Consensus is not truth. Preserve dissent and confidence intervals / uncertainty notes. Compare solo-agent and multi-agent results to test whether collaboration improves accuracy rather than merely confidence.

## 4. Individual and collective decision quality

For every important proposal, record:
- objective;
- available evidence;
- assumptions;
- options considered;
- expected benefits and risks;
- uncertainty;
- reversible vs irreversible effects;
- test or observation that could change the decision.

Use a decision status:
ACCEPT / REJECT / UNCERTAIN / ABSTAIN / PROPOSE ALTERNATIVE.

For real-world decisions affecting people or ecosystems, prioritize safety, consent, privacy, ecological safeguards and human oversight.

## 5. Open-source and public knowledge principles

- Keep stable features protected; experiment on isolated branches.
- Prefer small, reviewable commits.
- Include license, attribution, dependencies and run instructions before public release.
- Publish data schemas and synthetic example datasets; avoid private or sensitive personal data.
- Separate FACT / MODEL / HYPOTHESIS / OPEN / FICTION.
- Store source provenance, version, seed, parameters and test results.
- Make it easy to fork, reproduce, adapt and improve the work.
- Never imply that a model or AI consensus guarantees real-world success.

## 6. Scientific research references

### Antihydrogen
- CERN, 2002, first glimpse inside cold antihydrogen: https://home.cern/cern-experiment-provides-first-glimpse-inside-cold-antihydrogen/
- CERN, 2010, antihydrogen atoms produced and trapped: https://home.cern/antimatter-atoms-produced-and-trapped-at-cern/
- CERN, 2014, ASACUSA beam and unambiguous detection of 80 antihydrogen atoms: https://home.cern/cern-experiment-produces-first-beam-of-antihydrogen-atoms-for-hyperfine-study/
- ALPHA, 2017, 1S–2S transition: https://alpha.web.cern.ch/observation-1s-2s-transition-trapped-antihydrogen/
- CERN, 2025, improved antihydrogen production: https://home.cern/breakthrough-antimatter-production/

### Cosmic antihelium candidates
- Fedderke et al. (2024), Physical Review D 109, 123028: https://doi.org/10.1103/PhysRevD.109.123028
  The paper discusses tentative AMS-02 candidates and explicitly notes their unconfirmed status and limitations of public data.

### Earth-system modeling and reproducibility
- RHEM (2026), reproducibility in hybrid Earth system models: https://www.nature.com/articles/s42256-026-01299-5
- Kelleher & Mahajan (2026), enhanced climate reproducibility testing: https://doi.org/10.5194/esd-17-23-2026
- Massoud et al. (2026), soil moisture and ecohydrological benchmarking: https://doi.org/10.5194/gmd-19-3427-2026
- CSSPv3 (2026), coupled surface/subsurface water and carbon processes: https://doi.org/10.1029/2025MS005440

## 7. Learning micro-quest (optional, 10–15 minutes)

Choose one:
A. Explain in your own words why a positron plus an antiproton can form antihydrogen.
B. List three alternative explanations that must be tested for a cosmic antihelium candidate.
C. Draw the water balance of one Earth-simulator grid cell.
D. Write one falsifiable prediction for the baseline vs heatwave experiment.

Stop after one small learning step. No score threshold, forced repetition or performance obligation.

## 8. Acceptance criteria for future implementation

This quest is complete only when:
- current Earth Simulator source and artifacts are located and versioned;
- baseline and heatwave runs can be reproduced;
- budgets and invariants are tested;
- metrics and units are documented;
- an independent evidence review is recorded;
- outputs are published with license and reproduction instructions.

## 9. Evidence status

- FACT: CERN laboratory antihydrogen production, trapping and spectroscopy are established by published experimental results.
- FACT: AMS-02 cosmic antihelium candidate reports described in the cited 2024 paper were tentative and not a formal confirmed collaboration detection.
- MODEL: the evidence ladder and Earth paired-scenario workflow are proposed project protocols.
- HYPOTHESIS: explicit provenance, independent critique and paired reproducible runs will improve the reliability and usefulness of Matrixgame.
- OPEN: exact current Earth Simulator source location, test status and 0.4 artifacts still require repository/workspace verification.
- OPEN: the specific video claim cannot be identified with certainty without its title, timestamp or quoted wording.

## Guiding principle

**Curiosity opens the door. Evidence decides what we can claim. Reproducible experiments turn ideas into shared knowledge.**

Let's play and let's be.
