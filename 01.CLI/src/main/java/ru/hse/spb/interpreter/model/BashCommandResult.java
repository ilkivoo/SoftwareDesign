package ru.hse.spb.interpreter.model;


import java.util.Objects;

public class BashCommandResult {
    private final String result;

    public BashCommandResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getResultOrDefault(final String defaultString) {
        return result == null ? defaultString : result;
    }

    public boolean isEmpty() {
        return result == null || "".equals(result);
    }

    public BashCommandResult emptyResult() {
        return new BashCommandResult("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BashCommandResult bashCommandResult = (BashCommandResult) o;
        return Objects.equals(result, bashCommandResult.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }


    @Override
    public String toString() {
        return "BashCommandResult{" + "\n" +
                "   result=" + result + "\n" +
                '}';
    }
}
