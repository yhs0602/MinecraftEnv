package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

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
        val vendor = GL11.glGetString(GL11.GL_VENDOR)
        if (vendor == null) {
            println("FramebufferCapturer: Vendor is null")
        } else {
            println("FramebufferCapturer: Vendor: $vendor")
        }
        val num_extensions = GL30.glGetInteger(GL30.GL_NUM_EXTENSIONS)
        val extensions = (0 until num_extensions).map { GL30.glGetStringi(GL30.GL_EXTENSIONS, it) }
        if (extensions.isEmpty())
            println("FramebufferCapturer: Extensions is empty")
        else {
            println("FramebufferCapturer: Extensions: $extensions")
            if (extensions.contains("GL_ANGLE_pack_reverse_row_order")) {
                println("FramebufferCapturer: Extension available")
                isExtensionAvailable = true
            } else {
                println("FramebufferCapturer: Extension not available")
            }
        }
        hasCheckedExtension = true
    }

    const val RAW = 0
    const val PNG = 1
    var isExtensionAvailable: Boolean = false
    private var hasCheckedExtension: Boolean = false
}