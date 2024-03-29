package com.kyhsgeekcode.minecraft_env.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(com.mojang.blaze3d.platform.GlStateManager.class)
public class GLBindFrameBufferHookMixin {
    public static int customframebuffer = 0;
    @Inject(method = "_glBindFramebuffer", at = @At(value = "HEAD"), cancellable = true)
    private static void _glBindFramebuffer(int target, int framebuffer, CallbackInfo ci) {
        if (framebuffer == 0) {
            RenderSystem.assertOnRenderThreadOrInit();
            GL30.glBindFramebuffer(target, customframebuffer);
            ci.cancel();
        }
    }
}


