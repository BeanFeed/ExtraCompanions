package tk.beanfeed.extracompanions.entity.ai.goal;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import tk.beanfeed.extracompanions.entity.CompanionEntity;

public class CompanionWander extends WanderAroundGoal {
    private PathAwareEntity mob;
    public CompanionWander(PathAwareEntity mob, double speed) {
        super(mob, speed);
        this.mob = mob;
    }
    public CompanionWander(PathAwareEntity mob, double speed, int chance) {
        super(mob, speed, chance, false);
        this.mob = mob;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && (!((CompanionEntity)mob).isFollowing() && !((CompanionEntity)mob).isWaiting());
    }
    @Override
    public boolean canStart()
    {
        if(mob instanceof CompanionEntity CE) {
            return super.canStart() && (!CE.isFollowing() && !CE.isWaiting());
        }else return false;
    }
}
