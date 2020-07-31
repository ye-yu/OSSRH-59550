package handler

import GuiBlockEntity
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

class ServerInventoryHandlerImpl<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    sideInventory: Inventory,
    private val blockEntity: GuiBlockEntity
) : ServerInventoryHandler<T>(type, syncId, playerInventory) {

    init {
        super.blockInventory = sideInventory
    }

    override fun clientHasInit() {
        super.clientHasInit()
        ScreenPacket.sendPacket(
            syncId, Packets.STORED_TEXT_UPDATE, false,
            playerInventory.player as ServerPlayerEntity?
        ) {
            it.writeString(blockEntity.storedText)
        }
    }

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onClient2Server(action, context, buf)
        if (action.equals(Packets.STORED_TEXT_UPDATE, true)) {
            blockEntity.storedText = buf.readString()
        }
    }
}