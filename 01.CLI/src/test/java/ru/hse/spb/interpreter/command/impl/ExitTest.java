package ru.hse.spb.interpreter.command.impl;


import org.junit.Test;
import ru.hse.spb.interpreter.command.BashCommandTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExitTest extends BashCommandTest {
    @Test
    public void testIsFits() {
        final Exit exit = new Exit();
        assertTrue(exit.isFits(createEntity("exit 123123123")));
        assertTrue(exit.isFits(createEntity("exit 123 123 123")));
        assertTrue(exit.isFits(createEntity("exit        1231 23123 ")));
        assertTrue(exit.isFits(createEntity("    exit        1231 23123 ")));
        assertFalse(exit.isFits(createEntity("exit1 asdas asdca ds")));
        assertFalse(exit.isFits(createEntity("1exit asdas asdca ds")));
    }
}