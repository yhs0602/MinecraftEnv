package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString

object FramebufferCapturer {
    init {
        System.loadLibrary("framebuffer_capturer")
    }

    external fun captureFramebuffer(
        textureId: Int,
        textureWidth: Int,
        textureHeight: Int,
        targetSizeX: Int,
        targetSizeY: Int
    ): ByteString
}