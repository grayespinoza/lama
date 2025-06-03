package dev.heygrey;

import dev.heygrey.config.Configuration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class LookAtMyArms implements ClientModInitializer {
  private static KeyBinding toggleModEnabledKey;

  @Override
  public void onInitializeClient() {
    Configuration.init();
    toggleModEnabledKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.look-at-my-arms.toggle_mod_enabled",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-at-my-arms"));
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleModEnabledKey.wasPressed()) {
            Configuration.getInstance().modEnabled = !Configuration.getInstance().modEnabled;
            if (Configuration.getInstance().modEnabledAlert) {
              if (Configuration.getInstance().modEnabled) {
                client.player.sendMessage(Text.literal("Look At My Arms Enabled"), true);
              } else {
                client.player.sendMessage(Text.literal("Look At My Arms Disabled"), true);
              }
            }
          }
        });
  }
}
