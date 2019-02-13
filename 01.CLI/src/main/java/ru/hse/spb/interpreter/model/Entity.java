package ru.hse.spb.interpreter.model;

import java.util.Objects;

public class Entity {
    final private EntityType type;
    final private String value;

    public Entity(final EntityType type,
                  final String value) {
        this.type = type;
        this.value = value;
    }

    public EntityType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(value, entity.value)
                && type == entity.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "Entity{" + "\n" +
                "value=" + value + "; " + "\n" +
                "type=" + type + "\n" +
                " }";
    }


}
