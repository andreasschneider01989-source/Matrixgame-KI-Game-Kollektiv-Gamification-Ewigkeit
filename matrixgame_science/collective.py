"""Multi-objective comparison helpers. No universal value weights are imposed."""
from __future__ import annotations
from typing import Sequence


def pareto_frontier(candidates: Sequence[Sequence[float]]) -> tuple[int, ...]:
    """Indices not dominated when every objective is to be maximized."""
    rows = tuple(tuple(map(float, row)) for row in candidates)
    if not rows:
        return ()
    width = len(rows[0])
    if width == 0 or any(len(row) != width for row in rows):
        raise ValueError("Candidates must form a non-empty rectangular matrix.")
    frontier = []
    for index, candidate in enumerate(rows):
        dominated = False
        for other_index, other in enumerate(rows):
            if other_index == index:
                continue
            no_worse = all(a >= b for a, b in zip(other, candidate))
            strictly_better = any(a > b for a, b in zip(other, candidate))
            if no_worse and strictly_better:
                dominated = True
                break
        if not dominated:
            frontier.append(index)
    return tuple(frontier)
