package net.yuntal.autofish;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoFishingClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-fishing";

    private int recastTimer = -1;
    private int castCooldown = 0;
    private int inWaterTicks = 0;
    private boolean hookWasNull = true;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AutoFishConfig.class, GsonConfigSerializer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            ItemStack heldItem = client.player.getItemInHand(InteractionHand.MAIN_HAND);
            if (heldItem.getItem() != Items.FISHING_ROD) {
                recastTimer = -1;
                castCooldown = 0;
                inWaterTicks = 0;
                hookWasNull = true;
                return;
            }

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
                inWaterTicks = 0;
                return;
            }

            // new cast
            if (hookWasNull) {
                hookWasNull = false;
                castCooldown = config.castCooldown;
                inWaterTicks = 0;
            }

            if (castCooldown > 0) {
                castCooldown--;
                return;
            }

            if (hook.isInWater()) {
                inWaterTicks++;
            }

            if (inWaterTicks < 10) return;


            if (hook.getDeltaMovement().y < config.biteThreshold) {
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                recastTimer = config.recastDelay;
                inWaterTicks = 0;
            }
        });
    }
}