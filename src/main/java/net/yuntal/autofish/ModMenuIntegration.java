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

            return builder.build();
        };
    }
}