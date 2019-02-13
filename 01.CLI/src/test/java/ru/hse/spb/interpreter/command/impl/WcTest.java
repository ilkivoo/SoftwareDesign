package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.model.BashCommandResult;

import java.io.IOException;

import static org.junit.Assert.*;

public class WcTest {
    @Test
    public void testIsFits() {
        Wc wc = new Wc(null);
        assertTrue(wc.isFits("wc 124"));
        assertTrue(wc.isFits("wc 124"));
        assertTrue(wc.isFits("wc 124 123 123 123"));
        assertTrue(wc.isFits("wc         124   "));
        assertTrue(wc.isFits("wc                   124"));
        assertFalse(wc.isFits("wc1 124"));
        assertFalse(wc.isFits("1wc 124"));
        assertTrue(wc.isFits("    wc 124"));
    }

    @Test
    public void testApply() throws IOException {
        final Wc wc = new Wc(null);
        assertEquals("1  1  37  src/test/resources/1.txt", wc.apply("wc src/test/resources/1.txt").getResult());
    }

    @Test
    public void testApplyIfFileNotFound() throws IOException {
        final Wc wc = new Wc(null);
        assertEquals("", wc.apply("wc src/test/resources/notExist.txt").getResult());
    }

    @Test
    public void testApplyWithPredResult() throws IOException {
        final Wc wc = new Wc(null);
        assertEquals("2  5  20  ",
                wc.apply("wc ", new BashCommandResult("123 456 789\n 123 123")).getResult());
    }
}