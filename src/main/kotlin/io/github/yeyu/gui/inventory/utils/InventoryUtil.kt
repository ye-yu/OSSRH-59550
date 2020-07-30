package io.github.yeyu.gui.inventory.utils

import com.google.common.base.Preconditions
import io.github.yeyu.gui.ClickEvent
import net.minecraft.item.ItemStack
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.collections.HashMap

object InventoryUtil {
    /**
     * Calculates distribution of items from
     * drag action. Effective when <tt>draggedSlot.size() > 1</tt>
     *
     * @param calculatedStack  to be overwritten with the expected item at each stack
     * @param clickedSlots     a list of indices of the clicked slot
     * @param clickEvent       a click event object
     * @param constrainedSlots a list of slots
     * @param sourceStack      the item to be distributed
     * @param cursorStack      the current item at cursor
     * @return stacks that should be in the cursor slot
     */
    fun calculateDistributedSlots(
        calculatedStack: HashMap<Int, ItemStack>,
        clickedSlots: Collection<Int>,
        clickEvent: ClickEvent,
        constrainedSlots: List<CapacityConstrainedSlot>,
        sourceStack: ItemStack,
        cursorStack: ItemStack
    ): ItemStack {
        calculatedStack.clear()
        if (clickedSlots.size < 2) return cursorStack
        // do distribution calculation
        val action =
            SlotActionType.getActionFromClickEvent(
                clickEvent,
                sourceStack,
                null,
                true
            )
        return when (action) {
            SlotActionType.DISTRIBUTE_ALL -> {
                val compatibleSlotIndex = clickedSlots.stream()
                    .filter { i: Int? ->
                        constrainedSlots[i!!].canInsert(sourceStack)
                    }
                    .filter { i: Int? -> !constrainedSlots[i!!].isFull }
                    .collect(Collectors.toList())
                val count = sourceStack.count
                val size = count.coerceAtMost(compatibleSlotIndex.size)
                val div = Math.floorDiv(count, size)
                val mod = Math.floorMod(count, size)
                var overflows = 0
                var countdown = size
                for (index in compatibleSlotIndex) {
                    if (countdown-- == 0) break
                    val slot = constrainedSlots[index!!]
                    val copy = sourceStack.copy()
                    val remainingInserts = slot.getCapacity() - slot.stack.count
                    val overflow = div - remainingInserts
                    if (overflow > 0) {
                        copy.count = slot.getCapacity()
                        overflows += overflow
                    } else {
                        copy.count = slot.stack.count + div
                    }
                    calculatedStack[index] = copy
                }
                val expectedCursorStack = sourceStack.copy()
                expectedCursorStack.count = mod + overflows
                expectedCursorStack
            }
            SlotActionType.DISTRIBUTE_ONE -> {
                var count = sourceStack.count
                for (index in clickedSlots) {
                    if (!constrainedSlots[index].canInsert(sourceStack)) continue
                    if (constrainedSlots[index].isFull) continue  // is full, cannot insert anymore
                    val copy: ItemStack
                    if (!constrainedSlots[index].stack.isEmpty) {
                        copy = constrainedSlots[index].stack.copy()
                        copy.increment(1)
                    } else {
                        copy = sourceStack.copy()
                        copy.count = 1
                    }
                    calculatedStack[index] = copy
                    count--
                    if (count == 0) return ItemStack.EMPTY
                }

                // there exists remaining item;
                val expectedCursorStack = sourceStack.copy()
                expectedCursorStack.count = count
                expectedCursorStack
            }
            else -> ItemStack.EMPTY
        }
    }

    /**
     * @param calculatedStack to be overwritten with the stack that should be merged with the mapped slot
     * @param referenceSlot   the list of slots
     * @param slotNumber      the slot number that contains the stack to be transferred
     * @param targetIndex     the first index of the target slots
     * @param targetSize      the size of the target slots
     * @param forward         true to fill in the stack forward
     * @return ItemStack that should be in the current slot
     */
    fun calculateQuickMove(
        calculatedStack: HashMap<Int, ItemStack>,
        referenceSlot: List<CapacityConstrainedSlot>,
        slotNumber: Int,
        targetIndex: Int,
        targetSize: Int,
        forward: Boolean
    ): ItemStack {
        val targetStack = referenceSlot[slotNumber].stack
        Preconditions.checkArgument(targetSize > 0)
        Preconditions.checkArgument(targetIndex >= 0)
        if (targetStack.isEmpty) return ItemStack.EMPTY
        if (targetSize == 1) return targetStack
        calculatedStack.clear()
        val rangeStream = IntStream.range(0, targetSize)
        val indices =
            (if (forward) rangeStream.map { i: Int -> targetIndex + i } else rangeStream.map { i: Int -> targetIndex + targetSize - i - 1 }).toArray()
        val canInsert = IntStream.of(*indices).filter { i: Int ->
            !referenceSlot[i].isEmpty && referenceSlot[i].canInsert(targetStack)
        }.toArray()
        val emptySlots = IntStream.of(*indices).filter { i: Int ->
            referenceSlot[i].isEmpty && referenceSlot[i].canInsert(targetStack)
        }.toArray()
        val toInsert = targetStack.copy()
        for (i in canInsert) { // insert to slots that can be inserted
            val slot = referenceSlot[i]
            val capacity = slot.getCapacity()
            val currentCount = slot.stack.count
            val remainingInserts = capacity - currentCount
            val incomingCount = toInsert.count
            if (remainingInserts == 0) continue  // is full
            if (incomingCount - remainingInserts > 0) { // overflows
                toInsert.decrement(remainingInserts)
                val copy = toInsert.copy()
                copy.count = remainingInserts // because overflown, can only add as much as remainingInserts
                calculatedStack[i] = copy
            } else { // can fit inside the slot perfectly, return
                calculatedStack[i] = toInsert
                return ItemStack.EMPTY
            }
            if (toInsert.isEmpty) return ItemStack.EMPTY // fallback, the return statement should never be reached
        }

        // alright there is not enough merge-able slot, lets just put it in empty slots
        for (i in emptySlots) {
            if (i == slotNumber) continue  // oops don't insert to current slot yet
            val slot = referenceSlot[i]
            val capacity = slot.getCapacity()
            if (toInsert.count > capacity) {
                val copy = toInsert.copy()
                copy.count = capacity
                calculatedStack[i] = copy
                toInsert.decrement(capacity)
            } else {
                calculatedStack[i] = toInsert
                return ItemStack.EMPTY
            }
        }
        return toInsert
    }
}