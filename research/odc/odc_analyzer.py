#!/usr/bin/env python3
"""Exploratory Lorentz–Lorenz density predictor; standard library only.

This is a model comparator, not a material-property database and not a proof.
Input CSV minimum columns:
sample_id,material,composition_id,wavelength_nm,temperature_C,
density_kg_m3,n_measured,reference_density_kg_m3,reference_n
"""
import argparse
import csv
import math
from pathlib import Path

REQUIRED = (
    "sample_id", "material", "composition_id", "wavelength_nm",
    "temperature_C", "density_kg_m3", "n_measured",
    "reference_density_kg_m3", "reference_n",
)


def lorentz_lorenz_predict(density, reference_density, reference_n):
    """Predict n by holding specific Lorentz–Lorenz refractivity constant."""
    values = (density, reference_density, reference_n)
    if not all(math.isfinite(float(v)) for v in values):
        raise ValueError("all inputs must be finite")
    density = float(density)
    reference_density = float(reference_density)
    reference_n = float(reference_n)
    if density <= 0 or reference_density <= 0:
        raise ValueError("densities must be positive")
    if reference_n <= 0:
        raise ValueError("reference_n must be positive")
    q0 = (reference_n ** 2 - 1.0) / (reference_n ** 2 + 2.0)
    q = q0 * density / reference_density
    if not (-0.5 < q < 1.0):
        raise ValueError("Lorentz–Lorenz transformed value outside physical domain")
    return math.sqrt((1.0 + 2.0 * q) / (1.0 - q))


def analyze(input_path, output_path):
    with open(input_path, newline="", encoding="utf-8-sig") as f:
        reader = csv.DictReader(f)
        missing = [c for c in REQUIRED if c not in (reader.fieldnames or [])]
        if missing:
            raise ValueError("missing columns: " + ", ".join(missing))
        rows = list(reader)

    results = []
    for row in rows:
        for key in ("material", "composition_id", "wavelength_nm", "temperature_C"):
            if not row[key].strip():
                raise ValueError(f"{row['sample_id']}: missing {key}")
        predicted = lorentz_lorenz_predict(
            row["density_kg_m3"], row["reference_density_kg_m3"], row["reference_n"]
        )
        measured = float(row["n_measured"])
        if not math.isfinite(measured) or measured <= 0:
            raise ValueError(f"{row['sample_id']}: n_measured must be finite and positive")
        out = dict(row)
        out["n_predicted_LL"] = f"{predicted:.12g}"
        out["residual_measured_minus_predicted"] = f"{measured - predicted:.12g}"
        out["relative_residual"] = f"{(measured - predicted) / predicted:.12g}"
        out["interpretation"] = "MODEL_COMPARISON_NOT_PROOF"
        results.append(out)

    fields = list(reader.fieldnames) + [
        "n_predicted_LL", "residual_measured_minus_predicted",
        "relative_residual", "interpretation",
    ]
    with open(output_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fields)
        writer.writeheader()
        writer.writerows(results)
    return len(results)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input_csv", type=Path)
    parser.add_argument("output_csv", type=Path)
    args = parser.parse_args()
    count = analyze(args.input_csv, args.output_csv)
    print(f"Analyzed {count} rows; outputs are model comparisons, not proof.")


if __name__ == "__main__":
    main()
