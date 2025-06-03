package dev.heygrey.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "look-at-my-arms")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean modEnabled = true;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean modEnabledAlert = true;

  public static void init() {
    AutoConfig.register(Configuration.class, Toml4jConfigSerializer::new);
  }

  public static Configuration getInstance() {
    return AutoConfig.getConfigHolder(Configuration.class).getConfig();
  }
}
