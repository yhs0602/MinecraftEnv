package com.kyhsgeekcode.minecraft_env

import com.google.common.io.LittleEndianDataOutputStream
import com.google.protobuf.ByteString
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker
import com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage
import com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment
import com.kyhsgeekcode.minecraft_env.proto.observationSpaceMessage
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.MinecraftServer
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.time.format.DateTimeFormatter

enum class ResetPhase {
    WAIT_PLAYER_DEATH,
    WAIT_PLAYER_RESPAWN,
    WAIT_INIT_ENDS,
    END_RESET,
    IDLE,
}

enum class RunPhase {
    READ_ACTION,
    CLIENT_TICK,
    SERVER_TICK,
    SEND_OBSERVATION,
}

class Minecraft_env : ModInitializer, CommandExecutor {
    private lateinit var initialEnvironment: InitialEnvironment.InitialEnvironmentMessage
    private var soundListener: MinecraftSoundListener? = null
    private var entityListener: EntityRenderListenerImpl? = null // tracks the entities rendered in the last tick
    private var resetPhase: ResetPhase = ResetPhase.END_RESET
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")

    private var nextPhase: RunPhase = RunPhase.SERVER_TICK
    private val runPhaseLock = Object()
    private var skipClientTick = true

    private fun printWithTime(msg: String) {
        println("${formatter.format(java.time.LocalDateTime.now())} $msg")
    }

    override fun onInitialize() {
        val inputStream: InputStream
        val outputStream: OutputStream
        try {
            var port: Int
            val portStr = System.getenv("PORT")
            if (portStr != null) {
                port = portStr.toInt()
            } else {
                port = 8000
            }
            printWithTime("Connecting to $port")
            val serverSocket = ServerSocket(port)
            val socket = serverSocket.accept()
            socket.soTimeout = 30000
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        printWithTime("Hello Fabric world!")
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM)
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300)
        readInitialEnvironment(inputStream, outputStream)
        resetPhase = ResetPhase.WAIT_INIT_ENDS
        val initializer = EnvironmentInitializer(initialEnvironment)
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { client: MinecraftClient ->
            initializer.onClientTick(client)
            if (soundListener == null) soundListener = MinecraftSoundListener(client.soundManager)
            if (entityListener == null) entityListener =
                EntityRenderListenerImpl(client.worldRenderer as AddListenerInterface)
        })
        ClientTickEvents.START_WORLD_TICK.register(ClientTickEvents.StartWorldTick { world: ClientWorld ->
            synchronized(runPhaseLock) {
                printWithTime("Skip client tick set to false")
                skipClientTick = false
                nextPhase = RunPhase.READ_ACTION
                runPhaseLock.notifyAll()
            }
            // read input
            synchronized(runPhaseLock) {
                printWithTime("Waiting for state to be read action")
                while (nextPhase != RunPhase.READ_ACTION) {
                    runPhaseLock.wait()
                }
            }

            printWithTime("Start client world tick1")
            onStartWorldTick(initializer, world, inputStream)
            printWithTime("Start client world tick2")

            synchronized(runPhaseLock) {
                nextPhase = RunPhase.CLIENT_TICK
                runPhaseLock.notifyAll()
            }
        })
        ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { world: ClientWorld ->
            // allow server to start tick
            printWithTime("End client world tick1")
            synchronized(runPhaseLock) {
                nextPhase = RunPhase.SERVER_TICK
                runPhaseLock.notifyAll()
            }
            // wait until server tick ends
            printWithTime("Wait server world tick ends")
            synchronized(runPhaseLock) {
                while (nextPhase != RunPhase.SEND_OBSERVATION) {
                    runPhaseLock.wait()
                }
            }
            // send observation
            when (resetPhase) {
                ResetPhase.WAIT_PLAYER_DEATH -> {

                }

                ResetPhase.WAIT_PLAYER_RESPAWN -> {

                }

                ResetPhase.WAIT_INIT_ENDS -> {

                }

                ResetPhase.END_RESET -> {
                    sendObservation(outputStream, world)
                }

                ResetPhase.IDLE -> {
                    sendObservation(outputStream, world)
                }
            }
            printWithTime("End client world tick2")
            synchronized(runPhaseLock) {
                nextPhase = RunPhase.READ_ACTION
                runPhaseLock.notifyAll()
            }
        })
        ServerTickEvents.START_SERVER_TICK.register(ServerTickEvents.StartTick { server: MinecraftServer ->
            // wait until client tick ends
            printWithTime("Start server tick1")
            printWithTime("Wait client world tick ends")
            synchronized(runPhaseLock) {
                while (nextPhase != RunPhase.SERVER_TICK && !skipClientTick) {
                    runPhaseLock.wait()
                }
            }
            printWithTime("Start server tick2")
        })
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server: MinecraftServer ->
            // allow client to end tick
            printWithTime("End server tick1")
            synchronized(runPhaseLock) {
                nextPhase = RunPhase.SEND_OBSERVATION
                runPhaseLock.notifyAll()
            }
            printWithTime("End server world tick2")
        })
    }

    private fun onStartWorldTick(
        initializer: EnvironmentInitializer,
        world: ClientWorld,
        inputStream: InputStream,
    ) {
        val client = MinecraftClient.getInstance()
        soundListener!!.onTick()
        if (client.isPaused) return
        val player = client.player ?: return
        if (!player.isDead) {
            sendSetScreenNull(client)
        }
        initializer.onWorldTick(world.server, client.inGameHud.chatHud, this)

        when (resetPhase) {
            ResetPhase.WAIT_PLAYER_DEATH -> {
                printWithTime("Waiting for player death")
                if (player.isDead) {
                    player.requestRespawn()
                    resetPhase = ResetPhase.WAIT_PLAYER_RESPAWN
                }
                return
            }

            ResetPhase.WAIT_PLAYER_RESPAWN -> {
                println("Waiting for player respawn")
                if (!player.isDead) {
                    initializer.reset(client.inGameHud.chatHud, this)
                    resetPhase = ResetPhase.WAIT_INIT_ENDS
                }
                return
            }

            ResetPhase.WAIT_INIT_ENDS -> {
                println("Waiting for the initialization ends")
                if (initializer.initWorldFinished) {
                    sendSetScreenNull(client) // clear death screen
                    resetPhase = ResetPhase.END_RESET
                }
                return
            }

            ResetPhase.END_RESET -> {
                printWithTime("Reset end")
            }

            ResetPhase.IDLE -> TODO()
        }
        try {
            val action = readAction(inputStream)
            val command = action.command

            if (command.isNotEmpty()) {
                handleCommand(command, client, world, player)
                return
            }
            if (player.isDead) return else sendSetScreenNull(client)
            val actionArray = action.actionList
            if (applyAction(actionArray, player, client)) return
        } catch (e: SocketTimeoutException) {
            println("Timeout")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun sendSetScreenNull(client: MinecraftClient) {
        client.setScreen(null)
    }


    private fun handleCommand(
        command: String,
        client: MinecraftClient,
        world: ClientWorld,
        player: ClientPlayerEntity
    ): Boolean {
        if (command == "respawn") {
            if (client.currentScreen is DeathScreen && player.isDead) {
                player.requestRespawn()
                sendSetScreenNull(client)
            }
        } else if (command == "fastreset") {
            printWithTime("Fast resetting")
            resetPhase = ResetPhase.WAIT_PLAYER_DEATH
            player.kill() //kill player
//            runCommand(world.server, "/kill @p") // kill player
            runCommand(player, "/tp @e[type=!player] ~ -500 ~") // send to void
        } else {
            runCommand(player, command)
            println("Executed command: $command")
        }
        return true
    }

    private fun applyAction(
        actionArray: MutableList<Int>,
        player: ClientPlayerEntity,
        client: MinecraftClient?
    ): Boolean {
        if (actionArray.isEmpty()) {
            println("actionArray is empty")
            return true
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
            0 -> {
                player.input.pressingForward = false
                player.input.pressingBack = false
            }

            1 -> {
                player.input.pressingForward = true
                player.input.pressingBack = false
                //                    player.travel(Vec3d(0.0, 0.0, 1.0)) // sideway, upward, forward
            }

            2 -> {
                player.input.pressingForward = false
                player.input.pressingBack = true
            }
        }
        when (movementLR) {
            0 -> {
                player.input.pressingRight = false
                player.input.pressingLeft = false
            }

            1 -> {
                player.input.pressingRight = true
                player.input.pressingLeft = false
            }

            2 -> {
                player.input.pressingRight = false
                player.input.pressingLeft = true
                //                    player.travel(Vec3d(-1.0, 0.0, 0.0))
            }
        }
        when (jumpSneakSprint) {
            0 -> {
                //                    println("Sneaking reset")
                player.input.jumping = false
                player.input.sneaking = false
                //                    player.isSprinting = false
            }

            1 -> {
                player.input.jumping = true
                player.input.sneaking = false
                //                    if (player.isOnGround) {
                //                        player.jump()
                //                    }
            }

            2 -> {
                player.input.jumping = false
                player.input.sneaking = true
            }

            3 -> {
                player.input.jumping = false
                player.input.sneaking = false
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
                //                    val targetItem = Item.byRawId(argCraft)
                //                    val id = Registries.ITEM.getId(targetItem)
                //                    val itemStack = net.minecraft.item.ItemStack(targetItem, 1)
                //                    val inventory = player.inventory
                //                    val recipeMatcher = RecipeMatcher()
                //                    inventory.populateRecipeFinder(recipeMatcher)
                //                    player.playerScreenHandler.populateRecipeFinder(recipeMatcher)
                //                    val input = CraftingInventory(player.playerScreenHandler, 2, 2)
                //                    //                        manager.get(id).ifPresent(recipe -> {
                //                    //                            player.playerScreenHandler.matches()
                //                    //                            if (recipeMatcher.match(recipe, null)) {
                //                    //                                recipe.getIngredients();
                //                    //                            }
                //                    //
                //                    //                        });
                //                    inventory.insertStack(itemStack)
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
        return false
    }


    private fun readAction(inputStream: InputStream): ActionSpaceMessage {
        printWithTime("Reading action space")
        // read action from inputStream using protobuf
        val buffer = ByteBuffer.allocate(Integer.BYTES) // 4 bytes
        inputStream.read(buffer.array())
        val len = buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN).int
        val bytes = inputStream.readNBytes(len)
//        println("Read action space bytes $len")
        val actionSpace = ActionSpaceMessage.parseFrom(bytes)
        printWithTime("Read action space")
        return actionSpace
    }

    private fun sendObservation(outputStream: OutputStream, world: World) {
        printWithTime("send Observation")
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (player == null) {
            printWithTime("Player is null")
            return
        }
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

                val observationSpaceMessage = observationSpaceMessage {
                    image = ByteString.copyFrom(byteArray)
                    x = pos.x
                    y = pos.y
                    z = pos.z
                    pitch = player.pitch.toDouble()
                    yaw = player.yaw.toDouble()
                    health = player.health.toDouble()
                    foodLevel = player.hungerManager.foodLevel.toDouble()
                    saturationLevel = player.hungerManager.saturationLevel.toDouble()
                    isDead = player.isDead
                    inventory.addAll((player.inventory.main + player.inventory.armor + player.inventory.offHand).map {
                        it.toMessage()
                    })
                    raycastResult = player.raycast(100.0, 1.0f, false).toMessage(world)
                    soundSubtitles.addAll(
                        soundListener!!.entries.map {
                            it.toMessage()
                        }
                    )
                    statusEffects.addAll(player.statusEffects.map {
                        it.toMessage()
                    })
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
                    entityListener?.run {
                        for (entity in entities) {
                            // notify where entity is, what it is (supervised)
                            visibleEntities.add(entity.toMessage())
                        }
                    }
                }
                writeObservation(observationSpaceMessage, outputStream)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun writeObservation(
        observationSpace: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage,
        outputStream: OutputStream
    ) {
        printWithTime("Writing observation with size ${observationSpace.serializedSize}")
        val dataOutputStream = LittleEndianDataOutputStream(outputStream)
        dataOutputStream.writeInt(observationSpace.serializedSize)
//        println("Wrote observation size ${observationSpace.serializedSize}")
        observationSpace.writeTo(outputStream)
//        println("Wrote observation ${observationSpace.serializedSize}")
        outputStream.flush()
        printWithTime("Flushed")
    }

    private fun readInitialEnvironment(inputStream: InputStream, outputStream: OutputStream) {
        // read client environment settings
        while (true) {
            try {
                printWithTime("Reading initial environment")
                // read a single int from input stream
                val buffer = ByteBuffer.allocate(Integer.BYTES) // 4 bytes
                inputStream.read(buffer.array())
                val len = buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN).int
                printWithTime("$len")
                val bytes = inputStream.readNBytes(len.toInt())
                initialEnvironment = InitialEnvironment.InitialEnvironmentMessage.parseFrom(bytes)
                printWithTime("Read initial environment ${initialEnvironment!!.imageSizeX} ${initialEnvironment!!.imageSizeY}")
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
        printWithTime("End send command: $command")
    }

    companion object {
        val CUSTOM_ITEM = Item(FabricItemSettings().fireproof())
    }
}

