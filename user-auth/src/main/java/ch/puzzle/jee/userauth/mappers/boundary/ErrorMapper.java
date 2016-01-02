package ch.puzzle.jee.userauth.mappers.boundary;

import org.jboss.logging.MDC;

import javax.json.*;

public class ErrorMapper {

    public static final String KEY_ENTITY_NOT_FOUND = "ch.puzzle.jee.errors.entityNotFound";
    public static final String KEY_ENTITY_MISSING = "ch.puzzle.jee.errors.entityMissing";
    public static final String KEY_INVALID_INPUT = "ch.puzzle.jee.errors.invalidInput";
    public static final String KEY_INVALID_STATE = "ch.puzzle.jee.errors.invalidState";
    public static final String KEY_CONCURRENT_UPDATE = "ch.puzzle.jee.errors.concurrentUpdateOccured";
    public static final String KEY_INTERNAL_SERVER_ERROR = "ch.puzzle.jee.errors.internalServerError";

    public static final String MESSAGE_KEY = "messageKey";
    public static final String MESSAGE_TEXT = "messageText";
    public static final String MESSAGE_ARGUMENTS = "messageArguments";
    public static final String CORRELATION_ID = "correlationId";

    private final JsonObjectBuilder errors;
    private final JsonArrayBuilder jsonArrayBuilder;

    private ErrorMapper() {
        jsonArrayBuilder = Json.createArrayBuilder();
        errors = NullAwareJsonObjectBuilder.createObjectBuilder();
    }

    public static ErrorMapper create() {
        return new ErrorMapper();
    }

    private JsonArray toJsonArray(Object... messageParameters) {
        JsonArrayBuilder params = Json.createArrayBuilder();
        for (Object messageParameter : messageParameters) {
            params.add(messageParameter.toString());
        }
        return params.build();
    }

    public ErrorMapper addMessageKey(String messageKey, Object... messageParameters) {
        JsonObject error = NullAwareJsonObjectBuilder.createObjectBuilder()
                .add(MESSAGE_KEY, messageKey)
                .add(MESSAGE_ARGUMENTS, toJsonArray(messageParameters))
                .add(CORRELATION_ID, getCorrelationId())
                .build();
        jsonArrayBuilder.add(error);
        return this;
    }

    public ErrorMapper addMessageText(String messageText, Object... messageParameters) {
        JsonObject error = NullAwareJsonObjectBuilder.createObjectBuilder()
                .add(MESSAGE_TEXT, messageText)
                .add(MESSAGE_ARGUMENTS, toJsonArray(messageParameters))
                .add(CORRELATION_ID, getCorrelationId())
                .build();
        jsonArrayBuilder.add(error);
        return this;
    }

    private String getCorrelationId() {
        Object correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            return (String) correlationId;
        } else {
            return "<<empty>>";
        }
    }

    public JsonObject build() {
        return errors.add("errors", jsonArrayBuilder).build();
    }
}
