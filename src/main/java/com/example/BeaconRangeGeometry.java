package com.example;

import java.util.function.IntBinaryOperator;

public final class BeaconRangeGeometry {
	private BeaconRangeGeometry() {
	}

	public static int findHighestSurface(
			int centerX,
			int centerZ,
			int range,
			int minimumHeight,
			IntBinaryOperator heightAt
	) {
		int highestSurface = minimumHeight;
		for (int x = centerX - range; x <= centerX + range; x++) {
			for (int z = centerZ - range; z <= centerZ + range; z++) {
				highestSurface = Math.max(highestSurface, heightAt.applyAsInt(x, z));
			}
		}
		return highestSurface;
	}

	public static int calculateTop(int highestSurface, int clearance, int maxBuildHeight) {
		return Math.min(maxBuildHeight, highestSurface + clearance);
	}
}
