package net.yuntal.autofish;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "auto-fishing")
public class AutoFishConfig implements ConfigData {
    public boolean enabled = true;
    public int recastDelay = 20;
    public int castCooldown = 40;
    public boolean durabilityProtection = true;
    public int minDurability = 5;
    public HudDisplayMode showHud = HudDisplayMode.WHEN_FISHING;
    public HudAnchor hudAnchor = HudAnchor.TOP_LEFT;
    public int hudXOffset = 10;
    public int hudYOffset = 10;
    public boolean usePerHour = true;

    public enum HudDisplayMode {
        NONE, WHEN_FISHING, ALWAYS
    }

    public enum HudAnchor {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}