const fs = require('fs');
const path = require('path');
const vm = require('vm');

const htmlPath = path.resolve(process.argv[2] || path.join(__dirname, 'Matrixgame_GravityClusterLab_2D.html'));
const html = fs.readFileSync(htmlPath, 'utf8');
const start = html.indexOf('<script>') + '<script>'.length;
const end = html.lastIndexOf('</script>');
if (start < 8 || end < start) throw new Error('Inline simulator script missing');
const source = html.slice(start, end);
const noop = () => {};
const canvasContext = new Proxy({}, {
  get(target, key) { return key in target ? target[key] : noop; },
  set(target, key, value) { target[key] = value; return true; },
});
const elements = new Map();
function element(id) {
  if (!elements.has(id)) elements.set(id, {
    id, tagName: ['speed','impact','phase','separation','trails','forces'].includes(id) ? 'INPUT' : 'DIV',
    value: ({speed:'1',impact:'29',phase:'46',separation:'40'})[id] || '',
    checked:true, hidden:false, disabled:false, textContent:'', innerHTML:'',
    classList:{toggle:noop},
    addEventListener:noop,
    getBoundingClientRect:()=>({width:1600,height:950}),
  });
  return elements.get(id);
}
const canvas=element('view');
canvas.getContext=()=>canvasContext;
const document={body:{dataset:{}},getElementById:element,createElement:()=>({click:noop})};
const window={innerWidth:1600,devicePixelRatio:1,addEventListener:noop};
const runtime={
  document,window,URLSearchParams,Math,Number,Array,Object,String,Map,JSON,Blob,URL,
  clearTimeout:noop,setTimeout:()=>1,requestAnimationFrame:noop,console,
};
vm.runInNewContext(source,runtime,{timeout:20000});
const api=window.__gravityClusterTest;
if(!api)throw new Error('Test API missing');

// Same total cluster mass, same initial impact geometry; only satellite count differs.
api.S.trails=false;
for(let step=0;step<26000;step++)for(const lane of api.S.lanes)api.oneStep(lane);
const encounter=api.S.lanes.map(l=>({
  satellites:l.n,minDistance:l.minDistance,mergeCount:l.mergeCount,
  mergeTime:l.mergeTime,coreCount:l.bodies.filter(b=>b.kind==='core').length,passed:l.passed
}));
if(encounter[0].mergeCount!==0||encounter[0].minDistance<8.2)throw new Error('Zero-satellite baseline unexpectedly merged');
if(encounter[1].mergeCount!==1||encounter[1].coreCount!==1)throw new Error('One-satellite lane should trigger the baseline merger proxy');
if(encounter[2].mergeCount!==0||encounter[2].minDistance<8.0||encounter[2].minDistance>8.1)throw new Error('Two-satellite lane did not match the documented close flyby baseline');

// The merged system keeps evolving: both satellites remain and all state stays finite.
for(let step=0;step<10000;step++)for(const lane of api.S.lanes)api.oneStep(lane);
const mergedLane=api.S.lanes[1];
const mergedCore=mergedLane.bodies.find(b=>b.kind==='core');
const remaining=mergedLane.bodies.filter(b=>b.kind==='sat');
if(!mergedCore||mergedCore.mass!==192||remaining.length!==2)throw new Error('Post-merger dynamics lost the combined core or satellites');
if(!mergedLane.bodies.every(b=>[b.mass,b.x,b.y,b.vx,b.vy].every(Number.isFinite)))throw new Error('Post-merger state became non-finite');

// The inelastic core merger conserves total mass and linear momentum by construction.
const a={mass:60,x:-3,y:2,vx:1,vy:2,kind:'core',radius:4,trail:[]};
const b={mass:40,x:3,y:-1,vx:-2,vy:1,kind:'core',radius:4,trail:[]};
const lane={bodies:[a,b],time:0,mergeCount:0,mergeTime:null};
api.mergeCores(lane,a,b);
const merged=lane.bodies[0];
if(merged.mass!==100||Math.abs(merged.vx+0.2)>1e-12||Math.abs(merged.vy-1.6)>1e-12)throw new Error('Merger did not conserve mass and momentum');

// Bound binary-core mode remains finite for all three satellite counts.
api.P.mode='binary';api.P.separation=40;api.reset();
const ranges=api.S.lanes.map(()=>({min:Infinity,max:0}));
for(let step=0;step<20000;step++)for(let i=0;i<3;i++){
  const l=api.S.lanes[i];api.oneStep(l);
  const cores=l.bodies.filter(x=>x.kind==='core');
  if(cores.length!==2)throw new Error('Binary lane merged unexpectedly during short validation');
  const d=Math.hypot(cores[0].x-cores[1].x,cores[0].y-cores[1].y);
  ranges[i].min=Math.min(ranges[i].min,d);ranges[i].max=Math.max(ranges[i].max,d);
}
for(const l of api.S.lanes)if(!l.bodies.every(b=>[b.mass,b.x,b.y,b.vx,b.vy].every(Number.isFinite)))throw new Error('Non-finite binary state');

console.log(
 'PASS: encounter 0/1/2 satellites; min distances '+encounter.map(x=>x.minDistance.toFixed(3)).join('/')+
 '; merger counts '+encounter.map(x=>x.mergeCount).join('/')+
 '; post-merger dynamics; mass/momentum conservation; binary orbit 20,000 steps; ranges '+
 ranges.map(x=>x.min.toFixed(3)+'–'+x.max.toFixed(3)).join(' / ')
);
