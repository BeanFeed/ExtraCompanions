package tk.beanfeed.extracompanions.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegister {
    public static final Block MyBlock = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    public static void Register()
    {
        Registry.register(Registry.BLOCK, new Identifier("mylittleminions", "myblock"), MyBlock);
        Registry.register(Registry.ITEM, new Identifier("mylittleminions", "myblock"), new BlockItem(MyBlock, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
