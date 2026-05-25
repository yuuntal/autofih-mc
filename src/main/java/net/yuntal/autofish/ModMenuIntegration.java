package net.yuntal.autofish;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            AutoFishConfig config = AutoConfig.getConfigHolder(AutoFishConfig.class).getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Auto Fishing Config"))
                    .setSavingRunnable(() ->
                            AutoConfig.getConfigHolder(AutoFishConfig.class).save()
                    );

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

            general.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Component.literal("Whether the auto-fishing logic should be active"))
                    .setSaveConsumer(val -> config.enabled = val)
                    .build());

            general.addEntry(entryBuilder
                    .startIntField(Component.literal("Recast Delay (ticks)"), config.recastDelay)
                    .setDefaultValue(20)
                    .setMin(1)
                    .setMax(200)
                    .setTooltip(Component.literal("How long to wait before recasting after reeling in"))
                    .setSaveConsumer(val -> config.recastDelay = val)
                    .build());

            general.addEntry(entryBuilder
                    .startIntField(Component.literal("Cast Cooldown (ticks)"), config.castCooldown)
                    .setDefaultValue(40)
                    .setMin(1)
                    .setMax(200)
                    .setTooltip(Component.literal("How long to wait after casting before monitoring for bites"))
                    .setSaveConsumer(val -> config.castCooldown = val)
                    .build());

            ConfigCategory protection = builder.getOrCreateCategory(Component.literal("Protection"));

            protection.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Durability Protection"), config.durabilityProtection)
                    .setDefaultValue(true)
                    .setTooltip(Component.literal("Stop fishing if the rod's durability is too low"))
                    .setSaveConsumer(val -> config.durabilityProtection = val)
                    .build());

            protection.addEntry(entryBuilder
                    .startIntField(Component.literal("Min Durability"), config.minDurability)
                    .setDefaultValue(5)
                    .setMin(1)
                    .setMax(100)
                    .setTooltip(Component.literal("Minimum durability required to keep fishing"))
                    .setSaveConsumer(val -> config.minDurability = val)
                    .build());

            ConfigCategory hud = builder.getOrCreateCategory(Component.literal("HUD"));

            hud.addEntry(entryBuilder
                    .startEnumSelector(Component.literal("HUD Display Mode"), AutoFishConfig.HudDisplayMode.class, config.showHud)
                    .setDefaultValue(AutoFishConfig.HudDisplayMode.WHEN_FISHING)
                    .setTooltip(Component.literal("When to show the fishing tracker overlay"))
                    .setSaveConsumer(val -> config.showHud = val)
                    .build());

            hud.addEntry(entryBuilder
                    .startEnumSelector(Component.literal("HUD Anchor"), AutoFishConfig.HudAnchor.class, config.hudAnchor)
                    .setDefaultValue(AutoFishConfig.HudAnchor.TOP_LEFT)
                    .setTooltip(Component.literal("Which corner of the screen to anchor the HUD to"))
                    .setSaveConsumer(val -> config.hudAnchor = val)
                    .build());

            hud.addEntry(entryBuilder
                    .startIntField(Component.literal("HUD X Offset"), config.hudXOffset)
                    .setDefaultValue(10)
                    .setMin(-1000)
                    .setMax(1000)
                    .setTooltip(Component.literal("Horizontal distance from the anchor corner"))
                    .setSaveConsumer(val -> config.hudXOffset = val)
                    .build());

            hud.addEntry(entryBuilder
                    .startIntField(Component.literal("HUD Y Offset"), config.hudYOffset)
                    .setDefaultValue(10)
                    .setMin(-1000)
                    .setMax(1000)
                    .setTooltip(Component.literal("Vertical distance from the anchor corner"))
                    .setSaveConsumer(val -> config.hudYOffset = val)
                    .build());

            hud.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Show Rate per Hour"), config.usePerHour)
                    .setDefaultValue(true)
                    .setTooltip(Component.literal("Toggle between fish/hr and fish/min"))
                    .setSaveConsumer(val -> config.usePerHour = val)
                    .build());

            hud.addEntry(entryBuilder
                    .startTextDescription(Component.literal("Stats are reset when you restart the game."))
                    .build());

            return builder.build();
        };
    }
}