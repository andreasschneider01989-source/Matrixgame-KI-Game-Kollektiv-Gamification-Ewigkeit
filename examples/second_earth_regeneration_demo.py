"""Small reproducible Second Earth regeneration scenario demo."""
from matrixgame_science.second_earth import (
    ExternalPressures,
    RegenerativeActions,
    SecondEarthState,
    run_scenario,
    safety_gate_open,
)

initial = SecondEarthState()
actions = RegenerativeActions(
    habitat_restoration=0.8,
    renewable_energy=0.8,
    health_access=0.6,
    open_education=0.7,
    cooperation=0.8,
    emergency_preparedness=0.7,
)
pressures = ExternalPressures(
    ecosystem_stress=0.2,
    transition_friction=0.2,
    social_stress=0.15,
    information_disorder=0.1,
    hazard_pressure=0.1,
)
history = run_scenario(initial, actions, pressures, steps=10)
for tick, state in enumerate(history):
    print(
        f"tick={tick:02d} biodiversity={state.biodiversity:.3f} "
        f"clean_energy={state.clean_energy:.3f} wellbeing={state.wellbeing:.3f} "
        f"trust={state.trust:.3f} safety={state.safety:.3f} "
        f"safety_gate={safety_gate_open(state)}"
    )
