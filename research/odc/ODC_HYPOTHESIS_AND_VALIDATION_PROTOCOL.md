# ODC — Opto-Density Convergence: scientific audit and testable research protocol

**Status:** RESEARCH / HYPOTHESIS UNDER TEST — not an established axiom  
**Created:** 2026-11-09  
**Scope:** relation between refractive index, mass density, composition, wavelength and temperature.

## Executive finding

The useful core is real: for a *fixed material/composition* and controlled optical/thermodynamic conditions, refractive index is often correlated with density. The Lorentz–Lorenz (equivalently Clausius–Mossotti, under the corresponding assumptions) relation provides a physical bridge between macroscopic refractivity and microscopic number density and polarizability. In the dilute-gas limit, Gladstone–Dale is a useful approximation.

The stronger claim in the supplied Qwen text is **not validated**:

> “The refractive index is a deterministic function of mass density alone, and historical measurements converge to an exact density-based ideal as technology improves.”

Density alone is generally insufficient. Composition and molecular/atomic polarizability matter; refractive index is dispersive (depends on wavelength) and can depend on temperature, pressure, phase, and interactions. Better metrology reduces measurement uncertainty, but does not imply that older measured central values must move monotonically toward a single density-only prediction. A discrepancy may be due to sample composition, wavelength, temperature, calibration, model inadequacy, or systematic error.

## Evidence classes

### FACT — established physical relationships

1. **Lorentz–Lorenz form** (one common mass-density form):
   \[
   \frac{n^2-1}{n^2+2}
   =\frac{N_A\,\alpha(\lambda,T)}{3\epsilon_0 M}\,\rho
   \]
   for a material with molar mass \(M\), mass density \(\rho\), and polarizability \(\alpha\), with conventions/units kept consistent. The relation makes the dependency explicit: density is multiplied by a material- and condition-dependent polarizability-to-molar-mass factor. It is not a universal density-only law.

2. **Gladstone–Dale**:
   \[
   n-1\approx K(\lambda,T,\text{composition})\,\rho
   \]
   This is a useful approximation in appropriate regimes, notably gases and small refractivity. The constant \(K\) is not universal across substances or wavelengths.

3. **Dispersion and state dependence:** the refractive index varies with wavelength and can vary with temperature and density. Mixtures require composition-aware treatment. For dense media, local-field effects and intermolecular interactions can require corrections beyond a simple proportional law.

4. **GRIN optics:** graded-index elements guide light through a spatial refractive-index profile. In doped silica, dopant concentration (e.g. GeO₂) changes refractive index. This is not evidence that mass density alone controls the profile: composition/polarizability is central, and any density change must be measured separately.

### HYPOTHESIS — narrowed, falsifiable ODC claim

For a specified homogeneous material/composition, phase, wavelength and temperature range, the Lorentz–Lorenz specific refractivity
\[
S=\frac{1}{\rho}\frac{n^2-1}{n^2+2}
\]
will be approximately constant over a stated density interval, within quantified uncertainty and any required non-ideal correction terms.

This is a useful, testable version of ODC. It does **not** assert universality across materials.

### OPEN — historical-to-modern convergence

The claim that newer refractive-index measurements systematically converge toward a density-derived ideal is currently unsupported by the supplied text. To test it, one needs traceable datasets with:
- same material composition and sample provenance (or demonstrably equivalent samples);
- wavelength, temperature, pressure and phase recorded;
- density and refractive index measured independently;
- uncertainty budgets and calibration traceability;
- documented method changes and possible systematic effects.

Compare residuals against a physically specified model, not simply “old versus new”. Report uncertainty-weighted residuals, bias, repeatability and model discrepancy. A newer result is not automatically more accurate solely because it is newer.

## GRIN correction

A GRIN lens/fiber has a designed spatial index profile \(n(r)\). In common Ge-doped silica designs, changing Ge concentration changes the refractive index profile. This supports a composition–optical-response mechanism. It does not establish a universal density-to-index law or a gravitational control mechanism. Gravity is not the “direct controller” in this optical-fiber example.

## Proposed validation protocol

1. Select one material and composition (e.g. high-purity fused silica; do not mix different glass grades).
2. Fix wavelength, temperature, pressure and phase; record all values and units.
3. Obtain independently measured density and refractive-index values with uncertainty and provenance.
4. Choose a reference state \((\rho_0,n_0)\) for the same composition and conditions.
5. Calculate the Lorentz–Lorenz prediction at each density under the constant-specific-refractivity approximation:
   \[
   q_0=\frac{n_0^2-1}{n_0^2+2},\quad
   q(\rho)=q_0\frac{\rho}{\rho_0},\quad
   n_{pred}=\sqrt{\frac{1+2q}{1-q}}.
   \]
6. Compare \(n_{meas}-n_{pred}\) with propagated measurement uncertainty and model discrepancy. Do not interpret a small residual as proof of a universal law.
7. Repeat across wavelengths, temperatures and compositions as separate strata. Explicitly test whether the constant-specific-refractivity assumption fails.

## Data schema

CSV columns:
- `sample_id` — unique sample identifier
- `material`, `composition_id` — composition must be explicit
- `wavelength_nm`, `temperature_C`, `pressure_kPa`
- `density_kg_m3`, `density_uncertainty_kg_m3`
- `n_measured`, `n_uncertainty`
- `reference_density_kg_m3`, `reference_n`
- `source`, `measurement_year`, `notes`

No synthetic values should be presented as empirical measurements. The accompanying analyzer accepts a smaller minimum schema for exploratory calculations and labels outputs as model predictions.

## Acceptance criteria

- Input validation rejects non-positive densities, invalid refractive indices and missing required metadata.
- Predictions are only interpreted within the same material/composition and matched optical/thermodynamic conditions.
- The report states residuals and uncertainty; it does not declare “proof” from correlation.
- Historical convergence is not claimed unless a traceable, comparable multi-era dataset demonstrates it.
- Every dataset is preserved with source, version, units and checksum in a later data-ingestion stage.

## Matrixgame integration

ODC is a **testable model module**, not a sacred axiom:
- FACT: the equations and their stated assumptions.
- MODEL: constant-specific-refractivity prediction.
- HYPOTHESIS: approximate invariance for one material under controlled conditions.
- OPEN: historical convergence and broader universality.
- REJECT: density-only universality; GRIN as proof of density-only control.

Principle: *The model earns trust by surviving attempts to falsify it.*

## References

1. NASA Langley, Burner & Goad, “Gladstone-Dale constant for CF₄”, NASA-TM-80228 (1980): https://ntrs.nasa.gov/citations/19800015659
2. RSC, “Volumetric Properties: Introduction, Concepts and Selected Applications” (Lorentz–Lorenz, density expansion and polarizability): https://books.rsc.org/books/edited-volume/1309/chapter/944522/Volumetric-Properties-Introduction-Concepts-and
3. Kozyreva, TU Darmstadt dissertation, “Creation of High Energy Density in Matter” (discussion of Lorentz–Lorenz and limitations for liquids/solids): https://tuprints.ulb.tu-darmstadt.de/436/1/disser_kozyreva.pdf
4. Reisler, Eisenberg & Minton (1972), “Temperature and density dependence of the refractive index of pure liquids”, DOI: https://doi.org/10.1039/F29726801001
5. Noel Sekiei, “Integrated GRIN Lenses” (dopant concentration and refractive-index profile): https://www.noel-sekiei.co.jp/en/technology/grin_lenses/
6. Roser, University of Bonn thesis, “Optical Fibers” (graded-index fiber profile): https://quantum-technologies.iap.uni-bonn.de/assets/pdf/thesis/master/master_david_roser.pdf

These references support the relationships and caveats above; they do not validate the unqualified density-only or historical-convergence claims.
