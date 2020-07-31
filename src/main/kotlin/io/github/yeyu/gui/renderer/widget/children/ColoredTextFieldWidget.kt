package io.github.yeyu.gui.renderer.widget.children

import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.util.math.MatrixStack

/**
 * A colored text field widget
 * @see AbstractTextFieldWidget
 * */
class ColoredTextFieldWidget(
    relativeX: Int = 0,
    relativeY: Int = 0,
    width: Int = 1,
    height: Int = 1,
    textColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    caretColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val focusedBgColor: Int = DrawerUtil.constructColor(0xFF, 0x0, 0x0, 0x0),
    private val outFocusedBgColor: Int = DrawerUtil.constructColor(0xFF, 0x48, 0x48, 0x48),
    name: String
) : AbstractTextFieldWidget(relativeX, relativeY, width, height, textColor, caretColor, name) {
    override fun drawBackground(
        screen: ScreenRenderer<*>,
        matrices: MatrixStack,
        absX: Int,
        absY: Int
    ) {
        DrawerUtil.coloredRect(
            getDrawX(),
            getDrawY(),
            width,
            height,
            if (isFocused()) focusedBgColor else outFocusedBgColor
        )
    }
}