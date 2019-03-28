package ru.hse.spb.interpreter.command.impl;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Exit implements BashCommand {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*exit(\\s+|$)");

    @Override
    public boolean isFits(final List<Entity> entities) {
        if (entities == null || entities.size() == 0 || entities.get(0).getType() != EntityType.SIMPLE_PART) {
            return false;
        }
        final Matcher matcher = COMMAND_PATTERN.matcher(entities.get(0).getValue().toLowerCase());
        return matcher.find();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(final List<Entity> entities) {
        System.exit(0);
        return new BashCommandResult("");
    }
}
