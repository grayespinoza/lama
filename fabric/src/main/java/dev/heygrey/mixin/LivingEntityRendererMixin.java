package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.heygrey.config.LookAtMyArmsConfiguration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<
    T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
  @WrapOperation(
      method =
          "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
  private void wrapRender(
      M model,
      MatrixStack matrices,
      VertexConsumer vertices,
      int light,
      int overlay,
      int color,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    if (!(model instanceof PlayerEntityModel)
        || !(state instanceof PlayerEntityRenderState playerState)) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }
    if (!LookAtMyArmsConfiguration.getInstance().modEnabled) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }
    if (MinecraftClient.getInstance().currentScreen != null) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }
    boolean isSelf =
        playerState.name.equals(MinecraftClient.getInstance().player.getName().getString());
    if (!isSelf) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }
    boolean isFirstPerson =
        MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    if (!isFirstPerson) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsBody) {
      ((PlayerEntityModel) model).body.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsHead) {
      ((PlayerEntityModel) model).head.visible = false;
    }
    boolean isLeftHanded = MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT;
    if (LookAtMyArmsConfiguration.getInstance().affectsLeftArm
        && !LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands) {
      ((PlayerEntityModel) model).leftArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsLeftArm
            && LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands
            && isLeftHanded
            && MinecraftClient.getInstance().player.getMainHandStack().isEmpty()
        || LookAtMyArmsConfiguration.getInstance().affectsLeftArm
            && LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands
            && !isLeftHanded
            && MinecraftClient.getInstance().player.getOffHandStack().isEmpty()) {
      ((PlayerEntityModel) model).leftArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsLeftLeg) {
      ((PlayerEntityModel) model).leftLeg.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsRightArm
        && !LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands) {
      ((PlayerEntityModel) model).rightArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsRightArm
            && LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands
            && isLeftHanded
            && MinecraftClient.getInstance().player.getOffHandStack().isEmpty()
        || LookAtMyArmsConfiguration.getInstance().affectsRightArm
            && LookAtMyArmsConfiguration.getInstance().affectsOnlyEmptyHands
            && !isLeftHanded
            && MinecraftClient.getInstance().player.getMainHandStack().isEmpty()) {
      ((PlayerEntityModel) model).rightArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsRightLeg) {
      ((PlayerEntityModel) model).rightLeg.visible = false;
    }
    original.call(model, matrices, vertices, light, overlay, color);
  }

  @WrapOperation(
      method =
          "getShadowRadius(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;)F",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/entity/EntityRenderer;getShadowRadius(Lnet/minecraft/client/render/entity/state/EntityRenderState;)F"))
  private float wrapGetShadowRadius(
      LivingEntityRenderer renderer, EntityRenderState state, Operation<Float> original) {
    if (!(state instanceof PlayerEntityRenderState playerState)) {
      return original.call(renderer, state);
    }
    if (!LookAtMyArmsConfiguration.getInstance().modEnabled) {
      return original.call(renderer, state);
    }
    if (MinecraftClient.getInstance().currentScreen != null) {
      return original.call(renderer, state);
    }
    boolean isSelf =
        playerState.name.equals(MinecraftClient.getInstance().player.getName().getString());
    if (!isSelf) {
      return original.call(renderer, state);
    }
    boolean isFirstPerson =
        MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    if (!isFirstPerson) {
      return original.call(renderer, state);
    }
    if (!LookAtMyArmsConfiguration.getInstance().affectsPlayerShadow) {
      return original.call(renderer, state);
    }
    return 0.0f;
  }
}
