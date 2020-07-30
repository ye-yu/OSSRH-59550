import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

class TestGui: ModInitializer, ClientModInitializer {

    companion object {
        val NAMESPACE = "testgui"
        var GUI_BLOCK_ENTITY: BlockEntityType<GuiBlockEntity>? = null
        var GUI_BLOCK: GuiBlock? = null
        var GUI_BLOCK_ITEM: BlockItem? = null
    }

    override fun onInitialize() {
        Screens.registerServer()

        GUI_BLOCK = Registry.register(Registry.BLOCK, Identifier(NAMESPACE, "guiblock"), GuiBlock())

        GUI_BLOCK_ITEM = Registry.register(
            Registry.ITEM,
            Identifier(NAMESPACE, "guiblock"),
            BlockItem(
                GUI_BLOCK, Item.Settings().group(
                ItemGroup.MISC))
        )

        GUI_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            Identifier(NAMESPACE, "guiblock"),
            BlockEntityType.Builder.create<GuiBlockEntity>(Supplier { GuiBlockEntity() }, GUI_BLOCK).build(null)
        )
    }

    override fun onInitializeClient() {
        Screens.registerServer()
    }
}