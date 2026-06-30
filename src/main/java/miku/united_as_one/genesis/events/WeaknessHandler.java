package miku.united_as_one.genesis.events;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.utils.GenesisEntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaknessHandler {
    @SubscribeEvent
    public static void onWeaknessTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (isWeakness(living))
            weaknessTick(living.getPersistentData(), living);
    }

    private static boolean isWeakness(Entity living) {
        if (living.level().isClientSide())
            return false;
        if (!(living instanceof ServerPlayer))
            return false;
        return living.getPersistentData().contains(Genesis.KEY_WEAK_TICKS, Tag.TAG_INT);
    }

    private static void weaknessTick(CompoundTag nbt, LivingEntity living) {
        int weak = nbt.getInt(Genesis.KEY_WEAK_TICKS);
        if (weak > 0) {
            nbt.putInt(Genesis.KEY_WEAK_TICKS, weak - 1);
            return;
        }
        nbt.remove(Genesis.KEY_WEAK_TICKS); // 移除虚弱效果标签
        GenesisEntityUtil.removeWeakness(living);
    }
}
