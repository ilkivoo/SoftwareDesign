package ru.hse.spb.interpreter.exceptions;

public class BashCommandException extends IllegalArgumentException {
    public BashCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public BashCommandException(String message) {
        super(message);
    }

    public BashCommandException(Throwable cause) {
        super(cause);
    }
}