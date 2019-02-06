package ru.hse.spb.interpreter.command.impl;


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
    @Override
    public boolean isFits(final String inputString) {
        return getData(inputString.toLowerCase()).isPresent();
    }

    @Override
    @Nonnull
    public BashCommandResult apply(final String inputString) {
        final Optional<String> dataOpt = getData(inputString);
        //TODO запись в лог
        return dataOpt.map(BashCommandResult::new).orElseGet(
                () -> {
                    //TODO запись в лог
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
