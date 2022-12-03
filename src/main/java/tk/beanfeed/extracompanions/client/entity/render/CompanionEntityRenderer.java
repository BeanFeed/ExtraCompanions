package tk.beanfeed.extracompanions.client.entity.render;

import com.google.common.collect.Maps;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.client.entity.model.CompanionEntityModel;
import tk.beanfeed.extracompanions.client.entity.model.TestEntityModel;
import tk.beanfeed.extracompanions.entity.CompanionEntity;
import tk.beanfeed.extracompanions.entity.variant.CompanionVariant;

import java.util.Map;

public class CompanionEntityRenderer extends GeoEntityRenderer<CompanionEntity> {
    public static final Map<CompanionVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CompanionVariant.class), (map) -> {
                map.put(CompanionVariant.MALE0,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/male0.png"));
                map.put(CompanionVariant.MALE1,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/male1.png"));
                map.put(CompanionVariant.MALE2,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/male2.png"));
                map.put(CompanionVariant.MALE3,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/male3.png"));
                map.put(CompanionVariant.MALE4,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/male4.png"));
                map.put(CompanionVariant.FEMALE0,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/female0.png"));
                map.put(CompanionVariant.FEMALE1,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/female1.png"));
                map.put(CompanionVariant.FEMALE2,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/female2.png"));
                map.put(CompanionVariant.FEMALE3,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/female3.png"));
                map.put(CompanionVariant.FEMALE4,
                        new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/female4.png"));
            });
    public CompanionEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CompanionEntityModel());
    }

    @Override
    public Identifier getTextureLocation(CompanionEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
