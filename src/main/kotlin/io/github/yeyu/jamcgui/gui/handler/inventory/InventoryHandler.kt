package io.github.yeyu.jamcgui.gui.handler.inventory

import io.github.yeyu.jamcgui.gui.handler.inventory.utils.CapacityConstrainedSlot
import io.github.yeyu.jamcgui.gui.handler.inventory.utils.InventoryType
import io.github.yeyu.jamcgui.gui.renderer.widget.ClickEvent
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import java.util.*

interface InventoryHandler {
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
     * The calculated space before finalising
     * to constrained slots
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

    /**
     * Mouse down event describing a part of a slot action
     * */
    var clickEvent: ClickEvent


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

    /**
     * Initialise block/other inventory into the list of slots
     *
     * Note: Broadcast block inventory size and content to client
     * because client does not have access to the inventory source
     * @see constrainedSlots
     * */
    fun initBlockInventory(blockInv: Inventory)

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
