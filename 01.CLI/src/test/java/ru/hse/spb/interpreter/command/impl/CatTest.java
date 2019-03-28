package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.command.BashCommandTest;
import ru.hse.spb.interpreter.model.BashCommandResult;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CatTest extends BashCommandTest {


    @Test
    public void testIsFits() {
        Cat cat = new Cat(null);
        assertTrue(cat.isFits(createEntity("cat 124")));
        assertTrue(cat.isFits(createEntity("cAt 124")));
        assertTrue(cat.isFits(createEntity("cat 124 123 123 123")));
        assertTrue(cat.isFits(createEntity("cat         124   ")));
        assertTrue(cat.isFits(createEntity("cat                   124")));
        assertFalse(cat.isFits(createEntity("cat1 124")));
        assertFalse(cat.isFits(createEntity("1cat 124")));
        assertTrue(cat.isFits(createEntity("    cat 124")));
    }

    @Test
    public void testApply() {
        final Cat cat = new Cat(null);
        assertEquals("lalalala", cat.apply(createEntity("cat src/test/resources/1.txt")).getResult());
    }

    @Test
    public void testApplyIfFileNotFound() {
        final Cat cat = new Cat(null);
        assertEquals("", cat.apply(createEntity("cat src/test/resources/notExist.txt")).getResult());
    }

    @Test
    public void testApplyWithPredResult() {
        final Cat cat = new Cat(null);
        assertEquals("123 456 789",
                cat.apply(createEntity("cat "), new BashCommandResult("123 456 789")).getResult());
    }

}