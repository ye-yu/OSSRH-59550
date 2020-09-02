package io.github.yeyu.jamcgui.gui.handler.inventory.utils

import com.google.common.base.Preconditions
import io.github.yeyu.jamcgui.util.Classes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import kotlin.math.min

/**
 * A slot that has a capacity constraint
 * less than or equal to the original item
 * max capacity
 * */
class CapacityConstrainedSlot(inventory: Inventory, index: Int, private val slotNumber: Int) :
    Slot(inventory, index, 0, 0) {
    private var stackCapacity = 64 // default max stack size
    var takePredicate: (PlayerEntity) -> Boolean = { true }
    var insertPredicate: (ItemStack) -> Boolean = { true }

    /**
     * @return the remaining stack that exceeds the capacity
     */
    fun insertItem(item: ItemStack): ItemStack {
        val capacity = min(item.item.maxCount, getCapacity())
        if (!canInsert(item)) return item
        val currentStack = stack
        return if (currentStack.isEmpty) {
            if (item.count > capacity) {
                val toInsert = item.copy()
                toInsert.count = capacity
                stack = toInsert
                item.decrement(capacity)
                item
            } else {
                stack = item
                ItemStack.EMPTY
            }
        } else {
            if (currentStack.count >= capacity) return item // current stack is already full
            val remaining = getCapacity() - currentStack.count
            val toInsert = remaining.coerceAtMost(item.count)
            currentStack.increment(toInsert)
            item.decrement(toInsert)
            item
        }
    }

    /**
     * @return true if the incoming stack can be inserted
     * regardless of slot capacity
     * @see canCombine
     */
    override fun canInsert(stack: ItemStack): Boolean { // todo: check item cannot be inserted when swapping
        if (getStack().isEmpty) return insertPredicate(stack)
        if (stack.isEmpty) return insertPredicate(stack)
        if (!canCombine(stack)) return false
        val sidedCanInsert = Classes.getUnsafe(inventory, SidedInventory::class, true) {
            it.canInsert(slotNumber, stack, null)
        }
        return if (!sidedCanInsert) false
        else insertPredicate(stack)
    }

    /**
     * @return true if the incoming stack can be combined
     * with the current stack
     * @see .canInsert
     */
    fun canCombine(stack: ItemStack): Boolean {
        return getStack().item === stack.item && ItemStack.areTagsEqual(getStack(), stack)
    }

    /**
     * Force set stack at current slot.
     * */
    override fun setStack(stack: ItemStack) {
        @Suppress("DEPRECATION")
        setStack(stack, true)
    }

    /**
     * Provides checking when before item is inserted into
     * the slot.
     *
     * @throws IllegalArgumentException when the incoming stack cannot be inserted into the slot while <tt>force == true</tt>
     * @throws IllegalStateException    when the slot capacity is exceeded while <tt>force == true</tt>
     */
    @Deprecated("Logic error when `force` == false.")
    fun setStack(stack: ItemStack, force: Boolean) {
        if (!force) {
            require(canInsert(stack)) { "Cannot insert into slot!" } // todo: check logic
            check(stack.count <= stackCapacity) { "Slot capacity is exceeded!" }
        }
        super.setStack(stack)
    }

    /**
     * Clear current item
     */
    fun clear(): ItemStack {
        val currentStack = stack
        super.setStack(ItemStack.EMPTY)
        return currentStack
    }

    fun getCapacity(): Int {
        return stackCapacity
    }

    fun setCapacity(capacity: Int) {
        Preconditions.checkArgument(capacity <= stack.item.maxCount)
        this.stackCapacity = capacity
    }

    val isFull: Boolean
        get() {
            val capacity = stack.item.maxCount.coerceAtMost(getCapacity())
            return capacity == stack.count
        }

    val isEmpty: Boolean
        get() = stack.isEmpty

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        if (takePredicate(playerEntity)) return super.canTakeItems(playerEntity)
        return false
    }
}