package com.kyhsgeekcode.minecraft_env;

public class ObservationSpace {
    private String image;
    private double x;
    private double y;
    private double z;

    public ObservationSpace(String image, double x, double y, double z) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.z = z;
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
}
