package com.kyhsgeekcode.minecraft_env

import com.google.common.io.LittleEndianDataOutputStream
import com.google.gson.Gson
import com.google.protobuf.ByteString
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker
import com.kyhsgeekcode.minecraft_env.proto.*
import com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage
import com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment
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
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
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
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO

class Minecraft_env : ModInitializer, CommandExecutor {
    private var gson = Gson()
    private lateinit var initialEnvironment: InitialEnvironment.InitialEnvironmentMessage
    private var soundListener: MinecraftSoundListener? = null
    private var isResetting = false // if true, then pass through i/o and just let ticks go
    private var isRespawning = false // wait until player respawn and then run initialization
    private var beforeReset = false // if true, then reset before next tick
    private var wasResetting = false // last tick was resetting
    private var onceDied = false

    override fun onInitialize() {
        val inputStream: InputStream
        val outputStream: OutputStream
        try {
            val serverSocket = ServerSocket(8000)
            val socket = serverSocket.accept()
            socket.soTimeout = 30000
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        println("Hello Fabric world!")
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM)
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300)
        readInitialEnvironment(inputStream, outputStream)
        val initializer = EnvironmentInitializer(initialEnvironment)
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { client: MinecraftClient ->
            initializer.onClientTick(client)
            if (soundListener == null) soundListener = MinecraftSoundListener(client.soundManager)
        })
        ClientTickEvents.START_WORLD_TICK.register(ClientTickEvents.StartWorldTick { world: ClientWorld ->
            onStartWorldTick(initializer, inputStream, world)
        })
        ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { world: ClientWorld ->
            sendObservation(
                outputStream,
                world,
                initializer
            )
        })
    }

    private fun onStartWorldTick(
        initializer: EnvironmentInitializer,
        inputStream: InputStream,
        world: ClientWorld
    ) {
        println("start time: " + world.time)
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
            if (!onceDied) {
                if (!player.isDead) {
                    // wait for death
                    println("Waiting for death...")
                    return
                } else {
                    println("Player is dead.")
                    onceDied = true
                }
            }
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
//                    isResetting = false
                    wasResetting = true
                } else {
                    println("State: waiting for reset...")
                }
            }
            return
        }
        println("real start time: " + world.time)
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
            val action = readAction(inputStream)

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
                    onceDied = false
                } else {
                    runCommand(player, command)
                    println("Executed command: $command")
                }
                return
            }
            if (player.isDead) return else client.setScreen(null)
            val actionArray = action.actionList
            if (actionArray.isEmpty()) {
                println("actionArray is empty")
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

//    private fun readAction(bufferedReader: BufferedReader): ActionSpace {
//        //                System.out.println("Waiting for command");
//        val b64 = bufferedReader.readLine()
//        if (b64 == null) { // end of stream
//            println("End of stream")
//            exitProcess(0)
//        }
//        val json = String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8)
//        // decode json to object
//        return gson.fromJson(json, ActionSpace::class.java)
//    }

    private fun readAction(inputStream: InputStream): ActionSpaceMessage {
        println("Reading action space")
        // read action from inputStream using protobuf
        val buffer = ByteBuffer.allocate(Integer.BYTES) // 4 bytes
        inputStream.read(buffer.array())
        val len = buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN).int
        val bytes = inputStream.readNBytes(len)
        println("Read action space bytes $len")
        val actionSpace = ActionSpaceMessage.parseFrom(bytes)
        println("Read action space")
        return actionSpace
    }

    private fun sendObservation(outputStream: OutputStream, world: World, initializer: EnvironmentInitializer) {
        println("send time: " + world.time)
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (player == null) {
            println("Player is null")
            return
        }
        if (isResetting && initializer.initWorldFinished && !player.isDead && wasResetting) { // reset finished
            isResetting = false
            println("Finished resetting")
        }
        if (isResetting) {
            println("Waiting for world reset")
            return
        } else {
//            System.out.println("Is resetting: false, is world init finished:" + initializer.getInitWorldFinished());
        }
        println("real send time: " + world.time)
        val buffer = client.framebuffer
        try {
            ScreenshotRecorder.takeScreenshot(buffer).use { screenshot ->
                val byteArray =
                    encodeImageToBytes(
                        screenshot,
                        initialEnvironment.visibleSizeX,
                        initialEnvironment.visibleSizeY,
                        initialEnvironment.imageSizeX,
                        initialEnvironment.imageSizeY
                    )
                val pos = player.pos
                val playerInventory = player.inventory
                val mainInventory = playerInventory.main.map {
                    ItemStack(it)
                }
                val armorInventory = playerInventory.armor.map {
                    ItemStack(it)
                }
                val offhandInventory = playerInventory.offHand.map {
                    ItemStack(it)
                }
                val inventoryArray = mainInventory + armorInventory + offhandInventory
                val hungerManager = player.hungerManager
                val target = player.raycast(100.0, 1.0f, false)
                var hitResultMessage = hitResult {
                    type = com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.HitResult.Type.MISS
                }
                if (target.type == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                    val blockPos = (target as BlockHitResult).blockPos
                    val block = world.getBlockState(blockPos).block
                    hitResultMessage = hitResult {
                        type = com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.HitResult.Type.BLOCK
                        targetBlock = blockInfo {
                            x = blockPos.x
                            y = blockPos.y
                            z = blockPos.z
                            translationKey = block.translationKey
                        }
                    }
                } else if (target.type == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    val entity = (target as EntityHitResult).entity
                    var entityHealth = 0.0
                    if (entity is LivingEntity) {
                        entityHealth = entity.health.toDouble()
                    }
                    hitResultMessage = hitResult {
                        type = com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.HitResult.Type.ENTITY
                        targetEntity = entityInfo {
                            uniqueName = entity.entityName
                            translationKey = entity.type.translationKey
                            x = entity.x
                            y = entity.y
                            z = entity.z
                            yaw = entity.yaw.toDouble()
                            pitch = entity.pitch.toDouble()
                            health = entityHealth
                        }
                    }
                }
                val playerStatusEffects = player.statusEffects
                val statusEffectsMessage = playerStatusEffects.map {
                    statusEffect {
                        translationKey = it.translationKey
                        duration = it.duration
                        amplifier = it.amplifier
                    }
                }
                val isDeadb = if (wasResetting) {
                    wasResetting = false
                    false
                } else {
                    player.isDead
                }
                // for entitytype in requested entity type stats
                // get stat and add to result (map)


                val observationSpaceMessage = observationSpaceMessage {
                    image = ByteString.copyFrom(byteArray)
                    x = pos.x
                    y = pos.y
                    z = pos.z
                    pitch = player.pitch.toDouble()
                    yaw = player.yaw.toDouble()
                    health = player.health.toDouble()
                    foodLevel = hungerManager.foodLevel.toDouble()
                    saturationLevel = hungerManager.saturationLevel.toDouble()
                    isDead = isDeadb
                    inventory.addAll(inventoryArray.map {
                        itemStack {
                            rawId = it.rawId
                            translationKey = it.translationKey
                            count = it.count
                            durability = it.durability
                            maxDurability = it.maxDurability
                        }
                    })
                    raycastResult = hitResultMessage
                    soundSubtitles.addAll(
                        soundListener!!.entries.map {
                            soundEntry {
                                translateKey = it.translateKey
                                age = it.age
                                x = it.x
                                y = it.y
                                z = it.z
                            }
                        }
                    )
                    statusEffects.addAll(statusEffectsMessage)
                    for (killStatKey in initialEnvironment.killedStatKeysList) {
                        val key = EntityType.get(killStatKey).get()
                        val stat = player.statHandler.getStat(Stats.KILLED.getOrCreateStat(key))
                        killedStatistics[killStatKey] = stat
                    }
                    for (mineStatKey in initialEnvironment.minedStatKeysList) {
                        val key = Registries.BLOCK.get(Identifier.of("minecraft", mineStatKey))
                        val stat = player.statHandler.getStat(Stats.MINED.getOrCreateStat(key))
                        minedStatistics[mineStatKey] = stat
                    }
                    for (miscStatKey in initialEnvironment.miscStatKeysList) {
                        val key = Registries.CUSTOM_STAT.get(Identifier.of("minecraft", miscStatKey))
                        minedStatistics[miscStatKey] = player.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(key))
                    }
                }
                writeObservation(observationSpaceMessage, outputStream)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun writeObservation(
        observationSpace: ObservationSpace,
        writer: OutputStreamWriter
    ) {
        val json = gson.toJson(observationSpace)
        //            System.out.println("Sending observation");
        writer.write(json)
        writer.flush()
    }

    private fun writeObservation(
        observationSpace: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage,
        outputStream: OutputStream
    ) {
        println("Writing observation with size ${observationSpace.serializedSize}")
        val dataOutputStream = LittleEndianDataOutputStream(outputStream)
        dataOutputStream.writeInt(observationSpace.serializedSize)
        println("Wrote observation size ${observationSpace.serializedSize}")
        observationSpace.writeTo(outputStream)
        println("Wrote observation ${observationSpace.serializedSize}")
        outputStream.flush()
        println("Flushed")
    }

    private fun readInitialEnvironment(inputStream: InputStream, outputStream: OutputStream) {
        // read client environment settings
        while (true) {
            try {
                println("Reading initial environment")
                // read a single int from input stream
                val buffer = ByteBuffer.allocate(Integer.BYTES) // 4 bytes
                inputStream.read(buffer.array())
                val len = buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN).int
                println("$len")
                val bytes = inputStream.readNBytes(len.toInt())
                initialEnvironment = InitialEnvironment.InitialEnvironmentMessage.parseFrom(bytes)
                println("Read initial environment")
                writeObservation(
                    observationSpaceMessage { },
                    outputStream
                )
                println("Read initial environment ${initialEnvironment!!.imageSizeX} ${initialEnvironment!!.imageSizeY}")
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

@Throws(IOException::class)
fun encodeImageToBytes(
    image: NativeImage,
    originalSizeX: Int,
    originalSizeY: Int,
    targetSizeX: Int,
    targetSizeY: Int
): ByteArray {
    if (originalSizeX == targetSizeX && originalSizeY == targetSizeY)
        return image.bytes
    val data = image.bytes
    val originalImage = ImageIO.read(ByteArrayInputStream(data))
    val resizedImage = BufferedImage(targetSizeX, targetSizeY, originalImage.type)
    val graphics = resizedImage.createGraphics()
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    graphics.drawImage(originalImage, 0, 0, targetSizeX, targetSizeY, null)
    graphics.dispose()
    val baos = ByteArrayOutputStream()
    ImageIO.write(resizedImage, "png", baos)
    return baos.toByteArray()
}