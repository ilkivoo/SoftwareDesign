package ru.hse.spb.interpreter.command.util;


import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

public class BashCommandUtil {

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
                            //TODO запись в лог
                        }
                    });
        }
        return inputStreamMap;
    }

    @Nonnull
    public static Map<String, String> readFiles(final Map<String, InputStream> inputStreamMap) {
        final Map<String, String> textForInputStreams = new HashMap<>();
        inputStreamMap.keySet().stream().forEach(
                fileName -> {
                    try {
                        textForInputStreams.put(fileName,
                                IOUtils.toString(inputStreamMap.get(fileName)));
                    } catch (IOException e) {
                        //todo запись в лог
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

    private static boolean isNonEmpty(String data) {
        if (data == null) {
            return false;
        }
        final Pattern pattern = Pattern.compile("^\\s*$");
        final Matcher matcher = pattern.matcher(data);
        return !matcher.find();
    }
}
