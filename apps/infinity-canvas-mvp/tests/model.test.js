import test from "node:test";
import assert from "node:assert/strict";
import { parseDocument, serializeDocument, searchObjects, validateDocument } from "../src/model.js";
import { readFile } from "node:fs/promises";

const fixture = JSON.parse(await readFile(new URL("../data/matrixgame-canvas-demo.v1.json", import.meta.url), "utf8"));

test("demo fixture validates and contains all six typed object kinds", () => {
  assert.deepEqual(validateDocument(fixture), []);
  assert.deepEqual(new Set(fixture.objects.map(o => o.type)), new Set(["Mission","Quest","KnowledgeNode","Evidence","SimulationRun","Agent"]));
});

test("JSON round-trip preserves IDs, provenance, and extension fields", () => {
  const roundTrip = parseDocument(serializeDocument(fixture));
  assert.deepEqual(roundTrip, fixture);
  assert.equal(roundTrip.extensions["x-matrixgame-demo"], true);
});

test("invalid duplicate IDs and broken relations are rejected", () => {
  const broken = structuredClone(fixture);
  broken.objects.push({...broken.objects[0]});
  assert.ok(validateDocument(broken).some(e => e.includes("Duplicate object id")));
  const brokenRelation = structuredClone(fixture);
  brokenRelation.relations[0].to = "missing";
  assert.ok(validateDocument(brokenRelation).some(e => e.includes("missing object")));
});

test("search filters title, type, and description without mutating the document", () => {
  assert.equal(searchObjects(fixture, "Excalidraw").length, 1);
  assert.equal(searchObjects(fixture, "snapshot").length, 1);
  assert.equal(searchObjects(fixture, "").length, fixture.objects.length);
});
