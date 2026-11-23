from __future__ import annotations

import json
from pathlib import Path

import pytest

from lumini_compiler import CompileError, compile_file, compile_text, run_ir


EXAMPLE = Path(__file__).parents[1] / "examples" / "lumini" / "earth_surface_gravity.lumini"


def test_example_compiles_and_runs_with_expected_dimensions() -> None:
    ir = compile_file(EXAMPLE)
    result = run_ir(ir)
    gravity = result["outputs"]["surface_gravity"]
    weight = result["outputs"]["weight_at_surface"]
    assert gravity["value_si"] == pytest.approx(9.82, rel=0.01)
    assert gravity["dimension"] == [0, 1, -2, 0, 0, 0, 0]
    assert weight["dimension"] == [1, 1, -2, 0, 0, 0, 0]
    assert result["checks"]["empirical_validation"] == "NOT_PERFORMED"


def test_runs_are_deterministic_for_same_ir() -> None:
    ir = compile_file(EXAMPLE)
    assert run_ir(ir) == run_ir(ir)


def test_incompatible_dimensions_are_rejected() -> None:
    source = '''model "bad-units" version "0.1" {
      status MODEL
      parameter mass = 1 * kg
      formula impossible = mass + 1 * m
      output impossible
    }'''
    with pytest.raises(CompileError, match="Dimension mismatch"):
        compile_text(source)


def test_unknown_symbols_are_rejected() -> None:
    source = '''model "unknown" version "0.1" {
      status MODEL
      formula value = missing_symbol * 2
      output value
    }'''
    with pytest.raises(CompileError, match="unknown symbol"):
        compile_text(source)


def test_cyclic_dependencies_are_rejected() -> None:
    source = '''model "cycle" version "0.1" {
      status MODEL
      formula first = second + 1
      formula second = first + 1
      output first
    }'''
    with pytest.raises(CompileError, match="cyclic definition"):
        compile_text(source)


def test_declared_output_units_are_checked() -> None:
    source = '''model "wrong-declared-unit" version "0.1" {
      status MODEL
      parameter distance = 5 * m
      formula speed = distance : s
      output speed
    }'''
    with pytest.raises(CompileError, match="declared result unit"):
        compile_text(source)


def test_division_by_zero_is_rejected() -> None:
    source = '''model "zero-divisor" version "0.1" {
      status MODEL
      formula impossible = 1 / 0
      output impossible
    }'''
    with pytest.raises(CompileError, match="zero-valued quantity"):
        compile_text(source)


def test_only_registered_functions_are_available() -> None:
    source = '''model "unsafe-function" version "0.1" {
      status MODEL
      formula result = system_call(1)
      output result
    }'''
    with pytest.raises(CompileError, match="not in the safe function registry"):
        compile_text(source)


def test_model_ir_is_json_serializable_and_versioned() -> None:
    ir = compile_file(EXAMPLE)
    serialized = json.dumps(ir, sort_keys=True)
    assert '"ir_version": "0.1.0"' in serialized
    assert ir["provenance"]["source_sha256"]


def test_output_must_reference_a_definition() -> None:
    source = '''model "bad-output" version "0.1" {
      status MODEL
      formula result = 1 + 2
      output absent
    }'''
    with pytest.raises(CompileError, match="does not refer"):
        compile_text(source)


def test_unit_prefixes_are_converted_to_si() -> None:
    source = '''model "unit-conversion" version "0.1" {
      status MODEL
      parameter distance = 2 * km
      formula metres = distance : m
      output metres
    }'''
    result = run_ir(compile_text(source))
    assert result["outputs"]["metres"]["value_si"] == pytest.approx(2000.0)


def test_sqrt_preserves_and_checks_dimensions() -> None:
    source = '''model "sqrt" version "0.1" {
      status MODEL
      parameter area = 16 * m^2
      formula side = sqrt(area) : m
      output side
    }'''
    result = run_ir(compile_text(source))
    assert result["outputs"]["side"]["value_si"] == pytest.approx(4.0)


def test_excessive_integer_power_is_rejected() -> None:
    source = '''model "large-power" version "0.1" {
      status MODEL
      formula result = 2 ^ 999999999
      output result
    }'''
    with pytest.raises(CompileError, match="integer powers are limited"):
        compile_text(source)


def test_definition_cannot_shadow_si_unit() -> None:
    source = '''model "shadow" version "0.1" {
      status MODEL
      parameter kg = 3
      output kg
    }'''
    with pytest.raises(CompileError, match="reserved SI unit"):
        compile_text(source)


def test_tampered_ir_cannot_break_declared_dimensions() -> None:
    ir = compile_file(EXAMPLE)
    ir["definitions"][-2]["expected_dimension"] = [1, 0, 0, 0, 0, 0, 0]
    with pytest.raises(CompileError, match="dimensional contract failed"):
        run_ir(ir)
