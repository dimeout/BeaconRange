package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.gizmos.Gizmos.TemporaryCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(
			method = "collectPerFrameRenderThreadGizmos",
			at = @At("RETURN")
	)
	private void beaconRange$renderRanges(CallbackInfoReturnable<TemporaryCollection> cir) {
		ExampleModClient.renderRanges();
	}
}
