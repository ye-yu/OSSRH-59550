package io.github.yeyu.gui.handler.inventory

import net.minecraft.item.ItemStack

/**
 * Provider interface to give inventory information
 * */
interface InventoryProvider: InventoryHandler {
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