package tk.beanfeed.extracompanions.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tk.beanfeed.extracompanions.ExtraCompanions;

public class EntityRegister {

    public static final EntityType<TestEntity> TESTMOB = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(ExtraCompanions.MOD_ID,"testmob"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TestEntity::new).dimensions(EntityDimensions.fixed(0.3f,1.1f)).build()
    );

    public static final EntityType<CompanionEntity> COMPANIONENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(ExtraCompanions.MOD_ID, "companion"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionEntity::new).dimensions(EntityDimensions.fixed(0.5625f, 0.9375f)).build()
    );


    public static void Register() {
        FabricDefaultAttributeRegistry.register(TESTMOB, TestEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(COMPANIONENTITY, CompanionEntity.setAttributes());
    }
}
