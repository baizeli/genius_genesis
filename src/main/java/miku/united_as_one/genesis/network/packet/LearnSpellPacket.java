package miku.united_as_one.genesis.network.packet;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LearnSpellPacket {
    private final byte hand;
    private final String spellId;

    public LearnSpellPacket(InteractionHand hand, String spellId) {
        this.hand = handToByte(hand);
        this.spellId = spellId;
    }

    private LearnSpellPacket(FriendlyByteBuf buf) {
        this.hand = buf.readByte();
        this.spellId = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.hand);
        buf.writeUtf(this.spellId);
    }

    public static LearnSpellPacket decode(FriendlyByteBuf buf) {
        return new LearnSpellPacket(buf);
    }

    public static void handle(LearnSpellPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            ItemStack manuscript = player.getItemInHand(byteToHand(packet.hand));
            AbstractSpell spell = io.redspace.ironsspellbooks.api.registry.SpellRegistry.getSpell(packet.spellId);
            if (spell == io.redspace.ironsspellbooks.api.registry.SpellRegistry.none()) {
                return;
            }
            boolean canLearn = manuscript.is(ItemRegistry.CELESTIAL_SOURCE_MANUSCRIPT.get())
                    && spell.getSchoolType().equals(SpellSchoolRegistry.CELESTIAL_SOURCE.get());
            canLearn |= manuscript.is(ItemRegistry.CHAOS_MANUSCRIPT.get())
                    && spell.getSchoolType().equals(SpellSchoolRegistry.CHAOS.get());
            if (!canLearn) {
                return;
            }

            var data = MagicData.getPlayerMagicData(player).getSyncedData();
            if (data.isSpellLearned(spell) || manuscript.isEmpty()) {
                return;
            }

            data.learnSpell(spell);
            if (!player.getAbilities().instabuild) {
                manuscript.shrink(1);
            }
        });
        context.setPacketHandled(true);
    }

    private static byte handToByte(InteractionHand hand) {
        return (byte) (hand == InteractionHand.MAIN_HAND ? 1 : 0);
    }

    private static InteractionHand byteToHand(byte value) {
        return value > 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }
}
