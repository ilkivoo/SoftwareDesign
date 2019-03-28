package ru.hse.spb.interpreter;

import ru.hse.spb.interpreter.model.Token;

import javax.annotation.Nonnull;
import java.util.List;

public interface Tokenizer {
    @Nonnull
    List<Token> getTokens(final String command);
}
