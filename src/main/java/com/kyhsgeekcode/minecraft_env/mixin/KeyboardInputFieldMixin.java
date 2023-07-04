package com.kyhsgeekcode.minecraft_env.mixin;

import com.kyhsgeekcode.minecraft_env.KeyboardInputWillInterface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.input.KeyboardInput.class)
public class KeyboardInputFieldMixin implements KeyboardInputWillInterface {
    private boolean willPressingForward;
    private boolean willPressingBack;
    private boolean willPressingLeft;
    private boolean willPressingRight;
    private boolean willJumping;
    private boolean willSneaking;
    private boolean willSprinting;

    public boolean isWillPressingForward() {
        return willPressingForward;
    }

    public boolean isWillPressingBack() {
        return willPressingBack;
    }

    public boolean isWillPressingLeft() {
        return willPressingLeft;
    }

    public boolean isWillPressingRight() {
        return willPressingRight;
    }

    public boolean isWillJumping() {
        return willJumping;
    }

    public boolean isWillSneaking() {
        return willSneaking;
    }

    public boolean isWillSprinting() {
        return willSprinting;
    }

    @Override
    public void setWillPressingForward(boolean willPressingForward) {
        this.willPressingForward = willPressingForward;
    }

    @Override
    public void setWillPressingBack(boolean willPressingBack) {
        this.willPressingBack = willPressingBack;
    }

    @Override
    public void setWillPressingLeft(boolean willPressingLeft) {
        this.willPressingLeft = willPressingLeft;
    }

    @Override
    public void setWillPressingRight(boolean willPressingRight) {
        this.willPressingRight = willPressingRight;
    }

    @Override
    public void setWillJumping(boolean willJumping) {
        this.willJumping = willJumping;
    }

    @Override
    public void setWillSneaking(boolean willSneaking) {
        this.willSneaking = willSneaking;
    }

    @Override
    public void setWillSprinting(boolean willSprinting) {
        this.willSprinting = willSprinting;
    }
}
