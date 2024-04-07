package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString

object FramebufferCapturer {
    init {
        System.loadLibrary("native-lib")
    }

    external fun captureFramebuffer(
        textureId: Int,
        textureWidth: Int,
        textureHeight: Int,
        targetSizeX: Int,
        targetSizeY: Int,
        encodingMode: Int
    ): ByteString

    const val RAW = 0
    const val PNG = 1
}