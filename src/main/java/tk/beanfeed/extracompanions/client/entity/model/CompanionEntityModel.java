package tk.beanfeed.extracompanions.client.entity.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.client.entity.render.CompanionEntityRenderer;
import tk.beanfeed.extracompanions.entity.CompanionEntity;
import tk.beanfeed.extracompanions.entity.TestEntity;

public class CompanionEntityModel extends AnimatedGeoModel<CompanionEntity> {
    private static final Identifier modelResource = new Identifier(ExtraCompanions.MOD_ID, "geo/companion.geo.json");
    private static final Identifier animationResource = new Identifier(ExtraCompanions.MOD_ID, "animations/companion.animation.json");
    @Override
    public Identifier getModelResource(CompanionEntity object) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(CompanionEntity object) {
        return CompanionEntityRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(CompanionEntity animatable) {
        return animationResource;
    }

    public void setLivingAnimations(CompanionEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
