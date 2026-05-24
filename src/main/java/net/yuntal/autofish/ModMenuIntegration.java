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

            general.addEntry(entryBuilder
                    .startDoubleField(Component.literal("Bite Threshold"), config.biteThreshold)
                    .setDefaultValue(-0.08)
                    .setMin(-1.0)
                    .setMax(0.0)
                    .setTooltip(Component.literal("Downward velocity needed to trigger a reel-in"))
                    .setSaveConsumer(val -> config.biteThreshold = val)
                    .build());

            return builder.build();
        };
    }
}