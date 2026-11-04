# Matrixgame Infinity Canvas Gravity Lab

Open `Matrixgame_InfinityCanvas_GravityLab.html` directly in a current desktop browser. It is a single-file application and requires no local server, package manager, or external library.

## Controls

- Drag the canvas to pan; use the mouse wheel to zoom around the pointer.
- Arrow keys pan; `+` / `-` zoom; Space pauses or resumes.
- Use the outer-shell selector to compare 5, 6, 7, or 8 outer satellites.
- Add/remove additional 2+N layers with the Octet buttons.
- Switch between XY orbit view, radius/time, and speed/radius plots.
- Select a body to inspect its mass, orbital radius, speed, and coordinates.

## Physics model

Newtonian softened N-body gravity with pairwise interactions and a Velocity-Verlet integrator. The baseline preserves the earlier prototype's parameters: G=28, central mass=1,000,000, inner mass=12, outer mass=18, radii 32 and 72, softening=1.2.

The model uses arbitrary simulation units. It is not a quantum atom simulation: electron orbitals are not classical satellite paths. The atom comparison panel is only a separate electron-shell-count reference (2+5 nitrogen, 2+6 oxygen, 2+7 fluorine, 2+8 neon).

## Test

```sh
node web/tests/gravitylab_smoke.cjs web/Matrixgame_InfinityCanvas_GravityLab.html
```

The smoke test executes the inline simulation code for 2,000 integration steps using a minimal canvas stub and checks that all positions and velocities remain finite.
