package com.kyhsgeekcode.minecraft_env

import com.kyhsgeekcode.minecraft_env.mixin.ChatVisibleMessageAccessor
import com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.screen.world.WorldListWidget
import net.minecraft.client.gui.widget.*
import net.minecraft.server.MinecraftServer
import net.minecraft.world.GameMode


interface CommandExecutor {
    fun runCommand(server: MinecraftServer, command: String)
}

class EnvironmentInitializer(
    private val initialEnvironment: InitialEnvironmentMessage,
) {
    var hasRunInitWorld: Boolean = false
        private set
    var initWorldFinished: Boolean = false
        private set

    private lateinit var minecraftServer: MinecraftServer

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
//                println("Select world screen1")
                var widget: WorldListWidget? = null
                var deleteButton: ButtonWidget? = null
                var createButton: ButtonWidget? = null
                for (child in screen.children()) {
//                    println(child)
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
//                println("Create world screen")
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
//                        println(child)
                        if (child is TextFieldWidget) {
//                            println("Found text field")
                            child.text = initialEnvironment.seed.toString()
                        }
                    }
                }
                if (initialEnvironment.isWorldFlat) {
                    for (child in screen.children()) {
//                        println(child)
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
        val window = MinecraftClient.getInstance().window
        window.setWindowedSize(initialEnvironment.visibleSizeX, initialEnvironment.visibleSizeY)
        disablePauseOnLostFocus(client)
    }

    fun reset(chatHud: ChatHud, commandExecutor: CommandExecutor) {
        println("Resetting...")
        hasRunInitWorld = false
        initWorldFinished = false
        chatHud.clear(true)
        onWorldTick(null, chatHud, commandExecutor)
    }

    fun onWorldTick(
        minecraftServer: MinecraftServer?,
        chatHud: ChatHud,
        commandExecutor: CommandExecutor
    ) {
        if (minecraftServer != null)
            this.minecraftServer = minecraftServer
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
        initWorldFinished = (initWorldFinished || hasInitFinishMessage)
//        println("has init finish message: $hasInitFinishMessage, has run init world: $hasRunInitWorld, init world finished: $initWorldFinished")
        chatHud.clear(true)
        if (hasRunInitWorld)
            return
        // NOTE: should be called only once when initial environment is set
        val myCommandExecutor = { server: MinecraftServer, c: String ->
            commandExecutor.runCommand(server, c)
        }
        setupInitialPosition(myCommandExecutor)
        setupInitialWeather(myCommandExecutor)
        setupAllowMobSpawn(myCommandExecutor)
        setupInitialInventory(myCommandExecutor)
        summonInitialMobs(myCommandExecutor)
        if (initialEnvironment.alwaysDay)
            setupAlwaysDay(myCommandExecutor)
        if (initialEnvironment.alwaysNight)
            setupAlwaysNight(myCommandExecutor)
        for (command in initialEnvironment.initialExtraCommandsList)
            commandExecutor.runCommand(this.minecraftServer, "/$command")
        commandExecutor.runCommand(this.minecraftServer, "/say Initialization Done")
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

    private fun summonInitialMobs(commandExecutor: (MinecraftServer, String) -> Unit) {
        for (command in initialEnvironment.initialMobsCommandsList) {
            commandExecutor(minecraftServer, "/summon $command")
        }
    }

    private fun setupInitialInventory(

        commandExecutor: (MinecraftServer, String) -> Unit
    ) {
        for (command in initialEnvironment.initialInventoryCommandsList) {
            commandExecutor(minecraftServer, "/give @p $command")
        }
    }

    private fun setupInitialPosition(

        commandExecutor: (MinecraftServer, String) -> Unit
    ) {
        if (initialEnvironment.initialPositionList.isEmpty())
            return
        commandExecutor(
            minecraftServer,
            "/tp @p ${initialEnvironment.initialPositionList[0]} ${initialEnvironment.initialPositionList[1]} ${initialEnvironment.initialPositionList[2]}"
        )
    }

    private fun setupInitialWeather(commandExecutor: (MinecraftServer, String) -> Unit) {
        commandExecutor(
            minecraftServer,
            "/weather ${initialEnvironment.initialWeather}"
        )
    }

    private fun setupAllowMobSpawn(commandExecutor: (MinecraftServer, String) -> Unit) {
        if (initialEnvironment.allowMobSpawn)
            return
        commandExecutor(
            minecraftServer,
            "/gamerule doMobSpawning false"
        )
    }

    private fun setupAlwaysDay(commandExecutor: (MinecraftServer, String) -> Unit) {
        commandExecutor(
            minecraftServer,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            minecraftServer,
            "/time set day"
        )
    }

    private fun setupAlwaysNight(commandExecutor: (MinecraftServer, String) -> Unit) {
        commandExecutor(
            minecraftServer,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            minecraftServer,
            "/time set night"
        )
    }

    private fun disablePauseOnLostFocus(client: MinecraftClient) {
        val options = client.options
        if (options != null) {
            if (options.pauseOnLostFocus) {
                println("Disabled pause on lost focus")
                options.pauseOnLostFocus = false
                client.options.write()
            }
        }
    }
}