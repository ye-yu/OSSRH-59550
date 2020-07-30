package io.github.yeyu.gui.inventory.interfaces

import io.github.yeyu.gui.ClickEvent
import net.minecraft.item.ItemStack

interface InventoryInteractionListener {
    val calculatedStack: HashMap<Int, ItemStack>
    val clickedSlots: LinkedHashSet<Int>
    var clickedItem: ItemStack
    var clickEvent: ClickEvent

    /**
     * Sends update packet from server to client on the server-side, or
     * updates inventory content from the received packet.
     */
    fun onSlotChanged(slotNumber: Int, itemStack: ItemStack)

    fun onItemThrow(slotNumber: Int, all: Boolean)
}