# MATRIXGAME – Positive Change Lab

**Version:** 0.1  
**Status:** design specification; not yet implemented  
**Purpose:** turn broad hopes for a better life into small, voluntary, observable experiments.

> A better next step, not a perfect life score.

## 1. Design goal

The Positive Change Lab connects the project's existing work on social connection, wellbeing, self-regulation, human flourishing, and the Collective Improvement Network.

It is intended to help a person:
1. notice their current capacity and constraints;
2. select one area that matters now;
3. choose one small, reversible action;
4. reflect on what happened;
5. adapt without shame or pressure.

It is a self-organization and learning aid, **not** a medical product, diagnostic tool, treatment plan, or guarantee of improved wellbeing.

## 2. Evidence labels

Every statement and feature should retain its epistemic status:

- **FACT:** supported by an identifiable source or reproducible observation.
- **MODEL:** a useful simplification for planning or simulation.
- **HYPOTHESIS:** a proposal that can be tested but is not yet established.
- **OPEN:** insufficient information or an unresolved question.
- **FICTION:** an explicitly imaginative game-world element.

A design idea does not become a scientific fact merely because it appears in a game. Symbolic scales, including spiritual or fictional scales, may be used as creative language but must not be presented as validated psychological measurements.

## 3. Four selectable domains

The person chooses one domain per session. The system must not assume that productivity is always the best objective.

| Domain | Examples of small actions |
|---|---|
| **Recovery** | pause, reduce stimulation, drink water, step outside, prepare a calmer evening |
| **Learning and work** | open one file, read one page, run one test, make one small commit |
| **Connection** | send a kind low-pressure message, ask a question, listen, make a plan only when mutually welcome |
| **Environment** | clear one small surface, prepare an object for tomorrow, improve light or airflow where practical |

These are examples, not instructions. A user can replace, skip, or decline every suggestion.

## 4. The two-minute session

### Step A — Notice

Optional, subjective check-in:
- energy: low / medium / high;
- stress or load: low / medium / high;
- available time: 2, 5, 10, or 15 minutes;
- current need: recover / build / connect / explore.

A personal 0–10 rating may be added, but it is only a self-observation. It is not a validated clinical scale, should not be compared between people, and must not be used to infer a diagnosis.

### Step B — Choose

Select one domain and one action small enough to be realistically completed in the time available.

A good action is:
- voluntary;
- specific;
- reversible;
- low-risk;
- small enough to start;
- meaningful to the person.

### Step C — Do or deliberately rest

The user may complete the action, modify it, postpone it, or choose rest. Rest is a legitimate outcome, not a failed quest.

### Step D — Reflect

Record only what is useful:
- completed / modified / postponed / skipped;
- easier / similar / harder than expected;
- one sentence: “What did I notice?”

No points are awarded for pushing through exhaustion. There is no penalty for missed days.

## 5. Seven-day learning loop

A seven-day cycle can be used as a lightweight review, not as a performance challenge:

1. **Choose:** one personally meaningful area.
2. **Try:** one small action when it fits.
3. **Observe:** note context and subjective effect, if useful.
4. **Review:** identify what seemed helpful, neutral, or unhelpful.
5. **Adjust:** keep, change, or stop the experiment.

A single person's observations can reveal patterns worth exploring. They cannot, by themselves, prove that an action caused a change. Sleep, workload, relationships, environment, chance, and many other factors may contribute.

## 6. A non-addictive reward model

Reward learning, agency, and reflection—not intensity, screen time, or output volume.

Good signals:
- “I identified a need.”
- “I chose a manageable next step.”
- “I respected a limit.”
- “I learned that this approach did not fit.”
- “I asked for support.”
- “I took a genuine break.”

Forbidden design patterns:
- streak loss or shame for missed sessions;
- FOMO, artificial urgency, or coercive reminders;
- public rankings of wellbeing;
- points for overwork or disclosure of intimate information;
- hidden optimization for engagement time;
- a single score that claims to represent a person's worth or total happiness.

**MODEL:** progress is better represented as a small vector—agency, recovery, connection, learning, and environmental fit—than as one total score. These dimensions can conflict, and the person decides which matters most in a given moment.

## 7. Matrixgame integration

The following is a proposed control loop, not implemented functionality:

```text
OBSERVE
  -> CHECK SAFETY AND CONSTRAINTS
  -> OFFER A SMALL SET OF OPTIONS
  -> USER CHOOSES / MODIFIES / DECLINES
  -> ACTION OR REST
  -> OPTIONAL REFLECTION
  -> ADAPT THE NEXT SUGGESTION
```

The A-C-G-U cycle can organize the experience:
- **Aufbau:** learn or create one small thing.
- **Verbindung:** strengthen a respectful connection.
- **Prüfung:** test an assumption or review an outcome.
- **Regeneration:** pause, recover, or reduce demands.

The system should abstain when context is insufficient, when a suggestion could be unsafe, or when the user does not want advice. An AI assistant may propose; it must not take control of personal decisions.

## 8. Privacy and safety requirements

- Personal check-ins are private by default and should remain local unless the user explicitly chooses otherwise.
- Public examples use synthetic data only.
- Do not publish personal health, relationship, employment, or account information.
- Do not provide diagnosis, medication instructions, or substitute for professional care.
- Do not infer sensitive traits from activity patterns.
- Make pause, export, deletion, and opt-out controls explicit in any future implementation.
- Never treat a low self-rating as proof of illness or a high rating as proof that support is unnecessary.

## 9. Sample micro-quest

**Quest: one useful next step**

- Domain: chosen by the user.
- Time budget: 5 minutes.
- Action: select one safe, concrete action that fits the current situation.
- Exit condition: stop after five minutes, or earlier if it does not feel right.
- Reflection: “Did this fit what I needed?”
- Success: the person made an informed choice—including choosing to stop.

This is a sample interaction pattern, not a prescribed daily routine.

## 10. Implementation stages

### Stage 0 — Design
- [x] Define domains, voluntary flow, evidence labels, and privacy constraints.
- [x] Define non-addictive gamification principles.
- [ ] Review the wording with people who may use the tool.

### Stage 1 — Minimal private prototype
- [ ] Add a local-only check-in and option selector.
- [ ] Make all fields optional and easy to clear.
- [ ] Keep state out of analytics and public logs by default.
- [ ] Test keyboard access, readability, and a short session flow.

### Stage 2 — Evidence-aware review
- [ ] Add a simple weekly reflection that reports observations, not causal claims.
- [ ] Test whether the workflow reduces friction rather than creating new obligations.
- [ ] Allow users to disable prompts and gamification independently.

### Stage 3 — Matrixgame integration
- [ ] Connect the option selector to a reviewed quest registry.
- [ ] Add explicit user approval before saving or sharing.
- [ ] Keep wellbeing support separate from career performance scoring.

## 11. Related project foundations

- [Social connection, wellbeing and safe self-regulation](../research/MATRIXGAME_CONNECTION_WELLBEING_SAFETY_2026-11-06.md)
- [Human flourishing without coercion](../research/MATRIXGAME_PARADISE_WITHOUT_COERCION_2026-11-07.md)
- [Collective Improvement Network](COLLECTIVE_IMPROVEMENT_NETWORK.md)
- [LUMINI compiler and simulation architecture](LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md)

The module remains a design proposal until a separate implementation is tested and reviewed.
