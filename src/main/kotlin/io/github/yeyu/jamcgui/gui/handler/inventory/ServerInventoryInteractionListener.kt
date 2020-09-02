package io.github.yeyu.jamcgui.gui.handler.inventory

import io.github.yeyu.jamcgui.gui.handler.listener.InventoryInteractionListener

interface ServerInventoryInteractionListener :
    InventoryInteractionListener {
    /**
     * Parses the single slot number from the client-side.
     */
    fun onSingleSlotClickEvent(slotNumber: Int, button: Int, hasShiftDown: Boolean)

    /**
     * Use to receive the multiple slot action from the client-side.
     */
    fun onMultipleSlotClickEvent(
        clickedSlots: LinkedHashSet<Int>,
        button: Int,
        hasShiftDown: Boolean
    )
}