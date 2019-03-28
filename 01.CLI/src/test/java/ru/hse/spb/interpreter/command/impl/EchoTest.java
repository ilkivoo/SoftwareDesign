package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.command.BashCommandTest;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EchoTest extends BashCommandTest {

    @Test
    public void testIsFits() {
        final Echo echo = new Echo();
        assertTrue(echo.isFits(createEntity("echo 123123123")));
        assertTrue(echo.isFits(createEntity("echo 123 123 123")));
        assertTrue(echo.isFits(createEntity("echo        1231 23123 ")));
        assertTrue(echo.isFits(createEntity("    echo        1231 23123 ")));
        assertFalse(echo.isFits(createEntity("echo1 asdas asdca ds")));
        assertFalse(echo.isFits(createEntity("1echo asdas asdca ds")));
    }

    @Test
    public void testApply() {
        final Echo echo = new Echo();
        assertEquals("123 123 123 123 123",
                echo.apply(createEntity("echo        123  123 123 123             123")).getResult());
        assertEquals("123 123 123 123123",
                echo.apply(createEntity("echo        123  123 123 123123")).getResult());
        assertEquals("",
                echo.apply(createEntity("echo    ")).getResult());
        assertEquals("ABC 123 123 123123",
                echo.apply(createEntity("echo        ABC  123 123 123123")).getResult());
        assertEquals("ABC 123 123 123123 aaa          aaa",
                echo.apply(createEntity("echo        ABC  123 123 123123 ", "aaa          aaa")).getResult());
        assertEquals("  some                                   text  ",
                echo.apply(createEntity("echo ", "  some                                   text  ")).getResult());
    }
}