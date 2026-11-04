const fs = require("fs");
const path = require("path");
const vm = require("vm");

const htmlPath = path.resolve(process.argv[2] || path.join(__dirname, "..", "Matrixgame_InfinityCanvas_GravityLab.html"));
const html = fs.readFileSync(htmlPath, "utf8");
const match = html.match(/<script>([\s\S]*?)<\/script>/);
if (!match) throw new Error("No inline JavaScript block found in Gravity Lab HTML");
const source = match[1];
const noop = () => {};
const ctx = new Proxy({
  createRadialGradient: () => ({ addColorStop: noop }),
  setTransform: noop, fillRect: noop, beginPath: noop, arc: noop,
  fill: noop, stroke: noop, moveTo: noop, lineTo: noop,
  setLineDash: noop, fillText: noop, clearRect: noop,
}, {
  get(target, key) { return key in target ? target[key] : noop; },
  set(target, key, value) { target[key] = value; return true; },
});
function makeElement(id) {
  return {
    id,
    tagName: ["speedRange", "outerCount", "viewMode"].includes(id) ? "INPUT" : "DIV",
    style: {}, dataset: {}, textContent: "", innerHTML: "", disabled: false,
    value: id === "outerCount" ? "6" : id === "speedRange" ? "1" : "",
    checked: true, addEventListener: noop, setPointerCapture: noop,
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
const window = { devicePixelRatio: 1, location: { search: "?smoke=1" }, addEventListener: noop };
const runtime = {
  document, window, URLSearchParams, Math, Number, Array, Map, Object, String,
  clearTimeout: noop, setTimeout: () => 1, console,
};
vm.runInNewContext(source, runtime, { timeout: 10000 });

const result = document.body.dataset;
if (result.smoke !== "PASS") throw new Error("Gravity simulation returned non-finite values");
if (result.bodyCount !== "9") throw new Error("Expected the 1 + 2 + 6 = 9 body baseline");
console.log(
  "PASS: 2 inner + 6 outer; 2,000 Velocity-Verlet steps; " +
  "relative energy drift " + result.energyDriftPercent + "%"
);
