package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class BeaconColorScreen extends Screen {
	private static final String[] COLOR_NAMES = {
			"Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Purple", "Pink"
	};
	private static final int[] COLORS = {
			0xFFFF4D4D, 0xFFFFA64D, 0xFFFFFF4D, 0xFF66FF66,
			0xFF4DFFFF, 0xFF4D80FF, 0xFFB84DFF, 0xFFFF4DB8
	};

	private final Screen parent;
	private final BlockPos beaconPos;

	public BeaconColorScreen(Screen parent, BlockPos beaconPos) {
		super(Component.literal("Beacon Range Color"));
		this.parent = parent;
		this.beaconPos = beaconPos;
	}

	@Override
	protected void init() {
		int buttonWidth = 100;
		int buttonHeight = 20;
		int startX = this.width / 2 - buttonWidth - 4;
		int startY = this.height / 2 - 55;

		for (int i = 0; i < COLOR_NAMES.length; i++) {
			int index = i;
			int x = startX + (i % 2) * (buttonWidth + 8);
			int y = startY + (i / 2) * (buttonHeight + 5);
			this.addRenderableWidget(Button.builder(Component.literal(COLOR_NAMES[i]), button ->
					chooseColor(index)
			).bounds(x, y, buttonWidth, buttonHeight).build());
		}

		this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
				.bounds(this.width / 2 - 50, startY + 4 * (buttonHeight + 5), 100, buttonHeight)
				.build());
	}

	private void chooseColor(int index) {
		ExampleModClient.setBeaconColor(beaconPos, COLORS[index]);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreenAndShow(parent);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractMenuBackground(graphics);
		graphics.centeredText(this.font, this.title.getString(), this.width / 2, this.height / 2 - 85, 0xFFFFFFFF);
		graphics.centeredText(this.font, "Choose a range color", this.width / 2,
				this.height / 2 - 70, 0xFFAAAAAA);
		super.extractRenderState(graphics, mouseX, mouseY, delta);
	}
}
