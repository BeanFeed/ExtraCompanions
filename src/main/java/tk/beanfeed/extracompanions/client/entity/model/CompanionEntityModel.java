package tk.beanfeed.extracompanions.client.entity.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.entity.CompanionEntity;

public class CompanionEntityModel extends AnimatedGeoModel<CompanionEntity> {
    private static final Identifier modelResource = new Identifier(ExtraCompanions.MOD_ID, "geo/companion.geo.json");
    private static final Identifier textureResource = new Identifier(ExtraCompanions.MOD_ID, "textures/entity/companion/cvar0.png");
    private static final Identifier animationResource = new Identifier(ExtraCompanions.MOD_ID, "animations/companion.animation.json");
    @Override
    public Identifier getModelResource(CompanionEntity object) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(CompanionEntity object) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(CompanionEntity animatable) {
        return animationResource;
    }
}
