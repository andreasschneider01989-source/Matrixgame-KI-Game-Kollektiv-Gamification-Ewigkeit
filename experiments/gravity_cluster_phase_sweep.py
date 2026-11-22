#!/usr/bin/env python3
"""Matrixgame Gravity Cluster Phase Sweep (2D, deterministic, standard library).

Default uses isolated_equilibrium initialization: exact cluster COM and circular
orbits for an isolated softened two-body (n=1) or symmetric three-body (n=2)
cluster. Use --initialization legacy_html for historical canvas parity.
The merger is a perfectly inelastic core-collision proxy, never nuclear fusion.
"""
import argparse, csv, hashlib, json, math, platform, time
from pathlib import Path

G, EPS, MT, MS, R, CORE_R = 1.0, 0.5, 100.0, 4.0, 8.0, 4.0
CONTACT, DT, STEPS, POST = 8.0, 0.002, 26000, 10000
B_VALUES = [24, 26, 28, 29, 30, 32, 34]
PHASES = [0,30,60,90,120,150,180,210,240,270,300,330,46]

def accel(bs):
    ax, ay = [0.0]*len(bs), [0.0]*len(bs)
    for i in range(len(bs)):
        for j in range(i+1,len(bs)):
            a,b=bs[i],bs[j]; dx=b["x"]-a["x"]; dy=b["y"]-a["y"]
            q=dx*dx+dy*dy+EPS*EPS; f=G/(q*math.sqrt(q))
            ax[i]+=f*b["mass"]*dx; ay[i]+=f*b["mass"]*dy
            ax[j]-=f*a["mass"]*dx; ay[j]-=f*a["mass"]*dy
    return ax,ay

def energy(bs):
    e=sum(0.5*b["mass"]*(b["vx"]**2+b["vy"]**2) for b in bs)
    for i in range(len(bs)):
        for j in range(i+1,len(bs)):
            a,b=bs[i],bs[j]; dx=a["x"]-b["x"]; dy=a["y"]-b["y"]
            e-=G*a["mass"]*b["mass"]/math.sqrt(dx*dx+dy*dy+EPS*EPS)
    return e

def momentum(bs):
    return (sum(b["mass"]*b["vx"] for b in bs),
            sum(b["mass"]*b["vy"] for b in bs))

def momentum_scale(bs):
    return sum(b["mass"]*math.hypot(b["vx"],b["vy"]) for b in bs)

def angmom(bs):
    return sum(b["mass"]*(b["x"]*b["vy"]-b["y"]*b["vx"]) for b in bs)

def cluster(n,label,cx,cy,vx,vy,phase,mode):
    mc=MT-n*MS; sats=[]
    if mode=="legacy_html":
        w=math.sqrt(G*MT/R**3)
        rel=[]
        for i in range(n):
            a=phase+(math.pi if n==2 and i==1 else 0.0)
            x,y=R*math.cos(a),R*math.sin(a)
            rel.append((x,y,-w*y,w*x))
        for i,(x,y,vx0,vy0) in enumerate(rel):
            sats.append(dict(mass=MS,x=x,y=y,vx=vx0,vy=vy0,kind="sat",
                             cluster=label,name=f"Sat{i+1}_{label}"))
        # Historical HTML offset; not exact COM when n>0.
        ox=sum(s["mass"]*s["x"] for s in sats)/MT
        oy=sum(s["mass"]*s["y"] for s in sats)/MT
        ovx=sum(s["mass"]*s["vx"] for s in sats)/MT
        ovy=sum(s["mass"]*s["vy"] for s in sats)/MT
        core=dict(mass=mc,x=cx-ox,y=cy-oy,vx=vx-ovx,vy=vy-ovy,
                  kind="core",cluster=label,name=f"Kern_{label}")
        for s in sats: s["x"]+=cx; s["y"]+=cy; s["vx"]+=vx; s["vy"]+=vy
        return [core]+sats
    if mode!="isolated_equilibrium": raise ValueError("unknown initialization mode")
    if n==1:
        w=math.sqrt(G*MT/(R*R+EPS*EPS)**1.5)
        rx,ry=R*math.cos(phase),R*math.sin(phase)
        sp=(mc/MT*rx,mc/MT*ry); cp=(-MS/MT*rx,-MS/MT*ry)
        sv=(-w*sp[1],w*sp[0]); cv=(-w*cp[1],w*cp[0])
        sats=[dict(mass=MS,x=sp[0],y=sp[1],vx=sv[0],vy=sv[1],
                   kind="sat",cluster=label,name=f"Sat1_{label}")]
        core=dict(mass=mc,x=cp[0],y=cp[1],vx=cv[0],vy=cv[1],
                  kind="core",cluster=label,name=f"Kern_{label}")
    elif n==2:
        w2=G*mc/(R*R+EPS*EPS)**1.5+2*G*MS/(4*R*R+EPS*EPS)**1.5
        w=math.sqrt(w2)
        for i in range(2):
            a=phase+i*math.pi; x,y=R*math.cos(a),R*math.sin(a)
            sats.append(dict(mass=MS,x=x,y=y,vx=-w*y,vy=w*x,
                             kind="sat",cluster=label,name=f"Sat{i+1}_{label}"))
        core=dict(mass=mc,x=0.0,y=0.0,vx=0.0,vy=0.0,
                  kind="core",cluster=label,name=f"Kern_{label}")
    else:
        core=dict(mass=mc,x=0.0,y=0.0,vx=0.0,vy=0.0,
                  kind="core",cluster=label,name=f"Kern_{label}")
    core["x"]+=cx; core["y"]+=cy; core["vx"]+=vx; core["vy"]+=vy
    for s in sats: s["x"]+=cx; s["y"]+=cy; s["vx"]+=vx; s["vy"]+=vy
    return [core]+sats

def step(bs,dt):
    ax,ay=accel(bs)
    for i,b in enumerate(bs):
        b["x"]+=b["vx"]*dt+0.5*ax[i]*dt*dt
        b["y"]+=b["vy"]*dt+0.5*ay[i]*dt*dt
    ax2,ay2=accel(bs)
    for i,b in enumerate(bs):
        b["vx"]+=0.5*(ax[i]+ax2[i])*dt
        b["vy"]+=0.5*(ay[i]+ay2[i])*dt

def merge(bs,a,b):
    m=a["mass"]+b["mass"]
    c=dict(mass=m,x=(a["mass"]*a["x"]+b["mass"]*b["x"])/m,
           y=(a["mass"]*a["y"]+b["mass"]*b["y"])/m,
           vx=(a["mass"]*a["vx"]+b["mass"]*b["vx"])/m,
           vy=(a["mass"]*a["vy"]+b["mass"]*b["vy"])/m,
           kind="core",cluster="AB",name="merged_core")
    return [x for x in bs if x is not a and x is not b]+[c]

def min_cross(bs):
    dmin=math.inf
    for i,a in enumerate(bs):
        for b in bs[i+1:]:
            if a["cluster"]!=b["cluster"]:
                dmin=min(dmin,math.hypot(a["x"]-b["x"],a["y"]-b["y"]))
    return dmin

def run_one(n,b,phase,dt,steps,post,mode):
    y=b/2
    bs=cluster(n,"A",-50,y,1,0,0,mode)+cluster(n,"B",50,-y,-1,0,math.radians(phase),mode)
    initial=[dict(x) for x in bs]
    e0=energy(bs); p0=momentum(bs); pscale=max(momentum_scale(bs),1e-30); l0=angmom(bs)
    t=0.0; dmin=math.inf; tmin=0.0; cross=min_cross(bs); contact_t=None
    e_before=e_after=l_before=l_after=kinetic_loss=None; did_merge=False
    for _ in range(steps):
        step(bs,dt); t+=dt; cores=[x for x in bs if x["kind"]=="core"]
        if len(cores)==2:
            cross=min(cross,min_cross(bs))
            d=math.hypot(cores[0]["x"]-cores[1]["x"],cores[0]["y"]-cores[1]["y"])
            if d<dmin: dmin,tmin=d,t
            if d<=CONTACT:
                a,c=cores; contact_t=t; e_before=energy(bs); l_before=angmom(bs)
                mu=a["mass"]*c["mass"]/(a["mass"]+c["mass"])
                kinetic_loss=0.5*mu*((a["vx"]-c["vx"])**2+(a["vy"]-c["vy"])**2)
                bs=merge(bs,a,c); e_after=energy(bs); l_after=angmom(bs); did_merge=True
    e_end=energy(bs); p_end=momentum(bs); l_end=angmom(bs)
    drift=abs((e_before if did_merge else e_end)-e0)/max(abs(e0),1e-30)
    drift_signed=(e_before-e0)/max(abs(e0),1e-30) if did_merge else None
    pdrift=math.hypot(p_end[0]-p0[0],p_end[1]-p0[1])/pscale
    outcomes=[]
    if did_merge:
        for _ in range(post): step(bs,dt)
        rem=next(x for x in bs if x["kind"]=="core")
        for s in (x for x in bs if x["kind"]=="sat"):
            dx,dy=s["x"]-rem["x"],s["y"]-rem["y"]
            dvx,dvy=s["vx"]-rem["vx"],s["vy"]-rem["vy"]; rr=math.hypot(dx,dy)
            se=0.5*(dvx*dvx+dvy*dvy)-G*rem["mass"]/math.sqrt(rr*rr+EPS*EPS)
            vr=(dx*dvx+dy*dvy)/rr if rr else 0.0
            hint="bound_suggestive" if se<0 and vr<0 else "escaped_suggestive" if se>0 and rr>3*R else "unclear"
            outcomes.append(dict(name=s["name"],distance=rr,specific_energy_2body_approx=se,
                                 radial_velocity=vr,status_hint=hint,status="unclear"))
    pend=momentum(bs); pdrift_post=math.hypot(pend[0]-p0[0],pend[1]-p0[1])/pscale
    return dict(schema_version="1.0",experiment="gravity_cluster_phase_sweep",
        model="classical_newtonian_n_body_plummer_softened",integrator="velocity_verlet_fixed_dt",
        initialization_mode=mode,run_id=f"{'ABC'[n]}_b{int(b):02d}_phase{int(phase):03d}_dt{dt:g}",
        satellites_per_cluster=n,impact_parameter=b,relative_phase_deg=phase,G=G,softening=EPS,
        dt=dt,steps=steps,simulated_time=steps*dt,post_merger_steps=post if did_merge else 0,total_cluster_mass=MT,satellite_mass=MS,
        orbital_radius=R,core_radius_fixed=CORE_R,core_radius_policy="fixed_even_as_core_mass_changes",
        core_contact_distance=CONTACT,seed=None,initial_bodies=initial,
        min_core_distance_before_contact_or_end=dmin,closest_approach_time=tmin,
        min_cross_cluster_body_distance_before_merger=cross if math.isfinite(cross) else None,
        core_contact=contact_t is not None,first_contact_time=contact_t,merger_proxy=did_merge,
        merge_time=contact_t,energy_initial=e0,energy_at_base_run_end=e_end,energy_drift=drift,
        energy_drift_pre_merger=drift_signed,energy_before_merger=e_before,energy_after_merger=e_after,
        total_energy_jump_at_merger=(e_after-e_before) if did_merge else None,
        kinetic_energy_dissipated_by_core_velocity_merge=kinetic_loss,
        linear_momentum_initial={"x":p0[0],"y":p0[1]},linear_momentum_scale=pscale,
        linear_momentum_drift=pdrift,linear_momentum_drift_after_post_observation=pdrift_post,
        angular_momentum_initial_z=l0,angular_momentum_at_base_run_end_z=l_end,
        angular_momentum_change_at_merger_z=(l_after-l_before) if did_merge else None,
        satellite_outcomes=outcomes)

def build_runs(dt,steps):
    return [(n,b,p,dt,steps) for n in (0,1,2) for b in B_VALUES for p in ([0] if n==0 else PHASES)]

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--dt",type=float,default=DT); ap.add_argument("--steps",type=int,default=STEPS)
    ap.add_argument("--post-steps",type=int,default=POST); ap.add_argument("--output",default="sweep_results")
    ap.add_argument("--initialization",choices=("isolated_equilibrium","legacy_html"),default="isolated_equilibrium")
    ap.add_argument("--resume",action="store_true")
    ap.add_argument("--quick",action="store_true",help="Keep full encounter duration; omit post-merger observation.")
    ap.add_argument("--dt-check",type=int,default=0); ap.add_argument("--only-b",type=float)
    ap.add_argument("--only-sats",type=int,choices=(0,1,2)); a=ap.parse_args()
    if a.dt<=0 or a.steps<=0 or a.post_steps<0: ap.error("invalid dt/steps/post-steps")
    post=0 if a.quick else a.post_steps; out=Path(a.output); rd=out/"runs"; rd.mkdir(parents=True,exist_ok=True)
    runs=build_runs(a.dt,a.steps)
    if a.only_b is not None: runs=[r for r in runs if r[1]==a.only_b]
    if a.only_sats is not None: runs=[r for r in runs if r[0]==a.only_sats]
    results=[]; started=time.perf_counter()
    for i,(n,b,ph,dt,steps) in enumerate(runs,1):
        rid=f"{'ABC'[n]}_b{int(b):02d}_phase{int(ph):03d}_dt{dt:g}"; file=rd/f"{rid}.json"
        if a.resume and file.exists():
            cached=json.loads(file.read_text(encoding="utf-8"))
            expected={"initialization_mode":a.initialization,"satellites_per_cluster":n,
                      "impact_parameter":b,"relative_phase_deg":ph,"dt":dt,"steps":steps}
            matches=all(cached.get(k)==v for k,v in expected.items())
            matches=matches and cached.get("post_merger_steps")== (post if cached.get("merger_proxy") else 0)
            if matches: r=cached
            else:
                r=run_one(n,b,ph,dt,steps,post,a.initialization)
                file.write_text(json.dumps(r,indent=2,ensure_ascii=False),encoding="utf-8")
        else:
            r=run_one(n,b,ph,dt,steps,post,a.initialization)
            file.write_text(json.dumps(r,indent=2,ensure_ascii=False),encoding="utf-8")
        results.append(r); print(f"[{i}/{len(runs)}] {rid}",flush=True)
    cols=["run_id","satellites_per_cluster","impact_parameter","relative_phase_deg","dt","steps",
          "min_core_distance_before_contact_or_end","core_contact","first_contact_time","merger_proxy",
          "energy_drift","linear_momentum_drift","total_energy_jump_at_merger",
          "kinetic_energy_dissipated_by_core_velocity_merge","angular_momentum_change_at_merger_z"]
    with (out/"results.csv").open("w",newline="",encoding="utf-8") as f:
        w=csv.DictWriter(f,fieldnames=cols,extrasaction="ignore"); w.writeheader(); w.writerows(results)
    qc=[]
    if a.dt_check and a.dt>0.0005:
        border=sorted(results,key=lambda x:abs(x["min_core_distance_before_contact_or_end"]-CONTACT))[:a.dt_check]
        for r in border:
            e={"base_run_id":r["run_id"],"base":{k:r[k] for k in ("dt","min_core_distance_before_contact_or_end","core_contact","first_contact_time")}}
            for d2 in (0.001,0.0005):
                s2=run_one(r["satellites_per_cluster"],r["impact_parameter"],r["relative_phase_deg"],
                           d2,round(a.steps*a.dt/d2),0,a.initialization)
                e[f"dt_{d2:g}"]={k:s2[k] for k in ("min_core_distance_before_contact_or_end","core_contact","first_contact_time","energy_drift")}
                e[f"dt_{d2:g}"]["classification_switch"]=s2["core_contact"]!=r["core_contact"]
            e["numerically_sensitive"]=any(e[f"dt_{d2:g}"]["classification_switch"] for d2 in (0.001,0.0005)); qc.append(e)
    def stats(n):
        sub=[r for r in results if r["satellites_per_cluster"]==n]; con=sum(r["core_contact"] for r in sub)
        return {"runs":len(sub),"contact_runs":con,"contact_fraction_of_sampled_grid":con/len(sub) if sub else None}
    summary={"schema_version":"1.0","experiment":"gravity_cluster_phase_sweep","model":"classical_newtonian_n_body_plummer_softened",
        "integrator":"velocity_verlet_fixed_dt","initialization_mode":a.initialization,"total_runs":len(results),
        "expected_full_grid_runs":189,"grid_count_check":len(build_runs(DT,STEPS))==189,
        "dt":a.dt,"steps":a.steps,"post_merger_steps":post,"wall_time_seconds":round(time.perf_counter()-started,3),
        "python_version":platform.python_version(),"script_sha256":hashlib.sha256(Path(__file__).read_bytes()).hexdigest(),
        "variants":{"A_0sat":stats(0),"B_1sat":stats(1),"C_2sat":stats(2)},"dt_quality_control":qc,
        "limits":["Grid fractions are not universal probabilities.","Core radii remain fixed as core masses differ.",
        "Merger is a perfectly inelastic collision proxy, not fusion or chemistry.","Satellite escape labels are hints only.",
        "legacy_html preserves old offset behavior; isolated_equilibrium fixes COM and softened circular balance."]}
    (out/"summary.json").write_text(json.dumps(summary,indent=2,ensure_ascii=False),encoding="utf-8")
    print(f"Completed {len(results)} runs in {summary['wall_time_seconds']} s")

if __name__=="__main__": main()
