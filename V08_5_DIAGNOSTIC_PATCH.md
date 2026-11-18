# v0.8.5 – Diagnostic and control-stability patch

This branch documents the v0.8.5 local diagnostic build based on the v0.8.4 local source.

## Changes
- Corrected selected-creature inspector String.format argument order and made reproduction threshold/cost explicit.
- Split bottom controls into four rows and moved the restart control into a dedicated, more visible administration row.
- Added console event records for combat damage, combat deaths, starvation deaths and falling-rock collisions.
- Combat records include tick, actor/target IDs and types, both positions, damage amount and HP before/after.
- Added a regression assertion for damage provenance logging.

## Gameplay clarification
In Evolution mode, creatures have attack capability. An adjacent opposing-team creature may attack the selected human-controlled creature. Human control prevents AI action selection for the selected unit; it does not grant invulnerability and does not stop other teams. This patch adds observability without changing combat balance.

## Verification of local artifact
- Java 17 compilation with `-Xlint:all -Werror`: passed.
- Headless self-tests: 64 checks passed.
- GUI smoke test under a virtual display: window started and remained alive until the intentional timeout.
- Full manual Windows interaction test remains open, particularly the German-layout Ö key.

## Artifact authority
The complete locally compiled source and ZIP are the tested v0.8.5 artifact. This GitHub branch currently contains this status document, not the complete Java source file. Do not treat the branch as a byte-identical source mirror yet.
