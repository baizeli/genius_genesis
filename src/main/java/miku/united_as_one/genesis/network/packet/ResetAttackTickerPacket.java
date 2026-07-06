package miku.united_as_one.genesis.network.packet;

import java.util.function.Supplier;
import miku.united_as_one.genesis.network.GenesisNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class ResetAttackTickerPacket {
    public static void send(ServerPlayer player) {
        GenesisNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ResetAttackTickerPacket());
    }

    public static ResetAttackTickerPacket decode(FriendlyByteBuf buffer) {
        return new ResetAttackTickerPacket();
    }

    public static void encode(ResetAttackTickerPacket packet, FriendlyByteBuf buffer) {
    }

    public static void handle(ResetAttackTickerPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.resetAttackStrengthTicker();
            }
        });
        context.setPacketHandled(true);
    }
}
