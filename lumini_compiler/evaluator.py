"""Dimensional expression evaluator. It never invokes Python eval or user code."""
from __future__ import annotations

import math
from typing import Callable

from matrixgame_science.units import (
    AMOUNT, CURRENT, DIMENSIONLESS, LENGTH, LUMINOUS_INTENSITY,
    MASS, TEMPERATURE, TIME, Dimension, Quantity,
)

from .ast_nodes import BinaryNode, CallNode, Expression, NameNode, NumberNode, UnaryNode

ValueResolver = Callable[[str], Quantity]
_MAX_ABS_INTEGER_POWER = 64


def _unit(value: float, dimension: Dimension) -> Quantity:
    return Quantity(value, dimension)


UNIT_REGISTRY: dict[str, Quantity] = {
    "kg": _unit(1.0, MASS),
    "g": _unit(1e-3, MASS),
    "m": _unit(1.0, LENGTH),
    "km": _unit(1e3, LENGTH),
    "cm": _unit(1e-2, LENGTH),
    "mm": _unit(1e-3, LENGTH),
    "s": _unit(1.0, TIME),
    "min": _unit(60.0, TIME),
    "h": _unit(3600.0, TIME),
    "day": _unit(86400.0, TIME),
    "A": _unit(1.0, CURRENT),
    "K": _unit(1.0, TEMPERATURE),
    "mol": _unit(1.0, AMOUNT),
    "cd": _unit(1.0, LUMINOUS_INTENSITY),
}

UNIT_REGISTRY.update({
    "Hz": _unit(1.0, DIMENSIONLESS / TIME),
    "N": _unit(1.0, MASS * LENGTH / (TIME ** 2)),
    "J": _unit(1.0, MASS * (LENGTH ** 2) / (TIME ** 2)),
    "W": _unit(1.0, MASS * (LENGTH ** 2) / (TIME ** 3)),
    "Pa": _unit(1.0, MASS / LENGTH / (TIME ** 2)),
    "L": _unit(1e-3, LENGTH ** 3),
    "rad": _unit(1.0, DIMENSIONLESS),
})


class EvaluationError(ValueError):
    pass


def evaluate(node: Expression, resolver: ValueResolver) -> Quantity:
    if isinstance(node, NumberNode):
        return Quantity(node.value, DIMENSIONLESS)

    if isinstance(node, NameNode):
        try:
            return resolver(node.name)
        except KeyError as exc:
            raise EvaluationError(f"unknown symbol {node.name!r}") from exc

    if isinstance(node, UnaryNode):
        value = evaluate(node.operand, resolver)
        return value if node.operator == "+" else Quantity(-value.value, value.dimension)

    if isinstance(node, BinaryNode):
        left = evaluate(node.left, resolver)
        right = evaluate(node.right, resolver)
        if node.operator == "+":
            return left + right
        if node.operator == "-":
            return left - right
        if node.operator == "*":
            return left * right
        if node.operator == "/":
            return left / right
        if node.operator == "^":
            if right.dimension != DIMENSIONLESS:
                raise EvaluationError("an exponent must be dimensionless")
            exponent = int(right.value)
            if right.value != exponent:
                raise EvaluationError("only integer powers are supported in v0.1")
            if abs(exponent) > _MAX_ABS_INTEGER_POWER:
                raise EvaluationError(f"integer powers are limited to +/-{_MAX_ABS_INTEGER_POWER} in v0.1")
            if left.value == 0 and exponent < 0:
                raise EvaluationError("zero cannot be raised to a negative power")
            result = left.value ** exponent
            return Quantity(result, left.dimension ** exponent)
        raise EvaluationError(f"unsupported operator {node.operator!r}")

    if isinstance(node, CallNode):
        arguments = [evaluate(item, resolver) for item in node.arguments]
        if node.function == "sqrt":
            if len(arguments) != 1:
                raise EvaluationError("sqrt() expects exactly one argument")
            value = arguments[0]
            if value.value < 0:
                raise EvaluationError("sqrt() requires a non-negative value")
            if any(power % 2 for power in value.dimension.exponents):
                raise EvaluationError("sqrt() requires even unit exponents")
            return Quantity(math.sqrt(value.value), Dimension(tuple(power // 2 for power in value.dimension.exponents)))
        if node.function == "abs":
            if len(arguments) != 1:
                raise EvaluationError("abs() expects exactly one argument")
            return Quantity(abs(arguments[0].value), arguments[0].dimension)
        if node.function in {"min", "max"}:
            if not arguments:
                raise EvaluationError(f"{node.function}() expects at least one argument")
            dimension = arguments[0].dimension
            if any(item.dimension != dimension for item in arguments[1:]):
                raise EvaluationError(f"all {node.function}() arguments must have matching units")
            selected = min(arguments, key=lambda item: item.value) if node.function == "min" else max(arguments, key=lambda item: item.value)
            return selected
        if node.function in {"sin", "cos", "exp", "ln"}:
            if len(arguments) != 1 or arguments[0].dimension != DIMENSIONLESS:
                raise EvaluationError(f"{node.function}() expects one dimensionless argument")
            raw = arguments[0].value
            if node.function == "sin":
                result = math.sin(raw)
            elif node.function == "cos":
                result = math.cos(raw)
            elif node.function == "exp":
                result = math.exp(raw)
            else:
                if raw <= 0:
                    raise EvaluationError("ln() requires a positive argument")
                result = math.log(raw)
            return Quantity(result, DIMENSIONLESS)
        raise EvaluationError(f"function {node.function!r} is not in the safe function registry")

    raise EvaluationError(f"unsupported expression node {type(node).__name__}")


def resolve_definitions(definitions: list[dict] | tuple[dict, ...]) -> dict[str, Quantity]:
    by_name = {item["name"]: item for item in definitions}
    resolved: dict[str, Quantity] = dict(UNIT_REGISTRY)
    resolving: list[str] = []

    def resolve(name: str) -> Quantity:
        if name in resolved:
            return resolved[name]
        if name not in by_name:
            raise EvaluationError(f"unknown symbol {name!r}")
        if name in resolving:
            cycle = " -> ".join(resolving + [name])
            raise EvaluationError(f"cyclic definition dependency: {cycle}")
        resolving.append(name)
        try:
            item = by_name[name]
            try:
                value = evaluate_data(item["ast"], resolve)
            except (EvaluationError, ValueError, ZeroDivisionError, OverflowError) as exc:
                raise EvaluationError(f"{name} at line {item.get('line', '?')}: {exc}") from exc
            resolved[name] = value
            return value
        finally:
            resolving.pop()

    for name in by_name:
        resolve(name)
    return {name: resolved[name] for name in by_name}


def evaluate_data(data: dict, resolver: ValueResolver) -> Quantity:
    kind = data.get("node")
    if kind == "number":
        return Quantity(float(data["value"]), DIMENSIONLESS)
    if kind == "name":
        return resolver(str(data["name"]))
    if kind == "unary":
        value = evaluate_data(data["operand"], resolver)
        return value if data["operator"] == "+" else Quantity(-value.value, value.dimension)
    if kind == "binary":
        left = evaluate_data(data["left"], resolver)
        right = evaluate_data(data["right"], resolver)
        operator = data["operator"]
        if operator == "+":
            return left + right
        if operator == "-":
            return left - right
        if operator == "*":
            return left * right
        if operator == "/":
            return left / right
        if operator == "^":
            if right.dimension != DIMENSIONLESS or int(right.value) != right.value:
                raise EvaluationError("only integer, dimensionless powers are supported")
            exponent = int(right.value)
            if abs(exponent) > _MAX_ABS_INTEGER_POWER:
                raise EvaluationError(f"integer powers are limited to +/-{_MAX_ABS_INTEGER_POWER} in v0.1")
            if left.value == 0 and exponent < 0:
                raise EvaluationError("zero cannot be raised to a negative power")
            return Quantity(left.value ** exponent, left.dimension ** exponent)
    if kind == "call":
        from .ast_nodes import from_data
        return evaluate(from_data(data), resolver)
    raise EvaluationError(f"invalid AST data: {data!r}")
