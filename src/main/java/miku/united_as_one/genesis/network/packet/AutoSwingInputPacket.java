package miku.united_as_one.genesis.network.packet;

import java.util.function.Supplier;
import miku.united_as_one.genesis.combat.autoswing.AutoSwingManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record AutoSwingInputPacket(boolean down) {
    public static AutoSwingInputPacket decode(FriendlyByteBuf buffer) {
        return new AutoSwingInputPacket(buffer.readBoolean());
    }

    public static void encode(AutoSwingInputPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.down());
    }

    public static void handle(AutoSwingInputPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                AutoSwingManager.onAttackInput(player, packet.down());
            }
        });
        context.setPacketHandled(true);
    }
}
