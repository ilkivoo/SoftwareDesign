package ru.hse.spb.interpreter.command.impl;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;

@Component
public class Exit implements BashCommand {

    @Override
    public boolean isFits(String inputString) {
        final String[] words = inputString.split("\\s+");
        return (words.length > 0) && (words[0].equalsIgnoreCase("exit"));
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        System.exit(0);
        return new BashCommandResult("");
    }
}
