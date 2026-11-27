import random, json, math

SEED = 20261127
DAYS = 120
SOIL_CAPACITY_MM = 150.0
INITIAL_SOIL_WATER_MM = 70.0


def generate_weather(seed=SEED):
    rng = random.Random(seed)
    forcing = []
    for day in range(DAYS):
        rain = max(0.0, rng.gauss(1.8, 2.5))
        temperature = (
            18.0
            + 5.0 * math.sin(2.0 * math.pi * day / 120.0)
            + rng.gauss(0.0, 1.2)
        )
        forcing.append((rain, temperature))
    return forcing


FORCING = generate_weather()


def simulate(rain_factor=1.0, temperature_add=0.0,
             infiltration=0.78, restoration=False):
    storage = INITIAL_SOIL_WATER_MM
    vegetation = 35.0
    soil_quality = 35.0
    rows = []

    for day, (base_rain, base_temperature) in enumerate(FORCING, 1):
        drought_period = 21 <= day <= 80
        rain = base_rain * (rain_factor if drought_period else 1.0)
        temperature = base_temperature + (
            temperature_add if drought_period else 0.0
        )
        infiltration_fraction = min(
            1.0,
            max(0.0, infiltration + (
                0.08 if restoration and drought_period else 0.0
            )),
        )
        evaporation_demand = max(
            0.0, 2.0 + 0.08 * (temperature - 18.0)
        )
        if restoration:
            evaporation_demand *= 0.88
            soil_quality = min(
                100.0, soil_quality + (
                    0.12 if drought_period else 0.04
                )
            )

        old_storage = storage
        infiltrated_rain = rain * infiltration_fraction
        surface_runoff = rain - infiltrated_rain
        available_water = old_storage + infiltrated_rain
        actual_evapotranspiration = min(
            available_water,
            evaporation_demand
            * (0.65 + 0.007 * vegetation)
            * (0.75 + 0.005 * soil_quality),
        )
        after_evapotranspiration = (
            available_water - actual_evapotranspiration
        )
        bucket_overflow = max(
            0.0, after_evapotranspiration - SOIL_CAPACITY_MM
        )
        storage = min(
            SOIL_CAPACITY_MM, after_evapotranspiration
        )
        runoff = surface_runoff + bucket_overflow

        # Water conservation: old storage + rain = new storage + runoff + AET.
        residual = old_storage + rain - (
            storage + runoff + actual_evapotranspiration
        )

        moisture_fraction = storage / SOIL_CAPACITY_MM
        vegetation = min(
            100.0,
            max(
                0.0,
                vegetation
                + 0.18 * (moisture_fraction - 0.35)
                - 0.04 * max(0.0, temperature - 28.0),
            ),
        )
        rows.append({
            "day": day,
            "rain_mm": rain,
            "temperature_c": temperature,
            "soil_water_mm": storage,
            "runoff_mm": runoff,
            "actual_evapotranspiration_mm": actual_evapotranspiration,
            "vegetation_index": vegetation,
            "soil_quality_index": soil_quality,
            "balance_residual_mm": residual,
        })
    return rows


SCENARIOS = {
    "reference": simulate(),
    "drought_control": simulate(
        rain_factor=0.62, temperature_add=4.0
    ),
    "combined_restoration": simulate(
        rain_factor=0.62,
        temperature_add=4.0,
        infiltration=0.78,
        restoration=True,
    ),
}


def acceptance_tests():
    assert generate_weather(SEED) == generate_weather(SEED)
    assert all(len(rows) == DAYS for rows in SCENARIOS.values())

    for name, rows in SCENARIOS.items():
        assert all(
            -1e-10 <= row["soil_water_mm"]
            <= SOIL_CAPACITY_MM + 1e-10
            for row in rows
        ), f"storage bounds: {name}"
        assert all(
            0.0 <= row["vegetation_index"] <= 100.0
            for row in rows
        ), f"vegetation bounds: {name}"
        assert all(
            abs(row["balance_residual_mm"]) < 1e-8
            for row in rows
        ), f"water conservation: {name}"

    drought = SCENARIOS["drought_control"]
    restoration = SCENARIOS["combined_restoration"]
    assert [r["rain_mm"] for r in drought] == [
        r["rain_mm"] for r in restoration
    ]
    assert [r["temperature_c"] for r in drought] == [
        r["temperature_c"] for r in restoration
    ]


def summarize():
    result = {}
    for name, rows in SCENARIOS.items():
        result[name] = {
            "total_rain_mm": round(sum(
                r["rain_mm"] for r in rows
            ), 3),
            "total_runoff_mm": round(sum(
                r["runoff_mm"] for r in rows
            ), 3),
            "total_actual_evapotranspiration_mm": round(sum(
                r["actual_evapotranspiration_mm"] for r in rows
            ), 3),
            "final_soil_water_mm": round(
                rows[-1]["soil_water_mm"], 3
            ),
            "final_vegetation_index": round(
                rows[-1]["vegetation_index"], 3
            ),
            "max_abs_balance_residual_mm": max(
                abs(r["balance_residual_mm"]) for r in rows
            ),
        }
    return result


if __name__ == "__main__":
    acceptance_tests()
    print(json.dumps({
        "seed": SEED,
        "days": DAYS,
        "acceptance_tests": "PASS",
        "scenario_metrics": summarize(),
    }, indent=2))
