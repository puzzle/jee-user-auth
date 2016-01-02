package ch.puzzle.jee.userauth.converters.boundary;

import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocalDateConverterTest {

    private LocalDateConverter converter;

    @Before
    public void init() throws Exception {
        converter = new LocalDateConverter();
    }

    @Test
    public void shouldConvertToDatabaseDate() throws Exception {
        // given
        LocalDate reference = LocalDate.of(2015, 1, 1);

        // when & then
        assertThat(converter.convertToDatabaseColumn(reference), is(Date.valueOf(reference)));
        assertThat(converter.convertToDatabaseColumn(null), is(nullValue()));
    }

    @Test
    public void shouldConvertToLocalDate() throws Exception {
        // given
        LocalDate reference = LocalDate.of(2015, 1, 1);

        // when & then
        assertThat(converter.convertToEntityAttribute(Date.valueOf(reference)), is(reference));
        assertThat(converter.convertToEntityAttribute(null), is(nullValue()));
    }
}