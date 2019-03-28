package ru.hse.spb.interpreter.command;

import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BashCommandTest {
    protected List<Entity> createEntity(final String command) {
        return Collections.singletonList(new Entity(EntityType.SIMPLE_PART, command));
    }

    protected List<Entity> createEntity(final String command1, final String command2) {
        return Arrays.asList(new Entity(EntityType.SIMPLE_PART, command1),
                new Entity(EntityType.PART_IN_DOUBLE_PRIME, command2));
    }

}