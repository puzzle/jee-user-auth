package ch.puzzle.jee.userauth.security.entity;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static ch.puzzle.jee.userauth.security.entity.Action.*;
import static java.util.EnumSet.of;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class PermissionActionsConverterTest {

    private PermissionActionsConverter converter;

    @Before
    public void init() {
        converter = new PermissionActionsConverter();
    }

    @Test
    public void shouldMapKnownValuesToDatabaseColumn() throws Exception {
        // when & then
        assertThat(converter.convertToDatabaseColumn(of(CREATE)), is("C"));
        assertThat(converter.convertToDatabaseColumn(of(READ)), is("R"));
        assertThat(converter.convertToDatabaseColumn(of(UPDATE)), is("U"));
        assertThat(converter.convertToDatabaseColumn(of(DELETE)), is("D"));
        assertThat(converter.convertToDatabaseColumn(of(CREATE, READ, UPDATE, DELETE)), is("C,R,U,D"));
    }

    @Test
    public void shouldMapKnownValuesFromDatabaseToString() throws Exception {
        // when & then
        assertThat(converter.convertToEntityAttribute("C"), contains(CREATE));
        assertThat(converter.convertToEntityAttribute("R"), contains(READ));
        assertThat(converter.convertToEntityAttribute("U"), contains(UPDATE));
        assertThat(converter.convertToEntityAttribute("D"), contains(DELETE));
        assertThat(converter.convertToEntityAttribute("C,R,U,D"), containsInAnyOrder(CREATE, READ, UPDATE, DELETE));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotMapSpecifyValueToDatabaseColumn() throws Exception {
        // when
        converter.convertToDatabaseColumn(of(SPECIFY));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotMapUnknownValuesFromDatabase() throws Exception {
        // when & then
        converter.convertToEntityAttribute("unknown-value");
    }
}