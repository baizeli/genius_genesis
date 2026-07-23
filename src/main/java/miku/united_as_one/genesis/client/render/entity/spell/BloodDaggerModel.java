package miku.united_as_one.genesis.client.render.entity.spell;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.blood_boss.blood_dagger.BloodDaggerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class BloodDaggerModel extends GeoModel<BloodDaggerEntity> {
    private static final ResourceLocation TEXTURE = Genesis.id("textures/entity/fiery_dagger.png");
    private static final ResourceLocation MODEL = new ResourceLocation("irons_spellbooks", "geo/fiery_dagger.geo.json");

    @Override
    public ResourceLocation getModelResource(BloodDaggerEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BloodDaggerEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BloodDaggerEntity animatable) {
        return AbstractSpellCastingMob.animationInstantCast;
    }

    @Override
    public @Nullable RenderType getRenderType(BloodDaggerEntity animatable, ResourceLocation texture) {
        return RenderHelper.CustomerRenderType.magic(TEXTURE);
    }
}
