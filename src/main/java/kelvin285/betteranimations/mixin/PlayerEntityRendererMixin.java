package kelvin285.betteranimations.mixin;

import kelvin285.betteranimations.IPlayerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Shadow protected abstract void setModelPose(AbstractClientPlayerEntity player);

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", cancellable = true)
    public void render(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {

        if (!MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson() && player == MinecraftClient.getInstance().player) {
            return;
        }

        matrixStack.push();

        float lean_x = (float)player.getVelocity().z;
        float lean_z = -(float)player.getVelocity().x;

        float turnLeanAmount = ((IPlayerAccessor)player).getLeanAmount();
        float leanMultiplier = ((IPlayerAccessor)player).getLeanMultiplier();
        float player_squash = ((IPlayerAccessor)player).getSquash();
        player_squash = MathHelper.clamp(player_squash, -1, 1) * 0.25f;

        float h_scale = 1;
        float v_scale = 1;

        h_scale = MathHelper.lerp(player_squash, 1, 0.5f);
        v_scale = MathHelper.lerp(player_squash, 1, 1.5f);

        float yaw = (float)Math.toRadians(player.bodyYaw + 90);
        lean_x += Math.cos(yaw) * turnLeanAmount;
        lean_z += Math.sin(yaw) * turnLeanAmount;

        lean_x *= leanMultiplier;
        lean_z *= leanMultiplier;

        if (player.isFallFlying()) {
            lean_x = 0;
            lean_z = 0;
            h_scale = 1.0f;
            v_scale = 1.0f;
        }

        Quaternionf quat = new Quaternionf();
        quat = new Matrix4f().rotate(lean_x, new Vector3f(1, 0, 0)).rotate(lean_z, new Vector3f(0, 0, 1)).getNormalizedRotation(quat);

        matrixStack.multiply(quat);
        matrixStack.scale(h_scale, v_scale, h_scale);
        this.setModelPose(player);
        super.render(player, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();

        info.cancel();
    }
}
