import json
import subprocess
import sys
from pathlib import Path

import pytest

from evolution_forge import (
    Config,
    EvolutionSimulator,
    apply_llm_proposal,
    run_simulation,
)


def test_run_is_deterministic_for_fixed_seed():
    config = Config(scenario="icy_ocean", steps=80, seed=17)
    _, first = run_simulation(config)
    _, second = run_simulation(config)
    assert first == second
    assert first["model_version"] == "1.0.1"
    assert first["config"]["seed"] == 17


def test_all_declared_scenarios_are_valid():
    for scenario in ("earth_tidal_pool", "alkaline_vent", "icy_ocean", "titan_hypothetical"):
        simulator = EvolutionSimulator(Config(scenario=scenario, steps=20))
        assert simulator.scenario["label"]


def test_llm_proposal_is_bounded_and_whitelisted():
    base = Config()
    changed = apply_llm_proposal(
        base,
        {"scenario": "icy_ocean", "steps": 240, "cooperation_benefit": 0.08},
    )
    assert changed.scenario == "icy_ocean"
    assert changed.steps == 240
    assert changed.cooperation_benefit == pytest.approx(0.08)


def test_llm_proposal_rejects_unknown_fields():
    with pytest.raises(ValueError, match="Unsupported proposal fields"):
        apply_llm_proposal(Config(), {"shell_command": "echo unsafe"})


def test_cli_writes_versioned_summary_and_history(tmp_path: Path):
    module = Path(__file__).resolve().parents[1] / "evolution_forge.py"
    config_file = tmp_path / "request.json"
    output_dir = tmp_path / "run"
    config_file.write_text(
        json.dumps({"scenario": "icy_ocean", "steps": 30, "seed": 5}),
        encoding="utf-8",
    )
    completed = subprocess.run(
        [sys.executable, str(module), "--config", str(config_file), "--out", str(output_dir)],
        capture_output=True,
        text=True,
        check=False,
        timeout=90,
    )
    assert completed.returncode == 0, completed.stdout + completed.stderr
    summary = json.loads((output_dir / "summary.json").read_text(encoding="utf-8"))
    assert summary["model_version"] == "1.0.1"
    assert summary["scenario"] == "icy_ocean"
    assert summary["config"]["seed"] == 5
    assert (output_dir / "history.csv").is_file()


def test_energy_and_resource_overrides_are_recorded():
    simulator = EvolutionSimulator(
        Config(
            scenario="icy_ocean",
            steps=20,
            seed=7,
            energy_flux=0.65,
            resource_regen=0.022,
        )
    )
    assert simulator.scenario["energy_flux"] == pytest.approx(0.65)
    assert simulator.scenario["resource_regen"] == pytest.approx(0.022)


def test_optimized_icy_ocean_configuration_is_deterministic():
    config_path = Path(__file__).resolve().parents[1] / "experiments" / "icy_ocean_energy_cooperation.json"
    config = Config(**json.loads(config_path.read_text(encoding="utf-8")))
    _, first = run_simulation(config)
    _, second = run_simulation(config)
    assert first == second
    assert first["config"]["energy_flux"] == pytest.approx(0.65)
    assert first["config"]["maintenance_cost"] == pytest.approx(0.015)
    assert first["config"]["cooperation_benefit"] == pytest.approx(0.08)
