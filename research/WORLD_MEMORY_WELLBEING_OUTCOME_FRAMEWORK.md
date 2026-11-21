# WORLD MEMORY — Wellbeing and Positive-Impact Outcome Framework

**Artifact ID:** `mg.framework.wellbeing-outcomes-001`  
**Version:** 0.1.0  
**Status:** PROPOSED RESEARCH DESIGN  
**Claim status:** MODEL  
**Evidence level:** E1_SOURCE_LINKED  
**Purpose:** Prevent the forecasting benchmark from confusing more data, more attention, or more activity with a better future.

## 1. Core design principle

WORLD MEMORY has two connected but distinct evaluation loops:

1. **Forecast quality:** Did the model predict a defined observable outcome better than transparent baselines?
2. **Human and planetary outcomes:** Did a chosen intervention plausibly improve people's lives and preserve the resources future generations depend on?

A model can forecast news volume accurately while having no positive social impact. A policy can improve one indicator while worsening another. Neither forecast skill nor a high game score is itself proof of flourishing.

## 2. Outcome domains

Use the OECD How's Life? framework as a starting measurement map, not as a universal moral ranking. It distinguishes 11 dimensions of current wellbeing and four resources supporting future wellbeing.

### Current wellbeing (11 dimensions)
- Income and wealth
- Housing
- Work and job quality
- Health
- Knowledge and skills
- Environmental quality
- Subjective wellbeing
- Safety
- Work-life balance
- Social connections
- Civic engagement

### Distribution and fairness
For each outcome, report more than the population average where data permits:
- differences between relevant population groups;
- gaps between lower and higher outcome groups;
- deprivation / share below a meaningful threshold;
- uncertainty, missingness, and geographic coverage.

### Future wellbeing resources
Track four broad capital stocks separately:
- Natural capital
- Human capital
- Social capital
- Economic capital

Do not collapse these into one opaque "happiness score". Record units, population, geography, time period, data provenance, and known limitations for every indicator.

## 3. Two-stage causal discipline

The news forecast is an **early-observation layer**, not an outcome measure.

`NEWS_SIGNAL -> RISK_OR_OPPORTUNITY_HYPOTHESIS -> HUMAN_REVIEW -> OPTIONAL_INTERVENTION -> MEASURED_OUTCOME`

Each arrow is a separate claim that requires evidence. News-coded events may reflect media attention, source availability, translation, coding decisions, or repeated reporting rather than changes in underlying incidence.

For an intervention, define:
- target population and intended benefit;
- plausible mechanism and causal diagram;
- alternative explanations and confounders;
- potential harms, distributional effects, and who bears costs;
- counterfactual/comparison strategy where feasible;
- short-, medium-, and long-term outcome windows;
- stop, revise, and rollback conditions.

Do not infer causal impact from a before/after change alone when other explanations are plausible.

## 4. Multi-objective decision rule

Prefer Pareto improvements: an option is attractive when it improves at least one important outcome without materially worsening another protected outcome. Where trade-offs remain, show them explicitly and let affected people and accountable institutions deliberate.

A candidate decision vector can be represented as:

`D = (wellbeing, health, safety, autonomy, equity, social_connection, ecological_integrity, resilience, feasibility)`

This is a design vector, not a validated composite index. Do not sum its components without a separately justified and publicly reviewable weighting method.

Apply non-negotiable guardrails:
- protect basic rights, safety, consent, and human agency;
- do not sacrifice a minority's basic wellbeing for a small average gain;
- avoid irreversible ecological damage;
- disclose uncertainty and distribution of costs/benefits;
- retain a no-action and a reversible-pilot option.

## 5. Responsible gamification

Reward contribution quality, not volume or emotional intensity.

Positive rewards may include:
- reproducible findings and useful negative results;
- independent replication and correction of errors;
- clearer explanations and accessible translations;
- collaboration across disciplines and regions;
- interventions evaluated against outcomes;
- improvements that reduce resource use or remove participation barriers.

Never reward:
- sensationalism, outrage, fear, or conflict escalation;
- speculative certainty or unverified causal claims;
- collecting unnecessary personal data;
- maximizing screen time, compulsive return, or public ranking of personal worth;
- model confidence without calibration.

Make quests voluntary, accessible, low-cost, pauseable, and completable without public disclosure. A participant can choose private learning mode.

## 6. Evaluation horizons

Use multiple horizons rather than optimizing only the easiest short-term metric:

| Horizon | Example evaluation |
|---|---|
| Immediate (hours–weeks) | Safety, usability, access, unintended effects |
| Short (1–12 months) | Learning, service access, trust, health behaviours, local environmental measures |
| Medium (1–5 years) | Health, housing, work quality, social connection, resilience |
| Long (5–25+ years) | Ecosystem condition, education, inequality, institutional trust, intergenerational resources |

The horizons are planning categories, not claims that every outcome changes on those schedules. Choose windows appropriate to the intervention and available evidence.

## 7. Suggested experiment record fields

Use the existing Research Artifact envelope. Store this framework-specific data under a versioned `extensions.world_memory_outcomes` object:
- `indicator_id`, definition, unit, directionality;
- population, geography, period, collection method;
- source and license/rights status;
- baseline and comparison/counterfactual;
- distributional breakdowns and missingness;
- expected mechanism and confounders;
- observed outcome, uncertainty interval, and adverse effects;
- evaluator, review status, and follow-up date.

This keeps the general artifact schema stable while allowing the outcome framework to evolve independently.

## 8. Initial practical pilot

Do not begin with a grand global happiness score. Choose one modest, locally measurable and ethically low-risk use case after the event-data pipeline is validated. Examples for later co-design could include improving access to reliable heatwave information, mapping public cooling resources, or making a local learning resource easier to use.

A pilot must be selected with affected participants, not imposed on them. It should measure both intended benefit and possible harms, and it must be possible to stop it.

## 9. Sources

- OECD, How's Life? 2024 — current wellbeing, inequality, and sustainability: https://www.oecd.org/en/publications/2024/11/how-s-life-2024_bdcf2f9f/full-report/component-5.html
- OECD How's Life? Well-being Database definitions and metadata (updated October 2026): https://www.oecd.org/content/dam/oecd/en/topics/policy-sub-issues/measuring-well-being-and-progress/oecd-well-being-database-definitions.pdf
- OECD Guidebook on Best Practices in Public Health — complementary wellbeing outcomes and long evaluation windows: https://www.oecd.org/content/dam/oecd/en/publications/reports/2022/03/guidebook-on-best-practices-in-public-health_de8dbd33/4f4913dd-en.pdf

## 10. Current limitations

This is a measurement and governance design, not an implemented wellbeing dashboard, clinical instrument, causal estimator, or validated global utility function. The OECD framework is a useful reference, not a complete account of every culture's values. Indicators should be adapted transparently with local communities and disaggregated only where privacy and sample quality permit.
