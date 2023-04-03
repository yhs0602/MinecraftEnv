package com.kyhsgeekcode.minecraft_env.client;

import net.fabricmc.api.ClientModInitializer;

public class Minecraft_envClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("Hello Fabric world! client");
    }
}
