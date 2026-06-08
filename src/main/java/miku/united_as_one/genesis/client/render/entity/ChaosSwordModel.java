package miku.united_as_one.genesis.client.render.entity;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.chaos.ChaosSwordEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class ChaosSwordModel extends GeoModel<ChaosSwordEntity> {
    private static final ResourceLocation TEXTURE = Genesis.id("textures/entity/fiery_dagger.png");
    private static final ResourceLocation MODEL = new ResourceLocation("irons_spellbooks", "geo/fiery_dagger.geo.json");

    @Override
    public ResourceLocation getModelResource(ChaosSwordEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ChaosSwordEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ChaosSwordEntity animatable) {
        return AbstractSpellCastingMob.animationInstantCast;
    }

    @Override
    public @Nullable RenderType getRenderType(ChaosSwordEntity animatable, ResourceLocation texture) {
        return RenderHelper.CustomerRenderType.magic(TEXTURE);
    }
}
