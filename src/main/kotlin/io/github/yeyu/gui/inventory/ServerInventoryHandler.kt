package io.github.yeyu.gui.inventory

import io.github.yeyu.gui.ClickEvent
import io.github.yeyu.gui.ScreenRendererHandler
import io.github.yeyu.gui.ServerScreenHandler
import io.github.yeyu.gui.inventory.interfaces.InventoryProvider
import io.github.yeyu.gui.inventory.interfaces.ServerInventoryInteractionListener
import io.github.yeyu.gui.inventory.utils.CapacityConstrainedSlot
import io.github.yeyu.gui.inventory.utils.InventoryType
import io.github.yeyu.gui.inventory.utils.InventoryUtil
import io.github.yeyu.gui.inventory.utils.SlotActionType
import io.github.yeyu.gui.inventory.utils.SlotActionType.*
import io.github.yeyu.packet.ScreenPacket
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet


/**
 * Extend to initialize block inventory
 */
abstract class ServerInventoryHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    final override val playerInventory: PlayerInventory
) : ServerScreenHandler(type, syncId), ServerInventoryInteractionListener, InventoryProvider {

    // states
    var blockInventoryWaiting = false
    var clientHasInited = false

    // from inventory provider
    override var blockInventory: Inventory? = null
        set(value) {
            require(blockInventory == null) { "Can only initialize block inventory once!" }
            field = value

            Logger.info("Initialising block inventory of size ${field!!.size()}")
            IntStream.range(playerInventory.size(), playerInventory.size() + blockInventory!!.size())
                .forEach { i: Int ->
                    constrainedSlots.add(
                        CapacityConstrainedSlot(
                            blockInventory!!,
                            i - playerInventory.size(),
                            i
                        )
                    )
                }
            if (!clientHasInited) {
                blockInventoryWaiting = true
            } else {
                initBlockInventory()
            }
        }

    // from InventoryInteractionListener
    override val constrainedSlots: ArrayList<CapacityConstrainedSlot> = ArrayList()
    override var clickedSlots: LinkedHashSet<Int> = LinkedHashSet()
    override val calculatedStack: HashMap<Int, ItemStack> = HashMap()
    override var clickEvent: ClickEvent = ClickEvent(-1, false)
    override var clickedItem: ItemStack = ItemStack.EMPTY

    init {
        initPlayerInventory()
    }

    override fun getCursorStack(): ItemStack {
        return playerInventory.cursorStack
    }

    override fun onSingleSlotClickEvent(slotNumber: Int, button: Int, hasShiftDown: Boolean) {
        val clickEvent: ClickEvent = ClickEvent.of(button, hasShiftDown)
        val cursorStack = playerInventory.cursorStack
        val targetStack = constrainedSlots[slotNumber].stack
        val action: SlotActionType =
            SlotActionType.getActionFromClickEvent(clickEvent, cursorStack, targetStack, false)
        onSlotEvent(slotNumber, action)
    }

    open fun onSlotEvent(slotNumber: Int, action: SlotActionType) {
        val cursorStack = playerInventory.cursorStack
        val targetStack = constrainedSlots[slotNumber].stack

        when (action) {
            PICKUP_ALL -> {
                constrainedSlots[slotNumber].clear()
                playerInventory.cursorStack = targetStack
            }
            PICKUP_HALF -> {
                val half = targetStack.count / 2
                targetStack.decrement(half)
                playerInventory.cursorStack = targetStack
                val newTarget = targetStack.copy()
                newTarget.count = half
                constrainedSlots[slotNumber].setStack(newTarget, true)
            }
            PLACE_ALL -> {
                val leftOvers = constrainedSlots[slotNumber].insertItem(cursorStack)
                playerInventory.cursorStack = leftOvers
            }
            PLACE_ONE -> if (canStacksCombine(cursorStack, targetStack)) {
                targetStack.increment(1)
                cursorStack.decrement(1)
            } else {
                if (targetStack.isEmpty) {
                    val toInsert = cursorStack.copy()
                    toInsert.count = 1
                    constrainedSlots[slotNumber].setStack(toInsert, true)
                    cursorStack.decrement(1)
                }
            }
            CURSOR_SWAP -> {
                constrainedSlots[slotNumber].clear()
                val extra = constrainedSlots[slotNumber].insertItem(cursorStack)
                // todo: testing on capacity constrained slot
                if (extra.count == 0) {
                    playerInventory.cursorStack = targetStack
                } else {
                    playerInventory.cursorStack = extra
                    playerInventory.player.dropStack(targetStack)
                }
            }
            QUICK_MOVE -> {
                val inventoryType: InventoryType = getInventoryOfIndex(slotNumber)
                val remaining: ItemStack
                remaining = if (inventoryType === InventoryType.BLOCK) { // target slot is block inventory
                    InventoryUtil.calculateQuickMove(
                        calculatedStack,
                        constrainedSlots,
                        slotNumber,
                        9,
                        9 * 3,
                        false
                    )
                } else {
                    val canFill = IntStream.range(0, blockInventory!!.size())
                        .mapToObj { slot: Int -> blockInventory!!.getStack(slot) }.anyMatch { i: ItemStack ->
                            i.isEmpty || canStacksCombine(
                                i,
                                constrainedSlots[slotNumber].stack
                            )
                        }
                    if (blockInventory!!.size() > 0 && canFill) InventoryUtil.calculateQuickMove(
                        calculatedStack,
                        constrainedSlots,
                        slotNumber,
                        playerInventory.size(),
                        blockInventory!!.size(),
                        true
                    ) else if (inventoryType === InventoryType.PLAYER_INVENTORY) { // target is hotbar
                        InventoryUtil.calculateQuickMove(
                            calculatedStack,
                            constrainedSlots,
                            slotNumber,
                            0,
                            9,
                            false
                        )
                    } else { // send everything to player inventory
                        InventoryUtil.calculateQuickMove(
                            calculatedStack,
                            constrainedSlots,
                            slotNumber,
                            9,
                            9 * 3,
                            true
                        )
                    }
                }
                constrainedSlots[slotNumber].setStack(remaining, true)
                calculatedStack.forEach { (index: Int, stack: ItemStack) ->
                    check(
                        constrainedSlots[index].insertItem(stack).isEmpty
                    ) { "Quick move failed!" }
                }
            }
            else -> Logger.warn("Received invalid single slot click action of: $action")
        }
        sendContentUpdates()
    }

    override fun onMultipleSlotClickEvent(clickedSlots: LinkedHashSet<Int>, button: Int, hasShiftDown: Boolean) {
        val clickEvent = ClickEvent.of(button, hasShiftDown)
        val cursorStack = playerInventory.cursorStack
        if (clickedSlots.size > 1) {
            clickedItem = cursorStack
            this.clickEvent = clickEvent
            val action: SlotActionType =
                SlotActionType.getActionFromClickEvent(clickEvent, clickedItem, null, true)
            if (action === DRAG_EMPTY || action === NONE) return
            val newCursorStack: ItemStack = InventoryUtil.calculateDistributedSlots(
                calculatedStack,
                clickedSlots,
                clickEvent,
                constrainedSlots,
                clickedItem,
                playerInventory.cursorStack
            )
            playerInventory.cursorStack = newCursorStack
            calculatedStack.forEach { (slotNumber: Int, itemStack: ItemStack) ->
                constrainedSlots[slotNumber].setStack(itemStack, true)
            }
            sendContentUpdates() // todo: remove, since this is called on every tick
        } else {
            Logger.warn("Received empty click events.")
        }
    }

    override fun onSlotChanged(slotNumber: Int, itemStack: ItemStack) {
        // send to client
        if (!clientHasInited) return
        ScreenPacket.sendPacket(
            syncId, InventoryPacket.SLOT_UPDATE, false,
            playerInventory.player as ServerPlayerEntity
        ) {
            it.writeInt(slotNumber)
            it.writeItemStack(itemStack)
        }
    }

    override fun onItemThrow(slotNumber: Int, all: Boolean) {
        if (slotNumber == -999) {
            val cursorStack = playerInventory.cursorStack
            if (all) {
                playerInventory.cursorStack = ItemStack.EMPTY
                playerInventory.player.dropItem(cursorStack, false, true)
            } else {
                val drop = cursorStack.copy()
                drop.count = 1
                playerInventory.player.dropItem(drop, false, true)
                cursorStack.decrement(1)
            }
        } else {
            val targetStack = constrainedSlots[slotNumber].stack
            if (targetStack.isEmpty) return  // nothing to update anyway
            val drop = targetStack.copy()
            drop.count = 1
            playerInventory.player.dropItem(drop, false, true)
            targetStack.decrement(1)
        }
        sendCursorStackUpdate() // todo: remove
    }

    override fun getStack(slot: Int): ItemStack {
        return constrainedSlots[slot].stack
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

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onClient2Server(action, context, buf)
        if (action.equals(InventoryPacket.SLOT_CLICK_MULTIPLE, true)) {
            val button: Int = buf.readInt()
            val hasShiftDown: Boolean = buf.readBoolean()
            val clickSize: Int = buf.readInt()
            if (clickSize == 1) {
                onSingleSlotClickEvent(buf.readInt(), button, hasShiftDown)
            } else {
                clickedSlots.clear()
                IntStream.range(0, clickSize).map { i: Int -> buf.readInt() }
                    .forEach { e: Int -> clickedSlots.add(e) }
                onMultipleSlotClickEvent(clickedSlots, button, hasShiftDown)
            }
        } else if (action.equals(InventoryPacket.SLOT_CLICK_SIMPLE, true)) {
            val slotNumber: Int = buf.readInt()
            val button: Int = buf.readInt()
            val hasShiftDown: Boolean = buf.readBoolean()
            onSingleSlotClickEvent(slotNumber, button, hasShiftDown)
        } else if (action.equals(InventoryPacket.THROW_ITEM, true)) {
            onItemThrow(buf.readInt(), buf.readBoolean())
        }
    }

    override fun sendContentUpdates() {
        IntStream.range(0, constrainedSlots.size).forEach { i: Int -> onSlotChanged(i, constrainedSlots[i].stack) }
        sendCursorStackUpdate()
    }

    private fun sendCursorStackUpdate() {
        if (!clientHasInited) return
        ScreenPacket.sendPacket(
            syncId, InventoryPacket.CURSOR_UPDATE, false,
            playerInventory.player as ServerPlayerEntity
        ) {
            it.writeItemStack(playerInventory.cursorStack)
        }
    }

    override fun getInventorySize(inventoryType: InventoryType): Int {
        return if (inventoryType == InventoryType.BLOCK) {
            blockInventory!!.size()
        } else {
            playerInventory.size()
        }
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

    override fun initBlockInventory() {
        requireNotNull(blockInventory)
        ScreenPacket.sendPacket(
            syncId, InventoryPacket.BLOCK_INV_UPDATE, false,
            playerInventory.player as ServerPlayerEntity?
        ) {
            it.writeInt(blockInventory!!.size())
            for (i in 0 until blockInventory!!.size()) {
                it.writeItemStack(blockInventory!!.getStack(i))
            }
        }
    }

    override fun clientHasInit() {
        super.clientHasInit()
        clientHasInited = true
        if (blockInventoryWaiting) {
            initBlockInventory()
        }
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        constrainedSlots.clear()
        clientHasInited = false
    }

    override fun getSize(): Int {
        return if (blockInventory != null) {
            playerInventory.size() + blockInventory!!.size()
        } else {
            playerInventory.size()
        }
    }
}