package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.heygrey.config.LookAtMyArmsConfiguration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<
    S extends BipedEntityRenderState,
    M extends BipedEntityModel<S>,
    A extends BipedEntityModel<S>> {
  @Shadow
  protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

  @WrapOperation(
      method =
          "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V"))
  private void wrapRenderArmor(
      ArmorFeatureRenderer renderer,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      ItemStack stack,
      EquipmentSlot slot,
      int light,
      A armorModel,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    if (!(state instanceof PlayerEntityRenderState playerState)) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }
    if (!LookAtMyArmsConfiguration.getInstance().modEnabled) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }
    if (MinecraftClient.getInstance().currentScreen != null) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }
    boolean isSelf =
        playerState.name.equals(MinecraftClient.getInstance().player.getName().getString());
    if (!isSelf) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }
    boolean isFirstPerson =
        MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    if (!isFirstPerson) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }
    setVisible(armorModel, slot);
  }
}
