package com.kyhsgeekcode.minecraft_env;

public interface KeyboardInputWillInterface {
    void setWillPressingForward(boolean willPressingForward);

    void setWillPressingBack(boolean willPressingBack);

    void setWillPressingLeft(boolean willPressingLeft);

    void setWillPressingRight(boolean willPressingRight);

    void setWillJumping(boolean willJumping);

    void setWillSneaking(boolean willSneaking);

    void setWillSprinting(boolean willSprinting);

    boolean isWillPressingForward();

    boolean isWillPressingBack();

    boolean isWillPressingLeft();


    boolean isWillPressingRight();

    boolean isWillJumping();

    boolean isWillSneaking();

    boolean isWillSprinting();
}
