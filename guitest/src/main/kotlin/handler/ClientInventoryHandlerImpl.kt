package handler

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.listener.StringListener
import io.github.yeyu.gui.handler.provider.StringProvider
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

class ClientInventoryHandlerImpl<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(type, syncId, playerInventory), StringListener, StringProvider {

    var storedText: String = ""

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onServer2Client(action, context, buf)
        if (action.equals(Packets.STORED_TEXT_UPDATE, true)) {
            storedText = buf.readString()
        }
    }

    override fun onStringChange(str: String, name: String) {
        if (name.equals("storedText", true)) {
            storedText = str
            ScreenPacket.sendPacket(syncId, Packets.STORED_TEXT_UPDATE, true, null) {
                it.writeString(str)
            }
        }
    }

    override fun getString(name: String): String {
        if (name.equals("storedText", true)) {
            return storedText
        }
        throw IllegalArgumentException("Handler does not accept field named $name.")
    }
}