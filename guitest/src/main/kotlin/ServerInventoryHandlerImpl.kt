import io.github.yeyu.gui.ScreenRendererHandler
import io.github.yeyu.gui.inventory.ServerInventoryHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.ScreenHandlerType

class ServerInventoryHandlerImpl<T: ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    sideInventory: Inventory
) : ServerInventoryHandler<T>(type, syncId, playerInventory) {

    init {
        super.blockInventory = sideInventory
    }
}