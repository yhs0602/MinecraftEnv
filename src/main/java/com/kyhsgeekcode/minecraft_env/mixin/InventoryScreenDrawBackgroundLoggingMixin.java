package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.client.gui.screen.ingame.InventoryScreen.class)
public class InventoryScreenDrawBackgroundLoggingMixin {
    @Inject(at = @At("TAIL"), method = "drawBackground", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci, int i, int j) {
        System.out.println("drawBackground: " + context + ", " + delta + ", " + mouseX + ", " + mouseY + "," + i + ", " + j);
    }
}
