package ru.hse.spb.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;


import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Preprocessor {
    private final Map<String, String> envVariables;
    private static final String IDENTIFIER_PATTERN = "[_a-zA-Z]([_a-zA-Z0-9])*";
    private static final String REPLACEMENT_PATTERN = "(^|[^\\\\])(\\\\\\\\)*" + "\\$" + IDENTIFIER_PATTERN;
    private static final Logger LOG = LoggerFactory.getLogger(Preprocessor.class);

    public Preprocessor(@Qualifier("envVariables") final Map<String, String> envVariables) {
        this.envVariables = envVariables;
    }

    public String run(final List<Entity> entities) {
        return entities == null
                ? ""
                : entities.stream()
                .map(entity -> (entity.getType() != EntityType.PART_IN_PRIME)
                        ? replaceSpecialSymbols(replaceValues(entity.getValue()))
                        : entity.getValue())
                .collect(Collectors.joining());
    }

    @Nonnull
    private String replaceSpecialSymbols(final String input) {
        if (input == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        boolean isPrevOpenBackslash = false;
        for (char curSymbol : input.toCharArray()) {
            if (curSymbol == '\\' && !isPrevOpenBackslash) {
                isPrevOpenBackslash = true;
            } else {
                if (curSymbol == '~' && !isPrevOpenBackslash) {
                    builder.append(envVariables.get("HOME"));
                } else {
                    builder.append(curSymbol);
                }
                isPrevOpenBackslash = false;
            }
        }
        return builder.toString();

    }


    @Nonnull
    private String replaceValues(final String input) {
        if (input == null) {
            return "";
        }
        String result = input;
        Pattern pattern = Pattern.compile(REPLACEMENT_PATTERN);
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            final String firstPart = result.substring(0, matcher.start());
            final String lastPart = result.substring(matcher.end());
            final String substringWithReplacement = replaceIdentifier(matcher.group()).orElse("");
            result = firstPart + substringWithReplacement + lastPart;
            matcher = pattern.matcher(result);
        }
        return result;
    }

    private Optional<String> replaceIdentifier(final String input) {
        if (input == null) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile("\\$" + IDENTIFIER_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return Optional.empty();
        }

        final String identWithDollar = matcher.group();
        if (identWithDollar.length() < 1) {
            return Optional.empty();
        }
        final String ident = identWithDollar.substring(1);
        try {
            final String result = input.replaceAll("\\$" + ident, envVariables.getOrDefault(ident, ""));
            return Optional.of(result);
        } catch (Exception e) {
            LOG.error("Impossible to replace "+ "\\$" + ident + " with" +
                    envVariables.getOrDefault(ident, "") + " in " + input, e);
            return Optional.empty();
        }
    }

}





