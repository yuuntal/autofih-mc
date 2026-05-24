package net.yuntal.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoFishingClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-fishing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private int debounceTick = -1;
    private int castCooldown = 0;
    private boolean hookWasNull = true;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // client null -> return
            if (client.player == null || client.level == null) return;

            ItemStack heldItem = client.player.getItemInHand(InteractionHand.MAIN_HAND);
            if (heldItem.getItem() != Items.FISHING_ROD) {
                debounceTick = -1;
                castCooldown = 0;
                hookWasNull = true;
                return;
            }

            if (debounceTick > 0) {
                debounceTick--;
                return;
            }
            if (debounceTick == 0) {
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                debounceTick = -1;
                return;
            }

            var hook = client.player.fishing;

            // no hook
            if ( hook == null ) {
                hookWasNull = true;
                castCooldown = 0;
                return;
            }

            // hook appeared
            if ( hookWasNull ) {
                hookWasNull = false;
                castCooldown = 40;
            }

            // cast cooldown so skip check for bites
            if ( castCooldown > 0 ) {
                castCooldown--;
                return;
            }

            if (hook.getDeltaMovement().y < -0.08 && hook.isInWater()) {
                LOGGER.info("found fih");
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                hookWasNull = true;
                debounceTick = 20;
            }

            LOGGER.info(String.valueOf(debounceTick));


        });

//        LOGGER.info("client init");
    }
}
