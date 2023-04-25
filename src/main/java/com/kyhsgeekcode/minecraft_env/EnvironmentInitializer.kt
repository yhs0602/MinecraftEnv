package com.kyhsgeekcode.minecraft_env

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.screen.world.WorldListWidget
import net.minecraft.client.gui.widget.*
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.world.GameMode

class EnvironmentInitializer(private val initialEnvironment: InitialEnvironment) {
    private var hasRunInitWorld: Boolean = false
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

    fun onWorldTick(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        if (hasRunInitWorld)
            return
        val window = MinecraftClient.getInstance().window
        window.setWindowedSize(initialEnvironment.imageSizeX, initialEnvironment.imageSizeY)
        // NOTE: should be called only once when initial environment is set
        setupInitialPosition(player, commandExecutor)
        setupInitialWeather(player, commandExecutor)
        setupInitialInventory(player, commandExecutor)
        summonInitialMobs(player, commandExecutor)
        if (initialEnvironment.alwaysDay)
            setupAlwaysDay(player, commandExecutor)
        if (initialEnvironment.alwaysNight)
            setupAlwaysNight(player, commandExecutor)
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

    private fun summonInitialMobs(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        for (command in initialEnvironment.initialMobsCommands) {
            commandExecutor.runCommand(player, "/summon $command")
        }
    }

    private fun setupInitialInventory(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        for (command in initialEnvironment.initialInventoryCommands) {
            commandExecutor.runCommand(player, "/give @p $command")
        }
    }

    private fun setupInitialPosition(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        if (initialEnvironment.initialPosition == null)
            return
        commandExecutor.runCommand(
            player,
            "/tp @p ${initialEnvironment.initialPosition[0]} ${initialEnvironment.initialPosition[1]} ${initialEnvironment.initialPosition[2]}"
        )
    }

    private fun setupInitialWeather(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        commandExecutor.runCommand(
            player,
            "/weather ${initialEnvironment.initialWeather}"
        )
    }

    private fun setupAlwaysDay(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        commandExecutor.runCommand(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor.runCommand(
            player,
            "/time set day"
        )
    }

    private fun setupAlwaysNight(player: ClientPlayerEntity, commandExecutor: Minecraft_env) {
        commandExecutor.runCommand(
            player,
            "/gamerule doDaylightCycle false"
        )
        commandExecutor.runCommand(
            player,
            "/time set night"
        )
    }
}