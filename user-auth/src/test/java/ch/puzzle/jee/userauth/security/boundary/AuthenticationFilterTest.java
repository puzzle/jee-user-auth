package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.security.control.TokenService;
import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {
    private final String tokenString = "ktmucdib832qpoo16ntv37d3luaf1n9f";
    private Token token;
    private User user;
    private AuthenticationFilter filter;
    private TokenService tokenService;
    private ContainerRequestContext requestContext;

    @Before
    public void init() {
        tokenService = mock(TokenService.class);
        filter = new AuthenticationFilter(tokenService);
        user = new User("edward", "secret");
        token = new Token(tokenString, user);

        requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getMethod()).thenReturn("GET");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + tokenString);
        when(tokenService.validateAndRefreshToken(anyString())).thenReturn(token);
    }

    @Test
    public void shouldSetContextForValidToken() throws Exception {
        // when
        filter.filter(requestContext);

        // then
        assertThat(UserAuthContextHolder.get().getUsername(), is("edward"));
        assertThat(UserAuthContextHolder.get().getToken(), is(tokenString));
    }

    @Test
    public void shouldNotFilterOptionRequests() throws Exception {
        // given
        when(requestContext.getMethod()).thenReturn("OPTIONS");

        // when
        filter.filter(requestContext);

        // then
        verify(requestContext).abortWith(any());
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithMissingAuthorizationHeader() throws Exception {
        // given
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // when
        filter.filter(requestContext);
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithWrongFormattedAuthorizationHeader() throws Exception {
        // given
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("bla");

        // when
        filter.filter(requestContext);
    }
}