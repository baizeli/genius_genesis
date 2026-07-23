package miku.united_as_one.genesis.network.packet;

import miku.united_as_one.genesis.client.network.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerAnimationPacket(int entityId, ResourceLocation animation) {
    public static PlayerAnimationPacket decode(FriendlyByteBuf buffer) {
        return new PlayerAnimationPacket(buffer.readVarInt(), buffer.readResourceLocation());
    }

    public static void encode(PlayerAnimationPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeResourceLocation(packet.animation);
    }

    public static void handle(PlayerAnimationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handlePlayerAnimationPacket(packet.entityId, packet.animation)));
        context.setPacketHandled(true);
    }
}
