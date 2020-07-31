package io.github.yeyu.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.yeyu.gui.renderer.ScreenRenderer
import net.minecraft.client.MinecraftClient
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
    private val paintOffsetY: Int = 0
) {

    /**
     * @param renderer the screen renderer that provides `drawTexture` method
     * @param matrices the matrices to draw texture on
     * @param parentX the x coord to draw the texture on
     * @param parentY the y coord to draw the texture on
     * */
    fun drawOn(renderer: ScreenRenderer<*>, matrices: MatrixStack?, parentX: Int, parentY: Int) {
        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        MinecraftClient.getInstance().textureManager.bindTexture(id)
        renderer.drawTexture(
            matrices,
            parentX + paintOffsetX,
            parentY + paintOffsetY,
            captureX,
            captureY,
            width,
            height
        )
    }
}