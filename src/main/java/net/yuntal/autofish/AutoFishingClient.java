package net.yuntal.autofish;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.InteractionHand;

public class AutoFishingClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-fishing";

    private int recastTimer = -1;
    private int castCooldown = 0;
    private boolean hookWasNull = true;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AutoFishConfig.class, GsonConfigSerializer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            AutoFishConfig config = AutoConfig.getConfigHolder(AutoFishConfig.class).getConfig();

            if (recastTimer > 0) {
                recastTimer--;
                return;
            }
            if (recastTimer == 0) {
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                recastTimer = -1;
                return;
            }

            var hook = client.player.fishing;

            if (hook == null) {
                hookWasNull = true;
                return;
            }

            if (hookWasNull) {
                hookWasNull = false;
                castCooldown = config.castCooldown;
            }

            if (castCooldown > 0) {
                castCooldown--;
                return;
            }

            if (hook.isInWater() && hook.getDeltaMovement().y < config.biteThreshold) {
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                recastTimer = config.recastDelay;
            }
        });
    }
}