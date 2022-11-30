package tk.beanfeed.extracompanions;

import net.fabricmc.api.ModInitializer;
import software.bernie.geckolib3.GeckoLib;
import tk.beanfeed.extracompanions.entity.EntityRegister;

public class ExtraCompanions implements ModInitializer {
    public static final String MOD_ID = "extracompanions";

    public static void printOut(String s) {
        System.out.println(s);
    }

    public static void printOut(boolean b) {
        System.out.println(b);
    }

    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        //BlockRegister.Register();
        EntityRegister.Register();
    }
}
