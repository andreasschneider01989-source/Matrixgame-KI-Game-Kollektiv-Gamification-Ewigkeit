"""Non-scoring domain map for the Matrixgame Human Flourishing model.

This module describes a project-level framework. It is not a clinical,
psychometric, spiritual, or policy assessment instrument.
"""
from dataclasses import dataclass
from enum import Enum


class FlourishingDomain(str, Enum):
    HEALTH = "health"
    FREEDOM = "freedom"
    WILL_AND_MEANING = "will_and_meaning"
    RELATIONSHIPS = "relationships"
    KNOWLEDGE = "knowledge"
    SAFETY = "safety"
    JOY = "joy"


@dataclass(frozen=True)
class DomainDefinition:
    domain: FlourishingDomain
    description: str
    example_indicators: tuple[str, ...]


DOMAINS: tuple[DomainDefinition, ...] = (
    DomainDefinition(
        FlourishingDomain.HEALTH,
        "Physical and mental health, care access, prevention and healthy environments.",
        ("self-reported health", "access to appropriate care", "healthy life expectancy"),
    ),
    DomainDefinition(
        FlourishingDomain.FREEDOM,
        "Rights, autonomy, civil liberties and meaningful options without coercion.",
        ("rights protections", "self-reported autonomy", "freedom from coercion"),
    ),
    DomainDefinition(
        FlourishingDomain.WILL_AND_MEANING,
        "Self-chosen goals, agency, purpose and ability to act on considered intentions.",
        ("goal progress chosen by the person", "perceived agency", "meaning in life"),
    ),
    DomainDefinition(
        FlourishingDomain.RELATIONSHIPS,
        "Belonging, mutual trust, communication, respect and consent.",
        ("perceived social support", "social connection", "participation and inclusion"),
    ),
    DomainDefinition(
        FlourishingDomain.KNOWLEDGE,
        "Education, scientific literacy, critical thinking and access to reliable information.",
        ("learning access", "skills", "information literacy"),
    ),
    DomainDefinition(
        FlourishingDomain.SAFETY,
        "Personal security, peace, protection, material security and resilience.",
        ("exposure to violence", "perceived safety", "basic-needs security"),
    ),
    DomainDefinition(
        FlourishingDomain.JOY,
        "Positive experience, play, curiosity, culture and life satisfaction.",
        ("self-reported positive affect", "life satisfaction", "access to recreation"),
    ),
)


def validate_domain_map(
    domains: tuple[DomainDefinition, ...] = DOMAINS,
) -> tuple[str, ...]:
    """Return structural issues; deliberately computes no score or ranking."""
    errors: list[str] = []
    seen: set[FlourishingDomain] = set()
    for item in domains:
        if item.domain in seen:
            errors.append(f"duplicate domain: {item.domain.value}")
        seen.add(item.domain)
        if not item.description.strip():
            errors.append(f"empty description: {item.domain.value}")
        if not item.example_indicators:
            errors.append(f"missing candidate indicators: {item.domain.value}")
    missing = set(FlourishingDomain) - seen
    errors.extend(f"missing domain: {domain.value}" for domain in sorted(missing, key=lambda d: d.value))
    return tuple(errors)
