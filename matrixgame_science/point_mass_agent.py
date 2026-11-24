"""Physics-grounded 2D point-mass environment with a tabular learning agent.

This is an educational mechanics toy model, not a biomechanical or molecular
organism simulator.
"""
from __future__ import annotations
from dataclasses import dataclass
import math
import random

ACTIONS = ((0.,0.), (1.,0.), (-1.,0.), (0.,1.), (0.,-1.),
          (1.,1.), (1.,-1.), (-1.,1.), (-1.,-1.))


def _finite(name: str, value: float) -> float:
    value = float(value)
    if not math.isfinite(value):
        raise ValueError(f"{name} must be finite.")
    return value


@dataclass(frozen=True)
class PointMassConfig:
    dt: float = 0.1
    mass: float = 1.0
    max_force: float = 1.0
    damping: float = 0.96
    max_speed: float = 1.5
    world_size: float = 10.0
    target_radius: float = 0.35
    gravity_x: float = 0.0
    gravity_y: float = 0.0
    learning_rate: float = 0.18
    discount: float = 0.94
    exploration: float = 0.2
    bins: int = 6

    def __post_init__(self) -> None:
        for name in ("dt", "mass", "max_force", "max_speed", "world_size", "target_radius"):
            value = _finite(name, getattr(self, name))
            if value <= 0:
                raise ValueError(f"{name} must be positive.")
            object.__setattr__(self, name, value)
        for name in ("gravity_x", "gravity_y"):
            object.__setattr__(self, name, _finite(name, getattr(self, name)))
        for name in ("damping", "learning_rate", "discount", "exploration"):
            value = _finite(name, getattr(self, name))
            if not 0 <= value <= 1:
                raise ValueError(f"{name} must be within [0, 1].")
            object.__setattr__(self, name, value)
        if self.bins < 2:
            raise ValueError("bins must be at least 2.")


@dataclass(frozen=True)
class PointMassState:
    x: float
    y: float
    vx: float = 0.0
    vy: float = 0.0


@dataclass(frozen=True)
class PointMassTransition:
    state: PointMassState
    action: int
    next_state: PointMassState
    reward: float
    reached_target: bool
    energy_spent: float


@dataclass(frozen=True)
class PointMassEpisode:
    initial_state: PointMassState
    final_state: PointMassState
    steps: int
    reached_target: bool
    total_reward: float
    total_energy: float


class PointMassEnvironment:
    """Newtonian point mass with a bounded world, damping, and target reward."""
    def __init__(self, config: PointMassConfig | None = None,
                 target: tuple[float, float] | None = None) -> None:
        self.config = config or PointMassConfig()
        self.target = target or (0.8 * self.config.world_size, 0.8 * self.config.world_size)
        tx, ty = (_finite("target coordinate", v) for v in self.target)
        if not (0 <= tx <= self.config.world_size and 0 <= ty <= self.config.world_size):
            raise ValueError("target must lie inside the world.")
        self.target = (tx, ty)

    def distance(self, state: PointMassState) -> float:
        return math.hypot(self.target[0] - state.x, self.target[1] - state.y)

    def step(self, state: PointMassState, action: int) -> PointMassTransition:
        if not 0 <= action < len(ACTIONS):
            raise ValueError("action index is outside the action space.")
        if not all(math.isfinite(float(v)) for v in (state.x, state.y, state.vx, state.vy)):
            raise ValueError("state values must be finite.")
        if not (0 <= state.x <= self.config.world_size and 0 <= state.y <= self.config.world_size):
            raise ValueError("state position must lie inside the world.")
        ux, uy = ACTIONS[action]
        length = math.hypot(ux, uy)
        if length:
            ux, uy = ux / length, uy / length
        fx, fy = ux * self.config.max_force, uy * self.config.max_force
        ax = fx / self.config.mass + self.config.gravity_x
        ay = fy / self.config.mass + self.config.gravity_y
        dt = self.config.dt
        vx = (state.vx + ax * dt) * self.config.damping
        vy = (state.vy + ay * dt) * self.config.damping
        speed = math.hypot(vx, vy)
        if speed > self.config.max_speed:
            scale = self.config.max_speed / speed
            vx, vy = vx * scale, vy * scale
        x, y = state.x + vx * dt, state.y + vy * dt
        if x < 0 or x > self.config.world_size:
            x = min(self.config.world_size, max(0., x))
            vx = -vx * 0.5
        if y < 0 or y > self.config.world_size:
            y = min(self.config.world_size, max(0., y))
            vy = -vy * 0.5
        nxt = PointMassState(x, y, vx, vy)
        before, after = self.distance(state), self.distance(nxt)
        reached = after <= self.config.target_radius
        reward = before - after - 0.002 + (1.0 if reached else 0.0)
        energy = math.hypot(fx, fy) * math.hypot(vx, vy) * dt
        return PointMassTransition(state, action, nxt, reward, reached, energy)


class PointMassQLearner:
    """Tabular Q-learning over coarse target-relative position and velocity bins."""
    def __init__(self, config: PointMassConfig | None = None, seed: int = 0) -> None:
        self.config = config or PointMassConfig()
        self._rng = random.Random(seed)
        self._q: dict[tuple[int, ...], list[float]] = {}

    def state_key(self, state: PointMassState, target: tuple[float, float]) -> tuple[int, ...]:
        bins, size = self.config.bins, self.config.world_size
        def pos(v: float) -> int:
            z = min(1., max(-1., v / size))
            return min(bins - 1, max(0, int((z + 1.) * 0.5 * bins)))
        def vel(v: float) -> int:
            z = min(1., max(-1., v / self.config.max_speed))
            return min(bins - 1, max(0, int((z + 1.) * 0.5 * bins)))
        return (pos(target[0] - state.x), pos(target[1] - state.y),
                vel(state.vx), vel(state.vy))

    def choose_action(self, state: PointMassState, target: tuple[float, float],
                      explore: bool = True) -> int:
        values = self._q.setdefault(self.state_key(state, target), [0.] * len(ACTIONS))
        if explore and self._rng.random() < self.config.exploration:
            return self._rng.randrange(len(ACTIONS))
        return values.index(max(values))

    def learn(self, transition: PointMassTransition, target: tuple[float, float]) -> float:
        values = self._q.setdefault(self.state_key(transition.state, target), [0.] * len(ACTIONS))
        future = self._q.setdefault(self.state_key(transition.next_state, target), [0.] * len(ACTIONS))
        error = transition.reward + self.config.discount * max(future) - values[transition.action]
        values[transition.action] += self.config.learning_rate * error
        return error


class PointMassLearningEngine:
    """Observe -> choose force -> integrate physics -> receive reward -> update Q."""
    def __init__(self, config: PointMassConfig | None = None,
                 target: tuple[float, float] | None = None, seed: int = 0) -> None:
        self.environment = PointMassEnvironment(config, target)
        self.config = self.environment.config
        self.learner = PointMassQLearner(self.config, seed)

    def run_episode(self, initial_state: PointMassState = PointMassState(1., 1.),
                    max_steps: int = 250, explore: bool = True) -> PointMassEpisode:
        if max_steps < 1:
            raise ValueError("max_steps must be positive.")
        state = initial_state
        total_reward = total_energy = 0.
        reached = False
        steps = 0
        for steps in range(1, max_steps + 1):
            action = self.learner.choose_action(state, self.environment.target, explore)
            transition = self.environment.step(state, action)
            self.learner.learn(transition, self.environment.target)
            state = transition.next_state
            total_reward += transition.reward
            total_energy += transition.energy_spent
            if transition.reached_target:
                reached = True
                break
        return PointMassEpisode(initial_state, state, steps, reached, total_reward, total_energy)
