package ch.puzzle.jee.userauth.mappers.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ErrorMapperTest {
    private ErrorMapper mapper;

    @Before
    public void init() {
        mapper = ErrorMapper.create();
    }

    @Test
    public void shouldMapMessageKeysToErrorModel() throws Exception {
        // when
        JsonObject messages = mapper.addMessageKey("key1").addMessageKey("key2", "param1", "param2").build();

        // when
        JsonArray errors = messages.getJsonArray("errors");
        assertThat(errors, is(notNullValue()));

        JsonObject error1 = errors.getJsonObject(0);
        assertThat(error1.getString("messageKey"), is("key1"));
        assertThat(error1.getString("messageText", null), is(nullValue()));
        assertThat(error1.getString("messageArguments", null), is(nullValue()));
        assertThat(error1.getString("correlationId"), is("<<empty>>"));

        JsonObject error2 = errors.getJsonObject(1);
        assertThat(error2.getString("messageKey"), is("key2"));
        assertThat(error2.getString("messageText", null), is(nullValue()));
        assertThat(error2.getJsonArray("messageArguments").size(), is(2));
        assertThat(error2.getString("correlationId"), is("<<empty>>"));
    }

    @Test
    public void shouldMapMessageTextToErrorModel() throws Exception {
        // when
        JsonObject messages = mapper.addMessageText("Error Message {0}").build();

        // when
        JsonObject error = messages.getJsonArray("errors").getJsonObject(0);
        assertThat(error.getString("messageText", null), is("Error Message {0}"));
    }

    @Test
    public void shouldMapMessageArgumentsToErrorModel() throws Exception {
        // when
        JsonObject messages = mapper.addMessageKey("key", "param1", "param2").build();

        // when
        JsonObject error = messages.getJsonArray("errors").getJsonObject(0);
        JsonArray messageArguments = error.getJsonArray("messageArguments");
        assertThat(messageArguments.getString(0), is("param1"));
        assertThat(messageArguments.getString(1), is("param2"));
    }
}