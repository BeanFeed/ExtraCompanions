package tk.beanfeed.extracompanions.client.entity.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.entity.TestEntity;

public class TestEntityModel extends AnimatedGeoModel<TestEntity> {
    @Override
    public Identifier getModelResource(TestEntity object) {
        return new Identifier(ExtraCompanions.MOD_ID, "geo/testmob.geo.json");
    }

    @Override
    public Identifier getTextureResource(TestEntity object) {
        return new Identifier(ExtraCompanions.MOD_ID,"textures/entity/testmob/testmob.png");
    }

    @Override
    public Identifier getAnimationResource(TestEntity animatable) {
        return new Identifier(ExtraCompanions.MOD_ID, "animations/testmob.animation.json");
    }

    @Override
    public void setLivingAnimations(TestEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
