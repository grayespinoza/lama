package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.heygrey.config.LookAtMyArmsConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<
    T extends LivingEntity,
    S extends LivingEntityRenderState,
    M extends EntityModel<? super S>> {

  @WrapOperation(
      method =
          "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
  private void wrapRender(
      M model,
      PoseStack matrices,
      VertexConsumer vertices,
      int light,
      int overlay,
      int color,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    LookAtMyArmsConfiguration lamaConfiguration = LookAtMyArmsConfiguration.getInstance();
    Minecraft client = Minecraft.getInstance();

    if (!lamaConfiguration.hide) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    if (!(model instanceof PlayerModel)
        || !(state instanceof PlayerRenderState playerState)) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    if (client.screen != null) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    boolean isSelf = playerState.name.equals(client.player.getName().getString());
    if (!isSelf) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    boolean isFirstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
    if (!isFirstPerson) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    if (lamaConfiguration.affectsBody) {
      ((PlayerModel) model).body.visible = false;
    }

    if (lamaConfiguration.affectsHead) {
      ((PlayerModel) model).head.visible = false;
    }

    boolean isLeftHanded = client.player.getMainArm() == HumanoidArm.LEFT;
    if (lamaConfiguration.affectsLeftArm && !lamaConfiguration.affectsOnlyEmptyHands) {
      ((PlayerModel) model).leftArm.visible = false;
    }

    if (lamaConfiguration.affectsLeftArm
            && lamaConfiguration.affectsOnlyEmptyHands
            && isLeftHanded
            && client.player.getMainHandItem().isEmpty()
        || lamaConfiguration.affectsLeftArm
            && lamaConfiguration.affectsOnlyEmptyHands
            && !isLeftHanded
            && client.player.getOffhandItem().isEmpty()) {
      ((PlayerModel) model).leftArm.visible = false;
    }

    if (lamaConfiguration.affectsLeftLeg) {
      ((PlayerModel) model).leftLeg.visible = false;
    }

    if (lamaConfiguration.affectsRightArm && !lamaConfiguration.affectsOnlyEmptyHands) {
      ((PlayerModel) model).rightArm.visible = false;
    }

    if (lamaConfiguration.affectsRightArm
            && lamaConfiguration.affectsOnlyEmptyHands
            && isLeftHanded
            && client.player.getOffhandItem().isEmpty()
        || lamaConfiguration.affectsRightArm
            && lamaConfiguration.affectsOnlyEmptyHands
            && !isLeftHanded
            && client.player.getMainHandItem().isEmpty()) {
      ((PlayerModel) model).rightArm.visible = false;
    }

    if (lamaConfiguration.affectsRightLeg) {
      ((PlayerModel) model).rightLeg.visible = false;
    }

    original.call(model, matrices, vertices, light, overlay, color);
  }

  @WrapOperation(
      method =
          "getShadowRadius(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)F",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowRadius(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
  private float wrapGetShadowRadius(
      LivingEntityRenderer renderer, EntityRenderState state, Operation<Float> original) {
    Minecraft client = Minecraft.getInstance();

    if (!LookAtMyArmsConfiguration.getInstance().hide) {
      return original.call(renderer, state);
    }

    if (!(state instanceof PlayerRenderState playerState)) {
      return original.call(renderer, state);
    }

    if (client.screen != null) {
      return original.call(renderer, state);
    }

    boolean isSelf = playerState.name.equals(client.player.getName().getString());
    if (!isSelf) {
      return original.call(renderer, state);
    }

    boolean isFirstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
    if (!isFirstPerson) {
      return original.call(renderer, state);
    }

    if (!LookAtMyArmsConfiguration.getInstance().affectsPlayerShadow) {
      return original.call(renderer, state);
    }

    return 0.0f;
  }
}
