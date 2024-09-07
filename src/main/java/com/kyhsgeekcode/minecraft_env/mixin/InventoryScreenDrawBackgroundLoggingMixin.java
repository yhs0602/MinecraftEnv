package com.kyhsgeekcode.minecraft_env.mixin;

import com.kyhsgeekcode.minecraft_env.MouseInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.client.gui.screen.ingame.InventoryScreen.class)
public class InventoryScreenDrawBackgroundLoggingMixin {
    @Shadow
    private float mouseX;
    @Shadow
    private float mouseY;

    @Inject(at = @At("TAIL"), method = "drawBackground", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci, int i, int j) {
//        System.out.println(
//                "drawBackground: " + this.mouseX + "," + this.mouseY + ", " + delta + ", " + mouseX + ", " + mouseY + "," + i + ", " + j
//        );
//        MinecraftClient client = MinecraftClient.getInstance();
//        double mouseInfomouseX = MouseInfo.INSTANCE.getMouseX();
//        double mouseInfomouseY = MouseInfo.INSTANCE.getMouseY();
//        int convertedX = (int) (mouseInfomouseX * client.getWindow().getScaledWidth() / client.getWindow().getWidth());
//        int convertedY = (int) (mouseInfomouseY * client.getWindow().getScaledHeight() / client.getWindow().getHeight());
//        System.out.println(
//                "MouseInfo:" + mouseInfomouseX + "," + mouseInfomouseY + ",converted:" + convertedX + "," + convertedY
//        );
    }
}
