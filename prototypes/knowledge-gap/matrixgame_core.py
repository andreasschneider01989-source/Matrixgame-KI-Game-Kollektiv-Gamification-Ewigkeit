"""Matrixgame I/O prototype: transparent information-gap simulation.

All scenario numbers are illustrative model inputs, not real-world estimates.
Run: python matrixgame_core.py
Test: python -m unittest discover -s tests
"""
from __future__ import annotations

import json
from dataclasses import dataclass
from enum import Enum
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parent
DEFAULT_SCENARIO = ROOT / "scenarios" / "information_gap_demo.json"


class EvidenceLabel(str, Enum):
    FACT = "FAKT"
    LIKELY = "WAHRSCHEINLICH"
    CLAIM = "BEHAUPTUNG"
    UNCLEAR = "UNKLAR"
    REFUTED = "WIDERLEGT"
    MODEL_ASSUMPTION = "MODELLANNAHME"


@dataclass(frozen=True)
class Observation:
    day: int
    estimated_total: int
    officially_confirmed: int
    label: EvidenceLabel
    note: str

    @property
    def information_gap(self) -> int:
        return max(0, self.estimated_total - self.officially_confirmed)

    @property
    def gap_ratio(self) -> float:
        if self.estimated_total <= 0:
            return 0.0
        return min(1.0, self.information_gap / self.estimated_total)


def load_scenario(path: Path = DEFAULT_SCENARIO) -> dict[str, Any]:
    """INPUT: load a scenario file and perform basic structural checks."""
    with path.open("r", encoding="utf-8") as handle:
        scenario = json.load(handle)

    if not isinstance(scenario.get("timeline"), list) or not scenario["timeline"]:
        raise ValueError("Scenario must contain a non-empty 'timeline' list.")

    previous_day = 0
    for row in scenario["timeline"]:
        for key in ("day", "estimated_total", "officially_confirmed", "label", "note"):
            if key not in row:
                raise ValueError(f"Timeline row is missing required field: {key}")
        if row["day"] <= previous_day:
            raise ValueError("Timeline days must be strictly increasing.")
        if row["estimated_total"] < 0 or row["officially_confirmed"] < 0:
            raise ValueError("Counts cannot be negative.")
        previous_day = row["day"]
    return scenario


def operate(scenario: dict[str, Any]) -> list[Observation]:
    """OPERATION: transform declared inputs into explicit, reproducible metrics."""
    observations = []
    for row in scenario["timeline"]:
        observations.append(
            Observation(
                day=int(row["day"]),
                estimated_total=int(row["estimated_total"]),
                officially_confirmed=int(row["officially_confirmed"]),
                label=EvidenceLabel(row["label"]),
                note=str(row["note"]),
            )
        )
    return observations


def status_for(observation: Observation) -> str:
    """A simple teaching signal, not a validated risk forecast."""
    if observation.gap_ratio < 0.20:
        return "GRUEN / kleine modellierte Luecke"
    if observation.gap_ratio < 0.50:
        return "GELB / mittlere modellierte Luecke"
    return "ROT / grosse modellierte Luecke"


def render_output(scenario: dict[str, Any], observations: list[Observation]) -> None:
    """OUTPUT: human-readable report with assumptions kept visible."""
    print(f"=== MATRIXGAME: {scenario['title']} ===")
    print(f"Datentyp: {scenario['data_status']}")
    print("Hinweis: Die Zahlen sind Szenarioeingaben, keine realen Fallzahlen.\n")

    for item in observations:
        print(f"TAG {item.day}: {item.note}")
        print(f"  Geschaetzt im Modell: {item.estimated_total:>8,}")
        print(f"  Offiziell bestaetigt: {item.officially_confirmed:>8,}")
        print(f"  Informationsluecke:   {item.information_gap:>8,} ({item.gap_ratio:.0%})")
        print(f"  Erkenntnisstatus:     {item.label.value}")
        print(f"  Lehr-Ampel:           {status_for(item)}")
        print()

    print("IOO-LOOP: Input -> Operation -> Output -> menschliche Pruefung -> neues Input")
    print("Die Ampel beschreibt nur die gewaehlte Modellkennzahl; sie ist keine Prognose.")


def main() -> None:
    scenario = load_scenario()
    observations = operate(scenario)
    render_output(scenario, observations)


if __name__ == "__main__":
    main()
