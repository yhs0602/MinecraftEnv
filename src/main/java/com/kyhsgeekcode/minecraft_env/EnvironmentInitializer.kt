package com.kyhsgeekcode.minecraft_env

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.screen.world.WorldListWidget
import net.minecraft.client.gui.widget.*

class EnvironmentInitializer(val initialEnvironment: InitialEnvironment) {
    fun onClientTick(client: MinecraftClient) {
        val screen = client.currentScreen
        when (screen) {
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
                val realCheatAllowed = false
                val cheatRequested = true
                var indexOfWorldSettingTab = -1
                var cheatButton: CyclingButtonWidget<*>? = null
                var settingTabWidget: TabNavigationWidget? = null
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
                if (cheatButton != null) {
                    println("Cheat button not found")
                    val testString = if (cheatRequested) "ON" else "OFF"
                    while (!cheatButton.message.string.endsWith(testString)) {
                        cheatButton.onPress()
                    }
                }
                //                        realCheatAllowed = cheatRequested;
                // Select world settings tab
                settingTabWidget!!.selectTab(indexOfWorldSettingTab, false)
                // Search for seed input
                for (child in screen.children()) {
                    println(child)
                    if (child is TextFieldWidget) {
                        println("Found text field")
                        child.text = "123456789"
                    }
                }
                createButton?.onPress()
            }
        }
    }
}