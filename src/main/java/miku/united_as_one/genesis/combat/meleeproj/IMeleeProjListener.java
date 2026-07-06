/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package miku.united_as_one.genesis.combat.meleeproj;

import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IMeleeProjListener {
    default public boolean onMeleeSwingStart(MeleeProjBase proj, Level level, @Nullable LivingEntity user) {
        return false;
    }

    default public boolean onMeleeSwingHalf(MeleeProjBase proj, Level level, @Nullable LivingEntity user) {
        return false;
    }

    default public boolean onMeleeSwingEnd(MeleeProjBase proj, Level level, @Nullable LivingEntity user) {
        return false;
    }

    default public boolean onHitTarget(MeleeProjBase proj, Level level, @Nullable LivingEntity user, @Nullable LivingEntity target) {
        return false;
    }

    default public boolean onHitTarget(MeleeProjBase proj, Level level, @Nullable LivingEntity user, @Nullable LivingEntity target, int hitType) {
        return false;
    }

    default public boolean onDisplayDamage(MeleeProjBase proj, Level level, @Nullable LivingEntity target, float healthBefore, float absorbBefore) {
        return false;
    }
}


