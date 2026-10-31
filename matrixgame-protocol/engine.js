"use strict";

/**
 * Minimal deterministic Matrixgame engine.
 * Uses only built-in JavaScript features; no neural network is required.
 * This is a local prototype, not a production multi-user server.
 */

const METRIC_KEYS = [
  "wellbeing", "motivation", "cooperation", "transparency",
  "learning", "feasibility", "safety", "sustainability"
];

const PRIORITY_WEIGHTS = {
  wellbeing: 0.16,
  motivation: 0.08,
  cooperation: 0.14,
  transparency: 0.12,
  learning: 0.14,
  feasibility: 0.12,
  safety: 0.14,
  sustainability: 0.10
};

function clampScore(value) {
  if (!Number.isFinite(value)) throw new TypeError("Score must be a finite number.");
  return Math.max(0, Math.min(100, Math.round(value)));
}

function validateState(state) {
  if (!state || typeof state !== "object") throw new TypeError("State must be an object.");
  if (state.schema_version !== "1.0.0") throw new Error("Unsupported schema_version.");
  if (!state.metrics || !Array.isArray(state.quests) || !Array.isArray(state.events)) {
    throw new Error("State is missing metrics, quests or events.");
  }
  for (const key of METRIC_KEYS) {
    if (!Number.isInteger(state.metrics[key]) || state.metrics[key] < 0 || state.metrics[key] > 100) {
      throw new Error("Invalid metric: " + key);
    }
  }
  return true;
}

/**
 * Weighted progress indicator for reflection, not a measure of human value.
 * Safety is also a hard gate: a low safety score blocks a positive result.
 */
function calculateProgress(state) {
  validateState(state);
  const weighted = METRIC_KEYS.reduce(
    (sum, key) => sum + state.metrics[key] * PRIORITY_WEIGHTS[key], 0
  );
  const safetyGate = state.metrics.safety < 40 ? "BLOCKED_LOW_SAFETY" : "ELIGIBLE";
  return {
    score: Math.round(weighted * 10) / 10,
    safety_gate: safetyGate,
    components: Object.fromEntries(METRIC_KEYS.map(key => [key, state.metrics[key]])),
    note: "Game indicator only; not a clinical, scientific consciousness or human-worth measure."
  };
}

function applyMetricChange(state, metric, delta, actorId = "unknown") {
  validateState(state);
  if (!METRIC_KEYS.includes(metric)) throw new Error("Unknown metric: " + metric);
  if (!Number.isFinite(delta) || Math.abs(delta) > 20) {
    throw new Error("delta must be a finite number between -20 and 20.");
  }

  const next = structuredClone(state);
  const before = next.metrics[metric];
  next.metrics[metric] = clampScore(before + delta);
  next.updated_at = new Date().toISOString();
  next.events.push({
    event_id: "event-" + (next.events.length + 1),
    timestamp: next.updated_at,
    type: "result",
    epistemic_status: "MODEL",
    actor_id: actorId,
    summary: metric + " changed from " + before + " to " + next.metrics[metric] + " by a proposed game action.",
    evidence: []
  });
  return next;
}

function setQuestStatus(state, questId, status, actorId = "unknown") {
  validateState(state);
  const allowed = ["proposed", "active", "blocked", "completed", "paused"];
  if (!allowed.includes(status)) throw new Error("Invalid quest status.");
  const next = structuredClone(state);
  const quest = next.quests.find(item => item.quest_id === questId);
  if (!quest) throw new Error("Quest not found: " + questId);
  const previous = quest.status;
  quest.status = status;
  next.updated_at = new Date().toISOString();
  next.events.push({
    event_id: "event-" + (next.events.length + 1),
    timestamp: next.updated_at,
    type: "decision",
    epistemic_status: "FACT",
    actor_id: actorId,
    summary: "Quest " + questId + " status changed from " + previous + " to " + status + ".",
    evidence: []
  });
  return next;
}

module.exports = {
  METRIC_KEYS,
  validateState,
  calculateProgress,
  applyMetricChange,
  setQuestStatus
};
