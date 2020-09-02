package io.github.yeyu.jamcgui

import io.github.yeyu.jamcgui.packet.ScreenPacket
import io.github.yeyu.jamcgui.util.Logger
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl

class MCGuiMain : ModInitializer, ClientModInitializer {

    override fun onInitialize() {
        ServerSidePacketRegistryImpl.INSTANCE.register(ScreenPacket.C2SID, ScreenPacket::onClient2Server)
        Logger.info("Jamcgui server screen can now send screen packets to client.")
    }

    override fun onInitializeClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(ScreenPacket.S2CID, ScreenPacket::onServer2Client)
        Logger.info("Jamcgui client screen can now send screen packets to server.")
    }
}