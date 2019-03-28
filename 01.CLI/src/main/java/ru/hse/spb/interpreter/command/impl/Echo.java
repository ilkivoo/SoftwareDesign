package ru.hse.spb.interpreter.command.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.exceptions.BashCommandException;
import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.hse.spb.interpreter.command.util.BashCommandUtil.cut;

@Component
public class Echo implements BashCommand {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*echo(\\s+|$)");
    private static final Logger LOG = LoggerFactory.getLogger(Echo.class);

    @Override
    public boolean isFits(final List<Entity> entities) {
        return getData(entities).isPresent();
    }

    @Override
    @Nonnull
    public BashCommandResult apply(final List<Entity> entities) {
        final Optional<String> dataOpt = getData(entities);
        if (!dataOpt.isPresent()) {
            throw new BashCommandException("Can't apply echo for " + entities);
        }
        return new BashCommandResult(dataOpt.get());
    }

    @Nonnull
    private Optional<String> getData(final List<Entity> entities) {
        if (entities == null || entities.size() == 0 || entities.get(0).getType() != EntityType.SIMPLE_PART) {
            return Optional.empty();
        }
        final Matcher matcher = COMMAND_PATTERN.matcher(entities.get(0).getValue().toLowerCase());
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String firstArgument = cut(entities.get(0).getValue(), matcher.start(), matcher.end());
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(splitJoin(firstArgument));
        for (int i = 1; i < entities.size(); i++) {
            stringBuilder.append(getValue(entities.get(i)));
        }
        return Optional.of(stringBuilder.toString());
    }

    private String getValue(@Nonnull final Entity entity) {
        if (entity.getType() != EntityType.SIMPLE_PART) {
            return entity.getValue();
        } else return splitJoin(entity.getValue());
    }

    private String splitJoin(@Nonnull final String value) {
        if (value.isEmpty()) {
            return "";
        }
        String end = "";
        if (value.charAt(value.length() - 1) == ' ') {
            end = " ";
        }
        return String.join(" ", value.split("(\\s)+")) + end;
    }
}
