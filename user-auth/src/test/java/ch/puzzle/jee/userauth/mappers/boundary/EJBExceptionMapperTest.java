package ch.puzzle.jee.userauth.mappers.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.ejb.EJBException;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

import static ch.puzzle.jee.userauth.mappers.boundary.ErrorMapper.KEY_CONCURRENT_UPDATE;
import static ch.puzzle.jee.userauth.mappers.boundary.ErrorMapper.KEY_INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EJBExceptionMapperTest {

    private EJBExceptionMapper mapper;
    private ConstraintViolation constraintViolation;
    private EJBException constraintViolationEjbException;

    @Before
    public void init() {
        Set violations = new HashSet();
        mapper = new EJBExceptionMapper();
        constraintViolation = mock(ConstraintViolation.class);
        violations.add(constraintViolation);
        ConstraintViolationException constraintViolationException = new ConstraintViolationException("Message", violations);
        constraintViolationEjbException = new EJBException(constraintViolationException);

        when(constraintViolation.getPropertyPath()).thenReturn(mock(Path.class));
        when(constraintViolation.getMessageTemplate()).thenReturn("message.key");
    }

    @Test
    public void shouldMapConstraintViolationExceptionToResponseBody() throws Exception {
        // when
        Response response = mapper.toResponse(constraintViolationEjbException);

        // then
        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity().toString(), containsString("message.key"));
    }

    @Test
    public void shouldMapOptimisticLockingExceptionToResponseBody() throws Exception {
        // when
        Response response = mapper.toResponse(new EJBException(new OptimisticLockException()));

        // then
        assertThat(response.getStatus(), is(CONFLICT.getStatusCode()));
        assertThat(response.getEntity().toString(), containsString(KEY_CONCURRENT_UPDATE));
    }

    @Test
    public void shouldMapGenericExceptionToResponseBody() throws Exception {
        // when
        Response response = mapper.toResponse(new EJBException(new RuntimeException()));

        // then
        assertThat(response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.getEntity().toString(), containsString(KEY_INTERNAL_SERVER_ERROR));
    }
}
