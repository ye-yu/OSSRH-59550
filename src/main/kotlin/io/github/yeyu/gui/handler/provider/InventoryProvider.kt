package io.github.yeyu.gui.handler.provider

import io.github.yeyu.gui.handler.inventory.utils.CapacityConstrainedSlot
import io.github.yeyu.gui.handler.inventory.utils.InventoryType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import java.util.*

interface InventoryProvider {
    /**
     * A list of slots handled by the screen handler
     * */
    val constrainedSlots: ArrayList<CapacityConstrainedSlot>

    /**
     * The player inventory instance for the screen handler
     * */
    val playerInventory: PlayerInventory

    /**
     * The block/other inventory instance for the screen handler
     *
     * Note: Broadcast block inventory size and content to client
     * because client does not have access to the inventory source
     * */
    var blockInventory: Inventory? // init

    /**
     * @return the stack from the list of slots
     * @see constrainedSlots
     * */
    fun getStack(slot: Int): ItemStack

    /**
     * Initialise player inventory into the list of slots
     * @see constrainedSlots
     * */
    fun initPlayerInventory()

    // both client side and server needs to init block inventory
    // but it seems like client does it on packet received
    // review?
    /**
     * Initialise block/other inventory into the list of slots
     *
     * Note: Broadcast block inventory size and content to client
     * because client does not have access to the inventory source
     * @see constrainedSlots
     * */
    fun initBlockInventory()

    /**
     * @return cursor stack
     * */
    fun getCursorStack(): ItemStack

    /**
     * @return the inventory type based on the given slot index
     * @see InventoryType
     * */
    fun getInventoryOfIndex(index: Int): InventoryType

    /**
     * @return the size of the inventory based on the given
     * inventory type
     * */
    fun getInventorySize(inventoryType: InventoryType): Int

    /**
     * @return the total size of the slots
     * */
    fun getSize(): Int
}
