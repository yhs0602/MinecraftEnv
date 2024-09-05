package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.kyhsgeekcode.minecraft_env.Minecraft_envKt.getKeyMap;

@Mixin(net.minecraft.client.util.InputUtil.class)
public class InputUtilMixin {
    @Overwrite
    public static boolean isKeyPressed(long handle, int code) {
        return getKeyMap().getOrDefault(code, false);
    }
}
