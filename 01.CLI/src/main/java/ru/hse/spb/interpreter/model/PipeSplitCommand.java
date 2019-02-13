package ru.hse.spb.interpreter.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PipeSplitCommand {
    private final List<Entity> entities;

    public PipeSplitCommand(final List<Entity> entities) {
        this.entities = entities;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public String getEntitiesAsString() {
        return entities.stream().map(Entity::getValue).collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PipeSplitCommand pipeSplitCommands = (PipeSplitCommand) o;
        return Objects.equals(entities, pipeSplitCommands.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entities);
    }


    @Override
    public String toString() {
        return "PipeSplitCommand{" + "\n" +
                "   entities=" + entities + "\n" +
                '}';
    }
}
