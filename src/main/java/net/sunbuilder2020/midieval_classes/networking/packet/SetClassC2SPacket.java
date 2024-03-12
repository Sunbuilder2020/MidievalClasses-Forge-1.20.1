package net.sunbuilder2020.midieval_classes.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;

import java.util.function.Supplier;

public class SetClassC2SPacket {
    private final String classes;

    public SetClassC2SPacket(String classes) {
        this.classes = classes;
    }

    public SetClassC2SPacket(FriendlyByteBuf buf) {
        this.classes = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.classes);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                    classes.setClass(this.classes);

                    ClassManager.applyClassChanges((Player) player);

                    ModMessages.sendToClient(new ClassDataSyncS2CPacket(String.valueOf(classes.getClass())), player);

                    ClassManager.sendClassAssignedMessage(player, this.classes);
                });
            }
        });
    }
}

