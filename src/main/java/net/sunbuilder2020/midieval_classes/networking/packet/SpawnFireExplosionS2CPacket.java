package net.sunbuilder2020.midieval_classes.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.Random;
import java.util.function.Supplier;

public class SpawnFireExplosionS2CPacket {
    private final int entityId;

    public SpawnFireExplosionS2CPacket(Entity entity) {
        this.entityId = entity.getId();
    }

    public SpawnFireExplosionS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Entity entity = context.getSender().level().getEntity(this.entityId);
            if (entity != null) {
                Random rand = new Random();
                for (int i = 0; i < 20; i++) {
                    double vx = rand.nextDouble() * 2.0 - 1.0;
                    double vy = rand.nextDouble() * 2.0 - 1.0;
                    double vz = rand.nextDouble() * 2.0 - 1.0;

                    double speedFactor = 0.1;
                    double magnitude = Math.sqrt(vx * vx + vy * vy + vz * vz);
                    vx = (vx / magnitude) * speedFactor;
                    vy = (vy / magnitude) * speedFactor;
                    vz = (vz / magnitude) * speedFactor;

                    entity.level().addParticle(ParticleTypes.FLAME,
                            entity.getX() + 0.5,
                            entity.getY() + 0.5,
                            entity.getZ() + 0.5,
                            vx, vy, vz);
                    }
            }

            context.getSender().displayClientMessage(Component.literal("Spawned particles on Client side"), true);
        });
    }
}
