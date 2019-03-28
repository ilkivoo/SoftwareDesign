package ru.hse.spb.interpreter.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

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

import static ru.hse.spb.interpreter.command.util.BashCommandUtil.cut;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getInputStreams;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.getNonEmptyString;
import static ru.hse.spb.interpreter.command.util.BashCommandUtil.readFiles;


@Component
public class Wc implements BashCommand {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s)*wc(\\s+|$)");
    private static final Logger LOG = LoggerFactory.getLogger(Wc.class);
    private final InputStream defaultInputStream;

    @Inject
    public Wc(final InputStream defaultInputStream) {
        this.defaultInputStream = defaultInputStream;
    }

    @Override
    public boolean isFits(final List<Entity> entities) {
        return getData(entities).isPresent();
    }

    @Nonnull
    @Override
    public BashCommandResult apply(final List<Entity> entities) {
        final Optional<List<String>> dataOpt = getData(entities);
        if (!dataOpt.isPresent()) {
            LOG.warn("unable to apply command wc to " + entities);
            return new BashCommandResult("");
        }
        final Map<String, InputStream> inputStreamByFileName = getInputStreams(dataOpt.get(), defaultInputStream);
        final Map<String, String> textByFileName = readFiles(inputStreamByFileName);
        final String result = getResult(textByFileName);
        return new BashCommandResult(result);
    }

    @Override
    @Nonnull
    public BashCommandResult apply(final List<Entity> entities, final BashCommandResult predResult) {
        final Optional<List<String>> dataOpt = getData(entities);
        if (!dataOpt.isPresent()) {
            LOG.warn("unable to apply command wc to " + entities);
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
        return apply(entities);
    }


    @Nonnull
    private Optional<List<String>> getData(final List<Entity> entities) {
        if (entities == null || entities.size() == 0 || entities.get(0).getType() != EntityType.SIMPLE_PART) {
            return Optional.empty();
        }
        final Matcher matcher = COMMAND_PATTERN.matcher(entities.get(0).getValue().toLowerCase());
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String firstArgument = cut(entities.get(0).getValue(), matcher.start(), matcher.end());
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstArgument);
        for (int i = 1; i < entities.size(); i++) {
            stringBuilder.append(entities.get(i));
        }
        return Optional.of(Arrays.asList(stringBuilder.toString().split("\\s+")));
    }

    private String getResult(final Map<String, String> textByFileName) {
        if (textByFileName == null) {
            return "";
        }
        return textByFileName.keySet().stream()
                .map(fileName -> getWordCount(textByFileName.getOrDefault(fileName, ""), "  ", fileName))
                .collect(Collectors.joining("\n"));
    }

    private String getWordCount(final String text, final String delimiter, final String fileName) {
        final String textTrimmed = text.trim();
        return ((Integer) text.split(System.lineSeparator()).length).toString() + delimiter
                + ((Integer) textTrimmed.split("\\s+").length).toString() + delimiter
                + ((Integer) text.getBytes().length).toString() + delimiter
                + fileName;

    }


}
