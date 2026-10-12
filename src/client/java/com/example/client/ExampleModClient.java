package com.example.client;

import com.example.client.mixin.BeaconAccessor;
import com.example.BeaconRangeGeometry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("BeaconRange");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CONFIG_FILE = "beaconrange.json";
	private static final int[] BEACON_COLORS = {
			0xFFFF4D4D, 0xFFFFA64D, 0xFFFFFF4D, 0xFF66FF66,
			0xFF4DFFFF, 0xFF4D80FF, 0xFFB84DFF, 0xFFFF4DB8
	};
	public static final Set<BlockPos> pinnedBeacons = new HashSet<>();
	private static final Map<BeaconKey, Integer> beaconColors = new HashMap<>();
	private static final Map<BeaconKey, Integer> surfaceHeights = new HashMap<>();
	private static final Set<BeaconKey> hiddenBeacons = new HashSet<>();
	private static final KeyMapping TOGGLE_RANGES_KEY = new KeyMapping(
			"key.beacon_range.toggle",
			GLFW.GLFW_KEY_F8,
			KeyMapping.Category.MISC
	);
	private static boolean rangesVisible = true;
	private static long surfaceCacheWindow = Long.MIN_VALUE;
	private static Path configPath;

	@Override
	public void onInitializeClient() {
		configPath = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(CONFIG_FILE);
		loadSettings();
		ClientTickEvents.END_CLIENT_TICK.register(ExampleModClient::handleVisibilityToggle);
	}

	private static void handleVisibilityToggle(Minecraft client) {
		while (TOGGLE_RANGES_KEY.consumeClick()) {
			rangesVisible = !rangesVisible;
			saveSettings();
			if (client.player != null) {
				client.player.sendOverlayMessage(
						Component.literal("Beacon visuals turned " + (rangesVisible ? "on" : "off"))
				);
			}
		}
	}

	public static boolean areRangesVisible() {
		return rangesVisible;
	}

	public static void renderRanges() {
		if (!rangesVisible) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.level == null || client.player == null) {
			return;
		}

		long currentSurfaceCacheWindow = client.level.getGameTime() / 10;
		if (currentSurfaceCacheWindow != surfaceCacheWindow) {
			surfaceHeights.clear();
			surfaceCacheWindow = currentSurfaceCacheWindow;
		}

		Set<BlockPos> visibleBeacons = new HashSet<>(pinnedBeacons);
		BlockPos playerPos = client.player.blockPosition();
		int playerChunkX = playerPos.getX() >> 4;
		int playerChunkZ = playerPos.getZ() >> 4;

		for (int chunkX = playerChunkX - 8; chunkX <= playerChunkX + 8; chunkX++) {
			for (int chunkZ = playerChunkZ - 8; chunkZ <= playerChunkZ + 8; chunkZ++) {
				if (client.level.getChunkSource().hasChunk(chunkX, chunkZ)) {
					LevelChunk chunk = client.level.getChunkSource()
							.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
					if (chunk != null) {
						for (var blockEntity : chunk.getBlockEntities().values()) {
							if (blockEntity instanceof BeaconBlockEntity) {
								visibleBeacons.add(blockEntity.getBlockPos());
							}
						}
					}
				}
			}
		}

		for (BlockPos pos : visibleBeacons) {
			if (hiddenBeacons.contains(currentKey(pos))) {
				continue;
			}

			if (client.level.getBlockEntity(pos) instanceof BeaconBlockEntity beacon) {
				int level = ((BeaconAccessor) beacon).beaconRange$getLevels();
				if (level > 0) {
					int range = level * 10 + 10;
					BeaconKey key = currentKey(pos);
					int surface = surfaceHeights.computeIfAbsent(
							key,
							ignored -> getSurfaceHeight(client.level, pos, range)
					);
					int maxBuildHeight = client.level.dimensionType().minY()
							+ client.level.dimensionType().height();
					int top = Math.min(maxBuildHeight, surface + 50);
					renderBeaconBox(pos, range, top, beaconColor(pos));
				}
			} else {
				pinnedBeacons.remove(pos);
			}
		}
	}

	private static int getSurfaceHeight(net.minecraft.client.multiplayer.ClientLevel level, BlockPos pos, int range) {
		return BeaconRangeGeometry.findHighestSurface(
				pos.getX(),
				pos.getZ(),
				range,
				level.dimensionType().minY(),
				(x, z) -> level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z)
		);
	}

	private static int beaconColor(BlockPos pos) {
		Integer customColor = beaconColors.get(currentKey(pos));
		if (customColor != null) {
			return customColor;
		}

		int hash = pos.hashCode() * 0x45d9f3b;
		hash ^= hash >>> 16;
		return BEACON_COLORS[(hash & 0x7fffffff) % BEACON_COLORS.length];
	}

	public static void setBeaconColor(BlockPos pos, int color) {
		beaconColors.put(currentKey(pos), color);
		saveSettings();
	}

	public static boolean isBeaconRangeVisible(BlockPos pos) {
		return !hiddenBeacons.contains(currentKey(pos));
	}

	public static boolean toggleBeaconRange(BlockPos pos) {
		BeaconKey key = currentKey(pos);
		if (!hiddenBeacons.add(key)) {
			hiddenBeacons.remove(key);
		}
		saveSettings();
		return isBeaconRangeVisible(pos);
	}

	private static BeaconKey currentKey(BlockPos pos) {
		Minecraft client = Minecraft.getInstance();
		String dimension = client.level == null ? "unknown" : client.level.dimension().toString();
		return new BeaconKey(dimension, pos);
	}

	private static void loadSettings() {
		if (configPath == null || !Files.exists(configPath)) {
			return;
		}

		try (var reader = Files.newBufferedReader(configPath)) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
			if (root.has("rangesVisible")) {
				rangesVisible = root.get("rangesVisible").getAsBoolean();
			}

			if (root.has("beacons")) {
				for (JsonElement element : root.getAsJsonArray("beacons")) {
					JsonObject beacon = element.getAsJsonObject();
					BeaconKey key = new BeaconKey(
							beacon.get("dimension").getAsString(),
							new BlockPos(beacon.get("x").getAsInt(), beacon.get("y").getAsInt(),
									beacon.get("z").getAsInt()));
					if (beacon.has("color")) {
						beaconColors.put(key, beacon.get("color").getAsInt());
					}
					if (beacon.has("visible") && !beacon.get("visible").getAsBoolean()) {
						hiddenBeacons.add(key);
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Unable to load Beacon Range settings from {}", configPath, exception);
		}
	}

	private static void saveSettings() {
		if (configPath == null) {
			return;
		}

		JsonObject root = new JsonObject();
		root.addProperty("rangesVisible", rangesVisible);
		JsonArray beacons = new JsonArray();
		Set<BeaconKey> keys = new HashSet<>(beaconColors.keySet());
		keys.addAll(hiddenBeacons);
		for (BeaconKey key : keys) {
			JsonObject beacon = new JsonObject();
			beacon.addProperty("dimension", key.dimension());
			beacon.addProperty("x", key.pos().getX());
			beacon.addProperty("y", key.pos().getY());
			beacon.addProperty("z", key.pos().getZ());
			Integer color = beaconColors.get(key);
			if (color != null) {
				beacon.addProperty("color", color);
			}
			beacon.addProperty("visible", !hiddenBeacons.contains(key));
			beacons.add(beacon);
		}
		root.add("beacons", beacons);

		try {
			Files.createDirectories(configPath.getParent());
			Files.writeString(configPath, GSON.toJson(root));
		} catch (IOException exception) {
			LOGGER.error("Unable to save Beacon Range settings to {}", configPath, exception);
		}
	}

	private record BeaconKey(String dimension, BlockPos pos) {
	}

	private static void renderBeaconBox(BlockPos pos, int range, int top, int color) {
		Gizmos.cuboid(
				new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range,
						pos.getX() + range + 1, top, pos.getZ() + range + 1),
				GizmoStyle.strokeAndFill(color, 1.5f, color & 0x30FFFFFF)
		);
	}
}
