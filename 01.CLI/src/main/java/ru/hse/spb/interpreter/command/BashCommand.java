package ru.hse.spb.interpreter.command;


import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;


public interface BashCommand {
    boolean isFits(final List<Entity> entities);

    @Nonnull
    BashCommandResult apply(final List<Entity> entities);

    @Nonnull
    default BashCommandResult apply(final List<Entity> entities, final BashCommandResult predResult) {
        return apply(entities);
    }


}
