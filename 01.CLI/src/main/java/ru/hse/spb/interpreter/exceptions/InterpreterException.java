package ru.hse.spb.interpreter.exceptions;

public class InterpreterException extends IllegalArgumentException {
    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(Throwable cause) {
        super(cause);
    }
}
