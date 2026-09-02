package miku.united_as_one.genesis.content.item.armor;

import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.item.armor.IronsExtendedArmorMaterial;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.render.armor.ChaosSpellArmorModel;
import miku.united_as_one.genesis.client.tooltip.GenesisArmorTooltips;
import miku.united_as_one.genesis.content.item.GenesisArmorMaterials;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChaosSpellArmorItem extends ExtendedArmorItem implements GenesisArmorPiece {
    private static final String ARMOR_TEXTURE = Genesis.MOD_ID + ":textures/models/armor/chaos_spell.png";

    private final GenesisArmorMaterials genesisMaterial;

    public ChaosSpellArmorItem(GenesisArmorMaterials material, Type type, Properties properties) {
        super((IronsExtendedArmorMaterial) material, type, properties);
        this.genesisMaterial = material;
    }

    @Override
    public GenesisArmorMaterials genesisMaterial() {
        return genesisMaterial;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        GenesisArmorTooltips.append(this, tooltip);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final Map<Type, ChaosSpellArmorModel<LivingEntity>> models = new EnumMap<>(Type.class);

            @Override
            public Model getGenericArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                Type armorType = ChaosSpellArmorItem.this.getType();
                ChaosSpellArmorModel<LivingEntity> model = models.computeIfAbsent(armorType, ignored ->
                        new ChaosSpellArmorModel<>(ChaosSpellArmorModel.createBodyLayer().bakeRoot()));
                model.copyFrom(original, slot);
                return model;
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return null;
    }
}
