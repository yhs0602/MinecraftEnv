package com.kyhsgeekcode.minecraft_env;

public class ObservationSpace {
    private String image;
    private double x;
    private double y;
    private double z;

    private boolean isDead;

    public ObservationSpace(String image, double x, double y, double z, boolean isDead) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isDead = isDead;
    }

    public String getImage() {
        return image;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isDead() {
        return isDead;
    }
}
