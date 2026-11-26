from __future__ import annotations

import json
from pathlib import Path


SNAPSHOT = (
    Path(__file__).parents[1]
    / "research"
    / "MATRIXGAME_SOCIAL_CONNECTION_AND_SAFETY_2026-11-25.json"
)


def test_social_connection_snapshot_is_valid_json_and_versioned() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    assert data["schema_version"] == "0.1.0"
    assert data["snapshot_id"] == "social-connection-safety-germany-2026-11-25"
    assert data["as_of"] == "2026-11-25"
    assert data["status"] == "FACTS_WITH_METHOD_LIMITS"


def test_records_have_unique_ids_sources_and_scope_metadata() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    records = data["records"]
    ids = [item["id"] for item in records]

    assert len(records) >= 10
    assert len(ids) == len(set(ids))
    for item in records:
        assert item["indicator"]
        assert item["period"]
        assert item["geography"]
        assert item["source"].startswith("https://")
        assert item["epistemic_status"] == "FACT"
        assert item["evidence_level"] == "E1_SOURCE_LINKED"
        assert item["caveat"]


def test_numeric_changes_are_finite_and_units_are_explicit() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    for item in data["records"]:
        if item.get("value") is not None:
            assert isinstance(item["value"], (int, float))
        assert item["unit"]
        for key, value in item.items():
            if key.startswith("change_") and key.endswith("_pct") and value is not None:
                assert isinstance(value, (int, float))


def test_trend_interpretation_rules_prevent_mixed_measure_conclusions() -> None:
    data = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    rules = " ".join(data["interpretation_rules"]).lower()

    assert "never combine" in rules
    assert "not a causal explanation" in rules
    assert "not equivalent" in rules
