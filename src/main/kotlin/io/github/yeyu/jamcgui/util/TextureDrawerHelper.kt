package io.github.yeyu.jamcgui.util

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

/**
 * Draws cropped image onto the screen
 *
 * Note: Out of bound is unchecked. Make sure the
 * provided coords and size are in the bound of the
 * image size and the matrix stack.
 * @param id the location of the texture
 * @param captureX the x coord of the top left corner of the image to be cropped
 * @param captureY the y coord of the top left corner of the image to be cropped
 * @param width the width of the image to be cropped
 * @param height the height of the image to be cropped
 * @param paintOffsetX the offset x coord from the given parent x coord
 * @param paintOffsetY the offset y coord from the given parent y coord
 * */
class TextureDrawerHelper(
    private val id: Identifier,
    private val captureX: Int,
    private val captureY: Int,
    private val width: Int,
    private val height: Int,
    private val paintOffsetX: Int = 0,
    private val paintOffsetY: Int = 0,
    private val textureWidth: Int = 256,
    private val textureHeight: Int = 256
) {

    /**
     * @param matrices the matrices to draw texture on
     * @param parentX the x coord to draw the texture on
     * @param parentY the y coord to draw the texture on
     * */
    fun drawOn(matrices: MatrixStack, parentX: Int, parentY: Int) {
        drawOn(matrices, parentX, parentY, width, height)
    }

    /**
     * @param matrices the matrices to draw texture on
     * @param parentX the x coord to draw the texture on
     * @param parentY the y coord to draw the texture on
     * @param width the width to ve overridden
     * @param height the height to ve overridden
     * */
    fun drawOn(matrices: MatrixStack, parentX: Int, parentY: Int, width: Int, height: Int) {
        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        MinecraftClient.getInstance().textureManager.bindTexture(id)
        DrawableHelper.drawTexture(
            matrices,
            parentX + paintOffsetX,
            parentY + paintOffsetY,
            captureX.toFloat(),
            captureY.toFloat(),
            width,
            height,
            textureWidth,
            textureHeight
        )
    }
}