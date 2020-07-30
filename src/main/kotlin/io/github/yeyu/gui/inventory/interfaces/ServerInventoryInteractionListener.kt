package io.github.yeyu.gui.inventory.interfaces

interface ServerInventoryInteractionListener : InventoryInteractionListener {
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