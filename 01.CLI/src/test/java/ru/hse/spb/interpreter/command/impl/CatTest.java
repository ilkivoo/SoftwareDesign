package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.model.BashCommandResult;

import java.io.IOException;

import static org.junit.Assert.*;

public class CatTest {


    @Test
    public void testIsFits() {
        Cat cat = new Cat(null);
        assertTrue(cat.isFits("cat 124"));
        assertTrue(cat.isFits("cAt 124"));
        assertTrue(cat.isFits("cat 124 123 123 123"));
        assertTrue(cat.isFits("cat         124   "));
        assertTrue(cat.isFits("cat                   124"));
        assertFalse(cat.isFits("cat1 124"));
        assertFalse(cat.isFits("1cat 124"));
        assertTrue(cat.isFits("    cat 124"));
    }

    @Test
    public void testApply() {
        final Cat cat = new Cat(null);
        assertEquals("Приветики-Конфетики", cat.apply("cat src/test/resources/1.txt").getResult());
    }

    @Test
    public void testApplyIfFileNotFound() throws IOException {
        final Cat cat = new Cat(null);
        assertEquals("", cat.apply("cat src/test/resources/notExist.txt").getResult());
    }

    @Test
    public void testApplyWithPredResult() throws IOException {
        final Cat cat = new Cat(null);
        assertEquals("123 456 789",
                cat.apply("cat ", new BashCommandResult("123 456 789")).getResult());
    }

}