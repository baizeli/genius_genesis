package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.holy.WispSpell;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = WispSpell.class, remap = false)
public abstract class MixinWispSpell extends AbstractSpell {
    @Override
    public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {
        float power = super.getSpellPower(spellLevel, sourceEntity);
        if (sourceEntity instanceof LivingEntity entity && ModCurios.hasCurios(entity, RunePlusItem::isHoly)) {
            power *= 1.5F;
        }
        return power;
    }
}
