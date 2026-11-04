#!/usr/bin/env python3
"""Orbit-speed-factor experiments for the Matrixgame Gravity Cluster Lab.

Imports the verified softened-Newton/Velocity-Verlet core from the phase sweep.
The factor k scales only internal satellite-orbit velocities relative to each
cluster COM velocity. Cluster translational motion and total mass stay fixed.
This is a classical initial-condition study, not an added repulsive force.
"""
from __future__ import annotations
import argparse, csv, json, math, time
from pathlib import Path
import sys
sys.path.insert(0, str(Path(__file__).resolve().parent))
import gravity_cluster_phase_sweep as sim

DEFAULT_FACTORS=[0.8,0.9,1.0,1.1,1.2,1.3,1.4]
DEFAULT_B=[28,29,30]
DEFAULT_PHASES=[0,30,60,90,120,150,180,210,240,270,300,330,46]

def scale_internal_velocities(bodies, factor):
    if factor<=0:
        raise ValueError("velocity factor must be positive")
    checks={}
    for label in ("A","B"):
        group=[b for b in bodies if b["cluster"]==label]
        if not group: continue
        mass=sum(b["mass"] for b in group)
        vx=sum(b["mass"]*b["vx"] for b in group)/mass
        vy=sum(b["mass"]*b["vy"] for b in group)/mass
        checks[label]=(vx,vy)
        for b in group:
            b["vx"]=vx+factor*(b["vx"]-vx)
            b["vy"]=vy+factor*(b["vy"]-vy)
    return checks

def run_one(n_sats, impact, phase_deg, factor, dt=sim.DT, steps=sim.STEPS):
    y=impact/2
    bodies=(sim.cluster(n_sats,"A",-50.0,y,1.0,0.0,0.0,"isolated_equilibrium")
           +sim.cluster(n_sats,"B",50.0,-y,-1.0,0.0,math.radians(phase_deg),"isolated_equilibrium"))
    com_before=scale_internal_velocities(bodies,factor)
    p0=sim.momentum(bodies)
    pscale=max(sim.momentum_scale(bodies),1e-30)
    e0=sim.energy(bodies)
    angular0=sim.angmom(bodies)
    min_core=math.inf; closest_time=0.0; contact_time=None; current_time=0.0
    for _ in range(steps):
        sim.step(bodies,dt); current_time+=dt
        cores=[b for b in bodies if b["kind"]=="core"]
        if len(cores)==2:
            d=math.hypot(cores[0]["x"]-cores[1]["x"],cores[0]["y"]-cores[1]["y"])
            if d<min_core: min_core,closest_time=d,current_time
            if d<=sim.CONTACT:
                contact_time=current_time
                break
    p1=sim.momentum(bodies)
    pdrift=math.hypot(p1[0]-p0[0],p1[1]-p0[1])/pscale
    e1=sim.energy(bodies)
    drift=abs(e1-e0)/max(abs(e0),1e-30)
    l1=sim.angmom(bodies)
    return {
      "schema_version":"1.0","experiment":"gravity_cluster_orbit_speed_factor",
      "model":"classical_newtonian_n_body_plummer_softened",
      "integrator":"velocity_verlet_fixed_dt","initialization_mode":"isolated_equilibrium",
      "satellites_per_cluster":n_sats,"impact_parameter":impact,
      "relative_phase_deg":phase_deg,"orbit_speed_factor":factor,
      "dt":dt,"steps_executed":int(round(current_time/dt)),"simulated_time":current_time,
      "G":sim.G,"softening":sim.EPS,"total_cluster_mass":sim.MT,
      "satellite_mass":sim.MS,"orbital_radius":sim.R,
      "core_contact_distance":sim.CONTACT,"seed":None,
      "core_distance_at_contact_or_closest_approach":None if not math.isfinite(min_core) else min_core,
      "closest_approach_time":closest_time,"core_contact":contact_time is not None,
      "first_contact_time":contact_time,"energy_initial":e0,"energy_final":e1,
      "relative_energy_drift_until_contact_or_end":drift,
      "linear_momentum_drift":pdrift,
      "angular_momentum_initial_z":angular0,"angular_momentum_final_z":l1,
      "cluster_com_velocity_before_internal_scaling":{k:{"vx":v[0],"vy":v[1]} for k,v in com_before.items()},
      "interpretation":"Grid sample only; k changes initial tangential orbital velocities, not the force law."
    }

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--factors",default=",".join(map(str,DEFAULT_FACTORS)))
    ap.add_argument("--b",default=",".join(map(str,DEFAULT_B)))
    ap.add_argument("--phases",default=",".join(map(str,DEFAULT_PHASES)))
    ap.add_argument("--satellites",default="1,2")
    ap.add_argument("--dt",type=float,default=sim.DT)
    ap.add_argument("--steps",type=int,default=sim.STEPS)
    ap.add_argument("--output",default="velocity_sweep_results")
    args=ap.parse_args()
    factors=[float(x) for x in args.factors.split(",")]
    bs=[float(x) for x in args.b.split(",")]
    phases=[float(x) for x in args.phases.split(",")]
    sats=[int(x) for x in args.satellites.split(",")]
    if any(x<=0 for x in factors) or any(x not in (1,2) for x in sats):
        ap.error("factors must be positive; satellites must be 1 or 2")
    out=Path(args.output);out.mkdir(parents=True,exist_ok=True)
    rows=[];start=time.perf_counter()
    total=len(factors)*len(bs)*len(phases)*len(sats)
    for n in sats:
        for b in bs:
            for k in factors:
                for ph in phases:
                    r=run_one(n,b,ph,k,args.dt,args.steps);rows.append(r)
                    print(f"[{len(rows)}/{total}] n={n} b={b:g} k={k:g} phase={ph:g} contact={r['core_contact']}",flush=True)
    cols=list(rows[0].keys())
    with (out/"velocity_sweep.csv").open("w",newline="",encoding="utf-8") as f:
        w=csv.DictWriter(f,fieldnames=cols);w.writeheader();w.writerows(rows)
    summary={"schema_version":"1.0","experiment":"gravity_cluster_orbit_speed_factor",
      "total_runs":len(rows),"wall_time_seconds":round(time.perf_counter()-start,3),
      "grid":{"satellites":sats,"impact_parameters":bs,"speed_factors":factors,"phases_deg":phases},
      "by_variant_factor":{},"note":"Contact fractions are descriptive over the specified finite grid, not universal probabilities."}
    for n in sats:
        summary["by_variant_factor"][str(n)]={}
        for k in factors:
            subset=[r for r in rows if r["satellites_per_cluster"]==n and r["orbit_speed_factor"]==k]
            summary["by_variant_factor"][str(n)][str(k)]={"runs":len(subset),"contacts":sum(r["core_contact"] for r in subset),
             "contact_fraction":sum(r["core_contact"] for r in subset)/len(subset) if subset else None}
    (out/"summary.json").write_text(json.dumps(summary,indent=2,ensure_ascii=False),encoding="utf-8")
    print(f"Completed {len(rows)} runs.")

if __name__=="__main__":main()
