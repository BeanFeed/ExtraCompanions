package tk.beanfeed.extracompanions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import tk.beanfeed.extracompanions.ExtraCompanions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CompanionEntity extends PathAwareEntity implements IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private PlayerEntity master;

    private BlockPos station;

    private boolean isWaiting = false;

    private static final float speed = 4f / 10f;
    protected CompanionEntity(EntityType<CompanionEntity> entityType, World world) {
        super(entityType, world);
    }
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, speed, 5));
    }
    public static DefaultAttributeContainer.Builder setAttributes() {

        return PassiveEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed);
    }
    private <E extends IAnimatable> PlayState defPredicate(AnimationEvent<E> event) {
        if(!this.isMoving()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.companion.idle", ILoopType.EDefaultLoopTypes.LOOP));
        } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.companion.walk", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putUuid("Owner", this.getMasterUUID());
        nbt.putBoolean("isWaiting", isWaiting);
        if(this.station != null) nbt.putIntArray("stationPos", new int[]{this.station.getX(), this.station.getY(), this.station.getZ()});
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.containsUuid("Owner")) setMaster(this.world.getPlayerByUuid(nbt.getUuid("Owner")));
        if(nbt.contains("isWaiting")) this.isWaiting = nbt.getBoolean("isWaiting");
        if(nbt.contains("stationPos")) {
            int[] stationPos = nbt.getIntArray("stationPos");
            this.station = new BlockPos(stationPos[0], stationPos[1], stationPos[2]);
        }
    }
        @Override
    public void registerControllers(AnimationData data) {
        AnimationController<CompanionEntity> idleCon = new AnimationController<CompanionEntity>(this, "idleController", 0, this::defPredicate);
        data.addAnimationController(idleCon);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public PlayerEntity getMaster() {
        return this.master;
    }

    public void setMaster(PlayerEntity player) {
        this.master = player;
    }

    public void setMaster(UUID uuid) {
        this.master = this.world.getPlayerByUuid(uuid);
    }
    public void leaveMaster() { this.master = null;}
    public UUID getMasterUUID() {
        return this.master.getUuid();
    }

    private boolean isMoving(boolean includeY) {
        if(includeY) return this.getVelocity().x == 0 && this.getVelocity().z == 0 && this.getVelocity().y == 0;
        return this.getVelocity().x == 0 && this.getVelocity().z == 0;
    }

    private boolean isMoving() {
        return !(this.getVelocity().x == 0 && this.getVelocity().z == 0);
    }

    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.isAlive()) {
            return ActionResult.PASS;
        } else if (this.getHoldingEntity() == player) {
            this.detachLeash(true, !player.getAbilities().creativeMode);
            return ActionResult.success(this.world.isClient);
        } else {
            ActionResult actionResult = this.interactWithItem(player, hand);
            if (actionResult.isAccepted()) {
                return actionResult;
            } else {
                actionResult = this.interactMob(player, hand);
                if (actionResult.isAccepted()) {
                    this.emitGameEvent(GameEvent.ENTITY_INTERACT);
                    return actionResult;
                } else {
                    return super.interact(player, hand);

                }
            }
        }
    }

    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.LEAD) && this.canBeLeashedBy(player)) {
            this.attachLeash(player, true);
            itemStack.decrement(1);
            return ActionResult.success(this.world.isClient);
        } else if (itemStack.isOf(Items.NAME_TAG)) {
                ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
                if (actionResult.isAccepted()) {
                    return actionResult;
                }
        } else if(itemStack.isOf(Items.STICK)) {
            this.setMaster(player);
            if(player instanceof ServerPlayerEntity pl) pl.sendMessage(Text.of("This Companion Is Now Yours"));
            return ActionResult.SUCCESS;
        } else if(itemStack.isOf(Items.BLAZE_ROD)) {
            this.leaveMaster();
            if(player instanceof ServerPlayerEntity pl) pl.sendMessage(Text.of("This Companion Is No Longer Yours"));
            return ActionResult.SUCCESS;
        }


        return ActionResult.PASS;
    }

    public void tick() {
        super.tick();
        //ExtraCompanions.printOut(Boolean.toString(this.getVelocity().x == 0 && this.getVelocity().z == 0));
    }
}
