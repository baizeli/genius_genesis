package miku.united_as_one.genesis.client.render.armor;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.item.armor.GenesisGeoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CelestialSourceRingModel extends GeoModel<GenesisGeoArmorItem> {
    @Override
    public ResourceLocation getModelResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("geo/armor/celestial_source_spell_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("textures/models/armor/celestial_source_spell_ring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("animations/armor/celestial_source_spell_armor.animation.json");
    }
}
