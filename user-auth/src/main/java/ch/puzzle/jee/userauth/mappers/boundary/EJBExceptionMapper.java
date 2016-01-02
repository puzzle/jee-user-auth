package ch.puzzle.jee.userauth.mappers.boundary;

import javax.ejb.EJBException;
import javax.json.JsonObject;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    @Override
    public Response toResponse(EJBException exception) {
        Throwable cause = getCause(exception);

        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException root = (ConstraintViolationException) cause;
            Set<ConstraintViolation<?>> violations = root.getConstraintViolations();

            ErrorMapper errorMapper = ErrorMapper.create();
            for (ConstraintViolation<?> violation : violations) {
                String messageTemplate = violation.getMessageTemplate();
                if (messageTemplate != null) {
                    // removing characters '{' and '}' from string
                    errorMapper.addMessageKey(messageTemplate.replaceAll("[\\{\\}]", ""));
                }
            }
            return Response.status(BAD_REQUEST).entity(errorMapper.build()).build();
        }

        if (cause instanceof OptimisticLockException) {
            JsonObject error = ErrorMapper.create().addMessageKey(ErrorMapper.KEY_CONCURRENT_UPDATE).build();
            return Response.status(CONFLICT).entity(error).build();
        }

        if (cause instanceof WebApplicationException) {
            return ((WebApplicationException) cause).getResponse();
        }

        // default handler
        JsonObject error = ErrorMapper.create().addMessageKey(ErrorMapper.KEY_INTERNAL_SERVER_ERROR).build();
        return Response.serverError().entity(error).build();
    }

    private Throwable getCause(Throwable e) {
        return e.getCause() != null ? getCause(e.getCause()) : e;
    }
}
