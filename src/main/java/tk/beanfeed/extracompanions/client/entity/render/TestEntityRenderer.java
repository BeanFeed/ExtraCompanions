package tk.beanfeed.extracompanions.client.entity.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import tk.beanfeed.extracompanions.client.entity.model.TestEntityModel;
import tk.beanfeed.extracompanions.entity.TestEntity;

public class TestEntityRenderer extends GeoEntityRenderer<TestEntity> {
    public TestEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TestEntityModel());
    }
}
