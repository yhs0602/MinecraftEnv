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

    private external fun checkExtension(): Boolean

    fun checkExtensionJVM() {
        if (hasCheckedExtension)
            return
        isExtensionAvailable = checkExtension()
        if (!isExtensionAvailable) {
            println("FramebufferCapturer: Extension not available")
        }
        hasCheckedExtension = true
    }

    const val RAW = 0
    const val PNG = 1
    var isExtensionAvailable: Boolean = false
    private var hasCheckedExtension: Boolean = false
}