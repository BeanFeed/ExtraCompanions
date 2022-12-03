package tk.beanfeed.extracompanions.Items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.entity.EntityRegister;

public class ItemRegister {
    public static final Item COMPANION_SPAWN_EGG = new SpawnEggItem(EntityRegister.COMPANION, 0x262626, 0xffcd7d, new FabricItemSettings().group(ItemGroup.MISC).maxCount(64));


    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(ExtraCompanions.MOD_ID, "companion_spawn_egg"), COMPANION_SPAWN_EGG);
    }
}
