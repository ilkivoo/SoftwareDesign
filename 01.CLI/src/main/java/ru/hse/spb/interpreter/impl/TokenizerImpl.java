package ru.hse.spb.interpreter.impl;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.Tokenizer;
import ru.hse.spb.interpreter.exceptions.TokenizerException;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;
import ru.hse.spb.interpreter.model.PipeSplitCommand;
import ru.hse.spb.interpreter.model.Token;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TokenizerImpl implements Tokenizer {
    private final String singlePrime = "'";
    private final String allSymbols = ".*?";
    private final String doublePrime = "\"";
    private final String notSpecialSymbols = "([^;\'\"])*";
    private final String notSpecialSymbolsAndPipe = "([^|;\'\"])*";
    private final String evenBackslash = "([^\\\\'\"]+|^)(\\\\\\\\)*?";
    private final String evenBackslashWithoutSemicolon = "([^\\\\'\";]+|^)(\\\\\\\\)*?";
    private final String evenBackslashWithoutPipe = "([^\\\\'\";|]+|^)(\\\\\\\\)*?";
    private final String doublePrimePart = doublePrime + "(?<doublePrimePart>" + allSymbols + "(" + evenBackslash + ")*" + ")" + doublePrime;
    private final String singlePrimePart = singlePrime + "(?<singlePrimePart>" + allSymbols + ")" + singlePrime;
    private final String singlePrimeParts = "(" + singlePrimePart + ")*";
    private final String doublePrimeParts = "(" + doublePrimePart + ")*";
    private final String tokenPattern = "(" + notSpecialSymbols + evenBackslashWithoutSemicolon + "(" + doublePrimeParts + ")" + "(" + singlePrimeParts + ")" + ")*";
    private final String commandPattern = "(" + notSpecialSymbolsAndPipe + evenBackslashWithoutPipe + "(" + doublePrimeParts + ")" + "(" + singlePrimeParts + ")" + ")*";

    @Nonnull
    public List<Token> getTokens(final String inputString) {
        final String trimmedInputString = inputString.trim();
        final List<String> tokenStrings = getAllMatchers(trimmedInputString, tokenPattern);
        return tokenStrings.stream()
                .map(tokenString -> new Token(getPipeSplitCommand(tokenString)))
                .collect(Collectors.toList());
    }

    @Nonnull
    private List<PipeSplitCommand> getPipeSplitCommand(final String inputString) {
        final String trimmedInputString = inputString.trim();
        final List<String> commandStrings = getAllMatchers(trimmedInputString, commandPattern);
        if (!String.join("|", commandStrings).equals(trimmedInputString)) {
            throw new TokenizerException("Impossible to get commands in" + inputString);
        }

        return commandStrings.stream()
                .map(commandString -> new PipeSplitCommand(getEntities(commandString)))
                .collect(Collectors.toList());
    }

    @Nonnull
    private List<String> getAllMatchers(final String inputString,
                                        final String patternString) {
        final List<String> result = new ArrayList<>();
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            final String command = matcher.group();
            if (!command.isEmpty()) {
                result.add(command);
            }
        }
        return result;
    }

    @Nonnull
    private List<Entity> getEntities(final String inputString) {
        final String trimmedInputString = inputString.trim();
        if (trimmedInputString.length() == 0) {
            return new ArrayList<>();
        }
        final List<Entity> result = new ArrayList<>();
        final Pattern pattern = Pattern.compile(evenBackslash + "(?<primePart>" + doublePrimePart + "|" + singlePrimePart + ")");
        final Matcher matcher = pattern.matcher(trimmedInputString);
        if (matcher.find()) {
            int start = matcher.start("primePart");
            int end = matcher.end("primePart");
            result.addAll(getEntities(trimmedInputString.substring(0, start)));
            result.add(getEntity(matcher.group("primePart")));
            result.addAll(getEntities(trimmedInputString.substring(end)));
            return result;
        }
        result.add(getEntity(inputString));
        return result;
    }

    private Entity getEntity(final String inputString) {
        final int lastInd = inputString.length() - 1;
        if (inputString.length() < 2 &&
                (inputString.charAt(0) == '\'' || inputString.charAt(0) == '\"')) {
            throw new TokenizerException("Impossible to get Entity for " + inputString);
        }
        if (inputString.charAt(0) == '\'' && inputString.charAt(lastInd) == '\'') {
            return new Entity(EntityType.PART_IN_PRIME, inputString.substring(1, lastInd));
        }
        if (inputString.charAt(0) == '\"' && inputString.charAt(lastInd) == '\"') {
            return new Entity(EntityType.PART_IN_DOUBLE_PRIME, inputString.substring(1, lastInd));
        }
        return new Entity(EntityType.SIMPLE_PART, inputString);

    }

}
