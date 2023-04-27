package com.kyhsgeekcode.minecraft_env

import com.google.gson.Gson
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.*
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class Minecraft_env : ModInitializer, CommandExecutor {
    private var gson = Gson()
    private var initialEnvironment: InitialEnvironment? = null
    private var soundListener: MinecraftSoundListener? = null
    private var isResetting = false // if true, then pass through i/o and just let ticks go
    private var isRespawning = false // wait until player respawn and then run initialization
    private var beforeReset = false // if true, then reset before next tick

    override fun onInitialize() {
        val reader: InputStreamReader
        val bufferedReader: BufferedReader
        val writer: OutputStreamWriter
        try {
            val serverSocket = ServerSocket(8000)
            val socket = serverSocket.accept()
            socket.soTimeout = 30000
            val input = socket.getInputStream()
            reader = InputStreamReader(input)
            bufferedReader = BufferedReader(reader)
            writer = OutputStreamWriter(socket.getOutputStream())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        println("Hello Fabric world!")
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM)
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300)
        readInitialEnvironment(bufferedReader, writer)
        val initializer = EnvironmentInitializer(initialEnvironment!!)
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { client: MinecraftClient ->
            initializer.onClientTick(client)
            if (soundListener == null) soundListener = MinecraftSoundListener(client.soundManager)
        })
        ClientTickEvents.START_WORLD_TICK.register(ClientTickEvents.StartWorldTick { world: ClientWorld ->
            onStartWorldTick(initializer, bufferedReader, world)
        })
        ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { world: ClientWorld ->
            sendObservation(
                writer,
                world,
                initializer
            )
        })
    }

    private fun onStartWorldTick(
        initializer: EnvironmentInitializer,
        bufferedReader: BufferedReader,
        world: ClientWorld
    ) {
        val client = MinecraftClient.getInstance()
        soundListener!!.onTick()
        if (client.isPaused) return
        val tracker = client.worldGenerationProgressTracker
        if (tracker != null && tracker.progressPercentage < 100) {
            println("World is generating: " + client.worldGenerationProgressTracker!!.progressPercentage + "%")
            return
        }
        val player = client.player ?: return
        if (!player.isDead)
            client.setScreen(null)
        initializer.onWorldTick(client.inGameHud.chatHud, player, this)

        if (isResetting) {
            println("State: resetting...")
            if (player.isDead) {
                if (!isRespawning) {
                    println("State: player is dead, trying to respawn...")
                    player.requestRespawn() // automatically called in setScreen (null)
                    client.setScreen(null) // clear death screen
                    isRespawning = true
                } else {
                    // wait until respawn
                    println("State: waiting for respawn...")
                }
                beforeReset = true // next step is reset
                return
            }
            println("Player is alive, resetting...")
            // after player respawn
            isRespawning = false
            // reset only once
            if (beforeReset) {
                println("Before reset.")
                initializer.reset(client.inGameHud.chatHud, player, this)
                beforeReset = false
            } else { // had reset command, and the player is alive
                if (initializer.initWorldFinished) {
                    println("State: reset finished")
                    isResetting = false
                } else {
                    println("State: waiting for reset...")
                }
            }
            return
        }
        // Disable pause on lost focus
        val options = client.options
        if (options != null) {
            if (options.pauseOnLostFocus) {
                println("Disabled pause on lost focus")
                options.pauseOnLostFocus = false
                client.options.write()
            }
        }
        try {
            //                System.out.println("Waiting for command");
            val b64 = bufferedReader.readLine()
            if (b64 == null) { // end of stream
                println("End of stream")
                exitProcess(0)
            }
            val json = String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8)
            // decode json to object
            val action = gson.fromJson(json, ActionSpace::class.java)
            val command = action.command
            if (command.isNotEmpty()) {
                if (command == "respawn") {
                    if (client.currentScreen is DeathScreen && player.isDead) {
                        player.requestRespawn()
                        client.setScreen(null)
                    }
                } else if (command == "fastreset") {
                    runCommand(player, "/kill @p") // kill player
                    runCommand(player, "/tp @e[type=!player] ~ -500 ~") // send to void
                    isResetting = true // prevent sending the observation
                    isRespawning = false // wait until player respawn and then run initialization
                } else {
                    runCommand(player, command)
                    println("Executed command: $command")
                }
                return
            }
            if (player.isDead) return else client.setScreen(null)
            val actionArray = action.action
            if (actionArray == null) {
                println("actionArray is null")
                return
            }
            val movementFB = actionArray[0]
            val movementLR = actionArray[1]
            val jumpSneakSprint = actionArray[2]
            val deltaPitch = actionArray[3]
            val deltaYaw = actionArray[4]
            val functionalActions =
                actionArray[5] // 0: noop, 1: use, 2: drop, 3: attack, 4: craft, 5: equip, 6: place, 7: destroy
            val argCraft = actionArray[6]
            val argInventory = actionArray[7]
            when (movementFB) {
                1 -> {
                    player.travel(Vec3d(0.0, 0.0, 1.0)) // sideway, upward, forward
                }

                2 -> {
                    player.travel(Vec3d(0.0, 0.0, -1.0))
                }
            }
            when (movementLR) {
                1 -> {
                    player.travel(Vec3d(1.0, 0.0, 0.0))
                }

                2 -> {
                    player.travel(Vec3d(-1.0, 0.0, 0.0))
                }
            }
            when (jumpSneakSprint) {
                0 -> {
                    player.isSneaking = false
                    player.isSprinting = false
                }

                1 -> {
                    if (player.isOnGround) {
                        player.jump()
                    }
                }

                2 -> {
                    player.isSneaking = true
                }

                3 -> {
                    player.isSprinting = true
                }
            }
            val deltaPitchInDeg = (deltaPitch - 12f) / 12f * 180f
            val deltaYawInDeg = (deltaYaw - 12f) / 12f * 180f
            //                System.out.println("Will set pitch to " + player.getPitch() + " + " + deltaPitchInDeg + " = " + (player.getPitch() + deltaPitchInDeg));
            //                System.out.println("Will set yaw to " + player.getYaw() + " + " + deltaYawInDeg + " = " + (player.getYaw() + deltaYawInDeg));
            player.pitch = player.pitch + deltaPitchInDeg
            player.yaw = player.yaw + deltaYawInDeg
            player.pitch = MathHelper.clamp(player.pitch, -90.0f, 90.0f)
            when (functionalActions) {
                1 -> {
                    (client as ClientDoItemUseInvoker).invokeDoItemUse()
                }

                2 -> {
                    // slot = argInventory;
                    player.inventory.selectedSlot = argInventory
                    player.dropSelectedItem(false)
                }

                3 -> {
                    // attack
                    (client as ClientDoAttackInvoker).invokeDoAttack()
                }

                4 -> {
                    // craft
                    // unimplemented
                    // currently gives items with argCraft as raw id
                    // or use string
                    // to get the integer from string, use api
                    val targetItem = Item.byRawId(argCraft)
                    val id = Registries.ITEM.getId(targetItem)
                    val itemStack = net.minecraft.item.ItemStack(targetItem, 1)
                    val inventory = player.inventory
                    val recipeMatcher = RecipeMatcher()
                    inventory.populateRecipeFinder(recipeMatcher)
                    val manager = world.recipeManager
                    player.playerScreenHandler.populateRecipeFinder(recipeMatcher)
                    val input = CraftingInventory(player.playerScreenHandler, 2, 2)
                    //                        manager.get(id).ifPresent(recipe -> {
                    //                            player.playerScreenHandler.matches()
                    //                            if (recipeMatcher.match(recipe, null)) {
                    //                                recipe.getIngredients();
                    //                            }
                    //
                    //                        });
                    inventory.insertStack(itemStack)
                }

                5 -> {
                    // equip
                    player.inventory.selectedSlot = argInventory
                    (client as ClientDoItemUseInvoker).invokeDoItemUse()
                }

                6 -> {
                    // place
                    (client as ClientDoItemUseInvoker).invokeDoItemUse()
                }

                7 -> {
                    // destroy
                    (client as ClientDoAttackInvoker).invokeDoAttack()
                }
            }
            // TODO: overwrite handleInputEvents?
        } catch (e: SocketTimeoutException) {
            println("Timeout")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun sendObservation(writer: OutputStreamWriter, world: World, initializer: EnvironmentInitializer) {
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (player == null) {
            println("Player is null")
            return
        }
        if (isResetting && initializer.initWorldFinished && !player.isDead) { // reset finished
            isResetting = false
            println("Finished resetting")
        }
        if (isResetting) {
            println("Waiting for world reset")
            return
        } else {
//            System.out.println("Is resetting: false, is world init finished:" + initializer.getInitWorldFinished());
        }
        val buffer = client.framebuffer
        try {
            ScreenshotRecorder.takeScreenshot(buffer).use { screenshot ->
                val encoded =
                    encodeImageToBase64Png(screenshot, initialEnvironment!!.imageSizeX, initialEnvironment!!.imageSizeY)
                val pos = player.pos
                val inventory = player.inventory
                val mainInventory = inventory.main.map {
                    ItemStack(it)
                }
                val armorInventory = inventory.armor.map {
                    ItemStack(it)
                }
                val offhandInventory = inventory.offHand.map {
                    ItemStack(it)
                }
                val inventoryArray = mainInventory + armorInventory + offhandInventory
                val hungerManager = player.hungerManager
                val target = player.raycast(100.0, 1.0f, false)
                var hitResult = HitResult(net.minecraft.util.hit.HitResult.Type.MISS, null, null)
                if (target.type == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                    val blockPos = (target as BlockHitResult).blockPos
                    val block = world.getBlockState(blockPos).block
                    val blockInfo = BlockInfo(
                        blockPos.x, blockPos.y, blockPos.z, block.translationKey
                    )
                    hitResult = HitResult(
                        net.minecraft.util.hit.HitResult.Type.BLOCK,
                        blockInfo,
                        null
                    )
                } else if (target.type == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    val entity = (target as EntityHitResult).entity
                    var health = 0.0
                    if (entity is LivingEntity) {
                        health = entity.health.toDouble()
                    }
                    val entityInfo = EntityInfo(
                        entity.entityName,
                        entity.type.translationKey,
                        entity.x,
                        entity.y,
                        entity.z,
                        entity.yaw.toDouble(),
                        entity.pitch.toDouble(),
                        health
                    )
                    hitResult = HitResult(
                        net.minecraft.util.hit.HitResult.Type.ENTITY,
                        null,
                        entityInfo
                    )
                }
                val statusEffects = player.statusEffects
                val statusEffectsConverted = ArrayList<StatusEffect>()
                for (statusEffect in statusEffects) {
                    statusEffectsConverted.add(
                        StatusEffect(
                            statusEffect.translationKey,
                            statusEffect.duration,
                            statusEffect.amplifier
                        )
                    )
                }
                val isDead = if (isResetting) false else player.isDead
                val observationSpace = ObservationSpace(
                    encoded, pos.x, pos.y, pos.z,
                    player.pitch.toDouble(), player.yaw.toDouble(),
                    player.health.toDouble(),
                    hungerManager.foodLevel.toDouble(),
                    hungerManager.saturationLevel.toDouble(),
                    isDead,
                    inventoryArray,
                    hitResult,
                    soundListener!!.entries,
                    statusEffectsConverted
                )
                val json = gson.toJson(observationSpace)
                //            System.out.println("Sending observation");
                writer.write(json)
                writer.flush()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun readInitialEnvironment(bufferedReader: BufferedReader, writer: OutputStreamWriter) {
        // read client environment settings
        while (true) {
            try {
                val b64 = bufferedReader.readLine()
                val json = String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8)
                // decode json to object
                initialEnvironment = gson.fromJson(json, InitialEnvironment::class.java)
                val response = gson.toJson(ObservationSpace())
                println("Sending dummy observation")
                writer.write(response)
                writer.flush()
                break
            } catch (e: SocketTimeoutException) {
                println("Socket timeout")
                // wait and try again
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }

    override fun runCommand(player: ClientPlayerEntity, command: String) {
        var command = command
        println("Running command: $command")
        if (command.startsWith("/")) {
            command = command.substring(1)
        }
        player.networkHandler.sendChatCommand(command)
        println("End send command: $command")
    }

    companion object {
        val CUSTOM_ITEM = Item(FabricItemSettings().fireproof())


    }
}

@Throws(IOException::class)
fun encodeImageToBase64Png(image: NativeImage, targetSizeX: Int, targetSizeY: Int): String {
    val out = ByteArrayOutputStream()
    val base64Out = Base64.getEncoder().wrap(out)
    val data = image.bytes
    val originalImage = ImageIO.read(ByteArrayInputStream(data))
    val resizedImage = BufferedImage(targetSizeX, targetSizeY, originalImage.type)
    val graphics = resizedImage.createGraphics()
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    graphics.drawImage(originalImage, 0, 0, targetSizeX, targetSizeY, null)
    graphics.dispose()
    val baos = ByteArrayOutputStream()
    ImageIO.write(resizedImage, "png", baos)
    base64Out.write(baos.toByteArray())
    base64Out.flush()
    base64Out.close()
    // String size = String.format("%dx%d", image.getWidth(), image.getHeight());
    // size + "|" +
    return out.toString(StandardCharsets.UTF_8)
}