package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(net.minecraft.client.MinecraftClient.class)
public class TickSpeedMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int beginRenderTick(net.minecraft.client.render.RenderTickCounter renderTickCounter, long timeMillis) {
        renderTickCounter.lastFrameDuration = 50;// (float)(timeMillis - this.prevTimeMillis) / this.tickTime;
        ((RenderTickCounterAccessor) renderTickCounter).setPrevTimeMillis(timeMillis);
        renderTickCounter.tickDelta += renderTickCounter.lastFrameDuration;
        int i = (int) renderTickCounter.tickDelta;
        renderTickCounter.tickDelta -= (float) i;
        return i;
    }

//    @Inject(at = @At("RETURN"), method = "beginRenderTick(L)I")
//    private void init(net.minecraft.client.MinecraftClient client, CallbackInfo info) {
//        this.lastFrameDuration = (float) (timeMillis - this.prevTimeMillis) / this.tickTime;
//        this.prevTimeMillis = timeMillis;
//        this.tickDelta += this.lastFrameDuration;
//        int i = (int) this.tickDelta;
//        this.tickDelta -= (float) i;
//        return i;
//    }

}
