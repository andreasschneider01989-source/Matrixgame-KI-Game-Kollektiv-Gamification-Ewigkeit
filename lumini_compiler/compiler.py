"""LUMINI v0.1 compiler, typed Model IR generator and deterministic formula runner."""
from __future__ import annotations

import hashlib
import json
import math
from pathlib import Path
from typing import Any

from matrixgame_science.units import Quantity

from .ast_nodes import to_data
from .evaluator import EvaluationError, UNIT_REGISTRY, evaluate, resolve_definitions
from .parser import Definition, ModelSource, ParseError, parse_model

COMPILER_VERSION = "0.1.0"
IR_VERSION = "0.1.0"
_ALLOWED_EVIDENCE = {"FACT", "MODEL", "HYPOTHESIS", "OPEN", "FICTION"}


class CompileError(ValueError):
    pass


def _dimension_data(quantity: Quantity) -> list[int]:
    return list(quantity.dimension.exponents)


def _finite(value: float, name: str) -> float:
    number = float(value)
    if not math.isfinite(number):
        raise CompileError(f"{name} evaluated to a non-finite number")
    return number


def _definition_to_ir(definition: Definition) -> dict[str, Any]:
    data: dict[str, Any] = {
        "name": definition.name,
        "kind": definition.kind,
        "expression": definition.expression_text,
        "ast": to_data(definition.expression),
        "source": definition.source,
        "evidence_status": definition.evidence_status,
        "line": definition.line,
    }
    if definition.expected_unit is not None:
        data["expected_unit_ast"] = to_data(definition.expected_unit)
    return data


def compile_text(source: str, source_name: str = "<memory>") -> dict[str, Any]:
    try:
        model = parse_model(source)
    except (ParseError, ValueError) as exc:
        raise CompileError(str(exc)) from exc

    if model.engine_id != "formula.scalar":
        raise CompileError(f"engine {model.engine_id!r} is not registered in this MVP")

    definitions = [_definition_to_ir(item) for item in model.definitions]
    names = {item["name"] for item in definitions}
    for output in model.outputs:
        if output not in names:
            raise CompileError(f"output {output!r} does not refer to a declared parameter, constant or formula")

    for item in definitions:
        evidence = item.get("evidence_status")
        if evidence is not None and evidence not in _ALLOWED_EVIDENCE:
            raise CompileError(f"{item['name']}: unsupported evidence status {evidence!r}")
        if item["name"] in UNIT_REGISTRY:
            raise CompileError(f"{item['name']}: definition shadows a reserved SI unit symbol")

    try:
        values = resolve_definitions(definitions)
    except (EvaluationError, ValueError, ZeroDivisionError, OverflowError) as exc:
        raise CompileError(str(exc)) from exc

    by_name = {item.name: item for item in model.definitions}
    for item in definitions:
        value = values[item["name"]]
        item["value_si"] = _finite(value.value, item["name"])
        item["dimension"] = _dimension_data(value)
        source_def = by_name[item["name"]]
        if source_def.expected_unit is not None:
            try:
                expected = evaluate(source_def.expected_unit, lambda name: UNIT_REGISTRY[name])
            except (KeyError, ValueError, ZeroDivisionError, OverflowError) as exc:
                raise CompileError(f"{item['name']}: invalid declared unit: {exc}") from exc
            if expected.dimension != value.dimension:
                raise CompileError(
                    f"{item['name']}: declared result unit has dimensions {expected.dimension.exponents}, "
                    f"but the expression has {value.dimension.exponents}"
                )
            item["expected_dimension"] = _dimension_data(expected)

    source_hash = hashlib.sha256(source.encode("utf-8")).hexdigest()
    ir: dict[str, Any] = {
        "ir_version": IR_VERSION,
        "language": "LUMINI",
        "compiler_version": COMPILER_VERSION,
        "model": {
            "id": model.model_id,
            "version": model.version,
            "epistemic_status": model.epistemic_status,
            "implementation_status": "PROTOTYPE",
            "engine_id": model.engine_id,
            "description": model.description,
        },
        "definitions": definitions,
        "outputs": list(model.outputs),
        "provenance": {
            "source_name": source_name,
            "source_sha256": source_hash,
            "transform": "LUMINI source -> AST -> dimensional Model IR",
        },
        "validation": {
            "syntax": "PASS",
            "symbol_resolution": "PASS",
            "dimensional_analysis": "PASS",
            "scientific_validation": "NOT_PERFORMED",
        },
    }
    return ir


def compile_file(path: str | Path) -> dict[str, Any]:
    source_path = Path(path)
    source = source_path.read_text(encoding="utf-8")
    return compile_text(source, source_name=source_path.name)


def run_ir(ir: dict[str, Any]) -> dict[str, Any]:
    if ir.get("ir_version") != IR_VERSION:
        raise CompileError(f"unsupported Model IR version {ir.get('ir_version')!r}")
    model = ir.get("model", {})
    if model.get("engine_id") != "formula.scalar":
        raise CompileError(f"engine {model.get('engine_id')!r} is not registered in this MVP")

    definitions = ir.get("definitions", [])
    definition_names = [item.get("name") for item in definitions]
    if len(definition_names) != len(set(definition_names)):
        raise CompileError("Model IR contains duplicate definition names")
    reserved = set(UNIT_REGISTRY).intersection(definition_names)
    if reserved:
        raise CompileError(f"Model IR attempts to redefine reserved unit symbols: {sorted(reserved)}")
    try:
        values = resolve_definitions(definitions)
    except (EvaluationError, ValueError, ZeroDivisionError, OverflowError) as exc:
        raise CompileError(str(exc)) from exc

    output_values = {}
    by_name = {item["name"]: item for item in definitions}
    for item in definitions:
        expected = item.get("expected_dimension")
        if expected is not None and list(values[item["name"]].dimension.exponents) != expected:
            raise CompileError(f"IR dimensional contract failed for {item['name']!r}")

    for name in ir.get("outputs", []):
        if name not in values:
            raise CompileError(f"IR output references unknown definition {name!r}")
        value = values[name]
        output_values[name] = {
            "value_si": _finite(value.value, name),
            "dimension": _dimension_data(value),
            "kind": by_name[name]["kind"],
        }

    canonical_ir = json.dumps(ir, sort_keys=True, separators=(",", ":"), ensure_ascii=False)
    ir_hash = hashlib.sha256(canonical_ir.encode("utf-8")).hexdigest()
    source_hash = ir.get("provenance", {}).get("source_sha256")
    run_identity = json.dumps({
        "ir_sha256": ir_hash,
        "engine_id": model.get("engine_id"),
        "compiler_version": ir.get("compiler_version"),
    }, sort_keys=True, separators=(",", ":"))
    run_id = "run-" + hashlib.sha256(run_identity.encode("utf-8")).hexdigest()[:16]

    return {
        "run_manifest_version": "0.1.0",
        "run_id": run_id,
        "model_id": model.get("id"),
        "model_version": model.get("version"),
        "engine_id": model.get("engine_id"),
        "compiler_version": ir.get("compiler_version"),
        "model_ir_sha256": ir_hash,
        "source_sha256": source_hash,
        "determinism": "DETERMINISTIC_NO_RANDOMNESS_USED",
        "random_seed": None,
        "outputs": output_values,
        "checks": {
            "ir_version_supported": "PASS",
            "outputs_resolved": "PASS",
            "finite_numeric_values": "PASS",
            "empirical_validation": "NOT_PERFORMED",
        },
        "limitations": [
            "This is a scalar formula engine, not a general simulation engine.",
            "Values are represented in coherent SI base units.",
            "Successful compilation does not establish empirical truth of a model.",
        ],
    }


def dump_json(data: dict[str, Any], path: str | Path) -> Path:
    target = Path(path)
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(data, indent=2, sort_keys=True, ensure_ascii=False) + "\n", encoding="utf-8")
    return target
