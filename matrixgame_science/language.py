"""Provenance-aware bridge from language to formal model parameters.

This module stores reviewed mappings. It does not infer numeric parameters
from free text; a value needs explicit provenance and evidence status.
"""
from __future__ import annotations
from dataclasses import dataclass
from enum import Enum
import math


class EvidenceStatus(str, Enum):
    FACT = "FACT"
    MODEL = "MODEL"
    HYPOTHESIS = "HYPOTHESIS"
    OPEN = "OPEN"
    FICTION = "FICTION"


@dataclass(frozen=True)
class ModelClaim:
    claim_id: str
    statement: str
    symbolic_expression: str
    evidence_status: EvidenceStatus
    source: str
    assumptions: tuple[str, ...] = ()

    def __post_init__(self) -> None:
        fields = (self.claim_id, self.statement, self.symbolic_expression, self.source)
        if any(not value.strip() for value in fields):
            raise ValueError("Claim ID, statement, expression, and source are required.")


@dataclass(frozen=True)
class ParameterBinding:
    name: str
    value: float
    unit: str
    source: str
    evidence_status: EvidenceStatus

    def __post_init__(self) -> None:
        if not self.name.strip() or not self.unit.strip() or not self.source.strip():
            raise ValueError("Parameter name, unit, and source are required.")
        if not math.isfinite(float(self.value)):
            raise ValueError("Parameter value must be finite.")
