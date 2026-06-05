package miku.united_as_one.genesis.network.packet;

import java.util.function.Supplier;
import miku.united_as_one.genesis.client.network.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SpawnSlashPacket(int attackerId, int targetId) {
    public static SpawnSlashPacket decode(FriendlyByteBuf buffer) {
        return new SpawnSlashPacket(buffer.readInt(), buffer.readInt());
    }

    public static void encode(SpawnSlashPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.attackerId);
        buffer.writeInt(packet.targetId);
    }

    public static void handle(SpawnSlashPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleSlashPacket(packet.attackerId, packet.targetId)));
        context.setPacketHandled(true);
    }
}
