package miku.united_as_one.genesis.client.render.armor;

import miku.united_as_one.genesis.content.item.armor.GenesisGeoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class GenesisArmorRenderers {
    private GenesisArmorRenderers() {
    }

    public static GeoArmorRenderer<?> create(GenesisGeoArmorItem armor) {
        GenesisArmorRenderer renderer = new GenesisArmorRenderer();
        if (armor.isCelestialSourceSpell()) {
            renderer.addRenderLayer(new CelestialSourceRingLayer(renderer));
        }
        return renderer;
    }
}
