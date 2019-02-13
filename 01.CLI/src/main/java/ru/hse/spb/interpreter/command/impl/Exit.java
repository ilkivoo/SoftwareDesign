package ru.hse.spb.interpreter.command.impl;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Exit implements BashCommand {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*exit(\\s+|$)");
    @Override
    public boolean isFits(String inputString) {
        final String[] words = inputString.split("\\s+");
        final Matcher matcher = COMMAND_PATTERN.matcher(inputString);
        return matcher.find();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        System.exit(0);
        return new BashCommandResult("");
    }
}
