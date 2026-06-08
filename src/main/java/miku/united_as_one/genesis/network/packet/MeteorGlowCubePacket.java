package miku.united_as_one.genesis.network.packet;

import java.util.function.Supplier;
import miku.united_as_one.genesis.client.network.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record MeteorGlowCubePacket(double x, double y, double z, int count, long seed) {
    public static MeteorGlowCubePacket decode(FriendlyByteBuf buffer) {
        return new MeteorGlowCubePacket(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readVarInt(), buffer.readLong());
    }

    public static void encode(MeteorGlowCubePacket packet, FriendlyByteBuf buffer) {
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
        buffer.writeVarInt(packet.count);
        buffer.writeLong(packet.seed);
    }

    public static void handle(MeteorGlowCubePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleMeteorGlowCubePacket(packet.x, packet.y, packet.z, packet.count, packet.seed)));
        context.setPacketHandled(true);
    }
}
