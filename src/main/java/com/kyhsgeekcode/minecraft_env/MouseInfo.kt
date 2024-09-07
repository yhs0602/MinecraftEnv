package com.kyhsgeekcode.minecraft_env

import com.kyhsgeekcode.minecraft_env.mixin.MouseXYAccessor
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWMouseButtonCallbackI

object MouseInfo {
    var handle: Long = 0
    var cursorPosCallback: GLFWCursorPosCallbackI? = null
    var mouseButtonCallback: GLFWMouseButtonCallbackI? = null
    var mouseX: Double = 0.0
    var mouseY: Double = 0.0
    var leftButtonPressed: Boolean = false
    var rightButtonPressed: Boolean = false
    var showCursor: Boolean = false

    fun getMousePos(): Pair<Double, Double> {
        return Pair(mouseX, mouseY)
    }

    fun setCursorPos(x: Double, y: Double) {
        mouseX = x
        mouseY = y
        // Do not call the callback
        val client = MinecraftClient.getInstance()
        (client?.mouse as? MouseXYAccessor)?.setX(x)
        (client?.mouse as? MouseXYAccessor)?.setY(y)
        println("Set mouse pos to $x, $y")
    }

    fun setCursorShown(show: Boolean) {
        showCursor = show
    }

    fun moveMouseBy(dx: Double, dy: Double) {
        if (showCursor) {
            mouseX += dx
            mouseY += dy
            cursorPosCallback?.invoke(handle, mouseX, mouseY)
            println("Called callback with absolute position $mouseX, $mouseY, by $dx, $dy")
        } else {
            cursorPosCallback?.invoke(handle, dx, dy)
            println("Called callback with relative movement by $dx, $dy")
        }
    }


    // TODO: Mods (shift click, etc)
    fun clickLeftButton() {
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_PRESS, 0)
        leftButtonPressed = true
    }

    fun releaseLeftButton() {
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_RELEASE, 0)
        leftButtonPressed = false
    }

    fun clickRightButton() {
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_PRESS, 0)
        rightButtonPressed = true
    }

    fun releaseRightButton() {
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_RELEASE, 0)
        rightButtonPressed = false
    }
}