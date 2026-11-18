# Frontier research note: 3D motion, 2D pixels, AI media and an open ecosystem

Status labels: **FACT** = supported by the cited source; **MODEL** = proposed Matrixgame architecture; **HYPOTHESIS** = to be tested.

## 1. From 3D motion to a 2D pixel world

### FACT
Modern video-generation research is already combining learned video generation with explicit 3D scene information:
- GEN3C uses a 3D cache derived from estimated pixel depth and conditions video generation on 2D renderings of that cache plus camera movement. Its repository publishes code under Apache-2.0; model/inference requirements are substantial and its README reports testing on high-memory GPUs. https://github.com/nv-tlabs/GEN3C
- 3DScenePrompt (ICLR 2026) uses a 3D memory of static scene geometry, separates moving elements from the static environment, and combines spatially adjacent content with temporally adjacent frames. https://proceedings.iclr.cc/paper_files/paper/2026/hash/77f2e9559888c4ebf794dc0e3b0e5da6-Abstract-Conference.html

### MODEL: Matrixgame's layered pipeline
1. **World state:** units/buildings have 3D transforms (x, y, z), orientation, velocity, health and team.
2. **Motion source:** physics integration, skeletal animation, keyframe clips, motion capture, or an AI policy selects actions. No pre-authored path is required for every movement.
3. **Projection:** a camera maps the 3D scene into 2D screen coordinates.
4. **Pixel renderer:** render as pixel art, sprites, silhouettes or simplified stick-figure-like forms; optionally retain depth and object-ID maps.
5. **Neural media layer:** a video model can stylize or extend the rendered frames while receiving depth/segmentation/camera hints.
6. **Verification:** the deterministic simulator remains authoritative for positions, collisions, scores and outcomes; generated video is presentation, not the physics engine.

Important distinction: "without path functions" can mean no fixed, hand-authored trajectory per character. It cannot mean no state update at all. A moving object still needs a position and a rule that changes that position over time. That rule may be physics, a policy, a rig/animation system, or a learned motion model.

## 2. Small experimental target

Create one scene with:
- one 3D-positioned unit moving in a 2D grid environment;
- a camera with orthographic projection;
- a pixel/silhouette renderer;
- a deterministic seed and recorded transforms per frame;
- optional depth and segmentation output.

Compare three motion modes:
A. scripted keyframes; B. physics + action policy; C. learned motion proposal constrained by the simulator.

Measure: coordinate continuity, collision correctness, frame-to-frame identity, controllability, render time and reproducibility. Start with A/B before adding a video model.

## 3. Where else this can be reused

- Strategy game replays and cinematic kill/build animations.
- Educational science explainers: show a 3D molecule, cell, planet or ecosystem as a 2D pixel simulation.
- Procedural music visualizers synchronized to game events.
- Short clips generated from deterministic game replays, with AI used for visual style, camera suggestions, narration and editing.
- Accessibility modes: same world state rendered as pixel art, high-contrast silhouettes, simplified maps or audio descriptions.
- Research dashboards: transform simulation traces into short, inspectable visual explanations.

## 4. AI music and narrative

Use original, project-owned compositions and lyrics as an optional narrative layer:
- themes: collective intelligence, emergence, cooperation, uncertainty, evolution and repair;
- each track can map to a biome, faction, research milestone or quest;
- game events can trigger adaptive stems, tempo layers or sound motifs;
- keep attribution, licenses, model provenance and human contributions recorded.

Music should reward discovery and cooperation, not manipulate users into compulsive play or spending.

## 5. A sustainable open ecosystem

### FACT
OpenAI currently publishes gpt-oss-20b and gpt-oss-120b as open-weight models under Apache 2.0, allowing broad use, modification and commercial use subject to the usage policy. OpenAI states that these weights are not served through ChatGPT or its API; self-hosting still incurs compute, storage and hosting costs. https://help.openai.com/en/articles/11870455-openai-open-weight-models-gpt-oss

Therefore: OpenAI does have an open-weight offering, but it would be inaccurate to describe every OpenAI model or the entire ChatGPT service as open source.

### MODEL: Matrixgame's hybrid financing
- Open core: game rules, data formats, educational quests and basic simulator remain openly inspectable.
- Free entry: a playable local build and lightweight community-hosted demo.
- Optional paid services: managed multiplayer, hosted AI inference, storage, support and higher compute limits.
- Shared funding: grants, university partnerships, donations, sponsorships and transparent paid contributions.
- Contribution accounting: record code, testing, translation, moderation, documentation and compute as distinct contributions.
- Fair allocation: publish the share for infrastructure, maintainers, contributors, safety/moderation and a community reserve.
- User protections: no pay-to-win, no hidden data resale, clear compute budgets, exportable data and opt-in paid features.

Revenue and contributor payouts are a governance proposal, not a guaranteed income source. A real system needs legal review, accounting, tax handling, licensing and abuse prevention before money is distributed.

## 6. Next implementation order

1. Finish local compile and self-test of v0.6.1.
2. Implement a deterministic 2D-to-screen camera and a frame transform log.
3. Add one simple tower as a game entity in the existing resolver.
4. Add optional 3D transform fields behind a separate rendering adapter.
5. Export replay frames and metadata; only then experiment with neural video stylization.
6. Design a transparent contribution/revenue ledger before handling real payments.
