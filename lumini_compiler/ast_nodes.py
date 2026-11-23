"""Immutable AST nodes for the LUMINI expression subset."""
from __future__ import annotations

from dataclasses import dataclass
from typing import Union


@dataclass(frozen=True)
class NumberNode:
    value: float


@dataclass(frozen=True)
class NameNode:
    name: str


@dataclass(frozen=True)
class UnaryNode:
    operator: str
    operand: "Expression"


@dataclass(frozen=True)
class BinaryNode:
    operator: str
    left: "Expression"
    right: "Expression"


@dataclass(frozen=True)
class CallNode:
    function: str
    arguments: tuple["Expression", ...]


Expression = Union[NumberNode, NameNode, UnaryNode, BinaryNode, CallNode]


def to_data(node: Expression) -> dict:
    if isinstance(node, NumberNode):
        return {"node": "number", "value": node.value}
    if isinstance(node, NameNode):
        return {"node": "name", "name": node.name}
    if isinstance(node, UnaryNode):
        return {"node": "unary", "operator": node.operator, "operand": to_data(node.operand)}
    if isinstance(node, BinaryNode):
        return {
            "node": "binary", "operator": node.operator,
            "left": to_data(node.left), "right": to_data(node.right),
        }
    if isinstance(node, CallNode):
        return {"node": "call", "function": node.function, "arguments": [to_data(arg) for arg in node.arguments]}
    raise TypeError(f"Unsupported AST node: {type(node).__name__}")


def from_data(data: dict) -> Expression:
    kind = data.get("node")
    if kind == "number":
        return NumberNode(float(data["value"]))
    if kind == "name":
        return NameNode(str(data["name"]))
    if kind == "unary":
        return UnaryNode(str(data["operator"]), from_data(data["operand"]))
    if kind == "binary":
        return BinaryNode(str(data["operator"]), from_data(data["left"]), from_data(data["right"]))
    if kind == "call":
        return CallNode(str(data["function"]), tuple(from_data(item) for item in data["arguments"]))
    raise ValueError(f"Unknown AST node kind: {kind!r}")
