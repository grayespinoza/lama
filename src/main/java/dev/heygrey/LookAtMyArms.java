package dev.heygrey;

import dev.heygrey.config.LookAtMyArmsConfiguration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class LookAtMyArms implements ClientModInitializer {
  private static KeyMapping toggleHideKey;

  @Override
  public void onInitializeClient() {
    LookAtMyArmsConfiguration.init();
    LookAtMyArmsConfiguration lamaConfiguration = LookAtMyArmsConfiguration.getInstance();

    toggleHideKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.look-at-my-arms.toggle_hide",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-at-my-arms"));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleHideKey.consumeClick()) {
            lamaConfiguration.hide = !lamaConfiguration.hide;
          }
        });
  }
}
