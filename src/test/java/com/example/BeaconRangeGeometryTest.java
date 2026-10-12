package com.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeaconRangeGeometryTest {
	@Test
	void findsHighestSurfaceAcrossEntireBeaconRange() {
		Map<String, Integer> terrain = Map.of(
				"9,20", 82,
				"10,20", 140,
				"11,20", 96
		);

		int highest = BeaconRangeGeometry.findHighestSurface(
				10,
				20,
				1,
				-64,
				(x, z) -> terrain.getOrDefault(x + "," + z, 70)
		);

		assertEquals(140, highest);
	}

	@Test
	void includesRangeEdges() {
		int highest = BeaconRangeGeometry.findHighestSurface(
				0,
				0,
				2,
				-64,
				(x, z) -> x == 2 && z == -2 ? 123 : -64
		);

		assertEquals(123, highest);
	}

	@Test
	void usesMinimumHeightWhenTerrainIsLower() {
		int highest = BeaconRangeGeometry.findHighestSurface(
				0,
				0,
				1,
				-64,
				(x, z) -> -100
		);

		assertEquals(-64, highest);
	}

	@Test
	void addsClearanceAboveSurface() {
		assertEquals(190, BeaconRangeGeometry.calculateTop(140, 50, 320));
	}

	@Test
	void capsTopAtBuildHeight() {
		assertEquals(320, BeaconRangeGeometry.calculateTop(300, 50, 320));
	}
}
