package ru.hse.spb.interpreter;

import org.junit.Test;
import ru.hse.spb.interpreter.exceptions.TokenizerException;
import ru.hse.spb.interpreter.impl.TokenizerImpl;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;
import ru.hse.spb.interpreter.model.PipeSplitCommand;
import ru.hse.spb.interpreter.model.Token;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TokenizerTest {

    @Test
    public void testGetTokensWithoutPrimes() {
        Tokenizer tokenizer = new TokenizerImpl();
        List<Token> correct = Collections.singletonList(new Token(
                Collections.singletonList(new PipeSplitCommand(
                        Collections.singletonList(new Entity(EntityType.SIMPLE_PART, "echo 123"))
                ))
        ));
        assertEquals(correct, tokenizer.getTokens("echo 123"));
    }

    @Test
    public void testGetTokensWithPrimes() {
        Tokenizer tokenizer = new TokenizerImpl();
        List<Token> correct = Collections.singletonList(new Token(
                Collections.singletonList(new PipeSplitCommand(
                        Arrays.asList(new Entity(EntityType.SIMPLE_PART, "echo 123 "),
                                new Entity(EntityType.PART_IN_PRIME, "123"))
                ))
        ));
        assertEquals(correct, tokenizer.getTokens("echo 123 '123'"));
    }


    @Test
    public void testGetTokensWithDoublePrimes() {
        Tokenizer tokenizer = new TokenizerImpl();
        List<Token> correct = Collections.singletonList(new Token(
                Collections.singletonList(new PipeSplitCommand(
                        Arrays.asList(new Entity(EntityType.SIMPLE_PART, "echo 123 "),
                                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "123"))
                ))
        ));
        assertEquals(correct, tokenizer.getTokens("echo 123 \"123\""));
    }

    @Test
    public void testGetTokensWithBothPrimes() {
        Tokenizer tokenizer = new TokenizerImpl();
        List<Token> correct = Collections.singletonList(new Token(
                Collections.singletonList(new PipeSplitCommand(
                        Arrays.asList(new Entity(EntityType.SIMPLE_PART, "echo 123 "),
                                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "123"),
                                new Entity(EntityType.PART_IN_PRIME, "123"))
                ))
        ));
        assertEquals(correct, tokenizer.getTokens("echo 123 \"123\" '123'"));
    }


    @Test
    public void testGetTokensWithCommands() {
        Tokenizer tokenizer = new TokenizerImpl();
        List<Token> correct = Arrays.asList(new Token(
                        Collections.singletonList(new PipeSplitCommand(
                                Collections.singletonList(new Entity(EntityType.SIMPLE_PART, "echo 123"))))),
                new Token(
                        Collections.singletonList(new PipeSplitCommand(
                                Arrays.asList(new Entity(EntityType.SIMPLE_PART, "cat "),
                                        new Entity(EntityType.PART_IN_DOUBLE_PRIME, "1.txt"))))));

        assertEquals(correct, tokenizer.getTokens("echo 123; cat \"1.txt\""));
    }


    @Test(expected = TokenizerException.class)
    public void testGetTokensWithIncorrectPipe() {
        Tokenizer tokenizer = new TokenizerImpl();
        tokenizer.getTokens("cat 1.txt |");
    }


}