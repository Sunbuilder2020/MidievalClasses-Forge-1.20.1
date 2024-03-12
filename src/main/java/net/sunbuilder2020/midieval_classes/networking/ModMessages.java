package net.sunbuilder2020.midieval_classes.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;
import net.sunbuilder2020.midieval_classes.networking.packet.SetClassC2SPacket;
import net.sunbuilder2020.midieval_classes.networking.packet.SpawnFireExplosionS2CPacket;

public class ModMessages {
    private static SimpleChannel Instance;
    private static int packetID = 0;
    private static int ID() {
        return packetID++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MidievalClasses.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        Instance = net;

        net.messageBuilder(ClassDataSyncS2CPacket.class, ID(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(buf -> new ClassDataSyncS2CPacket(buf))
                .encoder(ClassDataSyncS2CPacket::toBytes)
                .consumerMainThread(ClassDataSyncS2CPacket::handle)
                .add();

        net.messageBuilder(SetClassC2SPacket.class, ID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(buf -> new SetClassC2SPacket(buf))
                .encoder(SetClassC2SPacket::toBytes)
                .consumerMainThread(SetClassC2SPacket::handle)
                .add();

        net.messageBuilder(SpawnFireExplosionS2CPacket.class, ID(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(buf -> new SpawnFireExplosionS2CPacket(buf))
                .encoder(SpawnFireExplosionS2CPacket::toBytes)
                .consumerMainThread(SpawnFireExplosionS2CPacket::handle)
                .add();

    }

    public static <MSG> void sendToServer(MSG message) {
        Instance.sendToServer(message);
    }

    public static <MSG> void sendToServer(MSG message, ServerPlayer player) {
        Instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        Instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
