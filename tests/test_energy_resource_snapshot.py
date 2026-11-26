from __future__ import annotations

import json
from pathlib import Path


SNAPSHOT = (
    Path(__file__).parents[1]
    / "research"
    / "MATRIXGAME_ENERGY_AND_RESOURCE_OPTIMIZATION_2026-11-25.json"
)


def test_energy_snapshot_is_versioned_and_has_unique_ids() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    assert data["schema_version"] == "0.1.0"
    assert data["snapshot_id"] == "energy-resource-optimization-global-2026-11-25"
    assert data["as_of"] == "2026-11-25"
    ids = [item["id"] for item in data["records"]]
    assert len(ids) >= 10
    assert len(ids) == len(set(ids))


def test_energy_records_have_scope_sources_units_and_caveats() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    for item in data["records"]:
        assert item["indicator"]
        assert item["unit"]
        assert item["period"]
        assert item["geography"]
        assert item["method"]
        assert item["source"].startswith("https://")
        assert item["status"]
        assert item["caveat"]


def test_energy_values_are_numeric_and_comparators_are_explicit() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    for item in data["records"]:
        if "value" in item:
            assert isinstance(item["value"], (int, float))
            assert item["value"] >= 0
        if "comparator" in item:
            assert item["comparator"] in {"nearly", "more_than"}


def test_energy_interpretation_guards_are_present() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    rules = " ".join(data["interpretation_rules"]).lower()
    assert "power (w), energy (wh)" in rules
    assert "deprivation" in rules
    assert "scenario" in rules
