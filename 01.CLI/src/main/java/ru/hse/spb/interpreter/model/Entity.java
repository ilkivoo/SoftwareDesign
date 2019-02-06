package ru.hse.spb.interpreter.model;

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
    public String toString() {
        return "Entity{" + "\n" +
                "value=" + value + "; " + "\n" +
                "type=" + type + "\n" +
                " }";
    }
}
