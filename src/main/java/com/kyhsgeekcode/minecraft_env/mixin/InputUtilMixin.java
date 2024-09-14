package com.kyhsgeekcode.minecraft_env.mixin;

import com.kyhsgeekcode.minecraft_env.MouseInfo;
import org.lwjgl.glfw.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.kyhsgeekcode.minecraft_env.Minecraft_envKt.getKeyMap;

@Mixin(net.minecraft.client.util.InputUtil.class)
public class InputUtilMixin {
    @Overwrite
    public static boolean isKeyPressed(long handle, int code) {
        return getKeyMap().getOrDefault(code, false);
    }

    @Overwrite
    public static void setMouseCallbacks(long handle, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI dropCallback) {
        MouseInfo.INSTANCE.setCursorPosCallback(cursorPosCallback);
        MouseInfo.INSTANCE.setMouseButtonCallback(mouseButtonCallback);
    }

    @Overwrite
    public static void setCursorParameters(long handler, int inputModeValue, double x, double y) {
//        MouseInfo.INSTANCE.setCursorPos(x, y);
        MouseInfo.INSTANCE.setCursorShown(inputModeValue == GLFW.GLFW_CURSOR_NORMAL);
    }
}
