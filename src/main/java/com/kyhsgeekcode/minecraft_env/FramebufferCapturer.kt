package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString

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

    external fun checkExtension(): Boolean

    private fun checkExtensionJVM(): Boolean {
        val isAvailable = checkExtension()
        if (!isAvailable) {
            println("FramebufferCapturer: Extension not available")
        }
        return isAvailable
    }

    const val RAW = 0
    const val PNG = 1
    val isExtensionAvailable = checkExtensionJVM()
}