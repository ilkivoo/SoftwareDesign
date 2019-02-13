package ru.hse.spb.interpreter.command;

import org.junit.Test;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AssignmentTest {

    @Test
    public void testUpdateVariables() {
        Map<String, String> variables = new HashMap<>();
        Assignment assignment = new Assignment(variables);
        assignment.updateVariables(Arrays.asList(new Entity(EntityType.SIMPLE_PART, "lalala="),
                new Entity(EntityType.PART_IN_PRIME, "123")));
        Map<String, String> correct = new HashMap<>();
        correct.put("lalala", "123");
        assertEquals(correct, variables);
    }

    @Test
    public void testUpdateVariablesWithWhiteSpace() {
        Map<String, String> variables = new HashMap<>();
        Assignment assignment = new Assignment(variables);
        assignment.updateVariables(Collections.singletonList(new Entity(EntityType.SIMPLE_PART, "lalala=123 123   ")));
        Map<String, String> correct = new HashMap<>();
        assertEquals(correct, variables);
    }

    @Test
    public void testUpdateVariablesWithDoublePrimes() {
        Map<String, String> variables = new HashMap<>();
        Assignment assignment = new Assignment(variables);
        assignment.updateVariables(Arrays.asList(new Entity(EntityType.SIMPLE_PART, "lalala=123"),
                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "456")));
        Map<String, String> correct = new HashMap<>();
        correct.put("lalala", "123456");
        assertEquals(correct, variables);
    }

    @Test
    public void testUpdateVariablesWithPrimes() {
        Map<String, String> variables = new HashMap<>();
        Assignment assignment = new Assignment(variables);
        assignment.updateVariables(Arrays.asList(new Entity(EntityType.SIMPLE_PART, "lalala=123"),
                new Entity(EntityType.PART_IN_PRIME, "456"),
                new Entity(EntityType.PART_IN_DOUBLE_PRIME, "789")));
        Map<String, String> correct = new HashMap<>();
        correct.put("lalala", "123456789");
        assertEquals(correct, variables);
    }
}