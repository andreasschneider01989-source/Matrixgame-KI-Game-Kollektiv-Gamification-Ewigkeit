# Research Quest: WORLD MEMORY 001

**Quest ID:** `mg.quest.world-memory-001`  
**Status:** PROPOSED  
**Mode:** EXPLORATORY  
**Created:** 2026-11-21

## Question
Can historical GDELT news-coded event time series improve next-7-day event-activity forecasts over simple time-series baselines?

## Why it matters
A reproducible benchmark can test whether global news-derived signals contain useful short-horizon information, while exposing media-coverage bias and uncertainty before any early-warning claims are made.

## Scope
- Domain: global event monitoring and forecasting
- Geography: pre-registered pilot country panel, later expanded if coverage permits
- Time period: GDELT 2.0 from 2015-02-19 onward, subject to completeness
- Horizon: next 7 complete UTC days
- Exclusions: personal profiling, individual-level prediction, causal claims from correlation, automated consequential decisions

## Hypotheses
- H1: lagged event activity improves forecasts over a zero/climatology baseline.
- H2: a rolling-mean or seasonal-naive baseline is a meaningful benchmark that complex models must beat.
- H3: source coverage and reporting volume materially affect apparent event trends.
- H4: forecast skill varies across countries, event types, and media-coverage strata.

## Data and rights
- Candidate: GDELT 2.0 Event Database and codebook.
- Record file URLs, retrieval dates, checksums, schema version, and redistribution rights before ingestion.
- Do not store copyrighted full-text articles in this repository.
- Consider GDELT 1.0 (1979 onward) a separate historical measurement era pending comparability analysis.

## Method
1. Inspect codebook and sample file schema.
2. Build a source/coverage audit and event deduplication report.
3. Define the forecast cutoff and enforce as-of availability time.
4. Aggregate distinct events by country and UTC day.
5. Establish pre-registered naive baselines.
6. Run rolling-origin 7-day backtests and retain a final untouched holdout.
7. Report MAE, RMSE, MASE, count-model log score, interval coverage, and calibration.
8. Test robustness by source/language coverage and event family.
9. Cross-check selected outcomes against independent event data where legally and methodologically appropriate.

## Acceptance criteria
- [ ] Data schema, source and rights documented.
- [ ] No future information enters features.
- [ ] Duplicates and missing coverage quantified.
- [ ] Baselines evaluated before complex models.
- [ ] Temporal holdout remains untouched until model selection is complete.
- [ ] Metrics include uncertainty and class imbalance diagnostics.
- [ ] Negative and inconclusive results are retained.
- [ ] Results serialized using the Matrixgame Research Artifact Schema.

## Current status
Design only. No forecasts or measured results have been produced.
