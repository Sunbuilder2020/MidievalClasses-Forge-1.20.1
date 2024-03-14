package net.sunbuilder2020.medieval_classes.player_hostility;

import net.minecraft.world.entity.Entity;
import java.util.Objects;

public class EntityPair {
    private final Entity entityOne;
    private final Entity entityTwo;

    public EntityPair(Entity entityOne, Entity entityTwo) {
        this.entityOne = entityOne;
        this.entityTwo = entityTwo;
    }

    public Entity getEntityOne() {
        return entityOne;
    }

    public Entity getEntityTwo() {
        return entityTwo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityPair)) return false;
        EntityPair that = (EntityPair) o;
        return (Objects.equals(entityOne, that.entityOne) && Objects.equals(entityTwo, that.entityTwo)) ||
                (Objects.equals(entityOne, that.entityTwo) && Objects.equals(entityTwo, that.entityOne));
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityOne) + Objects.hash(entityTwo); // Simple symmetrical hashing
    }
}
