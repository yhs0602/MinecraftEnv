package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.render.RenderTickCounter.class)
public interface RenderTickCounterAccessor {
    @Accessor("prevTimeMillis")
    public void setPrevTimeMillis(long prevTimeMillis);
}
