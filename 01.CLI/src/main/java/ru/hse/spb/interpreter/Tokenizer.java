package ru.hse.spb.interpreter;

import org.springframework.stereotype.Component;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;
import ru.hse.spb.interpreter.model.PipeSplitCommand;
import ru.hse.spb.interpreter.model.Token;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            final State state = getState(curSymbol, isPrimeOpen, isDoublePrimeOpen, countBackslash);
            if (!state.getTypeOpt().isPresent()) {
                entityBuilder.append(curSymbol);
            }
            else {
                putEntity(entities, state.getTypeOpt().get(), entityBuilder.toString());
                entityBuilder.setLength(0);
                isPrimeOpen = state.isPrimeOpen;
                isDoublePrimeOpen = state.isDoublePrimeOpen;
            }
            countBackslash = (curSymbol == '\\') ? countBackslash + 1 : 0;
        }

        if (entityBuilder.length() > 0) {
            final State state = getState('\0', isPrimeOpen, isDoublePrimeOpen, countBackslash);
            if (!state.getTypeOpt().isPresent()) {
                putEntity(entities, EntityType.SIMPLE_PART, entityBuilder.toString());
            }
            else {
                putEntity(entities, state.getTypeOpt().get(), entityBuilder.toString());
            }
        }
        return entities;
    }

    @Nonnull
    private State getState(char curSymbol,
                           Boolean isPrimeOpen,
                           Boolean isDoublePrimeOpen,
                           int countBackslash) {
        if (curSymbol == '\'') {
            if (isPrimeOpen) {
                return new State(EntityType.PART_IN_PRIME,
                        false,
                        isDoublePrimeOpen);
            }
            if (!isDoublePrimeOpen && countBackslash % 2 == 0) {
                return new State(EntityType.SIMPLE_PART,
                        true,
                        isDoublePrimeOpen);
            }

            return new State(isPrimeOpen, isDoublePrimeOpen);
        }
        if (curSymbol == '\"') {
            if (isDoublePrimeOpen && countBackslash % 2 == 0) {
                return new State(EntityType.PART_IN_DOUBLE_PRIME,
                        isPrimeOpen,
                        false);

            }
            if (!isPrimeOpen && !isDoublePrimeOpen && countBackslash % 2 == 0) {
                return new State(EntityType.SIMPLE_PART,
                        isPrimeOpen,
                        true);
            } else {
                return new State(isPrimeOpen, isDoublePrimeOpen);
            }
        } else {
            return new State(isPrimeOpen, isDoublePrimeOpen);
        }
    }

    private void putEntity(final List<Entity> entities,
                           final EntityType newEntityType,
                           final String newEntityValue) {
        if (newEntityValue.length() > 0) {
            entities.add(new Entity(newEntityType, newEntityValue));
        }
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

    private class State {
        private final EntityType type;
        private final boolean isPrimeOpen;
        private final boolean isDoublePrimeOpen;

        private State(boolean isPrimeOpen,
                      boolean isDoublePrimeOpen) {
            this.type = null;
            this.isPrimeOpen = isPrimeOpen;
            this.isDoublePrimeOpen = isDoublePrimeOpen;
        }

        private State(EntityType type,
                      boolean isPrimeOpen,
                      boolean isDoublePrimeOpen) {
            this.type = type;
            this.isPrimeOpen = isPrimeOpen;
            this.isDoublePrimeOpen = isDoublePrimeOpen;
        }

        @Nonnull
        private Optional<EntityType> getTypeOpt() {
            return type == null ? Optional.empty() : Optional.of(type);
        }
    }
}
