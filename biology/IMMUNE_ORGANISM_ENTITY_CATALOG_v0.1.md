# Matrixgame Biology — Immune & Human Organism Entity Catalog v0.1

**Date:** 10 November 2026  
**Status:** research-backed model specification; not a medical model and not yet a validated simulation  
**Target:** language-neutral ontology, then Java 21 / JSON / Python / JavaScript adapters

## Executive decision

The proposed direction is sound. Do **not** represent the entire human organism as one enormous vector or one flat list of "units". Use a layered, typed model:

1. **Entities:** cells, microorganisms, viruses, molecules, tissue structures and compartments.
2. **State:** measurable or explicitly normalized properties of each entity.
3. **Capabilities:** actions an entity type is allowed to perform.
4. **Interactions:** typed events connecting source and target entities.
5. **Fields and resources:** oxygen, nutrients, pH, temperature, chemical signals, pressure and extracellular matrix.
6. **Run provenance:** model version, parameters, seed, time step, data source and uncertainty.

The Human Reference Atlas 2026 reports roughly 27–37 trillion human cells and up to 10,000 cell types, with reference data over 73 organs and 1,283 anatomical structures. That makes a literal exhaustive hand-written list impractical and scientifically unstable. This catalog is a functional coverage map, not a claim to enumerate every known cell subtype. See the 2026 HRA population atlas and NIH overview [1,2].

## 1. Epistemic and implementation labels

Keep two orthogonal status systems.

### Scientific status
- FACT: supported by established observations or a cited source.
- MODEL: an explicit mathematical simplification.
- HYPOTHESIS: a falsifiable proposal not yet established.
- OPEN: missing evidence or unresolved interpretation.
- FICTION: story/game-only mechanics.

### Implementation status
- PLANNED, PROTOTYPE, IMPLEMENTED, TESTED, VERIFIED.

A measured velocity from one microfluidic assay is not a universal cell constant. A normalized value such as activation01 = 0.7 is not a biological concentration. Each parameter must also carry a provenance class:

- MEASURED_REFERENCE: published measurement with context and unit.
- ASSAY_SPECIFIC: measurement only valid in the stated assay and stimulus.
- MODEL_START: provisional numerical starting point for a toy model.
- UNCALIBRATED: no defensible universal value yet.
- USER_SCENARIO: explicitly chosen scenario input.

## 2. Entity inventory — functional coverage

### 2.1 Hematopoietic origin and blood

- Hematopoietic stem cells (HSC); multipotent progenitors; common myeloid progenitor; common lymphoid progenitor.
- Myeloid precursors: granulocyte/monocyte progenitors, megakaryocyte progenitors, erythroid progenitors.
- Lymphoid precursors and developing B/T/NK lineages.
- Erythrocytes / red blood cells (RBC): hemoglobin-based oxygen and CO₂ transport; not immune cells.
- Reticulocytes: immature circulating red cells.
- Platelets / thrombocytes: hemostasis and immune/inflammatory signaling; cell fragments rather than full nucleated cells.
- Plasma: fluid transport compartment carrying proteins, antibodies, complement, electrolytes, nutrients, hormones and metabolites.

### 2.2 Innate immune cells

- Neutrophils: rapid recruitment, phagocytosis, granule release, reactive oxygen species, NET formation; can also damage host tissue.
- Monocytes: classical, intermediate and non-classical subsets; circulation, recruitment and differentiation.
- Macrophages: tissue-resident and recruited populations; phagocytosis, sensing, cytokines, repair, antigen presentation. Tissue-resident examples include Kupffer cells (liver), alveolar macrophages (lung), microglia (CNS; specialized resident macrophage lineage), and other organ-specific macrophages.
- Conventional dendritic cells: cDC1 and cDC2; antigen uptake/presentation and T-cell priming.
- Plasmacytoid dendritic cells: strong type-I interferon response in appropriate viral contexts.
- Langerhans cells: specialized skin-resident antigen-presenting cells.
- Mast cells: tissue-resident, granule-mediated mediator release, vascular and allergic responses.
- Eosinophils: helminth/parasite defense and allergic inflammation.
- Basophils: rare circulating granulocytes contributing to type-2/allergic responses.
- Natural killer cells (NK): innate cytotoxicity and cytokine release.
- Innate lymphoid cells (ILC1, ILC2, ILC3): tissue-associated cytokine functions, without rearranged antigen-specific receptors like conventional T/B cells.

### 2.3 Adaptive immune cells

- B cells: transitional, naïve, follicular, marginal-zone, B-1-like, germinal-center, memory B cells.
- Plasmablasts and plasma cells: antibody secretion.
- CD4 T cells: naïve, Th1, Th2, Th17, T follicular helper (Tfh), regulatory T cells (Treg), and memory subsets.
- CD8 cytotoxic T cells: antigen-specific recognition and killing of infected or abnormal host cells.
- T-cell memory: central memory, effector memory and tissue-resident memory subsets.
- γδ T cells, MAIT cells and NKT cells: specialized T-cell populations with distinct recognition biology.
- Regulatory and tolerogenic populations: Treg cells and regulatory antigen-presenting-cell states; do not reduce immune regulation to a single "peace" variable.

### 2.4 Non-immune host cells that matter to immunity

- Epithelial cells: skin, airway, gut and urogenital barriers; mucus, antimicrobial peptides, tight junctions and local sensing.
- Endothelial cells: vascular barrier, adhesion molecules, leukocyte rolling/adhesion/transmigration and perfusion.
- Fibroblasts and stromal cells: extracellular matrix, tissue repair and chemokine niches.
- Lymph-node stromal cells: fibroblastic reticular cells and follicular dendritic cells (FDCs). FDCs are stromal cells, not conventional hematopoietic dendritic cells.
- Alveolar type-I and type-II epithelial cells: lung gas-exchange barrier and surfactant production.
- Hepatocytes: metabolism, plasma-protein production and systemic inflammatory response.
- Renal tubular and glomerular cells: filtration, electrolyte/water balance and metabolic homeostasis.
- Keratinocytes, melanocytes and skin appendage cells.
- Enterocytes, goblet cells, Paneth cells, enteroendocrine cells and gut-associated stromal/immune cells.
- Cardiomyocytes, vascular smooth-muscle cells and pericytes.
- Skeletal and smooth muscle cells.
- Neurons, astrocytes, oligodendrocytes, microglia and ependymal cells.
- Adipocytes; osteoblasts, osteocytes and osteoclasts; chondrocytes; endocrine and reproductive cell types.

For a broad organism atlas, source labels from HRA, HuBMAP, Tabula Sapiens and the Allen Human Immune Health Atlas rather than freezing the project to this hand-curated list [1–4].

### 2.5 Microbes and infectious agents

Keep these as distinct entity kinds:
- Bacteria: cellular prokaryotes with their own metabolism; species/strain-specific replication and susceptibility.
- Archaea: cellular prokaryotes, distinct from bacteria; include as microbiome participants when relevant.
- Fungi: yeasts, hyphae and multicellular mycelial forms; own metabolism and growth.
- Protozoa: unicellular eukaryotic parasites.
- Helminths: multicellular parasitic worms; model life stages separately if required.
- Viruses: acellular infectious particles; no autonomous complete metabolism; replicate only after entering suitable host cells.
- Viroids/prions: special non-cellular infectious entities; defer from MVP unless a specific learning scenario needs them.
- Toxins and allergens: molecules, not organisms.
- Tumor cells: altered host cells, not pathogens; model altered growth, immune recognition/escape and tissue effects separately.

### 2.6 Molecules, particles and extracellular structures

**Energy and metabolism:** ATP, ADP, AMP, glucose, glycogen, lactate, pyruvate, glutamine, glutamate, fatty acids, amino acids, acetyl-CoA, NAD(H), NADP(H), oxygen (O₂), carbon dioxide (CO₂), water, protons and key ions (Na⁺, K⁺, Ca²⁺, Cl⁻, Mg²⁺, phosphate, bicarbonate).

**Immune mediators:** cytokines (e.g. IL-1β, IL-2, IL-4, IL-6, IL-10, IL-12, IL-17, TNF, IFN-γ, type-I interferons, TGF-β); chemokines (e.g. CXCL8/IL-8, CCL2, CCL19/CCL21, CXCL9/10/11); lipid mediators (prostaglandins, leukotrienes, resolvins); histamine; nitric oxide; reactive oxygen species.

**Recognition and presentation:** antigen epitopes, peptides, MHC-I, MHC-II, T-cell receptors (TCR), B-cell receptors (BCR), antibodies (IgM, IgG subclasses, IgA, IgE, IgD), Fc receptors, complement receptors, pattern-recognition receptors (TLR, NLR, RLR and other families), co-stimulatory and inhibitory ligands.

**Complement:** C1q, C2, C3, C4, C5–C9, C3a, C3b, C5a, factor B, factor D, factor H, factor I and host-protective regulators. C3b is an opsonin; C3a/C5a are inflammatory mediators (C5a is strongly chemotactic); C5b–C9 forms the membrane-attack complex. Regulatory proteins protect host tissue [5].

**Coagulation and repair:** fibrinogen, fibrin, thrombin, von Willebrand factor, platelet granules, collagen, elastin, fibronectin, laminins, proteases and matrix metalloproteinases.

**Transport and extracellular objects:** albumin, lipoproteins, extracellular vesicles/exosomes, mucus, surfactant, extracellular DNA/NET material, antibodies, antigen–antibody complexes and cell debris.

## 3. Common entity envelope and typed state vectors

Do not put all biological attributes in one untyped numeric array. Use a shared envelope plus a type-specific payload.

### 3.1 Common identity / location envelope

| Field | Type | Unit / meaning |
|---|---|---|
| entityId | string | Stable unique ID |
| entityKind | enum | CELL, MICROBE, VIRION, MOLECULE, TISSUE, VESSEL, FIELD, RESOURCE |
| typeId | string | E.g. CELL.IMMUNE.NEUTROPHIL |
| speciesId | string | Default HUMAN.HOMO_SAPIENS; separate microbes by species/strain |
| compartmentId | string | BLOOD, LYMPH, LUNG, GUT, SKIN, INTERSTITIUM, LYMPH_NODE, etc. |
| positionUm | vector3 | x/y/z in micrometres; local tissue coordinates |
| velocityUmPerMin | vector3 | spatial velocity; only if movement is modeled |
| radiusUm | number/null | micrometres; calibrated per type |
| volumeFl | number/null | femtolitres; optional |
| createdAtMin, ageMin | number | simulated minutes |
| lifecycleState | enum | DEVELOPING, RESTING, ACTIVATED, MIGRATING, INTERACTING, DIVIDING, DEAD, CLEARED |
| viability01 | float | normalized model state 0–1, not a clinical measurement |
| modelProfileId | string | versioned parameter profile |

### 3.2 CellState vector — shared biological state

CellState should include:
- activation01, stress01, damage01, differentiation01;
- energyReserve01 (normalized model store; do not call it ATP concentration);
- oxygenPO2mmHg and glucoseMm only if the selected model includes local chemistry;
- atpProductionFmolPerMin, atpDemandFmolPerMin only when calibrated to cell type and assay;
- cellCyclePhase, divisionRatePerHour, deathHazardPerHour;
- receptorExpression, ligandExpression, surfaceMarkers as typed, assay-provenanced measurements;
- secretoryRates keyed by molecule ID and unit;
- antigenLoad, antigenPresentationState, memoryLinks where biologically applicable.

Normalized 0–1 values are convenient for a teaching simulation, but must be labelled MODEL_START, not measured biology.

### 3.3 ImmuneCellProfile — capabilities and role parameters

Suggested fields:
- chemotaxisSpeedUmPerMin
- chemotacticPersistence01
- chemokineSensitivity (assay-specific or normalized)
- adhesionStrength01
- rollingProbabilityPerMin, transmigrationProbabilityPerMin (uncalibrated until assay data are selected)
- phagocytosisEventsPerMin and phagocytosisSuccessProbability
- antigenUptakeCapacity, antigenPresentationCapacity
- cytotoxicEventsPerMin, targetRecognitionProbability
- cytokineReleaseRates
- granuleReleaseRate, ROSProductionRate, NETFormationProbability
- proliferationRatePerHour, apoptosisHazardPerHour
- memoryStrength01, memoryDecayRatePerDay (for memory populations)
- metabolicProfileId (GLYCOLYTIC_EFFECTOR, OXPHOS_MEMORY, MIXED, etc.; profiles are tendencies, not rigid cell identities).

Do not assign all capabilities to all cell types. Enforce capability interfaces so a red blood cell cannot call presentAntigen(), and a virion cannot call consumeGlucose() as an autonomous cell.

## 4. Starter behavior profiles and defensible values

The following values are reference anchors, not universal constants. Counts vary with age, body size, tissue, health, infection, assay and sampling method.

### 4.1 Whole-body immune-cell estimates

A PNAS quantitative estimate places total immune cells at about 1.8 × 10¹² (95% CI about 1.5–2.3 × 10¹²). Example estimated body-wide populations: T cells ~5 × 10¹¹; B cells ~3 × 10¹¹; neutrophils ~7 × 10¹¹; macrophages ~2 × 10¹¹; mast cells ~9 × 10¹⁰; dendritic cells ~5 × 10¹⁰; eosinophils ~4 × 10¹⁰; NK cells ~2 × 10¹⁰; plasma cells ~2 × 10¹⁰; monocytes ~3 × 10¹⁰; basophils ~2 × 10⁹ [6].

These are whole-body estimates, not counts per microlitre of blood, and must not be used as literal agent counts in a small spatial simulation. Use them as optional scaling targets or for aggregate compartments.

### 4.2 Blood reference counts

A commonly used adult blood reference appendix lists total leukocytes around 5,000–10,000 per mm³, neutrophils 2,500–6,000, lymphocytes 1,000–4,000, monocytes 200–800, eosinophils 50–300, basophils 0–100, and platelets 200,000–500,000 per mm³ [7]. Reference intervals differ by laboratory, population, age, sex and method. Do not translate these directly into a tissue population.

### 4.3 Motility values with assay context

- Human neutrophils: microfluidic chemotaxis assays report typical speeds spanning roughly 1–20 µm/min; one healthy-volunteer fMLP assay reported an average of 18 µm/min. Another maze assay measured 15.2 ± 4.4 µm/min for fMLF and 27.4 ± 5.0 µm/min for LTB4 [8–10].
- Macrophages: a review summarizes about 1 µm/min in vitro; higher values in wound models are context- and species-dependent, so do not transfer fish wound values to human tissue [11].
- Other immune-cell velocities: leave UNCALIBRATED until selecting a specific human tissue, assay and stimulus. Published lymphocyte velocity in a mouse lymph node is not a universal human speed.

For a first spatial toy model, use a scenario parameter, not a claim of universal physiology:
- neutrophil chemotaxisSpeedUmPerMin = 18 with provenance ASSAY_SPECIFIC (fMLP healthy-volunteer benchmark);
- macrophage chemotaxisSpeedUmPerMin = 1 with provenance ASSAY_SPECIFIC (in-vitro review summary);
- all other speed defaults are null / UNCALIBRATED until a matching reference is selected.

### 4.4 Neutrophil turnover

A review of human phagocyte kinetics summarizes blood neutrophil half-life estimates around 13–19 hours; it also distinguishes the preceding bone-marrow transit time (about 5.8 days after last proliferation) from time spent in circulation [12]. For the model, split maturationTransitHours and bloodHalfLifeHours; do not use one universal "cell lifespan" field.

### 4.5 Metabolism

Immune-cell metabolism changes with activation and differentiation. Effector T cells and inflammatory macrophage states often increase glycolytic flux for rapid ATP and biosynthetic needs; memory T cells and some regulatory/repair-associated states rely more on oxidative phosphorylation and fatty-acid oxidation. These are tendencies and context-dependent states, not immutable M1/M2 or cell-type rules [13,14].

**Recommended MVP:** use energyReserve01 and metabolicDemand01 as explicit normalized MODEL variables. Keep ATP, oxygen, glucose and lactate concentrations in physical units only when the model includes a defined compartment, measurement source and mass balance.

## 5. Biological functions as capability interfaces

Use small interfaces/traits, not a giant universal method list.

| Capability | Function name | Applicable entity kinds |
|---|---|---|
| Environmental sensing | senseField(fieldId) | cells, microbes, receptors |
| Receptor binding | bindLigand(targetId, receptorId) | cells, antibodies, molecules |
| Chemotaxis | moveAlongGradient(fieldId, dt) | motile cells |
| Adhesion | adhereTo(surfaceId) | leukocytes, platelets, epithelial/endothelial |
| Transmigration | crossBarrier(barrierId) | leukocytes |
| Phagocytosis | engulf(targetId) | neutrophils, macrophages, some DCs |
| Antigen processing | processAntigen(targetId) | APCs |
| Antigen presentation | presentPeptide(mhcClass, peptideId) | APCs |
| Co-stimulation | provideCostimulation(targetId) | activated APCs |
| Cytotoxicity | killTarget(targetId, mechanism) | CD8 T, NK, eosinophil in specific contexts |
| Secretion | secrete(moleculeId, rate, dt) | many cell types |
| Complement cleavage/deposition | activateComplement(pathway, surfaceId) | plasma proteins / cascade |
| Proliferation | divide(cloneRule) | progenitors, activated lymphocytes, microbes |
| Differentiation | differentiate(targetState) | stem/progenitor/immune cells |
| Apoptosis | initiateApoptosis(reason) | host cells and immune cells |
| Repair | repairMatrix(targetTissue, rate) | fibroblasts, epithelial cells, stromal cells |
| Oxygen transport | loadOxygen(), unloadOxygen(target) | RBC / hemoglobin |
| Resource uptake | consume(resourceId, amount) | living cells, microbes |
| Waste release | releaseMetabolite(moleculeId, amount) | living cells |
| Viral entry/replication | enterHostCell(), replicateUsingHost() | virions, susceptible host cells |
| Microbial competition | competeForNiche(resourceId) | bacteria, fungi, archaea, microbiota |

Functions should emit typed events; they should not directly mutate unrelated entities. The engine validates the event and then applies it atomically.

## 6. Interaction and event catalog

Each interaction record should include:
eventId, timeMin, sourceEntityId, targetEntityId, interactionType, compartmentId, locationUm, signalId, magnitude, unit, successState, resourceCost, damageToTarget, damageToHost, evidenceRef, epistemicStatus.

Initial interaction types:
1. CHEMOTACTIC_SIGNAL: gradient draws a responding cell.
2. ROLLING_AND_ADHESION: selectin/integrin-dependent vascular interaction.
3. TRANSMIGRATION: leukocyte crosses endothelium.
4. PATTERN_RECOGNITION: innate receptor senses PAMP/DAMP.
5. PHAGOCYTOSIS: target engulfment and intracellular processing.
6. OPSONIZATION: antibody or C3b coating raises phagocytic recognition.
7. ANTIGEN_PRESENTATION: peptide–MHC display.
8. COSTIMULATION / INHIBITION: additional activation or checkpoint signal.
9. CLONAL_EXPANSION: antigen-selected lymphocyte proliferation.
10. ANTIBODY_BINDING / NEUTRALIZATION.
11. COMPLEMENT_CASCADE: C3b opsonization, C3a/C5a inflammatory signaling, terminal MAC.
12. CYTOTOXIC_HIT: CD8/NK recognition and target-cell death.
13. CYTOKINE_FEEDBACK: local/systemic signaling.
14. TOLERANCE_OR_REGULATION: suppress or resolve activation.
15. APOPTOSIS_AND_EFFEROCYTOSIS: cell death and clearance.
16. TISSUE_REPAIR / BARRIER_RESTORATION.
17. MICROBIOME_COMPETITION: commensal resource/niche competition.
18. HOST_CELL_INFECTION / VIRAL_REPLICATION: virus uses host cell machinery.
19. OXYGEN_DELIVERY / OXYGEN_DIFFUSION.
20. METABOLIC_UPTAKE / METABOLIC_WASTE.

## 7. Core causal loops

### 7.1 Innate response
Tissue damage or pathogen pattern → resident macrophage/epithelial sensing → cytokines/chemokines + complement → endothelial activation → neutrophil/monocyte recruitment → phagocytosis or killing → debris clearance and resolution.

### 7.2 Adaptive response
Dendritic-cell antigen uptake → processing → peptide–MHC display plus required co-stimulation/context → naïve T-cell activation in lymphoid tissue → clonal expansion/differentiation → B-cell help and antibody production / CD8 cytotoxic response → contraction and memory formation.

T-cell activation must not be implemented as a one-step "antigen string matches T-cell string". TCR recognition of peptide–MHC and co-stimulatory/inhibitory context matter [15,16].

### 7.3 Tissue and oxygen loop
Ventilation + perfusion + alveolar barrier + hemoglobin + diffusion → oxygen delivery → cellular oxidative phosphorylation and ATP production. Hypoxia can follow impaired oxygen loading, blood flow, hemoglobin capacity or diffusion. Approximately 98% of transported oxygen is hemoglobin-bound; the dissolved fraction is small [17]. For a first model, use a simple oxygen transport budget and do not infer that inflammation alone directly sets oxygen saturation.

### 7.4 Resolution / collateral damage
Inflammation recruits and activates defense, but excessive or prolonged signaling can damage host tissue. Include anti-inflammatory/regulatory signals, apoptosis/efferocytosis, barrier integrity and repair. Success is multi-objective: pathogen control **and** preservation of host function.

## 8. Minimal first simulation (recommended)

Do not implement all entity types at once. Start with one tissue patch and a limited cast:

- 1 tissue compartment;
- 1 endothelial/barrier layer;
- a small population of resident macrophages;
- neutrophils;
- dendritic cells;
- one pathogen type;
- optional symbiont population;
- chemokine field;
- oxygen, glucose and one energy store;
- tissue integrity and inflammation states.

Use model agents (e.g. 50–500 agents) as representative samples, not literal cell counts. Record the scale factor: how many real cells each simulated agent represents.

Suggested numerical setup:
- coordinate unit: µm;
- simulation time unit: minute;
- start with 3D coordinates but a deliberately small tissue patch;
- dtMinutes = 0.1 as an engineering starting point (6 seconds), then compare 0.05 and 0.025 min for time-step convergence;
- use a deterministic PRNG seed;
- update from immutable state snapshot → proposed events → validation → apply events → new state;
- separate simulation thread from Swing UI; render immutable snapshots only.

The dt choice is a numerical design assumption, not a measured biological constant. If the time step is too coarse, fast receptor binding or cell movement can be misrepresented.

## 9. Validation plan

### Unit tests
- same seed/config → same event log and final state;
- no infection → baseline remains within declared bounds;
- no chemokine gradient → no systematic chemotactic drift;
- increased chemokine gradient → directional bias increases within configured range;
- phagocytosis removes only a valid target and records one event;
- a rejected action does not mutate state;
- antigen presentation without required co-stimulation does not produce full naïve T-cell activation in the simplified rules;
- C3b increases opsonization score; C3a/C5a increase inflammatory/chemotactic signaling; MAC is restricted to susceptible target classes;
- resolution phase reduces inflammation after threat control;
- excessive inflammation can reduce tissue integrity even when pathogen load falls;
- mass/energy bookkeeping residuals remain within declared tolerance;
- results are stable under smaller dt and multiple seeds.

### Validation levels
1. Code validation: compilation, schema, deterministic replay, unit tests.
2. Model verification: equations and numerical implementation match the written specification.
3. Calibration: selected parameters fit a specific assay/dataset.
4. Validation: predictions match independent measurements not used for calibration.
5. External review: immunology-domain review of cell types, pathways, units and omitted mechanisms.

Passing level 1 does not imply level 4. A visually plausible animation is not validation.

## 10. Reuse and improve the existing Matrixgame work

Repository search found older immune-related prototypes:
- Kollektives KI-Game 22042026 Gemini Oasenprojekt und Weltfrieden contains MatrixgameImmuneSystemv50, an earlier 2D Swing prototype, and PATHOGEN / GUARDIAN roles.
- The same archive contains a later BioEntity prototype with PATHOGEN, SYMBIONT, GUARDIAN, hp, energy and global behavior weights.
- KI-Architektur Imunsystem und Weltfrieden mit Plan und Nodestruktur ChatGPT und Gemini KI-Game Matrixgame Kollektiv(2).txt contains an older I/O/O agent proposal with STATE, ENERGY, ELEMENT, MEMORY, TARGET.
- MistralAIKombinationMatrixgameKIGame07082026 proposes a modular biology/Cell.java, DNA.java and ImmuneSystem.java layout.

These sources are useful historical material, but they are not a validated physiology model. In v50, survivalProbability = 1 - pathogenCount / 200 is an arbitrary display formula, not an empirical survival probability. Guardians automatically mark any pathogen within a 30-pixel radius dead, and pathogens replicate with a fixed per-update probability of 0.005; the simulation has no biological time unit, dose/response calibration, antigen specificity or adaptive response. Preserve the old code as a reference; do not use its output as biological evidence.

## 11. Suggested module layout

    biology/
      ontology/
        CellType.java
        EntityKind.java
        Compartment.java
        BiologicalCapability.java
      state/
        EntityEnvelope.java
        CellState.java
        ImmuneCellState.java
        PathogenState.java
        TissueState.java
        MoleculeState.java
        MetabolicState.java
      profiles/
        CellTypeProfile.json
        NeutrophilProfile.json
        MacrophageProfile.json
        DendriticCellProfile.json
      interactions/
        InteractionType.java
        BiologicalEvent.java
        EventValidator.java
      fields/
        ChemokineField.java
        OxygenField.java
        NutrientField.java
      engine/
        ImmuneSimulation.java
        ImmuneRules.java
        DeterministicScheduler.java
      validation/
        ImmuneBaselineTest.java
        ChemotaxisTest.java
        PhagocytosisTest.java
        TissueDamageTest.java

Keep biological domain code independent of Swing. The UI should display the domain state, not own the rules.

## 12. Recommended next actions

1. Reuse existing BioEntity / Entity concepts only as historical compatibility input; define typed records and explicit capability interfaces.
2. Create an annotated starter catalog for 8–12 entity profiles (neutrophil, macrophage, dendritic cell, CD4 T, CD8 T, B/plasma, NK, epithelial, endothelial, RBC, pathogen, symbiont).
3. Build one deterministic 3D tissue-patch experiment, but begin with two or three cell types and one pathogen.
4. Add state hashes, event counters and typed event logs before adding biological complexity.
5. Connect Java output to the existing JSON protocol; use Python/Colab only for plotting and parameter sweeps, not as a second source of truth.
6. Add selected reference datasets from HRA / Allen Immune Health Atlas with license, cohort, tissue, age range, assay and preprocessing metadata.

## References

[1] Bueckle et al. (2026), Cell Type Populations for 3D Anatomical Structures of the Human Reference Atlas, Scientific Data. https://www.nature.com/articles/s41597-026-06642-4  
[2] NIH Common Fund, Mapping the Human Body Cell by Cell. https://commonfund.nih.gov/dataecosystem/highlights/mapping-human-body-cell-cell  
[3] Allen Institute for Immunology, Human Immune Health Atlas (>1.8M PBMCs across 108 donors). https://apps.allenimmunology.org/aifi/resources/imm-health-atlas/  
[4] Tabula Sapiens, human single-cell transcriptome atlas. https://tabula-sapiens.sf.czbiohub.org/  
[5] NCBI Bookshelf, The complement system and innate immunity. https://www.ncbi.nlm.nih.gov/books/NBK27100/  
[6] Sender et al. (2023), The total mass, number, and distribution of immune cells in the human body, PNAS. https://doi.org/10.1073/pnas.2308511120  
[7] U.S. FDA, Investigations Operations Manual 2025 — Appendix C: Blood Hematology Normal Values. https://www.fda.gov/media/166526/download  
[8] Burn Injury Reduces Neutrophil Directional Migration Speed in Microfluidic Devices. https://pmc.ncbi.nlm.nih.gov/articles/PMC2912851/  
[9] Convergent and Divergent Migratory Patterns of Human Neutrophils inside Microfluidic Mazes. https://pmc.ncbi.nlm.nih.gov/articles/PMC5789854/  
[10] Microfluidic devices for neutrophil chemotaxis studies. https://link.springer.com/article/10.1186/s12967-020-02335-7  
[11] Macrophage Migration and Its Regulation by CSF-1. https://onlinelibrary.wiley.com/doi/10.1155/2012/501962  
[12] Monocytes, macrophages, dendritic cells and neutrophils: an update on lifespan kinetics in health and disease. https://pmc.ncbi.nlm.nih.gov/articles/PMC8207393/  
[13] O'Neill et al. (2016), A guide to immunometabolism for immunologists. https://www.nature.com/articles/nri.2016.70  
[14] Immunometabolism of T cells and macrophages: Human translational perspectives (2025). https://pubmed.ncbi.nlm.nih.gov/41496197/  
[15] A guide to antigen processing and presentation. https://www.nature.com/articles/s41577-022-00707-2  
[16] CD28 co-signaling in the adaptive immune response. https://pmc.ncbi.nlm.nih.gov/articles/PMC3047785/  
[17] NCBI Bookshelf, Physiology, Oxygen Transport. https://www.ncbi.nlm.nih.gov/sites/books/NBK538336/
