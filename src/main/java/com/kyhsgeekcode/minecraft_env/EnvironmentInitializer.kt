package com.kyhsgeekcode.minecraft_env

import com.kyhsgeekcode.minecraft_env.mixin.ChatVisibleMessageAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.FontStorage
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.screen.world.WorldListWidget
import net.minecraft.client.gui.widget.*
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.world.GameMode


interface CommandExecutor {
    fun runCommand(clientPlayerEntity: ClientPlayerEntity, command: String)
}

class EnvironmentInitializer(private val initialEnvironment: InitialEnvironment) {
    private var hasRunInitWorld: Boolean = false
    var initWorldFinished: Boolean = false
        private set

    fun onClientTick(client: MinecraftClient) {
        when (val screen = client.currentScreen) {
            is TitleScreen -> {
                screen.children().find {
                    it is ButtonWidget && it.message.string == "Singleplayer"
                }?.let {
                    it as ButtonWidget
                    it.onPress()
                    return
                }
            }

            is SelectWorldScreen -> {
                println("Select world screen1")
                var widget: WorldListWidget? = null
                var deleteButton: ButtonWidget? = null
                var createButton: ButtonWidget? = null
                for (child in screen.children()) {
                    println(child)
                    if (child is WorldListWidget) {
                        widget = child
                    } else if (child is ButtonWidget) {
                        if (child.message.string == "Delete Selected World") {
                            deleteButton = child
                        } else if (child.message.string == "Create New World") {
                            createButton = child
                        }
                    }
                }
                if (widget != null && deleteButton != null) {
                    widget.setSelected(widget.children()[0])
                    deleteButton.onPress()
                    return
                }
                createButton?.onPress()
            }

            is CreateWorldScreen -> {
                println("Create world screen")
                var createButton: ButtonWidget? = null
                val cheatRequested = true
                var indexOfWorldSettingTab = -1
                var cheatButton: CyclingButtonWidget<*>? = null
                var settingTabWidget: TabNavigationWidget? = null
                var worldTypeButton: CyclingButtonWidget<*>? = null
                for (child in screen.children()) {
                    // search for tab navigation widget, to find index of world settings tab
                    if (indexOfWorldSettingTab == -1 && child is TabNavigationWidget) {
                        settingTabWidget = child
                        for (i in child.children().indices) {
                            val tabChild: Element = child.children()[i]
                            if (tabChild is TabButtonWidget) {
                                if (tabChild.message.string == "World") {
                                    indexOfWorldSettingTab = i
                                }
                            }
                        }
                    }
                    // search for create button
                    if (createButton == null && child is ButtonWidget) {
                        if (child.message.string == "Create New World") {
                            createButton = child
                        }
                    }
                    // search for cheat button
                    if (cheatButton == null && child is CyclingButtonWidget<*>) {
                        if (child.message.string.startsWith("Allow Cheats")) {
                            cheatButton = child
                        }
                    }
                }
                // Set allow cheats to requested
                if (cheatButton != null)
                    setupAllowCheats(cheatButton, cheatRequested)
                else
                    println("Cheat button not found")
                // Select world settings tab
                settingTabWidget!!.selectTab(indexOfWorldSettingTab, false)
                // Search for seed input
                if (initialEnvironment.seed != null) {
                    for (child in screen.children()) {
                        println(child)
                        if (child is TextFieldWidget) {
                            println("Found text field")
                            child.text = initialEnvironment.seed.toString()
                        }
                    }
                }
                if (initialEnvironment.isWorldFlat) {
                    for (child in screen.children()) {
                        println(child)
                        if (worldTypeButton == null && child is CyclingButtonWidget<*>) {
                            if (child.message.string.startsWith("World Type")) {
                                worldTypeButton = child
                            }
                        }
                    }
                    if (worldTypeButton != null) {
                        while (!worldTypeButton.message.string.endsWith("flat")) {
                            worldTypeButton.onPress()
                        }
                    }
                }
                createButton?.onPress()
            }
        }
    }

    fun reset(chatHud: ChatHud, player: ClientPlayerEntity, commandExecutor: CommandExecutor) {
        println("Resetting...")
        hasRunInitWorld = false
        initWorldFinished = false
        chatHud.clear(true)
        onWorldTick(chatHud, player, commandExecutor)
    }

    fun onWorldTick(
        chatHud: ChatHud,
        player: ClientPlayerEntity,
        commandExecutor: CommandExecutor
    ) {
        val messages = (chatHud as ChatVisibleMessageAccessor).visibleMessages
        val hasInitFinishMessage = messages.find {
            val text = it.content
            val builder = StringBuilder()
            text.accept { index, style, codePoint ->
                val ch = codePoint.toChar()
                builder.append(ch)
                true
            }
            val content = builder.toString()
            content.contains("Initialization Done")
        } != null
        initWorldFinished = initWorldFinished || hasInitFinishMessage
//        println("has init finish message: $hasInitFinishMessage, has run init world: $hasRunInitWorld, init world finished: $initWorldFinished")
        chatHud.clear(true)
        if (hasRunInitWorld)
            return
        val window = MinecraftClient.getInstance().window
        window.setWindowedSize(initialEnvironment.visibleSizeX, initialEnvironment.visibleSizeY)
        // NOTE: should be called only once when initial environment is set
        val myCommandExecutor = { p: ClientPlayerEntity, c: String ->
            commandExecutor.runCommand(p, c)
        }
        setupInitialPosition(player, myCommandExecutor)
        setupInitialWeather(player, myCommandExecutor)
        setupAllowMobSpawn(player, myCommandExecutor)
        setupInitialInventory(player, myCommandExecutor)
        summonInitialMobs(player, myCommandExecutor)
        if (initialEnvironment.alwaysDay)
            setupAlwaysDay(player, myCommandExecutor)
        if (initialEnvironment.alwaysNight)
            setupAlwaysNight(player, myCommandExecutor)
        commandExecutor.runCommand(player, "/say Initialization Done")
        initWorldFinished = false
        hasRunInitWorld = true
    }

    private fun setupAllowCheats(
        cheatButton: CyclingButtonWidget<*>,
        cheatRequested: Boolean
    ) {
        val testString = if (cheatRequested) "ON" else "OFF"
        while (!cheatButton.message.string.endsWith(testString)) {
            cheatButton.onPress()
        }
    }

    private fun setupGameMode(
        gameModeButton: CyclingButtonWidget<*>,
        gameModeRequested: GameMode
    ) {
        val testString = gameModeRequested.name
        while (!gameModeButton.message.string.endsWith(testString)) {
            gameModeButton.onPress()
        }
    }

    private fun summonInitialMobs(player: ClientPlayerEntity, commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        for (command in initialEnvironment.initialMobsCommands) {
            commandExecutor(player, "/summon $command")
        }
    }

    private fun setupInitialInventory(
        player: ClientPlayerEntity,
        commandExecutor: (ClientPlayerEntity, String) -> Unit
    ) {
        for (command in initialEnvironment.initialInventoryCommands) {
            commandExecutor(player, "/give @p $command")
        }
    }

    private fun setupInitialPosition(
        player: ClientPlayerEntity,
        commandExecutor: (ClientPlayerEntity, String) -> Unit
    ) {
        if (initialEnvironment.initialPosition == null)
            return
        commandExecutor(
            player,
            "/tp @p ${initialEnvironment.initialPosition[0]} ${initialEnvironment.initialPosition[1]} ${initialEnvironment.initialPosition[2]}"
        )
    }

    private fun setupInitialWeather(player: ClientPlayerEntity, commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/weather ${initialEnvironment.initialWeather}"
        )
    }

    private fun setupAllowMobSpawn(player: ClientPlayerEntity, commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        if (initialEnvironment.allowMobSpawn)
            return
        commandExecutor(
            player,
            "/gamerule doMobSpawning false"
        )
    }

    private fun setupAlwaysDay(player: ClientPlayerEntity, commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            player,
            "/time set day"
        )
    }

    private fun setupAlwaysNight(player: ClientPlayerEntity, commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            player,
            "/time set night"
        )
    }
}