package ru.hse.spb.interpreter.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;
import org.apache.commons.cli.*;
import ru.hse.spb.interpreter.model.cli.GrepCliParams;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getCommandLine;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getInputStreams;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getLines;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.isLineMatch;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.printHelp;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.readFiles;

@Service
public class Grep implements BashCommand {
    private static final Logger LOG = LoggerFactory.getLogger(Grep.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*grep(\\s+)[^\\s]");
    private final InputStream defaultInputStream;

    @Inject
    public Grep(final InputStream defaultInputStream) {
        this.defaultInputStream = defaultInputStream;
    }

    @Override
    public boolean isFits(final String inputString) {
        if (inputString == null) {
            return false;
        }
        final Matcher matcher = COMMAND_PATTERN.matcher(inputString.toLowerCase());
        return matcher.find();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        return apply(inputString, new BashCommandResult(""));
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString, BashCommandResult prevResult) {
        if (!isFits(inputString)) {
            LOG.error("Unable to apply grep");
            return new BashCommandResult("");
        }
        final CommandLine cmd;
        final GrepCliParams grepCliParams;
        try {
            cmd = getCommandLine(inputString.split(("\\s+")),
                    GrepCliParams.cmdOptions);
            grepCliParams = GrepCliParams.of(cmd);
        } catch (Exception e) {
            printHelp(GrepCliParams.cmdOptions);
            return new BashCommandResult("");
        }

        final Map<String, String> textByFileName = new HashMap<>();

        if (!prevResult.isEmpty()) {
            textByFileName.put("", prevResult.getResult());
        } else {
            final List<String> fileNames = grepCliParams.getFileNames();
            final Map<String, InputStream> inputStreamByFileName =
                    getInputStreams(fileNames, defaultInputStream);
            textByFileName.putAll(readFiles(inputStreamByFileName));
        }
        return new BashCommandResult(getMatchedText(textByFileName, grepCliParams));
    }


    private String getMatchedText(final Map<String, String> textByFileName,
                                  final GrepCliParams grepCliParams) {
        if (grepCliParams == null || textByFileName == null) {
            return "";
        }
        Pattern pattern = getPattern(grepCliParams);
        final boolean printFileName = textByFileName.size() > 1;
        final int countLines = grepCliParams.getAfterContext() == null
                ? 0
                : grepCliParams.getAfterContext();
        final List<String> resultLines = new ArrayList<>();
        for (String fileName : textByFileName.keySet()) {
            final String text = textByFileName.getOrDefault(fileName, "");
            resultLines.addAll(getMatchedLines(text,
                    pattern,
                    printFileName ? fileName + ": " : "",
                    countLines));
        }
        return String.join("\n", resultLines);
    }

    private List<String> getMatchedLines(final String text,
                                         @Nonnull final Pattern pattern,
                                         final String prefix,
                                         final int countLines) {
        int countLinesToAdd = 0;
        final String linePrefix = prefix == null ? "" : prefix;
        if (text == null || text.equals("")) {
            return new ArrayList<>();
        }
        List<String> matchedLines = new ArrayList<>();
        final List<String> lines = getLines(text);
        for (String line : lines) {
            if (countLinesToAdd > 0) {
                matchedLines.add(linePrefix + line);
                countLinesToAdd--;
            } else {
                if (isLineMatch(line, pattern)) {
                    matchedLines.add(linePrefix + line);
                    countLinesToAdd = countLines > 0 ? countLines : 0;
                } else {
                    countLinesToAdd = 0;
                }
            }
        }
        return matchedLines;
    }

    @Nonnull
    private Pattern getPattern(final GrepCliParams grepCliParams) {
        if (grepCliParams == null) {
            return Pattern.compile(".*");
        }
        final String around = grepCliParams.isWordRegexp()
                ? "(^|\\s|$)+"
                : "";

        final int ignoreCase = grepCliParams.isIgnoreCase()
                ? CASE_INSENSITIVE
                : 0;
        return Pattern.compile(around + grepCliParams.getRegexp() + around, ignoreCase);
    }

}
