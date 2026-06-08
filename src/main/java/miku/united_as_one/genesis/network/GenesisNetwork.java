package miku.united_as_one.genesis.network;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.network.packet.LearnSpellPacket;
import miku.united_as_one.genesis.network.packet.MeteorGlowCubePacket;
import miku.united_as_one.genesis.network.packet.SpawnSlashPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class GenesisNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static boolean registered;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Genesis.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private GenesisNetwork() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        int id = 0;
        CHANNEL.registerMessage(id++, SpawnSlashPacket.class,
                SpawnSlashPacket::encode,
                SpawnSlashPacket::decode,
                SpawnSlashPacket::handle);
        CHANNEL.registerMessage(id++, LearnSpellPacket.class,
                LearnSpellPacket::encode,
                LearnSpellPacket::decode,
                LearnSpellPacket::handle);
        CHANNEL.registerMessage(id++, MeteorGlowCubePacket.class,
                MeteorGlowCubePacket::encode,
                MeteorGlowCubePacket::decode,
                MeteorGlowCubePacket::handle);
    }

    public static <MSG> void sendToTrackingAndSelf(Entity entity, MSG packet) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
    }
}
