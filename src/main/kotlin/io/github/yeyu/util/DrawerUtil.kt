package io.github.yeyu.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11

object DrawerUtil {
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

    fun invertedRect(x: Int, y: Int, width: Int, height: Int) {
        val tessellator = Tessellator.getInstance()
        val buf = tessellator.buffer
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


    fun constructColor(r: Int, g: Int, b: Int, a: Int): Int {
        return (a shl 24) + (r shl 16) + (g shl 8) + b
    }
}