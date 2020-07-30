import com.mojang.blaze3d.systems.RenderSystem
import io.github.yeyu.gui.ScreenRenderer
import io.github.yeyu.gui.ScreenRendererHandler
import io.github.yeyu.gui.widget.ParentWidget
import io.github.yeyu.gui.widget.ParentWidget.AnchorType
import io.github.yeyu.gui.widget.parents.InventoryPanel
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class BlockScreenRenderer(handler: ScreenRendererHandler, inventory: PlayerInventory, title: Text?) :
    ScreenRenderer<ScreenRendererHandler>(handler, inventory, title) {

    companion object {
        private val TEXTURE: Identifier = Identifier("textures/gui/container/anvil.png")
        private const val TEXT_FIELD_HEIGHT = 16
        private const val TEXT_FIELD_WIDTH = 110
        private const val TEXTURE_PADDING = 4
        private val OUT_FOCUSED_BG: TextureDrawerHelper = TextureDrawerHelper(
            TEXTURE,
            0, 166 + TEXT_FIELD_HEIGHT,
            TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT,
            -TEXTURE_PADDING, -TEXTURE_PADDING
        )
        private val FOCUSED_BG: TextureDrawerHelper = TextureDrawerHelper(
            TEXTURE,
            0, 166,
            TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT,
            -TEXTURE_PADDING, -TEXTURE_PADDING
        )
    }

    override fun init() {
        super.init()

        val hotbarPanel = InventoryPanel(
            relativeY = 68,
            height = 20,
            width = backgroundWidth - 14,
            inventoryIndex = 0,
            numberOfSlots = 9,
            cols = 9,
            horizontalAnchor = AnchorType.MIDDLE,
            verticalAnchor = AnchorType.MIDDLE
        )

        this.addParent(hotbarPanel)
        this.addListener(hotbarPanel)
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        if (this.client == null) return

        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(Companion.TEXTURE)
        val middleX = (width - backgroundWidth) / 2
        val middleY = (height - backgroundHeight) / 2
        this.drawTexture(matrices, middleX, middleY, 0, 0, backgroundWidth, backgroundHeight)
    }

}