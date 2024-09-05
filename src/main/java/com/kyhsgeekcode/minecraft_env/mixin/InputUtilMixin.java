package com.kyhsgeekcode.minecraft_env.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(net.minecraft.client.util.InputUtil.class)
public class InputUtilMixin {
    @Unique
    private static final HashMap<Integer, Boolean> keyMap = new HashMap<>();

    @Overwrite
    public static boolean isKeyPressed(long handle, int code) {
        return keyMap.getOrDefault(code, false);
    }

    @Unique
    public void onKeyPressed(int code) {
        keyMap.put(code, true);
    }

    @Unique
    public void onKeyReleased(int code) {
        keyMap.put(code, false);
    }
}
