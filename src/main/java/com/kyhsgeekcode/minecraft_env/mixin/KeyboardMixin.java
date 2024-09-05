package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
    private void setKeyboardCallbacks(CallbackInfo ci) {
        ci.cancel();
        // Call onKey, onChar directly to handle inputs, no external keyboard inputs
    }
}




