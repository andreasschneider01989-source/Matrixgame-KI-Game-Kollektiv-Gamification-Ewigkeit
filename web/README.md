# Matrixgame Infinity Canvas Gravity Lab

Open `Matrixgame_InfinityCanvas_GravityLab.html` directly in a current desktop browser. It is a single-file HTML5/JavaScript application: no local server, package manager, SQL database, or external library is required.

## Controls

- Drag the canvas to pan; use the mouse wheel to zoom around the pointer.
- Arrow keys pan; `+` / `-` zoom; Space pauses or resumes.
- Use the outer-shell selector to compare 5, 6, 7, or 8 outer satellites.
- Add/remove additional 2+N layers with the Octet buttons.
- Switch between XY orbit view, radius/time, and speed/radius plots.
- Select a body to inspect its mass, orbital radius, speed, position, acceleration, and gravitational force.
- Edit the selected body's mass, XY position, velocity components and time step. Changes pause the simulation and reset the energy reference.
- Add and remove test masses at runtime. Enable velocity and gravitational-force vectors to inspect the current dynamical state.

## Newtonian physics

This is a true pairwise Newtonian N-body calculation with softened gravity and a Velocity-Verlet integrator. For each body, the acceleration is calculated from the current position of every other body at every integration step. The visible paths are trails of integrated positions, not pre-rendered circles. Dashed circles are reference radii only.

The baseline preserves the earlier prototype's parameters: G=28, central mass=1,000,000, inner mass=12, outer mass=18, radii 32 and 72, softening=1.2. The editor currently operates in a 2D XY plane. A full XYZ editor is a planned later extension.

The model uses arbitrary simulation units. It is not a quantum atom simulation: electron orbitals are not classical satellite paths. The atom comparison panel is only a separate electron-shell-count reference (2+5 nitrogen, 2+6 oxygen, 2+7 fluorine, 2+8 neon).

## Tests

```sh
for n in 5 6 7 8; do node web/tests/gravitylab_smoke.cjs web/Matrixgame_InfinityCanvas_GravityLab.html $n; done
```

The automated test executes the inline application code in a minimal browser/canvas stub. It checks the 2,000-step finite-state simulation for each shell count, edits a body's mass/position/velocity/time step, confirms that the Newtonian acceleration field changes, and adds/removes a test body.


## 3D Orthogonal Orbital Lab

Open `Matrixgame_GravityLab_3D.html` directly in a desktop browser. This is a true 3D state with x/y/z positions and vx/vy/vz velocities, projected to a 2D canvas:
- one central mass, two inner bodies, six outer bodies;
- the six outer bodies form opposite pairs in mutually perpendicular XY, XZ, and YZ planes;
- left-drag rotates, Shift/right-drag pans, wheel zooms;
- edit mass, all three position and velocity components, and dt;
- switch a simplified satellite-only repulsion term independently of central attraction;
- JSON export/import saves masses, vectors, physics settings, and view.

The repulsion slider is a toy interaction, not electromagnetism. The three planes are a geometry analogy to p-orbital orientations, not a quantum orbital model. The code explicitly does not model spin, the Pauli principle, wavefunctions, or Coulomb attraction/repulsion.

Run tests:
```sh
for n in 5 6 7 8; do node web/tests/gravitylab3d_smoke.cjs web/Matrixgame_GravityLab_3D.html $n; done
```


Scientific model boundary and the proposed next experiment grid: [3D orbital analogy and physics documentation](../docs/GRAVITYLAB_3D_ORBITAL_ANALOGY.md).


### Geometric symmetry score

The UI reports a custom 0–100% model score based on radial equality and near-opposite positioning within the XY, XZ and YZ pairs, with a penalty when the satellite count differs from six. Baseline reference values are 56.4% (5 outer), 100.0% (6), 84.6% (7), and 71.7% (8). This is a geometric descriptor only; it does not measure chemical or quantum stability. JSON round-trip and score checks are included in the automated test.
