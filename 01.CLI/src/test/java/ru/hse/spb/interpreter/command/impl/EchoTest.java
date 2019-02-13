package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class EchoTest {

    @Test
    public void testIsFits() {
        final Echo echo = new Echo();
        assertTrue(echo.isFits("echo 123123123"));
        assertTrue(echo.isFits("echo 123 123 123"));
        assertTrue(echo.isFits("echo        1231 23123 "));
        assertTrue(echo.isFits("    echo        1231 23123 "));
        assertFalse(echo.isFits("echo1 asdas asdca ds"));
        assertFalse(echo.isFits("1echo asdas asdca ds"));
}

    @Test
    public void testApply() {
        final Echo echo = new Echo();
        assertEquals("123 123 123 123 123",
                echo.apply("echo        123  123 123 123             123").getResult());
        assertEquals("123 123 123 123123",
                echo.apply("echo        123  123 123 123123").getResult());
        assertEquals("",
                echo.apply("echo    ").getResult());
    }
}