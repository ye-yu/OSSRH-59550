package io.github.yeyu.jamcgui.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11

/**
 * Screen drawing utility file
 *
 * This portion is file is adapting codes from the
 * [LibGui repo][https://github.com/CottonMC/LibGui]
 *
 * Copyright: Copyright (c) 2018 The Cotton Project
 *
 * LICENSE: [MIT][https://github.com/CottonMC/LibGui/blob/master/LICENSE]
 * */
object DrawerUtil {
    /**
     * Draws a colored rectangle on the screen
     * @param left the pixel coord from the left
     * @param top the pixel coord from the top
     * @param width the rectangle width
     * @param height the rectangle height
     * @param color the integer color
     * @see constructColor
     * */
    fun coloredRect(left: Int, top: Int, width: Int, height: Int, color: Int) {
        var fixedWidth = width
        var fixedHeight = height
        if (fixedWidth <= 0) fixedWidth = 1
        if (fixedHeight <= 0) fixedHeight = 1
        val a = (color shr 24 and 255) / 255.0f
        val r = (color shr 16 and 255) / 255.0f
        val g = (color shr 8 and 255) / 255.0f
        val b = (color and 255) / 255.0f
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SrcFactor.ONE,
            GlStateManager.DstFactor.ZERO
        )
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR) //I thought GL_QUADS was deprecated but okay, sure.
        buffer.vertex(left.toDouble(), top + fixedHeight.toDouble(), 0.0).color(r, g, b, a).next()
        buffer.vertex(left + fixedWidth.toDouble(), top + fixedHeight.toDouble(), 0.0).color(r, g, b, a).next()
        buffer.vertex(left + fixedWidth.toDouble(), top.toDouble(), 0.0).color(r, g, b, a).next()
        buffer.vertex(left.toDouble(), top.toDouble(), 0.0).color(r, g, b, a).next()
        tessellator.draw()
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    /**
     * Invert the color on the area of the specified rectangle
     * */
    fun invertedRect(x: Int, y: Int, width: Int, height: Int) {
        val tessellator = Tessellator.getInstance()
        val buf = tessellator.buffer
        @Suppress("DEPRECATION")
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f)
        RenderSystem.disableTexture()
        RenderSystem.enableColorLogicOp()
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE)
        buf.begin(GL11.GL_QUADS, VertexFormats.POSITION)
        buf.vertex(x.toDouble(), y + height.toDouble(), 0.0).next()
        buf.vertex(x + width.toDouble(), y + height.toDouble(), 0.0).next()
        buf.vertex(x + width.toDouble(), y.toDouble(), 0.0).next()
        buf.vertex(x.toDouble(), y.toDouble(), 0.0).next()
        tessellator.draw()
        RenderSystem.disableColorLogicOp()
        RenderSystem.enableTexture()
    }

    /**
     * Note: Overflows are unchecked. Make sure the passed values
     * are in the range of an unsigned byte: [0, 256)
     * @param r the red channel intensity
     * @param g the green channel intensity
     * @param b the blue channel intensity
     * @param a the alpha channel intensity (opacity)
     * */
    fun constructColor(r: Int, g: Int, b: Int, a: Int): Int {
        return (a shl 24) + (r shl 16) + (g shl 8) + b
    }
}