package miku.united_as_one.genesis.client;

import com.tterrag.registrate.util.entry.FluidEntry;
import miku.bai_ze_li.genesis.api.render.particle.GlowCubeParticle;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderEffect;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.item.Scroll;
import miku.united_as_one.genesis.client.render.cosmic.CosmicModelLoader;
import miku.united_as_one.genesis.client.spellhud.SpellCardHudClientEvents;
import miku.united_as_one.genesis.client.render.block.ArcaneCauldronRenderer;
import miku.united_as_one.genesis.client.render.WingLayer;
import miku.united_as_one.genesis.client.render.armor.ChaosSpellArmorGlowLayer;
import miku.united_as_one.genesis.client.render.entity.spell.*;
import miku.united_as_one.genesis.client.render.entity.NoopRenderer;
import miku.united_as_one.genesis.client.render.entity.player.PlayerShadowRenderer;
import miku.united_as_one.genesis.client.particle.OverlordParticle;
import miku.united_as_one.genesis.content.fluid.FluidRegistry;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.workbench.registry.BlockEntityRegistry;
import miku.united_as_one.genesis.workbench.registry.MenuTypeRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Vector4f;

import java.util.stream.Stream;

public final class ClientSetup {

    private ClientSetup() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientSetup::clientSetup);
        modBus.addListener(ClientSetup::registerGeometryLoaders);
        modBus.addListener(ClientSetup::registerEntityRenderers);
        modBus.addListener(ClientSetup::addPlayerLayers);
        modBus.addListener(ClientSetup::registerParticleProviders);
        modBus.addListener(SpellCardHudClientEvents::registerKeyMapping);
        modBus.addListener(SpellCardHudClientEvents::registerOverlay);
        MinecraftForge.EVENT_BUS.addListener(SpellCardHudClientEvents::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(SpellCardHudClientEvents::onLoggingOut);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onRenderHand);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onRightClickItem);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onEntityInteractSpecific);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onLeftClickBlock);
        MinecraftForge.EVENT_BUS.addListener(AutoSwingClientEvents::onAttackEntity);
        MinecraftForge.EVENT_BUS.addListener(PlayerShadowRenderer::onClientLoggingOut);
        MinecraftForge.EVENT_BUS.addListener(PlayerShadowRenderer::onPlayerRenderPost);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Stream.of(
                ItemRegistry.THUNDER_LONGBOW.get(),
                ItemRegistry.FROST_LONGBOW.get(),
                ItemRegistry.WITCHCRAFT_BOW.get(),
                ItemRegistry.FLAME_BOW.get()
        ).forEach(ClientSetup::registerBowProperties));
        event.enqueueWork(() -> MenuScreens.register(MenuTypeRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new));
        event.enqueueWork(ClientSetup::registerScrollShaderResolvers);
        event.enqueueWork(ClientSetup::registerFluidRenderLayers);
    }

    private static void registerScrollShaderResolvers() {
        GenesisItemShaderRegistry.registerResolver(stack -> {
            if (!(stack.getItem() instanceof Scroll)) {
                return null;
            }
            SchoolType schoolType = ISpellContainer.getOrCreate(stack).getSpellAtIndex(0).getSpell().getSchoolType();
            if (schoolType.equals(SpellSchoolRegistry.CELESTIAL_SOURCE.get())) {
                return new GenesisItemShaderEffect(15, 0.6F, new Vector4f(0.1F, 0.1F, 0.1F, 1.0F));
            }
            return null;
        });
    }

    private static void registerBowProperties(Item bow) {
        ItemProperties.register(bow, new ResourceLocation("pull"), (stack, level, livingEntity, seed) -> {
            if (livingEntity == null || livingEntity.getUseItem() != stack) {
                return 0.0F;
            }
            return (float) (stack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });

        ItemProperties.register(bow, new ResourceLocation("pulling"), (stack, level, livingEntity, seed) ->
                livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    private static void registerFluidRenderLayers() {
        RenderType renderType = RenderType.translucent();
        registerFluidRenderLayer(FluidRegistry.SOURCE_FLUID, renderType);
        registerFluidRenderLayer(FluidRegistry.BLACKWATER_FLUID, renderType);
        registerFluidRenderLayer(FluidRegistry.BLOOD_FLUID, renderType);
    }

    private static void registerFluidRenderLayer(FluidEntry<? extends ForgeFlowingFluid> fluid, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(fluid.getSource(), renderType);
        fluid.getBlock().ifPresent(block -> ItemBlockRenderTypes.setRenderLayer(block, renderType));
    }

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("cosmic", CosmicModelLoader.INSTANCE);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.PROTECTED_ZOMBIE_DUMMY.get(), ZombieRenderer::new);
        // blood boss entities
        event.registerEntityRenderer(EntityRegistry.BLOOD_DAGGER_PROJECTILE.get(), BloodDaggerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BLOOD_FIELD.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BLOOD_BOSS_FIRE_ERUPTION_AOE.get(), NoopRenderer::new);

        event.registerEntityRenderer(EntityRegistry.METEOR_PROJECTILE.get(), MeteorProjectileRenderer::new);
        event.registerEntityRenderer(EntityRegistry.METEOR_STAR.get(), MeteorStarRenderer::new);
        event.registerEntityRenderer(EntityRegistry.METEOR_SHOCKWAVE.get(), MeteorShockwaveRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DEATH_LASER.get(), DeathLaserRenderer::new);

        event.registerEntityRenderer(EntityRegistry.MITHRIL_IMPACT_RING.get(), MithrilImpactRingRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MITHRIL_MELEE_SLASH.get(), MithrilMeleeSlashRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MELEE_PROJ_BASE.get(), MeleeProjBaseRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MELEE_DAMAGE_TEXT.get(), MeleeDamageTextRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARCANE_CAULDRON.get(), ArcaneCauldronRenderer::new);
    }

    private static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        addWingLayer(event, "default");
        addWingLayer(event, "slim");
        addChaosArmorGlowLayer(event, "default");
        addChaosArmorGlowLayer(event, "slim");
        addChaosArmorStandGlowLayer(event);
    }

    private static void addWingLayer(EntityRenderersEvent.AddLayers event, String skinName) {
        LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin(skinName);
        if (renderer != null) {
            renderer.addLayer(new WingLayer(renderer));
        }
    }

    private static void addChaosArmorGlowLayer(EntityRenderersEvent.AddLayers event, String skinName) {
        LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin(skinName);
        if (renderer != null) {
            renderer.addLayer(new ChaosSpellArmorGlowLayer<>(renderer));
        }
    }

    private static void addChaosArmorStandGlowLayer(EntityRenderersEvent.AddLayers event) {
        LivingEntityRenderer<ArmorStand, ? extends EntityModel<ArmorStand>> renderer = event.getRenderer(EntityType.ARMOR_STAND);
        if (renderer != null) {
            addChaosArmorGlowLayer(renderer);
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addChaosArmorGlowLayer(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new ChaosSpellArmorGlowLayer<>(renderer));
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(GenesisParticles.GLOW_CUBE.get(), GlowCubeParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(GenesisParticles.OVERLORD_PARTICLE.get(), OverlordParticle.Provider::new);
    }
}
