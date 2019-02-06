package ru.hse.spb.interpreter.command;


import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;
import java.util.Map;


public interface BashCommand {
    boolean isFits(final String inputString);

    @Nonnull
    BashCommandResult apply(final String inputString);

    @Nonnull
    default BashCommandResult apply(final String inputString, final BashCommandResult predResult) {
        return apply(inputString);
    }


}
