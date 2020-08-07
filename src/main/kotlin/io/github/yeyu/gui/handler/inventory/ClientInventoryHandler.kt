package io.github.yeyu.gui.handler.inventory

import io.github.yeyu.gui.handler.ClientScreenHandler
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.utils.CapacityConstrainedSlot
import io.github.yeyu.gui.handler.inventory.utils.InventoryType
import io.github.yeyu.gui.handler.inventory.utils.InventoryUtil
import io.github.yeyu.gui.handler.inventory.utils.InventoryUtil.readInventory
import io.github.yeyu.gui.handler.inventory.utils.SlotActionType
import io.github.yeyu.gui.renderer.widget.ClickEvent
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import java.util.*
import java.util.stream.IntStream

/**
 * Implemented client inventory handler
 *
 * Note: Extends to update the block inventory
 * on server block inventory update packet
 * */
abstract class ClientInventoryHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>, syncId: Int, override val playerInventory: PlayerInventory
) : ClientScreenHandler(type, syncId),
    ClientInventoryInteractionListener, InventoryHandler, InventoryProvider {

    var pauseUpdateListening = false

    override var blockInventory: Inventory? = SimpleInventory(0)

    // from InventoryInteractionListener
    override val constrainedSlots: ArrayList<CapacityConstrainedSlot> = ArrayList()

    override var clickedSlots: LinkedHashSet<Int> = LinkedHashSet()
    override val calculatedStack: HashMap<Int, ItemStack> = HashMap()
    override var clickEvent: ClickEvent =
        ClickEvent(-1, false)
    override var clickedItem: ItemStack = ItemStack.EMPTY
    override fun getStack(slot: Int): ItemStack {
        return constrainedSlots[slot].stack
    }

    init {
        initPlayerInventory()
    }

    final override fun initPlayerInventory() {
        IntStream.range(0, playerInventory.size()).forEach { i: Int ->
            constrainedSlots.add(
                CapacityConstrainedSlot(
                    playerInventory,
                    i,
                    i
                )
            )
        }
    }

    override fun initBlockInventory(blockInv: Inventory) {
        IntStream.range(playerInventory.size(), playerInventory.size() + blockInv.size())
            .forEach { k: Int ->
                val i = k - playerInventory.size()
                constrainedSlots.add(CapacityConstrainedSlot(blockInv, i, k))
            }
    }

    override fun getCursorStack(): ItemStack {
        return playerInventory.cursorStack
    }

    override fun getInventoryOfIndex(index: Int): InventoryType {
        if (index in 0..8) return InventoryType.PLAYER_HOTBAR
        if (index in 9..35) return InventoryType.PLAYER_INVENTORY
        if (36 <= index && index < playerInventory.size()) return InventoryType.PLAYER_EQUIPMENT_SLOT
        if (index < playerInventory.size() + blockInventory!!.size()) return InventoryType.BLOCK
        throw IndexOutOfBoundsException(
            String.format(
                "Index must be in-between 0 <= index < %d",
                playerInventory.size() + blockInventory!!.size()
            )
        )
    }

    override fun getInventorySize(inventoryType: InventoryType): Int {
        return if (inventoryType == InventoryType.BLOCK) {
            if (blockInventory == null) 0
            else blockInventory!!.size()
        } else {
            playerInventory.size()
        }
    }

    override fun onSlotClick(slotNumber: Int, button: Int, shiftDown: Boolean) {
        pauseUpdateListening = true
        if (!isListeningEvents) return
        val cursorStack = playerInventory.cursorStack
        val targetStack = constrainedSlots[slotNumber].stack
        if (clickedSlots.size < 1) {
            val clickEvent: ClickEvent = ClickEvent.of(button, shiftDown)
            val action: SlotActionType =
                SlotActionType.getActionFromClickEvent(clickEvent, cursorStack, targetStack, false)
            if (action === SlotActionType.PICKUP_ALL || action === SlotActionType.PICKUP_HALF || action === SlotActionType.CLONE) {
                ScreenPacket.sendPacket(syncId, InventoryPacket.SLOT_CLICK_SIMPLE, true, null) { wrappedBuf ->
                    wrappedBuf.writeInt(slotNumber)
                    wrappedBuf.writeInt(button)
                    wrappedBuf.writeBoolean(shiftDown)
                }
                isListeningEvents = false
            } else {
                this.clickEvent = clickEvent
                clickedItem = cursorStack
                clickedSlots.add(slotNumber)
            }
        } else {
            val action: SlotActionType =
                SlotActionType.getActionFromClickEvent(clickEvent, clickedItem, null, true)
            if (action === SlotActionType.DRAG_EMPTY || action === SlotActionType.NONE) return
            clickedSlots.add(slotNumber)
            val newCursorStack: ItemStack = InventoryUtil.calculateDistributedSlots(
                calculatedStack,
                clickedSlots,
                clickEvent,
                constrainedSlots,
                clickedItem,
                playerInventory.cursorStack
            )
            playerInventory.cursorStack = newCursorStack
        }
    }

    override fun onSlotRelease(slotNumber: Int) {
        pauseUpdateListening = false
        isListeningEvents = true
        if (clickedSlots.isEmpty()) return
        if (clickedSlots.size == 1) {
            val slot = clickedSlots.stream().findFirst().get()
            ScreenPacket.sendPacket(syncId, InventoryPacket.SLOT_CLICK_SIMPLE, true, null) { wrappedBuf ->
                wrappedBuf.writeInt(slot)
                wrappedBuf.writeInt(clickEvent.button)
                wrappedBuf.writeBoolean(clickEvent.hasShiftDown)
            }
        } else {
            ScreenPacket.sendPacket(syncId, InventoryPacket.SLOT_CLICK_MULTIPLE, true, null) { wrappedBuf ->
                wrappedBuf.writeInt(clickEvent.button)
                wrappedBuf.writeBoolean(clickEvent.hasShiftDown)
                wrappedBuf.writeInt(clickedSlots.size)
                clickedSlots.forEach { i: Int -> wrappedBuf.writeInt(i) }
            }
        }
        clickedSlots.clear()
    }

    override fun onItemThrow(slotNumber: Int, all: Boolean) {
        ScreenPacket.sendPacket(syncId, InventoryPacket.THROW_ITEM, true, null) {
            it.writeInt(slotNumber)
            it.writeBoolean(all)
        }
    }

    override fun getCalculatedStack(slotNumber: Int): ItemStack {
        return calculatedStack.getOrDefault(slotNumber, getStack(slotNumber))
    }

    override fun hasCalculatedStack(slotNumber: Int): Boolean {
        return calculatedStack.containsKey(slotNumber)
    }

    override fun hasStack(slotNumber: Int): Boolean {
        return slotNumber < constrainedSlots.size
    }

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onServer2Client(action, context, buf)
        when {
            action.equals(InventoryPacket.SLOT_UPDATE, true) -> {
                val slotNumber: Int = buf.readInt()
                val itemStack: ItemStack = buf.readItemStack()
                onSlotChanged(slotNumber, itemStack)
            }
            action.equals(InventoryPacket.BLOCK_INV_UPDATE, true) -> {
                blockInventory = buf.readInventory()
                initBlockInventory(blockInventory!!)
            }
            action.equals(InventoryPacket.CURSOR_UPDATE, true) -> {
                if (pauseUpdateListening) return
                val cursorStack: ItemStack = buf.readItemStack()
                playerInventory.cursorStack = cursorStack
            }
        }
    }

    /**
     * Updates current slot from packet sent by server
     * */
    override fun onSlotChanged(slotNumber: Int, itemStack: ItemStack) {
        // update slot
        if (pauseUpdateListening) return
        constrainedSlots[slotNumber].stack = itemStack
        calculatedStack.remove(slotNumber) // entry in calculated stack is now obsolete
    }

    override fun getSize(): Int {
        return if (blockInventory != null) {
            playerInventory.size() + blockInventory!!.size()
        } else {
            playerInventory.size()
        }
    }
}