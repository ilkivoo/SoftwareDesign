package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.command.BashCommandTest;
import ru.hse.spb.interpreter.model.BashCommandResult;


import static org.junit.Assert.*;

public class WcTest extends BashCommandTest {
    @Test
    public void testIsFits() {
        Wc wc = new Wc(null);
        assertTrue(wc.isFits(createEntity("wc 124")));
        assertTrue(wc.isFits(createEntity("wc 124")));
        assertTrue(wc.isFits(createEntity("wc 124 123 123 123")));
        assertTrue(wc.isFits(createEntity("wc         124   ")));
        assertTrue(wc.isFits(createEntity("wc                   124")));
        assertFalse(wc.isFits(createEntity("wc1 124")));
        assertFalse(wc.isFits(createEntity("1wc 124")));
        assertTrue(wc.isFits(createEntity("    wc 124")));
    }

    @Test
    public void testApply() {
        final Wc wc = new Wc(null);
        assertEquals("1  1  8  src/test/resources/1.txt", wc.apply(createEntity("wc src/test/resources/1.txt")).getResult());
    }

    @Test
    public void testApplyIfFileNotFound() {
        final Wc wc = new Wc(null);
        assertEquals("", wc.apply(createEntity("wc src/test/resources/notExist.txt")).getResult());
    }

    @Test
    public void testApplyWithPrevResult() {
        final Wc wc = new Wc(null);
        assertEquals("2  5  20  ",
                wc.apply(createEntity("wc "), new BashCommandResult("123 456 789\n 123 123")).getResult());
    }

    @Test
    public void testApplyWithSpaces() {
        final Wc wc = new Wc(null);
        assertEquals("1  3  35  src/test/resources/2.txt", wc.apply(createEntity("wc src/test/resources/2.txt")).getResult());
    }
}