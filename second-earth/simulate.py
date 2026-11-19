#!/usr/bin/env python3
"""Reproducible Second Earth resource-policy pilot; Python standard library only."""
from __future__ import annotations

import argparse
import json
import math
import random
import statistics
from dataclasses import dataclass
from pathlib import Path
from typing import Any


@dataclass(frozen=True)
class WorldConfig:
    model_version: str
    steps: int
    initial_biomass: float
    carrying_capacity: float
    regrowth_rate: float
    initial_food: float
    population: int
    food_need_per_person: float
    food_yield_per_biomass: float
    disturbance_probability: float
    disturbance_loss_fraction: float
    seed: int

    @classmethod
    def from_dict(cls, raw: dict[str, Any]) -> "WorldConfig":
        config = cls(**raw)
        if config.steps < 1 or config.population < 1:
            raise ValueError("steps and population must be positive")
        positive = (config.initial_biomass, config.carrying_capacity,
                    config.regrowth_rate, config.food_need_per_person,
                    config.food_yield_per_biomass)
        if any(not math.isfinite(x) or x <= 0 for x in positive):
            raise ValueError("biomass, capacity, rates, demand and yield must be finite and positive")
        if config.initial_biomass > config.carrying_capacity:
            raise ValueError("initial_biomass cannot exceed carrying_capacity")
        if not math.isfinite(config.initial_food) or config.initial_food < 0:
            raise ValueError("initial_food must be finite and non-negative")
        if not 0 <= config.disturbance_probability <= 1:
            raise ValueError("shock probability must be between 0 and 1")
        if not 0 <= config.disturbance_loss_fraction <= 1:
            raise ValueError("shock fraction must be between 0 and 1")
        return config


@dataclass(frozen=True)
class Policy:
    policy_id: str
    harvest_pressure: float
    routine_food_loss: float
    rationale: str


POLICIES = (
    Policy("GROWTH", 1.40, 0.12, "Harvest above modeled regrowth; prioritize short-term food output."),
    Policy("REGENERATION", 0.65, 0.05, "Harvest conservatively; retain more biomass."),
    Policy("COOPERATION", 0.90, 0.02, "Balance harvest near regrowth and reduce modeled food loss."),
)


def simulate(config: WorldConfig, policy: Policy, seed: int) -> dict[str, Any]:
    """Run one policy. All quantities are abstract model units, not SI or measured units."""
    rng = random.Random(seed)
    biomass = config.initial_biomass
    food = config.initial_food
    demand = config.population * config.food_need_per_person
    trace: list[dict[str, float | int | bool]] = []

    for step in range(config.steps):
        regrowth = max(0.0, config.regrowth_rate * biomass *
                      (1.0 - biomass / config.carrying_capacity))
        biomass = min(config.carrying_capacity, biomass + regrowth)

        shock = rng.random() < config.disturbance_probability
        biomass_shock_loss = biomass * config.disturbance_loss_fraction if shock else 0.0
        biomass -= biomass_shock_loss

        harvest = min(biomass, regrowth * policy.harvest_pressure)
        biomass -= harvest
        food += harvest * config.food_yield_per_biomass

        routine_loss = food * policy.routine_food_loss
        food -= routine_loss
        food_shock_loss = food * config.disturbance_loss_fraction if shock else 0.0
        food -= food_shock_loss

        served = min(food, demand)
        food -= served
        coverage = served / demand
        trace.append({
            "step": step + 1,
            "biomass": biomass,
            "food_store": food,
            "harvest": harvest,
            "food_served": served,
            "food_coverage": coverage,
            "shock": shock,
            "biomass_shock_loss": biomass_shock_loss,
            "food_shock_loss": food_shock_loss,
        })

    coverages = [float(row["food_coverage"]) for row in trace]
    biomasses = [float(row["biomass"]) for row in trace]
    return {
        "policy_id": policy.policy_id,
        "seed": seed,
        "final_biomass": biomasses[-1],
        "minimum_biomass": min(biomasses),
        "mean_food_coverage": statistics.fmean(coverages),
        "shortage_steps": sum(value < 0.999999 for value in coverages),
        "trace": trace,
    }


def run_experiment(config: WorldConfig, seed_count: int = 30) -> dict[str, Any]:
    if seed_count < 1:
        raise ValueError("seed_count must be at least 1")
    seeds = [config.seed + offset for offset in range(seed_count)]
    all_runs: dict[str, list[dict[str, Any]]] = {}
    summaries = []

    for policy in POLICIES:
        runs = [simulate(config, policy, seed) for seed in seeds]
        all_runs[policy.policy_id] = runs
        summaries.append({
            "policy_id": policy.policy_id,
            "policy_parameters": {
                "harvest_pressure": policy.harvest_pressure,
                "routine_food_loss": policy.routine_food_loss,
                "rationale": policy.rationale,
            },
            "mean_final_biomass": statistics.fmean(r["final_biomass"] for r in runs),
            "mean_minimum_biomass": statistics.fmean(r["minimum_biomass"] for r in runs),
            "mean_food_coverage": statistics.fmean(r["mean_food_coverage"] for r in runs),
            "mean_shortage_steps": statistics.fmean(r["shortage_steps"] for r in runs),
        })

    return {
        "experiment_id": "valley-001-policy-comparison",
        "model_version": config.model_version,
        "epistemic_status": "MODEL",
        "data_status": "SYNTHETIC_ASSUMPTIONS",
        "config": config.__dict__,
        "design": {
            "steps_per_run": config.steps,
            "seed_count": seed_count,
            "seeds": seeds,
            "paired_seeds": True,
            "policies_are_llm_agents": False,
        },
        "metrics": ["mean_final_biomass", "mean_minimum_biomass",
                    "mean_food_coverage", "mean_shortage_steps"],
        "policy_summaries": summaries,
        "limitations": [
            "Toy model; not calibrated to a real ecosystem or location.",
            "Biomass is one abstract renewable stock; biodiversity and soil are not modeled.",
            "Food yield, population demand, regrowth and shock rates are synthetic assumptions.",
            "Energy, water, infrastructure, trade, migration and social institutions are not modeled.",
            "Policy baselines are deterministic rules, not trained neural networks or autonomous LLM agents.",
            "Passing tests does not establish empirical validity or predictive skill.",
            "No single composite score ranks human or civilizational wellbeing."
        ],
        "runs": all_runs,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--config", type=Path, required=True)
    parser.add_argument("--seeds", type=int, default=30)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    config = WorldConfig.from_dict(json.loads(args.config.read_text(encoding="utf-8")))
    result = run_experiment(config, args.seeds)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"PASS: {result['experiment_id']} ({args.seeds} paired seeds, {len(POLICIES)} policies)")
    for summary in result["policy_summaries"]:
        print(f"{summary['policy_id']}: biomass={summary['mean_final_biomass']:.2f}, "
              f"food_coverage={summary['mean_food_coverage']:.3f}, "
              f"shortage_steps={summary['mean_shortage_steps']:.1f}")
    print(f"Output: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
