package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(net.minecraft.client.MinecraftClient.class)
public class TickSpeedMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int beginRenderTick(net.minecraft.client.render.RenderTickCounter renderTickCounter, long timeMillis) {
        renderTickCounter.lastFrameDuration = 1;// (float)(timeMillis - this.prevTimeMillis) / this.tickTime;
        ((RenderTickCounterAccessor) renderTickCounter).setPrevTimeMillis(timeMillis);
        renderTickCounter.tickDelta += renderTickCounter.lastFrameDuration;
        int i = (int) renderTickCounter.tickDelta;
        renderTickCounter.tickDelta -= (float) i;
        return i;
    }
}
