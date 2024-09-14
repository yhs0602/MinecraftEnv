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
//        println("Set mouse pos to $x, $y")
    }

    fun setCursorShown(show: Boolean) {
        showCursor = show
    }

    fun moveMouseBy(dx: Int, dy: Int) {
        // dx와 dy의 절대값 계산 (정수로 변환)
        val stepsX = Math.abs(dx)
        val stepsY = Math.abs(dy)

        // dx와 dy의 이동 방향 계산
        val stepX = if (dx > 0) 1 else -1
        val stepY = if (dy > 0) 1 else -1

        // 최대 이동 횟수 계산 (더 큰 쪽을 기준으로 반복)
        val maxSteps = Math.max(stepsX, stepsY)

        // X와 Y를 번갈아 가며 이동
        var movedX = 0
        var movedY = 0
        for (i in 0 until maxSteps) {
            if (movedX < stepsX) {
                mouseX += stepX
                cursorPosCallback?.invoke(handle, mouseX, mouseY)
                movedX++
            }
            if (movedY < stepsY) {
                mouseY += stepY
                cursorPosCallback?.invoke(handle, mouseX, mouseY)
                movedY++
            }
        }
    }



    // TODO: Mods (shift click, etc)
    fun clickLeftButton(shift: Boolean) {
        val mods = if (shift) GLFW.GLFW_MOD_SHIFT else 0
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_PRESS, mods)
        leftButtonPressed = true
    }

    fun releaseLeftButton(shift: Boolean) {
        val mods = if (shift) GLFW.GLFW_MOD_SHIFT else 0
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_RELEASE, mods)
        leftButtonPressed = false
    }

    fun clickRightButton(shift: Boolean) {
        val mods = if (shift) GLFW.GLFW_MOD_SHIFT else 0
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_PRESS, mods)
        rightButtonPressed = true
    }

    fun releaseRightButton(shift: Boolean) {
        val mods = if (shift) GLFW.GLFW_MOD_SHIFT else 0
        mouseButtonCallback?.invoke(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_RELEASE, mods)
        rightButtonPressed = false
    }
}