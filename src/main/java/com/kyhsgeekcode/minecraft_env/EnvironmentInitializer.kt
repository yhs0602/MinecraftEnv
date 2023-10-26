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
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.NarratorMode
import net.minecraft.client.tutorial.TutorialStep
import net.minecraft.server.MinecraftServer
import net.minecraft.sound.SoundCategory
import net.minecraft.world.GameMode


interface CommandExecutor {
    fun runCommand(server: ClientPlayerEntity, command: String)
}

class EnvironmentInitializer(
    private val initialEnvironment: InitialEnvironmentMessage,
) {
    var hasRunInitWorld: Boolean = false
        private set
    var initWorldFinished: Boolean = false
        private set

    private lateinit var minecraftServer: MinecraftServer
    private lateinit var player: ClientPlayerEntity
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
        disableOnboardAccessibility(client)
        setHudHidden(client, initialEnvironment.hudHidden)
        setRenderDistance(client, initialEnvironment.renderDistance)
        setSimulationDistance(client, initialEnvironment.simulationDistance)
        disableVSync(client)
        disableSound(client)
        disableNarrator(client)
        disableTutorial(client)
    }

    private fun disableSound(client: MinecraftClient) {
        client.options?.let {
            it.getSoundVolumeOption(SoundCategory.MASTER).value = 0.0
        }
    }

    private fun disableNarrator(client: MinecraftClient) {
        val options = client.options
        if (options != null) {
            options.narrator.value = NarratorMode.OFF
            options.write()
            println("Disabled narrator")
        }
    }

    private fun disableTutorial(client: MinecraftClient) {
        client.tutorialManager?.setStep(TutorialStep.NONE)
        println("Disabled tutorial")
    }

    private fun disableVSync(client: MinecraftClient) {
        val options = client.options
        if (options != null) {
            if (options.enableVsync.value) {
                options.enableVsync.value = false
                client.options.write()
                println("Disabled VSync")
            }
        }
    }

    private fun setSimulationDistance(client: MinecraftClient, simulationDistance: Int) {
        val options = client.options
        if (options != null) {
            if (options.simulationDistance.value != simulationDistance) {
                options.simulationDistance.value = simulationDistance
                client.options.write()
                println("Set simulation distance to $simulationDistance")
            }
        }
    }

    private fun setRenderDistance(client: MinecraftClient, renderDistance: Int) {
        val options = client.options
        if (options != null) {
            if (options.viewDistance.value != renderDistance) {
                options.simulationDistance.value = renderDistance
                client.options.write()
                println("Set simulation distance to $renderDistance")
            }
        }
    }

    fun reset(chatHud: ChatHud, commandExecutor: CommandExecutor, variableCommandAfterReset: List<String>) {
        println("Resetting...")
        hasRunInitWorld = false
        initWorldFinished = false
        chatHud.clear(true)
        onWorldTick(null, chatHud, commandExecutor, variableCommandAfterReset)
    }

    fun onWorldTick(
        minecraftServer: MinecraftServer?,
        chatHud: ChatHud,
        commandExecutor: CommandExecutor,
        variableCommandsAfterReset: List<String>,
    ) {
        player = MinecraftClient.getInstance().player ?: return
        val messages = ArrayList((chatHud as ChatVisibleMessageAccessor).visibleMessages)
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
        val myCommandExecutor = { player: ClientPlayerEntity, c: String ->
            commandExecutor.runCommand(player, c)
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
            commandExecutor.runCommand(this.player, "/$command")
        for (command in variableCommandsAfterReset)
            commandExecutor.runCommand(this.player, "/$command")
        commandExecutor.runCommand(this.player, "/say Initialization Done")
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

    private fun summonInitialMobs(commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        for (command in initialEnvironment.initialMobsCommandsList) {
            commandExecutor(player, "/summon $command")
        }
    }

    private fun setupInitialInventory(

        commandExecutor: (ClientPlayerEntity, String) -> Unit
    ) {
        for (command in initialEnvironment.initialInventoryCommandsList) {
            commandExecutor(player, "/give @p $command")
        }
    }

    private fun setupInitialPosition(
        commandExecutor: (ClientPlayerEntity, String) -> Unit
    ) {
        if (initialEnvironment.initialPositionList.isEmpty())
            return
        commandExecutor(
            player,
            "/tp @p ${initialEnvironment.initialPositionList[0]} ${initialEnvironment.initialPositionList[1]} ${initialEnvironment.initialPositionList[2]}"
        )
    }

    private fun setupInitialWeather(commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/weather ${initialEnvironment.initialWeather}"
        )
    }

    private fun setupAllowMobSpawn(commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        if (initialEnvironment.allowMobSpawn)
            return
        commandExecutor(
            player,
            "/gamerule doMobSpawning false"
        )
    }

    private fun setupAlwaysDay(commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            player,
            "/time set day"
        )
    }

    private fun setupAlwaysNight(commandExecutor: (ClientPlayerEntity, String) -> Unit) {
        commandExecutor(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor(
            player,
            "/time set midnight"
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

    private fun disableOnboardAccessibility(client: MinecraftClient) {
        val options = client.options
        if (options != null) {
            if (options.onboardAccessibility) {
                println("Disabled onboardAccessibility")
                options.onboardAccessibility = false
                client.options.write()
            }
        }
    }

    private fun setHudHidden(client: MinecraftClient, hudHidden: Boolean) {
        val options = client.options
        if (options != null) {
            if (options.hudHidden != hudHidden) {
                options.hudHidden = hudHidden
                client.options.write()
                if (hudHidden)
                    println("Hid hud")
                else
                    println("Showed hud")
            }
        }
    }
}