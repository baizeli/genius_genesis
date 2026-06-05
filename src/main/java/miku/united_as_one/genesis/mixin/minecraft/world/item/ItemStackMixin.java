package miku.united_as_one.genesis.mixin.minecraft.world.item;

import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void geniusGenesis$getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        OptionalInt durability = EquipmentStatsManager.getConfiguredDurability(self());
        if (durability.isPresent()) {
            cir.setReturnValue(durability.getAsInt());
        }
    }

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void geniusGenesis$isDamageableItem(CallbackInfoReturnable<Boolean> cir) {
        OptionalInt durability = EquipmentStatsManager.getConfiguredDurability(self());
        if (durability.isEmpty()) {
            return;
        }
        CompoundTag tag = self().getTag();
        boolean unbreakable = tag != null && tag.getBoolean("Unbreakable");
        cir.setReturnValue(durability.getAsInt() > 0 && !unbreakable);
    }

    private ItemStack self() {
        return (ItemStack) (Object) this;
    }
}
