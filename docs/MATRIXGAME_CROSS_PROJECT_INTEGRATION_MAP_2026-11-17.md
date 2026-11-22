# Matrixgame Cross-Project Integration Map
**Stand:** 17.11.2026  
**Branch:** expanded-world-genetic-lab-v08  
**Zweck:** Vorhandene Projekte auffindbar halten und schrittweise über gemeinsame Daten-, Modell- und Experimentverträge verbinden.

## 1. Leitidee

Matrixgame ist kein einzelnes Spiel, sondern ein modulares Ökosystem aus einer gemeinsamen Agenten- und Aktionsbasis, verschiedenen Simulationsdomänen, LUMINI als Modellierungssprache, reproduzierbaren Experimenten, Wissenspflege, Visualisierung und freiwilligen Lern-/Zukunftsszenarien.

Die Module sollen nicht in eine einzige Engine gezwungen werden. Sie teilen stabile Verträge und werden über Adapter gekoppelt.

## 2. Bestandskarte

| Modul | Vorhandener Bestand | Status bei dieser Sichtung | Nächste Verbindung |
|---|---|---|---|
| Universal Grid Lab | v0.8: 120×80 Grid, 37 Aktionen, genetische Sensor-/Operationsgene, Evolution | lokaler Prototyp; Build und 57 Selbsttests dokumentiert; vollständige Quelldatei noch nicht als .java im Repository | Referenzadapter für diskrete Grid-Agenten; Quelle in GitHub übernehmen |
| Matrix Core | Java 21: World, Agent, Resource, Message, Simulation, Experiment, Result, Claim, PAL; Allmende-Arena | Codekern und dokumentierte Selbsttests/200-Seed-Arena | Strategie-Benchmarks und Experimente mit Grid Lab vergleichen |
| Gravity / Infinity Canvas | ältere Java-Swing-MainGame-Klasse mit 3D-Positionen, Geschwindigkeiten, Masse, Energie, variablen Achsen, Zoom/Pan | konkreter historischer Prototyp; getrennt vom Grid Lab | kontinuierlichen Physik-Adapter mit Einheiten- und numerischen Tests bauen |
| GravityEngine / M1–M4 Fusion | Projektjournal nennt N-Body, Softening, Kraftbegrenzung, Kollision/Akkretion, Fraktalzerfall und Kamera-Hub | historische Projektdokumentation; GravityEngine-Quellstand bei dieser Suche nicht als eigenständige Datei vollständig verifiziert | Quelle lokalisieren, kompilieren, testen, erst dann integrieren |
| LUMINI | Sprach-, Compiler-, Model-IR- und Engine-Adapter-Entwurf | Spezifikation; vollständiger Compiler/Parser laut Dokument noch nicht implementiert | kleine formale Teilgrammatik und JSON-IR zuerst |
| Karriere-/Zukunfts-Szenarioengine | LUMINI-Beispiel mit Skills, Berufswegen, regionalem Marktsnapshot und drei Szenarien | konzeptioneller Entwurf; kein kalibrierter Prognosemotor | privates Profil + öffentliche Daten-Snapshots + Szenariovergleich |
| Universal Knowledge Engine | Wissensknoten, Evidenz, Beziehungen, Versionierung, Agentenrollen, Lernpfade | Architekturspezifikation; Implementation pending | Dokumentations- und Provenienzschicht verwenden |
| Kollektives Bewusstsein / 11 Welten / Neuronen | Journale beschreiben 6-Port-Neuronen, Reward-Lernen, Zellteilung, 11 Welten, Harmonie und M1–M4-Fusionen | historische Artefakte und frühere Prototypen; Status je Modul einzeln erneut prüfen | als optionale Agenten-/Weltmodule katalogisieren |
| LUMINI-Kommunikation / Team-KI-Games | Kommunikations-, Übersetzungs-, Rollen- und Perspektivenideen | Entwürfe und historische Chats/Prototypen | strukturiertes Message-Objekt mit Herkunft, Zeitstempel und Gegenprüfung |

### Fundstellen im Repository

- [LUMINI Compiler, Simulation und Career Engine](LUMINI_COMPILER_SIMULATION_AND_CAREER_ENGINE_2026-11-07.md)
- [Universal Knowledge Engine](UNIVERSAL_KNOWLEDGE_ENGINE.md)
- [Knowledge Node and Evidence Schema](KNOWLEDGE_NODE_AND_EVIDENCE_SCHEMA.md)
- [Matrix Core README](../matrixgame-core/matrixgame-core/README.md)
- [Matrix Core Architektur](../matrixgame-core/matrixgame-core/docs/ARCHITEKTUR.md)
- [Matrix Core Arena-Ergebnisse](../matrixgame-core/matrixgame-core/results/ARENA.md)
- [Infinite Canvas / Dual Interface / Quality Model](INFINITE_CANVAS_DUAL_INTERFACE_AND_QUALITY_MODEL_2026-11-11.md)
- [Historisches Matrixgame- und Kollektivbewusstseins-Journal](../Matrixgam%20und%20Kollektives%20Bewusstsein%20und%20KI-Game12.11.2026.md)
- [Historischer Java Gravity / Entropy / Infinity Canvas Prototyp](../MatrixgameKollektivKI-GameInfinitCanvasJavaGravityEntropieGameGeminiAndi10092026zuHause)

## 3. Matrix Core als Experiment-Prüfstand

Der Matrix Core beschreibt bereits die Schleife:

Observation → Agent.decide → Action → World.step → Feedback → Agent.learn

und die Systemformel:

Zustand + Regeln + Zufall + Rückkopplung + Zeit.

Die dokumentierte Allmende-Arena verwendet 200 Seeds und vier Strategien. Die Ergebnisse enthalten wichtige Gegenbeispiele:

- Reine N-Max-Max-Population gegen reine Greedy-Population: N-Max-Max schneidet in diesem Modell deutlich besser ab.
- Gemischte Population: Die einfache N-Max-Max-Strategie wird von Greedy-Agenten ausgebeutet und lebt kürzer.
- Vergleich mit Reziprozität: die einfache N-Max-Max-Population erreicht nicht die höchste Wohlfahrt.
- Negativkontrolle: keine klare Differenz wie erwartet.

Das ist ein Modellbefund, kein allgemeiner Beweis über Kooperation in realen Gesellschaften. Für die nächste KI-Evolution ist er eine Benchmark-Aufgabe: Robustheit gegen Ausbeutung, Reziprozität, Reputation, Teamkommunikation und adaptive Strategien vergleichen.

## 4. Gemeinsame Architektur: ein Vertrag, mehrere Engines

1. **Domain Model:** Typ, Variablen, Einheiten, Fähigkeiten, Grenzen und wissenschaftlicher Status.
2. **Observation:** Was ein Agent in seiner Domäne tatsächlich wahrnehmen darf.
3. **Action Space:** Endliche, typisierte Aktionen oder kontinuierliche Steuergrößen; jede Domäne deklariert ihre Legalitätsregeln.
4. **Decision Policy:** Greedy, Random, adaptive Softmax, genetische Policy, neuronales Netz, später Minimax oder Monte Carlo.
5. **Resolver / Engine:** Prüft und verarbeitet Aktionen in definierter Reihenfolge.
6. **Feedback / Metrics:** Ergebnis, Kosten, Erfolg, Nebenwirkungen, Fitness und Unsicherheit.
7. **Run Manifest:** Modellversion, Quell-Commit, Parameter, Seed, Daten-Hashes, Softwareversion und Tests.
8. **View Adapter:** Canvas, Diagramme, Textbericht, JSONL und Tabellen.

### Domänenspezifische Verträge

- **Grid / Agenten:** diskrete Ticks, Zellen, Nachbarschaft und legale Aktion aus festem Aktionsraum.
- **Gravitation:** kontinuierliche Zustände, SI-Einheiten, ForceModel, Integrator, CollisionModel, Randbedingungen und Erhaltungsgrößen.
- **Ökologie / System Dynamics:** Bestände, Flüsse, Regeneration, Kapazitäten und Verzögerungen.
- **Kommunikation / LUMINI:** strukturierte Nachrichten, Parser, Typ-/Einheitenprüfung, Model IR und freigegebene Adapter.
- **Zukunftsszenarien:** Akteure, Ressourcen, Annahmen, Bedingungen, alternative Pfade und Unsicherheitsbereiche.

Keine Engine soll stillschweigend den Zustand oder die Einheiten einer anderen Domäne umdeuten.

## 5. LUMINI als Verbindung

Die vorgesehene Kette:

Natürliche Sprache → .lumini → Lexer/Parser → AST → Semantik-/Einheitenprüfung → Model IR (JSON) → Engine-Adapter → Simulation → Messwerte/Unsicherheit/Provenienz.

LUMINI wird zur gemeinsamen Modellbeschreibung. Java, JavaScript oder Python können Adapter implementieren. Ein Sprachmodell kann beim Formulieren helfen, ersetzt aber weder Parser, Tests, numerische Validierung noch menschliche Freigabe.

Empfohlene Reihenfolge:
1. JSON-Model-IR-Schema mit Version und Typen.
2. Kleines LUMINI-Beispiel parsen und in IR übersetzen.
3. Matrix-Core-Allmende als ersten Adapter wiederholen.
4. Einfaches Newton-/Kepler-Modell mit Einheiten- und Erhaltungschecks als Physik-Adapter.
5. Grid-Agenten-Adapter für Universal Grid Lab.
6. Erst danach Career Map und weitere Domänen.

## 6. Gravitationssimulation: Prüfungen vor Integration

Die LUMINI-Spezifikation benennt konkrete Altlasten des älteren PhysicsEngine-Stands:

- Ein Entropiefaktor verändert die Newton-Kraft: als Spielvariante kennzeichnen, nicht als unveränderte Newtonsche Gravitation.
- Ein festgehaltener Körper muss weiterhin als Gravitationsquelle wirken können.
- Semi-impliziter Euler ist ein Einstieg; Bahn- und Langzeitfehler müssen gemessen werden.
- Ein Geschwindigkeits-Cap ist keine relativistische Dynamik.
- Softening und Kraftlimit müssen dokumentierte Modellparameter sein.

Erste Tests:
- Zweikörper-Kreisbahn / analytischer Vergleich,
- Energie- und Impulsdrift,
- Konvergenz beim Verkleinern des Zeitschritts,
- Nahbegegnung und Softening-Sensitivität,
- Kollision und Akkretion als getrennte Varianten,
- reproduzierbare Seeds und Parameter.

Die vorhandene 2D/3D-Gravity-Sandbox kann Visualisierung und Interaktion beisteuern. Die Physik-Engine muss unabhängig von der Kamera getestet werden.

## 7. Persönliche Zukunfts- und Karriereplanung: privat und nicht deterministisch

Der bestehende LUMINI-Entwurf beschreibt bereits ein Karriere-Szenario-Lab mit Skills und Kompetenznachweisen, Berufsprofilen, Zugangsvoraussetzungen, Lernangeboten, regionalen Arbeitsmarktdaten, Zeit-/Lernbudget, drei Szenarien sowie Skill-Gap-Map und Portfolio-Roadmap.

Das kann als persönliches Entscheidungsunterstützungsmodul dienen. Es soll Optionen, Bedingungen, Lernschritte und Unsicherheiten sichtbar machen – keine angeblich präzise Zukunft oder Jobzusage ausgeben.

**Datenschutzgrenze:**
- Persönliches Profil, private Lernnotizen und Bewerbungsdaten lokal oder in privatem Speicher halten.
- Keine persönlichen Gesundheitsdaten, privaten Nachrichten oder privaten Gesprächsexporte in öffentliche GitHub-Dateien übernehmen.
- Arbeitsmarktdaten nur mit Quelle, Region, Zeitraum, Abrufdatum und Nutzungsbedingungen verwenden.
- Persönliche Variablen freiwillig und mit klarer Herkunft übernehmen.
- Psychologische Selbsteinschätzungen als subjektive Angaben oder Spielvariablen kennzeichnen, nicht als Diagnose oder objektive Messung.

Vorhersagen sollten Szenarien mit Annahmen und Unsicherheit sein. Eine numerische Eintrittswahrscheinlichkeit ist erst vertretbar, wenn ein geeignetes Modell kalibriert und auf unabhängigen Daten validiert wurde.

## 8. Gemeinsame Wissens- und Evidenzregeln

Jedes Modul bekommt zwei getrennte Statusachsen:

- **Aussagenstatus:** FACT / MODEL / HYPOTHESIS / OPEN / FICTION.
- **Softwarestatus:** PLANNED / PROTOTYPE / IMPLEMENTED / TESTED / VERIFIED.

Ein erfolgreicher Compile belegt keine wissenschaftliche Gültigkeit. Ein reproduzierbarer Modelllauf beweist nicht, dass das Modell reale Menschen, Gesellschaften oder Naturprozesse korrekt vorhersagt.

Jeder Lauf sollte Frage/Hypothese, Modellannahmen, Parameter/Einheiten, Seed/Commit, Testmethode/Umgebung, Ergebnis einschließlich negativer Resultate und offene Grenzen enthalten.

## 9. Integrationsfahrplan

### A – Kontinuität sichern
- v0.8 vollständige Java-Quelldatei als echte .java-Datei ins Repository übernehmen.
- ZIP, README, Self-Test-Ausgabe und Run Manifest versionieren.
- Matrix Core und Grid Lab auf Schnittstellenebene vergleichen.

### B – Gemeinsame Experimentdaten
- Agent-ID, Observation, Action, Feedback, Tick, Seed und Genome-Metadaten normieren.
- JSONL-Event-Vertrag definieren.
- Populations-, Strategie- und Physikresultate über denselben Run-Manifest-Vertrag auffindbar machen.

### C – Erstes Cross-Engine-Experiment
- Kleine Allmende-Umwelt mit Ressourcenregeneration.
- Grid-Agenten und Matrix-Core-Strategien als getrennte Policies vergleichen.
- Dieselben Seeds, Ressourcenregeln und Metriken verwenden.
- Ergebnis durch Tests und unabhängige Wiederholung prüfen.

### D – Physik und Visualisierung
- Gravity-Quellstände lokalisieren und gegen dokumentierte Tests prüfen.
- Physik-Adapter getrennt vom Grid-Resolver halten.
- Gemeinsame Kamera/Canvas-Projektion als UI-Modul wiederverwenden.

### E – LUMINI / Career / Collective Knowledge
- Model IR und Knowledge Nodes minimal implementieren.
- Szenarien und private Career Map anschließen.
- Kommunikation und Team-Agenten über strukturierte Message-Objekte anbinden.

## 10. Offene Fragen für spätere Runden

1. Wo liegt der eigenständige aktuelle Quellstand von GravityEngine.java?
2. Welcher frühere M1/M2/M4-Prototyp soll zuerst erneut kompiliert und übernommen werden?
3. Soll der Matrix Core langfristig der Experiment-Backbone werden, oder behält Universal Grid Lab einen eigenen Runtime-Kern und implementiert nur dieselben Verträge?
4. Welche Teile des historischen LUMINI-Vokabulars sind bereits Code/Parser, welche nur Formelsammlung oder Textentwurf?
5. Welche persönlichen Daten sollen überhaupt in einer lokalen Career Map erscheinen? Öffentliche Repositories erhalten nur generische Schemas und nicht-private Beispieldaten.

## Quellenstatus

Diese Karte basiert auf am 17.11.2026 auffindbaren Spezifikationen, Quelltexten und historischen Projektjournalen. Historische Zusammenfassungen sind Wegweiser, nicht Ersatz für Quellcode. Vor Integration sind konkrete Datei, Build, Tests, Lizenz und Datenfreigabe erneut zu prüfen.

**Ziel:** Ein gemeinsamer modularer Rahmen, in dem Grid-Evolution, Gravitation, LUMINI, kollektive Agenten, Wissenspflege und persönliche Zukunftsszenarien voneinander lernen können, ohne ihre unterschiedlichen Gültigkeitsregeln zu vermischen.
