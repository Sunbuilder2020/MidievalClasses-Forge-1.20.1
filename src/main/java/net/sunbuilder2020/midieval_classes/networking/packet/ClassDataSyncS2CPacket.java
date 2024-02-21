package net.sunbuilder2020.midieval_classes.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.sunbuilder2020.midieval_classes.client.ClientClassData;

import java.util.function.Supplier;

public class ClassDataSyncS2CPacket {
    private final String classes;

    public ClassDataSyncS2CPacket(String classes) {
        this.classes = classes;
    }

    public ClassDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.classes = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(classes); // Directly write the string
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientClassData.set(classes);
        });
        return true;
    }
}

