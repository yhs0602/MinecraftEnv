package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoAttackInvoker
import com.kyhsgeekcode.minecraft_env.mixin.ClientDoItemUseInvoker
import com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment
import com.kyhsgeekcode.minecraft_env.proto.entitiesWithinDistance
import com.kyhsgeekcode.minecraft_env.proto.observationSpaceMessage
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.BackgroundRenderer
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.MinecraftServer
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess


enum class ResetPhase {
    WAIT_PLAYER_DEATH,
    WAIT_PLAYER_RESPAWN,
    WAIT_INIT_ENDS,
    END_RESET,
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
    private var deathMessageCollector: GetMessagesInterface? = null

    private var nextPhase: RunPhase = RunPhase.SERVER_TICK
    private val runPhaseLock = Object()
    private var skipClientTick = true
//    private var serverPlayerEntity: ServerPlayerEntity? = null

    private val variableCommandsAfterReset = mutableListOf<String>()


    override fun onInitialize() {
        Registry.register(Registries.ITEM, "minecraft_env:custom_item", CUSTOM_ITEM)
        FuelRegistry.INSTANCE.add(CUSTOM_ITEM, 300)
        val inputStream: InputStream
        val outputStream: OutputStream
        val socket: SocketChannel
        val messageIO: MessageIO
        try {
            val portStr = System.getenv("PORT")
            val port = portStr?.toInt() ?: 8000
            val socket_file_path = Path.of("/tmp/minecraftrl_${port}.sock")
            socket_file_path.toFile().deleteOnExit()
            printWithTime("Connecting to $port")
            Files.deleteIfExists(socket_file_path)
            val serverSocket =
                ServerSocketChannel.open(StandardProtocolFamily.UNIX).bind(UnixDomainSocketAddress.of(socket_file_path))
            socket = serverSocket.accept()
            messageIO = DomainSocketMessageIO(socket)
//            val serverSocket = ServerSocket(port)
//            val socket = serverSocket.accept()
//            socket.soTimeout = 30000
//            inputStream = socket.getInputStream()
//            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        printWithTime("Hello Fabric world!")
        initialEnvironment = messageIO.readInitialEnvironment()
//        readInitialEnvironment(inputStream, outputStream)
        resetPhase = ResetPhase.WAIT_INIT_ENDS
        val initializer = EnvironmentInitializer(initialEnvironment)
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { client: MinecraftClient ->
            initializer.onClientTick(client)
            if (soundListener == null) soundListener = MinecraftSoundListener(client.soundManager)
            if (entityListener == null) entityListener =
                EntityRenderListenerImpl(client.worldRenderer as AddListenerInterface)
            if (deathMessageCollector == null) deathMessageCollector =
                client.networkHandler as GetMessagesInterface?
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

            onStartWorldTick(initializer, world, messageIO)

            synchronized(runPhaseLock) {
                nextPhase = RunPhase.CLIENT_TICK
                runPhaseLock.notifyAll()
            }
        })
        ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { world: ClientWorld ->
            // allow server to start tick
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
                    sendObservation(messageIO, world)
                }
            }
            synchronized(runPhaseLock) {
                nextPhase = RunPhase.READ_ACTION
                runPhaseLock.notifyAll()
            }
        })
        ServerTickEvents.START_SERVER_TICK.register(ServerTickEvents.StartTick { server: MinecraftServer ->
            // wait until client tick ends
            printWithTime("Wait client world tick ends")
            synchronized(runPhaseLock) {
                while (nextPhase != RunPhase.SERVER_TICK && !skipClientTick) {
                    runPhaseLock.wait()
                }
            }
        })
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server: MinecraftServer ->
            // allow client to end tick
            synchronized(runPhaseLock) {
//                serverPlayerEntity = server.playerManager.playerList.first()
                nextPhase = RunPhase.SEND_OBSERVATION
                runPhaseLock.notifyAll()
            }
        })
    }

    private fun onStartWorldTick(
        initializer: EnvironmentInitializer,
        world: ClientWorld,
        messageIO: MessageIO
    ) {
        val client = MinecraftClient.getInstance()
        soundListener!!.onTick()
        if (client.isPaused) return
        val player = client.player ?: return
        if (!player.isDead) {
            sendSetScreenNull(client)
        }
        initializer.onWorldTick(world.server, client.inGameHud.chatHud, this, emptyList())

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
                    initializer.reset(client.inGameHud.chatHud, this, variableCommandsAfterReset)
                    variableCommandsAfterReset.clear()
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
        }
        try {
            val action = messageIO.readAction()
            val commands = action.commandsList

            if (commands.isNotEmpty()) {
                for (command in commands) {
                    if (handleCommand(command, client, player))
                        return
                }
            }
            if (player.isDead) return else sendSetScreenNull(client)
            val actionArray = action.actionList
            if (applyAction(actionArray, player, client)) return
        } catch (e: SocketTimeoutException) {
            println("Timeout")
        } catch (e: IOException) {
            // release lock
            runPhaseLock.notifyAll()
            e.printStackTrace()
            exitProcess(-1)
        } catch (e: Exception) {
            runPhaseLock.notifyAll()
            e.printStackTrace()
            exitProcess(-2)
        }
    }

    private fun sendSetScreenNull(client: MinecraftClient) {
        client.setScreen(null)
    }


    // Returns: Should ignore action
    private fun handleCommand(
        command: String,
        client: MinecraftClient,
        player: ClientPlayerEntity
    ): Boolean {
        if (command == "respawn") {
            if (client.currentScreen is DeathScreen && player.isDead) {
                player.requestRespawn()
                sendSetScreenNull(client)
            }
            return true
        } else if (command.startsWith("fastreset")) {
            printWithTime("Fast resetting")
            val extraCommand = command.substringAfter("fastreset ").trim()
            if (extraCommand.isNotEmpty()) {
                val commands = extraCommand.split(";")
                println("Extra commands: $commands")
                variableCommandsAfterReset.addAll(commands)
            }
            resetPhase = ResetPhase.WAIT_PLAYER_DEATH
//            player.kill() //kill player
            runCommand(player, "/kill @p") // kill player
            runCommand(player, "/tp @e[type=!player] ~ -500 ~") // send to void
            return true
        } else if (command.startsWith("random-summon")) {
            printWithTime("Random summon")
            val arguments = command.substringAfter("random-summon ").trim()
            val argumentsList = arguments.split(" ")
            val entityName = argumentsList[0]
            val x = argumentsList[1].toInt()
            val y = argumentsList[2].toInt()
            val z = argumentsList[3].toInt()
//            val entityType = EntityType.get(entityName).getOrNull() as? MobEntity ?: return false
//            LargeEntitySpawnHelper.trySpawnAt(
//                entityType.type,
//                SpawnReason.COMMAND,
//                world,
//                BlockPos(x, y, z),
//                20,
//                20,
//                20,
//                LargeEntitySpawnHelper.Requirements { world, pos, state, abovePos, aboveState ->
//                    aboveState.getCollisionShape(
//                        world,
//                        abovePos
//                    ).isEmpty && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP
//                }
//            )
            return false
        } else if (command == "exit") {
            println("Will terminate")
            runPhaseLock.notifyAll()
            exitProcess(0)
        } else {
            runCommand(player, command)
            println("Executed command: $command")
            return false
        }
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
                (player.input as KeyboardInputWillInterface).setWillPressingForward(false)
                (player.input as KeyboardInputWillInterface).setWillPressingBack(false)
            }

            1 -> {
                (player.input as KeyboardInputWillInterface).setWillPressingForward(true)
                (player.input as KeyboardInputWillInterface).setWillPressingBack(false)
                //                    player.travel(Vec3d(0.0, 0.0, 1.0)) // sideway, upward, forward
            }

            2 -> {
                (player.input as KeyboardInputWillInterface).setWillPressingForward(false)
                (player.input as KeyboardInputWillInterface).setWillPressingBack(true)
            }
        }
        when (movementLR) { // 0: noop, 1: move left, 2: move right
            0 -> {
                (player.input as KeyboardInputWillInterface).setWillPressingRight(false)
                (player.input as KeyboardInputWillInterface).setWillPressingLeft(false)
            }

            1 -> {
                (player.input as KeyboardInputWillInterface).setWillPressingRight(false)
                (player.input as KeyboardInputWillInterface).setWillPressingLeft(true)
            }

            2 -> {
                (player.input as KeyboardInputWillInterface).setWillPressingRight(true)
                (player.input as KeyboardInputWillInterface).setWillPressingLeft(false)
                //                    player.travel(Vec3d(-1.0, 0.0, 0.0))
            }
        }
        when (jumpSneakSprint) { // 0: noop, 1: jump, 2: sneak, 3:sprint
            0 -> {
                //                    println("Sneaking reset")
                (player.input as KeyboardInputWillInterface).setWillJumping(false)
                (player.input as KeyboardInputWillInterface).setWillSneaking(false)
                //                    player.isSprinting = false
            }

            1 -> {
                (player.input as KeyboardInputWillInterface).setWillJumping(true)
                (player.input as KeyboardInputWillInterface).setWillSneaking(false)
            }

            2 -> {
                (player.input as KeyboardInputWillInterface).setWillJumping(false)
                (player.input as KeyboardInputWillInterface).setWillSneaking(true)
            }

            3 -> {
                (player.input as KeyboardInputWillInterface).setWillJumping(false)
                (player.input as KeyboardInputWillInterface).setWillSneaking(false)
                (player.input as KeyboardInputWillInterface).setWillSprinting(true)
            }
        }
        // pitch: 0: -90 degree, 24: 90 degree
        val deltaPitchInDeg = (deltaPitch - 12f) / 12f * 90f
        // yaw: 0: -180 degree, 24: 180 degree
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


    private fun sendObservation(messageIO: MessageIO, world: World) {
        printWithTime("send Observation")
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (player == null) {
            printWithTime("Player is null")
            return
        }
        // request stats from server
        // TODO: Use server player stats directly instead of client player stats
        client.networkHandler?.sendPacket(ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS))
        val buffer = client.framebuffer
        try {
            val image_1: ByteString
            val image_2: ByteString
            val pos = player.pos
            if (initialEnvironment.biocular) {
                // translate the player position to the left and right
                val center = player.eyePos
                // calculate left direction based on yaw
                // go to the left by eyeWidth block
                val eyeWidth = initialEnvironment.eyeDistance
                val left = center.add(
                    eyeWidth * -sin(Math.toRadians(player.yaw.toDouble())),
                    0.0,
                    eyeWidth * cos(Math.toRadians(player.yaw.toDouble()))
                )
                // calculate right direction based on yaw
                // go to the right by eyeWidth block
                val right = center.add(
                    eyeWidth * sin(Math.toRadians(player.yaw.toDouble())),
                    0.0,
                    eyeWidth * -cos(Math.toRadians(player.yaw.toDouble()))
                )
                val oldPrevX = player.prevX
                val oldPrevY = player.prevY
                val oldPrevZ = player.prevZ
                // go to left and render, take screenshot
                player.prevX = left.x
                player.prevY = left.y
                player.prevZ = left.z
                player.setPos(left.x, left.y, left.z)
                println("New left position: ${left.x}, ${left.y}, ${left.z} ${player.prevX}, ${player.prevY}, ${player.prevZ}")
                // (client as ClientRenderInvoker).invokeRender(true)
                render(client)
                val image1ByteArray = ScreenshotRecorder.takeScreenshot(buffer).use { screenshot ->
                    encodeImageToBytes(
                        screenshot,
                        initialEnvironment.visibleSizeX,
                        initialEnvironment.visibleSizeY,
                        initialEnvironment.imageSizeX,
                        initialEnvironment.imageSizeY
                    )
                }
                image_1 = ByteString.copyFrom(image1ByteArray)
                player.prevX = right.x
                player.prevY = right.y
                player.prevZ = right.z
                player.setPosition(right.x, right.y, right.z)
                println("New right position: ${right.x}, ${right.y}, ${right.z} ${player.prevX}, ${player.prevY}, ${player.prevZ}")
//                (client as ClientRenderInvoker).invokeRender(true)
                render(client)
                val image2ByteArray = ScreenshotRecorder.takeScreenshot(buffer).use { screenshot ->
                    encodeImageToBytes(
                        screenshot,
                        initialEnvironment.visibleSizeX,
                        initialEnvironment.visibleSizeY,
                        initialEnvironment.imageSizeX,
                        initialEnvironment.imageSizeY
                    )
                }
                image_2 = ByteString.copyFrom(image2ByteArray)
                // return to the original position
                player.prevX = oldPrevX
                player.prevY = oldPrevY
                player.prevZ = oldPrevZ
                player.setPos(pos.x, pos.y, pos.z)
            } else {
                val image1ByteArray = ScreenshotRecorder.takeScreenshot(buffer).use { screenshot ->
                    encodeImageToBytes(
                        screenshot,
                        initialEnvironment.visibleSizeX,
                        initialEnvironment.visibleSizeY,
                        initialEnvironment.imageSizeX,
                        initialEnvironment.imageSizeY
                    )
                }
                image_1 = ByteString.copyFrom(image1ByteArray)
                image_2 = ByteString.copyFrom(image1ByteArray)
            }

            val observationSpaceMessage = observationSpaceMessage {
                image = image_1
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
                    miscStatistics[miscStatKey] = player.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(key))
                }
                entityListener?.run {
                    for (entity in entities) {
                        // notify where entity is, what it is (supervised)
                        visibleEntities.add(entity.toMessage())
                    }
                }
                for (distance in initialEnvironment.surroundingEntityDistancesList) {
                    val distanceDouble = distance.toDouble()
                    val EntitiesWithinDistanceMessage = entitiesWithinDistance {
                        world.getOtherEntities(
                            player,
                            player.boundingBox.expand(distanceDouble, distanceDouble, distanceDouble)
                        )
                            .forEach {
                                entities.add(it.toMessage())
                            }
                    }
                    surroundingEntities[distance] = EntitiesWithinDistanceMessage
                }
//                    bobberThrown = serverPlayerEntity?.fishHook != null
                bobberThrown = player.fishHook != null
                experience = player.totalExperience
                worldTime = world.time // world tick, monotonic increasing
                lastDeathMessage = deathMessageCollector?.lastDeathMessage?.firstOrNull() ?: ""
                image2 = image_2
            }
            messageIO.writeObservation(observationSpaceMessage)
        } catch (e: IOException) {
            e.printStackTrace()
            synchronized(runPhaseLock) {
                runPhaseLock.notifyAll()
            }
            client.scheduleStop()

            val threadGroup = Thread.currentThread().threadGroup
            val threads = arrayOfNulls<Thread>(threadGroup.activeCount())
            threadGroup.enumerate(threads)

            for (thread in threads) {
                if (thread == null)
                    continue
                if (thread != Thread.currentThread())
                    thread.interrupt()
            }
            println("Will exitprocess -3")
//            exitProcess(-3)
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

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")
fun printWithTime(msg: String) {
    if (false)
        println("${formatter.format(java.time.LocalDateTime.now())} $msg")
}

fun render(client: MinecraftClient) {
    RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT or GlConst.GL_COLOR_BUFFER_BIT, IS_SYSTEM_MAC)
    client.framebuffer.beginWrite(true)
    BackgroundRenderer.clearFog()
    RenderSystem.enableCull()
    val l = Util.getMeasuringTimeNano()
    client.gameRenderer.render(
        0.0f,// client.renderTickCounter.tickDelta,
        l,
        true // tick
    )
    client.framebuffer.endWrite()
    client.framebuffer.draw(client.window.framebufferWidth, client.window.framebufferHeight)
}