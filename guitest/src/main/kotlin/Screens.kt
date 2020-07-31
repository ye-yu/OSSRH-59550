import handler.ClientInventoryHandlerImpl
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object Screens {
    var blockScreen: ScreenHandlerType<ScreenRendererHandler>? = null


    fun registerServer() {
        blockScreen = ScreenHandlerRegistry.registerSimple<ScreenRendererHandler>(
            Identifier("testgui", "blockgui")
        ) { syncId: Int, playerInv: PlayerInventory ->
            ClientInventoryHandlerImpl(blockScreen!!, syncId, playerInv)
        }
    }

    fun registerClient() {
        ScreenRegistry.register<ScreenRendererHandler, BlockScreenRenderer>(
            blockScreen
        ) { screenRendererHandler, playerInventory, text ->
            BlockScreenRenderer(screenRendererHandler, playerInventory, text)
        }
    }
}