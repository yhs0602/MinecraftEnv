package com.kyhsgeekcode.minecraft_env;

import com.google.gson.Gson;
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker;
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;


public class Minecraft_env implements ModInitializer, CommandExecutor {
    public static final Item CUSTOM_ITEM = new Item(new FabricItemSettings().fireproof());
    Gson gson = new Gson();
    InitialEnvironment initialEnvironment;

    MinecraftSoundListener soundListener;
    boolean isResetting = false;

    @Override
    public void onInitialize() {
        InputStreamReader reader;
        BufferedReader bufferedReader;
        OutputStreamWriter writer;
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            var socket = serverSocket.accept();
            socket.setSoTimeout(30000);
            InputStream input = socket.getInputStream();
            reader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(reader);
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello Fabric world!");
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM);
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300);

        readInitialEnvironment(bufferedReader, writer);
        EnvironmentInitializer initializer = new EnvironmentInitializer(initialEnvironment);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            initializer.onClientTick(client);
            if (soundListener == null)
                soundListener = new MinecraftSoundListener(client.getSoundManager());
        });
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            soundListener.onTick();
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
            initializer.onWorldTick(client.inGameHud.getChatHud(), player, this);
            if (!initializer.getInitWorldFinished() || isResetting) {
                System.out.println("Waiting for world init ticks: " + isResetting);
                isResetting = true;
                return;
            }
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
//                System.out.println("Waiting for command");
                String b64 = bufferedReader.readLine();
                if (b64 == null) { // end of stream
                    System.out.println("End of stream");
                    System.exit(0);
                }
                String json = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
                // decode json to object
                var action = gson.fromJson(json, ActionSpace.class);

                var command = action.getCommand();
                if (command != null && !command.isEmpty()) {
                    if (command.equals("respawn")) {
                        if (client.currentScreen instanceof DeathScreen && player.isDead()) {
                            player.requestRespawn();
                            client.setScreen(null);
                        }
                    } else if (command.equals("fastreset")) {
                        player.kill(); // kill the player, will show death screen after some ticks
                        runCommand(player, "/tp @e[type=!player] ~ -500 ~"); // send to void
                        System.out.println("Player is dead, respawn");
                        player.requestRespawn();// automatically called in setScreen (null)
                        if (!player.isDead()) {
                            client.setScreen(null); // clear death screen
                        }
                        initializer.reset(client.inGameHud.getChatHud(), player, this);
                        isResetting = true; // prevent sending the observation
                    } else {
                        runCommand(player, command);
                        System.out.println("Executed command: " + command);
                    }
                    return;
                }
                if (player.isDead())
                    return;
                else
                    client.setScreen(null);

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
                var deltaPitchInDeg = (deltaPitch - 12f) / 12f * 180f;
                var deltaYawInDeg = (deltaYaw - 12f) / 12f * 180f;
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
                        // currently gives items with argCraft as raw id
                        // or use string
                        // to get the integer from string, use api
                        Item targetItem = Item.byRawId(argCraft);
                        Identifier id = Registries.ITEM.getId(targetItem);
                        ItemStack itemStack = new ItemStack(targetItem, 1);
                        PlayerInventory inventory = player.getInventory();
                        RecipeMatcher recipeMatcher = new RecipeMatcher();
                        inventory.populateRecipeFinder(recipeMatcher);
                        RecipeManager manager = world.getRecipeManager();
                        player.playerScreenHandler.populateRecipeFinder(recipeMatcher);
                        CraftingInventory input = new CraftingInventory(player.playerScreenHandler, 2, 2);
//                        manager.get(id).ifPresent(recipe -> {
//                            player.playerScreenHandler.matches()
//                            if (recipeMatcher.match(recipe, null)) {
//                                recipe.getIngredients();
//                            }
//
//                        });
                        inventory.insertStack(itemStack);
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
            sendObservation(writer, world, initializer);
        });
    }

    private void sendObservation(OutputStreamWriter writer, World world, EnvironmentInitializer initializer) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null) {
            System.out.println("Player is null");
            return;
        }
        if (isResetting && initializer.getInitWorldFinished()) { // reset finished
            isResetting = false;
            System.out.println("Finished resetting");
        }
        if (isResetting) {
            System.out.println("Waiting for world reset");
            return;
        } else {
//            System.out.println("Is resetting: false, is world init finished:" + initializer.getInitWorldFinished());
        }
        var buffer = client.getFramebuffer();
        try (var screenshot = ScreenshotRecorder.takeScreenshot(buffer)) {
            var encoded = encodeImageToBase64Png(screenshot, initialEnvironment.getImageSizeX(), initialEnvironment.getImageSizeY());
            var pos = player.getPos();
            var inventory = player.getInventory();
            var mainInventory = inventory.main;
            var armorInventory = inventory.armor;
            var offhandInventory = inventory.offHand;
            var inventoryArray = new com.kyhsgeekcode.minecraft_env.ItemStack[mainInventory.size() + armorInventory.size() + offhandInventory.size()];
            for (int i = 0; i < mainInventory.size(); i++) {
                ItemStack itemStack = mainInventory.get(i);
                inventoryArray[i] = new com.kyhsgeekcode.minecraft_env.ItemStack(itemStack);
            }
            for (int i = 0; i < armorInventory.size(); i++) {
                inventoryArray[i + mainInventory.size()] = new com.kyhsgeekcode.minecraft_env.ItemStack(armorInventory.get(i));
            }
            for (int i = 0; i < offhandInventory.size(); i++) {
                inventoryArray[i + mainInventory.size() + armorInventory.size()] = new com.kyhsgeekcode.minecraft_env.ItemStack(offhandInventory.get(i));
            }
            var hungerManager = player.getHungerManager();
            var target = player.raycast(100, 1.0f, false);
            com.kyhsgeekcode.minecraft_env.HitResult hitResult = new com.kyhsgeekcode.minecraft_env.HitResult(net.minecraft.util.hit.HitResult.Type.MISS, null, null);
            if (target.getType() == HitResult.Type.BLOCK) {
                var blockPos = ((BlockHitResult) target).getBlockPos();
                var block = world.getBlockState(blockPos).getBlock();
                BlockInfo blockInfo = new BlockInfo(
                        blockPos.getX(), blockPos.getY(), blockPos.getZ(), block.getTranslationKey()
                );
                hitResult = new com.kyhsgeekcode.minecraft_env.HitResult(
                        HitResult.Type.BLOCK,
                        blockInfo,
                        null
                );
            } else if (target.getType() == HitResult.Type.ENTITY) {
                var entity = ((EntityHitResult) target).getEntity();
                double health = 0;
                if (entity instanceof LivingEntity) {
                    health = ((LivingEntity) entity).getHealth();
                }
                EntityInfo entityInfo = new EntityInfo(
                        entity.getEntityName(),
                        entity.getType().getTranslationKey(),
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        entity.getYaw(),
                        entity.getPitch(),
                        health
                );
                hitResult = new com.kyhsgeekcode.minecraft_env.HitResult(
                        HitResult.Type.ENTITY,
                        null,
                        entityInfo
                );
            }

            var statusEffects = player.getStatusEffects();
            var statusEffectsConverted = new ArrayList<StatusEffect>();
            for (var statusEffect : statusEffects) {
                statusEffectsConverted.add(
                        new StatusEffect(
                                statusEffect.getTranslationKey(),
                                statusEffect.getDuration(),
                                statusEffect.getAmplifier()
                        )
                );
            }

            boolean isDead = player.isDead();

            var observationSpace = new ObservationSpace(
                    encoded, pos.x, pos.y, pos.z,
                    player.getPitch(), player.getYaw(),
                    player.getHealth(),
                    hungerManager.getFoodLevel(),
                    hungerManager.getSaturationLevel(),
                    isDead,
                    Arrays.stream(inventoryArray).toList(),
                    hitResult,
                    soundListener.getEntries(),
                    statusEffectsConverted
            );
            String json = gson.toJson(observationSpace);
//            System.out.println("Sending observation");
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readInitialEnvironment(BufferedReader bufferedReader, OutputStreamWriter writer) {
        // read client environment settings
        while (true) {
            try {
                String b64 = bufferedReader.readLine();
                String json = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
                // decode json to object
                initialEnvironment = gson.fromJson(json, InitialEnvironment.class);
                String response = gson.toJson(new ObservationSpace());
                System.out.println("Sending dummy observation");
                writer.write(response);
                writer.flush();
                break;
            } catch (SocketTimeoutException e) {
                System.out.println("Socket timeout");
                // wait and try again
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static String encodeImageToBase64Png(NativeImage image, int targetSizeX, int targetSizeY) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream base64Out = Base64.getEncoder().wrap(out);
        byte[] data = image.getBytes();
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(data));

        BufferedImage resizedImage = new BufferedImage(targetSizeX, targetSizeY, originalImage.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, targetSizeX, targetSizeY, null);
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

    public void runCommand(ClientPlayerEntity player, String command) {
        System.out.println("Running command: " + command);
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        player.networkHandler.sendChatCommand(command);
        System.out.println("End send command: " + command);
    }
}
