package ru.hse.spb.interpreter.command.util;


import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashCommandUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BashCommandUtil.class);

    @Nonnull
    public static Map<String, InputStream> getInputStreams(final List<String> fileNames,
                                                           final InputStream defaultInputStream) {
        final Map<String, InputStream> inputStreamMap = new HashMap<>();
        if (getNonEmptyString(fileNames).size() == 0) {
            inputStreamMap.put("", defaultInputStream);
        } else {
            fileNames.stream()
                    .filter(fileName -> fileName != null && !fileName.equals(""))
                    .forEach(fileName -> {
                        try {
                            inputStreamMap.put(fileName, new FileInputStream(fileName));
                        } catch (FileNotFoundException e) {
                            LOG.warn("file " + fileName + " not found", e);
                        }
                    });
        }
        return inputStreamMap;
    }

    public static boolean isLineMatch(final String line, @Nonnull Pattern pattern) {
        if (line == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }

    @Nonnull
    public static Map<String, String> readFiles(final Map<String, InputStream> inputStreamMap) {
        final Map<String, String> textForInputStreams = new HashMap<>();
        inputStreamMap.keySet().forEach(
                fileName -> {
                    try {
                        textForInputStreams.put(fileName,
                                IOUtils.toString(inputStreamMap.get(fileName)));
                    } catch (IOException e) {
                        LOG.error("Unable to read data from " + fileName, e);
                    }
                }
        );
        return textForInputStreams;
    }


    @Nonnull
    public static List<String> getNonEmptyString(final List<String> datas) {
        if (datas == null || datas.size() == 0) {
            return new ArrayList<>();
        }
        return datas.stream()
                .filter(BashCommandUtil::isNonEmpty)
                .collect(Collectors.toList());
    }

    @Nonnull
    public static CommandLine getCommandLine(@Nonnull final String[] args,
                                             @Nonnull final List<Option> cmdOptions) throws ParseException {
        Options options = new Options();
        for (Option cmdOption : cmdOptions) {
            options.addOption(cmdOption);
        }
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    @Nonnull
    public static List<String> getLines(final String text) {
        if (text == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(text.split("(\\n)+"));
    }
    public static void printHelp(@Nonnull final List<Option> cmdOptions) {
        Options options = new Options();
        for (Option cmdOption : cmdOptions) {
            options.addOption(cmdOption);
        }
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("utility-name", options);
    }

    private static boolean isNonEmpty(String data) {
        if (data == null) {
            return false;
        }
        final Pattern pattern = Pattern.compile("^\\s*$");
        final Matcher matcher = pattern.matcher(data);
        return !matcher.find();
    }
}
