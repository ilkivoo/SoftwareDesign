package ru.hse.spb.interpreter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ru.hse.spb.interpreter.Interpreter;
import ru.hse.spb.interpreter.Preprocessor;
import ru.hse.spb.interpreter.Tokenizer;
import ru.hse.spb.interpreter.command.Assignment;
import ru.hse.spb.interpreter.command.BashCommand;
import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.PipeSplitCommand;
import ru.hse.spb.interpreter.model.Token;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component("Interpreter")
public class InterpreterImpl implements Interpreter {
    private final Map<String, String> envVariables;
    private final Assignment assignment;
    private final Tokenizer tokenizer;
    private final Preprocessor preprocessor;
    private final List<BashCommand> commands;
    private final PrintStream out;
    private final Scanner in;
    private static final Logger LOG = LoggerFactory.getLogger(Preprocessor.class);

    @Inject
    public InterpreterImpl(final List<BashCommand> commands,
                           final Assignment assignment,
                           @Qualifier("envVariables") final Map<String, String> envVariables,
                           final PrintStream out,
                           final Scanner in,
                           final Preprocessor preprocessor,
                           final Tokenizer tokenizer) {
        this.commands = commands;
        this.assignment = assignment;
        this.envVariables = envVariables;
        this.out = out;
        this.in = in;
        this.preprocessor = preprocessor;
        this.tokenizer = tokenizer;
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            envVariables.putAll(System.getenv());
            final String input = in.nextLine();
            final List<Token> tokens = tokenizer.getTokens(input);
            for (Token token : tokens) {
                BashCommandResult prevResult = new BashCommandResult(null);
                for (PipeSplitCommand inputCommand : token.getCommands()) {
                    if (assignment.updateVariables(inputCommand.getEntities())) {
                        continue;
                    }
                    final List<Entity> commandWithReplacement = preprocessor.run(inputCommand.getEntities());
                    final Optional<BashCommandResult> response = findCommand(commandWithReplacement, prevResult);
                    if (!response.isPresent()) {
                        out.println("command not found: " + split(commandWithReplacement));
                        continue;
                    }
                    prevResult = response.orElseGet(() -> new BashCommandResult(""));
                }
                out.println(prevResult.getResultOrDefault(""));
            }
        }
    }

    private Optional<BashCommandResult> findCommand(@Nonnull final List<Entity> commandEntities, final BashCommandResult prevResult) {
        for (BashCommand command : commands) {
            if (command.isFits(commandEntities)) {
                return Optional.of(command.apply(commandEntities, prevResult));
            }
        }
        try {
            final String commandString = commandEntities.stream()
                    .map(Entity::getValue)
                    .collect(Collectors.joining(""));
            final Process process = Runtime.getRuntime().exec(commandString);
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOG.warn("Process interrupted", e);
            }
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return Optional.of(new BashCommandResult(stringBuilder.toString()));
        } catch (IOException e) {
            LOG.error("Unable to read data from process", e);
            return Optional.empty();
        }

    }

    @Nonnull
    final String split(@Nonnull final List<Entity> entities) {
        return entities.stream().map(Entity::getValue).collect(Collectors.joining(""));
    }
}
