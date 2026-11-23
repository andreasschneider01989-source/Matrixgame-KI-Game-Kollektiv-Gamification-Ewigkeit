# MACHX Second Earth — Quest 002: Perception, 3D worlds and sky science

**Date:** 2026-11-20  
**Status:** research and first executable solar-geometry module added to the existing Quest 001 branch; review and CI still required.

## 1. User's research direction
Connect:
1. image/video perception and semantic interpretation;
2. persistent 3D reconstruction and editable world state;
3. deterministic physics and environmental simulation;
4. agent-driven experiments and scientific learning;
5. versioned checkpoints so a later session can reload and continue.

The immediate scientific experiment is a gnomon (vertical-stick) observatory: record the Sun's apparent position and shadow length/direction over a day and across seasons. A calibrated water clock is a second, separate timekeeping experiment.

## 2. Evidence-based clarification: pixels are observations, not 3D geometry
An RGB image supplies 2D measurements. A single image does not uniquely determine absolute depth, hidden surfaces, object scale or geometry. A model may infer plausible 3D structure using learned priors, but ambiguity remains unless additional evidence is supplied.

A reconstruction pipeline may combine:
- image segmentation and object correspondence;
- camera intrinsics/extrinsics and pose estimation;
- monocular or multi-view depth;
- point clouds, meshes, voxel grids or 3D Gaussian Splatting;
- multi-view consistency and uncertainty;
- physics/material estimation as a separate, harder inference stage.

A mesh is explicit vertices/faces; a point cloud is sampled 3D points; a voxel world stores occupancy/material in a grid; Gaussian splats are a renderable scene representation. None automatically implies correct physical properties.

"Parse function" is not a universal required module. Parsing can mean extracting structured content from an image, but geometry reconstruction is a pipeline of estimation and optimization steps, not one magic function.

## 3. World-model architecture
Use separate layers with explicit contracts:

SENSOR_FRAME → PERCEPTION → SCENE_GRAPH → GEOMETRY → PHYSICS_STATE → SIMULATION → RENDER_FRAME

- **Perception:** image/video pixels, labels, contours, detections, depth estimates.
- **Scene graph:** object identity, parent/child relations, coordinate frames and provenance.
- **Geometry:** voxel occupancy, mesh, point cloud or Gaussian splats.
- **Physics state:** mass, dimensions, pose, velocity, material and uncertainty.
- **Simulation:** deterministic or seeded stochastic state transitions.
- **Renderer:** camera projection and pixels. Rendering never becomes the authoritative world state.

The simulator owns canonical state. A Minecraft Java or Luanti adapter displays approved state snapshots and submits bounded commands. Keep game-engine-specific blocks out of the scientific core.

## 4. Video generation versus persistent simulation
Frame-by-frame video generation can create convincing motion without a persistent, editable 3D world. Long-horizon consistency requires memory, camera/world coordinates, geometry or another persistent latent state, action conditioning, and feedback. A video model can be a useful visualizer or proposal generator; it is not automatically a physics engine.

A useful hybrid:
- 3D simulator produces state and camera trajectory;
- renderer creates structured RGB/depth/segmentation frames;
- video model enhances appearance while conditioned on the structure;
- reconstruction/perception tools compare output against the source state;
- metrics detect geometry drift, object identity switches and physical violations.

Do not infer that a neural network has discovered executable source code merely because its outputs resemble a program. A model can learn useful latent dynamics; code generation, compilation, tests and runtime verification are separate steps.

## 5. Current research references reviewed
- NVIDIA Project Lyra: https://github.com/nv-tlabs/lyra — generative, explorable 3D world research; source code Apache-2.0, model-specific terms must be checked.
- Tencent HY-World 2.0: https://github.com/Tencent-Hunyuan/HY-World-2.0 — multimodal 3D world reconstruction/generation with mesh and 3DGS outputs; inspect individual model/checkpoint licenses and hardware requirements.
- WorldCrafter: https://github.com/TencentARC/WorldCrafter — camera-controlled video world model with implicit 3D-aware memory; check its bundled third-party license terms.
- MineDojo Voyager: https://github.com/MineDojo/Voyager — MIT-licensed research agent for Minecraft with automatic curriculum, executable skill library and feedback-based improvement. It requires Minecraft, Fabric mods and an LLM API; it is a research baseline, not a plug-and-play autonomous scientific civilization.
- Luanti: https://github.com/luanti-org/luanti — open-source voxel game-creation engine; Lua API for games and mods. Engine and game/content licenses must be checked separately.

These references are research candidates, not integrated dependencies. Repository availability does not mean every model weight, dataset or third-party component has the same license.

## 6. Astronomy model added
second-earth/astronomy/solar_position.py implements an educational NOAA-style fractional-year approximation:
- solar declination;
- equation of time;
- conversion from civil clock time to apparent solar time;
- solar elevation and azimuth;
- vertical-stick shadow length and direction.

Tests cover solstice declinations, equinox noon geometry at 50° N, seasonal shadow differences, night-time behavior and invalid inputs. These are model-consistency tests; no field observation has yet validated the model.

Primary references:
- NOAA calculation equations: https://www.gml.noaa.gov/grad/solcalc/solareqns.PDF
- NOAA solar glossary: https://gml.noaa.gov/grad/solcalc/glossary.html
- US Naval Observatory equation of time: https://aa.usno.navy.mil/faq/eqtime
- NOAA shadow lesson: https://www.noaa.gov/jetstream/global/learning-lesson-shadow-knows-ii
- NOAA seasons lesson: https://www.noaa.gov/education/resource-collections/climate/changing-seasons
- NASA lunar synodic month lesson: https://www.nasa.gov/pdf/377727main_Lunar_Math.pdf

## 7. Causal discoveries and predictions
- Earth rotates west-to-east; the Sun's daily apparent motion is east-to-west. The solar hour angle changes about 15 degrees per hour.
- Solar noon is the Sun's meridian crossing, not necessarily civil-clock 12:00. Longitude, UTC offset/daylight saving and equation of time matter.
- Earth's axial tilt (~23.44°) and orbital position drive seasonal solar declination and day length. Earth–Sun distance is not the main cause of seasons.
- A vertical gnomon's shadow points opposite the Sun's azimuth; for a northern mid-latitude site at solar noon it points approximately north.
- The shortest daily shadow is near solar noon; its seasonal length changes as solar declination changes.
- The lunar phase cycle is synodic (~29.53 days), not the Moon's sidereal orbital period (~27.3 days), because Earth moves along its orbit while the Moon orbits Earth.

## 8. Water-clock caution
A simple hole in a draining vessel is not a constant-rate clock: hydrostatic pressure/head falls as the water level drops, changing outflow speed (Torricelli-type behavior). A useful experiment must record vessel geometry, water head, outlet area, temperature, time and collected volume. A constant-head arrangement or a calibration curve can improve repeatability. Compare the water-clock interval with apparent solar time only after repeated calibration. Do not claim that two uncalibrated cycles measure equal time.

## 9. Persistent simulation and continuity
Each experiment run should preserve:
- schema and model version;
- experiment ID, parent checkpoint and code commit;
- configuration, units, assumptions and data provenance;
- random seed and PRNG/model version where stochastic;
- initial state, event log and final state;
- metrics, test results, limitations and reviewer disposition.

A resumed run must state whether it restores the exact prior state or starts a new branch from a prior checkpoint. Never silently overwrite the previous run. Use immutable run IDs and append-only event logs; store large binary assets outside Git history (release assets/LFS/object storage as appropriate) and keep manifests and hashes in Git.

## 10. Agent roles and safe loop
- Observer: records raw observations and uncertainty.
- Researcher: finds primary sources and explains equations.
- Modeler: proposes a formal model and units.
- Implementer: writes a small isolated change.
- Critic: searches for counterexamples and hidden assumptions.
- Verifier: runs tests and independent checks.
- Archivist: saves run manifests, evidence and next quest.

Loop: OBSERVE → HYPOTHESIZE → PREDICT → EXPERIMENT → COMPARE → REVIEW → CHECKPOINT.

Agents may propose code and actions. They cannot silently change canonical state, merge, publish, contact people or perform real-world interventions. Human approval remains required.

## 11. Acceptance criteria
1. Solar module tests pass in the repository workflow.
2. A fixed date/time/location/height returns deterministic geometry.
3. Civil time and apparent solar time are explicitly distinguished.
4. Night-time shadow length is represented as unavailable, not a fabricated finite value.
5. Assumptions and approximation limits are visible.
6. No private coordinates or personal observations are committed without explicit consent.
7. Existing Gaia water-balance model and Java/MACHX engines remain untouched.

## 12. Next small quest
Run the unit tests; then perform one safe shadow-stick observation on a clear day, with measured stick height and time. Save a generic, privacy-safe observation record. Only after comparing several observations should we add a lunar phase model or water-clock module.
