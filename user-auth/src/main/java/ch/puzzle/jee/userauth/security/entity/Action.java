package ch.puzzle.jee.userauth.security.entity;

import java.util.EnumSet;

public enum Action {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SPECIFY;

    public static EnumSet<Action> all() {
        return EnumSet.of(CREATE, READ, UPDATE, DELETE);
    }
}
