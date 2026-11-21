export const SUPPORTED_TYPES = ["Mission", "Quest", "KnowledgeNode", "Evidence", "SimulationRun", "Agent"];

export function validateDocument(doc) {
  const errors = [];
  if (!doc || typeof doc !== "object" || Array.isArray(doc)) return ["Document must be an object."];
  if (typeof doc.schemaVersion !== "string" || !doc.schemaVersion) errors.push("schemaVersion is required.");
  if (typeof doc.documentId !== "string" || !doc.documentId) errors.push("documentId is required.");
  if (typeof doc.title !== "string" || !doc.title) errors.push("title is required.");
  if (!Array.isArray(doc.objects)) errors.push("objects must be an array.");
  if (!Array.isArray(doc.relations)) errors.push("relations must be an array.");
  if (errors.length) return errors;
  const ids = new Set();
  for (const object of doc.objects) {
    if (!object || typeof object !== "object") { errors.push("Every object must be an object."); continue; }
    if (typeof object.id !== "string" || !object.id) errors.push("Every object needs a stable id.");
    else if (ids.has(object.id)) errors.push(`Duplicate object id: ${object.id}`);
    else ids.add(object.id);
    if (!SUPPORTED_TYPES.includes(object.type)) errors.push(`Unsupported object type: ${object.type}`);
    if (typeof object.title !== "string") errors.push(`Object ${object.id ?? "?"} needs a title.`);
    if (!Number.isFinite(object.x) || !Number.isFinite(object.y)) errors.push(`Object ${object.id ?? "?"} needs finite canvas coordinates.`);
  }
  for (const relation of doc.relations) {
    if (!ids.has(relation.from) || !ids.has(relation.to)) errors.push(`Relation ${relation.id ?? "?"} points to a missing object.`);
  }
  return errors;
}

export function parseDocument(text) {
  const doc = JSON.parse(text);
  const errors = validateDocument(doc);
  if (errors.length) throw new Error(errors.join("\n"));
  return doc;
}

export function serializeDocument(doc) {
  const errors = validateDocument(doc);
  if (errors.length) throw new Error(errors.join("\n"));
  return JSON.stringify(doc, null, 2);
}

export function searchObjects(doc, query) {
  const q = String(query ?? "").trim().toLocaleLowerCase();
  if (!q) return doc.objects;
  return doc.objects.filter(o => [o.id, o.type, o.title, o.description, o.status]
    .some(value => String(value ?? "").toLocaleLowerCase().includes(q)));
}
