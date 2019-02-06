package ru.hse.spb.interpreter;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;
import ru.hse.spb.interpreter.model.PipeSplitCommand;
import ru.hse.spb.interpreter.model.Token;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Component
public class Tokenizer {

    @Nonnull
    public List<Token> getTokens(final String inputString) {
        final List<Token> tokens = new ArrayList<>();
        final List<PipeSplitCommand> commands = new ArrayList<>();
        boolean isPrimeOpen = false;
        boolean isDoublePrimeOpen = false;
        int countBackslash = 0;
        final StringBuilder commandBuilder = new StringBuilder();
        for (char curSymbol : inputString.toCharArray()) {
            if (curSymbol == '\'') {
                if (isPrimeOpen) {
                    isPrimeOpen = false;
                } else if (!isDoublePrimeOpen && countBackslash % 2 == 0) {
                    isPrimeOpen = true;
                }
            }
            if (curSymbol == '\"' && countBackslash % 2 == 0 && !isPrimeOpen) {
                isDoublePrimeOpen = !isDoublePrimeOpen;
            }
            if (isCommandFinish(curSymbol, countBackslash, isPrimeOpen, isDoublePrimeOpen)
                    && commandBuilder.toString().trim().length() > 0) {
                commands.add(new PipeSplitCommand(
                        getEntities(commandBuilder.toString().trim())));
                commandBuilder.setLength(0);
            } else {
                commandBuilder.append(curSymbol);
            }
            if (isTokenFinish(curSymbol, countBackslash, isPrimeOpen, isDoublePrimeOpen)
                    && commands.size() > 0) {
                tokens.add(new Token(new ArrayList<>(commands)));
                commands.clear();
            }
            countBackslash = (curSymbol == '\\') ? countBackslash + 1 : 0;
        }

        if (commandBuilder.toString().trim().length() > 0) {
            commands.add(new PipeSplitCommand(
                    getEntities(commandBuilder.toString().trim())));
        }
        if (commands.size() > 0) {
            tokens.add(new Token(commands));
        }
        return tokens;
    }

    @Nonnull
    private List<Entity> getEntities(final String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        final List<Entity> entities = new ArrayList<>();
        final StringBuilder entityBuilder = new StringBuilder();
        boolean isPrimeOpen = false;
        boolean isDoublePrimeOpen = false;
        int countBackslash = 0;
        for (char curSymbol : input.toCharArray()) {
            if (curSymbol == '\'') {
                if (isPrimeOpen) {
                    entities.add(new Entity(EntityType.PART_IN_PRIME, entityBuilder.toString()));
                    entityBuilder.setLength(0);
                    isPrimeOpen = false;
                } else if (!isDoublePrimeOpen && countBackslash % 2 == 0) {
                    isPrimeOpen = true;
                    entities.add(new Entity(EntityType.SIMPLE_PART, entityBuilder.toString()));
                    entityBuilder.setLength(0);
                } else {
                    entityBuilder.append(curSymbol);
                }
            } else if (curSymbol == '\"') {
                if (isDoublePrimeOpen && countBackslash % 2 == 0) {
                    entities.add(new Entity(EntityType.PART_IN_DOUBLE_PRIME, entityBuilder.toString()));
                    entityBuilder.setLength(0);
                    isDoublePrimeOpen = false;
                } else if (!isPrimeOpen && !isDoublePrimeOpen && countBackslash % 2 == 0) {
                    isDoublePrimeOpen = true;
                    entities.add(new Entity(EntityType.SIMPLE_PART, entityBuilder.toString()));
                    entityBuilder.setLength(0);
                } else {
                    entityBuilder.append(curSymbol);
                }
            } else {
                entityBuilder.append(curSymbol);
            }
            countBackslash = (curSymbol == '\\') ? countBackslash + 1 : 0;
        }

        if (entityBuilder.length() > 0) {
            if (isPrimeOpen) {
                entities.add(new Entity(EntityType.PART_IN_PRIME, entityBuilder.toString()));
            } else if (isDoublePrimeOpen) {
                entities.add(new Entity(EntityType.PART_IN_DOUBLE_PRIME, entityBuilder.toString()));
            } else {
                entities.add(new Entity(EntityType.SIMPLE_PART, entityBuilder.toString()));
            }
        }
        return entities;
    }

    private boolean isTokenFinish(char curSymbol,
                                  int countBackslash,
                                  boolean isPrimeOpen,
                                  boolean isDoublePrimeOpen) {
        return (!isPrimeOpen && !isDoublePrimeOpen && curSymbol == ';' && countBackslash % 2 == 0)
                || (curSymbol == '\n');
    }

    private boolean isCommandFinish(char curSymbol,
                                    int countBackslash,
                                    boolean isPrimeOpen,
                                    boolean isDoublePrimeOpen) {
        return (!isPrimeOpen && !isDoublePrimeOpen && curSymbol == '|' && countBackslash % 2 == 0)
                || isTokenFinish(curSymbol, countBackslash, isPrimeOpen, isDoublePrimeOpen);

    }
}
