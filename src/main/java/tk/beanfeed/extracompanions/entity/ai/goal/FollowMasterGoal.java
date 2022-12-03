package tk.beanfeed.extracompanions.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import tk.beanfeed.extracompanions.ExtraCompanions;
import tk.beanfeed.extracompanions.Utils;
import tk.beanfeed.extracompanions.entity.CompanionEntity;

public class FollowMasterGoal extends Goal {
    private CompanionEntity self;
    private double speed = 1.0;
    private double maxDistance;
    private EntityNavigation navigation;
    private WorldView world;
    private boolean movingToPlayer = false;
    private boolean hasAlertedPlayer = false;
    private boolean lastFollowVal = false;
    public FollowMasterGoal(CompanionEntity self, double maxDistance) {
        this.self = self;
        this.maxDistance = maxDistance;
        this.navigation = self.getNavigation();
        this.world = self.world;
    }
    @Override
    public boolean canStart() {
        boolean isCloseAndIn = false;
        if(self.getMaster() != null && self.isFollowing()) {

            if(self.world.getPlayerByUuid(self.getMasterUUID()) != null) {
                if (Utils.distanceTo(self.getBlockPos(), self.getMaster().getBlockPos()) < maxDistance) { isCloseAndIn = true; hasAlertedPlayer = false;}
                else
                {
                    if(tryTeleport()) return false;
                }
            }

            if(!isCloseAndIn && !hasAlertedPlayer && !lastFollowVal) { self.getMaster().sendMessage(Text.of("Companion Could Not Reach You")); hasAlertedPlayer = true;}
        }
        //ExtraCompanions.printOut(self.isWaiting());
        lastFollowVal = self.isFollowing();
        return isCloseAndIn;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }
    @Override
    public void stop() {
        if(movingToPlayer) { this.navigation.stop(); movingToPlayer = false; }
    }
    @Override
    public void tick() {
        if(this.self.getMaster() != null && !this.self.isLeashed())
        {
            this.self.getLookControl().lookAt(this.self.getMaster(), 10.0f, (float)this.self.getMaxLookPitchChange());
            if(Utils.distanceTo(this.self.getBlockPos(), this.self.getMaster().getBlockPos()) > 2 && shouldContinue())
            {
                if(self.isWaiting()) self.setWaiting();
                this.navigation.startMovingTo(this.self.getMaster(), speed);
                movingToPlayer = true;
            }else
            {
                this.navigation.stop();
                movingToPlayer = false;
            }
        }
    }

    private boolean tryTeleport() {
        BlockPos blockPos = this.self.getMaster().getBlockPos();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) {
                return true;
            }
        }
        return false;

    }
    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.self.getMaster().getX()) < 2.0 && Math.abs((double)z - this.self.getMaster().getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.self.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.self.getYaw(), this.self.getPitch());
            this.navigation.stop();
            return true;
        }
    }
    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockPos blockPos = pos.subtract(this.self.getBlockPos());
            return this.world.isSpaceEmpty(this.self, this.self.getBoundingBox().offset(blockPos));
        }
    }
    private int getRandomInt(int min, int max) {
        return this.self.getMaster().getRandom().nextInt(max - min + 1) + min;
    }

}
