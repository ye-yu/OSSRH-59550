package io.github.yeyu.gui.handler

import io.github.yeyu.packet.ClientScreenHandlerPacketListener
import io.github.yeyu.packet.PacketActions
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

/**
 * Client screen handler
 *
 * Note: Register this class in the fabric 'main' mod initializer
 * */
open class ClientScreenHandler(type: ScreenHandlerType<*>, syncId: Int) : ScreenRendererHandler(type, syncId),
    ClientScreenHandlerPacketListener {

    protected var isListeningEvents = true

    /**
     * Irrelevant method as it is called in server side only
     * */
    final override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
    }

    /**
     * Sends init packet to notify server that the client
     * has opened the screen
     * */
    fun sendInitPacket() {
        ScreenPacket.sendPacket(syncId, PacketActions.init, true, null) {}
    }
}