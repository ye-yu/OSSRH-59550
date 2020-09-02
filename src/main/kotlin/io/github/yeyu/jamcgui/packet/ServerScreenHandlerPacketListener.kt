package io.github.yeyu.jamcgui.packet

import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf

/**
 * Note: Implement on server screen handler
 * */
interface ServerScreenHandlerPacketListener {
    /**
     * Parses action type and execute relevant method based on the action type
     * @param action the action identifier
     * */
    fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf)
}