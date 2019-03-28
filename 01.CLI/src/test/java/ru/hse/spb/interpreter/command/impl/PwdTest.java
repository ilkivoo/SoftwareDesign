package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.command.BashCommandTest;
import ru.hse.spb.interpreter.model.BashCommandResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.*;

public class PwdTest extends BashCommandTest {

    @Test
    public void testIsFits() {
        final Pwd pwd = new Pwd();
        assertTrue(pwd.isFits(createEntity("pwd")));
        assertTrue(pwd.isFits(createEntity("pwd 123 123 123")));
        assertTrue(pwd.isFits(createEntity("pwd        1231 23123 ")));
        assertTrue(pwd.isFits(createEntity("    pwd        1231 23123 ")));
        assertFalse(pwd.isFits(createEntity("pwd1 asdas asdca ds")));
        assertFalse(pwd.isFits(createEntity("1pwd asdas asdca ds")));
    }

    @Test
    public void testApply() {
        final Pwd pwd = new Pwd();
        final String path = pwd.apply(createEntity("pwd ")).getResult();
        assertTrue(path.contains("01.CLI"));
    }
}