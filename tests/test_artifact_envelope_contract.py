from __future__ import annotations

import json
from datetime import datetime
from pathlib import Path


ROOT = Path(__file__).parents[1]
SCHEMA_PATH = ROOT / "matrixgame-protocol" / "artifact-envelope.schema.json"
EXAMPLE_PATH = ROOT / "matrixgame-protocol" / "artifact-envelope.example.json"


def _load_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def test_artifact_envelope_schema_and_example_are_valid_json() -> None:
    schema = _load_json(SCHEMA_PATH)
    example = _load_json(EXAMPLE_PATH)

    assert schema["$schema"] == "https://json-schema.org/draft/2020-12/schema"
    assert schema["type"] == "object"
    assert schema["additionalProperties"] is False


def test_example_contains_every_required_top_level_field() -> None:
    schema = _load_json(SCHEMA_PATH)
    example = _load_json(EXAMPLE_PATH)

    for field in schema["required"]:
        assert field in example, f"Missing required envelope field: {field}"

    properties = schema["properties"]
    for field, value in example.items():
        assert field in properties, f"Unexpected top-level field: {field}"
        specification = properties[field]
        if "const" in specification:
            assert value == specification["const"], field
        if "enum" in specification:
            assert value in specification["enum"], field


def test_example_nested_statuses_and_relationships_match_contract() -> None:
    schema = _load_json(SCHEMA_PATH)
    example = _load_json(EXAMPLE_PATH)
    definitions = schema["$defs"]

    assert example["artifact_type"] in definitions["artifactType"]["enum"]
    assert example["implementation_status"] in schema["properties"]["implementation_status"]["enum"]
    assert example["evidence_level"] in schema["properties"]["evidence_level"]["enum"]
    assert example["privacy_class"] in schema["properties"]["privacy_class"]["enum"]
    assert example["validation"]["status"] in definitions["validation"]["properties"]["status"]["enum"]

    relation_types = definitions["relationship"]["properties"]["type"]["enum"]
    for relation in example["relationships"]:
        assert relation["type"] in relation_types
        assert relation["target_id"]

    for source in example["provenance"]["source_refs"]:
        assert set(definitions["sourceRef"]["required"]).issubset(source)


def test_example_timestamps_are_iso_8601_compatible() -> None:
    example = _load_json(EXAMPLE_PATH)
    for field in ("created_at", "updated_at"):
        value = example[field].replace("Z", "+00:00")
        datetime.fromisoformat(value)


def test_knowledge_status_is_not_conflated_with_implementation_status() -> None:
    schema = _load_json(SCHEMA_PATH)
    properties = schema["properties"]

    assert "epistemic_status" in properties
    assert "implementation_status" in properties
    example_value = _load_json(EXAMPLE_PATH)
    assert example_value["epistemic_status"] is None
    assert example_value["implementation_status"] == "IMPLEMENTED"
