package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
	private static boolean rangesVisible = true;
	private static boolean f8WasDown;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(ExampleModClient::handleVisibilityToggle);
	}

	private static void handleVisibilityToggle(Minecraft client) {
		if (client.getWindow() == null) {
			return;
		}

		boolean f8IsDown = GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS;
		if (f8IsDown && !f8WasDown) {
			rangesVisible = !rangesVisible;
		}
		f8WasDown = f8IsDown;
	}

	public static boolean areRangesVisible() {
		return rangesVisible;
	}
}