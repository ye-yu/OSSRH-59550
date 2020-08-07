package io.github.yeyu.gui.handler.listener

import net.minecraft.item.ItemStack

interface InventoryInteractionListener {

    /**
     * Sends update packet from server to client on the server-side, or
     * updates inventory content from the received packet.
     */
    fun onSlotChanged(slotNumber: Int, itemStack: ItemStack)

    /**
     * Sends item throw packet to server
     * */
    fun onItemThrow(slotNumber: Int, all: Boolean)
}