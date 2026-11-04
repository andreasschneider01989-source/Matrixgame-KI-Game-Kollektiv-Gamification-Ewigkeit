# Collective AI Participation, Accessibility and Global Reach

**Status:** Research framing and proposed roadmap (November 2026)  
**Labels:** FACT / ESTIMATE / MODEL / OPEN / DESIGN  
**Implementation status:** This document is a research/design proposal. The participation pipeline and dashboards described here are not yet implemented.

## 1. Mission

Make it increasingly easy for people worldwide to contribute a question, observation, idea, improvement proposal, data point, translation, test, or simulation result to a shared human–AI knowledge process—even if they cannot code, use English, afford premium AI, or maintain a GitHub account.

Guiding principle: **A valuable contribution should not require expert tooling; verification and implementation can be supported by the collective.**

## 2. Global participation baseline (latest sources found)

| Indicator | Estimate | Interpretation |
|---|---:|---|
| World population, 2026 mid-year | ~8.30 billion | UN WPP 2024 medium projection, reproduced by Worldometer/PopulationPyramid; model estimate, not live census |
| Internet users, 2025 | ~6.0 billion / 74% | ITU Facts & Figures 2025 |
| Offline population, 2025 | ~2.2 billion / 26% | ITU |
| Generative AI diffusion, H2 2025 | 16.3% of world population | Microsoft AI Economy Institute estimate based on aggregated anonymized telemetry and adjustments |
| Generative AI diffusion, H1 2025 | 15.1% | Same report; +1.2 percentage points in H2 |
| Global North vs South, H2 2025 | 24.7% vs 14.1% | Microsoft working-age-population measure; definitions differ from global population measure |

### Approximate scale

Applying 16.3% to a rough 8.2–8.3 billion population suggests an order of magnitude of **~1.3–1.35 billion people** who used a generative AI product during the measured period. This is a rough multiplication, not a directly reported user count, and it does **not** mean that all these people use AI in a Matrixgame-like, iterative, project-integrated way.

### What we do not currently know

No reliable global statistic was found for people who simultaneously:
- use an LLM in an iterative multi-turn way;
- bring their own long-term project or vision;
- connect conversations to code, simulation, datasets or version control;
- provide feedback and help revise outputs;
- publish reusable open-source contributions.

Therefore, do **not** present a precise “Matrixgame-style interaction percentage” as a fact. Treat it as a research gap and measure it with surveys, user studies, open participation metrics, and clearly defined tiers.

## 3. Participation ladder (proposed model, not global statistics)

1. **P0 — Offline / no reliable digital access:** paper, community facilitators, radio/community networks, offline-first tools.
2. **P1 — Connected:** internet access and basic digital literacy.
3. **P2 — AI exposed:** has tried an AI assistant or generative AI tool.
4. **P3 — Interactive:** can ask follow-up questions and refine an answer.
5. **P4 — Project-integrated:** uses AI with a persistent project, files, code, or structured knowledge.
6. **P5 — Feedback-loop contributor:** evaluates results, provides real-world feedback, and improves the model/workflow.
7. **P6 — Reproducible contributor:** submits a documented issue, dataset, test, translation, code, or simulation result.
8. **P7 — Maintainer / facilitator:** reviews, coordinates, safeguards, and helps others participate.

Do not rank human worth by tier. People can move between tiers, contribute differently, or opt out.

## 4. Barriers to participation

Measure and reduce:
- connectivity, electricity, device and data costs;
- language and literacy barriers;
- disability and accessibility barriers;
- lack of time, confidence, coding knowledge or GitHub familiarity;
- age and safeguarding needs;
- AI access limits, subscriptions, regional availability and privacy concerns;
- cultural mismatch, trust, censorship, and fear of public exposure;
- compute/energy cost and environmental footprint;
- cognitive load, attention, fatigue, and emotional overload.

Meaningful access is more than an internet connection: quality, affordability, safety, skills, and ability to benefit matter.

## 5. Inclusion-by-design roadmap

### Stage A — Lowest-friction idea intake
- Plain-language prompt: “What would you improve?”
- Voice input, short text, image/diagram, or simple form.
- Ask permission before publishing; offer anonymous or private-by-default routes where feasible.
- Automatic language detection and translation, with contributor confirmation.
- No need to know GitHub, Java, Markdown, or technical vocabulary.

### Stage B — AI-supported conversion
Transform an idea into a structured draft:
- user’s original wording;
- short summary;
- desired outcome;
- assumptions and missing information;
- proposed experiment / action;
- risk and privacy check;
- evidence and confidence;
- suggested repository / issue / quest.

Never silently overwrite the contributor’s meaning. Preserve original text and provenance.

### Stage C — Human-in-the-loop review
- User confirms the draft.
- Domain reviewer or community moderator checks it.
- AI outputs are labeled and not treated as authoritative.
- Sensitive personal data, dangerous proposals, and unsupported claims are filtered or redirected.
- Public publishing requires explicit confirmation.

### Stage D — Contribution pathways
Offer multiple routes, not only coding:
- idea / problem statement;
- source verification;
- translation;
- documentation;
- accessibility test;
- user test;
- data cleaning;
- simulation design;
- code / pull request;
- moderation / community care;
- local observation / ecological quest.

### Stage E — Return the value
- Provide a readable status: received → clarified → reviewed → tested → accepted/rejected with reason.
- Notify the contributor if they opt in.
- Credit contributors as requested.
- Make outcomes and limitations transparent.
- Allow corrections, withdrawal, and deletion requests where technically and legally possible.

## 6. Matrixgame feedback-loop model

**Personal loop (MODEL):**
State snapshot → small action → prediction → real action → subjective feedback → calibrated personal model → next suggestion.

**Collective loop (MODEL):**
Idea → clarify → classify → evidence search → proposal → review → test → merge/share → observe impact → update.

**Causal chain:**
Access → Opportunity → Interaction → Contribution → Verification → Adoption → Outcome → Feedback → Better access and tools.

Do not assume each step automatically causes the next. Measure drop-off and confounding factors.

## 7. Metrics and ethical safeguards

### Reach
- visitors / users / contributors (separate metrics);
- language and region distribution where voluntarily and safely available;
- first-time vs returning contributors;
- completion rate per participation tier;
- time from idea submission to response.

### Quality and impact
- proposals clarified and reviewed;
- reproducible tests / datasets / code;
- accepted and rejected proposals with reasons;
- user-reported usefulness;
- evidence quality and uncertainty;
- documented real-world outcomes, including unintended effects.

### Equity and accessibility
- participation by language and access mode;
- success rate for non-coders;
- accessibility audit outcomes;
- device / bandwidth constraints;
- participation gaps and reasons for dropout.

### Sustainability and safety
- energy/compute per task where measurable;
- data minimization and privacy incidents;
- moderation burden;
- harmful or misleading outputs;
- contributors’ fatigue and wellbeing;
- quality of consent and attribution.

Avoid engagement-maximization, addictive streaks, coercive rewards, or leaderboard pressure. Reward useful, verified, cooperative contributions—not time spent or number of prompts.

## 8. Monte Carlo and feedback calibration: requirements

Grok's v0.13 description is a useful design input, but the code itself was not supplied here, so its claimed tests and implementation cannot be independently verified.

For any future Monte Carlo feedback engine:
- record input variables, units, ranges, assumptions, seed, and model version;
- use baseline and sensitivity analyses;
- distinguish uncertainty in parameters from randomness in outcomes;
- evaluate forecast calibration against held-out observations;
- avoid precise-looking values unsupported by data;
- allow ABSTAIN when data are inadequate;
- do not infer neurotransmitter concentrations or medical diagnoses from mood sliders;
- keep subjective state estimates optional, private, and non-clinical;
- treat dopamine/serotonin/noradrenaline/acetylcholine as explanatory hypotheses unless linked to validated measurements;
- keep Hawkins scale as symbolic/game vocabulary, not a validated psychometric or consciousness measure.

A model “learns” from feedback only if it updates a specified parameter or model, preserves data/provenance, and improves on a test set or baseline. Merely saving history is not evidence of learning.

## 9. First MVP: Global Participation Observatory + Idea-to-Quest Pipeline

Start small and avoid collecting sensitive data.

1. Create a static dashboard using public aggregate data: population, internet access, offline share, AI diffusion, with year/source/method.
2. Add a clearly labeled “unknown / not measured” band for Matrixgame-style participation.
3. Create a non-code idea intake template.
4. Convert one approved idea into a GitHub Issue with provenance.
5. Add one accessible path for translation or documentation contribution.
6. Track funnel metrics without storing personal state or unnecessary identifiers.
7. Publish a monthly report of barriers discovered, improvements tested, and remaining uncertainty.

## 10. Open-source and privacy principles

- Use open formats: Markdown, CSV, JSON, plain text, and accessible HTML.
- Publish code and documentation under explicit repository licenses; do not assume that “public on the internet” means unrestricted reuse.
- Attribute external sources and respect licenses, terms, privacy, and copyright.
- Do not publish private chats, health details, or identifiable user data without informed consent.
- Keep personal feedback separate from public project telemetry.
- Use opt-in contribution credit and data collection.
- Provide clear correction / removal procedures.
- Prefer small, modular, reproducible components and issue-based development.

## 11. Sources

- ITU, *Facts and Figures 2025*: https://www.itu.int/en/ITU-D/Statistics/Pages/Stat/default.aspx
- ITU press release, 17 Nov 2025: https://www.itu.int/en/mediacentre/Pages/PR-2025-11-17-Facts-and-Figures.aspx
- Microsoft AI Economy Institute, *Global AI Adoption in 2025*, published 8 Jan 2026: https://www.microsoft.com/en-us/corporate-responsibility/topics/ai-economyinstitute/reports/global-ai-adoption-2025/
- UN World Population Prospects 2024, via Worldometer population series: https://www.worldometers.info/world-population/
- U.S. Census Bureau, World Population Day 2026: https://www.census.gov/newsroom/stories/world-population-day.html

## 12. Definition of success

A person with limited time, language, technical skill, money, or connectivity can still contribute meaningfully—and can understand what happened to their contribution. The system improves participation without sacrificing truthfulness, privacy, human agency, accessibility, ecological responsibility, or wellbeing.
