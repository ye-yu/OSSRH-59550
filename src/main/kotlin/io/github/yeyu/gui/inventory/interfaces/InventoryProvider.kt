package io.github.yeyu.gui.inventory.interfaces

import io.github.yeyu.gui.inventory.utils.CapacityConstrainedSlot
import io.github.yeyu.gui.inventory.utils.InventoryType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import java.util.ArrayList

interface InventoryProvider {
    val constrainedSlots: ArrayList<CapacityConstrainedSlot>
    val playerInventory: PlayerInventory
    var blockInventory: Inventory? // init

    fun getStack(slot: Int): ItemStack
    fun initPlayerInventory()

    // both client side and server needs to init block inventory
    // but it seems like client does it on packet received
    // review?
    fun initBlockInventory()
    fun getCursorStack(): ItemStack
    fun getInventoryOfIndex(index: Int): InventoryType
    fun getInventorySize(inventoryType: InventoryType): Int
    fun getSize(): Int
}
