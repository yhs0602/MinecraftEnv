package com.kyhsgeekcode.minecraft_env;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;


public class Minecraft_env implements ModInitializer {
    public static final Item CUSTOM_ITEM = new Item(new FabricItemSettings().fireproof());

    @Override
    public void onInitialize() {
        InputStreamReader reader;
        OutputStreamWriter writer;
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            var socket = serverSocket.accept();
            InputStream input = socket.getInputStream();
            reader = new InputStreamReader(input);
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello Fabric world!");
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM);
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300);

//        ItemGroups.BUILDING_BLOCKS
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.isPaused())
                return;
            var player = client.player;
            if (player == null)
                return;
            System.out.println("Start tick");
            try {
                char c = (char) reader.read();
                System.out.println("Read: " + c);
                switch (c) {
                    case '0' -> {
                        if (player.isOnGround()) {
                            player.jump();
                        }
                    }
                    case '1' -> player.move(MovementType.PLAYER, new Vec3d(0, 1, 0));
                    case '2' -> player.move(MovementType.PLAYER, new Vec3d(0, -1, 0));
                    case '3' -> player.move(MovementType.PLAYER, new Vec3d(1, 0, 0));
                    default -> {
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            var blockPos = player.getBlockPos();
            //
//            var x = ChunkSectionPos.getSectionCoord(blockPos.getX());
//            var z = ChunkSectionPos.getSectionCoord(blockPos.getZ());

            //
            // check for network input, if any, do something
            if (client.options.forwardKey.isPressed()) {
                if (client.player.isOnGround())
                    client.player.jump();
            }

//            client.player.
//            client.player.move(MovementType.PLAYER, new Vec3d(0, 1, 0));
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            System.out.println("End tick");
            try {
                writer.write("Hello");
                writer.flush();
                System.out.println("Write: Hello");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

