"""Shared, typed contract for composing independent Matrixgame engines."""
from __future__ import annotations

from dataclasses import dataclass
from typing import Generic, Protocol, TypeVar

InputT = TypeVar("InputT", contravariant=True)
OutputT = TypeVar("OutputT", covariant=True)
StartT = TypeVar("StartT")
MiddleT = TypeVar("MiddleT")
EndT = TypeVar("EndT")


class Engine(Protocol[InputT, OutputT]):
    """Structural interface implemented by any one-step engine."""

    def step(self, value: InputT) -> OutputT:
        """Transform one explicit input state into one explicit output."""


@dataclass(frozen=True)
class PipelineResult(Generic[MiddleT, EndT]):
    """Keep both stage outputs visible for auditing and debugging."""

    intermediate: MiddleT
    final: EndT


class EnginePipeline(Generic[StartT, MiddleT, EndT]):
    """Compose two compatible engines without coupling their internals."""

    def __init__(
        self,
        first: Engine[StartT, MiddleT],
        second: Engine[MiddleT, EndT],
    ) -> None:
        self.first = first
        self.second = second

    def step(self, value: StartT) -> PipelineResult[MiddleT, EndT]:
        intermediate = self.first.step(value)
        final = self.second.step(intermediate)
        return PipelineResult(intermediate=intermediate, final=final)
