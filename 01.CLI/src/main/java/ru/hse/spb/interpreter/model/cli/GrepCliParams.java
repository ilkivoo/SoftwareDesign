package ru.hse.spb.interpreter.model.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GrepCliParams {
        private final static String IGNORE_CASE_OPTION_NAME = "ignore-case";
        private final static String WORD_REGEXP_OPTION_NAME = "word-regexp";
        private final static String AFTER_CONTEXT_OPTION_NAME = "after-context";

        public final static List<Option> cmdOptions = Arrays.asList(
                new Option("i", IGNORE_CASE_OPTION_NAME,
                        false, "Ignore case distinctions"),
                new Option("w", WORD_REGEXP_OPTION_NAME,
                        false, "Select only those lines containing matches that form whole words"),
                new Option("A", AFTER_CONTEXT_OPTION_NAME,
                        true, "Print NUM lines of trailing context after matching lines"));

        private final static int indexForRegexp = 1;
        @Nonnull
        private final String regexp;
        private final List<String> fileNames;
        private final boolean ignoreCase;
        private final boolean wordRegexp;
        private final Integer afterContext;


        public GrepCliParams(@Nonnull final String regexp,
                           final List<String> fileNames,
                           final boolean ignoreCase,
                           final boolean wordRegexp,
                           final Integer afterContext) {
            this.regexp = regexp;
            this.fileNames = fileNames;
            this.ignoreCase = ignoreCase;
            this.wordRegexp = wordRegexp;
            this.afterContext = afterContext;
        }

        public static GrepCliParams of(@Nonnull CommandLine cmd) throws ParseException, NumberFormatException {
            final List<String> args = cmd.getArgList();
            if (args == null || args.size() <= indexForRegexp) {
                throw new ParseException("Regexp not found");
            }
            final String numString = cmd.getOptionValue(AFTER_CONTEXT_OPTION_NAME);
            final Integer afterContextValue = numString == null
                    ? null
                    : Integer.parseInt(numString);
            final String regexp = args.get(indexForRegexp);
            if (regexp == null || "".equalsIgnoreCase(regexp)) {
                throw new ParseException("Regexp not found");
            }
            return new GrepCliParams(regexp,
                    args.subList(indexForRegexp + 1, args.size()),
                    cmd.hasOption(IGNORE_CASE_OPTION_NAME),
                    cmd.hasOption(WORD_REGEXP_OPTION_NAME),
                    afterContextValue
            );
        }

    @Nonnull
    public String getRegexp() {
        return regexp;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isWordRegexp() {
        return wordRegexp;
    }

    public Integer getAfterContext() {
        return afterContext;
    }
}
