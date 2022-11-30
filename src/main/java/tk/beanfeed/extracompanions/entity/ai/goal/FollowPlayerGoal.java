package tk.beanfeed.extracompanions.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import tk.beanfeed.extracompanions.Utils;

public class FollowPlayerGoal extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final double maxDistance;
    private final EntityNavigation navigation;
    private PlayerEntity target;

    private boolean movingToPlayer = false;

    public FollowPlayerGoal(PathAwareEntity mob, double speed, double maxDistance)
    {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.navigation = this.mob.getNavigation();
    }

    @Override
    public boolean canStart()
    {
        PlayerEntity target = mob.world.getClosestPlayer(this.mob, maxDistance);
        if(target == null) return false;
        if(Utils.distanceTo(mob.getBlockPos(), target.getBlockPos()) > maxDistance) return false;
        ItemStack mainHand = target.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHand = target.getStackInHand(Hand.OFF_HAND);
        if(mainHand.isOf(Items.CARROT) || offHand.isOf(Items.CARROT))
        {
            mob.setTarget(target);
            this.target = target;
            return true;
        }
        else
        {
            mob.setTarget(null);
            return false;
        }
    }
    
    @Override
    public boolean shouldContinue()
    {
        if(target == null) return false;
        ItemStack mainHand = this.target.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHand = this.target.getStackInHand(Hand.OFF_HAND);
        if((mainHand.isOf(Items.CARROT) || offHand.isOf(Items.CARROT)) && (Utils.distanceTo(this.mob.getBlockPos(), this.target.getBlockPos()) < maxDistance)) return true;
        return false;
    }
    
    @Override
    public void stop() {
        this.target = null;
        if(movingToPlayer) { this.navigation.stop(); movingToPlayer = false; }
    }
    
    @Override
    public void tick()
    {
        //Mylittleminions.printOut("tick");
        if(this.target != null && !this.mob.isLeashed())
        {
            this.mob.getLookControl().lookAt(this.target, 10.0f, (float)this.mob.getMaxLookPitchChange());
            if(Utils.distanceTo(this.mob.getBlockPos(), this.target.getBlockPos()) > 2 && shouldContinue())
            {
                this.navigation.startMovingTo(this.target, this.speed);
                movingToPlayer = true;
            }else
            {
                this.navigation.stop();
                movingToPlayer = false;
            }
        }

    }
}
