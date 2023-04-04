package com.kyhsgeekcode.minecraft_env;

import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Minecraft_env implements ModInitializer {
    public static final Item CUSTOM_ITEM = new Item(new FabricItemSettings().fireproof());

    @Override
    public void onInitialize() {

//        minecraft.joinWorld();
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

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
                    var screen = client.currentScreen;
                    if (screen instanceof TitleScreen) {
                        for (var child : screen.children()) {
                            System.out.println(child);
                            if (child instanceof ButtonWidget button) {
                                if (button.getMessage().getString().equals("Singleplayer")) {
                                    button.onPress();
                                    return;
                                }
                            }
                        }
                    } else if (screen instanceof SelectWorldScreen) {
                        System.out.println("Select world screen1");
                        WorldListWidget widget = null;
                        ButtonWidget startButton = null;
                        for (var child : screen.children()) {
                            System.out.println(child);
                            if (child instanceof WorldListWidget w) {
                                widget = w;
                            } else if (child instanceof ButtonWidget button) {
                                if (button.getMessage().getString().equals("Play Selected World")) {
                                    startButton = button;
                                }
                            }
                            if (widget != null && startButton != null) {
                                widget.setSelected(widget.children().get(0));
                                startButton.onPress();
                                return;
                            }
                        }

                    }
                }
        );

//        ItemGroups.BUILDING_BLOCKS
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (world.getTime() % 4 != 0)
                return;

            if (client.isPaused())
                return;
            var tracker = client.getWorldGenerationProgressTracker();
            if (tracker != null && tracker.getProgressPercentage() < 100) {
                System.out.println("World is generating: " + client.getWorldGenerationProgressTracker().getProgressPercentage() + "%");
                return;
            }
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
                    case '1' -> {
                        player.setSneaking(true);
                    }
                    case '2' -> {
                        player.setSneaking(false);
                    }
                    case '3' -> player.move(MovementType.PLAYER, new Vec3d(1, 0, 0));
                    case '4' -> player.move(MovementType.PLAYER, new Vec3d(0, 0, 1));
                    case '5' -> player.move(MovementType.PLAYER, new Vec3d(-1, 0, 0));
                    case '6' -> player.move(MovementType.PLAYER, new Vec3d(0, 0, -1));
                    case '7' -> {
                        player.changeLookDirection(0, 1);
                    }
                    case '8' -> {
                        player.changeLookDirection(0, -1);
                    }
                    case '9' -> {
                        player.changeLookDirection(1, 0);
                    }
                    case 'a' -> {
                        player.changeLookDirection(-1, 0);
                    }
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
            var client = MinecraftClient.getInstance();
            if (world.getTime() % 4 != 0)
                return;
            var player = client.player;
            if (player == null) {
                return;
            }
            var buffer = client.getFramebuffer();
            try (var screenshot = ScreenshotRecorder.takeScreenshot(buffer)) {
                var encoded = encodeImageToBase64Png(screenshot);
                var pos = player.getPos();
                var observationSpace = new ObservationSpace(
                        encoded, pos.x, pos.y, pos.z
                );
                Gson gson = new Gson();
                String json = gson.toJson(observationSpace);
                writer.write(json);
                writer.flush();
                System.out.println("Wrote json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String encodeImageToBase64Png(NativeImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream base64Out = Base64.getEncoder().wrap(out);
        byte[] data = image.getBytes();
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(data));
        int newWidth = 890;
        int newHeight = 500;

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        base64Out.write(baos.toByteArray());
        base64Out.flush();
        base64Out.close();
        // String size = String.format("%dx%d", image.getWidth(), image.getHeight());
        // size + "|" +
        return out.toString(StandardCharsets.UTF_8);
    }
}
