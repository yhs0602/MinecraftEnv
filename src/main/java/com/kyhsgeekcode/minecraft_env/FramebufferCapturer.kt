package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString
import org.lwjgl.opengl.GL11

object FramebufferCapturer {
    init {
        System.loadLibrary("native-lib")
    }

    external fun captureFramebuffer(
        textureId: Int,
        frameBufferId: Int,
        textureWidth: Int,
        textureHeight: Int,
        targetSizeX: Int,
        targetSizeY: Int,
        encodingMode: Int,
        isExtensionAvailable: Boolean,
    ): ByteString

    //    private external fun checkExtension(): Boolean
    fun checkExtensionJVM() {
        if (hasCheckedExtension)
            return
        val extensions = GL11.glGetString(GL11.GL_EXTENSIONS)
        if (extensions == null) {
            println("FramebufferCapturer: Extensions is null")
            hasCheckedExtension = true
            isExtensionAvailable = false
            return
        }
        isExtensionAvailable = extensions.contains("GL_ANGLE_pack_reverse_row_order")
        if (!isExtensionAvailable) {
            println("FramebufferCapturer: Extension not available: Availables: $extensions")
        } else {
            println("FramebufferCapturer: Extension available")
        }
        hasCheckedExtension = true
    }

    const val RAW = 0
    const val PNG = 1
    var isExtensionAvailable: Boolean = false
    private var hasCheckedExtension: Boolean = false
}