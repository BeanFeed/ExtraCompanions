package tk.beanfeed.extracompanions.client.entity.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import tk.beanfeed.extracompanions.client.entity.model.CompanionEntityModel;
import tk.beanfeed.extracompanions.client.entity.model.TestEntityModel;
import tk.beanfeed.extracompanions.entity.CompanionEntity;

public class CompanionEntityRenderer extends GeoEntityRenderer<CompanionEntity> {
    public CompanionEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CompanionEntityModel());
    }
}
