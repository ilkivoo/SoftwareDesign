package ru.hse.spb.interpreter.model;

import java.util.List;
import java.util.Objects;

public class Token {
    private final List<PipeSplitCommand> commands;

    public Token(final List<PipeSplitCommand> commands) {
        this.commands = commands;
    }

    public List<PipeSplitCommand> getcommands() {
        return commands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(commands, token.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands);
    }


    @Override
    public String toString() {
        return "Token{" + "\n" +
                "   commands=" + commands + "\n" +
                '}';
    }

}
