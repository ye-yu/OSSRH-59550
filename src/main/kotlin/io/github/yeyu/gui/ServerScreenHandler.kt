package io.github.yeyu.gui

import io.github.yeyu.packet.PacketActions
import io.github.yeyu.packet.ServerScreenHandlerPacketListener
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

open class ServerScreenHandler(type: ScreenHandlerType<*>, syncId: Int) : ScreenRendererHandler(type, syncId),
    ServerScreenHandlerPacketListener {

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) {
        if (action.equals(PacketActions.init, true)) {
            clientHasInit()
        }
    }

    open fun clientHasInit() {
        Logger.info("Client has init.")
    }
}