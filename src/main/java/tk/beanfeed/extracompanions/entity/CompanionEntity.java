package tk.beanfeed.extracompanions.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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
import tk.beanfeed.extracompanions.Utils;
import tk.beanfeed.extracompanions.entity.ai.goal.CompanionWander;
import tk.beanfeed.extracompanions.entity.ai.goal.FollowMasterGoal;
import tk.beanfeed.extracompanions.entity.variant.CompanionVariant;

import javax.annotation.Nullable;
import java.util.*;

public class CompanionEntity extends PathAwareEntity implements IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private PlayerEntity master;

    private BlockPos station;

    private boolean isWaiting = false;
    private boolean isFollowing = false;

    private NbtCompound persistantData = new NbtCompound();

    private String pDatS = "extracompanions.compData";

    private String name;

    private int tickCountDown = 0;


    private static final float speed = 4f / 10f;
    protected CompanionEntity(EntityType<CompanionEntity> entityType, World world) {
        super(entityType, world);
    }
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowMasterGoal(this, 14));
        this.goalSelector.add(2, new CompanionWander(this, speed, 5));
        this.goalSelector.add(5, new LookAroundGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
    }
    public static DefaultAttributeContainer.Builder setAttributes() {

        return PassiveEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed);
    }
    private <E extends IAnimatable> PlayState defPredicate(AnimationEvent<E> event) {
        if(!this.isMoving()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.companion.idle", ILoopType.EDefaultLoopTypes.LOOP));
        } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.companion.walk", ILoopType.EDefaultLoopTypes.LOOP));
        if(event.getController().getCurrentAnimation() != null && Objects.equals(event.getController().getCurrentAnimation().animationName, "animation.companion.walk") && this.isFollowing()){event.getController().setAnimationSpeed(5.0);}
        else event.getController().setAnimationSpeed(1.0);
        return PlayState.CONTINUE;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        if(persistantData == null) persistantData = new NbtCompound();
        nbt.put(pDatS, persistantData);
        if(this.master != null) persistantData.putUuid("Owner", this.getMasterUUID());
        persistantData.putBoolean("isWaiting", isWaiting);
        persistantData.putBoolean("isFollowing", isFollowing);
        if(this.station != null) persistantData.putIntArray("stationPos", new int[]{this.station.getX(), this.station.getY(), this.station.getZ()});
        persistantData.putInt("Variant", this.getTypeVariant());
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.contains(pDatS)) persistantData = nbt.getCompound(pDatS);
        if(persistantData != null)
        {
            if(persistantData.containsUuid("Owner")) setMaster(this.world.getPlayerByUuid(persistantData.getUuid("Owner")));
            if(persistantData.contains("isWaiting")) this.isWaiting = persistantData.getBoolean("isWaiting");
            if(persistantData.contains("isFollowing")) this.isFollowing = persistantData.getBoolean("isFollowing");
            if(persistantData.contains("stationPos")) {
                int[] stationPos = nbt.getIntArray("stationPos");
                this.station = new BlockPos(stationPos[0], stationPos[1], stationPos[2]);
            }
            if(persistantData.contains("Variant")) this.dataTracker.set(DATA_ID_TYPE_VARIANT, persistantData.getInt("Variant"));
        }

        super.readCustomDataFromNbt(nbt);
    }

    public NbtCompound getPersistantData() {
        if(this.persistantData == null) return new NbtCompound();
        return this.persistantData;
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
        persistantData.putUuid("Owner", this.getMasterUUID());
    }

    public void setMaster(UUID uuid) {

        this.master = this.world.getPlayerByUuid(uuid);
        persistantData.putUuid("Owner", uuid);
    }
    public void leaveMaster() {
        this.master = null;
        persistantData.putUuid("Owner", new UUID(0L, 0L));
    }
    public UUID getMasterUUID() {
        return master != null ? master.getUuid() : new UUID(0,0);
    }
    public boolean isWaiting() { return isWaiting; }
    public boolean isFollowing() { return isFollowing; }
    public void setFollowing() {
        this.isFollowing = !this.isFollowing;
        persistantData.putBoolean("isFollowing", this.isFollowing);
        if(isFollowing && isWaiting) setWaiting();

    }
    public void setWaiting() {
        this.isWaiting = !this.isWaiting;
        persistantData.putBoolean("isWaiting", this.isWaiting);
    }
    private boolean isMoving() {
        return !(this.getVelocity().x == 0 && this.getVelocity().z == 0);
    }
    private boolean isMoving(boolean includeY) {
        if(includeY) return this.getVelocity().x == 0 && this.getVelocity().z == 0 && this.getVelocity().y == 0;
        return this.getVelocity().x == 0 && this.getVelocity().z == 0;
    }

    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(tickCountDown != 0) return ActionResult.FAIL;
        tickCountDown += 10;
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
        }else if(itemStack.isOf(Items.PRISMARINE_CRYSTALS)) {
            setFollowing();
            if(player instanceof ServerPlayerEntity pl) pl.sendMessage(Text.of("Companion Is Now Following You"));
            return ActionResult.SUCCESS;
        }
        else if(itemStack.getItem() != null && getMaster() == player && player instanceof ServerPlayerEntity) { setWaiting(); return ActionResult.SUCCESS;}



        return ActionResult.PASS;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) { return false; }
    public void tick() {
        if(tickCountDown > 0) tickCountDown--;
        super.tick();
        //ExtraCompanions.printOut(Boolean.toString(this.getVelocity().x == 0 && this.getVelocity().z == 0));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
    }

    //Variant Stuff
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(CompanionEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        CompanionVariant variant = Util.getRandom(CompanionVariant.values(), this.random);
        setVariant(variant);
        int varID = variant.getId();
        if(Utils.isWithin(varID,0,4)) {
            name = Utils.getRandomMaleName();
            this.setCustomName(Text.of(name));
        } else {
            name = Utils.getRandomFemaleName();
            this.setCustomName(Text.of(name));
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public CompanionVariant getVariant() {
        return CompanionVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(CompanionVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
