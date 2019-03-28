package ru.hse.spb.interpreter.exceptions;

public class TokenizerException extends IllegalArgumentException {
    public TokenizerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenizerException(String message) {
        super(message);
    }

    public TokenizerException(Throwable cause) {
        super(cause);
    }
}
