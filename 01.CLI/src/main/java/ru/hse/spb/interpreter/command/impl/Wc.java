package ru.hse.spb.interpreter.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getInputStreams;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getNonEmptyString;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.readFiles;


@Component
public class Wc implements BashCommand {
    private final InputStream defaultInputStream;
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*wc(\\s+|$)");
    private static final Logger LOG = LoggerFactory.getLogger(Wc.class);

    @Inject
    public Wc(final InputStream defaultInputStream) {
        this.defaultInputStream = defaultInputStream;
    }

    @Override
    public boolean isFits(String inputString) {
        return getData(inputString.toLowerCase()).isPresent();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        final Optional<List<String>> dataOpt = getData(inputString);
        if (!dataOpt.isPresent()) {
            LOG.warn("unable to apply command wc to " + inputString);
            return new BashCommandResult("");
        }
        final Map<String, InputStream> inputStreamByFileName = getInputStreams(dataOpt.get(), defaultInputStream);
        final Map<String, String> textByFileName= readFiles(inputStreamByFileName);
        final String result = getResult(textByFileName);
        return new BashCommandResult(result);
    }

    @Override
    @Nonnull
    public BashCommandResult apply(final String inputString, final BashCommandResult predResult) {
        final Optional<List<String>> dataOpt = getData(inputString);
        if (!dataOpt.isPresent()) {
            LOG.warn("unable to apply command wc to " + inputString);
            return new BashCommandResult("");
        }
        if (getNonEmptyString(dataOpt.get()).size() == 0
                && predResult != null
                && predResult.getResult() != null) {
            final Map<String, InputStream> inputStreamByFileName = new HashMap<>();
            inputStreamByFileName.put("", new ByteArrayInputStream(predResult.getResult().getBytes()));
            final Map<String, String> textByFileName = readFiles(inputStreamByFileName);
            final String result = getResult(textByFileName);
            return new BashCommandResult(result);
        }
        return apply(inputString);
    }


    @Nonnull
    private Optional<List<String>> getData(final String inputString) {
        final Matcher matcher = COMMAND_PATTERN.matcher(inputString);
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String fileNames = matcher.replaceFirst("");
        return Optional.of(Arrays.asList(fileNames.split("\\s+")));
    }

    private String getResult(final Map<String, String> textByFileName) {
        if (textByFileName== null) {
            return "";
        }
        return textByFileName.keySet().stream()
                .map(fileName -> getWordCount(textByFileName.getOrDefault(fileName, ""), "  ", fileName))
                .collect(Collectors.joining("\n"));
    }

    private String getWordCount(final String text, final String delimiter, final String fileName) {
        return ((Integer) text.split("\n").length).toString() + delimiter
                + ((Integer) text.split("\\s+").length).toString() + delimiter
                + ((Integer) text.getBytes().length).toString() + delimiter
                + fileName;

    }


}
