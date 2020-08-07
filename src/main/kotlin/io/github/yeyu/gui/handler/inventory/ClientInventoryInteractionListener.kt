package io.github.yeyu.gui.handler.inventory

import io.github.yeyu.gui.handler.listener.InventoryInteractionListener

interface ClientInventoryInteractionListener :
    InventoryInteractionListener {
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
}
