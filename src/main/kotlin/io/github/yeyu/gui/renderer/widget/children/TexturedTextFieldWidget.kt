package io.github.yeyu.gui.renderer.widget.children

import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.util.DrawerUtil
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack

/**
 * A textured text field widget
 *
 * @see AbstractTextFieldWidget
 * */
class TexturedTextFieldWidget(
    relativeX: Int = 0,
    relativeY: Int = 0,
    width: Int = 1,
    height: Int = 1,
    textColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    caretColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val focusedTexture: TextureDrawerHelper,
    private val outFocusedTexture: TextureDrawerHelper,
    name: String
) : AbstractTextFieldWidget(relativeX, relativeY, width, height, textColor, caretColor, name) {
    override fun drawBackground(
        screen: ScreenRenderer<*>,
        matrices: MatrixStack,
        absX: Int,
        absY: Int
    ) {
        (if (isFocused()) focusedTexture else outFocusedTexture).drawOn(screen, matrices, absX, absY)
    }
}