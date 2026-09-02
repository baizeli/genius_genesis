package miku.united_as_one.genesis.content.item;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

import static java.lang.Math.sqrt;
import static net.minecraft.util.Mth.square;

@SuppressWarnings("removal")
public class FlyingSwallowThroughWillow extends SwordItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FlyingSwallowThroughWillow() {
        super(
                new ForgeTier(
                        0,
                        1451,
                        12.0F,
                        0.0F,
                        35,
                        BlockTags.NEEDS_STONE_TOOL,
                        () -> Ingredient.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation("irons_spellbooks", "arcane_ingot")))
                ),
                0,
                0.0F,
                new Item.Properties().rarity(Rarity.EPIC).fireResistant()
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 lookVec = player.getLookAngle();
        player.push(lookVec.x * 3.0D, lookVec.y * 3.0D, lookVec.z * 3.0D);

        if (level instanceof ServerLevel serverLevel) {
            Vec3 start = player.position();
            Vec3 end = start.add(lookVec.scale(6.0D));
            drawLine(0.01D, end, start, ParticleTypes.CLOUD, serverLevel);
        }

        player.getPersistentData().putLong("FlyingSwallowFallImmunity", level.getGameTime() + 60L);
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void drawLine(double interval, Vec3 to, Vec3 from, SimpleParticleType type, ServerLevel level) {
        double deltaX = to.x - from.x;
        double deltaY = to.y - from.y;
        double deltaZ = to.z - from.z;
        double length = sqrt(square(deltaX) + square(deltaY) + square(deltaZ));
        int amount = (int) (length / interval);
        for (int i = 0; i <= amount; i++) {
            level.sendParticles(type, from.x + deltaX * i / amount, from.y + deltaY * i / amount, from.z + deltaZ * i / amount, 0, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<FlyingSwallowThroughWillow> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new GeoItemRenderer<>(new GeoModel<>() {
                        @Override
                        public ResourceLocation getModelResource(FlyingSwallowThroughWillow animatable) {
                            return new ResourceLocation(Genesis.MOD_ID, "geo/item/flying_swallow_through_willow.geo.json");
                        }

                        @Override
                        public ResourceLocation getTextureResource(FlyingSwallowThroughWillow animatable) {
                            return new ResourceLocation(Genesis.MOD_ID, "textures/item/flying_swallow_through_willow_models.png");
                        }

                        @Override
                        public ResourceLocation getAnimationResource(FlyingSwallowThroughWillow animatable) {
                            return new ResourceLocation(Genesis.MOD_ID, "animations/item/flying_swallow_through_willow.animation.json");
                        }
                    });
                }
                return renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }
}
