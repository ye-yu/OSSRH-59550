import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList

class GuiBlockEntity() : BlockEntity(TestGui.GUI_BLOCK_ENTITY), ImplementedInventory, NamedScreenHandlerFactory {
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(4, ItemStack.EMPTY)

    override fun markDirty() {
        // does nothing
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ServerInventoryHandlerImpl(Screens.blockScreen!!, syncId, inv, this)
    }

    override fun getDisplayName(): Text {
        return LiteralText("container.custom.test")
    }


}
