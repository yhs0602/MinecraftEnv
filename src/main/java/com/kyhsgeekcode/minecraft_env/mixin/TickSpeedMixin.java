package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MinecraftClient.class)
public class TickSpeedMixin {
    @Redirect(
            method = "render(Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I")
    )
    private int beginRenderTick(RenderTickCounter renderTickCounter, long timeMillis) {
        ((RenderTickCounterAccessor) renderTickCounter).setLastFrameDuration(1);// (float)(timeMillis - this.prevTimeMillis) / this.tickTime;
        ((RenderTickCounterAccessor) renderTickCounter).setPrevTimeMillis(timeMillis);
        ((RenderTickCounterAccessor) renderTickCounter).setTickDelta(
                (renderTickCounter).tickDelta + renderTickCounter.lastFrameDuration
        );
        int i = (int) renderTickCounter.tickDelta;
        ((RenderTickCounterAccessor) renderTickCounter).setTickDelta((renderTickCounter.tickDelta - (float) i));
        return i;
    }
}
