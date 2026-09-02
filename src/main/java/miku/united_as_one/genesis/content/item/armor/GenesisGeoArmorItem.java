package miku.united_as_one.genesis.content.item.armor;

import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import miku.united_as_one.genesis.client.render.armor.GenesisArmorRenderers;
import miku.united_as_one.genesis.client.tooltip.GenesisArmorTooltips;
import miku.united_as_one.genesis.content.item.GenesisArmorMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class GenesisGeoArmorItem extends ExtendedArmorItem implements GenesisArmorPiece {
    private final GenesisArmorMaterials genesisMaterial;

    public GenesisGeoArmorItem(GenesisArmorMaterials material, Type type, Properties properties) {
        super(material, type, properties);
        this.genesisMaterial = material;
    }

    public String armorId() {
        return genesisMaterial.getName();
    }

    public GenesisArmorMaterials genesisMaterial() {
        return genesisMaterial;
    }

    public boolean isCelestialSourceSpell() {
        return genesisMaterial == GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        GenesisArmorTooltips.append(this, tooltip);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return GenesisArmorRenderers.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (isCelestialSourceSpell()) {
            controllers.add(new AnimationController<>(
                    this,
                    "celestial_source_spell_ring",
                    0,
                    state -> {
                        state.getController().setAnimation(
                                RawAnimation.begin().thenLoop("celestial_source_spell_ring.animation")
                        );
                        return PlayState.CONTINUE;
                    }
            ));
        }
    }
}
