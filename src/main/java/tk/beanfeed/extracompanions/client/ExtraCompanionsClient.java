package tk.beanfeed.extracompanions.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import tk.beanfeed.extracompanions.client.entity.render.CompanionEntityRenderer;
import tk.beanfeed.extracompanions.client.entity.render.TestEntityRenderer;
import tk.beanfeed.extracompanions.entity.EntityRegister;

@Environment(EnvType.CLIENT)
public class ExtraCompanionsClient implements ClientModInitializer {
    //public static final EntityModelLayer Model_TestMob_Layer = new EntityModelLayer(new Identifier(Mylittleminions.MOD_ID, "testmob"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegister.TESTMOB, TestEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegister.COMPANIONENTITY, CompanionEntityRenderer::new);
        //EntityModelLayerRegistry.registerModelLayer(Model_TestMob_Layer, TestMobModel::getTexturedModelData);
    }
}
