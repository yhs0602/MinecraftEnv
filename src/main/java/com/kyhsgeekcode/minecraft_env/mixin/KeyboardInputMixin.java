package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public class KeyboardInputMixin {

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V"))
    private void onTick(Input instance, boolean slowDown, float f) {
        instance.movementForward = getMovementMultiplier(instance.pressingForward, instance.pressingBack);
        instance.movementSideways = getMovementMultiplier(instance.pressingLeft, instance.pressingRight);
        if (slowDown) {
            instance.movementSideways *= f;
            instance.movementForward *= f;
        }
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }
}
