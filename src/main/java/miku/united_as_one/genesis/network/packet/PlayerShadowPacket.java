package miku.united_as_one.genesis.network.packet;

import miku.united_as_one.genesis.client.network.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerShadowPacket(int entityId, int durationTicks) {
    public static PlayerShadowPacket decode(FriendlyByteBuf buffer) {
        return new PlayerShadowPacket(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void encode(PlayerShadowPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeVarInt(packet.durationTicks);
    }

    public static void handle(PlayerShadowPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handlePlayerShadowPacket(packet.entityId, packet.durationTicks)));
        context.setPacketHandled(true);
    }
}
