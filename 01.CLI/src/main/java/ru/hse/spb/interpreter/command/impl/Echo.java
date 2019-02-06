package ru.hse.spb.interpreter.command.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Echo implements BashCommand {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("echo(\\s+|$)");
    private static final Logger LOG = LoggerFactory.getLogger(Echo.class);

    @Override
    public boolean isFits(final String inputString) {
        return getData(inputString.toLowerCase()).isPresent();
    }

    @Override
    @Nonnull
    public BashCommandResult apply(final String inputString) {
        final Optional<String> dataOpt = getData(inputString);
        return dataOpt.map(BashCommandResult::new).orElseGet(
                () -> {
                    LOG.warn("unable to apply command echo to " + inputString);
                    return new BashCommandResult("");
                });
    }

    @Nonnull
    private Optional<String> getData(final String inputString) {
        final Matcher matcher = COMMAND_PATTERN.matcher(inputString);
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String fileNames = matcher.replaceFirst("");
        return Optional.of(String.join(" ", fileNames.split("\\s+")));
    }
}
