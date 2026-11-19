#!/usr/bin/env python3
"""Dependency-free structural validator for the initial LUMINI LifeOS state contract."""
import json
import re
import sys
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
STATE = ROOT / "examples" / "lifeos-state.example.json"
ALLOWED_DOMAINS = {"LIFE_QUALITY", "CAREER", "MACHX", "MATRIXGAME", "LEARNING", "COMMUNITY", "RESEARCH"}
ALLOWED_MODES = {"STABLE", "EXPLORATORY", "RECOVERY", "PAUSED"}
ALLOWED_GOAL_STATUS = {"ACTIVE", "PLANNED", "BLOCKED", "DONE", "PAUSED"}
ALLOWED_ACTION_STATUS = {"READY", "DOING", "BLOCKED", "DONE", "DROPPED"}
ALLOWED_PRIORITY = {"LOW", "NORMAL", "HIGH", "TIME_SENSITIVE"}

def require(condition, message):
    if not condition:
        raise ValueError(message)

def validate(data):
    require(isinstance(data, dict), "state must be an object")
    required = {"schema_version", "updated_at", "mode", "focus", "goals", "next_actions", "automation"}
    require(required <= data.keys(), f"missing keys: {sorted(required - data.keys())}")
    require(data["schema_version"] == "1.0.0", "unsupported schema_version")
    datetime.fromisoformat(data["updated_at"].replace("Z", "+00:00"))
    require(data["mode"] in ALLOWED_MODES, "invalid mode")
    require(isinstance(data["focus"], str) and 1 <= len(data["focus"]) <= 160, "invalid focus")
    require(isinstance(data["goals"], list) and len(data["goals"]) <= 12, "goals must be a list of at most 12")
    require(isinstance(data["next_actions"], list) and len(data["next_actions"]) <= 20, "next_actions must be a list of at most 20")
    ids = set()
    for goal in data["goals"]:
        require(isinstance(goal, dict), "goal must be an object")
        require(re.fullmatch(r"[a-z0-9][a-z0-9-]{1,47}", goal.get("id", "")) is not None, "invalid goal id")
        require(goal["id"] not in ids, "duplicate id")
        ids.add(goal["id"])
        require(goal.get("domain") in ALLOWED_DOMAINS, "invalid goal domain")
        require(goal.get("status") in ALLOWED_GOAL_STATUS, "invalid goal status")
        require(goal.get("horizon") in {"WEEK", "MONTH", "QUARTER", "YEAR", "LONG_TERM"}, "invalid goal horizon")
        require(isinstance(goal.get("title"), str) and 1 <= len(goal["title"]) <= 160, "invalid goal title")
    for action in data["next_actions"]:
        require(isinstance(action, dict), "action must be an object")
        require(re.fullmatch(r"[a-z0-9][a-z0-9-]{1,47}", action.get("id", "")) is not None, "invalid action id")
        require(action["id"] not in ids, "duplicate id")
        ids.add(action["id"])
        require(action.get("domain") in ALLOWED_DOMAINS, "invalid action domain")
        require(action.get("status") in ALLOWED_ACTION_STATUS, "invalid action status")
        require(action.get("priority") in ALLOWED_PRIORITY, "invalid action priority")
        require(isinstance(action.get("effort_minutes"), int) and 2 <= action["effort_minutes"] <= 240, "invalid effort_minutes")
        require(isinstance(action.get("fallback"), str) and bool(action["fallback"].strip()), "fallback is required")
    automation = data["automation"]
    require(isinstance(automation, dict), "automation must be an object")
    require(automation.get("human_approval_required") is True, "human approval must remain required")
    require(automation.get("personal_data_in_public_repo") is False, "public state must not contain personal data")
    require(automation.get("level") in {"L0_MANUAL", "L1_REMINDER", "L2_READ_ONLY", "L3_PROPOSE_ONLY", "L4_APPROVED_EXECUTION"}, "invalid automation level")
    return True

if __name__ == "__main__":
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else STATE
    try:
        validate(json.loads(path.read_text(encoding="utf-8")))
        print(f"PASS: {path}")
    except Exception as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        sys.exit(1)
