package ru.hse.spb.interpreter;

import org.junit.Test;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PreprocessorTest {
    /*@Test
    public void testReplaceVariable() {
        final Map<String, String> variables = new HashMap<>();
        variables.put("lalala", "123");
        Preprocessor preprocessor = new Preprocessor(variables);
        final String correctCommand = "echo 123 123 $lalala";
        final String returnCommand = (preprocessor.run(Arrays.asList(new Entity(EntityType.SIMPLE_PART, "echo $lalala "),
                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "$lalala"),
                new Entity(EntityType.SIMPLE_PART, " "),
                new Entity(EntityType.PART_IN_PRIME, "$lalala"))));
        assertEquals(correctCommand, returnCommand);
    }

    @Test
    public void testReplaceIfVariableNotFound() {
        final Map<String, String> variables = new HashMap<>();
        variables.put("lr", "123");
        Preprocessor preprocessor = new Preprocessor(variables);
        final String correctCommand = "echo   $lalala";
        final String returnCommand = (preprocessor.run(Arrays.asList(new Entity(EntityType.SIMPLE_PART, "echo $lalala "),
                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "$lalala"),
                new Entity(EntityType.SIMPLE_PART, " "),
                new Entity(EntityType.PART_IN_PRIME, "$lalala"))));
        assertEquals(correctCommand, returnCommand);
    }*/

}