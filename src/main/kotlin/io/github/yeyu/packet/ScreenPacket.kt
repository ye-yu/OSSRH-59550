package io.github.yeyu.packet

import io.github.yeyu.util.Logger
import io.github.yeyu.Properties
import io.github.yeyu.util.Classes
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object ScreenPacket {

    val S2CID = Identifier(Properties.NAMESPACE, "s2c")
    val C2SID = Identifier(Properties.NAMESPACE, "c2s")

    private fun createWrappedPacket(syncId: Int, action: String): PacketByteBuf {
        val wrappedBuf = PacketByteBuf(Unpooled.buffer())
        wrappedBuf.writeInt(syncId)
        wrappedBuf.writeString(action)
        return wrappedBuf
    }

    fun onClient2Server(context: PacketContext, buf: PacketByteBuf) {
        val syncId = buf.readInt()
        val action = buf.readString()
        val currentScreenHandler = context.player.currentScreenHandler
        if (currentScreenHandler.syncId != syncId) {
            Logger.error("Sync ID is no longer in-sync with the server. Expected ${currentScreenHandler.syncId} but got $syncId instead.", Throwable())
            return
        }

        Classes.runUnsafe(currentScreenHandler, ServerScreenHandlerPacketListener::class, "Handler cannot parse custom screen packet.") {
            it.onClient2Server(action, context, buf)
        }
    }

    fun onServer2Client(context: PacketContext, buf: PacketByteBuf) {
        val syncId = buf.readInt()
        val action = buf.readString()
        val currentScreen = MinecraftClient.getInstance().currentScreen
        if (currentScreen == null) {
            Logger.error("Got packet from server but client screen is null. Did client send an init packet beforehand?", Throwable())
            return
        }

        val screenHandler = Classes.getUnsafe(currentScreen, HandledScreen::class.java, null) { it.screenHandler }
        if (screenHandler == null) {
            Logger.error("Current screen is not a handled screen. Programming error?", Throwable())
            return
        }

        if (screenHandler.syncId != syncId) {
            Logger.error("Sync ID is no longer in-sync with the client. Expected ${screenHandler.syncId} but got $syncId instead.", Throwable())
            return
        }

        Classes.runUnsafe(screenHandler, ClientScreenHandlerPacketListener::class, "Handler cannot parse custom screen packet.") {
            it.onServer2Client(action, context, buf)
        }
    }

    fun sendPacket(syncId: Int, action: String, toServer: Boolean, player: ServerPlayerEntity?, bufferWrapper: (PacketByteBuf) -> Unit) {
        val buf = createWrappedPacket(syncId, action)
        bufferWrapper(buf)
        if (toServer) {
            ClientSidePacketRegistryImpl.INSTANCE.sendToServer(C2SID, buf)
        } else {
            requireNotNull(player)
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, S2CID, buf)
        }
    }
}