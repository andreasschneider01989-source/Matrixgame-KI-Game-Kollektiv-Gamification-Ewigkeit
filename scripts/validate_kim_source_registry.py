#!/usr/bin/env python3
"""Validate the KIM source registry without third-party dependencies.

This checks the project's constrained source-record contract and privacy rules.
It is not a general-purpose JSON Schema 2020-12 validator.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from datetime import date
from pathlib import Path
from typing import Any

ACCESS = {
    "api_read", "browser_read", "library_read", "visible_partial_content",
    "title_only", "folder_listing_only", "render_error", "host_permission_denied",
    "binary_available_uninspected", "metadata_only",
}
PRIVACY = {"public", "user_workspace", "private", "unknown"}
REVIEW = {
    "content_inspected", "partial_review", "title_only", "not_reviewed",
    "not_accessible", "binary_not_extracted",
}
LICENSE = {
    "not_assessed", "public_link_only", "repository_license_check_required",
    "user_workspace_no_republication", "source_site_reuse_not_assessed",
}
CLAIM = {"FACT", "MODEL", "HYPOTHESIS", "OPEN", "FICTION"}
USES = {
    "project_status", "research_candidate", "code_recovery",
    "creative_reference", "learning_quest", "discovery_only",
}
KINDS = {
    "repository", "pull_request", "issue", "binary_file", "code_artifact",
    "folder", "document", "spreadsheet", "conversation", "workspace",
    "web_page", "video", "playlist", "search_page",
}
ID_RE = re.compile(r"^[a-z0-9][a-z0-9._-]{2,100}$")


class ValidationError(Exception):
    pass


def require(condition: bool, message: str) -> None:
    if not condition:
        raise ValidationError(message)


def valid_date(value: Any, label: str) -> None:
    require(isinstance(value, str), f"{label} must be a YYYY-MM-DD string")
    try:
        date.fromisoformat(value)
    except ValueError as exc:
        raise ValidationError(f"{label} is not a valid ISO date: {value!r}") from exc


def validate_registry(data: Any, schema: Any) -> list[str]:
    require(isinstance(schema, dict), "Schema root must be a JSON object")
    require(schema.get("$schema") == "https://json-schema.org/draft/2020-12/schema",
            "Unexpected or missing JSON Schema draft declaration")
    require(schema.get("title") == "KIM Source Registry", "Unexpected schema title")
    require(isinstance(data, dict), "Registry root must be a JSON object")
    require(set(data) == {"registry_version", "observed_at", "scope", "sources"},
            "Registry root has missing or unexpected fields")
    require(data["registry_version"] == "1.0.0", "registry_version must be 1.0.0")
    valid_date(data["observed_at"], "observed_at")
    require(isinstance(data["scope"], str) and len(data["scope"].strip()) >= 10,
            "scope must contain a meaningful description")
    require(isinstance(data["sources"], list) and data["sources"],
            "sources must be a non-empty array")

    seen: set[str] = set()
    ids: set[str] = set()
    errors: list[str] = []
    for index, source in enumerate(data["sources"]):
        prefix = f"sources[{index}]"
        require(isinstance(source, dict), f"{prefix} must be an object")
        required = {
            "id", "name", "platform", "kind", "access_status", "privacy_class",
            "review_state", "license_status", "claim_status", "use", "observed_at", "notes",
        }
        allowed = required | {"url", "related_ids"}
        require(required.issubset(source), f"{prefix} missing: {sorted(required - set(source))}")
        require(set(source).issubset(allowed), f"{prefix} has unexpected fields: {sorted(set(source) - allowed)}")

        source_id = source["id"]
        require(isinstance(source_id, str) and ID_RE.fullmatch(source_id) is not None,
                f"{prefix}.id has invalid format")
        require(source_id not in seen, f"duplicate source id: {source_id}")
        seen.add(source_id)
        ids.add(source_id)

        for field in ("name", "platform", "notes"):
            require(isinstance(source[field], str) and len(source[field].strip()) >= 2,
                    f"{prefix}.{field} must be a non-empty string")
        require(source["kind"] in KINDS, f"{prefix}.kind is not supported")
        require(source["access_status"] in ACCESS, f"{prefix}.access_status is not supported")
        require(source["privacy_class"] in PRIVACY, f"{prefix}.privacy_class is not supported")
        require(source["review_state"] in REVIEW, f"{prefix}.review_state is not supported")
        require(source["license_status"] in LICENSE, f"{prefix}.license_status is not supported")
        require(source["claim_status"] in CLAIM, f"{prefix}.claim_status is not supported")
        require(source["use"] in USES, f"{prefix}.use is not supported")
        valid_date(source["observed_at"], f"{prefix}.observed_at")

        if source["privacy_class"] in {"user_workspace", "private"}:
            require("url" not in source,
                    f"{prefix} is private/user workspace and must not publish a URL")
        if "url" in source:
            url = source["url"]
            require(isinstance(url, str) and url.startswith("https://"),
                    f"{prefix}.url must be an HTTPS URL")
            require(source["privacy_class"] == "public",
                    f"{prefix}.url is only allowed for public sources")
        if "related_ids" in source:
            require(isinstance(source["related_ids"], list),
                    f"{prefix}.related_ids must be an array")
            require(len(source["related_ids"]) == len(set(source["related_ids"])),
                    f"{prefix}.related_ids contains duplicates")

    for index, source in enumerate(data["sources"]):
        for related in source.get("related_ids", []):
            require(related in ids,
                    f"sources[{index}] references unknown related id {related!r}")

    return [f"Validated {len(data['sources'])} source records.",
            "Privacy check passed: private and user-workspace records contain no URLs.",
            "Uniqueness, dates, enumerations and internal links passed."]


def self_test(schema: dict[str, Any]) -> None:
    base = {
        "registry_version": "1.0.0",
        "observed_at": "2026-11-09",
        "scope": "Self-test registry contract.",
        "sources": [{
            "id": "test.source.one",
            "name": "Test public source",
            "platform": "Test",
            "kind": "web_page",
            "url": "https://example.org/",
            "access_status": "title_only",
            "privacy_class": "public",
            "review_state": "title_only",
            "license_status": "not_assessed",
            "claim_status": "OPEN",
            "use": "discovery_only",
            "observed_at": "2026-11-09",
            "notes": "Synthetic validator fixture; not a real source.",
        }],
    }
    validate_registry(base, schema)

    duplicate = json.loads(json.dumps(base))
    duplicate["sources"].append(json.loads(json.dumps(base["sources"][0])))
    try:
        validate_registry(duplicate, schema)
    except ValidationError:
        pass
    else:
        raise ValidationError("Self-test failed: duplicate IDs were accepted")

    private_url = json.loads(json.dumps(base))
    private_url["sources"][0]["privacy_class"] = "user_workspace"
    try:
        validate_registry(private_url, schema)
    except ValidationError:
        pass
    else:
        raise ValidationError("Self-test failed: private URL was accepted")

    bad_status = json.loads(json.dumps(base))
    bad_status["sources"][0]["review_state"] = "verified_by_ai_consensus"
    try:
        validate_registry(bad_status, schema)
    except ValidationError:
        pass
    else:
        raise ValidationError("Self-test failed: unsupported review state was accepted")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("registry", nargs="?", default="research/KIM_SOURCE_REGISTRY_2026-11-09.json")
    parser.add_argument("schema", nargs="?", default="schemas/kim-source-registry.schema.json")
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    try:
        schema = json.loads(Path(args.schema).read_text(encoding="utf-8"))
        if args.self_test:
            self_test(schema)
            print("PASS: 4 validator self-test cases.")
        data = json.loads(Path(args.registry).read_text(encoding="utf-8"))
        for line in validate_registry(data, schema):
            print("PASS:", line)
        return 0
    except (OSError, json.JSONDecodeError, ValidationError) as exc:
        print("FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
