const fs = require("fs");
const path = require("path");
const vm = require("vm");

const htmlPath = path.resolve(process.argv[2] || path.join(__dirname, "..", "Matrixgame_InfinityCanvas_GravityLab.html"));
const outerCount = process.argv[3] || "6";
const html = fs.readFileSync(htmlPath, "utf8");
const match = html.match(/<script>([\s\S]*?)<\/script>/);
if (!match) throw new Error("No inline JavaScript block found in Gravity Lab HTML");
const listeners = new Map();
const noop = () => {};
const ctx = new Proxy({
  createRadialGradient: () => ({ addColorStop: noop }),
  setTransform: noop, fillRect: noop, beginPath: noop, arc: noop,
  fill: noop, stroke: noop, moveTo: noop, lineTo: noop, closePath: noop,
  setLineDash: noop, fillText: noop, clearRect: noop,
}, {
  get(target, key) { return key in target ? target[key] : noop; },
  set(target, key, value) { target[key] = value; return true; },
});
function makeElement(id) {
  return {
    id,
    tagName: ["speedRange", "outerCount", "viewMode", "editMass", "editX", "editY", "editVx", "editVy", "editDt"].includes(id) ? "INPUT" : "DIV",
    style: {}, dataset: {}, classList: { add: noop, remove: noop },
    textContent: "", innerHTML: "", disabled: false,
    value: id === "outerCount" ? outerCount : id === "speedRange" ? "1" : id === "editMass" ? "18" : id === "editX" ? "72" : id === "editY" ? "0" : id === "editVx" ? "0" : id === "editVy" ? "0" : id === "editDt" ? "0.0008" : "",
    checked: true,
    addEventListener(type, cb) { listeners.set(id + ":" + type, cb); },
    setPointerCapture: noop,
    getBoundingClientRect: () => ({ width: 1400, height: 950 }),
  };
}
const elements = new Map();
const canvas = makeElement("space");
canvas.getContext = () => ctx;
const document = {
  body: { dataset: {} },
  getElementById(id) {
    if (id === "space") return canvas;
    if (!elements.has(id)) elements.set(id, makeElement(id));
    return elements.get(id);
  },
};
const window = { devicePixelRatio: 1, location: { search: "?smoke=1&outer=" + outerCount }, addEventListener: noop };
const runtime = {
  document, window, URLSearchParams, Math, Number, Array, Map, Object, String,
  clearTimeout: noop, setTimeout: () => 1, console,
};
vm.runInNewContext(match[1], runtime, { timeout: 15000 });

const result = document.body.dataset;
const api = window.__gravityLabTest;
if (result.smoke !== "PASS") throw new Error("Gravity simulation returned non-finite values");
if (result.bodyCount !== String(Number(outerCount) + 3)) {
  throw new Error("Expected one central body, two inner bodies and " + outerCount + " outer bodies");
}
if (!api) throw new Error("Gravity Lab test hooks were not exposed");

// A mass edit must change the calculated Newtonian acceleration of another body.
api.state.selectedId = "L1-O1";
api.syncReadouts();
const beforeAcceleration = api.acceleration(api.state.bodies)[1].x;
document.getElementById("editMass").value = "50000";
document.getElementById("editX").value = "82";
document.getElementById("editY").value = "3";
document.getElementById("editVx").value = "-4";
document.getElementById("editVy").value = "24";
document.getElementById("editDt").value = "0.0004";
listeners.get("applyEdit:click")();
const edited = api.state.bodies.find(b => b.id === "L1-O1");
if (edited.mass !== 50000 || edited.x !== 82 || edited.y !== 3 || edited.vx !== -4 || edited.vy !== 24) {
  throw new Error("Editor did not apply mass, position and velocity");
}
if (api.PHYS.dt !== 0.0004 || api.state.running) throw new Error("Editor did not pause or update dt");
const afterAcceleration = api.acceleration(api.state.bodies)[1].x;
if (Math.abs(afterAcceleration - beforeAcceleration) < 1e-10) {
  throw new Error("Changing a mass did not change the Newtonian acceleration field");
}

// Add a test mass at the last canvas pointer position and remove it again.
api.state.lastPointer = { x: 1050, y: 310 };
listeners.get("addBody:click")();
if (api.state.bodies.length !== Number(outerCount) + 4 || !api.state.selectedId.startsWith("T")) {
  throw new Error("Runtime test body was not added");
}
listeners.get("removeBody:click")();
if (api.state.bodies.length !== Number(outerCount) + 3 || api.state.selectedId !== null) {
  throw new Error("Runtime test body was not removed");
}
listeners.get("forces:change")({ target: { checked: true } });
listeners.get("vectors:change")({ target: { checked: true } });
const finite = api.state.bodies.every(b => [b.mass, b.x, b.y, b.vx, b.vy].every(Number.isFinite));
if (!finite) throw new Error("Non-finite body state after editor operations");

console.log(
  "PASS: 2 inner + " + outerCount + " outer; 2,000 Velocity-Verlet steps; " +
  "live mass/position/velocity/dt editor; Newton acceleration responds; add/remove test body; force and velocity vectors; " +
  "energy drift " + result.energyDriftPercent + "%"
);
