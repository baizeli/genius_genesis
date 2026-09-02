package miku.united_as_one.genesis.client.render.armor;

import miku.united_as_one.genesis.content.item.armor.GenesisGeoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GenesisArmorRenderer extends GeoArmorRenderer<GenesisGeoArmorItem> {
    public GenesisArmorRenderer() {
        super(new GenesisArmorModel());
    }
}
