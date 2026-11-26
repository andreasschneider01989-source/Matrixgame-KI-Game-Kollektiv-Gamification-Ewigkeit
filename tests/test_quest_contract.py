from __future__ import annotations

import json
import re
from pathlib import Path


ROOT = Path(__file__).parents[1]
SCHEMA_PATH = ROOT / "matrixgame-protocol" / "quest.schema.json"
EXAMPLE_PATH = ROOT / "matrixgame-protocol" / "quest.example.json"


def load_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def test_quest_schema_and_example_are_valid_json() -> None:
    schema = load_json(SCHEMA_PATH)
    example = load_json(EXAMPLE_PATH)
    assert schema["$schema"] == "https://json-schema.org/draft/2020-12/schema"
    assert schema["additionalProperties"] is False
    assert all(key in example for key in schema["required"])


def test_quest_identifiers_and_types_match_contract() -> None:
    schema = load_json(SCHEMA_PATH)
    quest = load_json(EXAMPLE_PATH)
    assert schema["properties"]["quest_id"]["pattern"]
    assert re.fullmatch(schema["properties"]["quest_id"]["pattern"], quest["quest_id"])
    assert quest["choice_mode"] in schema["properties"]["choice_mode"]["enum"]
    assert quest["quest_type"] in schema["properties"]["quest_type"]["enum"]
    assert quest["definition_status"] in schema["properties"]["definition_status"]["enum"]


def test_quest_steps_and_success_criteria_are_actionable() -> None:
    quest = load_json(EXAMPLE_PATH)
    step_ids = [step["step_id"] for step in quest["steps"]]
    assert len(step_ids) == len(set(step_ids))
    assert all(step.get("instruction") for step in quest["steps"])
    assert len(quest["success_criteria"]) >= 1
    assert quest["effort_minutes"]["minimum"] <= quest["effort_minutes"]["maximum"]


def test_safeguards_prevent_coercive_progress_mechanics() -> None:
    safeguards = load_json(EXAMPLE_PATH)["safeguards"]
    assert safeguards["pause_allowed"] is True
    assert safeguards["decline_allowed"] is True
    assert safeguards["penalty_for_pause_or_incompletion"] is False
    assert safeguards["streak_pressure"] is False
    assert safeguards["public_personal_ranking"] is False
    assert safeguards["human_confirmation_for_external_actions"] is True


def test_rewards_are_optional_and_not_a_measure_of_personal_worth() -> None:
    quest = load_json(EXAMPLE_PATH)
    assert quest["rewards"]["optional"] is True
    assert "personal worth" in quest["rewards"]["description"]


def test_emotion_regulation_quest_matches_quest_contract_and_safety_rules() -> None:
    schema = load_json(SCHEMA_PATH)
    quest_path = ROOT / "matrixgame-protocol" / "quest.emotion-regulation.example.json"
    quest = load_json(quest_path)

    assert all(key in quest for key in schema["required"])
    assert re.fullmatch(schema["properties"]["quest_id"]["pattern"], quest["quest_id"])
    assert quest["quest_type"] in schema["properties"]["quest_type"]["enum"]
    assert quest["choice_mode"] in schema["properties"]["choice_mode"]["enum"]
    assert 1 <= len(quest["options"]) <= 8
    assert len({option["option_id"] for option in quest["options"]}) == len(quest["options"])
    assert len({step["step_id"] for step in quest["steps"]}) == len(quest["steps"])
    assert quest["effort_minutes"]["minimum"] <= quest["effort_minutes"]["maximum"]

    safeguards = quest["safeguards"]
    assert safeguards["pause_allowed"] is True
    assert safeguards["decline_allowed"] is True
    assert safeguards["penalty_for_pause_or_incompletion"] is False
    assert safeguards["streak_pressure"] is False
    assert safeguards["public_personal_ranking"] is False
    assert safeguards["default_privacy"] == "PRIVATE"

    all_text = " ".join(
        [quest["purpose"]["objective"], quest["purpose"]["meaning"]]
        + [option["description"] for option in quest["options"]]
        + [step["instruction"] for step in quest["steps"]]
    ).lower()
    assert "do not force a positive interpretation" in all_text
    assert "no emotional disclosure is required" in all_text
    assert "do not judge or cancel it" in all_text
    assert "do not repeat a phrase" in all_text
