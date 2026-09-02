package miku.united_as_one.genesis.client.render.entity.spell;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.render.entity.laser.AbstractLaserRenderer;
import miku.united_as_one.genesis.content.entity.spell.thunder.DeathLaserEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathLaserRenderer extends AbstractLaserRenderer<DeathLaserEntity> {
    public DeathLaserRenderer(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @SuppressWarnings("removal")
    @Override
    protected ResourceLocation getLaserTexture() {
        return new ResourceLocation(Genesis.MOD_ID, "textures/entity/laser/death_beam.png");
    }
}