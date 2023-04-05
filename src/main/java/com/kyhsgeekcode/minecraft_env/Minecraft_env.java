package com.kyhsgeekcode.minecraft_env;

import com.google.gson.Gson;
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker;
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Minecraft_env implements ModInitializer {
    public static final Item CUSTOM_ITEM = new Item(new FabricItemSettings().fireproof());
    Gson gson = new Gson();
    InitialEnvironment initialEnvironment;

    @Override
    public void onInitialize() {
        InputStreamReader reader;
        BufferedReader bufferedReader;
        OutputStreamWriter writer;
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            var socket = serverSocket.accept();
            socket.setSoTimeout(10000);
            InputStream input = socket.getInputStream();
            reader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(reader);
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello Fabric world!");
//        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM);
//        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300);

        // read client environment settings
        try {
            String b64 = bufferedReader.readLine();
            String json = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
            // decode json to object
            Gson gson = new Gson();
            initialEnvironment = gson.fromJson(json, InitialEnvironment.class);
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timeout");
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                        ButtonWidget deleteButton = null;
                        ButtonWidget createButton = null;
                        for (var child : screen.children()) {
                            System.out.println(child);
                            if (child instanceof WorldListWidget w) {
                                widget = w;
                            } else if (child instanceof ButtonWidget button) {
                                if (button.getMessage().getString().equals("Delete Selected World")) {
                                    deleteButton = button;
                                } else if (button.getMessage().getString().equals("Create New World")) {
                                    createButton = button;
                                }
                            }
                        }
                        if (widget != null && deleteButton != null) {
                            widget.setSelected(widget.children().get(0));
                            deleteButton.onPress();
                            return;
                        }
                        if (createButton != null) {
                            createButton.onPress();
                        }
                    } else if (screen instanceof CreateWorldScreen) {
                        System.out.println("Create world screen");
                        ButtonWidget createButton = null;
                        boolean realCheatAllowed = false;
                        boolean cheatRequested = true;
                        int indexOfWorldSettingTab = -1;
                        CyclingButtonWidget cheatButton = null;
                        TabNavigationWidget settingTabWidget = null;
                        for (var child : screen.children()) {
                            // search for tab navigation widget, to find index of world settings tab
                            if (indexOfWorldSettingTab == -1 && child instanceof TabNavigationWidget tabNavigationWidget) {
                                settingTabWidget = tabNavigationWidget;
                                for (int i = 0; i < tabNavigationWidget.children().size(); i++) {
                                    var tabChild = tabNavigationWidget.children().get(i);
                                    if (tabChild instanceof TabButtonWidget tabButtonWidget) {
                                        if (tabButtonWidget.getMessage().getString().equals("World")) {
                                            indexOfWorldSettingTab = i;
                                        }
                                    }
                                }
                            }
                            // search for create button
                            if (createButton == null && child instanceof ButtonWidget button) {
                                if (button.getMessage().getString().equals("Create New World")) {
                                    createButton = button;
                                }
                            }
                            // search for cheat button
                            if (cheatButton == null && child instanceof CyclingButtonWidget cyclingButtonWidget) {
                                if (cyclingButtonWidget.getMessage().getString().startsWith("Allow Cheats")) {
                                    cheatButton = cyclingButtonWidget;
                                }
                            }
                        }
                        // Set allow cheats to requested
                        if (cheatButton != null) {
                            System.out.println("Cheat button not found");
                            var testString = cheatRequested ? "ON" : "OFF";
                            while (!cheatButton.getMessage().getString().endsWith(testString)) {
                                cheatButton.onPress();
                            }
                        }
//                        realCheatAllowed = cheatRequested;
                        // Select world settings tab
                        settingTabWidget.selectTab(indexOfWorldSettingTab, false);
                        // Search for seed input
                        for (var child : screen.children()) {
                            System.out.println(child);
                            if (child instanceof TextFieldWidget textFieldWidget) {
                                System.out.println("Found text field");
                                textFieldWidget.setText("123456789");
                            }
                        }
                        if (createButton != null) {
                            createButton.onPress();
                        }
                    }
                }
        );

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
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
            // Disable pause on lost focus
            var options = client.options;
            if (options != null) {
                if (options.pauseOnLostFocus) {
                    System.out.println("Disabled pause on lost focus");
                    options.pauseOnLostFocus = false;
                    client.options.write();
                }
            }

            try {
                System.out.println("Waiting for command");
                String b64 = bufferedReader.readLine();
                String json = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
                // decode json to object
                Gson gson = new Gson();
                var action = gson.fromJson(json, ActionSpace.class);

                var command = action.getCommand();
                if (command != null && !command.isEmpty()) {
                    runCommand(player, command);
                    System.out.println("Executed command: " + command);
                    return;
                }
                var actionArray = action.getAction();

                if (actionArray == null) {
                    System.out.println("actionArray is null");
                    return;
                }

                var movementFB = actionArray[0];
                var movementLR = actionArray[1];
                var jumpSneakSprint = actionArray[2];
                var deltaPitch = actionArray[3];
                var deltaYaw = actionArray[4];
                var functionalActions = actionArray[5]; // 0: noop, 1: use, 2: drop, 3: attack, 4: craft, 5: equip, 6: place, 7: destroy
                var argCraft = actionArray[6];
                var argInventory = actionArray[7];

                switch (movementFB) {
                    case 1 -> {
                        player.travel(new Vec3d(0, 0, 1)); // sideway, upward, forward
                    }
                    case 2 -> {
                        player.travel(new Vec3d(0, 0, -1));
                    }
                }
                switch (movementLR) {
                    case 1 -> {
                        player.travel(new Vec3d(1, 0, 0));
                    }
                    case 2 -> {
                        player.travel(new Vec3d(-1, 0, 0));
                    }
                }
                switch (jumpSneakSprint) {
                    case 0 -> {
                        player.setSneaking(false);
                        player.setSprinting(false);
                    }
                    case 1 -> {
                        if (player.isOnGround()) {
                            player.jump();
                        }
                    }
                    case 2 -> {
                        player.setSneaking(true);
                    }
                    case 3 -> {
                        player.setSprinting(true);
                    }

                }
                var deltaPitchInDeg = (deltaPitch - 12) / 12 * 180;
                var deltaYawInDeg = (deltaYaw - 12) / 12 * 180;
//                System.out.println("Will set pitch to " + player.getPitch() + " + " + deltaPitchInDeg + " = " + (player.getPitch() + deltaPitchInDeg));
//                System.out.println("Will set yaw to " + player.getYaw() + " + " + deltaYawInDeg + " = " + (player.getYaw() + deltaYawInDeg));
                player.setPitch(player.getPitch() + deltaPitchInDeg);
                player.setYaw(player.getYaw() + deltaYawInDeg);
                player.setPitch(MathHelper.clamp(player.getPitch(), -90.0f, 90.0f));

                switch (functionalActions) {
                    case 1 -> {
                        ((ClientDoItemUseInvoker) client).invokeDoItemUse();
                    }
                    case 2 -> {
                        // slot = argInventory;
                        player.getInventory().selectedSlot = argInventory;
                        player.dropSelectedItem(false);
                    }
                    case 3 -> {
                        // attack
                        ((ClientDoAttackInvoker) client).invokeDoAttack();
                    }
                    case 4 -> {
                        // craft
                        // unimplemented
                    }
                    case 5 -> {
                        // equip
                        player.getInventory().selectedSlot = argInventory;
                        ((ClientDoItemUseInvoker) client).invokeDoItemUse();
                    }
                    case 6 -> {
                        // place
                        ((ClientDoItemUseInvoker) client).invokeDoItemUse();
                    }
                    case 7 -> {
                        // destroy
                        ((ClientDoAttackInvoker) client).invokeDoAttack();
                    }
                }
                // TODO: overwrite handleInputEvents?
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            var client = MinecraftClient.getInstance();
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
                System.out.println("Sending observation");
                writer.write(json);
                writer.flush();
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

    private void runCommand(ClientPlayerEntity player, String command) {
        System.out.println("Running command: " + command);
        player.networkHandler.sendChatCommand(command);
        System.out.println("End send command: " + command);
    }
}
