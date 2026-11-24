"""Testable scientific model primitives for Matrixgame."""
from .biology import replicator_step
from .cellular_maintenance import CellState, CellularMaintenanceEngine, MaintenanceConfig, MaintenanceResult
from .chemistry import michaelis_menten_rate
from .mathematics import shannon_entropy, softmax
from .network_learning import NetworkEdge, NetworkLearningConfig, NetworkLearningEngine, NetworkLearningResult, NetworkNode
from .engine_contracts import Engine, EnginePipeline, PipelineResult
from .health_network import MaintenanceNetworkConfig, MaintenanceToNetworkAdapter, RegenerationNetworkPipeline
from .closed_loop_learning import ClosedLoopLearningEngine, ControlConfig, ControlTransition, EpisodeResult, LearningStep, ScalarTargetEnvironment, TemporalDifferenceController
from .physics_guided_learning import LinearAngleNeuron, NeuronTrainingResult, reflect_angle_deg, reflect_vector
from .point_mass_agent import PointMassConfig, PointMassEnvironment, PointMassEpisode, PointMassLearningEngine, PointMassState, PointMassTransition, PointMassQLearner
from .physics_validation import Trajectory, TrajectoryError, acceleration_from_force, compare_trajectory, integrate_constant_acceleration, momentum, work
from .physics import constant_acceleration_step, gravitational_acceleration, kinetic_energy, relative_position
from .psychology import decision_probabilities
from .units import ACCELERATION, CONCENTRATION, DIMENSIONLESS, ENERGY, FORCE, LENGTH, MASS, TIME, VELOCITY, Dimension, Quantity

__all__ = [
    "ACCELERATION", "CONCENTRATION", "DIMENSIONLESS", "ENERGY", "FORCE",
    "LENGTH", "MASS", "TIME", "VELOCITY", "Dimension", "Quantity",
    "CellState", "CellularMaintenanceEngine", "MaintenanceConfig", "MaintenanceResult",
    "NetworkEdge", "NetworkLearningConfig", "NetworkLearningEngine", "NetworkLearningResult", "NetworkNode",
    "Engine", "EnginePipeline", "PipelineResult", "MaintenanceNetworkConfig", "MaintenanceToNetworkAdapter", "RegenerationNetworkPipeline",
    "ClosedLoopLearningEngine", "ControlConfig", "ControlTransition", "EpisodeResult", "LearningStep",
    "ScalarTargetEnvironment", "TemporalDifferenceController",
    "LinearAngleNeuron", "NeuronTrainingResult", "reflect_angle_deg", "reflect_vector",
    "PointMassConfig", "PointMassEnvironment", "PointMassEpisode", "PointMassLearningEngine",
    "PointMassState", "PointMassTransition", "PointMassQLearner",
    "Trajectory", "TrajectoryError", "acceleration_from_force", "compare_trajectory",
    "integrate_constant_acceleration", "momentum", "work",
    "constant_acceleration_step", "decision_probabilities",
    "gravitational_acceleration", "kinetic_energy", "michaelis_menten_rate",
    "relative_position", "replicator_step", "shannon_entropy", "softmax",
]
