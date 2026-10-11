package com.example.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface BeaconScreenAccessor {
	@Accessor("leftPos")
	int beaconRange$getLeftPos();

	@Accessor("topPos")
	int beaconRange$getTopPos();

	@Accessor("imageWidth")
	int beaconRange$getImageWidth();
}
