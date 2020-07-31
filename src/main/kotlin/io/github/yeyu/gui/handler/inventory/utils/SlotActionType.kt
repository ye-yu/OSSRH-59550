package io.github.yeyu.gui.handler.inventory.utils

import io.github.yeyu.gui.renderer.widget.ClickEvent
import io.github.yeyu.gui.handler.inventory.utils.SlotActionType.Companion.getActionFromClickEvent
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import org.lwjgl.glfw.GLFW
import java.util.*

/**
 * An extension to [net.minecraft.screen.slot.SlotActionType]
 * Use [getActionFromClickEvent] to get the
 * correct action types.
 */
enum class SlotActionType {
    /**
     * Performs a normal slot click when the player cursor stack is empty.
     * This is a left-click action.
     */
    PICKUP_ALL,

    /**
     * Performs a normal slot click when the player cursor stack is empty.
     * This is a right-click action.
     */
    PICKUP_HALF,

    /**
     * Performs a normal slot click when the player cursor stack is not empty and the target stack is empty or can be merged with the player cursor stack.
     * [Distributing by dragging][.DISTRIBUTE_ALL] may be initiated.
     * This is a left-click action.
     */
    PLACE_ALL,

    /**
     * Performs a normal slot click when the player cursor stack is not empty and the target stack is empty or can be merged with the player cursor stack.
     * [Distributing by dragging][.DISTRIBUTE_ONE] may be initiated.
     * This is a right-click action.
     */
    PLACE_ONE,

    /**
     * Performs swapping between player cursor slot and target slot.
     * This is a left-click action.
     */
    CURSOR_SWAP,

    /**
     * Performs a shift-click. This usually quickly moves items between the player's inventory and the open screen handler.
     * This is a left-click action.
     */
    QUICK_MOVE,

    /**
     * Exchanges items between a slot and a hotbar slot, effectively an alias to [net.minecraft.screen.slot.SlotActionType.SWAP]. This is usually triggered by the player pressing a 1-9 number key while hovering over a slot.
     * This is a keyed action.
     */
    HOTBAR_SWAP,  // TODO: Make keyed action

    /**
     * Clones the item in the slot in creative mode.
     * This is a middle-click action.
     */
    CLONE,

    /**
     * Throws the whole item stack out of the inventory. This is usually triggered by the player pressing Q while hovering over a slot, or clicking outside the window.
     * This is a left-click action or a keyed action.
     *
     * When the action type is throw, the click data determines whether to throw a whole stack (1) or a single item from that stack (0).
     */
    THROW_ONE,  // TODO: move from onThrowItem to onSingleSlotAction

    /**
     * Throws the item out of the inventory. This is usually triggered by the player pressing Q while hovering over a slot, or clicking outside the window.
     * This is a right-click action or a keyed action.
     *
     * When the action type is throw, the click data determines whether to throw a whole stack (1) or a single item from that stack (0).
     */
    THROW_ALL,  // TODO: move from onThrowItem to onSingleSlotAction

    /**
     * Distributes the items in the player cursor stack among the dragged slots, effectively an alias to [net.minecraft.screen.slot.SlotActionType.QUICK_CRAFT].
     * This is a left-click action.
     */
    DISTRIBUTE_ALL,

    /**
     * Place one item of player cursor stack at each of the dragged slot.
     * This is a right-click action.
     */
    DISTRIBUTE_ONE,

    /**
     * When player performs dragging, but the cursor stack is empty.
     */
    DRAG_EMPTY,

    /**
     * None is applicable from the available actions.
     */
    NONE;

    companion object {
        /**
         * @param sourceStack set to cursor stack when <tt>multipleSlots == true</tt>, otherwise set to dragged item
         * @return relevant actions except keyed actions:
         * [HOTBAR_SWAP],
         * [THROW_ONE],
         * [THROW_ALL]
         */
        fun getActionFromClickEvent(
            clickEvent: ClickEvent,
            sourceStack: ItemStack,
            targetStack: ItemStack?,
            multipleSlots: Boolean
        ): SlotActionType {
            if (multipleSlots) {
                return when {
                    sourceStack.isEmpty -> {
                        // none is applicable when cursor stack is empty
                        DRAG_EMPTY
                    }
                    clickEvent.button == GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                        DISTRIBUTE_ALL
                    }
                    clickEvent.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                        DISTRIBUTE_ONE
                    }
                    else -> {
                        // none is applicable when clicking other than left or right button
                        NONE
                    }
                }
            }
            Objects.requireNonNull(targetStack)
            return if (sourceStack.isEmpty && targetStack!!.isEmpty) {
                NONE
            } else if (sourceStack.isEmpty) {
                // actions on a single slot
                if (clickEvent == ClickEvent.LEFT) {
                    PICKUP_ALL
                } else if (clickEvent == ClickEvent.MIDDLE) {
                    CLONE
                } else if (clickEvent == ClickEvent.RIGHT) {
                    PICKUP_HALF
                } else if (clickEvent == ClickEvent.SHIFT_LEFT) {
                    QUICK_MOVE
                } else {
                    NONE
                }
            } else if (targetStack!!.isEmpty) {
                when (clickEvent) {
                    ClickEvent.LEFT -> {
                        PLACE_ALL
                    }
                    ClickEvent.RIGHT -> {
                        PLACE_ONE
                    }
                    else -> {
                        NONE
                    }
                }
            } else {
                if (clickEvent == ClickEvent.LEFT) {
                    if (ScreenHandler.canStacksCombine(
                            sourceStack,
                            targetStack
                        )
                    ) PLACE_ALL else CURSOR_SWAP
                } else if (clickEvent == ClickEvent.RIGHT) {
                    if (ScreenHandler.canStacksCombine(
                            sourceStack,
                            targetStack
                        )
                    ) PLACE_ONE else NONE
                } else {
                    NONE
                }
            }
        }
    }
}
