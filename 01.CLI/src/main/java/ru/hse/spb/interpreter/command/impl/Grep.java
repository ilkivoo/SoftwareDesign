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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getCommandLine;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getInputStreams;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getLines;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.printHelp;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.readFiles;

@Service
public class Grep implements BashCommand {
    private static final Logger LOG = LoggerFactory.getLogger(Grep.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*grep(\\s+)[^\\s]*");
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
        if (grepCliParams.getFileNames() == null || grepCliParams.getFileNames().size() == 0) {
            printHelp(GrepCliParams.cmdOptions);
            return new BashCommandResult("");
        }

        final List<String> fileNames = grepCliParams.getFileNames();
        final Map<String, InputStream> inputStreamByFileName =
                getInputStreams(fileNames, defaultInputStream);
        final Map<String, String> textByFileName = readFiles(inputStreamByFileName);
        return new BashCommandResult(getMatcher(textByFileName, grepCliParams));
    }


    private String getMatcher(final Map<String, String> textByFileName,
                              final GrepCliParams grepCliParams) {
        if (grepCliParams == null || textByFileName == null) {
            return "";
        }
        final String around = grepCliParams.isWordRegexp()
                ? "(\\s)+"
                : "";
        final int ignoreCase = grepCliParams.isIgnoreCase()
                ? CASE_INSENSITIVE
                : 0;
        final Pattern pattern = Pattern.compile(around + grepCliParams.getRegexp() + around, ignoreCase);
        final StringBuilder stringBuilder = new StringBuilder();
        final int countLines = grepCliParams.getAfterContext() == null ? 0 : grepCliParams.getAfterContext();
        int countLinesToAdd = 0;
        for (String fileName : textByFileName.keySet()) {
            final String text = textByFileName.getOrDefault(fileName, "");
            final List<String> lines = getLines(text);
            for (String line : lines) {
                if (countLinesToAdd > 0) {
                    stringBuilder.append(line);
                    countLinesToAdd--;
                } else {
                    if (match(line, pattern)) {
                        stringBuilder.append(line);
                        countLinesToAdd = countLines;
                    } else {
                        countLinesToAdd = 0;
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    private boolean match(final String line, @Nonnull Pattern pattern) {
        if (line == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }


}
