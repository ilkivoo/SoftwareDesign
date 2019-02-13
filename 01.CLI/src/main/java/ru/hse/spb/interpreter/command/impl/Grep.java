package ru.hse.spb.interpreter.command.impl;

import org.springframework.stereotype.Service;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;
import org.apache.commons.cli.*;

import javax.annotation.Nonnull;
import java.util.Optional;

@Service
public class Grep implements BashCommand {
    private final static String COMMAND_NAME = "grep";

    @Override
    public boolean isFits(final String inputString) {
        final Optional<String[]> dataOpt = getData(inputString);
        if (!dataOpt.isPresent()) {
            return false;
        }
        final String[] data = dataOpt.get();
        if (data.length == 0) {
            return false;
        }

        final String commandName = data[0];
        return commandName.equalsIgnoreCase(COMMAND_NAME);
    }

    @Nonnull
    @Override
    public BashCommandResult apply(String inputString) {
        getCommandArguments(getData(inputString).get());
        return new BashCommandResult("meow");
    }


    private Optional<String[]> getData(final String inputString) {
        if (inputString == null || inputString.equals("")) {
            return Optional.empty();
        }
        return Optional.of(inputString.split("\\s+"));
    }

    private void getCommandArguments(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            return;
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        System.out.println(inputFilePath);
        System.out.println(outputFilePath);
    }
}
