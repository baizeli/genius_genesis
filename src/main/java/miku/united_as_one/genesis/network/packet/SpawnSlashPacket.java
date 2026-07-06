package miku.united_as_one.genesis.network.packet;

import java.util.function.Supplier;
import miku.united_as_one.genesis.client.network.ClientPacketHandlers;
import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SpawnSlashPacket(int attackerId, int targetId, int color) {
    public SpawnSlashPacket(int attackerId, int targetId) {
        this(attackerId, targetId, SlashColors.MITHRIL_LIGHT_BLUE);
    }

    public static SpawnSlashPacket decode(FriendlyByteBuf buffer) {
        return new SpawnSlashPacket(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void encode(SpawnSlashPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.attackerId);
        buffer.writeInt(packet.targetId);
        buffer.writeInt(packet.color);
    }

    public static void handle(SpawnSlashPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleSlashPacket(packet.attackerId, packet.targetId, packet.color)));
        context.setPacketHandled(true);
    }
}
