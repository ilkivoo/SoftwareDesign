package ru.hse.spb.interpreter.command.impl;

import org.junit.Test;
import ru.hse.spb.interpreter.model.BashCommandResult;

import java.io.IOException;

import static org.junit.Assert.*;

public class GrepTest {
    @Test
    public void testIsFits() {
        Grep grep = new Grep(System.in);
        assertTrue(grep.isFits("grep a"));
        assertTrue(grep.isFits("grep -i a"));
        assertTrue(grep.isFits("grep -i -w a"));
        assertTrue(grep.isFits("grep -i a 1.txt"));
        assertTrue(grep.isFits("grep -w a"));
        assertTrue(grep.isFits("grep -A n a"));

        assertFalse(grep.isFits("grep"));
        assertFalse(grep.isFits("grep1 -i"));
        assertFalse(grep.isFits("1grep -i"));
    }
    @Test
    public void testApply() {
        Grep grep = new Grep(System.in);
        assertEquals("456", grep.apply("grep 456 src/test/resources/2.txt").getResult());
    }

    @Test
    public void testApplyWithIgnoreCase() {
        Grep grep = new Grep(System.in);
        assertEquals("LALA\nlala\nLaLa\n1lala1", grep.apply("grep -i lala src/test/resources/ignoreCase.txt").getResult());
    }

    @Test
    public void testApplyWithWordRegexp() {
        Grep grep = new Grep(System.in);
        assertEquals("lala", grep.apply("grep -w lala src/test/resources/ignoreCase.txt").getResult());
        assertEquals("lala\nLaLa\n1lala1", grep.apply("grep a src/test/resources/ignoreCase.txt").getResult());
    }


    @Test
    public void testApplyWithAfterContext() {
        Grep grep = new Grep(System.in);
        assertEquals("123\n456\n123", grep.apply("grep -A 1 2 src/test/resources/afterContext.txt").getResult());

    }
    @Test
    public void testApplyWithDiffCase() {
        Grep grep = new Grep(System.in);
        assertEquals("lala\n1lala1", grep.apply("grep lala src/test/resources/ignoreCase.txt").getResult());
    }

    @Test
    public void testApplyIfFileNotFound() throws IOException {
        Grep grep = new Grep(System.in);
        assertEquals("", grep.apply("cat src/test/resources/notExist.txt").getResult());
    }


    @Test
    public void testApplyWithPredResult() throws IOException {
        Grep grep = new Grep(System.in);
        assertEquals("456",
                grep.apply("grep 5", new BashCommandResult("123\n456\n789")).getResult());
    }
}