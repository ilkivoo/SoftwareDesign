package ru.hse.spb.interpreter.command.impl;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Pwd implements BashCommand {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("pwd(\\s+|$)");

    @Override
    public boolean isFits(String inputString) {
        if (inputString == null) {
            return false;
        }
        final Matcher matcher = COMMAND_PATTERN.matcher(inputString.toLowerCase());
        return matcher.find();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        return new BashCommandResult(new File(".").getAbsolutePath());
    }
}
