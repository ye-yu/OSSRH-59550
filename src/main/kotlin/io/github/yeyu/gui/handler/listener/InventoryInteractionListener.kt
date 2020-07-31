package io.github.yeyu.gui.handler.listener

import io.github.yeyu.gui.renderer.widget.ClickEvent
import net.minecraft.item.ItemStack

interface InventoryInteractionListener {
    /**
     * The calculated space before finalising
     * to slots
     * @see io.github.yeyu.gui.handler.provider.InventoryProvider.constrainedSlots
     * */
    val calculatedStack: HashMap<Int, ItemStack>

    /**
     * The set of clicked slots
     * */
    val clickedSlots: LinkedHashSet<Int>

    /**
     * The clicked item
     * */
    var clickedItem: ItemStack
    var clickEvent: ClickEvent

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