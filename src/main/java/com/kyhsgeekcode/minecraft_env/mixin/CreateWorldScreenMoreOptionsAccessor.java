package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.screen.world.CreateWorldScreen.class)
public interface CreateWorldScreenMoreOptionsAccessor {
    @Accessor("moreOptionsOpen")
    boolean getMoreOptionsOpen();
}
