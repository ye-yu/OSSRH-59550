package io.github.yeyu.jamcgui.gui.handler.inventory

/**
 * The provided inventory packet action identifiers
 * for easy reference for the client and
 * the server
 * */
object InventoryPacket {
    const val BLOCK_INV_UPDATE = "block-inv-update"
    const val CURSOR_UPDATE = "cursor-update"
    const val SLOT_UPDATE = "slot-update"
    const val SLOT_CLICK_SIMPLE = "slot-click-simple"
    const val SLOT_CLICK_MULTIPLE = "slot-click-multiple"
    const val THROW_ITEM = "throw-item"
}