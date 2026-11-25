# Matrixgame – Collective AI Game & Simulation Project

## Current Repository Status

This repository is an evolving collection of ideas, experiments, documentation, Java code fragments, simulation concepts, and previous development work related to the Matrixgame and Collective AI Games.

It brings together material developed over multiple iterations, including:

- Game and simulation concepts
- Java prototypes and code fragments
- AI agent and decision-making ideas
- Documentation, design notes, and development discussions
- Concepts for collective intelligence, cooperation, and modular simulation systems

**Important:** This repository currently represents an evolving research and development collection, not a single finished, fully integrated simulator.

Some concepts and architectural structures described in this documentation are goals for future development. They should not be interpreted as implemented or tested functionality unless the corresponding code is present and verified.

## Development Vision

The long-term goal is to transform the most useful existing Java implementations, prototypes, and ideas into a modular, extensible, and maintainable simulation framework.

Rather than continuously expanding individual monolithic Java classes, the project aims to combine reusable components from previous versions into a coherent architecture.

The planned architecture separates functionality into packages and classes with clearly defined responsibilities, interfaces, and dependencies.

This should make it easier to:

- Reuse existing simulation components
- Combine different AI agents and strategies
- Experiment with game theory and collective decision-making
- Extend simulations without rewriting the entire application
- Test individual components independently
- Develop the project incrementally with AI-assisted programming
- Share improvements with the open-source community

The existing code and documentation provide a starting point for this process. The modular architecture described below is a **development plan**, not a claim that all components already exist or work together.

## Architecture standard and shared contracts

The repository now has a central integration standard and a shared metadata envelope. These connect existing knowledge, LUMINI, game-state and future simulation artifacts without replacing their domain-specific schemas.

- [Architecture Standard — integration contract](docs/ARCHITECTURE_STANDARD.md)
- [Artifact Envelope JSON Schema](matrixgame-protocol/artifact-envelope.schema.json)
- [Artifact Envelope example](matrixgame-protocol/artifact-envelope.example.json)

This first interface slice is reviewable on a feature branch; it is not yet merged into the stable main branch.

## Responsible gamification and portable quests

The existing learning-quest design now has a machine-readable quest-definition contract and a reusable improvement example. The design is grounded in autonomy, achievable competence, optional cooperation, honest feedback, and recovery without punitive streaks.

- [Responsible Gamification Standard](docs/RESPONSIBLE_GAMIFICATION_STANDARD.md)
- [Quest Definition JSON Schema](matrixgame-protocol/quest.schema.json)
- [Example improvement quest](matrixgame-protocol/quest.example.json)

This is a quest-definition format and design standard, not yet a persistent quest service or adaptive wellbeing system.



The broader vision for human–AI collaboration, cross-project improvement quests, evidence-aware knowledge sharing, and responsible automation is documented here:

- [Collective Improvement Network — Vision and Roadmap](docs/COLLECTIVE_IMPROVEMENT_NETWORK.md)

This is a proposed direction for future development, not a claim that the network or its automation is already implemented.

## Universal Knowledge Engine (proposed)

The Universal Knowledge Engine documentation connects scientific knowledge, personal and collective learning, simulations, evidence, quests, and future asynchronous research networks. These documents are specifications and learning maps, not claims that the described systems are already implemented.

- [Universal Knowledge Engine — Architecture](docs/UNIVERSAL_KNOWLEDGE_ENGINE.md)
- [Knowledge Node and Evidence Schema](docs/KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md)
- [Physics A–Z Atlas](docs/PHYSICS_AZ_ATLAS.md)
- [Learning Quests and Responsible Gamification](docs/LEARNING_QUESTS_AND_GAMIFICATION.md)
- [Interplanetary Knowledge Network](docs/INTERPLANETARY_KNOWLEDGE_NETWORK.md)
- [Proton–electron mass ratio and 2D shell-exchange research](docs/RESEARCH_PROTON_ELECTRON_RATIO_AND_2D_SHELL_EXCHANGE.md)
- [Planet Earth — Habitability and Evolution Project](docs/PLANET_EARTH_HABITABILITY_AND_EVOLUTION.md)
- [Collective AI Accessibility and Global Participation](docs/COLLECTIVE_AI_ACCESSIBILITY_AND_GLOBAL_PARTICIPATION.md)
- [Consciousness Simulator v0.14 Review and v0.15 Plan](docs/CONSCIOUSNESS_SIMULATOR_V014_REVIEW_AND_V015_PLAN.md)

The initial implementation target is deliberately small: one sourced density knowledge node, its equation and SI units, a dimensional validation test, one linked learning quest, and a reproducible example. Later search, agent collaboration, and DTN-inspired synchronization should build on that verified foundation.

## Global research update — 6 November 2026

The following research documents extend the Universal Knowledge Engine with a dated, evidence-aware snapshot of global progress, climate, AI, energy systems, space research, and collective intelligence.

- [Matrixgame Global Research 2026 — full report](research/MATRIXGAME_GLOBAL_RESEARCH_2026-11-06.md)
- [Global progress metrics — machine-readable JSON snapshot](research/MATRIXGAME_GLOBAL_PROGRESS_METRICS_2026-11-06.json)
- [Matrixgame Infinity Report v4.0 — archive](research/Matrixgame_Infinity_Report_v4_0_2026-11-06.md)

The report distinguishes observations, estimates, forecasts, models, hypotheses, and fiction. Metrics must be interpreted with their own period, unit, definition, and source. In particular, installed capacity is not electricity generation; target gain is not net fusion-plant output; and a one-year temperature anomaly is not identical to a long-term warming level.

## Social connection, violence and relationship trends — 25 November 2026

A dated, evidence-aware Germany/global snapshot separates police-recorded violence, under-reporting, global prevalence estimates, marriage/divorce statistics, household structures and relationship surveys. It deliberately avoids inferring one cause from one-year changes and keeps the measures separate for future modeling.

- [Social connection and safety — research synthesis](research/MATRIXGAME_SOCIAL_CONNECTION_AND_SAFETY_2026-11-25.md)
- [Social connection and safety — structured JSON snapshot](research/MATRIXGAME_SOCIAL_CONNECTION_AND_SAFETY_2026-11-25.json)

## Social connection, wellbeing and safety — 6 November 2026

A new research note integrates social connection, respectful dating and communication, emotion regulation, recovery-aware safeguards, and non-addictive gamification. It separates evidence from design models and open questions; it is a public concept document, not a clinical guide or an implemented feature.

- [Social connection, wellbeing and safe self-regulation — research and design note](research/MATRIXGAME_CONNECTION_WELLBEING_SAFETY_2026-11-06.md)

## Fragment 6 — historical synthesis and future architecture (23 November 2026)

The long-running Matrixgame Journal / Fragment 6 has been analyzed as a historical source. Its scientific ideas, software sketches, personal notes, links and speculative narratives are separated into a traceable architecture. Sensitive personal material and harmful calls to violence are intentionally excluded from public implementation. The synthesis connects the Genesis registry, evidence provenance, LUMINI compiler, engine registry, reproducible experiments, collective review, Infinity Canvas, learning and Earth systems research.

- [Fragment 6 — complete synthesis and realization roadmap](docs/FRAGMENT_6_SYNTHESIS_AND_FUTURE_ARCHITECTURE_2026-11-23.md)
- [Fragment 6 — curated link register](docs/FRAGMENT_6_CURATED_LINK_REGISTER_2026-11-23.md)
- [Original Fragment 6 journal](https://docs.google.com/document/d/1VIEINT69AqQbLZ66iTAUlLH1q5Av51JvDzKioHKTKc4/edit)

## LUMINI compiler, simulation and career scenario lab — 7 November 2026

The broader compiler and simulation architecture remains a staged roadmap. A first executable Python MVP now implements a restricted scalar-formula subset: lexer, parser, AST, SI dimensional analysis, versioned Model IR, and a deterministic `formula.scalar` runner. Physics, agent, dataset, career and natural-language pipelines remain planned, not implemented.

- [LUMINI — compiler, modeling, simulation and career engine architecture](docs/LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md)
- [Engineering Kanban and acceptance criteria — issue #22](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/22)

## LUMINI formula compiler MVP — 23 November 2026

A working, deliberately limited compiler is now available in `lumini_compiler/`. It converts a safe `.lumini` formula subset into typed Model IR, checks SI dimensions and dependencies, and produces a deterministic Run Manifest. It does not execute arbitrary code and does not claim scientific validation.

- [Executable compiler, DSL reference, tests and roadmap](docs/LUMINI_FORMULA_COMPILER_MVP_2026-11-23.md)
- [LUMINI Model IR 0.1 JSON Schema](matrixgame-protocol/lumini-model-ir.schema.json)
- [Runnable Earth-surface-gravity example](examples/lumini/earth_surface_gravity.lumini)

Quick start: `python -m pip install -e .`, then `lumini check examples/lumini/earth_surface_gravity.lumini` and `lumini run examples/lumini/earth_surface_gravity.lumini`.

## LUMINI Idea Inbox, compiler filters and multilingual interoperability — 11 November 2026

A specification for preserving raw ideas, tracing transparent compiler filters, separating evidence from models and hypotheses, proposing causal graphs, routing ideas into quests or code modules, and translating a shared semantic model into multiple natural languages and programming targets. Includes multi-LLM proposal/review roles, privacy and provenance rules, and a staged Java MVP. This is a design, not an implemented compiler.

- [LUMINI Idea Inbox, compiler filters and multilingual interoperability](docs/LUMINI_IDEA_COMPILER_FILTERS_MULTILINGUAL_INTEROP_2026-11-11.md)

## Infinite Canvas, dual interfaces and model quality — 11 November 2026

A specification for one shared simulation snapshot with human-readable console output, machine-readable JSONL events and an interactive Infinite Canvas. It defines variable-driven axes, a dynamic scale legend, reproducible RunManifests, agent communication phases, transcript-to-module provenance and testable quality gates. This is a design and acceptance document, not a claim that the full implementation already exists.

- [Infinite Canvas, dual interface and quality model](docs/INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md)

## Human flourishing without coercion — birth safety and anti-dystopia research (7 November 2026)

A research and design module connecting evidence-informed birth safety, sustainable wellbeing, the fictional drug Soma, and the autonomy lessons of Aldous Huxley's *Brave New World*. It separates facts, models, hypotheses and open questions; the module is a research concept, not a medical guide or an implemented feature.

- [Human Flourishing ohne Zwang — research and design note](research/MATRIXGAME_PARADISE_WITHOUT_COERCION_2026-11-07.md)
- [Kanban-ready implementation and evidence-review issue #21](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/21)

## Potential to progress — inspiration and research brief (6 November 2026)

A concise synthesis of current energy, AI-for-science, restoration, health and climate developments, translated into evidence-aware Matrixgame quests and a practical collaboration model.

- [Matrixgame Infinity — From potential to progress](research/MATRIXGAME_POTENTIAL_TO_PROGRESS_2026-11-06.md)

## Open implementation quests

The initial work is tracked as separate, reviewable GitHub issues:

- [MVP: KnowledgeNode and evidence validator](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/6)
- [Learning: Physics A–Z atlas and sourced quests](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/7)
- [Simulation: Two-node offline knowledge synchronization](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/8)
- [Research: Collective proof and verification workflow](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/9)
- [Research: Test the 8–27–51 mass-ratio pattern](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/10)
- [Simulation: 2D two-core/four-satellite exchange](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/11)
- [Performance: simulation throughput and convergence](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/12)
- [Validation: compare with reference He₂ potentials](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/13)
- [Earth Project: habitability simulator and regional learning quests](https://github.com/andreasschneider01989-source/Matrixgame-KI-Game-Kollektiv-Gamification-Ewigkeit/issues/15)

Issues define proposed next steps and acceptance criteria. They do not imply that the corresponding functionality is already implemented.

## Implementation Roadmap

The next development phase is intended to focus on:

1. Reviewing and identifying the most useful existing Java implementations.
2. Establishing a minimal, working simulation core.
3. Defining interfaces and data structures between components.
4. Integrating existing functionality incrementally.
5. Testing and documenting each integration step.
6. Gradually introducing the planned modular package structure.

The priority is to achieve a small, functional, verifiable system before expanding its complexity.

The architecture and package structure described in the following sections should therefore be understood as the intended direction of development.




# Matrixgame Simulator

## Kollektives KI-Spiel mit Physik, Bewusstsein und Gamification

**Version 1.0.0**

Ein hochmodularer, erweiterbarer Simulator, der Physik, Bewusstseinsentwicklung und Gamification-Elemente vereint. Perfekt für kollektives Spiel, Experimentieren und Prognostizieren.

---

## 🎮 Überblick

Der Matrixgame Simulator ist ein Java-basiertes Framework, das:

- **Physik-Simulation**: N-Körper-Gravitation, Kollisionen, Entropie und Relativität
- **Bewusstseins-Engine**: Hawkins-Bewusstseins-Skala mit dynamischer Entwicklung
- **Gamification**: Level, Erfahrung, Quests, Achievements und Belohnungen
- **Spieltheorie**: Entscheidungsbäume, Koalitionen und Vertrauensmodelle
- **Prognose**: Zukunftsvorhersagen mit Wahrscheinlichkeiten
- **Interaktive UI**: Swing-basierte Visualisierung mit Steuerung

---

## 📦 Struktur

```
matrixgame-simulator/
├── src/main/java/de/matrixgame/
│   ├── core/                   # Kernklassen
│   │   ├── MatrixGameCore.java      # Haupt-Simulationskern
│   │   ├── MatrixGameConfig.java     # Konfiguration
│   │   ├── Unit.java                # Einheit (physikalisches Objekt mit Bewusstsein)
│   │   ├── SimulationStatistics.java # Statistiken
│   │   └── GameStatePrediction.java # Prognosen
│   │
│   ├── physics/                 # Physik-Engine
│   │   ├── PhysicsEngine.java        # Gravitation, Kollisionen, Entropie
│   │   └── Vector3D.java            # 3D-Vektor
│   │
│   ├── consciousness/            # Bewusstseins-Engine
│   │   └── ConsciousnessEngine.java  # Hawkins-Skala, Modi, Entwicklung
│   │
│   ├── game/                    # Spiel-Logik
│   │   ├── QuestSystem.java         # Quests und Achievements
│   │   ├── RewardSystem.java        # Belohnungssystem
│   │   ├── DecisionTree.java        # Entscheidungsbäume
│   │   └── GameTheoryEngine.java    # Spieltheorie-Modelle
│   │
│   ├── api/                     # Programmierbare Schnittstelle
│   │   └── MatrixGameAPI.java       # Externe Steuerung
│   │
│   ├── ui/                      # Benutzeroberfläche
│   │   └── MatrixGameUI.java        # Swing-UI
│   │
│   └── util/                    # Hilfsklassen
│       └── MatrixGameUtils.java     # Utility-Methoden
│
├── pom.xml                      # Maven-Konfiguration
└── README.md                     # Diese Datei
```

---

## 🚀 Schnellstart

### Voraussetzungen

- Java 11 oder höher
- Maven (optional, für Build)

### Ausführung

#### 1. Konsolenanwendung (Test)

```bash
# Kompilieren und ausführen
javac -d bin src/main/java/de/matrixgame/core/*.java \
       src/main/java/de/matrixgame/physics/*.java \
       src/main/java/de/matrixgame/consciousness/*.java \
       src/main/java/de/matrixgame/game/*.java \
       src/main/java/de/matrixgame/api/*.java \
       src/main/java/de/matrixgame/util/*.java

java -cp bin de.matrixgame.core.MatrixGameCore
```

#### 2. Graphische Benutzeroberfläche

```bash
# UI starten
java -cp bin de.matrixgame.ui.MatrixGameUI
```

#### 3. Mit Maven

```bash
# Projekt bauen
mvn clean package

# Ausführen
java -jar target/matrixgame-simulator-1.0.0.jar
```
