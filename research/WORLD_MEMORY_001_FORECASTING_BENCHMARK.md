# WORLD MEMORY 001 — Historical Event Forecasting Benchmark

**Artifact ID:** `mg.experiment.world-memory-001`  
**Version:** 0.1.0  
**Created:** 2026-11-21  
**Status:** EXPERIMENT DESIGN — no empirical forecast result claimed  
**Mode:** EXPLORATORY  
**Claim kind:** MODEL / HYPOTHESIS  
**Evidence level:** E1_SOURCE_LINKED

## 1. Research question

Can time-ordered, country-level aggregates of news-coded events provide useful short-horizon forecasts of subsequent news-coded event activity, beyond simple historical baselines?

This first benchmark tests forecasting skill on a defined observation stream. It does **not** yet claim to predict all real-world events, causal mechanisms, conflict onset, or human wellbeing.

## 2. Data source decision

### Primary candidate: GDELT 2.0 Event Database

The GDELT Project documents the 2.0 event and Global Knowledge Graph streams as updating every 15 minutes. Event 2.0 includes a Mentions table and machine-translated reporting across 65 languages. Its published 2.0 codebook describes event records, actor/action codes, geolocation, Goldstein scores, and mention-level confidence/prominence fields.

- Project data and documentation: https://gdeltproject.org/data.html
- Event 2.0 codebook: https://data.gdeltproject.org/documentation/GDELT-Event_Codebook-V2.0.pdf
- GDELT 1.0 historical daily data: https://gdeltproject.org/data.html

GDELT 1.0 reaches back to 1979, but its file granularity and date semantics change over time: the project documents monthly/yearly files through 2013-03-31, then daily files from 2013-04-01. For the later daily files, records are organized by the date the event was found in news media rather than necessarily the date the event occurred. It is therefore a possible separate long-horizon historical series, but not a homogeneous daily panel back to 1979. Do **not** concatenate 1.0 and 2.0 as if measurement were identical. Treat them as separate dataset eras and require a bridge/overlap study before any combined model.

### Important source limitations

GDELT measures what its monitored news sources report and what its automated pipeline extracts/codes. It is not a census of all real-world events. OECD's 2026 review notes potential automated coding accuracy limitations and under-reporting in media-poor regions, alongside source and tone biases: https://www.oecd.org/en/publications/migration-anticipation-and-preparedness_4161131f-en/full-report/what-data-are-necessary-to-conduct-the-forecasting-exercise_a77624c7.html

A 2025 peer-reviewed assessment reports field accuracy and redundancy concerns and recommends correction/deduplication: https://doi.org/10.3390/data10100158

Therefore:
- describe targets as **news-coded / media-observed event counts**;
- deduplicate by stable event identifier, not by raw mention count;
- preserve source coverage and language mix as diagnostic variables;
- do not compare country counts without exposure/coverage normalization;
- keep unknown values distinct from zero;
- inspect samples manually and cross-check important findings independently.

## 3. Initial bounded task

**Target:** next-7-day country-level count of distinct GDELT 2.0 events, with a secondary target for the count/share of events in pre-registered CAMEO event families.

**Forecast issue time:** one fixed UTC cutoff per day or week.

**Prediction horizon:** the following 7 complete UTC days.

**Pilot geography:** select a small, pre-registered set of countries with differing media ecosystems for pipeline debugging. The pilot is not a representative global sample. Expand to a global panel only after coverage diagnostics.

**Historical window:** begin with 2015-02-19 onward, subject to actual file availability, schema consistency, and data completeness. Use a fixed end date that excludes incomplete recent days.

## 4. Critical anti-leakage rule

A forecast at cutoff T may use only records demonstrably available by T.

- Preserve both event date and first-seen/record-availability time where supplied.
- A historical event reported after T must not appear in features at T.
- Use archived 15-minute snapshots or another defensible as-of reconstruction. If this cannot be done, explicitly label the test as retrospective event-date forecasting, not a real-time backtest.
- Do not use future revisions, future mention counts, or article information published after T.
- Freeze the event taxonomy and feature transformations per experiment version.

This is essential: using only event date while ignoring when the report entered the data stream can create look-ahead leakage.

## 5. Variables and feature families

### Outcomes
- `event_count_7d`: distinct event records in the following 7 days.
- `cameo_family_count_7d`: counts for pre-registered event families.
- Optional `negative_goldstein_share_7d`: share of events with negative Goldstein score; analyze separately and do not treat it as a direct measure of societal harm.

### Candidate predictors (available at cutoff only)
- Lagged daily/weekly event counts (1, 7, 14, 28, 56 days).
- Rolling means, medians, volatility, and trend slopes.
- Event-family composition and actor diversity.
- Number of distinct reporting sources and source concentration.
- Mention volume and confidence/prominence only if available by cutoff.
- Coverage indicators: source counts, language mix, missingness, feed outages.
- Calendar effects and country-level context from independently versioned open datasets.

Keep raw counts and coverage-adjusted measures separate. Do not let news volume become a proxy for population risk without validation.

## 6. Baselines before complex AI

Evaluate at least:
1. Zero / climatology baseline where appropriate.
2. Last-observed-week baseline.
3. Seasonal-naive baseline (same weekday/week-of-year, where data supports it).
4. Rolling 4-week mean baseline.
5. Regularized count model (Poisson or negative binomial).
6. Only then, tree-based or sequence models.

All model comparisons use identical cutoffs, target definitions, and test windows.

## 7. Backtesting protocol

Use rolling-origin evaluation:
- train on the past only;
- forecast the next 7 days;
- move the cutoff forward;
- repeat across multiple years and periods;
- retain a final untouched holdout period;
- report results by country, event family, and coverage stratum.

Pre-register cutoffs, target definitions, exclusions, and model choices before viewing holdout results. Include periods of crisis and non-crisis, and report performance degradation under distribution shift.

## 8. Metrics

For counts:
- MAE and RMSE;
- MASE against a named naive baseline;
- Poisson deviance or negative-binomial log score where appropriate;
- prediction-interval coverage and width.

For binary alerts derived from a pre-registered threshold:
- precision, recall, PR-AUC;
- Brier score and reliability/calibration plots;
- false alarms and missed events per country-period.

Never report raw accuracy alone for rare events. Report base rates and confusion matrices. Compare calibrated probabilities, not only rankings.

## 9. Causal discovery boundary

Predictive association is not causal explanation.

Any proposed causal chain must:
- state a directed causal hypothesis and time order;
- identify plausible confounders and selection effects;
- distinguish media attention from underlying event incidence;
- be tested against alternative explanations;
- use independent evidence or a defensible quasi-experimental design before causal language is used.

Use labels: FACT / MODEL / HYPOTHESIS / OPEN / FICTION.

## 10. Independent validation

After the first GDELT-only benchmark is stable, compare selected domains with independent event datasets (for example, ACLED or UCDP where their terms, access conditions, and coverage permit). This tests whether a signal generalizes beyond one media-coding pipeline. Independent sources have their own definitions, coverage and licensing constraints; do not assume direct equivalence.

## 11. Reproducibility manifest

Each run records:
- dataset source, download time, file list, checksums, schema/codebook version;
- event date and first-seen/availability-time handling;
- filters, country codes, event taxonomy, deduplication key;
- cutoff schedule, horizon, train/validation/holdout ranges;
- feature code commit, environment and dependency versions;
- model family, parameters, random seed;
- baselines, metrics, uncertainty intervals;
- data coverage/missingness and excluded records;
- result artifact IDs and reviewer notes.

Do not commit large raw news datasets or copyrighted article text. Store metadata, scripts, small lawful fixtures, and reproducible download instructions.

## 12. Promotion criteria

Promote a finding beyond exploratory status only if:
- the as-of backtest is leakage-audited;
- results beat pre-registered baselines on the untouched holdout;
- calibration and subgroup/coverage diagnostics are acceptable;
- the finding is robust to reasonable deduplication and source filters;
- independent data or expert review supports the interpretation;
- limitations and failure cases are public.

## 13. First deliverable

A small pipeline that:
1. obtains a legally reusable, documented sample or aggregate;
2. creates daily country-level time series;
3. produces naive 7-day forecasts;
4. performs rolling-origin evaluation;
5. emits a JSON research artifact and a compact Markdown report.

**Current result:** design only. No data download, forecast, causal discovery, or predictive performance measurement has yet been completed.
