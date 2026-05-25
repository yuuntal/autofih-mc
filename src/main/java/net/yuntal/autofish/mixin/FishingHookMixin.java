package net.yuntal.autofish.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.FishingHook;
import net.yuntal.autofish.AutoFishingClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_BITING;

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void onBite(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        if (DATA_BITING.equals(accessor)) {
            FishingHook hook = (FishingHook) (Object) this;
            if (hook.getEntityData().get(DATA_BITING)) {
                if (hook.getPlayerOwner() == Minecraft.getInstance().player) {
                    AutoFishingClient.biteDetected = true;
                }
            }
        }
    }
}