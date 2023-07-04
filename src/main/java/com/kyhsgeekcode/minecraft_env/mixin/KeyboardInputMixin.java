package com.kyhsgeekcode.minecraft_env.mixin;

import com.kyhsgeekcode.minecraft_env.KeyboardInputWillInterface;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public class KeyboardInputMixin {

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V"))
    private void onTick(Input instance, boolean slowDown, float f) {
        instance.pressingForward = ((KeyboardInputWillInterface) instance).isWillPressingForward();
        instance.pressingBack = ((KeyboardInputWillInterface) instance).isWillPressingBack();
        instance.pressingLeft = ((KeyboardInputWillInterface) instance).isWillPressingLeft();
        instance.pressingRight = ((KeyboardInputWillInterface) instance).isWillPressingRight();
        instance.movementForward = getMovementMultiplier(instance.pressingForward, instance.pressingBack);
        instance.movementSideways = getMovementMultiplier(instance.pressingLeft, instance.pressingRight);
        instance.jumping = ((KeyboardInputWillInterface) instance).isWillJumping();
        instance.sneaking = ((KeyboardInputWillInterface) instance).isWillSneaking();
        if (slowDown) {
            instance.movementSideways *= f;
            instance.movementForward *= f;
        }
    }
}
