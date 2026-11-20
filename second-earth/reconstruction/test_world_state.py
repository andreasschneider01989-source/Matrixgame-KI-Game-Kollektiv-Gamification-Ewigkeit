import unittest

from reconstruction.world_state import Observation, WorldState, collective_update


def obs(oid="obs-1", *, pos=(1.0, 2.0, 3.0), confidence=0.8, step=1,
        source="camera-a", voxels=(), mesh=None):
    return Observation(oid, "tree-001", pos, confidence, step, source,
                       material_id="wood", voxel_cells=tuple(voxels),
                       mesh_asset_id=mesh)


class PersistentWorldTests(unittest.TestCase):
    def test_creates_stable_object_from_observation(self):
        world = WorldState("second-earth-001")
        result = collective_update(world, [obs()])
        self.assertEqual(result["accepted_observation_ids"], ["obs-1"])
        self.assertIn("tree-001", world.objects)
        self.assertEqual(world.step, 1)

    def test_fuses_two_consistent_agent_observations_by_confidence(self):
        world = WorldState("second-earth-001")
        collective_update(world, [
            obs("a", pos=(0, 0, 0), confidence=0.25, source="agent-a"),
            obs("b", pos=(2, 0, 0), confidence=0.75, source="agent-b"),
        ], min_confidence=0.2)
        self.assertAlmostEqual(world.objects["tree-001"].position_m[0], 1.5)

    def test_confidence_accumulates_but_never_exceeds_one(self):
        world = WorldState("second-earth-001")
        collective_update(world, [obs("a", confidence=0.8)])
        collective_update(world, [obs("b", confidence=0.8, step=2)])
        self.assertAlmostEqual(world.objects["tree-001"].confidence, 0.96)

    def test_low_confidence_proposal_is_rejected(self):
        world = WorldState("second-earth-001")
        result = collective_update(world, [obs(confidence=0.2)])
        self.assertEqual(result["rejected_observation_ids"], ["obs-1"])
        self.assertFalse(world.objects)

    def test_zero_confidence_proposal_is_rejected_even_with_zero_threshold(self):
        world = WorldState("second-earth-001")
        result = collective_update(world, [obs(confidence=0.0)], min_confidence=0.0)
        self.assertEqual(result["rejected_observation_ids"], ["obs-1"])
        self.assertFalse(world.objects)

    def test_inconsistent_agent_proposals_are_not_averaged(self):
        world = WorldState("second-earth-001")
        result = collective_update(world, [
            obs("a", pos=(0, 0, 0)), obs("b", pos=(20, 0, 0), source="agent-b")
        ])
        self.assertEqual(result["conflict_observation_ids"], ["a", "b"])
        self.assertFalse(world.objects)

    def test_world_conflict_does_not_overwrite_existing_object(self):
        world = WorldState("second-earth-001")
        collective_update(world, [obs(pos=(0, 0, 0))])
        result = collective_update(world, [obs("later", pos=(10, 0, 0), step=2)])
        self.assertEqual(result["conflict_observation_ids"], ["later"])
        self.assertEqual(world.objects["tree-001"].position_m, (0, 0, 0))

    def test_duplicate_observation_is_idempotent(self):
        world = WorldState("second-earth-001")
        first = collective_update(world, [obs()])
        second = collective_update(world, [obs()])
        self.assertEqual(first["accepted_observation_ids"], ["obs-1"])
        self.assertEqual(second["accepted_observation_ids"], [])
        self.assertAlmostEqual(world.objects["tree-001"].confidence, 0.8)

    def test_stale_observation_is_rejected(self):
        world = WorldState("second-earth-001", step=5)
        result = collective_update(world, [obs(step=3)])
        self.assertEqual(result["rejected_observation_ids"], ["obs-1"])

    def test_hybrid_voxel_and_mesh_references_are_preserved(self):
        world = WorldState("second-earth-001")
        collective_update(world, [obs(voxels=[(0, 0, 0), (0, 1, 0)], mesh="mesh://tree-001")])
        item = world.objects["tree-001"]
        self.assertEqual(len(item.voxel_cells), 2)
        self.assertEqual(item.mesh_asset_id, "mesh://tree-001")

    def test_json_roundtrip_is_deterministic(self):
        world = WorldState("second-earth-001")
        collective_update(world, [obs(voxels=[(1, 2, 3)], mesh="mesh://tree")])
        restored = WorldState.from_json(world.to_json())
        self.assertEqual(restored.to_dict(), world.to_dict())
        self.assertEqual(restored.to_json(), world.to_json())

    def test_invalid_confidence_and_scale_are_rejected(self):
        with self.assertRaises(ValueError):
            obs(confidence=1.5)
        with self.assertRaises(ValueError):
            Observation("x", "y", (0, 0, 0), 0.8, 1, "cam", scale_m=(1, 0, 1))

    def test_invalid_update_threshold_is_rejected(self):
        with self.assertRaises(ValueError):
            collective_update(WorldState("w"), [], max_position_delta_m=-1)


if __name__ == "__main__":
    unittest.main()
