package miku.united_as_one.genesis.content.fluid;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FluidCommonEvents {

    private FluidCommonEvents() {
    }

    @SubscribeEvent
    public static void onCreateFluidSource(BlockEvent.CreateFluidSourceEvent event) {
        if (isCustomFluid(event.getState().getFluidState())) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void onBucketFill(FillBucketEvent event) {
        if (event.getLevel().isClientSide() || event.getTarget() == null || event.getTarget().getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult hit = (BlockHitResult) event.getTarget();
        if (isCustomFluid(event.getLevel().getFluidState(hit.getBlockPos()))) {
            event.getLevel().playSound(null, hit.getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onBucketEmpty(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (isCustomBucket(event.getItemStack().getItem())) {
            event.getLevel().playSound(null, event.getPos(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        Player player = event.player;
        FluidState state = player.level().getFluidState(player.blockPosition());
        if (isBlackwater(state) && !player.hasEffect(MobEffects.WATER_BREATHING) && player.tickCount % 10 == 0) {
            player.hurt(player.damageSources().magic(), 4.0F);
        }
    }

    private static boolean isCustomFluid(FluidState state) {
        return isSource(state) || isBlackwater(state) || isBlood(state);
    }

    private static boolean isSource(FluidState state) {
        return state.getType() == FluidRegistry.SOURCE_FLUID.getSource();
    }

    private static boolean isBlackwater(FluidState state) {
        return state.getType() == FluidRegistry.BLACKWATER_FLUID.getSource();
    }

    private static boolean isBlood(FluidState state) {
        return state.getType() == FluidRegistry.BLOOD_FLUID.getSource();
    }

    private static boolean isCustomBucket(Item item) {
        return item == FluidRegistry.SOURCE_FLUID.getBucket().get()
                || item == FluidRegistry.BLACKWATER_FLUID.getBucket().get()
                || item == FluidRegistry.BLOOD_FLUID.getBucket().get();
    }
}
