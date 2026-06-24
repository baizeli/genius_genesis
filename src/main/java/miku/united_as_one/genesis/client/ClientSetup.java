package miku.united_as_one.genesis.client;

import miku.bai_ze_li.genesis.api.render.particle.GlowCubeParticle;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderEffect;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.item.Scroll;
import miku.united_as_one.genesis.client.render.cosmic.CosmicModelLoader;
import miku.united_as_one.genesis.client.render.block.ArcaneCauldronRenderer;
import miku.united_as_one.genesis.client.render.WingLayer;
import miku.united_as_one.genesis.client.render.entity.ChaosSwordRenderer;
import miku.united_as_one.genesis.client.render.entity.MeteorProjectileRenderer;
import miku.united_as_one.genesis.client.render.entity.MeteorStarRenderer;
import miku.united_as_one.genesis.client.render.entity.NoopRenderer;
import miku.united_as_one.genesis.client.render.effect.SlashEffectEvents;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.workbench.registry.BlockEntityRegistry;
import miku.united_as_one.genesis.workbench.registry.MenuTypeRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
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
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onRenderLevelStage);
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onLevelUnload);
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

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("cosmic", CosmicModelLoader.INSTANCE);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.METEOR_PROJECTILE.get(), MeteorProjectileRenderer::new);
        event.registerEntityRenderer(EntityRegistry.METEOR_STAR.get(), MeteorStarRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CHAOS_SWORD.get(), ChaosSwordRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CHAOS_SWORD_AOE.get(), context -> new NoopRenderer<>(context));
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARCANE_CAULDRON.get(), ArcaneCauldronRenderer::new);
    }

    private static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        addWingLayer(event, "default");
        addWingLayer(event, "slim");
    }

    private static void addWingLayer(EntityRenderersEvent.AddLayers event, String skinName) {
        LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin(skinName);
        if (renderer != null) {
            renderer.addLayer(new WingLayer(renderer));
        }
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(GenesisParticles.GLOW_CUBE.get(), GlowCubeParticle.Provider::new);
    }
}
