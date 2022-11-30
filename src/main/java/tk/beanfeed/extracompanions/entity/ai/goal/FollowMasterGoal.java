package tk.beanfeed.extracompanions.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import tk.beanfeed.extracompanions.entity.CompanionEntity;

public class FollowMasterGoal extends Goal {

    public FollowMasterGoal(CompanionEntity self){}
    @Override
    public boolean canStart() {
        return false;
    }
}
