package com.kyhsgeekcode.minecraft_env

import com.google.protobuf.ByteString
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.nio.ByteBuffer


fun captureFramebuffer(
    framebuffer: Framebuffer,
    targetSizeX: Int,
    targetSizeY: Int
): ByteString {
    val i = framebuffer.textureWidth
    val j = framebuffer.textureHeight
    RenderSystem.bindTexture(framebuffer.colorAttachment)
    RenderSystem.assertOnRenderThread()
    GlStateManager._pixelStore(GlConst.GL_PACK_ALIGNMENT, 3)
    val byteBuffer = ByteBuffer.allocateDirect(3 * i * j)
    GL11.glGetTexImage(GlConst.GL_TEXTURE_2D, 0, GlConst.GL_RGB, GlConst.GL_UNSIGNED_BYTE, byteBuffer)
    val dataBuffer = DataBufferByte(byteBuffer.array(), 3 * i * j)
    val raster = Raster.createInterleavedRaster(dataBuffer, i, j, 3 * i, 3, intArrayOf(2, 1, 0), null)
    val cm = ComponentColorModel(
        ComponentColorModel.getRGBdefault().colorSpace,
        false,
        false,
        ComponentColorModel.OPAQUE,
        DataBufferByte.TYPE_BYTE
    )
    val image = BufferedImage(cm, raster, false, null)
    // resize if needed
    val resizedImage: BufferedImage
    if (i != targetSizeX || j != targetSizeY) {
        resizedImage = BufferedImage(targetSizeX, targetSizeY, image.type)
        val graphics = resizedImage.createGraphics()
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        // resize and mirror
        graphics.drawImage(image, 0, targetSizeY, targetSizeX, -targetSizeY, null)
        graphics.dispose()
    } else {
        // TODO: only mirror
        resizedImage = image
    }
    // convert to protobuf ByteString
    // FIXME
//    return ByteString.copyFrom(resizedImage.bytes)
    return ByteString.copyFrom(byteBuffer)
}