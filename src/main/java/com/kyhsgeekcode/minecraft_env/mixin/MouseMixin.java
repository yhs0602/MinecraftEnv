package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "isCursorLocked", at = @At("HEAD"), cancellable = true)
    private void isCursorLocked(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
