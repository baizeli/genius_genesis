package miku.united_as_one.genesis.client.render.armor;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.item.armor.GenesisGeoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenesisArmorModel extends GeoModel<GenesisGeoArmorItem> {
    @Override
    public ResourceLocation getModelResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("geo/armor/" + armor.armorId() + "_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("textures/models/armor/" + armor.armorId() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenesisGeoArmorItem armor) {
        return Genesis.rl("animations/armor/" + armor.armorId() + "_armor.animation.json");
    }
}
