package ru.hse.spb.interpreter.command;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.spb.interpreter.model.Entity;
import ru.hse.spb.interpreter.model.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Assignment {
    private static final String IDENTIFIER_PATTERN = "[_a-zA-Z]([_a-zA-Z0-9])*";
    private final Map<String, String> envVariables;

    public Assignment(@Qualifier("envVariables") final Map<String, String> envVariables) {
        this.envVariables = envVariables;
    }

    public boolean updateVariables(final List<Entity> entities) {
        if (entities == null
                || entities.size() == 0
                || entities.get(0).getType() !=  EntityType.SIMPLE_PART) {
            return false;
        }
        final List<Entity> entitiesCopy = new ArrayList<>(entities);
        final String firstEntity = entitiesCopy.get(0).getValue();
        entitiesCopy.remove(0);
        Pattern pattern = Pattern.compile("^"  + IDENTIFIER_PATTERN + "=");
        Matcher matcher = pattern.matcher(firstEntity);
        if (!matcher.find()) {
            return false;
        }
        final String ident = firstEntity.substring(0, matcher.end() - 1);
        final String residue = firstEntity.substring(matcher.end()) +
                entitiesCopy.stream()
                        .map(Entity::getValue)
                        .collect(Collectors.joining()).trim();
        pattern = Pattern.compile("\\s+");
        matcher = pattern.matcher(residue);
        if (!matcher.find()) {
            envVariables.put(ident, residue);
            return true;
        }
        return false;

    }

}
