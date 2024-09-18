package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface ClientRenderTickCounterAccessor {
    @Accessor("renderTickCounter")
    RenderTickCounter getRenderTickCounter();
}
