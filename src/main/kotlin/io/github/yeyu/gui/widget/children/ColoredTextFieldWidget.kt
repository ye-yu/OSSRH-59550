package io.github.yeyu.gui.widget.children

import io.github.yeyu.gui.ScreenRenderer
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.util.math.MatrixStack

class ColoredTextFieldWidget(
    relativeX: Int = 0,
    relativeY: Int = 0,
    width: Int = 1,
    height: Int = 1,
    textColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    caretColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val focusedBgColor: Int = DrawerUtil.constructColor(0xFF, 0x0, 0x0, 0x0),
    private val outFocusedBgColor: Int = DrawerUtil.constructColor(0xFF, 0x48, 0x48, 0x48)
) : AbstractTextFieldWidget(relativeX, relativeY, width, height, textColor, caretColor) {
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