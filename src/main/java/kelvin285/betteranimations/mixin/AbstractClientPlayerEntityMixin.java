package kelvin285.betteranimations.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import kelvin285.betteranimations.BetterAnimations;
import kelvin285.betteranimations.IPlayerAccessor;
import net.minecraft.block.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements IPlayerAccessor {

    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    public KeyframeAnimation anim_idle = null;
    public KeyframeAnimation anim_sneak_idle = null;
    public KeyframeAnimation anim_sneak_walk = null;
    public KeyframeAnimation anim_walk = null;
    public KeyframeAnimation anim_run = null;
    public KeyframeAnimation anim_turn_right = null;
    public KeyframeAnimation anim_turn_left = null;
    public KeyframeAnimation anim_falling = null;
    public KeyframeAnimation anim_landing = null;
    public KeyframeAnimation anim_swimming = null;
    public KeyframeAnimation anim_swim_idle = null;
    public KeyframeAnimation anim_crawl_idle = null;
    public KeyframeAnimation anim_crawling = null;
    public KeyframeAnimation anim_eating = null;

    public KeyframeAnimation anim_climbing = null;
    public KeyframeAnimation anim_climbing_idle = null;
    public KeyframeAnimation anim_sprint_stop = null;
    public KeyframeAnimation anim_fence_idle = null;
    public KeyframeAnimation anim_fence_walk = null;
    public KeyframeAnimation anim_edge_idle = null;
    public KeyframeAnimation anim_elytra_fly = null;
    public KeyframeAnimation anim_flint_and_steel = null;
    public KeyframeAnimation anim_flint_and_steel_sneak = null;
    public KeyframeAnimation anim_boat_idle = null;
    public KeyframeAnimation anim_boat_left_paddle = null;
    public KeyframeAnimation anim_boat_right_paddle = null;
    public KeyframeAnimation anim_boat_forward = null;

    public KeyframeAnimation anim_rolling = null;

    public int punch_index = 0;
    public int jump_index = 0;
    public KeyframeAnimation anim_jump[] = new KeyframeAnimation[2];
    public KeyframeAnimation anim_fall[] = new KeyframeAnimation[2];

    public KeyframeAnimation anim_punch[] = new KeyframeAnimation[2];
    public KeyframeAnimation anim_punch_sneaking[] = new KeyframeAnimation[2];
    public KeyframeAnimation anim_sword_swing[] = new KeyframeAnimation[2];
    public KeyframeAnimation anim_sword_swing_sneak[] = new KeyframeAnimation[2];

    public float leanAmount = 0;
    public float leanMultiplier = 1;
    public float realLeanMultiplier = 1;

    public float squash = 0;
    public float realSquash = 0;

    public float momentum = 0;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(ClientWorld world, GameProfile profile, CallbackInfo info) {

        var animation = PlayerAnimationRegistry.getAnimation(new Identifier(BetterAnimations.MOD_ID, "idle"));

        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object)this).addAnimLayer(1000, modAnimationContainer);

        anim_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "idle"));
        anim_fall[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "fall_first"));
        anim_fall[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "fall_second"));
        anim_jump[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "jump_first"));
        anim_jump[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "jump_second"));
        anim_sneak_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sneak_idle"));
        anim_sneak_walk = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sneak_walk"));
        anim_walk = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "walking"));
        anim_run = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "running"));
        anim_turn_right = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "turn_right"));
        anim_turn_left = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "turn_left"));
        anim_punch[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "punch_right"));
        anim_punch[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "punch_left"));
        anim_punch_sneaking[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "punch_right_sneak"));
        anim_punch_sneaking[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "punch_left_sneak"));

        anim_sword_swing[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sword_swing_first"));
        anim_sword_swing[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sword_swing_second"));

        anim_sword_swing_sneak[0] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sword_swing_sneak_first"));
        anim_sword_swing_sneak[1] = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sword_swing_sneak_second"));

        anim_falling = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "falling"));
        anim_landing = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "landing"));
        anim_swimming = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "swimming"));
        anim_swim_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "swim_idle"));
        anim_crawl_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "crawl_idle"));
        anim_crawling = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "crawling"));
        anim_eating = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "eating"));
        anim_climbing = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "climbing"));
        anim_climbing_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "climbing_idle"));
        anim_sprint_stop = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "sprint_stop"));
        anim_fence_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "fence_idle"));
        anim_fence_walk = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "fence_walk"));
        anim_edge_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "edge_idle"));
        anim_elytra_fly = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "elytra_fly"));
        anim_flint_and_steel = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "flint_and_steel"));
        anim_flint_and_steel_sneak = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "flint_and_steel_sneak"));
        anim_boat_idle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "boat_idle"));
        anim_boat_forward = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "boat_forward"));
        anim_boat_right_paddle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "boat_right_paddle"));
        anim_boat_left_paddle = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "boat_left_paddle"));
        anim_rolling = PlayerAnimationRegistry.getAnimation(new Identifier("betteranimations", "rolling"));

    }
    public float turnDelta = 0;

    public Vec3d lastPos = new Vec3d(0, 0, 0);

    public boolean lastOnGround = false;


    @Override
    public void tick() {
        super.tick();

        float delta = 1.0f / 20.0f;
        Vec3d pos = getPos();

        if (!isOnGround() && lastOnGround && getVelocity().y > 0) {
            playJumpAnimation();
        }

        Block standingBlock = world.getBlockState(getBlockPos().down()).getBlock();
        boolean onFence = (standingBlock instanceof FenceBlock || standingBlock instanceof WallBlock || standingBlock instanceof PaneBlock) && isOnGround();

        boolean onEdge = !world.getBlockState(getBlockPos().down()).getMaterial().blocksMovement() && isOnGround();

        if (turnDelta != 0) {
            leanAmount = MathHelper.lerp(delta * 4, leanAmount, bodyYaw - prevBodyYaw);
        } else {
            leanAmount = MathHelper.lerp(delta * 4, leanAmount, 0);
        }
        leanMultiplier = MathHelper.lerp(delta * 8, leanMultiplier, realLeanMultiplier);

        squash = MathHelper.lerp(delta * 8, squash, realSquash);
        realSquash = MathHelper.lerp(delta * 8, realSquash, 0);


        Vector3f movementVector = new Vector3f((float)(pos.x - lastPos.x), 0, (float)(pos.z - lastPos.z));
        Vector3f lookVector = new Vector3f((float)Math.cos(Math.toRadians(bodyYaw + 90)), 0, (float)Math.sin(Math.toRadians(bodyYaw + 90)));

        boolean isWalking = movementVector.length() > 0;
        boolean isWalkingForwards = isWalking && movementVector.dot(lookVector) > 0;

        float walk_sign = isWalking ? isWalkingForwards ? 1 : -1 : 0;

        float sprint_multiplier = ((isSprinting() && isWalkingForwards) ? 2 : 1);
        momentum = MathHelper.lerp(delta * 2 * sprint_multiplier, momentum, (walk_sign) * sprint_multiplier);

        if (realLeanMultiplier < 1) {
            realLeanMultiplier += 0.1f;
        } else {
            realLeanMultiplier = 1;
        }
        /*
        if (Math.abs(prevBodyYaw - bodyYaw) > 3 && !isWalking) {
            turnDelta = Math.signum(bodyYaw - prevBodyYaw);
        } else {
            turnDelta = MathHelper.lerp(delta * 16, turnDelta, 0);
            if (Math.abs(turnDelta) < 0.01f) {
                turnDelta = 0;
            }
        }
         */



        KeyframeAnimation anim = null;

        float anim_speed = 1.0f;
        int fade_time = 5;

        boolean onGroundInWater = isSubmergedInWater() && this.getSteppingBlockState().getMaterial().isSolid() && !isSprinting();

        if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {

            if (hasVehicle() && getVehicle() instanceof BoatEntity) {
                anim = anim_boat_idle;
                boolean left_paddle = ((BoatEntity) getVehicle()).isPaddleMoving(0);
                boolean right_paddle = ((BoatEntity) getVehicle()).isPaddleMoving(1);
                if (left_paddle && right_paddle) {
                    anim = anim_boat_forward;
                } else if (left_paddle) {
                    anim = anim_boat_left_paddle;
                } else if (right_paddle) {
                    anim = anim_boat_right_paddle;
                }
            }
            else if (world.getBlockState(getBlockPos()).getBlock() instanceof LadderBlock && !isOnGround() && !jumping) {
                anim = anim_climbing_idle;
                if (getVelocity().y > 0) {
                    anim = anim_climbing;
                }
            }
            else if (isUsingItem() && getMainHandStack().getItem().isFood()) {
                anim = anim_eating;
            }
            else if (isFallFlying()) {
                anim = anim_elytra_fly;
            }
            else if (isOnGround() || onGroundInWater) {
                anim = anim_idle;
                if (onFence) {
                    anim = anim_fence_idle;
                } else if (onEdge) {
                    anim = anim_edge_idle;
                }

                if (turnDelta != 0 && !onEdge) {
                    anim = anim_turn_right;
                    if (turnDelta < 0) {
                        anim = anim_turn_left;
                    }
                }

                if ((isInsideWaterOrBubbleColumn() || isInLava()) && !onGroundInWater) {
                    if (this.isSwimming() || this.isSprinting()) {
                        anim = anim_swimming;
                    }
                }
                else if (isCrawling()) {
                    if (isWalking) {
                        if (currentAnimation == anim_crawl_idle) {
                            fade_time = 0;
                        }
                        anim = anim_crawling;
                    } else {
                        if (currentAnimation == anim_crawling) {
                            fade_time = 0;
                        }
                        anim = anim_crawl_idle;
                    }
                }
                else if (isSneaking()) {
                    anim = anim_sneak_idle;

                    if (isWalking || turnDelta != 0) {
                        anim = anim_sneak_walk;
                    }
                } else {
                    if (isWalking) {
                        if (momentum > 1 && !isWalkingForwards) {
                            anim = anim_sprint_stop;
                            fade_time = 2;
                        } else {
                            if (isSprinting() && !isUsingItem()) {
                                anim = anim_run;
                                anim_speed = 1.0f;
                            } else {
                                anim = anim_walk;
                                if (onFence) {
                                    anim = anim_fence_walk;
                                }
                            }
                        }
                    }
                }
            } else {

                if (isInsideWaterOrBubbleColumn() || isInLava()) {
                    if (this.isSwimming() || this.isSprinting()) {
                        anim = anim_swimming;
                    } else {
                        anim = anim_swim_idle;
                    }
                } else {
                    if (this.fallDistance > 1) {
                        if (this.fallDistance > 3) {
                            anim = anim_falling;
                        } else {
                            anim = anim_fall[jump_index];
                        }
                    }
                }
            }
        }
        playAnimation(anim, anim_speed, fade_time);

        if (this.isUsingItem()) {
            if (this.getActiveItem() != null) {
                var action = getActiveItem().getUseAction();
                if (action == UseAction.BOW || action == UseAction.CROSSBOW || action == UseAction.SPYGLASS || action == UseAction.SPEAR || action == UseAction.TOOT_HORN || action == UseAction.BLOCK ||
                action == UseAction.DRINK) {
                    disableArmAnimations();
                } else if (getActiveItem().getItem() instanceof FlintAndSteelItem) {
                    anim = anim_flint_and_steel;
                    if (isSneaking()) {
                        anim = anim_flint_and_steel_sneak;
                    }
                    playAnimation(anim, anim_speed, fade_time);
                }
            }
        } else {
            enableArmAnimations();
        }

        lastPos = new Vec3d(pos.x, pos.y, pos.z);
        lastOnGround = onGround;
    }

    KeyframeAnimation currentAnimation = null;

    public void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {

        if (fallDistance > 0) {
            if (onGround) {
                if (fallDistance >= 3) {
                    playAnimation(anim_landing, 1.0f, 0);
                }
                realSquash = -fallDistance / 10.0f;
            } else {
                realSquash = Math.min(fallDistance / 20.0f, 0.25f);
            }

        }

        super.fall(heightDifference, onGround, state, landedPosition);
    }

    public void playAnimation(KeyframeAnimation anim) {
        playAnimation(anim, 1.0f, 10);
    }

    private boolean modified = false;
    private boolean armAnimationsEnabled = true;
    public void playAnimation(KeyframeAnimation anim, float speed, int fade) {
        if (currentAnimation == anim || anim == null) return;

        currentAnimation = anim;
        ModifierLayer<IAnimation> animationContainer = modAnimationContainer;


        var builder = anim.mutableCopy();
        builder.leftArm.setEnabled(armAnimationsEnabled);
        builder.rightArm.setEnabled(armAnimationsEnabled);
        anim = builder.build();

        if (modified) {
            animationContainer.removeModifier(0);
        }
        modified = true;
        animationContainer.addModifierBefore(new SpeedModifier(speed));
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fade, Ease.LINEAR), new KeyframeAnimationPlayer(anim));
        animationContainer.setupAnim(1.0f / 20.0f);
    }

    public void disableArmAnimations() {
        if (currentAnimation != null && armAnimationsEnabled) {
            armAnimationsEnabled = false;
            ModifierLayer<IAnimation> animationContainer = modAnimationContainer;


            var builder = currentAnimation.mutableCopy();

            builder.leftArm.setEnabled(false);
            builder.rightArm.setEnabled(false);

            currentAnimation = builder.build();

            if (modified) {
                animationContainer.removeModifier(0);
            }
            modified = true;
            animationContainer.addModifierBefore(new SpeedModifier(speed));
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR), new KeyframeAnimationPlayer(currentAnimation));
            animationContainer.setupAnim(1.0f / 20.0f);
            animationContainer.tick();
        }
    }

    public void enableArmAnimations() {
        if (currentAnimation != null && !armAnimationsEnabled) {
            armAnimationsEnabled = true;
            ModifierLayer<IAnimation> animationContainer = modAnimationContainer;


            var builder = currentAnimation.mutableCopy();
            builder.leftArm.setEnabled(true);
            builder.rightArm.setEnabled(true);
            currentAnimation = builder.build();

            if (modified) {
                animationContainer.removeModifier(0);
            }
            modified = true;
            animationContainer.addModifierBefore(new SpeedModifier(speed));
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR), new KeyframeAnimationPlayer(currentAnimation));

        }
    }

    public void playJumpAnimation() {
        realLeanMultiplier = 0;
        realSquash = -0.1f;

        jump_index++;
        jump_index %= 2;

        playAnimation(anim_jump[jump_index], 1.0f, 0);
    }

    @Override
    public void jump() {
        super.jump();
    }

    private int getHandSwingDuration() {
        if (StatusEffectUtil.hasHaste(this)) {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
        } else {
            return this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
        }
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);

        if (getActiveItem().getItem() instanceof FlintAndSteelItem && isUsingItem()) {
            return;
        }

        if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
            punch_index++;
            punch_index %= 2;

            ItemStack stack = getMainHandStack();

            boolean sword_animations = false;

            if (stack != null) {
                if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof PickaxeItem ||
                stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem ||
                stack.getItem() instanceof ShovelItem || stack.getItem() instanceof FishingRodItem) {
                    sword_animations = true;
                }
            }

            if (isSneaking()) {
                if (sword_animations) {
                    playAnimation(anim_sword_swing_sneak[punch_index], 1.0f, 0);
                } else {
                    playAnimation(anim_punch_sneaking[punch_index], 1.0f, 0);
                }
            } else {
                if (sword_animations) {
                    playAnimation(anim_sword_swing[punch_index], 1.0f, 0);
                } else {
                    playAnimation(anim_punch[punch_index], 1.0f, 0);
                }
            }
        }
    }

    public float getLeanAmount() {
        return leanAmount * 0.01f;
    }

    public float getLeanMultiplier() {
        return leanMultiplier;
    }

    public float getSquash() {
        return squash;
    }
}
