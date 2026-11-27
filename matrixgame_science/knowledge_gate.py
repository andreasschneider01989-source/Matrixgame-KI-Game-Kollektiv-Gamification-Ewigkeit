"""Small structural gate for Matrixgame research claims.

This module checks whether a claim record is reviewable. It does not
establish scientific truth or replace domain-expert review.
"""
from dataclasses import dataclass, field
from enum import Enum
from typing import Iterable


class EvidenceClass(str, Enum):
    FACT = "FACT"
    MODEL = "MODEL"
    HYPOTHESIS = "HYPOTHESIS"
    OPEN = "OPEN"
    FICTION = "FICTION"


class ReviewStatus(str, Enum):
    ACCEPT = "ACCEPT"
    REJECT = "REJECT"
    UNCERTAIN = "UNCERTAIN"
    ABSTAIN = "ABSTAIN"
    PROPOSE_ALTERNATIVE = "PROPOSE_ALTERNATIVE"


@dataclass(frozen=True)
class Claim:
    claim_id: str
    text: str
    evidence_class: EvidenceClass
    source_ids: tuple[str, ...] = field(default_factory=tuple)
    method: str = ""
    scope: str = ""
    uncertainty: str = ""
    falsification_test: str = ""
    fiction_notice: str = ""


@dataclass(frozen=True)
class GateResult:
    reviewable: bool
    errors: tuple[str, ...]
    notice: str = (
        "Structural reviewability is not scientific validation or proof."
    )


def validate_claim(claim: Claim) -> GateResult:
    """Return structural issues without deciding whether the claim is true."""
    errors: list[str] = []

    if not claim.claim_id.strip():
        errors.append("claim_id is required")
    if not claim.text.strip():
        errors.append("claim text is required")
    if not claim.scope.strip():
        errors.append("scope is required")
    if not claim.uncertainty.strip():
        errors.append("uncertainty must be stated")

    if claim.evidence_class is EvidenceClass.FACT and not claim.source_ids:
        errors.append("FACT requires at least one traceable source_id")
    if claim.evidence_class is EvidenceClass.MODEL and not claim.method.strip():
        errors.append("MODEL requires a method or model specification")
    if claim.evidence_class is EvidenceClass.HYPOTHESIS:
        if not claim.falsification_test.strip():
            errors.append("HYPOTHESIS requires a falsification/acceptance test")
    if claim.evidence_class is EvidenceClass.FICTION:
        if not claim.fiction_notice.strip():
            errors.append("FICTION requires an explicit no-truth-claim notice")

    return GateResult(reviewable=not errors, errors=tuple(errors))


def validate_batch(claims: Iterable[Claim]) -> tuple[GateResult, ...]:
    """Validate each record independently; one bad record does not hide others."""
    return tuple(validate_claim(claim) for claim in claims)
