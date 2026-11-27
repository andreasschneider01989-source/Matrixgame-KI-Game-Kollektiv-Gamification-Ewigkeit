"""Deterministic, voluntary micro-action planner for Human Flourishing.

This is a planning aid, not a clinical, psychological, legal, or safety assessment.
It stores no personal data and computes no wellbeing score.
"""
from dataclasses import dataclass
from .flourishing import FlourishingDomain


@dataclass(frozen=True)
class MicroActionPlan:
    primary_domain: FlourishingDomain
    supporting_domain: FlourishingDomain | None
    user_goal: str
    action: str
    completion_check: str
    fallback: str
    timebox_minutes: int
    stop_rule: str = "Du kannst jederzeit pausieren, anpassen oder abbrechen."


_ACTIONS: dict[FlourishingDomain, tuple[str, str, str]] = {
    FlourishingDomain.HEALTH: (
        "Wähle eine kleine, für dich sichere Handlung, die deinem körperlichen oder psychischen Wohlbefinden dienen könnte.",
        "Notiere nur, ob die Handlung hilfreich, neutral oder unpassend war.",
        "Verkleinere den Schritt oder hole passende fachliche Unterstützung; keine medizinische Behandlung eigenständig ändern.",
    ),
    FlourishingDomain.FREEDOM: (
        "Kläre eine konkrete Wahlmöglichkeit: Welche Optionen hast du, und welche Information fehlt dir noch?",
        "Halte mindestens zwei Optionen oder bewusst die Entscheidung zum Vertagen fest.",
        "Wenn die Entscheidung unklar oder folgenreich ist, sammle erst Informationen oder bitte um Beratung.",
    ),
    FlourishingDomain.WILL_AND_MEANING: (
        "Formuliere dein selbst gewähltes Anliegen in einem Satz und bestimme den kleinstmöglichen nächsten Schritt.",
        "Prüfe, ob der Schritt deinem eigenen Anliegen entspricht – nicht einer fremden Erwartung.",
        "Du darfst das Ziel ändern, kleiner machen oder heute bewusst nicht verfolgen.",
    ),
    FlourishingDomain.RELATIONSHIPS: (
        "Wähle einen respektvollen Kontakt-Schritt, etwa eine kurze Nachricht ohne Antworterwartung oder eine Einladung mit echter Wahlfreiheit.",
        "Prüfe, ob deine Kommunikation klar, freundlich und frei von Druck war.",
        "Respektiere ein Nein oder ausbleibende Antwort und gib der anderen Person Raum.",
    ),
    FlourishingDomain.KNOWLEDGE: (
        "Untersuche eine kleine Frage anhand einer nachvollziehbaren Quelle und trenne Beobachtung, Interpretation und offene Frage.",
        "Notiere eine Aussage, ihre Quelle und eine verbleibende Unsicherheit.",
        "Wenn keine verlässliche Quelle verfügbar ist, markiere die Frage als offen statt zu raten.",
    ),
    FlourishingDomain.SAFETY: (
        "Identifiziere einen überschaubaren, konkreten Schritt, der deine persönliche oder organisatorische Sicherheit verbessern könnte.",
        "Prüfe, ob der Schritt erledigt, angepasst oder bewusst vertagt wurde.",
        "Bei unmittelbarer Gefahr hat reale menschliche Hilfe Vorrang vor diesem Planer.",
    ),
    FlourishingDomain.JOY: (
        "Wähle eine kleine Aktivität aus Neugier, Spiel, Kreativität oder Erholung – ohne Leistungsziel.",
        "Halte fest, ob du dabei einen Moment von Interesse, Ruhe oder Freude bemerkt hast.",
        "Wenn es sich nicht gut anfühlt, wechsle die Aktivität oder beende sie.",
    ),
}


def build_micro_action(
    user_goal: str,
    primary_domain: FlourishingDomain,
    supporting_domain: FlourishingDomain | None = None,
    timebox_minutes: int = 10,
) -> MicroActionPlan:
    """Build the same plan for the same inputs; never scores the person."""
    if not isinstance(user_goal, str) or not user_goal.strip():
        raise ValueError("user_goal must be a non-empty string")
    if len(user_goal.strip()) > 300:
        raise ValueError("user_goal must be at most 300 characters")
    if not isinstance(primary_domain, FlourishingDomain):
        raise ValueError("primary_domain must be a FlourishingDomain")
    if supporting_domain is not None and not isinstance(supporting_domain, FlourishingDomain):
        raise ValueError("supporting_domain must be a FlourishingDomain or None")
    if supporting_domain == primary_domain:
        raise ValueError("supporting_domain must differ from primary_domain")
    if isinstance(timebox_minutes, bool) or not isinstance(timebox_minutes, int):
        raise ValueError("timebox_minutes must be an integer")
    if not 1 <= timebox_minutes <= 30:
        raise ValueError("timebox_minutes must be between 1 and 30")

    action, completion_check, fallback = _ACTIONS[primary_domain]
    if supporting_domain is not None:
        action += f" Wenn passend, verbinde den Schritt freiwillig mit: {supporting_domain.value}."
    return MicroActionPlan(
        primary_domain=primary_domain,
        supporting_domain=supporting_domain,
        user_goal=user_goal.strip(),
        action=action,
        completion_check=completion_check,
        fallback=fallback,
        timebox_minutes=timebox_minutes,
    )
