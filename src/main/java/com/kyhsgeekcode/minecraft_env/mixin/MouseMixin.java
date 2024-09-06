package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Mouse.class)
abstract public class MouseMixin {
    @Inject(method = "isCursorLocked", at = @At("HEAD"), cancellable = true)
    private void isCursorLocked(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Accessor("x")
    abstract void setX(double x);

    @Accessor("y")
    abstract void setY(double y);
}
