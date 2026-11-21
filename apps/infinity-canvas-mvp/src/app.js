import { parseDocument, searchObjects, serializeDocument } from "./model.js";

const canvas = document.querySelector("#world");
const ctx = canvas.getContext("2d");
const inspector = document.querySelector("#inspector");
const objectList = document.querySelector("#object-list");
const search = document.querySelector("#search");
const status = document.querySelector("#status");
const colors = { Mission:"#63d7ff", Quest:"#a78bfa", KnowledgeNode:"#64d99b", Evidence:"#f6c85f", SimulationRun:"#ff9671", Agent:"#f08ac7" };
let doc;
let camera = { x: innerWidth / 2, y: innerHeight / 2, zoom: 1 };
let selectedId = null;
let drag = null;
let visibleObjects = [];

function resize() {
  const dpr = Math.max(1, window.devicePixelRatio || 1);
  canvas.width = Math.floor(canvas.clientWidth * dpr);
  canvas.height = Math.floor(canvas.clientHeight * dpr);
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  draw();
}
function toScreen(o) { return { x: o.x * camera.zoom + camera.x, y: o.y * camera.zoom + camera.y }; }
function draw() {
  if (!doc) return;
  const w = canvas.clientWidth, h = canvas.clientHeight;
  ctx.clearRect(0, 0, w, h);
  ctx.fillStyle = "#08111f"; ctx.fillRect(0, 0, w, h);
  const step = Math.max(24, 72 * camera.zoom);
  ctx.strokeStyle = "rgba(145,180,220,.09)"; ctx.lineWidth = 1; ctx.beginPath();
  const ox = ((camera.x % step) + step) % step, oy = ((camera.y % step) + step) % step;
  for (let x = ox; x < w; x += step) { ctx.moveTo(x,0); ctx.lineTo(x,h); }
  for (let y = oy; y < h; y += step) { ctx.moveTo(0,y); ctx.lineTo(w,y); }
  ctx.stroke();
  const byId = new Map(doc.objects.map(o => [o.id,o]));
  for (const r of doc.relations) {
    const a=byId.get(r.from), b=byId.get(r.to); if(!a||!b) continue;
    const p=toScreen(a), q=toScreen(b);
    ctx.strokeStyle="rgba(153,183,220,.34)"; ctx.lineWidth=1.5; ctx.beginPath(); ctx.moveTo(p.x,p.y); ctx.lineTo(q.x,q.y); ctx.stroke();
    ctx.fillStyle="#9fb4ce"; ctx.font="11px system-ui"; ctx.fillText(r.label || "", (p.x+q.x)/2+5,(p.y+q.y)/2-5);
  }
  visibleObjects = searchObjects(doc, search.value);
  for (const o of visibleObjects) {
    const p=toScreen(o), radius=Math.max(7,13*camera.zoom);
    if(p.x < -180 || p.y < -80 || p.x>w+180 || p.y>h+80) continue;
    ctx.beginPath(); ctx.arc(p.x,p.y,radius,0,Math.PI*2);
    ctx.fillStyle=colors[o.type] || "#fff"; ctx.fill();
    ctx.lineWidth=o.id===selectedId?4:1.5; ctx.strokeStyle=o.id===selectedId?"#fff":"rgba(255,255,255,.5)"; ctx.stroke();
    ctx.fillStyle="#edf5ff"; ctx.font="12px system-ui"; ctx.textAlign="center";
    ctx.fillText(o.title,p.x,p.y+radius+17); ctx.textAlign="start";
  }
  document.querySelector("#zoom").textContent=(camera.zoom*100).toFixed(0)+"%";
  document.querySelector("#count").textContent=visibleObjects.length+" / "+doc.objects.length+" objects";
  renderList();
}
function renderList() {
  objectList.replaceChildren();
  for (const o of visibleObjects) {
    const b=document.createElement("button"); b.className="object-row"; b.type="button";
    b.textContent=o.type+" · "+o.title; b.setAttribute("aria-pressed",String(o.id===selectedId));
    b.addEventListener("click",()=>select(o.id)); objectList.append(b);
  }
}
function select(id) {
  selectedId=id; const o=doc.objects.find(x=>x.id===id);
  inspector.replaceChildren();
  if(!o){ inspector.textContent="Select an object to inspect it."; draw(); return; }
  const h=document.createElement("h3"); h.textContent=o.title; inspector.append(h);
  for(const [k,v] of Object.entries(o)) {
    const p=document.createElement("p"); const strong=document.createElement("strong"); strong.textContent=k+": ";
    const span=document.createElement("span"); span.textContent=typeof v==="object"?JSON.stringify(v):String(v);
    p.append(strong,span); inspector.append(p);
  }
  draw();
}
function setDocument(next) {
  doc=next; selectedId=null; select(null);
  status.textContent="Loaded "+doc.objects.length+" typed objects · schema "+doc.schemaVersion;
}
async function loadFixture() {
  const res=await fetch("./data/matrixgame-canvas-demo.v1.json");
  if(!res.ok) throw new Error("Could not load demo fixture ("+res.status+")");
  setDocument(parseDocument(await res.text()));
}
function download() {
  const blob=new Blob([serializeDocument(doc)],{type:"application/json"});
  const url=URL.createObjectURL(blob); const a=document.createElement("a");
  a.href=url; a.download=doc.documentId+".json"; a.click(); URL.revokeObjectURL(url);
}
function screenToWorld(x,y){return {x:(x-camera.x)/camera.zoom,y:(y-camera.y)/camera.zoom};}
canvas.addEventListener("pointerdown",e=>{
  canvas.setPointerCapture(e.pointerId);
  const rect=canvas.getBoundingClientRect(), x=e.clientX-rect.left,y=e.clientY-rect.top;
  const hit=[...visibleObjects].reverse().find(o=>{const p=toScreen(o);return Math.hypot(p.x-x,p.y-y)<Math.max(16,20*camera.zoom);});
  if(hit){select(hit.id);drag={kind:"object",id:hit.id,lastX:x,lastY:y};}
  else drag={kind:"pan",lastX:x,lastY:y};
});
canvas.addEventListener("pointermove",e=>{
  if(!drag)return; const r=canvas.getBoundingClientRect(),x=e.clientX-r.left,y=e.clientY-r.top;
  const dx=x-drag.lastX,dy=y-drag.lastY;
  if(drag.kind==="pan"){camera.x+=dx;camera.y+=dy;}
  else {const o=doc.objects.find(v=>v.id===drag.id);if(o){o.x+=dx/camera.zoom;o.y+=dy/camera.zoom;}}
  drag.lastX=x;drag.lastY=y;draw();
});
canvas.addEventListener("pointerup",()=>drag=null);
canvas.addEventListener("pointercancel",()=>drag=null);
canvas.addEventListener("wheel",e=>{
  e.preventDefault(); const r=canvas.getBoundingClientRect(),sx=e.clientX-r.left,sy=e.clientY-r.top;
  const before=screenToWorld(sx,sy); camera.zoom=Math.min(4,Math.max(.15,camera.zoom*Math.exp(-e.deltaY*.001)));
  camera.x=sx-before.x*camera.zoom;camera.y=sy-before.y*camera.zoom;draw();
},{passive:false});
search.addEventListener("input",draw);
document.querySelector("#save").addEventListener("click",download);
document.querySelector("#load").addEventListener("click",()=>document.querySelector("#file").click());
document.querySelector("#file").addEventListener("change",async e=>{
  const f=e.target.files?.[0];if(!f)return;
  try{setDocument(parseDocument(await f.text()));status.textContent="Imported "+f.name;}
  catch(err){status.textContent="Import rejected: "+err.message;}
  e.target.value="";
});
document.querySelector("#reset").addEventListener("click",async()=>{camera={x:innerWidth/2,y:innerHeight/2,zoom:1};await loadFixture();draw();});
window.addEventListener("resize",resize);
window.addEventListener("keydown",e=>{if(e.key==="Escape")select(null);if((e.key==="+"||e.key==="=")&&!e.target.matches("input")){camera.zoom=Math.min(4,camera.zoom*1.15);draw();}if(e.key==="-"&&!e.target.matches("input")){camera.zoom=Math.max(.15,camera.zoom/1.15);draw();}});
loadFixture().then(resize).catch(e=>{status.textContent=e.message;});
