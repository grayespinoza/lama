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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
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
    if (LookAtMyArmsConfiguration.getInstance().affectsLeftArm) {
      ((PlayerEntityModel) model).leftArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsLeftLeg) {
      ((PlayerEntityModel) model).leftLeg.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsRightArm) {
      ((PlayerEntityModel) model).rightArm.visible = false;
    }
    if (LookAtMyArmsConfiguration.getInstance().affectsRightLeg) {
      ((PlayerEntityModel) model).rightLeg.visible = false;
    }
    original.call(model, matrices, vertices, light, overlay, color);
  }
}
