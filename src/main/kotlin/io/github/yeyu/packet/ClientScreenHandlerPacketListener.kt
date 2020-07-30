package io.github.yeyu.packet

import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf

/**
 * @implNote Implement on server screen handler
 * */
interface ClientScreenHandlerPacketListener {
    fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf);
}