package dev.heygrey;

import dev.heygrey.config.LookAtMyArmsConfiguration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class LookAtMyArms implements ClientModInitializer {
  private static KeyBinding toggleHideKey;

  @Override
  public void onInitializeClient() {
    LookAtMyArmsConfiguration.init();
    LookAtMyArmsConfiguration lamaConfiguration = LookAtMyArmsConfiguration.getInstance();

    toggleHideKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.look-at-my-arms.toggle_hide",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-at-my-arms"));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleHideKey.wasPressed()) {
            lamaConfiguration.hide = !lamaConfiguration.hide;
          }
        });
  }
}
