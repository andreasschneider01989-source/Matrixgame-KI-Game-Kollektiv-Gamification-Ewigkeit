#!/usr/bin/env python3
"""Validate Matrixgame research-artifact JSON Schema and fixtures.

Run from any directory:
  python scripts/validate_research_artifacts.py
"""
from __future__ import annotations

import copy
import json
import sys
from pathlib import Path

from jsonschema import Draft202012Validator, FormatChecker
from jsonschema.exceptions import ValidationError

ROOT = Path(__file__).resolve().parents[1]
SCHEMA_PATH = ROOT / "schemas" / "research-artifact.schema.json"
EXAMPLE_PATH = ROOT / "examples" / "research-artifact.example.json"


def load_json(path: Path):
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def expect_invalid(validator, name: str, candidate: dict) -> None:
    errors = list(validator.iter_errors(candidate))
    if not errors:
        raise AssertionError(f"Negative fixture unexpectedly passed: {name}")
    print(f"PASS negative fixture: {name} ({len(errors)} validation error(s))")


def main() -> int:
    schema = load_json(SCHEMA_PATH)
    example = load_json(EXAMPLE_PATH)

    Draft202012Validator.check_schema(schema)
    print("PASS schema validates against JSON Schema Draft 2020-12 meta-schema")

    validator = Draft202012Validator(schema, format_checker=FormatChecker())
    validator.validate(example)
    print("PASS illustrative example validates")

    round_trip = json.loads(json.dumps(example, ensure_ascii=False))
    if round_trip != example:
        raise AssertionError("JSON round-trip changed the artifact")
    print("PASS JSON serialization round-trip")

    missing_required = copy.deepcopy(example)
    del missing_required["title"]
    expect_invalid(validator, "missing required title", missing_required)

    invalid_status = copy.deepcopy(example)
    invalid_status["claim_status"] = "CERTAIN"
    expect_invalid(validator, "invalid claim status", invalid_status)

    malformed_datetime = copy.deepcopy(example)
    malformed_datetime["created_at"] = "not-a-date-time"
    expect_invalid(validator, "malformed created_at", malformed_datetime)

    malformed_uri = copy.deepcopy(example)
    malformed_uri["sources"][0]["url"] = "not a uri"
    expect_invalid(validator, "malformed source URI", malformed_uri)

    unknown_top_level = copy.deepcopy(example)
    unknown_top_level["unreviewed_magic"] = True
    expect_invalid(validator, "unknown top-level property", unknown_top_level)

    print("RESULT: all schema and fixture checks passed")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (AssertionError, ValidationError, json.JSONDecodeError, OSError) as exc:
        print(f"VALIDATION FAILED: {exc}", file=sys.stderr)
        raise SystemExit(1)
