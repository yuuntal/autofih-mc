package net.yuntal.autofish;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "auto-fishing")
public class AutoFishConfig implements ConfigData {
    public boolean enabled = true;
    public int recastDelay = 20;
    public int castCooldown = 40;
}