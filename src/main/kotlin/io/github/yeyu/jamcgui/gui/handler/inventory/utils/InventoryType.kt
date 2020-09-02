package io.github.yeyu.jamcgui.gui.handler.inventory.utils

enum class InventoryType {
    /**
     * Index range [0, 8]
     * */
    PLAYER_HOTBAR,

    /**
     * Index range [9, 35]
     * */
    PLAYER_INVENTORY,

    /**
     * Index range [36, 41]
     * */
    PLAYER_EQUIPMENT_SLOT,

    /**
     * Index from 42 onwards
     * */
    BLOCK
}