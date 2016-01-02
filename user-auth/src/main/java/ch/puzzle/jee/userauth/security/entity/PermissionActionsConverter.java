package ch.puzzle.jee.userauth.security.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Converter
public class PermissionActionsConverter implements AttributeConverter<EnumSet, String> {

    public static final String SEPARATOR = ",";
    public static final String DATABASE_VALUE_CREATE = "C";
    public static final String DATABASE_VALUE_READ = "R";
    public static final String DATABASE_VALUE_UPDATE = "U";
    public static final String DATABASE_VALUE_DELETE = "D";

    @Override
    public String convertToDatabaseColumn(EnumSet a) {
        EnumSet<Action> attribute = (EnumSet<Action>) a; // could not use generic type due to Hibernate Bug HHH-8804
        return attribute.stream().map(PermissionActionsConverter::stringOf).collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public EnumSet<Action> convertToEntityAttribute(String dbData) {
        EnumSet<Action> actions = EnumSet.noneOf(Action.class);
        if (dbData == null || dbData.isEmpty()) {
            return actions;
        }
        for (String s : dbData.split(SEPARATOR)) {
            actions.add(actionOf(s));
        }
        return actions;
    }

    private static String stringOf(Action action) {
        switch (action) {
            case READ:
                return DATABASE_VALUE_READ;
            case CREATE:
                return DATABASE_VALUE_CREATE;
            case UPDATE:
                return DATABASE_VALUE_UPDATE;
            case DELETE:
                return DATABASE_VALUE_DELETE;
            default:
                throw new IllegalStateException("Not recognized value " + action);
        }
    }

    private static Action actionOf(String s) {
        switch (s) {
            case DATABASE_VALUE_READ:
                return Action.READ;
            case DATABASE_VALUE_CREATE:
                return Action.CREATE;
            case DATABASE_VALUE_UPDATE:
                return Action.UPDATE;
            case DATABASE_VALUE_DELETE:
                return Action.DELETE;
            default:
                throw new IllegalStateException("Cannot convert value " + s + " to Action type");
        }
    }
}
