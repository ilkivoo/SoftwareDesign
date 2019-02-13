package ru.hse.spb.interpreter.command.impl;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExitTest {
    @Test
    public void testIsFits() {
        final Exit exit = new Exit();
        assertTrue(exit.isFits("exit 123123123"));
        assertTrue(exit.isFits("exit 123 123 123"));
        assertTrue(exit.isFits("exit        1231 23123 "));
        assertTrue(exit.isFits("    exit        1231 23123 "));
        assertFalse(exit.isFits("exit1 asdas asdca ds"));
        assertFalse(exit.isFits("1exit asdas asdca ds"));
    }
}