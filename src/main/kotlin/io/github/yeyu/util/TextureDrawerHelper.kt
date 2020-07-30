package io.github.yeyu.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.yeyu.gui.ScreenRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class TextureDrawerHelper(
    private val id: Identifier,
    private val captureX: Int,
    private val captureY: Int,
    private val width: Int,
    private val height: Int,
    private val paintOffsetX: Int,
    private val paintOffsetY: Int
) {
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