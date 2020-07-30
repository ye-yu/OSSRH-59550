package io.github.yeyu.gui.inventory.interfaces

import net.minecraft.item.ItemStack

interface ClientInventoryInteractionListener : InventoryInteractionListener {
    /**
     * Use from widget to send slot number that is clicked
     * on the client-side.
     *
     * Note: Register slot number into a set object and make
     * sure the mouse listener call [onSlotRelease] on mouse up.
     */
    fun onSlotClick(slotNumber: Int, button: Int, shiftDown: Boolean)

    /**
     * Note: Call this on mouse up from each listener.
     */
    fun onSlotRelease(slotNumber: Int)

    // do everything below here belong here? idk
    /**
     * For client widgets to get the pre-calculated stack
     * while dragging item slots
     * */
    fun getCalculatedStack(slotNumber: Int): ItemStack

    /**
     * For client widgets to check the pre-calculated stack
     * while dragging item slots
     * */
    fun hasCalculatedStack(slotNumber: Int): Boolean

    /**
     * For client widgets to check the current stack
     * */
    fun hasStack(slotNumber: Int): Boolean
}
