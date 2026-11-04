"""
EVOLUTION FORGE 1.0
A small, exploratory agent-based model for:
- prebiotic readiness (phenomenological proxy, not a chemistry simulator)
- protocell emergence (scenario-dependent stochastic gate)
- cell-level sensing and quorum signaling
- cooperation, public goods, and cheater pressure
- adhesion, colony formation, and primitive cell differentiation
- astrobiology scenario comparison

Educational research toy model. NOT a validated model of abiogenesis, Earth's
evolutionary history, or extraterrestrial life. Dependencies: numpy, matplotlib.
"""
from __future__ import annotations
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Any
import argparse, csv, json, math, random
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

MODEL_VERSION = "1.0.0"

SCENARIOS: dict[str, dict[str, Any]] = {
    "earth_tidal_pool": {
        "label": "Earth-like wet-dry pool", "energy_flux": 0.78,
        "solvent_stability": 0.82, "wet_dry_cycles": 0.92,
        "mineral_surfaces": 0.62, "redox_gradient": 0.52,
        "radiation_stress": 0.38, "thermal_stability": 0.68,
        "resource_regen": 0.030, "quorum_threshold": 1.30,
        "predation_pressure": 0.06, "note": "Earth-like aqueous scenario with wet-dry cycling."
    },
    "alkaline_vent": {
        "label": "Alkaline hydrothermal vent", "energy_flux": 0.88,
        "solvent_stability": 0.98, "wet_dry_cycles": 0.06,
        "mineral_surfaces": 0.96, "redox_gradient": 0.94,
        "radiation_stress": 0.08, "thermal_stability": 0.58,
        "resource_regen": 0.035, "quorum_threshold": 1.25,
        "predation_pressure": 0.04, "note": "Aqueous scenario emphasizing mineral surfaces and redox gradients."
    },
    "icy_ocean": {
        "label": "Icy ocean (Europa/Enceladus-like)", "energy_flux": 0.48,
        "solvent_stability": 0.98, "wet_dry_cycles": 0.02,
        "mineral_surfaces": 0.72, "redox_gradient": 0.66,
        "radiation_stress": 0.30, "thermal_stability": 0.88,
        "resource_regen": 0.018, "quorum_threshold": 1.45,
        "predation_pressure": 0.025, "note": "Exploratory water-ocean scenario with lower accessible energy."
    },
    "titan_hypothetical": {
        "label": "Titan-like alternative-solvent thought experiment", "energy_flux": 0.22,
        "solvent_stability": 0.28, "wet_dry_cycles": 0.01,
        "mineral_surfaces": 0.52, "redox_gradient": 0.35,
        "radiation_stress": 0.12, "thermal_stability": 0.95,
        "resource_regen": 0.009, "quorum_threshold": 1.70,
        "predation_pressure": 0.01, "note": "Highly speculative; this is not a methane-biochemistry model."
    },
}

@dataclass
class Config:
    scenario: str = "earth_tidal_pool"
    width: int = 72
    height: int = 48
    steps: int = 360
    seed: int = 7
    max_population: int = 520
    initial_cells: int = 0
    origin_rate: float = 0.035
    mutation_rate: float = 0.035
    replication_energy: float = 1.12
    reproduction_probability: float = 0.22
    maintenance_cost: float = 0.018
    signal_cost: float = 0.004
    cooperation_cost: float = 0.010
    cooperation_benefit: float = 0.050
    quorum_cell_count: int = 5
    multicell_threshold: int = 7
    differentiation_rate: float = 0.012
    max_age: int = 220
    max_cells_per_patch: int = 4
    move_probability: float = 0.025
    prebiotic_decay: float = 0.0025
    chemical_synthesis_rate: float = 0.008
    polymerization_rate: float = 0.006
    compartment_rate: float = 0.005
    replication_origin_rate: float = 0.005

@dataclass
class Cell:
    x: int
    y: int
    energy: float
    cooperation: float
    signaling: float
    adhesion: float
    specialization: float
    motility: float
    age: int = 0
    role: str = "germ"
    lineage: int = 0

def clamp(v: float, lo: float = 0.0, hi: float = 1.0) -> float:
    return max(lo, min(hi, float(v)))

def box_sum(a: np.ndarray) -> np.ndarray:
    p = np.pad(a, 1, mode="edge")
    h, w = a.shape
    return (p[0:h,0:w] + p[0:h,1:w+1] + p[0:h,2:w+2] +
            p[1:h+1,0:w] + p[1:h+1,1:w+1] + p[1:h+1,2:w+2] +
            p[2:h+2,0:w] + p[2:h+2,1:w+1] + p[2:h+2,2:w+2])

def diffuse(field: np.ndarray, rate: float) -> np.ndarray:
    p = np.pad(field, 1, mode="edge")
    h, w = field.shape
    neighbors = (p[0:h,1:w+1] + p[2:h+2,1:w+1] +
                 p[1:h+1,0:w] + p[1:h+1,2:w+2]) * 0.25
    return np.maximum(0.0, field + rate * (neighbors - field))

class EvolutionSimulator:
    def __init__(self, config: Config):
        if config.scenario not in SCENARIOS:
            raise ValueError(f"Unknown scenario: {config.scenario}")
        if config.steps < 1 or config.width < 4 or config.height < 4:
            raise ValueError("steps must be positive and grid dimensions at least 4")
        if config.initial_cells < 0 or config.initial_cells > config.max_population:
            raise ValueError("initial_cells must be between 0 and max_population")
        self.cfg = config
        self.scenario = SCENARIOS[config.scenario]
        self.rng = np.random.default_rng(config.seed)
        random.seed(config.seed)
        self.h, self.w = config.height, config.width
        self.resource = self._make_resource_field()
        self.signal_field = np.zeros((self.h, self.w), dtype=float)
        self.cells: list[Cell] = []
        self.next_lineage = 1
        self.life_originated = False
        self.first_cell_step: int | None = None
        self.chem = {"monomers": 0.05, "polymers": 0.01, "compartments": 0.01, "replicators": 0.005}
        self.history: list[dict[str, float]] = []
        self.snapshot: dict[str, Any] = {}
        for _ in range(config.initial_cells):
            self.cells.append(self._new_cell(int(self.rng.integers(self.w)), int(self.rng.integers(self.h))))
        if self.cells:
            self.life_originated = True
            self.first_cell_step = 0

    def _make_resource_field(self) -> np.ndarray:
        yy, xx = np.mgrid[0:self.h, 0:self.w]
        field = np.full((self.h, self.w), 0.42, dtype=float)
        centers = [(self.w*0.25,self.h*0.35,0.28),(self.w*0.70,self.h*0.63,0.22),(self.w*0.48,self.h*0.80,0.14)]
        for cx, cy, amp in centers:
            field += amp * np.exp(-((xx-cx)**2+(yy-cy)**2)/(2*(min(self.w,self.h)*0.12)**2))
        return np.clip(field, 0.0, 1.0)

    def _new_cell(self, x: int, y: int, parent: Cell | None = None) -> Cell:
        if parent is None:
            vals = self.rng.normal([0.58,0.45,0.48,0.42,0.55],[0.12]*5)
            cooperation, signaling, adhesion, specialization, motility = [clamp(v) for v in vals]
            lineage = self.next_lineage
            self.next_lineage += 1
            energy = 0.75
        else:
            traits = [parent.cooperation,parent.signaling,parent.adhesion,parent.specialization,parent.motility]
            mutated = [clamp(v+self.rng.normal(0,self.cfg.mutation_rate)) for v in traits]
            cooperation, signaling, adhesion, specialization, motility = mutated
            lineage, energy = parent.lineage, 0.56
        return Cell(x,y,energy,cooperation,signaling,adhesion,specialization,motility,0,"germ",lineage)

    def _update_prebiotic_chemistry(self) -> float:
        s, c = self.scenario, self.chem
        substrate = s["energy_flux"]*s["solvent_stability"]*(0.35+0.65*s["mineral_surfaces"])
        cycle_factor = 0.25+0.75*s["wet_dry_cycles"]+0.45*s["mineral_surfaces"]
        c["monomers"] = clamp(c["monomers"]+self.cfg.chemical_synthesis_rate*substrate-self.cfg.prebiotic_decay*c["monomers"])
        c["polymers"] = clamp(c["polymers"]+self.cfg.polymerization_rate*c["monomers"]*cycle_factor-self.cfg.prebiotic_decay*c["polymers"])
        c["compartments"] = clamp(c["compartments"]+self.cfg.compartment_rate*s["solvent_stability"]*(0.25+0.75*s["mineral_surfaces"])-self.cfg.prebiotic_decay*c["compartments"])
        c["replicators"] = clamp(c["replicators"]+self.cfg.replication_origin_rate*c["polymers"]*s["energy_flux"]-self.cfg.prebiotic_decay*c["replicators"])
        readiness = (max(c["monomers"],1e-8)*max(c["polymers"],1e-8)*max(c["compartments"],1e-8)*max(c["replicators"],1e-8))**0.25
        return clamp(readiness)

    def _try_origin_of_life(self, step: int, readiness: float) -> None:
        if self.life_originated or readiness < 0.12:
            return
        if self.rng.random() < self.cfg.origin_rate*readiness:
            y,x = np.unravel_index(np.argmax(self.resource),self.resource.shape)
            self.cells.append(self._new_cell(int(x),int(y)))
            self.life_originated, self.first_cell_step = True, step

    def _find_free_neighbor(self, x: int, y: int, density: np.ndarray) -> tuple[int,int] | None:
        offsets = [(dx,dy) for dx in (-1,0,1) for dy in (-1,0,1) if (dx,dy)!=(0,0)]
        self.rng.shuffle(offsets)
        for dx,dy in offsets:
            nx,ny = x+dx,y+dy
            if 0<=nx<self.w and 0<=ny<self.h and density[ny,nx]<self.cfg.max_cells_per_patch:
                return nx,ny
        return None

    def _record(self, step: int, readiness: float, quorum_fraction: float, colony_fraction: float,
                differentiated_fraction: float, mean_cooperation: float, cheat_fraction: float) -> None:
        self.history.append({
            "step":float(step),"population":float(len(self.cells)),"prebiotic_readiness":float(readiness),
            "monomers":self.chem["monomers"],"polymers":self.chem["polymers"],
            "compartments":self.chem["compartments"],"replicators":self.chem["replicators"],
            "quorum_fraction":float(quorum_fraction),"colony_fraction":float(colony_fraction),
            "differentiated_fraction":float(differentiated_fraction),"mean_cooperation":float(mean_cooperation),
            "cheater_fraction":float(cheat_fraction),"resource_mean":float(self.resource.mean())
        })

    def step(self, step: int) -> None:
        s,c = self.scenario,self.cfg
        readiness = self._update_prebiotic_chemistry()
        self._try_origin_of_life(step,readiness)
        self.resource = diffuse(self.resource,0.10)
        self.resource += s["resource_regen"]*s["energy_flux"]*(1.0-self.resource)
        self.resource = np.clip(self.resource,0.0,1.0)
        if not self.cells:
            self._record(step,readiness,0,0,0,0,0)
            return
        density = np.zeros((self.h,self.w),dtype=float)
        coop_sum,signal,adhesion_sum = np.zeros_like(density),np.zeros_like(density),np.zeros_like(density)
        xs=np.fromiter((a.x for a in self.cells),dtype=int); ys=np.fromiter((a.y for a in self.cells),dtype=int)
        coops=np.fromiter((a.cooperation for a in self.cells),dtype=float)
        signals=np.fromiter((a.signaling for a in self.cells),dtype=float)
        adhesions=np.fromiter((a.adhesion for a in self.cells),dtype=float)
        np.add.at(density,(ys,xs),1.0); np.add.at(coop_sum,(ys,xs),coops)
        np.add.at(signal,(ys,xs),signals); np.add.at(adhesion_sum,(ys,xs),adhesions)
        local_density=box_sum(density)
        local_coop=box_sum(coop_sum)/np.maximum(box_sum(density),1.0)
        local_adhesion=box_sum(adhesion_sum)/np.maximum(box_sum(density),1.0)
        signal=diffuse(signal,0.26); self.signal_field=signal
        survivors: list[Cell]=[]; newborns: list[Cell]=[]
        q_values: list[float]=[]; colony_flags: list[bool]=[]
        differentiated=0; cooperation_values: list[float]=[]; cheaters=0
        for a in self.cells:
            a.age+=1
            d=local_density[a.y,a.x]; neighbor_count=max(0.0,d-1.0)
            signal_level=signal[a.y,a.x]
            density_gate=min(1.0,neighbor_count/max(1,c.quorum_cell_count))
            signal_gate=1.0/(1.0+math.exp(-4.0*(signal_level-s["quorum_threshold"])))
            quorum=density_gate*signal_gate; q_values.append(quorum)
            mean_coop=local_coop[a.y,a.x]; mean_adh=local_adhesion[a.y,a.x]
            in_colony=(d>=c.multicell_threshold and quorum>0.42 and mean_adh>0.42)
            colony_flags.append(in_colony)
            if (a.role=="germ" and in_colony and mean_coop>0.48 and a.specialization>0.58
                    and self.rng.random()<c.differentiation_rate):
                a.role="soma"
            resource_here=self.resource[a.y,a.x]
            uptake=0.022+0.060*resource_here*s["energy_flux"]*(0.35+0.65*s["redox_gradient"])
            public_good=quorum*mean_coop*c.cooperation_benefit
            soma_support=0.012*quorum*mean_coop if a.role=="soma" else 0.0
            stress=0.009*s["radiation_stress"]+0.012*(1.0-s["thermal_stability"])
            cost=c.maintenance_cost+c.signal_cost*a.signaling+c.cooperation_cost*a.cooperation*quorum
            a.energy=min(2.0,a.energy+uptake+public_good+soma_support-cost)
            self.resource[a.y,a.x]=max(0.0,self.resource[a.y,a.x]-0.0035)
            predation=s["predation_pressure"]*(1.0-0.55*quorum*mean_coop)
            death_probability=0.001+stress*0.06+predation*0.04
            if a.age>c.max_age: death_probability+=0.08
            if a.energy<0.055 or self.rng.random()<death_probability: continue
            if a.role=="germ" and self.rng.random()<c.move_probability*a.motility and resource_here<0.65:
                nx,ny=self._find_free_neighbor(a.x,a.y,density) or (a.x,a.y); a.x,a.y=nx,ny
            cooperation_values.append(a.cooperation)
            if a.cooperation<0.25: cheaters+=1
            if a.role=="soma": differentiated+=1
            if (a.role=="germ" and a.energy>=c.replication_energy and
                    len(self.cells)+len(newborns)<c.max_population and self.rng.random()<c.reproduction_probability):
                free=self._find_free_neighbor(a.x,a.y,density)
                if free is not None:
                    newborns.append(self._new_cell(free[0],free[1],a)); a.energy-=0.38
            survivors.append(a)
        self.cells=survivors+newborns
        qfrac=float(np.mean(q_values)) if q_values else 0.0
        cfrac=float(np.mean(colony_flags)) if colony_flags else 0.0
        dfrac=differentiated/max(1,len(survivors))
        mcoop=float(np.mean(cooperation_values)) if cooperation_values else 0.0
        cheatfrac=cheaters/max(1,len(survivors))
        self._record(step,readiness,qfrac,cfrac,dfrac,mcoop,cheatfrac)
        if step==c.steps-1:
            self.snapshot={"resource":self.resource.copy(),"cells":[(a.x,a.y,a.role,a.cooperation,a.signaling,a.adhesion) for a in self.cells]}

    def run(self) -> dict[str,Any]:
        for step in range(self.cfg.steps): self.step(step)
        final=self.history[-1] if self.history else {}
        return {
            "model_version":MODEL_VERSION,"scenario":self.cfg.scenario,
            "scenario_label":self.scenario["label"],"scenario_note":self.scenario["note"],
            "seed":self.cfg.seed,"steps":self.cfg.steps,"config":asdict(self.cfg),
            "scenario_parameters":dict(self.scenario),"first_cell_step":self.first_cell_step,
            "final_population":int(final.get("population",0)),
            "final_prebiotic_readiness":round(final.get("prebiotic_readiness",0),4),
            "final_colony_fraction":round(final.get("colony_fraction",0),4),
            "final_differentiated_fraction":round(final.get("differentiated_fraction",0),4),
            "final_mean_cooperation":round(final.get("mean_cooperation",0),4),
            "final_cheater_fraction":round(final.get("cheater_fraction",0),4),
            "interpretation_warning":"Toy-model results are exploratory and scenario-dependent, not empirical probabilities or predictions."
        }

def run_simulation(config: Config) -> tuple[EvolutionSimulator,dict[str,Any]]:
    sim=EvolutionSimulator(config); return sim,sim.run()

def apply_llm_proposal(base: Config,proposal: dict[str,Any]) -> Config:
    allowed={"scenario","steps","seed","origin_rate","mutation_rate","replication_energy",
             "reproduction_probability","maintenance_cost","cooperation_benefit","quorum_cell_count",
             "multicell_threshold","differentiation_rate","predation_pressure"}
    unknown=set(proposal)-allowed
    if unknown: raise ValueError(f"Unsupported proposal fields: {sorted(unknown)}")
    values=asdict(base)
    bounds={"steps":(20,2000),"seed":(0,2**31-1),"origin_rate":(0.0,0.25),
            "mutation_rate":(0.0,0.25),"replication_energy":(0.4,1.9),
            "reproduction_probability":(0.0,0.8),"maintenance_cost":(0.001,0.15),
            "cooperation_benefit":(0.0,0.3),"quorum_cell_count":(1,20),
            "multicell_threshold":(2,40),"differentiation_rate":(0.0,0.15),
            "predation_pressure":(0.0,1.0)}
    for key,value in proposal.items():
        if key=="scenario":
            if value not in SCENARIOS: raise ValueError(f"Unknown scenario: {value}")
            values[key]=value
        else:
            lo,hi=bounds[key]; v=float(value)
            if not math.isfinite(v) or v<lo or v>hi: raise ValueError(f"{key} must be between {lo} and {hi}")
            values[key]=int(round(v)) if key in {"steps","seed","quorum_cell_count","multicell_threshold"} else v
    return Config(**values)

def save_outputs(sim: EvolutionSimulator,summary: dict[str,Any],output_dir: Path) -> None:
    output_dir.mkdir(parents=True,exist_ok=True)
    (output_dir/"summary.json").write_text(json.dumps(summary,indent=2),encoding="utf-8")
    if sim.history:
        keys=list(sim.history[0].keys())
        with (output_dir/"history.csv").open("w",newline="",encoding="utf-8") as f:
            writer=csv.DictWriter(f,fieldnames=keys); writer.writeheader(); writer.writerows(sim.history)
        x=[r["step"] for r in sim.history]
        fig=plt.figure(figsize=(9,4.8))
        plt.plot(x,[r["population"] for r in sim.history],label="Cells")
        plt.plot(x,[r["population"]*r["colony_fraction"] for r in sim.history],label="Cells in colony-like groups")
        plt.xlabel("Simulation step"); plt.ylabel("Population / group-associated cells")
        plt.title("Evolution Forge – population dynamics"); plt.legend(); plt.tight_layout()
        fig.savefig(output_dir/"population_dynamics.png",dpi=150); plt.close(fig)
        fig=plt.figure(figsize=(9,4.8))
        for key in ("monomers","polymers","compartments","replicators"):
            plt.plot(x,[r[key] for r in sim.history],label=key.capitalize())
        plt.xlabel("Simulation step"); plt.ylabel("Normalized proxy (0–1)")
        plt.title("Prebiotic readiness proxies (not chemical concentrations)")
        plt.legend(); plt.tight_layout(); fig.savefig(output_dir/"prebiotic_proxies.png",dpi=150); plt.close(fig)
        fig=plt.figure(figsize=(9,4.8))
        for key in ("mean_cooperation","quorum_fraction","colony_fraction","differentiated_fraction","cheater_fraction"):
            plt.plot(x,[r[key] for r in sim.history],label=key.replace("_"," ").title())
        plt.xlabel("Simulation step"); plt.ylabel("Fraction / mean trait")
        plt.title("Cooperation, quorum, colony formation and differentiation")
        plt.legend(); plt.tight_layout(); fig.savefig(output_dir/"collective_dynamics.png",dpi=150); plt.close(fig)
    if sim.snapshot:
        fig=plt.figure(figsize=(9,5.5)); resource=sim.snapshot["resource"]
        plt.imshow(resource,origin="lower",aspect="auto")
        cells=sim.snapshot["cells"]
        gx=[c[0] for c in cells if c[2]=="germ"]; gy=[c[1] for c in cells if c[2]=="germ"]
        sx=[c[0] for c in cells if c[2]=="soma"]; sy=[c[1] for c in cells if c[2]=="soma"]
        if gx: plt.scatter(gx,gy,s=14,label="Germ / reproducing cells")
        if sx: plt.scatter(sx,sy,s=22,marker="s",label="Soma / supporting cells")
        plt.xlabel("Environment x"); plt.ylabel("Environment y")
        plt.title("Final world snapshot: resources and cell roles")
        plt.legend(); plt.tight_layout(); fig.savefig(output_dir/"world_snapshot.png",dpi=150); plt.close(fig)

def main() -> None:
    parser=argparse.ArgumentParser(description=f"Run Evolution Forge {MODEL_VERSION}")
    parser.add_argument("--scenario",choices=SCENARIOS.keys(),default="earth_tidal_pool")
    parser.add_argument("--steps",type=int,default=360); parser.add_argument("--seed",type=int,default=7)
    parser.add_argument("--config",type=Path); parser.add_argument("--proposal",type=Path)
    parser.add_argument("--out",type=Path,default=Path("evolution_forge_output"))
    args=parser.parse_args()
    cfg=Config(scenario=args.scenario,steps=args.steps,seed=args.seed)
    if args.config: cfg=Config(**json.loads(args.config.read_text(encoding="utf-8")))
    if args.proposal: cfg=apply_llm_proposal(cfg,json.loads(args.proposal.read_text(encoding="utf-8")))
    sim,summary=run_simulation(cfg); save_outputs(sim,summary,args.out)
    print(json.dumps(summary,indent=2))

if __name__=="__main__": main()
