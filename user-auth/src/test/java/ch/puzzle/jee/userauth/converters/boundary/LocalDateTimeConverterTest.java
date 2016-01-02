package ch.puzzle.jee.userauth.converters.boundary;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocalDateTimeConverterTest {

    private LocalDateTimeConverter converter;

    @Before
    public void init() throws Exception {
        converter = new LocalDateTimeConverter();
    }

    @Test
    public void shouldConvertToDatabaseTimestamp() throws Exception {
        // given
        LocalDateTime reference = LocalDateTime.of(2015, 1, 1, 12, 30);

        // when & then
        assertThat(converter.convertToDatabaseColumn(reference), is(Timestamp.valueOf(reference)));
        assertThat(converter.convertToDatabaseColumn(null), is(nullValue()));
    }

    @Test
    public void shouldConvertToLocalDateTime() throws Exception {
        // given
        LocalDateTime reference = LocalDateTime.of(2015, 1, 1, 12, 30);

        // when & then
        assertThat(converter.convertToEntityAttribute(Timestamp.valueOf(reference)), is(reference));
        assertThat(converter.convertToEntityAttribute(null), is(nullValue()));
    }
}