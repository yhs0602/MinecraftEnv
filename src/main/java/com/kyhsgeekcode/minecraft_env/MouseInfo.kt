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

    fun moveMouseBy(dx: Double, dy: Double) {
        // 쪼개는 단위 크기 설정 (예: 1 픽셀씩 움직임)
        val stepSize = 1.0

        // dx와 dy의 절대값 계산
        val stepsX = Math.abs(dx / stepSize).toInt()
        val stepsY = Math.abs(dy / stepSize).toInt()

        // 가장 큰 움직임에 대한 총 단계 계산 (둘 중 더 큰 값)
        val totalSteps = Math.max(stepsX, stepsY)

        // 각 단계에서 움직일 dx, dy 비율 계산
        val stepDx = dx / totalSteps
        val stepDy = dy / totalSteps

        // 단계별로 커서를 이동
        for (i in 0 until totalSteps) {
            mouseX += stepDx
            mouseY += stepDy
            cursorPosCallback?.invoke(handle, mouseX, mouseY)
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