package com.example.client.mixin;

import com.example.client.BeaconColorScreen;
import com.example.client.ExampleModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreen.class)
public class BeaconScreenMixin {
	@Unique
	private BlockPos beaconRange$target;

	@Inject(method = "init", at = @At("TAIL"))
	private void beaconRange$addRangeButtons(CallbackInfo callbackInfo) {
		Minecraft client = Minecraft.getInstance();
		if (client.hitResult instanceof BlockHitResult hit
				&& hit.getType() == HitResult.Type.BLOCK
				&& client.level != null
				&& client.level.getBlockEntity(hit.getBlockPos()) != null) {
			beaconRange$target = hit.getBlockPos();
		}

		if (beaconRange$target == null) {
			return;
		}

		BeaconScreen screen = (BeaconScreen) (Object) this;
		int left = ((BeaconScreenAccessor) screen).beaconRange$getLeftPos();
		int top = ((BeaconScreenAccessor) screen).beaconRange$getTopPos();
		int imageWidth = ((BeaconScreenAccessor) screen).beaconRange$getImageWidth();
		Button rangeButton = Button.builder(
				Component.literal(beaconRange$getRangeLabel()),
				button -> {
					boolean visible = ExampleModClient.toggleBeaconRange(beaconRange$target);
					button.setMessage(Component.literal(visible ? "Range: ON" : "Range: OFF"));
				}
		).bounds(left + imageWidth - 110 - 4 - 110, top - 25, 110, 20).build();
		((ScreenAccessor) screen).beaconRange$addRenderableWidget(rangeButton);
		((ScreenAccessor) screen).beaconRange$addRenderableWidget(Button.builder(
				Component.literal("Range Color"),
				button -> client.setScreenAndShow(new BeaconColorScreen(screen, beaconRange$target))
		).bounds(left + imageWidth - 110, top - 25, 110, 20).build());
	}

	@Unique
	private String beaconRange$getRangeLabel() {
		return ExampleModClient.isBeaconRangeVisible(beaconRange$target) ? "Range: ON" : "Range: OFF";
	}
}
