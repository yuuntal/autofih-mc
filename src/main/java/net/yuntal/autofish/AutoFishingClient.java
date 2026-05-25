package net.yuntal.autofish;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class AutoFishingClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-fishing";

    private int recastTimer = -1;
    private int castCooldown = 0;
    private boolean hookWasNull = true;
    private InteractionHand fishingHand = InteractionHand.MAIN_HAND;

    public static boolean biteDetected = false;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AutoFishConfig.class, GsonConfigSerializer::new);

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "fishing_stats"),
            AutoFishingClient::extract
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            InteractionHand activeHand = null;
            if (client.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.FISHING_ROD) {
                activeHand = InteractionHand.MAIN_HAND;
            } else if (client.player.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.FISHING_ROD) {
                activeHand = InteractionHand.OFF_HAND;
            }

            if (activeHand == null) {
                recastTimer = -1;
                castCooldown = 0;
                hookWasNull = true;
                biteDetected = false;
                return;
            }

            fishingHand = activeHand;
            var stack = client.player.getItemInHand(fishingHand);

            AutoFishConfig config = AutoConfig.getConfigHolder(AutoFishConfig.class).getConfig();

            if (!config.enabled) {
                recastTimer = -1;
                castCooldown = 0;
                hookWasNull = true;
                biteDetected = false;
                return;
            }

            if (config.durabilityProtection) {
                int durability = stack.getMaxDamage() - stack.getDamageValue();
                if (stack.isDamageableItem() && durability <= config.minDurability) {
                    recastTimer = -1;
                    castCooldown = 0;
                    hookWasNull = true;
                    biteDetected = false;
                    return;
                }
            }

            if (recastTimer > 0) {
                recastTimer--;
                return;
            }
            if (recastTimer == 0) {
                client.gameMode.useItem(client.player, fishingHand);
                recastTimer = -1;
                return;
            }

            var hook = client.player.fishing;

            if (hook == null) {
                hookWasNull = true;
                biteDetected = false;
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

            if (biteDetected) {
                client.gameMode.useItem(client.player, fishingHand);
                FishingStats.onCatch();
                recastTimer = config.recastDelay;
                biteDetected = false;
            }
        });
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        AutoFishConfig config = AutoConfig.getConfigHolder(AutoFishConfig.class).getConfig();
        if (!config.enabled || config.showHud == AutoFishConfig.HudDisplayMode.NONE) return;

        Minecraft client = Minecraft.getInstance();
        if (config.showHud == AutoFishConfig.HudDisplayMode.WHEN_FISHING) {
            boolean holdingRod = client.player != null && (
                client.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.FISHING_ROD ||
                client.player.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.FISHING_ROD
            );
            if (!holdingRod) return;
        }

        String caughtText = "Caught: " + FishingStats.totalCaught;
        double rate = config.usePerHour ? FishingStats.getCatchPerHour() : FishingStats.getCatchPerMinute();
        String rateText = String.format("%.1f fish/%s", rate, config.usePerHour ? "hr" : "min");

        int x = config.hudXOffset;
        int y = config.hudYOffset;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int line1Width = client.font.width(caughtText);
        int line2Width = client.font.width(rateText);
        int maxTextWidth = Math.max(line1Width, line2Width);

        switch (config.hudAnchor) {
            case TOP_RIGHT:
                x = screenWidth - maxTextWidth - config.hudXOffset;
                break;
            case BOTTOM_LEFT:
                y = screenHeight - 20 - config.hudYOffset; // 20 for two lines of text
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - maxTextWidth - config.hudXOffset;
                y = screenHeight - 20 - config.hudYOffset;
                break;
            default: // TOP_LEFT handled by initial values
                break;
        }

        // Adjust text alignment for lines if they have different widths on the right side
        int line1X = x;
        int line2X = x;
        if (config.hudAnchor == AutoFishConfig.HudAnchor.TOP_RIGHT || config.hudAnchor == AutoFishConfig.HudAnchor.BOTTOM_RIGHT) {
            line1X = x + (maxTextWidth - line1Width);
            line2X = x + (maxTextWidth - line2Width);
        }

        graphics.text(client.font, caughtText, line1X, y, 0xFFFFFFFF, true);
        graphics.text(client.font, rateText, line2X, y + 10, 0xFFAAAAAA, true);
    }
}