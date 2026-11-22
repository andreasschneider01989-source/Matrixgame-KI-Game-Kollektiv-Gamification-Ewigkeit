const fs = require('fs');
const path = require('path');
const vm = require('vm');
const htmlPath = path.resolve(process.argv[2] || path.join(__dirname, 'GravityLab3D.html'));
const outer = process.argv[3] || '6';
const html = fs.readFileSync(htmlPath, 'utf8');
const start = html.indexOf('<script>') + '<script>'.length;
const end = html.lastIndexOf('</script>');
if (start < 8 || end < start) throw new Error('Inline script missing');
const source = html.slice(start, end);
const listeners = new Map();
const noop = () => {};
const drawing = new Proxy({createRadialGradient: () => ({addColorStop: noop})}, {
  get(t,k) { return k in t ? t[k] : noop; },
  set(t,k,v) { t[k]=v; return true; }
});
const els = new Map();
function el(id) {
  if (!els.has(id)) els.set(id, {
    id, tagName: ['speed','outer','repel','mass','x','y','z','vx','vy','vz','dt','file'].includes(id) ? 'INPUT' : 'DIV',
    value: ({outer, speed:'1', repel:'0', mass:'18', x:'72', y:'0', z:'0', vx:'0', vy:'0', vz:'0', dt:'0.0008'})[id] || '',
    checked:true, disabled:false, textContent:'', innerHTML:'', dataset:{}, classList:{add:noop,remove:noop},
    addEventListener(type,cb){listeners.set(id+':'+type,cb)}, click:noop,
    getBoundingClientRect:()=>({width:1400,height:900}), setPointerCapture:noop
  });
  return els.get(id);
}
const canvas=el('space');canvas.getContext=()=>drawing;
const document={body:{dataset:{}},getElementById:el,createElement:()=>({click:noop})};
const window={devicePixelRatio:1,addEventListener:noop};
const runtime={document,window,location:{search:'?smoke=1&outer='+outer},URLSearchParams,Math,Number,Array,Object,String,Map,JSON,Blob,URL,console,clearTimeout:noop,setTimeout:()=>1,requestAnimationFrame:noop,devicePixelRatio:1};
vm.runInNewContext(source,runtime,{timeout:20000});
const api=window.__gravity3DTest;
if(!api) throw new Error('Test API missing');
if(document.body.dataset.smoke!=='PASS') throw new Error('3D integration produced non-finite values');
if(String(document.body.dataset.count)!==String(Number(outer)+3)) throw new Error('Incorrect body count');
const names=api.S.bodies.map(b=>b.id);
if(!names.includes('outerXY+')||!names.includes('outerXZ+')||!names.includes('outerYZ+')) throw new Error('Three orbital planes not initialized');
api.createSystem();
const score=api.symmetryScore();
if(!Number.isFinite(score)||score<0||score>100) throw new Error('Symmetry score is outside 0..100');
if(Number(outer)===6&&score<99.9) throw new Error('The exact 2+6 baseline should have near-perfect geometric symmetry');
api.S.selected='outerXZ+';api.S.running=false;
const target=api.S.bodies.find(b=>b.id==='outerXZ+');
const otherIndex=api.S.bodies.findIndex(b=>b.id==='outerYZ+');
const before=api.accelerations(api.S.bodies)[otherIndex].x;
el('mass').value='50000';el('x').value='8';el('y').value='4';el('z').value='83';el('vx').value='-3';el('vy').value='12';el('vz').value='5';el('dt').value='0.0004';
api.apply();
if(target.mass!==50000||target.x!==8||target.y!==4||target.z!==83||target.vx!==-3||target.vy!==12||target.vz!==5) throw new Error('3D editor did not apply values');
if(api.P.dt!==0.0004||api.S.running) throw new Error('3D editor did not pause or update dt');
const after=api.accelerations(api.S.bodies)[otherIndex].x;
if(Math.abs(after-before)<1e-10) throw new Error('Mass/position edit had no effect on Newton acceleration');
api.P.repel=0;const grav=api.accelerations(api.S.bodies)[otherIndex].x;
api.P.repel=1.5;const rep=api.accelerations(api.S.bodies)[otherIndex].x;
if(Math.abs(rep-grav)<1e-10) throw new Error('Repulsion control did not change the force field');
const beforeCount=api.S.bodies.length;
api.addTest();
if(api.S.bodies.length!==beforeCount+1) throw new Error('Could not add 3D test body');
api.remove();
if(api.S.bodies.length!==beforeCount) throw new Error('Could not remove 3D test body');
const finite=api.S.bodies.every(b=>['mass','x','y','z','vx','vy','vz'].every(k=>Number.isFinite(b[k])));
if(!finite) throw new Error('A body became non-finite after editor changes');
const saved = {
  format: 'matrixgame-gravitylab-3d',
  version: 1,
  parameters: { ...api.P },
  view: { yaw: api.S.yaw, pitch: api.S.pitch, roll: api.S.roll, zoom: api.S.zoom, panX: api.S.panX, panY: api.S.panY },
  bodies: api.S.bodies.map(({trail,trace,...body}) => body)
};
const expectedMass = api.S.bodies.find(b => b.id === 'outerXZ+').mass;
api.loadJSON({ text: async () => JSON.stringify(saved) }).then(() => {
  if (api.S.bodies.length !== beforeCount || api.S.bodies.find(b => b.id === 'outerXZ+').mass !== expectedMass) {
    throw new Error('JSON state round-trip did not restore bodies');
  }
  if (api.P.repel !== saved.parameters.repel) throw new Error('JSON did not restore repulsion');
  console.log('PASS: 2+'+outer+' 3D bodies; orthogonal planes; Velocity-Verlet; 3D editor; repulsion; add/remove; JSON round-trip; symmetry '+score.toFixed(1)+'%; drift '+document.body.dataset.drift+'%');
}).catch(error => {
  console.error(error);
  process.exitCode = 1;
});
